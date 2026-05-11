# Dev-Spec — F02 Quản lý cửa sổ đăng ký

| Trường | Giá trị |
|--------|---------|
| Mã chức năng | F02 |
| Tên chức năng | Quản lý cửa sổ đăng ký (registration_window + fallback HK) |
| Liên kết BA-Flow | `features/F02_admin_registration_window/ba_flow.md` |
| Module backend | `backend-core` |
| Module frontend | `frontend/src/pages/AdminRegistrationWindowsPage.jsx` (ước lượng) |
| Trạng thái | Draft |

---

## 1) Tóm tắt kỹ thuật

Dữ liệu cửa được lưu bảng `registration_window` (Postgres). **Service** đảm validate thời gian, scope cohort/ngành, và không trùng khoá logic. **Đọc cánh cửa mở/đóng** cho sinh viên đi qua `RegistrationScheduleChecker` với thứ tự ưu tiên specific→general và cuối cùng là **cặp Instant trên entity `HocKy`**. Admin có thể vẫn dùng `AdminHocKyScheduleController` để set fallback không theo cohort.

---

## 2) Phụ thuộc

### 2.1 Nội bộ

- `RegistrationWindowRepository` + method `findExistingScope`, listing admin.
- `HocKyRepository`, `NganhDaoTaoRepository`.
- `RegistrationScheduleChecker` tiêu thụ repositories trên trong consumer paths khác.

### 2.2 Ngoại vi

- Spring Security JWT.
- Hibernate mapping entity `RegistrationWindow`.

---

## 3) Domain model

### 3.1 Entity

| Tên | File | Mô tả |
|-----|------|--------|
| `RegistrationWindow` | `domain/entity/RegistrationWindow.java` | FK `hoc_ky`, FK optional `nganh_dao_tao`, phase PRE/OFFICIAL, timestamps. |
| `HocKy` | `domain/entity/HocKy.java` | Four Instant fields fallback. |

### 3.2 Enum

| Tên | Giá trị | Ý nghĩa |
|-----|---------|---------|
| `RegistrationPhase` | `PRE`, `OFFICIAL` | Map text DB `varchar` CHECK `'PRE'\|'OFFICIAL'`. |

### 3.3 DTO request/response quan trọng

#### `RegistrationWindowUpsertRequest`

| Field | Type | Ràng buộc |
|-------|------|-----------|
| `idHocKy` | Long | `@NotNull` |
| `phase` | `RegistrationPhase` | `@NotNull` |
| `namNhapHoc` | Integer | optional; có thì \[1900,2100] |
| `idNganh` | Long | optional; **cấm** khi không có cohort |
| `openAt` | Instant | `@NotNull`, `< closeAt` |
| `closeAt` | Instant | `@NotNull` |
| `ghiChu` | String | optional max 500 |

#### `RegistrationWindowOpenNowRequest`

| Field | Ghi chú |
|-------|---------|
| `idHocKy`, `phase` | `@NotNull` |
| `namNhapHoc`, `idNganh` | như UPSERT logic scope |
| `durationDays` | `@Min(1)` optional → default **30** |
| `ghiChu` | optional |

#### Response `RegistrationWindowResponse`

bao gồm `id`, meta HK, cohort, nganh IDs + tên snapshot, timestamps, và Boolean **`dangMo`** được tính `now ∈ [openAt, closeAt]` tại **thời điểm build response**.

---

## 4) DB schema & migration

Nguồn: `backend-core/src/main/resources/db/migration_registration_window_sprint1.sql`.

Bảng **`registration_window`**: PK BIGINT, FK `hoc_ky`, FK optional `nganh_dao_tao`, `phase` CHECK, timestamps, chỉ số UNIQUE functional trên `(id_hoc_ky, phase, COALESCE(cohort sentinel), COALESCE(nganh sentinel))`.

Đồ họa quan hệ: `cross/03_db_dictionary.md`.

---

## 5) API contract (Admin CRUD)

Base: `/api/v1/admin/registration-windows` (`AdminRegistrationWindowController`).

Auth: Bearer JWT `ROLE_ADMIN`.

### 5.1 Bảng tổng

| Method | URL | Success | Fail chính |
|--------|-----|---------|-----------|
| GET | `?hocKyId={id}&phase=PRE\|OFFICIAL` optional | 200 `List` | 400 thiếu hocKyId |
| GET | `/{id}` | 200 | 404 |
| POST | `/` body upsert | 201 | 400 validation, 409 duplicate |
| PUT | `/{id}` | 200 | 404, 409 |
| DELETE | `/{id}` | 204 | 404 entity |
| POST | `/open-now` body `OpenNow` | 201 | 400 scope invalid |

