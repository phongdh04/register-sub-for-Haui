# Ke hoach: De xuat mon hoc ca nhan hoa + Vong doi dang ky theo cohort

Tai lieu nay hop nhat hai bai toan lien quan chat che:

1. De xuat mon hoc ki tiep theo theo dung tien do tung sinh vien.
2. Vong doi dang ky 2 pha (du kien -> chinh thuc) theo tung khoa, voi luong mo lop tu dong va dong bo thoi khoa bieu sau khi dang ky.

---

## 0) Pham vi va Out-of-scope

## 0.1 Trong pham vi

- Hien thi danh sach mon dac an cho ki ke tiep cho tung sinh vien dua tren bang diem va tien do CTDT thuc te.
- Hai pha dang ky:
  - Pha A: dang ky du kien (thu thap nguyen vong).
  - Pha B: dang ky chinh thuc (cap slot lop that, sinh thoi khoa bieu).
- Admin cau hinh thoi gian rieng cho tung pha va tung cohort (vi du K17, K18).
- Admin nhan thong ke tu dong va de xuat so lop can mo sau khi pha A ket thuc.
- Tu dong gan lop da dang ky vao thoi khoa bieu ca nhan sinh vien sau khi pha B thanh cong.

## 0.2 Out-of-scope o tai lieu nay

- Bai toan thanh toan hoc phi.
- Bai toan lich thi va xet tot nghiep.
- Mobile push notification (chi de cap event hook).

---

## 1) 5 yeu cau nghiep vu chot voi admin

| ID | Yeu cau | Trang thai code hien tai |
|---|---|---|
| BR-1 | He thong hien duoc danh sach mon ki ke tiep theo dung tien do tung hoc vien | Da co API rule-based `GET /api/v1/registration/suggestions/me`. Thieu persistence + explainability + ranking ca nhan hoa. |
| BR-2 | Sau khi sinh vien dang ky du kien xong, admin xem duoc thong ke va he thong tu de xuat so lop can mo | Da co `ForecastMoLopServiceImpl` (du bao tu CTDT). Thieu phan tong hop tu **dang ky du kien thuc te** theo cohort/nganh. |
| BR-3 | Khi mo dang ky chinh thuc, moi lop phai hien lich hoc va giang vien | Da co `LopHocPhan.thoiKhoaBieuJson` + `giangVien`. Lop sinh tu shell co the chua co lich -> can workflow xep lich truoc khi mo. |
| BR-4 | Sau khi sinh vien dang ky thanh cong, lop tu dong vao thoi khoa bieu ca nhan | Da co `GET /api/v1/timetable/me` doc tu `DangKyHocPhan`. Da hoat dong mien la lop co `thoiKhoaBieuJson`. |
| BR-5 | Admin cau hinh duoc thoi gian 2 pha dang ky cho tung cohort | Da co `RegistrationScheduleChecker` + `AdminHocKyScheduleController`, nhung **chi cap hoc ky**, **chua phan theo cohort**. |

---

## 2) Hien trang codebase (doi chieu)

## 2.1 Da co va dang chay

- Data nen tang: `SinhVien -> Lop(nienKhoa) -> NganhDaoTao -> Khoa`, `BangDiemMon`, `DangKyHocPhan`, `ChuongTrinhDaoTao`, `CtdtHocPhan(hocKyGoiY, batBuoc)`, `HocPhan(dieuKienRangBuocJson)`, `HocKy`, `LopHocPhan(siSoToiDa, siSoThucTe, thoiKhoaBieuJson)`, `GioHangDangKy`.
- API/Service:
  - `GET /api/v1/registration/suggestions/me` (rule-based goi y).
  - `GET /api/v1/timetable/me` (TKB ca nhan).
  - `AdminSchedulingForecastController` (`/forecast`, `approve`, `reject`, `spawn-shell`).
  - `AdminSchedulingGridController` (`/snapshot`, `/conflict-check` cho phong/GV/cohort/capacity).
  - `AdminSchedulingSlotController` (PATCH slot 1 LHP).
  - `AdminGiangVienConstraintsController` (busy slot GV).
  - `AdminHocKyScheduleController` (cau hinh `lich-dang-ky` cap hoc ky).
- Luong dang ky chinh thuc qua **Kafka + Go queue + Java consumer**:
  - `DangKyHocPhanServiceImpl.processRegistration` chay chain `DuplicateRegistrationHandler -> ScheduleConflictHandler -> PrerequisiteCourseHandler` roi `incrementSiSoThucTe` (atomic UPDATE co WHERE guard) roi insert `DangKyHocPhan`.
