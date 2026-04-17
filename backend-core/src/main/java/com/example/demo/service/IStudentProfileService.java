package com.example.demo.service;

import com.example.demo.payload.request.StudentContactPatchRequest;
import com.example.demo.payload.response.StudentProfileResponse;

public interface IStudentProfileService {

    StudentProfileResponse getMyProfile(String username);

    StudentProfileResponse patchMyContact(String username, StudentContactPatchRequest request);
}
