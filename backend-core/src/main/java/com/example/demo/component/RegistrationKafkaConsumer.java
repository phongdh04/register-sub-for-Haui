package com.example.demo.component;

import com.example.demo.payload.request.RegistrationMessageDto;
import com.example.demo.service.impl.DangKyHocPhanServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Kafka Consumer – Cầu nối trung tâm giữa Go Queue Service và Java Backend.
 *
 * Luồng đầy đủ:
 *   Go (Redis DECR + Kafka Publish)
 *     → Kafka Topic "eduport.dang-ky-hoc-phan"
 *       → Consumer (Deserialize JSON → DangKyMessageDto)
 *         → DangKyHocPhanServiceImpl (Chain Validate → INSERT DB)
 *
 * SRP: Chỉ nhận message và điều phối sang service, không chứa business logic.
 * OCP: Thêm topic → thêm @KafkaListener method mới, không sửa method cũ.
 *
 * Concurrency: concurrency=3 tức 3 thread consumer chạy song song.
 *              Kafka tự cân bằng partition nên không bị duplicate.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationKafkaConsumer {

    private final DangKyHocPhanServiceImpl dangKyService;

    // ObjectMapper dùng riêng để xử lý Instant (JavaTimeModule)
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    /**
     * Lắng nghe topic "eduport.dang-ky-hoc-phan".
     * groupId="eduport-java-consumer" đảm bảo horizontal scaling được.
     * concurrency=3 → 3 thread consumer, phù hợp với 3 partition Kafka.
     */
    @KafkaListener(
            topics = "${eduport.kafka.topic.dang-ky}",
            groupId = "${spring.kafka.consumer.group-id}",
            concurrency = "3"
    )
    public void consume(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("📨 Kafka nhận message: partition={} offset={}", partition, offset);

        RegistrationMessageDto dto;
        try {
            dto = objectMapper.readValue(message, RegistrationMessageDto.class);
        } catch (Exception e) {
            // Message không deserialize được → Log và skip (Dead Letter Queue nếu prod)
            log.error("❌ Lỗi deserialize message Kafka: offset={} err={} payload={}",
                    offset, e.getMessage(), message);
            return;
        }

        log.info("📋 Message parsed: traceID={} svID={} lopID={} isHuy={}",
                dto.getTraceId(), dto.getIdSinhVien(), dto.getIdLopHp(), dto.isCancellation());

        try {
            if (dto.isCancellation()) {
                // Message hủy (traceID bắt đầu bằng "CANCEL-")
                dangKyService.processCancellation(dto);
            } else {
                // Message đăng ký bình thường
                boolean success = dangKyService.processRegistration(dto);
                if (!success) {
                    log.warn("⚠️ Xử lý ĐKHP thất bại (vi phạm nghiệp vụ): traceID={}", dto.getTraceId());
                }
            }
        } catch (Exception e) {
            // Lỗi hệ thống (DB down, timeout...) → Log error, message sẽ được Kafka retry
            log.error("❌ Lỗi hệ thống khi xử lý ĐKHP: traceID={} err={}",
                    dto.getTraceId(), e.getMessage(), e);
            // Re-throw để Kafka retry theo cấu hình retry policy
            throw new RuntimeException("Lỗi xử lý ĐKHP: " + dto.getTraceId(), e);
        }
    }
}
