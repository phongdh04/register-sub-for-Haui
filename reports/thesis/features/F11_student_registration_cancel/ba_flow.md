# BA-Flow — F11 Hủy / rút môn sau đăng ký (STUDENT)

| Mã | F11 |
|----|-----|
| Vai trò | STUDENT |
| Dev | [`dev_spec.md`](dev_spec.md) |
| Tiền đề | Sinh viên đã có bản **`dang_ky_hoc_phan`** trong trạng thái được phép (`THANH_CONG`, `CHO_DUYET`) |
| Hai kênh | **REST đồng bộ** (trong SPA) và **Ingress Go/Kafka** (F15) |

---

## 1) Mục đích

Cho phép sinh viên **rút môn đã đăng ký**, trả lại chỗ cho lớp (`si_so_thuc_te`), đồng thời cập nhật trạng thái bản đăng ký trong DB **đến mức nghiệp vụ lõi**:

- không còn được tính là “đang theo học” trong các query timetable / conflict check (theo semantics `findRegisteredCoursesInSemester`).
- các hệ thống phụ (TKB projection đọc model, monitoring, …) có thể dựa trên domain event — **tùy kênh** (REST vs Kafka có **khác** — §6).

---

## 2) Actor

| Ai | Việc |
|-----|-----|
| Sinh Viên chủ phiếu | Gọi hủy trên học của chính mình |
| Hệ điều hành (Java) | Cập nhật DB và (nếu path Kafka) phát **`RegistrationCancelledEvent`** |
| Admin | Theo dõi chỉ số trong F05; không chỉnh tay trường hợp thường |

---

## 3) Tiền điều kiện

- JWT `ROLE_STUDENT` + đã resolve profile `sinh_vien`.
- Bản **`idDangKy`** là của user hiện tại.
- **`trangThaiDangKy` ∈ { `THANH_CONG`, `CHO_DUYET` }** — các state khác bị **`409 Conflict`**.

---

## 4) Hậu điều kiện (REST path — [`RegistrationServiceImpl.cancel`](../../../../backend-core/src/main/java/com/example/demo/service/impl/RegistrationServiceImpl.java))

| Thay đổi | Chi tiết |
|----------|-----------|
| Trạng thái HK | **`RUT_MON`** được set |
| Capacity | **`decrementSiSoThucTe(idLopHp)`** của lớp liên quan |

**Lưu ý codebase (2026-05)**: path REST hiện **không publish** [`RegistrationCancelledEvent`](../../../../backend-core/src/main/java/com/example/demo/event/RegistrationCancelledEvent.java) trong `cancel()` — chỉ **`processCancellation`** (Kafka consumer) có `eventPublisher.publishEvent(...)`.

---

## 5) Luồng UI (REST)

1. Sinh Viên vào danh sách đã đăng ký.
2. Bấm *Hủy* một dòng.
3. Client `DELETE /api/v1/registrations/{idDangKy}`.
4. Xuất **`204 No Content`** khi thành công.

---

## 6) Luồng Ingress (mirror F15)

1. Gateway/Client POST/DELETE Ingress `huy-dang-ky` với IDS.
2. Java `processCancellation` tìm bản dkhp ACTIVE theo trio SV+LHP+HK.
3. Cập nhật `RUT_MON`, decrement, **ghi log**, **publish `RegistrationCancelledEvent`** để các listener (**F06 projection**, event logger**) chạy `AFTER_COMMIT`.

---

## 7) Exceptions (REST)

| HTTP | Điều kiện |
|------|------------|
| 403 | Bản không thuộc SV |
| 404 | `idDangKy` không có |
| 409 | Trạng thái không hủy được |
| 204 | Thành công |

Kafka path dùng log outcome thay cho HTTP UX.

---

## 8) GIVEN-WHEN-THEN

| ID | Rule |
|----|------|
| F11-R1 | **GIVEN** SV A **WHEN** gọi hủy id của SV B **THEN** 403 |
| F11-R2 | **GIVEN** trạng thái không phải success/pending approve **THEN** không decrement |
| F11-R3 | **GIVEN** hủy qua ingress **THEN** có event cancel phục vụ projection downstream |

---

## 9) Wireframe ASCII

```
┌ Đăng ký của tôi — HK 2 ──────────────────────────────┐
│ □ DS001 Lập trình Web   [Đang học    ] [Hủy / Rút ]   │
│ □ TK002 Toán SS2        [Chờ duyệt   ] [Hủy / Rút ]   │
└────────────────────────────────────────────────────────┘
```

---

## 10) Acceptance

- [ ] `DELETE` chỉ STUDENT được phép và ownership enforced.
- [ ] Hai lần hủy cùng bản không increment đôi chỗ (**idempotent không** được implement ở REST — chỉ báo đồ án là user không nên double click trước khi có UX disable).
- [ ] Ingress path phát **`RegistrationCancelledEvent`** (projection test).

---

## 11) Lịch sử

| Ngày | |
|------|--|
| 2026-05 | Khởi tạo ngắn |
| 2026-05 | Ghi nhận bất đối xứng event REST vs Kafka |
