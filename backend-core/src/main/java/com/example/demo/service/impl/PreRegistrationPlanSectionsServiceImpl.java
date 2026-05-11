package com.example.demo.service.impl;

import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.entity.HocPhan;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.enums.LopHocPhanPublishStatus;
import com.example.demo.payload.request.PreRegistrationPlanSectionsRequest;
import com.example.demo.payload.response.PreRegistrationPlanSectionsResponse;
import com.example.demo.repository.HocKyRepository;
import com.example.demo.repository.HocPhanRepository;
import com.example.demo.repository.LopHocPhanRepository;
import com.example.demo.repository.PreRegistrationIntentRepository;
import com.example.demo.service.IPreRegistrationPlanSectionsService;
import com.example.demo.service.ITkbRevisionService;
import com.example.demo.service.LopHocPhongDualWriteService;
import com.example.demo.service.support.PreRegistrationPlanShellMaBuilder;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PreRegistrationPlanSectionsServiceImpl implements IPreRegistrationPlanSectionsService {

    private static final int MAX_SECTIONS = 200;

    private final PreRegistrationIntentRepository intentRepository;
    private final HocKyRepository hocKyRepository;
    private final HocPhanRepository hocPhanRepository;
    private final LopHocPhanRepository lopHocPhanRepository;
    private final LopHocPhongDualWriteService lopHocPhongDualWriteService;
    private final ITkbRevisionService tkbRevisionService;

    @Value("${eduport.prereg.target-class-size-default:40}")
    private int defaultTargetClassSize;

    @Override
    @Transactional
    public PreRegistrationPlanSectionsResponse planSections(PreRegistrationPlanSectionsRequest request, String idempotencyKey) {
        Objects.requireNonNull(request, "request");
        Long hocKyId = request.getHocKyId();
        Long idHocPhan = request.getIdHocPhan();
        if (hocKyId == null || idHocPhan == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "hocKyId và idHocPhan là bắt buộc.");
        }

        HocKy hocKy = hocKyRepository.findById(hocKyId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy học kỳ: " + hocKyId));
        HocPhan hocPhan = hocPhanRepository.findById(idHocPhan)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy học phần: " + idHocPhan));

        int classSize = resolveClassSize(request.getTargetClassSize());
        int siSoToiDa = request.getSiSoToiDa() != null && request.getSiSoToiDa() > 0
                ? request.getSiSoToiDa()
                : classSize;

        Integer explicitCount = request.getSectionCount();
        boolean useExplicit = explicitCount != null && explicitCount > 0;
        boolean useRecommended = !useExplicit
                && !Boolean.FALSE.equals(request.getUseRecommendedFromDemand());

        if (!useExplicit && !useRecommended) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cần sectionCount hoặc bật useRecommendedFromDemand.");
        }

        long totalIntentSnapshot = 0L;
        int recommendedFromDemand = 0;
        int n;

        if (useExplicit) {
            if (explicitCount > MAX_SECTIONS) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "sectionCount vượt quá " + MAX_SECTIONS + ".");
            }
            n = explicitCount;
        } else {
            totalIntentSnapshot = sumIntentForHocPhan(hocKyId, request.getNamNhapHoc(), request.getIdNganh(), idHocPhan);
            if (totalIntentSnapshot <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Không có intent PRE cho học phần và phạm vi lọc đã chọn.");
            }
            recommendedFromDemand = (int) Math.max(1, Math.ceil((double) totalIntentSnapshot / classSize));
            if (recommendedFromDemand > MAX_SECTIONS) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Số lớp đề xuất (" + recommendedFromDemand + ") vượt giới hạn " + MAX_SECTIONS
                                + "; hãy chia nhỏ phạm vi (cohort/ngành) hoặc tăng targetClassSize.");
            }
            n = recommendedFromDemand;
        }

        String digest8 = digest8(
                idempotencyKey,
                hocKyId,
                idHocPhan,
                request.getNamNhapHoc(),
                request.getIdNganh(),
                classSize,
                useExplicit,
                explicitCount,
                siSoToiDa);

        String maHp = hocPhan.getMaHocPhan();
        List<String> codes = new ArrayList<>(n);
        for (int idx = 1; idx <= n; idx++) {
            codes.add(PreRegistrationPlanShellMaBuilder.build(maHp, digest8, idx));
        }

        int existing = 0;
        List<Long> existingIds = new ArrayList<>();
        for (String ma : codes) {
            if (lopHocPhanRepository.existsByMaLopHp(ma)) {
                existing++;
                lopHocPhanRepository.findByMaLopHp(ma)
                        .ifPresent(l -> existingIds.add(l.getIdLopHp()));
            }
        }

        if (existing > 0 && existing < n) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Một phần mã lớp PRE plan đã tồn tại (" + existing + "/" + n + "). "
                            + "Xóa shell trùng hoặc đổi Idempotency-Key / tham số.");
        }

        if (existing == n) {
            return PreRegistrationPlanSectionsResponse.builder()
                    .hocKyId(hocKy.getIdHocKy())
                    .idHocPhan(hocPhan.getIdHocPhan())
                    .maHocPhan(maHp)
                    .sectionCountPlanned(n)
                    .totalIntentSnapshot(totalIntentSnapshot)
                    .recommendedFromDemand(recommendedFromDemand)
                    .createdCount(0)
                    .skippedExistingMaCount(n)
                    .createdLopHpIds(existingIds)
                    .idempotentReplay(true)
                    .build();
        }

        List<Long> createdIds = new ArrayList<>();
        int created = 0;
        for (int idx = 1; idx <= n; idx++) {
            String ma = codes.get(idx - 1);
            LopHocPhan shell = LopHocPhan.builder()
                    .maLopHp(ma)
                    .hocPhan(hocPhan)
                    .hocKy(hocKy)
                    .giangVien(null)
                    .phongHoc(null)
                    .tkbBlock(null)
                    .siSoToiDa(siSoToiDa)
                    .siSoThucTe(0)
                    .hocPhi(null)
                    .trangThai("CHUA_MO")
                    .statusPublish(LopHocPhanPublishStatus.SHELL)
                    .thoiKhoaBieuJson(List.of())
                    .build();
            lopHocPhongDualWriteService.synchronize(shell);
            LopHocPhan saved = lopHocPhanRepository.save(shell);
            createdIds.add(saved.getIdLopHp());
            created++;
        }

        if (created > 0) {
            tkbRevisionService.bumpAfterTkbMutation(hocKyId);
        }

        return PreRegistrationPlanSectionsResponse.builder()
                .hocKyId(hocKy.getIdHocKy())
                .idHocPhan(hocPhan.getIdHocPhan())
                .maHocPhan(maHp)
                .sectionCountPlanned(n)
                .totalIntentSnapshot(totalIntentSnapshot)
                .recommendedFromDemand(recommendedFromDemand)
                .createdCount(created)
                .skippedExistingMaCount(0)
                .createdLopHpIds(createdIds)
                .idempotentReplay(false)
                .build();
    }

    private int resolveClassSize(Integer targetClassSize) {
        if (targetClassSize != null && targetClassSize > 0) {
            return targetClassSize;
        }
        return defaultTargetClassSize;
    }

    private long sumIntentForHocPhan(Long hocKyId, Integer namNhapHoc, Long idNganh, Long idHocPhan) {
        List<Object[]> rows = intentRepository.aggregateDemand(hocKyId, namNhapHoc, idNganh);
        long sum = 0L;
        for (Object[] r : rows) {
            long hpId = ((Number) r[0]).longValue();
            if (hpId == idHocPhan) {
                sum += ((Number) r[7]).longValue();
            }
        }
        return sum;
    }

    private static String digest8(String idempotencyKey,
                                  long hocKyId,
                                  long idHocPhan,
                                  Integer namNhapHoc,
                                  Long idNganh,
                                  int classSize,
                                  boolean useExplicit,
                                  Integer explicitCount,
                                  int siSoToiDa) {
        String raw = (idempotencyKey == null ? "" : idempotencyKey.trim())
                + "|" + hocKyId
                + "|" + idHocPhan
                + "|" + namNhapHoc
                + "|" + idNganh
                + "|" + classSize
                + "|" + useExplicit
                + "|" + (explicitCount == null ? -1 : explicitCount)
                + "|" + siSoToiDa;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(8);
            for (int i = 0; i < 4; i++) {
                sb.append(String.format("%02x", d[i]));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256", e);
        }
    }
}
