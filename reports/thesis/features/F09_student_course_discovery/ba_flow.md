# BA-Flow — F09 Tra cứu lớp học phần (Course discovery)

| Mã | F09 |
|----|-----|
| Vai trò chính | STUDENT |
| Vai trò phụ | ADMIN (quản lý/xem nhanh), LECTURER (lọc lớp phụ trách — xem F13) |
| Liên quan | F03 (lớp PUBLISHED), F10 (đăng ký), F13 (GV dùng chung search) |
| Dev | [`dev_spec.md`](dev_spec.md) |

---

## 1) Mục đích

Sinh viên cần **khám phá** danh mục lớp học phần trong học kỳ trước khi đăng ký:

- lọc theo từ khóa, tín chỉ, khoa, còn chỗ, trạng thái `DANG_MO`;
- xem **chi tiết** một section (TKB, học phí, giảng viên nếu đã gán);
- (Tuỳ sản phẩm) nhận **gợi ý** theo chương trình — endpoint song song với registry gợi ý.

Hệ thống hỗ trợ **hai tầng API**: public **không JWT** (danh sách thô) và **JWT rich search** (phân trang, filter mạnh).

---

## 2) Actor

| Actor | Cách dùng |
|-------|-----------|
| STUDENT | `GET /api/v1/courses` + detail; gợi ý `RegistrationSuggestionController` |
| ADMIN | Cùng search để tra cứu khi hỗ trợ SV |
| LECTURER | Dùng `idGiangVien` filter (F13); RBAC dùng role **`LECTURER`** khớp JWT |

---

## 3) Tiền điều kiện

- Rich search: có JWT hợp lệ với authority phù hợp.
- Public list: không cần đăng nhập (dùng cho landing hoặc guest — cẩn trọng dữ liệu lộ).

---

## 4) Luồng SV điển hình

### Cách A — Danh mục public (nhanh)

1. Chọn học kỳ.
2. `GET /api/lop-hoc-phan/hoc-ky/{idHocKy}` (không JWT).
3. Mở chi tiết `GET /api/lop-hoc-phan/{id}`.

### Cách B — Tìm kiếm nâng cao (trong portal)

1. Đăng nhập.
2. `GET /api/v1/courses?idHocKy=&keyword=&chiConCho=true&trangThai=DANG_MO&page=0&size=20`.
3. Drill-down `GET /api/v1/courses/{idLopHp}`.

---

## 5) Ngoại lệ

| Case | Xu lý |
|------|--------|
| Gọi `/api/v1/courses` không có token | **401** |
| Token thiếu role | **403** |
| `idLopHp` không tồn tại (detail) | **404** theo service |

---

## 6) Business rules (UI)

- Mặc định filter `trangThai=DANG_MO` để tránh SV thấy lớp đóng (có thể đổi khi admin debug).
- `chiConCho=true` khi SV chỉ muốn lớp còn slot.

---

## 7) Acceptance

- [ ] SV đã login gọi được search + detail.
- [ ] Public path hoạt động **không** JWT theo `WebSecurityConfig`.
- [ ] LECTURER có thể gọi search (đồng bộ với `CourseSearchController` dùng `hasAnyRole(..., 'LECTURER')`).

---

## 8) Phụ thuộc

- `CourseSearchSpecification` whitelist sort field.
- F03 publish lifecycle.

---

## 9) GIVEN-WHEN-THEN

| ID | Rule |
|----|------|
| F09-R1 | **GIVEN** guest **WHEN** gọi catalogue public **THEN** không cần Bearer. |
| F09-R2 | **GIVEN** SV **WHEN** search rich **THEN** header JWT bắt buộc. |
| F09-R3 | **GIVEN** `chiConCho=true` **WHEN** search **THEN** chỉ các lớp còn slot thỏa filter service. |

---

## 10) Wireframe ASCII (Student portal)

```
┌── Danh mục lớp — HK dropdown [____] ──────────────────┐
│ [Tìm kiếm____] ☑ chỉ lớp còn chỗ   Trang ◀ 1 ▶       │
│ Mã HP | Môn        | TC | GV      | Slot | [Chi tiết]│
└────────────────────────────────────────────────────────┘
```

---

## 11) Kiểm thử gợi ý

| TID | Steps |
|-----|-------|
| F09-QA-01 | Guest GET `/lop-hoc-phan/hoc-ky/1` → 200 |
| F09-QA-02 | No header GET `/api/v1/courses` → 401 |
| F09-QA-03 | LECTURER filter `idGiangVien` self id → chỉ các lớp gán GV |

---

## 12) Lịch sử

- 2026-05 Khởi tạo.
- 2026-05 Cập nhật RBAC LECTURER; tách luồng public vs authenticated.
- 2026-05 Wireframe + GWT + QA matrix đồng bộ các feature khác.
