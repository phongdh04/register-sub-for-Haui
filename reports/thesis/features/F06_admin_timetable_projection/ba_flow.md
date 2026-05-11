# BA-Flow — F06 Công cụ rebuild projection TKB (Admin)

| Mã | F06 |
|----|-----|
| Vai trò | ADMIN |
| Mục đích vận hành | Khắc phục **lệch read-model** `student_timetable_entry` sau lỗi projection hiếm gặp hoặc sau **sửa TKB hàng loạt** |
| Liên quan | F12 (SV đọc `/timetable/me/snapshot`), listener [`RegistrationTimetableProjectionListener`](../../../../backend-core/src/main/java/com/example/demo/event/RegistrationTimetableProjectionListener.java) |
| Dev | [`dev_spec.md`](dev_spec.md) |
| Cross | [`cross/03_db_dictionary.md`](../../cross/03_db_dictionary.md) |

---

## 1) Bối cảnh

Hệ thống duy trì **hai tầng**:

1. **Nguồn sự thật giao dịch**: `dang_ky_hoc_phan`, `lop_hoc_phan.thoi_khoa_bieu_json`.
2. **Projection** (read-model): bảng `student_timetable_entry`, cập nhật thường xuyên bởi event `AFTER_COMMIT`.

Khi **projection miss** (exception log `[TKB-Projection]`): đăng ký vẫn hợp lệ nhưng SV thấy snapshot trống/thiếu tiết. Admin cần một thao tác **an toàn, idempotent**: rebuild cho **một** SV trong **một** học kỳ.

---

## 2) Actor & quyền

| Actor | Quyền |
|-------|--------|
| Admin vận hành | JWT `ROLE_ADMIN`, biết `sinhVienId` + `hocKyId` (từ báo cáo lỗi / support ticket) |
| Sinh viên | **Không** được gọi API rebuild |

---

## 3) Tiền điều kiện

- Đã xác định SV + HK sai lệch (ví dụ: so sánh `/timetable/me` composite còn chi tiết nhưng `/me/snapshot` thiếu).
- Dữ liệu TKB JSON trên các lớp đã đăng ký là hợp lệ (hoặc admin chấp nhận projection 0 slot cho lớp thiếu lịch).

---

## 4) Hậu điều kiện

- Bảng `student_timetable_entry` cho cặp `(sinhVienId, hocKyId)` phản ánh lại toàn bộ đăng ký **THANH_CONG/CHO_DUYET** (tuỳ subset mà service `findRegisteredCoursesInSemester` trả về — xem code repository).
- Response JSON trả về **`rebuiltSlots`**: tổng số dòng slot đã insert (0 nếu không có gì để chiếu).

---

## 5) Luồng chính (runbook)

1. Admin xác nhận log server có `upsert thất bại` hoặc user báo lịch lệch.
2. Lấy ID sinh viên & học kỳ từ portal admin hoặc DB.
3. Gọi **`POST /api/v1/admin/timetable-projection/rebuild?sinhVienId=&hocKyId=`** (xem dev_spec).
4. Đọc `rebuiltSlots` — nếu 0 nhưng mong đợi >0 → kiểm tra TKB JSON của lớp (F03).
5. (Tuỳ chọn) Nhờ SV refresh trang TKB snapshot.

---

## 6) Luồng phụ / rủi ro

| Case | Hành động |
|------|-----------|
| Rebuild trong lúc SV vừa hủy môn | Event cancel sẽ remove — có thể flicker nếu đo thời điểm; chấp nhận eventual consistency |
| Nhiều instance Java | Rebuild ghi DB trực tiếp — không cần sticky session |
| Sai `sinhVienId` | Chỉ rebuild SV khác — audit nên log request admin |

---

## 7) Acceptance

- [ ] Endpoint chỉ ADMIN (`403` cho STUDENT).
- [ ] Gọi lặp lại không nhân đôi slot (constraint UNIQUE `(id_dang_ky, slot_index)` + delete trước insert trong engine).
- [ ] Không được yêu cầu Sinh Viên biết endpoint nội bộ.

---

## 8) Wireframe ASCII (Admin toolbar)

```
┌── Projection TKB ──────────────────────────────────┐
│ SV ID [_____]  HK ID [_____]   [Rebuild]           │
│ Last result: rebuiltSlots = 42                      │
└────────────────────────────────────────────────────┘
```

---

## 9) Phụ thuộc

- Migration: [`migration_student_timetable_entry_sprint5.sql`](../../../../backend-core/src/main/resources/db/migration_student_timetable_entry_sprint5.sql).

---

## 10) Ma trận “trước / sau” rebuild (FAQ hội đồng)

| Triệu chứng | Kiểm tra DB | Sau rebuild mong đợi |
|-------------|-------------|------------------------|
| SV thấy môn ở composite `/me` nhưng snapshot **0 tiết** | `student_timetable_entry where id_sv & hk`; log `[TKB-Projection]` | `COUNT(*)>0` khớp tổng slot JSON các dkhp |
| GV vừa update TKB JSON | Snapshot cũ | Rebuild tái chiếu từ JSON mới |
| Ingress đăng ký + listener fail | dkhp SUCCESS vẫn có | Rows snapshot xuất hiện post-rebuild |

---

## 11) Liên API đọc (không trong F06 nhưng cùng data plane)

| Endpoint | Ghi |
|----------|-----|
| `GET /api/v1/timetable/me/snapshot` | SV consumer của projection |
| `POST /api/v1/admin/timetable-projection/rebuild` | *This* feature ingress |

---

## 12) Lịch sử

- 2026-05 Khởi tạo.
- 2026-05 Mô tả runbook và rủi ro chi tiết.
- 2026-05 FAQ matrix + endpoint cross-links (độ đồng đều báo cáo)
