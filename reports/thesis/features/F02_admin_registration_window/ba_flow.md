# BA-Flow — F02 Quản lý cửa sổ đăng ký

| Trường | Giá trị |
|--------|---------|
| Mã chức năng | F02 |
| Tên chức năng | Quản lý cửa sổ đăng ký (theo cohort/ngành) |
| Vai trò chủ đạo | ADMIN |
| Loại chức năng | Admin |
| Liên kết Dev-Spec | `features/F02_admin_registration_window/dev_spec.md` |
| Trạng thái | Draft |

---

## 1) Mục đích chức năng

Hệ thống cần khóa và mở thời điểm sinh viên được phép thao tác **pha PRE** ( đăng ký dự kiến / giỏ nháp / intent ) và **pha OFFICIAL** (đăng ký chính thức cấp slot môn học) theo học kỳ, **theo khóa (năm nhập học)** và **tuỳ chọn** theo **ngành**. Nếu thiếu cấu hình rõ phạm vi, không thể vận hành công bằng giữa nhiều khóa mở song song các chính sách cửa khác nhau hoặc sẽ dẫn tới cửa mở toàn cục vô chủ không kiểm soát được SLA.

Người dùng: **quan trị viên học vụ**.

---

## 2) Đối tượng sử dụng và quyền hạn

| Actor | Vai trò | Được phép | Không được |
|-------|---------|-----------|------------|
| Admin học vụ | Cấu hình vòng đời đăng ký | CRUD window, `open-now`, sửa lịch fallback HK | —
| Sinh viên | Chịu áp dụng | Chỉ xem trạng thái qua API của mình (ngoài scope F02 nhưng liên quan) | Không chỉnh sửa config |
| Hệ thống | Checker thời gian | Thuật toán Ưu tiên window DB trước, fallback HK sau | Bypass rule khi không đủ dữ liệu |

---

## 3) Tiền điều kiện

1. Admin đã đăng nhập với `ROLE_ADMIN`.
2. **`hoc_ky` mục tiêu đã được tạo** trong hệ thống master data.
3. Nếu cấu hình window theo **ngành**, phải có **`namNhapHoc` (cohort)** tương ứng — business rule: không thể “chỉ ngành không khóa”.
4. Các mốc thời gian `openAt < closeAt` (hoặc tương đương khi dùng `open-now`).

---

## 4) Hậu điều kiện

| Nhánh | Trạng thái |
|-------|------------|
| Success — tạo/sửa | Một dòng `registration_window` (hoặc HK fallback) phản ánh đúng phạm vi; sinh viên thuộc phạm vi đó được tool gate cho phép tương tác trong khoảng mở. |
| Duplicate scope | Conflict nghiệp vụ → UI admin phải sửa bản có sẵn hoặc xóa rồi tạo lại. |
| Bad cohort/ngành | 400 và message rõ để BA QA kiểm. |

---

## 5) Use case chính (Main Success Scenario)

**UC-CRUD một window định danh.**

1. Admin mở màn hình *Cấu hình cửa sổ đăng kỳ*.
2. Hệ thống tải danh sách theo **`hocKyId`** + có thể lọc **`phase`**.
3. Admin bấm *Thêm*.
4. Admin chọn: học kỳ, PRE hoặc OFFICIAL, (tuỳ chọn) cohort, (tuỳ chọn) ngành, `openAt`, `closeAt`, ghi chú.
5. Hệ thống validate không trùng scope.
6. Lưu thành công; hàng được highlight `dangMo` nếu thời gian hiện tại nằm trong khoảng.

**UC-Quick-open (mở ngay)**

1. Admin chọn học kỳ + phase + scope cohort/ngành.
2. Bấm *Mở ngay*.
3. Hệ thống đặt `openAt = now`, `closeAt = now + durationDays` (mặc định **30 ngày** khi không gửi `durationDays`).
4. Nếu đã tồn tại đúng scope → **ghi đè mốc open/close**, không nhân đôi bản ghi.

---

## 6) Use case phụ và ngoại lệ

