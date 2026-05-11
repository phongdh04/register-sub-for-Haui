# C01 — Glossary (mở rộng tối đa)

Tài liệu **thuật ngữ**, **viết tắt**, **mã trạng thái** và **chuẩn lỗi** dùng xuyên suốt bộ luận văn EduPort (đăng ký học phần). Khi có tranh luận nghĩa, **ưu tiên tài liệu này** và sau đó **`cross/03_db_dictionary.md`**.

---

## 0) Map tài liệu liên quan

| Đọc thêm khi cần chi tiết implementation | |
|---------------------------------------------|--|
| Thuật ngữ API & path | [`04_api_catalog.md`](04_api_catalog.md) |
| Cột bảng Postgres | [`03_db_dictionary.md`](03_db_dictionary.md) |
| Vai trò & filter security | [`06_rbac_security.md`](06_rbac_security.md) |
| Luồng nghiệp vụ từng màn | `features/Fxx_*/ba_flow.md` |

---

## 1) Thuật ngữ nghiệp vụ cốt lõi

| Thuật ngữ | EN / ER | Định nghĩa chi tiết | Ghi chú code |
|-----------|---------|---------------------|---------------|
| Học kỳ | `HocKy` | Chu kỳ học trong năm; có thể được đánh **hiện hành**. | PK `id_hoc_ky` |
| Học phần | `HocPhan` | Khối kiến thức trong CTĐT; có **mã**, **tín chỉ**; không trùng với “lớp đang học”. | |
| Lớp học phần | `LopHocPhan` / LHP | Một section mở trong HK: có **GV**, **lịch**, **slot**, **publish lifecycle**. | `ma_lop_hp` unique scoped data |
| Cửa sổ đăng ký granular | `RegistrationWindow` | Khoảng `[open_at,close_at]` theo (**HK + phase PRE|OFFICIAL + cohort? + nganh?**). Ưu tiên cụ thể trước, tổng quát sau. | Sprint 1 |
| Cửa fallback HK | Columns trên `hoc_ky` | Bốn mốc PRE + đăng ký chính thức trực tiếp trên `hoc_ky` khi chưa cấu hình window cohort. | Legacy path |
| Pha PRE | `RegistrationPhase.PRE` | Thu thập **intent**/giỏ nháp/chuẩn bị đăng ký; không cấp section như HK chính thức (tuỳ path). | |
| Pha OFFICIAL | `RegistrationPhase.OFFICIAL` | Đăng ký vào section, cập nhật `si_so_thuc_te`, sinh DKHP. | |
| Intent PRE | `PreRegistrationIntent` | Bản ghi (SV, HK, học phần) + priority; phục vụ demand aggregation. | |
| Đăng ký học phần | DKHP `DangKyHocPhan` | Liên kết SV–LHP–HK; có `trang_thai_dang_ky`. | |
| Khóa (cohort) | `lop.nam_nhap_hoc` | Năm 4 chữ số VD 2023. | Checker window |
| Ngành | `NganhDaoTao` | Gắn với SV qua **lớp hành chính**. | |
| Giỏ PRE (draft) | `PreRegistrationCart*` | Draft theo học kỳ (Task 5) — song song với Intent; không gộp vào một F-catalog nhưng cùng pha PRE. | `/api/v1/pre-reg/cart/*` |

---

## 2) Thuật ngữ vận hành đăng ký

| Thuật ngữ | Ý nghĩa trong luận văn |
|-----------|--------------------------|
| Giờ vàng | Thời điểm mở OFFICIAL, RPS và concurrent cao đột biến. |
| Ingress | Service biên nhận request trước (ở đồ án: Fiber Go `:3000`). |
| Producer | Publish message Kafka sang topic `eduport.dang-ky-hoc-phan` (+ pre-reg topic). |
| Consumer | Java `@KafkaListener` xử lý message → validate → DB → log → event. |
| Slot Redis | Counter phía Go trước khi vào Kafka; không thay cho `si_so_thuc_te` DB nhưng hấp thụ spike. |
| Idempotency key | Chuỗi xác định message để tránh replay tạo 2 DKHP (**Sprint 4**). |
| Projection TKB | Bảng `student_timetable_entry` là read-model derived từ TKB JSON + DKHP (**Sprint 5**). |
| Eventually consistent | Delay vài giây giữa Go OK và hiển thị DB/UI nếu không có WS/trigger UI pull. |

---

## 3) Thuật ngữ kỹ thuật (stack EduPort)

| Thuật ngữ | Ghi chú chi tiết triển khai |
|-----------|------------------------------|
| JWT stateless | Session `STATELESS`, token trong header Bearer. |
| SpEL `@PreAuthorize` | `hasRole('X')` ↔ authority `ROLE_X`. |
| Chain of Responsibility | Validators DKHP sequential; có `validate()` vs `validateSelfOnly()` (PRE rút gọn). |
| JPA Hibernate | DDL update possible in dev — production dùng migration SQL chứng minh trong repo reports. |
| JSONB `thoi_khoa_bieu_json` | Lịch học linh động; projection parse list map sang rows. |

---

## 4) Hai trục trạng thái của `LopHocPhan` (tránh nhầm)

### 4.1 Vận hành mở lớp / slot (`lop_hoc_phan.trang_thai`)

