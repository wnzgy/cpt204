# CPT204 Group Project — Urban Infrastructure Inspection System

Java source for Task A (sorting), Task B (graph queries), and Task C (OOP structure).

## Layout

```
src/cpt204/
  app/       UrbanInspectionApp, ExperimentOutputWriter
  model/     CandidateLocation
  io/        CsvDataLoader, BorderedTable
  sort/      sorting strategies, benchmarks, dataset analysis
  graph/     weighted graph, Dijkstra, BFS, query services
  chart/     PNG chart export for report appendix
Group Project Datasets/   coursework CSV inputs
scripts/run.ps1           compile and run
```

## Run

```powershell
.\scripts\run.ps1
```

Writes CSV and PNG files under `outputs/` (regenerated locally, not committed).

Task B official queries use **Dijkstra**. Comparison uses **Dijkstra** and **BFS**.
