package com.example.demo.service;

import com.example.demo.payload.request.LecturerRatingSubmitRequest;
import com.example.demo.payload.response.LecturerRatingListResponse;
import com.example.demo.payload.response.LecturerRatingRowResponse;

public interface ILecturerRatingService {

    LecturerRatingListResponse listMine(String username, Long hocKyId);

    LecturerRatingRowResponse submit(String username, LecturerRatingSubmitRequest body);
}
