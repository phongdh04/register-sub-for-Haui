# BA-Flow — F16 Chuỗi kiểm tra đăng ký (validation chain / Rules engine)

| Mã | F16 |
|----|-----|
| Vai trò | SYSTEM (ảnh hưởng STUDENT trong F10; đồng bộ semantics với F15 Kafka consumer) |
| Pattern | Chain of Responsibility trên các `AbstractValidationHandler` |
| Liên quan | F02 cửa sổ, F07 fallback HK, [`RegistrationScheduleChecker`](../../../../backend-core/src/main/java/com/example/demo/support/RegistrationScheduleChecker.java) (**thời gian mở** — *không* phải từng rule handler) |

---

## 1) Mục đích

Trước khi chiếm **slot** và ghi **`dang_ky_hoc_phan`**, mọi luồng đăng ký “chính thức” phải thỏa các **quy tắc nghiệp vụ** không thể giao cho DB đơn thuần:

| Quy tắc | Ý nghĩa BA |
|---------|------------|
| **Trùng lớp** | Một SV không đăng ký hai lần cùng **lớp học phần** trong cùng học kỳ logic nghiệp vụ *(unique index hỗ trợ nhưng handler fail-fast).* |
| **Trùng lịch học** | Không chồng chéo TKB các lớp đã có + lớp mới (composite JSON / logic schedule). |
| **Tiên quyết học phần** *(chỉ một số luồng)* | Hoàn thành / đạt môn trước mới được đăng môn sau — nếu implement đầy đủ. |

Trong PRE (một nhánh của REST — xem §4), hệ thống **coi nhẹ hoặc bỏ** một số rule để làm được demo/intent không gắn “slot chính thức” — spec này ghi nhận **đúng code hiện tại**.

---

## 2) Các luồng áp rule (quan trọng)

| Pipeline | Ai gọi | Cửa thời gian | Chuỗi validation |
|---------|--------|---------------|------------------|
| **REST đồng bộ** [`RegistrationServiceImpl.register`](../../../../backend-core/src/main/java/com/example/demo/service/impl/RegistrationServiceImpl.java) | SV qua [`RegistrationController`](../../../../backend-core/src/main/java/com/example/demo/controller/RegistrationController.java) | `ensureRegistrationOpen`: ưu tiên **OFFICIAL** khi cửa chính thức mở; nếu không thì PRE nếu mở | Nếu phase `PRE`: chỉ **`Duplicate`** + **`Schedule`** — mỗi cái `validateSelfOnly` (**không** chạy Prerequisite). Nếu `OFFICIAL`: **`validationChain.validate`** đủ Duplicate → Schedule → Prerequisite. |
| **Kafka / Ingress** [`DangKyHocPhanServiceImpl.processRegistration`](../../../../backend-core/src/main/java/com/example/demo/service/impl/DangKyHocPhanServiceImpl.java) | Go → Kafka → Java consumer | **Chỉ** `isOfficialRegistrationOpenFor(...)` (**không** đăng ký Kafka trong PRE window) | **Luôn** full chain: Duplicate → Schedule → Prerequisite |

---

## 3) Vị trí xử lý lỗi

- Handler ném **`RegistrationValidationException`** với **`errorCode`** (string cố định trong các handler — ví dụ trùng lớp có constant riêng).
- **REST** wrap → `ResponseStatusException` **`422 UNPROCESSABLE_ENTITY`** với body message (`ex.getMessage()`).
- **Kafka** map outcome qua **`mapValidationOutcome`** → log `registration_request_log` và **không increment** DB slot.

*(Khác với validation Bean Validation `@Valid` POST body → thường 400/422 tùy `GlobalExceptionHandler`.)*

---

## 4) Ordering (ưu tiên fail-fast)

Thứ tự lắp chain (**cả hai service** có cùng thứ tự `@PostConstruct`):

```
DuplicateRegistrationHandler → ScheduleConflictHandler → PrerequisiteCourseHandler
```

Lý do: kiểm tra trùng lớp và xung đột lịch rẻ hơn kiểm tra tiên quyết (đọc nhiều bảng).

---

## 5) Outputs & side effects ngắn gọn

- **SUCCESS path**: chỉ sau khi **toàn chain** PASS mới được phép **`incrementSiSoThucTe`** + insert `dang_ky_hoc_phan` (Kafka còn idempotency + event `RegistrationConfirmedEvent`; REST không publish event trong implementation hiện tại — ghi trong F10/F12 nếu cần so sánh).
- **FAIL path**: không tăng sĩ số DB; không tạo bản đăng ký.

---

## 6) Alternate / định vị học luận văn

| Chủ đề | BA ghi nhận |
|---------|--------------|
| **PRE vs OFFICIAL** | PRE cho phép nghiệp vụ “đăng ký sớm” với ít constraint hơn — phải nói rõ với GVHD để không bị hiểu nhầm với MVP production. |
| **Mở rộng OCP** | Rule mới = handler mới + `setNext` trong `@PostConstruct` — không fork service. |

---

## 7) GIVEN-WHEN-THEN

| ID | Rule |
|----|------|
| F16-R1 | **GIVEN** OFFICIAL **WHEN** đăng ký REST/Kafka **THEN** chạy **đủ** Duplicate + Schedule + Prerequisite (Kafka; REST có cùng 3 handler khi phase OFFICIAL). |
| F16-R2 | **GIVEN** chỉ PRE mở (official đóng) **WHEN** REST register **THEN** chỉ Duplicate + Schedule. |
| F16-R3 | **GIVEN** vi phạm bất kỳ handler **WHEN** trong transaction đăng ký **THEN** không commit slot (+ không insert dkhp trong Kafka path fail). |

---

## 8) Acceptance criteria

- [ ] Trùng lớp báo đúng mã/message trước khi có thể `incrementSiSo`.
- [ ] PRE REST không báo prerequisite nếu `PrerequisiteCourseHandler` chưa được gọi.
- [ ] Kafka **không** xử lý request khi ngoài **OFFICIAL** window (checker).

---

## 9) Liên quan tài liệu

| Tài liệu | Nội dung |
|----------|-----------|
| [`features/F15_high_load_queue_architecture/dev_spec.md`](../F15_high_load_queue_architecture/dev_spec.md) | Validator sau idempotency, trước increment |

---

## 10) Lịch sử

| Ngày | Ghi chú |
|------|---------|
| 2026-05 | Khởi tạo ngắn |
| 2026-05 | Chuẩn hoá chi tiết: REST PRE vs OFFICIAL, Kafka chỉ OFFICIAL, ordering, acceptance |
