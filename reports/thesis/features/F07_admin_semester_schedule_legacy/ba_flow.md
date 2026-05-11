# BA-Flow — F07 Cấu hình “lịch đăng ký” fallback trên học kỳ (Legacy)

| Mã | F07 |
|----|-----|
| Vai trò chủ đạo | ADMIN |
| Trạng thái chức năng quan hệ | F02 (**registration_window**) ưu tiên; F07 chỉ là **fallback** trong [`RegistrationScheduleChecker`](../../../../backend-core/src/main/java/com/example/demo/support/RegistrationScheduleChecker.java) |
| Dev | [`dev_spec.md`](dev_spec.md) |
| Cross | [`cross/03_db_dictionary.md`](../../cross/03_db_dictionary.md) (`hoc_ky.*` lịch), [`cross/01_glossary.md`](../../cross/01_glossary.md) |

---

## 1) Vấn đề nghiệp vụ

Một số cơ sở triển khai **trước** khi có bảng `registration_window` theo cohort/ngành cần cấu hình nhanh bốn mốc thời gian trên một dòng **`hoc_ky`**:

| Pha BA | Hai cột Postgres (camel trong API) |
|--------|--------------------------------------|
| Pre-registration (PRE) | `pre_dang_ky_mo_tu` … `den` ← `preDangKyMoTu/Den` |
| Đăng ký chính thức OFFICIAL | `dang_ky_chinh_thuc_tu` … `den` ← `dangKyChinhThucTu/Den` |

Điều này tránh khóa toàn học kỳ chỉ vì admin chưa tạo hàng trong F02 — nhưng khi có window khớp sinh viên, **window thắng**.

---

## 2) Actor & quyền

| Actor | Hành động |
|-------|-----------|
| ADMIN | `PUT /api/v1/admin/hoc-ky/{id}/lich-dang-ky` |
| STUDENT / LECTURER | **Không** sửa; chỉ nhận tín hiệu “cửa đang mở” gián tiếp qua F02/`window-status` và rule đăng ký |

---

## 3) Tiền điều kiện

- Đã có bản ghi `hoc_ky` (tạo qua luồng quản trị học kỳ).
- Admin đăng nhập `ROLE_ADMIN`.
- Hai mốc mỗi pha chỉ được: **cùng null** (pha đó không bị học kỳ giới hạn qua học kỳ) hoặc **cùng có giá trị** với **`from ≤ to`** (instant).

---

## 4) Luồng chính (Main)

1. Admin mở màn **Cấu hình học kỳ / Lịch đăng ký** (portal).
2. Chọn học kỳ mục tiêu (id đã biết sau `GET /api/hoc-ky` hoặc list admin).
3. Nhập 4 Instant (timezone-aware UI → gửi ISO-8601) hoặc xóa cặp PRE / cặp OFFICIAL để không dùng fallback pha đó.
4. Lưu → backend validate cặp và ghi xuống `hoc_ky`.
5. Sinh viên gọi `GET /api/v1/registrations/me/window-status` (F02) — checker sẽ dùng **window DB trước**, sau đó fallback sang các Instant trên học kỳ nếu không khớp.

---

## 5) Luồng phụ / ngoại lệ

| Case | Backend | Gợi ý UX |
|------|---------|----------|
| Chỉ nhập một nửa cặp | `IllegalArgumentException` → thường map 400/500 tùy global handler | Hiển thị “nhập đủ từ–đến hoặc để trống cả hai” |
| `from > to` | `IllegalArgumentException` | Date picker range validation |
| Học kỳ không tồn tại | `EntityNotFoundException` → 404 | Refresh danh sách HK |

*(Mapper HTTP cụ thể cho `IllegalArgumentException` xem [`AbstractValidationHandler`](../../../../backend-core/src/main/java/com/example/demo/service/validation/AbstractValidationHandler.java) hoặc `@ControllerAdvice` trong project.)*

---

## 6) GIVEN-WHEN-THEN

| ID | Rule |
|----|------|
| F7-R1 | **GIVEN** cả hai mốc PRE null **WHEN** checker fallback **THEN** không dùng PRE theo học kỳ (pha PRE có thể đóng trừ khi window DB mở). |
| F7-R2 | **GIVEN** window cohort khớp SV **WHEN** tính mở/đóng **THEN** không cần đọc các cột fallback cho pha đã resolve bởi window. |

---

## 7) Acceptance criteria

- [ ] ADMIN có thể set/clear hai cặp Instant hợp lệ và đọc lại trong [`HocKyResponse`](../../../../backend-core/src/main/java/com/example/demo/payload/response/HocKyResponse.java).
- [ ] STUDENT không thể hit endpoint admin PUT.
- [ ] Sai cặp (một phần) bị reject với message rõ ràng từ `validateWindowPair`.
- [ ] `HocKyServiceImpl.toResponse` phản chiếu flag **đang mở** (`preDangKyDangMo`, `dangKyChinhThucDangMo`) dựa trên checker — hữu ích cho UI admin dashboard.

---

## 8) Wireframe ASCII (Admin)

```
┌── Học kỳ: HK2 — 2024-2025 ─────────────────────────────┐
│ PRE   [từ] [datetime-local]  [đến] [datetime-local]     │
│ OFF   [từ] [datetime-local]  [đến] [datetime-local]     │
│ [ Lưu ]   Trạng thái hiện tại: PRE ☐ mở  OFF ☐ mở      │
└────────────────────────────────────────────────────────┘
```

---

## 9) Lịch sử

- 2026-05 Khởi tạo.
- 2026-05 Mở rộng mô tả fallback, rule, acceptance.
