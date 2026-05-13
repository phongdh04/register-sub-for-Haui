import React, { useCallback, useEffect, useMemo, useState } from 'react';

import { API_BASE_URL, authHeaders } from '../config/api';

const safeArray = (x) => (Array.isArray(x) ? x : []);

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
  if (Array.isArray(b?.errors) && b.errors[0]?.defaultMessage) return String(b.errors[0].defaultMessage);
  return d;
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

function computeProgress(openAt, closeAt) {
  const o = new Date(openAt).getTime();
  const c = new Date(closeAt).getTime();
  const n = Date.now();
  if (!Number.isFinite(o) || !Number.isFinite(c) || c <= o) {
    return { pct: 0, sub: '—' };
  }
  if (n < o) return { pct: 0, sub: 'Chưa bắt đầu' };
  if (n > c) return { pct: 100, sub: 'Đã kết thúc' };
  const pct = Math.round(((n - o) / (c - o)) * 100);
  const daysLeft = Math.max(0, Math.ceil((c - n) / 86400000));
  return { pct, sub: `Còn ${daysLeft} ngày` };
}

function statusMeta(row, nowTs = Date.now()) {
  const o = new Date(row.openAt).getTime();
  const c = new Date(row.closeAt).getTime();
  if (row.dangMo) {
    return { tone: 'open', label: 'Đang mở', dot: 'bg-primary' };
  }
  if (nowTs < o) {
    return { tone: 'soon', label: 'Sắp mở', dot: '' };
  }
  return { tone: 'end', label: 'Hết hạn', dot: '' };
}

