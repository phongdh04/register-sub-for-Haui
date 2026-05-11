package com.example.demo.service.impl;

import com.example.demo.domain.entity.DuBaoMoLopLine;
import com.example.demo.domain.entity.DuBaoMoLopVersion;
import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.entity.HocPhan;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.enums.LopHocPhanPublishStatus;
import com.example.demo.payload.response.ForecastMoLopVersionStatusResponse;
import com.example.demo.payload.response.ForecastShellSpawnResponse;
import com.example.demo.repository.DuBaoMoLopLineRepository;
import com.example.demo.repository.DuBaoMoLopVersionRepository;
import com.example.demo.repository.LopHocPhanRepository;
import com.example.demo.service.IForecastMoLopWorkflowService;
import com.example.demo.service.ITkbRevisionService;
import com.example.demo.service.LopHocPhongDualWriteService;
import com.example.demo.service.support.ForecastShellMaBuilder;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ForecastMoLopWorkflowServiceImpl implements IForecastMoLopWorkflowService {

    private static final String ST_DRAFT = "DRAFT";
    private static final String ST_APPROVED = "APPROVED";
    private static final String ST_REJECTED = "REJECTED";
    private static final int CREATED_IDS_PREVIEW_LIMIT = 50;

    private final DuBaoMoLopVersionRepository duBaoMoLopVersionRepository;
    private final DuBaoMoLopLineRepository duBaoMoLopLineRepository;
    private final LopHocPhanRepository lopHocPhanRepository;
    private final LopHocPhongDualWriteService lopHocPhongDualWriteService;
    private final ITkbRevisionService tkbRevisionService;

    @Override
    @Transactional
    public ForecastMoLopVersionStatusResponse approve(Long hocKyId, Long versionId) {
        DuBaoMoLopVersion v = loadVersionForHocKyOrThrow(versionId, hocKyId);
        ensureDraft(v.getTrangThai());
        v.setTrangThai(ST_APPROVED);
        return toStatus(duBaoMoLopVersionRepository.save(v));
    }

    @Override
    @Transactional
    public ForecastMoLopVersionStatusResponse reject(Long hocKyId, Long versionId) {
        DuBaoMoLopVersion v = loadVersionForHocKyOrThrow(versionId, hocKyId);
        ensureDraft(v.getTrangThai());
        v.setTrangThai(ST_REJECTED);
        return toStatus(duBaoMoLopVersionRepository.save(v));
    }

    @Override
    @Transactional
    public ForecastShellSpawnResponse spawnShellLhps(Long hocKyId, Long versionId) {
        DuBaoMoLopVersion v = loadVersionForHocKyOrThrow(versionId, hocKyId);
        if (!ST_APPROVED.equals(v.getTrangThai())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Chỉ phiên APPROVED mới sinh shell (trang_thai hiện tại=" + v.getTrangThai() + ")");
        }

        List<DuBaoMoLopLine> lines = duBaoMoLopLineRepository.findLinesWithHocPhanByVersion(versionId);
        HocKy hk = v.getHocKy();

        int created = 0;
        int skipped = 0;
        List<Long> createdIds = new ArrayList<>();

        for (DuBaoMoLopLine line : lines) {
            HocPhan hp = line.getHocPhan();
            if (hp == null) {
                continue;
            }
            int nSections = line.getSoLopDeXuat() == null ? 0 : Math.max(0, line.getSoLopDeXuat());
            for (int idx = 1; idx <= nSections; idx++) {
                String ma = ForecastShellMaBuilder.build(hp.getMaHocPhan(), v.getIdDuBaoVersion(), idx);
                if (lopHocPhanRepository.existsByMaLopHp(ma)) {
                    skipped++;
                    continue;
                }
                LopHocPhan shell = LopHocPhan.builder()
                        .maLopHp(ma)
                        .hocPhan(hp)
                        .hocKy(hk)
                        .giangVien(null)
                        .phongHoc(null)
                        .tkbBlock(null)
                        .siSoToiDa(v.getSiSoMacDinh())
                        .siSoThucTe(0)
                        .hocPhi(null)
                        .trangThai("CHUA_MO")
                        .statusPublish(LopHocPhanPublishStatus.SHELL)
                        .thoiKhoaBieuJson(List.of())
                        .build();
                lopHocPhongDualWriteService.synchronize(shell);
                LopHocPhan saved = lopHocPhanRepository.save(shell);
                created++;
                if (createdIds.size() < CREATED_IDS_PREVIEW_LIMIT) {
                    createdIds.add(saved.getIdLopHp());
                }
            }
        }

        if (created > 0) {
            tkbRevisionService.bumpAfterTkbMutation(hocKyId);
        }

        return ForecastShellSpawnResponse.builder()
                .hocKyId(hocKyId)
                .forecastVersionId(versionId)
                .createdCount(created)
                .skippedExistingMaCount(skipped)
                .createdLopHpIdsPreview(Collections.unmodifiableList(createdIds))
                .build();
    }

    private DuBaoMoLopVersion loadVersionForHocKyOrThrow(Long versionId, Long hocKyId) {
        DuBaoMoLopVersion v = duBaoMoLopVersionRepository.findById(versionId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy phiên dự báo: " + versionId));
        Long vh = v.getHocKy() != null ? v.getHocKy().getIdHocKy() : null;
        if (!Objects.equals(vh, hocKyId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Phiên dự báo không thuộc học kỳ path: version=" + versionId);
        }
        return v;
    }

    private static void ensureDraft(String trangThai) {
        if (!ST_DRAFT.equals(trangThai)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Chỉ phiên DRAFT mới được chuyển trạng thái (hiện tại=" + trangThai + ")");
        }
    }

    private static ForecastMoLopVersionStatusResponse toStatus(DuBaoMoLopVersion v) {
        return ForecastMoLopVersionStatusResponse.builder()
                .idDuBaoVersion(v.getIdDuBaoVersion())
                .hocKyId(v.getHocKy() != null ? v.getHocKy().getIdHocKy() : null)
                .idChuongTrinhDaoTao(
                        v.getChuongTrinhDaoTao() != null ? v.getChuongTrinhDaoTao().getIdCtdt() : null)
                .trangThai(v.getTrangThai())
                .createdAt(v.getCreatedAt())
                .build();
    }
}
