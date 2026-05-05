# Backend — Backlog task Giai đoạn 1 TKB (Scheduling)

**Tham chiếu:** `DOCS/Admin_TKB_Scheduling_Phase1_KeHoach.md` (EDU-ADM-TKB-P1 v1.2)  
**Phạm vi file này:** **chỉ backend** `backend-core` (Java/Spring/JPA/PostgreSQL). UI/FE được ghi là phụ thuộc hoặc ticket riêng.  
**Convention ID:** `BACK-TKB-###` (đưa vào Jira/Linear/GitHub Issues tùy team).

---

## 0. Hiện trạng nhanh trong repo

| Hạng mục | Trạng thái |
|-----------|------------|
| `LopHocPhan.thoiKhoaBieuJson` (JSONB) | Đã có |
| `ScheduleConflictHandler`, `TkbSlotConflictUtils` | Đã có (đăng ký SV, chưa đủ scheduling HK-wide) |
| Entity `PhongHoc` / bảng master phòng | Chưa có (cần P0) |
| `id_phong_hoc` / `block_id` trên LHP | Chưa có |
| `gv_busy_slot`, `schedule_change_set`, `notification_queue` | Chưa có |
| API `/api/v1/admin/scheduling/**` | Chưa có |

---

## 1. Bản đồ task theo phase (backend)

| Phase | Mục tiêu backend | Task ID (khối chính) |
|-------|------------------|----------------------|
| **P0** | Master data, migration kickoff, snapshot + conflict read-only | BACK-TKB-001 … 012 |
| **P1** | Dự báo mở lớp, spike OR-Tools CI | BACK-TKB-013 … 018 |
| **P2** | Solver CP-SAT MVP, pre-check, cohort rules, multi-scope merge | BACK-TKB-019 … 028 |
| **P3** | Publish/revision DB + stub notify + audit log (API hỗ trợ FE) | BACK-TKB-029 … 036 |
| **P4** | `TkbBlock` lifecycle + API bundle (backend) | BACK-TKB-037 … 040 |

---

## 2. Chi tiết ticket (copy-paste backlog)

### Epic A / P0 — Master data & migration

#### BACK-TKB-001 — Entity `PhongHoc` + Flyway/Liquibase migration

- **Mô tả:** Tạo bảng master phòng theo §5.1 kế hoạch: `ma_phong` (unique), `ten_phong`, FK/String cơ sở, `loai_phong` (enum JPA), `suc_chua`, `trang_thai`, `ghi_chu`.
- **Deliverable:** Entity, repository, snake_case bảng PostgreSQL, seed tối thiểu ≥ 20 phòng (SQL seed hoặc `DataSeeder`).
- **Tiêu chí:** CRUD repository; unique `ma_phong`; enum map ổn định (`LY_THUYET`, `MAY_TINH`, …).
- **Phụ thuộc:** —

#### BACK-TKB-002 — Admin REST CRUD `PhongHoc`

- **Mô tả:** Controller dưới `/api/v1/admin/phong` (hoặc prefix đã chuẩn hóa trong app), `ROLE_ADMIN`.
- **Tiêu chí:** GET list/paging, GET by id, POST, PUT/PATCH, DELETE (soft nếu có ràng buộc LHP).
- **Phụ thuộc:** BACK-TKB-001

#### BACK-TKB-003 — Cột `id_phong_hoc` nullable trên `Lop_Hoc_Phan`

- **Mô tả:** Migration + `@ManyToOne` optional → `PhongHoc`; đồng bộ với §3.1 dual-write sau.
- **Tiêu chí:** LHP load được khi FK null; không phá API hiện tại đọc JSON.
- **Phụ thuộc:** BACK-TKB-001

#### BACK-TKB-004 — Stub `TkbBlock` + cột `block_id` nullable trên `Lop_Hoc_Phan`

