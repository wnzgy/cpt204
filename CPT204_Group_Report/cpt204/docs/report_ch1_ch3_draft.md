# Chapter 1 - Sorting Algorithm (Task A)

## 1.1 任务目标与排序规则

本项目需要对三个候选数据集（A/B/C）分别执行三种排序算法（Bubble Sort, Quick Sort, Merge Sort），并按统一规则选出 Top 10 位置用于后续图算法阶段。统一排序规则如下：

1. 按 `priority_score` **降序**（分数越高优先级越高）；
2. 若 `priority_score` 相同，按 `location_id` **升序**。

该规则由 `CandidateLocation.RANKING_RULE` 统一定义，三种算法均使用同一比较器，保证结果可比较。

## 1.2 实现与公平性设置

- 三种算法实现：
  - `BubbleSortStrategy`
  - `QuickSortStrategy`
  - `MergeSortStrategy`
- 统一调度：`SortingService.evaluateDataset(...)`
- 公平性控制：
  - 每次运行都基于原始数据副本排序；
  - 同一数据集、同一比较器、同一运行轮数（`BENCHMARK_RUNS = 3`）；
  - 使用 `System.nanoTime()` 统计平均时间。

此外，程序会校验三种算法输出是否一致；若不一致则抛出异常，避免“计时了但结果不一致”的隐性错误。

## 1.3 结果表：三算法性能对比

数据来源：`outputs/sorting_benchmark.csv`

| Dataset | Bubble (ms) | Quick (ms) | Merge (ms) |
|---|---:|---:|---:|
| Dataset A | 0.164266 | 5.552866 | 0.358400 |
| Dataset B | 5.787933 | 0.091000 | 1.375300 |
| Dataset C | 1.083000 | 0.849333 | 1.274733 |

## 1.4 Top 10 结果

数据来源：`outputs/selected_locations.csv`

- Dataset A Top 10：L0001 ~ L0010（分数从 10000 到 9991）
- Dataset B Top 10：L0101 ~ L0110（分数从 10000 到 9991）
- Dataset C Top 10：L0201 ~ L0210（分数均为 5000，按 `location_id` 升序打破并列）

三组共得到 30 个目标点，为 Task B 的查询输入。

## 1.5 数据特征与性能分析

数据来源：`outputs/dataset_profiles.csv`

| Dataset | total_rows | unique_scores | tie_score_groups | already_sorted_by_rule |
|---|---:|---:|---:|---|
| Dataset A | 1000 | 1000 | 0 | false |
| Dataset B | 1000 | 1000 | 0 | false |
| Dataset C | 1000 | 41 | 41 | false |

核心数据特征补充（数据来源：`outputs/dataset_characteristics_core.csv`）：

| Dataset | total_inversions | bubble_passes | bubble_swaps | quick_sort_pivot_used | first_partition_left | first_partition_right | pivot_quality |
|---|---:|---:|---:|---|---:|---:|---|
| Dataset A | 17 | 2 | 17 | Last element | 999 | 0 | Highly unbalanced |
| Dataset B | 250696 | 964 | 250696 | Last element | 474 | 525 | Relatively balanced |
| Dataset C | 5995 | 25 | 5995 | Last element | 988 | 11 | Highly unbalanced |
分析结论：

1. **输入顺序影响明显**：Quick Sort 在 Dataset A 显著慢于 B/C，和当前实现采用“末元素为 pivot”有关；当输入分布不利于分区时，性能会退化。
2. **不同数据集最佳算法不同**：  
   - Dataset A：Bubble 最快（该数据上表现最优）；  
   - Dataset B：Quick 最快；  
   - Dataset C：Bubble 与 Quick 接近，Merge 较慢。
3. **稳定性角度**：Merge Sort 理论复杂度稳定（`O(n log n)`），但在本数据规模与实现常数开销下不一定最快。
4. **大规模数据建议**：若数据量显著增大，优先考虑 Quick/Merge 一类 `O(n log n)` 算法；若同时考虑最坏情况稳定性，可优先 Merge。
5. **时间与空间权衡**：Quick 额外空间较小，但最坏时间可能退化；Merge 时间更稳定但需要额外缓冲空间。

---

# Chapter 2 - Graph Algorithm (Task B)

## 2.1 图模型与数据结构

