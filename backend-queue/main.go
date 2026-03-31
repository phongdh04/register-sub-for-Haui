package main

import (
	"context"
	"fmt"
	"log"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/redis/go-redis/v9"
)

var ctx = context.Background()

func main() {
	// Khởi tạo Fiber App
	app := fiber.New()

	// Khởi tạo kết nối Redis
	rdb := redis.NewClient(&redis.Options{
		Addr:     "localhost:6379", // Kết nối tới Docker Redis
		Password: "eduport_redis",
		DB:       0,
	})

	// Test kết nối Redis
	if err := rdb.Ping(ctx).Err(); err != nil {
		log.Println("⚠️ Cảnh báo: Không thể kết nối tới Redis:", err)
	} else {
		log.Println("✅ Kết nối Redis thành công!")
	}

	app.Get("/", func(c *fiber.Ctx) error {
		return c.SendString("🚀 EduPort Queue Service (Golang) đang rỗng!")
	})

	app.Post("/api/v1/queue/register", func(c *fiber.Ctx) error {
		// Mock logic: Giảm slot trong môn học trên Redis
		// Push message vào Kafka
		return c.JSON(fiber.Map{
			"status":  "success",
			"message": "Đã đưa luồng đăng ký vào Hàng đợi (Queue)",
			"timestamp": time.Now(),
		})
	})

	log.Fatal(app.Listen(":3000"))
}
