import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { API_BASE_URL, authHeaders } from '../config/api';
import { rememberStudentHocKy, resolveInitialStudentHocKyFromRows } from '../utils/studentSemesterPersistence';

const safeArray = (x) => (Array.isArray(x) ? x : []);

async function parseBody(res) {
  const text = await res.text();
  if (!text) return {};
  try {
    return JSON.parse(text);
  } catch {
    return { message: text };
  }
}

function errMsg(b, fallback) {
  if (!b || typeof b !== 'object') return fallback;
  if (b.message) return String(b.message);
  if (Array.isArray(b.errors) && b.errors[0]?.defaultMessage) return String(b.errors[0].defaultMessage);
  return fallback;
}

function formatInstantVi(iso) {
  if (!iso) return '—';
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return String(iso);
  return d.toLocaleString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
}

function formatTkb(thoiKhoaBieuJson) {
  if (!Array.isArray(thoiKhoaBieuJson) || thoiKhoaBieuJson.length === 0) return '—';
  return thoiKhoaBieuJson
    .map((s) => {
      const thu = s?.thu === 8 ? 'CN' : `T${s?.thu ?? '?'}`;
      const tiet = s?.tiet ?? s?.tiet_bat_dau ?? '';
      const phong = s?.phong ?? s?.id_phong_hoc ?? '';
      return `${thu} tiết ${tiet}${phong ? ` · ${phong}` : ''}`;
    })
    .join(' / ');
}

