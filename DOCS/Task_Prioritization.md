# Bảng Phân Loại Ưu Tiên Chức Năng (Features Prioritization)
*Dự án: Hệ thống Đăng ký Học phần (EduPort) - Microservices Hybrid (Java + Go)*

## Chiến lược ưu tiên (Priority Strategy)
Để đảm bảo tiến độ bảo vệ đồ án và chứng minh được kỹ thuật giải quyết vấn đề cốt lõi (Xử lý đồng thời Giờ Vàng), các chức năng được chia làm 4 cấp độ:

- **🔴 P0-Blocker:** Cốt lõi hệ thống. Nếu không có, không lập trình/test được các chức năng khác. Bắt buộc làm đầu tiên.
- **🟠 P1-High:** Xương sống nghiệp vụ. Mang lại điểm số cao nhất cho đồ án.
- **🟡 P2-Medium:** Tính năng bổ trợ hoàn thiện hệ sinh thái (Ví, Học phí, Cố vấn). Làm sau khi xong lõi đăng ký môn.
- **🟢 P3-Low:** Tính năng mở rộng, trang trí (Analytics, Audit Log). Có thể cắt bỏ nếu sát deadline.

## Danh sách Phân Loại Đầy Đủ

| STT | Tên Task (Module Hệ Thống) | Role Đối Tượng | Mức Độ Ưu Tiên | Trạng thái | Lý Do Phân Loại (Kiến Trúc) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | Khởi tạo Đầu não (Dashboard Sinh viên) | STUDENT | **🔴 P0-Blocker** | ✅ Done (UI) | Cổng vào bắt buộc. Không có mặt tiền thì không test được click button đi đâu. |
| 2 | Tra cứu Hồ sơ Cá nhân & Thủ tục online | STUDENT | **🟡 P2-Medium** | ✅ **Done** | API `GET /api/v1/student-profile/me` + `PATCH .../me/contact` (email/SĐT/địa chỉ); join `SinhVien`↔`HoSoSinhVien`↔`Lop`↔`Nganh`↔`Khoa`↔`CoVan`; thủ tục demo trong response; UI `TraCuHSCNhnThTcOnline.jsx`. |
| 3 | Cây Khung Chương Trình (Degree Audit) | STUDENT | **🟠 P1-High** | ✅ **Done** | API `GET /api/v1/degree-audit/me` trả CTĐT theo ngành SV + mapping CTĐT↔Học phần và trạng thái hoàn thành; UI `CyKhungChngTrnhDegreeAuditRoadmap.jsx` hiển thị theo khối kiến thức. |
| 4 | Kiểm tra tiến độ học tập (Transcript/Bảng điểm)| STUDENT | **🟠 P1-High** | ✅ **Done** | Bảng `Bang_Diem_Mon` + API `GET /api/v1/transcript/me` (lọc `hocKyId`), GPA hệ 4; cập nhật prerequisite qua điểm ≥ 1.0; UI `KimTraTinHcTpTranscriptDashboard.jsx`. |
| 5 | Tính năng Trước Giờ G (Giỏ hàng Pre-Registration)| STUDENT | **🟡 P2-Medium** | ✅ **Done** | Bảng `Gio_Hang_Dang_Ky` + API `GET/POST/DELETE /api/v1/pre-reg/cart/*` (theo HK, chống trùng với ĐK chính thức, cảnh báo trùng TKB trong giỏ); UI `TnhNngTrcGiGPreRegistrationGiLp.jsx`. |
| 6 | Lọc Môn Đỉnh Cao (Tìm kiếm môn học) | STUDENT | **🟠 P1-High** | ✅ **Done** | JPA Specification (Builder Pattern) dynamic query: keyword + idHocKy + soTinChi + loaiMon + chiConCho. Page + Sort. API: GET /api/v1/courses. |
| 7 | Thuật Toán Logic Đóng Chốt (Validation Rules) | STUDENT | **🟠 P1-High** | ✅ **Done** | Chain of Responsibility: DuplicateRegistration → ScheduleConflict → PrerequisiteCourse + Kafka Consumer cầu nối Go→Java. |
| 8 | Ví Sinh Viên (Student Wallet) | STUDENT | **🟡 P2-Medium** | ✅ **Done** | Bảng `Vi_Sinh_Vien`, `Giao_Dich_Vi`; API `GET /api/v1/wallet/me`; nạp qua `POST /api/v1/payments/{id}/confirm-mock` (ghi có idempotent); ước tính nợ học phí từ đăng ký; UI `VSinhVinStudentWallet.jsx` + nút xác nhận MOCK trên `ThanhTonQrCodeOpenApi.jsx`. |
| 9 | Thanh Toán QR Code (Open API VNPay/MoMo) | STUDENT | **🟠 P1-High** | ✅ **Done** | API `POST /api/v1/payments/tuition-qr` + Adapter MOCK/VNPay/MoMo stub + bảng `Giao_Dich_Thanh_Toan`; UI `ThanhTonQrCodeOpenApi.jsx` tạo QR/liên kết. |
| 10 | Dịch vụ Thời Khóa Biểu thông minh | STUDENT | **🟠 P1-High** | ✅ **Done** | API `GET /api/v1/timetable/me` lấy dữ liệu thật từ đăng ký học phần + `thoi_khoa_bieu_json`, render UI realtime theo JWT Student. |
| 11 | Lịch Thi & Đánh Giá Giảng Viên | STUDENT | **🟡 P2-Medium** | ✅ **Done** | Bảng `Lich_Thi`, `Phieu_Du_Thi`, `Danh_Gia_Giang_Vien`; API `GET /api/v1/exams/me`, `GET/POST /api/v1/lecturer-ratings`; seed demo lịch/SBD khi có đăng ký sv01; UI `LchThinhGiGv.jsx`. |
| 12 | **Setup Cấu Hình Giờ Vàng (Queue / Kafka / Redis)**| ADMIN | **🔴 P0-Blocker** | ✅ **Done** | **🔥 TRÁI TIM DỰ ÁN GOLANG**. Triển khai: Cooldown → DistributedLock → Lua DECR Slot → Kafka Publish. |
| 13 | Quản Lý Danh Mục Khung & Mở Lớp | ADMIN | **🔴 P0-Blocker** | ✅ **Done** | Admin phải bơm Data môn học Data Master vào CSDL thì SV mới đăng ký được. |
| 14 | Giám Sát Tài Chính (Kế Toán Admin) | ADMIN | **🟡 P2-Medium** | ✅ **Done** | API `GET /api/v1/admin/finance/summary|receivables|payments|wallet-transactions` (tổng nợ HP−ví, phân trang, lọc TT); UI `GimStTiChnhKTonAdmin.jsx`. |
| 15 | Báo Cáo Phân Tích (Analytics) | ADMIN | **🟢 P3-Low** | ✅ **Done** | API `GET /api/v1/admin/analytics/dashboard` (đăng ký/HK, SV/khoa, GD theo TT, top lớp theo sĩ số HK mới nhất); UI `BoCoPhnTchAnalytics.jsx` biểu đồ thanh (CSS). |
| 16 | Quản lý Lớp Giảng dạy & Điểm danh | TEACHER | **🟠 P1-High** | ✅ **Done** | API `/api/v1/lecturer/attendance/*` (lớp phụ trách, buổi điểm danh, PATCH dòng); bảng `Buoi_Diem_Danh`, `Diem_Danh_Dang_Ky`; SV `POST /api/v1/attendance/me/check-in`; UI `QunLLpGingDyimDanh.jsx`; seed `GV_SEED` ↔ `gv01`. |
| 17 | Mạng lưới Nhập & Quản Lý Điểm | TEACHER | **🟡 P2-Medium** | ✅ **Done** | API `GET/PATCH/POST /api/v1/lecturer/grades/...` (nháp `CHO_CONG_BO` → công bố `DA_CONG_BO`); GPA/transcript & tiên quyết chỉ tính điểm đã công bố; UI `MngLiNhpQunLimGradingSystem.jsx`. |
| 18 | Gác thi & Xử lý Khiếu nại (Phúc khảo) | TEACHER | **🟢 P3-Low** | ⬜ Todo | Quy trình nghiệp vụ lắt nhắt của riêng Giảng viên, nhường chỗ cho code lõi Queue. |
| 19 | Cánh Tay Phải "Cố Vấn Học Tập" | TEACHER | **🟡 P2-Medium** | ✅ **Done** | API `GET /api/v1/lecturer/advisory/at-risk?minFailedCredits=12` (SV cùng khoa GV, tổng TC môn rớt điểm công bố dưới 1.0); UI `CnhTayPhiCVnHcTpAcademicAdvising.jsx`. |
| 20 | **Đăng nhập truyền thống & Quản lý Phiên** | ALL | **🔴 P0-Blocker** | ✅ Done | Cánh cửa bảo vệ. Không có JWT & Login (Spring Security) thì mọi API đều tịt ngòi 401. |
| 21 | **Hệ thống Phân Quyền Đa Tầng (RBAC)** | ADMIN | **🔴 P0-Blocker** | ✅ Done | Role Spring chặn API (Proxy Pattern). Tách Admin, Student, Teacher. |
| 22 | Xác thực Đa Yếu Tố (MFA / 2FA) | ADMIN | **🟢 P3-Low** | ⬜ Todo | Gửi Email mã OTP bảo mật Login. Chỉ để buff thêm điểm. |
| 23 | Lịch sử Nhật Ký Dấu Chân (Audit Trails) | ADMIN | **🟢 P3-Low** | ⬜ Todo | Bắt sự kiện System log ai click vào sửa điểm. Có thể tóm lược bỏ qua. |

