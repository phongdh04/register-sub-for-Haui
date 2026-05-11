# C02 — Architecture Overview

Tài liệu mô tả kiến trúc tổng thể của hệ thống đăng ký học phần EduPort, đủ chi tiết để dev cấp thấp hiểu được toàn cảnh trước khi đi vào tài liệu chức năng.

---

## 1) Mục tiêu kiến trúc

- Phục vụ ổn định 3 vai trò `STUDENT`, `LECTURER`, `ADMIN` trong toàn bộ vòng đời đăng ký học phần.
- Chịu được tải cao điểm "giờ vàng" với hàng nghìn request/giây ngắn hạn.
- Cập nhật realtime trạng thái slot, kết quả đăng ký và metrics vận hành qua WebSocket.
- Đảm bảo dữ liệu nhất quán: không double-register, không vượt sĩ số, không mất dữ liệu khi peak.

---

## 2) Thành phần hệ thống (component diagram)

```
                         ┌──────────────┐
                         │  Frontend    │  React + Vite + WebSocket
                         │  (3 portals) │
                         └──────┬───────┘
                                │ HTTPS / WSS
                                │
       ┌────────────────────────┼─────────────────────────┐
       │                        │                         │
       ▼                        ▼                         ▼
┌────────────┐         ┌────────────────┐        ┌────────────────┐
│ Nginx /    │         │ Backend Java   │        │ Backend Go     │
│ API GW     │────────▶│ Spring Boot 4  │◀──────▶│ Fiber Queue    │
│            │         │ - REST + WS    │ Kafka  │ - Redis lock   │
└────────────┘         │ - Validation   │ topic  │ - Producer     │
                       │ - Projection   │        └────────────────┘
                       └─────┬──────────┘
                             │
                ┌────────────┼────────────┐
                ▼            ▼            ▼
        ┌───────────┐  ┌─────────┐  ┌──────────┐
        │PostgreSQL │  │ Redis   │  │ Kafka    │
        │ - core DB │  │ - slot  │  │ - topics │
        └───────────┘  │ - cache │  └──────────┘
                       └─────────┘
```

Thành phần chính:
- **Frontend**: 3 portal SV/GV/Admin, dùng React, gọi REST + subscribe WebSocket.
- **Backend Java (`backend-core`)**: nguồn sự thật, viết bằng Spring Boot 4 + JPA + Spring Security + Spring Kafka + Spring Data Redis. Chứa REST controller, validation chain, persistence, projection và WebSocket gateway.
- **Backend Go (`backend-queue`)**: layer hấp thụ tải đỉnh, khoá slot atomic Redis, đẩy message Kafka.
- **PostgreSQL**: lưu trữ chính.
- **Redis**: lưu slot lớp, idempotency key, cache đọc, broker pub/sub cho WebSocket scale-out.
- **Kafka**: hàng đợi bất đồng bộ giữa Go và Java.

---

## 3) Hai luồng đăng ký song song

Hệ thống hiện hỗ trợ **hai con đường** đăng ký chính thức. Cần tài liệu hóa để chốt đường đi cho luận văn.

### 3.1 Sync REST path (Java direct)
```
SV --> POST /api/v1/registrations --> Java
Java --> Window check --> Validation chain --> Atomic UPDATE --> INSERT --> Event
```
- Ưu điểm: đơn giản, dễ debug, phù hợp tải vừa.
- Nhược: không hấp thụ tốt khi peak vài nghìn RPS đồng thời.

### 3.2 Async queue path (Go → Kafka → Java)
```
SV --> POST /api/v1/queue/dang-ky (Go)
Go --> Redis DECR slot --> Kafka publish
Java consumer --> Window check --> Validation --> DB write --> Event --> Log
```
- Ưu điểm: Go xử lý nhanh, Redis atomic, Kafka tách biệt back-end nặng.
- Nhược: eventual consistency vài giây, cần WebSocket báo kết quả về SV.

### 3.3 Quyết định cho luận văn
- **Mặc định**: dùng async queue path cho đăng ký chính thức (chịu tải cao).
- **Sync path**: giữ làm fallback hành chính (admin can thiệp), không khuyến khích cho UI sinh viên.
- Tài liệu chi tiết quyết định trong `features/F10_student_official_registration/` và `features/F15_high_load_queue_architecture/`.

---

## 4) Lớp logic backend (Java)

```
Controller (REST/WS)
   │
   ▼
Service interface
   │
   ▼
Service impl
   ├── ValidationChain (Chain of Responsibility)
   │     ├── DuplicateRegistrationHandler
   │     ├── ScheduleConflictHandler
   │     ├── PrerequisiteCourseHandler
   │     └── (extensible)
   │
   ├── Repository (JPA)
   │
   ├── DomainEventPublisher
   │     ├── RegistrationConfirmedEvent
   │     └── RegistrationCancelledEvent
   │
   └── WebSocket broadcast
         ├── /topic/lop/{idLop}
         └── /user/queue/registration
```

