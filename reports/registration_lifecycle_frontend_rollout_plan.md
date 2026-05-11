# Frontend rollout plan - Registration lifecycle Sprint 1-6

Tai lieu nay la ke hoach code frontend cho cac API backend vua hoan tat o Sprint 1-6:
- Sprint 1: cohort-aware registration windows
- Sprint 2: pre-registration intents + demand
- Sprint 3: class publish lifecycle (SHELL/SCHEDULED/PUBLISHED)
- Sprint 4: idempotency + registration events (backend internal)
- Sprint 5: student timetable snapshot read-model
- Sprint 6: admin registration monitoring dashboard

## 1) API inventory theo man hinh frontend

### A. Student - Pre-registration (pha PRE)
- `GET /api/v1/pre-registrations/intents/me?hocKyId=`
- `POST /api/v1/pre-registrations/intents`
- `PUT /api/v1/pre-registrations/intents/{intentId}`
- `DELETE /api/v1/pre-registrations/intents/{intentId}`

Muc tieu UI:
- Sinh vien xem/sua danh sach nguyen vong dang ky du kien theo hoc ky.
- Co priority va feedback UX ro khi ngoai cua so PRE.

### B. Student - Timetable sau dang ky OFFICIAL
- `GET /api/v1/timetable/me` (API cu, parse JSON theo dang ky)
- `GET /api/v1/timetable/me/snapshot?hocKyId=` (API moi Sprint 5, doc read-model)

Muc tieu UI:
- Uu tien render `snapshot` de load nhanh va cap nhat gan real-time.
- Fallback ve `/me` neu can cho hoc ky cu/chua co projection.

### C. Admin - Registration windows
- `GET /api/v1/admin/registration-windows?hocKyId=&phase=` (`phase`: `PRE` | `OFFICIAL`)
- `GET /api/v1/admin/registration-windows/{id}`
- `POST /api/v1/admin/registration-windows`
- `PUT /api/v1/admin/registration-windows/{id}`
- `DELETE /api/v1/admin/registration-windows/{id}`

Muc tieu UI:
- Cau hinh cua so theo hoc ky + phase + cohort/major scope.
- Tranh overlap sai bang validate UI truoc submit.

### D. Admin - Demand tu pre-registration
- `GET /api/v1/admin/pre-registrations/demand?hocKyId=&namNhapHoc=&idNganh=&targetClassSize=`

Muc tieu UI:
- Bang demand theo hoc phan: totalIntent + recommendedClasses.
- Filter/sort de admin review nhanh.

### E. Admin - Class publish workflow
- `POST /api/v1/admin/lop-hoc-phan/{id}/assign-giang-vien`
- `POST /api/v1/admin/lop-hoc-phan/{id}/publish`
- `POST /api/v1/admin/lop-hoc-phan/bulk-publish?hocKyId=`

Muc tieu UI:
- Man hinh bulk publish theo hoc ky.
- Hien ro skip reasons trong bulk response.

### F. Admin - Timetable projection operations
- `POST /api/v1/admin/timetable-projection/rebuild?sinhVienId=&hocKyId=`

Muc tieu UI:
- Tool van hanh/recovery khi can rebuild read-model cho 1 sinh vien.

### G. Admin - Monitoring dashboard
- `GET /api/v1/admin/registration-monitoring/outcomes?from=&to=` (macro theo registration_request_log)
- `GET /api/v1/admin/registration-monitoring/throughput?from=&to=`
- `GET /api/v1/admin/registration-monitoring/fill-rate?hocKyId=&from=&to=` (`fill-rate` doi chieu luon voi LHP PUBLISHED cua hoc ky do; `fullEvents` lay tu FULL attempts trong window)

Muc tieu UI:
- Dashboard live state registration: success/fail/full, throughput, lop day slot.

## 2) De xuat route frontend

- Student:
  - `/student/pre-registration` (list + edit intents)
  - `/student/timetable` (tab: `Snapshot` mac dinh, `Legacy` optional)

- Admin:
  - `/admin/registration-windows`
  - `/admin/pre-registration-demand`
  - `/admin/class-publish`
  - `/admin/registration-monitoring`
  - `/admin/timetable-projection-tools`

## 3) File structure de xuat

- `frontend/src/features/registration/api.js`
- `frontend/src/features/registration/types.js`
- `frontend/src/features/registration/constants.js`
- `frontend/src/features/registration/hooks/useRegistrationWindows.js`
- `frontend/src/features/registration/hooks/usePreRegistrationIntents.js`
- `frontend/src/features/registration/hooks/useClassPublish.js`
- `frontend/src/features/registration/hooks/useRegistrationMonitoring.js`
- `frontend/src/features/registration/hooks/useTimetableSnapshot.js`
- `frontend/src/pages/student/PreRegistrationIntentsPage.jsx`
- `frontend/src/pages/student/TimetablePage.jsx`
- `frontend/src/pages/admin/AdminRegistrationWindowsPage.jsx`
- `frontend/src/pages/admin/AdminPreRegistrationDemandPage.jsx`
- `frontend/src/pages/admin/AdminClassPublishPage.jsx`
- `frontend/src/pages/admin/AdminRegistrationMonitoringPage.jsx`
- `frontend/src/pages/admin/AdminTimetableProjectionToolsPage.jsx`

