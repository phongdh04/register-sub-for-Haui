# Backend - Ke hoach Public Pre-Registration cho tan sinh vien

Tai lieu nay de xuat ke hoach backend cho luong sinh vien moi vao truong, chua co tai khoan, truy cap link public (khong can login), dien form thong tin ca nhan, tao tai khoan va ghi du lieu vao database.  
Muc tieu: an toan, chong spam, chiu tai cao khi nhieu sinh vien submit cung luc.

**Pham vi:** `backend-core` (Java/Spring/JPA/PostgreSQL) + `backend-queue` (Go/Redis/Kafka).  
**Convention ID:** `BACK-PREREG-###`.

---

## 1) Muc tieu va nguyen tac thiet ke

- Public endpoint chi cho phep thao tac pre-registration, khong mo rong sang du lieu nhay cam khac.
- Bat buoc queue-first (Go + Redis + Kafka) ngay tu dau de tranh nghen DB.
- Moi submit phai idempotent, tranh tao trung account/ho so.
- Co kha nang theo doi trang thai xu ly theo `requestId`.
- Ho tro van hanh: metric, DLQ, replay job, audit log.

---

## 2) API de xuat

### 2.1 Public API (khong can login)

- `GET /api/public/v1/pre-reg/links/{token}`
  - Kiem tra link hop le, con han, con quota.
  - Tra metadata hien thi form (dot nhap hoc, required fields).
  - Response goi y:
    - `linkStatus`: `ACTIVE | EXPIRED | CLOSED | QUOTA_EXCEEDED`
    - `intakeCode`, `requiredFields[]`, `captchaRequired`.

- `POST /api/public/v1/pre-reg/links/{token}/submit`
  - Nhan payload thong tin sinh vien.
  - Tra ngay `requestId`, `status=PENDING` neu accepted.
  - Header khuyen nghi:
    - `X-Request-Id` (trace client -> server)
    - `Idempotency-Key` (optional, fallback bang `dedupeKey` server-side)
  - Payload goi y:
    - `hoTen`, `ngaySinh`, `gioiTinh`
    - `email`, `soDienThoai`
    - `soCCCD` (neu truong yeu cau)
    - `diaChiThuongTru`, `tinhThanh`
    - `maNganhDangKy`, `nienKhoa`, `coSo`
    - `nguonTuyenSinh` (optional)
  - Response goi y:
    - `requestId`, `status`, `estimatedProcessingSeconds`.

- `GET /api/public/v1/pre-reg/requests/{requestId}`
  - Tra trang thai: `PENDING | PROCESSING | SUCCESS | FAILED`.
  - Neu fail, tra `errorCode` va thong diep than thien.
  - Neu `SUCCESS`, tra them:
    - `studentCode`
    - `accountProvisioned` (true/false)
    - `nextStep` (vi du: doi email kich hoat / doi phong dao tao xac nhan).

### 2.2 Admin/Nghiep vu noi bo

- `POST /api/v1/admin/pre-reg/links`
  - Tao link theo dot, cau hinh `expiresAt`, `maxSubmissions`, whitelist domain (neu can).

- `GET /api/v1/admin/pre-reg/links`
  - Theo doi so luong submit, success rate, token status.

---

## 3) Thiet ke du lieu

### 3.1 Bang moi

1. `pre_registration_link`
- `id`, `token_hash` (unique), `status`, `expires_at`, `max_submissions`, `submitted_count` (snapshot eventual), `created_by`, `created_at`.
- Bo sung nen co:
  - `intake_code`, `campus_code`, `allow_domains` (json/text), `rate_limit_profile`.
- Luu y quota counter de tranh hot spot row-lock:
  - Enforce quota realtime bang Redis `INCR` atomic voi key `prereg:quota:{linkId}`, TTL = `expires_at` + buffer.
  - `submitted_count` trong DB chi dung cho snapshot dinh ky/bao cao, KHONG dung enforce quota trong hot path.
  - Khi Redis unavailable, fallback ve `COUNT(*)` tren `pre_registration_request` kem cache ngan (5-10s); chap nhan slight over-quota thay vi block toan bo submit.

2. `pre_registration_request`
- `id` (UUID), `link_id`, `dedupe_key` (unique), `payload_json`, `source_ip_hash`, `source_ip_prefix`, `user_agent_hash`, `status`, `error_code`, `created_at`, `processed_at`.
- Bo sung nen co:
  - `trace_id`, `retry_count`, `last_error_detail`, `kafka_partition`, `kafka_offset`.
- Ghi chu:
  - `source_ip_prefix` (VARCHAR(18)) luu /24 subnet (IPv4) hoac /48 prefix (IPv6) de block theo range khi abuse ma khong expose IP day du.
  - `source_ip_hash` giu nguyen de phuc vu fingerprint/idempotency support.

