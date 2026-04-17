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
| 2 | Tra cứu Hồ sơ Cá nhân & Thủ tục online | STUDENT | **🟡 P2-Medium** | ⬜ Todo | Chỉ hiển thị info tĩnh, làm sau khi xong đăng ký môn. |
| 3 | Cây Khung Chương Trình (Degree Audit) | STUDENT | **🟠 P1-High** | ✅ **Done** | API `GET /api/v1/degree-audit/me` trả CTĐT theo ngành SV + mapping CTĐT↔Học phần và trạng thái hoàn thành; UI `CyKhungChngTrnhDegreeAuditRoadmap.jsx` hiển thị theo khối kiến thức. |
| 4 | Kiểm tra tiến độ học tập (Transcript/Bảng điểm)| STUDENT | **🟠 P1-High** | ✅ **Done** | Bảng `Bang_Diem_Mon` + API `GET /api/v1/transcript/me` (lọc `hocKyId`), GPA hệ 4; cập nhật prerequisite qua điểm ≥ 1.0; UI `KimTraTinHcTpTranscriptDashboard.jsx`. |
| 5 | Tính năng Trước Giờ G (Giỏ hàng Pre-Registration)| STUDENT | **🟡 P2-Medium** | ⬜ Todo | Tính năng tiện ích, bản nháp, có thể bỏ qua để test lõi thực tế (Đăng ký thẳng) trước. |
| 6 | Lọc Môn Đỉnh Cao (Tìm kiếm môn học) | STUDENT | **🟠 P1-High** | ✅ **Done** | JPA Specification (Builder Pattern) dynamic query: keyword + idHocKy + soTinChi + loaiMon + chiConCho. Page + Sort. API: GET /api/v1/courses. |
| 7 | Thuật Toán Logic Đóng Chốt (Validation Rules) | STUDENT | **🟠 P1-High** | ✅ **Done** | Chain of Responsibility: DuplicateRegistration → ScheduleConflict → PrerequisiteCourse + Kafka Consumer cầu nối Go→Java. |
| 8 | Ví Sinh Viên (Student Wallet) | STUDENT | **🟡 P2-Medium** | ⬜ Todo | Tính năng ví nội bộ làm sau khi xong cổng thanh toán chính thức API. |
| 9 | Thanh Toán QR Code (Open API VNPay/MoMo) | STUDENT | **🟠 P1-High** | ✅ **Done** | API `POST /api/v1/payments/tuition-qr` + Adapter MOCK/VNPay/MoMo stub + bảng `Giao_Dich_Thanh_Toan`; UI `ThanhTonQrCodeOpenApi.jsx` tạo QR/liên kết. |
| 10 | Dịch vụ Thời Khóa Biểu thông minh | STUDENT | **🟠 P1-High** | ✅ **Done** | API `GET /api/v1/timetable/me` lấy dữ liệu thật từ đăng ký học phần + `thoi_khoa_bieu_json`, render UI realtime theo JWT Student. |
| 11 | Lịch Thi & Đánh Giá Giảng Viên | STUDENT | **🟡 P2-Medium** | ⬜ Todo | Modules Râu ria (Đánh giá chỉ là Form lưu Database). |
| 12 | **Setup Cấu Hình Giờ Vàng (Queue / Kafka / Redis)**| ADMIN | **🔴 P0-Blocker** | ✅ **Done** | **🔥 TRÁI TIM DỰ ÁN GOLANG**. Triển khai: Cooldown → DistributedLock → Lua DECR Slot → Kafka Publish. |
| 13 | Quản Lý Danh Mục Khung & Mở Lớp | ADMIN | **🔴 P0-Blocker** | ✅ **Done** | Admin phải bơm Data môn học Data Master vào CSDL thì SV mới đăng ký được. |
| 14 | Giám Sát Tài Chính (Kế Toán Admin) | ADMIN | **🟡 P2-Medium** | ⬜ Todo | Dashboard Kế toán. View sau khi Payment Gateaway chạy nuột. |
| 15 | Báo Cáo Phân Tích (Analytics) | ADMIN | **🟢 P3-Low** | ⬜ Todo | Vẽ biểu đồ Dashboard Chart. Trang trí Admin (Chỉ làm khi rảnh rỗi). |
| 16 | Quản lý Lớp Giảng dạy & Điểm danh | TEACHER | **🟠 P1-High** | ⬜ Todo | Tính năng chính diện của User Giảng viên. Bắt buộc có. |
| 17 | Mạng lưới Nhập & Quản Lý Điểm | TEACHER | **🟡 P2-Medium** | ⬜ Todo | Teacher nhập điểm, cập nhật state thành Phê Duyệt đẩy về Bảng điểm SV. |
| 18 | Gác thi & Xử lý Khiếu nại (Phúc khảo) | TEACHER | **🟢 P3-Low** | ⬜ Todo | Quy trình nghiệp vụ lắt nhắt của riêng Giảng viên, nhường chỗ cho code lõi Queue. |
| 19 | Cánh Tay Phải "Cố Vấn Học Tập" | TEACHER | **🟡 P2-Medium** | ⬜ Todo | Bộ lọc sinh viên nợ > 12 tín chỉ rớt đài. Cần thiết nhưng không gấp. |
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
