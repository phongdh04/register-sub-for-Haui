# C04 — API Catalog

Danh mục HTTP API phục vụ nghiệp vụ đăng ký học phần và biên giới (ingress/hàng đợi). Nguồn sự thật chính: controller trong `backend-core` và handler trong `backend-queue`.

---

## 1) Quy ước chung

### 1.1 Base URL và cổng mặc định dev

| Dịch vụ | Mặc định | Ghi chú |
|---------|----------|---------|
| `backend-core` (Spring Boot) | `http://localhost:8080` | Đặt qua `server.port`; frontend thường trỏ `VITE_API_BASE_URL`. |
| `backend-queue` (Fiber Go) | `http://localhost:3000` | `SERVER_PORT` env, mặc định `3000`. |

### 1.2 Xác thực JWT (Java)

Header bắt buộc với các route protected:

```http
Authorization: Bearer <access_token>
```

Token lấy từ luồng `POST /api/auth/login` (và MFA nếu bật). Role trong JWT là authority dạng `ROLE_STUDENT`, `ROLE_ADMIN`, `ROLE_LECTURER` (đồng bộ với `UserDetailsImpl.build`).

### 1.3 CORS (Java)

`WebSecurityConfig` cho phép origin dev Vite: `http://localhost:5173`, `http://127.0.0.1:5173`. Service Go Queue dùng CORS permissive trong `main.go` (production nên siết).

### 1.4 Định dạng lỗi (Java — `GlobalExceptionHandler`)

| Tình huống | HTTP | Body chính |
|------------|------|-------------|
| Not found (`EntityNotFoundException`) | 404 | `timestamp`, `status`, `error`, `message`. |
| Nghiệp vụ không hợp lệ (`IllegalArgumentException`) | 400 | Cùng cấu trúc trên với `message` tiếng Việt. |
| Forbidden RBAC (`AccessDeniedException`) | 403 | `message`: "Bạn không có quyền ...". |
| Validation `@Valid` (`MethodArgumentNotValidException`) | 422 | `status: 422`, `error`: "Validation Failed", `details`: map field → message. |
| `ResponseStatusException` | theo status | `reason` vào `message`. |
| Lỗi chưa phân loại | 500 | `message`: "Lỗi hệ thống: ...". |

Một số controller/service trả **`409 Conflict`** hoặc **`422 Unprocessable Entity`** cho xung đột nghiệp vụ (`ResponseStatusException`).

### 1.5 Định dạng lỗi (Go — Fiber handler)

Pattern phổ biến:

```json
{"status":"ERROR","message":"<mô tả>"}
```

Một endpoint trả thân `DangKyResponse` với `status` ∈ `OK | FULL | DUPLICATE | REJECTED | ERROR`; HTTP status gắn theo đó (409 cho FULL/DUPLICATE/REJECTED, 503 cho ERROR nghiêm trọng).

### 1.6 `permitAll` vs `authenticated` (Java filter chain)

Được khép cứng trong `WebSecurityConfig`:
- **`/api/auth/**`**, **`/api/public/**`**: không cần JWT.
- **GET** `/api/khoa/**`, `/api/nganh-dao-tao/**`, `/api/hoc-phan/**`, `/api/hoc-ky/**`, `/api/giang-vien/**`, `/api/lop-hoc-phan/**`: công khai đọc.
- Mọi request khác: **authenticated**, sau đó `@PreAuthorize` quyết định chi tiết.

### 1.7 Ánh xạ Frontend SPA (đã có UI)

Ánh xạ **chỉ ghi các màn trong `frontend/src/pages/` đang gọi API** để làm catalog thiết kế: API nào **đã** có điểm bám trong SPA thì ghi đường dẫn + file; ngược lại **– / chưa có trong màn lõi** — **không** đặt backlog thiết kế giao diện mới trong tài liệu này.

Bảng tổng hợp nhanh: **Annex A** (cuối file).

---

## 2) Auth

| Method | Path | Auth | Mô tả |
|--------|------|------|--------|
| POST | `/api/auth/login` | public | Username/password → JWT hoặc challenge MFA admin. |
| POST | `/api/auth/mfa/verify` | public | OTP sau MFA. |