Nguyên tắc:
- Controller chỉ điều hướng và bind DTO, không chứa nghiệp vụ.
- Service impl chứa toàn bộ logic, dùng `@Transactional` rõ ràng.
- ValidationChain dễ thêm rule mới mà không sửa code cũ (OCP).
- Event publish dùng `ApplicationEventPublisher` của Spring, listener async cho projection và WebSocket.

---

## 5) Vòng đời đăng ký (state machine)

```
┌──────────┐        ┌──────────┐        ┌────────────┐
│  PRE     │───────▶│ OFFICIAL │───────▶│ POST       │
│ intent   │        │ register │        │ projection │
└──────────┘        └──────────┘        └────────────┘
     ▲                  │                    │
     │                  ▼                    ▼
     │            ┌──────────┐          ┌──────────┐
     │            │ CANCEL   │◀─────────│ TIMETABLE│
     │            └──────────┘          └──────────┘
     │
     └──── Admin opens PRE window
```

State chính:
- **PRE**: SV gửi nguyện vọng (intent), không khoá slot, admin tổng hợp nhu cầu.
- **OFFICIAL**: SV đăng ký thật, khoá slot Redis, ghi DB.
- **POST**: hệ thống cập nhật `student_timetable_entry` projection để SV xem TKB.
- **CANCEL**: SV hủy, hệ thống trả slot và xóa entry projection.

---

## 6) Realtime với WebSocket

### 6.1 Mục đích
- SV thấy ngay slot lớp giảm theo thời gian thực.
- SV nhận trạng thái request đăng ký không cần reload.
- Admin thấy throughput, error rate, slot peak realtime.

### 6.2 Stack đề xuất
- Java: Spring `spring-boot-starter-websocket` + STOMP messaging broker (in-memory cho dev, hoặc bridge Redis pub/sub khi scale-out).
- Frontend: `@stomp/stompjs` + `sockjs-client`.

### 6.3 Channel chính
| Channel | Direction | Subscriber |
|---------|-----------|------------|
| `/topic/lop/{idLop}` | server → all | tất cả SV mở trang lớp đó |
| `/topic/admin/registration-monitoring` | server → admin | trang giám sát admin |
| `/user/queue/registration` | server → user cụ thể | SV vừa thao tác |

Chi tiết: `cross/05_websocket_protocol.md`.

---

## 7) Mô hình scale-out

### 7.1 Stateless backend
- Backend Java và Go đều stateless ở tầng HTTP/REST.
- State chia sẻ ở Redis và Postgres.
- Có thể chạy 2-N instance sau load balancer.

### 7.2 WebSocket scale-out
- Khi chạy nhiều instance Java, dùng broker bridge:
  - Option A: Redis pub/sub bridge cho STOMP.
  - Option B: dùng external broker RabbitMQ với STOMP plugin.
- Sticky session ở load balancer (theo `JSESSIONID` hoặc cookie hash) để tránh user nhảy instance.

### 7.3 Dòng dữ liệu khi peak
```
SV bursts → API gateway (rate limit per IP)
        → Go queue (Redis lock + Kafka publish)
        → Kafka partitions (3+ partitions, theo idLopHp hash)
        → Java consumers (concurrency=3+, scaling theo lag)
        → Postgres (UPDATE atomic)
        → Event → WebSocket → Redis pub/sub → các Java instance khác → SV
```

### 7.4 Backpressure và protect downstream
- Go layer áp `cooldown per student` (1-2s) qua Redis SET NX để chặn double-click bão.
- Kafka partition theo `idLopHp` để tránh hot partition.
- Postgres dùng `SELECT FOR UPDATE` chỉ ở row sĩ số lớp, không lock cả bảng.
- Idempotency key (UUID client) để retry không double-register.

---

## 8) Bảo mật

- JWT Bearer token cho mọi request (trừ `/api/auth/**`, `/api/public/**`).
- Role enum: `STUDENT`, `LECTURER`, `ADMIN` (hệ thống dùng prefix `ROLE_`).
- `@PreAuthorize("hasRole('STUDENT')")` ở từng endpoint nhạy cảm.
- WebSocket: handshake xác thực JWT trong header `Authorization` hoặc query token.
- CORS giới hạn origin frontend.
- Chi tiết: `cross/06_rbac_security.md`.

---

## 9) Quan sát (observability)

- Spring Actuator: `/actuator/health`, `/actuator/metrics`.
- Log structured JSON với `traceId`, `userId`, `idLopHp` khi có.
- Counter Prometheus:
  - `registration.success_total`
  - `registration.fail_total{reason}`
  - `registration.latency_seconds_bucket`
  - `ws.connections_active`
  - `kafka.consumer.lag`
- Trang admin monitoring đọc qua REST + WebSocket cập nhật.