- `RegistrationScheduleChecker` da kiem tra cua so pre + chinh thuc tren `HocKy` voi cap moc `(preDangKyMoTu, preDangKyMoDen)` va `(dangKyChinhThucTu, dangKyChinhThucDen)`.
- `CohortConflictRule` entity da ton tai cho rule cohort khi xep lich.

## 2.2 Khoang trong so voi 5 yeu cau nghiep vu

- Goi y mon hien tai chua dua **bang diem ca nhan** vao ranking, chua giai thich ly do.
- Khong co bang luu **dang ky du kien** cua sinh vien o pha A (chi co dashboard du bao theo CTDT/SV on-track).
- Khong co dashboard tong hop **demand thuc te** sau khi pha A dong.
- Khong co cua so dang ky **theo cohort** (hien dung cua so cap hoc ky).
- Lop shell sinh tu `spawn-shell` mac dinh `thoiKhoaBieuJson = []` -> can buoc xep lich + gan GV truoc khi cong bo cho pha B.

---

## 3) Cac phan con thieu (uu tien)

## P0 - Bat buoc

1. Bang `pre_registration_intent` luu nguyen vong dang ky du kien tung sinh vien theo cohort/nganh/ki.
2. Bang `registration_window` cau hinh thoi gian theo `(hocKyId, cohort, nganhId, phase)` voi phase = `PRE` hoac `OFFICIAL`.
3. Mo rong `RegistrationScheduleChecker` doc them `registration_window` theo cohort cua sinh vien dang dang ky, fallback ve `HocKy` neu khong co cau hinh cohort.
4. API admin tong hop demand sau pha A va goi `forecast` da co (mo rong tham so cohort).
5. Bang `recommendation_run` + `recommendation_item` luu ket qua de xuat ki ke tiep co `reason_codes` + `score_breakdown` cho moi sinh vien.
6. Workflow xep lich + gan GV cho cac LHP shell truoc khi cong bo pha B (tan dung `conflict-check` da co).
7. Optimistic lock (`version`) cho `lop_hoc_phan` de chac chan slot live khong oversell trong cao diem.

## P1 - Quan trong

1. Bang `recommendation_feedback` thu thap hanh vi (`ACCEPTED|DISMISSED|REGISTERED|IGNORED`).
2. Endpoint why-not cho moi mon: tai sao SV B chua duoc de xuat hoc phan A.
3. Dashboard chat luong de xuat (coverage, acceptance rate, stale).
4. Planner ke hoach 2-4 hoc ki cho sinh vien.
5. Event `REGISTRATION_CONFIRMED` chuan hoa de read-model TKB cap nhat trong < 30 giay (SLA).

## P2 - Mo rong khi can scale

1. A/B testing chien luoc xep hang de xuat.
2. Giam sat fairness theo khoa/nganh/cohort.
3. Offline evaluation pipeline (top-K acceptance proxy, completion lift).

---

## 4) Mo hinh du lieu can them

## 4.1 Cum **Recommendation** (P0/P1)

### 4.1.1 `recommendation_run`
- Muc dich: luu moi lan he thong tinh de xuat cho 1 sinh vien o 1 ki.
- Cot: `id`, `student_id`, `hoc_ky_id`, `ctdt_id`, `ctdt_version`, `strategy_version`, `generated_at`, `status`, `input_hash`.

### 4.1.2 `recommendation_item`
- Cot: `id`, `run_id`, `hoc_phan_id`, `lop_hoc_phan_id` nullable, `score_total`, `priority_rank`, `reason_codes` jsonb, `score_breakdown` jsonb, `eligibility_flags` jsonb, `recommended_credits`.

### 4.1.3 `recommendation_feedback`
- Cot: `id`, `student_id`, `recommendation_item_id`, `action`, `action_at`, `client_context` jsonb.

## 4.2 Cum **Registration lifecycle** (P0)

### 4.2.1 `registration_window`
- Cot: `id`, `hoc_ky_id`, `phase` (`PRE`|`OFFICIAL`), `cohort_code`, `nganh_id` nullable, `open_at`, `close_at`, `created_by`, `updated_at`.
- Index: `(hoc_ky_id, phase, cohort_code, nganh_id)` unique.
- Quy tac:
  - `nganh_id NULL` = ap dung cho moi nganh trong cohort.
  - Co the cau hinh nhieu cohort song song trong cung 1 hoc ky.

