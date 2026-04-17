import React, { useCallback, useEffect, useMemo, useState } from 'react';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const TT_LABEL = {
  CHO_GV_XU_LY: 'Chờ xử lý',
  DONG_Y: 'Đã đồng ý',
  TU_CHOI: 'Từ chối'
};

const formatDt = (iso) => {
  if (!iso) return '—';
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return iso;
  return d.toLocaleString('vi-VN');
};

const GcThiXLKhiuNiPhcKho = () => {
  const token = typeof localStorage !== 'undefined' ? localStorage.getItem('jwt_token') : null;
  const [filter, setFilter] = useState('CHO_GV_XU_LY');
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedId, setSelectedId] = useState(null);
  const [quyetDinh, setQuyetDinh] = useState('DONG_Y');
  const [diemSau, setDiemSau] = useState('');
  const [ghiChu, setGhiChu] = useState('');
  const [busy, setBusy] = useState(false);
  const [q, setQ] = useState('');

  const load = useCallback(async () => {
    if (!token) {
      setError('Chưa đăng nhập. Dùng tài khoản giảng viên (gv01) từ All Portal.');
      setRows([]);
      setLoading(false);
      return;
    }
    setLoading(true);
    setError('');
    try {
      const qs = filter === 'ALL' ? '' : `?trangThai=${encodeURIComponent(filter)}`;
      const res = await fetch(`${API_BASE_URL}/api/v1/lecturer/retake-appeals${qs}`, {
        headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' }
      });
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || 'Không tải danh sách phúc khảo.');
      setRows(Array.isArray(body) ? body : []);
    } catch (e) {
      setError(e.message || 'Lỗi tải dữ liệu.');
      setRows([]);
    } finally {
      setLoading(false);
    }
  }, [token, filter]);

  useEffect(() => {
    load();
  }, [load]);

  const selected = useMemo(() => rows.find((r) => r.idYeuCau === selectedId) || null, [rows, selectedId]);

  useEffect(() => {
    if (rows.length && !selectedId) {
      setSelectedId(rows[0].idYeuCau);
    }
  }, [rows, selectedId]);

  useEffect(() => {
    if (selected && selected.trangThai === 'CHO_GV_XU_LY') {
      const cur = selected.diemHe4HienTai != null ? String(selected.diemHe4HienTai) : '';
      setDiemSau(cur);
      setQuyetDinh('DONG_Y');
      setGhiChu('');
    }
  }, [selected]);

  const filteredRows = useMemo(() => {
    const t = q.trim().toLowerCase();
    if (!t) return rows;
    return rows.filter(
      (r) =>
        (r.hoTenSinhVien || '').toLowerCase().includes(t) ||
        (r.maSinhVien || '').toLowerCase().includes(t) ||
        (r.tenHocPhan || '').toLowerCase().includes(t)
    );
  }, [rows, q]);

  const initials = (name) => {
    if (!name) return '?';
    const p = name.trim().split(/\s+/);
    if (p.length === 1) return p[0].slice(0, 2).toUpperCase();
    return (p[0][0] + p[p.length - 1][0]).toUpperCase();
  };

  const submitDecision = async () => {
    if (!token || !selected || selected.trangThai !== 'CHO_GV_XU_LY') return;
    setBusy(true);
    setError('');
    try {
      const payload = { quyetDinh, ghiChuGiangVien: ghiChu.trim() || null };
      if (quyetDinh === 'DONG_Y') {
        const n = parseFloat(String(diemSau).replace(',', '.'));
        if (Number.isNaN(n)) throw new Error('Nhập điểm hệ 4 sau phúc khảo (0–4).');
        payload.diemSauPhucKhao = n;
      }
      const res = await fetch(`${API_BASE_URL}/api/v1/lecturer/retake-appeals/${selected.idYeuCau}`, {
        method: 'PATCH',
        headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || 'Không lưu được quyết định.');
      await load();
      setSelectedId(body.idYeuCau);
    } catch (e) {
      setError(e.message || 'Lỗi xử lý.');
    } finally {
      setBusy(false);
    }
  };

  return (
    <main className="min-h-screen p-8 md:p-12">
      <div className="max-w-7xl mx-auto mb-8">
        <h1 className="text-3xl md:text-4xl font-black font-headline text-on-surface tracking-tight">Gác thi &amp; phúc khảo</h1>
        <p className="mt-2 text-on-surface-variant max-w-2xl text-sm leading-relaxed">
          Xử lý yêu cầu phúc khảo điểm của sinh viên đối với các lớp học phần bạn phụ trách. Đồng ý sẽ cập nhật điểm hệ 4 và công bố lại trên bảng điểm.
        </p>
        <div className="mt-4 flex flex-wrap gap-3 items-center">
          <label className="text-xs font-bold text-on-surface-variant uppercase">Lọc trạng thái</label>
          <select
            className="px-4 py-2 rounded-full bg-surface-container-low text-sm font-bold border-none focus:ring-2 focus:ring-primary/30"
            value={filter}
            onChange={(e) => {
              setFilter(e.target.value);
              setSelectedId(null);
            }}
          >
            <option value="CHO_GV_XU_LY">Chờ xử lý</option>
            <option value="DONG_Y">Đã đồng ý</option>
            <option value="TU_CHOI">Đã từ chối</option>
            <option value="ALL">Tất cả</option>
          </select>
          <button
            type="button"
            onClick={load}
            className="px-4 py-2 rounded-full bg-primary text-white text-xs font-bold hover:opacity-90"
          >
            Làm mới
          </button>
        </div>
      </div>

      {error && (
        <div className="max-w-7xl mx-auto mb-6 rounded-xl bg-error-container text-on-error-container px-4 py-3 text-sm font-medium">
          {error}
        </div>
      )}

      <div className="max-w-7xl mx-auto grid grid-cols-12 gap-8">
        <section className="col-span-12 lg:col-span-5 flex flex-col gap-6">
          <div className="bg-surface-container-lowest rounded-xl shadow-sm overflow-hidden flex flex-col">
            <div className="p-6 border-b border-surface-container-high flex flex-col gap-3">
              <h3 className="font-headline text-lg font-bold">Danh sách yêu cầu</h3>
              <div className="relative">
                <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-outline text-sm">search</span>
                <input
                  className="pl-9 pr-4 py-2 bg-surface-container rounded-full text-xs border-none focus:ring-2 focus:ring-primary w-full"
                  placeholder="Tìm sinh viên / môn..."
                  type="text"
                  value={q}
                  onChange={(e) => setQ(e.target.value)}
                />
              </div>
            </div>
            <div className="flex-1 overflow-y-auto max-h-[640px]">
              {loading && <div className="p-6 text-sm text-on-surface-variant">Đang tải…</div>}
              {!loading && filteredRows.length === 0 && (
                <div className="p-6 text-sm text-on-surface-variant">Không có yêu cầu nào.</div>
              )}
              {!loading &&
                filteredRows.map((r) => (
                  <button
                    key={r.idYeuCau}
                    type="button"
                    onClick={() => setSelectedId(r.idYeuCau)}
                    className={`w-full text-left p-6 border-b border-surface-container-high transition-colors ${
                      selectedId === r.idYeuCau ? 'bg-primary-container/15 border-l-4 border-l-primary' : 'hover:bg-surface-container-low border-l-4 border-l-transparent'
                    }`}
                  >
                    <div className="flex justify-between items-start mb-2 gap-2">
                      <span className="text-xs font-bold text-primary px-2 py-1 bg-primary/10 rounded-full truncate max-w-[60%]">
                        {r.tenHocPhan || '—'}
                      </span>
                      <span className="text-[10px] font-bold text-on-surface-variant whitespace-nowrap">{formatDt(r.ngayTao)}</span>
                    </div>
                    <h4 className="font-headline font-bold text-on-surface">
                      {r.hoTenSinhVien} — {r.maSinhVien}
                    </h4>
                    <p className="text-xs text-on-surface-variant mt-1 font-mono">{r.maLopHp}</p>
                    <div className="flex items-center gap-3 mt-3 flex-wrap">
                      <span className="text-[10px] text-on-surface-variant font-medium">ĐIỂM HIỆN TẠI</span>
                      <span className="text-sm font-black text-on-surface">{r.diemHe4HienTai ?? '—'}</span>
                      <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-surface-container-high text-on-surface-variant">
                        {TT_LABEL[r.trangThai] || r.trangThai}
                      </span>
                    </div>
                  </button>
                ))}
            </div>
          </div>
        </section>

        <section className="col-span-12 lg:col-span-7 space-y-6">
          {!selected ? (
            <div className="bg-surface-container-lowest rounded-xl p-10 text-center text-on-surface-variant text-sm">
              Chọn một yêu cầu bên trái để xem chi tiết.
            </div>
          ) : (
            <>
              <div className="bg-surface-container-lowest rounded-xl shadow-sm p-8 flex gap-6 items-start relative overflow-hidden">
                <div className="w-24 h-24 rounded-2xl bg-primary/15 text-primary flex items-center justify-center text-2xl font-black flex-shrink-0 border-4 border-surface-container-high">
                  {initials(selected.hoTenSinhVien)}
                </div>
                <div className="flex-1 min-w-0">
                  <h3 className="text-2xl font-black text-on-surface tracking-tight truncate">{selected.hoTenSinhVien}</h3>
                  <p className="text-on-surface-variant font-medium text-sm mt-1">
                    MSSV: {selected.maSinhVien}
                    {selected.maLopHanhChinh ? ` · Lớp: ${selected.maLopHanhChinh}` : ''}
                  </p>
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mt-6 text-sm">
                    <div>
                      <p className="text-[10px] uppercase font-bold text-outline mb-1">Học phần</p>
                      <p className="font-bold text-on-surface">{selected.tenHocPhan}</p>
                      <p className="text-xs font-mono text-on-surface-variant">{selected.maHocPhan}</p>
                    </div>
                    <div>
                      <p className="text-[10px] uppercase font-bold text-outline mb-1">Học kỳ</p>
                      <p className="font-bold text-on-surface">{selected.hocKyLabel}</p>
                    </div>
                    <div>
                      <p className="text-[10px] uppercase font-bold text-outline mb-1">Lịch thi (nếu có)</p>
                      <p className="font-bold text-on-surface">
                        {selected.ngayThi || '—'} {selected.caThi ? `· ${selected.caThi}` : ''}
                      </p>
                      <p className="text-xs text-on-surface-variant">{selected.phongThi || ''}</p>
                    </div>
                    <div>
                      <p className="text-[10px] uppercase font-bold text-outline mb-1">Trạng thái yêu cầu</p>
                      <p className="font-bold text-primary">{TT_LABEL[selected.trangThai] || selected.trangThai}</p>
                      {selected.ngayXuLy && <p className="text-xs text-on-surface-variant mt-1">Xử lý: {formatDt(selected.ngayXuLy)}</p>}
                    </div>
                  </div>
                </div>
              </div>

              <div className="bg-surface-container-low rounded-xl p-6 border border-dashed border-outline-variant/50">
                <h4 className="text-xs font-bold text-on-surface-variant uppercase tracking-widest mb-3">Nội dung SV</h4>
                <p className="text-sm text-on-surface leading-relaxed whitespace-pre-wrap">{selected.lyDoSinhVien}</p>
              </div>

              {selected.trangThai !== 'CHO_GV_XU_LY' && (
                <div className="bg-surface-container-lowest rounded-xl p-6 shadow-sm text-sm space-y-2">
                  <p>
                    <span className="font-bold">Kết quả:</span> {TT_LABEL[selected.trangThai]}
                  </p>
                  {selected.diemSauXuLy != null && (
                    <p>
                      <span className="font-bold">Điểm sau xử lý:</span> {selected.diemSauXuLy}
                    </p>
                  )}
                  {selected.ghiChuGiangVien && (
                    <p className="text-on-surface-variant">
                      <span className="font-bold text-on-surface">Ghi chú GV:</span> {selected.ghiChuGiangVien}
                    </p>
                  )}
                </div>
              )}

              {selected.trangThai === 'CHO_GV_XU_LY' && (
                <div className="bg-surface-container-lowest rounded-xl shadow-sm p-8 space-y-6">
                  <h3 className="font-headline text-xl font-bold">Xử lý yêu cầu</h3>
                  <div className="flex flex-wrap gap-4">
                    <label className="flex items-center gap-2 cursor-pointer text-sm font-bold">
                      <input type="radio" name="qd" checked={quyetDinh === 'DONG_Y'} onChange={() => setQuyetDinh('DONG_Y')} />
                      Đồng ý phúc khảo (cập nhật điểm)
                    </label>
                    <label className="flex items-center gap-2 cursor-pointer text-sm font-bold">
                      <input type="radio" name="qd" checked={quyetDinh === 'TU_CHOI'} onChange={() => setQuyetDinh('TU_CHOI')} />
                      Từ chối
                    </label>
                  </div>
                  {quyetDinh === 'DONG_Y' && (
                    <div>
                      <label className="text-xs font-bold text-on-surface-variant uppercase block mb-2">Điểm hệ 4 sau phúc khảo</label>
                      <input
                        type="text"
                        inputMode="decimal"
                        className="w-40 px-4 py-3 rounded-xl bg-surface-container border-none text-lg font-black text-primary focus:ring-2 focus:ring-primary/30"
                        value={diemSau}
                        onChange={(e) => setDiemSau(e.target.value)}
                      />
                    </div>
                  )}
                  <div>
                    <label className="text-xs font-bold text-on-surface-variant uppercase block mb-2">Ghi chú giảng viên (tùy chọn)</label>
                    <textarea
                      className="w-full bg-surface-container rounded-xl border-none focus:ring-2 focus:ring-primary text-sm p-4 min-h-[100px]"
                      placeholder="Lý do từ chối hoặc ghi nhận điều chỉnh…"
                      value={ghiChu}
                      onChange={(e) => setGhiChu(e.target.value)}
                    />
                  </div>
                  <div className="flex justify-end gap-3 pt-2">
                    <button
                      type="button"
                      disabled={busy}
                      onClick={submitDecision}
                      className="px-8 py-3 rounded-full font-bold text-sm text-white bg-primary hover:opacity-90 disabled:opacity-50"
                    >
                      {busy ? 'Đang lưu…' : 'Xác nhận quyết định'}
                    </button>
                  </div>
                </div>
              )}
            </>
          )}
        </section>
      </div>
    </main>
  );
};

export default GcThiXLKhiuNiPhcKho;
