# Luận văn đăng ký học phần — Khoảng trống & đơn giản hóa

## Phạm vi UI cleanup vs backend

Chi tiết điều hành và manifest file xóa: [`ui_cleanup_static_api_screen_removal_dev_plan.md`](ui_cleanup_static_api_screen_removal_dev_plan.md).

- **Frontend (Pha 1–2)** chỉ chỉnh `frontend/` (route, menu, xóa page ngoài phạm vi).
- **Backend:** không đổi URL endpoint trong các pha cleanup UI. Các endpoint từng được gọi bởi page nhóm B/C (**wallet, analytics, MFA admin, lecturer, scheduling demo…**) **vẫn có thể tồn tại** trong `backend-core` nhưng **không còn màn lõi SPA** trong phạm vi luận văn — đánh dấu **UI out-of-scope / backend retained** trong biên bản sprint.
- Theo dõi rollback Pha 2: tag **`before-ui-delete-20260509`** (Annotated, ghi trong dev plan §9).

## UI routes sau cleanup (tham chiếu)

Đồng nhất với [`thesis_registration_02_role_workflows.md`](thesis_registration_02_role_workflows.md) và §7 của dev plan (`Student`/`Admin` bảng path).

### Hạng mục còn mở (Pha 3 trở đi)

Mock / hardcopy string trong các page lõi đăng ký: ticket **UI-CLEAN-031 … 036**. WebSocket chờ `cross/05_websocket_protocol.md` — chỉ khuyến nghị slot UI trước khi có kênh thật.

---

*Đồng bộ trạng thái §9 của [`ui_cleanup_static_api_and_screen_removal_plan.md`](ui_cleanup_static_api_and_screen_removal_plan.md) khi đóng mỗi pha.*
