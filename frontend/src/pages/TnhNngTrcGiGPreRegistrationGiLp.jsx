import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';

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

function errMessage(body, fallback) {
  if (!body || typeof body !== 'object') return fallback;
  if (body.message) return String(body.message);
  if (Array.isArray(body.errors) && body.errors[0]?.defaultMessage) {
    return String(body.errors[0].defaultMessage);
  }
  return fallback;
}

const prettyKhoi = (khoi) => {
  switch (khoi) {
    case 'DAI_CUONG':
      return 'Đại cương';
    case 'CO_SO_NGANH':
      return 'Cơ sở ngành';
    case 'CHUYEN_NGANH':
      return 'Chuyên ngành';
    case 'TU_CHON':
      return 'Tự chọn';
    default:
      return khoi || 'Khối';
  }
};

/** Trang PRE: nguyện vọng đăng ký dự kiến — API /api/v1/pre-registrations/intents */
const TnhNngTrcGiGPreRegistrationGiLp = () => {
  const location = useLocation();
  const [hocKys, setHocKys] = useState([]);
  const [hocKyId, setHocKyId] = useState('');
  const [intents, setIntents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [loadErr, setLoadErr] = useState('');
  const [actionBusy, setActionBusy] = useState(false);
  const [toast, setToast] = useState(null);
  const [bannerDismissed, setBannerDismissed] = useState(false);
  /** Mặc định khóa: chỉ mở thao tác sau khi window-status báo PRE đang mở (tránh race khi chưa tải xong đã nhấp Thêm). */
  const [preLocked, setPreLocked] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  /** Ghi nhận môn được chọn từ lưới CTĐT (ẩn select chung). */
  const [presetCurriculumCourse, setPresetCurriculumCourse] = useState(null);
  const [degreeAudit, setDegreeAudit] = useState(null);
  const [degreeLoading, setDegreeLoading] = useState(true);
  const [degreeErr, setDegreeErr] = useState('');
  const [hideCompletedCourses, setHideCompletedCourses] = useState(false);
  const [courseOptions, setCourseOptions] = useState([]);
  const [coursesLoading, setCoursesLoading] = useState(false);
  const [formIdHocPhan, setFormIdHocPhan] = useState('');
  const [formPriority, setFormPriority] = useState(1);
  const [formGhiChu, setFormGhiChu] = useState('');
  const toastTimerRef = useRef(null);

  const showToast = (kind, title, detail) => {
    setToast({ kind, title, detail });
    if (toastTimerRef.current) window.clearTimeout(toastTimerRef.current);
    toastTimerRef.current = window.setTimeout(() => setToast(null), 5200);
  };

  const tenHocKyChon = useMemo(() => {
    const id = Number(hocKyId);
    const hk = hocKys.find((h) => Number(h.idHocKy ?? h.id) === id);
    return hk?.tenHocKy || hk?.ten || '—';
  }, [hocKys, hocKyId]);

  const intentIds = useMemo(() => new Set(intents.map((i) => Number(i.idHocPhan))), [intents]);

  const tongTinChi = useMemo(
    () => intents.reduce((s, i) => s + (Number(i.soTinChi) || 0), 0),
    [intents]
  );

  /** Học phần trong CTĐT của ngành SV (Flatten + sort: kỳ gợi ý → mã). */
  const curriculumRows = useMemo(() => {
    if (!degreeAudit?.khois) return [];
    const rows = [];
    for (const k of degreeAudit.khois) {
      const khoi = k.khoiKienThuc ?? '';
      for (const c of safeArray(k.hocPhans)) {
        if (c?.idHocPhan == null) continue;
        rows.push({
          ...c,
          khoiKienThuc: khoi
        });
      }
    }
    rows.sort((a, b) => {
      const ka = a.hocKyGoiY != null ? Number(a.hocKyGoiY) : 999;
      const kb = b.hocKyGoiY != null ? Number(b.hocKyGoiY) : 999;
      if (ka !== kb) return ka - kb;
      return String(a.maHocPhan || '').localeCompare(String(b.maHocPhan || ''));
    });
    return rows;
  }, [degreeAudit]);

  const visibleCurriculumRows = useMemo(
    () => (hideCompletedCourses ? curriculumRows.filter((r) => !r.daHoanThanh) : curriculumRows),
    [curriculumRows, hideCompletedCourses]
  );

  const loadDegreeAudit = useCallback(async () => {
    setDegreeLoading(true);
    setDegreeErr('');
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/degree-audit/me`, { headers: authHeaders() });
      const body = await parseBody(res);
      if (!res.ok) throw new Error(errMessage(body, 'Không tải được chương trình đào tạo theo ngành.'));
      setDegreeAudit(body);
    } catch (e) {
      setDegreeErr(e.message || 'Lỗi CTĐT.');
      setDegreeAudit(null);
    } finally {
      setDegreeLoading(false);
    }
  }, []);

  const loadHocKy = useCallback(async () => {
    const res = await fetch(`${API_BASE_URL}/api/hoc-ky`, { headers: authHeaders() });
    const body = await parseBody(res);
    if (!res.ok) throw new Error(errMessage(body, 'Không tải được danh sách học kỳ.'));
    const rows = safeArray(body);
    setHocKys(rows);
    setHocKyId((cur) => resolveInitialStudentHocKyFromRows(rows, cur));
  }, []);

  const loadIntents = useCallback(async () => {
    if (!hocKyId) {
      setIntents([]);
      return;
    }
    const qs = `?hocKyId=${encodeURIComponent(hocKyId)}`;
    const res = await fetch(`${API_BASE_URL}/api/v1/pre-registrations/intents/me${qs}`, {
      headers: authHeaders()
    });
    const body = await parseBody(res);
    if (!res.ok) throw new Error(errMessage(body, 'Không tải được nguyện vọng.'));
    const rows = safeArray(body);
    rows.sort((a, b) => (a.priority ?? 999) - (b.priority ?? 999));
    setIntents(rows);
  }, [hocKyId]);

  const loadCoursesForModal = useCallback(async () => {
    if (!hocKyId) return;
    setCoursesLoading(true);
    try {
      const qs = new URLSearchParams({
        idHocKy: hocKyId,
        page: '0',
        size: '100',
        sortBy: 'tenHocPhan',
        sortDir: 'ASC'
      });
      const res = await fetch(`${API_BASE_URL}/api/v1/courses?${qs.toString()}`, {
        headers: authHeaders()
      });
      const body = await parseBody(res);
      if (!res.ok) throw new Error(errMessage(body, 'Không tải danh sách học phần.'));
      const raw = body.content ?? body;
      const list = safeArray(raw);
      const byHp = new Map();
      for (const row of list) {
        const hid = row.idHocPhan;
        if (hid != null && !byHp.has(hid)) byHp.set(hid, row);
      }
      setCourseOptions([...byHp.values()]);
    } catch (e) {
      showToast('error', 'Lỗi', e.message || 'Không tải học phần.');
      setCourseOptions([]);
    } finally {
      setCoursesLoading(false);
    }
  }, [hocKyId]);

  useEffect(() => {
    (async () => {
      try {
        await loadHocKy();
        await loadDegreeAudit();
      } catch (e) {
        setLoadErr(e.message || 'Lỗi học kỳ.');
      }
    })();
  }, [loadHocKy, loadDegreeAudit]);

  useEffect(() => {
    if (!hocKyId) {
      setLoading(false);
      return;
    }
    (async () => {
      setLoading(true);
      setLoadErr('');
      try {
        await loadIntents();
      } catch (e) {
        setLoadErr(e.message || 'Lỗi tải dữ liệu.');
        setIntents([]);
      } finally {
        setLoading(false);
      }
    })();
  }, [hocKyId, loadIntents]);

  /** Đồng bộ với backend — khi không còn cửa sổ / mốc PRE nào khớp, admin đã đóng hoàn toàn phiên PRE. */
  useEffect(() => {
    if (!hocKyId) {
      setPreLocked(true);
      return undefined;
    }
    setPreLocked(true);
    const ac = new AbortController();
    (async () => {
      try {
        const qs = `?hocKyId=${encodeURIComponent(hocKyId)}`;
        const res = await fetch(`${API_BASE_URL}/api/v1/registrations/me/window-status${qs}`, {
          headers: authHeaders(),
          signal: ac.signal
        });
        const body = await parseBody(res);
        if (ac.signal.aborted) return;
        if (!res.ok) {
          setPreLocked(true);
          return;
        }
        setPreLocked(body.preDangKyDangMo !== true);
      } catch {
        setPreLocked(true);
      }
    })();
    return () => ac.abort();
  }, [hocKyId]);

  const markLockedFromResponse = (res, body) => {
    if (res.status === 403) {
      setPreLocked(true);
      return;
    }
    const msg = errMessage(body, '');
    if (/PRE|pha|mở|đóng|cửa|window|không cho|không được/i.test(msg)) {
      setPreLocked(true);
    }
  };

  const openAddFromCurriculum = (row) => {
    if (preLocked || row?.daHoanThanh) return;
    if (intentIds.has(Number(row.idHocPhan))) {
      const existing = intents.find((i) => Number(i.idHocPhan) === Number(row.idHocPhan));
      if (existing) openEdit(existing);
      return;
    }
    setEditing(null);
    setPresetCurriculumCourse({
      idHocPhan: row.idHocPhan,
      maHocPhan: row.maHocPhan,
      tenHocPhan: row.tenHocPhan,
      hocKyGoiY: row.hocKyGoiY,
      khoiKienThuc: row.khoiKienThuc
    });
    setFormIdHocPhan(String(row.idHocPhan));
    setFormPriority(Math.max(1, intents.length + 1));
    setFormGhiChu('');
    setModalOpen(true);
  };

  const openAdd = () => {
    if (preLocked) {
      showToast('warn', 'Không thể thêm', 'Pha đăng ký dự kiến có thể đã đóng.');
      return;
    }
    setEditing(null);
    setPresetCurriculumCourse(null);
    setFormIdHocPhan('');
    setFormPriority(Math.max(1, intents.length + 1));
    setFormGhiChu('');
    setModalOpen(true);
    loadCoursesForModal();
  };

  const openEdit = (row) => {
    if (preLocked) return;
    setPresetCurriculumCourse(null);
    setEditing(row);
    setFormIdHocPhan(String(row.idHocPhan));
    setFormPriority(Number(row.priority) || 1);
    setFormGhiChu(row.ghiChu || '');
    setModalOpen(true);
    loadCoursesForModal();
  };

  const closeModal = () => {
    setModalOpen(false);
    setEditing(null);
    setPresetCurriculumCourse(null);
  };

  const submitModal = async () => {
    if (preLocked) {
      showToast('warn', 'Pha PRE đang đóng', 'Chờ admin mở pha đăng ký dự kiến hoặc tải lại trạng thái.');
      return;
    }
    const idHocKy = Number(hocKyId);
    const idHocPhan = Number(formIdHocPhan);
    if (!Number.isFinite(idHocKy) || !Number.isFinite(idHocPhan)) {
      showToast('error', 'Thiếu thông tin', 'Chọn học kỳ và học phần.');
      return;
    }
    const payload = {
      idHocKy,
      idHocPhan,
      priority: Number(formPriority) >= 1 ? Number(formPriority) : 1,
      ghiChu: formGhiChu?.trim() || undefined
    };
    setActionBusy(true);
    try {
      const url =
        editing != null
          ? `${API_BASE_URL}/api/v1/pre-registrations/intents/${editing.id}`
          : `${API_BASE_URL}/api/v1/pre-registrations/intents`;
      const method = editing != null ? 'PUT' : 'POST';
      const res = await fetch(url, {
        method,
        headers: authHeaders(),
        body: JSON.stringify(payload)
      });
      const body = await parseBody(res);
      if (!res.ok) {
        markLockedFromResponse(res, body);
        throw new Error(errMessage(body, 'Không lưu được.'));
      }
      await loadIntents();
      closeModal();
      showToast('ok', 'Đã lưu', editing ? 'Đã cập nhật nguyện vọng.' : 'Đã thêm nguyện vọng.');
    } catch (e) {
      showToast('error', 'Lỗi', e.message || 'Không lưu được.');
    } finally {
      setActionBusy(false);
    }
  };

  const removeIntent = async (row) => {
    if (preLocked) return;
    if (!window.confirm(`Xóa nguyện vọng ${row.maHocPhan}?`)) return;
    setActionBusy(true);
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/pre-registrations/intents/${row.id}`, {
        method: 'DELETE',
        headers: authHeaders()
      });
      const body = await parseBody(res);
      if (!res.ok) {
        if (res.status !== 204) markLockedFromResponse(res, body);
        throw new Error(errMessage(body, 'Không xóa được.'));
      }
      await loadIntents();
      showToast('ok', 'Đã xóa', row.maHocPhan);
    } catch (e) {
      showToast('error', 'Lỗi', e.message || 'Không xóa được.');
    } finally {
      setActionBusy(false);
    }
  };

  const refresh = async () => {
    if (!hocKyId) return;
    setLoading(true);
    setLoadErr('');
    try {
      await loadIntents();
    } catch (e) {
      setLoadErr(e.message || 'Lỗi làm mới.');
    } finally {
      setLoading(false);
    }
  };

  const showBanner =
    !bannerDismissed && (preLocked || location.state?.preClosed === true);

  const toastStyles =
    toast?.kind === 'error'
      ? 'border-error bg-inverse-surface text-surface'
      : toast?.kind === 'warn'
        ? 'border-secondary text-on-surface'
        : 'border-primary bg-inverse-surface text-surface';

  return (
    <div className="text-on-background selection:bg-primary-fixed selection:text-on-primary-fixed min-h-[60vh]">
      <header className="mb-8 flex flex-col gap-4 lg:flex-row lg:justify-between lg:items-start">
        <div className="max-w-2xl">
          <h1 className="text-3xl font-extrabold text-primary tracking-tight mb-2">
            Đăng ký dự kiến (PRE)
          </h1>
          <p className="text-on-surface-variant text-base leading-relaxed">
            Pha đăng ký sơ bộ giúp nhà trường dự báo nhu cầu mở lớp. Kết quả này không thay thế việc đăng ký
            chính thức.
          </p>
        </div>
        <div className="bg-surface-container rounded-full px-5 py-2 flex flex-wrap items-center gap-3 self-start">
          <span className="text-xs font-semibold text-on-surface-variant uppercase tracking-wider">
            Học kỳ
          </span>
          <select
            className="bg-transparent border-none text-primary font-bold focus:ring-0 cursor-pointer text-sm max-w-[14rem]"
            value={hocKyId}
            onChange={(e) => {
              const v = e.target.value;
              setHocKyId(v);
              rememberStudentHocKy(v);
              setBannerDismissed(false);
            }}
          >
            {hocKys.length === 0 && <option value="">—</option>}
            {hocKys.map((hk) => {
              const id = hk.idHocKy ?? hk.id;
              const label = hk.tenHocKy || hk.ten || String(id);
              return (
                <option key={id} value={String(id)}>
                  {label}
                </option>
              );
            })}
          </select>
        </div>
      </header>

      {showBanner && (
        <div className="mb-8 bg-secondary-container text-on-secondary-container p-4 rounded-xl flex items-center justify-between shadow-sm border-l-4 border-secondary">
          <div className="flex items-center gap-3 min-w-0">
            <span className="material-symbols-outlined text-secondary shrink-0">warning</span>
            <span className="font-medium text-sm">
              Pha đăng ký dự kiến chưa mở hoặc đã đóng — chỉ xem, không chỉnh sửa (hoặc hệ thống từ chối
              thao tác ghi).
            </span>
          </div>
          <button
            type="button"
            className="p-1 hover:bg-secondary/10 rounded-full transition-colors shrink-0"
            onClick={() => setBannerDismissed(true)}
            aria-label="Đóng"
          >
            <span className="material-symbols-outlined">close</span>
          </button>
        </div>
      )}

      <section className="mb-10 rounded-xl bg-surface-container-lowest shadow-sm overflow-hidden border border-outline-variant/10">
        <div className="px-5 py-4 border-b border-surface-container bg-surface-container-low flex flex-col gap-3 sm:flex-row sm:justify-between sm:items-center">
          <div>
            <h2 className="text-lg font-bold text-primary flex items-center gap-2">
              <span className="material-symbols-outlined">menu_book</span>
              Chương trình đào tạo của bạn
            </h2>
            {degreeAudit && (
              <p className="text-xs text-on-surface-variant mt-1">
                Ngành: <span className="font-semibold text-on-surface">{degreeAudit.tenNganh}</span>
                {degreeAudit.maNganh ? ` (${degreeAudit.maNganh})` : ''}
                {degreeAudit.tenKhoa ? ` · Khoa: ${degreeAudit.tenKhoa}` : ''}
                {degreeAudit.namApDung != null ? ` · CTĐT áp dụng ${degreeAudit.namApDung}` : ''}
              </p>
            )}
          </div>
          <label className="inline-flex items-center gap-2 text-sm cursor-pointer select-none text-on-surface-variant">
            <input
              type="checkbox"
              checked={hideCompletedCourses}
              onChange={(e) => setHideCompletedCourses(e.target.checked)}
              className="rounded border-outline-variant text-primary focus:ring-primary"
            />
            Ẩn môn đã hoàn thành
          </label>
        </div>

        {degreeLoading && (
          <div className="p-8 text-sm text-on-surface-variant">Đang tải khung học phần theo ngành…</div>
        )}
        {!degreeLoading && degreeErr && (
          <div className="p-6 border-t border-outline-variant/20">
            <p className="text-sm text-error font-medium">{degreeErr}</p>
            <p className="text-xs text-on-surface-variant mt-2">
              Khi pha PRE đang mở theo cửa sổ đăng ký, bạn có thể dùng «Thêm học phần» — nếu pha PRE chưa mở, nút
              sẽ bị khóa.
            </p>
          </div>
        )}
        {!degreeLoading && !degreeErr && visibleCurriculumRows.length === 0 && (
          <div className="p-8 text-center text-sm text-on-surface-variant">
            Không có học phần trong CTĐT (hoặc đã ẩn hết sau lọc).
          </div>
        )}
        {!degreeLoading && !degreeErr && visibleCurriculumRows.length > 0 && (
          <div className="overflow-x-auto">
            <table className="w-full min-w-[980px] text-left border-collapse text-sm">
              <thead className="bg-surface-container text-on-surface-variant text-[11px] uppercase tracking-wider">
                <tr>
                  <th className="px-4 py-3 font-bold">Kỳ gợi ý (CTĐT)</th>
                  <th className="px-4 py-3 font-bold">Khối</th>
                  <th className="px-4 py-3 font-bold">Mã HP</th>
                  <th className="px-4 py-3 font-bold">Tên học phần</th>
                  <th className="px-4 py-3 font-bold text-center">TC</th>
                  <th className="px-4 py-3 font-bold">Loại</th>
                  <th className="px-4 py-3 font-bold">Tiến độ học</th>
                  <th className="px-4 py-3 font-bold text-right">Đăng ký dự kiến</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-surface-container">
                {visibleCurriculumRows.map((row, idx) => {
                  const inPre = intentIds.has(Number(row.idHocPhan));
                  const done = !!row.daHoanThanh;
                  const kyLabel =
                    row.hocKyGoiY != null && row.hocKyGoiY !== '' ? `Kỳ ${row.hocKyGoiY}` : '—';
                  return (
                    <tr
                      key={`ctdt-${row.idHocPhan}-${idx}`}
                      className={`hover:bg-surface-container-low/60 ${done ? 'opacity-70' : ''}`}
                    >
                      <td className="px-4 py-3 font-semibold tabular-nums text-primary">{kyLabel}</td>
                      <td className="px-4 py-3 text-on-surface-variant text-xs">{prettyKhoi(row.khoiKienThuc)}</td>
                      <td className="px-4 py-3 font-mono text-xs">{row.maHocPhan}</td>
                      <td className="px-4 py-3 font-medium">{row.tenHocPhan}</td>
                      <td className="px-4 py-3 text-center">{row.soTinChi ?? '—'}</td>
                      <td className="px-4 py-3">
                        <span
                          className={`text-[10px] font-bold uppercase px-2 py-1 rounded-full ${
                            row.batBuoc ? 'bg-primary-fixed text-on-primary-fixed' : 'bg-secondary-container text-on-secondary-container'
                          }`}
                        >
                          {row.batBuoc ? 'Bắt buộc' : 'Tự chọn'}
                        </span>
                      </td>
                      <td className="px-4 py-3 text-xs">
                        {done ? (
                          <span className="text-on-surface-variant">Đã qua</span>
                        ) : (
                          <span className="text-secondary font-semibold">Chưa hoàn thành</span>
                        )}
                      </td>
                      <td className="px-4 py-3 text-right whitespace-nowrap">
                        {done ? (
                          <span className="text-on-surface-variant text-xs">—</span>
                        ) : inPre ? (
                          <button
                            type="button"
                            disabled={actionBusy || preLocked}
                            onClick={() => {
                              const ex = intents.find((i) => Number(i.idHocPhan) === Number(row.idHocPhan));
                              if (ex) openEdit(ex);
                            }}
                            className="text-primary font-bold text-xs hover:underline disabled:opacity-40"
                          >
                            Đã trong PRE · Sửa
                          </button>
                        ) : (
                          <button
                            type="button"
                            disabled={!hocKyId || actionBusy || preLocked}
                            onClick={() => openAddFromCurriculum(row)}
                            className="inline-flex items-center gap-1 rounded-full bg-primary/10 text-primary px-3 py-1.5 text-xs font-bold hover:bg-primary/20 disabled:opacity-40"
                          >
                            <span className="material-symbols-outlined text-[16px]">add_circle</span>
                            Thêm PRE
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
        <div className="px-5 py-3 bg-surface-container-low border-t border-surface-container text-[11px] text-on-surface-variant">
          «Kỳ gợi ý» là học kỳ trong lộ trình CTĐT (không trùng với học kỳ đăng ký PRE ở góc trên — đó là học kỳ thực tế đang đăng ký).
        </div>
      </section>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
        <div className="lg:col-span-8 space-y-4">
          <div className="flex flex-col sm:flex-row sm:justify-between sm:items-center gap-4">
            <div className="flex flex-wrap gap-3">
              <button
                type="button"
                disabled={!hocKyId || actionBusy || preLocked}
                onClick={openAdd}
                className="bg-primary hover:bg-primary-container disabled:opacity-50 text-white px-6 py-2.5 rounded-full font-bold flex items-center gap-2 shadow-lg shadow-primary/20 transition-all"
              >
                <span className="material-symbols-outlined text-[20px]">add</span>
                Thêm học phần (khác CTĐT)
              </button>
              <button
                type="button"
                disabled={!hocKyId || loading}
                onClick={refresh}
                className="bg-surface-container-high hover:bg-surface-container-highest text-on-surface px-5 py-2.5 rounded-full font-semibold flex items-center gap-2 transition-all"
              >
                <span className="material-symbols-outlined text-[20px]">refresh</span>
                Làm mới
              </button>
            </div>
            <p className="text-sm text-on-surface-variant font-medium">
              Đã chọn: <span className="text-primary font-bold">{intents.length} học phần</span> ({tongTinChi}{' '}
              tín chỉ)
            </p>
          </div>

          {loadErr && (
            <div className="rounded-xl border border-error-container bg-error-container/30 p-4 text-sm text-on-surface">
              {loadErr}
            </div>
          )}

          <div className="bg-surface-container-lowest rounded-xl overflow-hidden shadow-sm">
            {loading ? (
              <div className="p-8 text-on-surface-variant text-sm">Đang tải…</div>
            ) : (
              <table className="w-full text-left border-collapse">
                <thead className="bg-surface-container text-on-surface-variant">
                  <tr>
                    <th className="px-4 py-3 text-[11px] font-bold uppercase tracking-wider">Ưu tiên</th>
                    <th className="px-4 py-3 text-[11px] font-bold uppercase tracking-wider">Mã học phần</th>
                    <th className="px-4 py-3 text-[11px] font-bold uppercase tracking-wider">Tên học phần</th>
                    <th className="px-4 py-3 text-[11px] font-bold uppercase tracking-wider">Tín chỉ</th>
                    <th className="px-4 py-3 text-[11px] font-bold uppercase tracking-wider">Ghi chú</th>
                    <th className="px-4 py-3 text-[11px] font-bold uppercase tracking-wider text-right">
                      Thao tác
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-surface-container">
                  {intents.length === 0 && (
                    <tr>
                      <td colSpan={6} className="px-6 py-12 text-center text-on-surface-variant text-sm">
                        Chưa có nguyện vọng cho học kỳ này.
                        <div className="mt-3">
                          <button
                            type="button"
                            onClick={openAdd}
                            disabled={!hocKyId || preLocked}
                            className="text-primary font-bold hover:underline disabled:opacity-50"
                          >
                            Thêm học phần
                          </button>
                        </div>
                      </td>
                    </tr>
                  )}
                  {intents.map((row, idx) => (
                    <tr
                      key={row.id ?? idx}
                      className="hover:bg-surface-container-low/80 transition-colors group"
                    >
                      <td className="px-4 py-4">
                        <div className="flex items-center gap-2">
                          <span className="material-symbols-outlined text-outline opacity-40 text-[20px]">
                            drag_indicator
                          </span>
                          <span className="font-bold text-primary tabular-nums">
                            {String(row.priority ?? idx + 1).padStart(2, '0')}
                          </span>
                        </div>
                      </td>
                      <td className="px-4 py-4 font-mono text-sm text-on-surface-variant">{row.maHocPhan}</td>
                      <td className="px-4 py-4 font-semibold text-sm">{row.tenHocPhan}</td>
                      <td className="px-4 py-4">
                        <span className="bg-primary-fixed text-on-primary-fixed px-2.5 py-1 rounded-full text-xs font-bold">
                          {row.soTinChi ?? '—'}
                        </span>
                      </td>
                      <td className="px-4 py-4 text-sm italic text-on-surface-variant max-w-[12rem] truncate">
                        {row.ghiChu || '—'}
                      </td>
                      <td className="px-4 py-4 text-right space-x-1">
                        <button
                          type="button"
                          disabled={preLocked || actionBusy}
                          onClick={() => openEdit(row)}
                          className="p-2 text-on-surface-variant hover:text-primary transition-colors disabled:opacity-40"
                          aria-label="Sửa"
                        >
                          <span className="material-symbols-outlined text-[20px]">edit</span>
                        </button>
                        <button
                          type="button"
                          disabled={preLocked || actionBusy}
                          onClick={() => removeIntent(row)}
                          className="p-2 text-on-surface-variant hover:text-error transition-colors disabled:opacity-40"
                          aria-label="Xóa"
                        >
                          <span className="material-symbols-outlined text-[20px]">delete</span>
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </div>

        <aside className="lg:col-span-4 space-y-6">
          <div className="bg-surface-container rounded-2xl p-6">
            <h3 className="text-lg font-bold text-primary mb-3 flex items-center gap-2">
              <span className="material-symbols-outlined">info</span>
              Hướng dẫn đăng ký
            </h3>
            <ul className="space-y-3 text-on-surface-variant text-sm leading-relaxed">
              <li className="flex gap-3">
                <span className="bg-primary-container text-white h-6 w-6 rounded-full flex items-center justify-center shrink-0 text-xs font-bold">
                  1
                </span>
                <span>Sắp xếp học phần theo thứ tự ưu tiên quan trọng nhất.</span>
              </li>
              <li className="flex gap-3">
                <span className="bg-primary-container text-white h-6 w-6 rounded-full flex items-center justify-center shrink-0 text-xs font-bold">
                  2
                </span>
                <span>Dữ liệu giúp nhà trường dự báo mở lớp và phân bổ nguồn lực.</span>
              </li>
              <li className="flex gap-3">
                <span className="bg-primary-container text-white h-6 w-6 rounded-full flex items-center justify-center shrink-0 text-xs font-bold">
                  3
                </span>
                <span>Bạn có thể chỉnh sửa trước khi pha PRE kết thúc (nếu cửa sổ còn mở).</span>
              </li>
            </ul>
          </div>
          <div className="relative overflow-hidden bg-primary rounded-2xl p-6 text-white">
            <h4 className="text-base font-bold mb-2">Cần hỗ trợ?</h4>
            <p className="text-on-primary-container text-sm mb-4">
              Liên hệ phòng Đào tạo nếu bạn không tìm thấy học phần mong muốn.
            </p>
            <Link
              to="/student/tracuhscnhnthtconline"
              className="inline-block bg-white text-primary px-5 py-2 rounded-full text-sm font-bold hover:bg-primary-fixed transition-colors"
            >
              Hồ sơ & liên hệ
            </Link>
            <span className="material-symbols-outlined absolute -right-2 -bottom-2 text-white/10 text-8xl select-none pointer-events-none">
              support_agent
            </span>
          </div>
        </aside>
      </div>

      {modalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-on-surface/40 backdrop-blur-sm p-4">
          <div
            className="bg-surface-container-lowest w-full max-w-lg rounded-2xl shadow-2xl overflow-hidden"
            role="dialog"
            aria-modal="true"
            aria-labelledby="pre-modal-title"
          >
            <div className="px-6 py-4 border-b border-surface-container flex justify-between items-center bg-surface-container-low">
              <h3 id="pre-modal-title" className="text-lg font-bold text-primary">
                {editing ? 'Sửa nguyện vọng' : 'Thêm nguyện vọng học phần'}
              </h3>
              <button
                type="button"
                onClick={closeModal}
                className="text-on-surface-variant hover:text-on-surface"
                aria-label="Đóng"
              >
                <span className="material-symbols-outlined">close</span>
              </button>
            </div>
            <div className="p-6 space-y-5">
              <div className="space-y-2">
                <label className="text-xs font-bold uppercase tracking-wider text-on-surface-variant">
                  Học phần
                </label>
                {presetCurriculumCourse && editing == null ? (
                  <div className="rounded-xl bg-primary-fixed/15 border border-primary/20 px-4 py-3 text-sm">
                    <p className="font-mono font-bold text-primary">{presetCurriculumCourse.maHocPhan}</p>
                    <p className="font-semibold text-on-surface mt-1">{presetCurriculumCourse.tenHocPhan}</p>
                    <p className="text-xs text-on-surface-variant mt-2">
                      CTĐT · {prettyKhoi(presetCurriculumCourse.khoiKienThuc)}
                      {presetCurriculumCourse.hocKyGoiY != null
                        ? ` · Kỳ gợi ý Kỳ ${presetCurriculumCourse.hocKyGoiY}`
                        : ''}
                    </p>
                  </div>
                ) : (
                  <div className="relative">
                    <select
                      className="w-full bg-surface-container border-none rounded-xl px-4 py-3 focus:ring-2 focus:ring-primary"
                      value={formIdHocPhan}
                      onChange={(e) => setFormIdHocPhan(e.target.value)}
                      disabled={editing != null || coursesLoading}
                    >
                      <option value="">
                        {coursesLoading ? 'Đang tải…' : 'Chọn mã hoặc tên học phần…'}
                      </option>
                      {courseOptions.map((c) => (
                        <option
                          key={c.idHocPhan}
                          value={String(c.idHocPhan)}
                          disabled={intentIds.has(Number(c.idHocPhan)) && editing == null}
                        >
                          {c.maHocPhan} — {c.tenHocPhan}
                          {intentIds.has(Number(c.idHocPhan)) && editing == null ? ' (đã có)' : ''}
                        </option>
                      ))}
                    </select>
                  </div>
                )}
              </div>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div className="space-y-2">
                  <label className="text-xs font-bold uppercase tracking-wider text-on-surface-variant">
                    Mức độ ưu tiên
                  </label>
                  <input
                    type="number"
                    min={1}
                    className="w-full bg-surface-container border-none rounded-xl px-4 py-3 focus:ring-2 focus:ring-primary"
                    value={formPriority}
                    onChange={(e) => setFormPriority(Number(e.target.value))}
                  />
                </div>
                <div className="space-y-2">
                  <label className="text-xs font-bold uppercase tracking-wider text-on-surface-variant">
                    Học kỳ
                  </label>
                  <input
                    type="text"
                    readOnly
                    className="w-full bg-surface-container-low border-none rounded-xl px-4 py-3 text-on-surface-variant outline-none"
                    value={tenHocKyChon}
                  />
                </div>
              </div>
              <div className="space-y-2">
                <label className="text-xs font-bold uppercase tracking-wider text-on-surface-variant">
                  Ghi chú (nếu có)
                </label>
                <textarea
                  className="w-full bg-surface-container border-none rounded-xl px-4 py-3 focus:ring-2 focus:ring-primary h-24 resize-none"
                  placeholder="Nhập ghi chú cho bộ phận đào tạo…"
                  value={formGhiChu}
                  onChange={(e) => setFormGhiChu(e.target.value)}
                />
              </div>
            </div>
            <div className="px-6 py-4 bg-surface-container-low flex justify-end gap-3">
              <button
                type="button"
                onClick={closeModal}
                className="px-4 py-2 text-on-surface-variant font-bold hover:text-primary transition-colors"
              >
                Hủy
              </button>
              <button
                type="button"
                disabled={actionBusy || !formIdHocPhan}
                onClick={submitModal}
                className="bg-primary text-white px-6 py-2 rounded-full font-bold shadow-lg shadow-primary/20 hover:bg-primary-container transition-all disabled:opacity-50"
              >
                {actionBusy ? 'Đang lưu…' : 'Lưu nguyện vọng'}
              </button>
            </div>
          </div>
        </div>
      )}

      {toast && (
        <div className="fixed bottom-6 right-6 z-[60] max-w-md animate-in slide-in-from-right">
          <div
            className={`${toastStyles} px-4 py-3 rounded-xl shadow-2xl flex items-start gap-3 border-l-4`}
          >
            <span
              className="material-symbols-outlined shrink-0"
              style={toast.kind === 'error' ? { color: 'var(--color-error)' } : undefined}
            >
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

export default TnhNngTrcGiGPreRegistrationGiLp;