> **Frontend (đã có):** [`frontend/src/pages/ngNhpTruynThngQunLPhin.jsx`](../../../frontend/src/pages/ngNhpTruynThngQunLPhin.jsx) · route **`/login`**.

Chi tiết tham khảo `features/F01_auth/` và `AuthController`.

---

## 3) Public pre-registration (tuyển sinh / biểu mẫu công khai)

Base: `/api/public/v1/pre-reg`.

| Method | Path | Auth | Mô tả ngắn |
|--------|------|------|------------|
| GET | `/links/{token}` | public | Metadata link cố định. |
| POST | `/links/{token}/submit` | public | Nộp hồ sơ → 202 Accepted. |
| GET | `/requests/{requestId}` | public | Trạng thái UUID request. |

> **Frontend:** — *chưa có page trong [`frontend/src/pages/`](../../../frontend/src/pages/) gọi trực tiếp nhóm public pre-reg trong phạm vi cleanup luận văn; không mô tả thiết kế UI mới ở đây.*

---

## 4) Học kỳ và master-read (đăng ký cần)

Base: **`/api/hoc-ky`**.

| Method | Path | Auth | Mô tả |
|--------|------|------|--------|
| GET | `/` | public | Danh sách học kỳ. |
| GET | `/hien-hanh` | public | Học kỳ hiện hành. |
| GET | `/{id}` | public | Chi tiết. |
| POST | `/` | ADMIN | Tạo. |
| PATCH | `/{id}/kich-hoat` | ADMIN | Kích hoạt HK làm hiện hành. |
| DELETE | `/{id}` | ADMIN | Xóa. |

> **Frontend (đã có / gọi GET):**
> - **Student:** [`StudentRegistrationPage.jsx`](../../../frontend/src/pages/StudentRegistrationPage.jsx) (`/student/registration`), [`TnhNngTrcGiGPreRegistrationGiLp.jsx`](../../../frontend/src/pages/TnhNngTrcGiGPreRegistrationGiLp.jsx), [`DchVThiKhaBiuThngMinh.jsx`](../../../frontend/src/pages/DchVThiKhaBiuThngMinh.jsx) (`/student/timetable`) — dropdown/tải danh mục HK.
> - **Admin lõi đăng ký:** các trang cửa sổ, demand, publish, projection, monitoring, cấu hình HK (xem Annex A).
> - **ADMIN POST/PATCH/DELETE /api/hoc-ky:** không thấy gọi từ các page lõi đăng ký hiện tại — chỉnh qua Swagger/công cụ khác.

Các GET master khác được mở ở mục 1.6 (khoa, ngành, học phần, lớp học phần list theo học kỳ, giảng viên).

---

## 5) Lớp học phần (catalog thô)

Base: **`/api/lop-hoc-phan`**.

| Method | Path | Auth | Ghi chú đăng ký |
|--------|------|------|-----------------|
| GET | `/hoc-ky/{idHocKy}` | public | Danh sách lớp theo kỳ (UI đăng ký). |
| GET | `/{id}` | public | Chi tiết một section. |
| POST, PUT, DELETE, publish… | các path còn lại | chủ yếu ADMIN | Quản trị và kill-switch (xem controller đầy đủ). |

> **Frontend (đã có):**
> - **GET** `/hoc-ky/{idHocKy}`: [`AdminClassPublishPage.jsx`](../../../frontend/src/pages/AdminClassPublishPage.jsx) — tải lớp để assign/publish (**route** `/admin/class-publish`).
> - **GET** `{id}` (chi tiết) và các PUT/DELETE/publish **`/api/lop-hoc-phan` cũ**: có thể tồn tại chỗ khác trong repo — **spa lõi chỉ consume** chủ yếu qua **`/api/v1/courses`** và admin publish (`/api/v1/admin/lop-hoc-phan`).
> - **Đăng ký SV không gọi** GET catalog thô trực tiếp trong `StudentRegistrationPage` (ưu tiên `/api/v1/courses`).