## 4) Contract mapping nhanh (ready-to-code)

## 4.1 Windows

- Request create/update (`RegistrationWindowUpsertRequest` JSON):
  - `idHocKy` (required)
  - `phase` (`PRE` | `OFFICIAL`)
  - `namNhapHoc` (nullable; neu null = ap cho moi cohort trong scope hoc ky cho rule resolve)
  - `idNganh` (nullable; neu null = ap cho moi nganh trong cohort)
  - `openAt`, `closeAt` (required ISO-8601 `Instant`; du kien frontend convert tu datetime-local bang timezone ro rang)
  - `ghiChu` (optional, max 500)
- Response row (`RegistrationWindowResponse`):
  - `id`, `idHocKy`, `tenHocKy`, `phase`
  - `namNhapHoc`, `idNganh`, `tenNganh`
  - `openAt`, `closeAt`, `dangMo`
  - `ghiChu`, `createdBy`, `createdAt`, `updatedAt`

HTTP:
- POST create tra `201` + body (`RegistrationWindowResponse`).
- PUT update tra `200` + body.

UI rule:
- Neu `idNganh` co gia tri ma `namNhapHoc` null -> chan submit (dong bo backend rule).

## 4.2 Pre-registration intents

- Request submit/update (`PreRegistrationIntentSubmitRequest` JSON):
  - `idHocKy` (required)
  - `idHocPhan` (required)
  - `priority` (optional, min `1`; `null`/`omit` backend xu ly nhu muc do uu tien mac dinh)
  - `ghiChu` (optional, max 500)
- Response row (`PreRegistrationIntentResponse`):
  - `id`, `idSinhVien`, `idHocKy`, `tenHocKy`, `idHocPhan`, `maHocPhan`, `tenHocPhan`, `soTinChi`,
    `priority`, `ghiChu`, `createdAt`, `updatedAt`

UI rule:
- Chot optimistic update cho add/remove/sua priority.
- Neu backend tra ngoai cua so PRE -> banner warning + khoa thao tac.

## 4.3 Class publish

- Assign GV request (`LopHocPhanAssignGiangVienRequest`):
  - `idGiangVien` (required)

- Publish response (`LopHocPhanPublishResponse`):
  - `idLopHp`, `maLopHp`, `idHocKy`, `maHocPhan`, `tenHocPhan`
  - `idGiangVien`, `tenGiangVien`
  - `hasSchedule`
  - `statusPublish` (`SHELL` | `SCHEDULED` | `PUBLISHED`)
  - `version` (optimistic lock `Long`)
  - `message` (chu thich nghiep vu cho admin)

- Bulk publish response (`LopHocPhanBulkPublishResponse`):
  - `idHocKy`
  - `totalRequested`, `publishedCount`, `skippedCount`
  - `publishedIds` (`Long[]`)
  - `skipped[]`:
    - `idLopHp`, `maLopHp`, `reason`

UI rule:
- Bulk action phai co confirm modal.
- Sau bulk publish: toast tong hop `published/skipped`.

## 4.4 Timetable snapshot

### Legacy `/me` (`TimetableResponse`)
- Endpoint: `GET /api/v1/timetable/me?hocKyId=`
  - Neu `hocKyId` khong gui: backend resolve hoc ky `trangThaiHienHanh=true`.
- Shape:
  - `idSinhVien`, `maSinhVien`, `hoTenSinhVien`
  - `idHocKy`, `tenHocKy`
  - `tongMonDangKy`, `tongTinChi`
  - `courses[]` (`TimetableCourseResponse`):
    - `idDangKy`, `idLopHp`, `maLopHp`, `maHocPhan`, `tenHocPhan`, `soTinChi`, `tenGiangVien`
    - `sessions[]` (`TimetableSessionResponse`):
      - `thu`, `tiet`, `phong`
      - `ngayBatDau`, `ngayKetThuc` (backend tra `string`/`null`; parse o FE)