3. `pre_registration_audit_log` (khuyen nghi)
- `id`, `request_id`, `step`, `message`, `created_at`.
- `step` goi y:
  - `RECEIVED`, `ENQUEUED`, `CONSUMING`, `VALIDATED`, `ACCOUNT_CREATED`, `FAILED`, `RETRIED`.

### 3.2 Rang buoc tranh trung

- Van dung unique hien co: `User.username`, `SinhVien.maSinhVien`.
- Bo sung check nghiep vu theo lo trinh:
  - unique `dedupe_key` cho request.
  - can nhac unique/coalesce cho email/sdt tuy chinh sach truong.
- Cong thuc `dedupe_key` de xuat:
  - `SHA256(normalized(soCCCD|email|soDienThoai) + intakeCode)`.
- Giai thich:
  - Muc tieu dedupe la mot nguoi - mot dot tuyen sinh, khong phu thuoc thoi diem submit.
  - Neu cung nguoi submit lai (retry hoac double-click) trong bat ky thoi diem nao cua cung dot, key van trung va idempotency check bat duoc.
- Nguyen tac normalize:
  - email lower-case + trim
  - so dien thoai ve dinh dang E.164 hoac chi giu so
  - bo khoang trang/ky tu dac biet trong CCCD.

### 3.3 Transaction boundary va locking

- Thu tu xu ly bat buoc:
  1. Idempotency check (`dedupe_key` lookup) - thuc hien dau tien, truoc moi business logic; neu da ton tai thi tra ket qua cu, khong xu ly tiep.
  2. DB transaction: tao `User` -> `SinhVien` -> `HoSoSinhVien` -> update status request.
  3. Commit Kafka offset - CHI sau khi DB transaction commit thanh cong.
- Dung `SELECT ... FOR UPDATE` (hoac optimistic lock) khi cap nhat `pre_registration_request`.
- Neu tao `User` thanh cong nhung tao `SinhVien` loi thi rollback toan bo.
- Moi loi business phai map thanh `error_code` on dinh de frontend hien thi thong diep de hieu.
- Ghi chu at-least-once:
  - Neu consumer crash sau DB commit nhung truoc offset commit, message se duoc deliver lai; idempotency check buoc 1 se bat duoc va tra ket qua cu an toan.

### 3.4 Chinh sach bao ve du lieu ca nhan (PII)

**Ma hoa payload:**
- Cot `payload_json` tren `pre_registration_request` chua PII nhay cam (CCCD, SDT, dia chi) -> bat buoc ma hoa application-level truoc khi insert (AES-256-GCM, key quan ly qua Vault hoac KMS).
- Hoac toi thieu: PostgreSQL column-level encryption (pgcrypto) neu chua co Vault - ghi ro lua chon trong sprint P0.

**Retention & anonymization:**
- Sau khi request dat `SUCCESS` va tai khoan duoc tao: schedule job anonymize `payload_json` (xoa CCCD, SDT, dia chi; giu metadata tracing) sau N ngay (N theo chinh sach truong, goi y 90 ngay).
- `pre_registration_audit_log`: giu `step/message`, khong luu PII raw.

**Phan quyen DB:**
- Role DB rieng `prereg_writer` (INSERT/UPDATE gioi han bang pre-reg).
- Role `prereg_reader` chi SELECT - khong cap cho app consumer thong thuong.
- Admin DB access vao `payload_json` phai qua audit trail rieng.

---

## 4) Luong xu ly backend (chiu tai cao)

## 4.1 Luong de xuat mac dinh (queue-first, bat buoc)

1. Public submit vao `backend-core` endpoint.
2. Validate nhanh (required fields, format co ban, token con han) + tao `requestId`.
3. `backend-core` goi `backend-queue` (Go ingress) de dua message vao queue.
   - Fallback khi Go ingress unavailable:
     - `backend-core` dung circuit breaker (Resilience4j hoac tuong duong) khi goi Go ingress.
     - Neu circuit OPEN: tra `PREREG_SYSTEM_BUSY` + `Retry-After` header (goi y 30-60s), KHONG fallback publish Kafka truc tiep tu Java de tranh bypass rate limit Go.
     - Go ingress deploy toi thieu 2 instances voi health check; neu ca cluster down -> accepted degraded mode voi thong bao ro rang.
4. Go service ap dung rate limit/cooldown/lock Redis, sau do publish Kafka.
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
- Giai thuat khuyen nghi:
  - Token bucket cho IP (vi du 20 req/phut/IP).
  - Sliding window theo link token (vi du 300 req/phut/token).
  - Fingerprint key: hash(IP + userAgent + thiet bi) TTL 10-20 giay.

## 4.3 Contract message Kafka (de xuat)

- Topic:
  - `pre_registration_submitted`
