import React, { useState } from 'react';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const ThanhTonQrCodeOpenApi = () => {
  const [soTien, setSoTien] = useState('1200000');
  const [provider, setProvider] = useState('MOCK');
  const [noiDung, setNoiDung] = useState('Thanh toan hoc phi hoc ky');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [result, setResult] = useState(null);

  const qrImageUrl = result?.qrContent && !result.qrContent.startsWith('http')
    ? `https://api.qrserver.com/v1/create-qr-code/?size=220x220&data=${encodeURIComponent(result.qrContent)}`
    : null;

  const handleCreate = async (e) => {
    e.preventDefault();
    setError('');
    setResult(null);
    const token = localStorage.getItem('jwt_token');
    if (!token) {
      setError('Vui lòng đăng nhập tài khoản sinh viên.');
      return;
    }
    const amount = Number(soTien);
    if (!Number.isFinite(amount) || amount < 1000) {
      setError('Số tiền tối thiểu 1000 VND.');
      return;
    }
    try {
      setLoading(true);
      const response = await fetch(`${API_BASE_URL}/api/v1/payments/tuition-qr`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          soTien: amount,
          provider,
          noiDung
        })
      });
      const body = await response.json().catch(() => ({}));
      if (!response.ok) {
        throw new Error(body.message || 'Không tạo được giao dịch.');
      }
      setResult(body);
    } catch (err) {
      setError(err.message || 'Lỗi kết nối.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="px-6 pb-12 min-h-screen bg-surface">
      <div className="max-w-3xl mx-auto space-y-8 pt-8">
        <div>
          <h1 className="text-3xl font-extrabold text-on-surface tracking-tight mb-2">Thanh toán học phí (QR)</h1>
          <p className="text-on-surface-variant text-sm">
            Task 9: tạo giao dịch qua backend (MOCK / VNPay / MoMo stub). Đăng nhập sinh viên để gọi API.
          </p>
        </div>

        <form onSubmit={handleCreate} className="bg-surface-container-lowest rounded-xl p-6 shadow-sm space-y-4">
          <div>
            <label className="block text-xs font-bold text-on-surface-variant uppercase mb-1" htmlFor="amount">Số tiền (VND)</label>
            <input
              id="amount"
              type="number"
              min={1000}
              step={1000}
              className="w-full rounded-lg border border-outline-variant/30 px-4 py-3 text-sm"
              value={soTien}
              onChange={(e) => setSoTien(e.target.value)}
              required
            />
          </div>
          <div>
            <label className="block text-xs font-bold text-on-surface-variant uppercase mb-1" htmlFor="provider">Cổng thanh toán</label>
            <select
              id="provider"
              className="w-full rounded-lg border border-outline-variant/30 px-4 py-3 text-sm"
              value={provider}
              onChange={(e) => setProvider(e.target.value)}
            >
              <option value="MOCK">MOCK (QR demo)</option>
              <option value="VNPAY">VNPay (sandbox, cần cấu hình)</option>
              <option value="MOMO">MoMo (deep link stub)</option>
            </select>
          </div>
          <div>
            <label className="block text-xs font-bold text-on-surface-variant uppercase mb-1" htmlFor="desc">Nội dung</label>
            <input
              id="desc"
              type="text"
              className="w-full rounded-lg border border-outline-variant/30 px-4 py-3 text-sm"
              value={noiDung}
              onChange={(e) => setNoiDung(e.target.value)}
            />
          </div>
          <button
            type="submit"
            disabled={loading}
            className="w-full py-3 rounded-full bg-primary text-white font-bold text-sm hover:opacity-90 disabled:opacity-60"
          >
            {loading ? 'Đang tạo…' : 'Tạo thanh toán / QR'}
          </button>
        </form>

        {error && (
          <div className="rounded-xl border border-error/30 bg-error-container/30 p-4 text-sm text-error">
            {error}
          </div>
        )}

        {result && (
          <section className="bg-surface-container-lowest rounded-xl p-6 shadow-sm space-y-4">
            <h2 className="text-lg font-bold text-on-surface">Giao dịch đã tạo</h2>
            <dl className="grid grid-cols-1 sm:grid-cols-2 gap-2 text-sm">
              <div><span className="text-on-surface-variant">Mã đơn:</span> <span className="font-mono font-semibold">{result.maDonHang}</span></div>
              <div><span className="text-on-surface-variant">Trạng thái:</span> {result.trangThai}</div>
              <div><span className="text-on-surface-variant">Số tiền:</span> {Number(result.soTien).toLocaleString('vi-VN')} ₫</div>
              <div><span className="text-on-surface-variant">Provider:</span> {result.provider}</div>
            </dl>
            {result.ghiChu && (
              <p className="text-xs text-on-surface-variant">{result.ghiChu}</p>
            )}
            {qrImageUrl && (
              <div className="flex flex-col items-center gap-2 pt-2">
                <p className="text-xs font-bold text-on-surface-variant uppercase">QR (từ nội dung trả về)</p>
                <img src={qrImageUrl} alt="Mã QR thanh toán" className="rounded-lg border border-outline-variant/30" width={220} height={220} />
                <p className="text-[10px] text-on-surface-variant break-all max-w-full">{result.qrContent}</p>
              </div>
            )}
            {result.redirectUrl && (
              <a
                href={result.redirectUrl}
                target="_blank"
                rel="noreferrer"
                className="inline-flex items-center justify-center w-full py-3 rounded-full bg-secondary text-white font-bold text-sm"
              >
                Mở liên kết thanh toán
              </a>
            )}
          </section>
        )}
      </div>
    </main>
  );
};

export default ThanhTonQrCodeOpenApi;