### Snapshot `/me/snapshot` (`StudentTimetableSnapshotResponse`)
- Endpoint: `GET /api/v1/timetable/me/snapshot?hocKyId=` (**bat buoc** `hocKyId`)
- Shape:
  - `idSinhVien`, `idHocKy`, `totalSlots`
  - `entries[]` (`StudentTimetableEntryResponse`):
    - `idDangKy`, `idLopHp`, `maLopHp`, `maHocPhan`, `tenHocPhan`, `tenGiangVien`
    - `thu`, `tiet`, `phong`
    - `ngayBatDau`, `ngayKetThuc` (`date` JSON: `yyyy-MM-dd` hoac `null`)
    - `slotIndex` (0-based index trong `thoiKhoaBieuJson` cua lop)

UI rule:
- Group theo `thu` va sap xep theo `tiet`.
- Auto-refresh nhe (15-30s) trong giai doan dang ky cao diem.

## 4.5 Monitoring

- Outcomes (`RegistrationOutcomeStatsResponse`):
  - `fromAt`, `toAt` (`Instant` ISO-8601)
  - `total` (tong request trong window)
  - `byOutcome` (map `string -> long`; key thuong la `SUCCESS`, `FULL`, `DUPLICATE`, `VALIDATION_FAILED`, `REJECTED`, `CANCELLED`, ...)
  - `successRate` (`double`, da round 4 so le; cong thuc backend: `SUCCESS / (SUCCESS+FULL+DUPLICATE+VALIDATION_FAILED+REJECTED)`; **khong** tinh `CANCELLED` vao mau so)

- Throughput (`RegistrationThroughputResponse`):
  - `fromAt`, `toAt`
  - `rows[]`:
    - `requestType` (`REGISTER` | `CANCEL` | `null` neu data cu)
    - `outcome` (`SUCCESS` | ... | `null`)
    - `count`

- Fill-rate (`ClassFillRateResponse`):
  - `idHocKy`
  - `totalClasses`
  - `totalSlots` (sum `siSoToiDa`)
  - `takenSlots` (sum `siSoThucTe`)
  - `overallFillRate` (`double`, round 4)
  - `rows[]`:
    - `idLopHp`, `maLopHp`, `maHocPhan`, `tenHocPhan`
    - `siSoToiDa`, `siSoThucTe`
    - `fillRate` (`double`, round 4)
    - `fullEvents` (dem log outcome `FULL` cho lop trong window monitoring)

UI rule:
- Time range preset: `15m`, `1h`, `24h`, `custom`.
- Dashboard cards + 2 bang:
  - Bang throughput
  - Bang top lop sap/full.

## 5) Ke hoach trien khai theo sprint frontend

## FE Sprint A (2-3 ngay) - Foundation + API layer

- Tao `features/registration/api.js` wrappers.
- Tao hooks query/mutation (khuyen nghi React Query).
- Them type mapping + error mapper.

Deliverable:
- API layer goi duoc tat ca endpoint Sprint 1-6.

## FE Sprint B (3-4 ngay) - Student experience

- `PreRegistrationIntentsPage`
- `TimetablePage` voi tab `Snapshot`
- Empty/loading/error states chuan.

Deliverable:
- Sinh vien thao tac duoc PRE intents.
- Xem TKB tu read-model snapshot.

## FE Sprint C (4-5 ngay) - Admin operations

- `AdminRegistrationWindowsPage`
- `AdminPreRegistrationDemandPage`
- `AdminClassPublishPage`
- `AdminTimetableProjectionToolsPage`

Deliverable:
- Admin cau hinh window, xem demand, publish lop, rebuild projection.

## FE Sprint D (3 ngay) - Monitoring dashboard + polish

- `AdminRegistrationMonitoringPage`
- Time filters + refresh interval + loading skeletons
- Export CSV (optional) cho fill-rate table.

Deliverable:
- Admin theo doi live quality registration flow.

## 6) UX va ky thuat quan trong

- Idempotency o backend da co, FE van can:
  - disable nut submit khi pending
  - chong double-click local
- Error mapping theo `errorCode`, khong show raw message backend.
- Tat ca admin pages dung `authHeaders()` hien co.
- Kiem soat timezone khi gui `from/to` (luon ISO UTC).
- Co fallback khi `snapshot` chua co data:
  - hien "Dang dong bo TKB..." + nut refresh.

## 7) Checklist release

- [ ] Route + menu cho tat ca pages moi
- [ ] API wrappers + unit tests cho mapping
- [ ] Student PRE intents done
- [ ] Student timetable snapshot done
- [ ] Admin windows CRUD done
- [ ] Admin demand page done
- [ ] Admin class publish single/bulk done
- [ ] Admin monitoring dashboard done
- [ ] Admin projection rebuild tool done
- [ ] E2E smoke test cho luong PRE -> OFFICIAL -> snapshot

## 8) Uu tien neu can cat scope

Neu can release nhanh theo 2 dot:

- Dot 1 (must-have):
  - Student PRE intents
  - Admin windows + demand + class publish
  - Student timetable snapshot

- Dot 2 (enhancement):
  - Monitoring dashboard
  - Projection rebuild tool UI
  - Export/reporting nang cao

