# BA-Flow — F10 Đăng ký học phần (chính thức / cấp slot)

| Trường | Giá trị |
|--------|---------|
| Mã chức năng | F10 |
| Tên chức năng | Sinh viên đăng ký lớp học phần trong học kỳ |
| Vai trò chủ đạo | STUDENT |
| Liên kết Dev-Spec | `features/F10_student_official_registration/dev_spec.md` |
| Liên kết có quan | F02 (window), F03 (publish lớp), F16 (rules), **F15** (queue Ingress) |
| Trạng thái | Draft |

---

## 1) Mục đích chức năng

Cho phép sinh viên ghi nhận **đăng ký thành công vào một lớp học phần** trong học kỳ khi các điều kiện nghiệp vụ thoả: cửa thời gian mở, lớp còn chỗ, không trùng lịch, tiên quyết (trong **pha OFFICIAL** đầy đủ validation). Thiếu luồng này, sinh viên không thể hình thành thời khóa biểu và dữ liệu tài chính/tín chỉ downstream rỗng.

---

## 2) Actor và RBAC

| Actor | Cho phép | Cấm |
|-------|-----------|-----|
| Sinh viên (`ROLE_STUDENT`) | Gọi API đọc và đăng ký/hủy cho **chính mình** | Đụng chỗ SV khác |
| Admin/GV indirect | không nằm F10 | chỉ được xem báo cáo ở F05/F06 |

Luồng **Ingress Giờ Vàng Go** không thay RBAC của Java — thường cần gateway riêng; xem **F15**.

---

## 3) Tiền điều kiện

1. JWT hợp lệ của sinh viên.
2. Tài khoản đã map `sinh_vien` và `lop` (**cần có cohort + nganh để cửa theo cohort** hoạt động dự đoán được).
3. `hoc_ky` xác định (explicit query hoặc **mặc định HK hiện hành**/mới nhất).
4. Cửa thời gian cho pha thích hợp **đang** mở (xem checker F02).

---

## 4) Hai pha được phép trong cùng API

Hệ thống **không tách hai endpoint khác nhau** cho PRE vs OFFICIAL; `POST /api/v1/registrations` chọn nhánh hoạt động nội bộ:

- Nếu **OFFICIAL** đang mở cho SV ⇒ coi là **đăng ký chính thức**.
- Else nếu **PRE** đang mở ⇒ coi là **ghi nhận sớm** với chuẩn validation **rút gọn** (bỏ rule tiên quyết học).

UI nên gọi `GET /api/v1/registrations/me/window-status` để hiển thị banner và chọn wording.

Giá trị `activePhase` phản chiếu: `NONE`, `PRE`, `OFFICIAL`, **`PRE_AND_OFFICIAL`** (cả hai cùng mở chồng thời gian).

---

## 5) Hậu điều kiện thành công

| Thành phần | Kết quả |
|-------------|---------|
| Giao tiếp | HTTP 201 + snapshot toàn học phần đã đăng kỳ |
| Xuất phát dữ liệu | một dòng `dang_ky_hoc_phan` và gia tăng `lop_hoc_phan.si_so_thuc_te` |
| TKB projection | Listener `RegistrationConfirmedEvent` **chỉ** được phát trong **Kafka** path ([`DangKyHocPhanServiceImpl`](../../../../backend-core/src/main/java/com/example/demo/service/impl/DangKyHocPhanServiceImpl.java)). **REST đồng bộ** trong [`RegistrationServiceImpl.register`](../../../../backend-core/src/main/java/com/example/demo/service/impl/RegistrationServiceImpl.java) hiện **không publish** — snapshot `/timetable/me/snapshot` (F12) có thể lệch tới khi rebuild F06; composite `/me` parse JSON vẫn thấy môn. |

---

## 6) Luồng chính (REST)

1. SV mở màn chọn học kỳ & lớp (load `GET /api/lop-hoc-phan/hoc-ky/{id}` hoặc course search authenticated).
2. UI gọi `GET .../window-status` hiển thị pha.
3. SV bấm *Đăng ký*.
4. `POST /api/v1/registrations?idLopHp=...` (optional `hocKyId`).
5. Hệ thống validate và cấp chỗ atomic.
6. UI render snapshot `tongSoMon`, học phí ước lượng, danh sách.

---

## 7) Alternate / Exception

| Mã | Tình huống | Xu lý UX |
|----|-----------|----------|
| EX-WINDOW | Closed | Forbidden message gồm cohort/ngành |
| EX-ROOM | Không còn chỗ | HTTP 409 "het cho" hoặc tương đương |
| EX-CHAIN | Duplicate / clash / prerequisites | HTTP 422 (message trong body) cụ thể từ chain |
| EX-MISMATCH HK | id lớp không thuộc HK chọn | 400 BAD_REQUEST |
| ALT-QUEUE | Làm trong giờ G | Frontend gọi Go ingress thay REST | xem F15 |

---

## 8) Business rules (GIVEN–WHEN–THEN)

**BR-F10-001 Ưu tiên OFFICIAL**

WHEN cửa OFFICIAL cho SV đang mở  
THEN một request vào nhánh đăng ký sử dụng **chuỗi validation đầy đủ**.

**BR-F10-002 PRE chỉ học khi không OFFICIAL**

WHEN chỉ có PRE mở  
THEN chỉ ép duplicate và conflict lịch, **không** tiên quyết.

**BR-F10-003 Lớp phải cùng học kỳ**

WHERE `lop_hoc_phan.id_hoc_ky != hoặc không gắn` HK resolved  
THEN từ chối BAD_REQUEST.

**BR-F10-004 Không chồng chỗ**

GIVEN chỉ còn 0 chỗ trong DB và atomic increment sĩ số trả về 0 row thay đổi  
THEN từ chối không ghi nhận đăng ký.

---

## 9) Dữ liệu hiển thị và tiêu chí nghiệm thu

**Sau POST thành công** frontend hiển thị tóm tắt từ `RegistrationStudentResponse`:
- học kỳ, `tongSoMon`, `tongTinChi`, `tongHocPhi`;
- danh sách các dòng học đã có: mã/tên học phần, GV, chỉ số sĩ số đã học/ tối đa, học phí dòng.

**Acceptance:**
1. Với JWT SV hợp lệ và cửa OPEN, POST thêm một môn còn chỗ ⇒ 201 và `tongSoMon` tăng 1.
2. Hai lần liên tiếp cùng môn (trùng lớp học phần) ⇒ bị validator duplicate (422).
3. Khi chỉ có PRE opening, không cần môn tiên quyết vẫn đăng ký được môn mà trong OFFICIAL sẽ bị chặn (so sánh bằng test data).
4. Hai SV song song chỉ có 1 slot cuối — một thành công một thất bại chỗ không ghi vào hai dòng chỗ sai.

---

## 10) Giả định

- Ingress Go Kafka đồng bộ chỗ và DB không nằm trong single transaction với REST này; xử lý mâu thuẫn slot DB vs Redis được mô tả ở F15.

---

## 11) Phụ thuộc

- F01 JWT; F02 cửa đăng ký; F03 lớp đã PUBLISHED khi chính sách yêu cầu; F16 rule engine; tuỳ chọn **F15** Ingress Kafka.

---

## 12) Lịch sử

| Ngày | Thay đổi |
|------|----------|
| 2026-05 | Bổ sung §11–12; content core theo `RegistrationController` / `RegistrationServiceImpl` |