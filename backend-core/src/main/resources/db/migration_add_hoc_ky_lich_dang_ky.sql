-- Thêm cột lịch đăng ký theo học kỳ (PostgreSQL).
-- Khi dùng spring.jpa.hibernate.ddl-auto=update, Hibernate có thể đã tạo cột tương ứng; file này để áp thủ công / tài liệu.

ALTER TABLE hoc_ky ADD COLUMN IF NOT EXISTS pre_dk_mo_tu TIMESTAMPTZ;
ALTER TABLE hoc_ky ADD COLUMN IF NOT EXISTS pre_dk_mo_den TIMESTAMPTZ;
ALTER TABLE hoc_ky ADD COLUMN IF NOT EXISTS dk_chinh_thuc_tu TIMESTAMPTZ;
ALTER TABLE hoc_ky ADD COLUMN IF NOT EXISTS dk_chinh_thuc_den TIMESTAMPTZ;
