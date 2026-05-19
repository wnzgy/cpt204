# CPT204 Group Project Requirements Checklist

This file checks the project against the Group Project Task Sheet. A completed item is marked with `[x]`. Items that still need group members to finish manually are marked with `[ ]`.

## 1. Submission Files

| Requirement | Prepared material | Status | What is still needed |
|---|---|---:|---|
| ZIP file containing all Java code files | `src/`, `README.md`, datasets, outputs | [x] | Zip the project folder before LMO submission |
| Word report | `docs/report_process_outline.md`, `requirements/group_project_requirements_checklist.md`, latest `outputs/run_.../`, `evidence/screenshots/` | [ ] | Write the final Word report and paste all source code as text in Chapter 5 |
| MP4 video | Report outline and evidence are prepared | [ ] | Both students must record the video with own voices and faces |
| PPT used in video | Report outline can be used as PPT structure | [ ] | Create slides and keep the video within 8 minutes |
| Cover page | `B-GroupWork_Submission CoverPage_Template .pdf` | [ ] | Fill group number, student IDs, signatures, and date |

## 2. Task A - Sorting Algorithm

| Requirement | Prepared material | Status | What is still needed |
|---|---|---:|---|
| Read three candidate datasets | `CsvDataLoader.readCandidates()` reads A/B/C CSV files | [x] | None |
| Implement Bubble Sort | `src/cpt204/sort/BubbleSortStrategy.java` | [x] | None |
| Implement Quick Sort | `src/cpt204/sort/QuickSortStrategy.java` | [x] | None |
| Implement Merge Sort | `src/cpt204/sort/MergeSortStrategy.java` | [x] | None |
| Use ranking rule: priority descending, location id ascending | `CandidateLocation.RANKING_RULE` | [x] | None |
| Measure running time | `SortingService.evaluateDataset()` uses `System.nanoTime()` and averages runs | [x] | None |
| Select Top 10 from each dataset | latest `outputs/run_.../selected_locations.csv` | [x] | None |
| Provide timing table | latest `outputs/run_.../sorting_benchmark.csv`, `evidence/screenshots/02_sorting_benchmark.png` | [x] | Insert into report |
| Analyze input data properties | latest `outputs/run_.../dataset_profiles.csv` | [x] | Explain in Chapter 1 using the profile table |
| Provide deeper dataset-characteristics evidence | latest `outputs/run_.../dataset_characteristics_core.csv`, latest `outputs/run_.../dataset_characteristics_chart.png` | [x] | Insert into Chapter 1 if space allows |
| Count key sorting operations | latest `outputs/run_.../sorting_operation_counts.csv` | [x] | Use to support time-complexity explanation |
| Compare algorithm performance | Console output and latest `outputs/run_.../sorting_benchmark.csv` | [x] | Write the comparison paragraph in Chapter 1 |
| Provide visual sorting comparison | latest `outputs/run_.../sorting_runtime_chart.png` | [x] | Insert into Chapter 1 or Appendix |

## 3. Task B - Graph Algorithm

| Requirement | Prepared material | Status | What is still needed |
|---|---|---:|---|
| Read `paths.csv` | `CsvDataLoader.readUndirectedWeightedGraph()` | [x] | None |
| Build undirected weighted graph | `WeightedGraph.addUndirectedEdge()` | [x] | None |
| Treat 30 selected targets as important nodes, not the whole graph | Task B uses selected nodes as query points while Dijkstra searches the full graph | [x] | Explain this in Chapter 2 |
| Use suitable shortest-path algorithm | `DijkstraSolver.java` | [x] | Explain why Dijkstra fits positive weighted edges |
| Compare with extra shortest-path algorithms | `BidirectionalDijkstraSolver.java`, `AStarSolver.java`, latest `outputs/run_.../graph_algorithm_comparison.csv` | [x] | Explain that A* uses `h=0` because coordinates are not provided |
| Case 1: A1 to itself | latest `outputs/run_.../graph_cases.csv`, `evidence/screenshots/04_graph_cases.png` | [x] | Insert into report |
| Case 2: A1 to A10 | latest `outputs/run_.../graph_cases.csv`, `evidence/screenshots/04_graph_cases.png` | [x] | Insert into report |
| Case 3: A1 to B1 via B5 | latest `outputs/run_.../graph_cases.csv`, `evidence/screenshots/04_graph_cases.png` | [x] | Insert into report |
| Case 4: A1 to C1 via B5 then C5 | latest `outputs/run_.../graph_cases.csv`, `evidence/screenshots/04_graph_cases.png` | [x] | Insert into report |
| Report start, destination, path, and cost | latest `outputs/run_.../graph_cases.csv` | [x] | Insert table/screenshot into Chapter 2 |
| Generate graph visualization output files | latest `outputs/run_.../graph_path_visualization.png`, latest `outputs/run_.../graph_algorithm_comparison.png` | [x] | Insert into Chapter 2 or Appendix |
| Count key graph operations | latest `outputs/run_.../graph_operation_counts.csv` | [x] | Use to support Dijkstra-style complexity explanation |
| Discuss complexity and alternatives | Material is outlined in `docs/report_process_outline.md` and Chapter 2 | [x] | Keep explanation consistent with latest CSV |