- **Mô tả:** Bảng/entity `TkbBlock` tối thiểu (id, ma, ten, hoc_ky_id, json_slots, danh_sach_id_hoc_phan hoặc tương đương) + FK nullable `block_id` trên LHP (P0 theo §7.6 / §12 Epic A).
- **Tiêu chí:** Migration apply; entity quan hệ; có thể defer full API P4.
- **Phụ thuộc:** — (có thể song song BACK-TKB-003)

#### BACK-TKB-005 — Bảng `gv_busy_slot` + entity + repository

- **Mô tả:** DDL như §5.2 kế hoạch: `thu SMALLINT CHECK (thu BETWEEN 2 AND 8)` + comment chủ nhật; indexes `idx_gv_busy_gv_thu`, `idx_gv_busy_hk`.
- **Tiêu chí:** JPA mapping đúng tên bảng; validation service-layer cho overlap `tiet_bd/tiet_kt`.
- **Phụ thuộc:** —

#### BACK-TKB-006 — REST CRUD `gv_busy_slot` + read “constraints” GV

- **Mô tả:** `/api/v1/admin/giang-vien/{id}/constraints` (busy rows + sau này định mức): list/create/update/delete busy slot.
- **Tiêu chí:** Seed ≥ 5 pattern (DoD Epic A); filter theo `hoc_ky_id` nullable.
- **Phụ thuộc:** BACK-TKB-005

#### BACK-TKB-007 — Báo cáo audit JSON phòng (bước 3.1-a)

- **Mô tả:** Job hoặc endpoint nội bộ admin: thống kê LHP có `thoi_khoa_bieu_json` với `phong` — phân loại map 1-1 / ambiguous / missing.
- **Tiêu chí:** Output CSV/JSON cố định schema cho BA; không cần UI.
- **Phụ thuộc:** BACK-TKB-001, BACK-TKB-003

#### BACK-TKB-008 — Batch map JSON phòng → `id_phong_hoc` (3.1-b)

- **Mô tả:** Service nhận `hoc_ky_id`, normalize string phòng → lookup `PhongHoc.maPhong`; ambiguous → staging table hoặc list id cần resolve.
- **Tiêu chí:** Idempotent; log metric `fallback_json_phong` khi đọc từ JSON sau này.
- **Phụ thuộc:** BACK-TKB-007

#### BACK-TKB-009 — Dual-write hook: ghislot Admin cập nhật FK + JSON

- **Mô tả:** Mọi code path Admin (và sau đó solver) khi gán phòng: set `id_phong_hoc` đồng thời cập nhật field `phong` trong `thoiKhoaBieuJson` (policy đồng bộ chuỗi hiển thị).
- **Tiêu chí:** Unit test hook; feature flag hook nếu cần rollback.
- **Phụ thuộc:** BACK-TKB-003, BACK-TKB-008 (khuyến nghị)

#### BACK-TKB-010 — Index composite warm-path trên LHP (§8.2.1)

- **Mô tả:** Thêm index `(id_hoc_ky, id_phong_hoc, thu)` và `(id_hoc_ky, id_giang_vien, thu)` — `thu` surrogate từ slot đầu tiên trong JSON hoặc cột extracted (document encoding cố định trong code).
- **Tiêu chí:** Flyway migration; comment trong entity/migration về surrogate; query plan smoke trên staging.
- **Phụ thuộc:** BACK-TKB-003 (id_phong_hoc), quyết định cột `thu` nguồn

#### BACK-TKB-011 — `SchedulingSnapshot` in-memory + revision version

- **Mô tả:** Service build snapshot theo §8.2.2: maps `(roomId,thu)` / `(gvId,thu)` → intervals từ toàn bộ LHP của `hoc_ky_id`; `revisionVersion` (có thể `HocKy` field hoặc bảng phụ).
- **Tiêu chí:** Build O(n) từ DB; hỗ trợ invalidate khi bump version.
- **Phụ thuộc:** BACK-TKB-003, đọc được slot từ JSON/FK

