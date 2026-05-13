package com.example.demo.service.impl;

import com.example.demo.domain.entity.GiangVien;
import com.example.demo.domain.entity.SinhVien;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.enums.Role;
import com.example.demo.domain.enums.Status;
import com.example.demo.payload.request.TaiKhoanRequest;
import com.example.demo.payload.response.PagedResponse;
import com.example.demo.payload.response.TaiKhoanResponse;
import com.example.demo.repository.GiangVienRepository;
import com.example.demo.repository.SinhVienRepository;
import com.example.demo.repository.TaiKhoanRepository;
import com.example.demo.service.ITaiKhoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaiKhoanServiceImpl implements ITaiKhoanService {

    private final TaiKhoanRepository taiKhoanRepository;
    private final SinhVienRepository sinhVienRepository;
    private final GiangVienRepository giangVienRepository;

    @Override
    public PagedResponse<TaiKhoanResponse> getByRole(Role role, Pageable pageable) {
        Page<TaiKhoanResponse> page = taiKhoanRepository.findByRole(role, pageable)
                .map(this::toResponse);
        return PagedResponse.of(page);
    }

    @Override
    public PagedResponse<TaiKhoanResponse> searchByRole(Role role, String query, Pageable pageable) {
        Page<TaiKhoanResponse> page = taiKhoanRepository.searchByRole(role, query, pageable)
                .map(this::toResponse);
        return PagedResponse.of(page);
    }

    @Override
    public TaiKhoanResponse getById(Long id) {
        User tk = taiKhoanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản id=" + id));
        return toResponse(tk);
    }

    @Override
    @Transactional
    public TaiKhoanResponse create(TaiKhoanRequest request) {
        if (taiKhoanRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã tồn tại: " + request.getUsername());
        }

        User tk = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .role(request.getRole())
                .status(request.getTrangThai() != null ? request.getTrangThai() : Status.ACTIVE)
                .mfaEnabled(request.getMfaBat() != null ? request.getMfaBat() : false)
                .email(request.getEmailOtp())
                .build();

        tk = taiKhoanRepository.save(tk);
        User savedTk = tk;

        if (Role.STUDENT == request.getRole() && request.getSinhVienId() != null) {
            sinhVienRepository.findById(request.getSinhVienId()).ifPresent(sv -> {
                sv.setTaiKhoan(savedTk);
                sinhVienRepository.save(sv);
            });
        } else if (Role.LECTURER == request.getRole() && request.getGiangVienId() != null) {
            giangVienRepository.findById(request.getGiangVienId()).ifPresent(gv -> {
                gv.setTaiKhoan(savedTk);
                giangVienRepository.save(gv);
            });
        }

        return toResponse(tk);
    }

    @Override
    @Transactional
    public TaiKhoanResponse update(Long id, TaiKhoanRequest request) {
        User tk = taiKhoanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản id=" + id));

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            tk.setPassword(request.getPassword());
        }
        if (request.getTrangThai() != null) {
            tk.setStatus(request.getTrangThai());
        }
        if (request.getMfaBat() != null) {
            tk.setMfaEnabled(request.getMfaBat());
        }
        if (request.getEmailOtp() != null) {
            tk.setEmail(request.getEmailOtp());
        }

        tk = taiKhoanRepository.save(tk);
        return toResponse(tk);
    }

    @Override
    @Transactional
    public void updateTrangThai(Long id, Status trangThai) {
        User tk = taiKhoanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản id=" + id));
        tk.setStatus(trangThai);
        taiKhoanRepository.save(tk);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User tk = taiKhoanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản id=" + id));

        Optional<SinhVien> sv = sinhVienRepository.findByTaiKhoan_Id(id);
        sv.ifPresent(s -> {
            s.setTaiKhoan(null);
            sinhVienRepository.save(s);
        });

        Optional<GiangVien> gv = giangVienRepository.findByTaiKhoan_Id(id);
        gv.ifPresent(g -> {
            g.setTaiKhoan(null);
            giangVienRepository.save(g);
        });

        taiKhoanRepository.delete(tk);
    }

    private TaiKhoanResponse toResponse(User tk) {
        TaiKhoanResponse.TaiKhoanResponseBuilder b = TaiKhoanResponse.builder()
                .id(tk.getId())
                .username(tk.getUsername())
                .role(tk.getRole())
                .trangThai(tk.getStatus())
                .mfaBat(tk.getMfaEnabled())
                .emailOtp(tk.getEmail());

        if (tk.getRole() == Role.STUDENT) {
            sinhVienRepository.findByTaiKhoan_Id(tk.getId())
                    .ifPresent(sv -> {
                        b.sinhVienId(sv.getIdSinhVien())
                         .maSinhVien(sv.getMaSinhVien())
                         .hoTenSinhVien(sv.getHoTen());
                    });
        } else if (tk.getRole() == Role.LECTURER) {
            giangVienRepository.findByTaiKhoan_Id(tk.getId())
                    .ifPresent(gv -> {
                        b.giangVienId(gv.getIdGiangVien())
                         .maGiangVien(gv.getMaGiangVien())
                         .tenGiangVien(gv.getTenGiangVien());
                    });
        }

        return b.build();
    }
}
