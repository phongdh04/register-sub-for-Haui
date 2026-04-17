package com.example.demo.service;

import com.example.demo.payload.response.TranscriptResponse;

public interface ITranscriptService {
    TranscriptResponse getMyTranscript(String username, Long hocKyId);
}
