# Dev-Spec — F05 Admin registration monitoring REST

| Mã | F05 |
|----|-----|
| BA | [`ba_flow.md`](ba_flow.md) |
| Controller | [`AdminRegistrationMonitoringController`](../../../../backend-core/src/main/java/com/example/demo/controller/AdminRegistrationMonitoringController.java) |
| Base path | **`/api/v1/admin/registration-monitoring`** |
| Service | [`RegistrationMonitoringServiceImpl`](../../../../backend-core/src/main/java/com/example/demo/service/impl/RegistrationMonitoringServiceImpl.java) |
| Repository | [`RegistrationRequestLogRepository`](../../../../backend-core/src/main/java/com/example/demo/repository/RegistrationRequestLogRepository.java), [`LopHocPhanRepository`](../../../../backend-core/src/main/java/com/example/demo/repository/LopHocPhanRepository.java) (fill-rate) |

---

## 1) Tóm tắt kỹ thuật

- **READ-only** aggregate trong transaction `readOnly = true`.
- Cửa sổ thời gian được **chuẩn hoá** qua private `resolveWindow(from, to)`:  
  - cả hai null → **`[now-DEFAULT_WINDOW, now]`** với `DEFAULT_WINDOW = 24h`;
  - một tham số không hợp lệ hoặc `from > to` → có logic clamp/refuse — đọc nốt file service phần `Window`.
- **`successRate`**: \(\text{SUCCESS} / \sum(\text{ATTEMPT\_OUTCOMES})\).

---

## 2) Endpoint matrix

| Method | Path | Query | Role |
|--------|------|-------|------|
| GET | `/outcomes` | `from`, `to` optional ISO-8601 Instant | ADMIN |
| GET | `/throughput` | idem | ADMIN |
| GET | `/fill-rate` | **`hocKyId` required** Long; optional `from`, `to` | ADMIN |

`@DateTimeFormat(iso = ISO.DATE_TIME)` trên các `Instant`.

---

## 3) Response schema

### 3.1 `RegistrationOutcomeStatsResponse`

| Field | Kiểu | Ý |
|-------|------|---|
| `fromAt`, `toAt` | Instant | Cửa sổ thực tế đã clamp |
| `total` | long | Tất cả bản log trong cửa sổ (mọi outcome) |
| `byOutcome` | Map\<String,Long\> | key = `RegistrationOutcome.name()` |
| `successRate` | double | 4 chữ số thập phân (round trong service) |

### 3.2 `RegistrationThroughputResponse`

| Field | Ý |
|-------|---|
| `fromAt`, `toAt` | Window |
| `rows` | Mỗi phần tử: `requestType`, `outcome`, `count` |

*(Cấu trúc nested class `Row` trong cùng file Java.)*

### 3.3 `ClassFillRateResponse`

| Field | Ý |
|-------|---|
| `idHocKy` | học kỳ truyền vào |
| `totalClasses` | số lớp trong tập kết quả |
| `totalSlots` | \(\sum\) `si_so_toi_da` |
| `takenSlots` | \(\sum\) `si_so_thuc_te` |
| `overallFillRate` | tỷ lệ tổng |
| `rows[]` | `idLopHp`, `maLopHp`, `maHocPhan`, `tenHocPhan`, `siSoToiDa`, `siSoThucTe`, `fillRate`, `fullEvents` |

**Lưu ý**: `fullEvents` join từ log `topFullClasses` trong window — có thể >0 ngay cả khi `siSoThucTe` đã bão hoà từ trước window.

---

## 4) SQL / repository

Các method custom trên `RegistrationRequestLogRepository`:

- `aggregateByOutcome(from, to)`
- `aggregateByTypeAndOutcome(from, to)`
- `topFullClasses(from, to)`

Fill-rate: `lopHocPhanRepository.findFillRateRowsForHocKy(hocKyId)` — chi tiết projection cột xem implementation (Object[] index order match service loop).

---

## 5) Ví dụ PowerShell

```powershell
$hdr = @{ Authorization = "Bearer $adminToken" }
Invoke-RestMethod -Headers $hdr `
  "http://localhost:8080/api/v1/admin/registration-monitoring/outcomes"
Invoke-RestMethod -Headers $hdr `
  "http://localhost:8080/api/v1/admin/registration-monitoring/fill-rate?hocKyId=1"
```

---

## 6) Lỗi & status

| HTTP | Khi |
|------|-----|
| 403 | Non-admin |
| 400 | Thiếu `hocKyId` trên fill-rate |
| 200 | Luôn có body (có thể empty maps/lists) |

`hocKyId` không tồn tại: repository có thể trả list rỗng — **không** throw (verify khi maintain).

---

## 7) Test

[`RegistrationMonitoringServiceImplTest`](../../../../backend-core/src/test/java/com/example/demo/service/impl/RegistrationMonitoringServiceImplTest.java) — cover window + successRate denominator.

---

## 8) Checklist triển khai frontend

1. Debounce nút Refresh khi học kỳ đông.
2. Trục X thời gian dùng `fromAt/toAt` từ response (không tự đoán 24h nếu server clamp khác).

---

## 9) Lịch sử

- 2026-05 Draft.
- 2026-05 Chi tiết DTO + query + ví dụ gọi API.
