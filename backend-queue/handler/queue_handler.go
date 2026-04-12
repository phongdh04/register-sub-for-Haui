package handler

import (
	"eduport-queue/domain"
	"eduport-queue/service"

	"github.com/gofiber/fiber/v2"
)

// QueueHandler xử lý HTTP request từ Frontend / API Gateway.
// SRP: Chỉ chịu trách nhiệm parse request và format response HTTP.
// DIP: Phụ thuộc vào IQueueService interface, không biết QueueService cụ thể.
type QueueHandler struct {
	queueSvc service.IQueueService
}

// NewQueueHandler factory constructor.
func NewQueueHandler(svc service.IQueueService) *QueueHandler {
	return &QueueHandler{queueSvc: svc}
}

// RegisterRoutes đăng ký tất cả route liên quan đến queue.
func (h *QueueHandler) RegisterRoutes(app *fiber.App) {
	api := app.Group("/api/v1/queue")

	// POST /api/v1/queue/dang-ky  → SV nhấn "Đăng Ký" trong Giờ Vàng
	api.Post("/dang-ky", h.DangKyHocPhan)

	// DELETE /api/v1/queue/huy-dang-ky → SV hủy đăng ký
	api.Delete("/huy-dang-ky", h.HuyDangKy)

	// GET /api/v1/queue/slot/:id_lop_hp → Frontend polling slot còn lại (real-time)
	api.Get("/slot/:id_lop_hp", h.LayThongTinSlot)
}

// DangKyHocPhan godoc
// @Summary      Đăng ký học phần (Giờ Vàng)
// @Description  Nhận yêu cầu đăng ký, giảm slot Redis nguyên tử, đẩy message vào Kafka
// @Tags         queue
// @Accept       json
// @Produce      json
// @Param        body  body      domain.DangKyRequest  true  "Thông tin đăng ký"
// @Success      200   {object}  domain.DangKyResponse
// @Failure      400   {object}  map[string]string
// @Router       /api/v1/queue/dang-ky [post]
func (h *QueueHandler) DangKyHocPhan(c *fiber.Ctx) error {
	var req domain.DangKyRequest
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{
			"status":  "ERROR",
			"message": "Dữ liệu không hợp lệ: " + err.Error(),
		})
	}

	// Validate cơ bản
	if req.IDSinhVien <= 0 || req.IDLopHp <= 0 || req.IDHocKy <= 0 {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{
			"status":  "ERROR",
			"message": "id_sinh_vien, id_lop_hp và id_hoc_ky phải lớn hơn 0",
		})
	}

	resp, err := h.queueSvc.DangKyHocPhan(c.Context(), req)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"status":  "ERROR",
			"message": "Lỗi hệ thống: " + err.Error(),
		})
	}

	// Xác định HTTP status code dựa theo trạng thái nghiệp vụ
	statusCode := fiber.StatusOK
	if resp.Status == "FULL" || resp.Status == "DUPLICATE" || resp.Status == "REJECTED" {
		statusCode = fiber.StatusConflict
	}
	if resp.Status == "ERROR" {
		statusCode = fiber.StatusServiceUnavailable
	}

	return c.Status(statusCode).JSON(resp)
}

// HuyDangKy godoc
// @Summary      Hủy đăng ký học phần
// @Description  Trả lại slot Redis và gửi message hủy vào Kafka để Java xóa record DB
// @Tags         queue
// @Accept       json
// @Produce      json
// @Param        body  body      domain.HuyCancelRequest  true  "Thông tin hủy đăng ký"
// @Success      200   {object}  domain.DangKyResponse
// @Failure      400   {object}  map[string]string
// @Router       /api/v1/queue/huy-dang-ky [delete]
func (h *QueueHandler) HuyDangKy(c *fiber.Ctx) error {
	var req domain.HuyCancelRequest
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{
			"status":  "ERROR",
			"message": "Dữ liệu không hợp lệ: " + err.Error(),
		})
	}

	if req.IDSinhVien <= 0 || req.IDLopHp <= 0 || req.IDHocKy <= 0 {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{
			"status":  "ERROR",
			"message": "id_sinh_vien, id_lop_hp và id_hoc_ky phải lớn hơn 0",
		})
	}

	resp, err := h.queueSvc.HuyDangKy(c.Context(), req)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"status":  "ERROR",
			"message": "Lỗi hệ thống: " + err.Error(),
		})
	}

	return c.JSON(resp)
}

// LayThongTinSlot godoc
// @Summary      Xem số chỗ còn lại của lớp học phần
// @Description  Trả về số slot còn lại từ Redis (real-time)
// @Tags         queue
// @Produce      json
// @Param        id_lop_hp  path      int  true  "ID lớp học phần"
// @Success      200        {object}  domain.SlotInfo
// @Router       /api/v1/queue/slot/{id_lop_hp} [get]
func (h *QueueHandler) LayThongTinSlot(c *fiber.Ctx) error {
	lopHpID, err := c.ParamsInt("id_lop_hp")
	if err != nil || lopHpID <= 0 {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{
			"status":  "ERROR",
			"message": "id_lop_hp không hợp lệ",
		})
	}

	slot, err := h.queueSvc.LayThongTinSlot(c.Context(), int64(lopHpID))
	if err != nil {
		return c.Status(fiber.StatusNotFound).JSON(fiber.Map{
			"status":  "ERROR",
			"message": err.Error(),
		})
	}

	return c.JSON(domain.SlotInfo{
		IDLopHp:    int64(lopHpID),
		SlotConLai: slot,
	})
}
