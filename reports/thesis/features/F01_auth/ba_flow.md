# BA-Flow — F01 Đăng nhập, MFA (Admin), JWT và phân luồng portal

| Trường | Giá trị |
|--------|---------|
| Mã chức năng | F01 |
| Vai trò chủ đạo | ALL (`ROLE_STUDENT`, `ROLE_LECTURER`, `ROLE_ADMIN`) |
| Loại | Authentication / Tenant identity |
| Dev-Spec | [`dev_spec.md`](dev_spec.md) |
| Tiền đề hệ | Mọi F02–F16 protected API yêu cầu Bearer hợp lệ (**trừ** whitelist `WebSecurityConfig`) |

---

## 1) Mục đích

1. **Xác thực** người dùng bằng username/password được lưu hash (`bcrypt` trong [`User`](../../../../backend-core/src/main/java/com/example/demo/domain/entity/User.java)).
2. **Cấp JWT** chứa `sub` và granted authorities **`ROLE_<EnumRole>`**.
3. Với ADMIN bật **MFA email OTP**, bảo vệ escalation: chỉ sau khi challenge + OTP consumed mới được token có quyền admin.
4. Phân chia **SPA portal** `/student`, `/teacher`, `/admin` (**frontend guard**) dựa trên claims JWT.

Không có F01 thì RBAC không thể enforce đặc biệt stateless JWT filter.

---

## 2) Actors và phạm vi

| Người dùng | Đặc điểm BA |
|-------------|--------------|
| Student | JWT `ROLE_STUDENT`; map sang `sinh_vien` qua các service `/me`. |
| Lecturer (`gv01`) | `ROLE_LECTURER` — không dùng tên vai `TEACHER`. |
| Admin | Có MFA flags + email optional for OTP |

---

## 3) Tiền điều kiện

- Tài khoản seeded hoặc tạo.
- ADMIN MFA branch: **`mfaEnabled=true`** & email non-blank (**Task 22**).
- Frontend biết **`VITE_API_BASE_URL`**.

---

## 4) Hậu điều kiện thành công

Client lưu `token`, mọi call `Authorization: Bearer <JWT>` và refresh UX khi **`401 Unauthorized`**.

JWT hiện **không** có refresh-token rotation (**CLAUDE backlog** trong repo).

---

## 5) Main flow — MFA off

```
User -> POST /login {username,password}
AuthController verifies AuthenticationManager
JwtUtils.gen -> JwtResponse { roles[] }
SPA stores token & routes RequireAuth
```

---

## 6) Alternate — MFA branch (Admin only path)

Sequence:

1. Password OK nhưng admin MFA flagged → không trả final token.
2. Response [`MfaLoginChallengeResponse`](../../../../backend-core/src/main/java/com/example/demo/payload/response/MfaLoginChallengeResponse.java): `challengeId`, `maskedEmail`, `requiresMfa=true`.
3. Service `createChallenge`: hash OTP, TTL (**`eduport.mfa.otp-ttl-minutes`**).
4. Delivery demo: OTP log WARN (không SMTP thật) — báo trong luận văn hạn chế.
5. `POST /mfa/verify` body [`MfaVerifyRequest`](../../../../backend-core/src/main/java/com/example/demo/payload/request/MfaVerifyRequest.java) → JwtResponse OK.

OTP invalid/expired → user phải login lại (**security product choice** trong handler).

---

## 7) Logout UX (frontend chủ đạo)

- Xóa `localStorage`/session chứa token.
- Navigate `/login` — server **stateless**, không revocation list (giới hạn thesis honest section).

---

## 8) Exception matrix

| ID | Scenario | Expected |
|----|----------|-----------|
| E-AUTH-401 | Sai password | 401 không leak existence (Spring default) |
| E-MFA-exp | OTP TTL lỡ | business error trong verify |
| E-RBAC-portal | SV token vào `/admin` SPA | FE guard denies before API |

---

## 9) GIVEN-WHEN-THEN

| Rule | Clause |
|------|--------|
| F01-G1 | GIVEN MFA off WHEN login THEN immediate JWT roles |
| F01-G2 | GIVEN MFA on WHEN OTP wrong THEN không issue token |
| F01-G3 | GIVEN JwtFilter WHEN malformed header THEN 403/401 standardized |

---

## 10) Wireframe

Login: username/password + link forgot (future).

MFA overlay: otp 6 fields + resend backlog + countdown TTL client approximated.

---

## 11) Acceptance

- [ ] SV → token roles STUDENT portal only.
- [ ] Admin MFA on cannot admin API prior verify path.
- [ ] GV token hits course search (**LECTURER**).

---

## 12) Phụ thuộc backend files

[`AuthController`](../../../../backend-core/src/main/java/com/example/demo/controller/AuthController.java)

[`JwtUtils`](../../../../backend-core/src/main/java/com/example/demo/security/jwt/JwtUtils.java)

[`UserDetailsImpl.build`](../../../../backend-core/src/main/java/com/example/demo/security/services/UserDetailsImpl.java)

[`IMfaOtpService`](../../../../backend-core/src/main/java/com/example/demo/service/IMfaOtpService.java)

---

## 13) References cross

[`cross/06_rbac_security.md`](../../cross/06_rbac_security.md)

[`cross/04_api_catalog.md`](../../cross/04_api_catalog.md) § AUTH

---

## 14) Lịch sử

| Ngày | |
|------|--|
| 2026-05 | Theo Task 22 + Auth refactor |
| 2026-05 | Chuẩn hoá vai LECTURER + timeline MFA chi tiết |