const StudentRegistrationPage = () => {
  const [hocKys, setHocKys] = useState([]);
  const [hocKyId, setHocKyId] = useState('');
  const [windowStatus, setWindowStatus] = useState(null);
  const [registered, setRegistered] = useState([]);
  const [classes, setClasses] = useState([]);
  const [classesPage, setClassesPage] = useState({ page: 0, size: 20, totalPages: 1, total: 0 });
  const [keyword, setKeyword] = useState('');
  const [chiConCho, setChiConCho] = useState(true);
  const [busyId, setBusyId] = useState(null);
  const [loadingList, setLoadingList] = useState(false);
  const [loadingMine, setLoadingMine] = useState(false);
  const [errMsgList, setErrMsgList] = useState('');
  const [toast, setToast] = useState(null);
  const [preIntents, setPreIntents] = useState([]);
  const [loadingPre, setLoadingPre] = useState(false);
  const [sectionsFromPre, setSectionsFromPre] = useState({});
  const [loadingPreSections, setLoadingPreSections] = useState(false);
  const [snapshotRows, setSnapshotRows] = useState([]);
  const [loadingSnapshot, setLoadingSnapshot] = useState(false);
  const toastTimerRef = useRef(null);
  const navigate = useNavigate();

  const showToast = (kind, title, detail) => {
    setToast({ kind, title, detail });
    if (toastTimerRef.current) window.clearTimeout(toastTimerRef.current);
    toastTimerRef.current = window.setTimeout(() => setToast(null), 4500);
  };

  const tenHocKyChon = useMemo(() => {
    const id = Number(hocKyId);
    const hk = hocKys.find((h) => Number(h.idHocKy ?? h.id) === id);
    return hk?.tenHocKy || hk?.ten || windowStatus?.tenHocKy || '—';
  }, [hocKys, hocKyId, windowStatus]);

  const registeredIds = useMemo(
    () => new Set(registered.map((r) => Number(r.idLopHp))),
    [registered]
  );

  const tongTinChi = useMemo(
    () => registered.reduce((s, r) => s + (Number(r.soTinChi) || 0), 0),
    [registered]
  );

  const loadHocKy = useCallback(async () => {
    const res = await fetch(`${API_BASE_URL}/api/hoc-ky`, { headers: authHeaders() });
    const body = await parseBody(res);
    if (!res.ok) throw new Error(errMsg(body, 'Không tải được danh sách học kỳ.'));
    const rows = safeArray(body);
    setHocKys(rows);
    setHocKyId((cur) => {
      if (cur) return cur;
      if (rows.length === 0) return '';
      const current = rows.find((h) => h.trangThaiHienHanh === true);
      if (current) return String(current.idHocKy ?? current.id);
      return String(rows[0].idHocKy ?? rows[0].id);
    });
  }, []);

  const loadWindowStatus = useCallback(async () => {
    if (!hocKyId) return;
    const qs = `?hocKyId=${encodeURIComponent(hocKyId)}`;
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/registrations/me/window-status${qs}`, {
        headers: authHeaders()
      });
      const body = await parseBody(res);
      if (!res.ok) {
        setWindowStatus({
          idHocKy: Number(hocKyId),
          tenHocKy: '',
          maSinhVien: '',
          hoTenSinhVien: '',
          tenLop: '',
          namNhapHoc: null,
          idNganh: null,
          tenNganh: '',
          activePhase: 'NONE',
          dangKyAnyDangMo: false,
          preDangKyDangMo: false,
          dangKyChinhThucDangMo: false,
          debugReason:
            typeof body?.message === 'string'
              ? body.message
              : 'Không đọc được trạng thái đăng ký (API lỗi hoặc hết phiên đăng nhập). Phiên không được coi là mở.'
        });
        return;
      }
      setWindowStatus(body);
    } catch {
      setWindowStatus({
        idHocKy: Number(hocKyId),
        tenHocKy: '',
        maSinhVien: '',
        hoTenSinhVien: '',
        tenLop: '',
        namNhapHoc: null,
        idNganh: null,
        tenNganh: '',
        activePhase: 'NONE',
        dangKyAnyDangMo: false,
        preDangKyDangMo: false,
        dangKyChinhThucDangMo: false,
        debugReason: 'Không kết nối được máy chủ hoặc mạng. Phiên đăng ký không được coi là mở.'
      });
    }
  }, [hocKyId]);

  const loadMine = useCallback(async () => {
    if (!hocKyId) return;
    setLoadingMine(true);
    try {
      const qs = `?hocKyId=${encodeURIComponent(hocKyId)}`;
      const res = await fetch(`${API_BASE_URL}/api/v1/registrations/me${qs}`, {
        headers: authHeaders()
      });
      const body = await parseBody(res);
      if (!res.ok) throw new Error(errMsg(body, 'Không tải được lớp đã đăng ký.'));
      setRegistered(safeArray(body));
    } catch (e) {
      showToast('error', 'Lỗi', e.message || 'Không tải được.');
      setRegistered([]);
    } finally {
      setLoadingMine(false);
    }
  }, [hocKyId]);

  const loadPreIntents = useCallback(async () => {
    if (!hocKyId) return;
    setLoadingPre(true);
    try {
      const res = await fetch(
        `${API_BASE_URL}/api/v1/pre-registrations/intents/me?hocKyId=${encodeURIComponent(hocKyId)}`,
        { headers: authHeaders() }
      );
      const body = await parseBody(res);
      if (!res.ok) {
        setPreIntents([]);
        return;
      }
      setPreIntents(safeArray(body));
    } catch {
      setPreIntents([]);
    } finally {
      setLoadingPre(false);
    }
  }, [hocKyId]);

  const loadSnapshot = useCallback(async () => {
    if (!hocKyId) return;
    setLoadingSnapshot(true);
    try {
      const qs = `?hocKyId=${encodeURIComponent(hocKyId)}`;
      const res = await fetch(`${API_BASE_URL}/api/v1/timetable/me/snapshot${qs}`, {
        headers: authHeaders()
      });
      const body = await parseJson(res);
      if (!res.ok) throw new Error();
      setSnapshotRows(safeArray(body.entries ?? body));
    } catch {
      setSnapshotRows([]);
    } finally {
      setLoadingSnapshot(false);
    }
  }, [hocKyId]);

  const parseJson = async (res) => {
    const t = await res.text();
    if (!t) return {};
    try {
      return JSON.parse(t);
    } catch {
      return {};
    }
  };

  const loadClasses = useCallback(
    async (page = 0) => {
      if (!hocKyId) return;
      if (!windowStatus?.preDangKyDangMo) {
        return;
      }
      setLoadingList(true);
      setErrMsgList('');
      try {
        const qs = new URLSearchParams({
          idHocKy: hocKyId,
          page: String(page),
          size: '20',
          sortBy: 'tenHocPhan',
          sortDir: 'ASC',
          trangThai: 'DANG_MO'
        });
        if (keyword.trim()) qs.set('keyword', keyword.trim());
        if (chiConCho) qs.set('chiConCho', 'true');
        const res = await fetch(`${API_BASE_URL}/api/v1/courses?${qs}`, {
          headers: authHeaders()
        });
        const body = await parseBody(res);
        if (!res.ok) throw new Error(errMsg(body, 'Không tải được danh sách lớp.'));
        const content = safeArray(body.content ?? body);
        setClasses(content);
        setClassesPage({
          page: Number(body.number ?? body.page ?? 0),
          size: Number(body.size ?? 20),
          totalPages: Number(body.totalPages ?? 1),
          total: Number(body.totalElements ?? content.length)
        });
      } catch (e) {
        setErrMsgList(e.message || 'Lỗi tải danh sách lớp.');
        setClasses([]);
      } finally {
        setLoadingList(false);
      }
    },
    [hocKyId, keyword, chiConCho, windowStatus?.preDangKyDangMo]
  );

  useEffect(() => {
    (async () => {
      try {
        await loadHocKy();
      } catch (e) {
        showToast('error', 'Lỗi', e.message || 'Không tải được học kỳ.');
      }
    })();
  }, [loadHocKy]);

  useEffect(() => {
    if (!hocKyId) return;
    loadWindowStatus();
    loadMine();
    loadPreIntents();
    loadSnapshot();
  }, [hocKyId, loadWindowStatus, loadMine, loadPreIntents, loadSnapshot]);

  /** Khi trạng thái cửa sổ cập nhật (vd. admin mới mở pha OFFICIAL) — tải lại PRE intent khớp hocKy hiện tại. */
  useEffect(() => {
    if (!hocKyId) return;
    loadPreIntents();
  }, [hocKyId, windowStatus?.activePhase, loadPreIntents]);

  /** Chỉ trong pha PRE (kể cả khi PRE chạy song song OFFICIAL) mới được duyệt/danh mục mọi lớp như luồng dự kiến. */
  const showFullCourseBrowse = !!(windowStatus && windowStatus.preDangKyDangMo === true);

  const browseClosedHint = useMemo(() => {
    if (!hocKyId || !windowStatus) return null;
    if (showFullCourseBrowse) return null;
    if (!windowStatus.dangKyAnyDangMo) {
      return 'Phiên đăng ký chưa mở (hoặc đã đóng) cho khóa/ngành của bạn — không hiển thị danh mục lớp tự do.';
    }
    return 'Pha đăng ký chính thức: chỉ đăng ký được các học phần đã có trong đăng ký dự kiến. Chọn lớp trong mục «Từ đăng ký dự kiến» bên dưới.';
  }, [hocKyId, windowStatus, showFullCourseBrowse]);

  useEffect(() => {
    if (!hocKyId) return;
    if (!showFullCourseBrowse) {
      setClasses([]);
      setClassesPage({ page: 0, size: 20, totalPages: 1, total: 0 });
      setLoadingList(false);
      setErrMsgList('');
      return;
    }
    loadClasses(0);
  }, [hocKyId, showFullCourseBrowse, loadClasses]);

  const registrationPhaseInfo = useMemo(() => {
    const ap = windowStatus?.activePhase || 'NONE';
    return {
      activePhase: ap,
      isPreOnly: ap === 'PRE',
      isOfficial: ap === 'OFFICIAL' || ap === 'PRE_AND_OFFICIAL'
    };
  }, [windowStatus]);

  const registeredHocPhanIds = useMemo(
    () => new Set(registered.map((r) => Number(r.idHocPhan)).filter((n) => !Number.isNaN(n))),
    [registered]
  );

  const pendingFromPre = useMemo(() => {
    const rows = [...preIntents].sort((a, b) => (a.priority ?? 999) - (b.priority ?? 999));
    return rows.filter((it) => {
      const idHp = Number(it.idHocPhan);
      return it.idHocPhan != null && !Number.isNaN(idHp) && !registeredHocPhanIds.has(idHp);
    });
  }, [preIntents, registeredHocPhanIds]);

  const preIntentKey = useMemo(
    () => pendingFromPre.map((i) => `${i.id}:${i.idHocPhan}`).join('|'),
    [pendingFromPre]
  );

  useEffect(() => {
    if (
      !hocKyId ||
      !registrationPhaseInfo.isOfficial ||
      pendingFromPre.length === 0
    ) {
      setSectionsFromPre({});
      setLoadingPreSections(false);
      return undefined;
    }

    const ac = new AbortController();
    setLoadingPreSections(true);
    const run = async () => {
      const next = {};
      try {
        await Promise.all(
          pendingFromPre.map(async (it) => {
            const idHp = Number(it.idHocPhan);
            const qs = new URLSearchParams({
              idHocKy: String(hocKyId),
              idHocPhan: String(idHp),
              page: '0',
              size: '50',
              sortBy: 'maLopHp',
              sortDir: 'ASC',
              trangThai: 'DANG_MO'
            });
            if (chiConCho) qs.set('chiConCho', 'true');
            const res = await fetch(`${API_BASE_URL}/api/v1/courses?${qs}`, {
              headers: authHeaders(),
              signal: ac.signal
            });
            const body = await parseBody(res);
            if (!res.ok || ac.signal.aborted) return;
            next[idHp] = safeArray(body.content ?? body);
          })
        );
        if (!ac.signal.aborted) setSectionsFromPre(next);
      } catch {
        if (!ac.signal.aborted) setSectionsFromPre({});
      } finally {
        if (!ac.signal.aborted) setLoadingPreSections(false);
      }
    };
    run();
    return () => ac.abort();
  }, [hocKyId, registrationPhaseInfo.isOfficial, preIntentKey, chiConCho, pendingFromPre]);

  const doSearch = (e) => {
    e?.preventDefault?.();
    loadClasses(0);
  };

  const handleRegister = async (lhp) => {
    if (!hocKyId) return;
    if (!windowStatus?.dangKyAnyDangMo) {
      showToast('warn', 'Chưa mở phiên', 'Hệ thống không ghi nhận đăng ký khi phiên đăng ký đã đóng.');
      return;
    }
    if (registeredIds.has(Number(lhp.idLopHp))) {
      showToast('warn', 'Đã đăng ký', `Lớp ${lhp.maLopHp} đã có trong danh sách của bạn.`);
      return;
    }
    setBusyId(`reg-${lhp.idLopHp}`);
    try {
      const qs = new URLSearchParams({ idLopHp: String(lhp.idLopHp), hocKyId: String(hocKyId) });
      const res = await fetch(`${API_BASE_URL}/api/v1/registrations?${qs}`, {
        method: 'POST',
        headers: authHeaders()
      });
      const body = await parseBody(res);
      if (!res.ok) throw new Error(errMsg(body, 'Không đăng ký được.'));
      setRegistered(safeArray(body.items));
      if (windowStatus?.preDangKyDangMo) await loadClasses(classesPage.page);

      /* Poll snapshot — projection chạy async sau transaction commit (1-3s) */
      await reloadSnapshotWithRetry(lhp.maLopHp);
      showToast('ok', 'Đăng ký thành công', `${lhp.maLopHp} — ${lhp.tenHocPhan}`);
    } catch (e) {
      showToast('error', 'Đăng ký thất bại', e.message || 'Lỗi không xác định.');
    } finally {
      setBusyId(null);
    }
  };

  /** Retry polling snapshot cho đến khi môn vừa đăng ký xuất hiện (max ~8s). */
  async function reloadSnapshotWithRetry(maLopHpTarget) {
    const maxAttempts = 8;
    for (let i = 0; i < maxAttempts; i++) {
      await delay((i + 1) * 600);
      try {
        const qs = `?hocKyId=${encodeURIComponent(hocKyId)}`;
        const res = await fetch(`${API_BASE_URL}/api/v1/timetable/me/snapshot${qs}`, {
          headers: authHeaders()
        });
        const body = await parseJson(res);
        if (!res.ok) continue;
        const rows = safeArray(body.entries ?? body);
        const found = rows.find(
          (r) => r.maLopHp === maLopHpTarget || r.idLopHp == lhp?.idLopHp
        );
        if (found) {
          setSnapshotRows(rows);
          return;
        }
        setSnapshotRows(rows);
      } catch {
        /* ignore */
      }
    }
  }

  function delay(ms) {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }

  const handleCancel = async (item) => {
    if (!item?.idDangKy) return;
    if (!window.confirm(`Hủy đăng ký lớp ${item.maLopHp} (${item.tenHocPhan})?`)) return;
    setBusyId(`cancel-${item.idDangKy}`);
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/registrations/${item.idDangKy}`, {
        method: 'DELETE',
        headers: authHeaders()
      });
      if (!res.ok && res.status !== 204) {
        const body = await parseBody(res);
        throw new Error(errMsg(body, 'Không hủy được.'));
      }
      const refresh = [];
      refresh.push(loadMine());
      if (windowStatus?.preDangKyDangMo) refresh.push(loadClasses(classesPage.page));
      refresh.push(loadSnapshot());
      await Promise.all(refresh);
      showToast('ok', 'Đã hủy đăng ký', item.maLopHp);
    } catch (e) {
      showToast('error', 'Lỗi', e.message || 'Không hủy được.');
    } finally {
      setBusyId(null);
    }
  };

  const anyOpen = !!windowStatus?.dangKyAnyDangMo;
  const { activePhase, isPreOnly, isOfficial } = registrationPhaseInfo;

  let bannerColor = 'border-secondary bg-secondary-container/20';
  let bannerIcon = 'warning';
  let bannerIconColor = 'text-secondary';
  let bannerTitle = 'Phiên đăng ký CHƯA mở cho bạn';
  let bannerNote =
    windowStatus?.debugReason ||
    'Admin chưa cấu hình cửa sổ đăng ký phù hợp với cohort+ngành của bạn.';
  let bannerCloseAt = null;

  if (isOfficial) {
    bannerColor = 'border-primary bg-primary/5';
    bannerIcon = 'verified';
    bannerIconColor = 'text-primary';
    bannerTitle = 'Đăng ký CHÍNH THỨC đang mở';
    bannerNote = windowStatus?.officialScopeNote || '';
    bannerCloseAt = windowStatus?.officialCloseAt;
  } else if (isPreOnly) {
    bannerColor = 'border-tertiary bg-tertiary-container/20';
    bannerIcon = 'edit_note';
    bannerIconColor = 'text-tertiary';
    bannerTitle = 'Đăng ký DỰ KIẾN (PRE) đang mở — danh mục theo CTĐT ngành bạn';
    bannerNote =
      'Pha PRE: chỉ hiển thị các lớp thuộc học phần trong khung CTĐT ngành bạn — bỏ qua điều kiện tiên quyết khi ghi nhận. Pha chính thức sẽ kiểm lại và áp luật đăng ký đầy đủ.';
    bannerCloseAt = windowStatus?.preCloseAt;
  }

  const banner = (
    <div className={`rounded-xl border-l-4 p-4 flex items-start gap-3 ${bannerColor}`}>
      <span className={`material-symbols-outlined ${bannerIconColor}`}>{bannerIcon}</span>
      <div className="flex-1">
        <p className="font-bold text-on-surface">{bannerTitle}</p>
        <p className="text-sm text-on-surface-variant mt-0.5">{bannerNote}</p>
        {bannerCloseAt && (
          <p className="text-xs text-on-surface-variant mt-1">
            Đóng lúc: {formatInstantVi(bannerCloseAt)}
          </p>
        )}
      </div>
    </div>
  );

  const studentBadge =
    windowStatus &&
    (windowStatus.maSinhVien || windowStatus.hoTenSinhVien) && (
    <div className="rounded-xl bg-surface-container-low p-4 flex flex-wrap gap-x-6 gap-y-2 text-sm">
      <span>
        <span className="text-on-surface-variant">Sinh viên: </span>
        <span className="font-bold text-on-surface">{windowStatus.hoTenSinhVien || '—'}</span>
        {windowStatus.maSinhVien && (
          <span className="ml-2 font-mono text-xs text-on-surface-variant">[{windowStatus.maSinhVien}]</span>
        )}
      </span>
      <span>
        <span className="text-on-surface-variant">Lớp: </span>
        <span className="font-semibold text-on-surface">{windowStatus.tenLop || '—'}</span>
      </span>
      <span>
        <span className="text-on-surface-variant">Khóa: </span>
        <span className="font-semibold text-primary">
          {windowStatus.namNhapHoc != null ? `K${windowStatus.namNhapHoc}` : '— (chưa gán)'}
        </span>
      </span>
      <span>
        <span className="text-on-surface-variant">Ngành: </span>
        <span className="font-semibold text-primary">{windowStatus.tenNganh || '— (chưa gán)'}</span>
      </span>
    </div>
  );

  return (
    <div className="text-on-background min-h-[60vh]">
      <header className="mb-6 flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
        <div className="max-w-2xl">
          <h1 className="text-3xl font-extrabold text-primary tracking-tight mb-2">Đăng ký học phần</h1>
          <p className="text-on-surface-variant text-sm leading-relaxed">
            {windowStatus?.dangKyAnyDangMo
              ? 'Danh mục lớp được lọc theo chương trình đào tạo (CTĐT) của ngành bạn trong phạm vi pha PRE hoặc theo học phần đã chọn ở đăng ký dự kiến. Hệ thống kiểm tra trùng lịch và điều kiện tiên quyết khi pha chính thức.'
              : 'Xem trạng thái phiên đăng ký bên dưới. Chỉ khi pha được mở và đúng phạm vi khóa/ngành thì mới được thao tác đăng ký.'}
          </p>
        </div>
        <div className="bg-surface-container rounded-full px-5 py-2 flex flex-wrap items-center gap-3 self-start">
          <span className="text-xs font-semibold text-on-surface-variant uppercase tracking-wider">Học kỳ</span>
          <select
            className="bg-transparent border-none text-primary font-bold focus:ring-0 cursor-pointer text-sm max-w-[14rem]"
            value={hocKyId}
            onChange={(e) => {
              const v = e.target.value;
              setHocKyId(v);
              rememberStudentHocKy(v);
            }}
          >
            {hocKys.length === 0 && <option value="">—</option>}
            {hocKys.map((hk) => {
              const id = hk.idHocKy ?? hk.id;
              return (
                <option key={id} value={String(id)}>
                  {hk.tenHocKy || hk.ten || id}
                </option>
              );
            })}
          </select>
        </div>
      </header>

      {windowStatus ? (
        <div className="mb-6 space-y-3">
          {banner}
          {studentBadge}
        </div>
      ) : (
        hocKyId && (
          <div className="mb-6 rounded-xl border border-outline-variant/40 bg-surface-container-low p-4 text-sm text-on-surface-variant">
            Đang tải trạng thái đăng ký…
          </div>
        )
      )}

      <div className="grid grid-cols-1 xl:grid-cols-3 gap-6">
        <div className="xl:col-span-2 space-y-4">
          {browseClosedHint && (
            <div className="rounded-xl border border-secondary/30 bg-secondary-container/20 p-4 text-sm text-on-surface leading-relaxed">
              {browseClosedHint}
            </div>
          )}

          {isOfficial && !showFullCourseBrowse && anyOpen && (
            <div className="rounded-xl bg-surface-container-low p-4">
              <label className="flex items-center gap-2 text-sm text-on-surface select-none cursor-pointer">
                <input
                  type="checkbox"
                  className="h-4 w-4 rounded text-primary"
                  checked={chiConCho}
                  onChange={(e) => setChiConCho(e.target.checked)}
                />
                Chỉ lớp còn chỗ (áp dụng cho danh sách «Từ đăng ký dự kiến»)
              </label>
            </div>
          )}

          {showFullCourseBrowse && (
            <>
              <form
                onSubmit={doSearch}
                className="flex flex-col gap-3 rounded-xl bg-surface-container-low p-4 sm:flex-row sm:items-center"
              >
                <div className="relative flex-1">
                  <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-on-surface-variant text-[20px]">
                    search
                  </span>
                  <input
                    className="w-full rounded-lg border-none bg-surface-container-lowest py-3 pl-10 pr-4 text-sm text-on-surface outline-none focus:ring-2 focus:ring-primary/20"
                    placeholder="Tìm theo tên môn, mã môn, mã lớp…"
                    value={keyword}
                    onChange={(e) => setKeyword(e.target.value)}
                  />
                </div>
                <label className="flex items-center gap-2 text-sm text-on-surface select-none">
                  <input
                    type="checkbox"
                    className="h-4 w-4 rounded text-primary"
                    checked={chiConCho}
                    onChange={(e) => setChiConCho(e.target.checked)}
                  />
                  Chỉ lớp còn chỗ
                </label>
                <button
                  type="submit"
                  className="rounded-full bg-primary text-white px-6 py-3 text-sm font-bold hover:bg-primary-container transition"
                >
                  Tìm
                </button>
              </form>

              {errMsgList && (
                <div className="rounded-xl border border-error/30 bg-error-container/40 p-4 text-sm text-error">
                  {errMsgList}
                </div>
              )}

              <div className="overflow-hidden rounded-xl bg-surface-container-lowest shadow-sm">
                {loadingList ? (
                  <div className="p-8 text-sm text-on-surface-variant">Đang tải lớp…</div>
                ) : classes.length === 0 ? (
                  <div className="p-8 text-center text-sm text-on-surface-variant">
                    Không có lớp nào phù hợp. Thử bỏ &quot;chỉ lớp còn chỗ&quot; hoặc đổi từ khóa.
                  </div>
                ) : (
                  <div className="overflow-x-auto">
                    <table className="w-full text-left text-sm">
                      <thead className="bg-surface-container text-on-surface-variant">
                        <tr>
                          <th className="px-4 py-3 text-[11px] font-bold uppercase tracking-wider">Mã lớp</th>
                          <th className="px-4 py-3 text-[11px] font-bold uppercase tracking-wider">Học phần</th>
                          <th className="px-4 py-3 text-[11px] font-bold uppercase tracking-wider">TC</th>
                          <th className="px-4 py-3 text-[11px] font-bold uppercase tracking-wider">Giảng viên</th>
                          <th className="px-4 py-3 text-[11px] font-bold uppercase tracking-wider">Lịch học</th>
                          <th className="px-4 py-3 text-[11px] font-bold uppercase tracking-wider">Sĩ số</th>
                          <th className="px-4 py-3 text-[11px] font-bold uppercase tracking-wider text-right">Thao tác</th>
                        </tr>
                      </thead>
                      <tbody className="divide-y divide-surface-container">
                        {classes.map((c) => {
                          const isReg = registeredIds.has(Number(c.idLopHp));
                          const full = c.siSoConLai != null && c.siSoConLai <= 0;
                          const disabled = !anyOpen || isReg || full || busyId === `reg-${c.idLopHp}`;
                          return (
                            <tr key={c.idLopHp} className="hover:bg-surface-container-low/60 transition">
                              <td className="px-4 py-3 font-mono text-xs">{c.maLopHp}</td>
                              <td className="px-4 py-3">
                                <div className="font-semibold text-on-surface">{c.tenHocPhan}</div>
                                <div className="text-xs text-on-surface-variant">{c.maHocPhan}</div>
                              </td>
                              <td className="px-4 py-3 text-center">{c.soTinChi ?? '—'}</td>
                              <td className="px-4 py-3 text-on-surface-variant">{c.tenGiangVien || '—'}</td>
                              <td className="px-4 py-3 text-xs text-on-surface-variant max-w-[16rem]">
                                {formatTkb(c.thoiKhoaBieuJson)}
                              </td>
                              <td className="px-4 py-3">
                                <span
                                  className={`text-xs font-bold ${
                                    full ? 'text-error' : c.siSoConLai != null && c.siSoConLai <= 5 ? 'text-secondary' : 'text-primary'
                                  }`}
                                >
                                  {c.siSoThucTe ?? 0}/{c.siSoToiDa ?? 0}
                                </span>
                              </td>
                              <td className="px-4 py-3 text-right">
                                {isReg ? (
                                  <span className="inline-flex items-center gap-1 rounded-full bg-primary-fixed px-3 py-1 text-xs font-bold text-on-primary-fixed">
                                    <span className="material-symbols-outlined text-[14px]">check</span> Đã đăng ký
                                  </span>
                                ) : (
                                  <button
                                    type="button"
                                    disabled={disabled}
                                    onClick={() => handleRegister(c)}
                                    className="inline-flex items-center gap-1 rounded-full bg-primary text-white px-4 py-2 text-xs font-bold hover:bg-primary-container transition disabled:opacity-40"
                                  >
                                    {busyId === `reg-${c.idLopHp}` ? 'Đang gửi…' : full ? 'Hết chỗ' : 'Đăng ký'}
                                  </button>
                                )}
                              </td>
                            </tr>
                          );
                        })}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>

              {classes.length > 0 && classesPage.totalPages > 1 && (
                <div className="flex items-center justify-between text-sm">
                  <span className="text-on-surface-variant">
                    Trang {classesPage.page + 1}/{classesPage.totalPages} · {classesPage.total} lớp
                  </span>
                  <div className="flex gap-2">
                    <button
                      type="button"
                      onClick={() => loadClasses(Math.max(0, classesPage.page - 1))}
                      disabled={classesPage.page === 0 || loadingList}
                      className="rounded-full bg-surface-container-high px-4 py-2 text-xs font-bold disabled:opacity-40"
                    >
                      ← Trước
                    </button>
                    <button
                      type="button"
                      onClick={() => loadClasses(Math.min(classesPage.totalPages - 1, classesPage.page + 1))}
                      disabled={classesPage.page + 1 >= classesPage.totalPages || loadingList}
                      className="rounded-full bg-surface-container-high px-4 py-2 text-xs font-bold disabled:opacity-40"
                    >
                      Sau →
                    </button>
                  </div>
                </div>
              )}
            </>
          )}

          {isOfficial && loadingPre && (
            <div className="rounded-xl bg-surface-container-low p-4 text-sm text-on-surface-variant">
              Đang tải đăng ký dự kiến của bạn…
            </div>
          )}
          {isOfficial && !loadingPre && preIntents.length > 0 && pendingFromPre.length === 0 && (
            <div className="rounded-xl border border-primary/25 bg-primary/5 p-4 text-sm text-on-surface">
              Các học phần trong đăng ký dự kiến của bạn đã được đăng ký chính thức (hoặc không còn nguyện
              vọng chưa xử lý).
            </div>
          )}
          {isOfficial && !loadingPre && pendingFromPre.length > 0 && (
            <section className="space-y-4 rounded-xl border border-tertiary/30 bg-surface-container-low p-4">
              <div className="flex flex-wrap items-center justify-between gap-2">
                <h2 className="text-base font-bold text-primary flex items-center gap-2">
                  <span className="material-symbols-outlined text-[22px]">playlist_add_check</span>
                  Từ đăng ký dự kiến — chọn lớp đăng ký chính thức
                </h2>
                {loadingPreSections && (
                  <span className="text-xs font-semibold text-on-surface-variant">Đang tải lớp theo môn…</span>
                )}
              </div>
              <p className="text-xs text-on-surface-variant leading-relaxed">
                Dưới đây là các học phần bạn đã chọn ở pha PRE, sắp xếp theo thứ tự ưu tiên.{' '}
                {showFullCourseBrowse
                  ? 'Bộ lọc «Chỉ lớp còn chỗ» ở form tìm phía trên cũng áp dụng cho danh sách này.'
                  : 'Bộ lọc «Chỉ lớp còn chỗ» được bật/tắt ngay ô phía trên mục này.'}
              </p>
              {pendingFromPre.map((it) => {
                const idHp = Number(it.idHocPhan);
                const sectionRows = sectionsFromPre[idHp] ?? sectionsFromPre[String(idHp)] ?? [];
                return (
                  <div
                    key={it.id ?? `${idHp}-${it.priority}`}
                    className="rounded-lg border border-surface-container-high bg-surface-container-lowest overflow-hidden"
                  >
                    <div className="px-4 py-3 bg-surface-container flex flex-wrap items-baseline gap-2">
                      <span className="text-xs font-bold text-on-surface-variant uppercase tracking-wider">
                        Ưu tiên {it.priority ?? '—'}
                      </span>
                      <span className="font-mono text-xs text-primary">{it.maHocPhan}</span>
                      <span className="font-semibold text-on-surface">{it.tenHocPhan}</span>
                      {it.soTinChi != null && (
                        <span className="text-xs text-on-surface-variant">· {it.soTinChi} TC</span>
                      )}
                    </div>
                    {loadingPreSections ? (
                      <div className="p-6 text-sm text-on-surface-variant">Đang tải các lớp mở…</div>
                    ) : sectionRows.length === 0 ? (
                      <div className="p-6 text-sm text-on-surface-variant text-center italic">
                        Chưa có lớp mở đăng ký cho học phần này (hoặc không khớp bộ lọc chỗ).
                      </div>
                    ) : (
                      <div className="overflow-x-auto">
                        <table className="w-full text-left text-sm">
                          <thead className="bg-surface-container text-on-surface-variant">
                            <tr>
                              <th className="px-4 py-2 text-[11px] font-bold uppercase tracking-wider">Mã lớp</th>
                              <th className="px-4 py-2 text-[11px] font-bold uppercase tracking-wider">TC</th>
                              <th className="px-4 py-2 text-[11px] font-bold uppercase tracking-wider">Giảng viên</th>
                              <th className="px-4 py-2 text-[11px] font-bold uppercase tracking-wider">Lịch học</th>
                              <th className="px-4 py-2 text-[11px] font-bold uppercase tracking-wider">Sĩ số</th>
                              <th className="px-4 py-2 text-[11px] font-bold uppercase tracking-wider text-right">
                                Thao tác
                              </th>
                            </tr>
                          </thead>
                          <tbody className="divide-y divide-surface-container">
                            {sectionRows.map((c) => {
                              const isReg = registeredIds.has(Number(c.idLopHp));
                              const full = c.siSoConLai != null && c.siSoConLai <= 0;
                              const disabled = !anyOpen || isReg || full || busyId === `reg-${c.idLopHp}`;
                              return (
                                <tr key={c.idLopHp} className="hover:bg-surface-container-low/60 transition">
                                  <td className="px-4 py-2 font-mono text-xs">{c.maLopHp}</td>
                                  <td className="px-4 py-2 text-center">{c.soTinChi ?? '—'}</td>
                                  <td className="px-4 py-2 text-on-surface-variant">{c.tenGiangVien || '—'}</td>
                                  <td className="px-4 py-2 text-xs text-on-surface-variant max-w-[16rem]">
                                    {formatTkb(c.thoiKhoaBieuJson)}
                                  </td>
                                  <td className="px-4 py-2">
                                    <span
                                      className={`text-xs font-bold ${
                                        full
                                          ? 'text-error'
                                          : c.siSoConLai != null && c.siSoConLai <= 5
                                            ? 'text-secondary'
                                            : 'text-primary'
                                      }`}
                                    >
                                      {c.siSoThucTe ?? 0}/{c.siSoToiDa ?? 0}
                                    </span>
                                  </td>
                                  <td className="px-4 py-2 text-right">
                                    {isReg ? (
                                      <span className="inline-flex items-center gap-1 rounded-full bg-primary-fixed px-3 py-1 text-[11px] font-bold text-on-primary-fixed">
                                        <span className="material-symbols-outlined text-[14px]">check</span> Đã
                                        đăng ký
                                      </span>
                                    ) : (
                                      <button
                                        type="button"
                                        disabled={disabled}
                                        onClick={() => handleRegister(c)}
                                        className="inline-flex items-center gap-1 rounded-full bg-primary text-white px-3 py-1.5 text-[11px] font-bold hover:bg-primary-container transition disabled:opacity-40"
                                      >
                                        {busyId === `reg-${c.idLopHp}` ? 'Đang gửi…' : full ? 'Hết chỗ' : 'Đăng ký'}
                                      </button>
                                    )}
                                  </td>
                                </tr>
                              );
                            })}
                          </tbody>
                        </table>
                      </div>
                    )}
                  </div>
                );
              })}
            </section>
          )}

          {isOfficial && anyOpen && !showFullCourseBrowse && !loadingPre && preIntents.length === 0 && (
            <div className="rounded-xl border border-error/25 bg-error-container/15 p-4 text-sm text-on-surface leading-relaxed space-y-2">
              <p>
                Hệ thống không thấy nguyện vọng PRE nào cho <strong>học kỳ đang chọn ô phía trên</strong>.
                Intent luôn lưu theo từng học kỳ — nếu bạn đã chọn môn ở «Đăng ký dự kiến (PRE)», hãy đổi ô học kỳ
                cho khớp (đã đồng bộ nhớ học kỳ giữa hai trang khi đổi lựa chọn).
              </p>
              <p className="text-xs text-on-surface-variant">
                Vẫn trống? Kiểm tra lại học kỳ trên trang PRE, hoặc liên hệ phòng đào tạo nếu dữ liệu chưa ghi được.
              </p>
            </div>
          )}
        </div>

        <aside className="space-y-4">
          <div className="rounded-2xl bg-surface-container p-5">
            <div className="flex items-center justify-between mb-3">
              <h3 className="text-base font-bold text-primary flex items-center gap-2">
                <span className="material-symbols-outlined">checklist</span> Lớp đã đăng ký
              </h3>
              <span className="text-xs font-bold text-on-surface-variant">
                {registered.length} môn · {tongTinChi} TC
              </span>
            </div>
            {loadingMine ? (
              <p className="text-sm text-on-surface-variant">Đang tải…</p>
            ) : registered.length === 0 ? (
              <p className="text-sm text-on-surface-variant italic">
                Chưa đăng ký lớp nào trong {tenHocKyChon}.
              </p>
            ) : (
              <ul className="space-y-3">
                {registered.map((r) => (
                  <li
                    key={r.idDangKy}
                    className="rounded-lg bg-surface-container-lowest p-3 border border-surface-container-high"
                  >
                    <div className="flex items-start justify-between gap-2">
                      <div className="min-w-0 flex-1">
                        <p className="font-semibold text-sm text-on-surface truncate">{r.tenHocPhan}</p>
                        <p className="text-xs text-on-surface-variant">
                          <span className="font-mono">{r.maLopHp}</span>
                          {r.tenGiangVien ? ` · ${r.tenGiangVien}` : ''}
                        </p>
                        <p className="text-xs text-on-surface-variant mt-1">
                          {r.soTinChi ?? '—'} TC
                          {r.siSoToiDa != null ? ` · sĩ số ${r.siSoThucTe ?? 0}/${r.siSoToiDa}` : ''}
                        </p>
                      </div>
                      <button
                        type="button"
                        disabled={busyId === `cancel-${r.idDangKy}`}
                        onClick={() => handleCancel(r)}
                        className="text-xs font-bold text-error hover:bg-error-container/40 rounded-full px-3 py-1 transition disabled:opacity-40"
                      >
                        {busyId === `cancel-${r.idDangKy}` ? '…' : 'Hủy'}
                      </button>
                    </div>
                  </li>
                ))}
              </ul>
            )}
          </div>

          {/* Mini timetable — hien thi lich hoc tu snapshot */}
          <div className="rounded-2xl bg-surface-container overflow-hidden">
            <div className="flex items-center justify-between px-5 pt-4 pb-2">
              <h3 className="text-sm font-bold text-primary flex items-center gap-2">
                <span className="material-symbols-outlined text-[18px]">calendar_month</span>
                Lịch học trong tuần
              </h3>
              {snapshotRows.length > 0 && (
                <span className="text-xs text-on-surface-variant font-medium">
                  {snapshotRows.length} buổi
                </span>
              )}
            </div>

            {loadingSnapshot ? (
              <div className="px-5 pb-4">
                <div className="flex items-center gap-2 text-xs text-on-surface-variant">
                  <span className="material-symbols-outlined text-[16px] animate-spin">progress_activity</span>
                  Đang tải lịch học…
                </div>
              </div>
            ) : snapshotRows.length === 0 ? (
              <div className="px-5 pb-4">
                <p className="text-xs text-on-surface-variant italic leading-relaxed">
                  Chưa có lịch học.{' '}
                  {registered.length > 0
                    ? 'Lịch sẽ hiển thị sau khi đăng ký hoàn tất (1-3s).'
                    : 'Hãy đăng ký lớp học phần trước.'}
                </p>
              </div>
            ) : (
              <div className="px-2 pb-3">
                <MiniTimetable entries={snapshotRows} />
                <div className="px-3 pt-2">
                  <button
                    type="button"
                    onClick={() => navigate('/student/timetable')}
                    className="w-full py-2 rounded-lg bg-surface-container-lowest text-xs font-bold text-primary hover:bg-primary/10 transition flex items-center justify-center gap-1.5 border border-primary/20"
                  >
                    <span className="material-symbols-outlined text-[16px]">open_in_new</span>
                    Xem lịch đầy đủ
                  </button>
                </div>
              </div>
            )}
          </div>

          {windowStatus?.debugReason && !anyOpen && (
            <div className="rounded-2xl border border-error/30 bg-error-container/20 p-4 text-xs leading-relaxed">
              <p className="font-bold text-error mb-1 flex items-center gap-2">
                <span className="material-symbols-outlined text-[18px]">help</span> Vì sao chưa mở?
              </p>
              <p className="text-on-surface-variant whitespace-pre-line">{windowStatus.debugReason}</p>
            </div>
          )}
        </aside>
      </div>

      {toast && (
        <div className="fixed bottom-6 right-6 z-[60] max-w-sm">
          <div
            className={`px-4 py-3 rounded-xl shadow-2xl flex items-start gap-3 border-l-4 ${
              toast.kind === 'error'
                ? 'border-error bg-inverse-surface text-surface'
                : toast.kind === 'warn'
                  ? 'border-secondary bg-surface-container-lowest text-on-surface'
                  : 'border-primary bg-inverse-surface text-surface'
            }`}
          >
            <span className="material-symbols-outlined">
              {toast.kind === 'ok' ? 'check_circle' : toast.kind === 'warn' ? 'warning' : 'error'}
            </span>
            <div className="flex-1 min-w-0">
              <p className="font-bold text-sm">{toast.title}</p>
              {toast.detail && <p className="text-xs opacity-80 mt-0.5">{toast.detail}</p>}
            </div>
            <button
              type="button"
              onClick={() => setToast(null)}
              className="opacity-60 hover:opacity-100"
              aria-label="Đóng"
            >
              <span className="material-symbols-outlined text-sm">close</span>
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default StudentRegistrationPage;

/* ── MiniTimetable ────────────────────────────────────────────────────────── */
/** Hien thi lich hoc ngan gon trong aside panel. */
const THU_LABELS = { 2: 'T2', 3: 'T3', 4: 'T4', 5: 'T5', 6: 'T6', 7: 'T7', 8: 'CN' };
const TIET_SHORT = {
  1: '07:00', 2: '07:50', 3: '08:50', 4: '09:50', 5: '10:40',
  6: '13:00', 7: '13:50', 8: '14:50', 9: '15:50', 10: '16:40',
  11: '18:00', 12: '18:50'
};

const THEMES = [
  'bg-primary-fixed/70',
  'bg-tertiary-fixed/70',
  'bg-secondary-fixed/70',
  'bg-error-fixed/70',
  'bg-outline/70',
];
const THEMES_ON = [
  'text-on-primary-fixed',
  'text-on-tertiary-fixed',
  'text-on-secondary-fixed',
  'text-on-error-fixed',
  'text-on-surface',
];

/** Gom cac slot cung lop-thu-tiet thanh 1 block de hien thi compact. */
function groupSlots(entries) {
  const map = {};
  for (const e of entries) {
    const key = `${e.idLopHp ?? ''}_${e.thu}_${e.tiet}`;
    if (!map[key]) {
      map[key] = { ...e, count: 1 };
    } else {
      map[key].count++;
    }
  }
  return Object.values(map);
}

function parseTiet(tiet) {
  if (tiet == null) return { start: 1, span: 1 };
  const s = String(tiet).replace(/[^0-9\-–]/g, '').split(/[-–]/);
  const a = Number(s[0]) || 1;
  const b = s.length > 1 ? Number(s[1]) || a : a;
  return {
    start: Math.min(Math.max(a, 1), 12),
    span: Math.max(1, Math.min(b - a + 1, 13 - a))
  };
}

function MiniTimetable({ entries = [] }) {
  const grouped = useMemo(() => groupSlots(entries), [entries]);

  const byThu = useMemo(() => {
    const map = {};
    for (const g of grouped) {
      const thu = Number(g.thu) || 2;
      if (!map[thu]) map[thu] = [];
      map[thu].push(g);
    }
    for (const thu of Object.keys(map)) {
      map[thu].sort((a, b) => {
        const pa = parseTiet(a.tiet);
        const pb = parseTiet(b.tiet);
        return pa.start - pb.start;
      });
    }
    return map;
  }, [grouped]);

  const sortedThus = Object.keys(byThu).map(Number).sort((a, b) => a - b);

  if (entries.length === 0) return null;

  return (
    <div className="space-y-2">
      {sortedThus.map((thu) => (
        <div key={thu}>
          <div className="px-3 pt-1 pb-0.5">
            <span className="text-[10px] font-bold text-on-surface-variant uppercase tracking-wider">
              {THU_LABELS[thu] || `T${thu}`}
            </span>
          </div>
          <div className="space-y-1 px-1">
            {byThu[thu].map((g, idx) => {
              const themeIdx = Math.abs(Number(g.idLopHp || 0) + idx) % THEMES.length;
              const { span } = parseTiet(g.tiet);
              return (
                <div
                  key={`${g.idLopHp}_${g.tiet}_${idx}`}
                  className={`rounded-md px-2 py-1 ${THEMES[themeIdx]} ${THEMES_ON[themeIdx]}`}
                  style={{ minHeight: `${Math.max(span, 1) * 22 + 4}px` }}
                >
                  <p className="text-[10px] font-bold leading-tight truncate">
                    {g.tenHocPhan || g.maHocPhan || '—'}
                  </p>
                  <p className="text-[9px] opacity-75 leading-tight">
                    {TIET_SHORT[parseTiet(g.tiet).start] || g.tiet}
                    {g.phong ? ` · ${g.phong}` : ''}
                    {g.tenGiangVien ? ` · ${g.tenGiangVien.split(' ').slice(-1)[0]}` : ''}
                  </p>
                </div>
              );
            })}
          </div>
        </div>
      ))}
    </div>
  );
}
