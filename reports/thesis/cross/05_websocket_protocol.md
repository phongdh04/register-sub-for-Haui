# C05 — WebSocket / STOMP protocol (thiết kế mục tiêu — đặc tả tối đa)

Repo **hiện chưa có** WebSocket/STOMP được khai báo trong `backend-core`, `backend-queue`, `frontend`. Tài liệu này là **RFC nội bộ** có **tính đủ chi tiết** để một dev junior hoặc agent triển khai end‑to‑end mà không cần làm khảo sát kiến trúc thêm.

Đồng bộ với: [`cross/02_architecture_overview.md`](02_architecture_overview.md), [`features/F14_realtime_websocket_channels/`](../features/F14_realtime_websocket_channels/), [`cross/06_rbac_security.md`](06_rbac_security.md).

---

## 1) Drivers & ràng buộc thực tế đồ án

### 1.1 Vấn đề kỹ thuật cần giải quyết

| ID | Pain | Minh họa UX | Workaround hiện tại trong repo |
|----|------|---------------|--------------------------------|
| WS-01 | Ingress async không trả DKHP trong cùng HTTP window | Spinner vô định | Manual refresh list hoặc poll `/registration/me` |
| WS-02 | Slot polling chi phí \(O(Nclients × pollRate × classes)\) | Admin lo DB rate limit | Fiber `GET /queue/slot/:id` polling |
| WS-03 | Dashboard admin F05 chỉ có REST | Charts lag vài giây | Periodic `fetch()` timer |
| WS-04 | Hai instance Java không share in-memory broker | Sai user instance subscription | Hiện chưa chạy multi-instance WS |

---

## 2) Lựa chọn stack và dependency

### 2.1 Backend Java (`backend-core`)

| Dependency Maven (gợi ý) | Vai trò |
|--------------------------|---------|
| `spring-boot-starter-websocket` | SockJS/STOMP servlet registration |
| (Optional) Spring Security Messaging | Authorize MESSAGE inbound |
| (Optional) Redis + Spring Integration / custom relay | Broker pub/sub đa instance |

Không được hard-code `@CrossOrigin('*')` trên SockJS handshake production — chỉ whitelist domain portal.

### 2.2 Frontend

| Package NPM | Phiên bản (chỉ dẫn) | Notes |
|-------------|---------------------|-------|
| `@stomp/stompjs` | `^7.x` | Client STOMP chính |
| `sockjs-client` | tuỳ chọn | Fallback nơi không có pure WS |

### 2.3 Scaling pattern

Two-level approach:

```
Client STOMP ---> Java instance #i (broker local simple broker)
                           │
              (publish internal app event)
                           ▼
                 Redis CHANNEL `eduport.ws.fanout`
                           ▼
Java instance #j SUBSCRIBE -> forward to các STOMP session local của user/sessionId
```

Nếu team nhỏ, **Sticky Session** chỉ LB + broker in-memory được chấp nhận báo luận với **disclaimer không HA**.

---

## 3) Phiên bản giao thức và namespace

### 3.1 `schemaVersion`

Mọi body JSON outbound:

| Phiên bản | Ghi nhận | Breaking examples |
|-----------|----------|-------------------|
| 1 | Current RFC | Rename field `lopHpId` |
| Bump major | Frontend must branch switch | Remove `payload` subtree |

---

## 4) STOMP handshake & security

### 4.1 URL endpoint

Đề xuất SockJS fallback:

```
https://{{host}}{{context}}/ws
```

SockJS handshake path sẽ thêm `/ws/{server-id}/{session-id}/websocket` fragments — document cho tester QA.

### 4.2 JWT vào CONNECT

| Cách | Ưu nhược |
|------|----------|
| Header `Authorization: Bearer` trong CONNECT extensions | Ưu: không lộ trong URL logs |
| Query `token=` | Dễ test browser nhưng lộ referrer logs LB |

Khuyến nghị: **Header** + SockJS bridging custom.

### 4.3 AuthZ per destination

Pseudo rules mapping role:

| Pattern | STUDENT | LECTURER | ADMIN |
|---------|---------|-----------|-------|
| `/user/queue/registration-result` | Chỉ chính mình (`Principal.name`) | — | ALL via impersonation không |
| `/topic/registrations/live/*` | — | Cho phép nếu cần (optional future) | Yes |
| `/user/queue/slot-watch.*` | Chỉ nếu `sinh_vien.id` resolves | Deny hoặc read-only GV class | Override debug |

Chi tiết implement: `@MessageMapping` không thay `@PreAuthorize` HTTP — cần `ChannelSecurityInterceptor`.

---

## 5) Destination catalog (canonical)

`/topic/` broadcast; `/queue/` user-queue (Spring prepend `/user/` automatically from client `/queue/xyz` subscriptions).

### 5.1 Server → Student

