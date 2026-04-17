import React, { useCallback, useEffect, useState } from 'react';

import { API_BASE_URL } from '../config/api';

const initials = (name) => {
  if (!name || typeof name !== 'string') return '?';
  const p = name.trim().split(/\s+/);
  if (p.length === 1) return p[0].slice(0, 2).toUpperCase();
  return (p[0][0] + p[p.length - 1][0]).toUpperCase();
};

const CnhTayPhiCVnHcTpAcademicAdvising = () => {
  const token = typeof localStorage !== 'undefined' ? localStorage.getItem('jwt_token') : null;
  const [threshold, setThreshold] = useState(12);
  const [draftThreshold, setDraftThreshold] = useState('12');
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    if (!token) {
      setError('Chưa đăng nhập. Dùng tài khoản giảng viên (gv01) từ All Portal.');
      setData(null);
      setLoading(false);
      return;
    }
    setLoading(true);
    setError('');
    try {
      const q = new URLSearchParams({ minFailedCredits: String(threshold) });
      const res = await fetch(`${API_BASE_URL}/api/v1/lecturer/advisory/at-risk?${q}`, {
        headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' }
      });
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || 'Không tải danh sách cảnh báo.');
      setData(body);
    } catch (e) {
      setError(e.message || 'Lỗi tải dữ liệu.');
      setData(null);
    } finally {
      setLoading(false);
    }
  }, [token, threshold]);

  useEffect(() => {
    load();
  }, [load]);

  const applyThreshold = () => {
    const n = parseInt(draftThreshold, 10);
    if (Number.isNaN(n) || n < 0) {
      setError('Ngưỡng tín chỉ rớt phải là số ≥ 0.');
      return;
    }
    setError('');
    setThreshold(n);
  };

  const rows = data?.sinhViens || [];
  const avgGpa = (() => {
    const gs = rows.map((r) => r.gpaTichLuy).filter((g) => g != null);
    if (!gs.length) return null;
    const sum = gs.reduce((a, g) => a + Number(g), 0);
    return (sum / gs.length).toFixed(2);
  })();
  const maxRot = rows.reduce((m, r) => Math.max(m, r.tinChiRot || 0), 0);

  return (
    <main className="min-h-screen">
      <section className="p-8 space-y-10">
        <div className="flex flex-col lg:flex-row lg:justify-between lg:items-end gap-6">
          <div className="max-w-2xl">
            <span className="text-secondary font-bold tracking-widest text-xs uppercase mb-2 block">Quản lý cố vấn</span>
            <h1 className="text-4xl md:text-5xl font-black font-headline text-on-surface leading-tight tracking-tight">
              Sinh viên cảnh báo học vụ
            </h1>
            <p className="mt-4 text-on-surface-variant font-body leading-relaxed">
              Danh sách sinh viên thuộc các ngành của khoa bạn, có tổng tín chỉ các môn đã công bố với điểm hệ 4 dưới 1.0
              từ <span className="font-bold text-on-surface">{data?.minFailedCredits ?? threshold}</span> trở lên (mặc định 12 theo task).
            </p>
            {data?.khoaTen && (
              <p className="mt-2 text-sm text-primary font-bold">
                Phạm vi: {data.khoaMa} — {data.khoaTen}
              </p>
            )}
          </div>
          <div className="flex flex-wrap gap-3 items-end">
            <div className="flex items-center gap-2 bg-surface-container-low rounded-full px-4 py-2">
              <label htmlFor="thr" className="text-xs font-bold text-on-surface-variant whitespace-nowrap">
                Ngưỡng TC rớt
              </label>
              <input
                id="thr"
                type="number"
                min={0}
                className="w-16 bg-transparent border-none text-sm font-bold text-on-surface focus:ring-0"
                value={draftThreshold}
                onChange={(e) => setDraftThreshold(e.target.value)}
              />
              <button
                type="button"
                onClick={applyThreshold}
                className="px-3 py-1 rounded-full bg-primary text-white text-xs font-bold hover:opacity-90"
              >
                Áp dụng
              </button>
            </div>
            <button
              type="button"
              onClick={load}
              className="px-6 py-3 rounded-full bg-surface-container-high text-primary font-bold text-sm hover:bg-surface-container-highest transition-colors"
            >
              Làm mới
            </button>
          </div>
        </div>

        {error && (
          <div className="rounded-xl bg-error-container text-on-error-container px-4 py-3 text-sm font-medium">{error}</div>
        )}
        {data?.hint && (
          <div className="rounded-xl bg-secondary-container text-on-secondary-container px-4 py-3 text-sm font-medium">
            {data.hint}
          </div>
        )}

        <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
          <div className="bg-surface-container-lowest p-6 rounded-xl shadow-sm border-l-4 border-primary">
            <p className="text-sm font-medium text-on-surface-variant mb-1">Số SV đạt ngưỡng</p>
            <p className="text-3xl font-black text-on-surface">{loading ? '…' : data?.tongSoSinhVien ?? 0}</p>
          </div>
          <div className="bg-surface-container-lowest p-6 rounded-xl shadow-sm border-l-4 border-secondary">
            <p className="text-sm font-medium text-on-surface-variant mb-1">GPA TB (có dữ liệu)</p>
            <p className="text-3xl font-black text-on-surface">{loading ? '…' : avgGpa ?? '—'}</p>
          </div>
          <div className="bg-surface-container-lowest p-6 rounded-xl shadow-sm border-l-4 border-error">
            <p className="text-sm font-medium text-on-surface-variant mb-1">Cao nhất TC rớt</p>
            <p className="text-3xl font-black text-error">{loading ? '…' : maxRot}</p>
          </div>
          <div className="bg-surface-container-lowest p-6 rounded-xl shadow-sm border-l-4 border-primary-container">
            <p className="text-sm font-medium text-on-surface-variant mb-1">Ngưỡng đang dùng</p>
            <p className="text-3xl font-black text-on-surface">{data?.minFailedCredits ?? threshold}</p>
          </div>
        </div>

        <div className="bg-surface-container-lowest rounded-xl shadow-sm overflow-hidden">
          <div className="px-8 py-6 border-b border-surface-container">
            <h3 className="text-lg font-bold text-on-surface font-headline">Chi tiết sinh viên</h3>
            <p className="text-xs text-on-surface-variant mt-1">Điểm chỉ tính khi trạng thái bảng điểm là đã công bố (hoặc dữ liệu cũ null).</p>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-surface-container-low/50">
                  <th className="px-8 py-4 text-[0.6875rem] font-bold text-on-surface-variant uppercase tracking-[0.05em]">Họ và tên</th>
                  <th className="px-6 py-4 text-[0.6875rem] font-bold text-on-surface-variant uppercase tracking-[0.05em]">Mã SV</th>
                  <th className="px-6 py-4 text-[0.6875rem] font-bold text-on-surface-variant uppercase tracking-[0.05em]">Lớp hành chính</th>
                  <th className="px-6 py-4 text-[0.6875rem] font-bold text-on-surface-variant uppercase tracking-[0.05em]">GPA TL</th>
                  <th className="px-6 py-4 text-[0.6875rem] font-bold text-on-surface-variant uppercase tracking-[0.05em]">TC rớt</th>
                  <th className="px-6 py-4 text-[0.6875rem] font-bold text-on-surface-variant uppercase tracking-[0.05em]">Số môn rớt</th>
                </tr>
              </thead>
              <tbody className="divide-y-0">
                {loading && (
                  <tr>
                    <td colSpan={6} className="px-8 py-10 text-center text-on-surface-variant text-sm">
                      Đang tải…
                    </td>
                  </tr>
                )}
                {!loading && rows.length === 0 && (
                  <tr>
                    <td colSpan={6} className="px-8 py-10 text-center text-on-surface-variant text-sm">
                      Không có sinh viên nào vượt ngưỡng. Thử hạ ngưỡng (ví dụ 3–6) nếu dữ liệu demo còn ít điểm rớt.
                    </td>
                  </tr>
                )}
                {!loading &&
                  rows.map((r) => (
                    <tr key={r.idSinhVien} className="hover:bg-surface-container-low transition-colors">
                      <td className="px-8 py-4">
                        <div className="flex items-center gap-3">
                          <div className="w-10 h-10 rounded-full bg-primary/15 text-primary flex items-center justify-center text-xs font-black flex-shrink-0">
                            {initials(r.hoTen)}
                          </div>
                          <div className="font-bold text-on-surface">{r.hoTen}</div>
                        </div>
                      </td>
                      <td className="px-6 py-4 text-on-surface-variant font-mono text-sm">{r.maSinhVien}</td>
                      <td className="px-6 py-4 text-sm">
                        <span className="font-mono text-on-surface-variant">{r.maLop}</span>
                        <span className="text-on-surface-variant text-xs block">{r.tenLop}</span>
                      </td>
                      <td className="px-6 py-4">
                        {r.gpaTichLuy != null ? (
                          <span className={`font-bold ${Number(r.gpaTichLuy) < 2 ? 'text-error' : 'text-on-surface'}`}>{r.gpaTichLuy}</span>
                        ) : (
                          <span className="text-on-surface-variant text-sm">—</span>
                        )}
                      </td>
                      <td className="px-6 py-4">
                        <span className="px-3 py-1 rounded-full bg-error-container text-on-error-container text-xs font-bold">{r.tinChiRot} TC</span>
                      </td>
                      <td className="px-6 py-4 text-on-surface-variant text-sm">{r.soMonRot}</td>
                    </tr>
                  ))}
              </tbody>
            </table>
          </div>
        </div>
      </section>
    </main>
  );
};

export default CnhTayPhiCVnHcTpAcademicAdvising;
