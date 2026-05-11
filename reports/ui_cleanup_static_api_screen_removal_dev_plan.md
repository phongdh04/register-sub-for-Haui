# Dev plan chi tiết — Dọn UI tĩnh & route (thi hành theo BA plan)

Phiên bản: 2026-05.  
Đối tượng đọc: dev / reviewer / agent code.  
Nguồn nghiệp vụ: [`ui_cleanup_static_api_and_screen_removal_plan.md`](ui_cleanup_static_api_and_screen_removal_plan.md).

Mục tiêu của tài liệu này: **chia việc theo ticket nhỏ, có thứ tự merge, có lệnh verify và acceptance cụ thể**, không chỉ checklist trừu tượng.

---

## 0) Pha 0 — Triển khai trước khi gõ code (DONE)

Hoàn thành khi bắt tay ticket UI-CLEAN-011:

| Việc | Trạng thái |
|------|------------|
| Annotated git tag rollback trước Pha 2 (`before-ui-delete-20260509`) | ✅ |
| Tạo 3 file Pha 4 (đặt đúng tên backlog BA): [`thesis_registration_02_role_workflows.md`](thesis_registration_02_role_workflows.md), [`thesis_registration_07_api_contracts_and_ui_mapping.md`](thesis_registration_07_api_contracts_and_ui_mapping.md), [`thesis_registration_08_gap_and_simplification_plan.md`](thesis_registration_08_gap_and_simplification_plan.md) — có section **UI routes sau cleanup** | ✅ |
| Nhánh chuyên dụng `chore/ui-cleanup-registration-scope` | Chuẩn bị có thể: `git switch -c chore/ui-cleanup-registration-scope` **sau khi working tree của feature hiện tại đã commit ổn định** (tránh stash lẫn nhiều ticket) |

Ghi nhật ký trong [`ui_cleanup_static_api_and_screen_removal_plan.md`](ui_cleanup_static_api_and_screen_removal_plan.md) §9 phần Prep.

---

## 1) Quy ước & nhánh làm việc

| Quy ước | Giá trị |
|---------|---------|
| Nhánh gợi ý | `chore/ui-cleanup-registration-scope` |
| Commit style | một commit / pha lớn (Pha 1 tách được 2 commit: route vs menu nếu cần rollback tốt hơn) |
| Không được | đổi URL backend trong pha cleanup; chỉ đụng **`frontend/`** (+ tài liệu Pha 4) |

Rollback Pha 1: `git revert` một commit chỉ chứa App + layout. Rollback Pha 2: cần **tag trước khi delete** hoặc giữ nhánh parallel.

---

## 2) Manifest file — nhóm xóa (Pha 2)

### 2.1 Nhóm A — chỉ chứa markup tĩnh (xoá `.jsx`)

| Đường dẫn |
|-----------|
| `frontend/src/pages/DashboardSinhVinTrangCh.jsx` |
| `frontend/src/pages/TnhNngLcMnnhCao.jsx` |
| `frontend/src/pages/ThutTonLogicngChtValidationRulesEngine.jsx` |

### 2.2 Nhóm B — có API nhưng ngoài luồng đăng ký học phần (xoá `.jsx`)

| Đường dẫn |
|-----------|
| `frontend/src/pages/VSinhVinStudentWallet.jsx` |
| `frontend/src/pages/ThanhTonQrCodeOpenApi.jsx` |
| `frontend/src/pages/GimStTiChnhKTonAdmin.jsx` |
| `frontend/src/pages/BoCoPhnTchAnalytics.jsx` |
| `frontend/src/pages/HThngPhnQuynaTngRbacRoleBasedAccessControl.jsx` |
| `frontend/src/pages/XcThcaYuTMfa2FaVChKS.jsx` |
| `frontend/src/pages/LchSNhtKDuChnAuditTrailsLogging.jsx` |
| `frontend/src/pages/QunLDanhMcKhungMLpDataMaster.jsx` |
| `frontend/src/pages/TraCuHSCNhnThTcOnline.jsx` |
| `frontend/src/pages/CyKhungChngTrnhDegreeAuditRoadmap.jsx` |
| `frontend/src/pages/KimTraTinHcTpTranscriptDashboard.jsx` |
| `frontend/src/pages/LchThinhGiGv.jsx` |
| `frontend/src/pages/SetupCuHnhGiVngTrafficSplittingQueueControl.jsx` |

### 2.3 Nhóm C — lecturer (default luận văn: xoá khỏi SPA)

