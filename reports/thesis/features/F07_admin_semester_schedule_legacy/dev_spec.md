# Dev-Spec — F07 PUT lịch đăng ký trên `hoc_ky` (fallback)

| Mã | F07 |
|----|-----|
| Module | `backend-core` |
| BA | [`ba_flow.md`](ba_flow.md) |
| Controller | [`AdminHocKyScheduleController`](../../../../backend-core/src/main/java/com/example/demo/controller/AdminHocKyScheduleController.java) |
| Path base | **`/api/v1/admin/hoc-ky`** (tách khỏi `/api/hoc-ky` để tránh clash route) |

---

## 1) Tóm tắt

Endpoint **PUT** cập nhật bốn cột Instant trên entity [`HocKy`](../../../../backend-core/src/main/java/com/example/demo/domain/entity/HocKy.java).

Validation **`validateWindowPair`** trong [`HocKyServiceImpl`](../../../../backend-core/src/main/java/com/example/demo/service/impl/HocKyServiceImpl.java):

- Label **"Đăng ký trước"** → cặp `preDangKyMoTu/preDangKyMoDen`.
- Label **"Đăng ký chính thức"** → `dangKyChinhThucTu/dangKyChinhThucDen`.

`RegistrationScheduleChecker` đọc getter trên entity / context HK khi không có cửa sổ granular phù hợp — chi tiết thuật toán trong file Java + [`RegistrationScheduleCheckerTest`](../../../../backend-core/src/test/java/com/example/demo/support/RegistrationScheduleCheckerTest.java).

---

## 2) Request / response schema

### 2.1 Request body — [`HocKyLichDangKyRequest`](../../../../backend-core/src/main/java/com/example/demo/payload/request/HocKyLichDangKyRequest.java)

| Field JSON (Jackson mặc định camelCase) | Kiểu | NULL | Semantic |
|----------------------------------------|------|------|----------|
| `preDangKyMoTu` | `string` ISO-8601 → `Instant` | yes | PRE start |
| `preDangKyMoDen` | `Instant` | yes | PRE end |
| `dangKyChinhThucTu` | `Instant` | yes | OFFICIAL start |
| `dangKyChinhThucDen` | `Instant` | yes | OFFICIAL end |

**Rule**: mỗi cặp **cùng null** hoặc **cùng non-null** và `tu.isAfter(to)==false`.

### 2.2 Response — [`HocKyResponse`](../../../../backend-core/src/main/java/com/example/demo/payload/response/HocKyResponse.java)

Sau PUT trả **`200 OK`** và body đầy đủ học kỳ (bao gồm 4 Instant vừa lưu + các field như `tenHocKy` build sẵn + cờ **`preDangKyDangMo`**, **`dangKyChinhThucDangMo`** được tính lại qua checker trong `toResponse`).

---

## 3) API contract

```
PUT /api/v1/admin/hoc-ky/{id}/lich-dang-ky
Authorization: Bearer <JWT admin>
Content-Type: application/json
```

| HTTP | Body / lỗi |
|------|------------|
| 200 | `HocKyResponse` |
| 403 | Non-admin |
| 404 | HK `id` không tồn tại |
| 4xx/5xx | `IllegalArgumentException` khi cặp không hợp lệ (handler phải map rõ ràng cho client) |

**Auth**: `@PreAuthorize("hasRole('ADMIN')")` trên method controller.

---

## 4) DB mapping

Cột trên `hoc_ky` (xem C03):

- `pre_dang_ky_mo_tu`, `pre_dang_ky_mo_den`
- `dang_ky_chinh_thuc_tu`, `dang_ky_chinh_thuc_den`

Không có migration riêng F07 nếu các cột đã có từ seed trước — kiểm tra file SQL trong `backend-core/src/main/resources/db/`.

---

## 5) Sequence

```
Admin UI -> PUT /api/v1/admin/hoc-ky/1/lich-dang-ky + JSON body
Controller -> IHocKyService.updateLichDangKy
Service -> validateWindowPair x2 -> set fields on HocKy -> save
Service -> toResponse(checker flags)
--> 200 HocKyResponse
```

Đồng thời các request sau từ SV (registration) gọi `RegistrationScheduleChecker` path khác trong transaction/read-only service.

---

## 6) Ví dụ request

```json
{
  "preDangKyMoTu": "2026-01-10T00:00:00Z",
  "preDangKyMoDen": "2026-01-20T23:59:59Z",
  "dangKyChinhThucTu": null,
  "dangKyChinhThucDen": null
}
```

Xóá fallback PRE: gửi cả hai PRE `null`.

---

## 7) Frontend gợi ý

- Trang có thể tên như `AdminHocKyScheduleConfigPage.jsx` (theo các plan UI trong repo).
- Convert `datetime-local` → ISO UTC trước khi PUT để khớp `Instant`.

---

## 8) Test

| Test | File / ý |
|------|----------|
| Đơn vị checker | [`RegistrationScheduleCheckerTest`](../../../../backend-core/src/test/java/com/example/demo/support/RegistrationScheduleCheckerTest.java) |
| Service | Mock `HocKyRepository` + verify `validateWindowPair` rejects half-pair |

---

## 9) Implement checklist

1. Không nhân đôi path `/api/hoc-ky` của public — chỉ `/api/v1/admin/hoc-ky`.
2. Mọi thay đổi schedule phải đi qua service để không duplicate validation.
3. Document cho BA: fallback **không** thay được F02 nếu admin đã tạo window chi tiết và sinh viên khớp scope.

---

## 10) Lịch sử

- 2026-05 Draft.
- 2026-05 Mở rộng schema, sequence, ví dụ.
