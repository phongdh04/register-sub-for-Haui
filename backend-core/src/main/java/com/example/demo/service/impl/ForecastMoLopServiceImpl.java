package com.example.demo.service.impl;

import com.example.demo.domain.entity.ChuongTrinhDaoTao;
import com.example.demo.domain.entity.CtdtHocPhan;
import com.example.demo.domain.entity.DuBaoMoLopLine;
import com.example.demo.domain.entity.DuBaoMoLopVersion;
import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.entity.HocPhan;
import com.example.demo.payload.request.ForecastMoLopRunRequest;
import com.example.demo.payload.response.ForecastMoLopLineItemResponse;
import com.example.demo.payload.response.ForecastMoLopRunResponse;
import com.example.demo.repository.BangDiemMonRepository;
import com.example.demo.repository.ChuongTrinhDaoTaoRepository;
import com.example.demo.repository.CtdtHocPhanRepository;
import com.example.demo.repository.DuBaoMoLopLineRepository;
import com.example.demo.repository.DuBaoMoLopVersionRepository;
import com.example.demo.repository.HocKyRepository;
import com.example.demo.repository.SinhVienRepository;
import com.example.demo.service.IForecastMoLopService;
import com.example.demo.service.support.DuBaoMoLopCalculator;
import com.example.demo.service.support.HocKyMoLopForecastUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ForecastMoLopServiceImpl implements IForecastMoLopService {

    private final HocKyRepository hocKyRepository;
    private final ChuongTrinhDaoTaoRepository chuongTrinhDaoTaoRepository;
    private final CtdtHocPhanRepository ctdtHocPhanRepository;
    private final SinhVienRepository sinhVienRepository;
    private final BangDiemMonRepository bangDiemMonRepository;
    private final DuBaoMoLopVersionRepository duBaoMoLopVersionRepository;
    private final DuBaoMoLopLineRepository duBaoMoLopLineRepository;
    private final EntityManager entityManager;

    @Value("${eduport.forecast.si-so-mac-dinh:40}")
    private int defaultSiSo;

    @Value("${eduport.forecast.he-so-du-phong:1.10}")
    private BigDecimal defaultHeSo;

    @Value("${eduport.forecast.ty-le-sv-hoc-lai:0.60}")
    private BigDecimal defaultTyLe;

    @Override
    @Transactional
    public ForecastMoLopRunResponse runForecast(Long hocKyId, ForecastMoLopRunRequest request) {
        HocKy hk = hocKyRepository.findById(hocKyId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy học kỳ: " + hocKyId));
        ChuongTrinhDaoTao ctdt = chuongTrinhDaoTaoRepository.findById(request.getIdChuongTrinhDaoTao())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy CTĐT: " + request.getIdChuongTrinhDaoTao()));

        int namDau = HocKyMoLopForecastUtil.parseNamHocStartYear(hk.getNamHoc());
        int kyThu = HocKyMoLopForecastUtil.kyThuSanitized(hk);

        int siSo = request.getSiSoToiDaMacDinh() != null ? request.getSiSoToiDaMacDinh() : defaultSiSo;
        BigDecimal heSo = request.getHeSoDuPhong() != null ? request.getHeSoDuPhong() : defaultHeSo;
        BigDecimal tyLe = request.getTyLeSvHocLaiThamGia() != null ? request.getTyLeSvHocLaiThamGia() : defaultTyLe;

        DuBaoMoLopVersion version = DuBaoMoLopVersion.builder()
                .hocKy(hk)
                .chuongTrinhDaoTao(ctdt)
                .trangThai("DRAFT")
                .siSoMacDinh(siSo)
                .heSoDuPhong(heSo)
                .tyLeSvHocLai(tyLe)
                .namHocNamKe((short) namDau)
                .kyThuMucTieu(kyThu)
                .lines(new ArrayList<>())
                .build();

        List<CtdtHocPhan> mappings = ctdtHocPhanRepository.findAllByCtdtId(ctdt.getIdCtdt());
        List<DuBaoMoLopLine> built = new ArrayList<>();
        long tongSv = 0;
        long tongSec = 0;

        for (CtdtHocPhan m : mappings) {
            if (!Boolean.TRUE.equals(m.getBatBuoc()) || m.getHocKyGoiY() == null) {
                continue;
            }
            HocPhan hp = m.getHocPhan();
            Long hpId = hp != null ? hp.getIdHocPhan() : null;
            if (hpId == null) {
                continue;
            }
            Integer gCourse = m.getHocKyGoiY();

            long nOn = sinhVienRepository.countSvOnTrackForCtdtAndCourseSemesterSlot(
                    ctdt.getIdCtdt(),
                    namDau,
                    kyThu,
                    gCourse);
            long nRet = bangDiemMonRepository.countDistinctSinhVienRetakeDemandForCourse(hpId);

            int demand = DuBaoMoLopCalculator.demandSv((int) nOn, (int) nRet, tyLe);
            int sections = DuBaoMoLopCalculator.soLopDeXuat(demand, heSo, siSo);

            tongSv += demand;
            tongSec += sections;

            HocPhan hpRef = entityManager.getReference(HocPhan.class, hpId);
            DuBaoMoLopLine line = DuBaoMoLopLine.builder()
                    .duBaoMoLopVersion(version)
                    .hocPhan(hpRef)
                    .hocKyGoiYCtdt(gCourse)
                    .soSvOnTrack((int) nOn)
                    .soSvHocLai((int) nRet)
                    .soSvDuKien(demand)
                    .soLopDeXuat(sections)
                    .build();
            built.add(line);
        }

        version.setLines(built);
        DuBaoMoLopVersion saved = duBaoMoLopVersionRepository.save(version);

        List<ForecastMoLopLineItemResponse> rows = duBaoMoLopLineRepository.findLinesWithHocPhanByVersion(saved.getIdDuBaoVersion())
                .stream()
                .map(this::toDto)
                .toList();

        return ForecastMoLopRunResponse.builder()
                .idDuBaoVersion(saved.getIdDuBaoVersion())
                .hocKyId(hocKyId)
                .idChuongTrinhDaoTao(ctdt.getIdCtdt())
                .trangThai(saved.getTrangThai())
                .createdAt(saved.getCreatedAt())
                .siSoMacDinhUsed(saved.getSiSoMacDinh())
                .heSoDuPhongUsed(saved.getHeSoDuPhong())
                .tyLeSvHocLaiUsed(saved.getTyLeSvHocLai())
                .namHocNamKeStored(saved.getNamHocNamKe())
                .kyThuMucTieuStored(saved.getKyThuMucTieu())
                .lineCount(rows.size())
                .tongSoLopDeXuat(tongSec)
                .tongSoSvDuKien(tongSv)
                .lines(rows)
                .build();
    }

    private ForecastMoLopLineItemResponse toDto(DuBaoMoLopLine l) {
        HocPhan hp = l.getHocPhan();
        return ForecastMoLopLineItemResponse.builder()
                .idHocPhan(hp != null ? hp.getIdHocPhan() : null)
                .maHocPhan(hp != null ? hp.getMaHocPhan() : null)
                .tenHocPhan(hp != null ? hp.getTenHocPhan() : null)
                .hocKyGoiYCtdt(l.getHocKyGoiYCtdt())
                .soSvOnTrack(l.getSoSvOnTrack())
                .soSvHocLai(l.getSoSvHocLai())
                .soSvDuKien(l.getSoSvDuKien())
                .soLopDeXuat(l.getSoLopDeXuat())
                .build();
    }
}