---

## 6) Course search (tìm lớp theo filter + phân trang)

Base: **`/api/v1/courses`**.

| Method | Path | Auth | Mô tả |
|--------|------|------|--------|
| GET | `/` | `STUDENT`, `ADMIN`, `LECTURER` | Query: `keyword`, `idHocKy`, filters, pagination. |
| GET | `/{id}` | tương tự | Chi tiết. |

**Ghi chú**: `CourseSearchController` dùng `hasAnyRole('STUDENT','ADMIN','LECTURER')` — khớp authority JWT `ROLE_*` của giảng viên trong seed/demo.

> **Frontend (đã có):** [`StudentRegistrationPage.jsx`](../../../frontend/src/pages/StudentRegistrationPage.jsx) · [`TnhNngTrcGiGPreRegistrationGiLp.jsx`](../../../frontend/src/pages/TnhNngTrcGiGPreRegistrationGiLp.jsx) (**GET** `/` + có thể chi tiết từng lớp tùy implement).

---

## 7) Registration suggestions (SV)

Base: **`/api/v1/registration/suggestions`**.

| Method | Path | Auth | Mô tả |
|--------|------|------|--------|
| GET | `/me` | STUDENT | Gợi ý lớp theo khối/ngành/CTĐT; `hocKyId` optional. |

> **Frontend:** — *chưa có page lõi trong `frontend/src/pages` gọi endpoint này; **không** mở backlog UI trong catalog.*

---

## 8) PRE — Giỏ nháp (cart)

Base: **`/api/v1/pre-reg/cart`**.

| Method | Path | Auth | Body / query |
|--------|------|------|---------------|
| GET | `/me` | STUDENT | `hocKyId` optional |
| POST | `/items` | STUDENT | `{"idLopHp":..., "hocKyId":...?}` |
| POST | `/blocks` | STUDENT | `PreRegCartAddBlockRequest` |
| DELETE | `/items/{idGioHang}` | STUDENT | Path id giỏ |

> **Frontend:** — *SPA đăng ký lõi không gọi cart; chỉ có flow **intent** (§9).*

---

## 9) PRE — Intent (nguyện vọng theo học phần)

Base: **`/api/v1/pre-registrations/intents`**.

| Method | Path | Auth | Mô tả |
|--------|------|------|--------|
| GET | `/me` | STUDENT | `hocKyId` optional — danh sách intent của tôi |
| POST | `/` | STUDENT | `{ idHocKy, idHocPhan, priority?, ghiChu? }` |
| PUT | `/{intentId}` | STUDENT | Cập nhật cùng schema |
| DELETE | `/{intentId}` | STUDENT | Xóa |

> **Frontend (đã có):** [`TnhNngTrcGiGPreRegistrationGiLp.jsx`](../../../frontend/src/pages/TnhNngTrcGiGPreRegistrationGiLp.jsx) (**route hiện tại** trong `App.jsx`: `/student/tnhnngtrcgigpreregistrationgilp`, alias `intents-legacy`; roadmap cleanup: **`/student/pre-registration`** — xem `reports/ui_cleanup_*.md`).

---

## 10) Đăng ký chính thức / slot (REST sync — Java)

Base: **`/api/v1/registrations`**.

| Method | Path | Auth | Query / note |
|--------|------|------|----------------|
| GET | `/me` | STUDENT | `hocKyId` optional — danh sách môn đã đăng ký kỳ |
| GET | `/me/window-status` | STUDENT | Trạng thái PRE/OFFICIAL theo cohort+ngành + gợi ý debug |
| POST | `/` | STUDENT | `idLopHp` bắt buộc, `hocKyId` optional (mặc định HK hiện hành) |
| DELETE | `/{idDangKy}` | STUDENT | Hủy đăng ký của chính mình |

Chi tiết nghiệp vụ và validation trong `features/F10_student_official_registration/dev_spec.md`.

