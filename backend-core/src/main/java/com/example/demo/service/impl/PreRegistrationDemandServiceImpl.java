package com.example.demo.service.impl;

import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.entity.NganhDaoTao;
import com.example.demo.payload.response.PreRegistrationDemandItemResponse;
import com.example.demo.payload.response.PreRegistrationDemandResponse;
import com.example.demo.repository.HocKyRepository;
import com.example.demo.repository.NganhDaoTaoRepository;
import com.example.demo.repository.PreRegistrationIntentRepository;
import com.example.demo.service.IPreRegistrationDemandService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Tổng hợp demand đăng ký dự kiến cho admin (đọc-only).
 *
 * <p>Tính toán client-friendly:
 * <ul>
 *   <li>Tổng số intent.</li>
 *   <li>Số lớp đề xuất theo từng học phần = {@code ceil(totalIntent / targetClassSize)}.</li>
 *   <li>Tổng số lớp đề xuất trong toàn phạm vi.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class PreRegistrationDemandServiceImpl implements IPreRegistrationDemandService {

    private final PreRegistrationIntentRepository intentRepository;
    private final HocKyRepository hocKyRepository;
    private final NganhDaoTaoRepository nganhDaoTaoRepository;

    @Value("${eduport.prereg.target-class-size-default:40}")
    private int defaultTargetClassSize;

    @Override
    @Transactional(readOnly = true)
    public PreRegistrationDemandResponse aggregate(Long hocKyId,
                                                   Integer namNhapHoc,
                                                   Long idNganh,
                                                   Integer targetClassSize) {
        if (hocKyId == null) {
            throw new IllegalArgumentException("hocKyId là bắt buộc.");
        }
        HocKy hocKy = hocKyRepository.findById(hocKyId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy học kỳ: " + hocKyId));

        int classSize = (targetClassSize != null && targetClassSize > 0)
                ? targetClassSize
                : defaultTargetClassSize;

        List<Object[]> rows = intentRepository.aggregateDemand(hocKyId, namNhapHoc, idNganh);

        long totalIntents = 0L;
        int totalClasses = 0;
        List<PreRegistrationDemandItemResponse> items = rows.stream()
                .map(r -> {
                    long total = ((Number) r[7]).longValue();
                    int recommended = (int) Math.max(1, Math.ceil((double) total / classSize));
                    return PreRegistrationDemandItemResponse.builder()
                            .idHocPhan(((Number) r[0]).longValue())
                            .maHocPhan((String) r[1])
                            .tenHocPhan((String) r[2])
                            .soTinChi(r[3] == null ? null : ((Number) r[3]).intValue())
                            .namNhapHoc(r[4] == null ? null : ((Number) r[4]).intValue())
                            .idNganh(r[5] == null ? null : ((Number) r[5]).longValue())
                            .tenNganh((String) r[6])
                            .totalIntent(total)
                            .recommendedClasses(recommended)
                            .build();
                })
                .toList();

        for (PreRegistrationDemandItemResponse it : items) {
            totalIntents += it.getTotalIntent();
            totalClasses += it.getRecommendedClasses();
        }

        String tenNganh = null;
        if (idNganh != null) {
            tenNganh = nganhDaoTaoRepository.findById(idNganh)
                    .map(NganhDaoTao::getTenNganh)
                    .orElse(null);
        }

        return PreRegistrationDemandResponse.builder()
                .idHocKy(hocKy.getIdHocKy())
                .tenHocKy("HK" + hocKy.getKyThu() + " " + hocKy.getNamHoc())
                .namNhapHoc(namNhapHoc)
                .idNganh(idNganh)
                .tenNganh(tenNganh)
                .targetClassSize(classSize)
                .totalIntents(totalIntents)
                .totalRecommendedClasses(totalClasses)
                .items(items)
                .build();
    }
}
