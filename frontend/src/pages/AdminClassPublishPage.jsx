import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { useSearchParams } from 'react-router-dom';

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

function errMsg(body, fallback) {
  if (body?.message) return String(body.message);
  if (Array.isArray(body?.errors) && body.errors[0]?.defaultMessage) return String(body.errors[0].defaultMessage);
  return fallback;
}

const safeArray = (x) => (Array.isArray(x) ? x : []);

function hasSchedule(row) {
  return Array.isArray(row?.thoiKhoaBieuJson) && row.thoiKhoaBieuJson.length > 0;
}

function inferStatus(row, statusOverrides) {
  const s = statusOverrides[String(row.idLopHp)];
  if (s) return s;
  if (row.statusPublish) return String(row.statusPublish);
  if (row.idGiangVien && hasSchedule(row)) return 'SCHEDULED';
  return 'SHELL';
}

const AdminClassPublishPage = () => {
  const [searchParams] = useSearchParams();
  const appliedHocKyFromUrl = useRef(false);
  const [hocKys, setHocKys] = useState([]);
  const [hocKyId, setHocKyId] = useState('');
  const [rows, setRows] = useState([]);
  const [statusOverrides, setStatusOverrides] = useState({});
  const [versionMap, setVersionMap] = useState({});
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const [selectedIds, setSelectedIds] = useState([]);
  const [activeId, setActiveId] = useState(null);

  const [giangViens, setGiangViens] = useState([]);
  const [gvQuery, setGvQuery] = useState('');
  const [gvSelectId, setGvSelectId] = useState('');
  const [savingGv, setSavingGv] = useState(false);
  const [publishingOne, setPublishingOne] = useState(false);

  // Schedule form state
  const [tkbThu, setTkbThu] = useState('2');
  const [tkbTiet, setTkbTiet] = useState('1-3');
  const [tkbPhong, setTkbPhong] = useState('A101');
  const [savingTkb, setSavingTkb] = useState(false);

  const [bulkBusy, setBulkBusy] = useState(false);
  const [bulkResult, setBulkResult] = useState(null);
  const [toast, setToast] = useState(null);

  const showToast = (kind, text) => {
    setToast({ kind, text });
    window.setTimeout(() => setToast(null), 3500);
  };

  const loadMeta = useCallback(async () => {
    const [hkRes, gvRes] = await Promise.all([
      fetch(`${API_BASE_URL}/api/hoc-ky`, { headers: authHeaders() }),
      fetch(`${API_BASE_URL}/api/giang-vien`, { headers: authHeaders() })
    ]);

    const hkBody = await parseBody(hkRes);
    if (!hkRes.ok) throw new Error(errMsg(hkBody, 'Không tải được học kỳ.'));
    const hkList = safeArray(hkBody);
    setHocKys(hkList);
    setHocKyId((cur) => {
      if (cur) return cur;
      const current = hkList.find((x) => x.trangThaiHienHanh === true) || hkList[0];
      return current ? String(current.idHocKy ?? current.id) : '';
    });

    const gvBody = await parseBody(gvRes);
    if (gvRes.ok) setGiangViens(safeArray(gvBody));
  }, []);

  const loadRows = useCallback(async () => {
    if (!hocKyId) {
      setRows([]);
      return;
    }
    setLoading(true);
    setError('');
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/admin/lop-hoc-phan/hoc-ky/${hocKyId}`, { headers: authHeaders() });
      const body = await parseBody(res);
      if (!res.ok) throw new Error(errMsg(body, 'Không tải được danh sách lớp học phần.'));
      const list = safeArray(body);
      setRows(list);
      if (list.length > 0) {
        setActiveId((cur) => (cur && list.some((x) => String(x.idLopHp) === String(cur)) ? cur : list[0].idLopHp));
      } else {
        setActiveId(null);
      }
      setSelectedIds([]);
    } catch (e) {
      setError(e.message || 'Lỗi tải dữ liệu.');
      setRows([]);
    } finally {
      setLoading(false);
    }
  }, [hocKyId]);

  useEffect(() => {
    loadMeta().catch((e) => setError(e.message || 'Lỗi tải dữ liệu nền.'));
  }, [loadMeta]);

  /** Deep-link từ Nhu cầu PRE: /admin/class-publish?hocKyId=... */
  useEffect(() => {
    if (appliedHocKyFromUrl.current || hocKys.length === 0) return;
    const wanted = searchParams.get('hocKyId');
    if (!wanted) return;
    const ok = hocKys.some((x) => String(x.idHocKy ?? x.id) === String(wanted));
    if (ok) {
      setHocKyId(String(wanted));
      appliedHocKyFromUrl.current = true;
    }
  }, [hocKys, searchParams]);

  useEffect(() => {
    loadRows();
  }, [loadRows]);

  const activeRow = useMemo(
    () => rows.find((x) => String(x.idLopHp) === String(activeId)) || null,
    [rows, activeId]
  );

  const filteredGiangViens = useMemo(() => {
    const q = gvQuery.trim().toLowerCase();
    if (!q) return giangViens.slice(0, 12);
    return giangViens
      .filter((gv) => {
        const ten = String(gv.tenGiangVien || '').toLowerCase();
        const ma = String(gv.maGiangVien || '').toLowerCase();
        return ten.includes(q) || ma.includes(q);
      })
      .slice(0, 12);
  }, [giangViens, gvQuery]);

  const isChecked = (id) => selectedIds.includes(String(id));

  const toggleChecked = (id) => {
    const key = String(id);
    setSelectedIds((prev) => (prev.includes(key) ? prev.filter((x) => x !== key) : [...prev, key]));
  };

  const onAssignGiangVien = async () => {
    if (!activeRow || !gvSelectId) return;
    setSavingGv(true);
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/admin/lop-hoc-phan/${activeRow.idLopHp}/assign-giang-vien`, {
        method: 'POST',
        headers: authHeaders(),
        body: JSON.stringify({ idGiangVien: Number(gvSelectId) })
      });
      const body = await parseBody(res);
      if (!res.ok) throw new Error(errMsg(body, 'Gán giảng viên thất bại.'));
      const gv = giangViens.find((x) => Number(x.idGiangVien) === Number(gvSelectId));
      setRows((prev) => prev.map((r) => (r.idLopHp === activeRow.idLopHp ? { ...r, idGiangVien: gv?.idGiangVien, tenGiangVien: gv?.tenGiangVien } : r)));
      setStatusOverrides((prev) => ({ ...prev, [String(activeRow.idLopHp)]: String(body.statusPublish || inferStatus(activeRow, prev)) }));
      setVersionMap((prev) => ({ ...prev, [String(activeRow.idLopHp)]: body.version }));
      showToast('ok', body.message || 'Đã gán giảng viên.');
    } catch (e) {
      showToast('err', e.message || 'Không thể gán giảng viên.');
    } finally {
      setSavingGv(false);
    }
  };

  const onAssignSchedule = async () => {
    if (!activeRow) return;
    setSavingTkb(true);
    try {
      const schedule = [{ thu: Number(tkbThu), tiet: tkbTiet, phong: tkbPhong }];
      const res = await fetch(`${API_BASE_URL}/api/v1/admin/lop-hoc-phan/${activeRow.idLopHp}/assign-schedule`, {
        method: 'POST',
        headers: authHeaders(),
        body: JSON.stringify(schedule)
      });
      const body = await parseBody(res);
      if (!res.ok) throw new Error(errMsg(body, 'Gán lịch thất bại.'));
      setRows((prev) => prev.map((r) => (r.idLopHp === activeRow.idLopHp ? { ...r, thoiKhoaBieuJson: schedule } : r)));
      setStatusOverrides((prev) => ({ ...prev, [String(activeRow.idLopHp)]: String(body.statusPublish || inferStatus(activeRow, prev)) }));
      setVersionMap((prev) => ({ ...prev, [String(activeRow.idLopHp)]: body.version }));
      showToast('ok', body.message || 'Đã gán lịch học.');
    } catch (e) {
      showToast('err', e.message || 'Không thể gán lịch.');
    } finally {
      setSavingTkb(false);
    }
  };

  const onPublishOne = async () => {
    if (!activeRow) return;
    setPublishingOne(true);
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/admin/lop-hoc-phan/${activeRow.idLopHp}/publish`, {
        method: 'POST',
        headers: authHeaders()
      });
      const body = await parseBody(res);
      if (!res.ok) throw new Error(errMsg(body, 'Publish lớp thất bại.'));
      setStatusOverrides((prev) => ({ ...prev, [String(activeRow.idLopHp)]: String(body.statusPublish || 'PUBLISHED') }));
      setVersionMap((prev) => ({ ...prev, [String(activeRow.idLopHp)]: body.version }));
      showToast('ok', body.message || 'Đã publish lớp.');
    } catch (e) {
      showToast('err', e.message || 'Không thể publish lớp.');
    } finally {
      setPublishingOne(false);
    }
  };

  const onBulkPublish = async () => {
    if (!hocKyId) return;
    setBulkBusy(true);
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/admin/lop-hoc-phan/bulk-publish?hocKyId=${hocKyId}`, {
        method: 'POST',
        headers: authHeaders()
      });
      const body = await parseBody(res);
      if (!res.ok) throw new Error(errMsg(body, 'Bulk publish thất bại.'));
      const ids = safeArray(body.publishedIds).map((x) => String(x));
      setStatusOverrides((prev) => {
        const next = { ...prev };
        ids.forEach((id) => {
          next[id] = 'PUBLISHED';
        });
        return next;
      });
      setBulkResult(body);
      showToast('ok', `Bulk publish: ${body.publishedCount || 0}/${body.totalRequested || 0} lớp.`);
    } catch (e) {
      showToast('err', e.message || 'Không thể publish hàng loạt.');
    } finally {
      setBulkBusy(false);
    }
  };

  const canPublish = useMemo(() => {
    if (!activeRow) return false;
    const st = inferStatus(activeRow, statusOverrides);
    return st === 'SCHEDULED' && !!activeRow.idGiangVien && hasSchedule(activeRow);
  }, [activeRow, statusOverrides]);

  const statusChip = (status) => {
    if (status === 'PUBLISHED') return 'bg-tertiary-fixed text-on-tertiary-fixed';
    if (status === 'SCHEDULED') return 'bg-primary-fixed text-on-primary-fixed';
    if (status === 'SHELL') return 'bg-orange-100 text-orange-900 dark:bg-orange-900/30 dark:text-orange-100';
    return 'bg-surface-variant text-on-surface-variant';
  };

  const exportSkippedCsv = () => {
    const skipped = safeArray(bulkResult?.skipped);
    if (!skipped.length) return;
    const lines = ['idLopHp,maLopHp,reason', ...skipped.map((x) => `${x.idLopHp || ''},"${x.maLopHp || ''}","${String(x.reason || '').replaceAll('"', '""')}"`)];
    const blob = new Blob([lines.join('\n')], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `class-publish-skipped-hocKy-${hocKyId}.csv`;
    a.click();
    URL.revokeObjectURL(url);
  };

  return (
    <div className="flex h-[calc(100vh-9rem)] gap-6">
      <section className="flex min-w-0 flex-1 flex-col gap-6">
        <div className="flex items-end justify-between rounded-xl bg-surface-container-lowest p-6 shadow-[0_20px_40px_rgba(20,27,43,0.05)]">
          <div>
            <h2 className="mb-2 text-3xl font-bold tracking-tight">Xuất bản lớp học phần</h2>
            <p className="text-sm text-on-surface-variant">Quản lý và công bố thông tin lớp học cho sinh viên đăng ký.</p>
          </div>
          <div className="flex gap-3">
            <button type="button" onClick={loadRows} className="rounded-full bg-surface-container-high px-5 py-2.5 text-sm font-semibold text-primary hover:bg-surface-container-highest">
              Làm mới
            </button>
            <button type="button" onClick={onBulkPublish} disabled={bulkBusy || !hocKyId} className="rounded-full bg-gradient-to-r from-primary to-primary-container px-6 py-2.5 text-sm font-semibold text-on-primary disabled:opacity-50">
              Publish hàng loạt theo học kỳ
            </button>
          </div>
        </div>

        <div className="flex items-center gap-3 text-sm">
          <span className="text-on-surface-variant">Học kỳ</span>
          <select value={hocKyId} onChange={(e) => setHocKyId(e.target.value)} className="rounded-lg bg-surface-container-low px-3 py-2 outline-none focus:ring-2 focus:ring-primary/20">
            {hocKys.map((hk) => {
              const id = hk.idHocKy ?? hk.id;
              return (
                <option key={id} value={String(id)}>
                  {hk.tenHocKy || hk.ten}
                </option>
              );
            })}
          </select>
          {error && <span className="rounded bg-error-container/50 px-3 py-1 text-xs text-error">{error}</span>}
        </div>

        <div className="flex min-h-0 flex-1 flex-col overflow-hidden rounded-xl bg-surface-container-lowest p-1 shadow-[0_20px_40px_rgba(20,27,43,0.05)]">
          <div className="overflow-auto">
            <table className="w-full min-w-[900px] border-collapse text-left text-sm">
              <thead>
                <tr className="bg-surface text-xs uppercase tracking-wider text-on-surface-variant">
                  <th className="w-12 p-4 text-center" />
                  <th className="p-4">Mã lớp</th>
                  <th className="p-4">Môn học</th>
                  <th className="p-4">Giảng viên</th>
                  <th className="p-4 text-center">Lịch</th>
                  <th className="p-4">Trạng thái</th>
                  <th className="p-4">Phiên bản</th>
                </tr>
              </thead>
              <tbody>
                {loading && (
                  <tr>
                    <td className="p-6 text-on-surface-variant" colSpan={7}>Đang tải dữ liệu…</td>
                  </tr>
                )}
                {!loading && rows.map((row) => {
                  const selected = String(activeId) === String(row.idLopHp);
                  const st = inferStatus(row, statusOverrides);
                  return (
                    <tr key={row.idLopHp} className={`cursor-pointer border-t border-surface-container-low hover:bg-surface-container-low ${selected ? 'bg-surface-container-low/60' : ''}`} onClick={() => setActiveId(row.idLopHp)}>
                      <td className="p-4 text-center">
                        <input type="checkbox" checked={isChecked(row.idLopHp)} onChange={() => toggleChecked(row.idLopHp)} onClick={(e) => e.stopPropagation()} />
                      </td>
                      <td className="p-4 font-semibold text-primary">{row.maLopHp}</td>
                      <td className="p-4">{row.tenHocPhan || row.maHocPhan}</td>
                      <td className="p-4 text-on-surface-variant">{row.tenGiangVien || 'Chưa gán'}</td>
                      <td className="p-4 text-center">
                        <span className={`material-symbols-outlined ${hasSchedule(row) ? 'text-primary' : 'text-error'}`}>
                          {hasSchedule(row) ? 'check_circle' : 'cancel'}
                        </span>
                      </td>
                      <td className="p-4"><span className={`inline-flex rounded-full px-2.5 py-1 text-xs font-semibold ${statusChip(st)}`}>{st}</span></td>
                      <td className="p-4 text-xs text-on-surface-variant">v{versionMap[String(row.idLopHp)] ?? row.version ?? 0}</td>
                    </tr>
                  );
                })}
                {!loading && rows.length === 0 && (
                  <tr>
                    <td className="p-8 text-center text-on-surface-variant" colSpan={7}>Không có lớp học phần trong học kỳ này.</td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
          <div className="mt-auto flex items-center justify-between border-t border-surface-container p-4 text-sm text-on-surface-variant">
            <span>Đã chọn {selectedIds.length} lớp</span>
            <span>Tổng {rows.length} lớp</span>
          </div>
        </div>
      </section>

      <aside className="w-96 shrink-0 overflow-auto rounded-xl bg-surface-container-lowest p-6 shadow-[0_20px_40px_rgba(20,27,43,0.05)]">
        {!activeRow && <div className="text-sm text-on-surface-variant">Chọn một lớp để xem chi tiết.</div>}
        {activeRow && (
          <div className="flex h-full flex-col gap-6">
            <div>
              <p className="text-xs uppercase tracking-widest text-primary">Chi tiết lớp học phần</p>
              <h3 className="text-2xl font-bold">{activeRow.maLopHp}</h3>
              <p className="text-sm text-on-surface-variant">{activeRow.tenHocPhan || activeRow.maHocPhan}</p>
            </div>

            {!hasSchedule(activeRow) && (
              <div className="rounded-xl border-l-4 border-error bg-error-container p-4 text-on-error-container">
                <p className="text-sm font-bold">Thiếu lịch biểu</p>
                <p className="mt-1 text-xs">
                  Lớp đang ở dạng shell: chưa có dữ liệu lịch học (TKB). Bạn không thể publish cho đến khi có lịch. Hãy cập nhật lịch
                  trong module thời khóa biểu / xếp slot, hoặc sửa JSON lịch trên lớp nếu quy trình của bạn cho phép.
                </p>
              </div>
            )}

            <div className="rounded-xl bg-surface-container-low p-4">
              <p className="mb-2 text-xs font-semibold text-on-surface-variant">Phân công giảng viên</p>
              <input
                type="text"
                value={gvQuery}
                onChange={(e) => setGvQuery(e.target.value)}
                placeholder="Nhập tên hoặc mã GV…"
                className="mb-2 w-full rounded-lg bg-surface-container-lowest px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-primary/20"
              />
              <select value={gvSelectId} onChange={(e) => setGvSelectId(e.target.value)} className="mb-3 w-full rounded-lg bg-surface-container-lowest px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-primary/20">
                <option value="">Chọn giảng viên</option>
                {filteredGiangViens.map((gv) => (
                  <option key={gv.idGiangVien} value={String(gv.idGiangVien)}>
                    {gv.tenGiangVien} {gv.maGiangVien ? `(${gv.maGiangVien})` : ''}
                  </option>
                ))}
              </select>
              <button type="button" onClick={onAssignGiangVien} disabled={!gvSelectId || savingGv} className="rounded-lg bg-primary-container px-4 py-2 text-sm font-semibold text-on-primary disabled:opacity-50">
                {savingGv ? 'Đang lưu…' : 'Lưu giảng viên'}
              </button>
            </div>

            <div className="rounded-xl bg-surface-container-low p-4">
              <p className="mb-2 text-xs font-semibold text-on-surface-variant">Gán lịch học (TKB)</p>
              {hasSchedule(activeRow) ? (
                <div className="mb-2 rounded-lg bg-primary-fixed/20 p-3 text-xs">
                  <p className="font-semibold text-primary">Đã có lịch:</p>
                  {activeRow.thoiKhoaBieuJson.map((slot, idx) => (
                    <p key={idx} className="mt-1">Thứ {slot.thu} | Tiết {slot.tiet} | Phòng {slot.phong || '—'}</p>
                  ))}
                </div>
              ) : (
                <p className="mb-2 text-xs text-error">Chưa có lịch — nhập bên dưới:</p>
              )}
              <div className="mb-2 grid grid-cols-3 gap-2">
                <div>
                  <label className="text-xs text-on-surface-variant">Thứ</label>
                  <select value={tkbThu} onChange={(e) => setTkbThu(e.target.value)} className="w-full rounded-lg bg-surface-container-lowest px-2 py-1.5 text-sm outline-none focus:ring-2 focus:ring-primary/20">
                    <option value="2">Thứ 2</option>
                    <option value="3">Thứ 3</option>
                    <option value="4">Thứ 4</option>
                    <option value="5">Thứ 5</option>
                    <option value="6">Thứ 6</option>
                    <option value="7">Thứ 7</option>
                    <option value="8">CN</option>
                  </select>
                </div>
                <div>
                  <label className="text-xs text-on-surface-variant">Tiết</label>
                  <select value={tkbTiet} onChange={(e) => setTkbTiet(e.target.value)} className="w-full rounded-lg bg-surface-container-lowest px-2 py-1.5 text-sm outline-none focus:ring-2 focus:ring-primary/20">
                    <option value="1-3">Tiết 1-3</option>
                    <option value="4-6">Tiết 4-6</option>
                    <option value="7-9">Tiết 7-9</option>
                    <option value="10-12">Tiết 10-12</option>
                    <option value="1-5">Tiết 1-5</option>
                    <option value="6-10">Tiết 6-10</option>
                  </select>
                </div>
                <div>
                  <label className="text-xs text-on-surface-variant">Phòng</label>
                  <input type="text" value={tkbPhong} onChange={(e) => setTkbPhong(e.target.value)} placeholder="A101" className="w-full rounded-lg bg-surface-container-lowest px-2 py-1.5 text-sm outline-none focus:ring-2 focus:ring-primary/20" />
                </div>
              </div>
              <button type="button" onClick={onAssignSchedule} disabled={savingTkb} className="rounded-lg bg-primary-container px-4 py-2 text-sm font-semibold text-on-primary disabled:opacity-50">
                {savingTkb ? 'Đang lưu…' : 'Lưu lịch học'}
              </button>
            </div>

            <div className="space-y-2 text-sm">
              <div className="flex items-center justify-between border-b border-dashed border-surface-container-low py-2"><span className="text-on-surface-variant">Sĩ số tối đa</span><span className="font-semibold">{activeRow.siSoToiDa || '—'} sinh viên</span></div>
              <div className="flex items-center justify-between border-b border-dashed border-surface-container-low py-2"><span className="text-on-surface-variant">Phòng học</span><span className="font-semibold">{activeRow.maPhongHoc || 'Chưa cấp'}</span></div>
              <div className="flex items-center justify-between py-2"><span className="text-on-surface-variant">Trạng thái publish</span><span className="font-semibold text-primary">{inferStatus(activeRow, statusOverrides)}</span></div>
            </div>

            <div className="mt-auto">
              <button type="button" onClick={onPublishOne} disabled={!canPublish || publishingOne} className="w-full rounded-full bg-gradient-to-r from-primary to-primary-container px-6 py-3 text-sm font-bold text-on-primary disabled:cursor-not-allowed disabled:opacity-50">
                {publishingOne ? 'Đang publish…' : 'Xuất bản lớp'}
              </button>
              {!canPublish && (
                <p className="mt-2 text-center text-xs text-on-surface-variant">
                  Vui lòng gán giảng viên và có lịch học (TKB) trước khi xuất bản.
                </p>
              )}
            </div>
          </div>
        )}
      </aside>

      {bulkResult && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-inverse-surface/40 p-4">
          <div className="w-full max-w-2xl rounded-2xl bg-surface-container-lowest shadow-[0_20px_40px_rgba(20,27,43,0.1)]">
            <div className="flex items-center justify-between bg-surface-container p-6">
              <h3 className="text-xl font-bold">Kết quả xuất bản hàng loạt</h3>
              <button type="button" className="rounded-full bg-surface-container-high p-2" onClick={() => setBulkResult(null)}>
                <span className="material-symbols-outlined text-sm">close</span>
              </button>
            </div>
            <div className="space-y-5 p-6">
              <div className="grid grid-cols-3 gap-3 text-center">
                <div className="rounded-xl bg-surface-container-low p-4"><p className="text-xs text-on-surface-variant">Tổng yêu cầu</p><p className="text-3xl font-black">{bulkResult.totalRequested || 0}</p></div>
                <div className="rounded-xl border border-primary-fixed bg-primary-fixed/30 p-4"><p className="text-xs text-primary">Thành công</p><p className="text-3xl font-black text-primary">{bulkResult.publishedCount || 0}</p></div>
                <div className="rounded-xl border border-error-container bg-error-container/35 p-4"><p className="text-xs text-error">Bỏ qua</p><p className="text-3xl font-black text-error">{bulkResult.skippedCount || 0}</p></div>
              </div>

              <div className="overflow-hidden rounded-xl border border-surface-container-high">
                <table className="w-full text-left text-sm">
                  <thead className="bg-surface-container-low text-xs uppercase text-on-surface-variant"><tr><th className="px-4 py-2">Mã lớp</th><th className="px-4 py-2">Lý do</th></tr></thead>
                  <tbody>
                    {safeArray(bulkResult.skipped).length === 0 && (
                      <tr><td colSpan={2} className="px-4 py-4 text-on-surface-variant">Không có dòng bị bỏ qua.</td></tr>
                    )}
                    {safeArray(bulkResult.skipped).map((it) => (
                      <tr key={`${it.idLopHp}-${it.maLopHp}`} className="border-t border-surface-container-high">
                        <td className="px-4 py-2 font-medium">{it.maLopHp}</td>
                        <td className="px-4 py-2 text-error">{it.reason}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
            <div className="flex justify-end gap-3 border-t border-surface-container p-6">
              <button type="button" onClick={exportSkippedCsv} className="rounded-full border border-outline px-4 py-2 text-sm font-semibold">Xuất CSV các dòng lỗi</button>
              <button type="button" onClick={() => setBulkResult(null)} className="rounded-full bg-primary px-5 py-2 text-sm font-semibold text-on-primary">Đóng</button>
            </div>
          </div>
        </div>
      )}

      {toast && (
        <div className={`fixed bottom-6 right-6 rounded-lg px-4 py-3 text-sm font-semibold shadow-lg ${toast.kind === 'ok' ? 'bg-primary text-on-primary' : 'bg-error-container text-on-error-container'}`}>
          {toast.text}
        </div>
      )}
    </div>
  );
};

export default AdminClassPublishPage;

