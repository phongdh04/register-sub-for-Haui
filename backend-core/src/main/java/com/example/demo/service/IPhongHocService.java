package com.example.demo.service;

import com.example.demo.payload.request.PhongHocUpsertRequest;
import com.example.demo.payload.response.PhongHocResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IPhongHocService {

    Page<PhongHocResponse> page(Pageable pageable, String maCoSo);

    PhongHocResponse getById(Long id);

    PhongHocResponse create(PhongHocUpsertRequest request);

    PhongHocResponse update(Long id, PhongHocUpsertRequest request);

    void delete(Long id);
}
