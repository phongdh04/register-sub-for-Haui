# BA-Flow — F12 Thời khóa biểu cá nhân (STUDENT)

| Mã | F12 |
|----|-----|
| Vai trò chủ đạo | STUDENT |
| Liên quan | F03 (TKB JSON trên `lop_hoc_phan`), F10/F11 (sự kiện đăng ký → projection), F06 (rebuild ADMIN) |
| Dev | [`dev_spec.md`](dev_spec.md) |
| Cross | [`cross/03_db_dictionary.md`](../../cross/03_db_dictionary.md) (`student_timetable_entry`), [`cross/04_api_catalog.md`](../../cross/04_api_catalog.md) |

---

## 1) Mục đích

Sinh viên xem **lịch học của chính mình** trong một học kỳ, để:
- học chủ động theo tiết/ngày/phòng;
- kiểm tra xung đột trực quan sau khi đăng ký (bổ sung với các rule server-side khác).

Hệ thống cung cấp **hai nguồn** hiển thị có mục tiêu khác nhau (xem §3).

---

## 2) Đối tượng và quyền

| Actor | Quyền |
|-------|-------|
| Sinh viên | JWT `ROLE_STUDENT`; tài khoản phải **đã map** sang bản ghi `sinh_vien`. |
| Admin | Không đọc TKB của SV qua các endpoint `/api/v1/timetable/me*` (ADMIN dùng F06 và công cụ khác). |
| Khách | Không được gọi API thời khóa biểu cá nhân. |

---

## 3) Hai nhánh nghiệp vụ (“Composite” vs “Snapshot projection”)

### 3.1 Nhánh A — **`GET /api/v1/timetable/me`** (composite / Task 10)

- **Input**: học kỳ tùy chọn (`hocKyId`); nếu bỏ trống → dùng **học kỳ hiện hành** (nếu hệ thống đã đánh `trang_thai_hien_hanh`).
- **Cách làm**: aggregate từ `dang_ky_hoc_phan` + chi tiết lớp, **parse** `lop_hoc_phan.thoi_khoa_bieu_json` thành danh sách “session” trong response.
- **Khi chọn UX**: báo cáo tóm tắt môn, tín chỉ, danh mục môn theo học kỳ; phù hợp UI “dashboard TKB”.
- **Hạn chế**: phụ thuộc parsing JSON và mô hình dữ liệu lớp; không phản ánh thời điểm eventual consistency của read-model sau event.

### 3.2 Nhánh B — **`GET /api/v1/timetable/me/snapshot`** (read-model Sprint 5)

- **Input**: **`hocKyId` bắt buộc** (avoid ambiguity).
- **Cách làm**: đọc bản ghi đã flatten trong bảng `student_timetable_entry` (1 row = 1 tiết/phần trong tuần của **một** đăng ký).
- **Khi chọn UX**: lưới lịch (grid tuần) **không** cần parse JSON phía client; cập nhật gần realtime sau khi giao dịch đăng ký **commit** (listener `AFTER_COMMIT`).
- **Hạn chế**: nếu lớp chưa có TKB JSON hợp lệ, projection có thể **0 slot** dù đã đăng ký (nghiệp vụ lịch vẫn do F03).

---

## 4) Tiền điều kiện

- Đã đăng nhập với role STUDENT.
- Hồ sơ `sinh_vien` gắn với `User` hiện tại.
- Với snapshot: biết `hocKyId` hợp lệ (thường lấy từ selector HK hoặc `GET /api/hoc-ky/hien-hanh`).

---

## 5) Luồng chính (Main success)

### UC1 — Xem TKB composite theo học kỳ hiện hành

1. Sinh viên vào portal Student → trang “Thời khóa biểu”.
2. Hệ thống gọi `GET /api/v1/timetable/me` (không query).
3. Hiển thị tên SV, học kỳ, tổng môn, tổng TC, và từng môn với các session parse từ JSON.

### UC2 — Xem snapshot lưới theo học kỳ chọn

1. Sinh viên chọn học kỳ trong dropdown (`id`).
2. Gọi `GET /api/v1/timetable/me/snapshot?hocKyId={id}`.
3. Hiển thị `totalSlots` và bảng/ lưới từ `entries[]` (thứ, tiết, phòng, GV, khóa học phần, `slotIndex`, …).

