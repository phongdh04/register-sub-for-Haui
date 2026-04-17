import React, { useCallback, useEffect, useState } from 'react';

import { API_BASE_URL } from '../config/api';

const formatVnd = (n) => {
  if (n == null || n === '') return '—';
  const x = Number(n);
  if (!Number.isFinite(x)) return '—';
  return new Intl.NumberFormat('vi-VN').format(x) + ' ₫';
};

const TnhNngTrcGiGPreRegistrationGiLp = () => {
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [hocKyFilter, setHocKyFilter] = useState('');
  const [idLopHp, setIdLopHp] = useState('');
  const [hocKyAdd, setHocKyAdd] = useState('');
  const [addBusy, setAddBusy] = useState(false);
  const [addMsg, setAddMsg] = useState('');

  const fetchCart = useCallback(async (hocKyId) => {
    const token = localStorage.getItem('jwt_token');
    if (!token) {
      throw new Error('Vui lòng đăng nhập tài khoản sinh viên.');
    }
    const qs = hocKyId ? `?hocKyId=${encodeURIComponent(hocKyId)}` : '';
    const res = await fetch(`${API_BASE_URL}/api/v1/pre-reg/cart/me${qs}`, {
      headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' }
    });
    const body = await res.json().catch(() => ({}));
    if (!res.ok) {
      throw new Error(body.message || 'Không tải được giỏ đăng ký trước.');
    }
    return body;
  }, []);

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const hk = hocKyFilter.trim() || null;
      const data = await fetchCart(hk);
      setCart(data);
    } catch (e) {
      setError(e.message || 'Lỗi tải dữ liệu.');
      setCart(null);
    } finally {
      setLoading(false);
    }
  }, [fetchCart, hocKyFilter]);

  useEffect(() => {
    load();
  }, []);

  const applySemesterFilter = () => {
    load();
  };

  const addItem = async () => {
    const token = localStorage.getItem('jwt_token');
    if (!token) {
      setAddMsg('Vui lòng đăng nhập.');
      return;
    }
    const id = Number(idLopHp);
    if (!Number.isFinite(id) || id <= 0) {
      setAddMsg('Nhập id lớp học phần (số) hợp lệ.');
      return;
    }
    setAddBusy(true);
    setAddMsg('');
    try {
      const payload = { idLopHp: id };
      const hk = hocKyAdd.trim();
      if (hk) payload.hocKyId = Number(hk);
      const res = await fetch(`${API_BASE_URL}/api/v1/pre-reg/cart/items`, {
        method: 'POST',
        headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      const body = await res.json().catch(() => ({}));
      if (!res.ok) {
        throw new Error(body.message || 'Không thêm được lớp.');
      }
      setIdLopHp('');
      await load();
      setAddMsg('Đã thêm vào giỏ.');
    } catch (e) {
      setAddMsg(e.message || 'Lỗi thêm lớp.');
    } finally {
      setAddBusy(false);
    }
  };

  const deleteCartLine = async (idGioHang) => {
    const token = localStorage.getItem('jwt_token');
    if (!token) throw new Error('Chưa đăng nhập.');
    const res = await fetch(`${API_BASE_URL}/api/v1/pre-reg/cart/items/${idGioHang}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${token}` }
    });
    if (!res.ok && res.status !== 204) {
      const body = await res.json().catch(() => ({}));
      throw new Error(body.message || 'Không xóa được.');
    }
  };

  const removeItem = async (idGioHang) => {
    try {
      await deleteCartLine(idGioHang);
      await load();
    } catch (e) {
      setError(e.message || 'Lỗi xóa dòng giỏ hàng.');
    }
  };

  const clearCart = async () => {
    if (!cart?.items?.length) return;
    if (!window.confirm('Xóa toàn bộ môn trong giỏ đăng ký trước?')) return;
    try {
      for (const it of cart.items) {
        await deleteCartLine(it.idGioHang);
      }
      await load();
    } catch (e) {
      setError(e.message || 'Lỗi khi xóa giỏ hàng.');
      await load();
    }
  };

  const hkBadge = cart?.hocKyLabel || '—';
  const internalConflicts = cart?.soDoiTrungLichTrongGioHang ?? 0;
  const vsOfficial = cart?.coTrungLichVoiDangKyChinhThuc;

  return (
    <>
      <div className="flex min-h-screen">
        <main className="flex-1 flex flex-col min-w-0">
          <div className="p-8 space-y-8 overflow-y-auto">
            <section className="grid grid-cols-1 lg:grid-cols-3 gap-8 items-end">
              <div className="lg:col-span-2 space-y-4">
                <span className="inline-block py-1 px-3 bg-primary-fixed text-on-primary-fixed rounded-full text-xs font-bold uppercase tracking-wider">
                  {hkBadge}
                </span>
                <h2 className="text-4xl lg:text-5xl font-extrabold text-on-surface tracking-tight leading-tight">
                  Lên kế hoạch <span className="text-primary italic font-serif">trước giờ G.</span>
                </h2>
                <p className="text-on-surface-variant max-w-2xl leading-relaxed text-lg">
                  Giỏ đăng ký trước (bản nháp) theo học kỳ: thêm lớp theo <code className="text-xs bg-surface-container-high px-1 rounded">idLopHp</code>, kiểm tra trùng lịch trong giỏ và với đăng ký chính thức. API:{' '}
                  <code className="text-xs bg-surface-container-high px-1 rounded">GET/POST/DELETE /api/v1/pre-reg/cart/*</code>
                </p>
                <div className="flex flex-wrap gap-3 items-end pt-2">
                  <div>
                    <label className="block text-xs text-on-surface-variant mb-1">Lọc theo học kỳ (id, để trống = HK hiện hành)</label>
                    <input
                      type="text"
                      value={hocKyFilter}
                      onChange={(e) => setHocKyFilter(e.target.value)}
                      className="border border-outline-variant rounded-lg px-3 py-2 text-sm w-40 bg-surface"
                      placeholder="id học kỳ"
                    />
                  </div>
                  <button
                    type="button"
                    onClick={applySemesterFilter}
                    className="px-4 py-2 rounded-lg bg-surface-container-high text-on-surface text-sm font-semibold hover:opacity-90"
                  >
                    Áp dụng HK
                  </button>
                </div>
              </div>
              <div className="flex flex-col gap-4">
                <div className="bg-surface-container-lowest p-6 rounded-xl shadow-sm space-y-3">
                  <div className="flex items-center justify-between">
                    <span className="text-on-surface-variant font-medium">Trạng thái cổng chính thức:</span>
                    <span className="text-primary font-bold">Chưa mở (demo)</span>
                  </div>
                  <button
                    type="button"
                    disabled
                    className="w-full py-4 rounded-full bg-primary opacity-50 cursor-not-allowed text-on-primary font-bold flex items-center justify-center gap-2 transition-all"
                  >
                    <span className="material-symbols-outlined">send</span>
                    <span>Xác nhận Nộp Lưới Môn</span>
                  </button>
                  <p className="text-[10px] text-center text-on-surface-variant italic">
                    Nút này dành cho luồng đăng ký chính thức khi cổng mở; giỏ trước giờ G chỉ là bản nháp.
                  </p>
                </div>
              </div>
            </section>

            {error && (
              <div className="rounded-xl border border-error/30 bg-error-container/20 px-4 py-3 text-sm text-error font-medium">{error}</div>
            )}
            {loading && <p className="text-on-surface-variant text-sm">Đang tải giỏ hàng…</p>}

            <section className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
              <div className="bg-surface-container-lowest p-6 rounded-xl flex flex-col justify-between h-32 group hover:translate-y-[-4px] transition-transform duration-300">
                <div className="flex justify-between items-start">
                  <span className="text-xs font-bold uppercase tracking-widest text-on-surface-variant">Tổng tín chỉ</span>
                  <span className="material-symbols-outlined text-primary group-hover:scale-110 transition-transform">menu_book</span>
                </div>
                <span className="text-3xl font-black text-on-surface">{cart?.tongTinChi ?? '—'}</span>
              </div>
              <div className="bg-surface-container-lowest p-6 rounded-xl flex flex-col justify-between h-32 group hover:translate-y-[-4px] transition-transform duration-300">
                <div className="flex justify-between items-start">
                  <span className="text-xs font-bold uppercase tracking-widest text-on-surface-variant">Môn trong giỏ</span>
                  <span className="material-symbols-outlined text-secondary group-hover:scale-110 transition-transform">list_alt</span>
                </div>
                <span className="text-3xl font-black text-on-surface">{cart?.tongSoMon ?? '—'}</span>
              </div>
              <div
                className={`bg-surface-container-lowest p-6 rounded-xl flex flex-col justify-between h-32 group hover:translate-y-[-4px] transition-transform duration-300 ${
                  internalConflicts > 0 ? 'border-2 border-error/10' : ''
                }`}
              >
                <div className="flex justify-between items-start">
                  <span className={`text-xs font-bold uppercase tracking-widest ${internalConflicts > 0 ? 'text-error' : 'text-on-surface-variant'}`}>
                    Cặp trùng lịch (trong giỏ)
                  </span>
                  <span className={`material-symbols-outlined group-hover:scale-110 transition-transform ${internalConflicts > 0 ? 'text-error' : 'text-on-surface-variant'}`}>
                    event_busy
                  </span>
                </div>
                <span className={`text-3xl font-black ${internalConflicts > 0 ? 'text-error' : 'text-on-surface'}`}>{internalConflicts}</span>
              </div>
              <div className="bg-surface-container-lowest p-6 rounded-xl flex flex-col justify-between h-32 group hover:translate-y-[-4px] transition-transform duration-300">
                <div className="flex justify-between items-start">
                  <span className="text-xs font-bold uppercase tracking-widest text-on-surface-variant">Học phí ước tính</span>
                  <span className="material-symbols-outlined text-tertiary group-hover:scale-110 transition-transform">payments</span>
                </div>
                <span className="text-xl font-black text-on-surface leading-tight">{cart ? formatVnd(cart.tongHocPhi) : '—'}</span>
              </div>
            </section>

            <section className="bg-surface-container-lowest rounded-xl shadow-sm overflow-hidden border-2 border-surface-container">
              <div className="px-8 py-6 bg-surface-container-low flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between border-b border-outline-variant/20">
                <h3 className="text-xl font-bold text-on-surface">Danh sách môn đã chọn</h3>
                <div className="flex flex-col sm:flex-row flex-wrap gap-3 items-stretch sm:items-end">
                  <div className="flex gap-2 flex-wrap items-end">
                    <div>
                      <label className="block text-[10px] uppercase text-on-surface-variant mb-1">id lớp HP</label>
                      <input
                        type="number"
                        min={1}
                        value={idLopHp}
                        onChange={(e) => setIdLopHp(e.target.value)}
                        className="border border-outline-variant rounded-lg px-3 py-2 text-sm w-36 bg-surface"
                        placeholder="vd: 1"
                      />
                    </div>
                    <div>
                      <label className="block text-[10px] uppercase text-on-surface-variant mb-1">HK khi thêm (tuỳ chọn)</label>
                      <input
                        type="number"
                        min={1}
                        value={hocKyAdd}
                        onChange={(e) => setHocKyAdd(e.target.value)}
                        className="border border-outline-variant rounded-lg px-3 py-2 text-sm w-36 bg-surface"
                        placeholder="id HK"
                      />
                    </div>
                    <button
                      type="button"
                      disabled={addBusy}
                      onClick={addItem}
                      className="px-4 py-2 rounded-lg bg-primary text-on-primary text-sm font-semibold hover:opacity-90 disabled:opacity-50"
                    >
                      {addBusy ? 'Đang thêm…' : 'Thêm lớp'}
                    </button>
                  </div>
                  <div className="flex gap-2">
                    <button
                      type="button"
                      onClick={() => window.print()}
                      className="px-4 py-2 rounded-lg bg-surface-container text-on-surface-variant text-sm font-semibold hover:bg-surface-container-high transition-colors flex items-center gap-2"
                    >
                      <span className="material-symbols-outlined text-sm">print</span>
                      <span>In bản nháp</span>
                    </button>
                    <button
                      type="button"
                      onClick={load}
                      className="px-4 py-2 rounded-lg bg-surface-container text-on-surface-variant text-sm font-semibold hover:bg-surface-container-high transition-colors"
                    >
                      Làm mới
                    </button>
                  </div>
                </div>
                {addMsg && <p className="text-sm text-on-surface-variant">{addMsg}</p>}
              </div>
              <div className="overflow-x-auto">
                <table className="w-full text-left border-collapse">
                  <thead>
                    <tr className="bg-surface-container-low">
                      <th className="px-8 py-4 text-[11px] font-bold uppercase tracking-[0.1em] text-on-surface-variant/80">Mã lớp</th>
                      <th className="px-8 py-4 text-[11px] font-bold uppercase tracking-[0.1em] text-on-surface-variant/80">Tên học phần</th>
                      <th className="px-8 py-4 text-[11px] font-bold uppercase tracking-[0.1em] text-on-surface-variant/80 text-center">Tín chỉ</th>
                      <th className="px-8 py-4 text-[11px] font-bold uppercase tracking-[0.1em] text-on-surface-variant/80 text-right">Học phí</th>
                      <th className="px-8 py-4 text-[11px] font-bold uppercase tracking-[0.1em] text-on-surface-variant/80 text-right">Thao tác</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-surface-container">
                    {!loading && cart && cart.items?.length === 0 && (
                      <tr>
                        <td colSpan={5} className="px-8 py-10 text-center text-on-surface-variant text-sm">
                          Giỏ trống. Thêm lớp bằng id lớp học phần (có thể lấy từ trang tìm kiếm môn / seed dữ liệu).
                        </td>
                      </tr>
                    )}
                    {cart?.items?.map((row) => (
                      <tr key={row.idGioHang} className="hover:bg-surface-container-low transition-colors group">
                        <td className="px-8 py-5">
                          <div className="flex flex-col">
                            <span className="text-sm font-bold text-primary">{row.maLopHp || '—'}</span>
                            <span className="text-[10px] text-on-surface-variant">id: {row.idLopHp}</span>
                          </div>
                        </td>
                        <td className="px-8 py-5">
                          <span className="text-sm font-medium text-on-surface">{row.tenHocPhan || '—'}</span>
                          {row.maHocPhan && <div className="text-[10px] text-on-surface-variant mt-0.5">{row.maHocPhan}</div>}
                        </td>
                        <td className="px-8 py-5 text-center">
                          <span className="text-sm font-medium">{row.soTinChi ?? '—'}</span>
                        </td>
                        <td className="px-8 py-5 text-right font-medium text-sm">{formatVnd(row.hocPhi)}</td>
                        <td className="px-8 py-5 text-right">
                          <button
                            type="button"
                            onClick={() => removeItem(row.idGioHang)}
                            className="p-2 text-outline hover:text-error hover:bg-error-container rounded-full transition-all"
                            aria-label="Xóa khỏi giỏ"
                          >
                            <span className="material-symbols-outlined text-lg">delete</span>
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
              <div className="bg-surface-container-low px-8 py-10">
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
                  <div className="lg:col-span-2 space-y-3">
                    {internalConflicts > 0 && (
                      <div className="flex items-center gap-2 text-error font-bold">
                        <span className="material-symbols-outlined">error</span>
                        <span className="text-sm">
                          Phát hiện {internalConflicts} cặp lớp trong giỏ có tiết trùng (theo TKB JSON).
                        </span>
                      </div>
                    )}
                    {vsOfficial && (
                      <div className="flex items-center gap-2 text-secondary font-bold">
                        <span className="material-symbols-outlined">report_problem</span>
                        <span className="text-sm">Có lớp trong giỏ trùng lịch với đăng ký chính thức trong cùng học kỳ.</span>
                      </div>
                    )}
                    {!internalConflicts && !vsOfficial && cart?.items?.length > 0 && (
                      <p className="text-sm text-on-surface-variant">Không phát hiện trùng lịch trong giỏ và với đăng ký hiện tại.</p>
                    )}
                    <p className="text-on-surface-variant text-xs mt-4">
                      * Học phí hiển thị theo dữ liệu lớp học phần; mức phí chính thức có thể thay đổi khi đăng ký thật.
                    </p>
                  </div>
                  <div className="lg:col-span-2 flex flex-col items-end gap-2">
                    <div className="flex items-center gap-8 w-full max-w-md justify-between border-b border-outline-variant/30 pb-2">
                      <span className="text-on-surface-variant font-medium">Tổng số tín chỉ dự kiến:</span>
                      <span className="text-2xl font-black text-on-surface">{cart?.tongTinChi ?? 0} tín chỉ</span>
                    </div>
                    <div className="flex items-center gap-8 w-full max-w-md justify-between pt-2">
                      <span className="text-on-surface-variant font-bold text-lg">Tổng học phí dự kiến:</span>
                      <span className="text-3xl font-black text-primary">{cart ? formatVnd(cart.tongHocPhi) : '—'}</span>
                    </div>
                    <div className="mt-8 flex gap-4 flex-wrap justify-end">
                      <button
                        type="button"
                        onClick={clearCart}
                        className="px-8 py-3 rounded-full border-2 border-primary text-primary font-bold hover:bg-primary-container/10 transition-colors"
                      >
                        Hủy giỏ hàng
                      </button>
                      <button
                        type="button"
                        onClick={load}
                        className="px-10 py-3 rounded-full bg-primary text-on-primary font-bold shadow-lg shadow-primary/20 hover:scale-105 active:scale-95 transition-all"
                      >
                        Lưu / làm mới nháp
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </section>

            <section className="space-y-6">
              <h3 className="text-xl font-bold flex items-center gap-2">
                <span className="material-symbols-outlined text-secondary">tips_and_updates</span>
                Gợi ý
              </h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="bg-surface-container p-6 rounded-xl border-l-4 border-secondary space-y-3">
                  <h4 className="font-bold text-on-surface">Tránh trùng lịch</h4>
                  <p className="text-sm text-on-surface-variant">
                    Nếu số cặp trùng lịch &gt; 0, hãy xóa một trong các lớp xung đột hoặc chọn lớp khác (id lớp khác) trước khi đăng ký chính thức.
                  </p>
                </div>
                <div className="bg-surface-container p-6 rounded-xl border-l-4 border-primary space-y-3">
                  <h4 className="font-bold text-on-surface">Đồng bộ học kỳ</h4>
                  <p className="text-sm text-on-surface-variant">
                    Dùng &quot;Áp dụng HK&quot; để xem giỏ theo học kỳ cụ thể; khi thêm lớp có thể truyền thêm học kỳ nếu khác HK mặc định của hệ thống.
                  </p>
                </div>
              </div>
            </section>
          </div>
        </main>
      </div>
      <nav className="md:hidden fixed bottom-0 left-0 right-0 bg-slate-50 border-t border-slate-200 flex justify-around py-2 z-50">
        <a className="flex flex-col items-center p-2 text-slate-400" href="#">
          <span className="material-symbols-outlined">dashboard</span>
          <span className="text-[10px] font-bold">DASHBOARD</span>
        </a>
        <a className="flex flex-col items-center p-2 text-primary" href="#">
          <span className="material-symbols-outlined" style={{ fontVariationSettings: '"FILL" 1' }}>
            app_registration
          </span>
          <span className="text-[10px] font-bold">ĐĂNG KÝ</span>
        </a>
        <a className="flex flex-col items-center p-2 text-slate-400" href="#">
          <span className="material-symbols-outlined">calendar_month</span>
          <span className="text-[10px] font-bold">LỊCH</span>
        </a>
        <a className="flex flex-col items-center p-2 text-slate-400" href="#">
          <span className="material-symbols-outlined">person</span>
          <span className="text-[10px] font-bold">HỒ SƠ</span>
        </a>
      </nav>
    </>
  );
};

export default TnhNngTrcGiGPreRegistrationGiLp;
