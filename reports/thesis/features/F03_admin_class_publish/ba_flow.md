# BA-Flow — F03 Quản lý xuất bản lớp học phần (Admin lifecycle Sprint 3)

| Trường | Giá trị |
|--------|---------|
| Mã chức năng | F03 |
| Vai trò chủ đạo | ADMIN |
| Dev-Spec | [`dev_spec.md`](dev_spec.md) |
| Lifecycle | `statusPublish`: **`SHELL` → `SCHEDULED` → `PUBLISHED`** |

---

## 1) Mục đích

Đưa từng **section `lop_hoc_phan`** từ draft nội bộ đến trạng thái được phép đứng trong **catalog** và **registration flow**:

- **`SHELL`**: được tạo nhưng chưa đủ dữ liệu vận hành.
- **`SCHEDULED`**: đã có lịch TKB composite JSON và/hoặc đã được gán giảng viên (auto-promotion theo helper service).
- **`PUBLISHED`**: công bố cho học sinh — search + đăng ký chỉ nhận lớp ở trạng thái đúng (tuỳ spec search filter `status_publish` / `chiConCho`).

Ngoài ra có shortcut **`force-publish-all`** phục vụ **demo học kỳ** có nhiều lớp thiếu GV — phải cảnh báo BA + luận văn (**không dùng production**).

---

## 2) Phân biệt `status_publish` với các trường vận hành khác

Thuật ngữ hai trục (xem **[`cross/01_glossary.md`](../../cross/01_glossary.md)**):

| Field | Semantic |
|-------|----------|
| `lop_hoc_phan.status_publish` (Sprint 3) | Chu kỳ công khai học phần |
| `lop_hoc_phan.trang_thai` khác *(DANG_MO, …)* | Vận hành chỗ/ngừng nhận khi đã public |

Đồ án báo luận văn cần nêu hai trục này khi GVHD tra “tại sao vẫn DANG_MO mà không publish được?”.

---

## 3) Actor

| Ai | Việc |
|-----|------|
| Quản đào tạo / Admin | Assign GV, Publish single/bulk, Force |
| STUDENT | Chịu outcome — không chỉnh `status_publish` |

---

## 4) Tiền điều kiện

- ADMIN JWT (`ROLE_ADMIN`).
- `id_lop_hp` tồn tại và thuộc học kỳ để bulk.

---

## 5) Luồng chi tiết (Main)

### 5.1 Gán GV

1. Admin chọn một lớp `SHELL` hoặc `SCHEDULED`.
2. Gọi `POST …/assign-giang-vien` body `{ idGiangVien }`.
3. Hệ có thể **auto-promote** `SHELL → SCHEDULED` khi TKB đã không rỗng (logic trong [`ClassPublishServiceImpl.assignGiangVien`](../../../../backend-core/src/main/java/com/example/demo/service/impl/ClassPublishServiceImpl.java)).

### 5.2 Publish một lớp (`POST …/{id}/publish`)

Điều kiện nghiêm: **`ensurePublishable`** — GV non-null và `hasSchedule`:

- PASS → `statusPublish = PUBLISHED` và response message phản chiếu.
- FAIL → `422`.

### 5.3 Bulk publish

`POST /bulk-publish?hocKyId=`

Returns [`LopHocPhanBulkPublishResponse`](../../../../backend-core/src/main/java/com/example/demo/payload/response/LopHocPhanBulkPublishResponse.java):

- **`publishedIds`**
- **`skipped[]`** có `reason` per class (giải thích cho UI báo họp)

### 5.4 Force publish all (**demo hammer**)

`POST /force-publish-all?hocKyId=`

- Bypass guard — set mass `PUBLISHED` + **`DANG_MO`** operational để học sinh thấy nhanh.
- Acceptance: chỉ được bấm trong môi trường thử nghiệm.

---

## 6) Alternate / Exceptions

| Mã | Tình huống | HTTP/Semantic |
|----|-----------|----------------|
| EX-03-01 | Gán GV khi lớp **đã `PUBLISHED`** | **409 Conflict** Sprint 3 |
| EX-03-02 | Publish thủ công nhưng thiếu GV hoặc TKB rỗng | **422** |
| ALT-Force | Bypass | OK bulk response counts |

---

## 7) GIVEN-WHEN-THEN samples

| ID | Statement |
|----|-----------|
| F03-G1 | GIVEN SHELL và đủ TKB WHEN assign GV MAY promote SCHEDULED |
| F03-G2 | GIVEN không force WHEN publish MUST satisfy ensurePublishable |
| F03-G3 | GIVEN `/force-publish-all` WHEN run THEN skip strict guard |

---

## 8) Wireframe

```
┌ Xuất bản lớp — HK #2 ──────────────────────────────┐
│ [Assign GV] [Publish] [Bulk HK] [(Warn)Force All] │
│ Table: MaLHP | GV | PublishStatus | hasSchedule …│
└────────────────────────────────────────────────────┘
```

---

## 9) Acceptance

- [ ] assign + publish không cho double-assign sau published.
- [ ] Bulk returns detailed skip reasons (`SkippedLopHocPhan`).
- [ ] Force path documented as non-prod baseline.

---

## 10) Phụ thuộc

[`LopHocPhanPublishStatus`](../../../../backend-core/src/main/java/com/example/demo/domain/enums/LopHocPhanPublishStatus.java) enum.

Migration: `migration_lop_hoc_phan_publish_sprint3.sql` (đường dẫn repo).

---

## 11) Lịch sử

| Ngày | |
|------|--|
| 2026-05 | Khởi tạo |
| 2026-05 | Nhấn hai trục status + force vs scholarly honesty |
