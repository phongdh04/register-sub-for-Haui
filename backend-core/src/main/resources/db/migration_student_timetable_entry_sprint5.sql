-- ====================================================================
-- Sprint 5 — Read-model TKB sinh viên (student_timetable_entry)
-- ====================================================================
-- Read-model denormalize từ Lop_Hoc_Phan.thoi_khoa_bieu_json để render
-- TKB sinh viên không phải parse JSON mỗi request, và phản ánh ngay
-- sau khi REGISTRATION_CONFIRMED event commit.
-- ====================================================================

CREATE TABLE IF NOT EXISTS student_timetable_entry (
    id_entry         BIGSERIAL PRIMARY KEY,
    id_sinh_vien     BIGINT       NOT NULL,
    id_hoc_ky        BIGINT       NOT NULL,
    id_dang_ky       BIGINT       NOT NULL,
    id_lop_hp        BIGINT       NOT NULL,
    ma_lop_hp        VARCHAR(30),
    ma_hoc_phan      VARCHAR(30),
    ten_hoc_phan     VARCHAR(255),
    ten_giang_vien   VARCHAR(255),
    slot_index       SMALLINT     NOT NULL,
    thu              SMALLINT,
    tiet             VARCHAR(20),
    phong            VARCHAR(50),
    ngay_bat_dau     DATE,
    ngay_ket_thuc    DATE,
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- 1 đăng ký + slotIndex → 1 row (idempotent re-emit event).
CREATE UNIQUE INDEX IF NOT EXISTS uq_stt_dangky_slot
    ON student_timetable_entry (id_dang_ky, slot_index);

-- Truy vấn TKB sinh viên theo học kỳ.
CREATE INDEX IF NOT EXISTS idx_stt_sv_hk
    ON student_timetable_entry (id_sinh_vien, id_hoc_ky);

-- Tra cứu theo lớp (admin: ai đang ở lớp này).
CREATE INDEX IF NOT EXISTS idx_stt_lhp
    ON student_timetable_entry (id_lop_hp);

-- Tra cứu theo đăng ký (cancel → xoá nhanh).
CREATE INDEX IF NOT EXISTS idx_stt_dangky
    ON student_timetable_entry (id_dang_ky);

-- Sanity check thứ trong tuần (hoặc null nếu lớp chưa có lịch).
ALTER TABLE student_timetable_entry
    DROP CONSTRAINT IF EXISTS chk_stt_thu_range;
ALTER TABLE student_timetable_entry
    ADD CONSTRAINT chk_stt_thu_range CHECK (thu IS NULL OR thu BETWEEN 1 AND 8);

COMMENT ON TABLE student_timetable_entry IS
    'Sprint 5 — read-model TKB sinh viên, cập nhật bởi RegistrationTimetableProjectionListener (AFTER_COMMIT).';
COMMENT ON COLUMN student_timetable_entry.slot_index IS
    'Index của slot trong mảng thoi_khoa_bieu_json của lớp (0-based).';