> **Frontend (đã có):** [`StudentRegistrationPage.jsx`](../../../frontend/src/pages/StudentRegistrationPage.jsx) — `GET /me/window-status`, `GET /me`, `POST /`, `DELETE /{idDangKy}`. (**Route** `/student/registration`).

---

## 11) Thời khóa biểu (SV)

Base: **`/api/v1/timetable`**.

| Method | Path | Auth | Mô tả |
|--------|------|------|--------|
| GET | `/me` | STUDENT | TKB aggregate (logic cũ + service). |
| GET | `/me/snapshot` | STUDENT | Bắt buộc `hocKyId` — đọc read-model projection. |

> **Frontend (đã có):** [`DchVThiKhaBiuThngMinh.jsx`](../../../frontend/src/pages/DchVThiKhaBiuThngMinh.jsx) (**route** `/student/timetable` — gọi cả `/me` và `/me/snapshot`).

---

## 12) Admin — Cửa sổ đăng ký cohort

Base: **`/api/v1/admin/registration-windows`**.

| Method | Path | Auth | Query / body |
|--------|------|------|----------------|
| GET | `/` | ADMIN | `hocKyId`, `phase?` PRE\|OFFICIAL |
| GET | `/{id}` | ADMIN | |
| POST | `/` | ADMIN | `RegistrationWindowUpsertRequest` + `createdBy` implicit |
| PUT | `/{id}` | ADMIN | Cùng upsert schema |
| DELETE | `/{id}` | ADMIN | |
| POST | `/open-now` | ADMIN | `RegistrationWindowOpenNowRequest` |

> **Frontend (đã có):** [`AdminRegistrationWindowsPage.jsx`](../../../frontend/src/pages/AdminRegistrationWindowsPage.jsx) — CRUD + `open-now`; kèm gọi `GET /api/nganh-dao-tao`, `POST .../force-publish-all` (**route** `/admin/registration-windows`).

---

## 13) Admin — Lịch đăng ký fallback trên học kỳ (legacy path)

Base: **`/api/v1/admin/hoc-ky`**.

| Method | Path | Auth | Body |
|--------|------|------|------|
| PUT | `/{id}/lich-dang-ky` | ADMIN | `HocKyLichDangKyRequest` — 4 mốc Instant optional theo cặp |

> **Frontend (đã có):** [`AdminHocKyScheduleConfigPage.jsx`](../../../frontend/src/pages/AdminHocKyScheduleConfigPage.jsx) — `GET /api/hoc-ky/{id}` + **`PUT`** `.../lich-dang-ky` (**route** `/admin/lichdangkyhockyconfig`).

---

## 14) Admin — Công bố lớp (publish workflow)

Base: **`/api/v1/admin/lop-hoc-phan`**.

| Method | Path | Auth |
|--------|------|------|
| POST | `/{idLopHp}/assign-giang-vien` | ADMIN |
| POST | `/{idLopHp}/publish` | ADMIN |
| POST | `/bulk-publish?hocKyId=` | ADMIN |
| POST | `/force-publish-all?hocKyId=` | ADMIN (demo/accelerator) |

> **Frontend (đã có):** [`AdminClassPublishPage.jsx`](../../../frontend/src/pages/AdminClassPublishPage.jsx) — assign GV, publish, bulk-publish (**route** `/admin/class-publish`).
> **`force-publish-all`** cũng được gọi từ [`AdminRegistrationWindowsPage.jsx`](../../../frontend/src/pages/AdminRegistrationWindowsPage.jsx).

---

## 15) Admin — Pre-reg links quản trị

Base: **`/api/v1/admin/pre-reg/links`**.

| Method | Path | Auth |
|--------|------|------|
| POST | `/` | ADMIN |
| GET | `/` | ADMIN |
| PUT | `/{linkId}/close` | ADMIN |
| GET | `/{linkId}/stats` | ADMIN |

> **Frontend:** — *không có màn lõi trong `frontend/src/pages` cho nhóm **`/api/v1/admin/pre-reg/links`**.*

---

## 16) Admin — Nhu cầu PRE intent (aggregation)

Base: **`/api/v1/admin/pre-registrations`**.

