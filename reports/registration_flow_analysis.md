# Báo cáo Phân tích Luồng chức năng Đăng ký học phần (Course Registration Flow)

## Tổng quan Kiến trúc
Luồng đăng ký học phần được thiết kế theo kiến trúc Microservices phân tán với cơ chế xử lý bất đồng bộ thông qua Kafka. Yêu cầu đăng ký từ sinh viên không đi thẳng vào database mà được đưa vào message queue để xử lý tuần tự, đảm bảo tính nhất quán (ACID) trong môi trường concurrency cao.

## Phân quyền & Vai trò (Roles) trong Luồng Đăng ký

### 1. Vai trò Quản trị viên (ADMIN)
**Nhiệm vụ:** Quản lý kỳ đăng ký, mở/đóng lớp, và theo dõi thống kê.
- **Quản lý lịch đăng ký:** Mở/đóng thời gian đăng ký chính thức cho từng học kỳ (`AdminHocKyScheduleController`). Đăng ký ngoài thời gian này sẽ bị từ chối (`registrationScheduleChecker.isOfficialRegistrationOpen`).
- **Quản lý Lớp học phần:** Tạo mới, chỉnh sửa, xóa lớp học phần.
  - Sử dụng nút **"Launch Semester"** (`/phat-hanh`) để mở lớp (chuyển trạng thái sang `DANG_MO`).
  - Sử dụng nút **"Kill Switch"** (`/dong-lop`) để khóa lớp thủ công khi cần bảo trì hoặc hết chỗ (`LopHocPhanController`).
- **Giám sát & Thống kê:** Xem dashboard theo dõi thời gian thực số lượng đăng ký, tỷ lệ lấp đầy các lớp học phần top đầu (`AdminAnalyticsServiceImpl`).

### 2. Vai trò Sinh viên (STUDENT)
**Nhiệm vụ:** Xem lớp, lên danh sách dự kiến và thực hiện đăng ký.
- **Xem danh sách lớp:** Lấy thông tin các lớp học phần được mở trong học kỳ để chuẩn bị đăng ký (`LopHocPhanController.getAllByHocKy`).
- **Giỏ đăng ký nháp (Pre-Registration):** Thêm, sửa, xóa các môn học vào "giỏ hàng" trước giờ mở đăng ký chính thức (`PreRegistrationCartController`).
- **Thực hiện Đăng ký (Core):**
  - Gửi yêu cầu đăng ký (thường qua API Gateway/Go Service) tạo message lên Kafka.
  - Hủy đăng ký: Gửi yêu cầu hủy đăng ký, hệ thống sẽ trả lại chỗ (decrement sĩ số) và đổi trạng thái thành `RUT_MON`.
- **Xem kết quả TKB:** Tra cứu thời khóa biểu cá nhân sau khi đăng ký thành công (`TimetableController.getMyTimetable`).

### 3. Vai trò Giảng viên (LECTURER / GIANG_VIEN)
**Nhiệm vụ:** Không trực tiếp thao tác vào luồng đăng ký.
- Giảng viên chỉ tiếp nhận kết quả của quá trình đăng ký thông qua danh sách sinh viên lớp học phần (Attendance / Grading). Sự cố đăng ký của sinh viên không xuất phát từ role giảng viên.

## Phân tích Logic xử lý Đăng ký (Backend Java - Worker)
Khi Message Kafka được Worker Java `DangKyHocPhanServiceImpl` xử lý, hệ thống chạy qua các chốt kiểm duyệt (Chain of Responsibility) để đánh giá tính hợp lệ:

1. **Kiểm tra Học kỳ & Lịch (Fail-fast):** Học kỳ có tồn tại không? Thời gian hiện tại có nằm trong lịch đăng ký chính thức không?
2. **Validation Chain:**
   - **TrungLop:** Tránh sinh viên đăng ký nhiều lớp của cùng 1 môn (`DuplicateRegistrationHandler`).
   - **TrungLich:** Trùng thời khóa biểu với các môn đã đăng ký khác (`ScheduleConflictHandler`).
   - **TienQuyet:** Chưa học hoặc rớt môn tiên quyết (`PrerequisiteCourseHandler`).
3. **Cập nhật Sĩ số (Concurrency Safe):** Thực hiện `incrementSiSoThucTe` bằng atomic UPDATE ở database. Nếu `siSoThucTe >= siSoToiDa`, DB sẽ từ chối và worker trả về "Hết chỗ".
4. **Ghi nhận DB:** Nếu vượt qua mọi chốt, ghi log đăng ký vào bảng `dang_ky_hoc_phan` với trạng thái `THANH_CONG`.

## Điểm có thể gây lỗi (Troubleshooting Suggestions)
Nếu luồng "đăng ký học phần đang gặp vấn đề", bạn có thể rà soát các chốt sau:
1. **Lịch đăng ký của Admin:** Học kỳ hiện tại đã được thiết lập khoảng thời gian mở đăng ký chính thức chưa? (`AdminHocKyScheduleController`).
2. **Trạng thái Lớp Học Phần:** Các lớp học phần đang ở trạng thái `CHUA_MO` hay `DANG_MO`? Admin đã bấm phát hành lớp chưa?
3. **Giới hạn sĩ số:** Lớp đã bị đầy chưa (`siSoThucTe == siSoToiDa`)?
4. **Worker Kafka:** Java Worker có đang subscribe đúng topic Kafka do Go Service đẩy tới không? Có exception văng ra ở bước validation không (check log `RegistrationValidationException`)?
