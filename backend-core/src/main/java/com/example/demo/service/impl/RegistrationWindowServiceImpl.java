package com.example.demo.service.impl;

import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.entity.NganhDaoTao;
import com.example.demo.domain.entity.RegistrationWindow;
import com.example.demo.domain.enums.RegistrationPhase;
import com.example.demo.payload.request.RegistrationWindowOpenNowRequest;
import com.example.demo.payload.request.RegistrationWindowUpsertRequest;
import com.example.demo.payload.response.RegistrationWindowResponse;
import com.example.demo.repository.HocKyRepository;
import com.example.demo.repository.NganhDaoTaoRepository;
import com.example.demo.repository.RegistrationWindowRepository;
import com.example.demo.service.IRegistrationWindowService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * SRP: Quản lý vòng đời {@link RegistrationWindow}.
 *
 * <p>Validation:
 * <ul>
 *   <li>{@code openAt} phải trước {@code closeAt}.</li>
 *   <li>Không cho phép trùng (hocKy, phase, namNhapHoc, idNganh).</li>
 *   <li>Window ngành cụ thể chỉ được tạo khi đã có cohort tương ứng (namNhapHoc != null).</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class RegistrationWindowServiceImpl implements IRegistrationWindowService {

    private final RegistrationWindowRepository registrationWindowRepository;
    private final HocKyRepository hocKyRepository;
    private final NganhDaoTaoRepository nganhDaoTaoRepository;

    @Override
    @Transactional
    public RegistrationWindowResponse create(RegistrationWindowUpsertRequest request, String createdBy) {
        validatePayload(request);
        ensureNoDuplicate(request, null);

        HocKy hocKy = loadHocKy(request.getIdHocKy());
        NganhDaoTao nganh = loadNganh(request.getIdNganh());

        RegistrationWindow entity = RegistrationWindow.builder()
                .hocKy(hocKy)
                .phase(request.getPhase())
                .namNhapHoc(request.getNamNhapHoc())
                .nganhDaoTao(nganh)
                .openAt(request.getOpenAt())
                .closeAt(request.getCloseAt())
                .ghiChu(request.getGhiChu())
                .createdBy(createdBy)
                .build();
        return toResponse(registrationWindowRepository.save(entity));
    }

    @Override
    @Transactional
    public RegistrationWindowResponse update(Long id, RegistrationWindowUpsertRequest request) {
        validatePayload(request);
        RegistrationWindow entity = findOrThrow(id);
        ensureNoDuplicate(request, id);

        HocKy hocKy = loadHocKy(request.getIdHocKy());
        NganhDaoTao nganh = loadNganh(request.getIdNganh());

        entity.setHocKy(hocKy);
        entity.setPhase(request.getPhase());
        entity.setNamNhapHoc(request.getNamNhapHoc());
        entity.setNganhDaoTao(nganh);
        entity.setOpenAt(request.getOpenAt());
        entity.setCloseAt(request.getCloseAt());
        entity.setGhiChu(request.getGhiChu());
        return toResponse(registrationWindowRepository.save(entity));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!registrationWindowRepository.existsById(id)) {
            throw new EntityNotFoundException("Không tìm thấy registration_window: " + id);
        }
        registrationWindowRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public RegistrationWindowResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional
    public RegistrationWindowResponse openNow(RegistrationWindowOpenNowRequest request, String createdBy) {
        if (request.getIdHocKy() == null || request.getPhase() == null) {
            throw new IllegalArgumentException("idHocKy va phase la bat buoc.");
        }
        if (request.getNamNhapHoc() == null && request.getIdNganh() != null) {
            throw new IllegalArgumentException(
                    "Khong the cau hinh theo nganh khi chua chi dinh cohort (namNhapHoc).");
        }
        Instant now = Instant.now();
        int days = request.getDurationDays() != null ? request.getDurationDays() : 30;
        Instant closeAt = now.plus(days, ChronoUnit.DAYS);

        HocKy hocKy = loadHocKy(request.getIdHocKy());
        NganhDaoTao nganh = loadNganh(request.getIdNganh());

        Optional<RegistrationWindow> existing = registrationWindowRepository.findExistingScope(
                request.getIdHocKy(), request.getPhase(), request.getNamNhapHoc(), request.getIdNganh());

        RegistrationWindow entity = existing.orElseGet(() -> RegistrationWindow.builder()
                .hocKy(hocKy)
                .phase(request.getPhase())
                .namNhapHoc(request.getNamNhapHoc())
                .nganhDaoTao(nganh)
                .createdBy(createdBy)
                .build());
        entity.setHocKy(hocKy);
        entity.setPhase(request.getPhase());
        entity.setNamNhapHoc(request.getNamNhapHoc());
        entity.setNganhDaoTao(nganh);
        entity.setOpenAt(now);
        entity.setCloseAt(closeAt);
        if (request.getGhiChu() != null && !request.getGhiChu().isBlank()) {
            entity.setGhiChu(request.getGhiChu());
        }
        return toResponse(registrationWindowRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrationWindowResponse> list(Long hocKyId, RegistrationPhase phase) {
        if (hocKyId == null) {
            throw new IllegalArgumentException("hocKyId là bắt buộc.");
        }
        return registrationWindowRepository.findForAdminListing(hocKyId, phase)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // -- Helpers --

    private RegistrationWindow findOrThrow(Long id) {
        return registrationWindowRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy registration_window: " + id));
    }

    private HocKy loadHocKy(Long hocKyId) {
        return hocKyRepository.findById(hocKyId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy học kỳ: " + hocKyId));
    }

    private NganhDaoTao loadNganh(Long nganhId) {
        if (nganhId == null) {
            return null;
        }
        return nganhDaoTaoRepository.findById(nganhId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy ngành: " + nganhId));
    }

    private static void validatePayload(RegistrationWindowUpsertRequest req) {
        if (req.getOpenAt() == null || req.getCloseAt() == null) {
            throw new IllegalArgumentException("openAt và closeAt không được rỗng.");
        }
        if (!req.getOpenAt().isBefore(req.getCloseAt())) {
            throw new IllegalArgumentException("openAt phải trước closeAt.");
        }
        if (req.getNamNhapHoc() != null) {
            int y = req.getNamNhapHoc();
            if (y < 1900 || y > 2100) {
                throw new IllegalArgumentException("Năm nhập học phải theo định dạng YYYY (ví dụ: 2023), không nhập K17/17.");
            }
        }
        if (req.getNamNhapHoc() == null && req.getIdNganh() != null) {
            throw new IllegalArgumentException(
                    "Không thể cấu hình theo ngành khi chưa chỉ định cohort (namNhapHoc).");
        }
    }

    private void ensureNoDuplicate(RegistrationWindowUpsertRequest req, Long excludeId) {
        registrationWindowRepository.findExistingScope(
                        req.getIdHocKy(), req.getPhase(), req.getNamNhapHoc(), req.getIdNganh())
                .ifPresent(existing -> {
                    if (excludeId == null || !Objects.equals(existing.getId(), excludeId)) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT,
                                "Đã tồn tại window cho phạm vi này (hocKy, phase, cohort, ngành).");
                    }
                });
    }

    private RegistrationWindowResponse toResponse(RegistrationWindow w) {
        Instant now = Instant.now();
        boolean dangMo = !now.isBefore(w.getOpenAt()) && !now.isAfter(w.getCloseAt());
        HocKy hk = w.getHocKy();
        NganhDaoTao nganh = w.getNganhDaoTao();
        com.example.demo.domain.entity.RegistrationCampaign campaign = w.getCampaign();
        return RegistrationWindowResponse.builder()
                .id(w.getId())
                .idHocKy(hk != null ? hk.getIdHocKy() : null)
                .tenHocKy(hk != null ? "HK" + hk.getKyThu() + " " + hk.getNamHoc() : null)
                .phase(w.getPhase())
                .namNhapHoc(w.getNamNhapHoc())
                .idNganh(nganh != null ? nganh.getIdNganh() : null)
                .tenNganh(nganh != null ? nganh.getTenNganh() : null)
                .idCampaign(campaign != null ? campaign.getId() : null)
                .tenCampaign(campaign != null ? campaign.getTenCampaign() : null)
                .openAt(w.getOpenAt())
                .closeAt(w.getCloseAt())
                .dangMo(dangMo)
                .ghiChu(w.getGhiChu())
                .createdBy(w.getCreatedBy())
                .createdAt(w.getCreatedAt())
                .updatedAt(w.getUpdatedAt())
                .build();
    }
}