- Key:
  - `requestId` (giu thu tu theo request, de debug/replay de hon).
- Value fields toi thieu:
  - `requestId`, `linkId`, `dedupeKey`, `submittedAt`
  - `payload` (hoac `payloadRefId` neu toi uu kich thuoc)
  - `traceId`, `sourceIpHash`, `userAgentHash`.
- Schema versioning policy:
  - Them field `schema_version` (integer, bat dau = 1) vao moi message.
  - Rule tien hoa schema: CHI them field optional (co default/null); KHONG xoa, KHONG doi ten, KHONG thay doi kieu du lieu field da co.
  - Consumer phai ignore unknown fields (Jackson: `FAIL_ON_UNKNOWN_PROPERTIES = false`).
  - Neu breaking change bat buoc: tao topic moi `pre_registration_submitted_v2`, chay dual-publish trong 1 sprint roi deprecate v1.
  - Can nhac Confluent Schema Registry neu ha tang da co; neu chua: document schema trong repo `contracts/` va enforce qua unit test serialization/deserialization.
- DLQ topic:
  - `pre_registration_submitted_dlq`.
- Retry strategy:
  - 3 lan retry voi exponential backoff (10s, 30s, 90s), qua nguong vao DLQ.

---

## 4.4 Sizing va SLO ban dau (de van hanh thuc te)

- Muc tieu burst: >= 5,000 submit/10 phut trong dot cao diem.
- API nhan submit: p95 < 300ms (chi nhan va enqueue, khong insert truc tiep).
- Kafka consumer lag canh bao khi > 60s; critical khi > 180s.
- Ty le SUCCESS end-to-end >= 99% (tru truong hop payload sai nghiep vu).
- Thoi gian xu ly den `SUCCESS`: p95 < 2 phut, p99 < 5 phut.
- Baseline scale goi y:
  - Go ingress: 2-3 instances, CPU autoscale theo RPS.
  - Kafka partitions ban dau: 6-12 tuy throughput.
  - Java consumer group: 3-6 instances, scale theo queue lag.

## 4.5 Error code va mapping UX (goi y)

- `PREREG_LINK_EXPIRED`
- `PREREG_LINK_QUOTA_EXCEEDED`
- `PREREG_DUPLICATE_REQUEST`
- `PREREG_INVALID_PAYLOAD`
- `PREREG_ACCOUNT_CONFLICT`
- `PREREG_SYSTEM_BUSY`
- `PREREG_INTERNAL_ERROR`

Frontend chi hien thi message than thien, khong lo chi tiet he thong.

---

## 5) Task backlog de xuat

### Phase P0 - Foundation bat buoc cho queue-first

#### BACK-PREREG-001 - Tao migration bang `pre_registration_link`
- DDL + index + unique `token_hash`.

#### BACK-PREREG-002 - Tao migration bang `pre_registration_request`
- DDL + unique `dedupe_key` + index `status, created_at`.

#### BACK-PREREG-003 - Entity/Repository cho link va request
- JPA entities + repositories + mapping.

#### BACK-PREREG-004 - Public controller `GET /links/{token}`
- Kiem tra token, han su dung, quota.

#### BACK-PREREG-005 - Public controller `POST /submit`
- Validate co ban + tao request `PENDING` + goi Go ingress enqueue + tra `requestId`.
- Tieu chi:
  - Khong thuc hien insert `User/SinhVien` truc tiep trong API thread.
  - Luon tra `requestId` neu accepted.

#### BACK-PREREG-006 - API `GET /requests/{requestId}`
- Tra status tracking cho frontend.

#### BACK-PREREG-007 - Security hardening cho namespace `/api/public/v1/pre-reg/**`
- PermitAll dung pham vi, deny cac path ngoai policy.

#### BACK-PREREG-007A - PII encryption + retention job
- Implement ma hoa `payload_json` + anonymization job theo chinh sach PII.

### Phase P1 - Queue va xu ly dong thoi (bat buoc)

#### BACK-PREREG-008 - Them message contract Kafka cho pre-reg
- Dinh nghia topic + schema payload.

#### BACK-PREREG-009 - Go API endpoint cho pre-reg submit
- Them route rieng cho pre-reg (validate contract, logging, tracing, response code chuan).

#### BACK-PREREG-009B - Go service ingest + Redis rate limit/cooldown
- Reuse pattern queue hien co trong `backend-queue`.
- Phu thuoc: BACK-PREREG-009.
- Tieu chi:
  - Co config profile `normal/high-peak`.
  - Co metric reject boi rate limit/cooldown.

#### BACK-PREREG-010 - Java consumer xu ly pre-reg
- Consume, transactional create user/sinh vien/ho so.
- Tieu chi:
  - Retry an toan, khong tao trung du lieu.
  - Update status request day du `PROCESSING -> SUCCESS/FAILED`.