## 4. Task C - Overall Application Design

| Requirement | Prepared material | Status | What is still needed |
|---|---|---:|---|
| Explain candidate data structure | `List<CandidateLocation>` in `UrbanInspectionApp` and `CsvDataLoader` | [x] | Write explanation in Chapter 3 |
| Explain graph data structure | `Map<String, List<Edge>>` in `WeightedGraph` | [x] | Write explanation in Chapter 3 |
| Compare alternative list data structures | latest `outputs/run_.../data_structure_comparison.csv`, latest `outputs/run_.../data_structure_comparison_chart.png` | [x] | Use as optional support for Task C discussion |
| Explain classes and responsibilities | Code is separated into `model`, `io`, `sort`, `graph`, `app` packages | [x] | Add class responsibility table or UML diagram |
| Show encapsulation | Private fields and getters in model/result classes | [x] | Mention examples in report |
| Show abstraction and polymorphism | `SortStrategy` interface plus three sorting classes | [x] | Mention examples in report |
| Integrate Task A and Task B into one application | `UrbanInspectionApp.java` | [x] | None |

## 5. Task D - Project Reflection

| Requirement | Prepared material | Status | What is still needed |
|---|---|---:|---|
| AI-assisted planning and collaboration | `docs/report_process_outline.md` gives the writing direction | [ ] | Add the group's real planning process and tool use |
| EDI understanding and future improvements | `docs/report_process_outline.md` gives the writing direction | [ ] | Add group-specific ideas, e.g. accessibility features |
| Lifelong learning and team role | Report outline prepared | [ ] | Each member should add real contribution and reflection |
| Depth of reflection | Outline prepared | [ ] | Write specific examples based on actual group work |

## 6. Task E - PPT and Video

| Requirement | Prepared material | Status | What is still needed |
|---|---|---:|---|
| Introduce project purpose | Report outline prepared | [ ] | Build PPT introduction slide |
| Explain sorting task | Code and output screenshots prepared | [ ] | Add PPT slide and speaker notes |
| Explain graph task | Code and output screenshots prepared | [ ] | Add PPT slide and speaker notes |
| Explain OOP design | Code package structure prepared | [ ] | Add class diagram or package diagram slide |
| Add reflection | Report outline prepared | [ ] | Add group-specific reflection slide |
| Video <= 8 minutes | No video generated | [ ] | Record with both students' faces and voices |

## 7. Report Format and Evidence

| Requirement | Prepared material | Status | What is still needed |
|---|---|---:|---|
| Chapter 1 Sorting Algorithm | latest `outputs/run_.../sorting_benchmark.csv`, latest `outputs/run_.../selected_locations.csv`, latest `outputs/run_.../dataset_characteristics_core.csv`, screenshots | [x] | Write final report text |
| Chapter 2 Graph Algorithm | latest `outputs/run_.../graph_cases.csv`, latest `outputs/run_.../graph_algorithm_comparison.csv`, visualization images, screenshots | [x] | Write final report text |
| Chapter 3 Application Design | Java source structure, OOP examples, latest `outputs/run_.../data_structure_comparison.csv` | [x] | Write final report text and optional UML |
| Chapter 4 Reflection | Outline only | [ ] | Add real group reflection |
| Chapter 5 Program Code as text | Source code exists in `src/` | [ ] | Paste all source files as text into Word report |
| Chapter 6 Appendix | `evidence/console_output.txt`, `evidence/screenshots/` | [x] | Insert selected screenshots into Word report |
| Chapter 7 Contribution Form | Requirement identified | [ ] | Fill student IDs and contribution percentages |
| Word formatting: Calibri 12, 1.5 spacing, normal margins | Requirement identified | [ ] | Apply formatting in final Word file |
| Main report max 20 pages excluding cover and Chapters 5-7 | Requirement identified | [ ] | Check page count after writing |
| Code must use CPT204-covered libraries only | Java standard library only | [x] | None |

## 8. Prepared Evidence Files

| Evidence file | Purpose |
|---|---|
| `evidence/console_output.txt` | Full console output after running the program |
| `evidence/screenshots/01_console_output.png` | Screenshot evidence of Task A and Task B program output |
| `evidence/screenshots/02_sorting_benchmark.png` | Screenshot of sorting timing table |
| `evidence/screenshots/03_selected_locations.png` | Screenshot of selected Top 10 locations |
| `evidence/screenshots/04_graph_cases.png` | Screenshot of shortest-path cases |
