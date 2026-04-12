package config

import (
	"log"
	"os"
	"strconv"
)

// AppConfig tổng hợp toàn bộ cấu hình của service.
// SRP: Chỉ chịu trách nhiệm đọc và cung cấp thông tin cấu hình.
type AppConfig struct {
	Server ServerConfig
	Redis  RedisConfig
	Kafka  KafkaConfig
	DB     DBConfig
}

type ServerConfig struct {
	Port string
}

type RedisConfig struct {
	Addr     string
	Password string
	DB       int
}

type KafkaConfig struct {
	Brokers []string
	Topic   string
}

type DBConfig struct {
	DSN string
}

// Load đọc tất cả cấu hình từ biến môi trường.
// Nếu biến môi trường không tồn tại, trả về giá trị mặc định.
func Load() *AppConfig {
	rDB, err := strconv.Atoi(getEnv("REDIS_DB", "0"))
	if err != nil {
		log.Println("⚠️  REDIS_DB không hợp lệ, dùng mặc định: 0")
		rDB = 0
	}

	return &AppConfig{
		Server: ServerConfig{
			Port: getEnv("SERVER_PORT", "3000"),
		},
		Redis: RedisConfig{
			Addr:     getEnv("REDIS_ADDR", "localhost:6379"),
			Password: getEnv("REDIS_PASSWORD", "eduport_redis"),
			DB:       rDB,
		},
		Kafka: KafkaConfig{
			Brokers: []string{getEnv("KAFKA_BROKER", "localhost:9092")},
			Topic:   getEnv("KAFKA_TOPIC", "eduport.dang-ky-hoc-phan"),
		},
		DB: DBConfig{
			DSN: getEnv("DATABASE_DSN",
				"host=localhost user=eduport_user password=eduport_password dbname=eduport_db port=5432 sslmode=disable TimeZone=Asia/Ho_Chi_Minh"),
		},
	}
}

func getEnv(key, defaultValue string) string {
	if value := os.Getenv(key); value != "" {
		return value
	}
	return defaultValue
}