### 4.2.2 `pre_registration_intent`
- Cot: `id`, `student_id`, `hoc_ky_id`, `hoc_phan_id`, `priority` (1..n), `created_at`, `updated_at`.
- Unique `(student_id, hoc_ky_id, hoc_phan_id)`.
- Khong cap slot, khong block trung mon o pha nay.

### 4.2.3 `pre_registration_demand_snapshot`
- Cot: `id`, `hoc_ky_id`, `cohort_code`, `nganh_id`, `hoc_phan_id`, `total_intent`, `eligible_intent`, `recommended_classes`, `snapshot_at`.
- Snapshot duoc sinh khi pha A dong de admin xem va ra quyet dinh mo lop.

### 4.2.4 Mo rong `lop_hoc_phan` (KHONG doi ten cot cu)
- Giu nguyen `siSoToiDa`, `siSoThucTe` (da chay production).
- Them moi:
  - `version` (`@Version`) cho optimistic lock o cac luong cao diem.
  - `status_publish` enum `SHELL|SCHEDULED|PUBLISHED` de phan biet lop chua xep lich vs da san sang cho pha B.
  - `idempotency_pool` (optional) qua bang phu `registration_request_log`.

### 4.2.5 `registration_request_log`
- Cot: `id`, `idempotency_key`, `student_id`, `lop_hoc_phan_id`, `result`, `created_at`.
- Unique `idempotency_key` de chong replay/double-submit.

---

## 5) API contract

Quy uoc: phan **MO RONG** la them tham so cho endpoint da co; phan **MOI** la endpoint moi.

## 5.1 Sinh vien

- MOI `GET /api/v1/registration/recommendations/me?hocKyId=...`
  - tra danh sach mon ki ke tiep + reason_codes + score.
- MOI `GET /api/v1/registration/recommendations/me/why-not?hocPhanId=...`
  - tra ly do chua du dieu kien.
- MOI `POST /api/v1/registration/recommendations/me/feedback`
  - body `{recommendationItemId, action, clientContext?}`.
- MOI `POST /api/v1/pre-registrations/intents` va `DELETE /api/v1/pre-registrations/intents/{id}`
  - tao/sua/xoa nguyen vong dang ky du kien.
- MOI `GET /api/v1/pre-registrations/intents/me?hocKyId=...`
  - tra nguyen vong cua chinh sinh vien.

## 5.2 Admin - cau hinh thoi gian dang ky

- MOI `POST /api/v1/admin/registration-windows`
  - body `{hocKyId, phase, cohortCodes[], nganhIds[] optional, openAt, closeAt}`.
- MOI `GET /api/v1/admin/registration-windows?hocKyId=...&phase=...`
- MOI `PUT /api/v1/admin/registration-windows/{id}`
- MOI `DELETE /api/v1/admin/registration-windows/{id}`

## 5.3 Admin - thong ke + mo lop

- MOI `GET /api/v1/admin/pre-registrations/demand?hocKyId=...&cohort=K17&nganhId=...`
  - tra demand theo `hoc_phan x cohort x nganh`.
- MOI `POST /api/v1/admin/pre-registrations/snapshot:generate`
  - chot demand thanh `pre_registration_demand_snapshot`.
- MO RONG `POST /api/v1/admin/scheduling/hoc-ky/{hocKyId}/forecast`
  - them tham so `cohortCode`, `nganhId`, va che do `useDemandSnapshot=true` de tinh tu nguyen vong thuc te thay vi suy luan tu CTDT.
- TAI SU DUNG `POST /forecast-versions/{id}/approve`
  - approve phien.
- TAI SU DUNG `POST /forecast-versions/{id}/spawn-shell`
  - sinh LHP shell (status_publish = `SHELL`).
- MO RONG `PATCH /api/v1/admin/scheduling/lop-hoc-phan/{idLopHp}/slot`
  - sau khi xep lich + gan GV xong, chuyen `status_publish = SCHEDULED`.
- TAI SU DUNG `POST /conflict-check` truoc khi cong bo.
- MOI `POST /api/v1/admin/lop-hoc-phan/{idLopHp}:publish`
  - chuyen `status_publish = PUBLISHED` -> san sang cho pha B.
- MOI `POST /api/v1/admin/lop-hoc-phan:bulk-publish?hocKyId=...&cohort=K17`
  - cong bo hang loat khi het luot review.

## 5.4 Quan tri RBAC

- Tat ca endpoint `admin/*` yeu cau `ROLE_ADMIN`.
- `recommendation-feedback` yeu cau `ROLE_STUDENT`.