| Logical name | Canonical destination | Payload `type` chính | Rate limit |
|--------------|------------------------|-----------------------|-------------|
| Kết quả đăng ký cá nhân | `/user/queue/registration-result` | `REGISTRATION_FINALIZED`, `REGISTRATION_REJECTED` | 10/s/IP soft |
| Theo dõi chỗ một lớp | `/queue/slot-watch` + selective filter param | `SLOT_TICK` aggregated | Debounce ≥200ms/server |
| Cửa sổ thay đổi cohort | `/topic/windows/hoc-ky.{id}` sparse | `WINDOW_CHANGED` | Event-driven không flood |

Subscription client chỉ được open sau JWT verify.

### 5.2 Server → Admin

| Destination | Mục đích | Notes |
|-------------|----------|------|
| `/topic/registrations/live/summary` | outcome counts bucketed | không stream raw Kafka |
| `/topic/registrations/live/hoc-ky.{id}` | local campus filter | Aggregation server interval 2–5 s |

Admin STOMP có thể reuse token admin JWT long TTL — MFA already satisfied at login HTTP.

---

## 6) Envelope JSON chi tiết (field-level)

Schema chung wrapper:

```json
{
  "schemaVersion": 1,
  "type": "<ENUM_STRING>",
  "traceId": "string-non-empty",
  "occurredAt": "2026-05-09T08:01:05.121Z",
  "actorRole": "STUDENT",
  "payload": {}
}
```

### 6.1 `REGISTRATION_PENDING` (`payload`)

| Field | Bắt buộc | Kiểu | Ghi chú |
|-------|----------|------|---------|
| `idSinhVien` | có | number | Snapshot |
| `idLopHp` | có | number | |
| `idHocKy` | có | number | |

### 6.2 `REGISTRATION_FINALIZED`

| Field | Bắt buộc | Kiểu |
|-------|----------|------|
| `status` | có | `"CONFIRMED"` \| `"REJECTED"` |
| `idDangKy` | có nếu confirmed | number |
| `businessCode` | optional | Duplicate string của `RegistrationValidationException` flattened |
| `messageVi` | optional | Text UI |

### 6.3 `REGISTRATION_CANCELLED`

Giống confirmed nhưng `status=CANCELLED` + không `idDangKy` hoặc `idDangKy` historical.

### 6.4 `SLOT_TICK`

```json
{
  "lopHpId": 42,
  "slotRedis": 5,
  "slotDbApprox": null,
  "reason": "REDIS_MIRROR"
}
```

Không được coi authoritative DB — chỉ UX.

---

## 7) Server-side emission hooks (mapping code hiện tại)

Điểm gắn *sau implementation*:

| Existing Java artifact | Sau khi nào phát envelope |
|------------------------|-----------------------------|
| `RegistrationConfirmedEvent` listener NEW | AFTER_COMMIT SUCCESS DB |
| `RegistrationCancelledEvent` | analog |
| Fiber queue response `OK` không đồng nghĩa DB finalized — chỉ được phát **`REGISTRATION_PENDING`** từ Ingress qua bridging **nếu** Java relay trust network internal |

Để tránh double push, chọn một source of truth: **Java AFTER COMMIT**.

---

## 8) Frontend integration sequence (pseudo TypeScript đầy đủ các bước)

```
1. const token = localStorage.getItem('jwt')
2. const client = Stomp.over(new SockJS('/ws'))
3. client.connect({'Authorization':'Bearer '+token}, () => {
4.    client.subscribe('/user/queue/registration-result', (msg) => {
5.        const env = JSON.parse(msg.body)
6.        dispatchRedux(envelopeReceived(env))
7.    })
8. })
```

Disconnect trên logout: unsubscribe + deactivate + SockJS.close.

Reconnect strategy:

| Attempt | backoff | max |
|---------|---------|-----|
| 1 | immediate | |
| n | exponential min 250ms ×2 | cap 30000 |

---

## 9) Operational limits & abuse controls

| Control | Threshold gợi ý MVP |
|---------|---------------------|
| Max simultaneous WS per JWT | 3 |
| Max subscription topics per connection | 20 |
| Max inbound fake SUBSCRIBE to foreign `/user/` | Deny silently + audit log |
| Payload max | 8192 bytes hard reject |

---

## 10) Test matrix (minimal before merge feature)

| TC | Scenario | Expected |
|----|-----------|----------|
| T1 | STUDENT không token connect | CLOSE frame unauthorized |
| T2 | SV subscribes other user guessed queue | không nhận (security) |
| T3 | Event registration success | Received envelope < 1 s LAN |
| T4 | 10k broadcasts admin summary bucket | không CPU peg single thread (> profile) |

---

## 11) Traceability và logging

Outbound log INFO:

```
WS_SEND dest=/user/.../registration-result trace=<id> sv=<idSv> bytes=<n>
```

Không log full JWT substring.

---

## 12) Khoảng cách hiện trạng (checklist báo luận văn)

- [ ] Không có `WebSocketConfigurer` Java.
- [ ] Không dependency stomp trong `frontend/package.json`.
- [ ] Không SockJS CDN integration.
- Fallback operational: Poll REST + backoff exponential client-side recommendation.
