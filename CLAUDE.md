# EduPort Global Context (Quick Resume)

Muc tieu: file nho nhanh de lan sau tiep tuc lam viec ma khong can doc lai toan bo du an.

## 1) Tong quan he thong
- Monorepo gom:
  - `backend-core`: Spring Boot 4, Java 21, JPA, Spring Security JWT, PostgreSQL.
  - `backend-queue`: Go service cho gio vang (Redis + Kafka producer).
  - `frontend`: React + Vite, cac portal Student/Admin/Teacher.
  - `database`, `DOCS`: SQL/seed + tai lieu task.
- Nhanh chong chay local:
  - Backend Java: `cd backend-core && mvn -DskipTests compile` (hoac `mvn spring-boot:run`)
  - Frontend: `cd frontend && npm run dev` (build: `npm run build`)

## 2) Tinh trang task
- Theo `DOCS/Task_Prioritization.md`: task 1 -> 23 da danh dau Done.
- Task 22 (MFA) va Task 23 (Audit trail) da xong va da merge vao `main`.

## 3) Auth/RBAC/MFA hien tai (quan trong)
- Login endpoint: `POST /api/auth/login`
  - Username/password dung -> neu khong bat MFA: tra JWT ngay.
  - Neu ADMIN bat MFA + co email -> tra `requiresMfa=true`, `challengeId`.
- Verify OTP endpoint: `POST /api/auth/mfa/verify`
  - Input: `challengeId`, `otp` (6 so) -> tra JWT.
- MFA admin settings:
  - `GET /api/v1/admin/mfa/status`
  - `PUT /api/v1/admin/mfa/settings`
- Du lieu MFA:
  - `User`: co `email` (`email_otp`) + `mfaEnabled` (`mfa_bat`)
  - Bang `mfa_otp_challenge`: luu challenge OTP hash + TTL + consumed
- OTP delivery:
  - Hien tai mode demo ghi log WARN (chua bat SMTP that)
  - Config TTL: `eduport.mfa.otp-ttl-minutes` trong `application.properties`

## 4) Frontend refactor moi nhat
- Da co route dang nhap chinh: `/login`
- Co route guard role/JWT:
  - Student chi vao `/student` voi `ROLE_STUDENT`
  - Admin chi vao `/admin` voi `ROLE_ADMIN`
  - Teacher chi vao `/teacher` voi `ROLE_LECTURER`
- Utility dung chung:
  - `frontend/src/config/api.js`: `API_BASE_URL`, `authHeaders`, `clearSession`, `hasAnyRole`
  - `frontend/src/components/RequireAuth.jsx`
- Logout trong layouts da xoa session that (`localStorage`) va redirect `/login`.
- Da bo layout cu `AllLayout.jsx` va redirect URL login cu sang `/login`.
- Co file mau env:
  - `frontend/.env.example` (`VITE_API_BASE_URL=http://localhost:8080`)

## 5) Cac file backend moi cua Task 22
- `backend-core/src/main/java/com/example/demo/controller/AdminMfaController.java`
- `backend-core/src/main/java/com/example/demo/domain/entity/MfaOtpChallenge.java`
- `backend-core/src/main/java/com/example/demo/payload/request/AdminMfaUpdateRequest.java`
- `backend-core/src/main/java/com/example/demo/payload/request/MfaVerifyRequest.java`
- `backend-core/src/main/java/com/example/demo/payload/response/AdminMfaStatusResponse.java`
- `backend-core/src/main/java/com/example/demo/payload/response/MfaLoginChallengeResponse.java`
- `backend-core/src/main/java/com/example/demo/repository/MfaOtpChallengeRepository.java`
- `backend-core/src/main/java/com/example/demo/service/IMfaOtpService.java`
- `backend-core/src/main/java/com/example/demo/service/impl/MfaOtpDelivery.java`
- `backend-core/src/main/java/com/example/demo/service/impl/MfaOtpServiceImpl.java`
- `backend-core/src/main/java/com/example/demo/util/EmailMaskUtil.java`

## 6) Nhanh/commit quan trong gan day
- `40acdb4` feat(admin): MFA email OTP login (Task 22)
- `7d7a9a3` feat(admin): audit trail for grading and retake appeals (Task 23)
- `44d748d` feat(frontend): protected portals, /login, shared API config

## 7) Seed tai khoan demo
- Admin: `admin / 123456`
- Lecturer: `gv01 / 123456`
- Student: `sv01 / 123456`
- Admin duoc seed email mac dinh `admin@eduport.demo` (MFA default off).

## 8) Huong tiep theo de nang cap nhanh (neu can)
- Bat SMTP that (Spring Mail) thay cho log OTP demo.
- Them refresh token + session expiration UX cho frontend.
- Viet test tich hop cho luong MFA (login -> verify -> token).

## 9) Luu y lam viec voi AI lan sau
- Neu can tiep tuc nhanh, doc file nay truoc.
- Sau do chi can check nhanh:
  - `git status -sb`
  - `DOCS/Task_Prioritization.md`
  - `frontend/src/App.jsx`
  - `backend-core/src/main/java/com/example/demo/controller/AuthController.java`

