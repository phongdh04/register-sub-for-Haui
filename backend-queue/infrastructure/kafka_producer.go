package infrastructure

import (
	"eduport-queue/config"
	"log"

	"github.com/IBM/sarama"
)

// NewKafkaProducer khởi tạo Kafka SyncProducer sẵn sàng gửi message.
// OCP: Nhận KafkaConfig, không phụ thuộc chi tiết broker cụ thể.
func NewKafkaProducer(cfg config.KafkaConfig) sarama.SyncProducer {
	saramaConfig := sarama.NewConfig()

	// Đảm bảo producer nhận ACK từ tất cả Kafka leader replicas.
	saramaConfig.Producer.RequiredAcks = sarama.WaitForAll

	// Retry khi gửi thất bại.
	saramaConfig.Producer.Retry.Max = 5

	// Bật chế độ đồng bộ (SyncProducer) để nhận xác nhận.
	saramaConfig.Producer.Return.Successes = true

	// Bật idempotent producer để tránh duplicate message khi retry.
	saramaConfig.Producer.Idempotent = true
	saramaConfig.Net.MaxOpenRequests = 1

	producer, err := sarama.NewSyncProducer(cfg.Brokers, saramaConfig)
	if err != nil {
		log.Printf("⚠️ Kafka producer chưa kết nối được [%v]: %v. Service vẫn khởi động với Redis queue.", cfg.Brokers, err)
		return nil
	}

	log.Printf("✅ Kết nối Kafka thành công! Brokers=%v Topic=%s", cfg.Brokers, cfg.Topic)
	return producer
}

// NewKafkaConsumer khởi tạo Kafka Consumer để Java backend có thể tham khảo topic.
func NewKafkaConsumer(cfg config.KafkaConfig) sarama.Consumer {
	consumer, err := sarama.NewConsumer(cfg.Brokers, sarama.NewConfig())
	if err != nil {
		log.Printf("⚠️ Kafka consumer chưa kết nối được: %v", err)
		return nil
	}
	return consumer
}
