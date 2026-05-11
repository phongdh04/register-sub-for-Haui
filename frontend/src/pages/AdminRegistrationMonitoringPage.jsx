import React, { useCallback, useEffect, useMemo, useState } from 'react';

import { API_BASE_URL, authHeaders } from '../config/api';

const PRESET_OPTIONS = [
  { id: '15m', label: '15 phút', ms: 15 * 60 * 1000 },
  { id: '1h', label: '1 giờ', ms: 60 * 60 * 1000 },
  { id: '24h', label: '24 giờ', ms: 24 * 60 * 60 * 1000 },
  { id: 'custom', label: 'Tùy chọn', ms: null }
];

const OUTCOME_COLORS = {
  SUCCESS: 'bg-primary',
  FULL: 'bg-secondary-container',
  DUPLICATE: 'bg-error',
  VALIDATION_FAILED: 'bg-surface-variant',
  REJECTED: 'bg-tertiary-container',
  CANCELLED: 'bg-outline'
};

async function parseBody(res) {
  const t = await res.text();
  if (!t) return {};
  try {
    return JSON.parse(t);
  } catch {
    return { message: t };
  }
}

function errMsg(body, fallback) {
  if (body?.message) return String(body.message);
  return fallback;
}

function pct(v) {
  return `${(Number(v || 0) * 100).toFixed(1)}%`;
}

function num(v) {
  return Number(v || 0).toLocaleString('vi-VN');
}

