# BA-Flow — F04 Phân tích nhu cầu PRE (Admin capacity planning)

| Mã chức năng | F04 |
|----------------|-----|
| Vai trò | ADMIN |
| Dev-Spec | [`dev_spec.md`](dev_spec.md) |
| Nguồn dữ liệu | [`pre_registration_intent`](../../cross/03_db_dictionary.md) — F08 |

---

## 1) Mục đích

Trong pha **pre-registration**, sinh viên thể hiện ý định học (intent) cho từng học phần. Đội học vụ cần một **dashboard đọc**:

- tổng số intent **theo học phần** trong học kỳ;
- có thể **thu hẹp phạm vi** theo cohort (`namNhapHoc`) và/hoặc ngành (`idNganh`) để lên kế hoạch mở lớp cho từng khóa;
- mô phỏng **bao nhiêu section** (`recommendedClasses`) là đủ với giả định một sĩ số mục tiêu `targetClassSize` (tham số truy vấn có thể chỉnh tay trên UI như “what-if” slider). Công thức khớp backend: \(\max(1, \lceil \texttt{totalIntent}/\texttt{targetClassSize}\rceil)\) cho mỗi dòng aggregate (xem [`PreRegistrationDemandServiceImpl`](../../../../backend-core/src/main/java/com/example/demo/service/impl/PreRegistrationDemandServiceImpl.java)).

F04 **không** tạo lớp học phần tự động — chỉ hỗ trợ quyết định con người (F03 sau đó).

---

## 2) Actor

| Actor | Nhiệm vụ |
|-------|----------|
| Phòng Đào tạo / Kế hoạch lớp | Xem report, export số liệu, thống nhất `targetClassSize` với quy chế sĩ số tối đa |
| Auditor | Đối chiếu tổng intent với báo cáo thủ công (out of scope tool) |

---

## 3) Tiền điều kiện

- JWT `ROLE_ADMIN`.
- Intent đã được SV gửi (F08); bảng không rỗng **hoặc** chấp nhận danh sách rỗng (0 item).
- Biết `hocKyId` của kỳ PRE đang quan tâm.

---

## 4) Hậu điều kiện

- Không thay đổi trạng thái nghiệp vụ; API **read-only**.
- Admin có thể lưu screenshot/JSON phục vụ biên bản họp mở lớp.

---

## 5) Luồng chính (Main)

1. Admin mở màn **PRE Demand** (tên gợi ý).
2. Chọn học kỳ bắt buộc (`hocKyId`).
3. Tuỳ chọn: chọn năm nhập học (lọc cohort) và/hoặc ngành.
4. Tuỳ chọn: chỉnh **`targetClassSize`** (ví dụ 35, 40, 50) để xem lại số lớp đề xuất.
5. Hệ thống trả về:
   - metadata phạm vi (`tenHocKy`, `namNhapHoc`, `tenNganh` nếu filter);
   - **`totalIntents`** tổng dòng intent (theo aggregate service);
   - **`totalRecommendedClasses`** tổng \(\sum\) `recommendedClasses` theo từng học phần;
   - `items[]`: từng `maHocPhan`, `intentCount`, `recommendedClasses`.

---

## 6) Luồng phụ / ngoại lệ

| Case | Hệ thống | BA ghi chú |
|------|----------|------------|
| Thiếu `hocKyId` | 400 | Form validation client |
| `hocKyId` không tồn tại | 404 | Refresh danh mục HK |
| Không có intent | 200 + list rỗng, totals = 0 | “Chưa có dữ liệu PRE” chứ không phải lỗi |
| `targetClassSize ≤ 0` hoặc không gửi | Backend dùng **`eduport.prereg.target-class-size-default`** (default 40) — xem dev_spec | UI nên hiển thị đang dùng default |

---

## 7) Công thức nghiệp vụ (khớp code hiện tại)

Cho mỗi học phần có dòng aggregate:

\[
\text{recommendedClasses}_i = \max\left(1,\ \left\lceil \frac{\text{totalIntent}_i}{\text{targetClassSize}} \right\rceil\right)
\]

Tổng số lớp đề xuất (`totalRecommendedClasses`) = \(\sum_i \text{recommendedClasses}_i\).

*Nếu không có học phần nào trong kết quả SQL (intent rỗng), danh sách items rỗng và `totalRecommendedClasses = 0`.*

---

## 8) GIVEN-WHEN-THEN

| ID | Rule |
|----|------|
| F04-R1 | **GIVEN** filter cohort/ngành **WHEN** không có intent khớp **THEN** `items` có thể rỗng nhưng HK vẫn hợp lệ. |
| F04-R2 | **GIVEN** đổi `targetClassSize` **WHEN** gọi lại API **THEN** `recommendedClasses` thay đổi tuyến tính theo ceil. |

---

## 9) Wireframe ASCII

```
┌── Nhu cầu PRE — HK2 2024-2025 ─────────────────────────────┐
│ Cohort [2023 ▼]  Ngành [CNTT ▼]   Sĩ số mục tiêu [ 40 ] [Lọc] │
│ Tổng intent: 842   | Tổng lớp đề xuất: 27                    │
├──────────┬──────────┬──────────┬───────────────────────────┤
│ Mã HP    │ Tên      │ Intent   │ Đề xuất lớp               │
└──────────┴──────────┴──────────┴───────────────────────────┘
```

---

## 10) Acceptance criteria

- [x] ADMIN-only; SV **403** — `@PreAuthorize('ADMIN')` trên controller; 403 STUDENT do security toàn cục (chưa có `@WebMvcTest` riêng — xem [`progress_report.md`](progress_report.md) §2).
- [x] Bắt buộc `hocKyId`; response khớp aggregation SQL (`PreRegistrationIntentRepository.aggregateDemand` GROUP BY học phần / cohort / ngành).
- [x] Default `targetClassSize` khi tham số xấu phải nhất quán với property backend (`eduport.prereg.target-class-size-default`).
- [x] Tổng trên banner khớp tổng phần tử `items` — đã verify trong [`PreRegistrationDemandServiceImplTest`](../../../../backend-core/src/test/java/com/example/demo/service/impl/PreRegistrationDemandServiceImplTest.java).

**Báo cáo tiến độ chi tiết:** [`progress_report.md`](progress_report.md) (cập nhật 2026-05-09).

---

## 11) Phụ thuộc

- F08 gửi intent.
- Migration `migration_pre_registration_intent_sprint2.sql` (đường dẫn trong repo).
- Luồng vận hành end-to-end sau PRE (lớp, GV, TKB, cửa đăng ký): **[F17](../F17_registration_capacity_planning_pipeline/ba_flow.md)**.

---

## 12) Lịch sử

| Ngày | Ghi chú |
|------|---------|
| 2026-05 | Khởi tạo |
| 2026-05 | Wireframe, công thức ngoại lệ, nhấn read-only |
| 2026-05-09 | Rà implementation; cập nhật §10; thêm [`progress_report.md`](progress_report.md) |
