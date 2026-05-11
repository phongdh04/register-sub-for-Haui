# Dev-Spec — F14 WebSocket realtime (thiết kế — chưa triển khai)

| Mã | F14 |
|----|-----|
| BA | [`ba_flow.md`](ba_flow.md) |
| Chuẩn đối thoại | [`cross/05_websocket_protocol.md`](../../cross/05_websocket_protocol.md) |
| Repo hiện tại | Không có `spring-boot-starter-websocket`; không có `sockjs` / `@stomp/stompjs` dependency trong frontend — chỉ backlog |

---

## 1) Tóm tắt backlog implement

### Backend (Spring Boot 4)

Dependency gợi ý Gradle/Maven:

- `spring-boot-starter-websocket`

Cấu hình mẫu (pseudo — không trong repo):

- `@EnableWebSocketMessageBroker`
- `configureMessageBroker(registry)` đăng ký `/topic`, `/queue`, relay nếu cần
- Đăng ký stomp endpoints `/ws` và CORS whitelist dev

**Security**:

- Extend `AbstractSecurityWebSocketMessageBrokerConfigurer` **hoặc** cơ chế tương đương Spring Security cho WebSocket
- Principal từ `JwtAuthenticationConverter` tái dùng JWT REST

---

## 2) Event sources → push trigger

Không chỉ một nguồn — backlog nên unify **ApplicationEvent**:

| Event / hook | Chuẩn bị adapter |
|----------------|-------------------|
| `RegistrationConfirmedEvent` | Sau commit — publish user queue lightweight DTO |
| Kafka consumer success path | Tránh duplicate: idempotency key join với `@TransactionalEventListener` strategy |
| `RegistrationMonitoringServiceImpl` | Không ép chạy trên socket — thay bằng scheduled task push snapshot hash |
| Ingress Go | Optional bridge HTTP internal → Redis pub/sub subscribed bởi Java |

Rate-limit aggregator trước khi vào Socket broker (token bucket trong service).

---

## 3) Client (Vite/React)

Khuyến nghị thư viện:

```bash
npm i @stomp/stompjs sockjs-client
```

Pattern:

```javascript
const client = new Client({
  webSocketFactory: () => new SockJS(`${API_BASE_URL}/ws`),
  connectHeaders: { Authorization: `Bearer ${token}` },
});
```

Subscribe sau `onConnect`; cleanup `client.deactivate()` trên logout.

Chi tiết STOMP heartbeat / reconnect: C05.

---

## 4) Mapping schema JSON (proposal)

Đặt trong `reports/thesis/templates` hoặc OpenAPI không áp được — chỉ ví dụ:

```json
{
  "schemaVersion": 1,
  "type": "REGISTRATION_OUTCOME",
  "payload": {
    "registrationId": 9001,
    "outcome": "SUCCESS",
    "idLopHp": 42,
    "occurredAt": "2026-05-09T10:02:03.451Z",
    "idempotencyKey": "evt-..."
  }
}
```

Type enum mở rộng dần; client **reject unknown nhưng ignore** không crash.

---

## 5) Test strategy (khi code có)

| Layer | Cách |
|-------|------|
| Unit | Serialize DTO websocket |
| Integration | `@SpringBootTest` + `MessagingTemplate` trong memory broker |
|E2E | Playwright chờ toast sau push giả |

---

## 6) Operational checklist

| Check | Detail |
|-------|--------|
| Sticky không bắt buộc nếu dùng external broker (Rabbit/Stomp Relay) |
| TLS termination tại Ingress; `ws` → `wss` |
| Log sampling — không trace full JWT |

---

## 7) Dependencies chéo tài liệu

| ID | Topic |
|----|-------|
| C06 | JWT leak, ROLE matrix |
| C07 | SLO P95 delivery push |
| F05 | Fallback REST metrics |
| F10 | UX sau đăng ký |

---

## 8) Lịch sử

- 2026-05 Draft.
- 2026-05 Bổ sung proposal schema, libs, checklist vận hành.
