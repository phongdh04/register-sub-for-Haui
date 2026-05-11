# Bộ tài liệu Đồ án — Đăng ký học phần (3 vai trò)

Tài liệu này là master index cho toàn bộ bộ tài liệu luận văn tập trung vào nghiệp vụ **đăng ký học phần** của 3 vai trò `STUDENT`, `LECTURER`, `ADMIN`.

Quy ước viết:
- Mỗi chức năng có **2 lớp tài liệu**: BA-Flow (nghiệp vụ) và Dev-Spec (kỹ thuật).
- Tài liệu cross-cutting để tham chiếu chung cho tất cả chức năng.
- Mọi tài liệu đều phải đủ chi tiết để **dev cấp thấp hoặc agent yếu** vẫn implement được, không cần đặt lại câu hỏi nghiệp vụ.

---

## 1) Cấu trúc thư mục

```
reports/thesis/
  README.md                     ← bạn đang đọc
  templates/
    ba_flow_template.md         ← template tài liệu BA-Flow
    dev_spec_template.md        ← template tài liệu Dev-Spec
  cross/
    01_glossary.md              ← thuật ngữ và viết tắt
    02_architecture_overview.md ← kiến trúc tổng thể
    03_db_dictionary.md         ← từ điển bảng/cột nghiệp vụ
    04_api_catalog.md           ← danh mục API toàn hệ thống
    05_websocket_protocol.md    ← giao thức WebSocket realtime
    06_rbac_security.md         ← phân quyền và bảo mật
    07_performance_and_slo.md   ← KPI hiệu năng và load test
    08_test_plan.md             ← chiến lược test
    09_demo_script.md           ← kịch bản demo bảo vệ
    10_deployment_runbook.md    ← cài đặt và vận hành
  features/
    F01_auth/                   ← đăng nhập và phân quyền portal
    F02_admin_registration_window/
    F03_admin_class_publish/
    F04_admin_pre_registration_demand/
    F05_admin_registration_monitoring/
    F06_admin_timetable_projection/
    F07_admin_semester_schedule_legacy/
    F08_student_pre_registration_intent/
    F09_student_course_discovery/
    F10_student_official_registration/
    F11_student_registration_cancel/
    F12_student_personal_timetable/
    F13_lecturer_assigned_classes_view/
    F14_realtime_websocket_channels/
    F15_high_load_queue_architecture/
    F16_validation_rules_engine/
    F17_registration_capacity_planning_pipeline/
```

Mỗi thư mục chức năng `Fxx_*` chứa 2 file:
- `ba_flow.md`
- `dev_spec.md`

---

## 2) Catalog chức năng

| ID | Tên chức năng | Vai trò chủ đạo | Loại |
|----|----------------|-----------------|------|
| F01 | Đăng nhập và phân quyền portal | ALL | Auth |
| F02 | Quản lý cửa sổ đăng ký | ADMIN | Admin |
| F03 | Quản lý xuất bản lớp học phần | ADMIN | Admin |
| F04 | Phân tích nhu cầu PRE | ADMIN | Admin |
| F05 | Giám sát đăng ký realtime | ADMIN | Admin |
| F06 | Công cụ projection thời khóa biểu | ADMIN | Admin |
| F07 | Cấu hình lịch học kỳ fallback | ADMIN | Admin |
| F08 | Đăng ký dự kiến PRE (intent) | STUDENT | Student |
| F09 | Tra cứu lớp học phần mở | STUDENT | Student |
| F10 | Đăng ký chính thức học phần | STUDENT | Student |
| F11 | Hủy đăng ký học phần | STUDENT | Student |
| F12 | Thời khóa biểu cá nhân | STUDENT | Student |
| F13 | Xem lớp được phân công | LECTURER | Lecturer |
| F14 | Kênh WebSocket realtime | ALL | Cross |
| F15 | Kiến trúc hàng đợi chịu tải | System | Cross |
| F16 | Validation Rules Engine | System | Cross |
| F17 | Pipeline kế hoạch năng lực (PRE → lớp/GV/TKB → đăng ký) | ADMIN | Cross-process |

---

## 3) Loại tài liệu cho mỗi chức năng

### 3.1 BA-Flow (`ba_flow.md`)
Mục đích: phân tích nghiệp vụ và luồng người dùng. Người đọc: BA, GVHD, hội đồng.

Nội dung bắt buộc:
1. Mục đích chức năng và đối tượng sử dụng.
2. Các bên liên quan (actor) và quyền hạn.
3. Tiền điều kiện và hậu điều kiện.
4. Use case chính (Main Success Scenario).
5. Use case phụ và ngoại lệ (Alternate Flow + Exception Flow).
6. Business rule rõ ràng dạng "GIVEN-WHEN-THEN".
7. Wireframe hoặc mô tả màn hình, trường dữ liệu hiển thị.
8. Tiêu chí nghiệm thu (Acceptance Criteria).
9. Câu hỏi/giả định cần xác nhận.

### 3.2 Dev-Spec (`dev_spec.md`)
Mục đích: đặc tả kỹ thuật. Người đọc: dev, agent code.

