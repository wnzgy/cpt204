# Requirements Audit (from Group Project Task Sheet)

## Global constraints

- Language: Java, object-oriented design.
- Data: must use `candidates_A.csv`, `candidates_B.csv`, `candidates_C.csv`, and `paths.csv`.
- Libraries: only CPT204-covered libraries (this implementation uses only Java standard library).
- Submission items (outside code): ZIP code, Word report, PPT, MP4 video.

## Task A - Sorting

### Hard requirements

- Implement and compare:
  - Bubble Sort
  - Quick Sort
  - Merge Sort
- Common ranking rule:
  - `priority_score` descending
  - tie-break: `location_id` ascending
- For each dataset:
  - read CSV
  - sort
  - measure running time
  - extract top 10
- Final selection count: 30 locations in total.

### Target effect

- Produce runtime comparison table for all 3 datasets and 3 algorithms.
- Output exact top 10 selected locations for each dataset.
- Extra visual evidence is generated for sorting runtime and dataset characteristics.

## Task B - Graph

### Hard requirements

- Build undirected weighted graph from `paths.csv`.
- Use 30 selected targets from Task A as important nodes (not the only nodes in graph).
- Output shortest-path results for 4 required cases:
  - Case 1: A1 -> A1
  - Case 2: A1 -> A10
  - Case 3: A1 -> B1 via B5
  - Case 4: A1 -> C1 via B5 then C5
- Each case must include:
  - start node
  - destination node
  - path
  - total cost

### Target effect

- Correct shortest-path outputs for all 4 cases.
- Correct handling of ordered waypoints.
- Extra comparison between Dijkstra, bidirectional Dijkstra, and A* is exported in `graph_algorithm_comparison.csv`.
- Graph output visualization images are generated in each `outputs/run_.../` folder.

## Task C - Application design

### Hard requirements

- Explain data structures for candidate datasets and weighted graph.
- Explain classes/functions and workflow collaboration.
- Show OOP principles (encapsulation, abstraction, polymorphism, etc.).

### Target effect

- One coherent Java application integrating Task A and Task B.
- Extra data-structure comparison output helps justify the use of array-backed lists for index-based sorting.

## Task D - Reflection (report only)

- AI-assisted planning and collaboration reflection.
- EDI reflection.
- Lifelong learning and team-role reflection.

## Task E - PPT + Video (presentation only)

- Cover project brief, Task A/B/C, and reflection.
- Video <= 8 minutes.
- Both students present with own voices and appear on camera.

## What this repository already completes

- Full executable implementation for Task A + Task B + Task C technical core.
- Reproducible experiment outputs for required tables/cases.
- Report-support files are generated in separate `outputs/run_.../` folders, and chapter-writing process is in `docs/report_process_outline.md`.
