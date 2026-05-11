# C06 — RBAC & Security (đặc tả tối đa)

Nguồn sự thật vận hành: `backend-core/src/main/java/com/example/demo/security/WebSecurityConfig.java`, `AuthTokenFilter`, `UserDetailsImpl`, annotation `@PreAuthorize` trên controllers, `GlobalExceptionHandler`.

Mục tiêu: một **dev mới** vào dự án có thể trả lời: *user nào được gọi API nào, cơ chế JWT, lớp ownership, gap bảo mật còn tồn tại.*

---

## 1) Mô hình role & authority canonical

### 1.1 Domain enum

`Role.java` values: `STUDENT`, `ADMIN`, `LECTURER` (duy nhất 3).

### 1.2 Mapping sang Spring GrantedAuthority

Constructor `UserDetailsImpl.build(User)`:

```
authority = ROLE_ + user.getRole().name()
```

Bảng đối chiếu SpEL:

| `Role` DB | Authority string | `@PreAuthorize` string arg |
|-----------|------------------|----------------------------|
| STUDENT | `ROLE_STUDENT` | `'STUDENT'` |
| ADMIN | `ROLE_ADMIN` | `'ADMIN'` |
| LECTURER | `ROLE_LECTURER` | `'LECTURER'` |

Không tồn tại `ROLE_TEACHER`.

---

## 2) Filter chain HTTP (stateless)

### 2.1 Chuỗi khái niệm token

1. Client `POST /api/auth/login`.
2. Username/password success (và MFA cleared) → `JwtUtils.generateJwtToken(Authentication)`.
3. `AuthTokenFilter` trên mỗi incoming request (trừ matchers public) đọc Bearer, validate, set `SecurityContext`.

Session policy: `STATELESS` — server không lưu HTTP session cho auth.

CSRF: disable (typical pure API + JWT).

### 2.2 CORS

`WebSecurityConfig` currently whitelists strictly `http://localhost:5173` and `127.0.0.1:5173` with allowed methods HEAD/GET/POST/PUT/DELETE/PATCH/OPTIONS and `Authorization` header.

Production must replace with environment-specific list.

### 2.3 `authorizeHttpRequests` exact matrix (theo code)

| Pattern | Http method | Access |
|---------|-------------|--------|
| `/api/auth/**` | * | PERMIT |
| `/api/public/**` | * | PERMIT |
| `/api/khoa/**` | GET | PERMIT |
| `/api/nganh-dao-tao/**` | GET | PERMIT |
| `/api/hoc-phan/**` | GET | PERMIT |
| `/api/hoc-ky/**` | GET | PERMIT |
| `/api/giang-vien/**` | GET | PERMIT |
| `/api/lop-hoc-phan/**` | GET | PERMIT |
| `/**` other | * | AUTHENTICATED (then method security) |

Important nuance: **`/api/v1/**` is NOT whitelisted** globally — must authenticate.

---

## 3) Method security coverage (pattern)

| Pattern class | Examples | Mechanism |
|---------------|----------|-----------|
| Student self-service | `RegistrationController`, intents, cart, timetable/me | `hasRole('STUDENT')` |
| Admin tooling | `Admin*Controller` under `/api/v1/admin` | `hasRole('ADMIN')` |
| Mixed read | `CourseSearchController` uses `hasAnyRole('STUDENT','ADMIN','LECTURER')` | OK (`LECTURER` aligned) |

Use `grep` in repo: `@PreAuthorize("hasRole` để inventory live.

---

## 4) Ownership layer (defense in depth)

Even with role match, service must ensure:

| API | Check |
|-----|--------|
| DELETE registration | `idDangKy.sinhVien == current sinhVien` else 403 textual message |
| PUT/DELETE intent | `intent.sinhVien` match |
| Any “/me” resolver | uses `Authentication.getName()` |

This pattern prevents **horizontal privilege escalation** if guessed incremental ID.

---

## 5) MFA (Task 22) security notes

Flow:

1. Admin with flags → challenge not final JWT.
2. `POST /api/auth/mfa/verify` verifies challenge + consumes OTP.

Threat mitigations to document:

| Threat | Mitigation in design |
|--------|----------------------|
| Brute force OTP | Rate limit (future) + TTL + single-use hash |
| Replay challenge | Mark consumed row in `mfa_otp_challenge` |
| Email leak | masked email in login response |

Operational: demo uses log delivery not SMTP.

---

## 6) Go queue security gap (must be explicit)

Current `backend-queue` queue routes do **NOT** validate JWT (prototype). Attack surface if exposed:

| Vector | Impact |
|--------|--------|
| Spoof enrollment | Craft JSON with arbitrary `id_sinh_vien` |
| DoS | Spam Redis DECR / Kafka |

Mandatory production controls (document for thesis defense even if not coded):

1. mTLS between gateway and Go.
2. Internal network only.
3. Signed service token from Java after verifying student action.
4. Or move queue behind same API gateway issuing short-lived HMAC.

---

## 7) Data protection & logging

| Data | Storage | Log policy |
|------|---------|------------|
| Password | bcrypt hash | never plaintext |
| MFA secret | hashed challenge | never raw OTP |
| JWT | client | log only last 6 chars if needed |

Recommend redaction filter for `Authorization` header in access logs if Nginx captures.

---

## 8) Threat modeling quick table (STRIDE subset)

| Threat | Component | Mitigation status |
|--------|-----------|-------------------|
| Spoofing | Queue ingress | OPEN (no auth) |
| Tampering | JWT | signature ok |
| Repudiation | registration log | kafka idempotency partially |
| Info disclosure | error messages | Vietnamese but may leak internal |
| DoS | Ingress | need rate limit LB |
| Elevation | Stale `@PreAuthorize` strings | Regression via grep `TEACHER` trong Java |

---

## 9) Regression QA script (copy into test plan)

1. Anonymous GET `/api/hoc-ky/hien-hanh` → 200.
2. STUDENT token GET `/api/v1/admin/registration-windows` → 403 AccessDenied.
3. ADMIN token POST `/api/v1/admin/registration-windows/open-now` → success path.
4. LECTURER token GET `/api/v1/courses?page=0&size=20` → **200** (role string `LECTURER`).
5. STUDENT attempt DELETE other student's registration id → 403 textual.

---

## 10) Bibliography của code artifacts

Đường dẫn nhanh (relative repo root):

- `backend-core/src/main/java/com/example/demo/security/WebSecurityConfig.java`
- `backend-core/src/main/java/com/example/demo/security/jwt/AuthTokenFilter.java`
- `backend-core/src/main/java/com/example/demo/exception/GlobalExceptionHandler.java`

---

## 11) Pending security roadmap (honesty for thesis limitation section)

| ID | Topic | Severity |
|----|-------|-----------|
| SEC-001 | ~~Replace TEACHER with LECTURER~~ (**Done** course search 2026-05) | — |
| SEC-002 | AuthN on Go ingress | Critical external |
| SEC-003 | Refresh token/session rotation UX | Medium |
| SEC-004 | Fine-grained admin roles ( Registrar vs Superadmin ) | Low future |