/* ─── CampaignTab component ─── */
const CampaignTab = ({
  campaigns,
  loading,
  loadErr,
  onEdit,
  onDelete,
  campaignDeleteId,
  onConfirmDelete,
  campaignDeleteBusy,
  onCancelDelete
}) => {
  const windowsCount = (c) => safeArray(c.windows).length;

  return (
    <>
      {loadErr && (
        <div className="mb-4 rounded-xl border border-error/30 bg-error-container/40 p-4 text-sm text-error">{loadErr}</div>
      )}

      <div className="overflow-hidden rounded-xl bg-surface-container-lowest pb-4 shadow-[0_20px_40px_rgba(20,27,43,0.05)]">
        {loading ? (
          <div className="p-8 text-sm text-on-surface-variant">Đang tải…</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full border-collapse text-left">
              <thead>
                <tr className="border-0 text-sm font-semibold uppercase tracking-[0.05em] text-on-surface-variant">
                  <th className="px-6 py-5">Tên chiến dịch</th>
                  <th className="px-6 py-5">Khóa</th>
                  <th className="px-6 py-5">Pha</th>
                  <th className="px-6 py-5">Thời gian</th>
                  <th className="px-6 py-5">Trạng thái</th>
                  <th className="px-6 py-5">Học kỳ được tạo</th>
                  <th className="px-6 py-5 text-right">Thao tác</th>
                </tr>
              </thead>
              <tbody className="text-sm">
                {campaigns.length === 0 && (
                  <tr>
                    <td colSpan={7} className="px-6 py-10 text-center text-on-surface-variant">
                      Chưa có chiến dịch nào.
                    </td>
                  </tr>
                )}
                {campaigns.map((c, idx) => {
                  const pg = computeProgress(c.openAt, c.closeAt);
                  const zebra = idx % 2 === 1;
                  const ended = Date.now() > new Date(c.closeAt).getTime();
                  const nowTs = Date.now();
                  const o = new Date(c.openAt).getTime();
                  const isActive = c.dangMo || (nowTs >= o && nowTs <= new Date(c.closeAt).getTime());
                  return (
                    <tr
                      key={c.id}
                      className={`group transition-colors hover:bg-surface-container-low ${zebra ? 'bg-surface-container-low/50' : ''} ${ended ? 'opacity-70' : ''}`}
                    >
                      <td className="px-6 py-4">
                        <div className="font-semibold text-on-surface">{c.tenCampaign || '—'}</div>
                        <div className="mt-1 flex flex-wrap gap-1">
                          {safeArray(c.windows).map((w) => (
                            <span
                              key={w.id}
                              className="inline-flex items-center rounded-full bg-surface-container px-2 py-0.5 text-xs text-on-surface-variant"
                            >
                              <span className="mr-1 material-symbols-outlined text-[10px]">schedule</span>
                              {w.phase === 'OFFICIAL' ? 'OFF' : 'PRE'}
                              {w.namNhapHoc != null ? ` K${w.namNhapHoc}` : ''}
                              {w.tenNganh ? ` · ${w.tenNganh}` : ''}
                            </span>
                          ))}
                        </div>
                      </td>
                      <td className="px-6 py-4">
                        {c.namNhapHoc != null ? (
                          <span className="font-medium text-on-surface">K{c.namNhapHoc}</span>
                        ) : (
                          <span className="text-on-surface-variant">—</span>
                        )}
                      </td>
                      <td className="px-6 py-4">
                        <span className={`rounded-full px-3 py-1 text-xs font-semibold tracking-wide ${
                          c.phase === 'OFFICIAL'
                            ? 'bg-primary/10 text-primary'
                            : 'bg-secondary/10 text-secondary'
                        }`}>
                          {c.phase === 'OFFICIAL' ? 'OFFICIAL' : 'PRE-REG'}
                        </span>
                      </td>
                      <td className="px-6 py-4">
                        <div className="flex flex-col gap-1 text-on-surface-variant text-xs">
                          <span className="flex items-center gap-1">
                            <span className="material-symbols-outlined text-[14px]">calendar_today</span>
                            {formatInstantVi(c.openAt)}
                          </span>
                          <span className="flex items-center gap-1">
                            <span className="material-symbols-outlined text-[14px]">schedule</span>
                            {formatInstantVi(c.closeAt)}
                          </span>
                          <div className="mt-1 h-1.5 w-32 overflow-hidden rounded-full bg-surface-container-high">
                            <div
                              className={`h-full rounded-full ${ended ? 'bg-outline-variant' : 'bg-primary'}`}
                              style={{ width: `${pg.pct}%` }}
                            />
                          </div>
                          <span className="text-[11px] text-on-surface-variant">{pg.sub}</span>
                        </div>
                      </td>
                      <td className="px-6 py-4">
                        {isActive && !ended ? (
                          <span className="inline-flex w-max items-center gap-1 rounded-full bg-primary-fixed px-3 py-1 text-xs font-bold text-on-primary-fixed">
                            <span className="inline-block h-2 w-2 rounded-full bg-primary" />
                            Đang mở
                          </span>
                        ) : ended ? (
                          <span className="inline-flex w-max items-center gap-1 rounded-full bg-surface-container-high px-3 py-1 text-xs font-bold text-on-surface-variant">
                            Hết hạn
                          </span>
                        ) : (
                          <span className="inline-flex w-max items-center gap-1 rounded-full bg-surface-container-high px-3 py-1 text-xs font-bold text-primary">
                            Sắp mở
                          </span>
                        )}
                      </td>
                      <td className="px-6 py-4">
                        <span className="text-on-surface-variant">{c.tenHocKy || c.hocKyTen || '—'}</span>
                      </td>
                      <td className="px-6 py-4 text-right">
                        <div className="flex items-center justify-end gap-1 opacity-100 transition-opacity sm:opacity-0 sm:group-hover:opacity-100">
                          <button
                            type="button"
                            title="Sửa"
                            className="rounded-full p-2 text-on-surface-variant hover:bg-surface-container-high hover:text-primary"
                            onClick={() => onEdit(c)}
                          >
                            <span className="material-symbols-outlined text-[20px]">edit</span>
                          </button>
                          <button
                            type="button"
                            title="Xóa"
                            className="rounded-full p-2 text-on-surface-variant hover:bg-error-container hover:text-error"
                            onClick={() => onDelete(c.id)}
                          >
                            <span className="material-symbols-outlined text-[20px]">delete</span>
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Campaign delete confirmation */}
      {campaignDeleteId != null && (
        <div className="fixed inset-0 z-[60] flex items-end justify-center bg-inverse-surface/50 p-4 sm:items-center">
          <div className="w-full max-w-md rounded-xl bg-inverse-surface px-6 py-4 text-inverse-on-surface shadow-[0_20px_40px_rgba(20,27,43,0.12)]">
            <div className="flex gap-4">
              <span className="material-symbols-outlined text-error-container">warning</span>
              <div>
                <p className="font-semibold">Xóa chiến dịch?</p>
                <p className="mt-1 text-sm opacity-80">Tất cả các cửa sổ liên quan cũng sẽ bị xóa.</p>
              </div>
            </div>
            <div className="mt-4 flex justify-end gap-2">
              <button
                type="button"
                className="rounded-md px-4 py-2 text-sm font-semibold hover:bg-white/10"
                onClick={onCancelDelete}
              >
                Hủy
              </button>
              <button
                type="button"
                disabled={campaignDeleteBusy}
                className="rounded-md px-4 py-2 text-sm font-semibold text-error hover:bg-error-container/20"
                onClick={onConfirmDelete}
              >
                {campaignDeleteBusy ? 'Đang xóa…' : 'Xóa'}
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

/* ─── Main page component ─── */
const AdminRegistrationWindowsPage = () => {
  const [tab, setTab] = useState('windows'); // 'windows' | 'campaigns'
  const [hocKys, setHocKys] = useState([]);
  const [hocKyId, setHocKyId] = useState('');
  const [phaseFilter, setPhaseFilter] = useState('PRE');
  const [search, setSearch] = useState('');
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [loadErr, setLoadErr] = useState('');
  const [toast, setToast] = useState(null);

  const [nganhList, setNganhList] = useState([]);
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [saving, setSaving] = useState(false);
  const [formErr, setFormErr] = useState('');
  const [scopeErr, setScopeErr] = useState('');

  const [fIdHocKy, setFIdHocKy] = useState('');
  const [fPhase, setFPhase] = useState('PRE');
  const [fNam, setFNam] = useState('');
  const [fIdNganh, setFIdNganh] = useState('');
  const [fOpen, setFOpen] = useState('');
  const [fClose, setFClose] = useState('');
  const [fGhiChu, setFGhiChu] = useState('');

  const [deleteId, setDeleteId] = useState(null);
  const [deleteBusy, setDeleteBusy] = useState(false);

  const [quickOpen, setQuickOpen] = useState(false);
  const [quickPhase, setQuickPhase] = useState('OFFICIAL');
  const [quickNam, setQuickNam] = useState('');
  const [quickIdNganh, setQuickIdNganh] = useState('');
  const [quickDays, setQuickDays] = useState(30);
  const [quickBusy, setQuickBusy] = useState(false);
  const [forceBusy, setForceBusy] = useState(false);

  // Campaign state
  const [campaigns, setCampaigns] = useState([]);
  const [campaignLoading, setCampaignLoading] = useState(false);
  const [campaignLoadErr, setCampaignLoadErr] = useState('');
  const [campaignDrawerOpen, setCampaignDrawerOpen] = useState(false);
  const [campaignEditing, setCampaignEditing] = useState(null);
  const [campaignSaving, setCampaignSaving] = useState(false);
  const [campaignFormErr, setCampaignFormErr] = useState('');
  const [cFNam, setCFNam] = useState('');
  const [cFPhase, setCFPhase] = useState('OFFICIAL');
  const [cFOpen, setCFOpen] = useState('');
  const [cFClose, setCFClose] = useState('');
  const [cFGhiChu, setCFGhiChu] = useState('');
  const [cFTen, setCFTen] = useState('');
  const [campaignDeleteId, setCampaignDeleteId] = useState(null);
  const [campaignDeleteBusy, setCampaignDeleteBusy] = useState(false);

  const showToast = (k, t, d) => {
    setToast({ kind: k, title: t, detail: d });
    window.setTimeout(() => setToast(null), 4000);
  };

  const loadHocKy = useCallback(async () => {
    const res = await fetch(`${API_BASE_URL}/api/hoc-ky`, { headers: authHeaders() });
    const body = await parseBody(res);
    if (!res.ok) throw new Error(errMsg(body, 'Không tải học kỳ.'));
    const list = safeArray(body);
    setHocKys(list);
    setHocKyId((cur) => {
      if (cur) return cur;
      if (list.length === 0) return '';
      const hk = list.find((h) => h.trangThaiHienHanh === true) || list[0];
      return String(hk.idHocKy ?? hk.id);
    });
    setFIdHocKy((cur) => {
      if (cur) return cur;
      if (list.length === 0) return '';
      const hk = list.find((h) => h.trangThaiHienHanh === true) || list[0];
      return String(hk.idHocKy ?? hk.id);
    });
  }, []);

  const loadNganh = useCallback(async () => {
    const res = await fetch(`${API_BASE_URL}/api/nganh-dao-tao`, { headers: authHeaders() });
    const body = await parseBody(res);
    if (!res.ok) return;
    setNganhList(safeArray(body));
  }, []);

  const loadWindows = useCallback(async () => {
    if (!hocKyId) {
      setRows([]);
      setLoading(false);
      return;
    }
    setLoading(true);
    setLoadErr('');
    try {
      const q = new URLSearchParams({ hocKyId });
      if (phaseFilter === 'PRE' || phaseFilter === 'OFFICIAL') q.set('phase', phaseFilter);
      const res = await fetch(`${API_BASE_URL}/api/v1/admin/registration-windows?${q}`, {
        headers: authHeaders()
      });
      const body = await parseBody(res);
      if (!res.ok) throw new Error(errMsg(body, 'Không tải cửa sổ.'));
      setRows(safeArray(body));
    } catch (e) {
      setLoadErr(e.message || 'Lỗi tải.');
      setRows([]);
    } finally {
      setLoading(false);
    }
  }, [hocKyId, phaseFilter]);

  useEffect(() => {
    (async () => {
      try {
        await loadHocKy();
      } catch {
        /* bỏ trống hocKyId */
      }
    })();
  }, [loadHocKy]);

  useEffect(() => {
    loadNganh();
  }, [loadNganh]);

  useEffect(() => {
    loadWindows();
  }, [loadWindows]);

  // ---- Campaign loaders ----
  const loadCampaigns = useCallback(async () => {
    setCampaignLoading(true);
    setCampaignLoadErr('');
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/admin/registration-campaigns`, {
        headers: authHeaders()
      });
      const body = await parseBody(res);
      if (!res.ok) throw new Error(errMsg(body, 'Không tải chiến dịch.'));
      setCampaigns(safeArray(body));
    } catch (e) {
      setCampaignLoadErr(e.message || 'Lỗi tải chiến dịch.');
    } finally {
      setCampaignLoading(false);
    }
  }, []);

  useEffect(() => {
    if (tab === 'campaigns') {
      loadCampaigns();
    }
  }, [tab, loadCampaigns]);

  const openCampaignCreate = () => {
    setCampaignEditing(null);
    setCampaignFormErr('');
    setCFTen('');
    setCFNam('');
    setCFPhase('OFFICIAL');
    const now = new Date();
    setCFOpen(toLocalDatetimeValue(now));
    setCFClose(toLocalDatetimeValue(new Date(now.getTime() + 30 * 86400000)));
    setCFGhiChu('');
    setCampaignDrawerOpen(true);
  };

  const openCampaignEdit = (c) => {
    setCampaignEditing(c);
    setCampaignFormErr('');
    setCFTen(c.tenCampaign || '');
    setCFNam(c.namNhapHoc != null ? String(c.namNhapHoc) : '');
    setCFPhase(c.phase === 'OFFICIAL' ? 'OFFICIAL' : 'PRE');
    setCFOpen(toLocalDatetimeValue(new Date(c.openAt)));
    setCFClose(toLocalDatetimeValue(new Date(c.closeAt)));
    setCFGhiChu(c.ghiChu || '');
    setCampaignDrawerOpen(true);
  };

  const submitCampaignForm = async () => {
    if (!cFTen.trim()) {
      setCampaignFormErr('Tên chiến dịch không được để trống.');
      return;
    }
    const namNum = Number(cFNam.trim());
    if (!cFNam.trim() || !Number.isFinite(namNum) || namNum < 2000 || namNum > 2100) {
      setCampaignFormErr('Năm nhập học phải dạng YYYY (ví dụ 2024).');
      return;
    }
    const openIso = fromLocalDatetimeValue(cFOpen);
    const closeIso = fromLocalDatetimeValue(cFClose);
    if (!openIso || !closeIso || new Date(closeIso) <= new Date(openIso)) {
      setCampaignFormErr('Thời gian không hợp lệ: đóng lúc phải sau mở lúc.');
      return;
    }
    const payload = {
      tenCampaign: cFTen.trim(),
      namNhapHoc: Math.trunc(namNum),
      phase: cFPhase,
      openAt: openIso,
      closeAt: closeIso,
      ghiChu: cFGhiChu?.trim() || undefined
    };
    setCampaignSaving(true);
    setCampaignFormErr('');
    try {
      const url = campaignEditing
        ? `${API_BASE_URL}/api/v1/admin/registration-campaigns/${campaignEditing.id}`
        : `${API_BASE_URL}/api/v1/admin/registration-campaigns`;
      const res = await fetch(url, {
        method: campaignEditing ? 'PUT' : 'POST',
        headers: authHeaders(),
        body: JSON.stringify(payload)
      });
      const body = await parseBody(res);
      if (!res.ok) throw new Error(errMsg(body, 'Không lưu được.'));
      setCampaignDrawerOpen(false);
      await loadCampaigns();
      showToast('ok', 'Đã lưu', campaignEditing ? 'Chiến dịch đã cập nhật.' : 'Chiến dịch đã tạo + windows tự động sinh.');
    } catch (e) {
      setCampaignFormErr(e.message || 'Lỗi lưu.');
    } finally {
      setCampaignSaving(false);
    }
  };

  const doCampaignDelete = async () => {
    if (!campaignDeleteId) return;
    setCampaignDeleteBusy(true);
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/admin/registration-campaigns/${campaignDeleteId}`, {
        method: 'DELETE',
        headers: authHeaders()
      });
      if (!res.ok) {
        const body = await parseBody(res);
        throw new Error(errMsg(body, 'Không xóa được.'));
      }
      setCampaignDeleteId(null);
      await loadCampaigns();
      showToast('ok', 'Đã xóa', 'Chiến dịch và các cửa sổ liên quan đã gỡ.');
    } catch (e) {
      showToast('err', 'Lỗi', e.message);
    } finally {
      setCampaignDeleteBusy(false);
    }
  };

  const filtered = useMemo(() => {
    const q = search.trim().toLowerCase();
    if (!q) return rows;
    return rows.filter((r) => {
      const hay = [
        r.tenNganh,
        r.ghiChu,
        r.phase,
        r.namNhapHoc,
        r.createdBy,
        String(r.id)
      ]
        .filter(Boolean)
        .join(' ')
        .toLowerCase();
      return hay.includes(q);
    });
  }, [rows, search]);

  const openCreate = () => {
    setEditingId(null);
    setFormErr('');
    setScopeErr('');
    setFIdHocKy(hocKyId || '');
    setFPhase('PRE');
    setFNam('');
    setFIdNganh('');
    const now = new Date();
    setFOpen(toLocalDatetimeValue(now));
    setFClose(toLocalDatetimeValue(new Date(now.getTime() + 7 * 86400000)));
    setFGhiChu('');
    setDrawerOpen(true);
  };

  const openEdit = (r) => {
    setEditingId(r.id);
    setFormErr('');
    setScopeErr('');
    setFIdHocKy(String(r.idHocKy));
    setFPhase(r.phase === 'OFFICIAL' ? 'OFFICIAL' : 'PRE');
    setFNam(r.namNhapHoc != null ? String(r.namNhapHoc) : '');
    setFIdNganh(r.idNganh != null ? String(r.idNganh) : '');
    setFOpen(toLocalDatetimeValue(new Date(r.openAt)));
    setFClose(toLocalDatetimeValue(new Date(r.closeAt)));
    setFGhiChu(r.ghiChu || '');
    setDrawerOpen(true);
  };

  const validateScope = () => {
    const hasNganh = fIdNganh && fIdNganh !== '';
    const namNum = fNam.trim() === '' ? null : Number(fNam.trim());
    if (hasNganh && (namNum == null || !Number.isFinite(namNum))) {
      setScopeErr('Nếu chọn ngành, bắt buộc nhập năm nhập học (khóa).');
      return false;
    }
    if (namNum != null && Number.isFinite(namNum)) {
      const y = Math.trunc(namNum);
      if (y < 1900 || y > 2100) {
        setScopeErr('Năm nhập học phải dạng YYYY (ví dụ 2023), không nhập K17/17.');
        return false;
      }
    }
    setScopeErr('');
    return true;
  };

  const submitForm = async () => {
    if (!validateScope()) return;
    const openIso = fromLocalDatetimeValue(fOpen);
    const closeIso = fromLocalDatetimeValue(fClose);
    if (!openIso || !closeIso) {
      setFormErr('Mở lúc / Đóng lúc không hợp lệ.');
      return;
    }
    if (new Date(closeIso) <= new Date(openIso)) {
      setFormErr('Đóng lúc phải sau mở lúc.');
      return;
    }
    const payload = {
      idHocKy: Number(fIdHocKy),
      phase: fPhase,
      openAt: openIso,
      closeAt: closeIso,
      ghiChu: fGhiChu?.trim() || undefined
    };
    const namNum = fNam.trim() === '' ? null : Number(fNam.trim());
    payload.namNhapHoc = namNum != null && Number.isFinite(namNum) ? Math.trunc(namNum) : null;
    const nid = fIdNganh ? Number(fIdNganh) : null;
    payload.idNganh = nid != null && Number.isFinite(nid) ? nid : null;

    setSaving(true);
    setFormErr('');
    try {
      const url =
        editingId != null
          ? `${API_BASE_URL}/api/v1/admin/registration-windows/${editingId}`
          : `${API_BASE_URL}/api/v1/admin/registration-windows`;
      const res = await fetch(url, {
        method: editingId != null ? 'PUT' : 'POST',
        headers: authHeaders(),
        body: JSON.stringify(payload)
      });
      const body = await parseBody(res);
      if (!res.ok) throw new Error(errMsg(body, 'Không lưu được.'));
      setDrawerOpen(false);
      await loadWindows();
      showToast('ok', 'Đã lưu', 'Cửa sổ đăng ký đã cập nhật.');
    } catch (e) {
      setFormErr(e.message || 'Lỗi lưu.');
    } finally {
      setSaving(false);
    }
  };

  const submitQuickOpen = async () => {
    if (!hocKyId) {
      showToast('err', 'Lỗi', 'Chọn học kỳ trước.');
      return;
    }
    const namNum = quickNam.trim() === '' ? null : Number(quickNam.trim());
    if (quickIdNganh && namNum == null) {
      showToast('err', 'Lỗi', 'Chọn ngành thì phải nhập năm nhập học (cohort).');
      return;
    }
    const payload = {
      idHocKy: Number(hocKyId),
      phase: quickPhase,
      namNhapHoc: namNum != null && Number.isFinite(namNum) ? Math.trunc(namNum) : null,
      idNganh: quickIdNganh ? Number(quickIdNganh) : null,
      durationDays: Number(quickDays) > 0 ? Number(quickDays) : 30
    };
    setQuickBusy(true);
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/admin/registration-windows/open-now`, {
        method: 'POST',
        headers: authHeaders(),
        body: JSON.stringify(payload)
      });
      const body = await parseBody(res);
      if (!res.ok) throw new Error(errMsg(body, 'Không mở được phiên.'));
      setQuickOpen(false);
      await loadWindows();
      showToast('ok', 'Đã mở phiên đăng ký', `${quickPhase} · K${payload.namNhapHoc ?? '*'} · ngành ${payload.idNganh ?? '*'}`);
    } catch (e) {
      showToast('err', 'Lỗi', e.message);
    } finally {
      setQuickBusy(false);
    }
  };

  const forcePublishAll = async () => {
    if (!hocKyId) {
      showToast('err', 'Lỗi', 'Chọn học kỳ trước.');
      return;
    }
    if (!window.confirm('Bạn có chắc muốn MỞ TẤT CẢ lớp của học kỳ về trạng thái PUBLISHED + DANG_MO? (kể cả lớp chưa có giảng viên/lịch).')) {
      return;
    }
    setForceBusy(true);
    try {
      const qs = new URLSearchParams({ hocKyId });
      const res = await fetch(`${API_BASE_URL}/api/v1/admin/lop-hoc-phan/force-publish-all?${qs}`, {
        method: 'POST',
        headers: authHeaders()
      });
      const body = await parseBody(res);
      if (!res.ok) throw new Error(errMsg(body, 'Không mở được lớp.'));
      showToast('ok', 'Đã mở lớp', `Total ${body.totalRequested ?? 0} · published ${body.publishedCount ?? 0}`);
    } catch (e) {
      showToast('err', 'Lỗi', e.message);
    } finally {
      setForceBusy(false);
    }
  };

  const doDelete = async () => {
    if (!deleteId) return;
    setDeleteBusy(true);
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/admin/registration-windows/${deleteId}`, {
        method: 'DELETE',
        headers: authHeaders()
      });
      if (!res.ok) {
        const body = await parseBody(res);
        throw new Error(errMsg(body, 'Không xóa được.'));
      }
      setDeleteId(null);
      await loadWindows();
      showToast('ok', 'Đã xóa', 'Cửa sổ đã gỡ.');
    } catch (e) {
      showToast('err', 'Lỗi', e.message);
    } finally {
      setDeleteBusy(false);
    }
  };

  const scopeText = (r) => {
    const cohort =
      r.namNhapHoc != null ? (
        <span className="font-medium text-on-surface">K{r.namNhapHoc}</span>
      ) : (
        <span className="rounded-md bg-secondary-fixed px-2 py-0.5 text-xs font-medium text-on-secondary-fixed">
          Toàn khóa
        </span>
      );
    const nganh =
      r.tenNganh || r.idNganh != null ? (
        <span className="rounded-md bg-surface-container px-2 py-0.5 text-xs text-on-surface-variant">
          {r.tenNganh || `#${r.idNganh}`}
        </span>
      ) : (
        <span className="rounded-md bg-surface-container px-2 py-0.5 text-xs text-on-surface-variant">Tất cả ngành</span>
      );
    return (
      <div className="flex flex-wrap items-center gap-2">
        {cohort}
        {nganh}
      </div>
    );
  };

  const phaseChip = (ph) => (
    <span className="rounded-full bg-surface-container-high px-3 py-1 text-xs font-semibold tracking-wide text-primary">
      {ph === 'OFFICIAL' ? 'OFFICIAL' : 'PRE-REG'}
    </span>
  );

  const legacyEnded = (r) => Date.now() > new Date(r.closeAt).getTime();

  return (
    <div className="mx-auto max-w-6xl text-on-surface">
      <div className="mb-4 flex flex-col justify-between gap-4 sm:flex-row sm:items-end">
        <div>
          <h2 className="mb-2 text-2xl font-bold tracking-tight text-on-surface md:text-3xl">Quản lý Cửa sổ Đăng ký</h2>
          <p className="text-sm text-on-surface-variant">Thiết lập và theo dõi các đợt mở đăng ký học phần (PRE / OFFICIAL).</p>
        </div>
        <div className="flex flex-wrap gap-2">
          {tab === 'campaigns' && (
            <button
              type="button"
              onClick={openCampaignCreate}
              className="inline-flex items-center gap-2 rounded-full bg-gradient-to-br from-[#00288e] to-[#1e40af] px-6 py-3 text-sm font-semibold text-white shadow-[0_20px_40px_rgba(20,27,43,0.12)] transition-opacity hover:opacity-90"
            >
              <span className="material-symbols-outlined text-[20px]" style={{ fontVariationSettings: "'FILL' 1" }}>
                add_circle
              </span>
              Tạo chiến dịch
            </button>
          )}
          {tab === 'windows' && (
            <>
              <button
                type="button"
                onClick={() => {
                  setQuickPhase('OFFICIAL');
                  setQuickNam('');
                  setQuickIdNganh('');
                  setQuickDays(30);
                  setQuickOpen(true);
                }}
                disabled={!hocKyId}
                className="inline-flex items-center gap-2 rounded-full bg-primary px-5 py-3 text-sm font-semibold text-white shadow hover:bg-primary-container transition disabled:opacity-50"
              >
                <span className="material-symbols-outlined text-[20px]">flash_on</span>
                Mở đăng ký NGAY
              </button>
              <button
                type="button"
                onClick={forcePublishAll}
                disabled={!hocKyId || forceBusy}
                className="inline-flex items-center gap-2 rounded-full bg-tertiary px-5 py-3 text-sm font-semibold text-white shadow hover:opacity-90 transition disabled:opacity-50"
              >
                <span className="material-symbols-outlined text-[20px]">publish</span>
                {forceBusy ? 'Đang mở…' : 'Mở tất cả lớp'}
              </button>
              <button
                type="button"
                onClick={openCreate}
                disabled={!hocKyId}
                className="inline-flex items-center gap-2 rounded-full bg-gradient-to-br from-[#00288e] to-[#1e40af] px-6 py-3 text-sm font-semibold text-white shadow-[0_20px_40px_rgba(20,27,43,0.12)] transition-opacity hover:opacity-90 disabled:opacity-50"
              >
                <span className="material-symbols-outlined text-[20px]" style={{ fontVariationSettings: "'FILL' 1" }}>
                  add_circle
                </span>
                Tạo cửa sổ mới
              </button>
            </>
          )}
        </div>
      </div>

      {/* Tab bar */}
      <div className="mb-6 flex gap-1 rounded-xl bg-surface-container-lowest p-1 w-fit shadow-sm">
        {[
          ['windows', 'Cửa sổ'],
          ['campaigns', 'Chiến dịch theo khóa']
        ].map(([v, lab]) => (
          <button
            key={v}
            type="button"
            onClick={() => setTab(v)}
            className={`rounded-lg px-5 py-2.5 text-sm font-semibold transition-all ${
              tab === v
                ? 'bg-surface-container-high text-primary shadow-sm'
                : 'text-on-surface-variant hover:bg-surface-container-low'
            }`}
          >
            {lab}
          </button>
        ))}
      </div>

      {tab === 'campaigns' ? (
        <CampaignTab
          campaigns={campaigns}
          loading={campaignLoading}
          loadErr={campaignLoadErr}
          onEdit={openCampaignEdit}
          onDelete={(id) => setCampaignDeleteId(id)}
          campaignDeleteId={campaignDeleteId}
          onConfirmDelete={doCampaignDelete}
          campaignDeleteBusy={campaignDeleteBusy}
          onCancelDelete={() => setCampaignDeleteId(null)}
        />
      ) : (
        <>
          {/* Filter bar */}
          <div className="mb-8 flex flex-col gap-4 rounded-xl bg-surface-container-low p-4 lg:flex-row lg:items-center lg:justify-between">
            <div className="flex flex-col gap-4 lg:flex-row lg:items-center">
              <div className="relative">
                <select
                  className="appearance-none rounded-lg border-none bg-surface-container-low py-3 pl-4 pr-10 text-sm text-on-surface outline-none focus:ring-2 focus:ring-primary/20"
                  value={hocKyId}
                  onChange={(e) => setHocKyId(e.target.value)}
                >
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
              <div className="flex gap-1 rounded-lg bg-surface-container-lowest p-1 shadow-sm">
                {[
                  ['PRE', 'PRE'],
                  ['OFFICIAL', 'OFFICIAL'],
                  ['', 'Tất cả']
                ].map(([v, lab]) => (
                  <button
                    key={v || 'all'}
                    type="button"
                    onClick={() => setPhaseFilter(v)}
                    className={`rounded-md px-4 py-2 text-xs font-semibold uppercase transition-colors ${
                      phaseFilter === v
                        ? 'bg-surface-container-high text-primary'
                        : 'font-medium text-on-surface-variant hover:bg-surface-container'
                    }`}
                  >
                    {lab}
                  </button>
                ))}
              </div>
            </div>
            <div className="relative w-full lg:max-w-sm">
              <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-on-surface-variant text-[20px]">
                search
              </span>
              <input
                className="w-full rounded-lg border-none bg-surface-container-lowest py-3 pl-10 pr-4 text-sm text-on-surface outline-none focus:ring-2 focus:ring-primary/20"
                placeholder="Tìm ngành, ghi chú, mã pha…"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
              />
            </div>
          </div>

          {loadErr && (
            <div className="mb-4 rounded-xl border border-error/30 bg-error-container/40 p-4 text-sm text-error">{loadErr}</div>
          )}

          <div className="overflow-hidden rounded-xl bg-surface-container-lowest pb-4 shadow-[0_20px_40px_rgba(20,27,43,0.05)]">
            {loading ? (
              <div className="p-8 text-sm text-on-surface-variant">Đang tải…</div>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full border-collapse text-left">
                  <thead>
                    <tr className="border-0 text-sm font-semibold uppercase tracking-[0.05em] text-on-surface-variant">
                      <th className="px-6 py-5">Pha</th>
                      <th className="px-6 py-5">Phạm vi Khóa / Ngành</th>
                      <th className="px-6 py-5">Thời gian Mở — Đóng</th>
                      <th className="px-6 py-5">Tiến trình</th>
                      <th className="px-6 py-5">Trạng thái</th>
                      <th className="px-6 py-5 text-right">Thao tác</th>
                    </tr>
                  </thead>
                  <tbody className="text-sm">
                    {filtered.length === 0 && (
                      <tr>
                        <td colSpan={6} className="px-6 py-10 text-center text-on-surface-variant">
                          Không có cửa sổ nào. Chọn học kỳ hoặc tạo mới.
                        </td>
                      </tr>
                    )}
                    {filtered.map((r, idx) => {
                      const pg = computeProgress(r.openAt, r.closeAt);
                      const st = statusMeta(r);
                      const ended = legacyEnded(r);
                      const zebra = idx % 2 === 1;
                      return (
                        <tr
                          key={r.id}
                          className={`group transition-colors hover:bg-surface-container-low ${zebra ? 'bg-surface-container-low/50' : ''} ${ended ? 'opacity-70' : ''}`}
                        >
                          <td className="px-6 py-4">{phaseChip(r.phase)}</td>
                          <td className="px-6 py-4">{scopeText(r)}</td>
                          <td className="px-6 py-4">
                            <div className="flex flex-col gap-1 text-on-surface-variant text-sm">
                              <span className="flex items-center gap-1">
                                <span className="material-symbols-outlined text-[16px]">calendar_today</span>
                                {formatInstantVi(r.openAt)}
                              </span>
                              <span className="flex items-center gap-1">
                                <span className="material-symbols-outlined text-[16px]">schedule</span>
                                {formatInstantVi(r.closeAt)}
                              </span>
                            </div>
                          </td>
                          <td className="w-52 px-6 py-4">
                            <div className="relative h-2 w-full overflow-hidden rounded-full bg-surface-container-high">
                              <div
                                className={`h-2 rounded-full ${ended ? 'bg-outline-variant' : 'bg-primary'}`}
                                style={{ width: `${pg.pct}%` }}
                              />
                              {pg.pct > 0 && pg.pct < 100 && !ended && (
                                <div
                                  className="absolute top-0 z-10 h-full w-0.5 bg-secondary-container"
                                  style={{ left: `${pg.pct}%` }}
                                />
                              )}
                            </div>
                            <div
                              className={`mt-1 text-xs ${pg.pct >= 100 ? 'text-right' : pg.pct === 0 ? 'text-left' : 'text-right'} text-on-surface-variant`}
                            >
                              {pg.sub}
                            </div>
                          </td>
                          <td className="px-6 py-4">
                            {st.tone === 'open' && (
                              <span className="inline-flex w-max items-center gap-1 rounded-full bg-primary-fixed px-3 py-1 text-xs font-bold text-on-primary-fixed">
                                <span className="inline-block h-2 w-2 rounded-full bg-primary" />
                                Đang mở
                              </span>
                            )}
                            {st.tone === 'soon' && (
                              <span className="inline-flex w-max items-center gap-1 rounded-full bg-surface-container-high px-3 py-1 text-xs font-bold text-primary">
                                Sắp mở
                              </span>
                            )}
                            {st.tone === 'end' && (
                              <span className="inline-flex w-max items-center gap-1 rounded-full bg-surface-container-high px-3 py-1 text-xs font-bold text-on-surface-variant">
                                Hết hạn
                              </span>
                            )}
                          </td>
                          <td className="px-6 py-4 text-right">
                            <div className="flex items-center justify-end gap-1 opacity-100 transition-opacity sm:opacity-0 sm:group-hover:opacity-100">
                              <button
                                type="button"
                                title="Sửa"
                                disabled={ended}
                                className={`rounded-full p-2 ${ended ? 'cursor-not-allowed text-outline-variant' : 'text-on-surface-variant hover:bg-surface-container-high hover:text-primary'}`}
                                onClick={() => !ended && openEdit(r)}
                              >
                                <span className="material-symbols-outlined text-[20px]">edit</span>
                              </button>
                              <button
                                type="button"
                                title="Xóa"
                                className="rounded-full p-2 text-on-surface-variant hover:bg-error-container hover:text-error"
                                onClick={() => setDeleteId(r.id)}
                              >
                                <span className="material-symbols-outlined text-[20px]">delete</span>
                              </button>
                            </div>
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </>
      )}

      {/* Windows drawer */}
      {drawerOpen && (
        <div className="fixed inset-0 z-50 flex justify-end bg-inverse-surface/40 backdrop-blur-sm">
          <div className="flex h-full w-full max-w-md flex-col bg-surface-container-lowest p-8 shadow-[0_20px_40px_rgba(20,27,43,0.08)]">
            <div className="mb-6 flex items-center justify-between">
              <h3 className="text-lg font-bold text-on-surface">{editingId != null ? 'Sửa cửa sổ' : 'Tạo cửa sổ mới'}</h3>
              <button
                type="button"
                className="rounded-full p-2 text-on-surface-variant hover:bg-surface-container-high"
                onClick={() => setDrawerOpen(false)}
              >
                <span className="material-symbols-outlined">close</span>
              </button>
            </div>
            <div className="flex-1 space-y-5 overflow-y-auto pr-1">
              {formErr && <div className="rounded-lg bg-error-container/40 p-3 text-sm text-error">{formErr}</div>}
              <div>
                <label className="mb-2 block text-sm font-semibold text-on-surface">
                  Học kỳ <span className="text-error">*</span>
                </label>
                <select
                  className="w-full rounded-lg border-none bg-surface-container-low py-3 pl-4 pr-4 text-sm text-on-surface outline-none focus:ring-2 focus:ring-primary/20"
                  value={fIdHocKy}
                  onChange={(e) => setFIdHocKy(e.target.value)}
                >
                  {hocKys.map((hk) => {
                    const id = hk.idHocKy ?? hk.id;
                    return (
                      <option key={id} value={String(id)}>
                        {hk.tenHocKy || hk.ten}
                      </option>
                    );
                  })}
                </select>
              </div>
              <div>
                <label className="mb-2 block text-sm font-semibold text-on-surface">
                  Pha đăng ký <span className="text-error">*</span>
                </label>
                <div className="flex gap-1 rounded-lg bg-surface-container-low p-1">
                  <button
                    type="button"
                    className={`flex-1 rounded-md py-2 text-xs font-semibold ${
                      fPhase === 'PRE' ? 'bg-surface-container-lowest text-primary shadow-sm' : 'text-on-surface-variant hover:bg-surface-container'
                    }`}
                    onClick={() => setFPhase('PRE')}
                  >
                    PRE
                  </button>
                  <button
                    type="button"
                    className={`flex-1 rounded-md py-2 text-xs font-semibold ${
                      fPhase === 'OFFICIAL' ? 'bg-surface-container-lowest text-primary shadow-sm' : 'text-on-surface-variant hover:bg-surface-container'
                    }`}
                    onClick={() => setFPhase('OFFICIAL')}
                  >
                    OFFICIAL
                  </button>
                </div>
              </div>
              <div className="flex gap-4">
                <div className="flex-1">
                  <label className="mb-2 block text-sm font-semibold text-on-surface">Năm nhập học (khóa, YYYY)</label>
                  <input
                    type="number"
                    className={`w-full rounded-lg border-none bg-surface-container-low py-3 px-4 text-sm text-on-surface outline-none focus:ring-2 ${scopeErr ? 'ring-2 ring-error/30' : 'focus:ring-primary/20'}`}
                    placeholder="VD: 2023"
                    value={fNam}
                    onChange={(e) => {
                      setFNam(e.target.value);
                      setScopeErr('');
                    }}
                  />
                </div>
                <div className="flex-1">
                  <label className="mb-2 block text-sm font-semibold text-on-surface">Ngành học</label>
                  <select
                    className="w-full rounded-lg border-none bg-surface-container-low py-3 px-4 text-sm text-on-surface outline-none focus:ring-2 focus:ring-primary/20"
                    value={fIdNganh}
                    onChange={(e) => {
                      setFIdNganh(e.target.value);
                      setScopeErr('');
                    }}
                  >
                    <option value="">Tất cả ngành</option>
                    {nganhList.map((n) => (
                      <option key={n.idNganh} value={String(n.idNganh)}>
                        {n.tenNganh}
                      </option>
                    ))}
                  </select>
                </div>
              </div>
              <p className="flex items-start gap-2 rounded-lg bg-secondary-container/10 p-3 text-xs text-secondary">
                <span className="material-symbols-outlined text-[16px]">info</span>
                Nếu chọn ngành cụ thể, bắt buộc nhập năm nhập học tương ứng theo dạng YYYY (ví dụ 2023).
              </p>
              {scopeErr && <p className="text-sm text-error">{scopeErr}</p>}
              <div className="flex gap-4">
                <div className="flex-1">
                  <label className="mb-2 block text-sm font-semibold text-on-surface">
                    Mở lúc <span className="text-error">*</span>
                  </label>
                  <input
                    type="datetime-local"
                    className="w-full rounded-lg border-none bg-surface-container-low py-3 px-4 text-sm text-on-surface outline-none focus:ring-2 focus:ring-primary/20"
                    value={fOpen}
                    onChange={(e) => setFOpen(e.target.value)}
                  />
                </div>
                <div className="flex-1">
                  <label className="mb-2 block text-sm font-semibold text-on-surface">
                    Đóng lúc <span className="text-error">*</span>
                  </label>
                  <input
                    type="datetime-local"
                    className="w-full rounded-lg border-none bg-surface-container-low py-3 px-4 text-sm text-on-surface outline-none focus:ring-2 focus:ring-primary/20"
                    value={fClose}
                    onChange={(e) => setFClose(e.target.value)}
                  />
                </div>
              </div>
              <div>
                <label className="mb-2 block text-sm font-semibold text-on-surface">Ghi chú nội bộ</label>
                <textarea
                  rows={3}
                  className="w-full resize-none rounded-lg border-none bg-surface-container-low py-3 px-4 text-sm text-on-surface outline-none focus:ring-2 focus:ring-primary/20"
                  placeholder="Nhập ghi chú (nếu có)…"
                  value={fGhiChu}
                  onChange={(e) => setFGhiChu(e.target.value)}
                />
              </div>
            </div>
            <div className="mt-6 flex justify-end gap-3 border-t border-outline-variant/20 pt-6">
              <button
                type="button"
                className="rounded-full px-6 py-3 text-sm font-semibold text-primary hover:bg-surface-container-low"
                onClick={() => setDrawerOpen(false)}
              >
                Hủy
              </button>
              <button
                type="button"
                disabled={saving}
                onClick={submitForm}
                className="rounded-full bg-gradient-to-br from-[#00288e] to-[#1e40af] px-8 py-3 text-sm font-semibold text-white shadow-[0_20px_40px_rgba(20,27,43,0.12)] hover:opacity-90 disabled:opacity-50"
              >
                {saving ? 'Đang lưu…' : 'Lưu cửa sổ'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Quick open dialog */}
      {quickOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-inverse-surface/40 backdrop-blur-sm p-4">
          <div className="w-full max-w-lg rounded-2xl bg-surface-container-lowest shadow-[0_20px_40px_rgba(20,27,43,0.08)] overflow-hidden">
            <div className="flex items-center justify-between px-6 py-4 border-b border-surface-container">
              <h3 className="text-lg font-bold text-on-surface flex items-center gap-2">
                <span className="material-symbols-outlined text-primary">flash_on</span>
                Mở đăng ký ngay
              </h3>
              <button
                type="button"
                className="rounded-full p-2 text-on-surface-variant hover:bg-surface-container-high"
                onClick={() => setQuickOpen(false)}
              >
                <span className="material-symbols-outlined">close</span>
              </button>
            </div>
            <div className="px-6 py-5 space-y-5">
              <p className="text-sm text-on-surface-variant">
                Tạo cửa sổ mở từ <strong>thời điểm hiện tại</strong>, kéo dài bao lâu tùy chọn. Nếu đã có window cùng phạm vi sẽ được cập nhật về now.
              </p>

              <div>
                <label className="mb-2 block text-sm font-semibold text-on-surface">Pha đăng ký</label>
                <div className="flex gap-1 rounded-lg bg-surface-container-low p-1">
                  {['OFFICIAL', 'PRE'].map((ph) => (
                    <button
                      key={ph}
                      type="button"
                      className={`flex-1 rounded-md py-2 text-xs font-semibold ${quickPhase === ph ? 'bg-surface-container-lowest text-primary shadow-sm' : 'text-on-surface-variant'}`}
                      onClick={() => setQuickPhase(ph)}
                    >
                      {ph === 'OFFICIAL' ? 'Chính thức' : 'Dự kiến (PRE)'}
                    </button>
                  ))}
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="mb-2 block text-sm font-semibold text-on-surface">Năm nhập học (cohort)</label>
                  <input
                    type="number"
                    placeholder="VD 2023, để trống = mọi khóa"
                    className="w-full rounded-lg border-none bg-surface-container-low py-3 px-4 text-sm outline-none focus:ring-2 focus:ring-primary/20"
                    value={quickNam}
                    onChange={(e) => setQuickNam(e.target.value)}
                  />
                </div>
                <div>
                  <label className="mb-2 block text-sm font-semibold text-on-surface">Ngành</label>
                  <select
                    className="w-full rounded-lg border-none bg-surface-container-low py-3 px-4 text-sm outline-none focus:ring-2 focus:ring-primary/20"
                    value={quickIdNganh}
                    onChange={(e) => setQuickIdNganh(e.target.value)}
                  >
                    <option value="">— Tất cả ngành —</option>
                    {nganhList.map((n) => (
                      <option key={n.idNganh} value={String(n.idNganh)}>
                        {n.tenNganh}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div>
                <label className="mb-2 block text-sm font-semibold text-on-surface">Kéo dài (ngày)</label>
                <input
                  type="number"
                  min={1}
                  className="w-full rounded-lg border-none bg-surface-container-low py-3 px-4 text-sm outline-none focus:ring-2 focus:ring-primary/20"
                  value={quickDays}
                  onChange={(e) => setQuickDays(e.target.value)}
                />
              </div>

              <div className="rounded-lg bg-secondary-container/20 border-l-4 border-secondary p-3 text-xs text-on-surface flex gap-2">
                <span className="material-symbols-outlined text-secondary text-[18px]">info</span>
                <span>
                  Nếu chọn ngành cụ thể, BẮT BUỘC nhập năm nhập học. Để trống cohort + ngành = mở cho toàn học kỳ.
                </span>
              </div>
            </div>
            <div className="px-6 py-4 bg-surface-container-low flex justify-end gap-3">
              <button
                type="button"
                className="rounded-full px-5 py-2.5 text-sm font-semibold text-primary hover:bg-surface-container"
                onClick={() => setQuickOpen(false)}
              >
                Hủy
              </button>
              <button
                type="button"
                disabled={quickBusy}
                onClick={submitQuickOpen}
                className="rounded-full bg-primary text-white px-6 py-2.5 text-sm font-bold shadow hover:bg-primary-container disabled:opacity-40"
              >
                {quickBusy ? 'Đang mở…' : 'Mở phiên ngay'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Windows delete confirmation */}
      {deleteId != null && (
        <div className="fixed inset-0 z-[60] flex items-end justify-center bg-inverse-surface/50 p-4 sm:items-center">
          <div className="w-full max-w-md rounded-xl bg-inverse-surface px-6 py-4 text-inverse-on-surface shadow-[0_20px_40px_rgba(20,27,43,0.12)]">
            <div className="flex gap-4">
              <span className="material-symbols-outlined text-error-container">warning</span>
              <div>
                <p className="font-semibold">Xác nhận xóa cửa sổ?</p>
                <p className="mt-1 text-sm opacity-80">Hành động không hoàn tác.</p>
              </div>
            </div>
            <div className="mt-4 flex justify-end gap-2">
              <button type="button" className="rounded-md px-4 py-2 text-sm font-semibold hover:bg-white/10" onClick={() => setDeleteId(null)}>
                Hủy
              </button>
              <button
                type="button"
                disabled={deleteBusy}
                className="rounded-md px-4 py-2 text-sm font-semibold text-error hover:bg-error-container/20"
                onClick={doDelete}
              >
                {deleteBusy ? 'Đang xóa…' : 'Xóa'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Campaign form drawer */}
      {campaignDrawerOpen && (
        <div className="fixed inset-0 z-50 flex justify-end bg-inverse-surface/40 backdrop-blur-sm">
          <div className="flex h-full w-full max-w-md flex-col bg-surface-container-lowest p-8 shadow-[0_20px_40px_rgba(20,27,43,0.08)]">
            <div className="mb-6 flex items-center justify-between">
              <h3 className="text-lg font-bold text-on-surface">
                {campaignEditing != null ? 'Sửa chiến dịch' : 'Tạo chiến dịch mới'}
              </h3>
              <button
                type="button"
                className="rounded-full p-2 text-on-surface-variant hover:bg-surface-container-high"
                onClick={() => setCampaignDrawerOpen(false)}
              >
                <span className="material-symbols-outlined">close</span>
              </button>
            </div>
            <div className="flex-1 space-y-5 overflow-y-auto pr-1">
              {campaignFormErr && (
                <div className="rounded-lg bg-error-container/40 p-3 text-sm text-error">{campaignFormErr}</div>
              )}

              <div>
                <label className="mb-2 block text-sm font-semibold text-on-surface">
                  Tên chiến dịch <span className="text-error">*</span>
                </label>
                <input
                  type="text"
                  className="w-full rounded-lg border-none bg-surface-container-low py-3 px-4 text-sm text-on-surface outline-none focus:ring-2 focus:ring-primary/20"
                  placeholder="VD: Đợt 1 - K17"
                  value={cFTen}
                  onChange={(e) => setCFTen(e.target.value)}
                />
              </div>

              <div>
                <label className="mb-2 block text-sm font-semibold text-on-surface">
                  Năm nhập học (YYYY) <span className="text-error">*</span>
                </label>
                <input
                  type="number"
                  className="w-full rounded-lg border-none bg-surface-container-low py-3 px-4 text-sm text-on-surface outline-none focus:ring-2 focus:ring-primary/20"
                  placeholder="VD: 2024"
                  value={cFNam}
                  onChange={(e) => setCFNam(e.target.value)}
                />
              </div>

              <div>
                <label className="mb-2 block text-sm font-semibold text-on-surface">
                  Pha <span className="text-error">*</span>
                </label>
                <div className="flex gap-1 rounded-lg bg-surface-container-low p-1">
                  <button
                    type="button"
                    className={`flex-1 rounded-md py-2 text-xs font-semibold ${
                      cFPhase === 'PRE' ? 'bg-surface-container-lowest text-primary shadow-sm' : 'text-on-surface-variant hover:bg-surface-container'
                    }`}
                    onClick={() => setCFPhase('PRE')}
                  >
                    PRE
                  </button>
                  <button
                    type="button"
                    className={`flex-1 rounded-md py-2 text-xs font-semibold ${
                      cFPhase === 'OFFICIAL' ? 'bg-surface-container-lowest text-primary shadow-sm' : 'text-on-surface-variant hover:bg-surface-container'
                    }`}
                    onClick={() => setCFPhase('OFFICIAL')}
                  >
                    OFFICIAL
                  </button>
                </div>
              </div>

              <div>
                <label className="mb-2 block text-sm font-semibold text-on-surface">
                  Thời gian mở <span className="text-error">*</span>
                </label>
                <input
                  type="datetime-local"
                  className="w-full rounded-lg border-none bg-surface-container-low py-3 px-4 text-sm text-on-surface outline-none focus:ring-2 focus:ring-primary/20"
                  value={cFOpen}
                  onChange={(e) => setCFOpen(e.target.value)}
                />
              </div>

              <div>
                <label className="mb-2 block text-sm font-semibold text-on-surface">
                  Thời gian đóng <span className="text-error">*</span>
                </label>
                <input
                  type="datetime-local"
                  className="w-full rounded-lg border-none bg-surface-container-low py-3 px-4 text-sm text-on-surface outline-none focus:ring-2 focus:ring-primary/20"
                  value={cFClose}
                  onChange={(e) => setCFClose(e.target.value)}
                />
              </div>

              <div>
                <label className="mb-2 block text-sm font-semibold text-on-surface">Ghi chú</label>
                <textarea
                  rows={3}
                  className="w-full resize-none rounded-lg border-none bg-surface-container-low py-3 px-4 text-sm text-on-surface outline-none focus:ring-2 focus:ring-primary/20"
                  placeholder="Nhập ghi chú (nếu có)…"
                  value={cFGhiChu}
                  onChange={(e) => setCFGhiChu(e.target.value)}
                />
              </div>
            </div>
            <div className="mt-6 flex justify-end gap-3 border-t border-outline-variant/20 pt-6">
              <button
                type="button"
                className="rounded-full px-6 py-3 text-sm font-semibold text-primary hover:bg-surface-container-low"
                onClick={() => setCampaignDrawerOpen(false)}
              >
                Hủy
              </button>
              <button
                type="button"
                disabled={campaignSaving}
                onClick={submitCampaignForm}
                className="rounded-full bg-gradient-to-br from-[#00288e] to-[#1e40af] px-8 py-3 text-sm font-semibold text-white shadow-[0_20px_40px_rgba(20,27,43,0.12)] hover:opacity-90 disabled:opacity-50"
              >
                {campaignSaving ? 'Đang lưu…' : 'Lưu chiến dịch'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Toast */}
      {toast && (
        <div className="fixed bottom-6 right-6 z-[70] max-w-sm rounded-lg border border-outline-variant bg-surface-container-lowest px-4 py-3 text-sm shadow-lg">
          <p className="font-bold text-on-surface">{toast.title}</p>
          {toast.detail && <p className="text-on-surface-variant">{toast.detail}</p>}
        </div>
      )}
    </div>
  );
};

export default AdminRegistrationWindowsPage;
