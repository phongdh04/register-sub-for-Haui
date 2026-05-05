package com.example.demo.service;

import com.example.demo.payload.request.TkbBlockUpsertRequest;
import com.example.demo.payload.response.TkbBlockResponse;

import java.util.List;

/** BACK-TKB-037 — lifecycle CRUD TkbBlock theo học kỳ + validator danh sách học phần. */
public interface ITkbBlockAdminService {

    List<TkbBlockResponse> listByHocKy(Long hocKyId);

    TkbBlockResponse create(Long hocKyId, TkbBlockUpsertRequest request);

    TkbBlockResponse update(Long hocKyId, Long idTkbBlock, TkbBlockUpsertRequest request);

    void delete(Long hocKyId, Long idTkbBlock);
}
