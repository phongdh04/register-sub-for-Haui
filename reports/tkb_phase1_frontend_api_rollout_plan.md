# TKB Phase 1 - API Scan va ke hoach code Frontend

Tai lieu nay tong hop cac API da implement cho TKB Phase 1 va de xuat ke hoach rollout frontend tuong ung.

## 1) API inventory (nhom theo man hinh FE)

### A. Admin - Master room va ranh buoc GV

- `GET /api/v1/admin/phong?page=&size=&maCoSo=`
- `GET /api/v1/admin/phong/{id}`
- `POST /api/v1/admin/phong`
- `PUT /api/v1/admin/phong/{id}`
- `DELETE /api/v1/admin/phong/{id}`
- `GET /api/v1/admin/giang-vien/{giangVienId}/constraints?hocKyId=`
- `POST /api/v1/admin/giang-vien/{giangVienId}/constraints/busy-slots`
- `PUT /api/v1/admin/giang-vien/{giangVienId}/constraints/busy-slots/{slotId}`
- `DELETE /api/v1/admin/giang-vien/{giangVienId}/constraints/busy-slots/{slotId}`

Payload chinh:
- `PhongHocUpsertRequest`: `maPhong`, `tenPhong`, `maCoSo`, `loaiPhong`, `sucChua`, `trangThai`, `ghiChu`.
- `GvBusySlotUpsertRequest`: `hocKyId`, `thu`, `tietBd`, `tietKt`, `loai`, `lyDo`, `ngayBd`, `ngayKt`.

### B. Admin - Grid scheduling, conflict-check, patch slot

- `GET /api/v1/admin/scheduling/hoc-ky/{hocKyId}/snapshot`
- `POST /api/v1/admin/scheduling/hoc-ky/{hocKyId}/conflict-check`
- `PATCH /api/v1/admin/scheduling/lop-hoc-phan/{idLopHp}/slot`

Payload chinh:
- `SchedulingConflictCheckRequest`: `idLopHp`, `slots[]`, `overrideGiangVienId`, `overridePhongHocId`.
- `SchedulingSlotPatchRequest`: `slots[]`, `overrideGiangVienId`, `overridePhongHocId`, `lyDoThayDoi`.

Luu y behavior:
- `PATCH slot` chi persist khi conflict-check pass.
- Neu hoc ky `CONG_BO`, backend tra conflict/forbidden theo policy change-set.

### C. Admin - JSON room migration

- `GET /api/v1/admin/scheduling/hoc-ky/{hocKyId}/phong-from-json/audit?includeDetails=`
- `POST /api/v1/admin/scheduling/hoc-ky/{hocKyId}/phong-from-json/apply?dryRun=`

### D. Admin - Forecast mo lop

- `POST /api/v1/admin/scheduling/hoc-ky/{hocKyId}/forecast`
- `POST /api/v1/admin/scheduling/hoc-ky/{hocKyId}/forecast-versions/{versionId}/approve`
- `POST /api/v1/admin/scheduling/hoc-ky/{hocKyId}/forecast-versions/{versionId}/reject`
- `POST /api/v1/admin/scheduling/hoc-ky/{hocKyId}/forecast-versions/{versionId}/spawn-shell`

### E. Admin - Solver

- `POST /api/v1/admin/scheduling/hoc-ky/{hocKyId}/solver/dry-run`
- `POST /api/v1/admin/scheduling/hoc-ky/{hocKyId}/solver/mvp-run`
- `POST /api/v1/admin/scheduling/hoc-ky/{hocKyId}/solver/run` (async)
- `GET /api/v1/admin/scheduling/hoc-ky/{hocKyId}/solver/jobs/{jobId}`

### F. Admin - Change set workflow (publish)

- `GET /api/v1/admin/scheduling/hoc-ky/{hocKyId}/change-sets`
- `POST /api/v1/admin/scheduling/hoc-ky/{hocKyId}/change-sets/submit`
- `POST /api/v1/admin/scheduling/hoc-ky/{hocKyId}/change-sets/{changeSetId}/review`
- `POST /api/v1/admin/scheduling/hoc-ky/{hocKyId}/change-sets/{changeSetId}/apply`

### G. Admin - TKB block lifecycle

- `GET /api/v1/admin/scheduling/hoc-ky/{hocKyId}/tkb-blocks`
- `POST /api/v1/admin/scheduling/hoc-ky/{hocKyId}/tkb-blocks`
- `PUT /api/v1/admin/scheduling/hoc-ky/{hocKyId}/tkb-blocks/{idTkbBlock}`
- `DELETE /api/v1/admin/scheduling/hoc-ky/{hocKyId}/tkb-blocks/{idTkbBlock}`

