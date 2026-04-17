import React, { useCallback, useEffect, useState } from 'react';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const CnhTayPhiCVnHcTpAcademicAdvising = () => {
  const [minFailTc, setMinFailTc] = useState(12);
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    const token = localStorage.getItem('jwt_token');
    if (!token) {
      setError('Vui lòng đăng nhập tài khoản giảng viên.');
      setData(null);
      setLoading(false);
      return;
    }
    setLoading(true);
    setError('');
    try {
      const qs = `?minFailTc=${encodeURIComponent(String(minFailTc))}`;
      const res = await fetch(`${API_BASE_URL}/api/v1/lecturer/advisory/at-risk${qs}`, {
        headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' }
      });
      const body = await res.json().catch(() => ({}));
      if (!res.ok) {
        throw new Error(body.message || 'Không tải được danh sách cảnh báo.');
      }
      setData(body);
    } catch (e) {
      setError(e.message || 'Lỗi tải dữ liệu.');
      setData(null);
    } finally {
      setLoading(false);
    }
  }, [minFailTc]);

  useEffect(() => {
    load();
  }, [load]);

  const rows = data?.rows || [];

  return (
    <main className="min-h-screen">
      <section className="p-8 space-y-12">
        <div className="flex flex-col lg:flex-row lg:justify-between lg:items-end gap-6">
          <div className="max-w-2xl">
            <span className="text-secondary font-bold tracking-widest text-xs uppercase mb-2 block">Task 19 — Cố vấn học tập</span>
            <h1 className="text-4xl lg:text-5xl font-black font-headline text-on-surface leading-tight tracking-tight">
              Sinh viên rớt nhiều tín chỉ
            </h1>
            <p className="mt-4 text-on-surface-variant font-body leading-relaxed">
              Lọc theo <strong>khoa của giảng viên đăng nhập</strong>: tổng tín chỉ các môn đã công bố có điểm hệ 4 &lt; 1.0 vượt ngưỡng. API{' '}
              <code className="text-xs bg-surface-container-high px-1 rounded">GET /api/v1/lecturer/advisory/at-risk?minFailTc=</code>
            </p>
          </div>
          <div className="flex flex-wrap gap-3 items-end">
            <div>
              <label className="block text-xs text-on-surface-variant mb-1">Ngưỡng tín chỉ rớt (&gt;)</label>
              <input
                type="number"
                min={1}
                max={120}
                value={minFailTc}
                onChange={(e) => setMinFailTc(Number(e.target.value) || 12)}
                className="border border-outline-variant rounded-lg px-3 py-2 w-28 bg-surface text-sm"
              />
            </div>
            <button
              type="button"
              onClick={load}
              className="px-6 py-3 rounded-full bg-primary text-white font-bold text-sm shadow-lg hover:opacity-90"
            >
              Áp dụng
            </button>
          </div>
        </div>

        {error && (
          <div className="rounded-xl border border-error/30 bg-error-container/20 px-4 py-3 text-sm text-error font-medium">{error}</div>
        )}
        {loading && <p className="text-sm text-on-surface-variant">Đang tải…</p>}

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-6">
          <div className="bg-surface-container-lowest p-6 rounded-xl shadow-sm border-l-4 border-primary">
            <p className="text-sm font-medium text-on-surface-variant mb-1">Khoa</p>
            <p className="text-2xl font-black text-on-surface">{data?.tenKhoa || '—'}</p>
          </div>
          <div className="bg-surface-container-lowest p-6 rounded-xl shadow-sm border-l-4 border-error">
            <p className="text-sm font-medium text-on-surface-variant mb-1">SV vượt ngưỡng</p>
            <p className="text-2xl font-black text-error">{data?.tongSoBanGhi ?? '—'}</p>
          </div>
          <div className="bg-surface-container-lowest p-6 rounded-xl shadow-sm border-l-4 border-secondary">
            <p className="text-sm font-medium text-on-surface-variant mb-1">Ngưỡng đang dùng</p>
            <p className="text-2xl font-black text-on-surface">{data?.nguongTinChiRot ?? minFailTc} TC</p>
          </div>
        </div>

        <div className="bg-surface-container-lowest rounded-xl shadow-sm overflow-hidden">
          <div className="px-8 py-6 border-b border-surface-container flex justify-between items-center">
            <h3 className="text-lg font-bold text-on-surface font-headline">Danh sách chi tiết</h3>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-surface-container-low/50">
                  <th className="px-8 py-4 text-[0.6875rem] font-bold text-on-surface-variant uppercase">Họ tên</th>
                  <th className="px-6 py-4 text-[0.6875rem] font-bold text-on-surface-variant uppercase">Mã SV</th>
                  <th className="px-6 py-4 text-[0.6875rem] font-bold text-on-surface-variant uppercase">Lớp</th>
                  <th className="px-6 py-4 text-[0.6875rem] font-bold text-on-surface-variant uppercase">TC rớt</th>
                  <th className="px-6 py-4 text-[0.6875rem] font-bold text-on-surface-variant uppercase">Số môn rớt</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-surface-container">
                {!loading && rows.length === 0 && (
                  <tr>
                    <td colSpan={5} className="px-8 py-10 text-center text-on-surface-variant text-sm">
                      Không có sinh viên nào vượt ngưỡng trong khoa (hoặc chưa có điểm công bố).
                    </td>
                  </tr>
                )}
                {rows.map((r) => (
                  <tr key={r.idSinhVien} className="hover:bg-surface-container-low transition-colors">
                    <td className="px-8 py-4 font-semibold text-on-surface">{r.hoTen}</td>
                    <td className="px-6 py-4 font-mono text-sm text-primary">{r.maSinhVien}</td>
                    <td className="px-6 py-4 text-sm text-on-surface-variant">{r.maLop || '—'}</td>
                    <td className="px-6 py-4 font-bold text-error">{r.tongTinChiRot != null ? String(r.tongTinChiRot) : '—'}</td>
                    <td className="px-6 py-4 text-sm">{r.soMonRot}</td>
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
