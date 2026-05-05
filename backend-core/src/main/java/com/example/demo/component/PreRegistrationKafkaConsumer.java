package com.example.demo.component;

import com.example.demo.payload.request.PreRegistrationQueueMessageDto;
import com.example.demo.service.impl.PublicPreRegistrationServiceImpl;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PreRegistrationKafkaConsumer {

    private final PublicPreRegistrationServiceImpl preRegistrationService;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @KafkaListener(
            topics = "${eduport.kafka.topic.pre-registration}",
            groupId = "${spring.kafka.consumer.group-id}",
            concurrency = "2"
    )
    public void consume(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        PreRegistrationQueueMessageDto dto;
        try {
            dto = objectMapper.readValue(message, PreRegistrationQueueMessageDto.class);
        } catch (Exception ex) {
            log.error("❌ Lỗi deserialize pre-reg message: partition={} offset={} err={} payload={}",
                    partition, offset, ex.getMessage(), message);
            return;
        }

        try {
            preRegistrationService.processQueuedRequest(dto, partition, offset);
            log.info("✅ Pre-reg message processed: requestId={} partition={} offset={}",
                    dto.getRequestId(), partition, offset);
        } catch (IllegalArgumentException ex) {
            log.warn("⚠️ Pre-reg message business-invalid: requestId={} partition={} offset={} err={}",
                    dto.getRequestId(), partition, offset, ex.getMessage());
        } catch (Exception ex) {
            log.error("❌ Lỗi xử lý pre-reg message: requestId={} partition={} offset={} err={}",
                    dto.getRequestId(), partition, offset, ex.getMessage(), ex);
            throw new RuntimeException("Lỗi xử lý pre-registration message", ex);
        }
    }
}