| Method | Path | Auth | Query |
|--------|------|------|--------|
| GET | `/demand` | ADMIN | `hocKyId`; optional `namNhapHoc`, `idNganh`, `targetClassSize` |

> **Frontend (đã có):** [`AdminPreRegistrationDemandPage.jsx`](../../../frontend/src/pages/AdminPreRegistrationDemandPage.jsx) — **`GET /demand`** + `GET /api/nganh-dao-tao` (**route** `/admin/pre-registration-demand`).

---

## 17) Admin — Monitoring đăng ký (registration_request_log)

Base: **`/api/v1/admin/registration-monitoring`**.

| Method | Path | Auth | Query |
|--------|------|------|--------|
| GET | `/outcomes` | ADMIN | `from`, `to` ISO-8601 optional |
| GET | `/throughput` | ADMIN | tương tự |
| GET | `/fill-rate` | ADMIN | **bắt buộc** `hocKyId`; optional `from`/`to` |

> **Frontend (đã có):** [`AdminRegistrationMonitoringPage.jsx`](../../../frontend/src/pages/AdminRegistrationMonitoringPage.jsx) — outcomes / throughput / fill-rate (**route** `/admin/registration-monitoring`).

---

## 18) Admin — Rebuild timetable projection

Base: **`/api/v1/admin/timetable-projection`**.

| Method | Path | Auth | Query |
|--------|------|------|--------|
| POST | `/rebuild` | ADMIN | `sinhVienId`, `hocKyId` |

> **Frontend (đã có):** [`AdminTimetableProjectionToolsPage.jsx`](../../../frontend/src/pages/AdminTimetableProjectionToolsPage.jsx) (**route** `/admin/timetable-projection-tools`).

---

## 19) MFA admin (ngoài lõi ĐKHP nhưng hay dùng cùng portal)

Đường dẫn `GET /api/v1/admin/mfa/status`, `PUT /api/v1/admin/mfa/settings` (`hasRole ADMIN`). Chi tiết `CLAUDE.md` task 22.

> **Frontend (đã có — ngoài scope lõi ĐKHP):** [`XcThcaYuTMfa2FaVChKS.jsx`](../../../frontend/src/pages/XcThcaYuTMfa2FaVChKS.jsx); **Đăng nhập MFA:** cùng trang **`/login`**. *(Roadmap `ui_cleanup` có thể ẩn màn MFA admin khỏi menu — không tạo tài liệu thiết kế mới ở đây.)*

---

## 20) Backend-queue — Ingress hàng đợi và slot Redis

Base context path: **`/api/v1/queue`** (Fiber).

| Method | Path | Body JSON (Go field) | HTTP đặc biệt |
|--------|------|---------------------|---------------|
| POST | `/dang-ky` | `id_sinh_vien`, `id_lop_hp`, `id_hoc_ky` | 409 khi FULL/DUPLICATE/REJECTED |
| DELETE | `/huy-dang-ky` | cùng 3 field | |
| GET | `/slot/:id_lop_hp` | — | Slot còn lại từ Redis |
| POST | `/pre-reg` | `requestId`, `linkId`, `dedupeKey`, ... | Ingress pre-reg từ core |

Admin Go (song song):

| POST | `/api/v1/admin/khoi-tao-slot` | `id_lop_hp`, `si_so_toi_da` | Nạp slot Redis trước giờ G |

Kafka topic mặc định: `eduport.dang-ky-hoc-phan`, `eduport.pre-registration-submitted` (env `KAFKA_TOPIC`, `KAFKA_PREREG_TOPIC`).

> **Frontend:** — *Ingress Go (`localhost:3000`) **không** được SPA lõi trong `frontend/src/pages` gọi trực tiếp; đăng ký hiển thị trên **`StudentRegistrationPage`** chỉ đi **`/api/v1/registrations`** (Java).*

---

## 21) Phạm vi ngoài catalog này

- Hàng chục controller admin scheduling/solver/finance không liệt kê đầy đủ ở đây; chỉ cần khi mở rộng luận văn.
- WebSocket: **chưa có endpoint** — thiết kế mục tiêu ở `cross/05_websocket_protocol.md`.

