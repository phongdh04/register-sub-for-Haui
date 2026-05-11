# C07 — Performance & SLO (đặc tả tối đa — đo lường, kịch bản load, tuning)

Đây là tài liệu **thiết kế thí nghiệm hiệu năng** phục vụ luận văn. Các ngưỡng là **hypothesis** có thể bị chứng minh / bác bỏ bằng số đo thực tế — không trình bày như cam kết hợp đồng.

Tham chiếu: [`cross/05_websocket_protocol.md`](05_websocket_protocol.md) (giảm poll), [`features/F15`](../features/F15_high_load_queue_architecture/).

---

## 1) Đơn vị và định nghĩa metrix

| Ký hiệu | Định nghĩa |
|---------|-------------|
| RPS | Completed HTTP responses / giây tại LB |
| p50/p95/p99 latency | Theo histogram client-side observability hoặc server access log NGINX |
| Success HTTP | Codes 2xx trong context |
| Business success | Enrollment DB row persisted `THANH_CONG` or consumer outcome `SUCCESS` |
| Kafka lag | difference between newest offset và consumer committed offset aggregated |
| Warm hit | JVM + connection pool saturated steady-state |

---

## 2) Phân hoạch workload personas

### 2.1 Persona STUDENT_BURST

Ramp 3000 virtual users vào trong 120s như trong trường học nhỏ mở slot.

Characteristics:

- Majority action: `POST /queue/dang-ky` hoặc `POST /api/v1/registrations`.
- Read mix: `/queue/slot/:id` (poll) — degrade target by replacing WS (future).

### 2.2 Persona ADMIN_STEADY

Periodic GET monitoring endpoints không ảnh hưởng core path — chỉ background noise simulation 5–20 RPS.

### 2.3 Persona POLLING_POLLUTED

Worst-case anti-pattern: SV refresh slot 2Hz — must show why WS lowers DB/LB load.

---

## 3) SLO table chi tiết (version thesis)

|SLO-ID|Đối tượng|Metric|Hypothesis MVP|Đo|Sai lệch ghi vào báo|
|------|---------|------|----------------|-----|-----|
|SLO-JWT-avail|Spring `/api/auth/login`|success rate/week|≥99%|synthetic uptime| |
|SLO-catalog|GET HK + list LHP|p95 latency|≤250ms LAN|k6|`http_req_duration{name:catalog}` |
|SLO-queue-ingress|Go `POST /dang-ky`|p95≤250ms; error not crash|Đúng|k6+histogram influx|Exclude network WAN |
|SLO-queue-503|`ERROR`|rate < 0.5% stable cluster|Đúng|parse JSON statuses|infra saturation note|
|SLO-spring-register|REST register|p95≤750ms small DB|Đúng|micrometer|`registration.http` timer custom|
|SLO-kafka-consume lag|consumer group|p99≤2s|Đúng|kafka exporter|rebalance flagged|
|SLO-slot-integrity|`si_so_thuc_te` không vượt|0 violation|manual SQL asserts|stress B oversub|

---

## 4) Chuẩn bị fixture dữ liệu (deterministic benchmarking)

Steps:

1. Seed `hoc_ky` hiện hành và >=100 sections `lop_hoc_phan` each capacity between 40–120.
2. Ensure `publish` statuses align F03 realistic distribution.
3. Pre-create N student accounts mapped `sinh_vien` with coherent cohort/year.
4. Redis run `POST /admin/khoi-tao-slot` batch script aligning `si_so_toi_da`.

Record seed script version Git hash in thesis appendix reproducibility statement.

---

## 5) k6 scenario outline — Ingress path (pseudo script)

Pseudo JavaScript exported for appendix:

```
import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  scenarios: {
    burst: {
      executor: 'ramping-arrival-rate',
      startRate: 0,
      timeUnit: '1s',
      preAllocatedVUs: 500,
      maxVUs: 2000,
      stages: [
        { target: 50, duration: '30s' },
        { target: 250, duration: '2m' },
        { target: 0, duration: '30s' },
      ],
    },
  },
};

export default function () {
  const body = JSON.stringify({
    id_sinh_vien: __VU % 2999 + 1,
    id_lop_hp: (__VU % 97) + 1,
    id_hoc_ky: 1,
  });
  const res = http.post('http://localhost:3000/api/v1/queue/dang-ky', body, {
    headers: { 'Content-Type': 'application/json' },
  });
  check(res, { 'status_okish': r => [200, 409].includes(r.status) });
  sleep(0.05);
}
```

Replace IDs with scripted CSV parameterization reflecting real seeded dataset.

Parallel scenario `spring_registration.js`:

- Hits `8080`.
- Bearer token array loaded from CSV (secure local only).

---

## 6) Database tuning knobs (PostgreSQL guideline)

Không chỉnh production DB trong luận văn nhưng nêu lý thuyết:

| Knob area | Recommendation narrative |
|-----------|---------------------------|
| Connection pool (Hikari) | Formula `connections ≈ cores * (2 effective spindle)` heuristic; không vượt Postgres `max_connections` |
| Index | Ensure `(id_hoc_ky)`, `(id_lop_hp, id_sinh_vien composite unique enforcement)` migrations exist |
| Autovacuum | Monitor bloat nhật ký lớn `registration_request_log` |
| Transaction length | Reduce fan-out fetch joins in prerequisites handler |

Include `EXPLAIN ANALYZE` screenshots instruction for appendix.

---

## 7) Kafka tuning hints

| Parameter | Thesis guidance |
|-----------|----------------|
| Partition count topic `eduport.dang-ky-hoc-phan` | >= consumer threads; avoid hotspot single partition |
| Replication | min 3 prod; 1 acceptable demo |
| `linger.ms` & `batch.size` producers Go | Tune trade latency vs throughput |
| Retry / DLQ design | Mention future—not implemented perhaps |

---

## 8) Result capture template for thesis appendix chapter

Paste tables after each run Date:

```
Run ID: DKHP-LOAD-YYYYMMDD-HHMM
Scenario: Ingress_only
Kafka brokers: ...
Redis: ...

| Metric | Value |
| Ingress p95 | |
| Ingress p99 | |
| Spring p95 parallel control | ... |
| Error rate ingress | ... |
| Max consumer lag observed | ... |
| Slot integrity assertions | PASS/FAIL |
| Notes anomalies | [...]
```

---

## 9) Ethical testing constraints

Chú ý chỉ benchmark hạ tầng **thuộc quyền quản trị**, không probing hệ thống thật sinh hoạt trái phép hiệu chỉnh của trường.

---

## 10) Link to websocket mitigation

Polling slot at high frequency violates SLO in table—cross reference reduction plan.
