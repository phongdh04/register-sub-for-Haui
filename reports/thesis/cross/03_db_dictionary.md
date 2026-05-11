# C03 — DB Dictionary

Từ điển bảng/cột nghiệp vụ liên quan luồng đăng ký học phần. Lấy nguồn sự thật từ các migration trong `backend-core/src/main/resources/db/`.

Quy ước:
- `BIGSERIAL`: số nguyên 64-bit auto-increment (Postgres).
- `TIMESTAMPTZ`: timestamp có timezone (UTC).
- Mọi bảng có `created_at`, `updated_at` đều dùng `TIMESTAMPTZ DEFAULT NOW()`.
- Tên bảng/cột dùng `snake_case` Việt không dấu.

---

## 1) Sơ đồ quan hệ rút gọn (ERD)

```
khoa ─< nganh_dao_tao ─< lop ─< sinh_vien ─< dang_ky_hoc_phan >─ lop_hoc_phan >─ hoc_phan
                                              │
                                              ▼
                                    student_timetable_entry

hoc_ky ─< lop_hoc_phan
hoc_ky ─< registration_window
hoc_ky ─< pre_registration_intent

registration_request_log (idempotency log, chỉ tham chiếu lỏng theo id_*)
```

---

## 2) Bảng nghiệp vụ chính

### 2.1 `hoc_ky`
Học kỳ.
| Cột | Kiểu | NULL | Default | Mô tả |
|-----|------|------|---------|-------|
| id_hoc_ky | BIGSERIAL | NO | auto | PK |
| ky_thu | SMALLINT | NO | — | Số kỳ trong năm: 1, 2, 3 |
| nam_hoc | VARCHAR(20) | NO | — | "2024-2025" |
| trang_thai_hien_hanh | BOOLEAN | NO | false | Đánh dấu HK hiện hành (chỉ 1 row true) |
| pre_dang_ky_mo_tu | TIMESTAMPTZ | YES | — | Fallback: bắt đầu PRE |
| pre_dang_ky_mo_den | TIMESTAMPTZ | YES | — | Fallback: kết thúc PRE |
| dang_ky_chinh_thuc_tu | TIMESTAMPTZ | YES | — | Fallback: bắt đầu OFFICIAL |
| dang_ky_chinh_thuc_den | TIMESTAMPTZ | YES | — | Fallback: kết thúc OFFICIAL |

Index/Constraint:
- UNIQUE `(ky_thu, nam_hoc)`.

Quy tắc:
- Cặp `pre_dang_ky_mo_*` cùng null hoặc cùng có giá trị.
- Cặp `dang_ky_chinh_thuc_*` cùng null hoặc cùng có giá trị.

### 2.2 `khoa`
| Cột | Kiểu | NULL | Mô tả |
|-----|------|------|-------|
| id_khoa | BIGSERIAL | NO | PK |
| ma_khoa | VARCHAR(30) | NO | UNIQUE, ví dụ "CNTT" |
| ten_khoa | VARCHAR(150) | NO | "Công nghệ thông tin" |
| mo_ta | VARCHAR(500) | YES | |

### 2.3 `nganh_dao_tao`
| Cột | Kiểu | NULL | Mô tả |
|-----|------|------|-------|
| id_nganh | BIGSERIAL | NO | PK |
| ma_nganh | VARCHAR(30) | NO | UNIQUE |
| ten_nganh | VARCHAR(200) | NO | |
| id_khoa | BIGINT | NO | FK → khoa |
| he_dao_tao | VARCHAR(50) | YES | |

### 2.4 `lop`
Lớp hành chính (cohort).
| Cột | Kiểu | NULL | Mô tả |
|-----|------|------|-------|
| id_lop | BIGSERIAL | NO | PK |
| ma_lop | VARCHAR(20) | NO | UNIQUE |
| ten_lop | VARCHAR(100) | NO | |
| nam_nhap_hoc | INT | YES | Năm nhập học (4 chữ số), ví dụ 2023 |
| id_nganh | BIGINT | NO | FK → nganh_dao_tao |

Quy tắc:
- `nam_nhap_hoc` phải là YYYY (1900..2100). Validation phía service.

### 2.5 `sinh_vien`
| Cột | Kiểu | NULL | Mô tả |
|-----|------|------|-------|
| id_sinh_vien | BIGSERIAL | NO | PK |
| ma_sinh_vien | VARCHAR(30) | NO | UNIQUE |
| ho_ten | VARCHAR(150) | NO | |
| id_lop | BIGINT | NO | FK → lop |
| tai_khoan_id | BIGINT | YES | FK → tai_khoan |