| Mã | Tình huống | Mô tả | Cách xử lý |
|----|-----------|-------|------------|
| ALT-1 | Đối chiếu với HK legacy | HK vẫn có 4 mốc `pre_dang_ky_*` và `dang_ky_chinh_thuc_*` | Khi không match window trong DB → `RegistrationScheduleChecker` fallback các cột này. |
| ALT-2 | Window trùng thời gian nhưng scope khác | Hai cohort khác nhau | Được phép — độ phân giải theo cohort. |
| EX-1 | Cố tạo `idNganh` khi không có cohort | Logic DB + service chặn | 400 cụ thể. |
| EX-2 | Trùng (hocKy, phase, cohort, nganh*) | UNIQUE index trong DB sau migration | HTTP 409. |

\* `nganh`: null được coi như một “giá trị” riêng trong unique index Postgres (functional index trong migration Sprint 1).

---

## 7) Business Rules (GIVEN–WHEN–THEN)

**BR-F02-001 Phạm vi null**

GIVEN học kỳ H và pha P  
WHEN namNhapHoc null và idNganh null  
THEN window được hiểu là **áp dụng cho mọi cohort và mọi ngành** trong học kỳ đó (trừ các window cụ thể hơn theo checker).

**BR-F02-002 Ưu tiên specificity**

GIVEN có nhiều window có thể match SV  
THEN matcher chọn cửa cụ thể nhất theo logic `RegistrationScheduleChecker` (hocKy + phase → cohort → ngành → fallback HK).

**BR-F02-003 Đối chiếu thời gian**

WHEN Instant.now() không nằm trong [openAt, closeAt]  
THEN cho phần cửa đó = **đóng** (trừ khi có window khác mở cho scope khác của cùng pha).

**BR-F02-004 Fallback HK**

WHEN không có row `registration_window` phù hợp và cột học kỳ tương ứng vẫn null (cặp không áp dụng)  
THEN phần cửa thời gian **không chặn** (theo checker: null timestamps = không áp fallback cho pha đó — xem chi tiết `RegistrationScheduleChecker`).

---

## 8) Wireframe và dữ liệu hiển thị Admin

Danh sách (read model `RegistrationWindowResponse`):

- Học kỳ hiển thị dạng chuẩn `HK{kyThu} {namHoc}`
- Phase: PRE / OFFICIAL
- Cohort hiển thị năm 4 chữ số hoặc “Tất cả”
- Ngành hoặc “Tất cả”
- openAt / closeAt theo TZ hiển thị (prefer browser local có footnote UTC)
- Cờ **`dangMo`** (computed)
- `createdBy` audit ngắn

Form sửa: mirror payload `RegistrationWindowUpsertRequest`.

---

## 9) Acceptance Criteria

1. Admin list window theo `hocKyId` bắt buộc; lỗi 400 khi thiếu.
2. Tạo mới không tạo trùng unique scope trong DB (409 khi collide).
3. `open-now` mở ra window hoạt động được phía SV trong cùng phút (timezone aware).
4. SV thuộc cohort có window riêng **không** bị ép theo cohort khác và ngược lại.
5. Tài liệu song song chỉ ra path legacy `PUT /api/v1/admin/hoc-ky/{id}/lich-dang-ky` vẫn hoạt động khi chưa triển khai window granular.

---

## 10) Câu hỏi và giả định

- Giả định: chỉ có hai pha `PRE`, `OFFICIAL` trong enum `RegistrationPhase` — không có pha WAITLIST trong code hiện tại.
- Cần xác nhận thực tế học viện: có cần thêm weekday/hour không hay chỉ cần mốc absolute Instant.

---

## 11) Phụ thuộc

- Dữ liệu: `hoc_ky`, `registration_window`, `nganh_dao_tao`, `lop.nam_nhap_hoc` của sinh viên.
- Chức năng downstream: F08 (PRE), F10 (đăng ký chính thức đọc cửa).

---

## 12) Lịch sử

| Ngày | Thay đổi |
|------|----------|
| 2026-05 | Bổ sung §11–12; nội dung trước đó từ draft kiến trúc sprint 1 |
