# Evidence Verification Report

This report verifies that generated outputs are consistent with the console evidence.

## 1) Files checked

- `outputs/sorting_benchmark.csv`
- `outputs/dataset_profiles.csv`
- `outputs/selected_locations.csv`
- `outputs/graph_cases.csv`
- `evidence/console_output.txt`

## 2) Consistency check result

- **Sorting benchmark**: dataset-level runtime values in `console_output.txt` match rows in `sorting_benchmark.csv`.
- **Dataset profile**: profile lines in `console_output.txt` match rows in `dataset_profiles.csv`.
- **Top 10 selections**: all three dataset top-10 lists in `console_output.txt` match `selected_locations.csv`.
- **Graph cases**: Case 1-4 start/destination/waypoints/path/cost match `graph_cases.csv`.

## 3) Required values (quick reference)

### Sorting benchmark (ms)

- Dataset A: Bubble `0.147940`, Quick `5.292900`, Merge `0.255840`
- Dataset B: Bubble `4.871140`, Quick `0.089640`, Merge `1.590300`
- Dataset C: Bubble `0.817320`, Quick `0.891480`, Merge `1.904140`

### Graph case total costs

- Case 1: `0`
- Case 2: `27`
- Case 3: `39`
- Case 4: `48`

## 4) Screenshot package note

The assignment asks for screenshot evidence. In this repository, the validated textual evidence is provided under:

- `evidence/console_output.txt`
- `evidence/screenshots/01_console_output.md`
- `evidence/screenshots/02_sorting_benchmark.md`
- `evidence/screenshots/03_selected_locations.md`
- `evidence/screenshots/04_graph_cases.md`

If image-format screenshots are required by your marker, open each markdown file and export/capture as PNG before final submission.