---

## 9) Chi tiet JSON + huong UI (design-ready)

Muc tieu phan nay: FE co the ve wireframe/nguyen mau dua tren payload that, khong phai doan field.

### 9.1 Quy uoc chung (quan trong khi typing TypeScript va ve UI filter)

- **Encoding JSON**: camelCase (`idHocKy`, `openAt`, `statusPublish`, ...).
- **Thoi gian (`Instant`)**: ISO-8601 co zone, vi du `"2026-08-01T06:30:00Z"`.
- **Ngay (`LocalDate`)**: `"YYYY-MM-DD"` hoac `null`.
- **Enum string**:
  - `RegistrationPhase`: `"PRE"` | `"OFFICIAL"`
  - `LopHocPhanPublishStatus`: `"SHELL"` | `"SCHEDULED"` | `"PUBLISHED"`
  - `RegistrationOutcome` (monitoring/log): `"SUCCESS"` | `"DUPLICATE"` | `"FULL"` | `"VALIDATION_FAILED"` | `"REJECTED"` | `"CANCELLED"`
- **Auth**:
  - Student/admin endpoint deu can JWT header nhu codebase hien tai (`Authorization: Bearer ...`).
  - Student route lay username tu token giong cac endpoint khac.

### 9.2 Man Student - Pre-registration intents

#### `GET /api/v1/pre-registrations/intents/me`
- Query:
  - `hocKyId` optional; neu bo trong: FE nen luon truyen `hocKyId` luc da chon ky de tranh confusion.
- Tra ve: `PreRegistrationIntentResponse[]`

Vi du payload (dummy):

```json
[
  {
    "id": 12,
    "idSinhVien": 3,
    "idHocKy": 99,
    "tenHocKy": "HK1 - 2025-2026",
    "idHocPhan": 505,
    "maHocPhan": "INT2204",
    "tenHocPhan": "Lap trinh huong doi tuong",
    "soTinChi": 3,
    "priority": 1,
    "ghiChu": "Uu tien mon core",
    "createdAt": "2026-05-01T10:15:30Z",
    "updatedAt": "2026-05-06T07:41:02Z"
  }
]
```

Goi y UI/components:
- `HocKySelect` (dropdown) neu `hocKyId` khong fixed.
- `IntentTable`:
  - columns: priority (editable), maHocPhan, tenHocPhan, soTinChi, ghiChu, actions (edit/delete)
  - reorder priority bang drag-drop hoac spinner (PUT update)
- Empty state neu list rong + CTA “Them hoc phan”.

#### `POST /api/v1/pre-registrations/intents`
- Body (`PreRegistrationIntentSubmitRequest`):

```json
{ "idHocKy": 99, "idHocPhan": 505, "priority": 2, "ghiChu": "optional" }
```

- Tra ve: `PreRegistrationIntentResponse` + HTTP `201`

#### `PUT /api/v1/pre-registrations/intents/{intentId}`
- Body giong POST.

#### `DELETE /api/v1/pre-registrations/intents/{intentId}`
- Tra ve `204 No Content`.

Goi y error UX:
- Neu backend chan vi ngoai pha PRE: show banner dinh muc “Pha du kien chua mo / da dong”.
- Xu ly conflict duplicate unique (neu backend tra 409/400): toast + refresh list.

### 9.3 Man Student - Timetable (2 luong UI)

Tab A (prefer): **`GET /api/v1/timetable/me/snapshot?hocKyId=99`**

```json
{
  "idSinhVien": 3,
  "idHocKy": 99,
  "totalSlots": 4,
  "entries": [
    {
      "idDangKy": 1201,
      "idLopHp": 441,
      "maLopHp": "INT2204_01",
      "maHocPhan": "INT2204",
      "tenHocPhan": "Lap trinh huong doi tuong",
      "tenGiangVien": "Nguyen Van A",
      "thu": 2,
      "tiet": "1-3",
      "phong": "A.101",
      "ngayBatDau": "2026-09-01",
      "ngayKetThuc": "2026-12-15",
      "slotIndex": 0
    }
  ]
}
```

Goi y UI:
- `WeeklyGrid`:
  - group `entries` theo `thu` → render cell theo `tiet` text (tam thoi FE parse `"1-3"` manually hoac chi hien literal)
  - Tooltip/hover card: GV, phong, mon, ma lop, range ngay
- `StaleBanner` neu `totalSlots==0` nhung student dang co dang ky: offer nut “Refresh”, fallback tab legacy.

Tab B (fallback): **`GET /api/v1/timetable/me?hocKyId=99`**

