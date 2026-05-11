# BA-Flow — F15 Kiến trúc hàng đợi chịu tải (Go Ingress + Redis + Kafka + Java)

| Mã | F15 |
|----|-----|
| Vai trò | SYSTEM (+ Sinh Viên là người bấm, nhưng không “thấy” Kafka) |
| Repo | **`backend-queue`** (Go Fiber), **`backend-core`** (consumer Java) |
| Liên quan | F05 (`registration_request_log`), F16 (validation Kafka), F10 REST song song |

---

## 1) Vấn đề nghiệp vụ

Giờ mở đăng ký có thể có **burst** HTTP đồng thời. Nếu mọi request đập trực tiếp vào Postgres:

- connection pool exhaustion;
- race `si_so_thuc_te`;
- UX timeout dài.

Kiến trúc đồ án tách một **Ingress siêu nhẹ**:

1. Kiểm tra nhanh + **atomically** chiếm “vé” chỗ trong **Redis**.
2. **Publish** tin nhắn tiêu chuẩn sang **Kafka**.
3. Trả HTTP **ngay** (OK / FULL / …) chứ không chờ PostgreSQL hoàn thành toàn pipeline.

Đăng ký **chốt vào Postgres** được thực hiện async bởi Java consumer transactional.

---

## 2) Thành phần và trách nhiệm

| Thành phần | Nhiệm vụ BA |
|-----------|--------------|
| **Go Fiber** (`backend-queue`) | HTTP API `/api/v1/queue/dang-ky`, nhận `id_sinh_vien`, `id_lop_hp`, `id_hoc_ky`; map status ngắn gọn cho client |
| **Redis** | Lưu “slot remaining” keyed theo section; `DECR` atomic — **Ingress gate** đầu tiên |
| **Kafka** | Hàng đợi không mất tin (replay, consumer group scaling) topic mặc định ví dụ `eduport.dang-ky-hoc-phan` |
| **Java consumer** [`RegistrationKafkaConsumer`](../../../../backend-core/src/main/java/com/example/demo/component/RegistrationKafkaConsumer.java) | Idempotent + validate + **`increment`** DB giống nghiệp vụ REST + **`RegistrationConfirmedEvent`** |

---

## 3) Tiền điều kiện vận hành demo

| # | Kiểm tra |
|---|----------|
| 1 | Redis up, key slot đã **seed** (admin Go `POST /api/v1/admin/khoi-tao-slot`). |
| 2 | Kafka broker + topic tồn tại khớp env `backend-queue` và `SPRING_*` consumer Java. |
| 3 | `backend-core` consumer bật; DB migration đăng ký + log Sprint 4. |

---

## 4) Luồng đăng ký chính (happy path Go)

```
SV/Client --> POST Ingress /dang-ky JSON
Ingress --> Redis atomic slot decrement (available > 0)
Ingress --> Produce Kafka JSON (DangKyMessage)
Ingress --> HTTP 200/... DangKyResponse { status OK }
(Java async) Consumer --> Idempotency key?
         --> Official window checker
         --> F16 full validation chain
         --> increment Postgres si_so
         --> insert DangKyHocPhan
         --> RegistrationConfirmedEvent + registration_request_log SUCCESS
```

**Lưu ý nhất quán dữ liệu**: Ingress đã trừ Redis **trước** khi Java xác nhận; nếu Java FAIL sau đó có thể lệch Redis ↔ DB (**đã ghi trong dev_spec / luận văn như một trade-off của prototype**).

---

## 5) Luồng hủy đối xứng

`tương tự`:
- Ingress `DELETE /api/v1/queue/huy-dang-ky` có thể **INCR Redis** và publish message cancel/trace.
- Consumer Java [`processCancellation`](../../../../backend-core/src/main/java/com/example/demo/service/impl/DangKyHocPhanServiceImpl.java): idempotent (`CANCEL`/trace prefix), đặt `RUT_MON`, decrement DB, **`RegistrationCancelledEvent`**, log `CANCELLED` hoặc `REJECTED` nếu không tìm thấy dkhp.

*(REST hủy trực tiếp không qua Ingress — đối chiếu F11.)*

---

## 6) Phạm vi bảo vệ và hạn chế (thành thật cho thesis)

| Hạn chế | Impact |
|---------|--------|
| Ingress prototype **thiếu JWT** | Bất cứ ai truy cập URL đều giả được id SV — chỉ được phép demo LAN / chapter “future work”. |
| Không có pathway PRE trong Kafka consumer | PRE chỉ qua REST / intent — phù hợp quyết định product hiện tại. |

---

## 7) GIVEN-WHEN-THEN

| ID | Rule |
|----|------|
| F15-R1 | **GIVEN** Redis slot về **0** **WHEN** Ingress request mới **THEN** HTTP báo FULL (không gửi message DB success). |
| F15-R2 | **GIVEN** Java consumer replay cùng idempotency key **WHEN** message trùng **THEN** không double insert dkhp (read log prior). |

---

## 8) Acceptance

- [ ] `POST /dang-ky` trả nhanh mà không block transaction Postgres.
- [ ] Consumer ghi được `SUCCESS` và F05 có dòng trong `registration_request_log` với đúng `requestType`/outcome.
- [ ] Hủy qua Ingress phát sinh **`RegistrationCancelledEvent`** (projection F06/F12 có thể dựa vào đây).

---

## 9) Cross refs

| Tài liệu | § |
|-----------|---|
| [`cross/04_api_catalog.md`](../../cross/04_api_catalog.md) | § Ingress Go |
| [`cross/06_rbac_security.md`](../../cross/06_rbac_security.md) | Gap auth Go |

---

## 10) Lịch sử

| Ngày | |
|------|--|
| 2026-05 | Draft |
| 2026-05 | Mô tả end-to-end, trade-off Redis/DB, hủy symmetry |
