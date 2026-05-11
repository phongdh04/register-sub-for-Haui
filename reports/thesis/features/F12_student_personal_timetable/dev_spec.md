# Dev-Spec — F12 Thời khóa biểu cá nhân (Student)

| Mã | F12 |
|----|-----|
| Module | `backend-core` |
| BA | [`ba_flow.md`](ba_flow.md) |
| Trạng thái | Done (theo codebase hiện tại) |

---

## 1) Tóm tắt kỹ thuật

- **Dual-read pattern**: một endpoint đọc **aggregate** và parse JSON (`ITimetableService`); một endpoint đọc **read-model denormalized** (`IStudentTimetableProjection`).
- Projection cập nhật qua **domain events** (`RegistrationConfirmedEvent`, `RegistrationCancelledEvent`) và phase **`TransactionPhase.AFTER_COMMIT`** → đăng ký không bị roll back khi projection lỗi.
- `StudentTimetableProjectionImpl` dùng `REQUIRES_NEW` để transaction projection tách khỏi listener.

---

## 2) Phụ thuộc

| Loại | Thành phần |
|------|-------------|
| Controller | [`TimetableController`](../../../../backend-core/src/main/java/com/example/demo/controller/TimetableController.java) `@RequestMapping("/api/v1/timetable")` |
| Composite service | [`ITimetableService`](../../../../backend-core/src/main/java/com/example/demo/service/ITimetableService.java), [`TimetableServiceImpl`](../../../../backend-core/src/main/java/com/example/demo/service/impl/TimetableServiceImpl.java) |
| Projection | [`IStudentTimetableProjection`](../../../../backend-core/src/main/java/com/example/demo/service/IStudentTimetableProjection.java), [`StudentTimetableProjectionImpl`](../../../../backend-core/src/main/java/com/example/demo/service/impl/StudentTimetableProjectionImpl.java) |
| Listener | [`RegistrationTimetableProjectionListener`](../../../../backend-core/src/main/java/com/example/demo/event/RegistrationTimetableProjectionListener.java) |
| Repo | [`StudentTimetableEntryRepository`](../../../../backend-core/src/main/java/com/example/demo/repository/StudentTimetableEntryRepository.java), `DangKyHocPhanRepository`, `UserRepository`, `SinhVienRepository` |
| Admin rebuild | [`AdminTimetableProjectionController`](../../../../backend-core/src/main/java/com/example/demo/controller/AdminTimetableProjectionController.java) — F06 |

---

## 3) Domain model

### 3.1 Entity projection

[`StudentTimetableEntry`](../../../../backend-core/src/main/java/com/example/demo/domain/entity/StudentTimetableEntry.java)

| Thuộc tính (Java) | Cột DB | Kiểu | Ghi chú |
|-------------------|--------|------|---------|
| `id` | `id_entry` | BIGINT PK | |
| `idSinhVien` | `id_sinh_vien` | BIGINT | FK logic |
| `idHocKy` | `id_hoc_ky` | BIGINT | |
| `idDangKy` | `id_dang_ky` | BIGINT | |
| `idLopHp` | `id_lop_hp` | BIGINT | |
| `slotIndex` | `slot_index` | SMALLINT | 0-based index trong JSON; UNIQUE với idDangKy |
| `thu`,`tiet`,`phong` | snake | optional | Parse JSON keys `thu`, `tiet`, `phong` |
| `ngayBatDau`, `ngayKetThuc` | dates | LocalDate | JSON keys `ngay_bat_dau`, `ngay_ket_thuc` |

**Index**: `(id_sinh_vien, id_hoc_ky)`, `id_lop_hp`, `id_dang_ky`; UNIQUE `(id_dang_ky, slot_index)`.

### 3.2 Response DTO

| Class | Mục đích |
|-------|----------|
| [`TimetableResponse`](../../../../backend-core/src/main/java/com/example/demo/payload/response/TimetableResponse.java) | `/me` — `courses: List<TimetableCourseResponse>` |
| [`TimetableCourseResponse`](../../../../backend-core/src/main/java/com/example/demo/payload/response/TimetableCourseResponse.java) | Mỗi môn + `sessions` |
| [`TimetableSessionResponse`](../../../../backend-core/src/main/java/com/example/demo/payload/response/TimetableSessionResponse.java) | Parse từ map JSON |
| [`StudentTimetableSnapshotResponse`](../../../../backend-core/src/main/java/com/example/demo/payload/response/StudentTimetableSnapshotResponse.java) | `idSinhVien`, `idHocKy`, `totalSlots`, `entries` |
| [`StudentTimetableEntryResponse`](../../../../backend-core/src/main/java/com/example/demo/payload/response/StudentTimetableEntryResponse.java) | `from(StudentTimetableEntry)` |

---

