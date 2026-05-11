# Dev-Spec — F17 Kế hoạch năng lực đăng ký (pipeline PRE → lớp → đăng ký)

| Trường | Giá trị |
|--------|---------|
| Mã chức năng | F17 |
| Tên chức năng | Pipeline tích hợp + backlog kỹ thuật |
| Liên kết BA-Flow | [`ba_flow.md`](ba_flow.md) |
| Module backend | `backend-core` (tham chiếu); **không** bắt buộc class Java riêng tên F17 |
| Module frontend | Gợi ý: trang tổng hợp admin (chưa bắt buộc) |
| Trạng thái | Draft |

---

## 1) Tóm tắt kỹ thuật

F17 là **đặc tả quy trình và phụ thuộc** giữa các module đã có. **BL-01** đã implement: `POST /api/v1/admin/pre-registrations/plan-sections` sinh hàng loạt `lop_hoc_phan` shell (`SHELL`, `CHUA_MO`) từ intent PRE hoặc từ `sectionCount` tay.

Phần **mục 8** mô tả hợp đồng API đó; backlog UI wizard / export vẫn mở.

---

## 2) Phụ thuộc

### 2.1 Trong repo

| Thành phần | Vai trò |
|------------|---------|
| `PreRegistrationIntent` / `PreRegistrationIntentRepository.aggregateDemand` | Nguồn số liệu F04 |
| `PreRegistrationDemandServiceImpl` | `recommendedClasses`, `targetClassSize` |
| `PreRegistrationPlanSectionsServiceImpl` | BL-01: tạo shell `lop_hoc_phan` từ aggregate |
| `AdminPreRegistrationDemandController` | `GET .../demand`, `POST .../plan-sections` |
| `RegistrationWindow` + `RegistrationScheduleChecker` | Cửa mở/đóng theo bản ghi window (F02) |
| `ClassPublishServiceImpl` + `AdminClassPublishController` | GV, publish, lifecycle (F03) |
| `RegistrationServiceImpl` / `DangKyHocPhanServiceImpl` | Đăng ký F10 |
| `RegistrationMonitoringServiceImpl` | F05 |

### 2.2 Cấu hình

| Key | Mặc định (tham khảo) | Ý nghĩa |
|-----|----------------------|---------|
| `eduport.prereg.target-class-size-default` | `40` | F04 khi không truyền `targetClassSize` hợp lệ |

---

## 3) Hợp đồng API (pipeline)

Admin / hệ thống dùng lần lượt (tuỳ triển khai UI):

| Thứ tự gợi ý | Method | URL (namespace) | Ghi chú |
|---------------|--------|-----------------|--------|
| A | GET | `/api/v1/admin/pre-registrations/demand` | Query: `hocKyId` (bắt buộc), `namNhapHoc`, `idNganh`, `targetClassSize` |
| A2 | POST | `/api/v1/admin/pre-registrations/plan-sections` | JSON body: `PreRegistrationPlanSectionsRequest`; header tuỳ chọn `Idempotency-Key`. **201** khi tạo mới, **200** khi replay (mã lớp đã đủ). |
| B | (CRUD) | Lớp học phần theo học kỳ | Tạo section lẻ — vẫn dùng được song song với A2 |
| C | POST | `/api/v1/admin/.../assign-giang-vien` | Xem `AdminClassPublishController` |
| D | POST | `/api/v1/admin/.../publish` / `bulk-publish` | F03 |
| E | CRUD | `/api/v1/admin/registration-windows` | F02 |
| F | POST | `/api/v1/registrations` | F10 (SV) |

Chi tiết status code: tham chiếu [`cross/04_api_catalog.md`](../../cross/04_api_catalog.md) khi được cập nhật; nếu lệch, ưu tiên **code controller** làm nguồn sự thật.

---

## 4) Luồng dữ liệu (lineage)

```
pre_registration_intent (sinh_vien × hoc_phan × hoc_ky)
    → aggregate SQL (GROUP BY hoc_phan × cohort × nganh)
    → PreRegistrationDemandResponse.items[].{totalIntent, recommendedClasses}
    → POST plan-sections hoặc tạo tay → lop_hoc_phan (N rows per hoc_phan)
    → assign giang_vien + TKB JSON
    → status_publish PUBLISHED
    → registration_window (PRE / OFFICIAL)
    → dang_ky_hoc_phan (F10)
```

---

## 5) Sequence (text) — happy path

