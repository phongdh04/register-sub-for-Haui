# Kế hoạch dọn API tĩnh và xóa giao diện không cần thiết

Chi tiết thi công dev (ticket, lệnh, manifest file): **[`reports/ui_cleanup_static_api_screen_removal_dev_plan.md`](ui_cleanup_static_api_screen_removal_dev_plan.md)**.

Phiên bản: rà soát thực trạng, phân loại theo mức độ tĩnh và tác động.

Mục tiêu:
- Giảm nhiễu UI, chỉ giữ màn hình phục vụ trực tiếp luồng đăng ký học phần.
- Loại bỏ các page demo tĩnh và các page ngoài phạm vi luận văn.
- Chuẩn hóa điều hướng để người dùng chỉ thấy flow thật cho 3 role.

---

## 1) Phạm vi dọn dẹp

### In-scope
- Route trong `frontend/src/App.jsx`.
- Menu trong `frontend/src/layouts/StudentLayout.jsx`, `AdminLayout.jsx`, `TeacherLayout.jsx`.
- Page tại `frontend/src/pages/`.
- Mock data và đoạn UI tĩnh chèn lẫn trong page giữ lại.

### Out-of-scope
- Refactor business logic backend.
- Đổi tên endpoint backend.
- Thiết kế lại toàn bộ UI; chỉ cắt theo phạm vi đăng ký học phần.

---

## 2) Kết quả audit thực tế

### Nhóm A — Page hoàn toàn tĩnh (xóa thẳng)
Tiêu chí: không có `import { ... } from '../config/api'`, không có `fetch(...)`, nội dung là markup hardcode.
- `frontend/src/pages/DashboardSinhVinTrangCh.jsx`
  - Hardcode: `Văn A`, `5,000,000đ`, `1,200,000đ`, `Lập trình Java`, `Phòng 402 - Tòa A1`.
- `frontend/src/pages/TnhNngLcMnnhCao.jsx`
  - Hardcode: countdown `Còn lại 03 ngày 14:20`, môn học mẫu.
- `frontend/src/pages/ThutTonLogicngChtValidationRulesEngine.jsx`
  - Trùng nội dung gần như tuyệt đối với `TnhNngLcMnnhCao.jsx` (file demo trùng).

### Nhóm B — Page có API thật nhưng ngoài phạm vi luận văn (xóa, lưu nhánh backup)
- `frontend/src/pages/VSinhVinStudentWallet.jsx`
- `frontend/src/pages/ThanhTonQrCodeOpenApi.jsx`
- `frontend/src/pages/GimStTiChnhKTonAdmin.jsx`
- `frontend/src/pages/BoCoPhnTchAnalytics.jsx`
- `frontend/src/pages/HThngPhnQuynaTngRbacRoleBasedAccessControl.jsx`
- `frontend/src/pages/XcThcaYuTMfa2FaVChKS.jsx`
- `frontend/src/pages/LchSNhtKDuChnAuditTrailsLogging.jsx`
- `frontend/src/pages/QunLDanhMcKhungMLpDataMaster.jsx`
- `frontend/src/pages/TraCuHSCNhnThTcOnline.jsx`
- `frontend/src/pages/CyKhungChngTrnhDegreeAuditRoadmap.jsx`
- `frontend/src/pages/KimTraTinHcTpTranscriptDashboard.jsx`
- `frontend/src/pages/LchThinhGiGv.jsx`
- `frontend/src/pages/SetupCuHnhGiVngTrafficSplittingQueueControl.jsx`

### Nhóm C — Page giảng viên (xóa khỏi phạm vi đăng ký, có thể giữ ở phụ lục)
- `frontend/src/pages/QunLLpGingDyimDanh.jsx`
- `frontend/src/pages/MngLiNhpQunLimGradingSystem.jsx`
- `frontend/src/pages/GcThiXLKhiuNiPhcKho.jsx`
- `frontend/src/pages/CnhTayPhiCVnHcTpAcademicAdvising.jsx`

Quyết định mặc định: **không có vai trò trực tiếp trong giao dịch đăng ký**, đề xuất ẩn khỏi điều hướng chính, giữ file và tham chiếu trong tài liệu phụ lục.

