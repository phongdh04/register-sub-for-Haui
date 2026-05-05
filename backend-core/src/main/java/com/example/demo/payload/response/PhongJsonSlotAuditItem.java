package com.example.demo.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhongJsonSlotAuditItem {

    private int slotIndex;
    private String phongRaw;
    /** UNIQUE | AMBIGUOUS | MISSING | NO_MATCH */
    private String resolution;
    private Long matchedIdPhong;
    private String matchedMaPhong;
    /** Ma phòng các ứng viên khi AMBIGUOUS */
    private String ambiguousMaPhongsJoined;
}