```json
{
  "idSinhVien": 3,
  "maSinhVien": "SV001",
  "hoTenSinhVien": "Tran B",
  "idHocKy": 99,
  "tenHocKy": "HK1 - 2025-2026",
  "tongMonDangKy": 2,
  "tongTinChi": 6,
  "courses": [
    {
      "idDangKy": 1201,
      "idLopHp": 441,
      "maLopHp": "INT2204_01",
      "maHocPhan": "INT2204",
      "tenHocPhan": "Lap trinh huong doi tuong",
      "soTinChi": 3,
      "tenGiangVien": "Nguyen Van A",
      "sessions": [{ "thu": 2, "tiet": "1-3", "phong": "A.101", "ngayBatDau": "2026-09-01", "ngayKetThuc": "2026-12-15" }]
    }
  ]
}
```

So sanh UX:
- Snapshot phu hop real-time/grid manh hon (flat rows).
- Legacy phu hop “course-centric” accordion.

### 9.4 Man Admin - Registration windows CRUD

#### `GET /api/v1/admin/registration-windows`
- Required query `hocKyId`
- Optional `phase` (`PRE`/`OFFICIAL`)
- Tra ve `RegistrationWindowResponse[]`

#### `GET /api/v1/admin/registration-windows/{id}`
- Tra ve 1 ban ghi detail (dung cho deep-link tu bang).

#### Create/Update payloads (giong nhau schema)

```json
{
  "idHocKy": 99,
  "phase": "PRE",
  "namNhapHoc": 2021,
  "idNganh": 12,
  "openAt": "2026-05-01T00:00:00Z",
  "closeAt": "2026-05-07T23:59:59Z",
  "ghiChu": "K17 nganh CNPM"
}
```

Goi y UI:
- `ScopeExplainer`:
  - show chip “Global cohort/all majors” neu null fields tuong ung (theo semantics backend trong `RegistrationWindowUpsertRequest`)
- `ValidityBar`: progress line `openAt -> closeAt` + badge `NOW` vs window
- `StatusBadge`:
  - `dangMo=true` highlight xanh/ch do
  - false thi mute + reason “Sap mo / Het han” dua tren so sanh timestamp

HTTP:
- POST `201` + body
- PUT `200` + body
- DELETE `204`

### 9.5 Man Admin - Pre-registration demand

#### `GET /api/v1/admin/pre-registrations/demand`
Query:
- `hocKyId` required
- `namNhapHoc` optional
- `idNganh` optional
- `targetClassSize` optional (fallback default trong service backend; FE nen hien numeric input + default tooltip)

Vi du payload (`PreRegistrationDemandResponse` dummy):

```json
{
  "idHocKy": 99,
  "tenHocKy": "HK1 - 2025-2026",
  "namNhapHoc": 2021,
  "idNganh": 12,
  "tenNganh": "Cong nghe phan mem",
  "targetClassSize": 45,
  "totalIntents": 128,
  "totalRecommendedClasses": 37,
  "items": [
    {
      "idHocPhan": 505,
      "maHocPhan": "INT2204",
      "tenHocPhan": "Lap trinh huong doi tuong",
      "soTinChi": 3,
      "namNhapHoc": 2021,
      "idNganh": 12,
      "tenNganh": "Cong nghe phan mem",
      "totalIntent": 92,
      "recommendedClasses": 3
    }
  ]
}
```

Goi y UI:
- KPI row: totalIntents / totalRecommendedClasses / targetClassSize
- Heat table:
  - color scale theo `totalIntent / recommendedClasses`
- Row actions hook (future): navigate sang scheduling/spawn-shell (da co tai lieu TKB roi).

### 9.6 Man Admin - Publish lop hoc phan

#### `POST /api/v1/admin/lop-hoc-phan/{idLopHp}/assign-giang-vien`
Body:

```json
{ "idGiangVien": 77 }
```

Response (`LopHocPhanPublishResponse` dummy):

```json
{
  "idLopHp": 441,
  "maLopHp": "INT2204_01",
  "idHocKy": 99,
  "maHocPhan": "INT2204",
  "tenHocPhan": "Lap trinh huong doi tuong",
  "idGiangVien": 77,
  "tenGiangVien": "Nguyen Van A",
  "hasSchedule": true,
  "statusPublish": "SCHEDULED",
  "version": 4,
  "message": "Auto-promoted to SCHEDULED"
}
```

Goi y UI:
- Drawer “Class publish” show timeline:
  - `SHELL` (chua lam du) -> `SCHEDULED` (du lich/gv?) -> `PUBLISHED`
- Field badges:
  - `hasSchedule` false => block publish + highlight schedule editor link

#### `POST /api/v1/admin/lop-hoc-phan/{idLopHp}/publish`
Response schema giong assign.

#### `POST /api/v1/admin/lop-hoc-phan/bulk-publish?hocKyId=99`

