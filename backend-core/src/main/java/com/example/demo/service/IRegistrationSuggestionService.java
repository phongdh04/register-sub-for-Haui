package com.example.demo.service;

import com.example.demo.payload.response.RegistrationSuggestionResponse;

/**
 * Gợi ý lớp học phần đang mở theo lộ trình CTĐT + khóa (năm nhập học) và học kỳ đăng ký.
 */
public interface IRegistrationSuggestionService {

    RegistrationSuggestionResponse suggestForCurrentStudent(String username, Long hocKyId);
}
