# Public Pre-Registration - API Scan va ke hoach code Frontend

Tai lieu nay tong hop cac API backend pre-registration da implement va de xuat ke hoach rollout frontend tuong ung cho 2 nhom: public onboarding va admin van hanh.

## 1) API inventory (nhom theo man hinh FE)

### A. Public - Pre-registration link va submit form (khong can login)

- `GET /api/public/v1/pre-reg/links/{token}`
- `POST /api/public/v1/pre-reg/links/{token}/submit`
- `GET /api/public/v1/pre-reg/requests/{requestId}`

Payload chinh:
- `PublicPreRegistrationSubmitRequest`:
  - `hoTen`, `ngaySinh`, `gioiTinh`
  - `email`, `soDienThoai`, `soCCCD`
  - `diaChiThuongTru`, `tinhThanh`
  - `maNganhDangKy`, `nienKhoa`, `coSo`, `nguonTuyenSinh`

Response chinh:
- `PublicPreRegistrationLinkResponse`:
  - `linkStatus`, `intakeCode`, `requiredFields[]`, `captchaRequired`, `expiresAt`
- `PublicPreRegistrationSubmitResponse`:
  - `requestId`, `status`, `estimatedProcessingSeconds`
- `PublicPreRegistrationRequestStatusResponse`:
  - `requestId`, `status`, `errorCode`, `studentCode`, `accountProvisioned`, `nextStep`, `processedAt`

Behavior quan trong:
- Submit tra `202 Accepted`, frontend phai poll request status.
- `status` co the la `PENDING | PROCESSING | SUCCESS | FAILED`.
- Khi `SUCCESS`, can hien `studentCode` + huong dan buoc tiep theo.

### B. Admin - Quan ly pre-registration links

- `POST /api/v1/admin/pre-reg/links`
- `GET /api/v1/admin/pre-reg/links`
- `PUT /api/v1/admin/pre-reg/links/{linkId}/close`
- `GET /api/v1/admin/pre-reg/links/{linkId}/stats`

Payload chinh:
- `AdminPreRegistrationLinkCreateRequest`:
  - `intakeCode`, `campusCode`, `expiresAt`, `maxSubmissions`, `rateLimitProfile`

Response chinh:
- `AdminPreRegistrationLinkCreateResponse`:
  - `id`, `token`, `intakeCode`, `campusCode`, `expiresAt`, `maxSubmissions`, `status`
- `AdminPreRegistrationLinkItemResponse`:
  - `id`, `intakeCode`, `campusCode`, `maxSubmissions`, `submittedCount`, `status`, `expiresAt`, `createdAt`
- `AdminPreRegistrationLinkStatsResponse`:
  - `linkId`, `totalRequests`, `pendingRequests`, `processingRequests`, `successRequests`, `failedRequests`

Behavior quan trong:
- `token` plain text chi tra mot lan luc tao link, FE can nhac user copy ngay.
- `close` la thao tac quan tri quan trong, can confirm modal truoc khi goi API.

## 2) Ke hoach code frontend tuong ung

## 2.1 Sprint FE-1 (uu tien cao - Public onboarding MVP)

- Tao route public:
  - `frontend/src/pages/public/PreRegistrationPage.jsx` (path de xuat: `/pre-reg/:token`)
- Flow trang:
  1. call `GET /links/{token}` khi mount
  2. neu link invalid/expired -> show trang thong bao
  3. neu active -> render form + validate
  4. submit -> call `POST /submit`, nhan `requestId`
  5. poll `GET /requests/{requestId}` den khi `SUCCESS/FAILED`
- Tao component:
  - `PreRegistrationForm`
  - `PreRegistrationStatusPanel`
  - `PreRegistrationInvalidLinkState`

## 2.2 Sprint FE-2 (Admin link operations)

- Tao trang `AdminPreRegistrationLinksPage`:
  - list links (table + filter local)
  - create link modal/form
  - hien token vua tao (one-time reveal) + nut copy
  - dong link (`PUT .../close`) co confirm