```json
{
  "idHocKy": 99,
  "totalRequested": 120,
  "publishedCount": 80,
  "skippedCount": 40,
  "publishedIds": [441, 442],
  "skipped": [
    { "idLopHp": 500, "maLopHp": "XXX_01", "reason": "Chua du dieu kien publish" }
  ]
}
```

Goi y UI:
- Summary cards + expandable table skipped reasons
- Export skipped rows CSV (optional Dot 2)

### 9.7 Man Admin - Projection rebuild tool

#### `POST /api/v1/admin/timetable-projection/rebuild?sinhVienId=3&hocKyId=99`
Response (`Map<String,Object>` JSON):

```json
{
  "sinhVienId": 3,
  "hocKyId": 99,
  "rebuiltSlots": 12
}
```

Goi y UI:
- Minimal form inputs + ket qua toast `rebuiltSlots`
- Checkbox “toi da hieu day la thao tac recovery” confirm modal

### 9.8 Man Admin - Registration monitoring

#### `/outcomes`

```json
{
  "fromAt": "2026-05-05T22:09:52.831Z",
  "toAt": "2026-05-06T22:09:52.831Z",
  "total": 1532,
  "byOutcome": { "SUCCESS": 980, "FULL": 120, "CANCELLED": 300 },
  "successRate": 0.8125
}
```

Goi y UI:
- donut/pie hoac stacked bar bang `byOutcome`
- KPI card rieng cho `successRate` (explain tooltip cong thuc)

#### `/throughput`

```json
{
  "fromAt": "...",
  "toAt": "...",
  "rows": [
    { "requestType": "REGISTER", "outcome": "SUCCESS", "count": 900 },
    { "requestType": "REGISTER", "outcome": "FULL", "count": 120 },
    { "requestType": "CANCEL", "outcome": "CANCELLED", "count": 300 }
  ]
}
```

Goi y UI:
- pivot table/filter theo REGISTER vs CANCEL.

#### `/fill-rate?hocKyId=99`

```json
{
  "idHocKy": 99,
  "totalClasses": 200,
  "totalSlots": 9000,
  "takenSlots": 6820,
  "overallFillRate": 0.7578,
  "rows": [
    {
      "idLopHp": 441,
      "maLopHp": "INT2204_01",
      "maHocPhan": "INT2204",
      "tenHocPhan": "Lap trinh huong doi tuong",
      "siSoToiDa": 50,
      "siSoThucTe": 50,
      "fillRate": 1.0,
      "fullEvents": 37
    }
  ]
}
```

Goi y UI:
- top table sort by `fullEvents`, `fillRate`
- histogram overall capacity

### 9.9 Ghi chu thiet ke lien domain (khong bi “lech” UX)

- `fill-rate.overallFillRate` la **capacity-weighted**, khong bang trung binh arithmetic cua rows.
- `successRate` trong outcomes **exclude** cancellations khoi mau so (co y nghiep vu).
- Hai TKB endpoints khac semantic:
  - `/timetable/me` doi khi tien hon de show “lop + sessions nested”
  - `/snapshot` tien hon de render grid realtime + dong bo projection

---

## 10) Prompt Stitch (Text-to-UI) — sinh giao dien theo ke hoach

**Cách dùng:** sao chép từng prompt dưới đây vào Stitch (Google Stitch / Text-to-UI). Mỗi prompt là một màn hoặc một luồng riêng. Nếu Stitch giới hạn độ dài, tách phần "Hệ thiết kế chung" sang một phiên riêng, rồi dùng các prompt màn hình.

### 10.1 Hệ thiết kế chung (chạy một lần hoặc gán vào dự án Stitch)

```
Cổng thông tin đại học cho sinh viên (phong cách EduPort). Ứng web desktop, hiển thị dày đặc nhưng gọn và dịu.

Hình ảnh: học thuật, chỉnh bản — xanh dương đậm làm màu chính, nền trắng ngà, font Manrope + Inter, khoảng trắng rộng rãi, thẻ mềm không viền cứng, đổ bóng nhẹ. Phân vùng theo vai trò: khu sinh viên ấm và rõ ràng; khu quản trị nhiều dữ liệu hơn, có thanh công cụ.

Truy cập: tương phản tốt (WCAG), vòng focus rõ, trạng thái lỗi thể hiện rõ.

Ngôn ngữ giao diện: tiếng Việt; mã học phần/kỹ thuật hiển thị font monospace nếu cần.
```

### 10.2 Sinh viên — Nguyện vọng đăng ký dự kiến (`/student/pre-registration`)