---

## 6) Luong nghiep vu end-to-end (4 giai doan)

## Giai doan 1 - Recommendation cho ki ke tiep

1. He thong (job + on-demand) tinh `recommendation_run` cho moi sinh vien thuoc cohort dang chuan bi mo dang ky.
2. Input bao gom:
   - bang diem (mon da dat, mon truot, mon hoan/khong tinh)
   - so tin chi tich luy
   - CTDT phien ban gan voi cohort cua sinh vien
   - tien quyet trong `HocPhan.dieuKienRangBuocJson`
3. Output:
   - moi mon de xuat co `priority_rank`, `score_total`, `reason_codes` (vi du `MON_BAT_BUOC_KI_GOI_Y`, `DA_DAT_TIEN_QUYET`, `RUOT_LAN_TRUOC`, `MON_TU_CHON_PHU_HOP_KHOI`).
4. Sinh vien xem o trang goi y, co hanh dong: them vao nguyen vong, bo qua, hoi why-not.

## Giai doan 2 - Pha A: dang ky du kien

1. Admin cau hinh `registration_window(phase=PRE)` cho cohort K17 (hoac nhom cohort) voi `openAt/closeAt`.
2. Sinh vien chon mon (xuat phat tu de xuat hoac tim them) -> ghi vao `pre_registration_intent`.
3. Cho phep nhieu mon, nhieu phuong an uu tien; trung mon chi canh bao, khong block.
4. Het thoi gian pha A:
   - he thong tao `pre_registration_demand_snapshot`.
   - canh bao mon co demand vuot suc chua du kien.

## Giai doan 3 - Tinh toan va mo lop

1. Admin xem dashboard demand theo cohort/nganh/mon.
2. Goi `runForecast` voi `useDemandSnapshot=true`:
   - cong thuc: `classes_needed = ceil(eligible_intent / target_class_size)` co he so du phong cho mon ti le rot cao.
3. `approve` phien -> `spawn-shell` sinh LHP voi `status_publish = SHELL`.
4. Admin xep lich va gan GV cho tung LHP:
   - chay `conflict-check` cho phong, GV (`gv_busy_slot`), capacity, cohort overlap.
   - khi lich hop le -> chuyen `status_publish = SCHEDULED`.
5. Truoc khi mo pha B, `bulk-publish` chuyen tat ca LHP da SCHEDULED sang `PUBLISHED`.

## Giai doan 4 - Pha B: dang ky chinh thuc

1. Admin cau hinh `registration_window(phase=OFFICIAL)` theo tung cohort.
2. Sinh vien dang ky:
   - chi nhin thay LHP `PUBLISHED` thuoc cohort/nganh duoc mo cua.
   - tren UI hien thi day du `thoiKhoaBieuJson` + ten giang vien truoc khi xac nhan.
3. He thong dang ky qua queue Kafka:
   - chain validate (trung lop, trung lich, tien quyet)
   - atomic `UPDATE lop_hoc_phan SET siSoThucTe = siSoThucTe + 1 WHERE id = ? AND siSoThucTe < siSoToiDa`
   - insert `DangKyHocPhan` voi `trangThaiDangKy = THANH_CONG`
   - publish event `REGISTRATION_CONFIRMED`.
4. Read-model TKB:
   - `GET /api/v1/timetable/me` doc tu `DangKyHocPhan` join `LopHocPhan.thoiKhoaBieuJson` va `giangVien` -> sinh vien thay ngay tren TKB.
5. Khi rut mon: doi xung qua `processCancellation` (decrement + chuyen `RUT_MON`) + event `REGISTRATION_CANCELLED`.

---

## 7) Yeu cau ky thuat live-safe

## 7.1 Slot allocation

- Nguon su that: `lop_hoc_phan.siSoToiDa` va `siSoThucTe`.
- Atomic update da co trong `LopHocPhanRepository.incrementSiSoThucTe`. Yeu cau:
  - giu trong cung transaction voi insert `DangKyHocPhan`.
  - `rows_affected = 0` -> tra loi `HET_CHO`.
- Bo sung `version` de tang an toan o cac luong batch admin (PATCH slot, publish).

## 7.2 Idempotency

- Header `Idempotency-Key` cho moi luong dang ky.
- Bang `registration_request_log(idempotency_key UNIQUE)`.
- Trung key trong cua so 24h -> tra ket qua cu, khong xu ly lai.

## 7.3 Chong trung dang ky

