package com.example.demo.service.impl;

import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.entity.NganhDaoTao;
import com.example.demo.domain.entity.RegistrationCampaign;
import com.example.demo.domain.entity.RegistrationWindow;
import com.example.demo.domain.enums.RegistrationPhase;
import com.example.demo.payload.request.RegistrationCampaignRequest;
import com.example.demo.payload.response.RegistrationCampaignResponse;
import com.example.demo.repository.HocKyRepository;
import com.example.demo.repository.NganhDaoTaoRepository;
import com.example.demo.repository.RegistrationCampaignRepository;
import com.example.demo.repository.RegistrationWindowRepository;
import com.example.demo.service.IRegistrationCampaignService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Triển khai chiến dịch đăng ký theo khóa.
 *
 * <p>Khi tạo campaign, hệ thống tự động tạo {@link RegistrationWindow} cho
 * từng {@link HocKy} mà khóa sinh viên có thể đăng ký.
 *
 * <p>Logic chọn HK:
 * <ul>
 *   <li>Gom nhóm HK theo năm học (vd: "2024-2025").</li>
 *   <li>Tính năm học hiện tại của SV: currentYear - cohortYear + 1.</li>
 *   <li>Chỉ tạo windows cho HK thuộc năm học hiện tại hoặc năm học tiếp theo.</li>
 * </ul>
 *
 * <p>Backward-compatible: campaign là optional. Admin vẫn tạo
 * {@code RegistrationWindow} thủ công không qua campaign.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationCampaignServiceImpl implements IRegistrationCampaignService {

    private final RegistrationCampaignRepository campaignRepository;
    private final RegistrationWindowRepository windowRepository;
    private final HocKyRepository hocKyRepository;
    private final NganhDaoTaoRepository nganhDaoTaoRepository;

    // Khoang thoi gian toi da de mot cohort con co the dang ky (nam).
    private static final int MAX_PROGRAM_YEARS = 6;

    @Override
    @Transactional
    public RegistrationCampaignResponse create(RegistrationCampaignRequest request, String createdBy) {
        validateCampaignRequest(request);

        if (campaignRepository.existsByNamNhapHocAndPhase(request.getNamNhapHoc(), request.getPhase())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Da ton tai chiến dịch cho khóa K" + request.getNamNhapHoc() + " / "
                            + request.getPhase() + ". Vui long sua hoac xoa chiến dịch cu.");
        }

        // Tao campaign
        RegistrationCampaign campaign = RegistrationCampaign.builder()
                .tenCampaign(request.getTenCampaign())
                .namNhapHoc(request.getNamNhapHoc())
                .phase(request.getPhase())
                .openAt(request.getOpenAt())
                .closeAt(request.getCloseAt())
                .ghiChu(request.getGhiChu())
                .createdBy(createdBy)
                .build();
        campaign = campaignRepository.save(campaign);
        log.info("CAMPAIGN created id={} khoa={} phase={}", campaign.getId(), campaign.getNamNhapHoc(), campaign.getPhase());

        // Auto-generate windows
        List<RegistrationWindow> windows = generateWindowsForCampaign(campaign);
        windows = windowRepository.saveAll(windows);
        log.info("CAMPAIGN id={} auto-generated {} windows", campaign.getId(), windows.size());

        return toResponse(campaign, windows);
    }

    @Override
    @Transactional
    public RegistrationCampaignResponse update(Long id, RegistrationCampaignRequest request) {
        validateCampaignRequest(request);
        RegistrationCampaign campaign = findOrThrow(id);

        // Kiem tra scope khong doi (khong doi duoc namNhapHoc hoac phase)
        if (!campaign.getNamNhapHoc().equals(request.getNamNhapHoc())
                || campaign.getPhase() != request.getPhase()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Khong the doi namNhapHoc hoac phase cua chiến dịch da ton tai.");
        }

        campaign.setTenCampaign(request.getTenCampaign());
        campaign.setOpenAt(request.getOpenAt());
        campaign.setCloseAt(request.getCloseAt());
        campaign.setGhiChu(request.getGhiChu());
        campaign = campaignRepository.save(campaign);

        // Dong bo tat ca windows thuoc campaign
        List<RegistrationWindow> existingWindows = windowRepository.findByCampaignId(id);
        for (RegistrationWindow w : existingWindows) {
            w.setOpenAt(request.getOpenAt());
            w.setCloseAt(request.getCloseAt());
        }
        windowRepository.saveAll(existingWindows);

        log.info("CAMPAIGN updated id={}, synced {} windows", id, existingWindows.size());
        return toResponse(campaign, existingWindows);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!campaignRepository.existsById(id)) {
            throw new EntityNotFoundException("Khong tim thay chiến dịch: " + id);
        }
        campaignRepository.deleteById(id);
        log.info("CAMPAIGN deleted id={} (windows auto-removed via orphanRemoval)", id);
    }

    @Override
    @Transactional(readOnly = true)
    public RegistrationCampaignResponse getById(Long id) {
        RegistrationCampaign campaign = findOrThrow(id);
        List<RegistrationWindow> windows = windowRepository.findByCampaignId(id);
        return toResponse(campaign, windows);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrationCampaignResponse> listAll() {
        List<RegistrationCampaign> campaigns = campaignRepository.findAllByOrderByNamNhapHocDescPhaseAsc();
        Map<Long, List<RegistrationWindow>> windowsByCampaign = windowRepository
                .findAllByCampaignIds(campaigns.stream().map(RegistrationCampaign::getId).toList())
                .stream()
                .collect(Collectors.groupingBy(w -> w.getCampaign().getId()));
        return campaigns.stream()
                .map(c -> toResponse(c, windowsByCampaign.getOrDefault(c.getId(), List.of())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrationCampaignResponse> listActive() {
        List<RegistrationCampaign> active = campaignRepository.findAllActive();
        Map<Long, List<RegistrationWindow>> windowsByCampaign = windowRepository
                .findAllByCampaignIds(active.stream().map(RegistrationCampaign::getId).toList())
                .stream()
                .collect(Collectors.groupingBy(w -> w.getCampaign().getId()));
        return active.stream()
                .map(c -> toResponse(c, windowsByCampaign.getOrDefault(c.getId(), List.of())))
                .toList();
    }

    // -------- Core logic --------

    /**
     * Sinh danh sach RegistrationWindow cho campaign.
     *
     * <p>Chon cac HocKy:
     * - Lay nam hoc hien tai cua HK (nam bat dau cua namHoc, vd "2024-2025" -> 2024).
     * - Tinh nam hoc hien tai cua cohort = currentYear - cohortYear + 1.
     * - Chi tao windows cho HK nam trong khoang [cohortYear, cohortYear + MAX_PROGRAM_YEARS].
     * - Uu tien HK cua nam hoc hien tai + nam hoc tiep theo.
     */
    private List<RegistrationWindow> generateWindowsForCampaign(RegistrationCampaign campaign) {
        Instant now = Instant.now();

        // Lay nam hien tai (nam bat dau cua nam hoc)
        int currentYear = now.atZone(java.time.ZoneId.systemDefault()).getYear();
        int cohortYear = campaign.getNamNhapHoc();

        // Lay tat ca HK co namHoc >= cohortYear
        List<HocKy> allHocKys = hocKyRepository.findAll();
        List<HocKy> eligibleHocKys = allHocKys.stream()
                .filter(hk -> {
                    String namHoc = hk.getNamHoc();
                    if (namHoc == null) return false;
                    int startYear = parseNamHocStartYear(namHoc);
                    // Chi lay HK thuoc nam hoc >= cohortYear va <= cohortYear + MAX
                    return startYear >= cohortYear && startYear <= cohortYear + MAX_PROGRAM_YEARS;
                })
                .sorted((a, b) -> {
                    int cmp = a.getNamHoc().compareTo(b.getNamHoc());
                    return cmp != 0 ? cmp : Integer.compare(a.getKyThu(), b.getKyThu());
                })
                .toList();

        if (eligibleHocKys.isEmpty()) {
            log.warn("CAMPAIGN id={}: khong tim thay HocKy nao phu hop cho cohort {}",
                    campaign.getId(), cohortYear);
            return List.of();
        }

        // Loc: uu tien HK nam trong [currentYear, currentYear+1]
        List<HocKy> prioritized = new ArrayList<>();
        List<HocKy> others = new ArrayList<>();
        for (HocKy hk : eligibleHocKys) {
            int startYear = parseNamHocStartYear(hk.getNamHoc());
            if (startYear >= currentYear && startYear <= currentYear + 1) {
                prioritized.add(hk);
            } else {
                others.add(hk);
            }
        }

        List<HocKy> toInclude = new ArrayList<>(prioritized);
        if (toInclude.isEmpty()) {
            // Neu khong co HK nao trong nam hien tai, lay HK gan nhat
            toInclude.add(eligibleHocKys.get(0));
        }

        // Tao RegistrationWindow cho moi HK
        List<RegistrationWindow> windows = new ArrayList<>();
        for (HocKy hk : toInclude) {
            RegistrationWindow w = RegistrationWindow.builder()
                    .hocKy(hk)
                    .campaign(campaign)
                    .phase(campaign.getPhase())
                    .namNhapHoc(campaign.getNamNhapHoc())
                    .openAt(campaign.getOpenAt())
                    .closeAt(campaign.getCloseAt())
                    .ghiChu("Tu dong tao boi campaign: " + campaign.getTenCampaign())
                    .createdBy(campaign.getCreatedBy())
                    .build();
            windows.add(w);
        }

        return windows;
    }

    private static int parseNamHocStartYear(String namHoc) {
        if (namHoc == null || namHoc.isBlank()) return 0;
        String[] parts = namHoc.split("-");
        if (parts.length > 0) {
            try {
                return Integer.parseInt(parts[0].trim());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    private void validateCampaignRequest(RegistrationCampaignRequest req) {
        if (req.getOpenAt() == null || req.getCloseAt() == null) {
            throw new IllegalArgumentException("openAt và closeAt khong duoc rong.");
        }
        if (!req.getOpenAt().isBefore(req.getCloseAt())) {
            throw new IllegalArgumentException("openAt phai truoc closeAt.");
        }
        if (req.getNamNhapHoc() == null || req.getNamNhapHoc() < 2000 || req.getNamNhapHoc() > 2100) {
            throw new IllegalArgumentException("namNhapHoc phai la nam day du (YYYY), vd 2021.");
        }
    }

    private RegistrationCampaign findOrThrow(Long id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Khong tim thay chiến dịch: " + id));
    }

    private RegistrationCampaignResponse toResponse(RegistrationCampaign c, List<RegistrationWindow> windows) {
        Instant now = Instant.now();
        return RegistrationCampaignResponse.builder()
                .id(c.getId())
                .tenCampaign(c.getTenCampaign())
                .namNhapHoc(c.getNamNhapHoc())
                .phase(c.getPhase())
                .openAt(c.getOpenAt())
                .closeAt(c.getCloseAt())
                .ghiChu(c.getGhiChu())
                .dangMo(!now.isBefore(c.getOpenAt()) && !now.isAfter(c.getCloseAt()))
                .createdBy(c.getCreatedBy())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .windows(windows.stream().map(this::toWindowSummary).toList())
                .build();
    }

    private RegistrationCampaignResponse.WindowSummary toWindowSummary(RegistrationWindow w) {
        HocKy hk = w.getHocKy();
        Instant now = Instant.now();
        return RegistrationCampaignResponse.WindowSummary.builder()
                .id(w.getId())
                .idHocKy(hk != null ? hk.getIdHocKy() : null)
                .tenHocKy(hk != null ? "HK" + hk.getKyThu() + " " + hk.getNamHoc() : null)
                .openAt(w.getOpenAt())
                .closeAt(w.getCloseAt())
                .dangMo(!now.isBefore(w.getOpenAt()) && !now.isAfter(w.getCloseAt()))
                .build();
    }
}
