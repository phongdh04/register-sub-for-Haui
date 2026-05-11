# Dev-Spec — F10 Đăng ký học phần (REST đồng bộ + lưu ý queue)

| Trường | Giá trị |
|--------|---------|
| Mã chức năng | F10 |
| Liên kết BA-Flow | `features/F10_student_official_registration/ba_flow.md` |
| Module backend chính | `backend-core`: `RegistrationController`, `RegistrationServiceImpl` |
| Module async / chịu tải | `backend-queue` Ingress + `DangKyHocPhanServiceImpl` (Kafka) |
| Trạng thái | Draft |

---

## 1) Tóm tắt kỹ thuật

1. **REST path** (`RegistrationServiceImpl`): trong **một `@Transactional`**, resolve sinh viên + học kỳ, kiểm tra cửa thời gian (F02), validate (chain), **tăng `si_so_thuc_te` atomic**, insert `dang_ky_hoc_phan`, trả snapshot.
2. **Ưu tiên pha**: `ensureRegistrationOpen` trả `"OFFICIAL"` nếu cửa chính thức đang mở, ngược lại `"PRE"` nếu chỉ PRE — **khớp** logic chọn validation.
3. PRE: hai handler đầu chỉ `validateSelfOnly` (`DuplicateRegistrationHandler`, `ScheduleConflictHandler`) — **bỏ tiên quyết**. OFFICIAL: `validationChain.validate` đủ chuỗi.
4. **Queue path** chỉ mở cửa OFFICIAL (`DangKyHocPhanServiceImpl.processRegistration`) và có idempotency log — không trùng vai trò REST; tham chiếu **F15**.
5. Snapshot response tránh N+1 nhờ JOIN FETCH trong `findRegisteredCoursesInSemester`.

---

## 2) File chính trong repo

| Thành phần | Đường dẫn |
|-------------|-----------|
| Controller | `backend-core/.../RegistrationController.java` |
| Service REST | `backend-core/.../RegistrationServiceImpl.java` |
| Window gate | `backend-core/.../support/RegistrationScheduleChecker.java` |
| Repo atomic slot | `LopHocPhanRepository.incrementSiSoThucTe` |
| Validator chain | `AbstractValidationHandler`, handlers duplicate/schedule/prerequisite |

---

## 3) API contract chi tiết

Base: **`/api/v1/registrations`** — Bearer JWT **`ROLE_STUDENT`**.

### `GET /me`

| Query | Kiểu | Bắt buộc |
|-------|------|----------|
| `hocKyId` | Long | Không — mặc định học kỳ active hoặc mới nhất |

Response: **JSON array** các `RegisteredItem` — không bọc paging.

Schema `RegisteredItem` (Jackson):

| Field | Kiểu | Ghi chú |
|-------|------|---------|
| `idDangKy` | Long | PK `dang_ky_hoc_phan` |
| `idLopHp`, `maLopHp` | Long, string | Section |
| `idHocPhan`, `maHocPhan`, `tenHocPhan` | varied | Course |
| `soTinChi` | int | Nullable entity edge |
| `tenGiangVien` | string | Nullable nếu chưa gán GV |
| `siSoToiDa`, `siSoThucTe` | int | Slot |
| `hocPhi` | decimal | Theo lớp |
| `trangThaiDangKy` | string | VD `THANH_CONG` |
| `ngayDangKy` | ISO local datetime | `LocalDateTime` |

### `GET /me/window-status`

Cùng query `hocKyId` optional như resolver HK.

Điểm nổi bật `activePhase` (string trong code):

- `NONE` — không có PRE/OFFICIAL nào mở trong scope cohort/ngành của SV (thực tế code hiển thị `PRE`, `OFFICIAL`, `PRE_AND_OFFICIAL`).
- `PRE`
- `OFFICIAL`
- `PRE_AND_OFFICIAL` — trùng thời gian cả hai

`debugReason` chỉ có khi không mở gì — hướng dẫn seed cohort/ngành.

### `POST /`

| Query | Bắt buộc | Ghi chú |
|-------|----------|---------|
| `idLopHp` | **Có** | |
| `hocKyId` | Không | resolve như các GET khác |

**Thành công**: `201` + **`RegistrationStudentResponse`** (bao gồm `items` đầy đủ của kỳ, `tongSoMon`, `tongTinChi`, `tongHocPhi`).

**Lỗi** (điển hình):

| Status | Nguyên nhân |
|--------|--------------|
| 400 | Lớp không thuộc HK resolves |
| 403 | cửa thời gian đóng cho cohort/ngành |
| 404 | không tài khoản / không map SV / không HK |
| 409 | hết chỗ sau atomic increment (`CONFLICT`) |
| 422 (`UNPROCESSABLE_ENTITY`) | `RegistrationValidationException` (duplicate/clash/prerequisite…) |

### `DELETE /{idDangKy}`

**204 No Content** — chỉ chủ `dang_ky` và trạng thái `{THANH_CONG|CHO_DUYET}` được phép → set `RUT_MON` và `decrementSiSoThucTe`.

---

## 4) Chuỗi validation

```64:71:backend-core/src/main/java/com/example/demo/service/impl/RegistrationServiceImpl.java
    @PostConstruct
    void buildChain() {
        duplicateRegistrationHandler
                .setNext(scheduleConflictHandler)
                .setNext(prerequisiteCourseHandler);
        validationChain = duplicateRegistrationHandler;
    }
```

Nhánh PRE trong `register` không gọi `prerequisiteCourseHandler.validate`.

---

## 5) So với Kafka consumer

| Thuộc tính | `processRegistration` (Kafka) | `register` (REST) |
|------------|-------------------------------|-------------------|
| Idempotency log Sprint 4 | Có (`RegistrationIdempotencyService`) | Hiện **không** |
| Publish `RegistrationConfirmedEvent` | Có | REST **không** publish trong bản khôi phục hiện tại — backlog parity nếu cần TKB/event |
| Cửa thời gian | CHỉ `isOfficialRegistrationOpenFor` | PRE hoặc OFFICIAL theo checker + nhánh validator |

*(Nếu sau này thống nhất: trích abstraction service chung cho cả hai.)*

---

## 6) Events & projection

Đăng ký qua Kafka phát **`RegistrationConfirmedEvent`** — projection TKB bám event đó.

REST không phát trong code đã khôi phục — nếu thấy **`student_timetable_entry`** chỉ nhảy sau path Kafka là hiện tượng thiết kế; backlog: phát shared event trong REST để symmetry.

---

## 7) Test gợi ý

- Unit: nhánh `"PRE"` vs `"OFFICIAL"` mocking `RegistrationScheduleChecker`.
- Integration: hai transaction song song decrement slot cuối — một PASS một CONFLICT.

---

## 8) Ingress Go (thiếu trong class Java này)

| Method | URL | Payload |
|--------|-----|---------|
| POST | `http://localhost:3000/api/v1/queue/dang-ky` | `domain.DangKyRequest` snake_case |

HTTP status và `status` trong body: xem **`cross/04_api_catalog.md` §20.

---

## 9) Implement checklist (junior/agent)

1. Giữ contract query param không đổi để không gãy SPA.
2. Thêm logging trace_id nếu cần merge REST + Kafka dashboards.
3. Khi chỉnh `activePhase`, cập nhật đồng thời `RegistrationWindowStatusResponse` JavaDoc và tài liệu BA frontend.