- Tao panel stats theo link:
  - card tong quan: total/pending/processing/success/failed
  - refresh thu cong + auto refresh moi 15-30s (tuy nhu cau)

## 2.3 Sprint FE-3 (UX hardening + operational readiness)

- Them guard UX cho submit:
  - disable nut submit khi dang gui
  - chong double-click bang local lock
- Error UX mapping:
  - map `errorCode` (`PREREG_LINK_EXPIRED`, `PREREG_SYSTEM_BUSY`, ...)
  - thong diep than thien theo ma loi
- Tracking UX:
  - luu `requestId` vao `sessionStorage` de F5 van tiep tuc theo doi status.
- Accessibility:
  - focus management, aria labels, keyboard navigation cho form dai.

## 3) Giao dien va state management de xuat

- API layer:
  - Tao module `frontend/src/features/prereg/api.js`
  - Tach nho:
    - `publicPreRegApi`: `getLinkInfo`, `submitPreReg`, `getRequestStatus`
    - `adminPreRegApi`: `createLink`, `listLinks`, `closeLink`, `getLinkStats`
- Types/contracts:
  - Tao `frontend/src/features/prereg/types.js` map response backend.
- State:
  - De xuat React Query cho:
    - poll status request
    - list links admin
    - stats per link
- Auth:
  - public flow khong gui `Authorization`
  - admin flow tiep tuc dung `authHeaders()` hien co.

## 4) Checklist FE theo endpoint (ready-to-code)

- [ ] Public page load link info (`GET /links/{token}`)
- [ ] Public submit form (`POST /submit`)
- [ ] Poll request status (`GET /requests/{requestId}`)
- [ ] Admin create link (`POST /api/v1/admin/pre-reg/links`)
- [ ] Admin list links (`GET /api/v1/admin/pre-reg/links`)
- [ ] Admin close link (`PUT /{linkId}/close`)
- [ ] Admin link stats (`GET /{linkId}/stats`)
- [ ] Error code mapping va message UX
- [ ] One-time token copy flow sau tao link

## 5) Validation rules phia frontend (de dong bo backend)

- `hoTen`: bat buoc, max 200
- `ngaySinh`: bat buoc (format `yyyy-MM-dd` hoac theo date-picker convert sang API format)
- `email`: dung format email
- `soDienThoai`: max 20
- `soCCCD`: neu nhap thi 9-20 chu so
- `maNganhDangKy`: bat buoc, max 50
- `nienKhoa`: bat buoc, max 20
- `diaChiThuongTru`: max 500

## 6) Rui ro va de xuat giam thieu

- Token bi user quen copy luc tao link:
  - FE can show modal one-time ro rang + yeu cau confirm da copy.
- Polling qua nhieu request:
  - dung backoff (2s -> 3s -> 5s), timeout hop ly.
- Public form bi spam client-side:
  - disable submit button, local cooldown 3-5 giay.
- Error message backend thay doi:
  - map theo `errorCode`, khong map theo raw `message`.

## 7) De xuat route va file structure

- `frontend/src/features/prereg/api.js`
- `frontend/src/features/prereg/types.js`
- `frontend/src/features/prereg/hooks/usePreRegStatusPoll.js`
- `frontend/src/pages/public/PreRegistrationPage.jsx`
- `frontend/src/pages/admin/AdminPreRegistrationLinksPage.jsx`
- `frontend/src/components/prereg/PreRegistrationForm.jsx`
- `frontend/src/components/prereg/PreRegistrationStatusPanel.jsx`
- `frontend/src/components/admin/prereg/CreatePreRegLinkModal.jsx`

## 8) Thu tu trien khai de xuat (2 sprint FE)

- **Sprint FE-1:** Public onboarding flow (route + form + submit + poll status).
- **Sprint FE-2:** Admin operations (create/list/close/stats) + UX hardening.

Ket qua sau Sprint FE-2: frontend co the van hanh day du luong pre-registration public va dashboard admin toi thieu.
