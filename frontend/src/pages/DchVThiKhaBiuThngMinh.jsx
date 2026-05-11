import React, { useCallback, useEffect, useMemo, useState } from 'react';

import { API_BASE_URL, authHeaders } from '../config/api';

const WEEK_DAYS = [2, 3, 4, 5, 6, 7, 8];
const ROW_H = 80;
const NUM_PERIODS = 12;
const GRID_H = NUM_PERIODS * ROW_H;

/** Thứ 2–7 → thứ 2; CN → chủ nhật */
const formatWeekDayShort = (thu) => (thu === 8 ? 'CN' : `Thứ ${thu}`);

function startOfMonday(d) {
  const x = new Date(d);
  const day = x.getDay();
  const diff = day === 0 ? -6 : 1 - day;
  x.setDate(x.getDate() + diff);
  x.setHours(12, 0, 0, 0);
  return x;
}

function addDays(d, n) {
  const x = new Date(d);
  x.setDate(x.getDate() + n);
  return x;
}

function formatDdMm(d) {
  return d.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit' });
}

function parseTietRange(tiet) {
  if (tiet == null || tiet === '') return { start: 1, span: 1 };
  const s = String(tiet).trim();
  const parts = s.split(/[-–]/);
  const a = Number(parts[0]);
  const b = parts.length > 1 ? Number(parts[1]) : a;
  const start = Number.isFinite(a) ? Math.min(Math.max(a, 1), NUM_PERIODS) : 1;
  const end = Number.isFinite(b) ? Math.min(Math.max(b, start), NUM_PERIODS) : start;
  return { start, span: Math.max(1, end - start + 1) };
}

function extractPeriodStart(tiet) {
  const { start } = parseTietRange(tiet);
  return start;
}

const safeArray = (x) => (Array.isArray(x) ? x : []);

async function parseJson(res) {
  const t = await res.text();
  if (!t) return {};
  try {
    return JSON.parse(t);
  } catch {
    return {};
  }
}

/** Tầng lớp / màu block theo chỉ số (ổn định theo id dòng) */
function blockStyle(seed) {
  const themes = [
    {
      fill: 'bg-primary-fixed/60',
      on: 'text-on-primary-fixed',
      onV: 'text-on-primary-fixed-variant',
      badge: 'text-on-primary-fixed bg-surface-container-lowest/80',
      icon: 'text-primary',
      borderL: 'border-l-4 border-primary'
    },
    {
      fill: 'bg-tertiary-fixed/60',
      on: 'text-on-tertiary-fixed',
      onV: 'text-on-tertiary-fixed-variant',
      badge: 'text-on-tertiary-fixed bg-surface-container-lowest/80',
      icon: 'text-tertiary',
      borderL: 'border-l-4 border-tertiary'
    },
    {
      fill: 'bg-secondary-fixed/60',
      on: 'text-on-secondary-fixed',
      onV: 'text-on-secondary-fixed-variant',
      badge: 'text-on-secondary-fixed bg-surface-container-lowest/80',
      icon: 'text-secondary',
      borderL: 'border-l-4 border-secondary'
    }
  ];
  const i = Math.abs(Number(seed) || 0) % themes.length;
  return themes[i];
}

function isPracticeEntry(row) {
  const t = `${row.tenHocPhan || ''} ${row.maLopHp || ''}`.toLowerCase();
  return t.includes('thực hành') || t.includes('(th') || t.includes('_th') || t.includes('lab');
}

const TIET_TIMES = [
  '07:00',
  '07:50',
  '08:50',
  '09:50',
  '10:40',
  '13:00',
  '13:50',
  '14:50',
  '15:50',
  '16:40',
  '18:00',
  '18:50'
];

/** Hiển thị ngày dd/MM/yyyy từ API (string ISO, YYYY-MM-DD, hoặc mảng Jackson). */
function formatDateVi(v) {
  if (v == null || v === '') return '—';
  if (Array.isArray(v) && v.length >= 3) {
    const [y, m, d] = v;
    return `${String(d).padStart(2, '0')}/${String(m).padStart(2, '0')}/${y}`;
  }
  const s = String(v);
  if (/^\d{4}-\d{2}-\d{2}/.test(s)) {
    const [y, m, d] = s.slice(0, 10).split('-');
    return `${d}/${m}/${y}`;
  }
  const t = Date.parse(s);
  if (!Number.isNaN(t)) {
    const x = new Date(t);
    return x.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });
  }
  return s;
}

