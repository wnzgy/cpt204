# CPT204 Group Project Presentation

## Slide 1 - Title

- Urban Infrastructure Inspection System
- CPT204 Group Project
- Student A ID / Student B ID
- Date

## Slide 2 - Project background and goals

- Problem: identify high-priority inspection locations and compute efficient routes
- Input data:
  - `candidates_A.csv`, `candidates_B.csv`, `candidates_C.csv`
  - `paths.csv` (undirected weighted graph)
- Core goals:
  - Task A: sorting + Top 10 selection
  - Task B: shortest-path queries
  - Task C: OOP architecture
  - Task D: reflection

## Slide 3 - System workflow overview

- CSV loading
- Sorting benchmark (3 algorithms, 3 datasets)
- Top 10 extraction per dataset
- Graph building + shortest-path queries
- Output export for report evidence

## Slide 4 - Task A: sorting algorithms and ranking rule

- Algorithms: Bubble / Quick / Merge
- Shared ranking rule:
  - `priority_score` descending
  - tie -> `location_id` ascending
- Fairness setup:
  - same input, same comparator, repeated runs, average runtime

## Slide 5 - Task A: benchmark results

- Insert table from `outputs/sorting_benchmark.csv`
- Key findings:
  - Dataset A: Bubble fastest, Quick slowest
  - Dataset B: Quick fastest
  - Dataset C: Bubble and Quick close
- Explain dataset characteristics impact with `outputs/dataset_profiles.csv`

## Slide 6 - Task A: Top 10 selection output

- Insert selected locations from `outputs/selected_locations.csv`
- Highlight:
  - A/B top-10 have unique descending scores
  - C has tied scores and uses `location_id` tie-break
- Total selected targets: 30

## Slide 7 - Task B: graph modeling and algorithm

- `paths.csv` -> undirected weighted adjacency list
- Graph algorithm: Dijkstra
- Why suitable:
  - positive weights
  - efficient shortest-path computation
- Complexity:
  - time `O((V+E) log V)`
  - space `O(V+E)`

## Slide 8 - Task B: required 4 query cases

- Case 1: A1 -> A1
- Case 2: A1 -> A10
- Case 3: A1 -> B1 via B5
- Case 4: A1 -> C1 via B5 then C5
- Insert result table from `outputs/graph_cases.csv`

## Slide 9 - Task C: OOP design

- Package structure:
  - `model`, `io`, `sort`, `graph`, `app`
- Main abstractions:
  - `SortStrategy` interface + 3 implementations (polymorphism)
- Service collaboration:
  - `SortingService`, `DijkstraSolver`, `GraphQueryService`, `ExperimentOutputWriter`

## Slide 10 - Task D: reflection

- AI-assisted planning: checklist, writing structure, risk scan
- Team collaboration: task split + integration loop
- EDI perspective: accessibility and inclusive outputs
- Lifelong learning: from coding task to full delivery mindset

## Slide 11 - Limitations and future work

- Benchmark rigor can be improved (warm-up, variance)
- More robust input validation
- More advanced route-planning approaches for larger graphs

## Slide 12 - Conclusion

- Completed technical core for Task A/B/C
- Built reproducible evidence pipeline for report
- Prepared delivery artifacts for final submission
- Q&A
