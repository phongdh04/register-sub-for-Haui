# Kế hoạch tài liệu trọng tâm Đăng ký học phần (3 vai trò)

Phiên bản: rà soát thực trạng sản phẩm, có WebSocket và yêu cầu chịu tải.

Mục tiêu của bản này:
- Quét toàn dự án và chốt phạm vi luận văn chỉ tập trung vào nghiệp vụ đăng ký học phần.
- Liệt kê rõ chức năng theo 3 vai trò `STUDENT`, `LECTURER`, `ADMIN`.
- Chỉ ra chức năng còn thiếu, chức năng thừa nên rút.
- Đưa ra danh mục tài liệu cần tạo, theo thứ tự viết từng tài liệu.

---

## 0) Hiện trạng sản phẩm (kết quả quét repo)

### Đã có và chạy thật
- Backend Java Spring Boot 4 (Java 21), JPA, Spring Security JWT.
- Backend Go Fiber cho hàng đợi giờ vàng.
- Kafka (`spring-kafka`) + Redis (`spring-boot-starter-data-redis`) đã được khai báo trong `backend-core/pom.xml`.
- Bộ controller đăng ký học phần đầy đủ:
  - `RegistrationController`, `PreRegistrationCartController`, `PreRegistrationIntentController`.
  - `AdminRegistrationWindowController`, `AdminPreRegistrationDemandController`, `AdminClassPublishController`, `AdminRegistrationMonitoringController`, `AdminTimetableProjectionController`, `AdminHocKyScheduleController`, `RegistrationSuggestionController`.
- Frontend đã có các page chính cho luồng đăng ký:
  - SV: `StudentRegistrationPage.jsx`, `TnhNngTrcGiGPreRegistrationGiLp.jsx`, `DchVThiKhaBiuThngMinh.jsx`.
  - Admin: `AdminRegistrationWindowsPage.jsx`, `AdminClassPublishPage.jsx`, `AdminPreRegistrationDemandPage.jsx`, `AdminRegistrationMonitoringPage.jsx`, `AdminTimetableProjectionToolsPage.jsx`, `AdminHocKyScheduleConfigPage.jsx`.
- 27 page frontend đều có ít nhất một lần gọi API thật, ngoại trừ một vài page tĩnh nêu ở mục 0.3.

### Chưa có (cần làm cho luận văn)
- WebSocket: chưa có ở cả 3 nơi.
  - Backend Java chưa thêm `spring-boot-starter-websocket`.
  - Backend Go chưa có thư viện WebSocket (`gorilla/websocket`/`melody`).
  - Frontend chưa có `socket.io-client`/`@stomp/stompjs`.
- Kiến trúc scale-out cho realtime (sticky session hoặc broker bridge Redis pub/sub) chưa thiết kế.
- Tài liệu KPI hiệu năng và kế hoạch load test chưa có.

### Page hoàn toàn tĩnh (không gọi API, không phục vụ luận văn)
- `frontend/src/pages/DashboardSinhVinTrangCh.jsx`: hardcode tên SV, số dư ví, môn học hôm nay.
- `frontend/src/pages/TnhNngLcMnnhCao.jsx`: hardcode đếm ngược "03 ngày 14:20".
- `frontend/src/pages/ThutTonLogicngChtValidationRulesEngine.jsx`: nội dung trùng hệt page trên.

### Page có API thật nhưng ngoài phạm vi đăng ký học phần
- Tài chính/ví/thanh toán: `VSinhVinStudentWallet`, `ThanhTonQrCodeOpenApi`, `GimStTiChnhKTonAdmin`.
- Bảo mật/RBAC/Audit: `HThngPhnQuynaTngRbacRoleBasedAccessControl`, `XcThcaYuTMfa2FaVChKS`, `LchSNhtKDuChnAuditTrailsLogging`.
- Học vụ/khảo thí/CV: `KimTraTinHcTpTranscriptDashboard`, `TraCuHSCNhnThTcOnline`, `LchThinhGiGv`, `CyKhungChngTrnhDegreeAuditRoadmap`.
- Solver/data master/analytics: `SetupCuHnhGiVngTrafficSplittingQueueControl`, `QunLDanhMcKhungMLpDataMaster`, `BoCoPhnTchAnalytics`.

---

## 1) Phạm vi luận văn sau khi rút gọn

### Giữ lại (core)
- Luồng đăng ký học phần theo pha: `PRE` -> `OFFICIAL` -> hậu kiểm.
- Quản trị cửa sổ đăng ký theo học kỳ/cohort/ngành.
- Quản lý mở lớp phục vụ đăng ký (publish lớp, phân công giảng viên cho lớp).
- Giám sát vận hành đăng ký (outcome, throughput, fill-rate).
- Cập nhật realtime bằng WebSocket cho trạng thái slot/lớp và tiến trình xử lý đăng ký (cần triển khai).
- Thiết kế chịu tải cao cho cao điểm đăng ký (horizontal scale, queue, backpressure, retry).
- Đồng bộ thời khóa biểu sau đăng ký (projection/snapshot).
- Luồng sinh viên: xem lớp, đăng ký/hủy, xem kết quả và thời khóa biểu.