- Unique `(student_id, lop_hoc_phan_id)` tren `dang_ky_hoc_phan` ket hop chain handler hien co (`DuplicateRegistrationHandler`).
- Voi rang buoc nghiep vu "1 sinh vien 1 hoc phan/hoc ki": unique `(student_id, hoc_phan_id, hoc_ky_id)` chi tinh `trangThaiDangKy = THANH_CONG`.

## 7.4 Kafka + Redis

- Tan dung kien truc da co `backend-queue` Go + Redis + Kafka:
  - Redis: warm slot counter, rate limit per student, queue priority.
  - Kafka: dam bao xu ly tuan tu cho cung 1 lop (partition theo `lopHpId`).
- Java consumer giu nguyen co che `DangKyHocPhanServiceImpl.processRegistration`.

## 7.5 SLA va eventual consistency

- SLA cong bo: tu thoi diem dang ky thanh cong toi luc TKB hien tren UI < 30 giay.
- Read-model TKB phai idempotent theo `idDangKy`.

---

## 8) Quy trinh admin van hanh

## 8.1 Truoc pha A

- Cau hinh `registration_window(phase=PRE)` cho tung cohort.
- Phat hanh recommendation run cho toan bo sinh vien thuoc cohort.
- Kiem tra data chuan: `CtdtHocPhan.hocKyGoiY`, `HocPhan.dieuKienRangBuocJson`, `BangDiemMon.trangThai = PUBLISHED`.

## 8.2 Trong pha A

- Theo doi dashboard demand realtime theo cohort/nganh.
- Canh bao mon co demand vuot capacity.

## 8.3 Sau pha A

- Generate snapshot demand.
- Chay forecast voi snapshot, approve, spawn shell.
- Xep lich, gan GV, conflict-check tung lop.
- Bulk publish cac lop SCHEDULED -> PUBLISHED.

## 8.4 Truoc pha B

- Cau hinh `registration_window(phase=OFFICIAL)` cho tung cohort, co the lech thoi gian giua K17/K18.
- Smoke test luong dang ky tren moi truong staging.

## 8.5 Trong pha B

- Monitor metric: success rate, conflict rate, het cho rate, p95 latency.
- Sang cho cohort tiep theo khi ket thuc cua so.

## 8.6 Sau pha B

- Doi soat: dang ky thuc te vs nguyen vong vs de xuat.
- Ghi feedback `REGISTERED` cho recommendation_item tuong ung de cap nhat ranking.

---

## 9) Test plan

## 9.1 Unit/Service

- Recommendation: rule cohort, version CTDT, du dieu kien tien quyet, mon hoc lai/cai thien.
- Slot allocation: race condition co/khong co `version`.
- Eligibility cohort: SV ngoai cohort khong vao duoc cua dang ky tuong ung.

## 9.2 Integration

- End-to-end: PRE intent -> snapshot -> forecast -> shell -> schedule -> publish -> dang ky -> TKB.
- Why-not endpoint cho mon thieu tien quyet.
- Idempotency key: lap lai 5 lan, chi 1 dang ky thanh cong.

## 9.3 Load/Chaos

- 1k+ concurrent registration tren cung 1 lop, kiem tra khong oversell.
- Kafka consumer crash giua chung -> recovery khong duplicate.
- Redis down -> fallback ve DB.

## 9.4 Hoi quy

- Khong duoc lam vo `RegistrationScheduleChecker` cu (back-compat: thieu `registration_window` thi fallback ve `HocKy`).
- Khong duoc lam vo `incrementSiSoThucTe` semantics.

---

## 10) Roadmap thong nhat 6 sprint

| Sprint | Muc tieu | Output chinh |
|---|---|---|
| 1 | Cau hinh thoi gian theo cohort | Bang `registration_window` + 4 endpoint admin + mo rong `RegistrationScheduleChecker` |
| 2 | Pha A va dashboard demand | Bang `pre_registration_intent` + API SV + dashboard demand realtime |
| 3 | Mo lop tu demand thuc | Mo rong `forecast` voi `useDemandSnapshot`, workflow xep lich + gan GV + `status_publish` |
| 4 | Pha B live-safe | `version` cho LHP, `registration_request_log`, Idempotency-Key, event `REGISTRATION_CONFIRMED` |
| 5 | Recommendation engine | 3 bang recommendation, scoring co `reason_codes`, why-not endpoint |
| 6 | Hardening | Feedback loop, dashboard chat luong, load/chaos test, runbook su co |

---