function toLocalDatetimeValue(d) {
  if (!(d instanceof Date) || Number.isNaN(d.getTime())) return '';
  const p = (n) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())}T${p(d.getHours())}:${p(d.getMinutes())}`;
}

function fromLocalDatetimeValue(v) {
  if (!v) return null;
  const t = new Date(v);
  return Number.isNaN(t.getTime()) ? null : t.toISOString();
}

const Skeleton = ({ className }) => (
  <div className={`animate-pulse rounded-lg bg-surface-container ${className || ''}`} />
);

const AdminRegistrationMonitoringPage = () => {
  const [hocKys, setHocKys] = useState([]);
  const [hocKyId, setHocKyId] = useState('');

  const [preset, setPreset] = useState('1h');
  const [autoRefresh, setAutoRefresh] = useState(true);
  const [lastUpdated, setLastUpdated] = useState(null);

  const [loadingOutcomes, setLoadingOutcomes] = useState(false);
  const [loadingThroughput, setLoadingThroughput] = useState(false);
  const [loadingFillRate, setLoadingFillRate] = useState(false);
  const [error, setError] = useState('');

  const [outcomes, setOutcomes] = useState(null);
  const [throughput, setThroughput] = useState(null);
  const [fillRate, setFillRate] = useState(null);

  const windowMs = useMemo(() => PRESET_OPTIONS.find((x) => x.id === preset)?.ms ?? 3600000, [preset]);
  const [customFrom, setCustomFrom] = useState('');
  const [customTo, setCustomTo] = useState('');

  const loadHocKy = useCallback(async () => {
    const res = await fetch(`${API_BASE_URL}/api/hoc-ky`, { headers: authHeaders() });
    const body = await parseBody(res);
    if (!res.ok) throw new Error(errMsg(body, 'Không tải được học kỳ.'));
    const list = Array.isArray(body) ? body : [];
    setHocKys(list);
    setHocKyId((cur) => {
      if (cur) return cur;
      const hk = list.find((x) => x.trangThaiHienHanh === true) || list[0];
      return hk ? String(hk.idHocKy ?? hk.id) : '';
    });
  }, []);

  const getRange = useCallback(() => {
    const now = new Date();
    if (preset !== 'custom') {
      return { from: new Date(now.getTime() - windowMs), to: now };
    }
    const fromIso = fromLocalDatetimeValue(customFrom);
    const toIso = fromLocalDatetimeValue(customTo);
    const from = fromIso ? new Date(fromIso) : null;
    const to = toIso ? new Date(toIso) : null;
    if (!from || !to || Number.isNaN(from.getTime()) || Number.isNaN(to.getTime()) || to <= from) {
      // Fallback an toàn nếu user chưa nhập đủ / nhập sai.
      return { from: new Date(now.getTime() - 3600000), to: now };
    }
    return { from, to };
  }, [preset, windowMs, customFrom, customTo]);

  const loadData = useCallback(async () => {
    if (!hocKyId) return;
    setError('');
    try {
      const { from, to } = getRange();
      const q = new URLSearchParams({ from: from.toISOString(), to: to.toISOString() });
      const qFill = new URLSearchParams({ hocKyId: String(hocKyId), from: from.toISOString(), to: to.toISOString() });

      setLoadingOutcomes(true);
      setLoadingThroughput(true);
      setLoadingFillRate(true);

      const [oRes, tRes, fRes] = await Promise.all([
        fetch(`${API_BASE_URL}/api/v1/admin/registration-monitoring/outcomes?${q}`, { headers: authHeaders() }),
        fetch(`${API_BASE_URL}/api/v1/admin/registration-monitoring/throughput?${q}`, { headers: authHeaders() }),
        fetch(`${API_BASE_URL}/api/v1/admin/registration-monitoring/fill-rate?${qFill}`, { headers: authHeaders() })
      ]);

      const [oBody, tBody, fBody] = await Promise.all([parseBody(oRes), parseBody(tRes), parseBody(fRes)]);
      if (!oRes.ok) throw new Error(errMsg(oBody, 'Không tải outcomes.'));
      if (!tRes.ok) throw new Error(errMsg(tBody, 'Không tải throughput.'));
      if (!fRes.ok) throw new Error(errMsg(fBody, 'Không tải fill-rate.'));

      setOutcomes(oBody);
      setThroughput(tBody);
      setFillRate(fBody);
      setLastUpdated(new Date());
    } catch (e) {
      setError(e.message || 'Lỗi tải dữ liệu giám sát.');
    } finally {
      setLoadingOutcomes(false);
      setLoadingThroughput(false);
      setLoadingFillRate(false);
    }
  }, [hocKyId, getRange]);

  useEffect(() => {
    loadHocKy().catch((e) => setError(e.message || 'Lỗi tải học kỳ.'));
  }, [loadHocKy]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  useEffect(() => {
    if (!autoRefresh) return undefined;
    const timer = window.setInterval(() => {
      loadData();
    }, 30000);
    return () => window.clearInterval(timer);
  }, [autoRefresh, loadData]);

  const outcomeEntries = useMemo(() => Object.entries(outcomes?.byOutcome || {}), [outcomes]);

  const throughputRows = useMemo(() => (Array.isArray(throughput?.rows) ? throughput.rows : []), [throughput]);

  const sortedFillRows = useMemo(() => {
    const rows = Array.isArray(fillRate?.rows) ? [...fillRate.rows] : [];
    rows.sort((a, b) => (b.fullEvents || 0) - (a.fullEvents || 0));
    return rows;
  }, [fillRate]);

  const [page, setPage] = useState(1);
  const pageSize = 10;
  const totalPages = useMemo(() => Math.max(1, Math.ceil(sortedFillRows.length / pageSize)), [sortedFillRows.length]);
  useEffect(() => {
    setPage(1);
  }, [hocKyId, preset, customFrom, customTo]);

  const pageRows = useMemo(() => {
    const start = (page - 1) * pageSize;
    return sortedFillRows.slice(start, start + pageSize);
  }, [sortedFillRows, page]);

  return (
    <div className="space-y-6">
      <header className="sticky top-0 z-10 flex items-center justify-between rounded-xl border border-surface-variant bg-surface-container-lowest p-4 shadow-sm">
        <div className="flex items-center gap-2">
          <h2 className="text-xl font-bold">Giám sát đăng ký</h2>
          <span className="mt-1 inline-block h-2 w-2 animate-pulse rounded-full bg-secondary-container" />
        </div>
        <div className="flex items-center gap-3">
          <div className="flex items-center rounded-full bg-surface-container p-1 text-xs">
            {PRESET_OPTIONS.map((p) => (
              <button
                key={p.id}
                type="button"
                onClick={() => setPreset(p.id)}
                className={`rounded-full px-3 py-1.5 font-medium ${preset === p.id ? 'bg-surface-container-lowest shadow-sm' : 'text-on-surface-variant'}`}
              >
                {p.label}
              </button>
            ))}
          </div>
          {preset === 'custom' && (
            <div className="hidden items-center gap-2 rounded-xl bg-surface-container px-3 py-2 text-xs lg:flex">
              <span className="text-on-surface-variant">Từ</span>
              <input
                type="datetime-local"
                value={customFrom}
                onChange={(e) => setCustomFrom(e.target.value)}
                className="rounded-lg bg-surface-container-lowest px-2 py-1 outline-none"
              />
              <span className="text-on-surface-variant">Đến</span>
              <input
                type="datetime-local"
                value={customTo}
                onChange={(e) => setCustomTo(e.target.value)}
                className="rounded-lg bg-surface-container-lowest px-2 py-1 outline-none"
              />
            </div>
          )}
          <select
            value={hocKyId}
            onChange={(e) => setHocKyId(e.target.value)}
            className="rounded-full bg-surface-container px-3 py-1.5 text-xs outline-none"
          >
            {hocKys.map((hk) => {
              const id = hk.idHocKy ?? hk.id;
              return <option key={id} value={String(id)}>{hk.tenHocKy || `HK${hk.kyThu} ${hk.namHoc}`}</option>;
            })}
          </select>
          <label className="flex items-center gap-2 text-xs">
            <span>Tự động</span>
            <input type="checkbox" checked={autoRefresh} onChange={(e) => setAutoRefresh(e.target.checked)} />
          </label>
          <button type="button" onClick={loadData} className="rounded-full bg-surface-container px-3 py-1.5 text-xs font-medium text-primary">
            Làm mới
          </button>
        </div>
      </header>

      {error && <div className="rounded-lg bg-error-container px-4 py-3 text-sm text-on-error-container">{error}</div>}

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-12">
        <div className="space-y-6 lg:col-span-8">
          <section className="grid grid-cols-1 gap-4 md:grid-cols-3">
            <div className="rounded-xl bg-surface-container-lowest p-5 shadow-sm">
              <p className="text-xs text-on-surface-variant">Tổng số yêu cầu</p>
              {loadingOutcomes ? (
                <div className="mt-3 space-y-2">
                  <Skeleton className="h-9 w-40" />
                  <Skeleton className="h-4 w-56" />
                </div>
              ) : (
                <>
                  <p className="mt-3 text-3xl font-bold">{num(outcomes?.total)}</p>
                  <p className="mt-2 text-xs text-on-surface-variant">
                    Trong cửa sổ {PRESET_OPTIONS.find((x) => x.id === preset)?.label}
                  </p>
                </>
              )}
            </div>
            <div className="rounded-xl bg-surface-container-lowest p-5 shadow-sm">
              <p className="text-xs text-on-surface-variant">Phân bổ kết quả</p>
              <div className="mt-3 space-y-2">
                {loadingOutcomes ? (
                  <>
                    <Skeleton className="h-4 w-full" />
                    <Skeleton className="h-4 w-5/6" />
                    <Skeleton className="h-4 w-4/6" />
                    <Skeleton className="h-4 w-3/6" />
                  </>
                ) : (
                  outcomeEntries.map(([k, v]) => (
                    <div key={k} className="flex items-center gap-2 text-xs">
                      <span className={`inline-block h-2 w-2 rounded-full ${OUTCOME_COLORS[k] || 'bg-outline'}`} />
                      <span className="min-w-[120px] text-on-surface">{k}</span>
                      <span className="font-semibold">{num(v)}</span>
                    </div>
                  ))
                )}
              </div>
            </div>
            <div className="rounded-xl bg-surface-container-lowest p-5 shadow-sm">
              <p className="text-xs text-on-surface-variant">Tỷ lệ thành công %</p>
              {loadingOutcomes ? (
                <div className="mt-3 space-y-2">
                  <Skeleton className="h-9 w-32" />
                  <Skeleton className="h-4 w-40" />
                </div>
              ) : (
                <>
                  <p className="mt-3 text-3xl font-bold">{(Number(outcomes?.successRate || 0) * 100).toFixed(1)}%</p>
                  <p className="mt-2 text-xs text-on-surface-variant">Không tính CANCELLED</p>
                </>
              )}
            </div>
          </section>

          <section className="overflow-hidden rounded-xl bg-surface-container-lowest shadow-sm">
            <div className="flex items-center justify-between border-b border-surface-variant p-5">
              <h3 className="text-base font-bold">Lưu lượng phân tích</h3>
            </div>
            <div className="overflow-x-auto p-5">
              <table className="w-full min-w-[500px] text-left text-sm">
                <thead>
                  <tr className="border-b border-surface-variant text-xs uppercase text-on-surface-variant">
                    <th className="py-2">Loại yêu cầu</th>
                    <th className="py-2">Kết quả</th>
                    <th className="py-2 text-right">Khối lượng</th>
                  </tr>
                </thead>
                <tbody>
                  {loadingThroughput ? (
                    <>
                      <tr><td colSpan={3} className="py-2"><Skeleton className="h-4 w-full" /></td></tr>
                      <tr><td colSpan={3} className="py-2"><Skeleton className="h-4 w-5/6" /></td></tr>
                      <tr><td colSpan={3} className="py-2"><Skeleton className="h-4 w-4/6" /></td></tr>
                    </>
                  ) : (
                    <>
                      {throughputRows.map((r, idx) => (
                        <tr key={`${r.requestType}-${r.outcome}-${idx}`} className="border-b border-surface-container-high last:border-0">
                          <td className="py-2">{r.requestType}</td>
                          <td className="py-2">{r.outcome}</td>
                          <td className="py-2 text-right font-medium">{num(r.count)}</td>
                        </tr>
                      ))}
                      {throughputRows.length === 0 && (
                        <tr><td className="py-4 text-on-surface-variant" colSpan={3}>Không có dữ liệu.</td></tr>
                      )}
                    </>
                  )}
                </tbody>
              </table>
            </div>
          </section>
        </div>

        <div className="space-y-6 lg:col-span-4">
          <section className="rounded-xl bg-surface-container-lowest p-5 shadow-sm">
            <h3 className="text-base font-bold">Tình trạng lấp đầy</h3>
            <div className="mt-4">
              <div className="mb-2 flex items-end justify-between">
                <span className="text-xs text-on-surface-variant">Tỷ lệ tổng thể</span>
                <span className="text-2xl font-bold">
                  {loadingFillRate ? <span className="inline-block align-middle"><Skeleton className="h-7 w-20" /></span> : pct(fillRate?.overallFillRate)}
                </span>
              </div>
              <div className="h-3 w-full overflow-hidden rounded-full bg-surface-container-high">
                {loadingFillRate ? (
                  <div className="h-3 w-1/2 rounded-full bg-surface-container" />
                ) : (
                  <div className="h-3 rounded-full bg-gradient-to-r from-primary to-primary-container" style={{ width: `${Math.max(0, Math.min(100, Number(fillRate?.overallFillRate || 0) * 100))}%` }} />
                )}
              </div>
              <div className="mt-2 flex justify-between text-[11px] text-on-surface-variant">
                {loadingFillRate ? (
                  <>
                    <Skeleton className="h-4 w-28" />
                    <Skeleton className="h-4 w-28" />
                  </>
                ) : (
                  <>
                    <span>Đã đăng ký: {num(fillRate?.takenSlots)}</span>
                    <span>Tổng chỉ tiêu: {num(fillRate?.totalSlots)}</span>
                  </>
                )}
              </div>
            </div>
            <div className="mt-5 grid grid-cols-2 gap-3 text-sm">
              <div>
                <p className="text-xs text-on-surface-variant">Tổng số lớp</p>
                {loadingFillRate ? <Skeleton className="mt-2 h-5 w-16" /> : <p className="font-bold">{num(fillRate?.totalClasses)}</p>}
              </div>
              <div>
                <p className="text-xs text-on-surface-variant">Lớp có FULL event</p>
                {loadingFillRate ? (
                  <Skeleton className="mt-2 h-5 w-16" />
                ) : (
                  <p className="font-bold text-secondary">{num((Array.isArray(fillRate?.rows) ? fillRate.rows.filter((r) => Number(r.fullEvents) > 0).length : 0))}</p>
                )}
              </div>
            </div>
          </section>
        </div>
      </div>

      <section className="overflow-hidden rounded-xl bg-surface-container-lowest shadow-sm">
        <div className="flex items-center justify-between border-b border-surface-variant p-5">
          <h3 className="text-base font-bold">Lớp học cần chú ý</h3>
          <div className="text-xs text-on-surface-variant">
            Hiển thị {(sortedFillRows.length === 0 ? 0 : (page - 1) * pageSize + 1)}-{Math.min(page * pageSize, sortedFillRows.length)} / {sortedFillRows.length}
          </div>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full min-w-[900px] text-left text-sm">
            <thead>
              <tr className="bg-surface-container/30 text-xs uppercase text-on-surface-variant">
                <th className="px-5 py-3">Mã lớp</th>
                <th className="px-5 py-3">Môn học</th>
                <th className="px-5 py-3 text-right">Chỉ tiêu</th>
                <th className="px-5 py-3 text-right">Đã đăng ký</th>
                <th className="px-5 py-3">Fill rate</th>
                <th className="px-5 py-3 text-center">Số lần FULL</th>
              </tr>
            </thead>
            <tbody>
              {loadingFillRate ? (
                <>
                  <tr><td className="px-5 py-4" colSpan={6}><Skeleton className="h-4 w-full" /></td></tr>
                  <tr><td className="px-5 py-4" colSpan={6}><Skeleton className="h-4 w-5/6" /></td></tr>
                  <tr><td className="px-5 py-4" colSpan={6}><Skeleton className="h-4 w-4/6" /></td></tr>
                </>
              ) : (
                <>
                  {pageRows.map((r) => {
                    const percent = Math.round(Number(r.fillRate || 0) * 100);
                    return (
                      <tr key={r.idLopHp} className="border-t border-surface-container-high">
                        <td className="px-5 py-3 font-medium text-primary">{r.maLopHp}</td>
                        <td className="px-5 py-3">{r.tenHocPhan || r.maHocPhan}</td>
                        <td className="px-5 py-3 text-right">{num(r.siSoToiDa)}</td>
                        <td className="px-5 py-3 text-right">{num(r.siSoThucTe)}</td>
                        <td className="px-5 py-3">
                          <div className="flex items-center gap-2">
                            <div className="h-1.5 w-full overflow-hidden rounded-full bg-surface-container-high">
                              <div className={`h-1.5 rounded-full ${percent >= 100 ? 'bg-error' : percent >= 95 ? 'bg-secondary' : 'bg-primary'}`} style={{ width: `${Math.min(100, percent)}%` }} />
                            </div>
                            <span className="text-xs font-semibold">{percent}%</span>
                          </div>
                        </td>
                        <td className="px-5 py-3 text-center"><span className="inline-flex h-6 w-6 items-center justify-center rounded bg-surface-container text-xs font-bold">{num(r.fullEvents)}</span></td>
                      </tr>
                    );
                  })}
                  {pageRows.length === 0 && (
                    <tr><td className="px-5 py-5 text-on-surface-variant" colSpan={6}>Chưa có dữ liệu lớp cần chú ý.</td></tr>
                  )}
                </>
              )}
            </tbody>
          </table>
        </div>
        <div className="flex items-center justify-between border-t border-surface-variant bg-surface-bright p-4 text-xs text-on-surface-variant">
          <span>Trang {page} / {totalPages}</span>
          <div className="flex gap-1">
            <button
              type="button"
              onClick={() => setPage((p) => Math.max(1, p - 1))}
              disabled={page <= 1 || loadingFillRate}
              className="rounded border border-outline-variant bg-surface-container-lowest px-2 py-1 disabled:cursor-not-allowed disabled:opacity-50"
            >
              <span className="material-symbols-outlined text-[16px]">chevron_left</span>
            </button>
            <button
              type="button"
              onClick={() => setPage((p) => Math.min(totalPages, p + 1))}
              disabled={page >= totalPages || loadingFillRate}
              className="rounded border border-outline-variant bg-surface-container-lowest px-2 py-1 disabled:cursor-not-allowed disabled:opacity-50"
            >
              <span className="material-symbols-outlined text-[16px]">chevron_right</span>
            </button>
          </div>
        </div>
      </section>

      <footer className="pb-4 text-center text-xs text-on-surface-variant">
        Lần cập nhật cuối: {lastUpdated ? lastUpdated.toLocaleTimeString('vi-VN') : '—'} UTC+7
      </footer>
    </div>
  );
};

export default AdminRegistrationMonitoringPage;
