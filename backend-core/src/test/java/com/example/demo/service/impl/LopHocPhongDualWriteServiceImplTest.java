package com.example.demo.service.impl;

import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.entity.PhongHoc;
import com.example.demo.domain.enums.LoaiPhong;
import com.example.demo.domain.enums.TrangThaiPhong;
import com.example.demo.repository.PhongHocRepository;
import com.example.demo.service.support.PhongStringResolver;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LopHocPhongDualWriteServiceImplTest {

    @Mock
    private PhongHocRepository phongHocRepository;

    @Mock
    private EntityManager entityManager;

    private final PhongStringResolver phongStringResolver = new PhongStringResolver();

    @InjectMocks
    private LopHocPhongDualWriteServiceImpl dualWriteService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(dualWriteService, "phongStringResolver", phongStringResolver);
        ReflectionTestUtils.setField(dualWriteService, "dualWriteEnabled", true);
    }

    @Test
    void dualWriteDisabled_doesNotMutate() {
        ReflectionTestUtils.setField(dualWriteService, "dualWriteEnabled", false);
        PhongHoc p = baseRoom(1L, "A101");
        List<Map<String, Object>> json = new ArrayList<>();
        Map<String, Object> slot = new LinkedHashMap<>();
        slot.put("phong", "OLD");
        json.add(slot);
        LopHocPhan lhp = LopHocPhan.builder().idLopHp(10L).phongHoc(p).thoiKhoaBieuJson(json).build();

        dualWriteService.synchronize(lhp);

        assertEquals("OLD", json.getFirst().get("phong"));
        verifyNoInteractions(phongHocRepository, entityManager);
    }

    @Test
    void fkPresent_overwritesPrimarySlotPhongWithCanonicalMa() {
        PhongHoc p = baseRoom(5L, "A101");
        List<Map<String, Object>> json = new ArrayList<>();
        json.add(new LinkedHashMap<>(Map.of("thu", 2, "phong", "a.101")));

        LopHocPhan lhp =
                LopHocPhan.builder().idLopHp(1L).maLopHp("HP1").phongHoc(p).thoiKhoaBieuJson(json).build();

        dualWriteService.synchronize(lhp);

        assertEquals("A101", lhp.getThoiKhoaBieuJson().getFirst().get("phong"));
    }

    @Test
    void fkNull_fillsFkFromUniqueJsonMatch() {
        PhongHoc room = baseRoom(9L, "X101");

        Map<String, Object> slot = new LinkedHashMap<>();
        slot.put("thu", 3);
        slot.put("phong", "x.101");

        List<Map<String, Object>> json = new ArrayList<>();
        json.add(slot);

        LopHocPhan lhp = LopHocPhan.builder().idLopHp(7L).phongHoc(null).thoiKhoaBieuJson(json).build();

        when(phongHocRepository.findAll()).thenReturn(List.of(room));
        when(entityManager.getReference(eq(PhongHoc.class), eq(9L))).thenReturn(room);

        dualWriteService.synchronize(lhp);

        assertNotNull(lhp.getPhongHoc());
        assertEquals(9L, lhp.getPhongHoc().getIdPhong());
        assertEquals("X101", lhp.getThoiKhoaBieuJson().getFirst().get("phong"));
    }

    private static PhongHoc baseRoom(long id, String ma) {
        return PhongHoc.builder()
                .idPhong(id)
                .maPhong(ma)
                .tenPhong("t")
                .maCoSo("CS1")
                .loaiPhong(LoaiPhong.LY_THUYET)
                .sucChua(50)
                .trangThai(TrangThaiPhong.HOAT_DONG)
                .build();
    }
}
