package com.example.demo.scheduling.solver;

public interface ISchedulingSolverService {

    SolverRunResult dryRunSynchronously(SolverRunRequest request);
}