### Nhóm D — Page giữ lại (core đăng ký học phần)
Student:
- `frontend/src/pages/StudentRegistrationPage.jsx` — màn hình đăng ký OFFICIAL.
- `frontend/src/pages/TnhNngTrcGiGPreRegistrationGiLp.jsx` — PRE intents (đổi route).
- `frontend/src/pages/DchVThiKhaBiuThngMinh.jsx` — TKB sau đăng ký.

Admin:
- `frontend/src/pages/AdminRegistrationWindowsPage.jsx`
- `frontend/src/pages/AdminClassPublishPage.jsx`
- `frontend/src/pages/AdminPreRegistrationDemandPage.jsx`
- `frontend/src/pages/AdminRegistrationMonitoringPage.jsx`
- `frontend/src/pages/AdminTimetableProjectionToolsPage.jsx`
- `frontend/src/pages/AdminHocKyScheduleConfigPage.jsx` — giữ tạm cho fallback legacy.

Auth:
- `frontend/src/pages/ngNhpTruynThngQunLPhin.jsx`

---

## 3) Route giữ lại sau cleanup

### Student
- `/student` -> redirect `/student/registration` (thay vì dashboard tĩnh).
- `/student/registration` -> đăng ký OFFICIAL.
- `/student/pre-registration` -> page PRE thật (không redirect sang OFFICIAL nữa).
- `/student/timetable` -> TKB sau đăng ký.

### Admin
- `/admin/registration-windows`
- `/admin/class-publish`
- `/admin/pre-registration-demand`
- `/admin/registration-monitoring`
- `/admin/timetable-projection-tools`
- `/admin/lichdangkyhockyconfig` — fallback legacy, đánh dấu rõ.

### Lecturer
- Chỉ giữ `/teacher` redirect mặc định nếu cần demo phạm vi rộng; nếu không, ẩn portal teacher khỏi luận văn.

---

## 4) Menu và default redirect cần sửa

### `StudentLayout.jsx`
Hiện có 11 mục, sau cleanup chỉ còn 3 mục đăng ký học phần:
- Đăng ký học phần (`/student/registration`).
- Đăng ký dự kiến PRE (`/student/pre-registration`).
- Thời khóa biểu (`/student/timetable`).
Bỏ các mục: `Dashboard`, `Hồ sơ`, `Degree Audit`, `Transcript`, `Lọc môn học`, `Validation Rules`, `Ví`, `Thanh toán`, `Lịch thi`.

### `AdminLayout.jsx`
Hiện có 13 mục, sau cleanup giữ 6 mục:
- Cửa sổ đăng ký, Nhu cầu PRE, Xuất bản lớp, Projection TKB, Giám sát đăng ký, Lịch đăng ký HK (legacy).
Bỏ: Analytics, Giám sát tài chính, Data master, Solver scheduling, RBAC, MFA/2FA, Audit trail.

### `TeacherLayout.jsx`
Mặc định bỏ portal teacher khỏi điều hướng chính của luận văn.

### `App.jsx`
- `/student` mặc định redirect `dashboardsinhvintrangch` -> đổi sang `registration`.
- `/admin` mặc định redirect `bocophntchanalytics` -> đổi sang `registration-windows`.
- Xóa các `<Route>` thuộc nhóm A/B/C.
- Xóa các `import` mồ côi tương ứng.

---

## 5) Page giữ nhưng cần chỉnh

- `TnhNngTrcGiGPreRegistrationGiLp.jsx`
  - Đổi route hiển thị thành `/student/pre-registration`.
  - Có thể giữ thêm alias slug cũ trong giai đoạn chuyển tiếp 1 sprint.
- `StudentRegistrationPage.jsx`
  - Bổ sung khu vực hiển thị realtime trạng thái request đăng ký (chuẩn bị nối WebSocket).
- `DchVThiKhaBiuThngMinh.jsx`
  - Đảm bảo đọc cả snapshot mới và fallback legacy, hiển thị nguồn dữ liệu.
- `AdminHocKyScheduleConfigPage.jsx`
  - Ghi rõ trên UI rằng cấu hình này là fallback nếu không có `registration_window` chuyên biệt.
- `AdminRegistrationMonitoringPage.jsx`
  - Bổ sung kênh nhận event realtime để cập nhật metric không cần reload.