---

## 10) Bảng phụ thuộc giữa các thành phần

| Tầng | Phụ thuộc bắt buộc | Phụ thuộc tuỳ chọn |
|------|---------------------|---------------------|
| Frontend | Backend Java | None |
| Backend Java | Postgres, Kafka, Redis | OR-Tools (solver, ngoài scope) |
| Backend Go | Redis, Kafka | Backend Java healthcheck |
| WebSocket | Backend Java + Redis pub/sub | Sticky session ở LB |

---

## 11) Sơ đồ deployment đề xuất cho luận văn

```
┌──────────┐
│  Nginx   │  443 (HTTPS) + WSS
└─────┬────┘
      │
┌─────┴────────────────────────────────┐
│  Backend Java x2 (Spring Boot)       │
│  (cùng instance Redis + Kafka)       │
└─────┬────────────────────────────────┘
      │
┌─────┴───────────┐  ┌────────────┐  ┌─────────────┐
│  Backend Go x2  │  │ PostgreSQL │  │ Kafka 3 brk │
└─────────────────┘  └────────────┘  └─────────────┘
                          │              │
                     ┌────┴──────────────┴────┐
                     │      Redis (1 node)    │
                     └────────────────────────┘
```

Cho demo luận văn, chấp nhận:
- 1 Postgres, 1 Redis, 1 Kafka broker đủ.
- 2 instance Java + 1 instance Go đủ minh hoạ scale-out.

---

## 12) Tài liệu tham chiếu

- `cross/03_db_dictionary.md` cho schema chi tiết.
- `cross/04_api_catalog.md` cho danh sách endpoint.
- `cross/05_websocket_protocol.md` cho channel và payload.
- `cross/07_performance_and_slo.md` cho mục tiêu hiệu năng.
- `features/F15_high_load_queue_architecture/` cho thiết kế chịu tải.

---

## 13) Ma trận nhất quán giữa REST sync và Ingress async (quan trọng cho luận văn)

| Khía cạnh | REST `POST /api/v1/registrations` | Ingress `POST /api/v1/queue/dang-ky` → Kafka consumer |
|-----------|------------------------------------|--------------------------------------------------------|
| Cửa PRE | Cho phép nếu PRE mở; validation rút gọn (bỏ tiên quyết) **theo RegistrationServiceImpl** | Consumer **không** mở path PRE |
| Slot | `incrementSiSoThucTe` transactional DB | Redis DECR trước, Java vẫn increment DB conditional |
| Idempotency log | Hiện **không** chuẩn hoá parity với kafka path | **Có** `RegistrationIdempotencyService` + outcome log |
| Domain event projection | Tuỳ commit (xem discrepancy F10 docs) | `RegistrationConfirmedEvent` sau SUCCESS |
| Phù hợp UX | Phản hồi đồng bộ ngay trong HTTP | Lag + cần pull/WS để báo nhận |
| Threat model demo | Theo JWT STUDENT như các API khác | Public ingress không JWT — chỉ LAN/trusted GW |

Không xem hai cột là tương đương vận hành; báo luận văn phải nêu rõ đường “chính thức trình chiếu” user chọn (thường Ingress + WS future).

---

## 14) Bảng chiến lược xử lý sự cố (ops mindset)

| Sự cố | Triệu chứng SV | Kiểm tra nhanh | Hành động khắc phục gợi ý |
|-------|----------------|-----------------|------------------------------|
| Window mismatch cohort | Banner “chưa đến…” dù đã nhập kỳ đúng | DB `registration_window`; SV `lop.nam_nhap_hoc`; unique index scope | Thu hẹp/mở window hoặc sửa seed SV |
| Slot Redis < DB | Ingress FULL nhưng DB còn chỗ | Redis GET vs `si_so_*` Postgres | Seed lại Redis `khoi-tao-slot` |
| Projection trống | Snapshot TKB không có tiết đã DK | Listener miss / JSON TKB empty | POST admin rebuild F06 |
| GV không xem được course | Dashboard GV 403 courses | Kiểm tra `@PreAuthorize` / token role | Align `LECTURER` + token hợp lệ (`CourseSearchController` đã đồng bộ) |
| Consumer lag spike | SV chờ lâu dù Ingress OK | `kafka-consumer-groups` lag | Scale consumer concurrency / partition sizing |

Chi tiết hành lệnh: [`cross/10_deployment_runbook.md`](10_deployment_runbook.md).

---

## 15) Chuẩn tracing & correlation cross-service

Đề xuất:

1. Ingress Go generate `trace_id` và gửi vào Kafka JSON (field `trace_id`).
2. Java consumer propagate sang log `registration_request_log` và application logs.
3. Frontend khi được (header `X-Request-Id`) map UI bug report.

Đồng bộ trong luận văn không bắt buộc bằng một chuẩn OpenTelemetry full — chỉ cần reproducible storyline.