## 4) API contract

### 4.1 `GET /api/v1/timetable/me`

| | |
|--|--|
| Auth | `Authorization: Bearer <JWT>` |
| Role | `@PreAuthorize("hasRole('STUDENT')")` |
| Query | `hocKyId` (optional, `Long`) |

**Hành vi**:
1. `username` → `User` → `SinhVien`.
2. `resolveHocKy(hocKyId)`: nếu null → `hocKyRepository.findByTrangThaiHienHanhTrue()` có thể null.
3. `findTimetableRegistrations(svId, hkId)` — nếu hk null vẫn có thể trả list theo implementation repo.
4. Build `TimetableResponse` với `mapSessions` — key JSON: `thu`, `tiet`, `phong`, `ngay_bat_dau`, `ngay_ket_thuc` (string hóa qua `toStringSafe`).

| HTTP | Điều kiện |
|------|-----------|
| 200 | Luôn trả body (có thể rỗng). |
| 404 | User không tồn tại; SV chưa map; HK id không tồn tại khi có `hocKyId`. |

### 4.2 `GET /api/v1/timetable/me/snapshot`

| | |
|--|--|
| Auth | Bearer JWT |
| Role | `STUDENT` |
| Query | `hocKyId` **required** (`Long`) |

**Pipeline**: `resolveSinhVien` → `projection.readForStudent(idSinhVien, hocKyId)` → map `StudentTimetableEntryResponse::from`.

| HTTP | Điều kiện |
|------|-----------|
| 200 | `{ idSinhVien, idHocKy, totalSlots, entries: [...] }` |
| 404 | User/SV không resolve được (cùng message với `/me`). |
| 400 | Thiếu `hocKyId` (Spring MVC). |

---

## 5) Sequence — snapshot sau đăng ký

```
SV->API: POST đăng ký (F10)
API->DB: COMMIT DangKyHocPhan
API-->>Listener: RegistrationConfirmedEvent (after commit)
Listener->Projection: upsertForRegistration(idDangKy) [REQUIRES_NEW]
Projection->DB: DELETE by id_dang_ky; INSERT N rows student_timetable_entry
SV->API: GET /timetable/me/snapshot?hocKyId=
API->DB: SELECT * FROM student_timetable_entry WHERE sv+hk
API-->>SV: 200 + entries
```

Nếu `upsertForRegistration` ném exception: **chỉ log** (`RegistrationTimetableProjectionListener`) — cần **rebuild** (F06).

---

## 6) JSON TKB trong `lop_hoc_phan` (implicit contract)

`StudentTimetableProjectionImpl` đọc `List<Map<String,Object>>` từ [`LopHocPhan.getThoiKhoaBieuJson()`](../../../../backend-core/src/main/java/com/example/demo/domain/entity/LopHocPhan.java).

Field được map:

| Key JSON | Kiểu gợi ý | Map sang |
|----------|------------|----------|
| `thu` | number | `Short` |
| `tiet` | string | `String` |
| `phong` | string | `String` |
| `ngay_bat_dau` | string/date | `LocalDate` parse |
| `ngay_ket_thuc` | string/date | `LocalDate` parse |

Thiếu JSON hoặc rỗng → `upsert` return 0; delete cũ vẫn chạy → snapshot clean.

---

## 7) Ví dụ `curl`/PowerShell

```powershell
# Sau khi có $hdr như cross/04 §23
Invoke-RestMethod -Headers $hdr -Uri "http://localhost:8080/api/v1/timetable/me"
Invoke-RestMethod -Headers $hdr -Uri "http://localhost:8080/api/v1/timetable/me/snapshot?hocKyId=1"
```

---

## 8) Test gợi ý

| Loại | Nội dung |
|------|----------|
| Unit | Parse slot index / date trong `StudentTimetableProjectionImpl` |
| Integration | `@WebMvcTest` hoặc MockMvc: 403 với `ROLE_ADMIN` trên `/me` |
| IT | Sau khi seed 1 đăng ký + JSON TKB, assert `readForStudent` count |
| Reference | [`StudentTimetableProjectionImplTest`](../../../../backend-core/src/test/java/com/example/demo/service/impl/StudentTimetableProjectionImplTest.java) |

---

## 9) Implement checklist (junior dev)

1. Không thêm business vào controller — chỉ delegate.
2. Mọi endpoint student-TKB bắt `@PreAuthorize("hasRole('STUDENT')")`.
3. Frontend: dùng **một** endpoint cho grid (snapshot), **một** cho summary card (`/me`).
4. Khi debug lệch dữ liệu: xem log `[TKB-Projection]`, rồi gọi admin rebuild (F06).

---

## 10) Lịch sử

- 2026-05 Draft ngắn.
- 2026-05 Mở rộng contract, sequence, schema, test.
