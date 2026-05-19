# Report Process Outline (flow only)

Use this as the writing flow, not as final polished report text.

## Chapter 1 - Task A (Sorting)

1. State ranking rule and fairness setup (same data, same comparator, repeated timing).
2. Briefly describe Bubble/Quick/Merge implementation.
3. Insert timing table from the latest `outputs/run_.../sorting_benchmark.csv`.
4. Insert top-10 tables from the latest `outputs/run_.../selected_locations.csv`.
5. Insert optional Task A visual evidence from `sorting_runtime_chart.png` and `dataset_characteristics_chart.png`.
6. Analyze:
   - dataset characteristics from the latest `outputs/run_.../dataset_profiles.csv`
   - inversion/pass/pivot evidence from the latest `outputs/run_.../dataset_characteristics_core.csv`
   - key operation counts from the latest `outputs/run_.../sorting_operation_counts.csv`
   - why runtime differs by dataset
   - best algorithm per dataset and overall
   - scalability and memory tradeoff decision.

## Chapter 2 - Task B (Graph)

1. Explain graph model: undirected weighted adjacency list from `paths.csv`.
2. Explain Dijkstra choice and complexity.
3. Define 4 required cases exactly as task sheet.
4. Insert case results from the latest `outputs/run_.../graph_cases.csv`:
   - start / destination
   - waypoint constraints
   - shortest path
   - total cost
5. Insert the graph visualization image from the latest `outputs/run_.../graph_path_visualization.png`.
6. Add algorithm comparison using the latest `outputs/run_.../graph_algorithm_comparison.csv` and `graph_algorithm_comparison.png`.
7. Add graph operation counts from the latest `outputs/run_.../graph_operation_counts.csv`.
8. Discuss:
   - local shortest path vs global inspection planning
   - alternatives for unweighted graphs (BFS)
   - bidirectional Dijkstra for larger point-to-point queries
   - A* for coordinate-aware routing
   - why A* uses `h=0` in this project because no coordinates are provided.

## Chapter 3 - Task C (Overall design)

1. Data structures:
   - `List<CandidateLocation>` for candidate sets
   - adjacency `Map<String, List<Edge>>` for graph
   - optional `ArrayList` vs `LinkedList` evidence from `data_structure_comparison.csv`
2. Class responsibility map:
   - loader, sort strategies, sorting service, graph solver, query service, output writer, app entry.
3. OOP principles:
   - encapsulation: private fields in model/graph classes
   - polymorphism: `SortStrategy` interface with 3 implementations
   - abstraction: service classes hide workflow details
4. Add UML/class diagram snapshot.

## Chapter 4 - Task D (Reflection)

1. Planning collaboration process (task split, tracking, review loop).
2. AI-assisted tools: concrete usage, benefits, and risks.
3. EDI: accessibility/fairness enhancements for future versions.
4. Lifelong learning: what you learned, your role, and next iteration plan.

## Chapter 5 - Code

1. Paste complete source code text from `src/` (no screenshots).

## Chapter 6 - Appendix

1. Add extra console output screenshots and optional extended experiments.
2. Add AI tool citation statement according to module policy.

## Chapter 7 - Contribution

1. Fill contribution table; total must equal 100%.
