# C09 — Kịch bản Demo & Bảo vệ Luận văn (chi tiết tối đa)

Script này dùng khi demo trước hội đồng / quay video chứng minh. Thời lượng mục tiêu demo lõi đăng ký: **25–35 phút** (+ Q&A).

Chuẩn bị: môi trường `docker`/local Postgres + Kafka + Redis nếu cần chạy Ingress; Seed tài khoản `CLAUDE.md` (`admin/gv01/sv01`).

---

## 0) Roles & narration guide

Người thuyết trình = **Architect + BA + Operator**.

| Phân đoạn | Thời gian | Ai nói điều gì |
|-----------|-----------|----------------|
| Mở đầu | 2′ | Problem statement EduPort DKHP & multi-phase |
| Kiến trúc slide | 4′ | C02 diagrams two paths REST vs Ingress |
| Admin config | 6′ | F02+F03+F07 |
| SV journey | 8′ | F08+F09+F10+F12 |
| Observability | 3′ | F05 dashboards |
| Nợ realtime & security honesty | 2′ | WS design + Ingress auth backlog |
| Kết luận | rest | Contributions & Future work |

Backup offline slides if network fails (screenshots Postman responses).

---

## 1) Data sanity checklist (< 15 phút trước giờ G demo)

### 1.1 Database

SQL quick checks:

```sql
SELECT id_hoc_ky, trang_thai_hien_hanh FROM hoc_ky;
SELECT COUNT(*) registration_window FROM registration_window;
SELECT COUNT(*) FROM lop_hoc_phan WHERE id_hoc_ky = /* active */;
```

Đảm bảo có ít nhất **01 window OFFICIAL mở** cho cohort SV seed.

### 1.2 Ports

| Service | Port | Probe |
|---------|------|-------|
| Spring | 8080 | `/actuator/health` nếu enable |
| Go | 3000 | ping root error ok |
| Vite frontend | 5173 | SPA loads |

### 1.3 Accounts

Đăng nhập thử 3 vai trò nhanh; clear localStorage stale tokens.

---

## 2) Kịch bản demo Admin (ADMIN)

Steps phải nói to **tại sao** (business) song song UI.

### D1 — MFA off path

1. Browser incognito `/login`.
2. `admin / 123456`.
3. Nêu MFA optional (Task22) chỉ nhắc nếu GV hỏi.

### D2 — Cửa đăng ký granular (F02)

1. Màn Admin Registration Windows (`AdminRegistrationWindowsPage`).
2. `GET list?hocKyId=<active>` narration: cohort filter table columns.
3. **Tạo window PRE** và **OPEN NOW** cho pha **OFFICIAL** (đường tắt demo).

### D3 — Xuất bản lớp (F03)

1. Show một lớp SHELL hoặc SCHEDULED seeded.
2. Assign GV + publish single + optional bulk-publish commentary.

### D4 — Demand (F04)

1. Navigate demand dashboard aggregated counts với slider class size hypothetical.

### D5 — Monitoring (F05)

1. Explain registration_request_log basis.
2. Show outcomes + throughput snapshots (possibly empty instruct how to enqueue sample events via queue run).

---

## 3) Kịch bản Sinh viên (STUDENT)

### S1 — Khám phá môn (F09)

1. Login `sv01`.
2. Demonstrate BOTH:
   - Public list `/lop-hoc-phan/hoc-ky/{id}` via network tab narration.
   - Authenticated `/api/v1/courses` filters.

Nếu demo kịch bản GV: đăng nhập `gv01` và chứng minh `GET /api/v1/courses` trả **200** (đã khớp `ROLE_LECTURER`).

### S2 — PRE Intent (F08)

1. Only if PRE window open.
2. Submit intent và show duplicate rejection path gracefully.

### S3 — Đăng ký chính thức (F10)

1. Landing page shows **window-status** banner fields (cohort, phase).
2. Register one class REST path `POST /api/v1/registrations`.
3. Show response snapshot aggregates.

### S3b — Ingress high load teaser (Optional F15)

If infrastructure ready:

1. Use Postman Runner 20 parallel requests emphasizing Go returns fast while DB settles.
2. Narrate eventual consistency caveat.

### S4 — TKB (F12)

1. `/timetable/me` vs `/timetable/me/snapshot`.
2. If snapshot empty cite requirement TKB JSON on class.

### S5 — Hủy (F11)

1. DELETE registration verifying 204 và slot decrement verifying via admin SQL or monitoring.

---

## 4) GV (optional min 3′)

Demonstrate blocker 403 searching courses unless patch — đây là **honest limitation** strengthens scientific integrity.

Discuss planned fix referencing `cross/06`.

---

## 5) Storyline Q&A cheatsheet — câu hỏi dự báo của hội đồng

### Q: Tại sao Redis + Kafka mà không chỉ upscale Java?

Đáp trúng bottleneck IO connection thread model + amortized CPU cost Go.

### Q: Làm sao không double enroll?

Đáp: handler duplicate + unique idx + kafka idempotent service + atomic sql update.

### Q: WebSocket ở đâu trong code?

Honest pending + cross RFC C05 completeness.

---

## 6) Artifacts screenshot list (minimum set)

Capture PNG named:

```
demo-01-login-admin.png
demo-02-window-list.png
demo-03-publish-class.png
demo-04-sv-register.png
demo-05-dashboard-monitoring.png
demo-06-sequence-async.png (diagram export)
```

---

## 7) Rollback trong demo fail

Priority quick fixes:

| Failure | Bypass |
|---------|-------|
| Window closed wrongly | use `POST /open-now` |
| Published missing | Force publish sprint tool (document ethical note) |

---

## 8) Appendix — spoken script verbatim (opening 45s Vietnamese)

“(30s)” *Chào hội đồng, em trình EduPort nhằm số hoá hai pha PRE và đăng ký chính thức với hai đường REST và Ingress chịu tải…”*  
Tune voice natural.