| Đường dẫn |
|-----------|
| `frontend/src/pages/QunLLpGingDyimDanh.jsx` |
| `frontend/src/pages/MngLiNhpQunLimGradingSystem.jsx` |
| `frontend/src/pages/GcThiXLKhiuNiPhcKho.jsx` |
| `frontend/src/pages/CnhTayPhiCVnHcTpAcademicAdvising.jsx` |

**Tùy chọn** (nếu vẫn cần demo GV): giữ nhóm C + `TeacherLayout` + block route `/teacher` và ghi vào báo luận văn *phụ lục* — khi đó **không** xoá 2.3 và không xoá `TeacherLayout.jsx`.

---

## 3) File giữ nguyên (core registration)

### Student pages

| File | Route sau cleanup (đề xuất slug ổn định) |
|------|------------------------------------------|
| `StudentRegistrationPage.jsx` | `/student/registration` |
| `TnhNngTrcGiGPreRegistrationGiLp.jsx` | `/student/pre-registration` (**component**) |
| `DchVThiKhaBiuThngMinh.jsx` | `/student/timetable` |

### Admin pages

| File | Route |
|------|--------|
| `AdminRegistrationWindowsPage.jsx` | `/admin/registration-windows` |
| `AdminClassPublishPage.jsx` | `/admin/class-publish` |
| `AdminPreRegistrationDemandPage.jsx` | `/admin/pre-registration-demand` |
| `AdminRegistrationMonitoringPage.jsx` | `/admin/registration-monitoring` |
| `AdminTimetableProjectionToolsPage.jsx` | `/admin/timetable-projection-tools` |
| `AdminHocKyScheduleConfigPage.jsx` | `/admin/lichdangkyhockyconfig` |

### Auth

| File | Route |
|------|--------|
| `ngNhpTruynThngQunLPhin.jsx` | `/login` |

### Layout giữ

- `frontend/src/layouts/StudentLayout.jsx` (thu nhỏ menu)
- `frontend/src/layouts/AdminLayout.jsx` (thu nhỏ menu)
- `frontend/src/layouts/TeacherLayout.jsx` — **chỉ khi** chọn nhánh “vẫn demo teacher”; ngược lại có thể xoá luôn + block `/teacher` trong `RequireAuth`/App (xem ticket UI-CLEAN-019).

---

## 4) Tickets Pha 1 — route + redirect + menu (chưa xoá file page)

Ticket thứ tự thực hiện; mỗi ticket có Acceptance riêng.

### UI-CLEAN-011 — `App.jsx` student redirects & PRE route

**File:** `frontend/src/App.jsx`

| Thao tác | Hiện trạng (tham chiếu) | Sau sửa |
|----------|-------------------------|---------|
| Index redirect `/student` | `<Navigate to="/student/dashboardsinhvintrangch"` | `<Navigate to="/student/registration" replace />` |
| Route PRE | `pre-registration` → `Navigate to /registration` | `element={<TnhNngTrcGiGPreRegistrationGiLp />}` (**bỏ** redirect sang OFFICIAL) |
| Giữ alias legacy (tuỳ chọn 1 sprint) | `tnhnngtrcgigpreregistrationgilp`, `intents-legacy` | Thêm `<Route>` `Navigate` SPA sang `/student/pre-registration`; hoặc giữ hai path trỏ cùng một component — khuyến nghị: **ưu tiên một slug duy nhất** `pre-registration`. |

**Kiểm tra:** sau login `sv01`, URL `/student` land đúng `/student/registration`. `/student/pre-registration` load PRE page, không nhảy registration.

---

### UI-CLEAN-012 — `App.jsx` admin index redirect

| Thao tác | Sau sửa |
|----------|---------|
| `/admin` index | `<Navigate to="/admin/registration-windows" replace />` |

**Kiểm tra:** admin vào `/admin` vào đúng Cửa sổ đăng ký.

---

### UI-CLEAN-013 — Gỡ `<Route>` + `import` không còn trong Pha 1

**Chiến lược:** trong Pha 1 có thể **comment** các route nhóm B/C và import tương ứng **hoặc** giữ route nhưng `Navigate to /registration` ngắn — **không khuyến khích**. Khuyến nghị: Pha 1 chỉ chỉnh redirect + PRE + layout menu; các route chết được gỡ cùng lúc với UI-CLEAN-014.

**Ít nhất** phải gỡ route trỏ tới **dashboard tĩnh** vì index đã đổi.

---

### UI-CLEAN-014 — `StudentLayout.jsx` — danh mục `studentNav`

**File:** `frontend/src/layouts/StudentLayout.jsx`

Thay constant `studentNav` thành **đúng 3 mục** (đề xuất thứ tự UX):

