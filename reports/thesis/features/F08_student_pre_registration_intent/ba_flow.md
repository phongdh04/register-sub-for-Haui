# BA-Flow — F08 PRE intent (Sinh viên khai báo nguyện vọng học phần)

| Mã | F08 |
|----|-----|
| Vai trò chủ đạo | STUDENT |
| Dev | [`dev_spec.md`](dev_spec.md) |
| Pha nghiệp vụ | **PRE** (`requirePreRegistrationOpenFor`) — không tạo `dang_ky_hoc_phan` chỉ trong use case PRE intent (không gắn section cố định) |
| Hạ nguồn | **F04** demand aggregation |

---

## 1) Mục đích

Trong cửa sở **PRE** (theo học kỳ + cohort + ngành sinh viên), người học được phép:

- **Khai báo** mong muốn học các **học phần** (không chỉ một lớp học phần cụ thể);
- **Sửa** thứ tự ưu tiên / ghi chú;
- **Gỡ** intent khi đổi ý — **không chiếm** slot Redis Ingress.

Dữ liệu trở thành input thống kê cho học vụ (**F04**).

---

## 2) Actor

| Actor | Hành vi |
|-------|---------|
| STUDENT có profile `lop`/`nganh`/`nam_nhap_hoc` | CRUD intents của mình trong PRE |
| Hệ | Reject CRUD khi PRE đóng |
| ADMIN | Đọc aggregate F04 (**không** qua các endpoint STUDENT của F08) |

---

## 3) Tiền điều kiện

- JWT STUDENT và `User` ⇄ [`SinhVien`](../../../../backend-core/src/main/java/com/example/demo/domain/entity/SinhVien.java) map.
- Cửa **PRE mở** cho SV được xác định bằng [`RegistrationScheduleChecker.requirePreRegistrationOpenFor`](../../../../backend-core/src/main/java/com/example/demo/support/RegistrationScheduleChecker.java) trong service (**throw** forbidden nếu đóng).
- **`id_hoc_ky`** + **`id_hoc_phan`** hợp lệ tồn tại trong DB seed.

---

## 4) Hậu điều kiện

| Thao tác | Kết quả |
|-----------|---------|
| POST | một dòng **`pre_registration_intent`** UNIQUE theo semantic service (triple sv+hk+hp) |
| PUT | mutate fields không đổi triple identity key (theo PK `intentId`) |
| DELETE | xóa dòng theo chủ |

---

## 5) Luồng CRUD chi tiết (Main success)

### UC-F08-001 — Submit mới

1. SV vào wizard **PRE intents**.
2. Chọn học kỳ mục tiêu.
3. Chọn học phần và (tuỳ chọn) `priority>=1`, `ghiChu` ≤ 500 chars.
4. **`POST`** body JSON.
5. Nhận **`201`** + [`PreRegistrationIntentResponse`](../../../../backend-core/src/main/java/com/example/demo/payload/response/PreRegistrationIntentResponse.java).

### UC-F08-002 — Cập nhật / xóá

| Action | Verb | Endpoint |
|--------|------|----------|
| list | GET | `/me?hocKyId=` optional narrowing |
| update | PUT | `/{intentId}` |
| delete | DELETE | `/{intentId}` |

---

## 6) Alternate / Exceptions

| HTTP | Meaning |
|------|---------|
| 409 | Đã tồn tại `(sv,hk,hp)` — message gợi ý dùng **PUT** thay INSERT |
| 403 | Sửa/xó intent người khác; PRE closed; role sai |
| 404 | `intentId` không thuộc user |
| 422 body | Fail Bean Validation `@NotNull/@Min` fields |

*(Exact mapping của 403 PRE closed xem [`PreRegistrationIntentServiceImpl`](../../../../backend-core/src/main/java/com/example/demo/service/impl/PreRegistrationIntentServiceImpl.java) — có thể `ResponseStatusException`.)*

---

## 7) Business rules GWT

| ID | Rule |
|----|------|
| F08-R1 | Unique triple **per student HK HP** không duplicate insert |
| F08-R2 | `priority` defaults **1** if null/zero negative handled by validators |
| F08-R3 | không intent CRUD sau khi cửa PRE đã đóng (server-enforced)

---

## 8) Liên database

Migration: [`migration_pre_registration_intent_sprint2.sql`](../../../../backend-core/src/main/resources/db/migration_pre_registration_intent_sprint2.sql) + dictionary **C03**.

---

## 9) Acceptance

- [ ] CRUD STUDENT chỉ chủ được.
- [ ] Duplicate triple → **`409`**.
- [ ] Không được gọi thành công khi PRE đóng.
- [ ] F04 aggregates intent sau khi seed.

---

## 10) Wireframe gợi ý

```
┌── Nguyện vọng PRE — HK… ────────────────────────┐
│ [+] Thêm học phần  [Import gợi ý CTĐT (future)]  │
│ ─────────────────────────────────────────────── │
│ # | Mã HP | Tên HP      | Ưu tiên │ Ghi chú │ … │
└──────────────────────────────────────────────────┘
```

---

## 11) Lịch sử

| Ngày | |
|------|--|
| 2026-05 | Khởi tạo |
| 2026-05 | UC CRUD chi tiết, matrix lỗi, link migration |
