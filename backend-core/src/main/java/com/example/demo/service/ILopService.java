package com.example.demo.service;

import com.example.demo.payload.request.LopRequest;
import com.example.demo.payload.response.LopResponse;
import java.util.List;

public interface ILopService {
    List<LopResponse> getAll();
    LopResponse getById(Long id);
    LopResponse create(LopRequest request);
    LopResponse update(Long id, LopRequest request);
    void delete(Long id);
}