### 2.6 `hoc_phan`
| Cột | Kiểu | NULL | Mô tả |
|-----|------|------|-------|
| id_hoc_phan | BIGSERIAL | NO | PK |
| ma_hoc_phan | VARCHAR(30) | NO | UNIQUE |
| ten_hoc_phan | VARCHAR(255) | NO | |
| so_tin_chi | INT | NO | >= 1 |

### 2.7 `lop_hoc_phan`
Lớp học phần (class section).
| Cột | Kiểu | NULL | Mô tả |
|-----|------|------|-------|
| id_lop_hp | BIGSERIAL | NO | PK |
| ma_lop_hp | VARCHAR(30) | NO | UNIQUE |
| id_hoc_phan | BIGINT | NO | FK → hoc_phan |
| id_hoc_ky | BIGINT | NO | FK → hoc_ky |
| id_giang_vien | BIGINT | YES | FK → giang_vien |
| si_so_toi_da | INT | NO | >= 1 |
| si_so_thuc_te | INT | NO | DEFAULT 0 |
| hoc_phi | NUMERIC(15,0) | YES | |
| trang_thai | VARCHAR(30) | NO | DEFAULT `DANG_MO` |
| status_publish | VARCHAR(16) | NO | DEFAULT `PUBLISHED`, CHECK in (`SHELL`,`SCHEDULED`,`PUBLISHED`) |
| version | BIGINT | NO | DEFAULT 0, optimistic lock |
| thoi_khoa_bieu_json | JSONB | YES | mảng slot {thu, tiet, phong, ngay_bd, ngay_kt} |
| id_tkb_block | BIGINT | YES | FK → tkb_block (nếu thuộc block bắt buộc cả block) |

Index:
- `idx_lhp_hk_status_publish (id_hoc_ky, status_publish)`.
- `idx_lhp_hk (id_hoc_ky)`.

Ràng buộc:
- `si_so_thuc_te <= si_so_toi_da` (kiểm tra ở service + DB CHECK ở migration sau).
- Cập nhật atomic bằng `UPDATE ... SET si_so_thuc_te = si_so_thuc_te + 1 WHERE si_so_thuc_te < si_so_toi_da AND id_lop_hp = ?`.

### 2.8 `registration_window`
Cửa sổ đăng ký theo cohort/ngành (Sprint 1).
| Cột | Kiểu | NULL | Mô tả |
|-----|------|------|-------|
| id_registration_window | BIGSERIAL | NO | PK |
| id_hoc_ky | BIGINT | NO | FK → hoc_ky ON DELETE CASCADE |
| phase | VARCHAR(16) | NO | CHECK in (`PRE`,`OFFICIAL`) |
| nam_nhap_hoc | INT | YES | NULL = áp cho mọi cohort |
| id_nganh | BIGINT | YES | FK → nganh_dao_tao ON DELETE SET NULL; NULL = mọi ngành |
| open_at | TIMESTAMPTZ | NO | bắt đầu mở |
| close_at | TIMESTAMPTZ | NO | kết thúc mở |
| ghi_chu | VARCHAR(500) | YES | |
| created_by | VARCHAR(100) | YES | username admin |
| created_at | TIMESTAMPTZ | NO | |
| updated_at | TIMESTAMPTZ | NO | |

Index/Constraint:
- `idx_regwin_hk_phase (id_hoc_ky, phase)`.
- `idx_regwin_open_close (open_at, close_at)`.
- UNIQUE functional `(id_hoc_ky, phase, COALESCE(nam_nhap_hoc,-1), COALESCE(id_nganh,-1))`.
- CHECK `open_at < close_at`.
- CHECK `id_nganh IS NULL OR nam_nhap_hoc IS NOT NULL` (chọn ngành phải kèm cohort).

Quy tắc resolve scope (specific → general):
1. Match đúng `(hocKy, phase, namNhapHoc, idNganh)`.
2. Match `(hocKy, phase, namNhapHoc)`, ngành null trong window.
3. Match `(hocKy, phase)`, cả cohort/ngành null.
4. Fallback các cột `pre_dang_ky_*` / `dang_ky_chinh_thuc_*` trên `hoc_ky`.

