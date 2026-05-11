# BA-Flow — F14 Kênh WebSocket / STOMP realtime (thiết kế mục tiêu)

| Mã | F14 |
|----|-----|
| Vai trò | ALL cục bộ có lợi (STUDENT, ADMIN đặc biệt trong giờ vàng); LECTURER tuỳ sản phẩm |
| Trạng thái repo | **Chưa** có broker WebSocket triển khai — chức năng mô tả cho **định hướng luận văn** và thẳng ghép với `cross/05` |
| Chi tiết giao thức | [`cross/05_websocket_protocol.md`](../../cross/05_websocket_protocol.md) |
| Thay thế tạm thời | F05 polling REST — F10 chờ Kafka callback/poll |

---

## 1) Mục đích

Giảm tải **polling** và trải nghiệm “kéo tay” trong cửa sổ đăng ký đồng loạt:

| Stakeholder | Nhu cầu realtime |
|-------------|------------------|
| Sinh viên | Biết kết quả **ACCEPT/FULL/DUPLICATE** sau vài trăm ms khi Ingress + Kafka đã commit |
| Admin | Theo dõi throughput / spike **subscription** chứ không chỉ nút Refresh 2s một lần |
| Lecturer | (Tuỳ chọn) khi GV xem roster live — không bắt buộc F14 lõi |
| Ops | Chuẩn bị scaling nhiều pod Java nhờ Redis pub/sub backbone (thiết kế C02/C07) |

---

## 2) Đặc điểm kiến trúc (mục tiêu)

- **Inbound auth**: Bearer JWT trong `CONNECT` frame STOMP hoặc subprotocol handshake (chi tiết C05 § handshake).
- **Outbound destination** theo chuẩn đặt trong C05 (`/topic/...`, `/user/queue/...`).
- Envelope JSON **phiên bản hoá** (`schemaVersion`) để client cũ ignore field mới.
- Topic **broadcast** chỉ chứa dữ liệu không nhạy cảm; thông tin cá nhân SV qua **user queue**.

---

## 3) Catalogue kênh nghiệp vụ gợi ý

| Destination (mẫu) | Producer | Subscriber | Payload (ý) |
|-------------------|----------|------------|-------------|
| `/user/queue/registration-result` | Java sau commit / Kafka consumer mapping | STUDENT chủ owning | `{ registrationId, outcome, idLopHp, ts }` |
| `/topic/admin/registration-metrics` | Job aggregate hoặc stream processor | ADMIN dashboard | rollup counts (rate-limited ≥1 Hz) |
| `/topic/courses/{idLopHp}/slots` | Go ingress hoặc Java sau slot update Redis | STUDENT có thể không nên nhận toàn HK — chỉ khi UX cần | `remainingSlots` |

*(Tên cụ thể freeze khi có PR implement — không thay định luận văn mà chỉ appendix mapping.)*

---

## 4) Luồng nghiệp vụ điển hình (SV)

```
Client CONNECT + SUBSCRIBE /user/queue/registration-result
SV bấm Đăng ký Go ingress
Ingress -> Kafka -> Java consumer persists
Java push STOMP message về đúng user session
Toast UI không cần reload trang
```

---

## 5) Acceptance (kể cả tiêu chí không chức năng — khi code có)

### Chức năng
- [ ] Ít nhất **một** kênh cá nhân hoá và **một** kênh admin/broadcast được demo E2E.
- [ ] Khi JWT hết hạn → server **DENY** CONNECT với frame lỗi rõ; client redirect login.

### Bảo mật
- [ ] JWT **không** xuất hiện plain trong log Ingress proxy (Ingress header strip guideline — C06).

### Ingress / infra
- [ ] Ingress (Nginx/cloud) có `Upgrade` và `proxy_read_timeout` đủ dài — C02 troubleshooting.

---

## 6) Rủi ro & giới hạn

| Rủi ro | Kiểm soát |
|--------|-----------|
| Fan-out explosion | Aggregation + rate-limit server push |
| Kết nối rơi | Client STOMP heartbeat + backoff reconnect |
| Stale Redis slot vs DB | Chuẩn hoá Single Source of Truth (DB) và TTL cache |

---

## 7) Thay thế trước khi có F14

| Nhu cầu | workaround |
|---------|-------------|
| Kết quả đăng ký | Redirect poll `GET registrations` hoặc toast REST |
| Metrics | F05 endpoints chu kỳ 5–10 s |

---

## 8) Lịch sử

| Ngày | |
|------|--|
| 2026-05 | Draft ban đầu |
| 2026-05 | Bổ sung catalogue kênh, acceptance không chức năng, gap ingress |
