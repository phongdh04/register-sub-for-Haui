# Luận văn đăng ký học phần — Luồng theo vai trò (workflow)

*Tài liệu gốc hóa chức năng và luồng nghiệp vụ*: `reports/thesis/features/F*/` và RBAC trong [`reports/thesis/cross/06_rbac_security.md`](thesis/cross/06_rbac_security.md).

---

## UI routes sau cleanup (chốt backlog dev)

Đồng bộ với [`ui_cleanup_static_api_screen_removal_dev_plan.md`](ui_cleanup_static_api_screen_removal_dev_plan.md) (§7) và [`ui_cleanup_static_api_and_screen_removal_plan.md`](ui_cleanup_static_api_and_screen_removal_plan.md).

### STUDENT (`ROLE_STUDENT`)

| Path | Màn |
|------|-----|
| `/student` | → `/student/registration` |
| `/student/registration` | Đăng ký học phần chính thức (OFFICIAL) |
| `/student/pre-registration` | PRE — nguyện vọng (intent) |
| `/student/timetable` | Thời khóa biểu |

Sidebar sau cleanup chỉ còn 3 mục trên (ticket UI-CLEAN-014).

### ADMIN (`ROLE_ADMIN`)

| Path | Màn |
|------|-----|
| `/admin` | → `/admin/registration-windows` |
| `/admin/registration-windows` | Cửa sổ đăng ký (F02) |
| `/admin/pre-registration-demand` | Nhu cầu PRE (F04) |
| `/admin/class-publish` | Xuất bản lớp (F03) |
| `/admin/timetable-projection-tools` | Projection TKB (F06) |
| `/admin/registration-monitoring` | Giám sát đăng ký (F05) |
| `/admin/lichdangkyhockyconfig` | Lịch đăng ký học kỳ — fallback legacy (F07) |

Menu sau cleanup chỉ còn 6 mục trên (ticket UI-CLEAN-015).

### Giảng viên / các portal khác

Mặc định backlog cleanup: có thể gỡ toàn block `/teacher` và `TeacherLayout`; khi đó `ROLE_LECTURER` chỉ dùng trong seed/RBAC chứ không có SPA luận văn (`UI-CLEAN-023`). Chi tiết trong dev plan nhánh teacher.

---

*Tài liệu này thay placeholder tên cố định `thesis_registration_02_*` được trích trong kế hoạch UI cleanup (Pha 4).*