### 2.9 `pre_registration_intent`
Nguyện vọng đăng ký dự kiến (Sprint 2).
| Cột | Kiểu | NULL | Mô tả |
|-----|------|------|-------|
| id_intent | BIGSERIAL | NO | PK |
| id_sinh_vien | BIGINT | NO | FK → sinh_vien ON DELETE CASCADE |
| id_hoc_ky | BIGINT | NO | FK → hoc_ky ON DELETE CASCADE |
| id_hoc_phan | BIGINT | NO | FK → hoc_phan ON DELETE CASCADE |
| priority | INT | NO | DEFAULT 1, >= 1 |
| ghi_chu | VARCHAR(500) | YES | |
| created_at | TIMESTAMPTZ | NO | |
| updated_at | TIMESTAMPTZ | NO | |

Index:
- `idx_prereg_intent_hk_hp (id_hoc_ky, id_hoc_phan)` cho aggregate demand.
- `idx_prereg_intent_sv_hk (id_sinh_vien, id_hoc_ky)`.
- UNIQUE `(id_sinh_vien, id_hoc_ky, id_hoc_phan)`.

### 2.10 `dang_ky_hoc_phan`
Đăng ký chính thức.
| Cột | Kiểu | NULL | Mô tả |
|-----|------|------|-------|
| id_dang_ky | BIGSERIAL | NO | PK |
| id_sinh_vien | BIGINT | NO | FK → sinh_vien |
| id_lop_hp | BIGINT | NO | FK → lop_hoc_phan |
| id_hoc_ky | BIGINT | NO | FK → hoc_ky |
| ngay_dang_ky | TIMESTAMP | NO | DEFAULT now |
| trang_thai_dang_ky | VARCHAR(30) | NO | DEFAULT `THANH_CONG` |

Index:
- `idx_dkhp_sv (id_sinh_vien)`.
- `idx_dkhp_hoc_ky (id_hoc_ky)`.
- UNIQUE `(id_sinh_vien, id_lop_hp)`.

### 2.11 `registration_request_log`
Idempotency + audit log cho luồng đăng ký/hủy (Sprint 4).
| Cột | Kiểu | NULL | Mô tả |
|-----|------|------|-------|
| id_log | BIGSERIAL | NO | PK |
| idempotency_key | VARCHAR(128) | NO | UNIQUE; mặc định = `traceId` từ Kafka |
| id_sinh_vien | BIGINT | YES | tham chiếu lỏng |
| id_lop_hp | BIGINT | YES | tham chiếu lỏng |
| id_hoc_ky | BIGINT | YES | tham chiếu lỏng |
| request_type | VARCHAR(16) | YES | CHECK in (`REGISTER`,`CANCEL`) |
| outcome | VARCHAR(32) | YES | CHECK in (`SUCCESS`,`DUPLICATE`,`FULL`,`VALIDATION_FAILED`,`REJECTED`,`CANCELLED`) |
| id_dang_ky | BIGINT | YES | trỏ về `dang_ky_hoc_phan` nếu thành công |
| error_code | VARCHAR(64) | YES | mã lỗi nghiệp vụ |
| error_message | VARCHAR(1000) | YES | |
| created_at | TIMESTAMPTZ | NO | |

Index:
- UNIQUE `idempotency_key`.
- `idx_reqlog_sv_lhp_created (id_sinh_vien, id_lop_hp, created_at)` cho audit.
- `idx_reqlog_outcome (outcome)` cho dashboard.

### 2.12 `student_timetable_entry`
Read-model TKB sinh viên (Sprint 5), cập nhật bởi listener AFTER_COMMIT.
| Cột | Kiểu | NULL | Mô tả |
|-----|------|------|-------|
| id_entry | BIGSERIAL | NO | PK |
| id_sinh_vien | BIGINT | NO | |
| id_hoc_ky | BIGINT | NO | |
| id_dang_ky | BIGINT | NO | |
| id_lop_hp | BIGINT | NO | |
| ma_lop_hp | VARCHAR(30) | YES | denormalized |
| ma_hoc_phan | VARCHAR(30) | YES | |
| ten_hoc_phan | VARCHAR(255) | YES | |
| ten_giang_vien | VARCHAR(255) | YES | |
| slot_index | SMALLINT | NO | index trong mảng thoi_khoa_bieu_json (0-based) |
| thu | SMALLINT | YES | 1..8 hoặc null |
| tiet | VARCHAR(20) | YES | "1-3" |
| phong | VARCHAR(50) | YES | |
| ngay_bat_dau | DATE | YES | |
| ngay_ket_thuc | DATE | YES | |
| updated_at | TIMESTAMPTZ | NO | |