#### BACK-TKB-012 — `GET .../scheduling/hoc-ky/{id}/snapshot` + `POST .../conflict-check`

- **Mô tả:** API §9: snapshot payload grid (LHP + slot + phòng + GV + ETag/`revision_version`); conflict-check body = draft delta 1 LHP/slot mới → list mã violation §8.2 (`PHONG_TRUNG`, `GV_TRUNG`, `COHORT_TRUNG`, `PHONG_KHONG_DU_SUC_CHUA`).
- **Tiêu chí:** Dùng snapshot cache (Caffeine/Redis tùy chọn P0); log `cold|warm`; p95/p99 theo DoD Epic B (đo sau, có tag perf).
- **Phụ thuộc:** BACK-TKB-011; rule cohort có thể stub `COHORT_TRUNG` đến BACK-TKB-027

---

### Epic C / P1 — Dự báo & shell LHP

#### BACK-TKB-013 — Entity `DuBaoMoLop` / `DemandPlanLine` + version

- **Mô tả:** Bảng theo §6.3: `id_hoc_phan`, `hoc_ky_id`, `so_sv_du_kien`, `so_lop_de_xuat`, `si_so_mac_dinh`, `version_id` (+ trạng thái draft/approved).
- **Tiêu chí:** Query theo học kỳ + version mới nhất.
- **Phụ thuộc:** —

#### BACK-TKB-014 — Service `DuBaoMoLopCalculator`

- **Mô tả:** Công thức §6.2–6.3: `N_on_track`, `N_retake` từ `BangDiemMon`, `CtdtHocPhan`, cohort; tham số `si_so_toi_da_mac_dinh`, `he_so_du_phong`, `ty_le_sv_hoc_lai_tham_gia`.
- **Tiêu chí:** Test cố định dataset → `so_lop_de_xuat` khớp (DoD Epic C).
- **Phụ thuộc:** BACK-TKB-013

#### BACK-TKB-015 — `POST .../scheduling/hoc-ky/{id}/forecast`

- **Mô tả:** Chạy tính toán, lưu version mới, trả id version + tóm tắt.
- **Phụ thuộc:** BACK-TKB-014

#### BACK-TKB-016 — Workflow duyệt version dự báo (state machine tối thiểu)

- **Mô tả:** Endpoint approve/reject; chỉ version approved mới cho phép “sinh LHP shell”.
- **Phụ thuộc:** BACK-TKB-015

#### BACK-TKB-017 — Job “Sinh LHP shell” từ dự báo đã duyệt

- **Mô tả:** Tạo `LopHocPhan` chưa slot (hoặc placeholder JSON rỗng), `ma_lop_hp` deterministic, gắn `hoc_ky_id`, `hoc_phan_id`, `si_so_toi_da` từ dự báo.
- **Tiêu chí:** Idempotent theo (version, hoc_phan) hoặc flag đã spawn.
- **Phụ thuộc:** BACK-TKB-016

#### BACK-TKB-018 — Spike OR-Tools trong CI (kết quả ghi RFC)

- **Mô tả:** Theo §10 P1: 2–3 ngày — thêm dependency `com.google.ortools` (hoặc contract Python sidecar), test “model rỗng solves”, chạy trên pipeline.
- **Deliverable:** File `DOCS` hoặc `reports/ortools_spike_ci.md` với kết luận; **không blocking** code nghiệp vụ nhưng blocking estimate P2.
- **Phụ thuộc:** —

---

### Epic D / P2 — Solver

#### BACK-TKB-019 — Module package `scheduling.solver` + config time limit 120s

- **Mô tả:** Khung service `SolverRunRequest` (hoc_ky_id, scope, seed), `SolverRunResult` (status, assignments, stats).
- **Phụ thuộc:** BACK-TKB-018

#### BACK-TKB-020 — `buildDomain(lhp_i)` bắt buộc: filter R, G_i, S_i

