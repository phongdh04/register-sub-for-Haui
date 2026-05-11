package com.example.demo.service;

import com.example.demo.payload.response.RegistrationStudentResponse;
import com.example.demo.payload.response.RegistrationWindowStatusResponse;

import java.util.List;

/**
 * Sinh vien — dang ky hoc phan chinh thuc (sync, khong qua Kafka).
 *
 * <p>Day la luong don gian hoa cho do an: thay vi gui message qua Go service + Kafka,
 * sinh vien goi REST API truc tiep. Service nay dung lai validation chain hien co
 * (DuplicateRegistrationHandler -> ScheduleConflictHandler -> PrerequisiteCourseHandler)
 * va atomic increment si so giong nhu code path Kafka.
 */
public interface IRegistrationService {

    /** Dang ky 1 lop hoc phan, tra ve danh sach moi cap nhat. */
    RegistrationStudentResponse register(String username, Long idLopHp, Long idHocKy);

    /** Huy 1 lop da dang ky theo idDangKy (sinh vien chi huy duoc cua chinh minh). */
    void cancel(String username, Long idDangKy);

    /** Lay danh sach lop sinh vien dang dang ky trong hoc ky. */
    List<RegistrationStudentResponse.RegisteredItem> listMine(String username, Long idHocKy);

    /** Trang thai cua so dang ky chinh thuc cho sinh vien dang dang nhap. */
    RegistrationWindowStatusResponse getMyWindowStatus(String username, Long idHocKy);
}
