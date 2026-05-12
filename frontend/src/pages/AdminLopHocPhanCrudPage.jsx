import React, { useCallback, useEffect, useState } from 'react';
import { API_BASE_URL, authHeaders } from '../config/api';

const safe = (x) => (Array.isArray(x) ? x : []);

const AdminLopHocPhanCrudPage = () => {
  const [hocKys, setHocKys] = useState([]);
  const [selectedHocKyId, setSelectedHocKyId] = useState('');
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(false);
  const [msg, setMsg] = useState('');
  const [err, setErr] = useState('');
  const [panelOpen, setPanelOpen] = useState(false);
  const [editId, setEditId] = useState(null);
  const [saving, setSaving] = useState(false);

  /* Ref data for dropdowns */
  const [hocPhans, setHocPhans] = useState([]);
  const [giangViens, setGiangViens] = useState([]);

  const [form, setForm] = useState({
    maLopHp: '', idHocPhan: '', idHocKy: '', idGiangVien: '',
    siSoToiDa: '', hocPhi: '', thoiKhoaBieuText: '[]',
  });

  /* Load học kỳ list */
  useEffect(() => {
    (async () => {
      try {
        const res = await fetch(`${API_BASE_URL}/api/hoc-ky`, { headers: authHeaders() });
        const body = await res.json().catch(() => []);
        const list = safe(body);
        setHocKys(list);
        if (list.length) setSelectedHocKyId(String(list[0].idHocKy));
      } catch { /* ignore */ }
    })();
  }, []);

  /* Load ref data */
  useEffect(() => {
    (async () => {
      try {
        const [hpRes, gvRes] = await Promise.all([
          fetch(`${API_BASE_URL}/api/hoc-phan`, { headers: authHeaders() }),
          fetch(`${API_BASE_URL}/api/giang-vien`, { headers: authHeaders() }),
        ]);
        setHocPhans(safe(await hpRes.json().catch(() => [])));
        setGiangViens(safe(await gvRes.json().catch(() => [])));
      } catch { /* ignore */ }
    })();
  }, []);

  /* Load LHP by HK */
  const loadRows = useCallback(async () => {
    if (!selectedHocKyId) return;
    setLoading(true); setErr('');
    try {
      const res = await fetch(`${API_BASE_URL}/api/lop-hoc-phan/hoc-ky/${selectedHocKyId}`, { headers: authHeaders() });
      const body = await res.json().catch(() => []);
      if (!res.ok) throw new Error(body.message || `Lỗi ${res.status}`);
      setRows(safe(body));
    } catch (e) { setErr(e.message); } finally { setLoading(false); }
  }, [selectedHocKyId]);

  useEffect(() => { loadRows(); }, [loadRows]);

  const resetForm = () => { setPanelOpen(false); setEditId(null); };

  const openCreate = () => {
    setForm({ maLopHp: '', idHocPhan: '', idHocKy: selectedHocKyId, idGiangVien: '', siSoToiDa: '50', hocPhi: '', thoiKhoaBieuText: '[]' });
    setEditId(null); setPanelOpen(true);
  };

  const openEdit = (r) => {
    setForm({
      maLopHp: r.maLopHp || '', idHocPhan: r.idHocPhan || '', idHocKy: r.idHocKy || selectedHocKyId,
      idGiangVien: r.idGiangVien || '', siSoToiDa: r.siSoToiDa || '', hocPhi: r.hocPhi || '',
      thoiKhoaBieuText: JSON.stringify(r.thoiKhoaBieuJson || [], null, 2),
    });
    setEditId(r.idLopHp); setPanelOpen(true);
  };

  const saveForm = async () => {
    setSaving(true); setErr(''); setMsg('');
    try {
      let tkb = [];
      try { tkb = JSON.parse(form.thoiKhoaBieuText); } catch { tkb = []; }
      const payload = {
        maLopHp: form.maLopHp, idHocPhan: Number(form.idHocPhan), idHocKy: Number(form.idHocKy || selectedHocKyId),
        idGiangVien: form.idGiangVien ? Number(form.idGiangVien) : null,
        siSoToiDa: Number(form.siSoToiDa), hocPhi: form.hocPhi ? Number(form.hocPhi) : null,
        thoiKhoaBieuJson: tkb,
      };
      const isEdit = editId != null;
      const url = isEdit ? `${API_BASE_URL}/api/lop-hoc-phan/${editId}` : `${API_BASE_URL}/api/lop-hoc-phan`;
      const res = await fetch(url, { method: isEdit ? 'PUT' : 'POST', headers: authHeaders(), body: JSON.stringify(payload) });
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || 'Lưu thất bại.');
      setMsg(isEdit ? 'Đã cập nhật LHP.' : 'Đã tạo LHP mới.');
      resetForm(); await loadRows();
    } catch (e) { setErr(e.message); } finally { setSaving(false); }
  };

  const deleteRow = async (id) => {
    if (!window.confirm('Xóa lớp học phần này?')) return;
    setErr(''); setMsg('');
    try {
      const res = await fetch(`${API_BASE_URL}/api/lop-hoc-phan/${id}`, { method: 'DELETE', headers: authHeaders() });
      if (!res.ok && res.status !== 204) { const b = await res.json().catch(() => ({})); throw new Error(b.message || 'Xóa thất bại.'); }
      setMsg('Đã xóa.'); await loadRows();
    } catch (e) { setErr(e.message); }
  };

  const actionLhp = async (id, action, label) => {
    setErr(''); setMsg('');
    try {
      const res = await fetch(`${API_BASE_URL}/api/lop-hoc-phan/${id}/${action}`, { method: 'PATCH', headers: authHeaders() });
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || `${label} thất bại.`);
      setMsg(`${label} thành công.`); await loadRows();
    } catch (e) { setErr(e.message); }
  };

  const statusBadge = (s) => {
    const colors = { SHELL: 'bg-gray-100 text-gray-700', SCHEDULED: 'bg-blue-50 text-blue-700', PUBLISHED: 'bg-emerald-50 text-emerald-700' };
    return <span className={`px-2 py-0.5 text-[10px] font-bold uppercase rounded-full ${colors[s] || 'bg-gray-100 text-gray-700'}`}>{s || '—'}</span>;
  };

  const inp = 'mt-1 w-full bg-[#f1f3ff] rounded-xl py-2.5 px-3 text-sm border border-[#dce2f7] focus:border-[#00288e] focus:outline-none';

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-end justify-between gap-4">
        <div>
          <h1 className="text-3xl font-black text-[#00288e] tracking-tight">Quản lý Lớp Học Phần</h1>
          <p className="text-sm text-[#64748b] mt-1">CRUD lớp học phần + Publish / Close theo học kỳ.</p>
        </div>
        <div className="flex gap-2 items-center">
          <select className="bg-white border border-[#dce2f7] rounded-xl px-4 py-2 text-sm font-semibold" value={selectedHocKyId} onChange={(e) => setSelectedHocKyId(e.target.value)}>
            {hocKys.map((h) => <option key={h.idHocKy} value={h.idHocKy}>HK{h.kyThu} - {h.namHoc}</option>)}
          </select>
          <button type="button" onClick={openCreate} className="px-5 py-2.5 bg-[#00288e] text-white font-bold rounded-full text-sm hover:bg-[#001a5c] transition">
            <span className="material-symbols-outlined text-base align-middle mr-1">add</span>Thêm LHP
          </button>
        </div>
      </div>

      {err && <div className="rounded-lg bg-red-50 border border-red-200 text-red-800 px-4 py-3 text-sm">{err}</div>}
      {msg && <div className="rounded-lg bg-emerald-50 border border-emerald-200 text-emerald-800 px-4 py-3 text-sm">{msg}</div>}

      <div className="bg-white rounded-xl border border-[#dce2f7] shadow-sm overflow-hidden">
        <div className="px-5 py-3 bg-[#f1f3ff] flex items-center justify-between">
          <span className="text-sm font-semibold text-[#334155]">{rows.length} lớp học phần</span>
          <button type="button" onClick={loadRows} className="text-xs text-[#00288e] font-semibold hover:underline">Làm mới</button>
        </div>
        {loading ? <div className="p-6 text-sm text-[#64748b]">Đang tải…</div> : rows.length === 0 ? <div className="p-6 text-sm text-[#64748b]">Chưa có LHP cho học kỳ này.</div> : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead>
                <tr className="bg-[#f1f3ff]">
                  <th className="px-3 py-3 text-[10px] font-bold uppercase text-[#64748b]">Mã LHP</th>
                  <th className="px-3 py-3 text-[10px] font-bold uppercase text-[#64748b]">Học phần</th>
                  <th className="px-3 py-3 text-[10px] font-bold uppercase text-[#64748b]">Giảng viên</th>
                  <th className="px-3 py-3 text-[10px] font-bold uppercase text-[#64748b] text-center">Sĩ số</th>
                  <th className="px-3 py-3 text-[10px] font-bold uppercase text-[#64748b]">Trạng thái</th>
                  <th className="px-3 py-3 text-[10px] font-bold uppercase text-[#64748b]">Publish</th>
                  <th className="px-3 py-3 text-[10px] font-bold uppercase text-[#64748b] text-right">Thao tác</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-[#eef1fa]">
                {rows.map((r) => (
                  <tr key={r.idLopHp} className="hover:bg-[#f9f9ff] transition">
                    <td className="px-3 py-3 font-mono text-[#00288e] font-semibold">{r.maLopHp}</td>
                    <td className="px-3 py-3"><div className="font-medium">{r.tenHocPhan}</div><div className="text-xs text-[#64748b]">{r.maHocPhan} • {r.soTinChi}TC</div></td>
                    <td className="px-3 py-3">{r.tenGiangVien || '—'}</td>
                    <td className="px-3 py-3 text-center"><span className="font-bold">{r.siSoThucTe ?? 0}</span>/{r.siSoToiDa}</td>
                    <td className="px-3 py-3">{r.trangThai || '—'}</td>
                    <td className="px-3 py-3">{statusBadge(r.statusPublish)}</td>
                    <td className="px-3 py-3 text-right">
                      <div className="flex gap-1 justify-end flex-wrap">
                        <button type="button" onClick={() => openEdit(r)} className="px-2 py-1 text-xs rounded border border-[#dce2f7] hover:bg-[#dde1ff] font-semibold">Sửa</button>
                        {r.statusPublish !== 'PUBLISHED' && (
                          <button type="button" onClick={() => actionLhp(r.idLopHp, 'phat-hanh', 'Phát hành')} className="px-2 py-1 text-xs rounded bg-emerald-50 text-emerald-700 hover:bg-emerald-100 font-semibold">Publish</button>
                        )}
                        {r.statusPublish === 'PUBLISHED' && (
                          <button type="button" onClick={() => actionLhp(r.idLopHp, 'dong-lop', 'Đóng lớp')} className="px-2 py-1 text-xs rounded bg-amber-50 text-amber-700 hover:bg-amber-100 font-semibold">Đóng</button>
                        )}
                        <button type="button" onClick={() => deleteRow(r.idLopHp)} className="px-2 py-1 text-xs rounded bg-red-50 text-red-700 hover:bg-red-100 font-semibold">Xóa</button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Slide Panel */}
      {panelOpen && (
        <div className="fixed inset-y-0 right-0 w-[500px] bg-white shadow-2xl z-50 border-l border-[#dce2f7] overflow-y-auto">
          <div className="sticky top-0 bg-white px-6 py-4 flex justify-between items-center border-b border-[#dce2f7]">
            <h2 className="text-xl font-black text-[#00288e]">{editId != null ? 'Cập nhật' : 'Thêm'} Lớp HP</h2>
            <button type="button" onClick={resetForm} className="w-8 h-8 rounded-full hover:bg-[#f1f3ff] flex items-center justify-center">
              <span className="material-symbols-outlined">close</span>
            </button>
          </div>
          <div className="p-6 space-y-4">
            <label className="block text-sm font-semibold text-[#334155]">Mã LHP <span className="text-red-500">*</span>
              <input className={inp} value={form.maLopHp} onChange={(e) => setForm((v) => ({ ...v, maLopHp: e.target.value }))} />
            </label>
            <label className="block text-sm font-semibold text-[#334155]">Học phần <span className="text-red-500">*</span>
              <select className={inp} value={form.idHocPhan} onChange={(e) => setForm((v) => ({ ...v, idHocPhan: e.target.value }))}>
                <option value="">— Chọn —</option>
                {hocPhans.map((h) => <option key={h.idHocPhan} value={h.idHocPhan}>{h.maHocPhan} — {h.tenHocPhan}</option>)}
              </select>
            </label>
            <label className="block text-sm font-semibold text-[#334155]">Giảng viên
              <select className={inp} value={form.idGiangVien} onChange={(e) => setForm((v) => ({ ...v, idGiangVien: e.target.value }))}>
                <option value="">— Chưa gán —</option>
                {giangViens.map((g) => <option key={g.idGiangVien} value={g.idGiangVien}>{g.maGiangVien} — {g.tenGiangVien}</option>)}
              </select>
            </label>
            <div className="grid grid-cols-2 gap-3">
              <label className="block text-sm font-semibold text-[#334155]">Sĩ số tối đa <span className="text-red-500">*</span>
                <input type="number" min={1} className={inp} value={form.siSoToiDa} onChange={(e) => setForm((v) => ({ ...v, siSoToiDa: e.target.value }))} />
              </label>
              <label className="block text-sm font-semibold text-[#334155]">Học phí
                <input type="number" min={0} className={inp} value={form.hocPhi} onChange={(e) => setForm((v) => ({ ...v, hocPhi: e.target.value }))} />
              </label>
            </div>
            <label className="block text-sm font-semibold text-[#334155]">Thời khóa biểu (JSON)
              <textarea rows={5} className={`${inp} font-mono text-xs`} value={form.thoiKhoaBieuText} onChange={(e) => setForm((v) => ({ ...v, thoiKhoaBieuText: e.target.value }))} />
            </label>
            <div className="pt-3 flex gap-3">
              <button type="button" disabled={saving} onClick={saveForm}
                className="flex-1 py-3 bg-[#00288e] text-white font-bold rounded-full disabled:opacity-50 hover:bg-[#001a5c] transition text-sm">
                {saving ? 'Đang lưu…' : editId != null ? 'Lưu cập nhật' : 'Xác nhận tạo'}
              </button>
              <button type="button" onClick={resetForm} className="px-6 py-3 bg-[#f1f3ff] text-[#334155] font-bold rounded-full text-sm">Hủy</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminLopHocPhanCrudPage;
