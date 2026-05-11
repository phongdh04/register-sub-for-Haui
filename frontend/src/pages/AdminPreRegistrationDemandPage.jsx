import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';

import { API_BASE_URL, authHeaders } from '../config/api';

async function parseBody(res) {
  const t = await res.text();
  if (!t) return {};
  try {
    return JSON.parse(t);
  } catch {
    return { message: t };
  }
}

function errMsg(b, d) {
  if (b?.message) return String(b.message);
  return d;
}

const safeArray = (x) => (Array.isArray(x) ? x : []);

/** Độ nhu cầu 0–1 để tô màu ô (so với max totalIntent trong bảng). */
function intentHeatRatio(intent, maxIntent) {
  if (!maxIntent || maxIntent <= 0) return 0;
  return Math.min(1, intent / maxIntent);
}

function heatCellClass(ratio) {
  if (ratio >= 0.75) return 'bg-error-container/35 text-on-surface';
  if (ratio >= 0.5) return 'bg-secondary-container/30 text-on-surface';
  if (ratio >= 0.25) return 'bg-primary-fixed/40 text-on-surface';
  return 'bg-surface-container-low/80 text-on-surface';
}

/** Khóa idempotent cố định theo dòng + bộ lọc (tránh double-click tạo trùng mã lớp). */
function planIdempotencyKey(hocKyId, row, targetClassSizeStr) {
  const nn = row.namNhapHoc != null ? String(row.namNhapHoc) : 'all';
  const nid = row.idNganh != null ? String(row.idNganh) : 'all';
  const tcs = targetClassSizeStr?.trim() || 'default';
  return `preplan-${hocKyId}-${row.idHocPhan}-${nn}-${nid}-${tcs}-rec`;
}

/**
 * Admin — Nhu cầu đăng ký dự kiến (PRE).
 * GET /api/v1/admin/pre-registrations/demand
 */