```
Màn cổng sinh viên: "Đăng ký dự kiến (PRE)".

Trên cùng: tiêu đề trang + dòng giải thích ngắn: khác nhau giữa pha PRE và đăng ký chính thức.
Dòng dưới: hộp chọn "Học kỳ" (giữ chỗ ví dụ HK1 2025-2026).

Banner cảnh báo nổi bật (có thể đóng): "Pha đăng ký dự kiến chưa mở hoặc đã đóng — chỉ xem, không chỉnh sửa" khi ngoài cửa sổ.

Khối chính: bảng dữ liệu "Nguyện vọng" các cột: ưu tiên (chỉnh số hoặc kéo thả), mã học phần, tên học phần, số tín chỉ, ghi chú, thao tác (Sửa / Xóa).
Thanh công cụ trên bảng: nút chính "Thêm học phần", nút phụ "Làm mới".

Trạng thái rỗng: minh họa nhẹ + "Chưa có nguyện vọng" + nút kêu gọi "Thêm học phần".

Hộp thoại "Thêm / Sửa nguyện vọng": ô tìm/chọn học phần (combo giả), ưu tiên, ghi chú (gợi ý độ dài), Hủy / Lưu. Kiểm tra ngay trên form.

Khu toast cho lỗi trùng/xung đột. Khung chờ (skeleton) khi tải bảng.

Bố cục desktop 1280px, thanh điều hướng bên có mục PRE đang được chọn.
```

### 10.3 Sinh viên — Thời khóa biểu, tab Snapshot (`/student/timetable`, tab mặc định)

```
Cổng sinh viên "Thời khóa biểu" — tab "Snapshot" đang chọn; tab phụ mờ "Legacy".

Đầu trang: họ tên giữ chỗ, chọn học kỳ, nhãn "Đồng bộ nhanh", liên kết "Tài liệu hướng dẫn".

Khi chưa có dữ liệu projection: banner thông tin màu hổ phách "Đang đồng bộ TKB..." + nút "Thử làm mới", "Chuyển Legacy".

Lưới theo tuần: thứ Hai đến Chủ nhật (thứ 2–7 + CN), cột giờ/tiết bên trái, ô gộp cho nhiều tiết. Ô mẫu ghi "INT2204_01", tiết "1-3", phòng A101. Khi rê chuột: thẻ gợi ý — tên môn, giảng viên, khoảng ngày bắt đầu–kết thúc.

Chân trang: tổng số slot, chú giải màu theo môn.

Dòng trạng thái làm mới nhẹ: "Cập nhật: 12:30". Tông màu sinh viên dịu.
```

### 10.4 Sinh viên — Thời khóa biểu, tab Legacy (`/student/timetable`)

```
Cùng đường dẫn TKB; tab "Legacy" đang chọn, tab "Snapshot" không hoạt động.

Danh sách thẻ gập theo từng môn: phần đầu thẻ hiển thị mã HP, tên HP, tín chỉ, mã lớp, giảng viên. Mở rộng: bảng các buổi (thứ, tiết, phòng, ngày bắt đầu/kết thúc).

Trên cùng: chip tóm tắt — tổng môn, tổng tín chỉ.

Banner góc: ghi chú tải chậm hơn so với Snapshot. Bố cục desktop khu sinh viên.
```

### 10.5 Quản trị — Cửa sổ đăng ký (thêm/sửa/xóa) (`/admin/registration-windows`)

```
Bảng điều khiển quản trị "Cửa sổ đăng ký".

Thanh lọc trên: bắt buộc chọn học kỳ, chip/bật tắt pha PRE | OFFICIAL, ô tìm kiếm tùy chọn.

Bảng các cột: pha, khóa (năm nhập học hoặc "Tất cả"), ngành hoặc "Tất cả ngành trong khóa", mở lúc, đóng lúc, nhãn trạng thái "Đang mở" / "Sắp mở" / "Hết hạn", ghi chú rút gọn, cập nhật, thao tác (Xem / Sửa / Xóa).

Mở rộng dòng hoặc xem trước bên: chip giải thích phạm vi — "Toàn khóa", "Tất cả ngành" khi trường tương ứng để trống.

Nút chính "Tạo cửa sổ". Ngăn kéo hoặc hộp thoại form: học kỳ, pha, năm nhập học (tùy), ngành (tùy) kèm dòng phụ "Nếu chọn ngành thì phải chọn năm nhập học", ngày giờ mở/đóng kèm ghi chú múi giờ, ghi chú. Gửi form vô hiệu khi tổ hợp sai, hiện lỗi ngay dưới ô.

Thanh tiến trình mini từ mở đến đóng, đánh dấu "Hiện tại". Vùng xóa nguy hiểm với xác nhận.

Giao diện quản trị chuyên nghiệp, bảng dày thông tin.
```

### 10.6 Quản trị — Nhu cầu đăng ký dự kiến (`/admin/pre-registration-demand`)

