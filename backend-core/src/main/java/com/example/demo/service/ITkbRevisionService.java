package com.example.demo.service;

/** BK-TKB-011 — tăng {@code HocKy.tkbRevision} và xóa cache snapshot sau khi TKB học kỳ thay đổi. */
public interface ITkbRevisionService {

    void bumpAfterTkbMutation(Long hocKyId);
}
