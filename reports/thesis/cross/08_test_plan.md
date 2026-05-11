# C08 — Chiến lược kiểm thử (đầy đủ cho luận văn + vận hành QA)

Áp dụng cho monorepo: `frontend`, `backend-core`, `backend-queue`, `database` migrations & seed.

Đối tượng QA: Tester thủ công, Automation engineer, GVHD reviewing coverage matrix.

Tham chiếu feature-level cases: `features/Fxx/*/dev_spec.md` § tests.

---

## 1) Mục tiêu và phạm vi

### 1.1 Mục tiêu

| ID | Goal | Success signal |
|----|------|----------------|
| TP-01 | Đảm bảo vòng đời đăng ký không vượt sĩ số và không nhân đôi DKHP khi replay | Automated assertions |
| TP-02 | RBAC không lộ nhánh admin cho SV | 403 repeatable |
| TP-03 | PRE vs OFFICIAL validation divergence predictable | testcase matrix |
| TP-04 | Ingress + consumer không làm sai trạng thái transactional DB | mô phỏng chaos nhẹ |

---

## 1.2 Ngoài phạm vi hiện phase

- End-to-end SMTP email thật OTP (chỉ kiểm chứng log demo).
- Solver lịch scheduling nặng (benchmark OR-Tools không nằm core ĐKHP).
- WebSocket trước khi implement (`cross/05`).

---

## 2) Kim tự tháp kiểm thử (test pyramid)

| Tầng | Công cụ gợi ý | Tỉ lệ mục tiêu chỉ báo (không cứng) |
|------|---------------|---------------------------------------|
| Unit | JUnit 5 Mockito | Đặt vào các handler validators, checker window |
| Slice / WebMvcTest | Spring | Controller auth negative |
| Integration | `@SpringBootTest` + Testcontainers Postgres + optional Kafka emulator | Ít nhưng phủ các flow transaction |
| Contract | Kiểm contract JSON Ingress ↔ Kafka DTO parity | Scripted jsonschema |
| UI E2E | Playwright Cypress (future) — hiện manual SV portal | Low count |
| Load | k6 theo [`cross/07_performance_and_slo.md`](07_performance_and_slo.md) | Theo đợt demo |

---

## 3) Matrices test chức năng lõi đăng ký

### 3.1 Cửa sổ thời gian (checker)

| Case ID | Dữ liệu | Mong đợi |
|---------|---------|-----------|
| W-001 | HK có cửa toàn khóa mở, SV không cohort | Theo `debugReason` / hoặc mở toàn học kỳ nếu null wildcard |
| W-002 | Window cohort K2023, SV cohort K2021 | không mở cho SV |
| W-003 | Fallback HK dates null | PRE/OFF không chặn theo fallback — confirm `RegistrationScheduleChecker` doc |
| W-004 | PRE + OFF cùng mở overlapping | SV status `PRE_AND_OFFICIAL` có trường hợp |

### 3.2 Đăng ký REST PRE vs OFFICIAL

| Case | Validation chain |
|------|------------------|
| R-PRE | Duplicate OK check; schedule conflict; **KHÔNG** prerequisites |
| R-OFFICIAL-FULL | Duplicate + Schedule + prerequisite (full chain) |

Kiểm thử hai trạng thái bằng cách tắt/mở window official only vs pre only.

### 3.3 Hết chỗ (slot)

**REST**: Hai thread song song chỉ có 1 slot residual — chỉ một HTTP 201, còn lại HTTP 409.  
Assert SQL `si_so_thuc_te <= si_so_toi_da`.

**Ingress**: Burst N>capacity — expect majority `FULL` JSON status; không stacktrace 500 ingress.

---

## 4) Bảng testcase RBAC cực thiết yếu

| TID | Curl / Action | JWT | Expected |
|-----|----------------|-----|----------|
| RB-SV-NEG-01 | `GET /api/v1/admin/pre-registrations/demand` | STUDENT token | 403 JSON |
| RB-ADM-NEG-02 | `GET /api/v1/registrations/me` | ADMIN token | 403 |
| RB-PUBLIC-OK | `/api/lop-hoc-phan/hoc-ky/{id}` | none | 200 |
| RB-COURSE-LEC-OK | `GET /api/v1/courses?page=0&size=5` | LECTURER token (`gv01`) | 200 và `Page` JSON |

---

## 5) Kiểm thử idempotency & replay

Kafka path:

1. Gửi message với deterministic idempotency key (theo Sprint 4 service).
2. Replay cùng key — verify outcome stable (không hai `dang_ky` ACTIVE).

Manual SQL:

```sql
SELECT count(*) FROM dang_ky_hoc_phan
WHERE id_sinh_vien=:sv AND id_lop_hp=:lhp AND id_hoc_ky=:hk
  AND trang_thai_dang_ky IN ('THANH_CONG','CHO_DUYET');
```

Expect ≤1.

REST path: không log idempotent — chỉ brute double-click concurrency test không duplicate acceptable.

---

## 6) Ma trận regression sau thay đổi có rủi ro

Thay đổi các file/class sau phải chạy tối thiểu bộ nhỏ đính kèm:

| Thay đổi trong | Regression tối thiểu |
|-----------------|----------------------|
| `RegistrationScheduleChecker` | W-* window tests + REST window-status |
| `RegistrationServiceImpl` | R-* + concurrency slot |
| `DangKyHocPhanServiceImpl` | Idempotency + consumer happy path smoke |
| `CourseSearchSpecification` hoặc `CourseSearchController` RBAC string | RB-COURSE* |
| migrations sprint 1–5 | smoke boot + Hibernate mapping validation |

---

## 7) Báo lỗi & severities

| Mức | Đặc điểm ví dụ | SLA báo nhóm |
|-----|----------------|---------------|
| S0 | Enrollment oversubscribed DB không rollback | Immediate |
| S1 | Queue open public ingress misconfig | Immediate |
| S2 | UI mapping wrong countdown pre-reg | Sprint fix |
| S3 | Copywriting minor | backlog |

---

## 8) Dữ liệu test reproducible tagging

Đặt thẻ `DATASET=EduPort-DKHP-<gitsha>` vào báo cáo thí nghiệm.

Seed accounts (demo trong `CLAUDE.md`):

- admin / gv01 / sv01 — document if customized.

---

## 9) Test automation backlog (minimal GitHub Issues style)

| Issue | Acceptance |
|-------|---------------|
| `QA-AUTO-001` Wire SpringBootTest Postgres container verifying window creation | merges green |
| `QA-AUTO-002` Lightweight Go handler unit tests not present today | propose Go testing package |
| `QA-AUTO-003` Frontend route guard snapshots | shallow |

---

## 10) Kết luận sử dụng trong báo luận văn

Chèn mục *Phương pháp kiểm chứng* trích C08 vào chương phương pháp; phụ lục chứa bảng số đo load (C07) kết quả thực tế sau khi chạy.