const AdminPreRegistrationDemandPage = () => {
  const [hocKys, setHocKys] = useState([]);
  const [hocKyId, setHocKyId] = useState('');
  const [namNhapHoc, setNamNhapHoc] = useState('');
  const [idNganh, setIdNganh] = useState('');
  const [targetClassSize, setTargetClassSize] = useState('');
  const [nganhList, setNganhList] = useState([]);

  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [sortKey, setSortKey] = useState('totalIntent');
  const [sortDir, setSortDir] = useState('desc');
  const [planningKey, setPlanningKey] = useState(null);
  /** Lỗi khi gọi plan-sections (tách khỏi lỗi tải demand). */
  const [planError, setPlanError] = useState('');
  /** Sau khi tạo/replay shell — hướng dẫn bước tiếp theo cho admin. */
  const [planGuide, setPlanGuide] = useState(null);

  const loadMeta = useCallback(async () => {
    const [hkRes, ngRes] = await Promise.all([
      fetch(`${API_BASE_URL}/api/hoc-ky`, { headers: authHeaders() }),
      fetch(`${API_BASE_URL}/api/nganh-dao-tao`, { headers: authHeaders() })
    ]);
    const hkBody = await parseBody(hkRes);
    const ngBody = await parseBody(ngRes);
    if (hkRes.ok) {
      const list = safeArray(hkBody);
      setHocKys(list);
      setHocKyId((cur) => {
        if (cur) return cur;
        if (list.length === 0) return '';
        const h = list.find((x) => x.trangThaiHienHanh === true) || list[0];
        return String(h.idHocKy ?? h.id);
      });
    }
    if (ngRes.ok) setNganhList(safeArray(ngBody));
  }, []);

  const fetchDemand = useCallback(async () => {
    if (!hocKyId) {
      setData(null);
      return;
    }
    setLoading(true);
    setError('');
    try {
      const q = new URLSearchParams({ hocKyId });
      if (namNhapHoc.trim() !== '') q.set('namNhapHoc', namNhapHoc.trim());
      if (idNganh) q.set('idNganh', idNganh);
      if (targetClassSize.trim() !== '') q.set('targetClassSize', targetClassSize.trim());
      const res = await fetch(`${API_BASE_URL}/api/v1/admin/pre-registrations/demand?${q}`, {
        headers: authHeaders()
      });
      const body = await parseBody(res);
      if (!res.ok) throw new Error(errMsg(body, 'Không tải được nhu cầu.'));
      setData(body);
    } catch (e) {
      setError(e.message || 'Lỗi tải dữ liệu.');
      setData(null);
    } finally {
      setLoading(false);
    }
  }, [hocKyId, namNhapHoc, idNganh, targetClassSize]);

  useEffect(() => {
    loadMeta();
  }, [loadMeta]);

  useEffect(() => {
    fetchDemand();
  }, [fetchDemand]);

  const items = useMemo(() => safeArray(data?.items), [data]);

  const maxIntent = useMemo(() => items.reduce((m, it) => Math.max(m, Number(it.totalIntent) || 0), 0), [items]);

  const sortedItems = useMemo(() => {
    const copy = [...items];
    copy.sort((a, b) => {
      const av = a[sortKey];
      const bv = b[sortKey];
      const an = typeof av === 'number' ? av : Number(av) || String(av || '');
      const bn = typeof bv === 'number' ? bv : Number(bv) || String(bv || '');
      if (typeof an === 'number' && typeof bn === 'number') {
        return sortDir === 'desc' ? bn - an : an - bn;
      }
      const as = String(an);
      const bs = String(bn);
      return sortDir === 'desc' ? bs.localeCompare(as) : as.localeCompare(bs);
    });
    return copy;
  }, [items, sortKey, sortDir]);

  const toggleSort = (key) => {
    if (sortKey === key) {
      setSortDir((d) => (d === 'desc' ? 'asc' : 'desc'));
    } else {
      setSortKey(key);
      setSortDir('desc');
    }
  };

  const planShellsForRow = async (row) => {
    if (!hocKyId || !row?.idHocPhan) return;
    const tid = Number(row.totalIntent) || 0;
    if (tid <= 0) {
      setPlanError('Dòng này có tổng intent = 0 — không tạo shell theo nhu cầu.');
      setPlanGuide(null);
      return;
    }
    const rowKey = `${row.idHocPhan}-${row.namNhapHoc ?? 'x'}-${row.idNganh ?? 'x'}`;
    setPlanningKey(rowKey);
    setPlanError('');
    setPlanGuide(null);
    try {
      const body = {
        hocKyId: Number(hocKyId),
        idHocPhan: Number(row.idHocPhan),
        useRecommendedFromDemand: true
      };
      if (row.namNhapHoc != null) body.namNhapHoc = Number(row.namNhapHoc);
      if (row.idNganh != null) body.idNganh = Number(row.idNganh);
      if (targetClassSize.trim() !== '') {
        const n = Number(targetClassSize.trim());
        if (Number.isFinite(n) && n > 0) body.targetClassSize = n;
      }
      const idem = planIdempotencyKey(hocKyId, row, targetClassSize);
      const res = await fetch(`${API_BASE_URL}/api/v1/admin/pre-registrations/plan-sections`, {
        method: 'POST',
        headers: { ...authHeaders(), 'Idempotency-Key': idem },
        body: JSON.stringify(body)
      });
      const b = await parseBody(res);
      if (!res.ok) {
        const msg = errMsg(b, res.status === 409 ? 'Xung đột mã lớp (một phần đã tồn tại).' : 'Không tạo được lớp shell.');
        throw new Error(msg);
      }
      const replay = b.idempotentReplay === true;
      const planned = b.sectionCountPlanned ?? safeArray(b.createdLopHpIds).length;
      const ids = safeArray(b.createdLopHpIds).join(', ');
      const idList = safeArray(b.createdLopHpIds).map((x) => Number(x)).filter((n) => Number.isFinite(n));
      setPlanGuide({
        replay,
        planned,
        ids: idList,
        hocKyId: String(hocKyId),
        maHocPhan: row.maHocPhan || ''
      });
    } catch (e) {
      setPlanError(e.message || 'Lỗi plan-sections.');
    } finally {
      setPlanningKey(null);
    }
  };

  const kpiTitle = data?.tenHocKy || '—';

  return (
    <div className="mx-auto max-w-7xl text-on-surface">
      <div className="mb-10 flex flex-col justify-between gap-4 sm:flex-row sm:items-end">
        <div>
          <h2 className="mb-2 text-2xl font-bold tracking-tight text-on-surface md:text-3xl">Nhu cầu đăng ký dự kiến</h2>
          <p className="text-sm text-on-surface-variant">
            Tổng hợp intent PRE theo học phần trong phạm vi khóa/ngành — hỗ trợ dự báo mở lớp. Có thể{' '}
            <strong>tạo lớp shell</strong> (SHELL) theo cột &quot;Lớp gợi ý&quot;, rồi{' '}
            <Link
              to={hocKyId ? `/admin/class-publish?hocKyId=${encodeURIComponent(hocKyId)}` : '/admin/class-publish'}
              className="font-semibold text-primary underline-offset-2 hover:underline"
            >
              xuất bản lớp
            </Link>
            .
          </p>
        </div>
        <button
          type="button"
          onClick={() => fetchDemand()}
          disabled={loading || !hocKyId}
          className="inline-flex items-center gap-2 rounded-full border border-outline-variant/30 bg-surface-container-low px-5 py-2.5 text-sm font-semibold text-primary hover:bg-surface-container-high disabled:opacity-50"
        >
          <span className="material-symbols-outlined text-[18px]">refresh</span>
          Làm mới
        </button>
      </div>

      <div className="mb-8 rounded-xl bg-surface-container-low p-4 shadow-[0_20px_40px_rgba(20,27,43,0.05)]">
        <div className="grid gap-4 lg:grid-cols-12 lg:items-end">
          <div className="lg:col-span-3">
            <label className="mb-1 block text-xs font-semibold uppercase tracking-wide text-on-surface-variant">
              Học kỳ <span className="text-error">*</span>
            </label>
            <div className="relative">
              <select
                className="w-full cursor-pointer appearance-none rounded-lg border-none bg-surface-container-lowest py-3 pl-4 pr-10 text-sm text-on-surface outline-none focus:ring-2 focus:ring-primary/20"
                value={hocKyId}
                onChange={(e) => setHocKyId(e.target.value)}
              >
                {hocKys.length === 0 && <option value="">—</option>}
                {hocKys.map((hk) => {
                  const id = hk.idHocKy ?? hk.id;
                  return (
                    <option key={id} value={String(id)}>
                      {hk.tenHocKy || hk.ten}
                    </option>
                  );
                })}
              </select>
              <span className="pointer-events-none absolute right-3 top-1/2 -translate-y-1/2 text-on-surface-variant material-symbols-outlined">
                expand_more
              </span>
            </div>
          </div>
          <div className="lg:col-span-2">
            <label className="mb-1 block text-xs font-semibold text-on-surface-variant">Năm nhập học</label>
            <input
              type="number"
              placeholder="Tất cả khóa"
              className="w-full rounded-lg border-none bg-surface-container-lowest py-3 px-4 text-sm text-on-surface outline-none focus:ring-2 focus:ring-primary/20"
              value={namNhapHoc}
              onChange={(e) => setNamNhapHoc(e.target.value)}
            />
          </div>
          <div className="lg:col-span-3">
            <label className="mb-1 block text-xs font-semibold text-on-surface-variant">Ngành</label>
            <select
              className="w-full rounded-lg border-none bg-surface-container-lowest py-3 px-4 text-sm text-on-surface outline-none focus:ring-2 focus:ring-primary/20"
              value={idNganh}
              onChange={(e) => setIdNganh(e.target.value)}
            >
              <option value="">Tất cả ngành</option>
              {nganhList.map((n) => (
                <option key={n.idNganh} value={String(n.idNganh)}>
                  {n.tenNganh}
                </option>
              ))}
            </select>
          </div>
          <div className="lg:col-span-2">
            <label className="mb-1 block text-xs font-semibold text-on-surface-variant" title="Dùng để tính số lớp gợi ý">
              Quy mô lớp mục tiêu
            </label>
            <input
              type="number"
              min={1}
              placeholder="Mặc định BE"
              className="w-full rounded-lg border-none bg-surface-container-lowest py-3 px-4 text-sm text-on-surface outline-none focus:ring-2 focus:ring-primary/20"
              value={targetClassSize}
              onChange={(e) => setTargetClassSize(e.target.value)}
            />
          </div>
          <div className="lg:col-span-2 flex items-end">
            <p className="flex items-start gap-2 rounded-lg bg-secondary-container/15 p-3 text-xs text-secondary">
              <span className="material-symbols-outlined shrink-0 text-[16px]">info</span>
              Bộ lọc gửi lên API; để trống = theo mặc định backend.
            </p>
          </div>
        </div>
      </div>

      {error && (
        <div className="mb-4 rounded-xl border border-error/30 bg-error-container/40 p-4 text-sm text-error">{error}</div>
      )}
      {planError && (
        <div className="mb-4 rounded-xl border border-error/30 bg-error-container/40 p-4 text-sm text-error">{planError}</div>
      )}
      {planGuide && (
        <div className="mb-4 rounded-xl border border-primary/30 bg-primary-fixed-dim/25 p-4 text-sm text-on-surface">
          <p className="font-semibold text-primary">
            {planGuide.replay
              ? `Không tạo thêm lớp — đã có ${planGuide.planned} section shell (bấm lại / cùng thao tác idempotent).`
              : `Đã tạo ${planGuide.planned} section shell mới.`}{' '}
            {planGuide.maHocPhan ? `(Học phần: ${planGuide.maHocPhan})` : ''}
          </p>
          {planGuide.ids.length > 0 && (
            <p className="mt-2 text-on-surface-variant">
              Mã nội bộ trong CSDL (id_lop_hp): <span className="font-mono font-semibold text-on-surface">{planGuide.ids.join(', ')}</span>
              {' — '}
              dùng để đối chiếu trong danh sách lớp, không phải sĩ số.
            </p>
          )}
          <p className="mt-3 text-xs font-bold uppercase tracking-wide text-on-surface-variant">Việc làm tiếp theo</p>
          <ol className="mt-2 list-decimal space-y-2 pl-5 text-on-surface">
            <li>
              Mở{' '}
              <Link
                to={`/admin/class-publish?hocKyId=${encodeURIComponent(planGuide.hocKyId)}`}
                className="font-semibold text-primary underline-offset-2 hover:underline"
              >
                Xuất bản lớp
              </Link>
              , chọn đúng học kỳ (đã gợi ý sẵn trên link).
            </li>
            <li>
              Trong danh sách lớp, tìm các dòng trạng thái <strong>SHELL</strong> / chưa publish — đúng học phần vừa lên kế hoạch
              {planGuide.ids.length === 1 ? ` (có thể trùng id ${planGuide.ids[0]})` : ''}.
            </li>
            <li>
              Gán <strong>giảng viên</strong>, nhập / chỉnh <strong>lịch học</strong> (TKB) cho từng lớp cho đến khi đủ điều kiện publish.
            </li>
            <li>
              Bấm <strong>Publish</strong> từng lớp (hoặc bulk) khi đã có GV + lịch — lúc đó sinh viên mới thấy lớp trong luồng đăng ký (theo F03).
            </li>
            <li>
              Khi sẵn sàng mở đăng ký chính thức: cấu hình cửa sổ <strong>OFFICIAL</strong> trong mục cửa đăng ký (F02).
            </li>
          </ol>
          <p className="mt-3 border-t border-outline-variant/20 pt-3 text-xs text-on-surface-variant">
            Cần tạo <strong>thêm</strong> section cho đúng dòng này? Đổi &quot;Quy mô lớp mục tiêu&quot; hoặc bộ lọc khóa/ngành (khóa idempotent đổi theo), hoặc xóa shell
            trùng trong hệ thống rồi bấm Tạo shell lại.
          </p>
        </div>
      )}

      {data && (
        <div className="mb-6 grid gap-4 md:grid-cols-3">
          <div className="rounded-xl bg-surface-container-lowest p-5 shadow-[0_20px_40px_rgba(20,27,43,0.05)]">
            <p className="text-xs font-bold uppercase tracking-wide text-on-surface-variant">Học kỳ (ngữ cảnh)</p>
            <p className="mt-2 text-lg font-bold text-primary">{kpiTitle}</p>
            <p className="mt-1 text-xs text-on-surface-variant">
              {data.namNhapHoc != null ? `Khóa: ${data.namNhapHoc}` : 'Mọi khóa'}{' '}
              · {data.tenNganh || 'Mọi ngành'}
            </p>
          </div>
          <div className="rounded-xl bg-surface-container-lowest p-5 shadow-[0_20px_40px_rgba(20,27,43,0.05)]">
            <p className="text-xs font-bold uppercase tracking-wide text-on-surface-variant">Tổng intent</p>
            <p className="mt-2 text-3xl font-black text-on-surface">{data.totalIntents ?? 0}</p>
            <p className="mt-1 text-xs text-on-surface-variant">Sinh viên đăng ký dự kiến (gộp)</p>
          </div>
          <div className="rounded-xl bg-surface-container-lowest p-5 shadow-[0_20px_40px_rgba(20,27,43,0.05)]">
            <p className="text-xs font-bold uppercase tracking-wide text-on-surface-variant">Lớp gợi ý / quy mô</p>
            <p className="mt-2 text-3xl font-black text-primary">{data.totalRecommendedClasses ?? 0}</p>
            <p className="mt-1 text-xs text-on-surface-variant">
              Target: <span className="font-semibold">{data.targetClassSize ?? '—'}</span> chỗ/lớp
            </p>
          </div>
        </div>
      )}

      <div className="overflow-hidden rounded-xl bg-surface-container-lowest shadow-[0_20px_40px_rgba(20,27,43,0.05)]">
        {loading && (
          <div className="p-8 text-sm text-on-surface-variant">Đang tải bảng nhu cầu…</div>
        )}
        {!loading && data && (
          <div className="overflow-x-auto">
            <table className="w-full min-w-[900px] border-collapse text-left text-sm">
              <thead>
                <tr className="border-b border-outline-variant/20 text-xs font-semibold uppercase tracking-wider text-on-surface-variant">
                  <th className="px-4 py-4">Mã HP</th>
                  <th className="px-4 py-4">Tên học phần</th>
                  <th className="px-4 py-4 text-center">TC</th>
                  <th className="px-4 py-4">Phạm vi (trên dòng)</th>
                  <th className="px-4 py-4 text-right">
                    <button type="button" className="inline-flex items-center gap-1 hover:text-primary" onClick={() => toggleSort('totalIntent')}>
                      Tổng intent
                      <span className="material-symbols-outlined text-[16px]">
                        {sortKey === 'totalIntent' ? (sortDir === 'desc' ? 'arrow_downward' : 'arrow_upward') : 'swap_vert'}
                      </span>
                    </button>
                  </th>
                  <th className="px-4 py-4 text-right">
                    <button type="button" className="inline-flex items-center gap-1 hover:text-primary" onClick={() => toggleSort('recommendedClasses')}>
                      Lớp gợi ý
                      <span className="material-symbols-outlined text-[16px]">
                        {sortKey === 'recommendedClasses' ? (sortDir === 'desc' ? 'arrow_downward' : 'arrow_upward') : 'swap_vert'}
                      </span>
                    </button>
                  </th>
                  <th className="px-4 py-4 text-center">Nhiệt độ</th>
                  <th className="px-4 py-4 text-center">Shell</th>
                </tr>
              </thead>
              <tbody>
                {sortedItems.length === 0 && (
                  <tr>
                    <td colSpan={8} className="px-4 py-10 text-center text-on-surface-variant">
                      Không có dòng nào. Kiểm tra phạm vi lọc hoặc dữ liệu PRE.
                    </td>
                  </tr>
                )}
                {sortedItems.map((row, idx) => {
                  const ratio = intentHeatRatio(Number(row.totalIntent) || 0, maxIntent);
                  const rowBusyKey = `${row.idHocPhan}-${row.namNhapHoc ?? 'x'}-${row.idNganh ?? 'x'}`;
                  const planning = planningKey === rowBusyKey;
                  return (
                    <tr
                      key={`${row.maHocPhan}-${row.idHocPhan}-${idx}`}
                      className={idx % 2 === 1 ? 'bg-surface-container-low/40' : ''}
                    >
                      <td className="px-4 py-3 font-mono text-xs font-semibold text-primary">{row.maHocPhan}</td>
                      <td className="px-4 py-3 font-medium">{row.tenHocPhan}</td>
                      <td className="px-4 py-3 text-center">{row.soTinChi ?? '—'}</td>
                      <td className="px-4 py-3 text-xs text-on-surface-variant">
                        {row.namNhapHoc != null ? `K${row.namNhapHoc}` : '—'} · {row.tenNganh || '—'}
                      </td>
                      <td className={`px-4 py-3 text-right font-semibold ${heatCellClass(ratio)}`}>
                        {row.totalIntent}
                      </td>
                      <td className="px-4 py-3 text-right font-bold text-primary">{row.recommendedClasses}</td>
                      <td className="px-4 py-3">
                        <div className="mx-auto h-2 w-full max-w-[120px] overflow-hidden rounded-full bg-surface-container-high">
                          <div
                            className="h-2 rounded-full bg-gradient-to-r from-primary-fixed to-primary"
                            style={{ width: `${Math.round(ratio * 100)}%` }}
                          />
                        </div>
                      </td>
                      <td className="px-4 py-3 text-center">
                        <button
                          type="button"
                          title="POST plan-sections theo lớp gợi ý (SHELL)"
                          disabled={loading || planning || (Number(row.totalIntent) || 0) <= 0}
                          onClick={() => planShellsForRow(row)}
                          className="inline-flex items-center gap-1 rounded-full border border-primary/30 bg-primary/10 px-3 py-1.5 text-xs font-semibold text-primary hover:bg-primary/20 disabled:cursor-not-allowed disabled:opacity-40"
                        >
                          <span className="material-symbols-outlined text-[16px]">
                            {planning ? 'progress_activity' : 'add_circle'}
                          </span>
                          {planning ? '…' : 'Tạo shell'}
                        </button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default AdminPreRegistrationDemandPage;
