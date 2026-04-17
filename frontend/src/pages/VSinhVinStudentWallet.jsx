import React, { useCallback, useEffect, useState } from 'react';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const formatVnd = (n) => {
  if (n == null || n === '') return '—';
  const x = Number(n);
  if (!Number.isFinite(x)) return '—';
  return new Intl.NumberFormat('vi-VN').format(x) + ' ₫';
};

const VSinhVinStudentWallet = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    const token = localStorage.getItem('jwt_token');
    if (!token) {
      setError('Vui lòng đăng nhập tài khoản sinh viên.');
      setData(null);
      setLoading(false);
      return;
    }
    setLoading(true);
    setError('');
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/wallet/me`, {
        headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' }
      });
      const body = await res.json().catch(() => ({}));
      if (!res.ok) {
        throw new Error(body.message || 'Không tải được ví.');
      }
      setData(body);
    } catch (e) {
      setError(e.message || 'Lỗi tải dữ liệu.');
      setData(null);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const username = typeof localStorage !== 'undefined' ? localStorage.getItem('username') : '';

  return (
    <main className="px-6 pb-12 min-h-screen bg-surface">
      <div className="max-w-5xl mx-auto space-y-8 pt-8">
        <div className="flex flex-col md:flex-row md:items-end justify-between gap-4">
          <div>
            <h1 className="text-3xl font-extrabold text-on-surface tracking-tight mb-2">Ví sinh viên</h1>
            <p className="text-on-surface-variant text-sm">
              Task 8 — <code className="text-xs bg-surface-container-high px-1 rounded">GET /api/v1/wallet/me</code>
              . Nạp tiền qua thanh toán MOCK: trang Thanh toán QR → &quot;Xác nhận thanh toán MOCK&quot;.
            </p>
            {username && (
              <p className="text-sm text-on-surface mt-2">
                Xin chào, <span className="text-primary font-bold">{username}</span>
              </p>
            )}
          </div>
          <button
            type="button"
            onClick={load}
            disabled={loading}
            className="px-5 py-2.5 rounded-full border border-primary text-primary font-bold text-sm hover:bg-primary/10 disabled:opacity-50"
          >
            {loading ? 'Đang tải…' : 'Làm mới'}
          </button>
        </div>

        {error && (
          <div className="rounded-xl border border-error/30 bg-error-container/30 p-4 text-sm text-error">
            {error}
          </div>
        )}

        {!loading && !error && data && (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <section className="bg-surface-container-lowest rounded-2xl p-8 shadow-sm border border-outline-variant/20">
              <p className="text-xs font-bold text-primary uppercase tracking-widest mb-2">Số dư ví</p>
              <p className="text-4xl font-black text-on-surface">{formatVnd(data.soDu)}</p>
              <p className="text-xs text-on-surface-variant mt-4">
                Mã ví: <span className="font-mono">{data.idVi}</span>
              </p>
            </section>
            <section className="bg-surface-container-lowest rounded-2xl p-8 shadow-sm border border-outline-variant/20">
              <p className="text-xs font-bold text-error uppercase tracking-widest mb-2">Ước tính học phí & công nợ</p>
              <p className="text-sm text-on-surface-variant">
                Tổng học phí lớp đã đăng ký (theo dữ liệu lớp):{' '}
                <span className="font-bold text-on-surface">{formatVnd(data.tongHocPhiDangKyUocTinh)}</span>
              </p>
              <p className="text-lg font-bold text-error mt-3">
                Nợ ước tính (max(0, học phí − số dư ví)): {formatVnd(data.noHocPhiUocTinh)}
              </p>
              <p className="text-[11px] text-on-surface-variant mt-3">
                * Chỉ minh họa đồ án; không thay thế kế toán thực tế.
              </p>
            </section>
          </div>
        )}

        {!loading && !error && data && (
          <section className="bg-surface-container-lowest rounded-xl p-6 shadow-sm border border-outline-variant/20">
            <h2 className="text-lg font-bold text-on-surface mb-4">Lịch sử giao dịch ví</h2>
            {(data.giaoDichGanDay || []).length === 0 ? (
              <p className="text-sm text-on-surface-variant">Chưa có giao dịch. Xác nhận một giao dịch MOCK để thấy dòng nạp tiền.</p>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-left text-sm min-w-[560px]">
                  <thead className="text-[11px] uppercase text-on-surface-variant border-b border-outline-variant/20">
                    <tr>
                      <th className="py-2 pr-4">Thời gian</th>
                      <th className="py-2 pr-4">Loại</th>
                      <th className="py-2 pr-4 text-right">Số tiền</th>
                      <th className="py-2 pr-4 text-right">Số dư sau</th>
                      <th className="py-2">Mã đơn TT</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-outline-variant/10">
                    {data.giaoDichGanDay.map((g) => (
                      <tr key={g.idGiaoDichVi} className="hover:bg-surface-container-low/40">
                        <td className="py-3 pr-4 whitespace-nowrap text-on-surface-variant">
                          {g.thoiGian ? String(g.thoiGian).replace('T', ' ').slice(0, 19) : '—'}
                        </td>
                        <td className="py-3 pr-4 font-mono text-xs">{g.loai}</td>
                        <td className="py-3 pr-4 text-right font-semibold text-emerald-800">
                          +{formatVnd(g.soTien)}
                        </td>
                        <td className="py-3 pr-4 text-right">{formatVnd(g.soDuSau)}</td>
                        <td className="py-3 font-mono text-xs">{g.maDonHangThanhToan || '—'}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </section>
        )}
      </div>
    </main>
  );
};

export default VSinhVinStudentWallet;