---

## 22) Tra cứu nhanh theo feature ID

| Feature | Endpoints chính |
|---------|-----------------|
| F02 | §12, §13, `GET /api/v1/registrations/me/window-status` |
| F03 | §14 |
| F04 | §16 |
| F05 | §17 |
| F06 | §18 |
| F07 | §13 |
| F08 | §8, §9 |
| F09 | §5, §6 |
| F10 | §10, §20 `/dang-ky` |
| F11 | `DELETE /api/v1/registrations/{id}`, Go `huy-dang-ky` |
| F12 | §11 |

---

## 23) Phụ lục — ví dụ `curl`/PowerShell thống nhất (copy-paste)

### 23.1 Lấy JWT (ADMIN hoặc SV)

```powershell
$body = @{ username = "sv01"; password = "123456" } | ConvertTo-Json
$r = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/auth/login" -Body $body -ContentType "application/json"
$token = $r.token
$hdr = @{ Authorization = "Bearer $token" }
```

*(Nếu response là MFA challenge, dùng luồng `mfa/verify` — xem [`features/F01_auth/dev_spec.md`](../features/F01_auth/dev_spec.md).)*

### 23.2 Kiểm tra cửa đăng ký SV

```
Invoke-RestMethod -Headers $hdr `
  -Uri "http://localhost:8080/api/v1/registrations/me/window-status?hocKyId=1"
```

### 23.3 Đăng ký REST đồng bộ một lớp

```
Invoke-WebRequest -Headers $hdr -Method Post `
  -Uri "http://localhost:8080/api/v1/registrations?idLopHp=10&hocKyId=1" `
  | Select-Object -ExpandProperty StatusCode  # mong đợi 201 nếu hợp lệ
