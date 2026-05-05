import React, { useCallback, useEffect, useMemo, useState } from 'react';

import { API_BASE_URL, authHeaders } from '../config/api';

const formatVnd = (n) => {
  if (n == null || n === '') return '—';
  const x = Number(n);
  if (!Number.isFinite(x)) return '—';
  return new Intl.NumberFormat('vi-VN').format(x) + ' ₫';
};

const formatIso = (iso) => {
  if (!iso) return '—';
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return iso;
  return d.toLocaleString('vi-VN');
};

const safeArray = (x) => (Array.isArray(x) ? x : []);

const firstSlotLabel = (slots) => {
  const data = safeArray(slots);
  if (!data.length) return 'Chưa có TKB';
  const s = data[0] || {};
  const thu = s.thu || '?';
  const tietBd = s.tiet_bat_dau || s.tietBatDau || '?';
  const tietKt = s.tiet_ket_thuc || s.tietKetThuc || '?';
  return `Thứ ${thu} (${tietBd}-${tietKt})`;
};

const TnhNngTrcGiGPreRegistrationGiLp = () => {
  const [hocKys, setHocKys] = useState([]);
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [search, setSearch] = useState('');
  const [hocKyFilter, setHocKyFilter] = useState('');
  const [addBusyId, setAddBusyId] = useState(null);
  const [msg, setMsg] = useState('');
  const [suggest, setSuggest] = useState(null);
  const [suggestLoading, setSuggestLoading] = useState(false);
  const [suggestErr, setSuggestErr] = useState('');
  const [blockModalOpen, setBlockModalOpen] = useState(false);
  const [blockIdInput, setBlockIdInput] = useState('');
  const [blockBusy, setBlockBusy] = useState(false);

  const fetchCart = useCallback(async (hocKyId) => {
    const qs = hocKyId ? `?hocKyId=${encodeURIComponent(hocKyId)}` : '';
    const res = await fetch(`${API_BASE_URL}/api/v1/pre-reg/cart/me${qs}`, {
      headers: authHeaders()
    });
    const body = await res.json().catch(() => ({}));
    if (!res.ok) {
      throw new Error(body.message || 'Không tải được giỏ đăng ký trước.');
    }
    return body;
  }, []);

  const loadHocKy = useCallback(async () => {
    const res = await fetch(`${API_BASE_URL}/api/hoc-ky`, { headers: authHeaders() });
    const body = await res.json().catch(() => []);
    if (!res.ok) throw new Error(body.message || 'Không tải được danh sách học kỳ.');
    const rows = safeArray(body);
    setHocKys(rows);
    if (!hocKyFilter && rows.length > 0) {
      setHocKyFilter(String(rows[0].idHocKy));
    }
  }, [hocKyFilter]);

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    setMsg('');
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
    (async () => {
      try {
        await loadHocKy();
      } catch (e) {
        setError(e.message || 'Lỗi tải học kỳ.');
      }
    })();
  }, [loadHocKy]);

  useEffect(() => {
    load();
  }, [load]);

  const addItemById = async (idLopHp) => {
    const id = Number(idLopHp);
    if (!Number.isFinite(id) || id <= 0) return;
    setAddBusyId(id);
    setMsg('');
    setError('');
    try {
      const payload = { idLopHp: id };
      const hk = hocKyFilter.trim();
      if (hk) payload.hocKyId = Number(hk);
      const res = await fetch(`${API_BASE_URL}/api/v1/pre-reg/cart/items`, {
        method: 'POST',
        headers: authHeaders(),
        body: JSON.stringify(payload)
      });
      const body = await res.json().catch(() => ({}));
      if (!res.ok) {
        throw new Error(body.message || 'Không thêm được lớp.');
      }
      await load();
      setMsg('Đã thêm học phần vào giỏ.');
    } catch (e) {
      setError(e.message || 'Lỗi thêm lớp.');
    } finally {
      setAddBusyId(null);
    }
  };

  const deleteCartLine = async (idGioHang) => {
    const res = await fetch(`${API_BASE_URL}/api/v1/pre-reg/cart/items/${idGioHang}`, {
      method: 'DELETE',
      headers: authHeaders()
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

  const loadSuggest = useCallback(async () => {
    const hkId = hocKyFilter.trim() || (cart?.idHocKy != null ? String(cart.idHocKy) : '');
    if (!hkId) return;
    setSuggestLoading(true);
    setSuggestErr('');
    try {
      const res = await fetch(
        `${API_BASE_URL}/api/v1/registration/suggestions/me?hocKyId=${encodeURIComponent(hkId)}`,
        { headers: authHeaders() }
      );
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || 'Không tải gợi ý môn.');
      setSuggest(body);
    } catch (e) {
      setSuggestErr(e.message || 'Lỗi gợi ý.');
      setSuggest(null);
    } finally {
      setSuggestLoading(false);
    }
  }, [cart?.idHocKy, hocKyFilter]);

  useEffect(() => {
    if (!loading && cart?.idHocKy) {
      loadSuggest();
    }
  }, [loading, cart?.idHocKy, hocKyFilter, loadSuggest]);

  const hkBadge = cart?.hocKyLabel || '—';
  const internalConflicts = cart?.soDoiTrungLichTrongGioHang ?? 0;
  const vsOfficial = cart?.coTrungLichVoiDangKyChinhThuc;
  const preOpen = cart != null && cart.preDangKyDangMo !== false;
  const canAddToCart = preOpen;

  const filteredSuggest = useMemo(() => {
    const rows = safeArray(suggest?.lopDeXuat);
    const kw = search.trim().toLowerCase();
    if (!kw) return rows;
    return rows.filter((r) => {
      const blob = [r.maLopHp, r.tenHocPhan, r.maHocPhan, r.tenGiangVien].join(' ').toLowerCase();
      return blob.includes(kw);
    });
  }, [suggest, search]);

  const addBlock = async () => {
    const blockId = Number(blockIdInput);
    if (!Number.isFinite(blockId) || blockId <= 0) {
      setError('Nhập id block hợp lệ.');
      return;
    }
    setBlockBusy(true);
    setError('');
    setMsg('');
    try {
      const payload = { idTkbBlock: blockId };
      if (hocKyFilter) payload.hocKyId = Number(hocKyFilter);
      const res = await fetch(`${API_BASE_URL}/api/v1/pre-reg/cart/blocks`, {
        method: 'POST',
        headers: authHeaders(),
        body: JSON.stringify(payload)
      });
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || 'Không thêm được block.');
      setBlockModalOpen(false);
      setBlockIdInput('');
      setMsg('Đã thêm block vào giỏ.');
      await load();
    } catch (e) {
      setError(e.message || 'Lỗi thêm block.');
    } finally {
      setBlockBusy(false);
    }
  };

  return (
    <main className="min-h-screen bg-[#f9f9ff] p-8 lg:p-12">
      <header className="mb-10">
        <h2 className="text-3xl font-extrabold text-on-surface tracking-tight">Đăng ký học phần sơ bộ</h2>
        <p className="text-on-surface-variant font-medium mt-1">{hkBadge}</p>
      </header>

      {error && <div className="mb-4 rounded-lg bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-800">{error}</div>}
      {msg && <div className="mb-4 rounded-lg bg-emerald-50 border border-emerald-200 px-4 py-3 text-sm text-emerald-800">{msg}</div>}

      <div className="grid grid-cols-12 gap-8">
        <section className="col-span-12 lg:col-span-8 space-y-6">
          <div className="bg-white rounded-xl p-6 shadow-sm space-y-5">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="space-y-2">
                <label className="text-xs font-bold uppercase tracking-wider text-on-surface-variant">Học kỳ</label>
                <select
                  className="w-full bg-surface-container-low border-none rounded-lg focus:ring-2 focus:ring-primary text-sm py-3 px-4"
                  value={hocKyFilter}
                  onChange={(e) => setHocKyFilter(e.target.value)}
                >
                  {hocKys.map((h) => (
                    <option key={h.idHocKy} value={h.idHocKy}>
                      HK{h.kyThu} ({h.namHoc})
                    </option>
                  ))}
                </select>
              </div>
              <div className="md:col-span-2 space-y-2">
                <label className="text-xs font-bold uppercase tracking-wider text-on-surface-variant">Tìm kiếm học phần</label>
                <input
                  className="w-full bg-surface-container-low border-none rounded-lg focus:ring-2 focus:ring-primary text-sm py-3 px-4"
                  placeholder="Nhập mã lớp, tên môn học hoặc giảng viên..."
                  value={search}
                  onChange={(e) => setSearch(e.target.value)}
                />
              </div>
            </div>
            <div className="flex gap-3">
              <button type="button" onClick={loadSuggest} className="px-4 py-2 rounded-full bg-primary text-white text-sm font-bold">
                Tải danh sách gợi ý
              </button>
              <button type="button" onClick={() => setBlockModalOpen(true)} className="px-4 py-2 rounded-full bg-surface-container text-on-surface text-sm font-semibold">
                Thêm theo Block
              </button>
            </div>
            {!preOpen && (
              <p className="text-xs text-amber-800 bg-amber-50 border border-amber-200 rounded-lg px-3 py-2">
                Ngoài phiên pre-registration do admin cấu hình, thao tác thêm vào giỏ sẽ bị chặn.
              </p>
            )}
          </div>

          <div className="space-y-4">
            <h3 className="text-sm font-bold text-on-surface-variant uppercase tracking-widest px-1">
              Kết quả tìm kiếm ({filteredSuggest.length})
            </h3>
            {suggestLoading && <p className="text-sm text-on-surface-variant">Đang tải dữ liệu môn mở...</p>}
            {suggestErr && <p className="text-sm text-red-700 bg-red-50 border border-red-200 rounded-lg px-3 py-2">{suggestErr}</p>}
            {!suggestLoading &&
              filteredSuggest.map((row) => (
                <div key={row.idLopHp} className="bg-white rounded-xl p-6 shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-5">
                  <div className="flex-1 space-y-2">
                    <div className="flex items-center gap-3">
                      <span className="bg-primary-fixed text-on-primary-fixed text-[10px] font-bold px-2 py-0.5 rounded uppercase">
                        {row.maLopHp}
                      </span>
                      <h4 className="text-lg font-bold text-on-surface">{row.tenHocPhan}</h4>
                    </div>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-y-2 text-sm text-on-surface-variant">
                      <div>{row.tenGiangVien || 'Chưa gán GV'}</div>
                      <div>{firstSlotLabel(row.thoiKhoaBieuJson)}</div>
                      <div>{row.soTinChi ?? '—'} tín chỉ</div>
                    </div>
                  </div>
                  <div className="flex gap-3">
                    <button
                      type="button"
                      disabled={!canAddToCart || addBusyId === row.idLopHp}
                      onClick={() => addItemById(row.idLopHp)}
                      className="px-4 py-2 rounded-full border-2 border-primary text-primary font-bold text-sm disabled:opacity-50"
                    >
                      {addBusyId === row.idLopHp ? 'Đang thêm...' : 'Thêm vào giỏ'}
                    </button>
                    <button
                      type="button"
                      onClick={() => setBlockModalOpen(true)}
                      className="px-4 py-2 rounded-full bg-primary text-white font-bold text-sm"
                    >
                      Thêm theo Block
                    </button>
                  </div>
                </div>
              ))}
            {!suggestLoading && filteredSuggest.length === 0 && (
              <div className="bg-white border border-surface-container rounded-xl p-6 text-sm text-on-surface-variant">
                Không có học phần phù hợp bộ lọc hiện tại.
              </div>
            )}
          </div>
        </section>

        <aside className="col-span-12 lg:col-span-4">
          <div className="bg-white rounded-xl shadow-sm overflow-hidden sticky top-8">
            <div className="bg-primary-container p-6">
              <h3 className="text-xl font-bold text-on-primary">Giỏ đăng ký của tôi</h3>
              <p className="text-blue-200 text-xs mt-1">
                Đã chọn {cart?.tongSoMon ?? 0} học phần - {cart?.tongTinChi ?? 0} tín chỉ
              </p>
            </div>
            <div className="p-4 space-y-4 max-h-[60vh] overflow-y-auto">
              {loading && <p className="text-sm text-on-surface-variant">Đang tải giỏ...</p>}
              {!loading && safeArray(cart?.items).length === 0 && (
                <p className="text-sm text-on-surface-variant">Giỏ đang trống.</p>
              )}
              {safeArray(cart?.items).map((it) => (
                <div key={it.idGioHang} className="flex justify-between items-start border-b border-surface-container pb-4">
                  <div>
                    <p className="text-[10px] font-bold text-primary">{it.maLopHp}</p>
                    <h5 className="font-bold text-sm">{it.tenHocPhan}</h5>
                    <p className="text-xs text-on-surface-variant">{it.soTinChi ?? '—'} TC</p>
                  </div>
                  <button type="button" onClick={() => removeItem(it.idGioHang)} className="text-outline hover:text-error p-1">
                    <span className="material-symbols-outlined text-lg">delete</span>
                  </button>
                </div>
              ))}
            </div>
            <div className="p-6 bg-surface-container-low border-t border-surface-container mt-auto">
              {(internalConflicts > 0 || vsOfficial) && (
                <div className="mb-3 text-xs text-red-700 bg-red-50 border border-red-200 rounded-lg px-3 py-2">
                  {internalConflicts > 0 ? `Có ${internalConflicts} cặp trùng lịch trong giỏ. ` : ''}
                  {vsOfficial ? 'Có lớp trùng lịch với đăng ký chính thức.' : ''}
                </div>
              )}
              <button type="button" onClick={load} className="w-full bg-primary text-white py-3 rounded-full font-bold">
                Lưu / làm mới nháp
              </button>
              <p className="text-[10px] text-center text-on-surface-variant mt-3 italic">
                Đây là giỏ sơ bộ (pre-registration), chưa phải đăng ký chính thức.
              </p>
              <p className="text-[10px] text-center text-on-surface-variant mt-1">
                Khung giờ pre-reg: {formatIso(cart?.preDangKyMoTu)} - {formatIso(cart?.preDangKyMoDen)}
              </p>
            </div>
          </div>
        </aside>
      </div>

      {blockModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-on-surface/40 backdrop-blur-sm">
          <div className="bg-white w-full max-w-xl rounded-2xl shadow-xl overflow-hidden">
            <div className="p-6 border-b border-surface-container flex justify-between items-center">
              <h3 className="text-xl font-extrabold text-on-surface">Chọn Block TKB</h3>
              <button type="button" onClick={() => setBlockModalOpen(false)} className="p-2 hover:bg-surface-container rounded-full">
                <span className="material-symbols-outlined">close</span>
              </button>
            </div>
            <div className="p-6 space-y-3">
              <p className="text-sm text-on-surface-variant">
                Backend hiện dùng API thêm block theo <code className="text-xs bg-surface-container px-1 rounded">idTkbBlock</code>.
              </p>
              <label className="text-xs font-semibold text-on-surface-variant uppercase block">
                ID Block
                <input
                  type="number"
                  min={1}
                  value={blockIdInput}
                  onChange={(e) => setBlockIdInput(e.target.value)}
                  className="mt-1 w-full border border-outline-variant rounded-lg px-3 py-2 text-sm"
                  placeholder="Nhập id block..."
                />
              </label>
            </div>
            <div className="p-6 bg-surface-container-low flex justify-end gap-3 border-t border-surface-container">
              <button type="button" onClick={() => setBlockModalOpen(false)} className="px-6 py-2 rounded-full text-on-surface-variant font-bold">
                Hủy
              </button>
              <button
                type="button"
                onClick={addBlock}
                disabled={blockBusy}
                className="px-6 py-2 rounded-full bg-primary text-white font-bold disabled:opacity-50"
              >
                {blockBusy ? 'Đang thêm...' : 'Thêm block vào giỏ'}
              </button>
            </div>
          </div>
        </div>
      )}
    </main>
  );
};

export default TnhNngTrcGiGPreRegistrationGiLp;
