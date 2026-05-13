package com.example.demo.service;

import com.example.demo.payload.request.LopRequest;
import com.example.demo.payload.response.LopResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ILopService {
    List<LopResponse> getAll();
    Page<LopResponse> getAllPaged(Pageable pageable);
    LopResponse getById(Long id);
    LopResponse create(LopRequest request);
    LopResponse update(Long id, LopRequest request);
    void delete(Long id);
}
