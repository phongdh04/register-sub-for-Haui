import React, { useCallback, useEffect, useState } from 'react';

import { API_BASE_URL } from '../config/api';

const formatVnd = (n) => {
  if (n == null || n === '') return '—';
  const x = Number(n);
  if (!Number.isFinite(x)) return '—';
  return new Intl.NumberFormat('vi-VN').format(x) + ' ₫';
};

const maxIn = (arr, pick) => (arr.length ? Math.max(1, ...arr.map(pick)) : 1);

const BoCoPhnTchAnalytics = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    const token = localStorage.getItem('jwt_token');
    if (!token) {
      setError('Vui lòng đăng nhập tài khoản admin.');
      setData(null);
      setLoading(false);
      return;
    }
    setLoading(true);
    setError('');
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/admin/analytics/dashboard`, {
        headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' }
      });
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || 'Không tải được dashboard phân tích.');
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

  const dk = data?.dangKyTheoHocKy || [];
  const kh = data?.sinhVienTheoKhoa || [];
  const pay = data?.thanhToanTheoTrangThai || [];
  const top = data?.topLopTheoSiSo || [];
  const maxDk = maxIn(dk, (x) => Number(x.soDangKyHieuLuc) || 0);
  const maxKh = maxIn(kh, (x) => Number(x.soSinhVien) || 0);

  return (
    <main className="min-h-screen p-8 lg:p-12">
      <div className="flex flex-col lg:flex-row lg:items-end justify-between gap-6 mb-10">
        <div>
          <span className="text-primary font-bold tracking-widest text-xs uppercase mb-2 block">Task 15 — Analytics</span>
          <h1 className="text-4xl font-black text-on-surface font-headline">Báo cáo &amp; Phân tích</h1>
          <p className="text-on-surface-variant text-sm mt-2 max-w-2xl">
            Dữ liệu thật từ <code className="text-xs bg-surface-container-high px-1 rounded">GET /api/v1/admin/analytics/dashboard</code> (đăng ký theo HK, SV theo khoa, thanh toán, top lớp theo sĩ số HK mới nhất).
          </p>
        </div>
        <button
          type="button"
          onClick={load}
          className="px-6 py-3 rounded-full bg-primary text-on-primary font-bold text-sm shadow-lg hover:opacity-90"
        >
          Làm mới
        </button>
      </div>

      {error && (
        <div className="mb-6 rounded-xl border border-error/30 bg-error-container/20 px-4 py-3 text-sm text-error font-medium">{error}</div>
      )}
      {loading && <p className="text-sm text-on-surface-variant mb-6">Đang tải…</p>}

      <div className="grid grid-cols-1 sm:grid-cols-3 gap-6 mb-10">
        <div className="bg-primary-container text-on-primary-container rounded-xl p-6 shadow-md">
          <p className="text-sm font-medium opacity-80 mb-1">Tổng sinh viên</p>
          <p className="text-4xl font-black">{data?.tongSinhVien ?? '—'}</p>
        </div>
        <div className="bg-secondary-container text-on-secondary-container rounded-xl p-6 shadow-md">
          <p className="text-sm font-medium opacity-80 mb-1">Đăng ký hiệu lực</p>
          <p className="text-4xl font-black">{data?.tongDangKyHieuLuc ?? '—'}</p>
        </div>
        <div className="bg-tertiary-container/30 rounded-xl p-6 shadow-md border border-outline-variant/30">
          <p className="text-sm font-medium text-on-surface-variant mb-1">Giao dịch thanh toán</p>
          <p className="text-4xl font-black text-on-surface">{data?.tongGiaoDichThanhToan ?? '—'}</p>
        </div>
      </div>

      <div className="grid grid-cols-1 xl:grid-cols-2 gap-8 mb-10">
        <section className="bg-surface-container-lowest rounded-xl p-8 shadow-sm">
          <h3 className="font-headline text-lg font-bold text-on-surface mb-2">Đăng ký theo học kỳ</h3>
          <p className="text-on-surface-variant text-sm mb-6">Số dòng đăng ký THANH_CONG / CHO_DUYET</p>
          <div className="space-y-4">
            {dk.length === 0 && <p className="text-sm text-on-surface-variant">Chưa có dữ liệu.</p>}
            {dk.map((row) => {
              const pct = ((Number(row.soDangKyHieuLuc) || 0) / maxDk) * 100;
              return (
                <div key={row.idHocKy}>
                  <div className="flex justify-between text-xs font-semibold mb-1">
                    <span>{row.hocKyLabel}</span>
                    <span>{row.soDangKyHieuLuc}</span>
                  </div>
                  <div className="h-3 rounded-full bg-surface-container-high overflow-hidden">
                    <div className="h-full bg-primary rounded-full transition-all" style={{ width: `${pct}%` }} />
                  </div>
                </div>
              );
            })}
          </div>
        </section>

        <section className="bg-surface-container-lowest rounded-xl p-8 shadow-sm">
          <h3 className="font-headline text-lg font-bold text-on-surface mb-2">Sinh viên theo khoa</h3>
          <p className="text-on-surface-variant text-sm mb-6">Phân bổ hồ sơ SV (lớp → ngành → khoa)</p>
          <div className="space-y-4">
            {kh.length === 0 && <p className="text-sm text-on-surface-variant">Chưa có dữ liệu.</p>}
            {kh.map((row) => {
              const pct = ((Number(row.soSinhVien) || 0) / maxKh) * 100;
              return (
                <div key={row.maKhoa}>
                  <div className="flex justify-between text-xs font-semibold mb-1">
                    <span>
                      {row.tenKhoa} <span className="text-on-surface-variant font-normal">({row.maKhoa})</span>
                    </span>
                    <span>{row.soSinhVien}</span>
                  </div>
                  <div className="h-3 rounded-full bg-surface-container-high overflow-hidden">
                    <div className="h-full bg-secondary rounded-full transition-all" style={{ width: `${pct}%` }} />
                  </div>
                </div>
              );
            })}
          </div>
        </section>
      </div>

      <div className="grid grid-cols-1 xl:grid-cols-2 gap-8">
        <section className="bg-surface-container-lowest rounded-xl overflow-hidden shadow-sm">
          <div className="p-6 border-b border-surface-container">
            <h3 className="font-headline text-lg font-bold text-on-surface">Thanh toán theo trạng thái</h3>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead>
                <tr className="bg-surface-container-low/50">
                  <th className="px-6 py-3 text-xs font-bold uppercase text-on-surface-variant">Trạng thái</th>
                  <th className="px-6 py-3 text-xs font-bold uppercase text-on-surface-variant">Số lượng</th>
                  <th className="px-6 py-3 text-xs font-bold uppercase text-on-surface-variant">Tổng tiền</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-surface-container">
                {pay.length === 0 && (
                  <tr>
                    <td colSpan={3} className="px-6 py-8 text-center text-on-surface-variant">
                      Chưa có giao dịch.
                    </td>
                  </tr>
                )}
                {pay.map((row) => (
                  <tr key={row.trangThai}>
                    <td className="px-6 py-3 font-mono text-xs">{row.trangThai}</td>
                    <td className="px-6 py-3">{row.soLuong}</td>
                    <td className="px-6 py-3 font-semibold">{formatVnd(row.tongSoTien)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>

        <section className="bg-surface-container-lowest rounded-xl overflow-hidden shadow-sm">
          <div className="p-6 border-b border-surface-container flex justify-between items-start gap-4">
            <div>
              <h3 className="font-headline text-lg font-bold text-on-surface">Top lớp theo sĩ số thực tế</h3>
              <p className="text-on-surface-variant text-sm mt-1">
                Học kỳ: <strong>{data?.hocKyLabelTopClasses || '—'}</strong>
              </p>
            </div>
            <span
              className="material-symbols-outlined text-orange-600"
              style={{ fontVariationSettings: '"FILL" 1' }}
              aria-hidden
            >
              local_fire_department
            </span>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead>
                <tr className="bg-surface-container-low/50">
                  <th className="px-6 py-3 text-xs font-bold uppercase text-on-surface-variant">Lớp / HP</th>
                  <th className="px-6 py-3 text-xs font-bold uppercase text-on-surface-variant">Sĩ số</th>
                  <th className="px-6 py-3 text-xs font-bold uppercase text-on-surface-variant">Lấp đầy</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-surface-container">
                {top.length === 0 && (
                  <tr>
                    <td colSpan={3} className="px-6 py-8 text-center text-on-surface-variant">
                      Chưa có lớp học phần trong HK này.
                    </td>
                  </tr>
                )}
                {top.map((row) => (
                  <tr key={row.maLopHp}>
                    <td className="px-6 py-3">
                      <div className="font-semibold text-on-surface">{row.tenHocPhan}</div>
                      <div className="text-xs font-mono text-primary">{row.maLopHp}</div>
                    </td>
                    <td className="px-6 py-3 whitespace-nowrap">
                      {row.siSoThucTe} / {row.siSoToiDa}
                    </td>
                    <td className="px-6 py-3 min-w-[120px]">
                      <div className="h-2 rounded-full bg-surface-container-high overflow-hidden">
                        <div className="h-full bg-orange-500 rounded-full" style={{ width: `${Math.min(100, row.tyLePhanTram)}%` }} />
                      </div>
                      <span className="text-[10px] text-on-surface-variant">{row.tyLePhanTram}%</span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>
      </div>
    </main>
  );
};

export default BoCoPhnTchAnalytics;
