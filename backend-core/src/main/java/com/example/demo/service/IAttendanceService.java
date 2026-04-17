package com.example.demo.service;

import com.example.demo.payload.request.AttendanceCheckInRequest;
import com.example.demo.payload.request.CreateAttendanceSessionRequest;
import com.example.demo.payload.request.PatchAttendanceRowRequest;
import com.example.demo.payload.response.AttendanceCheckInResponse;
import com.example.demo.payload.response.AttendanceRowResponse;
import com.example.demo.payload.response.AttendanceSessionResponse;
import com.example.demo.payload.response.LecturerTeachingClassResponse;

import java.util.List;

public interface IAttendanceService {

    List<LecturerTeachingClassResponse> listMyTeachingClasses(String username);

    AttendanceSessionResponse createOrGetSession(String username, Long idLopHp, CreateAttendanceSessionRequest request);

    AttendanceSessionResponse getSessionForLecturer(String username, Long idBuoi);

    AttendanceRowResponse patchRowForLecturer(String username, Long idDiemDanh, PatchAttendanceRowRequest request);

    AttendanceCheckInResponse studentCheckIn(String username, AttendanceCheckInRequest request);
}
