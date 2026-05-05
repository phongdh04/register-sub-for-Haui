# RFC — Spike OR-Tools CP-SAT (BACK-TKB-018)

## Mục tiêu

Xác nhận **Maven + Java 21 + Windows/Linux CI** có thể tải **ORTools JNI** và chạy **CP-SAT** tối thiểu trước khi nhập vào backlog P2 (**BACK-TKB-019+**).

## Kết luận (ngắn)

| Hạng mục | Kết quả |
|----------|---------|
| Dependency | `com.google.ortools:ortools-java:9.14.6206` (được pin trong `backend-core/pom.xml` (`ortools.version`)). |
| Bằng chứng | Smoke test `OrToolsCpSatSmokeTest`: ràng buộc khả thi (`x+y==1`), maximize domain nhỏ ⇒ `OPTIMAL`. |
| Spring Boot | Không cần bean riêng cho spike; solver gọi trực tiếp khi implement `scheduling.solver` (task sau). |

## Rủi ro & mitigation

1. **Native libraries:** `Loader.loadNativeLibraries()` phải chạy trước mọi lời gọi SAT. Trong `@BeforeAll` của test và (sau này) một `@PostConstruct`/singleton init cho module solver.
2. **CI agent OS:** Artefact `ortools-java` mang theo các nền tảng được hỗ trợ — nếu pipeline dùng image lạ/thiếu arch, có thể cần **pin image** hoặc tắt test bằng profile `-DskipOrtoolsTests` (chưa cần trừ khi gặp sự cố).
3. **Thời gian solve HK lớn:** DoD Epic D có time limit (~120s) — sẽ gắn `CpSolver.parameters` và scope **7.7** trong P2.

## Hướng P2 đề xuất

1. Package `com.example.demo.scheduling.solver` (**BACK-TKB-019**): `SolverRunRequest` / `SolverRunResult`, config timeout.
2. **buildDomain** lọc phòng/GV/busy-slot (**BACK-TKB-020**); model hard constraint phòng/GV (**022**).
3. Sau solve: dual-write JSON + FK + **`bump`** revision snapshot (**024**).

## Tham khảo

- [OR-Tools Java — Maven](https://developers.google.com/optimization/install/java/pkg)
- Artefact Maven Central: `com.google.ortools:ortools-java`

*Tài liệu spike — cập nhật khi nâng `ortools.version`.*
