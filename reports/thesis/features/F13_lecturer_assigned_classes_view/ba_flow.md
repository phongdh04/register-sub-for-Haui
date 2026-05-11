# BA-Flow — F13 Giảng viên xem lớp được phân công

| Mã | F13 |
|----|-----|
| Vai trò chủ đạo | `LECTURER` (JWT authority `ROLE_LECTURER` — không dùng `TEACHER`) |
| Luồng dữ liệu chủ đạo | Admin gán GV lên **`lop_hoc_phan`** (F03) → GV tra cứu lớp “của tôi” |
| Dev | [`dev_spec.md`](dev_spec.md) |
| Cross | [`cross/06_rbac_security.md`](../../cross/06_rbac_security.md), [`cross/04_api_catalog.md`](../../cross/04_api_catalog.md) § course search |

---

## 1) Mục đích

Giảng viên cần danh sách **section** (lớp học phần) được gán cho mình trong một học kỳ để:

- chuẩn bị giảng dạy, phòng, ca;
- liên kết với các module khác (điểm danh, gradebook — ngoài scope lõi F13 nhưng dùng chung dữ liệu `id_lop_hp`).

---

## 2) Nguồn sự thật dữ liệu

| Thực thể | Trường | Ý |
|----------|--------|---|
| `lop_hoc_phan` | `id_giang_vien` (FK) | Lớp thuộc GV này sau khi admin assign |
| `giang_vien` | liên kết `User`/`tai_khoan` | Map username đăng nhập → GV |

Chi tiết entity: [`GiangVien`](../../../../backend-core/src/main/java/com/example/demo/domain/entity/GiangVien.java), [`LopHocPhan`](../../../../backend-core/src/main/java/com/example/demo/domain/entity/LopHocPhan.java).

---

## 3) Hai cách đáp ứng UX (baseline vs tối ưu)

### 3.1 Baseline MVP (đang spec đủ cho luận văn — **đã khớp RBAC** trong core)

**Bước A** — Frontend (hoặc BFF nhỏ) resolve `id_giang_vien` tương ứng tài khoản đăng nhập (`gv01`): có thể từ cache profile, lookup API nội bộ, hoặc `GET` admin-only **không** khuyến nghị; phương án production-friendly nằm ở **Bước B** backlog.

**Bước B** — Gọi tìm kiếm lớp public:

```http
GET /api/v1/courses?idHocKy={}&idGiangVien={}&page=0&size=50
Authorization: Bearer <JWT lecturer>
```

`CourseSearchController` cho phép `hasAnyRole('STUDENT','ADMIN','LECTURER')`.

### 3.2 Tối ưu backlog (RESTful “me” — *chưa* bắt buộc trong repo)

Endpoint dạng `GET /api/v1/lecturer/lop-hoc-phan/me?hocKyId=` nội bộ map `authentication.getName()` → `GiangVienRepository` và forward sang cùng `ICourseSearchService` — giảm lộ ID trên client.

*(Chỉ mô tả; implement khi backlog cho phép.)*

---

## 4) Tiền điều kiện

- Tài khoản có **`ROLE_LECTURER`**.
- Đã biết **`id_giang_vien`** khi gọi search (baseline) HOÀN TOÀN rõ trong payload profile/từ backend.
- Có **`id_hoc_ky`** (dropdown học kỳ).

---

## 5) Luồng chính — xem danh sách

1. GV đăng nhập cổng `/teacher`.
2. Chọn học kỳ.
3. Hệ thống gọi `GET /api/v1/courses` với filter `idGiangVien` = GV hiện tại.
4. Hiển thị bảng: mã HP, mã lớp, TKB preview, SL đăng ký (theo các field trong [`CourseSearchResponse`](../../../../backend-core/src/main/java/com/example/demo/payload/response/CourseSearchResponse.java)).

---

## 6) Luồng Admin liên quan (F03)

1. **`POST`** … `/assign-giang-vien` trên [`AdminClassPublishController`](../../../../backend-core/src/main/java/com/example/demo/controller/AdminClassPublishController.java).

Sau assign, GV phải thấy lớp xuất hiện trong kết quả search (Specification phải join đúng `id_giang_vien`).

---

## 7) Ngoại lệ

| Tình huống | UX / HTTP |
|------------|-----------|
| JWT hết hạn | 401, redirect `/login` |
| Thiếu quyền | 403 |
| `idGiangVien` sai / không có lớp | 200 + `Page` empty (bình thường) |
| Chưa assign GV | GV không nhìn thấy section — chỉ ADMIN thấy toàn cục search |

---

## 8) GIVEN-WHEN-THEN

| ID | Rule |
|----|------|
| F13-R1 | **GIVEN** GV A được gán lớp L **WHEN** search với `idGiangVien=A` và đúng HK **THEN** L nằm trong `content`. |
| F13-R2 | **GIVEN** JWT chứa **LECTURER** **WHEN** gọi `/api/v1/courses` **THEN** không bị 403 chỉ do role string sai. |

---

## 9) Acceptance

- [x] `CourseSearchController` dùng role **`LECTURER`** khớp hệ JWT (đã chỉnh trong codebase kèm tài liệu này).
- [ ] UI Teacher hiển thị danh sách lớp phụ trách theo học kỳ (frontend có thể nằm ngoài phạm vi thesis nếu chỉ chứng minh API).

---

## 10) Modules giảng viên khác (tham chiếu)

Để báo cáo mở rộng: [`LecturerAttendanceController`](../../../../backend-core/src/main/java/com/example/demo/controller/LecturerAttendanceController.java) `myClasses()`, [`LecturerGradingController`](../../../../backend-core/src/main/java/com/example/demo/controller/LecturerGradingController.java) — cũng resolve class từ username; có thể tái sử dụng pattern service.

---

## 11) Lịch sử

- 2026-05 Khởi tạo (ghi nhận gap RBAC).
- 2026-05 Cập nhật: RBAC `TEACHER` → `LECTURER` trong `CourseSearchController`; mở rộng BA.