Index:
- UNIQUE `(id_dang_ky, slot_index)`.
- `idx_stt_sv_hk (id_sinh_vien, id_hoc_ky)`.
- `idx_stt_lhp (id_lop_hp)`.
- `idx_stt_dangky (id_dang_ky)`.

### 2.13 `tai_khoan` (rút gọn)
| Cột | Kiểu | NULL | Mô tả |
|-----|------|------|-------|
| id | BIGSERIAL | NO | PK |
| username | VARCHAR(100) | NO | UNIQUE |
| password | VARCHAR(255) | NO | BCrypt hash |
| role | VARCHAR(20) | NO | `STUDENT` / `LECTURER` / `ADMIN` |
| status | VARCHAR(20) | NO | `ACTIVE` / `LOCKED` |
| email | VARCHAR(255) | YES | |
| mfa_bat | BOOLEAN | NO | DEFAULT false |

---

## 3) Bảng phụ trợ (giữ nhưng ngoài scope sâu)

| Bảng | Vai trò | Tham chiếu chi tiết |
|------|--------|----------------------|
| `gio_hang_dang_ky` | Giỏ đăng ký nháp PRE (luồng cũ song song với intent) | `PreRegistrationCartServiceImpl` |
| `pre_registration_link`, `pre_registration_request` | Public pre-reg cho tuyển sinh, không thuộc scope | migration `migration_prereg_public_p0.sql` |
| `tkb_block` | Block môn bắt buộc đăng ký cả block | migration `migration_tkb_block_*.sql` |
| `mfa_otp_challenge` | OTP MFA admin | tài liệu MFA riêng |
| `lich_thi`, `phieu_du_thi` | Khảo thí | ngoài scope |

---

## 4) Mẫu seed data tối thiểu cho test

```sql
-- 1 học kỳ hiện hành
INSERT INTO hoc_ky (ky_thu, nam_hoc, trang_thai_hien_hanh,
                    pre_dang_ky_mo_tu, pre_dang_ky_mo_den,
                    dang_ky_chinh_thuc_tu, dang_ky_chinh_thuc_den)
VALUES (1, '2024-2025', true,
        NOW() - INTERVAL '1 day', NOW() + INTERVAL '7 day',
        NOW() + INTERVAL '8 day', NOW() + INTERVAL '14 day');

-- 1 cửa sổ PRE cho khóa K2023 ngành CT863
INSERT INTO registration_window (id_hoc_ky, phase, nam_nhap_hoc, id_nganh,
                                 open_at, close_at, created_by)
SELECT hk.id_hoc_ky, 'PRE', 2023, nd.id_nganh,
       NOW() - INTERVAL '1 day', NOW() + INTERVAL '7 day', 'admin'
FROM hoc_ky hk
CROSS JOIN nganh_dao_tao nd
WHERE hk.trang_thai_hien_hanh = true AND nd.ma_nganh = 'CT863';

-- 1 lớp học phần đã PUBLISHED
INSERT INTO lop_hoc_phan (ma_lop_hp, id_hoc_phan, id_hoc_ky, id_giang_vien,
                          si_so_toi_da, si_so_thuc_te, hoc_phi,
                          trang_thai, status_publish, thoi_khoa_bieu_json)
VALUES ('IT6002_1', 1, 1, 1,
        50, 0, 1500000,
        'DANG_MO', 'PUBLISHED',
        '[{"thu":2,"tiet":"1-3","phong":"D3-501"}]'::jsonb);
```

---

## 5) Quy tắc concurrency và lock

- Khi đăng ký:
  ```sql
  UPDATE lop_hoc_phan
     SET si_so_thuc_te = si_so_thuc_te + 1,
         version       = version + 1
   WHERE id_lop_hp = :id
     AND si_so_thuc_te < si_so_toi_da
     AND status_publish = 'PUBLISHED';
  ```
  Nếu `affected_rows = 0` → trả `CLASS_FULL` hoặc `NOT_PUBLISHED`.
- Khi hủy:
  ```sql
  UPDATE lop_hoc_phan
     SET si_so_thuc_te = GREATEST(0, si_so_thuc_te - 1),
         version       = version + 1
   WHERE id_lop_hp = :id;
  ```