#### BACK-PREREG-011 - Idempotency service
- Tao/kiem tra `dedupe_key`, safe retry, at-least-once.

#### BACK-PREREG-012 - DLQ + retry policy
- Retry theo backoff; loi business vao FAILED, loi ha tang vao retry/DLQ.
- Tieu chi:
  - Co script/job replay tu DLQ theo `requestId`.
  - Replay khong vi pham idempotency.

### Phase P2 - Van hanh va bao mat nang cao

#### BACK-PREREG-013 - Admin API tao/quan ly pre-reg links
- Tao link, dong link, xem thong ke.

#### BACK-PREREG-014 - Observability
- Metric: submit throughput, queue lag, success/fail ratio, p95.
- Bo sung:
  - dashboard theo `intakeCode`, `linkId`.
  - canh bao khi fail rate > 3% trong 5 phut.

#### BACK-PREREG-015 - Audit va replay tool
- Replay request tu DLQ/FAILED co kiem soat.

#### BACK-PREREG-016 - Captcha/abuse protection
- Bat dieu kien theo nguong abuse.

#### BACK-PREREG-012A - Capacity test cho Go + Kafka + consumer
- Test burst va soak; xac dinh nguong scale pods/instances va kafka partitions.
- Chay song song sau khi Sprint 2 deploy on dinh, khong la deliverable blocking Sprint 2.

---

## 6) Tieu chi chap nhan (Definition of Done)

- Co the tao tai khoan + ho so sinh vien tu link public ma khong can login.
- 1 request duoc xu ly idempotent (gui lai khong tao trung tai khoan).
- Chiu tai burst (muc tieu test: >= 5000 submit/10 phut, khong nghen DB).
- Co dashboard can ban theo doi queue lag + ty le loi.
- Co quy trinh xu ly FAILED/DLQ ro rang cho van hanh.
- Co tai lieu runbook su co (queue lag, Kafka outage, DB pressure).

---

## 7) Rui ro va cach giam thieu

- **Rui ro spam bot:** them rate limit + captcha + blocklist IP.
- **Rui ro duplicate do retry:** enforce dedupe key + transaction boundary.
- **Rui ro lag queue:** scale consumer group, canh bao queue lag.
- **Rui ro du lieu sai:** validate 2 lop (API layer + business layer), luu error code de doi soat.
- **Go ingress toan bo instances down | Cao:** Circuit breaker + `PREREG_SYSTEM_BUSY` + alert PagerDuty/oncall; runbook restart Go cluster.

---

## 8) Thu tu trien khai de xuat (2 sprint)

- **Sprint 1:** BACK-PREREG-001 -> 010 (schema + public API + Go ingest + consumer basic).
- **Sprint 2:** BACK-PREREG-011 -> 016 va 009B (idempotency hardening, retry/DLQ, observability, admin tools).
- **Sau Sprint 2 (song song, khong blocking):** BACK-PREREG-012A (capacity test tren staging on dinh).

Ket qua sau Sprint 2: he thong san sang cho dot nhap hoc co tai cao, co kha nang van hanh thuc te.

---

## 9) Ke hoach test va cutover (chi tiet)

### 9.1 Test chuc nang

- Submit hop le -> `PENDING` -> `SUCCESS`.
- Submit payload thieu field bat buoc -> `FAILED` voi `PREREG_INVALID_PAYLOAD`.
- Submit trung (cung dedupe) -> tra ket qua cu, khong tao trung du lieu.
- Link het han/het quota -> reject dung `errorCode`.

### 9.2 Test tai

- Burst test: 5,000 submit/10 phut.
- Soak test: tai deu 1-2 gio de check memory leak/queue drift.
- Failure injection:
  - tam tat Kafka
  - tang do tre DB
  - restart consumer lien tuc.

### 9.3 Cutover production

1. Deploy schema va API read-only (`GET link`, `GET request`).
2. Deploy `POST submit` + Go ingest + Kafka topic.
3. Mo 10% link dot nhap hoc (pilot), theo doi metric 1-2 ngay.
4. Tang 50% -> 100% khi queue lag va fail rate on dinh.
5. Chot runbook on-call cho tuan cao diem.

**Rollback plan:**
- Neu phat hien van de trong pilot 10%:
  1. Disable tat ca link token (set `status = CLOSED`) - khong can undeploy code.
  2. Drain queue: cho consumer xu ly het message dang pending hoac stop consumer va de message trong Kafka (retention).
  3. KHONG can migrate nguoc DB - data `pre_registration_request` giu nguyen de audit; chi anonymize neu can theo PII policy.
  4. Fix issue -> re-enable link token theo quy trinh pilot lai.
- Quyen trigger rollback: on-call lead hoac tech lead.

---

Phien ban: 1.1 — cap nhat per review [05/05/2026].