```javascript
const studentNav = [
  { to: '/student/registration', label: 'Đăng ký học phần', icon: 'how_to_reg' },
  { to: '/student/pre-registration', label: 'Đăng ký dự kiến (PRE)', icon: 'edit_calendar' },
  { to: '/student/timetable', label: 'Thời khóa biểu', icon: 'calendar_month' }
];
```

**Acceptance:** sidebar không hiển thị link Dashboard / Ví / …

---

### UI-CLEAN-015 — `AdminLayout.jsx` — danh mục `adminNav`

**File:** `frontend/src/layouts/AdminLayout.jsx`

Đề xuất `adminNav`:

```javascript
const adminNav = [
  { to: '/admin/registration-windows', label: 'Cửa sổ đăng ký', icon: 'event_available' },
  { to: '/admin/pre-registration-demand', label: 'Nhu cầu PRE', icon: 'analytics' },
  { to: '/admin/class-publish', label: 'Xuất bản lớp', icon: 'publish' },
  { to: '/admin/timetable-projection-tools', label: 'Projection TKB', icon: 'build' },
  { to: '/admin/registration-monitoring', label: 'Giám sát đăng ký', icon: 'monitoring' },
  { to: '/admin/lichdangkyhockyconfig', label: 'Lịch đăng ký HK (fallback)', icon: 'schedule' }
];
```

**Acceptance:** không còn Analytics / RBAC / MFA / Audit / Data master / Solver / Finance.

---

### UI-CLEAN-016 — Verify build Pha 1

Chạy tại máy dev:

```powershell
Set-Location "d:\docs do an\frontend"
npm run lint
npm run build
```

Acceptance: **0 error**; có thể còn warning cũ — ghi vào backlog nếu không liên quan cleanup.

---

## 5) Tickets Pha 2 — xoá component page & dọn import

### UI-CLEAN-021 — Xoá file nhóm A

Xoá 3 file mục 2.1. Chạy lại lint — sửa mọi import còn tham chiếu (đáng nhẽ không còn sau Pha 1 hoàn chỉnh).

---

### UI-CLEAN-022 — Xoá file nhóm B

Xoá 13 file mục 2.2.

---

### UI-CLEAN-023 — Teacher: một trong hai nhánh

| Nhánh | Hành động |
|-------|-----------|
| **Đồng bộ luận văn (default)** | Xoá 4 file 2.3 + gỡ toàn block `/teacher` trong `App.jsx` + xoá `TeacherLayout.jsx` nếu không dùng; cập nhật `README` seed “gv01 chỉ để đăng nhập RBAC không có portal” HOẶC redirect `/teacher` → `/login` với thông báo |
| Giữ lecturer demo | Không xoá 2.3; chỉnh `TeacherLayout.jsx` giảm menu nếu cần |

**Acceptance nhánh default:** không còn route `/teacher/qun...` và build pass.

---

### UI-CLEAN-024 — Sweep import & dead code trong `App.jsx`

Sau khi xoá file, **`App.jsx` chỉ còn import:**

- Layouts còn dùng
- `RequireAuth`
- Router primitives
- Các page còn trong manifest §3 (+ login page)

Optional: **`eslint-plugin-import`** rule `unused-imports` nếu chưa bật.

---

### UI-CLEAN-025 — Grep không còn path chết trong repo

```powershell
Set-Location "d:\docs do an\frontend"
# Ví dụ tìm slug cũ (thêm các pattern BA plan có)
rg "dashboardsinhvintrangch|bocophntchanalytics|ThanhTonQrCodeOpenApi" src
```

Expect: **không** match trong `src` (ngoại trừ comment intentional).

---

## 6) Tickets Pha 3 — dọn mock / hardcode trong page core

Làm theo thứ tự ít risk → nhiều risk.

### UI-CLEAN-031 — Audit từng page core (`rg` heuristic)

```powershell
rg "Văn A|5,000,000|Phòng 402|TBD|dummy|TODO mock" frontend/src/pages/StudentRegistrationPage.jsx frontend/src/pages/TnhNngTrcGiGPreRegistrationGiLp.jsx frontend/src/pages/DchVThiKhaBiuThngMinh.jsx frontend/src/pages/Admin*.jsx
```

Mỗi hit: đổi sang **fetch thật** hoặc **Empty state** có copy chuẩn.

---

### UI-CLEAN-032 — `TnhNngTrcGiGPreRegistrationGiLp.jsx`

**BA yêu cầu:** route slug chuẩn `/student/pre-registration` đã được Pha 1 đáp ứng.

Dev thêm trong page (nếu chưa có):

- Spinner khi đang GET intents
- Retry khi API lỗi mạng
- Banner khi cửa PRE đóng (đọc từ endpoint window hoặc từ lỗi 403 có message)

---

### UI-CLEAN-033 — `StudentRegistrationPage.jsx`