1. **Client** (admin UI): `GET .../demand?hocKyId=H&targetClassSize=60`.
2. **Server**: trả `items`; UI hiển thị bảng + tổng `totalRecommendedClasses`.
3. **Admin** gọi `plan-sections` hoặc tạo `lop_hoc_phan` thủ công theo biên bản.
4. **Admin**: assign GV → nhập lịch → publish (F03).
5. **Admin**: tạo `registration_window` phase OFFICIAL (F02).
6. **Student**: discovery (F09) → `POST /api/v1/registrations` (F10).
7. **Admin**: F05 theo dõi.

---

## 6) Ràng buộc nhất quán (để dev không regress)

| Ràng buộc | Nguồn |
|-----------|--------|
| Không có `registration_window` khớp scope → đăng ký PRE/OFFICIAL **đóng** | `RegistrationScheduleChecker` |
| Publish lớp cần GV + schedule đủ điều kiện | `ClassPublishServiceImpl.ensurePublishable` (F03 dev-spec) |
| `recommendedClasses` tối thiểu 1 khi `totalIntent > 0` | `Math.max(1, ceil(...))` trong `PreRegistrationDemandServiceImpl` |

---

## 7) Test gợi ý (pipeline)

| Layer | File / ý tưởng |
|-------|----------------|
| Unit | `PreRegistrationDemandServiceImplTest` — đúng ceil và tổng |
| Unit | `RegistrationScheduleCheckerTest` — không fallback HocKy |
| Integration | `@WebMvcTest` hoặc MockMvc cho `/pre-registrations/demand` + security ADMIN |
| E2E / demo | [`cross/09_demo_script.md`](../../cross/09_demo_script.md) — bổ sung bước: demand → tạo lớp → publish → window → SV đăng ký |

---

## 8) BL-01 — `POST /api/v1/admin/pre-registrations/plan-sections` (đã implement)

**Body** (`PreRegistrationPlanSectionsRequest`):

| Field | Bắt buộc | Mô tả |
|-------|----------|--------|
| `hocKyId` | Có | Học kỳ mục tiêu |
| `idHocPhan` | Có | Học phần cần mở section |
| `namNhapHoc`, `idNganh` | Không | Khớp tham số aggregate F04; null = gom **mọi** dòng cùng `idHocPhan` (cộng intent) |
| `sectionCount` | Không | Nếu có (1–200): tạo đúng N lớp, **bỏ qua** nhu cầu PRE |
| `useRecommendedFromDemand` | Không | Mặc định true khi không gửi `sectionCount`; `false` bắt buộc đi kèm `sectionCount` |
| `targetClassSize` | Không | Cho nhánh recommended; null → `eduport.prereg.target-class-size-default` |
| `siSoToiDa` | Không | Ghi vào shell; null → cùng giá trị class size đã chọn |

**Header:** `Idempotency-Key` (tuỳ chọn) — trộn vào SHA-256 tạo suffix mã lớp 8 hex (`_R{hex}_`).

**Mã lớp:** `PreRegistrationPlanShellMaBuilder` (tối đa 30 ký tự, tương thích `ma_lop_hp`).

**Luồng nội bộ:**

1. `aggregateDemand` → cộng `totalIntent` mọi dòng có `id_hoc_phan` khớp (trong phạm vi lọc).
2. \(N = \max(1, \lceil \text{sumIntent} / \text{classSize} \rceil)\) khi dùng recommended; `N > 200` → **400**.
3. Nếu **đủ** `N` mã đã tồn tại → **200**, `idempotentReplay=true`, không insert, không `bump` TKB.
4. Nếu **một phần** mã tồn tại → **409** (trạng thái lệ).
5. Ngược lại insert `N` shell giống forecast shell (`SHELL`, `CHUA_MO`, TKB rỗng), `bumpAfterTkbMutation` → **201**.

**Response:** `PreRegistrationPlanSectionsResponse` (`createdLopHpIds`, `sectionCountPlanned`, `totalIntentSnapshot`, …).

**Code:** `PreRegistrationPlanSectionsServiceImpl`, test `PreRegistrationPlanSectionsServiceImplTest`.

---

## 9) An ninh

- Mọi endpoint mục 3 (bảng A–F): `ROLE_ADMIN` (hoặc tương đương theo `SecurityConfig`).
- Demand không chứa PII chi tiết SV; chỉ aggregate — vẫn coi là dữ liệu nội bộ Đào tạo.

---

## 10) Checklist triển khai tài liệu

- [x] `README.md` mục lục thesis có dòng F17.
- [x] F04 `ba_flow.md` mục 11 đã có link F17.

---

## 11) Lịch sử

| Ngày | Ghi chú |
|------|---------|
| 2026-05-10 | Khởi tạo dev-spec F17 |
