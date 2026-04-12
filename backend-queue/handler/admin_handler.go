package handler

import (
	"eduport-queue/service"

	"github.com/gofiber/fiber/v2"
)

// AdminHandler xử lý các API nội bộ dành cho Admin.
// Mục đích chính: Admin mở đợt ĐKHP → Nạp slot vào Redis trước Giờ Vàng.
type AdminHandler struct {
	queueSvc service.IQueueService
}

// NewAdminHandler factory constructor.
func NewAdminHandler(svc service.IQueueService) *AdminHandler {
	return &AdminHandler{queueSvc: svc}
}

// RegisterRoutes đăng ký các route admin.
func (h *AdminHandler) RegisterRoutes(app *fiber.App) {
	admin := app.Group("/api/v1/admin")

	// POST /api/v1/admin/khoi-tao-slot → Admin nạp slot cho một lớp học phần
	admin.Post("/khoi-tao-slot", h.KhoiTaoSlot)
}

type KhoiTaoSlotRequest struct {
	IDLopHp   int64 `json:"id_lop_hp" validate:"required,gt=0"`
	SiSoToiDa int64 `json:"si_so_toi_da" validate:"required,gt=0"`
}

// KhoiTaoSlot godoc
// @Summary      Khởi tạo slot Redis cho lớp học phần (Admin)
// @Description  Admin gọi API này trước khi mở đợt ĐKHP để nạp số slot vào Redis
// @Tags         admin
// @Accept       json
// @Produce      json
// @Param        body  body  KhoiTaoSlotRequest  true  "id_lop_hp và si_so_toi_da"
// @Success      200   {object}  map[string]string
// @Router       /api/v1/admin/khoi-tao-slot [post]
func (h *AdminHandler) KhoiTaoSlot(c *fiber.Ctx) error {
	var req KhoiTaoSlotRequest
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{
			"status":  "ERROR",
			"message": "Dữ liệu không hợp lệ: " + err.Error(),
		})
	}

	if req.IDLopHp <= 0 || req.SiSoToiDa <= 0 {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{
			"status":  "ERROR",
			"message": "id_lop_hp và si_so_toi_da phải lớn hơn 0",
		})
	}

	if err := h.queueSvc.KhoiTaoSlot(c.Context(), req.IDLopHp, req.SiSoToiDa); err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"status":  "ERROR",
			"message": "Lỗi khởi tạo slot: " + err.Error(),
		})
	}

	return c.JSON(fiber.Map{
		"status":  "SUCCESS",
		"message": "Đã khởi tạo slot Redis thành công",
		"data": fiber.Map{
			"id_lop_hp":   req.IDLopHp,
			"si_so_toi_da": req.SiSoToiDa,
		},
	})
}