- Idempotency log:
  ```sql
  INSERT INTO registration_request_log(idempotency_key, ...)
  VALUES (:key, ...)
  ON CONFLICT (idempotency_key) DO NOTHING
  RETURNING id_log;
  ```
  Nếu `RETURNING` rỗng nghĩa là replay, tra cứu bản cũ và trả lại kết quả tương tự.

---

## 6) Mapping bảng ↔ entity Java

| Bảng | Entity Java |
|------|-------------|
| `hoc_ky` | `domain/entity/HocKy.java` |
| `khoa` | `domain/entity/Khoa.java` |
| `nganh_dao_tao` | `domain/entity/NganhDaoTao.java` |
| `lop` | `domain/entity/Lop.java` |
| `sinh_vien` | `domain/entity/SinhVien.java` |
| `hoc_phan` | `domain/entity/HocPhan.java` |
| `lop_hoc_phan` | `domain/entity/LopHocPhan.java` |
| `registration_window` | `domain/entity/RegistrationWindow.java` |
| `pre_registration_intent` | `domain/entity/PreRegistrationIntent.java` |
| `dang_ky_hoc_phan` | `domain/entity/DangKyHocPhan.java` |
| `registration_request_log` | `domain/entity/RegistrationRequestLog.java` |
| `student_timetable_entry` | `domain/entity/StudentTimetableEntry.java` |
| `tai_khoan` | `domain/entity/User.java` |

---

## 7) Phụ lục vận hành — index, batch query và chăm sóc dữ liệu

### 7.1 Bảng tra index nghiệp vụ (Postgres object name)

| Mục đích truy vấn | Bảng | Index / UNIQUE (tên trong entity migration) |
|-------------------|------|----------------------------------------------|
| TKB snapshot theo SV + HK | `student_timetable_entry` | `idx_stt_sv_hk (id_sinh_vien, id_hoc_ky)` |
| Rebuild một đăng ký | `student_timetable_entry` | `idx_stt_dangky`, `uq_stt_dangky_slot` |
| Lọp theo lớp học phần | `student_timetable_entry` | `idx_stt_lhp` |
| Idempotency replay | `registration_request_log` | UNIQUE `idempotency_key` (xem migration sprint 4) |
| Cửa sổ theo HK + scope | `registration_window` | index theo `id_hoc_ky` + constraint business (đọc migration sprint 1) |

*(Chi tiết cột đầy đủ nằm ở các section 2.x phía trên và file SQL trong `backend-core/src/main/resources/db/`.)*

### 7.2 Ví dụ SQL kiểm tra sau triển khai

Đếm projection slot theo học kỳ (smoke):

```sql
SELECT id_hoc_ky, COUNT(*) AS slots
FROM student_timetable_entry
GROUP BY id_hoc_ky
ORDER BY id_hoc_ky;
```

Tìm SV có projection trống nhưng vẫn có đăng ký ACTIVE (ghi nhận bug / sync):

```sql
SELECT dk.id_sinh_vien, dk.id_hoc_ky, COUNT(dk.id_dang_ky) AS regs
FROM dang_ky_hoc_phan dk
LEFT JOIN student_timetable_entry e
  ON e.id_dang_ky = dk.id_dang_ky
WHERE dk.trang_thai_dang_ky IN ('THANH_CONG', 'CHO_DUYET')
GROUP BY dk.id_sinh_vien, dk.id_hoc_ky
HAVING COUNT(e.id_entry) = 0
LIMIT 20;
```

### 7.3 Rebuild projection (ADMIN)

Rebuild toàn projection một SV trong một HK thực hiện qua service admin F06/F12 — không chạy `DELETE` tay trừ khi runbook yêu cầu.

Nguyên tắc:
- Projection là **read-model**; có thể xóa và tái sinh từ `dang_ky_hoc_phan` + `lop_hoc_phan.thoi_khoa_bieu_json`.
- Nếu lớp chưa có JSON TKB → projection trả **0 slot** nhưng đăng ký vẫn hợp lệ (UX phụ thuộc F03 scheduling).

### 7.4 Bảo trì Postgres (gợi ý SRE)

| Hoạt động | Khi nào | Ghi chú |
|-----------|---------|---------|
| `VACUUM (ANALYZE)` các bản ghi append-only lớn | sau mùa ĐK | `registration_request_log` growth |
| Theo dõi bloat JSON | QA | slot TKB không đồng nhất schema → log projection warn |
| Backup trước migration | mọi lần release | đặc biệt thêm cột publish / window |
