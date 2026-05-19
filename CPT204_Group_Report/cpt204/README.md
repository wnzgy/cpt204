# CPT204 Group Project - Urban Infrastructure Inspection System

This repository implements the full technical part of the CPT204 group project brief:
- Task A: sorting algorithm evaluation (Bubble / Quick / Merge)
- Task B: weighted graph shortest-path queries (4 required cases)
- Task C: object-oriented application structure (class collaboration and encapsulation)

## Project structure

- `src/cpt204/model`: domain model (`CandidateLocation`)
- `src/cpt204/io`: CSV loading
- `src/cpt204/sort`: sorting strategies, profiling, benchmarking
- `src/cpt204/graph`: weighted graph, Dijkstra, bidirectional Dijkstra, and A* solvers
- `src/cpt204/app`: main application and output export
- `visualizers/`: visual output code for Task B PNG files
- `docs/`: requirement checklist and report writing flow
- `requirements/`: full task-sheet checklist and prepared materials
- `evidence/`: console output and screenshots for the report appendix

## Run

The simplest way is to open the project in IntelliJ IDEA or another Java IDE and run:

```text
cpt204.app.UrbanInspectionApp.main
```

## Generated experiment outputs

After each run, outputs are written to a separate folder such as `outputs/run_20260518_153000_123/`.
This avoids errors when an old CSV file is still open in Excel.

- `sorting_benchmark.csv`
- `dataset_profiles.csv`
- `selected_locations.csv`
- `graph_cases.csv`
- `graph_algorithm_comparison.csv`
- `graph_path_visualization.png`
- `graph_algorithm_comparison.png`

Screenshot evidence is written to `evidence/screenshots/`.
