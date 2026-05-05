package com.example.demo.payload.request;

import com.example.demo.scheduling.solver.SolverScope;
import lombok.Data;

@Data
public class SolverRunJobRequest {
    private SolverScope scope = SolverScope.PER_HOC_KY;
    private Long seed;
    /** Mặc định true vì /solver/run được dùng cho flow apply kết quả. */
    private Boolean persist = true;
}
