# Báo cáo tiến độ triển khai — F04 Nhu cầu PRE (Admin)

| Mục | Giá trị |
|-----|---------|
| Mã chức năng | F04 |
| Ngày rà soát | 2026-05-09 |
| Tài liệu BA | [`ba_flow.md`](ba_flow.md) |
| Tài liệu Dev | [`dev_spec.md`](dev_spec.md) |

---

## 1) Đánh giá tổng thể

**Mức độ hoàn thành: cao (MVP nghiệp vụ đạt).** Luồng read-only “tổng hợp intent theo học phần + gợi ý số lớp” đã có **backend đầy đủ**, **SQL aggregate GROUP BY**, **UI admin** và **unit test service**. Các hạng mục còn lại chủ yếu là **tinh chỉnh UX** (slider), **export** chính thức và **test bảo mật tích hợp** (không bắt buộc để demo luận văn).

---

## 2) Ma trận nghiệp thu (BA §10) ↔ thực tế code

| Tiêu chí | Trạng thái | Minh chứng |
|----------|------------|------------|
| ADMIN-only; sinh viên **403** | **Đạt (theo cơ chế Spring)** | `AdminPreRegistrationDemandController` có `@PreAuthorize("hasRole('ADMIN')")`. Chưa có `@WebMvcTest` riêng cho endpoint này; 403 sinh viên phụ thuộc cấu hình security toàn cục (`WebSecurityConfig` + JWT). |
| Bắt buộc `hocKyId`; response khớp aggregation | **Đạt** | `hocKyId` là `@RequestParam Long` (Spring trả 400 nếu thiếu sai kiểu); `hocKyId == null` được chặn trong service. `PreRegistrationIntentRepository.aggregateDemand(...)` dùng JPQL `GROUP BY` học phần + cohort + ngành. |
| `targetClassSize` xấu / thiếu → default config | **Đạt** | `PreRegistrationDemandServiceImpl`: `eduport.prereg.target-class-size-default` (mặc định 40). UI để trống field thì không gửi param → BE dùng default. |
| Tổng banner khớp tổng từ `items` | **Đạt + đã test** | Service cộng `totalIntents` và `totalRecommendedClasses` từ từng dòng. `PreRegistrationDemandServiceImplTest` kiểm tra tổng intent, tổng lớp, `ceil`, tối thiểu 1 lớp/HP, custom `targetClassSize`. |

---

## 3) Backend

| Thành phần | File / vị trí | Ghi chú |
|------------|---------------|---------|
| API | `AdminPreRegistrationDemandController` | `GET /api/v1/admin/pre-registrations/demand` đúng prefix dev_spec. |
| Service | `PreRegistrationDemandServiceImpl` | Validate HK, resolve `classSize`, map `Object[]` → DTO, load `tenNganh` khi filter `idNganh`. |
| Repository | `PreRegistrationIntentRepository#aggregateDemand` | JPQL JOIN intent → học phần, sinh viên → lớp → ngành; filter optional `namNhapHoc`, `idNganh`. |
| DTO | `PreRegistrationDemandResponse`, `PreRegistrationDemandItemResponse` | Khớp bảng trong dev_spec. |
| Migration dữ liệu F08 | `migration_pre_registration_intent_sprint2.sql` | Tiền đề dữ liệu intent (BA §11). |

**Công thức `recommendedClasses`:** `max(1, ceil(totalIntent / classSize))` — khớp BA §7 và [`PreRegistrationDemandServiceImpl`](../../../../backend-core/src/main/java/com/example/demo/service/impl/PreRegistrationDemandServiceImpl.java) dòng 62–63.

---

## 4) Frontend

| Hạng mục | Trạng thái | Chi tiết |
|----------|------------|----------|
| Màn admin | **Có** | [`AdminPreRegistrationDemandPage.jsx`](../../../../frontend/src/pages/AdminPreRegistrationDemandPage.jsx) |
| Route | **Có** | `App.jsx`: `/admin/pre-registration-demand`. |
| Gọi API | **Đúng contract** | Query `hocKyId`, optional `namNhapHoc`, `idNganh`, `targetClassSize`. |
| Meta | **Đủ** | Nạp `GET /api/hoc-ky`, `GET /api/nganh-dao-tao`; mặc định HK hiện hành nếu có. |
| Wireframe BA §9 | **Gần khớp + mở rộng** | Có filter cohort/ngành/target, KPI tổng intent & tổng lớp gợi ý, bảng `maHocPhan` / intent / lớp gợi ý. |
| “Slider” what-if | **Chưa** | Hiện là **ô số** “Quy mô lớp mục tiêu”; đủ MVP, chưa có `<input type="range">` như wireframe gợi ý. |
| Export JSON/CSV | **Chưa** | BA nói admin có thể lưu screenshot/JSON thủ công — chưa có nút export trong UI. |

---

## 5) Kiểm thử tự động

| Loại | File | Nội dung chính |
|------|------|----------------|
| Unit (service) | `PreRegistrationDemandServiceImplTest.java` | HK không tồn tại; `ceil` và tổng; demand rỗng; custom `targetClassSize`; tối thiểu 1 lớp khi 1 intent. |
| Integration / `@WebMvcTest` controller | **Chưa có** | Có thể bổ sung sau để khóa cứng 403 STUDENT + 200 ADMIN. |

---

## 6) Phụ thuộc chuỗi (F08)

- **F08** (intent sinh viên) phải có dữ liệu thì bảng demand mới có nhiều dòng — khi rỗng, API **200 + items []** và totals 0 (**đạt** ngoại lệ BA §6).

---

## 7) Backlog đề xuất (ngoài MVP F04)

1. Slider hoặc preset nhanh (35 / 40 / 50) cho `targetClassSize`.  
2. Nút **Tải JSON** / CSV từ `data` hiện có (không cần endpoint mới).  
3. `@WebMvcTest` hoặc test bảo mật cho `GET .../demand` với JWT STUDENT vs ADMIN.  
4. Ghi nhận số **`eduport.prereg.target-class-size-default`** đang áp trong response `targetClassSize` để UI hiển thị rõ khi để trống ô (đã hiển thị số từ response sau khi load).

---

## 8) Kết luận

Chức năng F04 trong repo **đã sẵn sàng demo và bảo vệ luận văn** ở phạm vi: *đọc tổng hợp nhu cầu PRE để lên kế hoạch mở lớp (không tự động tạo lớp — đúng phạm vi BA)*. Việc còn lại là tùy chọn làm đẹp/đủ chứng cứ QA (export, slider, test MVC).