### UC3 — Sau khi đăng ký hoặc hủy (F10/F11)

1. Giao dịch nghiệp vụ hoàn tất trên server.
2. Projection (nếu thành công) thêm hoặc gỡ dòng trong `student_timetable_entry`.
3. SV refresh snapshot → lưới phản ánh thay đổi trong vòng **milliseconds → vài giây** (trừ lỗi projection — xem §7).

---

## 6) Luồng phụ / ngoại lệ

| Tình huống | Hệ thống | UX gợi ý |
|------------|----------|-----------|
| Tài khoản chưa map SV | `404` với thông điệp “chưa liên kết hồ sơ” | Hướng dẫn liên hệ phòng ĐT |
| `hocKyId` composite trỏ HK không tồn tại | `404` “Không tìm thấy học kỳ” | Reset selector |
| Snapshot thiếu `hocKyId` | `400` (Spring binding) | Bắt buộc chọn HK |
| Lớp không có TKB JSON | Composite: `sessions: []`; Snapshot: không có entry cho đăng ký đó | Banner “Lịch chi tiết chưa cập nhật” |
| Projection lỗi sau commit | Log server; đăng ký vẫn hiệu lực; snapshot có thể lệch | Nút “Làm mới”; admin F06 rebuild |

---

## 7) GIVEN-WHEN-THEN (business rules)

| ID | Rule |
|----|------|
| R1 | **GIVEN** SV đã đăng nhập **WHEN** gọi `/me` hoặc `/me/snapshot` **THEN** chỉ được thấy dữ liệu của **chính mình**. |
| R2 | **GIVEN** không truyền `hocKyId` **WHEN** gọi `/me` **THEN** hệ thống resolve HK hiện hành hoặc rỗng theo [`TimetableServiceImpl`](../../../../backend-core/src/main/java/com/example/demo/service/impl/TimetableServiceImpl.java). |
| R3 | **GIVEN** đăng ký mới được xác nhận **WHEN** transaction commit **THEN** projection chạy `AFTER_COMMIT` (không roll back đăng ký nếu projection fail). |

---

## 8) Wireframe ASCII — trang Student TKB

```
┌─────────────────────────────────────────────────────────────────┐
│  EduPort  [HK: ▼ HK2 2024-2025]                    [Đăng xuất]   │
├─────────────────────────────────────────────────────────────────┤
│  Thời khóa biểu — Nguyễn Văn A          Tổng: 18 TC | 6 môn     │
│  Tabs: [ Tổng quan (/me) ] [ Lưới tuần (snapshot) ]              │
├─────────────────────────────────────────────────────────────────┤
│  Mon   Tue      Wed       Thu       Fri                         │
│  ┌────┬────────┬─────────┬────────┬────────┐                      │
│  │    │ IT101  │         │_MATH_ │        │                     │
│  │    │ Tiết2-4│         │ Slot  │        │                     │
│  └────┴────────┴─────────┴────────┴────────┘                      │
└─────────────────────────────────────────────────────────────────┘
```

**Trường hiển thị khuyến nghị (snapshot)**: `maLopHp`, `tenHocPhan`, `tenGiangVien`, `thu`, `tiet`, `phong`, (optional `ngayBatDau/ketThuc` nếu lịch không lặp theo tuần).

---

## 9) Tiêu chí nghiệm thu

- [ ] STUDENT được 200 cho `/me`; non-STUDENT bị **403**.
- [ ] STUDENT được 200 cho `/me/snapshot?hocKyId=`; snapshot **không** trộn SV khác.
- [ ] Sau F10 trong môi trường demo, reload snapshot tăng `totalSlots` tương ứng số block JSON của lớp (hoặc 0 nếu chưa có TKB — ghi nhận là chấp nhận được với điều kiện F03).

---

## 10) Giả định / cần xác nhận với cơ sở

- Quy ước **`thu`**: đồng bộ với JSON (`2` = Monday, … — cần thống nhất với F03 và seed).
- Có hay không hiển thị **học phí / slot trống** trên trang TKB (ngoài scope F12 lõi).

---

## 11) Lịch sử

- 2026-05 Khởi tạo.
- 2026-05 Nâng cấp mô tả tối đa: hai nhánh, UC, acceptance, rule.
