import csv
from collections import Counter
from pathlib import Path


DATA_DIR = Path("Group Project Datasets")
OUTPUT_DIR = Path("outputs")


def ranking_key(row):
    # Same rule as CandidateLocation.RANKING_RULE: high score first, then small id first.
    return -row["score"], row["location_id"]


def read_candidates(file_path):
    candidates = []
    with file_path.open("r", encoding="utf-8", newline="") as file:
        reader = csv.DictReader(file)
        for row in reader:
            candidates.append({
                "location_id": row["location_id"],
                "score": int(row["priority_score"]),
            })
    return candidates


def count_total_inversions(candidates):
    keys = [ranking_key(row) for row in candidates]
    inversions = 0
    for i in range(len(keys)):
        for j in range(i + 1, len(keys)):
            if keys[i] > keys[j]:
                inversions += 1
    return inversions


def find_adjacent_inversions(candidates):
    keys = [ranking_key(row) for row in candidates]
    examples = []
    count = 0
    for i in range(len(keys) - 1):
        if keys[i] > keys[i + 1]:
            count += 1
            if len(examples) < 8:
                first = candidates[i]["location_id"]
                second = candidates[i + 1]["location_id"]
                examples.append(first + "/" + second)
    return count, examples


def simulate_bubble_sort(candidates):
    data = list(candidates)
    passes = 0
    swaps = 0

    # This follows BubbleSortStrategy.java exactly.
    for i in range(len(data) - 1):
        swapped = False
        for j in range(len(data) - 1 - i):
            if ranking_key(data[j]) > ranking_key(data[j + 1]):
                data[j], data[j + 1] = data[j + 1], data[j]
                swaps += 1
                swapped = True
        passes += 1
        if not swapped:
            break
    return passes, swaps


def pivot_balance(candidates, pivot_position):
    if pivot_position == "first":
        pivot_index = 0
    else:
        pivot_index = len(candidates) - 1

    pivot = candidates[pivot_index]
    pivot_key = ranking_key(pivot)
    left_count = 0
    right_count = 0
    for i, row in enumerate(candidates):
        if i == pivot_index:
            continue
        if ranking_key(row) <= pivot_key:
            left_count += 1
        else:
            right_count += 1

    rank = left_count + 1
    return {
        "pivot_id": pivot["location_id"],
        "pivot_score": pivot["score"],
        "rank": rank,
        "left_count": left_count,
        "right_count": right_count,
    }


def simulate_quick_sort_with_last_pivot(candidates):
    data = list(candidates)
    stats = {
        "partitions": 0,
        "comparisons": 0,
        "swap_calls": 0,
        "max_depth": 0,
    }

    def quick_sort(low, high, depth):
        if low >= high:
            return
        stats["max_depth"] = max(stats["max_depth"], depth)
        pivot_index = partition(low, high)
        quick_sort(low, pivot_index - 1, depth + 1)
        quick_sort(pivot_index + 1, high, depth + 1)

    def partition(low, high):
        stats["partitions"] += 1
        pivot = data[high]
        i = low - 1
        for j in range(low, high):
            stats["comparisons"] += 1
            if ranking_key(data[j]) <= ranking_key(pivot):
                i += 1
                data[i], data[j] = data[j], data[i]
                stats["swap_calls"] += 1
        data[i + 1], data[high] = data[high], data[i + 1]
        stats["swap_calls"] += 1
        return i + 1

    quick_sort(0, len(data) - 1, 1)
    return stats


