# Dev-Spec — F16 Registration validation handlers (chain)

| Mã | F16 |
|----|-----|
| BA | [`ba_flow.md`](ba_flow.md) |
| Packages | [`com.example.demo.service.validation`](../../../../backend-core/src/main/java/com/example/demo/service/validation/) |

---

## 1) Kiến trúc

### 1.1 Template method — [`AbstractValidationHandler`](../../../../backend-core/src/main/java/com/example/demo/service/validation/AbstractValidationHandler.java)

| Method | Hành vi |
|--------|---------|
| `validate(msg)` | `doValidate(msg)` sau đó **forward** sang `next.validate(msg)` đệ quy |
| `validateSelfOnly(msg)` | chỉ `doValidate` — **không** gọi `next` |

Interface: [`IRegistrationValidationHandler`](../../../../backend-core/src/main/java/com/example/demo/service/validation/IRegistrationValidationHandler.java).

### 1.2 Dữ liệu vào

[`RegistrationMessageDto`](../../../../backend-core/src/main/java/com/example/demo/payload/request/RegistrationMessageDto.java) — các trường tối thiểu: `idSinhVien`, `idLopHp`, `idHocKy`, `traceId` (HTTP tự khởi tạo prefix `HTTP-`).

### 1.3 Ngoại lệ

[`RegistrationValidationException`](../../../../backend-core/src/main/java/com/example/demo/service/validation/RegistrationValidationException.java) — luôn kèm **`errorCode`** (string) để:

- Kafka: `mapValidationOutcome` trong [`DangKyHocPhanServiceImpl`](../../../../backend-core/src/main/java/com/example/demo/service/impl/DangKyHocPhanServiceImpl.java)
- REST: catch trong `RegistrationServiceImpl` và map `422`

---

## 2) Implementations

| Handler | File | Responsibility (tóm tắt) |
|---------|------|---------------------------|
| Duplicate | [`DuplicateRegistrationHandler`](../../../../backend-core/src/main/java/com/example/demo/service/validation/handler/DuplicateRegistrationHandler.java) | Fail nếu đã có `@ManyToOne` registration active cùng lớp+HK |
| Schedule | [`ScheduleConflictHandler`](../../../../backend-core/src/main/java/com/example/demo/service/validation/handler/ScheduleConflictHandler.java) | So khớp TKB JSON / các đăng ký hiện có |
| Prerequisite | [`PrerequisiteCourseHandler`](../../../../backend-core/src/main/java/com/example/demo/service/validation/handler/PrerequisiteCourseHandler.java) | Chuỗi học phần tiên quyết — chỉ vào luồng full chain |

---

## 3) Wiring

### 3.1 REST — [`RegistrationServiceImpl`](../../../../backend-core/src/main/java/com/example/demo/service/impl/RegistrationServiceImpl.java)

`@PostConstruct buildChain`:

```java
duplicate → schedule → prerequisite
validationChain = duplicate (head)
```

Trong `register(...)`:

- `activePhase == "PRE"` → `duplicate.validateSelfOnly(msg); schedule.validateSelfOnly(msg);`
- else → `validationChain.validate(msg)`

Phase resolve: **`ensureRegistrationOpen`** — **`OFFICIAL` thắng** nếu `isOfficialRegistrationOpenFor`; ngược lại `PRE` nếu PRE mở; cả hai đóng → `403 Forbidden`.

### 3.2 Kafka — [`DangKyHocPhanServiceImpl.buildValidationChain`](../../../../backend-core/src/main/java/com/example/demo/service/impl/DangKyHocPhanServiceImpl.java)

- Cùng thứ tự `duplicate → schedule → prerequisite`.
- `processRegistration`: sau idempotency + gate HK/SV exists + **official window OPEN** → **`validationChain.validate(msg)`**.

**Không** có nhánh PRE trên Kafka path trong code hiện tại.

---

## 4) Mở rộng (OCP)

1. Tạo class `extends AbstractValidationHandler` + `@Component`.
2. Inject vào constructor service builder chain.
3. `existingTail.setNext(newHandler)` (giữ invariant "fail-fast nhẹ trước").

Đừng gọi `validate()` và `validateSelfOnly()` xen kẽ trên **cùng** msg trong một request — dễ bỏ sót tail.

---

## 5) Test

| Artifact | Scope |
|---------|-------|
| Unit handler | [`DuplicateRegistrationHandler`](../../../../backend-core/src/test/java/com/example/demo/service/validation/handler/DuplicateRegistrationHandler.java) (package test) |

Gợi ý integration: Mockito chain + fixture `RegistrationMessageDto` không cần DB khi chỉ đơn vị logic.

---

## 6) Checklist RCA khi GVHD hỏi “sao PRE vẫn đăng ký không cần môn A?”

1. Confirm `ensureRegistrationOpen` trả **`PRE`** (official đã đóng).  
2. Step debug: có gọi `prerequisiteCourseHandler.validate` không? (**Không** nếu PRE.)  
3. Nếu cần business “PRE vẫn phải tiên quyết” → đổi policy trong `RegistrationServiceImpl` (**ngoài scope tài liệu** nhưng đủ chỉ chỗ).

---

## 7) Error code canon — [`RegistrationValidationException`](../../../../backend-core/src/main/java/com/example/demo/service/validation/RegistrationValidationException.java)

| Constant | Meaning | Typical handler |
|---------|---------|-----------------|
| `TRUNG_LOP` | Duplicate same section HK | DuplicateRegistrationHandler → Kafka maps `DUPLICATE` |
| `TRUNG_LICH` | Schedule conflict | ScheduleConflictHandler |
| `CHUA_HOC_TIEN_QUYET` | Prerequisite missing transcripts | PrerequisiteCourseHandler |
| `LOP_KHONG_TON_TAI` | Section invalid / gated | các guard khác có thể dùng constant |
| `HET_CHO` | Capacity safety (validator layer) | rare vs atomic increment |

**REST mapping**: thrown `422` trong `RegistrationServiceImpl` preserving message string.

---

## 8) Anti-pattern khi chỉnh sửa

| Anti-pattern | Tại sao |
|---------------|---------|
| Gọi `validate()` rồi lại manually invoke lower handler | Double evaluation side effects hypothetical |
| Bổ sung DB write trong `doValidate()` | Violates separation — keep pure checks |

---

## 9) Lịch sử

| Ngày | Ghi |
|------|-----|
| 2026-05 | Draft ngắn |
| 2026-05 | Wiring chi tiết REST vs Kafka, PRE vs OFFICIAL |
| 2026-05 | Append error-code matrix + anti-patterns đồng đều với các feature có matrix lỗi |
