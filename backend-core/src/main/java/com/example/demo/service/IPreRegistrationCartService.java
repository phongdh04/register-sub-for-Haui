package com.example.demo.service;

import com.example.demo.payload.request.PreRegCartAddItemRequest;
import com.example.demo.payload.request.PreRegCartAddBlockRequest;
import com.example.demo.payload.response.PreRegCartItemResponse;
import com.example.demo.payload.response.PreRegCartResponse;

public interface IPreRegistrationCartService {

    PreRegCartResponse getMyCart(String username, Long hocKyId);

    PreRegCartItemResponse addItem(String username, PreRegCartAddItemRequest request);

    PreRegCartResponse addBlockAtomically(String username, PreRegCartAddBlockRequest request);

    void removeItem(String username, Long idGioHang);
}
