# BA-Flow — `<Mã chức năng>` `<Tên chức năng>`

> Template tài liệu BA-Flow. Khi viết tài liệu thật, **xóa hết các block `> Hướng dẫn:`** và thay bằng nội dung thật.

| Trường | Giá trị |
|--------|---------|
| Mã chức năng | F0x |
| Tên chức năng | ... |
| Vai trò chủ đạo | STUDENT / LECTURER / ADMIN / SYSTEM |
| Loại chức năng | Auth / Student / Admin / Lecturer / Cross |
| Người chịu trách nhiệm BA | ... |
| Liên kết Dev-Spec | `features/F0x_*/dev_spec.md` |
| Trạng thái | Draft / Reviewed / Approved |

---

## 1) Mục đích chức năng

> Hướng dẫn: viết 3-5 câu trả lời các câu hỏi:
> - Chức năng này giải quyết vấn đề gì?
> - Cho ai dùng?
> - Nếu thiếu thì hệ thống sẽ thiệt hại gì?

---

## 2) Đối tượng sử dụng và quyền hạn

| Actor | Vai trò trong chức năng | Hành vi được phép | Hành vi bị cấm |
|-------|--------------------------|-------------------|-----------------|
| ... | ... | ... | ... |

---

## 3) Tiền điều kiện

> Hướng dẫn: liệt kê những trạng thái phải đúng trước khi actor thực hiện chức năng. Ví dụ:
> - Đã đăng nhập với role X.
> - Học kỳ Y đã được tạo.
> - Cửa sổ đăng ký đang mở.

---

## 4) Hậu điều kiện

> Hướng dẫn: trạng thái hệ thống sau khi chức năng hoàn tất ở các nhánh khác nhau (success / failure).

| Nhánh | Trạng thái sau khi xong |
|-------|--------------------------|
| Success | ... |
| Failure - lỗi nghiệp vụ | ... |
| Failure - lỗi hệ thống | ... |

---

## 5) Use case chính (Main Success Scenario)

> Hướng dẫn: viết các bước theo dạng đánh số. Mỗi bước nói **ai làm gì** và **hệ thống phản hồi gì**.

1. Actor mở màn hình ...
2. Hệ thống tải ...
3. Actor nhập ...
4. Hệ thống validate ...
5. Hệ thống xử lý ...
6. Hệ thống thông báo kết quả ...

---

## 6) Use case phụ và ngoại lệ

| Mã | Tình huống | Mô tả | Cách xử lý |
|----|-----------|-------|------------|
| ALT-1 | ... | ... | ... |
| ALT-2 | ... | ... | ... |
| EX-1 | ... | ... | ... |
| EX-2 | ... | ... | ... |

---

## 7) Business Rules

> Hướng dẫn: viết theo dạng GIVEN-WHEN-THEN để rõ ràng và testable.

- **BR-01**:
  - GIVEN ...
  - WHEN ...
  - THEN ...

- **BR-02**:
  - GIVEN ...
  - WHEN ...
  - THEN ...

---

## 8) Wireframe / Mô tả màn hình

### 8.1 Màn hình chính
> Hướng dẫn: mô tả layout dạng văn bản, vẽ ASCII hoặc gắn ảnh wireframe. Ghi rõ vị trí các vùng, nút, bảng.

```
+------------------------------------------------------+
| Header                                               |
+------------------------------------------------------+
| Filter bar: [HK] [Pha] [Tìm kiếm]   [+ Tạo mới]      |
+------------------------------------------------------+
| Bảng danh sách:                                      |
|  | Cột 1 | Cột 2 | Cột 3 | Trạng thái | Thao tác |   |
|  +-------+-------+-------+-------------+----------+   |
+------------------------------------------------------+
```

### 8.2 Field hiển thị

| Field | Kiểu hiển thị | Ràng buộc nhập | Lấy từ |
|-------|----------------|-----------------|--------|
| ... | ... | ... | API |

### 8.3 Trạng thái UI

| Trạng thái | Khi nào | Hiển thị |
|------------|---------|---------|
| Loading | ... | Skeleton + dòng "Đang tải…" |
| Empty | ... | Icon + dòng "Chưa có dữ liệu" |
| Error | ... | Banner đỏ + nút "Thử lại" |

---

## 9) Tiêu chí nghiệm thu (Acceptance Criteria)

- [ ] AC-01: ...
- [ ] AC-02: ...
- [ ] AC-03: ...
- [ ] AC-04: với role không hợp lệ phải bị từ chối với mã 403 và message tiếng Việt rõ.
- [ ] AC-05: thao tác trùng phải idempotent hoặc báo lỗi rõ.
- [ ] AC-06: dữ liệu hiển thị có Vietnamese localization, không hard-coded English.

---

## 10) Câu hỏi cần xác nhận

> Hướng dẫn: ghi lại tất cả điểm chưa rõ với GVHD/khách hàng.

- [ ] Q1: ...
- [ ] Q2: ...

---

## 11) Phụ thuộc

- Phụ thuộc nghiệp vụ vào: `<các chức năng khác>`.
- Phụ thuộc dữ liệu master vào: `<các bảng/seed>`.

---

## 12) Lịch sử sửa đổi

| Ngày | Người | Thay đổi |
|------|-------|----------|
| ... | ... | Tạo mới |