- **Mô tả:** Implement §7.2: chỉ materialize tuples sau filter loại phòng + pool GV + slot loại trừ `gv_busy_slot` HARD.
- **Tiêu chí:** Unit test: domain size không Cartesian full; log approximate var count metric.
- **Phụ thuộc:** BACK-TKB-001, BACK-TKB-005, BACK-TKB-019

#### BACK-TKB-021 — Feasibility pre-check §7.3

- **Mô tả:** Trả `INFEASIBLE_EARLY` có thống kê định lượng trước CP-SAT; test dataset lab quá tải (DoD Epic D).
- **Phụ thuộc:** BACK-TKB-020

#### BACK-TKB-022 — CP-SAT model: hard constraints phòng/GV/capacity

- **Mô tả:** Bài toán cốt lõi §7.2 mapping bảng: không double-book phòng/GV overlap tiết; sức chỗ khi đã có sĩ số.
- **Phụ thuộc:** BACK-TKB-020, BACK-TKB-021

#### BACK-TKB-023 — Soft objective (ưu tiên GV/campus/block) — MVP tối thiểu

- **Mô tả:** Một hoặc hai penalty weights configurable per HK để chứng minh end-to-end.
- **Phụ thuộc:** BACK-TKB-022

#### BACK-TKB-024 — Persist kết quả solver → `thoi_khoa_bieu_json` + FK + invalidate snapshot

- **Mô tả:** Sau solve: dual-write BACK-TKB-009; bump `revisionVersion`; optional job id async.
- **Phụ thuộc:** BACK-TKB-009, BACK-TKB-011, BACK-TKB-022

#### BACK-TKB-025 — `POST .../solver/run` (async job + status poll)

- **Mô tả:** §9: enqueue `SolverJob`, trả job id; GET status/result; timeout 120s server-side aligned DoD Epic D.
- **Phụ thuộc:** BACK-TKB-024

#### BACK-TKB-026 — Loader rule `nhom_trung_tiet` (JSON §5.3) ✅ DONE

- **Mô tả:** Entity/bảng `quy_tac_trung_tiet_cohort` hoặc JSON file per HK; map sang constraint pair LHP trong scope.
- **Phụ thuộc:** BACK-TKB-022

#### BACK-TKB-027 — Enforce cohort trong snapshot + conflict-check + solver ✅ DONE

- **Mô tả:** Giữ một nguồn sự thật checker; solver dùng cùng rule.
- **Phụ thuộc:** BACK-TKB-012, BACK-TKB-026

#### BACK-TKB-028 — Multi-scope runner + merge pass policy §7.7 ✅ DONE

- **Mô tả:** Theo topo order; relax fix biên theo `slackHardCount`; một lần retry micro-scope; log `{scope_conflict_ids, gv_ids, room_ids}`.
- **Tiêu chí:** Integration test nhỏ 2 scope conflicting (có/không resolve).
- **Phụ thuộc:** BACK-TKB-025

---

### Epic publish / P3 — ADR 4.1 backend

#### BACK-TKB-029 — Bảng `schedule_change_set` + FK `HocKy.pending_change_set_id` ✅ DONE

- **Mô tả:** DDL §4.1 (trạng thái PENDING|APPROVED|REJECTED|APPLIED, `payload_delta` JSONB, …); nullable FK trên `Hoc_Ky` hoặc bảng `hoc_ky_lich_ban` 1–1.
- **Phụ thuộc:** —

#### BACK-TKB-030 — Impact analysis: `affected_sv_count` + list `affected_sv_ids` ✅ DONE

- **Mô tả:** Từ `DangKyHocPhan` / enrollment SUCCESS cho các `lhp_id` trong delta.
- **Phụ thuộc:** BACK-TKB-029

#### BACK-TKB-031 — Bảng `notification_queue` (stub) + contract JSON SCHEDULE_CHANGED ✅ DONE

