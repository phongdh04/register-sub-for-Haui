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
public class PhongJsonLhpAuditItem {

    private Long idLopHp;
    private String maLopHp;
    private Long idPhongFkCurrent;
    /**
     * NO_JSON | EMPTY_JSON | NO_PHONG_KEYS | ALL_SLOTS_UNIQUE |
     * HAS_AMBIGUOUS | HAS_NO_MATCH | MIXED
     */
    private String overallCategory;
    /** Gợi ý FK: slot đầu tiên trong JSON có resolution UNIQUE (dùng cho batch apply). */
    private Long suggestedFkPhongId;
    private List<PhongJsonSlotAuditItem> slots;
}
