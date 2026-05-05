package com.example.demo.scheduling.solver.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** BACK-TKB-019 — giới hạn thời gian / seed OR-Tools CP-SAT (đồng bộ DoD Epic D ~120s). */
@Getter
@Setter
@ConfigurationProperties(prefix = "eduport.solver.cpsat")
public class SolverCpSatProperties {

    /** Tham số CP-SAT {@code max_time_in_seconds}. */
    private int maxTimeSeconds = 120;

    /** Seed mặc định khi request không gửi seed. */
    private long randomSeed = 1L;

    private int numSearchWorkers = 4;

    /** GV tối đa khi LHP chưa có GV (pool dự phòng — MVP). */
    private int lecturerPoolCap = 500;

    /** Tiết tối đa / ngày trong lưới nguyên thủy (atomic). */
    private int maxTietPerDay = 12;

    /** BACK-TKB-022 — số LHP tối đa đưa vào model CP-SAT mini (tránh nổ biến). */
    private int mvpMaxClasses = 20;

    /** BACK-TKB-022 — giới hạn Cartesian (R×G×S) / lớp sau khi sort ổn định. */
    private int mvpMaxTuplesPerClass = 150;

    /** BACK-TKB-028 — kích thước mỗi scope con khi chạy merge tuần tự. */
    private int mvpMultiScopeChunkSize = 4;
}
