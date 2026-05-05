package com.example.demo.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhongJsonAuditResponse {

    private Long hocKyId;
    private String schemaVersion;
    private PhongJsonAuditSummary summary;
    private List<PhongJsonLhpAuditItem> details;
    private boolean detailsTruncated;
    private Integer detailsTotalBeforeTruncation;
}