`paths.csv` 被建模为**无向加权图**：

- 节点：`location_id`
- 边：`from_location` 与 `to_location` 间连接
- 权重：`weight`

实现采用邻接表：`Map<String, List<Edge>>`。每读入一条 CSV 边，调用 `addUndirectedEdge(from, to, weight)`，内部写入双向边，符合“可双向通行”的题目语义。

## 2.2 算法选择与复杂度

本项目使用 Dijkstra 求解单源到单终点最短路径，适用于非负权图。  
在二叉堆优先队列实现下，复杂度可写为：

- 时间复杂度：`O((V + E) log V)`
- 空间复杂度：`O(V + E)`

## 2.3 四个必做查询案例与输出

案例由 Task A 的排序结果动态确定：

- A1 = Dataset A 第 1 个
- A10 = Dataset A 第 10 个
- B1 = Dataset B 第 1 个
- B5 = Dataset B 第 5 个
- C1 = Dataset C 第 1 个
- C5 = Dataset C 第 5 个

数据来源：`outputs/graph_cases.csv`

| Case | Start | Destination | Waypoints (ordered) | Total Cost |
|---|---|---|---|---:|
| Case 1 | L0001 | L0001 | NONE | 0 |
| Case 2 | L0001 | L0010 | NONE | 27 |
| Case 3 | L0001 | L0101 | L0105 | 39 |
| Case 4 | L0001 | L0201 | L0105 -> L0205 | 48 |

程序同时输出完整路径字符串（`Lxxxx -> ... -> Lxxxx`），满足题目对起点、终点、路径、总代价的四项要求。

## 2.4 结果讨论

1. **为什么选 Dijkstra**：本题权重为非负距离，Dijkstra 在正确性与实现成本之间平衡较好。
2. **“局部最优 != 全局巡检最优”**：每个查询最短路径最优，不代表把所有目标点的整体巡检顺序也最优；后者更接近路径规划组合优化问题。
3. **可替代算法**：  
   - 无权图时可用 BFS；  
   - 更大规模且有坐标时可考虑 A*、分层图或预处理索引方法。

---

# Chapter 3 - Design of the Overall Application (Task C)

## 3.1 整体架构

项目按职责分为五个包：

- `model`：领域模型（如 `CandidateLocation`）
- `io`：CSV 读取（`CsvDataLoader`）
- `sort`：排序策略与评测服务
- `graph`：图结构、最短路径求解、约束查询拼接
- `app`：主流程编排与结果导出

主入口 `UrbanInspectionApp` 串联了 Task A 与 Task B：  
读取数据 -> 排序评测 -> 选 Top10 -> 读取图 -> 执行 4 个路径案例 -> 输出到 `outputs/`。

## 3.2 数据结构选择与理由

1. **候选数据集**：`List<CandidateLocation>`  
   适合排序算法原地操作与顺序访问，接口简单，便于三算法共用。
2. **图结构**：`Map<String, List<Edge>>` 邻接表  
   对稀疏图更节省空间，邻接访问效率高，适合 Dijkstra 的“按当前节点扩展邻居”过程。

## 3.3 类职责与协作

- `CsvDataLoader`：读取候选点与路径 CSV，构建内存对象；
- `SortStrategy` + 三实现：封装算法差异；
- `SortingService`：统一计时、结果一致性校验、TopN 选择；
- `WeightedGraph`：图存储；
- `DijkstraSolver`：基本最短路径；
- `GraphQueryService`：处理“按顺序经过 waypoint”的复合查询；
- `ExperimentOutputWriter`：统一导出 CSV 证据；
- `UrbanInspectionApp`：流程组织与控制台展示。

## 3.4 OOP 原则体现

1. **封装**：模型与结果类多为私有字段 + getter，内部状态不直接暴露；
2. **抽象**：`SortStrategy` 将“排序能力”抽象为统一接口；
3. **多态**：Bubble/Quick/Merge 通过同一接口被 `SortingService` 统一调用；
4. **单一职责**：读取、排序、图查询、输出分别由独立类负责，降低耦合。

## 3.5 设计收益

- 易于维护：修改某一算法不会影响主流程结构；
- 易于扩展：可继续增加新排序策略或新图算法；
- 易于报告解释：类边界清晰，便于在 UML 或类职责表中展示。