```
Màn quản trị "Nhu cầu từ đăng ký dự kiến".

Bộ lọc: học kỳ (bắt buộc), năm nhập học, ngành, ô số "quy mô lớp mục tiêu" kèm chú thích mặc định backend.

Ba thẻ KPI: tổng intent, tổng lớp gợi ý, quy mô mục tiêu.

Bảng chính có thể sắp xếp, tô màu theo mức nhu cầu: mã HP, tên HP, tín chỉ, cột phạm vi/lọc, tổng intent, lớp gợi ý, gợi ý tỷ lệ (thang màu). Tiêu đề cột có chú thích khi rê chuột.

Thanh công cụ: nút phụ "Xuất CSV", "Làm mới". Trạng thái không có dữ liệu.

Nền sáng, bảng dòng thấp gọn.
```

### 10.7 Quản trị — Xuất bản lớp học phần (`/admin/class-publish`)

```
Màn vận hành "Xuất bản lớp học phần".

Bố cục: bên trái bảng/danh sách lớp của học kỳ đang chọn, cột: mã lớp, môn, giảng viên, biểu tượng "đã có lịch hay chưa", nhãn trạng thái xuất bản SHELL / SCHEDULED / PUBLISHED, số phiên bản.

Bấm dòng mở ngăn phải "Chi tiết lớp":
- Bước tiến trình Shell → Scheduled → Xuất bản.
- Khi chưa có lịch: nhãn đỏ "Thiếu lịch — không thể publish".
- Form gán giảng viên: gõ gợi ý, Lưu.
- Nút chính "Publish lớp" mờ khi bị chặn, kèm dòng chữ gợi ý.

Thanh thao hàng loạt phía trên: "Publish hàng loạt theo học kỳ" mở hộp xác nhận rủi ro + vùng tiến trình giả định.

Sau bulk: khung kết quả với thẻ tóm tắt tổng yêu cầu / đã publish / đã bỏ qua; bảng dòng bỏ qua có thể mở rộng (id lớp, mã lớp, lý do). Nút phụ "Xuất CSV các dòng bỏ qua".

Khu xếp chồng toast. Phong cách công cụ quản trị nghiêm, tối giản crom.
```

### 10.8 Quản trị — Công cụ build lại projection TKB (`/admin/timetable-projection-tools`)

```
Trang tiện ích tối giản "Build lại projection thời khóa biểu".

Hộp cảnh báo vàng: thao tác khôi phục, chỉ dùng khi thật sự cần.

Form: ô số mã sinh viên, mã học kỳ, nút chính "Build lại". Hộp chọn "Tôi hiểu đây là thao tác khôi phục" bật mới cho gửi.

Thẻ kết quả thành công làm nổi số slot đã build lại. Mẫu cảnh báo lỗi. Khung căn giữa tối đa ~640px, breadcrumb Tiện ích / Projection TKB.
```

### 10.9 Quản trị — Bảng điều khiển giám sát đăng ký (`/admin/registration-monitoring`)

```
Dashboard quản trị "Giám sát đăng ký", vận hành theo thời gian thực.

Điều khiển thời gian toàn cục: chip mẫu 15 phút / 1 giờ / 24 giờ / tùy khoảng với chọn ngày giờ, dòng phụ múi giờ UTC, công tắc tự làm mới theo chu kỳ.

Khối A — Kết quả: thẻ tổng số request; biểu đồ vành hoặc cột chồng theo SUCCESS / FULL / DUPLICATE / VALIDATION_FAILED / REJECTED / CANCELLED; thẻ riêng tỷ lệ thành công % kèm chú thích: mẫu số không tính các lần đã hủy (CANCELLED).

Khối B — Throughput: bảng dạng pivô — loại REGISTER | CANCEL, kết quả, số lượng; lọc theo cột.

Khối C — Đầy chỗ (fill rate): chọn học kỳ; thẻ KPI tổng lớp / tổng chỗ / chỗ đã lấy / tỉ lệ tổng thể thanh tiến trình; biểu đồ cột tổng năng lực.

Phía dưới: bảng dày "Top lớp" sắp theo số sự kiện FULL và tỷ lệ đầy: mã lớp, môn, chỉ tiêu, đã đăng, thanh fill rate, cột FULL.

Skeleton chờ cho từng khối; chân trang hiển thị "Lần cập nhật cuối".
```

### 10.10 Gợi ý ghép vào Stitch nhanh

| Màn / luồng | Prompt tham chiếu |
|-------------|-------------------|
| PRE intents | §10.2 |
| TKB Snapshot | §10.3 |
| TKB Legacy | §10.4 |
| Cửa sổ đăng ký | §10.5 |
| Nhu cầu PRE | §10.6 |
| Xuất bản lớp | §10.7 |
| Projection TKB | §10.8 |
| Giám sát đăng ký | §10.9 |

Nếu Stitch tạo nhiều màn liền mạch: gửi prompt §10.1 trước, sau đó yêu cầu "dùng hệ thiết kế vừa tạo cho các màn sinh viên/quản trị tiếp theo".
