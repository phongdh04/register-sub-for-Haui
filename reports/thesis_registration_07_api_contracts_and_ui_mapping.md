# Luận văn đăng ký học phần — Ánh xạ API ↔ UI

**Catalog endpoint HTTP và gợi ý consumer:** [`reports/thesis/cross/04_api_catalog.md`](thesis/cross/04_api_catalog.md) — gồm blockquote Frontend theo § và **Annex A** (ánh xạ SPA ↔ API trong phạm vi catalog lõi đăng ký).

Danh mục rộng toàn SPA (bao gồm màn sẽ xóa ở Pha 2) có thể bổ sung trong Annex của catalog khi backlog mở rộng — **sau cleanup** chỉ các route trong § dưới đây còn được điều hướng từ menu.

---

## UI routes sau cleanup (single source trong dev plan)

Bảng chính thức trùng với [`ui_cleanup_static_api_screen_removal_dev_plan.md`](ui_cleanup_static_api_screen_removal_dev_plan.md) mục **§7**.

### STUDENT

| Path | Screen |
|------|--------|
| `/student` | → `/student/registration` |
| `/student/registration` | OFFICIAL |
| `/student/pre-registration` | PRE intents |
| `/student/timetable` | TKB |

### ADMIN

| Path | Screen |
|------|--------|
| `/admin` | → `/admin/registration-windows` |
| `/admin/registration-windows` | F02 |
| `/admin/pre-registration-demand` | F04 |
| `/admin/class-publish` | F03 |
| `/admin/timetable-projection-tools` | F06 |
| `/admin/registration-monitoring` | F05 |
| `/admin/lichdangkyhockyconfig` | F07 fallback |

### Auth

- `/login` — [`frontend/src/pages/ngNhpTruynThngQunLPhin.jsx`](../frontend/src/pages/ngNhpTruynThngQunLPhin.jsx)

---

*Khi chỉnh contract API sau merge cleanup, sửa trước `04_api_catalog.md` rồi căn chỉnh bảng tại đây nếu cần mô tả riêng luận văn.*
