import React, { useEffect, useMemo, useState } from 'react';

import { API_BASE_URL, authHeaders } from '../config/api';

const formatDt = (iso) => {
  if (!iso) return '—';
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return String(iso);
  return d.toLocaleString('vi-VN');
};

const statusMeta = (s) => {
  const x = String(s || '').toUpperCase();
  if (x === 'PENDING') return { label: 'ĐÃ GỬI', cls: 'bg-blue-100 text-blue-700' };
  if (x === 'APPROVED') return { label: 'ĐÃ DUYỆT', cls: 'bg-green-100 text-green-700' };
  if (x === 'APPLIED') return { label: 'ĐÃ ÁP DỤNG', cls: 'bg-purple-100 text-purple-700' };
  if (x === 'REJECTED') return { label: 'TỪ CHỐI', cls: 'bg-red-100 text-red-700' };
  return { label: x || 'UNKNOWN', cls: 'bg-slate-100 text-slate-600' };
};

const templatePayload = (template) => {
  if (template === 'ROOM_CHANGE') {
    return {
      action: 'ROOM_SWAP',
      data: { idLopHp: 1, fromRoom: 'B201', toRoom: 'A405', reason: 'AC maintenance' }
    };
  }
  if (template === 'LECTURER_SWAP') {
    return {
      action: 'LECTURER_SWAP',
      data: { idLopHp: 1, fromLecturerId: 10, toLecturerId: 22, reason: 'Teaching load balance' }
    };
  }
  if (template === 'SLOT_SHIFT') {
    return {
      action: 'SLOT_SHIFT',
      data: { idLopHp: 1, oldSlot: { thu: 2, tiet: '1-3' }, newSlot: { thu: 4, tiet: '4-6' } }
    };
  }
  return {};
};