Nội dung bắt buộc:
1. Tóm tắt kỹ thuật và phụ thuộc.
2. Lớp domain liên quan (entity, value object, enum).
3. DB schema chi tiết: bảng, cột, kiểu, ràng buộc, index.
4. API contract đầy đủ:
   - Method, URL.
   - Header (auth).
   - Request body schema.
   - Response body schema.
   - Status code và error code.
   - Ví dụ request/response.
5. Validation rules per field.
6. Business rules logic.
7. Sequence diagram chi tiết các bước.
8. Edge case và lỗi cần xử lý.
9. Event/message phát ra (Kafka, WebSocket, domain event).
10. Test case (unit + integration) liên quan.
11. Hướng dẫn implement step-by-step.

---

## 4) Tài liệu cross-cutting

| Mã | Tên tài liệu | Vai trò |
|----|--------------|---------|
| C01 | Glossary | Thuật ngữ chung, tránh hiểu nhầm. |
| C02 | Architecture Overview | Bản đồ thành phần và data flow tổng thể. |
| C03 | DB Dictionary | Từ điển dữ liệu cốt lõi. |
| C04 | API Catalog | Danh sách endpoint toàn hệ thống. |
| C05 | WebSocket Protocol | Channel, event, payload. |
| C06 | RBAC & Security | Quyền và bảo mật. |
| C07 | Performance & SLO | KPI, load profile. |
| C08 | Test Plan | Chiến lược test. |
| C09 | Demo Script | Kịch bản bảo vệ. |
| C10 | Deployment Runbook | Cài đặt và vận hành. |

---

## 5) Thứ tự sản xuất tài liệu

Pha 1 — Foundation (đã/đang làm trong lượt hiện tại):
1. README master.
2. `templates/ba_flow_template.md`.
3. `templates/dev_spec_template.md`.
4. `cross/01_glossary.md`.
5. `cross/02_architecture_overview.md`.
6. `cross/03_db_dictionary.md`.

Pha 2 — Cross-cutting còn lại (Đã có bản Draft đầy đủ trong repo):
7. `cross/04_api_catalog.md`.
8. `cross/06_rbac_security.md`.
9. `cross/05_websocket_protocol.md`.
10. `cross/07_performance_and_slo.md`.

Pha 3 — Feature theo độ ưu tiên — **Đã có đủ BA + Dev trong `features/Fxx_*` cho F01→F16 (Draft có thể review lại khi codebase đổi)**:
11. F02 Admin Registration Window (BA + Dev).
12. F03 Admin Class Publish (BA + Dev).
13. F08 Student Pre-Registration Intent (BA + Dev).
14. F10 Student Official Registration (BA + Dev).
15. F11 Student Registration Cancel (BA + Dev).
16. F12 Student Personal Timetable (BA + Dev).
17. F09 Student Course Discovery (BA + Dev).
18. F05 Admin Registration Monitoring (BA + Dev).
19. F04 Admin Pre-Registration Demand (BA + Dev).
20. F06 Admin Timetable Projection (BA + Dev).
21. F07 Admin Semester Schedule Legacy (BA + Dev).
22. F14 Realtime WebSocket Channels (BA + Dev).
23. F15 High-Load Queue Architecture (BA + Dev).
24. F16 Validation Rules Engine (BA + Dev).
25. F13 Lecturer Assigned Classes (BA + Dev).
26. F01 Auth (BA + Dev).

Pha 4 — Cross-cutting kết thúc (**Done** trong repo hiện tại):
27. `cross/08_test_plan.md`.
28. `cross/09_demo_script.md`.
29. `cross/10_deployment_runbook.md`.

---

## 6) Trạng thái sản xuất

| Mã | Tài liệu | Trạng thái |
|----|----------|------------|
| — | README master | Done |
| — | BA-Flow template | Done |
| — | Dev-Spec template | Done |
| C01 | Glossary | Done |
| C02 | Architecture Overview | Done |
| C03 | DB Dictionary | Done |
| C04 | API Catalog | Done |
| C05 | WebSocket Protocol | Done |
| C06 | RBAC & Security | Done |
| C07 | Performance & SLO | Done |
| C08 | Test Plan | Done |
| C09 | Demo Script | Done |
| C10 | Deployment Runbook | Done |
| F01 | Auth | Done |
| F02 | Admin Registration Window | Done |
| F03 | Admin Class Publish | Done |
| F04 | Admin Pre-Registration Demand | Done |
| F05 | Admin Registration Monitoring | Done |
| F06 | Admin Timetable Projection | Done |
| F07 | Admin Semester Schedule Legacy | Done |
| F08 | Student Pre-Registration Intent | Done |
| F09 | Student Course Discovery | Done |
| F10 | Student Official Registration | Done |
| F11 | Student Registration Cancel | Done |
| F12 | Student Personal Timetable | Done |
| F13 | Lecturer Assigned Classes View | Done |
| F14 | Realtime WebSocket Channels | Done |
| F15 | High-Load Queue Architecture | Done |
| F16 | Validation Rules Engine | Done |

Cập nhật bảng này mỗi khi hoàn thành một tài liệu.
