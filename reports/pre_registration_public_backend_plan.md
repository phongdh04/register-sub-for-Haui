# Backend - Ke hoach Public Pre-Registration cho tan sinh vien

Tai lieu nay de xuat ke hoach backend cho luong sinh vien moi vao truong, chua co tai khoan, truy cap link public (khong can login), dien form thong tin ca nhan, tao tai khoan va ghi du lieu vao database.  
Muc tieu: an toan, chong spam, chiu tai cao khi nhieu sinh vien submit cung luc.

**Pham vi:** `backend-core` (Java/Spring/JPA/PostgreSQL) + `backend-queue` (Go/Redis/Kafka).  
**Convention ID:** `BACK-PREREG-###`.

---

## 1) Muc tieu va nguyen tac thiet ke

- Public endpoint chi cho phep thao tac pre-registration, khong mo rong sang du lieu nhay cam khac.
- Khi cao diem, uu tien queue-first de tranh nghen DB.
- Moi submit phai idempotent, tranh tao trung account/ho so.
- Co kha nang theo doi trang thai xu ly theo `requestId`.
- Ho tro van hanh: metric, DLQ, replay job, audit log.

---

## 2) API de xuat

### 2.1 Public API (khong can login)

- `GET /api/public/v1/pre-reg/links/{token}`
  - Kiem tra link hop le, con han, con quota.
  - Tra metadata hien thi form (dot nhap hoc, required fields).

- `POST /api/public/v1/pre-reg/links/{token}/submit`
  - Nhan payload thong tin sinh vien.
  - Tra ngay `requestId`, `status=PENDING` neu accepted.

- `GET /api/public/v1/pre-reg/requests/{requestId}`
  - Tra trang thai: `PENDING | PROCESSING | SUCCESS | FAILED`.
  - Neu fail, tra `errorCode` va thong diep than thien.

### 2.2 Admin/Nghiep vu noi bo

- `POST /api/v1/admin/pre-reg/links`
  - Tao link theo dot, cau hinh `expiresAt`, `maxSubmissions`, whitelist domain (neu can).

- `GET /api/v1/admin/pre-reg/links`
  - Theo doi so luong submit, success rate, token status.

---

## 3) Thiet ke du lieu

### 3.1 Bang moi

1. `pre_registration_link`
- `id`, `token_hash` (unique), `status`, `expires_at`, `max_submissions`, `submitted_count`, `created_by`, `created_at`.

2. `pre_registration_request`
- `id` (UUID), `link_id`, `dedupe_key` (unique), `payload_json`, `source_ip_hash`, `user_agent_hash`, `status`, `error_code`, `created_at`, `processed_at`.

3. `pre_registration_audit_log` (khuyen nghi)
- `id`, `request_id`, `step`, `message`, `created_at`.

### 3.2 Rang buoc tranh trung

- Van dung unique hien co: `User.username`, `SinhVien.maSinhVien`.
- Bo sung check nghiep vu theo lo trinh:
  - unique `dedupe_key` cho request.
  - can nhac unique/coalesce cho email/sdt tuy chinh sach truong.

---

## 4) Luong xu ly backend (chiu tai cao)

## 4.1 Luong de xuat mac dinh (queue-first)

1. Public submit vao `backend-core` endpoint.
2. Validate nhanh (required fields, format co ban, token con han).
3. Tao ban ghi `pre_registration_request` trang thai `PENDING`.
4. Day message sang queue (Go -> Kafka).
5. Consumer ben `backend-core` lay message, xu ly transaction:
   - kiem tra idempotency/dedupe
   - tao `User`
   - tao `SinhVien`
   - tao/cap nhat `HoSoSinhVien`
   - cap nhat status `SUCCESS/FAILED`
6. Client poll theo `requestId`.

## 4.2 Co che anti-spike/anti-spam

- Rate limit theo IP + token (Redis).
- Cooldown ngan theo fingerprint submit.
- Lock ngan han tranh double-click/double-submit.
- Captcha (phase 2) khi phat hien abuse.
- DLQ cho message loi de replay.

---

## 5) Task backlog de xuat

### Phase P0 - Schema va API can ban (MVP)

#### BACK-PREREG-001 - Tao migration bang `pre_registration_link`
- DDL + index + unique `token_hash`.

#### BACK-PREREG-002 - Tao migration bang `pre_registration_request`
- DDL + unique `dedupe_key` + index `status, created_at`.

#### BACK-PREREG-003 - Entity/Repository cho link va request
- JPA entities + repositories + mapping.

#### BACK-PREREG-004 - Public controller `GET /links/{token}`
- Kiem tra token, han su dung, quota.

#### BACK-PREREG-005 - Public controller `POST /submit`
- Validate co ban + tao request `PENDING` + tra `requestId`.

#### BACK-PREREG-006 - API `GET /requests/{requestId}`
- Tra status tracking cho frontend.

#### BACK-PREREG-007 - Security hardening cho namespace `/api/public/v1/pre-reg/**`
- PermitAll dung pham vi, deny cac path ngoai policy.

### Phase P1 - Queue va xu ly dong thoi

#### BACK-PREREG-008 - Them message contract Kafka cho pre-reg
- Dinh nghia topic + schema payload.

#### BACK-PREREG-009 - Go service ingest + Redis rate limit/cooldown
- Reuse pattern queue hien co trong `backend-queue`.

#### BACK-PREREG-010 - Java consumer xu ly pre-reg
- Consume, transactional create user/sinh vien/ho so.

#### BACK-PREREG-011 - Idempotency service
- Tao/kiem tra `dedupe_key`, safe retry, at-least-once.

#### BACK-PREREG-012 - DLQ + retry policy
- Retry theo backoff; loi business vao FAILED, loi ha tang vao retry/DLQ.

### Phase P2 - Van hanh va bao mat nang cao

#### BACK-PREREG-013 - Admin API tao/quan ly pre-reg links
- Tao link, dong link, xem thong ke.

#### BACK-PREREG-014 - Observability
- Metric: submit throughput, queue lag, success/fail ratio, p95.

#### BACK-PREREG-015 - Audit va replay tool
- Replay request tu DLQ/FAILED co kiem soat.

#### BACK-PREREG-016 - Captcha/abuse protection
- Bat dieu kien theo nguong abuse.

---

## 6) Tieu chi chap nhan (Definition of Done)

- Co the tao tai khoan + ho so sinh vien tu link public ma khong can login.
- 1 request duoc xu ly idempotent (gui lai khong tao trung tai khoan).
- Chiu tai burst (muc tieu test: >= 1000 submit/5 phut, khong nghen DB).
- Co dashboard can ban theo doi queue lag + ty le loi.
- Co quy trinh xu ly FAILED/DLQ ro rang cho van hanh.

---

## 7) Rui ro va cach giam thieu

- **Rui ro spam bot:** them rate limit + captcha + blocklist IP.
- **Rui ro duplicate do retry:** enforce dedupe key + transaction boundary.
- **Rui ro lag queue:** scale consumer group, canh bao queue lag.
- **Rui ro du lieu sai:** validate 2 lop (API layer + business layer), luu error code de doi soat.

---

## 8) Thu tu trien khai de xuat (2 sprint)

- **Sprint 1:** BACK-PREREG-001 -> 007 (MVP endpoint + schema + tracking status).
- **Sprint 2:** BACK-PREREG-008 -> 016 (queue, anti-spike, observability, admin tools).

Ket qua sau Sprint 2: he thong san sang cho dot nhap hoc co tai cao, co kha nang van hanh thuc te.