```

### 23.4 Ingress Go (burst test đơn lẻ)

```powershell
$q = @{ id_sinh_vien = 1; id_lop_hp = 10; id_hoc_ky = 1 } | ConvertTo-Json -Compress
Invoke-RestMethod -Method Post -Uri "http://localhost:3000/api/v1/queue/dang-ky" -Body $q -ContentType "application/json"
```

### 23.5 Quy ước query Spring Data Page (`/api/v1/courses`)

| Param | Kiểu | Default | Max size gợi ý |
|-------|------|---------|----------------|
| `page` | int | `0` | — |
| `size` | int | `20` | `100` trong controller annotation |
| `sortBy` | string | lesson plan | chỉ các field được phép whitelist service |
| `sortDir` | `ASC`|`DESC` | varies | |

Pagination response `Page<CourseSearchResponse>` (Spring):

| JSON field (Jackson) quen thuộc | Ý |
|----------------------------------|--|
| `content` | mảng phần tử trang hiện tại |
| `totalElements`, `totalPages` | thống kê |

### 23.6 Lỗi 422 validation body (canonical)

HTTP 422 ví dụ (rút gọn):

```json
{
  "timestamp": "2026-05-09T10:01:02.123456",
  "status": 422,
  "error": "Validation Failed",
  "details": {
    "idHocKy": "must not be null"
  }
}
```

---

## Annex A — Ánh xạ nhanh SPA ↔ API (phạm vi catalog §2–§21)

Chỉ các màn trong `frontend/src/pages` **đang gọi** endpoint thuộc các mục trên; không liệt kê admin scheduling/finance/analytics v.v. Route lấy từ [`frontend/src/App.jsx`](../../../frontend/src/App.jsx) (path đầy đủ = prefix portal + path dưới đây).

| § | Route SPA | File | Endpoint / nhóm API chính (rút gọn) |
|---|-----------|------|-------------------------------------|
| §2 | `/login` | [`ngNhpTruynThngQunLPhin.jsx`](../../../frontend/src/pages/ngNhpTruynThngQunLPhin.jsx) | `POST /api/auth/login`, `POST /api/auth/mfa/verify` |
| §4 | `/student/*` (dropdown HK) | [`StudentRegistrationPage.jsx`](../../../frontend/src/pages/StudentRegistrationPage.jsx) … | `GET /api/hoc-ky` |
| §4, §13 | `/admin/lichdangkyhockyconfig` | [`AdminHocKyScheduleConfigPage.jsx`](../../../frontend/src/pages/AdminHocKyScheduleConfigPage.jsx) | `GET /api/hoc-ky`, `GET /api/hoc-ky/{id}`, `PUT /api/v1/admin/hoc-ky/{id}/lich-dang-ky` |
| §4 | `/admin/*` (nhiều màn lõi đăng ký) | [`AdminRegistrationWindowsPage.jsx`](../../../frontend/src/pages/AdminRegistrationWindowsPage.jsx) và các admin page đăng ký khác | `GET /api/hoc-ky` |
| §5, §14 | `/admin/class-publish` | [`AdminClassPublishPage.jsx`](../../../frontend/src/pages/AdminClassPublishPage.jsx) | `GET /api/lop-hoc-phan/hoc-ky/{id}`, `/api/v1/admin/lop-hoc-phan/...` |
| §6, §10 | `/student/registration` | [`StudentRegistrationPage.jsx`](../../../frontend/src/pages/StudentRegistrationPage.jsx) | `GET /api/v1/courses`, `GET/POST/DELETE /api/v1/registrations…`, `GET …/me/window-status` |
| §6, §9 | `/student/tnhnngtrcgigpreregistrationgilp` (alias `…/intents-legacy`) | [`TnhNngTrcGiGPreRegistrationGiLp.jsx`](../../../frontend/src/pages/TnhNngTrcGiGPreRegistrationGiLp.jsx) | `GET /api/v1/courses`, `/api/v1/pre-registrations/intents…`; `GET /api/hoc-ky` |
| §11 | `/student/timetable` hoặc `/student/dchvthikhabiuthngminh` | [`DchVThiKhaBiuThngMinh.jsx`](../../../frontend/src/pages/DchVThiKhaBiuThngMinh.jsx) | `GET /api/v1/timetable/me`, `GET /api/v1/timetable/me/snapshot`; `GET /api/hoc-ky` |
| §12 | `/admin/registration-windows` | [`AdminRegistrationWindowsPage.jsx`](../../../frontend/src/pages/AdminRegistrationWindowsPage.jsx) | CRUD `/api/v1/admin/registration-windows`, `POST …/open-now`; kèm `GET /api/nganh-dao-tao`, `POST …/force-publish-all` |
| §14 | *(cùng §12)* | [`AdminRegistrationWindowsPage.jsx`](../../../frontend/src/pages/AdminRegistrationWindowsPage.jsx) | `POST /api/v1/admin/lop-hoc-phan/force-publish-all` |
| §16 | `/admin/pre-registration-demand` | [`AdminPreRegistrationDemandPage.jsx`](../../../frontend/src/pages/AdminPreRegistrationDemandPage.jsx) | `GET /api/v1/admin/pre-registrations/demand`; `GET /api/nganh-dao-tao` |
| §17 | `/admin/registration-monitoring` | [`AdminRegistrationMonitoringPage.jsx`](../../../frontend/src/pages/AdminRegistrationMonitoringPage.jsx) | `GET /api/v1/admin/registration-monitoring/outcomes|throughput|fill-rate` |
| §18 | `/admin/timetable-projection-tools` | [`AdminTimetableProjectionToolsPage.jsx`](../../../frontend/src/pages/AdminTimetableProjectionToolsPage.jsx) | `POST /api/v1/admin/timetable-projection/rebuild` |
| §19 | `/admin/xcthcayutmfa2favchks` | [`XcThcaYuTMfa2FaVChKS.jsx`](../../../frontend/src/pages/XcThcaYuTMfa2FaVChKS.jsx) | `GET/PUT /api/v1/admin/mfa/status|settings` |

**Không có màn SPA catalog cho:** §3 (public pre-reg), §7 (suggestions), §8 (cart), §15 (admin pre-reg links), §20 (Go queue trực tiếp) — đã ghi rõ từng mục trong phần trên.