const LchSNhtKDuChnAuditTrailsLogging = () => {
  const [hocKys, setHocKys] = useState([]);
  const [hocKyId, setHocKyId] = useState('');
  const [template, setTemplate] = useState('');
  const [jsonText, setJsonText] = useState('');
  const [requestedBy, setRequestedBy] = useState('');
  const [note, setNote] = useState('');
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [msg, setMsg] = useState('');
  const [err, setErr] = useState('');
  const [searchId, setSearchId] = useState('');

  const loadHocKy = async () => {
    const res = await fetch(`${API_BASE_URL}/api/hoc-ky`, { headers: authHeaders() });
    const body = await res.json().catch(() => []);
    if (!res.ok) throw new Error(body.message || 'Không tải danh sách học kỳ.');
    const list = Array.isArray(body) ? body : [];
    setHocKys(list);
    if (!hocKyId && list.length > 0) setHocKyId(String(list[0].idHocKy));
  };

  const loadChangeSets = async () => {
    if (!hocKyId) return;
    setLoading(true);
    setErr('');
    try {
      const res = await fetch(
        `${API_BASE_URL}/api/v1/admin/scheduling/hoc-ky/${hocKyId}/change-sets`,
        { headers: authHeaders() }
      );
      const body = await res.json().catch(() => []);
      if (!res.ok) throw new Error(body.message || 'Không tải danh sách change set.');
      const list = Array.isArray(body) ? body : [];
      list.sort((a, b) => new Date(b.createdAt || 0).getTime() - new Date(a.createdAt || 0).getTime());
      setRows(list);
    } catch (e) {
      setErr(e.message || 'Lỗi tải change set.');
      setRows([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    (async () => {
      try {
        await loadHocKy();
      } catch (e) {
        setErr(e.message || 'Lỗi tải học kỳ.');
      }
    })();
  }, []);

  useEffect(() => {
    loadChangeSets();
  }, [hocKyId]);

  const applyTemplate = (tpl) => {
    setTemplate(tpl);
    setJsonText(JSON.stringify(templatePayload(tpl), null, 2));
  };

  const submitChangeSet = async () => {
    if (!hocKyId) return;
    setSubmitting(true);
    setErr('');
    setMsg('');
    try {
      const payloadDelta = JSON.parse(jsonText || '{}');
      const res = await fetch(
        `${API_BASE_URL}/api/v1/admin/scheduling/hoc-ky/${hocKyId}/change-sets/submit`,
        {
          method: 'POST',
          headers: authHeaders(),
          body: JSON.stringify({
            payloadDelta,
            requestedBy: requestedBy.trim() || null,
            note: note.trim() || null
          })
        }
      );
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || 'Gửi change set thất bại.');
      setMsg(`Change Set #${body.id} đã được tạo.`);
      await loadChangeSets();
    } catch (e) {
      setErr(e.message || 'Lỗi gửi change set.');
    } finally {
      setSubmitting(false);
    }
  };

  const reviewChangeSet = async (id, approve) => {
    if (!hocKyId) return;
    const lyDoThayDoi = window.prompt(approve ? 'Nhập lý do duyệt:' : 'Nhập lý do từ chối:');
    if (!lyDoThayDoi) return;
    setErr('');
    setMsg('');
    try {
      const res = await fetch(
        `${API_BASE_URL}/api/v1/admin/scheduling/hoc-ky/${hocKyId}/change-sets/${id}/review`,
        {
          method: 'POST',
          headers: authHeaders(),
          body: JSON.stringify({
            approve,
            reviewedBy: requestedBy.trim() || null,
            lyDoThayDoi,
            reviewNote: null
          })
        }
      );
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || 'Review thất bại.');
      setMsg(`Đã ${approve ? 'duyệt' : 'từ chối'} #${id}.`);
      await loadChangeSets();
    } catch (e) {
      setErr(e.message || 'Lỗi review.');
    }
  };

  const applyChangeSet = async (id) => {
    if (!hocKyId) return;
    const lyDoThayDoi = window.prompt('Nhập lý do áp dụng change set:');
    if (!lyDoThayDoi) return;
    setErr('');
    setMsg('');
    try {
      const res = await fetch(
        `${API_BASE_URL}/api/v1/admin/scheduling/hoc-ky/${hocKyId}/change-sets/${id}/apply`,
        {
          method: 'POST',
          headers: authHeaders(),
          body: JSON.stringify({
            appliedBy: requestedBy.trim() || null,
            lyDoThayDoi
          })
        }
      );
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || 'Apply thất bại.');
      setMsg(`Đã áp dụng #${id}.`);
      await loadChangeSets();
    } catch (e) {
      setErr(e.message || 'Lỗi apply.');
    }
  };

  const visibleRows = useMemo(() => {
    const q = searchId.trim().toLowerCase();
    if (!q) return rows;
    return rows.filter((r) => String(r.id).toLowerCase().includes(q));
  }, [rows, searchId]);

  const approvedCount = rows.filter((r) => String(r.trangThai).toUpperCase() === 'APPLIED').length;
  const pendingCount = rows.filter((r) => String(r.trangThai).toUpperCase() === 'PENDING').length;

  return (
    <main className="ml-64 flex-1 p-8 min-h-screen bg-background">
      <section className="mb-6">
        <div className="bg-secondary-fixed text-on-secondary-fixed px-6 py-4 rounded-xl flex items-center gap-3">
          <span className="material-symbols-outlined text-secondary">warning</span>
          <p className="font-semibold text-sm">Học kỳ đã CÔNG BỐ — chỉ áp dụng change-set theo policy quy định</p>
        </div>
      </section>

      <header className="mb-8 max-w-3xl">
        <h2 className="text-4xl font-extrabold text-on-surface mb-2">Workflow công bố thời khóa biểu</h2>
        <p className="text-on-surface-variant">
          Quản lý phiên bản và thay đổi lịch trình giảng dạy. Mọi thay đổi sau khi công bố đều phải qua Change Set.
        </p>
      </header>

      {err && <div className="mb-4 rounded-lg bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-700">{err}</div>}
      {msg && <div className="mb-4 rounded-lg bg-emerald-50 border border-emerald-200 px-4 py-3 text-sm text-emerald-700">{msg}</div>}

      <div className="grid grid-cols-12 gap-8">
        <section className="col-span-12 xl:col-span-4">
          <div className="bg-white rounded-xl p-6 shadow-sm space-y-4">
            <div className="flex items-center gap-2 border-b border-surface-container pb-3">
              <span className="material-symbols-outlined text-primary">add_box</span>
              <h3 className="font-bold text-lg">Tạo Change Set mới</h3>
            </div>

            <label className="block text-xs font-bold text-on-surface-variant uppercase">
              Học kỳ áp dụng
              <select
                className="mt-1 w-full bg-surface-container-low rounded-lg py-2.5 px-3"
                value={hocKyId}
                onChange={(e) => setHocKyId(e.target.value)}
              >
                {hocKys.map((h) => (
                  <option key={h.idHocKy} value={h.idHocKy}>
                    HK{h.kyThu} - {h.namHoc}
                  </option>
                ))}
              </select>
            </label>

            <label className="block text-xs font-bold text-on-surface-variant uppercase">
              Chọn template mẫu
              <select
                className="mt-1 w-full bg-surface-container-low rounded-lg py-2.5 px-3"
                value={template}
                onChange={(e) => applyTemplate(e.target.value)}
              >
                <option value="">-- Chọn template --</option>
                <option value="ROOM_CHANGE">Thay đổi phòng học</option>
                <option value="LECTURER_SWAP">Đổi giảng viên</option>
                <option value="SLOT_SHIFT">Điều chỉnh giờ học</option>
              </select>
            </label>

            <label className="block text-xs font-bold text-on-surface-variant uppercase">
              Requested By (optional)
              <input
                value={requestedBy}
                onChange={(e) => setRequestedBy(e.target.value)}
                className="mt-1 w-full bg-surface-container-low rounded-lg py-2.5 px-3 text-sm"
                placeholder="admin01"
              />
            </label>

            <label className="block text-xs font-bold text-on-surface-variant uppercase">
              Note
              <input
                value={note}
                onChange={(e) => setNote(e.target.value)}
                className="mt-1 w-full bg-surface-container-low rounded-lg py-2.5 px-3 text-sm"
                placeholder="Ghi chú thay đổi"
              />
            </label>

            <label className="block text-xs font-bold text-on-surface-variant uppercase">
              Cấu hình JSON
              <textarea
                className="mt-1 w-full h-64 bg-slate-900 text-blue-100 font-mono text-xs p-4 rounded-lg"
                value={jsonText}
                onChange={(e) => setJsonText(e.target.value)}
                placeholder='{"action":"ROOM_SWAP","data":{"idLopHp":1}}'
              />
            </label>

            <button
              type="button"
              disabled={submitting}
              onClick={submitChangeSet}
              className="w-full bg-primary text-white py-3.5 rounded-full font-bold disabled:opacity-50"
            >
              {submitting ? 'Đang gửi...' : 'Gửi Change Set'}
            </button>
          </div>
        </section>

        <section className="col-span-12 xl:col-span-8">
          <div className="bg-white rounded-xl shadow-sm overflow-hidden">
            <div className="p-6 flex justify-between items-center border-b border-surface-container">
              <h3 className="font-bold text-lg">Danh sách Change Set gần đây</h3>
              <div className="flex gap-2">
                <input
                  className="bg-surface-container-low rounded-lg px-3 py-2 text-xs w-36"
                  placeholder="Tìm ID..."
                  value={searchId}
                  onChange={(e) => setSearchId(e.target.value)}
                />
                <button type="button" onClick={loadChangeSets} className="px-3 py-2 rounded-lg bg-surface-container-low text-xs font-semibold">
                  Làm mới
                </button>
              </div>
            </div>
            <div className="overflow-x-auto">
              <table className="w-full text-left">
                <thead className="bg-surface-container-low">
                  <tr>
                    <th className="px-6 py-4 text-xs font-bold text-on-surface-variant uppercase">ID</th>
                    <th className="px-6 py-4 text-xs font-bold text-on-surface-variant uppercase">Học kỳ</th>
                    <th className="px-6 py-4 text-xs font-bold text-on-surface-variant uppercase">Người tạo</th>
                    <th className="px-6 py-4 text-xs font-bold text-on-surface-variant uppercase">Ngày tạo</th>
                    <th className="px-6 py-4 text-xs font-bold text-on-surface-variant uppercase">Trạng thái</th>
                    <th className="px-6 py-4 text-xs font-bold text-on-surface-variant uppercase text-right">Thao tác</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-surface-container/30">
                  {loading ? (
                    <tr><td colSpan={6} className="px-6 py-5 text-sm text-on-surface-variant">Đang tải...</td></tr>
                  ) : visibleRows.length === 0 ? (
                    <tr><td colSpan={6} className="px-6 py-5 text-sm text-on-surface-variant">Chưa có change set.</td></tr>
                  ) : (
                    visibleRows.map((r) => {
                      const st = statusMeta(r.trangThai);
                      return (
                        <tr key={r.id} className="hover:bg-surface-container-low/40">
                          <td className="px-6 py-5 font-mono text-sm font-bold text-primary">#CS-{r.id}</td>
                          <td className="px-6 py-5 text-sm">{r.hocKyId}</td>
                          <td className="px-6 py-5 text-sm">{r.requestedBy || '—'}</td>
                          <td className="px-6 py-5 text-sm text-on-surface-variant">{formatDt(r.createdAt)}</td>
                          <td className="px-6 py-5">
                            <span className={`px-3 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider ${st.cls}`}>{st.label}</span>
                          </td>
                          <td className="px-6 py-5 text-right">
                            {String(r.trangThai).toUpperCase() === 'PENDING' && (
                              <div className="flex justify-end gap-2">
                                <button type="button" onClick={() => reviewChangeSet(r.id, true)} className="bg-primary text-white text-[11px] font-bold px-4 py-2 rounded-full">
                                  Review
                                </button>
                                <button type="button" onClick={() => reviewChangeSet(r.id, false)} className="bg-red-50 text-red-700 text-[11px] font-bold px-4 py-2 rounded-full">
                                  Từ chối
                                </button>
                              </div>
                            )}
                            {String(r.trangThai).toUpperCase() === 'APPROVED' && (
                              <button type="button" onClick={() => applyChangeSet(r.id)} className="bg-green-600 text-white text-[11px] font-bold px-4 py-2 rounded-full">
                                Áp dụng
                              </button>
                            )}
                            {!['PENDING', 'APPROVED'].includes(String(r.trangThai).toUpperCase()) && (
                              <button
                                type="button"
                                className="text-on-surface-variant p-2 rounded-full hover:bg-surface-container-high"
                                title={JSON.stringify(r.payloadDelta || {})}
                              >
                                <span className="material-symbols-outlined">visibility</span>
                              </button>
                            )}
                          </td>
                        </tr>
                      );
                    })
                  )}
                </tbody>
              </table>
            </div>
          </div>

          <div className="mt-8 grid grid-cols-2 gap-6">
            <div className="bg-surface-container p-6 rounded-xl flex items-center gap-4">
              <div className="w-12 h-12 rounded-full bg-white flex items-center justify-center text-primary">
                <span className="material-symbols-outlined">check_box</span>
              </div>
              <div>
                <p className="text-xs font-bold text-on-surface-variant uppercase">Đã áp dụng</p>
                <p className="text-3xl font-black text-primary">{approvedCount}</p>
              </div>
            </div>
            <div className="bg-surface-container-high p-6 rounded-xl flex items-center gap-4">
              <div className="w-12 h-12 rounded-full bg-white flex items-center justify-center text-secondary">
                <span className="material-symbols-outlined">pending_actions</span>
              </div>
              <div>
                <p className="text-xs font-bold text-on-surface-variant uppercase">Chờ phê duyệt</p>
                <p className="text-3xl font-black text-secondary">{pendingCount}</p>
              </div>
            </div>
          </div>
        </section>
      </div>
    </main>
  );
};

export default LchSNhtKDuChnAuditTrailsLogging;