---
## Lộ trình triển khai khuyến nghị (Execution Flow)
1. **Giai đoạn 1 (Core & Infra - Tuần 1):** Code dứt điểm toàn bộ các Task P0 (Auth, RBAC, Redis Queue, Load Data Master).
2. **Giai đoạn 2 (Logic Đăng Ký - Tuần 2):** Xây dựng cầu nối Go (Queue) -> Kafka -> Java (Validation, SQL Update).
3. **Giai đoạn 3 (Nghiệp vụ Xung Quanh - Tuần 3):** Timeline TKB, Degree Audit, Thanh toán Học phí.
4. **Giai đoạn 4 (Phụ trợ & Trang trí - Tuần 4):** Xử lý luồng P2 và P3 còn lại, test Performance JMeter.

---
## P0 + P1 Checklist
- [x] Task 20 - Auth/JWT Login (`AuthController`, `WebSecurityConfig`, JWT)
- [x] Task 21 - RBAC (`@EnableMethodSecurity`, `@PreAuthorize`, Role enum)
- [x] Task 1 - Dashboard SV (Frontend UI: `DashboardSinhVienTrangCh.jsx`)
- [x] Task 13 - Data Master API (Entities: Khoa, NganhDaoTao, HocKy, GiangVien, HocPhan, LopHocPhan, DangKyHocPhan, SinhVien, HoSoSinhVien + Repos + Services + Controllers + GlobalExceptionHandler)
- [x] Task 12 - Setup Queue/Redis/Kafka (Go service) - Cấu trúc: config/ infra/ domain/ service/ handler/ + Dockerfile + docker-compose cập nhật
- [x] Task 7 - Kafka Consumer + Chain of Responsibility Validation (DuplicateRegistration → ScheduleConflict → PrerequisiteCourse) + DangKyHocPhanServiceImpl + SinhVienRepository
- [x] Task 6 - Tìm kiếm môn học: CourseSearchRequest + CourseSearchSpecification (JPA Specification Builder Pattern) + ICourseSearchService + CourseSearchServiceImpl + CourseSearchController (GET /api/v1/courses)
- [x] Task 10 - Dịch vụ Thời khóa biểu: `TimetableController` + `ITimetableService/TimetableServiceImpl` + DTO `TimetableResponse` + frontend `DchVThiKhaBiuThngMinh.jsx` gọi API `GET /api/v1/timetable/me`
- [x] Task 4 - Bảng điểm / Transcript: `BangDiemMon` + `TranscriptController` + `ITranscriptService/TranscriptServiceImpl` + `findTranscriptRows` + frontend `KimTraTinHcTpTranscriptDashboard.jsx` gọi `GET /api/v1/transcript/me`
- [x] Task 9 - Thanh toán QR: `PaymentController` + `IPaymentService/PaymentServiceImpl` + `PaymentGatewayAdapter` (MOCK/VNPay/MoMo) + `GiaoDichThanhToan` + UI `ThanhTonQrCodeOpenApi.jsx`
- [x] Task 8 - Ví SV: `ViSinhVien` + `GiaoDichVi` + `IWalletService/WalletServiceImpl` + `WalletController` + `confirm-mock` + `sumHocPhiDangKyBySinhVien` + UI `VSinhVinStudentWallet.jsx`
- [x] Task 2 - Hồ sơ SV: `findWithProfileByTaiKhoanId` + `IStudentProfileService/StudentProfileServiceImpl` + `StudentProfileController` + UI `TraCuHSCNhnThTcOnline.jsx`
- [x] Task 16 - Điểm danh GV: `BuoiDiemDanh` + `DiemDanhDangKy` + `IAttendanceService/AttendanceServiceImpl` + `LecturerAttendanceController` + `StudentAttendanceController` + UI `QunLLpGingDyimDanh.jsx` + `DataSeeder` (GV_SEED)
- [x] Task 17 - Nhập điểm GV: `BangDiemMonRepository` + `IGradingService/GradingServiceImpl` + `LecturerGradingController` + `findGradebookRowsForLop` + transcript filter công bố + UI `MngLiNhpQunLimGradingSystem.jsx`
- [x] Task 5 - Giỏ trước giờ G: `GioHangDangKy` + `GioHangDangKyRepository` + `IPreRegistrationCartService/PreRegistrationCartServiceImpl` + `PreRegistrationCartController` + `TkbSlotConflictUtils` (dùng chung xung đột TKB) + UI `TnhNngTrcGiGPreRegistrationGiLp.jsx`
- [x] Task 11 - Lịch thi & đánh giá GV: `LichThi` + `PhieuDuThi` + `DanhGiaGiangVien` + `ExamScheduleController` + `LecturerRatingController` + services + `HocKyRepository.findTopByOrderByIdHocKyDesc` + `DataSeeder` demo + UI `LchThinhGiGv.jsx`
- [x] Task 14 - Giám sát tài chính Admin: `IAdminFinanceService/AdminFinanceServiceImpl` + `AdminFinanceController` + queries `GiaoDichThanhToan`/`GiaoDichVi`/`DangKyHocPhan`/`ViSinhVien` + UI `GimStTiChnhKTonAdmin.jsx`
- [x] Task 15 - Analytics Admin: `IAdminAnalyticsService/AdminAnalyticsServiceImpl` + `AdminAnalyticsController` + queries tổng hợp `DangKy`/`SinhVien`/`GiaoDichThanhToan`/`LopHocPhan` + UI `BoCoPhnTchAnalytics.jsx`
- [x] Task 19 - Cố vấn học tập: `ILecturerAdvisoryService/LecturerAdvisoryServiceImpl` + `LecturerAdvisoryController` + `DangKyHocPhanRepository` (at-risk + GPA) + UI `CnhTayPhiCVnHcTpAcademicAdvising.jsx`