### Để phụ lục hoặc loại khỏi phạm vi chính
- Wallet/Payment/Finance.
- MFA, audit mở rộng, RBAC matrix chi tiết.
- Attendance/Grading/Retake appeal.
- Public pre-registration cho tuyển sinh (không phải đăng ký học phần của SV nội bộ).
- Các trang demo tĩnh không gọi API thật (xem mục 0.3).

---

## 2) Bản đồ chức năng theo 3 vai trò

### STUDENT
Đã có:
- Đăng ký chính thức học phần (`/api/v1/registrations*`).
- Hủy đăng ký học phần.
- Xem trạng thái cửa sổ đăng ký theo học kỳ.
- Tra cứu danh sách lớp học phần mở.
- Luồng PRE (nguyện vọng dự kiến) qua intents.
- Xem thời khóa biểu sau đăng ký (snapshot + fallback).

Cần làm:
- Nhận realtime trạng thái hàng đợi/kết quả đăng ký qua WebSocket.
- Màn hình lịch sử request đăng ký theo từng lần (thành công/thất bại/lý do).
- Đặt lại route PRE thành `/student/pre-registration` thay vì slug khó đọc.
- Chuẩn hóa ngôn ngữ giữa các biến thể PRE (intent/cart/public pre-reg).

### LECTURER
Đã có:
- Tham gia gián tiếp qua phân công lớp do admin gán.

Cần chốt phạm vi:
- Khẳng định trong tài liệu: giảng viên chỉ là actor hỗ trợ mở lớp, không can thiệp giao dịch đăng ký.
- Sửa kỹ thuật: đồng bộ naming role (`TEACHER` vs `LECTURER`) ở `CourseSearchController` và các tham chiếu khác.

### ADMIN
Đã có:
- Cấu hình cửa sổ đăng ký theo học kỳ/cohort/ngành/pha.
- Quản lý publish lớp học phần.
- Xem cầu PRE và dữ liệu phục vụ quyết định mở lớp.
- Theo dõi monitoring đăng ký (kết quả, tốc độ, tỷ lệ đầy).
- Công cụ rebuild projection thời khóa biểu.
- Cấu hình lịch học kỳ fallback.

Cần làm:
- Theo dõi realtime sức tải hệ thống và điều phối vận hành trong giờ cao điểm (cần WebSocket + dashboard).
- Drill-down lỗi đăng ký đến từng request để xử lý vận hành.
- Chốt nguồn sự thật giữa cơ chế cửa sổ mới (`registration_window`) và lịch fallback `HocKy`.
- Tài liệu chuẩn cho kiến trúc WebSocket scale-out (multi-instance, sticky session/Redis pub-sub bridge).

---

## 3) Chức năng thừa nên rút khỏi luận văn chính

- Mọi module tài chính/thu học phí nâng cao.
- Luồng khảo thí, điểm danh, nhập điểm, phúc khảo.
- Chức năng tối ưu solver tổng quát nếu không phục vụ trực tiếp quyết định mở lớp cho đăng ký.
- Các trang dashboard trình diễn không phục vụ câu chuyện đăng ký học phần.

Lưu ý: WebSocket và năng lực chịu tải **không** nằm trong nhóm rút bỏ, mà là hạng mục bắt buộc của phạm vi chính.

---

## 4) Danh mục tài liệu cần tạo (bộ tài liệu chính thức)

Các tài liệu dưới đây là bộ tối thiểu để bảo vệ luận văn theo trọng tâm đăng ký học phần:

1. `reports/thesis_registration_01_scope_and_problem.md`
   - Bài toán, mục tiêu, ranh giới, giả định, KPI.

2. `reports/thesis_registration_02_role_workflows.md`
   - Workflow chi tiết cho `STUDENT` / `LECTURER` / `ADMIN`.
   - Use-case chính/phụ, tiền điều kiện/hậu điều kiện.

3. `reports/thesis_registration_03_system_architecture.md`
   - Kiến trúc Java + Go + Kafka + Redis.
   - Sync path vs async queue path, idempotency, validation chain.
   - Kiến trúc WebSocket realtime và mô hình scale-out.

4. `reports/thesis_registration_04_pre_registration_and_demand.md`
   - PRE intent, tổng hợp nhu cầu, quyết định mở lớp.
   - Làm rõ PRE nội bộ vs public pre-reg.

