package com.example.demo.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreRegistrationQueueMessageDto {
    private String requestId;
    private Long linkId;
    private String dedupeKey;
    private String traceId;
    private String submittedAt;
    private Long payloadRefId;

    @JsonProperty("schema_version")
    private Integer schemaVersion;
}