Theo BA §5:

- Chuẩn bị vùng **status queue** / placeholder cho WebSocket (có thể chỉ là state + typography “Đang chờ kafka…” — không cần implement WS trong ticket này nếu ngoài scope).

Acceptance có thể tách ticket WS riêng: chỉ hiện có **UI slot** không crash.

---

### UI-CLEAN-034 — `DchVThiKhaBiuThngMinh.jsx`

Đảm bảo có pattern:

1. Gọi `GET /api/v1/timetable/me` (composite) và/hoặc
2. `GET /api/v1/timetable/me/snapshot?hocKyId=`

Hiển thị label “Nguồn: Snapshot projection” vs “Aggregate” trong UI nhỏ để demo luận văn không bị nhầm.

---

### UI-CLEAN-035 — `AdminHocKyScheduleConfigPage.jsx`

Thêm Banner/footnote component phía đầu form:

> Cấu hình này chỉ được dùng khi không có **`registration_window`** chi tiết trong F02.

---

### UI-CLEAN-036 — `AdminRegistrationMonitoringPage.jsx`

Tuỳ mức effort:

- MVP: nút Refresh + polling `setInterval` 10s các endpoint F05 (**không thêm deps**).
- Hoặc hook TODO WebSocket chỉ trong comment + issue tracker.

Acceptance MVP: không cần F5 tay để demo ngắn.

---

## 7) Tickets Pha 4 — tài liệu

Đồng bộ các file được BA list (hoặc thay thế bằng `reports/thesis/` nếu đã supersede):

- `reports/thesis_registration_02_role_workflows.md`
- `reports/thesis_registration_07_api_contracts_and_ui_mapping.md`
- `reports/thesis_registration_08_gap_and_simplification_plan.md`

Mỗi file: một section **“UI routes sau cleanup”** khớp bảng dưới.

### Bảng route cuối cùng (single source trong doc này)

**Student**

| Path | Screen |
|------|--------|
| `/student` | → `/student/registration` |
| `/student/registration` | OFFICIAL |
| `/student/pre-registration` | PRE intents |
| `/student/timetable` | TKB |

**Admin**

| Path | Screen |
|------|--------|
| `/admin` | → `/admin/registration-windows` |
| `/admin/registration-windows` | F02 |
| `/admin/pre-registration-demand` | F04 |
| `/admin/class-publish` | F03 |
| `/admin/timetable-projection-tools` | F06 |
| `/admin/registration-monitoring` | F05 |
| `/admin/lichdangkyhockyconfig` | F07 fallback |

---

## 8) Test matrix (manual smoke sau Pha 2–3)

| ID | Ai | Steps | Pass |
|----|-----|-------|------|
| SMK-01 | STUDENT | Login → chỉ thấy 3 mục menu | ✓ |
| SMK-02 | STUDENT | `/student/dashboardsinhvintrangch` (nếu còn) → catch-all `/` `/login` | ✓ hoặc 404 không crash |
| SMK-03 | STUDENT | PRE page load, submit intent không lỗi console | ✓ |
| SMK-04 | STUDENT | OFFICIAL register một lớp, TKB có dữ liệu kỳ ít nhất một nguồn | ✓ |
| SMK-05 | ADMIN | thấy 6 mục menu; create window stub | ✓ |
| SMK-06 | Any | bookmark URL cũ analytics → không white screen (**Navigate** catch-all đã có) | ✓ |

---

## 9) Risks đã được map vào task

| Rủi ro | Ticket giảm thiểu |
|--------|-------------------|
| Xoá file không có backup | Tag `before-ui-delete` hoặc branch backup |
| User bookmark slug tiếng Việt không dấu dài | Thêm `<Route>` redirect tạm trong Pha 1 (optional) |

---

## 10) Theo dõi tiến độ (ghi nhật ký PR)

Áp vào ô trạng thái của BA plan §9 hoặc bảng dưới:

| Phase | Ticket chính | Done? |
|-------|---------------|-------|
| 0 | Prep (§0): tag + 3 file `thesis_registration_*` + checklist BA §9 | ☑ |
| 1 | UI-CLEAN-011 … 016 | ☐ |
| 2 | UI-CLEAN-021 … 025 | ☐ |
| 3 | UI-CLEAN-031 … 036 | ☐ |
| 4 | Doc sync bổ sung sau merge (ghi chú vào các file `thesis_registration_*` + feature docs nếu cần) | ☐ chi tiết còn theo reviewer |

---

*Tài liệu này là bản chi tiết hóa có thể bám trực tiếp vào backlog (Jira/Linear/issue list) bằng cách đặt Ticket ID trong commit message.*
