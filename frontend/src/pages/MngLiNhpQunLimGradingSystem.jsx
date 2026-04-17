import React, { useCallback, useEffect, useState } from 'react';

import { API_BASE_URL } from '../config/api';

const MngLiNhpQunLimGradingSystem = () => {
  const [classes, setClasses] = useState([]);
  const [selectedId, setSelectedId] = useState('');
  const [gradebook, setGradebook] = useState(null);
  const [drafts, setDrafts] = useState({});
  const [loading, setLoading] = useState(true);
  const [gbLoading, setGbLoading] = useState(false);
  const [error, setError] = useState('');
  const [busyId, setBusyId] = useState(null);

  const token = typeof localStorage !== 'undefined' ? localStorage.getItem('jwt_token') : null;

  const loadClasses = useCallback(async () => {
    if (!token) {
      setError('Chưa đăng nhập. Dùng tài khoản giảng viên (gv01) từ All Portal.');
      setClasses([]);
      setLoading(false);
      return;
    }
    setLoading(true);
    setError('');
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/lecturer/attendance/my-classes`, {
        headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' }
      });
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || 'Không tải danh sách lớp.');
      setClasses(Array.isArray(body) ? body : []);
    } catch (e) {
      setError(e.message || 'Lỗi tải lớp.');
      setClasses([]);
    } finally {
      setLoading(false);
    }
  }, [token]);

  useEffect(() => {
    loadClasses();
  }, [loadClasses]);

  const loadGradebook = useCallback(async (idLopHp) => {
    if (!token || !idLopHp) return;
    setGbLoading(true);
    setError('');
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/lecturer/grades/classes/${idLopHp}`, {
        headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' }
      });
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || 'Không tải bảng điểm lớp.');
      setGradebook(body);
      const d = {};
      (body.rows || []).forEach((r) => {
        d[r.idDangKy] = r.diemHe4 != null ? String(r.diemHe4) : '';
      });
      setDrafts(d);
    } catch (e) {
      setError(e.message || 'Lỗi bảng điểm.');
      setGradebook(null);
    } finally {
      setGbLoading(false);
    }
  }, [token]);

  useEffect(() => {
    if (selectedId) {
      loadGradebook(selectedId);
    } else {
      setGradebook(null);
    }
  }, [selectedId, loadGradebook]);

  const saveDraft = async (idDangKy) => {
    const raw = drafts[idDangKy];
    if (raw === undefined || raw === '') {
      setError('Nhập điểm hệ 4 (0–4) trước khi lưu nháp.');
      return;
    }
    const n = Number(raw);
    if (Number.isNaN(n) || n < 0 || n > 4) {
      setError('Điểm hệ 4 không hợp lệ.');
      return;
    }
    setBusyId(idDangKy);
    setError('');
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/lecturer/grades/by-dang-ky/${idDangKy}`, {
        method: 'PATCH',
        headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
        body: JSON.stringify({ diemHe4: n })
      });
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || 'Lưu nháp thất bại.');
      await loadGradebook(selectedId);
    } catch (e) {
      setError(e.message || 'Lỗi lưu.');
    } finally {
      setBusyId(null);
    }
  };

  const publish = async (idDangKy) => {
    setBusyId(idDangKy);
    setError('');
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/lecturer/grades/by-dang-ky/${idDangKy}/publish`, {
        method: 'POST',
        headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' }
      });
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || 'Công bố thất bại.');
      await loadGradebook(selectedId);
    } catch (e) {
      setError(e.message || 'Lỗi công bố.');
    } finally {
      setBusyId(null);
    }
  };

  const statusLabel = (tt) => {
    if (tt === 'CHO_CONG_BO') return { text: 'Nháp', cls: 'text-amber-800 bg-amber-50' };
    if (tt === 'DA_CONG_BO' || tt == null) return { text: 'Đã công bố', cls: 'text-emerald-800 bg-emerald-50' };
    return { text: tt || '—', cls: 'text-slate-600 bg-slate-100' };
  };

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div>
        <h2 className="text-2xl font-bold text-[#141b2b]">Nhập & quản lý điểm (Gradebook)</h2>
        <p className="text-sm text-slate-600 mt-1">
          Task 17 — <code className="text-xs bg-slate-100 px-1 rounded">PATCH .../grades/by-dang-ky/&#123;id&#125;</code>{' '}
          (nháp), <code className="text-xs bg-slate-100 px-1 rounded">POST .../publish</code> (công bố).
        </p>
      </div>

      {error && (
        <div className="rounded-lg border border-red-200 bg-red-50 text-red-800 px-4 py-3 text-sm">{error}</div>
      )}

      <section className="bg-white rounded-xl border border-[#dce2f7] p-6 shadow-sm space-y-4">
        <label className="block text-xs font-semibold text-slate-500">Lớp học phần</label>
        <div className="flex flex-col sm:flex-row gap-3 sm:items-center">
          <select
            className="flex-1 border border-slate-200 rounded-lg px-3 py-2 text-sm"
            value={selectedId}
            onChange={(e) => setSelectedId(e.target.value)}
          >
            <option value="">— Chọn lớp —</option>
            {classes.map((c) => (
              <option key={c.idLopHp} value={c.idLopHp}>
                {c.maLopHp} — {c.tenHocPhan}
              </option>
            ))}
          </select>
          {gbLoading && <span className="text-xs text-slate-500">Đang tải bảng điểm…</span>}
        </div>
      </section>

      {gradebook && !gbLoading && (
        <section className="bg-white rounded-xl border border-[#dce2f7] shadow-sm overflow-hidden">
          <div className="px-6 py-4 border-b border-slate-100 flex flex-wrap justify-between gap-2">
            <div>
              <h3 className="text-lg font-bold text-[#141b2b]">{gradebook.maLopHp}</h3>
              <p className="text-sm text-slate-600">{gradebook.tenHocPhan}</p>
              <p className="text-xs text-slate-500 mt-1">{gradebook.hocKyLabel} · Sĩ số: {gradebook.siSo}</p>
            </div>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm min-w-[640px]">
              <thead className="bg-slate-50 text-xs uppercase text-slate-500">
                <tr>
                  <th className="px-4 py-3">MSSV</th>
                  <th className="px-4 py-3">Họ tên</th>
                  <th className="px-4 py-3 w-28">Điểm hệ 4</th>
                  <th className="px-4 py-3 w-24">Chữ</th>
                  <th className="px-4 py-3 w-32">Trạng thái</th>
                  <th className="px-4 py-3 text-right">Thao tác</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {(gradebook.rows || []).map((r) => {
                  const st = statusLabel(r.trangThaiBangDiem);
                  const locked =
                    r.trangThaiBangDiem === 'DA_CONG_BO' ||
                    (r.trangThaiBangDiem == null && r.diemHe4 != null);
                  return (
                    <tr key={r.idDangKy} className="hover:bg-slate-50/80">
                      <td className="px-4 py-3 font-mono text-xs">{r.maSinhVien}</td>
                      <td className="px-4 py-3 font-medium">{r.hoTen}</td>
                      <td className="px-4 py-3">
                        <input
                          type="text"
                          inputMode="decimal"
                          disabled={locked}
                          className="w-24 border border-slate-200 rounded px-2 py-1 text-sm disabled:bg-slate-100"
                          value={drafts[r.idDangKy] ?? ''}
                          onChange={(e) => setDrafts((prev) => ({ ...prev, [r.idDangKy]: e.target.value }))}
                        />
                      </td>
                      <td className="px-4 py-3 text-slate-600">{r.diemChu || '—'}</td>
                      <td className="px-4 py-3">
                        <span className={`text-[10px] font-bold px-2 py-1 rounded ${st.cls}`}>{st.text}</span>
                      </td>
                      <td className="px-4 py-3 text-right space-x-1">
                        <button
                          type="button"
                          disabled={locked || busyId === r.idDangKy}
                          onClick={() => saveDraft(r.idDangKy)}
                          className="text-xs px-2 py-1 rounded bg-[#00288e] text-white disabled:opacity-40"
                        >
                          Lưu nháp
                        </button>
                        <button
                          type="button"
                          disabled={locked || busyId === r.idDangKy}
                          onClick={() => publish(r.idDangKy)}
                          className="text-xs px-2 py-1 rounded border border-emerald-700 text-emerald-800 disabled:opacity-40"
                        >
                          Công bố
                        </button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </section>
      )}

      {loading && <p className="text-sm text-slate-500">Đang tải…</p>}
    </div>
  );
};

export default MngLiNhpQunLimGradingSystem;
