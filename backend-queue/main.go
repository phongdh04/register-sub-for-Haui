package main

import (
	"eduport-queue/config"
	"eduport-queue/handler"
	"eduport-queue/infrastructure"
	"eduport-queue/service"
	"log"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/fiber/v2/middleware/cors"
	"github.com/gofiber/fiber/v2/middleware/logger"
	"github.com/gofiber/fiber/v2/middleware/recover"
)

func main() {
	// ── 1. Load cấu hình từ biến môi trường ───────────────────
	cfg := config.Load()

	// ── 2. Khởi tạo Infrastructure ────────────────────────────
	rdb := infrastructure.NewRedisClient(cfg.Redis)
	defer rdb.Close()

	kafkaProducer := infrastructure.NewKafkaProducer(cfg.Kafka)
	if kafkaProducer != nil {
		defer kafkaProducer.Close()
	}

	// ── 3. Khởi tạo Service Layer (Business Logic) ────────────
	queueSvc := service.NewQueueService(rdb, kafkaProducer, cfg.Kafka.Topic)

	// ── 4. Khởi tạo Fiber App với middleware ──────────────────
	app := fiber.New(fiber.Config{
		AppName:      "EduPort Queue Service v1.0",
		ReadTimeout:  10 * time.Second,
		WriteTimeout: 10 * time.Second,
		// ErrorHandler tập trung – tránh để lộ stacktrace ra ngoài
		ErrorHandler: func(c *fiber.Ctx, err error) error {
			code := fiber.StatusInternalServerError
			var e *fiber.Error
			if ok := false; ok {
				_ = e
				code = e.Code
			}
			return c.Status(code).JSON(fiber.Map{
				"status":  "ERROR",
				"message": err.Error(),
			})
		},
	})

	// Middleware: CORS cho Frontend
	app.Use(cors.New(cors.Config{
		AllowOrigins: "*",
		AllowMethods: "GET,POST,DELETE,OPTIONS",
		AllowHeaders: "Origin,Content-Type,Authorization",
	}))

	// Middleware: Request Logger (format ELK-ready)
	app.Use(logger.New(logger.Config{
		Format: "[${time}] ${method} ${path} ${status} ${latency} ${ip}\n",
	}))

	// Middleware: Panic Recovery
	app.Use(recover.New())

	// ── 5. Đăng ký Routes ─────────────────────────────────────
	queueHandler := handler.NewQueueHandler(queueSvc)
	queueHandler.RegisterRoutes(app)

	adminHandler := handler.NewAdminHandler(queueSvc)
	adminHandler.RegisterRoutes(app)

	// Health check endpoint
	app.Get("/health", func(c *fiber.Ctx) error {
		return c.JSON(fiber.Map{
			"status":  "UP",
			"service": "eduport-go-queue",
			"version": "1.0.0",
		})
	})

	// Root endpoint
	app.Get("/", func(c *fiber.Ctx) error {
		return c.JSON(fiber.Map{
			"service": "🚀 EduPort Queue Service (Golang)",
			"desc":    "Cổng vào Giờ Vàng: Cooldown → Lock → Redis DECR → Kafka",
			"endpoints": []string{
				"POST   /api/v1/queue/dang-ky",
				"DELETE /api/v1/queue/huy-dang-ky",
				"GET    /api/v1/queue/slot/:id_lop_hp",
				"POST   /api/v1/admin/khoi-tao-slot",
			},
		})
	})

	// ── 6. Graceful Shutdown ───────────────────────────────────
	quit := make(chan os.Signal, 1)
	signal.Notify(quit, os.Interrupt, syscall.SIGTERM)

	go func() {
		log.Printf("🚀 EduPort Go Queue Service đang chạy trên port :%s", cfg.Server.Port)
		if err := app.Listen(":" + cfg.Server.Port); err != nil {
			log.Fatalf("❌ Không thể start server: %v", err)
		}
	}()

	// Chờ tín hiệu tắt máy
	<-quit
	log.Println("🔻 Nhận tín hiệu tắt – đang shutdown gracefully...")

	if err := app.ShutdownWithTimeout(5 * time.Second); err != nil {
		log.Printf("⚠️ Shutdown timeout: %v", err)
	}
	log.Println("✅ Server đã dừng an toàn.")
}
