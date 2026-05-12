package com.example.demo.service.impl;

import com.example.demo.domain.entity.ChuongTrinhDaoTao;
import com.example.demo.domain.entity.CtdtHocPhan;
import com.example.demo.domain.entity.HocPhan;
import com.example.demo.domain.entity.NganhDaoTao;
import com.example.demo.payload.request.ChuongTrinhDaoTaoRequest;
import com.example.demo.payload.request.CtdtHocPhanRequest;
import com.example.demo.payload.response.ChuongTrinhDaoTaoResponse;
import com.example.demo.payload.response.CtdtHocPhanResponse;
import com.example.demo.repository.ChuongTrinhDaoTaoRepository;
import com.example.demo.repository.CtdtHocPhanRepository;
import com.example.demo.repository.HocPhanRepository;
import com.example.demo.repository.NganhDaoTaoRepository;
import com.example.demo.service.IChuongTrinhDaoTaoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChuongTrinhDaoTaoServiceImpl implements IChuongTrinhDaoTaoService {

    private final ChuongTrinhDaoTaoRepository ctdtRepository;
    private final CtdtHocPhanRepository ctdtHocPhanRepository;
    private final NganhDaoTaoRepository nganhDaoTaoRepository;
    private final HocPhanRepository hocPhanRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ChuongTrinhDaoTaoResponse> getAll() {
        return ctdtRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ChuongTrinhDaoTaoResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional
    public ChuongTrinhDaoTaoResponse create(ChuongTrinhDaoTaoRequest req) {
        NganhDaoTao nganh = nganhDaoTaoRepository.findById(req.getIdNganh())
                .orElseThrow(() -> new EntityNotFoundException("Ngành không tồn tại: " + req.getIdNganh()));
        ChuongTrinhDaoTao ctdt = ChuongTrinhDaoTao.builder()
                .nganhDaoTao(nganh)
                .tongSoTinChi(req.getTongSoTinChi())
                .mucTieu(req.getMucTieu())
                .thoiGianGiangDay(req.getThoiGianGiangDay())
                .doiTuongTuyenSinh(req.getDoiTuongTuyenSinh())
                .namApDung(req.getNamApDung())
                .build();
        return toResponse(ctdtRepository.save(ctdt));
    }

    @Override
    @Transactional
    public ChuongTrinhDaoTaoResponse update(Long id, ChuongTrinhDaoTaoRequest req) {
        ChuongTrinhDaoTao ctdt = findOrThrow(id);
        ctdt.setTongSoTinChi(req.getTongSoTinChi());
        ctdt.setMucTieu(req.getMucTieu());
        ctdt.setThoiGianGiangDay(req.getThoiGianGiangDay());
        ctdt.setDoiTuongTuyenSinh(req.getDoiTuongTuyenSinh());
        ctdt.setNamApDung(req.getNamApDung());
        if (req.getIdNganh() != null) {
            ctdt.setNganhDaoTao(nganhDaoTaoRepository.findById(req.getIdNganh())
                    .orElseThrow(() -> new EntityNotFoundException("Ngành không tồn tại")));
        }
        return toResponse(ctdtRepository.save(ctdt));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findOrThrow(id);
        ctdtRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CtdtHocPhanResponse> getHocPhanByCtdt(Long ctdtId) {
        return ctdtHocPhanRepository.findAllByCtdtId(ctdtId).stream().map(this::toHpResponse).toList();
    }

    @Override
    @Transactional
    public CtdtHocPhanResponse addHocPhan(CtdtHocPhanRequest req) {
        if (ctdtHocPhanRepository.existsByChuongTrinhDaoTao_IdCtdtAndHocPhan_IdHocPhan(req.getIdCtdt(), req.getIdHocPhan())) {
            throw new IllegalArgumentException("Học phần đã có trong CTĐT.");
        }
        ChuongTrinhDaoTao ctdt = ctdtRepository.findById(req.getIdCtdt())
                .orElseThrow(() -> new EntityNotFoundException("CTĐT không tồn tại: " + req.getIdCtdt()));
        HocPhan hp = hocPhanRepository.findById(req.getIdHocPhan())
                .orElseThrow(() -> new EntityNotFoundException("Học phần không tồn tại: " + req.getIdHocPhan()));
        CtdtHocPhan mapping = CtdtHocPhan.builder()
                .chuongTrinhDaoTao(ctdt)
                .hocPhan(hp)
                .khoiKienThuc(req.getKhoiKienThuc())
                .batBuoc(req.getBatBuoc() != null ? req.getBatBuoc() : true)
                .hocKyGoiY(req.getHocKyGoiY())
                .build();
        return toHpResponse(ctdtHocPhanRepository.save(mapping));
    }

    @Override
    @Transactional
    public void removeHocPhan(Long idCtdtHp) {
        if (!ctdtHocPhanRepository.existsById(idCtdtHp)) {
            throw new EntityNotFoundException("Mapping không tồn tại: " + idCtdtHp);
        }
        ctdtHocPhanRepository.deleteById(idCtdtHp);
    }

    private ChuongTrinhDaoTao findOrThrow(Long id) {
        return ctdtRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy CTĐT: " + id));
    }

    private ChuongTrinhDaoTaoResponse toResponse(ChuongTrinhDaoTao c) {
        return ChuongTrinhDaoTaoResponse.builder()
                .idCtdt(c.getIdCtdt())
                .idNganh(c.getNganhDaoTao().getIdNganh())
                .tenNganh(c.getNganhDaoTao().getTenNganh())
                .tongSoTinChi(c.getTongSoTinChi())
                .mucTieu(c.getMucTieu())
                .thoiGianGiangDay(c.getThoiGianGiangDay())
                .doiTuongTuyenSinh(c.getDoiTuongTuyenSinh())
                .namApDung(c.getNamApDung())
                .build();
    }

    private CtdtHocPhanResponse toHpResponse(CtdtHocPhan m) {
        HocPhan hp = m.getHocPhan();
        return CtdtHocPhanResponse.builder()
                .idCtdtHp(m.getIdCtdtHp())
                .idCtdt(m.getChuongTrinhDaoTao().getIdCtdt())
                .idHocPhan(hp.getIdHocPhan())
                .maHocPhan(hp.getMaHocPhan())
                .tenHocPhan(hp.getTenHocPhan())
                .soTinChi(hp.getSoTinChi())
                .khoiKienThuc(m.getKhoiKienThuc())
                .batBuoc(m.getBatBuoc())
                .hocKyGoiY(m.getHocKyGoiY())
                .build();
    }
}
