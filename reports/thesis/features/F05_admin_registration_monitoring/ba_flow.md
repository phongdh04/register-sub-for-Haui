# BA-Flow — F05 Giám sát đăng ký (Admin dashboard REST)

| Mã | F05 |
|----|-----|
| Vai trò | ADMIN (read-only) |
| Liên quan | F15 (Ingress/Kafka → `registration_request_log`), F02/F10 (đăng ký thực), F14 (WebSocket tương lai — F05 chỉ polling) |
| Dev | [`dev_spec.md`](dev_spec.md) |
| Cross | [`cross/04_api_catalog.md`](../../cross/04_api_catalog.md), [`cross/07_performance_and_slo.md`](../../cross/07_performance_and_slo.md) |

---

## 1) Vấn đề nghiệp vụ

Trong cửa **đăng ký đồng loạt** (giờ vàng), ban trị vụ phải trả lời nhanh:

- có bao nhiêu attempt **SUCCESS** vs **FULL** vs **DUPLICATE**?
- throughput theo kiểu request (ingress Go vs REST trực tiếp) ra sao?
- lớp nào gần **đầy** (`fill rate`), phục vụ quyết định **mở thêm section** hay truyền thông?

F05 không thay luồng đăng ký — chỉ **đọc log** và tổng hợp.

---

## 2) Actor

| Actor | Việc |
|-------|------|
| Quản trị viên đào tạo / IT vận hành | Mở dashboard, chọn khoảng thời gian, đọc biểu đồ/bảng |
| Hệ thống ghi log | F15 populates `registration_request_log` (mỗi attempt một dòng hoặc idempotent replay — theo implement) |

---

## 3) Tiền điều kiện

- JWT `ROLE_ADMIN`.
- Bảng `registration_request_log` đã migrate (Sprint 4).
- Với **fill-rate**: biết `hocKyId` (thường HK đang mở đăng ký).

---

## 4) Hậu điều kiện

- Chỉ phản hồi JSON tổng hợp — **không** ghi DB qua API F05.
- Admin có thể export snapshot (copy JSON / paste vào báo cáo luận văn) — ngoài implementation.

---

## 5) Luồng chính (polling)

1. Admin mở `AdminRegistrationMonitoringPage` (hoặc tương đương).
2. (Tuỳ chọn) chọn `from` / `to` dạng **ISO-8601 Instant** (UTC). Bỏ trống → hệ thống dùng **24 giờ gần nhất** (xem service).
3. Gọi **song song** hoặc lần lượt:
   - `GET .../outcomes`
   - `GET .../throughput`
4. Với tab “Lớp đầy”: chọn học kỳ → `GET .../fill-rate?hocKyId=`
5. UI vẽ:
   - pie/bar từ `byOutcome`
   - stacked table từ throughput `rows`
   - heat table fill rate theo `maLopHp`

---

## 6) Ý nghĩa metric (BA)

| Metric | Diễn giải cho hội đồng |
|--------|-------------------------|
| `successRate` | **SUCCESS** chia cho tổng **attempt** (SUCCESS, FULL, DUPLICATE, VALIDATION_FAILED, REJECTED). **CANCELLED** không vào mẫu số — vì không phải “cố gắng chiếm slot”. |
| Throughput | Gom theo cặp `(requestType, outcome)` — so sánh Go queue vs REST. |
| `fullEvents` (trong row fill-rate) | Số lần log outcome FULL trong window — khác với `siSoThucTe` tĩnh (realtime capacity). |

---

## 7) Ngoại lệ & giả định

| Case | Hiển thị |
|------|----------|
| Chưa có log trong window | Zero counts, charts empty — **không** báo đỏ “lỗi hệ thống” |
| Clock skew máy admin | Interpret `from`/`to` theo UTC nhất quán |
| `hocKyId` không tồn tại trong fill-rate | Theo DB query — có thể rỗng hoặc lỗi tùy impl repository (ghi nhận khi QA) |

**Giả định**: realtime push là F14; F05 chỉ REST **pull** tránh coupling.

---

## 8) Wireframe ASCII

```
┌── Giám sát đăng ký ────────────────────────────────────────┐
│ From [ datetime ]  To [ datetime ]   [Áp dụng]               │
├── Outcomes ──────────────────────────────────────────────────┤
│  total: 1240    successRate: 0.42    [chart byOutcome]       │
├── Throughput ──────────────────────────────────────────────┤
│  type=outcome │ count │                                      │
├── Fill rate (HK #2 ▼) ───────────────────────────────────────┤
│  Overall 78% │ table: lớp, fill%, FULL events               │
└──────────────────────────────────────────────────────────────┘
```

---

## 9) GIVEN-WHEN-THEN

| ID | Rule |
|----|------|
| F5-R1 | **GIVEN** không truyền from/to **WHEN** gọi outcomes/throughput **THEN** cửa sổ là 24h tính đến `now`. |
| F5-R2 | **GIVEN** gọi fill-rate **WHEN** không có hocKyId **THEN** HTTP 400 (Spring binding missing required Long). |

---

## 10) Acceptance criteria

- [ ] ADMIN-only (403 student).
- [ ] Default window outcomes = throughput behavior đồng nhất trong [`RegistrationMonitoringServiceImpl`](../../../../backend-core/src/main/java/com/example/demo/service/impl/RegistrationMonitoringServiceImpl.java).
- [ ] Fill-rate luôn yêu cầu query `hocKyId` (đã annotate controller).
- [ ] Empty data ≠ HTTP 500.

---

## 11) Phụ thuộc

- Chuẩn outcome: [`RegistrationOutcome`](../../../../backend-core/src/main/java/com/example/demo/domain/enums/RegistrationOutcome.java).

---

## 12) Lịch sử

| Ngày | Ghi chú |
|------|---------|
| 2026-05 | Khởi tạo |
| 2026-05 | Mở rộng UX, định nghĩa metric, wireframe |