def analyze_dataset(name, file_name):
    candidates = read_candidates(DATA_DIR / file_name)
    row_count = len(candidates)
    max_pairs = row_count * (row_count - 1) // 2
    score_counts = Counter(row["score"] for row in candidates)

    total_inversions = count_total_inversions(candidates)
    adjacent_inversions, adjacent_examples = find_adjacent_inversions(candidates)
    bubble_passes, bubble_swaps = simulate_bubble_sort(candidates)
    actual_last_pivot = pivot_balance(candidates, "last")
    if_first_pivot = pivot_balance(candidates, "first")
    quick_stats = simulate_quick_sort_with_last_pivot(candidates)

    adjacent_order_percent = 100.0 * (row_count - 1 - adjacent_inversions) / (row_count - 1)
    pair_order_percent = 100.0 * (max_pairs - total_inversions) / max_pairs

    return {
        "dataset": name,
        "rows": row_count,
        "unique_scores": len(score_counts),
        "tie_score_groups": sum(1 for count in score_counts.values() if count > 1),
        "adjacent_inversions": adjacent_inversions,
        "adjacent_inversion_examples": "; ".join(adjacent_examples),
        "adjacent_order_percent": adjacent_order_percent,
        "total_inversions": total_inversions,
        "max_inversions": max_pairs,
        "pair_order_percent": pair_order_percent,
        "bubble_passes": bubble_passes,
        "bubble_swaps": bubble_swaps,
        "actual_last_pivot_id": actual_last_pivot["pivot_id"],
        "actual_last_pivot_score": actual_last_pivot["pivot_score"],
        "actual_last_pivot_rank": actual_last_pivot["rank"],
        "actual_last_pivot_left": actual_last_pivot["left_count"],
        "actual_last_pivot_right": actual_last_pivot["right_count"],
        "if_first_pivot_id": if_first_pivot["pivot_id"],
        "if_first_pivot_score": if_first_pivot["pivot_score"],
        "if_first_pivot_rank": if_first_pivot["rank"],
        "if_first_pivot_left": if_first_pivot["left_count"],
        "if_first_pivot_right": if_first_pivot["right_count"],
        "quick_last_pivot_partitions": quick_stats["partitions"],
        "quick_last_pivot_comparisons": quick_stats["comparisons"],
        "quick_last_pivot_swap_calls": quick_stats["swap_calls"],
        "quick_last_pivot_max_depth": quick_stats["max_depth"],
    }


def write_csv(results, output_file):
    field_names = list(results[0].keys())
    with output_file.open("w", encoding="utf-8", newline="") as file:
        writer = csv.DictWriter(file, fieldnames=field_names)
        writer.writeheader()
        writer.writerows(results)


def write_markdown(results, output_file):
    lines = []
    lines.append("# Dataset Characteristics Analysis")
    lines.append("")
    lines.append("Quick Sort in the current code uses the last element as the pivot.")
    lines.append("The first-element pivot columns are included only to check report wording that discusses first-element pivot.")
    lines.append("")
    lines.append("| Dataset | Adjacent inversions | Total inversions | Bubble passes | Bubble swaps | Actual last pivot split | If first pivot split |")
    lines.append("|---|---:|---:|---:|---:|---|---|")

    for row in results:
        actual_split = (
            row["actual_last_pivot_id"] + " rank " + str(row["actual_last_pivot_rank"])
            + ", " + str(row["actual_last_pivot_left"]) + " left / "
            + str(row["actual_last_pivot_right"]) + " right"
        )
        first_split = (
            row["if_first_pivot_id"] + " rank " + str(row["if_first_pivot_rank"])
            + ", " + str(row["if_first_pivot_left"]) + " left / "
            + str(row["if_first_pivot_right"]) + " right"
        )
        lines.append(
            "| {dataset} | {adjacent_inversions} | {total_inversions} | {bubble_passes} | "
            "{bubble_swaps} | {actual_split} | {first_split} |".format(
                dataset=row["dataset"],
                adjacent_inversions=row["adjacent_inversions"],
                total_inversions=row["total_inversions"],
                bubble_passes=row["bubble_passes"],
                bubble_swaps=row["bubble_swaps"],
                actual_split=actual_split,
                first_split=first_split,
            )
        )

    lines.append("")
    lines.append("Full numeric fields are saved in `dataset_characteristics_analysis.csv`.")
    output_file.write_text("\n".join(lines) + "\n", encoding="utf-8")


def main():
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    datasets = [
        ("Dataset A", "candidates_A.csv"),
        ("Dataset B", "candidates_B.csv"),
        ("Dataset C", "candidates_C.csv"),
    ]

    results = [analyze_dataset(name, file_name) for name, file_name in datasets]
    csv_file = OUTPUT_DIR / "dataset_characteristics_analysis.csv"
    markdown_file = OUTPUT_DIR / "dataset_characteristics_analysis.md"
    write_csv(results, csv_file)
    write_markdown(results, markdown_file)

    for row in results:
        print(
            "{dataset}: inversions={total_inversions}, bubble_passes={bubble_passes}, "
            "actual_last_pivot={actual_last_pivot_left}/{actual_last_pivot_right}, "
            "if_first_pivot={if_first_pivot_left}/{if_first_pivot_right}".format(**row)
        )
    print("Saved:", csv_file)
    print("Saved:", markdown_file)


if __name__ == "__main__":
    main()