## 11) Monitoring & KPI

## 11.1 Metric ky thuat

- p50/p95/p99 latency: dang ky, why-not, recommendation.
- Success rate / conflict rate / het cho rate / duplicate rate.
- Slot oversell attempt count (target = 0).
- Kafka consumer lag, Redis cache hit ratio.
- Deadlock/lock-wait count.

## 11.2 KPI nghiep vu

- Coverage: % sinh vien thuoc cohort co recommendation hop le.
- Acceptance rate: % de xuat duoc them vao intent va sau do dang ky chinh thuc.
- Capacity pressure: % mon co demand vuot suc chua du kien.
- TKB sync SLA: do tre `dang ky thanh cong -> hien tren TKB` (< 30s).
- Access coverage: % sinh vien du dieu kien cohort vao duoc cua dang ky tuong ung.

## 11.3 Alert bat buoc

- siSoThucTe > siSoToiDa (critical, ngat luong dang ky).
- error rate > nguong trong cua so 5 phut.
- Kafka lag > nguong.
- Tre dong bo TKB > 60s.

---

## 12) Migration & rui ro

## 12.1 Migration

- Bo sung cot `version` (`bigint default 0`) tren `lop_hoc_phan` voi backfill 0.
- Bo sung `status_publish` tren `lop_hoc_phan` voi default `PUBLISHED` cho lop hien hanh, `SHELL` cho lop sinh tu spawn-shell trong tuong lai.
- Tao bang moi: `registration_window`, `pre_registration_intent`, `pre_registration_demand_snapshot`, `registration_request_log`, `recommendation_run`, `recommendation_item`, `recommendation_feedback`.

## 12.2 Back-compat

- Khi `registration_window` rong, `RegistrationScheduleChecker` doc cua so cu tren `HocKy`.
- Khi `recommendation_run` chua co cho 1 sinh vien, fallback rule-based hien tai.

## 12.3 Rui ro chinh

- Du lieu CTDT cu khong co `hocKyGoiY` -> recommendation kem -> can data QA truoc khi trien khai cohort dau tien.
- Lop SHELL bi cong bo nham -> them check `status_publish = PUBLISHED` o tat ca query danh sach mo cua pha B.
- Rut mon ngoai cua so dang ky -> can policy ro va doi xung event `REGISTRATION_CANCELLED`.
- Sinh vien chuyen cohort/chuyen nganh giua chung -> them rule eligibility o `registration_window` resolver.

---

## 13) Action list ngay

1. Chot ten cohort la `Lop.nienKhoa` va chuan hoa thanh `cohort_code` (vi du `K17`).
2. Tao migration cum 4.2 truoc, bat dau tu `registration_window`.
3. Mo rong `RegistrationScheduleChecker` doc theo cohort cua sinh vien.
4. Bo sung `status_publish` va `version` cho `lop_hoc_phan`.
5. Lam dashboard demand sau khi co `pre_registration_intent`.
6. Tach data QA CTDT cho cohort thi diem (khoa K17) truoc khi rollout dien rong.

---

## 14) Doi chieu admin hien tai vs luong moi

Phan nay ra soat tat ca chuc nang admin dang co (ca backend va frontend) de quyet dinh cai gi tan dung, cai gi mo rong, cai gi xay moi.

## 14.1 Backend admin controllers

