package com.example.demo.service;

import com.example.demo.payload.request.ChuongTrinhDaoTaoRequest;
import com.example.demo.payload.request.CtdtHocPhanRequest;
import com.example.demo.payload.response.ChuongTrinhDaoTaoResponse;
import com.example.demo.payload.response.CtdtHocPhanResponse;
import java.util.List;

public interface IChuongTrinhDaoTaoService {
    List<ChuongTrinhDaoTaoResponse> getAll();
    ChuongTrinhDaoTaoResponse getById(Long id);
    ChuongTrinhDaoTaoResponse create(ChuongTrinhDaoTaoRequest request);
    ChuongTrinhDaoTaoResponse update(Long id, ChuongTrinhDaoTaoRequest request);
    void delete(Long id);

    /* Mapping HP vào CTĐT */
    List<CtdtHocPhanResponse> getHocPhanByCtdt(Long ctdtId);
    CtdtHocPhanResponse addHocPhan(CtdtHocPhanRequest request);
    void removeHocPhan(Long idCtdtHp);
}