- **Mô tả:** Insert payload §4.1; log WARN/INFO đầy đủ; không cần dispatcher email thật P3.
- **Phụ thuộc:** BACK-TKB-030

#### BACK-TKB-032 — Workflow API submit/review/apply change set ✅ DONE

- **Mô tả:** Submit delta → conflict với enrollment → approve → bump `version_no` effective → APPLY → invalidate snapshot · cache.
- **Phụ thuộc:** BACK-TKB-029, BACK-TKB-012, BACK-TKB-031

#### BACK-TKB-033 — `tkb_chinh_sua_log` (§8.4) ✅ DONE

- **Mô tả:** Audit: user, lhp, `ly_do_thay_doi` NOT NULL từ bước review, payload cũ/mới, `effective_version_no`.
- **Phụ thuộc:** BACK-TKB-032

#### BACK-TKB-034 — PATCH slot: từ chối nếu hard violation sau check ✅ DONE

- **Mô tả:** `/scheduling/lop-hoc-phan/{id}/slot` §9 — chỉ persist khi PASS; học kỳ `CONG_BO` yêu cầu luồng change set (policy).
- **Phụ thuộc:** BACK-TKB-012, BACK-TKB-032

#### BACK-TKB-035 — Field `tkb_trang_thai` / publish guard ✅ DONE

- **Mô tả:** Enum hoặc bảng trạng thái HK §4 — transition `NHAP` → `CHO_DUYET` → `CONG_BO`; guard solver/patch theo state.
- **Phụ thuộc:** entity `HocKy` có field hoặc join

#### BACK-TKB-036 — Metrics & logging solver + merge + change set ✅ DONE

- **Mô tả:** Micrometer/timer cho conflict-check, solver, apply delta; correlation id job.
- **Phụ thuộc:** lệ thuộc các task trước

---

### Epic block / P4 — Đăng ký bundle (backend)

#### BACK-TKB-037 — Lifecycle API `TkbBlock` đầy đủ ✅ DONE

- **Mô tả:** CRUD block theo học kỳ; validator `danh_sach_id_hoc_phan`.
- **Phụ thuộc:** BACK-TKB-004

#### BACK-TKB-038 — Validator đăng ký bundle atomically ✅ DONE

- **Mô tả:** Transaction đăng ký đồng thời các LHP cùng `block_id`; flag `bat_buoc_chon_ca_block`.
- **Phụ thuộc:** BACK-TKB-037, luồng đăng ký hiện có

#### BACK-TKB-039 — Conflict SV vs block (reuse ScheduleConflictHandler mở rộng nếu cần) ✅ DONE

- **Mô tả:** Không overlap slot trong block + các môn ngoài block.
- **Phụ thuộc:** BACK-TKB-038

#### BACK-TKB-040 — Test tích hợp đăng ký block E2E (Testcontainers nhẹ) ✅ DONE

- **Mô tả:** Một scenario happy path + một conflict path.
- **Phụ thuộc:** BACK-TKB-039

---

## 3. Trật tự khuyến nghị (đường găng cốt lõi)

```
BACK-TKB-001 → 002 → 003 → 005 → 006 → 007 → 008 → 010 → 009
        → 011 → 012 → … (P1/P2/P3 như epic)
```

`004` và `029+` có thể song song sau khi nền LHP FK ổn định.

---

## 4. Gợi ý mapping đội sprint

| Sprint tập trung | Gói đóng backend |
|------------------|------------------|
| 1 | 001–006, bắt đầu 007 |
| 2 | 007–012 |
| 3 | 013–018 |
| 4–5 | 019–028 (phụ thuộc spike 018 xanh) |
| 6 | 029–036 + tích `034`/`035` |

---

*Tài liệu backlog backend — có thể tách nhỏ từng `BACK-TKB-###` thành issue riêng; cập nhật khi OR-Tools chốt bindings Post-spike.*