| Controller | URL prefix | Trang thai voi luong moi | Hanh dong |
|---|---|---|---|
| `AdminHocKyScheduleController` | `/api/v1/admin/hoc-ky/{id}/lich-dang-ky` | Chi cap **hoc ky**, khong co cohort | **Mo rong**: them `/registration-windows` cap cohort, giu endpoint cu lam fallback |
| `AdminSchedulingForecastController` | `/api/v1/admin/scheduling/hoc-ky/{id}/forecast`, `approve`, `reject`, `spawn-shell` | Tinh tu CTDT on-track, chua doc demand thuc te tu intent | **Mo rong**: them tham so `useDemandSnapshot`, `cohortCode`, `nganhId` |
| `AdminSchedulingGridController` | `/api/v1/admin/scheduling/hoc-ky/{id}/snapshot`, `conflict-check` | Da co conflict-check phong/GV/cohort/capacity | **Tan dung nguyen**, dung trong workflow xep lich |
| `AdminSchedulingSlotController` | `PATCH /api/v1/admin/scheduling/lop-hoc-phan/{id}/slot` | Da co | **Mo rong**: sau patch -> tu dong chuyen `status_publish = SCHEDULED` |
| `AdminSchedulingSolverController` | `/api/v1/admin/scheduling/hoc-ky/{id}/solver` | Solver auto-schedule | **Tan dung**: them buoc tu dong `gan GV` neu solver chua co |
| `AdminSchedulingPhongJsonController` | `phong-from-json` | Audit phong JSON | **Tan dung** |
| `AdminGiangVienConstraintsController` | `/giang-vien/{id}/constraints/busy-slots` | Busy slot GV | **Tan dung**, can them api gan GV vao LHP |
| `AdminScheduleChangeSetController` | `/change-sets` | Change set lich | **Tan dung** |
| `AdminTkbBlockController` | `/tkb-blocks` | Block TKB | **Tan dung** |
| `AdminPreRegistrationController` | `/api/v1/admin/pre-reg/links` | **CHU Y**: dang la pre-reg admission (link tuyen sinh), KHONG phai pre-reg dang ky mon | **Tach namespace** moi `/api/v1/admin/pre-registrations/intents` cho course pre-reg |
| `AdminAnalyticsController` | `/analytics/dashboard` | Generic dashboard | **Mo rong**: them widget cohort-aware (so SV cohort, ti le on-track, capacity pressure) |
| `AdminFinanceController` | `/finance` | Tai chinh | Khong lien quan luong nay |
| `AdminAuditTrailController` | `/audit-logs` | Da co | **Tan dung** ghi log thao tac registration_window + publish |
| `AdminMfaController`, `AdminPhongHocController` | n/a | Khong lien quan | Khong dung |

**Thieu hoan toan:**
- `AdminRegistrationWindowController` (4 endpoint CRUD).
- `AdminPreRegistrationIntentController` (admin xem intent + summary).
- `AdminClassPublishController` (chuyen `status_publish`, gan GV, bulk publish).
- `AdminRecommendationController` (trigger run, xem chat luong, why-not cho sinh vien cu the).

## 14.2 Frontend admin pages

| Page | Chuc nang hien tai | Trang thai | Hanh dong |
|---|---|---|---|
| `AdminHocKyScheduleConfigPage.jsx` | 4 datetime field cho 1 hoc ky | Chi 1 hoc ky / 1 cap window | **Refactor**: chuyen sang grid theo cohort + nganh, them nut "duplicate window cho cohort khac" |
| `QunLDanhMcKhungMLpDataMaster.jsx` | Block TKB + Forecast + Approve + Spawn shell | Da co tab Forecast | **Mo rong**: them tab "Demand thuc te tu pre-reg" + nut chay forecast voi `useDemandSnapshot` |
| `SetupCuHnhGiVngTrafficSplittingQueueControl.jsx` | Solver job control | Tot | **Tan dung** |
| `BoCoPhnTchAnalytics.jsx` | Analytics tong | Generic | **Mo rong**: them widget cohort registration funnel (intent -> approved class -> registered) |
| `LchSNhtKDuChnAuditTrailsLogging.jsx` | Audit log | Da co | **Tan dung**, them filter theo entity `registration_window`/`lop_hoc_phan` |
| `HThngPhnQuynaTngRbacRoleBasedAccessControl.jsx` | RBAC | Da co | **Mo rong**: them role moi neu can (vd `ROLE_REGISTRAR`) |
| `GimStTiChnhKTonAdmin.jsx` | Tai chinh | Khong lien quan | Khong dung |
| `XcThcaYuTMfa2FaVChKS.jsx` | MFA | Khong lien quan | Khong dung |

**Thieu hoan toan (page can xay):**
- `AdminCohortWindowsPage.jsx`: cau hinh windows theo `(cohort, phase)` cap hoc ky.
- `AdminPreRegDemandDashboard.jsx`: dashboard demand realtime trong pha A va sau pha A.
- `AdminClassPublishWorkbench.jsx`: workspace 1 man hinh: shell list -> gan GV -> xep lich -> conflict-check -> publish (tan dung component cu).
- `AdminRecommendationOpsPage.jsx`: trigger run, theo doi coverage / acceptance rate, why-not search cho 1 sinh vien.

## 14.3 Cai thien cu the can lam ngay (theo thu tu uu tien)

### Sprint 1 - Cohort window

1. **Backend**:
   - Tao `registration_window` entity + repository.
   - Them `AdminRegistrationWindowController` voi 4 endpoint CRUD.
   - Mo rong `RegistrationScheduleChecker.isPreRegistrationOpen(...)` va `isOfficialRegistrationOpen(...)` nhan them `cohortCode + nganhId` cua sinh vien, doc tu `registration_window` truoc, fallback ve `HocKy`.
   - Cap nhat `DangKyHocPhanServiceImpl` truyen cohort cua sinh vien vao checker.