Payload JSON field names camelCase như Jackson mặc định của Spring Boot (`openAt`, `namNhapHoc`, ...).

### 5.2 Ví dụ `POST open-now`

```http
POST /api/v1/admin/registration-windows/open-now HTTP/1.1
Authorization: Bearer <jwt>
Content-Type: application/json

{
  "idHocKy": 1,
  "phase": "OFFICIAL",
  "namNhapHoc": 2023,
  "durationDays": 14,
  "ghiChu": "Mo dot bu cho K2023"
}
```

### 5.3 Ví dụ `POST create`

```json
{
  "idHocKy": 1,
  "phase": "PRE",
  "namNhapHoc": 2023,
  "idNganh": null,
  "openAt": "2026-06-01T00:00:00Z",
  "closeAt": "2026-06-07T23:59:59Z",
  "ghiChu": "PRE K2023 (tat ca nganh trong khoi)"
}
```

---

## 6) Fallback legacy học kỳ

Endpoint: **`PUT /api/v1/admin/hoc-ky/{id}/lich-dang-ky`**

Body `HocKyLichDangKyRequest`:
- Hai cặp optional: PRE (`preDangKyMoTu`, `preDangKyMoDen`), OFFICIAL (`dangKyChinhThucTu`, `dangKyChinhThucDen`).
- Quy tắc service: trong mỗi cặp, **hoặc cả hai null** (không giới hạn pha đó qua HK) **hoặc cả hai Instant** và from ≤ to.

---

## 7) Business logic (chi tiết)

### 7.1 `RegistrationWindowServiceImpl`

| Method | Hành vi |
|--------|---------|
| `create` | validatePayload + ensureNoDuplicate + save builder `createdBy` |
| `update` | không đổi id; check duplicate excluding self |
| `delete` | 404 guard |
| `openNow` | now + duration; upsert semantic qua repository `findExistingScope` |
| `list` | bắt buộc `hocKyId` |

### 7.2 Thời gian và zone

Kiểu `Instant` không mang timezone định phương — UI frontend nên annotate `Z` và convert local.

---

## 8) Thuật toán checker (consumer)

Đọc và implement future changes thống nhất **`RegistrationScheduleChecker`**:

```14:71:backend-core/src/main/java/com/example/demo/support/RegistrationScheduleChecker.java
 * - Window khớp (hocKy, phase, namNhapHoc, idNganh) trong bảng {@code registration_window}.
 * - Window khớp (hocKy, phase, namNhapHoc) — không quan tâm ngành.
 * - Window khớp (hocKy, phase) — toàn cohort.
 * - Fallback: cột {@code preDangKyMo*} / {@code dangKyChinhThuc*} trên {@link HocKy} (back-compat).
```

Sinh viên resolve cohort/ngành từ `sinh_vien → lop → namNhapHoc / nganh_dao_tao` (failure mode: không có cohort → debug message trong endpoint window-status của SV).

---

## 9) Edge cases QA

| Case | Mong đợi |
|------|----------|
| cohort null trên SV | Checker không có cohort context → chỉ có thể khớp window “toàn học kỳ” hoặc fallback HK; window-status có `debugReason` gợi ý. |
| Duplicate unique | 409 từ `ensureNoDuplicate` |
| PATCH HK legacy null | Checker coi không chặn theo học kỳ cho pha đó |

---

## 10) Events / realtime

Không có event dedicated cho F02 trong code hiện tại — thiết kế WS push **`WINDOW_CHANGED`** tại `cross/05_websocket_protocol.md`.

---

## 11) Tests gợi ý

- Unit: mapper `ensureNoDuplicate` edge excludeId null vs có.
- Integration: controller create → fetch list → DELETE.
- Checker: seeded windows với các bậc specificity.

---

## 12) Implement step-by-step (agent playbook)

1. Đảm migration đã áp Postgres (hoặc Hibernate sync dev).
2. Seed ít nhất 2 học kỳ, 2 cohort, và 3 window chồng không trùng.
3. Smoke `GET /api/v1/admin/registration-windows?hocKyId=1`.
4. Smoke `POST /open-now` rồi gọi `GET /api/v1/registrations/me/window-status` bằng JWT SV thuộc cohort phù hợp.
5. Verify conflict path bằng double submit create duplicate scope expecting 409.
