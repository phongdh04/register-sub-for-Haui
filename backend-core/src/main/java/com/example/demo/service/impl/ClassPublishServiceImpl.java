package com.example.demo.service.impl;

import com.example.demo.domain.entity.GiangVien;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.enums.LopHocPhanPublishStatus;
import com.example.demo.payload.request.LopHocPhanAssignGiangVienRequest;
import com.example.demo.payload.response.LopHocPhanBulkPublishResponse;
import com.example.demo.payload.response.LopHocPhanPublishResponse;
import com.example.demo.repository.GiangVienRepository;
import com.example.demo.repository.LopHocPhanRepository;
import com.example.demo.service.IClassPublishService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * Sprint 3 — Workflow công bố lớp học phần.
 *
 * <p>Quy tắc transition:
 * <ul>
 *   <li>SHELL → SCHEDULED: khi có cả {@code giangVien} + {@code thoiKhoaBieuJson}.</li>
 *   <li>SCHEDULED → PUBLISHED: qua endpoint publish (đã đủ điều kiện).</li>
 *   <li>Không cho lùi (PUBLISHED → SCHEDULED) ở Sprint 3 (sẽ mở rộng nếu có yêu cầu hoãn).</li>
 * </ul>
 *
 * <p>Validation guard ở publish: nếu thiếu GV hoặc lịch → từ chối với HTTP 422.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassPublishServiceImpl implements IClassPublishService {

    private final LopHocPhanRepository lopHocPhanRepository;
    private final GiangVienRepository giangVienRepository;

    @Override
    @Transactional
    public LopHocPhanPublishResponse assignGiangVien(Long idLopHp, LopHocPhanAssignGiangVienRequest request) {
        LopHocPhan lhp = findOrThrow(idLopHp);
        if (lhp.getStatusPublish() == LopHocPhanPublishStatus.PUBLISHED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Lớp đã PUBLISHED, không cho phép thay giảng viên ở Sprint 3.");
        }
        GiangVien gv = giangVienRepository.findById(request.getIdGiangVien())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Không tìm thấy giảng viên: " + request.getIdGiangVien()));
        lhp.setGiangVien(gv);

        // Auto-promote sang SCHEDULED nếu đã có lịch
        if (hasSchedule(lhp) && lhp.getStatusPublish() == LopHocPhanPublishStatus.SHELL) {
            lhp.setStatusPublish(LopHocPhanPublishStatus.SCHEDULED);
        }
        LopHocPhan saved = lopHocPhanRepository.save(lhp);
        return toResponse(saved, "Đã gán giảng viên" + (saved.getStatusPublish() == LopHocPhanPublishStatus.SCHEDULED
                ? " (auto-promote SCHEDULED)" : ""));
    }

    @Override
    @Transactional
    public LopHocPhanPublishResponse assignSchedule(Long idLopHp, java.util.List<java.util.Map<String, Object>> thoiKhoaBieu) {
        LopHocPhan lhp = findOrThrow(idLopHp);
        if (lhp.getStatusPublish() == LopHocPhanPublishStatus.PUBLISHED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Lớp đã PUBLISHED, không cho phép sửa lịch ở Sprint 3.");
        }
        if (thoiKhoaBieu == null || thoiKhoaBieu.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Danh sách thời khóa biểu không được rỗng.");
        }
        lhp.setThoiKhoaBieuJson(thoiKhoaBieu);

        // Auto-promote sang SCHEDULED nếu đã có giảng viên
        if (lhp.getGiangVien() != null && lhp.getStatusPublish() == LopHocPhanPublishStatus.SHELL) {
            lhp.setStatusPublish(LopHocPhanPublishStatus.SCHEDULED);
        }
        LopHocPhan saved = lopHocPhanRepository.save(lhp);
        log.info("📅 ASSIGN-SCHEDULE lhp={} maLopHp={} slots={}", saved.getIdLopHp(), saved.getMaLopHp(), thoiKhoaBieu.size());
        return toResponse(saved, "Đã gán lịch học" + (saved.getStatusPublish() == LopHocPhanPublishStatus.SCHEDULED
                ? " (auto-promote SCHEDULED)" : ""));
    }

    @Override
    @Transactional
    public LopHocPhanPublishResponse publish(Long idLopHp) {
        LopHocPhan lhp = findOrThrow(idLopHp);
        if (lhp.getStatusPublish() == LopHocPhanPublishStatus.PUBLISHED) {
            return toResponse(lhp, "Lớp đã PUBLISHED trước đó");
        }
        ensurePublishable(lhp);
        lhp.setStatusPublish(LopHocPhanPublishStatus.PUBLISHED);
        // B3 fix: publish phải mở lớp nếu đang CHUA_MO (để FE filter trangThai=DANG_MO thấy được)
        if ("CHUA_MO".equals(lhp.getTrangThai())) {
            lhp.setTrangThai("DANG_MO");
        }
        LopHocPhan saved = lopHocPhanRepository.save(lhp);
        log.info("✅ PUBLISH lhp={} maLopHp={}", saved.getIdLopHp(), saved.getMaLopHp());
        return toResponse(saved, "Đã PUBLISHED");
    }

    @Override
    @Transactional
    public LopHocPhanBulkPublishResponse bulkPublish(Long idHocKy) {
        if (idHocKy == null) {
            throw new IllegalArgumentException("idHocKy là bắt buộc.");
        }
        List<LopHocPhan> candidates = lopHocPhanRepository
                .findByHocKyAndStatusPublish(idHocKy, LopHocPhanPublishStatus.SCHEDULED);

        List<Long> publishedIds = new ArrayList<>();
        List<LopHocPhanBulkPublishResponse.SkippedLopHocPhan> skipped = new ArrayList<>();

        for (LopHocPhan lhp : candidates) {
            try {
                ensurePublishable(lhp);
                lhp.setStatusPublish(LopHocPhanPublishStatus.PUBLISHED);
                // B3 fix: bulk publish cũng đổi trangThai sang DANG_MO
                if ("CHUA_MO".equals(lhp.getTrangThai())) {
                    lhp.setTrangThai("DANG_MO");
                }
                lopHocPhanRepository.save(lhp);
                publishedIds.add(lhp.getIdLopHp());
            } catch (ResponseStatusException ex) {
                skipped.add(LopHocPhanBulkPublishResponse.SkippedLopHocPhan.builder()
                        .idLopHp(lhp.getIdLopHp())
                        .maLopHp(lhp.getMaLopHp())
                        .reason(ex.getReason())
                        .build());
            }
        }

        log.info("📦 BULK-PUBLISH hocKy={} requested={} published={} skipped={}",
                idHocKy, candidates.size(), publishedIds.size(), skipped.size());

        return LopHocPhanBulkPublishResponse.builder()
                .idHocKy(idHocKy)
                .totalRequested(candidates.size())
                .publishedCount(publishedIds.size())
                .skippedCount(skipped.size())
                .publishedIds(publishedIds)
                .skipped(skipped)
                .build();
    }

    @Override
    @Transactional
    public LopHocPhanBulkPublishResponse forcePublishAll(Long idHocKy) {
        if (idHocKy == null) {
            throw new IllegalArgumentException("idHocKy là bắt buộc.");
        }
        // Lấy toàn bộ lớp của học kỳ (mọi statusPublish, kể cả SHELL)
        List<LopHocPhan> all = lopHocPhanRepository.findByHocKy_IdHocKy(idHocKy);
        List<Long> publishedIds = new ArrayList<>();
        for (LopHocPhan lhp : all) {
            lhp.setStatusPublish(LopHocPhanPublishStatus.PUBLISHED);
            if (!"DANG_MO".equals(lhp.getTrangThai())) {
                lhp.setTrangThai("DANG_MO");
            }
            lopHocPhanRepository.save(lhp);
            publishedIds.add(lhp.getIdLopHp());
        }
        log.info("🔓 FORCE-PUBLISH-ALL hocKy={} count={}", idHocKy, publishedIds.size());
        return LopHocPhanBulkPublishResponse.builder()
                .idHocKy(idHocKy)
                .totalRequested(all.size())
                .publishedCount(publishedIds.size())
                .skippedCount(0)
                .publishedIds(publishedIds)
                .skipped(new ArrayList<>())
                .build();
    }

    // -- Helpers --

    private LopHocPhan findOrThrow(Long id) {
        return lopHocPhanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lớp học phần: " + id));
    }

    private static boolean hasSchedule(LopHocPhan lhp) {
        return lhp.getThoiKhoaBieuJson() != null && !lhp.getThoiKhoaBieuJson().isEmpty();
    }

    private static void ensurePublishable(LopHocPhan lhp) {
        if (lhp.getGiangVien() == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Lớp chưa có giảng viên — không thể publish.");
        }
        if (!hasSchedule(lhp)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Lớp chưa có thời khóa biểu — không thể publish.");
        }
        if (lhp.getStatusPublish() == LopHocPhanPublishStatus.SHELL) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Lớp đang SHELL — cần gán GV + xếp lịch trước khi publish.");
        }
    }

    private static LopHocPhanPublishResponse toResponse(LopHocPhan lhp, String message) {
        return LopHocPhanPublishResponse.builder()
                .idLopHp(lhp.getIdLopHp())
                .maLopHp(lhp.getMaLopHp())
                .idHocKy(lhp.getHocKy() != null ? lhp.getHocKy().getIdHocKy() : null)
                .maHocPhan(lhp.getHocPhan() != null ? lhp.getHocPhan().getMaHocPhan() : null)
                .tenHocPhan(lhp.getHocPhan() != null ? lhp.getHocPhan().getTenHocPhan() : null)
                .idGiangVien(lhp.getGiangVien() != null ? lhp.getGiangVien().getIdGiangVien() : null)
                .tenGiangVien(lhp.getGiangVien() != null ? lhp.getGiangVien().getTenGiangVien() : null)
                .hasSchedule(hasSchedule(lhp))
                .statusPublish(lhp.getStatusPublish())
                .version(lhp.getVersion())
                .message(message)
                .build();
    }
}