Payload chinh:
- `TkbBlockUpsertRequest`: `maBlock`, `tenBlock`, `jsonSlots`, `danhSachIdHocPhan`, `batBuocChonCaBlock`.

### H. Student - Pre-registration cart + block bundle

- `GET /api/v1/pre-reg/cart/me?hocKyId=`
- `POST /api/v1/pre-reg/cart/items`
- `POST /api/v1/pre-reg/cart/blocks`
- `DELETE /api/v1/pre-reg/cart/items/{idGioHang}`

Payload chinh:
- `PreRegCartAddItemRequest`: `idLopHp`, `hocKyId`.
- `PreRegCartAddBlockRequest`: `idTkbBlock`, `hocKyId`.

Behavior quan trong:
- Neu LHP thuoc block co `batBuocChonCaBlock=true`, add item le se bi chan.
- Add block se validate conflict noi bo block va conflict voi gio/official registration.

## 2) Ke hoach code frontend tuong ung

## 2.1 Sprint FE-1 (uu tien cao - Admin scheduling shell)

- Tao module `frontend/src/features/admin/scheduling/api.js` gom typed wrappers cho nhom B, D, E, F, G.
- Tao trang `AdminSchedulingSnapshotPage`:
  - chon hoc ky
  - load snapshot + table row
  - button conflict-check cho dong dang edit
- Tao dialog `EditSlotDialog`:
  - edit `slots`
  - call `conflict-check` truoc, neu pass moi cho `PATCH slot`.
- Tao `useSolverJobs` hook:
  - start async job qua `/solver/run`
  - poll `/solver/jobs/{jobId}` theo interval.

## 2.2 Sprint FE-2 (Admin workflow full)

- Tao `AdminForecastPage`:
  - run forecast
  - list version (can bo sung API list neu chua co)
  - approve/reject/spawn-shell.
- Tao `AdminChangeSetPage`:
  - create submit payload (json editor + templates)
  - review approve/reject
  - apply change set.
- Tao `AdminTkbBlockPage`:
  - CRUD block
  - builder danh sach hoc phan
  - toggle `batBuocChonCaBlock`.

## 2.3 Sprint FE-3 (Student pre-reg block)

- Cap nhat `PreRegCart` UI:
  - button "Them theo block"
  - modal chon block theo hoc ky
  - call `/pre-reg/cart/blocks`.
- UX loi:
  - hien thi thong diep conflict backend cho block xung dot.
  - khi add item le bi chan do block mandatory, show CTA "Dang ky theo block".

## 2.4 Sprint FE-4 (Admin master data)

- `AdminPhongPage`: paging/search/form CRUD theo `PhongHocUpsertRequest`.
- `AdminGiangVienConstraintsPage`: list busy slots + CRUD inline.
- `Phong JSON migration` tab:
  - audit summary
  - dry-run/apply.

## 3) Giao dien va state management de xuat

- API layer:
  - Tach theo feature: `adminSchedulingApi`, `adminRoomApi`, `studentPreRegApi`.
  - Dung `authHeaders()` hien co trong `frontend/src/config/api.js`.
- Query cache:
  - Neu dang dung fetch thuong, de xuat nang cap React Query cho cac luong poll solver va snapshot.
- Types:
  - Tao `frontend/src/features/admin/scheduling/types.js` map response backend:
    - `SchedulingSnapshotResponse`
    - `SchedulingConflictCheckResponse`
    - `SolverRunJobStatusResponse`
    - `ScheduleChangeSetResponse`
    - `TkbBlockResponse`.

## 4) Checklist FE theo endpoint (ready-to-code)

- [ ] Snapshot/Conflict/Patch slot
- [ ] Forecast run + workflow
- [ ] Solver dry/mvp/async + poll
- [ ] Change-set submit/review/apply
- [ ] TkbBlock CRUD + mandatory-block UX
- [ ] Pre-reg add block
- [ ] Room CRUD + GV busy constraints
- [ ] JSON room audit/apply

## 5) Rui ro va de xuat giam thieu

- Chua co endpoint list forecast versions chi tiet:
  - FE tam thoi luu id sau run; de xuat backend bo sung list/read endpoint neu can.
- Change-set payload la JSON tu do:
  - FE nen co preset schema + validator client de giam sai format.
- Solver async poll:
  - can timeout/retry strategy client de tranh loading vo han.
- Conflict message dang text:
  - FE parse `conflictType` la chinh, `detail` chi de hien thi.
