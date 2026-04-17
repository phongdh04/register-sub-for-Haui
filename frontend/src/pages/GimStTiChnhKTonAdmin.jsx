import React, { useCallback, useEffect, useState } from 'react';
import { API_BASE_URL, authHeaders } from '../config/api';

const formatVnd = (n) => {
  if (n == null || n === '') return '—';
  const x = Number(n);
  if (!Number.isFinite(x)) return '—';
  return new Intl.NumberFormat('vi-VN').format(x) + ' ₫';
};

const downloadCsv = (rows, filename) => {
  const header = ['maSinhVien', 'hoTen', 'maLop', 'soDuVi', 'tongHocPhiDangKy', 'conNoUocTinh', 'coNo'];
  const lines = [header.join(',')];
  for (const r of rows) {
    lines.push(
      [r.maSinhVien, `"${(r.hoTen || '').replace(/"/g, '""')}"`, r.maLop, r.soDuVi, r.tongHocPhiDangKy, r.conNoUocTinh, r.coNo].join(',')
    );
  }
  const blob = new Blob(['\ufeff' + lines.join('\n')], { type: 'text/csv;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  a.click();
  URL.revokeObjectURL(url);
};

const GimStTiChnhKTonAdmin = () => {
  const [summary, setSummary] = useState(null);
  const [err, setErr] = useState('');
  const [recPage, setRecPage] = useState(0);
  const [recData, setRecData] = useState(null);
  const [payPage, setPayPage] = useState(0);
  const [payFilter, setPayFilter] = useState('');
  const [payData, setPayData] = useState(null);
  const [wxPage, setWxPage] = useState(0);
  const [wxData, setWxData] = useState(null);
  const [loading, setLoading] = useState(true);

  const loadSummary = useCallback(async () => {
    const res = await fetch(`${API_BASE_URL}/api/v1/admin/finance/summary`, { headers: authHeaders() });
    const body = await res.json().catch(() => ({}));
    if (!res.ok) throw new Error(body.message || `Lỗi ${res.status} khi tải tổng quan.`);
    setSummary(body);
  }, []);

  const loadReceivables = useCallback(async () => {
    const res = await fetch(`${API_BASE_URL}/api/v1/admin/finance/receivables?page=${recPage}&size=15`, { headers: authHeaders() });
    const body = await res.json().catch(() => ({}));
    if (!res.ok) throw new Error(body.message || `Lỗi ${res.status} khi tải công nợ.`);
    setRecData(body);
  }, [recPage]);

  const loadPayments = useCallback(async () => {
    const qs = new URLSearchParams({ page: String(payPage), size: '15' });
    if (payFilter.trim()) qs.set('trangThai', payFilter.trim());
    const res = await fetch(`${API_BASE_URL}/api/v1/admin/finance/payments?${qs}`, { headers: authHeaders() });
    const body = await res.json().catch(() => ({}));
    if (!res.ok) throw new Error(body.message || `Lỗi ${res.status} khi tải giao dịch thanh toán.`);
    setPayData(body);
  }, [payPage, payFilter]);

  const loadWalletTx = useCallback(async () => {
    const res = await fetch(`${API_BASE_URL}/api/v1/admin/finance/wallet-transactions?page=${wxPage}&size=10`, {
      headers: authHeaders()
    });
    const body = await res.json().catch(() => ({}));
    if (!res.ok) throw new Error(body.message || `Lỗi ${res.status} khi tải sổ ví.`);
    setWxData(body);
  }, [wxPage]);

  const refreshAll = useCallback(async () => {
    setLoading(true);
    setErr('');
    try {
      await Promise.all([loadSummary(), loadReceivables(), loadPayments(), loadWalletTx()]);
    } catch (e) {
      setErr(e.message || 'Lỗi tải dữ liệu (cần đăng nhập admin).');
    } finally {
      setLoading(false);
    }
  }, [loadSummary, loadReceivables, loadPayments, loadWalletTx]);

  useEffect(() => {
    loadSummary().catch((e) => setErr(e.message));
  }, [loadSummary]);

  useEffect(() => {
    loadReceivables().catch((e) => setErr(e.message));
  }, [loadReceivables]);

  useEffect(() => {
    loadPayments().catch((e) => setErr(e.message));
  }, [loadPayments]);

  useEffect(() => {
    loadWalletTx().catch((e) => setErr(e.message));
  }, [loadWalletTx]);

  const recRows = recData?.content || [];
  const payRows = payData?.content || [];
  const wxRows = wxData?.content || [];

  return (
    <main className="px-8 pb-12">
      <div className="flex flex-col lg:flex-row lg:items-end justify-between gap-6 mb-12">
        <div>
          <nav className="flex items-center gap-2 text-xs font-semibold uppercase tracking-widest text-on-surface-variant mb-4">
            <span>Tài chính</span>
            <span className="material-symbols-outlined text-[14px]">chevron_right</span>
            <span className="text-primary">Giám sát (Task 14)</span>
          </nav>
          <h1 className="text-5xl font-extrabold font-headline tracking-tight text-on-surface">Quản lý Công nợ &amp; Tài chính</h1>
          <p className="mt-2 text-on-surface-variant max-w-2xl font-body">
            Dữ liệu thật từ <code className="text-xs bg-surface-container-high px-1 rounded">GET /api/v1/admin/finance/*</code> (đăng ký học phí, ví, giao dịch thanh toán, ghi có ví).
          </p>
        </div>
        <div className="flex flex-wrap gap-3">
          <button
            type="button"
            onClick={() => downloadCsv(recRows, `cong-no-trang-${recPage + 1}.csv`)}
            className="flex items-center gap-2 px-6 py-3 border border-outline-variant hover:bg-surface-container-high transition-colors rounded-full font-semibold text-sm"
          >
            <span className="material-symbols-outlined text-xl">upload_file</span> Xuất CSV (trang hiện tại)
          </button>
          <button
            type="button"
            onClick={refreshAll}
            className="jewel-gradient flex items-center gap-2 px-8 py-3 text-white rounded-full font-bold text-sm shadow-lg hover:opacity-90 transition-all"
          >
            <span className="material-symbols-outlined text-xl">refresh</span>
            Làm mới
          </button>
        </div>
      </div>

      {err && <div className="mb-6 rounded-xl border border-error/30 bg-error-container/20 px-4 py-3 text-sm text-error">{err}</div>}
      {loading && <p className="text-sm text-on-surface-variant mb-6">Đang tải…</p>}

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-12">
        <div className="bg-surface-container-lowest p-8 rounded-xl editorial-shadow border-l-4 border-primary">
          <p className="label-sm font-bold uppercase tracking-widest text-on-surface-variant mb-2">Tổng nợ ước tính (HP − ví)</p>
          <div className="flex items-end gap-2">
            <span className="text-3xl font-black text-on-surface">{formatVnd(summary?.tongNoHocPhiUocTinh)}</span>
          </div>
          <p className="text-xs text-on-surface-variant mt-2">{summary?.soSinhVienConNo ?? '—'} SV còn dư nợ</p>
        </div>
        <div className="bg-surface-container-lowest p-8 rounded-xl editorial-shadow border-l-4 border-secondary">
          <p className="label-sm font-bold uppercase tracking-widest text-on-surface-variant mb-2">Tổng số dư ví</p>
          <div className="flex items-end gap-2">
            <span className="text-3xl font-black text-on-surface">{formatVnd(summary?.tongSoDuViTatCa)}</span>
          </div>
          <p className="text-xs text-on-surface-variant mt-2">{summary?.tongSoSinhVien ?? '—'} hồ sơ sinh viên</p>
        </div>
        <div className="bg-surface-container-lowest p-8 rounded-xl editorial-shadow border-l-4 border-tertiary">
          <p className="label-sm font-bold uppercase tracking-widest text-on-surface-variant mb-2">GD thanh toán</p>
          <div className="text-sm text-on-surface-variant space-y-1">
            <p>
              Thành công: <strong className="text-on-surface">{summary?.soGiaoDichThanhCong ?? 0}</strong> —{' '}
              {formatVnd(summary?.tongSoTienGiaoDichThanhCong)}
            </p>
            <p>
              Chờ: <strong>{summary?.soGiaoDichChoThanhToan ?? 0}</strong> — {formatVnd(summary?.tongSoTienGiaoDichChoThanhToan)}
            </p>
            <p>
              Ghi ví: <strong>{summary?.soGiaoDichViGhiNhan ?? 0}</strong> dòng
            </p>
          </div>
        </div>
      </div>

      <div className="bg-surface-container-lowest rounded-xl editorial-shadow overflow-hidden mb-10">
        <div className="p-6 bg-surface-container-low flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div className="flex items-center gap-4">
            <h3 className="font-headline font-bold text-lg text-on-surface">Sổ cái Phải thu (theo SV)</h3>
            <div className="h-4 w-px bg-outline-variant" />
            <span className="text-sm font-medium text-on-surface-variant">
              Trang {recData != null ? recData.number + 1 : 1} / {recData?.totalPages || 1} — {recData?.totalElements ?? 0} bản ghi
            </span>
          </div>
          <div className="flex items-center gap-2">
            <button
              type="button"
              disabled={!recData || recData.first}
              onClick={() => setRecPage((p) => Math.max(0, p - 1))}
              className="px-3 py-2 rounded-full border text-sm disabled:opacity-40"
            >
              Trước
            </button>
            <button
              type="button"
              disabled={!recData || recData.last}
              onClick={() => setRecPage((p) => p + 1)}
              className="px-3 py-2 rounded-full border text-sm disabled:opacity-40"
            >
              Sau
            </button>
          </div>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-surface-container-low/50">
                <th className="px-6 py-4 text-[11px] font-bold uppercase tracking-widest text-on-surface-variant">Mã SV</th>
                <th className="px-6 py-4 text-[11px] font-bold uppercase tracking-widest text-on-surface-variant">Họ tên</th>
                <th className="px-6 py-4 text-[11px] font-bold uppercase tracking-widest text-on-surface-variant">Lớp</th>
                <th className="px-6 py-4 text-[11px] font-bold uppercase tracking-widest text-on-surface-variant">Số dư ví</th>
                <th className="px-6 py-4 text-[11px] font-bold uppercase tracking-widest text-on-surface-variant">HP đăng ký</th>
                <th className="px-6 py-4 text-[11px] font-bold uppercase tracking-widest text-on-surface-variant">Còn nợ ước tính</th>
                <th className="px-6 py-4 text-[11px] font-bold uppercase tracking-widest text-on-surface-variant">Trạng thái</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-surface-container">
              {recRows.map((r) => (
                <tr key={r.idSinhVien} className="hover:bg-surface-container-low transition-colors">
                  <td className="px-6 py-4 font-mono text-sm font-semibold text-primary">{r.maSinhVien}</td>
                  <td className="px-6 py-4 font-semibold text-on-surface">{r.hoTen}</td>
                  <td className="px-6 py-4 text-sm text-on-surface-variant">{r.maLop || '—'}</td>
                  <td className="px-6 py-4 text-sm font-medium text-on-surface">{formatVnd(r.soDuVi)}</td>
                  <td className="px-6 py-4 text-sm font-medium text-on-surface">{formatVnd(r.tongHocPhiDangKy)}</td>
                  <td className={`px-6 py-4 text-sm font-bold ${r.coNo ? 'text-error' : 'text-on-surface'}`}>{formatVnd(r.conNoUocTinh)}</td>
                  <td className="px-6 py-4">
                    <span
                      className={
                        r.coNo
                          ? 'px-3 py-1 bg-error-container text-on-error-container rounded-full text-[10px] font-bold uppercase tracking-wider'
                          : 'px-3 py-1 bg-primary-fixed text-on-primary-fixed rounded-full text-[10px] font-bold uppercase tracking-wider'
                      }
                    >
                      {r.coNo ? 'Còn nợ' : 'Đủ / dư'}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      <div className="bg-surface-container-lowest rounded-xl editorial-shadow overflow-hidden mb-10">
        <div className="p-6 bg-surface-container-low flex flex-col md:flex-row md:items-center justify-between gap-4">
          <h3 className="font-headline font-bold text-lg text-on-surface">Giao dịch thanh toán (QR / cổng)</h3>
          <div className="flex items-center gap-2">
            <select
              className="pl-4 pr-8 py-2 bg-white rounded-full border border-outline-variant text-sm font-medium"
              value={payFilter}
              onChange={(e) => {
                setPayFilter(e.target.value);
                setPayPage(0);
              }}
            >
              <option value="">Tất cả trạng thái</option>
              <option value="CHO_THANH_TOAN">CHO_THANH_TOAN</option>
              <option value="THANH_CONG">THANH_CONG</option>
              <option value="THAT_BAI">THAT_BAI</option>
              <option value="HUY">HUY</option>
            </select>
            <button
              type="button"
              disabled={!payData || payData.first}
              onClick={() => setPayPage((p) => Math.max(0, p - 1))}
              className="px-3 py-2 rounded-full border text-sm disabled:opacity-40"
            >
              Trước
            </button>
            <button
              type="button"
              disabled={!payData || payData.last}
              onClick={() => setPayPage((p) => p + 1)}
              className="px-3 py-2 rounded-full border text-sm disabled:opacity-40"
            >
              Sau
            </button>
          </div>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse text-sm">
            <thead>
              <tr className="bg-surface-container-low/50">
                <th className="px-4 py-3 text-[10px] font-bold uppercase text-on-surface-variant">Thời gian</th>
                <th className="px-4 py-3 text-[10px] font-bold uppercase text-on-surface-variant">SV</th>
                <th className="px-4 py-3 text-[10px] font-bold uppercase text-on-surface-variant">Số tiền</th>
                <th className="px-4 py-3 text-[10px] font-bold uppercase text-on-surface-variant">Provider</th>
                <th className="px-4 py-3 text-[10px] font-bold uppercase text-on-surface-variant">Trạng thái</th>
                <th className="px-4 py-3 text-[10px] font-bold uppercase text-on-surface-variant">Mã đơn</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-surface-container">
              {payRows.map((r) => (
                <tr key={r.idGiaoDich}>
                  <td className="px-4 py-3 whitespace-nowrap">{r.taoLuc ? new Date(r.taoLuc).toLocaleString('vi-VN') : '—'}</td>
                  <td className="px-4 py-3">
                    <div className="font-medium">{r.hoTenSinhVien}</div>
                    <div className="text-xs text-on-surface-variant font-mono">{r.maSinhVien}</div>
                  </td>
                  <td className="px-4 py-3 font-semibold">{formatVnd(r.soTien)}</td>
                  <td className="px-4 py-3">{r.provider}</td>
                  <td className="px-4 py-3">{r.trangThai}</td>
                  <td className="px-4 py-3 font-mono text-xs break-all max-w-[140px]">{r.maDonHang}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      <div className="bg-surface-container-lowest rounded-xl editorial-shadow overflow-hidden mb-10">
        <div className="p-6 bg-surface-container-low flex justify-between items-center gap-4">
          <h3 className="font-headline font-bold text-lg text-on-surface">Giao dịch ví (ghi có)</h3>
          <div className="flex gap-2">
            <button
              type="button"
              disabled={!wxData || wxData.first}
              onClick={() => setWxPage((p) => Math.max(0, p - 1))}
              className="px-3 py-2 rounded-full border text-sm disabled:opacity-40"
            >
              Trước
            </button>
            <button
              type="button"
              disabled={!wxData || wxData.last}
              onClick={() => setWxPage((p) => p + 1)}
              className="px-3 py-2 rounded-full border text-sm disabled:opacity-40"
            >
              Sau
            </button>
          </div>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse text-sm">
            <thead>
              <tr className="bg-surface-container-low/50">
                <th className="px-4 py-3 text-[10px] font-bold uppercase text-on-surface-variant">Thời gian</th>
                <th className="px-4 py-3 text-[10px] font-bold uppercase text-on-surface-variant">SV</th>
                <th className="px-4 py-3 text-[10px] font-bold uppercase text-on-surface-variant">Loại</th>
                <th className="px-4 py-3 text-[10px] font-bold uppercase text-on-surface-variant">Số tiền</th>
                <th className="px-4 py-3 text-[10px] font-bold uppercase text-on-surface-variant">Số dư sau</th>
                <th className="px-4 py-3 text-[10px] font-bold uppercase text-on-surface-variant">Mã đơn TT</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-surface-container">
              {wxRows.map((r) => (
                <tr key={r.idGiaoDichVi}>
                  <td className="px-4 py-3 whitespace-nowrap">{r.thoiGian ? new Date(r.thoiGian).toLocaleString('vi-VN') : '—'}</td>
                  <td className="px-4 py-3">
                    <div className="font-medium">{r.hoTenSinhVien}</div>
                    <div className="text-xs font-mono text-on-surface-variant">{r.maSinhVien}</div>
                  </td>
                  <td className="px-4 py-3">{r.loai}</td>
                  <td className="px-4 py-3 font-semibold">{formatVnd(r.soTien)}</td>
                  <td className="px-4 py-3">{formatVnd(r.soDuSau)}</td>
                  <td className="px-4 py-3 font-mono text-xs break-all max-w-[120px]">{r.maDonHangThanhToan || '—'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      <div className="mt-12 grid grid-cols-1 lg:grid-cols-12 gap-8">
        <div className="lg:col-span-4 bg-primary text-white p-10 rounded-xl relative overflow-hidden flex flex-col justify-between">
          <div className="relative z-10">
            <h4 className="text-2xl font-bold font-headline mb-4">Chỉ số tổng quan</h4>
            <p className="text-primary-fixed-dim text-sm leading-relaxed mb-8">
              Công nợ ước tính = tổng học phí các lớp đang đăng ký hiệu lực trừ số dư ví. Thanh toán thành công đồng bộ ghi có ví (idempotent theo mã giao dịch).
            </p>
          </div>
          <div className="relative z-10 flex items-center gap-2 text-sm opacity-90">
            <span className="material-symbols-outlined" style={{ fontVariationSettings: '"FILL" 1' }}>
              analytics
            </span>
            Task 14 — Admin finance read APIs
          </div>
          <div className="absolute -bottom-10 -right-10 w-40 h-40 bg-primary-container rounded-full opacity-50 blur-3xl" />
        </div>
        <div className="lg:col-span-8 bg-surface-container-highest p-8 rounded-xl flex flex-col gap-6">
          <div>
            <h4 className="text-xl font-bold font-headline text-on-surface mb-2">Thao tác thủ công</h4>
            <p className="text-on-surface-variant text-sm">
              Khóa thi hàng loạt và bù trừ thủ công chưa có API — chỉ hiển thị số liệu giám sát. Có thể mở rộng sau với workflow duyệt.
            </p>
          </div>
          <div className="flex flex-wrap gap-3">
            <button type="button" disabled className="bg-error/40 text-white px-6 py-2 rounded-full font-bold text-sm cursor-not-allowed">
              Khóa thi hàng loạt (n/a)
            </button>
            <button type="button" disabled className="border border-outline-variant px-6 py-2 rounded-full font-bold text-sm cursor-not-allowed">
              Bù trừ thủ công (n/a)
            </button>
          </div>
        </div>
      </div>
    </main>
  );
};

export default GimStTiChnhKTonAdmin;
