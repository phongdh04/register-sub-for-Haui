package com.example.demo.service.impl;

import com.example.demo.domain.entity.GiangVien;
import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.entity.HocPhan;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.payload.request.LopHocPhanRequest;
import com.example.demo.payload.response.LopHocPhanResponse;
import com.example.demo.repository.GiangVienRepository;
import com.example.demo.repository.HocKyRepository;
import com.example.demo.repository.HocPhanRepository;
import com.example.demo.repository.LopHocPhanRepository;
import com.example.demo.service.ILopHocPhanService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SRP: Chỉ xử lý nghiệp vụ Lớp Học Phần (mở lớp, phát hành, đóng lớp).
 * DIP: Phụ thuộc vào các Repository qua abstraction interface.
 *
 * Lưu ý: Khi publishLop() được gọi, trong tương lai sẽ trigger
 * warm-up Redis slot (Task P0-Blocker Queue). Hiện tại chỉ đổi trangThai.
 */
@Service
@RequiredArgsConstructor
public class LopHocPhanServiceImpl implements ILopHocPhanService {

    private final LopHocPhanRepository lopHocPhanRepository;
    private final HocPhanRepository hocPhanRepository;
    private final HocKyRepository hocKyRepository;
    private final GiangVienRepository giangVienRepository;

    @Override
    @Transactional(readOnly = true)
    public List<LopHocPhanResponse> getAllByHocKy(Long idHocKy) {
        return lopHocPhanRepository.findByHocKy_IdHocKy(idHocKy)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LopHocPhanResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional
    public LopHocPhanResponse create(LopHocPhanRequest request) {
        if (lopHocPhanRepository.existsByMaLopHp(request.getMaLopHp())) {
            throw new IllegalArgumentException("Mã lớp học phần đã tồn tại: " + request.getMaLopHp());
        }
        HocPhan hocPhan = hocPhanRepository.findById(request.getIdHocPhan())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy học phần ID: " + request.getIdHocPhan()));
        HocKy hocKy = hocKyRepository.findById(request.getIdHocKy())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy học kỳ ID: " + request.getIdHocKy()));
        GiangVien giangVien = null;
        if (request.getIdGiangVien() != null) {
            giangVien = giangVienRepository.findById(request.getIdGiangVien())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy giảng viên ID: " + request.getIdGiangVien()));
        }

        LopHocPhan lhp = LopHocPhan.builder()
                .maLopHp(request.getMaLopHp())
                .hocPhan(hocPhan)
                .hocKy(hocKy)
                .giangVien(giangVien)
                .siSoToiDa(request.getSiSoToiDa())
                .siSoThucTe(0)
                .hocPhi(request.getHocPhi())
                .trangThai("CHUA_MO") // Chưa phát hành - Admin phải gọi publishLop() riêng
                .thoiKhoaBieuJson(request.getThoiKhoaBieuJson())
                .build();
        return toResponse(lopHocPhanRepository.save(lhp));
    }

    @Override
    @Transactional
    public LopHocPhanResponse update(Long id, LopHocPhanRequest request) {
        LopHocPhan lhp = findOrThrow(id);
        if (request.getIdGiangVien() != null) {
            GiangVien gv = giangVienRepository.findById(request.getIdGiangVien())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy giảng viên ID: " + request.getIdGiangVien()));
            lhp.setGiangVien(gv);
        }
        lhp.setSiSoToiDa(request.getSiSoToiDa());
        lhp.setHocPhi(request.getHocPhi());
        lhp.setThoiKhoaBieuJson(request.getThoiKhoaBieuJson());
        return toResponse(lopHocPhanRepository.save(lhp));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findOrThrow(id);
        lopHocPhanRepository.deleteById(id);
    }

    /**
     * Phát hành lớp: Chuyển trạng thái sang DANG_MO.
     * OCP: Khi thêm Redis warm-up, chỉ cần inject RedisService và gọi thêm ở đây.
     *      Không cần sửa method create() hay logic nghiệp vụ cũ.
     */
    @Override
    @Transactional
    public LopHocPhanResponse publishLop(Long id) {
        LopHocPhan lhp = findOrThrow(id);
        lhp.setTrangThai("DANG_MO");
        // TODO (Task P0-Queue): redisService.warmUpSlot(lhp.getMaLopHp(), lhp.getSiSoToiDa());
        return toResponse(lopHocPhanRepository.save(lhp));
    }

    /**
     * Khóa lớp không cho đăng ký thêm (Admin kill switch).
     */
    @Override
    @Transactional
    public LopHocPhanResponse closeLop(Long id) {
        LopHocPhan lhp = findOrThrow(id);
        lhp.setTrangThai("KHOA");
        return toResponse(lopHocPhanRepository.save(lhp));
    }

    // --- Private helpers ---

    private LopHocPhan findOrThrow(Long id) {
        return lopHocPhanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lớp học phần với ID: " + id));
    }

    private LopHocPhanResponse toResponse(LopHocPhan lhp) {
        HocPhan hp = lhp.getHocPhan();
        HocKy hk = lhp.getHocKy();
        GiangVien gv = lhp.getGiangVien();
        int conLai = lhp.getSiSoToiDa() - (lhp.getSiSoThucTe() != null ? lhp.getSiSoThucTe() : 0);
        return LopHocPhanResponse.builder()
                .idLopHp(lhp.getIdLopHp())
                .maLopHp(lhp.getMaLopHp())
                .idHocPhan(hp != null ? hp.getIdHocPhan() : null)
                .maHocPhan(hp != null ? hp.getMaHocPhan() : null)
                .tenHocPhan(hp != null ? hp.getTenHocPhan() : null)
                .soTinChi(hp != null ? hp.getSoTinChi() : null)
                .idHocKy(hk != null ? hk.getIdHocKy() : null)
                .tenHocKy(hk != null ? "HK" + hk.getKyThu() + " " + hk.getNamHoc() : null)
                .idGiangVien(gv != null ? gv.getIdGiangVien() : null)
                .tenGiangVien(gv != null ? gv.getTenGiangVien() : null)
                .siSoToiDa(lhp.getSiSoToiDa())
                .siSoThucTe(lhp.getSiSoThucTe())
                .siSoConLai(conLai)
                .hocPhi(lhp.getHocPhi())
                .trangThai(lhp.getTrangThai())
                .thoiKhoaBieuJson(lhp.getThoiKhoaBieuJson())
                .build();
    }
}