---

## 6) Kế hoạch xóa theo pha

### Pha 1 - Dọn route và menu (an toàn, dễ rollback)
1. Cập nhật `frontend/src/App.jsx`:
   - Xóa các `<Route>` thuộc nhóm A và B; ẩn nhóm C nếu không demo lecturer.
   - Đổi 2 default redirect (student + admin) như mục 4.
2. Cập nhật 3 file layout: rút menu về danh sách core.
3. Đổi route PRE: bỏ redirect `/student/pre-registration` -> `/student/registration`, trỏ về page PRE thật.
4. Kiểm tra build:
   - `npm run lint`
   - `npm run build`

### Pha 2 - Xóa file page khỏi repo
1. Xóa các file thuộc nhóm A.
2. Xóa các file thuộc nhóm B.
3. (Tùy chọn) Xóa hoặc tạm giữ nhóm C theo phạm vi luận văn đã chốt.
4. Loại bỏ import mồ côi và component không dùng.
5. Lặp lại lint + build, đảm bảo không còn warning về unused.

### Pha 3 - Dọn API tĩnh và mock trong page giữ lại
1. Soát các page core, loại bỏ:
   - String hardcode dữ liệu (tên, số, môn học, phòng).
   - Mock array cố định không dùng để fallback.
2. Mọi nội dung động đều phải đến từ API thật hoặc empty-state chuẩn.
3. Bổ sung skeleton loading + error retry cho UX nhất quán.

### Pha 4 - Cập nhật tài liệu sau cleanup
- `reports/thesis_registration_02_role_workflows.md`
- `reports/thesis_registration_07_api_contracts_and_ui_mapping.md`
- `reports/thesis_registration_08_gap_and_simplification_plan.md`

---

## 7) Tác động và rủi ro

- Người dùng đang quen điều hướng cũ sẽ thấy mất link; cần ghi chú trong tài liệu.
- Một số page nhóm B có gọi API; sau khi xóa, các endpoint backend tương ứng vẫn còn nhưng không còn UI dùng. Đề xuất:
  - Không xóa endpoint backend ở pha này.
  - Đánh dấu `out-of-scope` trong `thesis_registration_08_gap_and_simplification_plan.md`.
- Pha 2 không thể rollback nhanh nếu chưa có nhánh backup; nên thực hiện trên branch `chore/ui-cleanup` rồi merge khi pass.

---

## 8) Checklist nghiệm thu sau khi xóa

- `npm run lint` pass, không còn warning import mồ côi.
- `npm run build` pass.
- Đăng nhập role `STUDENT` chỉ thấy 3 menu: PRE, đăng ký, TKB.
- Đăng nhập role `ADMIN` chỉ thấy menu trong nhóm đăng ký học phần.
- Truy cập trực tiếp URL nhóm A/B trả về `Navigate to /` (catch-all đã có).
- Smoke test:
  - Admin tạo cửa sổ đăng ký, publish 1 lớp.
  - Sinh viên đăng ký, thấy lớp xuất hiện trong TKB.
  - Admin thấy số liệu trong monitoring.

---

## 9) Trạng thái thực hiện

### Prep — trước khi bắt đầu code (Dev plan §0)

- **Đã có** annotated tag `before-ui-delete-20260509` (baseline trước Pha 2 — xóa file page).
- **Đã tạo** 3 file báo luận văn chứa bảng **UI routes sau cleanup**: `reports/thesis_registration_02_role_workflows.md`, `reports/thesis_registration_07_api_contracts_and_ui_mapping.md`, `reports/thesis_registration_08_gap_and_simplification_plan.md`.
- **Nhánh khuyến nghị** `chore/ui-cleanup-registration-scope`: tách khi đã commit ổn định; xem dev plan §0.

### Các pha chính

- Pha 1 (route + menu): chưa thực hiện.
- Pha 2 (xóa file): chưa thực hiện.
- Pha 3 (dọn mock trong page giữ): chưa thực hiện.
- Pha 4 (đồng bộ chi tiết thêm vào báo luận văn sau merge cleanup): chờ reviewer.

Ghi chú: cập nhật mục này mỗi khi hoàn thành một pha để theo dõi tiến độ.
