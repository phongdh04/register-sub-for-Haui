package com.example.demo.service.impl;

import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.entity.HocPhan;
import com.example.demo.domain.entity.GioHangDangKy;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.entity.SinhVien;
import com.example.demo.domain.entity.TkbBlock;
import com.example.demo.domain.entity.User;
import com.example.demo.payload.request.PreRegCartAddBlockRequest;
import com.example.demo.repository.DangKyHocPhanRepository;
import com.example.demo.repository.GioHangDangKyRepository;
import com.example.demo.repository.HocKyRepository;
import com.example.demo.repository.LopHocPhanRepository;
import com.example.demo.repository.SinhVienRepository;
import com.example.demo.repository.TkbBlockRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.support.RegistrationScheduleChecker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PreRegistrationCartServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private SinhVienRepository sinhVienRepository;
    @Mock
    private HocKyRepository hocKyRepository;
    @Mock
    private LopHocPhanRepository lopHocPhanRepository;
    @Mock
    private GioHangDangKyRepository gioHangDangKyRepository;
    @Mock
    private DangKyHocPhanRepository dangKyHocPhanRepository;
    @Mock
    private TkbBlockRepository tkbBlockRepository;
    @Mock
    private RegistrationScheduleChecker registrationScheduleChecker;

    @InjectMocks
    private PreRegistrationCartServiceImpl service;

    @Test
    void addBlockAtomically_conflictInsideBlock_rejects() {
        User u = User.builder().id(1L).username("sv").build();
        SinhVien sv = SinhVien.builder().idSinhVien(9L).build();
        HocKy hk = HocKy.builder().idHocKy(3L).build();
        TkbBlock block = TkbBlock.builder().idTkbBlock(77L).hocKy(hk).build();
        LopHocPhan a = LopHocPhan.builder().idLopHp(10L).hocKy(hk).thoiKhoaBieuJson(List.of(java.util.Map.of("thu", 2, "tiet", "1-2"))).build();
        LopHocPhan b = LopHocPhan.builder().idLopHp(11L).hocKy(hk).thoiKhoaBieuJson(List.of(java.util.Map.of("thu", 2, "tiet", "2-3"))).build();

        when(userRepository.findByUsername("sv")).thenReturn(Optional.of(u));
        when(sinhVienRepository.findByTaiKhoan_Id(1L)).thenReturn(Optional.of(sv));
        when(tkbBlockRepository.findById(77L)).thenReturn(Optional.of(block));
        when(hocKyRepository.findById(3L)).thenReturn(Optional.of(hk));
        when(lopHocPhanRepository.findByTkbBlock_IdTkbBlock(77L)).thenReturn(List.of(a, b));

        PreRegCartAddBlockRequest req = new PreRegCartAddBlockRequest();
        req.setIdTkbBlock(77L);
        req.setHocKyId(3L);
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.addBlockAtomically("sv", req));
    }

    @Test
    void addBlockAtomically_happyPath_addsAllAndReturnsCart() {
        User u = User.builder().id(1L).username("sv").build();
        SinhVien sv = SinhVien.builder().idSinhVien(9L).build();
        HocKy hk = HocKy.builder().idHocKy(3L).kyThu(1).namHoc("2025-2026").build();
        TkbBlock block = TkbBlock.builder().idTkbBlock(77L).hocKy(hk).build();
        HocPhan hpA = HocPhan.builder().idHocPhan(100L).tenHocPhan("A").soTinChi(3).build();
        HocPhan hpB = HocPhan.builder().idHocPhan(101L).tenHocPhan("B").soTinChi(2).build();
        LopHocPhan a = LopHocPhan.builder()
                .idLopHp(10L).hocKy(hk).hocPhan(hpA).hocPhi(new BigDecimal("100"))
                .thoiKhoaBieuJson(List.of(java.util.Map.of("thu", 2, "tiet", "1-2"))).build();
        LopHocPhan b = LopHocPhan.builder()
                .idLopHp(11L).hocKy(hk).hocPhan(hpB).hocPhi(new BigDecimal("200"))
                .thoiKhoaBieuJson(List.of(java.util.Map.of("thu", 3, "tiet", "1-2"))).build();
        GioHangDangKy rowA = GioHangDangKy.builder().idGioHang(1L).hocKy(hk).lopHocPhan(a).sinhVien(sv).build();
        GioHangDangKy rowB = GioHangDangKy.builder().idGioHang(2L).hocKy(hk).lopHocPhan(b).sinhVien(sv).build();

        when(userRepository.findByUsername("sv")).thenReturn(Optional.of(u));
        when(sinhVienRepository.findByTaiKhoan_Id(1L)).thenReturn(Optional.of(sv));
        when(tkbBlockRepository.findById(77L)).thenReturn(Optional.of(block));
        when(hocKyRepository.findById(3L)).thenReturn(Optional.of(hk));
        when(lopHocPhanRepository.findByTkbBlock_IdTkbBlock(77L)).thenReturn(List.of(a, b));
        when(gioHangDangKyRepository.findBySinhVienAndHocKyWithLop(9L, 3L))
                .thenReturn(List.of(), List.of(rowA, rowB));
        when(gioHangDangKyRepository.existsBySinhVien_IdSinhVienAndLopHocPhan_IdLopHpAndHocKy_IdHocKy(9L, 10L, 3L))
                .thenReturn(false);
        when(gioHangDangKyRepository.existsBySinhVien_IdSinhVienAndLopHocPhan_IdLopHpAndHocKy_IdHocKy(9L, 11L, 3L))
                .thenReturn(false);
        when(dangKyHocPhanRepository.findRegisteredCoursesInSemester(9L, 3L)).thenReturn(List.of());
        when(registrationScheduleChecker.isPreRegistrationOpen(hk)).thenReturn(true);
        when(registrationScheduleChecker.isOfficialRegistrationOpen(hk)).thenReturn(false);
        when(gioHangDangKyRepository.save(any(GioHangDangKy.class))).thenAnswer(inv -> inv.getArgument(0));

        PreRegCartAddBlockRequest req = new PreRegCartAddBlockRequest();
        req.setIdTkbBlock(77L);
        req.setHocKyId(3L);

        var out = service.addBlockAtomically("sv", req);

        Assertions.assertEquals(2, out.getTongSoMon());
        Assertions.assertEquals(5, out.getTongTinChi());
        Assertions.assertEquals(new BigDecimal("300"), out.getTongHocPhi());
    }
}
