# Dev-Spec — F13 Lecturer: lớp được phân công

| Mã | F13 |
|----|-----|
| BA | [`ba_flow.md`](ba_flow.md) |
| Module chính | `backend-core` — course search + admin assign |

---

## 1) Trạng thái triển khai (2026-05)

| Thành phần | File | Ghi chú |
|------------|------|---------|
| Gán GV | [`AdminClassPublishController`](../../../../backend-core/src/main/java/com/example/demo/controller/AdminClassPublishController.java) `POST .../assign-giang-vien` | F03 |
| Lọc theo GV | [`CourseSearchController`](../../../../backend-core/src/main/java/com/example/demo/controller/CourseSearchController.java) query `idGiangVien` | F09 shared |
| Đặc tả search | [`CourseSearchRequest`](../../../../backend-core/src/main/java/com/example/demo/payload/request/CourseSearchRequest.java), [`CourseSearchSpecification`](../../../../backend-core/src/main/java/com/example/demo/repository/specification/CourseSearchSpecification.java) | |
| RBAC read lớp | **`@PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'LECTURER')")`** trên `GET /api/v1/courses` và `GET /api/v1/courses/{idLopHp}` | **Đã sửa** từ `TEACHER` → `LECTURER` để khớp enum role JWT |

---

## 2) API — tra cứu lớp “của GV” (baseline)

### `GET /api/v1/courses`

| Param | Bắt buộc | Mô tả |
|-------|----------|-------|
| `idHocKy` | khuyến nghị | Phạm vi học kỳ |
| `idGiangVien` | **có** cho use case F13 | ID PK bảng `giang_vien` |
| `page`, `size` | default 0/20 | max `size` 100 per controller |
| `keyword`, `idKhoa`, … | optional | giống F09 |

**Response**: `Page<CourseSearchResponse>` — cấu trúc Spring page (`content`, `totalElements`, …).

**Auth header**: `Authorization: Bearer <jwt>`.

### `GET /api/v1/courses/{idLopHp}`

Role set giống search — cho phép GV mở **chi tiết** một section (preview TKB trong response).

---

## 3) Gán GV (Admin-only)

```
POST /api/v1/admin/lop-hoc-phan/{idLopHp}/assign-giang-vien
```

Body [`LopHocPhanAssignGiangVienRequest`](../../../../backend-core/src/main/java/com/example/demo/payload/request/LopHocPhanAssignGiangVienRequest.java) — xem F03 `dev_spec`.

---

## 4) Map username → `idGiangVien` (pattern)

Entity [`GiangVien`](../../../../backend-core/src/main/java/com/example/demo/domain/entity/GiangVien.java) có quan hệ `taiKhoan` → `User`.

Pseudo:

```java
User u = userRepository.findByUsername(username).orElseThrow();
GiangVien gv = giangVienRepository.findByTaiKhoan_Id(u.getId()).orElseThrow();
Long idGv = gv.getIdGiangVien();
```

*(Controller chuyên dụng có thể bọc đoạn này — backlog F13 §3.2 trong BA.)*

---

## 5) Lỗi & status

| HTTP | Nguyên nhân |
|------|----------------|
| 401 | Thiếu JWT |
| 403 | Role không thuộc STUDENT/ADMIN/LECTURER |
| 200 + empty | Không có lớp thỏa filter |
| 404 (detail) | `idLopHp` không tồn tại — theo service impl |

---

## 6) Test gợi ý

| Case | Cách |
|------|------|
| RBAC | MockMvc với `@WithMockUser(roles = "LECTURER")` → 200 search |
| Filter | Seed 2 lớp cùng HK, 1 gán GV A — search `idGiangVien=A` trả 1 row |
| Regression | Đảm bảo không còn string role `TEACHER` trong `CourseSearchController` |

---

## 7) Backlog kỹ thuật (tuỳ chọn)

- Endpoint `.../lecturer/me` giấu `idGiangVien`.
- Trả thêm `siSoThucTe/siSoToiDa` normalized cho UI “fill rate” (nếu field đã có trong `CourseSearchResponse`).

---

## 8) Troubleshooting GV vẫn 403?

| Cause | Evidence | Fix |
|-------|-----------|-----|
| Token STUDENT không phải LECTURER | JWT decode lacks `ROLE_LECTURER` | đăng nhập `gv*` seed |
| CORS OPTIONS fail | Preflight không pass | check `WebSecurityConfig` cors |
| `idGiangVien` không match profile | Wrong integer | resolve id from GV admin screen |

---

## 9) Lịch sử

- 2026-05 Draft (gap RBAC).
- 2026-05 Cập nhật sau khi align `LECTURER` trong controller + mở rộng spec.
- 2026-05 Thêm troubleshoot matrix đồng bộ với cross C02/C06