2. **Frontend**:
   - Tao `AdminCohortWindowsPage.jsx`.
   - Refactor `AdminHocKyScheduleConfigPage.jsx`: tab 1 = "Mac dinh hoc ky" (giu UI cu), tab 2 = "Theo cohort/nganh" (UI moi).
3. **Audit**: log moi thay doi window vao `nhat_ky_hanh_dong` qua co che hien co.

### Sprint 2 - Pre-registration intent + demand dashboard

1. **Backend**:
   - Tao `pre_registration_intent` entity + service.
   - Endpoint sinh vien: `POST/DELETE /api/v1/pre-registrations/intents`, `GET /me`.
   - Endpoint admin: `GET /api/v1/admin/pre-registrations/demand`, `POST snapshot:generate`.
2. **Frontend**:
   - Trang sinh vien: them tab "Nguyen vong dang ky" trong page pre-registration hien co.
   - Trang admin: `AdminPreRegDemandDashboard.jsx` voi filter cohort/nganh, bang demand vs capacity.
3. **Tan dung**: `Course Search` + `Recommendation` da co de sinh vien nhanh chong them mon vao intent.

### Sprint 3 - Workflow mo lop voi schedule + GV

1. **Backend**:
   - Them `status_publish` (`SHELL|SCHEDULED|PUBLISHED`) cho `lop_hoc_phan`.
   - Them `version` (optimistic lock) cho `lop_hoc_phan`.
   - `AdminClassPublishController`:
     - `POST /api/v1/admin/lop-hoc-phan/{id}/assign-gv`
     - `POST /api/v1/admin/lop-hoc-phan/{id}:publish`
     - `POST /api/v1/admin/lop-hoc-phan:bulk-publish?hocKyId=...&cohort=K17`
   - Mo rong `forecast` voi `useDemandSnapshot=true`.
   - Block dang ky: handler check `status_publish = PUBLISHED` truoc khi insert.
2. **Frontend**:
   - Tao `AdminClassPublishWorkbench.jsx`.
   - Mo rong tab Forecast trong `QunLDanhMcKhungMLpDataMaster.jsx` them nut "Su dung demand thuc te".

### Sprint 4 - Live-safe & idempotency

1. **Backend**:
   - Tao `registration_request_log` (idempotency key UNIQUE).
   - Filter Idempotency-Key cho luong dang ky, tra cache result trong 24h.
   - Publish event `REGISTRATION_CONFIRMED` sau commit.
2. **Frontend**:
   - Truong hop sinh vien retry: hien message "Da xu ly truoc do" thay vi loi.

### Sprint 5 - Recommendation engine

1. **Backend**:
   - 3 bang `recommendation_run/item/feedback`.
   - `AdminRecommendationController`: trigger run, list runs, why-not.
   - Job nightly tao recommendation cho cohort sap mo dang ky.
2. **Frontend**:
   - `AdminRecommendationOpsPage.jsx`: chat luong + why-not search.
   - Sinh vien: them reason_codes va action feedback vao trang goi y.

### Sprint 6 - Hardening

1. Load test concurrent registration.
2. Chaos test Kafka consumer.
3. Runbook su co (overbook, lag consumer, timetable sync tre).
4. Mo rong `BoCoPhnTchAnalytics.jsx` voi metric cohort-aware.

## 14.4 Rui ro re-use va breaking change

- `AdminHocKyScheduleController` cu **khong duoc pha API** vi co the dang duoc dung; chi them endpoint moi.
- `AdminPreRegistrationController` la pre-reg tuyen sinh, **dat namespace khac** cho intent dang ky mon de tranh nham lan: `/api/v1/admin/pre-registrations/intents` vs `/api/v1/admin/pre-reg/links`.
- `LopHocPhan.trangThai` hien co (`DANG_MO`, `CHUA_MO`) khac voi `status_publish` moi -> can phan biet ro:
  - `trangThai` = trang thai van hanh (mo dang ky / dong / da ket thuc).
  - `status_publish` = trang thai vong doi cong bo (shell -> scheduled -> published).
  - Hai cot song song, khong gop.
- Khi chuyen `status_publish = PUBLISHED`, neu lop chua co `thoiKhoaBieuJson` hoac `giangVien`, **block** tai server (validation guard).