| Giá trị | Ý nghĩa | Ảnh hưởng SV |
|---------|---------|----------------|
| `DANG_MO` | Lớp đang được phép cố đăng ký theo các guard khác (window, chỗ…) | Positive |
| `HET_CHO` | Theo nghiệp vụ vận hành có thể được set sau khi full | Negative |
| `KHOA` | Khóa không cho đăng ký | Kill switch |
| `DA_HUY` | Lớp huỷ hẳn | Negative |

*(Danh giá trị chính xác theo comment entity `LopHocPhan`.)*

### 4.2 Vòng đời công bố (`lop_hoc_phan.status_publish` — enum Sprint 3)

| Giá trị | Định nghĩa | Ai chuyển |
|---------|-------------|-----------|
| `SHELL` | Section shell từ forecast, chưa đủ lịch hoặc GV | Hệ/admin planning |
| `SCHEDULED` | Đã có GV + `thoiKhoaBieuJson`; sẵn sàng kiểm tra publish — auto promote có thể từ assign GV | Workflow |
| `PUBLISHED` | Được công bố cho đợt ĐK chính thức theo Sprint 3 | Admin publish/bulk |

> **Không được** gộp bảng 4.1 và 4.2 trong báo luận văn — đây là hai khái niệm trực giao trong code.

---

## 5) Trạng thái đăng ký DKHP (`DangKyHocPhan.trang_thai_dang_ky`)

| Giá trị | Ý nghĩa | Gợi ý hủy được? |
|---------|---------|-----------------|
| `THANH_CONG` | Đăng ký active | Theo phép trong service cancel |
| `CHO_DUYET` | Pending business | Tuỳ chính sách — code hiện allow cancel like success |
| `RUT_MON` | Đã rút formal | Historical |
| `HUY_BO` | Huỷ bởi quy định | Historical |

Chi tiết thao tác hủy: `features/F11_student_registration_cancel/`.

---

## 6) `RegistrationOutcome` (log monitoring — không đánh đồng HK HTTP status)

Đồng bộ package `RegistrationOutcome.java`. Dùng cho **analytics** chứ không phải một HTTP enum duy nhất.

Phổ biến: `SUCCESS`, `FULL`, `DUPLICATE`, `VALIDATION_FAILED`, `REJECTED`, ...

**Lưu ý**: Chuỗi aggregate trong dashboard có thể dùng tên khác shorthand — luôn trỏ codebase `RegistrationMonitoringServiceImpl.ATTEMPT_OUTCOMES`.

---

## 7) Mã viết tắt

| Mã | Đầy đủ | Vai trò |
|----|--------|---------|
| SV | Sinh viên | `STUDENT` |
| GV | Giảng viên | `LECTURER` |
| AD | Admin | `ADMIN` |
| HP/HK/LHP/DKHP/TKB | — | Theo mục 1 |

---

## 8) Mã lỗi nghiệp vụ đối chiếu HTTP (ánh xạ thực tế codebase)

HTTP có thể lệch nhãn nhưng dưới đây là **cam kết tài liệu** để QA viết testcase:

| Mã semantic | HTTP thường gặp | Khi |
|---------------|-----------------|-----|
| `UNAUTHENTICATED` | 401 | Thiếu JWT / malformed |
| `FORBIDDEN` | 403 | RBAC fail hoặc window closed trong REST một số path |
| `VALIDATION_BODY` | 422 | `@Valid` field fail |
| `BUSINESS_CHAIN` | 422 | `RegistrationValidationException` (REST POST register) |
| `NOT_FOUND_ENTITY` | 404 | Không HK / không SV mapping |
| `CLASS_FULL` | 409 | Si số không tăng được |
| `CONFLICT_PUBLISH_EDIT` | 409 | Sprint 3 assign GV sau PUBLISHED |
| `INTERNAL` | 500 | Catch-all handler |

Ingress Go có JSON `{status:"ERROR"|...}` không giống `GlobalExceptionHandler` — không so sánh tuyệt đối trong một bảng.

---

## 9) Chuẩn ngôn ngữ & UX

| Phạm vi | Quy chuẩn |
|---------|-----------|
| Message ra UI người dùng CUỐI | **Tiếng Việt có dấu** |
| REST `message` lỗi nội bộ demo | Repo đang xen kẽ không dấu typo — báo luận văn ghi là **technical debt normalization** backlog |
| Log | EN + không dấu + traceId/hash id |
| Snake_case DB | Theo migration SQL |

---

## 10) Câu hỏi thường gặp (định chuẩn nội dung báo luận văn)

**Q1: PRE và OFFICIAL cùng mở chồng nhau có hợp lệ không?**  
Được hệ phản chiếu bằng `activePhase` = `PRE_AND_OFFICIAL` trong `RegistrationWindowStatusResponse`.

**Q2: Kafka path có nhận PRE không?**  
Implementation hiện gate **chỉ official** trong consumer — xem [`F15_high_load_queue_architecture/dev_spec.md`](../features/F15_high_load_queue_architecture/dev_spec.md).

**Q3: Hai sinh viên cùng bấm 1 chỗ cuối?**  
PostgreSQL conditional update và/hoặc Ingress Redis atomic — chỉ một path thắng; trả FULL/CONFLICT rõ trong F10/F15.

---

## 11) Lịch sử thay đổi glossary

| Ngày | Mô tả |
|------|--------|
| 2026-05 | Bản mở rộng max: phân tách `trang_thai` vs `status_publish`, semantic errors, linkage features |
