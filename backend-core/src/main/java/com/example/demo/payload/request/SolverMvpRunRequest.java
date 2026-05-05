package com.example.demo.payload.request;

import com.example.demo.scheduling.solver.SolverScope;
import lombok.Data;

@Data
public class SolverMvpRunRequest {

    private SolverScope scope;

    private Long seed;

    /** Khi true: ghi {@code thoi_khoa_bieu_json} + FK phòng/GV và bump revision (BACK-TKB-024). */
    private Boolean persist;
}