5. `reports/thesis_registration_05_official_registration_flow.md`
   - Luồng đăng ký chính thức end-to-end.
   - Rule nghiệp vụ: trùng môn, trùng lịch, điều kiện tiên quyết, cửa sổ thời gian.

6. `reports/thesis_registration_06_admin_management_operations.md`
   - Cấu hình cửa sổ, publish lớp, monitoring, projection tools.
   - Runbook vận hành khi cao điểm đăng ký.

7. `reports/thesis_registration_07_api_contracts_and_ui_mapping.md`
   - Mapping endpoint <-> màn hình theo role.
   - Danh sách API bắt buộc cho demo.

8. `reports/thesis_registration_08_gap_and_simplification_plan.md`
   - Chức năng thiếu, chức năng thừa, technical debt.
   - Kế hoạch cắt giảm và chuẩn hóa luồng.

9. `reports/thesis_registration_09_test_and_demo_scenarios.md`
   - Kịch bản test theo role.
   - Script demo 15 phút bảo vệ.
   - Kịch bản load test và tiêu chí pass/fail chịu tải.

10. `reports/thesis_registration_10_backlog_future_work.md`
    - Những phần để phase sau (tài chính, khảo thí, nâng cấp vận hành).

11. `reports/thesis_registration_11_realtime_and_performance_blueprint.md`
    - Thiết kế chi tiết WebSocket, channel/topic, event contract.
    - Load profile mục tiêu, capacity planning, autoscaling, SLO/SLA.
    - Kế hoạch benchmark (k6/JMeter/Gatling), quan sát và tối ưu.

Tài liệu vận hành đi kèm:
- `reports/ui_cleanup_static_api_and_screen_removal_plan.md` (đã có).

---

## 5) Thứ tự tạo tài liệu (khuyến nghị)

Thứ tự viết để nhanh hoàn thiện:

1) `01_scope_and_problem`
2) `02_role_workflows`
3) `05_official_registration_flow`
4) `06_admin_management_operations`
5) `07_api_contracts_and_ui_mapping`
6) `04_pre_registration_and_demand`
7) `03_system_architecture`
8) `11_realtime_and_performance_blueprint`
9) `08_gap_and_simplification_plan`
10) `09_test_and_demo_scenarios`
11) `10_backlog_future_work`

Lý do: chốt nghiệp vụ trước, sau đó mới chốt kỹ thuật và demo.

---

## 6) Quy ước khi viết từng tài liệu

Mỗi tài liệu phải có các mục cố định:
- Mục tiêu tài liệu.
- Phạm vi in-scope/out-of-scope.
- Luồng chính theo actor.
- Danh sách API/DB/event liên quan.
- Điểm rủi ro và tiêu chí nghiệm thu.
- Chỉ số hiệu năng mục tiêu (p95 latency, throughput, error rate) nếu tài liệu có liên quan vận hành.

Điều này giúp toàn bộ bộ tài liệu nhất quán khi bảo vệ.

---

## 7) Kế hoạch bước tiếp theo

Bộ tài liệu chính thức đã chuyển sang cấu trúc thư mục có tổ chức tại `reports/thesis/`. Index master ở `reports/thesis/README.md`.

Cấu trúc chuẩn:
- `reports/thesis/README.md` — master index và bảng trạng thái sản xuất.
- `reports/thesis/templates/` — template BA-Flow và Dev-Spec.
- `reports/thesis/cross/` — tài liệu cross-cutting (glossary, architecture, DB dictionary, API catalog, WS protocol, RBAC, performance, test, demo, runbook).
- `reports/thesis/features/Fxx_*/` — mỗi chức năng có đôi `ba_flow.md` + `dev_spec.md`.

Đã tạo trong vòng đầu:
- `reports/thesis/README.md`
- `reports/thesis/templates/ba_flow_template.md`
- `reports/thesis/templates/dev_spec_template.md`
- `reports/thesis/cross/01_glossary.md`
- `reports/thesis/cross/02_architecture_overview.md`
- `reports/thesis/cross/03_db_dictionary.md`

Vòng tiếp theo, tạo các tài liệu cross-cutting còn lại và bắt đầu các feature theo thứ tự:
1. `reports/thesis/cross/04_api_catalog.md`
2. `reports/thesis/cross/06_rbac_security.md`
3. `reports/thesis/cross/05_websocket_protocol.md`
4. `reports/thesis/cross/07_performance_and_slo.md`
5. `reports/thesis/features/F02_admin_registration_window/` (ba_flow + dev_spec)
6. `reports/thesis/features/F10_student_official_registration/` (ba_flow + dev_spec)

Song song có thể bắt đầu Pha 1 trong `reports/ui_cleanup_static_api_and_screen_removal_plan.md` (dọn route + menu) để demo gọn.
