package com.example.demo.payload.request;

import com.example.demo.scheduling.solver.SolverScope;
import lombok.Data;

@Data
public class SolverDryRunRequest {
    private SolverScope scope = SolverScope.PER_HOC_KY;
    private Long seed;
}
