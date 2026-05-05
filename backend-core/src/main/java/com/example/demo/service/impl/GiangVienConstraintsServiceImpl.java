package com.example.demo.service.impl;

import com.example.demo.domain.entity.GiangVien;
import com.example.demo.domain.entity.GvBusySlot;
import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.enums.GvBusyLoai;
import com.example.demo.payload.request.GvBusySlotUpsertRequest;
import com.example.demo.payload.response.GiangVienConstraintsResponse;
import com.example.demo.payload.response.GvBusySlotResponse;
import com.example.demo.repository.GiangVienRepository;
import com.example.demo.repository.GvBusySlotRepository;
import com.example.demo.repository.HocKyRepository;
import com.example.demo.service.IGiangVienConstraintsService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GiangVienConstraintsServiceImpl implements IGiangVienConstraintsService {

    private final GiangVienRepository giangVienRepository;
    private final HocKyRepository hocKyRepository;
    private final GvBusySlotRepository gvBusySlotRepository;

    @Override
    @Transactional(readOnly = true)
    public GiangVienConstraintsResponse getConstraints(Long giangVienId, Long hocKyFilter) {
        validateGiangVienExists(giangVienId);
        List<GvBusySlot> slots;
        if (hocKyFilter == null) {
            slots = gvBusySlotRepository.findByGiangVien_IdGiangVienOrderByThuAscTietBdAsc(giangVienId);
        } else {
            throwIfMissingHocKy(hocKyFilter);
            slots = gvBusySlotRepository.findForSchedulingView(giangVienId, hocKyFilter);
        }
        List<GvBusySlotResponse> out = slots.stream().map(this::toResponse).toList();
        return GiangVienConstraintsResponse.builder()
                .giangVienId(giangVienId)
                .busySlots(out)
                .extended(null)
                .build();
    }

    @Override
    @Transactional
    public GvBusySlotResponse createBusySlot(Long giangVienId, GvBusySlotUpsertRequest request) {
        GiangVien gv = giangVienRepository.findById(giangVienId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy giảng viên: " + giangVienId));

        validateTiet(request.getTietBd(), request.getTietKt());
        HocKy hk = resolveHocKy(request.getHocKyId());

        GvBusySlot slot = GvBusySlot.builder()
                .giangVien(gv)
                .hocKy(hk)
                .thu(request.getThu())
                .tietBd(request.getTietBd())
                .tietKt(request.getTietKt())
                .loai(request.getLoai() != null ? request.getLoai() : GvBusyLoai.HARD)
                .lyDo(request.getLyDo())
                .ngayBd(request.getNgayBd())
                .ngayKt(request.getNgayKt())
                .build();

        assertNoOverlappingHard(slot, null);
        return toResponse(gvBusySlotRepository.save(slot));
    }

    @Override
    @Transactional
    public GvBusySlotResponse updateBusySlot(Long giangVienId, Long slotId, GvBusySlotUpsertRequest request) {
        GvBusySlot slot = gvBusySlotRepository.findById(slotId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy busy slot: " + slotId));
        if (!slot.getGiangVien().getIdGiangVien().equals(giangVienId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Busy slot không thuộc giảng viên này.");
        }

        validateTiet(request.getTietBd(), request.getTietKt());

        slot.setHocKy(resolveHocKy(request.getHocKyId()));
        slot.setThu(request.getThu());
        slot.setTietBd(request.getTietBd());
        slot.setTietKt(request.getTietKt());
        if (request.getLoai() != null) {
            slot.setLoai(request.getLoai());
        }
        slot.setLyDo(request.getLyDo());
        slot.setNgayBd(request.getNgayBd());
        slot.setNgayKt(request.getNgayKt());

        assertNoOverlappingHard(slot, slotId);
        return toResponse(gvBusySlotRepository.save(slot));
    }

    @Override
    @Transactional
    public void deleteBusySlot(Long giangVienId, Long slotId) {
        GvBusySlot slot = gvBusySlotRepository.findById(slotId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy busy slot: " + slotId));
        if (!slot.getGiangVien().getIdGiangVien().equals(giangVienId)) {
            throw new EntityNotFoundException("Không tìm thấy busy slot của giảng viên này.");
        }
        gvBusySlotRepository.delete(slot);
    }

    private void validateGiangVienExists(Long id) {
        giangVienRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy giảng viên: " + id));
    }

    private void throwIfMissingHocKy(Long id) {
        if (!hocKyRepository.existsById(id)) {
            throw new EntityNotFoundException("Không tìm thấy học kỳ: " + id);
        }
    }

    /** NULL id → entity null (global busy). Id không null → FK hoặc 404. */
    private HocKy resolveHocKy(Long hocKyId) {
        if (hocKyId == null) {
            return null;
        }
        return hocKyRepository.findById(hocKyId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy học kỳ: " + hocKyId));
    }

    private void validateTiet(short bd, short kt) {
        if (bd <= 0 || kt <= 0 || bd > kt) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tiết không hợp lệ (bd/kt phải > 0 và bd ≤ kt).");
        }
    }

    private void assertNoOverlappingHard(GvBusySlot cand, Long excludeId) {
        if (cand.getLoai() != GvBusyLoai.HARD) {
            return;
        }
        List<GvBusySlot> hard = gvBusySlotRepository.findByGiangVien_IdGiangVienAndLoaiOrderByThuAscTietBdAsc(
                cand.getGiangVien().getIdGiangVien(),
                GvBusyLoai.HARD);
        for (GvBusySlot o : hard) {
            if (excludeId != null && o.getId().equals(excludeId)) {
                continue;
            }
            if (!sameHocKyScope(o, cand)) {
                continue;
            }
            if (!o.getThu().equals(cand.getThu())) {
                continue;
            }
            if (datesFullySpecifiedDisjoint(o, cand)) {
                continue;
            }
            if (overlapTiet(o.getTietBd(), o.getTietKt(), cand.getTietBd(), cand.getTietKt())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Đã có busy HARD trùng phạm vi học kỳ + thứ + tiết: " + o.getThu() + "." + " (" + excludeLabel(o)
                                + ")");
            }
        }
    }

    private static String excludeLabel(GvBusySlot o) {
        return "Tiết " + o.getTietBd() + "–" + o.getTietKt();
    }

    /** Global-null kẹp HK hoặc cùng id cụ thể → xung đột tiềm ẩn trong xếp lịch HK đó. */
    private boolean sameHocKyScope(GvBusySlot a, GvBusySlot b) {
        if (a.getHocKy() == null || b.getHocKy() == null) {
            return true;
        }
        return a.getHocKy().getIdHocKy().equals(b.getHocKy().getIdHocKy());
    }

    /** Cả hai có khoảng ngày đầy đủ và không giao nhau → không coi là trùng slot. */
    private static boolean datesFullySpecifiedDisjoint(GvBusySlot a, GvBusySlot b) {
        if (a.getNgayBd() == null || a.getNgayKt() == null || b.getNgayBd() == null || b.getNgayKt() == null) {
            return false;
        }
        return a.getNgayKt().isBefore(b.getNgayBd()) || b.getNgayKt().isBefore(a.getNgayBd());
    }

    private static boolean overlapTiet(short bd1, short kt1, short bd2, short kt2) {
        return bd1 <= kt2 && bd2 <= kt1;
    }

    private GvBusySlotResponse toResponse(GvBusySlot e) {
        Long hk = e.getHocKy() == null ? null : e.getHocKy().getIdHocKy();
        return GvBusySlotResponse.builder()
                .id(e.getId())
                .hocKyId(hk)
                .thu(e.getThu())
                .tietBd(e.getTietBd())
                .tietKt(e.getTietKt())
                .loai(e.getLoai())
                .lyDo(e.getLyDo())
                .ngayBd(e.getNgayBd())
                .ngayKt(e.getNgayKt())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