function courseKey(course, index = 0) {
  const k = course?.idLopHp ?? course?.maLopHp ?? course?.maHocPhan;
  if (k != null && k !== '') return String(k);
  return `course-${index}`;
}

/** Hai chữ đại diện môn từ mã học phần */
function courseInitials(maHocPhan) {
  const raw = String(maHocPhan || 'HP').replace(/[^a-zA-Z0-9]/g, '');
  if (raw.length >= 2) return raw.slice(0, 2).toUpperCase();
  return (String(maHocPhan || 'M').slice(0, 2) || 'M').toUpperCase();
}

/** Số lớp hiển thị: INT2204_01 → 01 */
function lopDisplaySuffix(maLopHp) {
  if (!maLopHp) return '—';
  const s = String(maLopHp);
  const i = s.lastIndexOf('_');
  if (i >= 0 && i < s.length - 1) return s.slice(i + 1);
  return s;
}

const AVATAR_THEMES = [
  'bg-primary-container text-on-primary-container',
  'bg-tertiary-container text-on-tertiary-container',
  'bg-secondary-container text-on-secondary-container'
];

/**
 * Thời khóa biểu sinh viên — Snapshot (read-model) + Legacy.
 */
const DchVThiKhaBiuThngMinh = () => {
  const [tab, setTab] = useState('snapshot');
  const [hocKys, setHocKys] = useState([]);
  const [hocKyId, setHocKyId] = useState('');
  const [weekOffset, setWeekOffset] = useState(0);
  const [bannerDismissed, setBannerDismissed] = useState(false);
  const [lastUpdated, setLastUpdated] = useState(null);

  const [snapLoading, setSnapLoading] = useState(true);
  const [snapErr, setSnapErr] = useState('');
  const [snapshot, setSnapshot] = useState(null);

  const [legLoading, setLegLoading] = useState(true);
  const [legErr, setLegErr] = useState('');
  const [legacy, setLegacy] = useState(null);
  const [legUpdated, setLegUpdated] = useState(null);
  const [legacyOpen, setLegacyOpen] = useState(() => new Set());

  const username = localStorage.getItem('username') || '';

  const loadHocKy = useCallback(async () => {
    const res = await fetch(`${API_BASE_URL}/api/hoc-ky`, { headers: authHeaders() });
    const body = await parseJson(res);
    if (!res.ok) throw new Error(body.message || 'Không tải được học kỳ.');
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

  const fetchSnapshot = useCallback(async () => {
    if (!hocKyId) {
      setSnapshot(null);
      return;
    }
    setSnapLoading(true);
    setSnapErr('');
    try {
      const qs = `?hocKyId=${encodeURIComponent(hocKyId)}`;
      const res = await fetch(`${API_BASE_URL}/api/v1/timetable/me/snapshot${qs}`, {
        headers: authHeaders()
      });
      const body = await parseJson(res);
      if (!res.ok) throw new Error(body.message || 'Không tải snapshot TKB.');
      setSnapshot(body);
      setLastUpdated(new Date());
    } catch (e) {
      setSnapErr(e.message || 'Lỗi snapshot.');
      setSnapshot(null);
    } finally {
      setSnapLoading(false);
    }
  }, [hocKyId]);

  const fetchLegacy = useCallback(async () => {
    if (!hocKyId) {
      setLegacy(null);
      return;
    }
    setLegLoading(true);
    setLegErr('');
    try {
      const qs = `?hocKyId=${encodeURIComponent(hocKyId)}`;
      const res = await fetch(`${API_BASE_URL}/api/v1/timetable/me${qs}`, {
        headers: authHeaders()
      });
      const body = await parseJson(res);
      if (!res.ok) throw new Error(body.message || 'Không tải TKB legacy.');
      setLegacy(body);
      setLegUpdated(new Date());
    } catch (e) {
      setLegErr(e.message || 'Lỗi TKB.');
      setLegacy(null);
      setLegUpdated(null);
    } finally {
      setLegLoading(false);
    }
  }, [hocKyId]);

  useEffect(() => {
    setLegacyOpen(new Set());
  }, [hocKyId]);

  useEffect(() => {
    if (!legacy?.courses?.length) return;
    setLegacyOpen((prev) => {
      if (prev.size > 0) return prev;
      const k = courseKey(legacy.courses[0], 0);
      return new Set([k]);
    });
  }, [legacy]);

  useEffect(() => {
    (async () => {
      try {
        await loadHocKy();
      } catch {
        /* handled via empty hocKyId */
      }
    })();
  }, [loadHocKy]);

  useEffect(() => {
    fetchSnapshot();
    fetchLegacy();
  }, [fetchSnapshot, fetchLegacy]);

  useEffect(() => {
    if (tab !== 'snapshot') return undefined;
    if (!hocKyId) return undefined;
    const id = window.setInterval(() => {
      fetchSnapshot();
    }, 28000);
    return () => window.clearInterval(id);
  }, [tab, hocKyId, fetchSnapshot]);

  const tenHocKyChon = useMemo(() => {
    const id = Number(hocKyId);
    const hk = hocKys.find((h) => Number(h.idHocKy ?? h.id) === id);
    return hk?.tenHocKy || hk?.ten || legacy?.tenHocKy || '—';
  }, [hocKys, hocKyId, legacy]);

  const entries = useMemo(() => safeArray(snapshot?.entries), [snapshot]);

  const entriesByDay = useMemo(() => {
    const map = new Map();
    WEEK_DAYS.forEach((d) => map.set(d, []));
    entries.forEach((e, idx) => {
      const thu = Number(e.thu);
      if (!map.has(thu)) return;
      map.get(thu).push({ ...e, _i: idx });
    });
    WEEK_DAYS.forEach((d) => {
      map.get(d).sort((a, b) => extractPeriodStart(a.tiet) - extractPeriodStart(b.tiet));
    });
    return map;
  }, [entries]);

  const mondayThisWeek = useMemo(() => {
    const base = startOfMonday(new Date());
    base.setDate(base.getDate() + weekOffset * 7);
    return base;
  }, [weekOffset]);

  const weekLabel = useMemo(() => {
    const mon = mondayThisWeek;
    const sun = addDays(mon, 6);
    return {
      mon,
      sun,
      text: `${formatDdMm(mon)} – ${formatDdMm(sun)}`
    };
  }, [mondayThisWeek]);

  const today = useMemo(() => {
    const t = new Date();
    t.setHours(12, 0, 0, 0);
    return t;
  }, []);

  const isSameDay = (a, b) =>
    a.getFullYear() === b.getFullYear() && a.getMonth() === b.getMonth() && a.getDate() === b.getDate();

  const totalTietSnapshot = useMemo(
    () => entries.reduce((s, e) => s + parseTietRange(e.tiet).span, 0),
    [entries]
  );

  const showSyncBanner =
    !bannerDismissed && !snapLoading && tab === 'snapshot' && hocKyId && entries.length === 0;

  const displayName = legacy?.hoTenSinhVien || username;
  const maSv = legacy?.maSinhVien || '';

  const slotBlocks = (list) =>
    list.map((row) => {
      const { start, span } = parseTietRange(row.tiet);
      const top = (start - 1) * ROW_H;
      const height = span * ROW_H;
      const st = blockStyle(row.idDangKy ?? row._i ?? row.maLopHp);
      const practice = isPracticeEntry(row);
      return (
        <div
          key={`${row.maLopHp}-${row.tiet}-${row._i ?? row.idDangKy}`}
          className="absolute w-full px-1.5 py-1"
          style={{ top, height }}
        >
          <div
            className={`group relative flex h-full w-full cursor-pointer flex-col rounded-xl p-3 transition-shadow hover:shadow-[0_8px_24px_rgba(20,27,43,0.12)] ${st.fill} ${st.borderL}`}
          >
            <div className="mb-1 flex items-start justify-between">
              <span className={`rounded-md px-2 py-0.5 text-xs font-bold backdrop-blur-sm ${st.badge}`}>
                {practice ? 'Thực hành' : 'Lý thuyết'}
              </span>
              <span className={`material-symbols-outlined text-[18px] ${st.icon}`}>
                {practice ? 'laptop_mac' : 'menu_book'}
              </span>
            </div>
            <h3 className={`mt-1 text-sm font-bold leading-tight ${st.on}`}>{row.maLopHp}</h3>
            <p className={`mt-0.5 truncate text-xs font-medium ${st.onV}`}>{row.tenHocPhan}</p>
            <div className="mt-auto space-y-1">
              <div className={`flex items-center text-xs ${st.onV}`}>
                <span className="material-symbols-outlined mr-1 text-[14px]">schedule</span>
                <span>Tiết {row.tiet}</span>
              </div>
              <div className={`flex items-center text-xs font-bold ${st.onV}`}>
                <span className="material-symbols-outlined mr-1 text-[14px]">location_on</span>
                <span>P. {row.phong || '—'}</span>
              </div>
            </div>
            <div className="pointer-events-none absolute left-full top-0 z-50 ml-2 hidden w-64 origin-left rounded-xl border border-surface-container bg-surface-container-lowest p-4 shadow-[0_20px_40px_rgba(20,27,43,0.1)] transition-transform group-hover:pointer-events-auto group-hover:block group-hover:scale-100">
              <h4 className="mb-2 text-base font-bold leading-tight text-on-surface">{row.tenHocPhan}</h4>
              <div className="space-y-2 text-sm">
                <div className="flex items-start">
                  <span className="material-symbols-outlined mr-2 mt-0.5 text-[18px] text-outline">person</span>
                  <div>
                    <span className="block text-xs text-on-surface-variant">Giảng viên</span>
                    <span className="font-semibold text-on-surface">{row.tenGiangVien || '—'}</span>
                  </div>
                </div>
                <div className="flex items-start">
                  <span className="material-symbols-outlined mr-2 mt-0.5 text-[18px] text-outline">date_range</span>
                  <div>
                    <span className="block text-xs text-on-surface-variant">Thời gian học</span>
                    <span className="font-semibold text-on-surface">
                      {row.ngayBatDau || '—'} → {row.ngayKetThuc || '—'}
                    </span>
                  </div>
                </div>
                <div className="flex items-start">
                  <span className="material-symbols-outlined mr-2 mt-0.5 text-[18px] text-outline">info</span>
                  <div>
                    <span className="block text-xs text-on-surface-variant">Mã lớp</span>
                    <span className="font-semibold text-on-surface">{row.maLopHp}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      );
    });

  return (
    <div className="min-h-[60vh] text-on-background">
      <header className="mb-6 flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
        <div className="flex flex-wrap items-center gap-4">
          <h1 className="text-2xl font-bold text-on-surface">Thời khóa biểu</h1>
          <div className="relative">
            <select
              className="appearance-none rounded-lg bg-surface-container-lowest px-4 py-2 pr-9 text-sm font-medium text-on-surface shadow-[0_2px_8px_rgba(20,27,43,0.04)] focus:outline-none focus:ring-2 focus:ring-primary"
              value={hocKyId}
              onChange={(e) => {
                setHocKyId(e.target.value);
                setBannerDismissed(false);
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
            <span className="pointer-events-none absolute right-2 top-1/2 -translate-y-1/2 text-outline material-symbols-outlined text-lg">
              expand_more
            </span>
          </div>
          <div className="hidden items-center space-x-1.5 rounded-md bg-surface-container px-3 py-1.5 text-xs font-medium text-on-surface-variant md:flex">
            <span className="material-symbols-outlined text-[16px]">sync</span>
            <span>Đồng bộ nhanh</span>
          </div>
        </div>
        <div className="flex flex-wrap items-center gap-4">
          <span className="text-sm font-medium text-primary hover:text-primary-container">Tài liệu hướng dẫn</span>
          <div className="hidden items-center gap-3 border-l border-outline-variant/30 pl-6 md:flex">
            <div className="text-right">
              <div className="text-sm font-bold text-on-surface">{displayName}</div>
              <div className="text-xs font-medium text-on-surface-variant">
                {maSv ? `${maSv}` : 'Sinh viên'}
              </div>
            </div>
            <div className="flex h-10 w-10 items-center justify-center rounded-full bg-primary-container text-lg font-bold text-on-primary ring-2 ring-surface-container">
              {displayName ? displayName.charAt(0).toUpperCase() : '?'}
            </div>
          </div>
        </div>
      </header>

      <div className="mx-auto max-w-7xl space-y-6">
        <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
          <div className="flex items-center gap-8 border-b border-outline-variant/30 pb-1">
            <button
              type="button"
              onClick={() => setTab('snapshot')}
              className={`pb-3 px-2 text-sm font-medium transition-colors ${
                tab === 'snapshot'
                  ? 'border-b-2 border-primary font-bold text-primary'
                  : 'text-on-surface-variant hover:text-primary'
              }`}
            >
              Snapshot
            </button>
            <button
              type="button"
              onClick={() => setTab('legacy')}
              className={`flex items-center gap-2 pb-3 px-2 text-sm transition-colors ${
                tab === 'legacy'
                  ? 'border-b-2 border-primary font-bold text-primary'
                  : 'text-on-surface-variant hover:text-primary'
              }`}
            >
              Legacy
              <span className="material-symbols-outlined text-[16px] text-secondary">history</span>
            </button>
          </div>
          {tab === 'snapshot' && (
            <div className="flex items-center space-x-2 rounded-xl bg-surface-container-lowest px-2 py-1.5 shadow-[0_2px_8px_rgba(20,27,43,0.03)]">
              <button
                type="button"
                className="rounded-md p-1.5 text-outline transition-colors hover:bg-surface-container hover:text-on-surface"
                onClick={() => setWeekOffset((w) => w - 1)}
                aria-label="Tuần trước"
              >
                <span className="material-symbols-outlined text-xl">chevron_left</span>
              </button>
              <span className="px-2 text-sm font-bold text-on-surface">Tuần: {weekLabel.text}</span>
              <button
                type="button"
                className="rounded-md p-1.5 text-outline transition-colors hover:bg-surface-container hover:text-on-surface"
                onClick={() => setWeekOffset((w) => w + 1)}
                aria-label="Tuần sau"
              >
                <span className="material-symbols-outlined text-xl">chevron_right</span>
              </button>
              <button
                type="button"
                className="ml-2 rounded-md bg-primary/10 px-3 py-1.5 text-xs font-semibold text-primary hover:bg-primary/20"
                onClick={() => setWeekOffset(0)}
              >
                Hôm nay
              </button>
            </div>
          )}
        </div>

        {tab === 'snapshot' && showSyncBanner && (
          <div className="flex flex-col items-start justify-between gap-4 rounded-r-xl border-l-4 border-secondary-container bg-secondary-container/10 p-4 sm:flex-row sm:items-center">
            <div className="flex items-center space-x-3">
              <span className="material-symbols-outlined animate-spin text-secondary-container" style={{ animationDuration: '3s' }}>
                sync
              </span>
              <div>
                <h4 className="text-sm font-bold text-on-surface">Đang đồng bộ TKB…</h4>
                <p className="mt-0.5 text-xs text-on-surface-variant">
                  Chưa có slot trong read-model. Thử làm mới hoặc xem Legacy nếu học kỳ đã có đăng ký.
                </p>
              </div>
            </div>
            <div className="flex items-center space-x-3">
              <button
                type="button"
                onClick={() => fetchSnapshot()}
                className="rounded-full bg-surface-container-lowest px-4 py-2 text-xs font-bold text-secondary shadow-sm transition-all hover:shadow-md"
              >
                Thử làm mới
              </button>
              <button
                type="button"
                onClick={() => setTab('legacy')}
                className="rounded-full bg-transparent px-4 py-2 text-xs font-bold text-on-surface-variant hover:bg-surface-variant"
              >
                Chuyển Legacy
              </button>
              <button type="button" className="text-xs text-outline hover:text-on-surface" onClick={() => setBannerDismissed(true)}>
                Đóng
              </button>
            </div>
          </div>
        )}

        {tab === 'snapshot' && snapErr && (
          <div className="rounded-xl border border-error/30 bg-error-container/40 p-4 text-sm text-error">{snapErr}</div>
        )}

        {tab === 'snapshot' && snapLoading && (
          <div className="rounded-xl bg-surface-container-lowest p-6 text-sm text-on-surface-variant">Đang tải snapshot…</div>
        )}

        {tab === 'snapshot' && !snapLoading && !snapErr && (
          <div className="overflow-hidden rounded-xl bg-surface-container-lowest shadow-[0_4px_24px_rgba(20,27,43,0.04)]">
            <div className="sticky top-0 z-10 grid grid-cols-[60px_1fr_1fr_1fr_1fr_1fr_1fr_1fr] border-b border-transparent bg-surface-container-low">
              <div className="flex items-center justify-center p-3 text-xs font-bold uppercase tracking-wider text-outline">
                Tiết
              </div>
              {WEEK_DAYS.map((day, idx) => {
                const d = addDays(mondayThisWeek, idx);
                const weekend = day >= 7;
                const isToday = isSameDay(d, today);
                return (
                  <div
                    key={day}
                    className={`relative border-l border-surface-container-highest/50 p-3 text-center ${weekend ? 'bg-surface-dim/20' : ''} ${isToday ? 'bg-primary/5' : ''}`}
                  >
                    <div
                      className={`mb-1 text-xs font-bold uppercase tracking-wider ${
                        isToday ? 'text-primary' : 'text-on-surface'
                      }`}
                    >
                      {formatWeekDayShort(day)}
                    </div>
                    <div className={`text-sm font-medium ${isToday ? 'font-bold text-primary' : 'text-outline'}`}>
                      {formatDdMm(d)}
                    </div>
                    {isToday && <div className="absolute bottom-0 left-0 h-1 w-full bg-primary" />}
                  </div>
                );
              })}
            </div>

            <div className="relative max-h-[600px] overflow-y-auto [scrollbar-width:thin]">
              <div className="pointer-events-none absolute inset-0 z-0">
                <div className="grid h-full min-h-[960px] w-full grid-rows-12">
                  {Array.from({ length: NUM_PERIODS }).map((_, i) => (
                    <div
                      key={i}
                      className={`h-[80px] border-b border-surface-container-low ${i === 5 ? 'bg-surface-container-low/50' : ''}`}
                    />
                  ))}
                </div>
              </div>

              <div
                className="relative z-10 grid min-h-[960px] grid-cols-[60px_1fr_1fr_1fr_1fr_1fr_1fr_1fr]"
                style={{ height: GRID_H }}
              >
                <div className="flex flex-col items-center border-r border-surface-container-low bg-surface-container-lowest pt-2">
                  {Array.from({ length: NUM_PERIODS }).map((_, i) => (
                    <div key={i} className="relative flex h-[80px] w-full flex-col items-center justify-start">
                      <span className="text-sm font-bold text-on-surface-variant">{i + 1}</span>
                      <span className="absolute top-6 text-[10px] text-outline">{TIET_TIMES[i] || ''}</span>
                    </div>
                  ))}
                </div>

                {WEEK_DAYS.map((day) => (
                  <div
                    key={day}
                    className={`relative border-r border-surface-container-low/50 ${day >= 7 ? 'bg-surface-dim/10' : ''}`}
                  >
                    {slotBlocks(entriesByDay.get(day) || [])}
                  </div>
                ))}
              </div>
            </div>

            <div className="flex flex-col items-center justify-between gap-4 border-t border-transparent bg-surface-container-low p-4 sm:flex-row">
              <div className="flex flex-wrap items-center gap-6 text-sm font-medium">
                <div className="flex items-center space-x-2">
                  <span className="h-3 w-3 rounded-full border border-primary bg-primary-fixed" />
                  <span className="text-on-surface">Lý thuyết</span>
                </div>
                <div className="flex items-center space-x-2">
                  <span className="h-3 w-3 rounded-full border border-tertiary bg-tertiary-fixed" />
                  <span className="text-on-surface">Thực hành</span>
                </div>
                <div className="flex items-center space-x-2">
                  <span className="h-3 w-3 rounded-full border border-secondary bg-secondary-fixed" />
                  <span className="text-on-surface">Khác</span>
                </div>
              </div>
              <div className="flex flex-wrap items-center gap-6">
                <div className="text-sm font-medium text-on-surface-variant">
                  Tổng số: <span className="font-bold text-primary">{totalTietSnapshot}</span> tiết / tuần
                  {snapshot?.totalSlots != null && (
                    <span className="ml-2 text-xs text-outline">({snapshot.totalSlots} slot)</span>
                  )}
                </div>
                <div className="flex items-center space-x-1 text-xs text-outline">
                  <span className="material-symbols-outlined text-[16px]">history</span>
                  <span>
                    Cập nhật:{' '}
                    {lastUpdated
                      ? lastUpdated.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })
                      : '—'}
                  </span>
                </div>
              </div>
            </div>
          </div>
        )}

        {tab === 'legacy' && (
          <>
            {legLoading && (
              <div className="rounded-xl bg-surface-container-lowest p-6 text-sm text-on-surface-variant">
                Đang tải TKB (Legacy)…
              </div>
            )}
            {legErr && (
              <div className="rounded-xl border border-error/30 bg-error-container/40 p-4 text-sm text-error">{legErr}</div>
            )}
            {!legLoading && !legErr && legacy && (
              <div className="mx-auto max-w-6xl space-y-8">
                <div className="flex flex-col justify-between gap-6 md:flex-row md:items-end">
                  <div>
                    <p className="text-sm font-medium text-on-surface-variant">{legacy.tenHocKy || tenHocKyChon}</p>
                    <p className="mt-1 text-xs text-outline">Nguồn Legacy · {legacy.hoTenSinhVien || username}</p>
                  </div>
                  <div className="flex flex-wrap gap-3">
                    <div className="flex items-center gap-2 rounded-full bg-surface-container-highest px-4 py-2 text-sm font-medium text-on-surface">
                      <span className="material-symbols-outlined text-[18px] text-primary">library_books</span>
                      Tổng số môn:{' '}
                      <span className="font-bold">{legacy.tongMonDangKy ?? safeArray(legacy.courses).length}</span>
                    </div>
                    <div className="flex items-center gap-2 rounded-full bg-surface-container-highest px-4 py-2 text-sm font-medium text-on-surface">
                      <span className="material-symbols-outlined text-[18px] text-secondary">military_tech</span>
                      Tổng tín chỉ: <span className="font-bold">{legacy.tongTinChi ?? '—'}</span>
                    </div>
                  </div>
                </div>

                <div className="flex w-full max-w-2xl items-start gap-3 rounded-xl border border-outline-variant/20 bg-secondary-container/20 p-4 text-on-surface">
                  <span className="material-symbols-outlined mt-0.5 text-secondary">info</span>
                  <p className="text-sm leading-relaxed">
                    <strong>Lưu ý:</strong> Dữ liệu ở tab Legacy có thể tải chậm hơn so với bản Snapshot. Nguồn
                    aggregate từ đăng ký — nếu Snapshot đã cập nhật mà đây chưa khớp, thử làm mới trang sau vài
                    giây.
                  </p>
                </div>

                <div className="space-y-6">
                  {safeArray(legacy.courses).length === 0 ? (
                    <div className="rounded-xl bg-surface-container-lowest p-8 text-center text-sm text-on-surface-variant shadow-[0_20px_40px_rgba(20,27,43,0.05)]">
                      Chưa có lớp học phần nào trong học kỳ này.
                    </div>
                  ) : (
                    safeArray(legacy.courses).map((course, cidx) => {
                      const key = courseKey(course, cidx);
                      const open = legacyOpen.has(key);
                      const sessions = safeArray(course.sessions).slice().sort((a, b) => {
                        if (a.thu !== b.thu) return (a.thu || 99) - (b.thu || 99);
                        return String(a.tiet).localeCompare(String(b.tiet));
                      });
                      const avatarCls = AVATAR_THEMES[cidx % AVATAR_THEMES.length];
                      const tietLabel = (t) => {
                        const p = parseTietRange(t);
                        return p.span > 1 ? `${p.start} - ${p.start + p.span - 1}` : String(t ?? '—');
                      };
                      return (
                        <div
                          key={key}
                          className="overflow-hidden rounded-xl bg-surface-container-lowest shadow-[0_20px_40px_rgba(20,27,43,0.05)] transition-all duration-200"
                        >
                          <button
                            type="button"
                            onClick={() => {
                              setLegacyOpen((prev) => {
                                const next = new Set(prev);
                                if (next.has(key)) next.delete(key);
                                else next.add(key);
                                return next;
                              });
                            }}
                            className="flex w-full cursor-pointer flex-col gap-4 p-6 text-left transition-colors hover:bg-surface-container-low md:flex-row md:items-center md:justify-between"
                          >
                            <div className="flex flex-1 items-start gap-4">
                              <div
                                className={`flex h-12 w-12 shrink-0 items-center justify-center rounded-xl text-lg font-bold ${avatarCls}`}
                              >
                                {courseInitials(course.maHocPhan)}
                              </div>
                              <div className="min-w-0">
                                <div className="mb-1 flex flex-wrap items-center gap-3">
                                  <span className="rounded bg-surface-dim px-2 py-0.5 text-xs font-bold uppercase tracking-wider text-on-surface-variant">
                                    {course.maHocPhan}
                                  </span>
                                  <h3 className="text-lg font-bold text-on-surface">{course.tenHocPhan}</h3>
                                </div>
                                <div className="mt-2 flex flex-wrap gap-x-4 gap-y-2 text-sm text-on-surface-variant">
                                  <span className="flex items-center gap-1">
                                    <span className="material-symbols-outlined text-[16px]">credit_score</span>
                                    {course.soTinChi ?? '—'} tín chỉ
                                  </span>
                                  <span className="flex items-center gap-1">
                                    <span className="material-symbols-outlined text-[16px]">meeting_room</span>
                                    Lớp: {lopDisplaySuffix(course.maLopHp)}
                                  </span>
                                  <span className="flex items-center gap-1">
                                    <span className="material-symbols-outlined text-[16px]">person</span>
                                    {course.tenGiangVien || '—'}
                                  </span>
                                </div>
                              </div>
                            </div>
                            <div className="shrink-0 md:ml-0 md:self-center">
                              <span
                                className={`material-symbols-outlined inline-flex rounded-full bg-primary/10 p-2 text-primary transition-transform duration-200 ${
                                  open ? 'rotate-180' : ''
                                }`}
                              >
                                expand_more
                              </span>
                            </div>
                          </button>
                          {open && sessions.length > 0 && (
                            <div className="border-t border-outline-variant/20 bg-surface-container-low/50 p-6">
                              <div className="overflow-x-auto">
                                <table className="w-full border-collapse text-left">
                                  <thead>
                                    <tr className="border-b border-outline-variant/20 text-xs font-bold uppercase tracking-wider text-on-surface-variant">
                                      <th className="px-4 pb-3">Thứ</th>
                                      <th className="px-4 pb-3">Tiết</th>
                                      <th className="px-4 pb-3">Phòng</th>
                                      <th className="px-4 pb-3">Ngày bắt đầu</th>
                                      <th className="px-4 pb-3">Ngày kết thúc</th>
                                    </tr>
                                  </thead>
                                  <tbody className="text-sm text-on-surface">
                                    {sessions.map((session, sidx) => (
                                      <tr
                                        key={`${key}-s-${sidx}`}
                                        className={`hover:bg-surface-container-low transition-colors ${
                                          sidx % 2 === 1 ? 'bg-surface-container-low/30' : ''
                                        }`}
                                      >
                                        <td className="px-4 py-4 font-medium">{formatWeekDayShort(session.thu)}</td>
                                        <td className="px-4 py-4">{tietLabel(session.tiet)}</td>
                                        <td className="px-4 py-4">
                                          <span className="rounded bg-surface-container-highest px-2 py-1 text-xs font-medium">
                                            {session.phong || '—'}
                                          </span>
                                        </td>
                                        <td className="px-4 py-4 text-on-surface-variant">
                                          {formatDateVi(session.ngayBatDau)}
                                        </td>
                                        <td className="px-4 py-4 text-on-surface-variant">
                                          {formatDateVi(session.ngayKetThuc)}
                                        </td>
                                      </tr>
                                    ))}
                                  </tbody>
                                </table>
                              </div>
                            </div>
                          )}
                          {open && sessions.length === 0 && (
                            <div className="border-t border-outline-variant/20 bg-surface-container-low/50 px-6 py-4 text-sm text-on-surface-variant">
                              Chưa có buổi học cụ thể trong dữ liệu Legacy.
                            </div>
                          )}
                        </div>
                      );
                    })
                  )}
                </div>

                <div className="mt-12 flex justify-end border-t border-outline-variant/20 pt-6">
                  <p className="flex items-center gap-2 text-xs text-on-surface-variant">
                    <span className="material-symbols-outlined text-[14px]">update</span>
                    Cập nhật:{' '}
                    {legUpdated
                      ? `${legUpdated.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })}, ${
                          legUpdated.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' }) ===
                          new Date().toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' })
                            ? 'Hôm nay'
                            : legUpdated.toLocaleDateString('vi-VN')
                        }`
                      : '—'}
                  </p>
                </div>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
};

export default DchVThiKhaBiuThngMinh;
