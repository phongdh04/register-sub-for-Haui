import React, { useCallback, useEffect, useState } from 'react';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const formatDate = (iso) => {
  if (!iso) return '—';
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return iso;
  return d.toLocaleDateString('vi-VN');
};

const statusBadge = (code) => {
  if (code === 'DUOC_THI') {
    return 'inline-flex items-center px-3 py-1 rounded-full text-[10px] font-bold uppercase tracking-wide bg-primary-fixed text-on-primary-fixed-variant';
  }
  if (code === 'BI_CAM_THI') {
    return 'inline-flex items-center px-3 py-1 rounded-full text-[10px] font-bold uppercase tracking-wide bg-error-container text-on-error-container';
  }
  return 'inline-flex items-center px-3 py-1 rounded-full text-[10px] font-bold uppercase tracking-wide bg-surface-container-high text-on-surface-variant';
};

const LchThinhGiGv = () => {
  const [hocKyId, setHocKyId] = useState('');
  const [examData, setExamData] = useState(null);
  const [ratingData, setRatingData] = useState(null);
  const [loadingEx, setLoadingEx] = useState(true);
  const [loadingRt, setLoadingRt] = useState(true);
  const [errEx, setErrEx] = useState('');
  const [errRt, setErrRt] = useState('');
  const [forms, setForms] = useState({});

  const qs = () => {
    const h = hocKyId.trim();
    return h ? `?hocKyId=${encodeURIComponent(h)}` : '';
  };

  const loadExams = useCallback(async () => {
    const token = localStorage.getItem('jwt_token');
    if (!token) {
      setErrEx('Vui lòng đăng nhập tài khoản sinh viên.');
      setExamData(null);
      setLoadingEx(false);
      return;
    }
    setLoadingEx(true);
    setErrEx('');
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/exams/me${qs()}`, {
        headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' }
      });
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || 'Không tải được lịch thi.');
      setExamData(body);
    } catch (e) {
      setErrEx(e.message || 'Lỗi lịch thi.');
      setExamData(null);
    } finally {
      setLoadingEx(false);
    }
  }, [hocKyId]);

  const loadRatings = useCallback(async () => {
    const token = localStorage.getItem('jwt_token');
    if (!token) {
      setErrRt('Vui lòng đăng nhập tài khoản sinh viên.');
      setRatingData(null);
      setLoadingRt(false);
      return;
    }
    setLoadingRt(true);
    setErrRt('');
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/lecturer-ratings/me${qs()}`, {
        headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' }
      });
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || 'Không tải được danh sách đánh giá.');
      setRatingData(body);
      const init = {};
      (body.rows || []).forEach((r) => {
        init[r.idDangKy] = {
          diemTong: r.diemTong ?? 5,
          binhLuan: r.binhLuan ?? '',
          msg: '',
          busy: false
        };
      });
      setForms(init);
    } catch (e) {
      setErrRt(e.message || 'Lỗi đánh giá.');
      setRatingData(null);
    } finally {
      setLoadingRt(false);
    }
  }, [hocKyId]);

  useEffect(() => {
    loadExams();
  }, [loadExams]);

  useEffect(() => {
    loadRatings();
  }, [loadRatings]);

  const applyHk = () => {
    loadExams();
    loadRatings();
  };

  const submitRating = async (idDangKy) => {
    const token = localStorage.getItem('jwt_token');
    const f = forms[idDangKy];
    if (!token || !f) return;
    setForms((prev) => ({ ...prev, [idDangKy]: { ...prev[idDangKy], busy: true, msg: '' } }));
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/lecturer-ratings`, {
        method: 'POST',
        headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
        body: JSON.stringify({
          idDangKy,
          diemTong: Number(f.diemTong),
          binhLuan: f.binhLuan || ''
        })
      });
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || 'Gửi đánh giá thất bại.');
      setForms((prev) => ({
        ...prev,
        [idDangKy]: { ...prev[idDangKy], busy: false, msg: 'Đã lưu đánh giá.' }
      }));
      await loadRatings();
    } catch (e) {
      setForms((prev) => ({
        ...prev,
        [idDangKy]: { ...prev[idDangKy], busy: false, msg: e.message || 'Lỗi.' }
      }));
    }
  };

  const rows = examData?.rows || [];
  const rrows = ratingData?.rows || [];
  const tongTc = rows.reduce((s, r) => s + (Number(r.soTinChi) || 0), 0);

  return (
    <main className="min-h-screen">
      <div className="p-12 max-w-7xl mx-auto space-y-12">
        <div className="flex flex-col md:flex-row md:items-end justify-between gap-6">
          <div>
            <h2 className="text-4xl font-black text-on-surface tracking-tight mb-2">Lịch thi & đánh giá GV</h2>
            <p className="text-on-surface-variant font-medium">
              {examData?.hocKyLabel || ratingData?.hocKyLabel || '—'} • API{' '}
              <code className="text-xs bg-surface-container-high px-1 rounded">GET /api/v1/exams/me</code>,{' '}
              <code className="text-xs bg-surface-container-high px-1 rounded">GET/POST /api/v1/lecturer-ratings</code>
            </p>
            <div className="flex flex-wrap gap-2 items-end mt-4">
              <input
                type="text"
                value={hocKyId}
                onChange={(e) => setHocKyId(e.target.value)}
                placeholder="id học kỳ (tuỳ chọn)"
                className="border border-outline-variant rounded-lg px-3 py-2 text-sm w-40 bg-surface"
              />
              <button
                type="button"
                onClick={applyHk}
                className="px-4 py-2 rounded-lg bg-primary text-on-primary text-sm font-bold"
              >
                Áp dụng HK
              </button>
            </div>
          </div>
          <button
            type="button"
            onClick={() => window.print()}
            className="flex items-center gap-2 bg-gradient-to-br from-primary to-primary-container text-white px-8 py-3 rounded-full font-bold shadow-[0_10px_20px_rgba(0,40,142,0.15)] hover:scale-[1.02] active:scale-95 transition-all"
          >
            <span className="material-symbols-outlined text-xl">print</span>
            <span>In lịch thi</span>
          </button>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="bg-surface-container-lowest p-8 rounded-xl shadow-[0_20px_40px_rgba(20,27,43,0.03)] flex flex-col justify-between">
            <div className="flex items-center gap-4 mb-4">
              <div className="w-12 h-12 bg-primary-fixed rounded-full flex items-center justify-center text-primary">
                <span className="material-symbols-outlined" style={{ fontVariationSettings: '"FILL" 1' }}>
                  auto_stories
                </span>
              </div>
              <span className="text-sm font-bold uppercase tracking-wider text-on-surface-variant">Môn đã đăng ký (HK)</span>
            </div>
            <div>
              <span className="text-5xl font-black text-on-surface">{examData?.tongMonCoDangKy ?? '—'}</span>
              <span className="text-on-surface-variant font-medium ml-2">học phần</span>
            </div>
          </div>
          <div className="bg-surface-container-lowest p-8 rounded-xl shadow-[0_20px_40px_rgba(20,27,43,0.03)] flex flex-col justify-between">
            <div className="flex items-center gap-4 mb-4">
              <div className="w-12 h-12 bg-secondary-fixed rounded-full flex items-center justify-center text-secondary">
                <span className="material-symbols-outlined" style={{ fontVariationSettings: '"FILL" 1' }}>
                  event_note
                </span>
              </div>
              <span className="text-sm font-bold uppercase tracking-wider text-on-surface-variant">Đã có lịch thi</span>
            </div>
            <div>
              <span className="text-5xl font-black text-on-surface">{examData?.tongMonCoLichThi ?? '—'}</span>
              <span className="text-on-surface-variant font-medium ml-2">/ tổng môn</span>
            </div>
          </div>
          <div className="bg-surface-container-lowest p-8 rounded-xl shadow-[0_20px_40px_rgba(20,27,43,0.03)] flex flex-col justify-between col-span-1 md:col-span-1 relative overflow-hidden">
            <div className="relative z-10 flex flex-col gap-4 h-full justify-center">
              <div className="flex items-center gap-4 mb-2">
                <div className="w-12 h-12 bg-tertiary-fixed rounded-full flex items-center justify-center text-tertiary">
                  <span className="material-symbols-outlined">menu_book</span>
                </div>
                <span className="text-sm font-bold uppercase tracking-wider text-on-surface-variant">Tổng tín chỉ (bảng lịch)</span>
              </div>
              <span className="text-4xl font-black text-primary">{tongTc || '—'}</span>
            </div>
            <div className="absolute -right-8 -bottom-8 w-48 h-48 bg-secondary/5 rounded-full blur-3xl" />
          </div>
        </div>

        {errEx && <div className="rounded-xl border border-error/30 bg-error-container/20 px-4 py-3 text-sm text-error">{errEx}</div>}
        {loadingEx && <p className="text-sm text-on-surface-variant">Đang tải lịch thi…</p>}

        <div className="bg-surface-container-lowest rounded-xl shadow-[0_20px_40px_rgba(20,27,43,0.03)] overflow-hidden">
          <div className="px-8 py-4 border-b border-outline-variant/20">
            <h3 className="text-lg font-bold text-on-surface">Lịch thi theo đăng ký</h3>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-surface-container-low">
                  <th className="px-8 py-6 text-[11px] font-black uppercase tracking-[0.1em] text-on-surface-variant">Môn thi</th>
                  <th className="px-6 py-6 text-[11px] font-black uppercase tracking-[0.1em] text-on-surface-variant">Ngày thi</th>
                  <th className="px-6 py-6 text-[11px] font-black uppercase tracking-[0.1em] text-on-surface-variant">Ca thi</th>
                  <th className="px-6 py-6 text-[11px] font-black uppercase tracking-[0.1em] text-on-surface-variant">SBD</th>
                  <th className="px-6 py-6 text-[11px] font-black uppercase tracking-[0.1em] text-on-surface-variant text-center">Lần thi</th>
                  <th className="px-6 py-6 text-[11px] font-black uppercase tracking-[0.1em] text-on-surface-variant">Địa điểm</th>
                  <th className="px-8 py-6 text-[11px] font-black uppercase tracking-[0.1em] text-on-surface-variant">Tình trạng</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {!loadingEx && rows.length === 0 && (
                  <tr>
                    <td colSpan={7} className="px-8 py-10 text-center text-on-surface-variant text-sm">
                      Không có đăng ký trong học kỳ này.
                    </td>
                  </tr>
                )}
                {rows.map((r) => (
                  <tr key={r.idDangKy} className="hover:bg-slate-50/50 transition-colors">
                    <td className="px-8 py-6">
                      <div className="flex flex-col">
                        <span className="font-bold text-on-surface">{r.tenHocPhan || '—'}</span>
                        <span className="text-xs text-on-surface-variant font-medium">
                          {r.maHocPhan || '—'} • {r.soTinChi != null ? `${r.soTinChi} TC` : '—'} • {r.maLopHp}
                        </span>
                      </div>
                    </td>
                    <td className="px-6 py-6 font-semibold text-on-surface">{r.coLichThi ? formatDate(r.ngayThi) : '—'}</td>
                    <td className="px-6 py-6">
                      {r.coLichThi ? (
                        <div className="flex items-center gap-2">
                          <span className="w-2 h-2 rounded-full bg-secondary" />
                          <span className="text-sm font-medium">
                            {r.caThi}
                            {r.gioBatDau ? ` (${r.gioBatDau})` : ''}
                          </span>
                        </div>
                      ) : (
                        <span className="text-sm text-on-surface-variant">Chưa cấp lịch</span>
                      )}
                    </td>
                    <td className="px-6 py-6 font-mono font-bold text-primary">{r.soBaoDanh || '—'}</td>
                    <td className="px-6 py-6 text-center font-medium">{r.lanThi ?? '—'}</td>
                    <td className="px-6 py-6">
                      <span className="text-sm font-medium">{r.coLichThi ? r.phongThi || '—' : '—'}</span>
                    </td>
                    <td className="px-8 py-6">
                      <span className={statusBadge(r.trangThaiDuThi)}>
                        {r.trangThaiDuThi === 'CHUA_CAP' ? 'Chưa cấp phiếu' : r.trangThaiDuThi === 'DUOC_THI' ? 'Được thi' : r.trangThaiDuThi === 'BI_CAM_THI' ? 'Bị cấm thi' : r.trangThaiDuThi}
                      </span>
                      {r.lyDo && <div className="text-[10px] text-error font-medium italic mt-1">{r.lyDo}</div>}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        <div className="bg-tertiary-container/10 p-8 rounded-xl border border-tertiary-container/20 flex flex-col md:flex-row gap-8 items-center justify-between">
          <div className="flex gap-6 items-center">
            <span className="material-symbols-outlined text-tertiary-container text-4xl">info</span>
            <div>
              <h4 className="font-bold text-on-surface">Lưu ý sinh viên</h4>
              <p className="text-sm text-on-surface-variant max-w-xl">
                Dữ liệu lịch thi / SBD do phòng Đào tạo cập nhật. Trường hợp &quot;Bị cấm thi&quot; vui lòng liên hệ khoa. Đánh giá giảng viên chỉ áp dụng lớp đã gán GV.
              </p>
            </div>
          </div>
        </div>

        <div className="space-y-4">
          <h3 className="text-2xl font-black text-on-surface">Đánh giá giảng viên</h3>
          {errRt && <div className="rounded-xl border border-error/30 bg-error-container/20 px-4 py-3 text-sm text-error">{errRt}</div>}
          {loadingRt && <p className="text-sm text-on-surface-variant">Đang tải…</p>}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {rrows.map((r) => {
              const f = forms[r.idDangKy] || { diemTong: 5, binhLuan: '', msg: '', busy: false };
              return (
                <div key={r.idDangKy} className="bg-surface-container-lowest rounded-xl p-6 shadow-sm border border-outline-variant/20 space-y-3">
                  <div>
                    <p className="font-bold text-on-surface">{r.tenHocPhan}</p>
                    <p className="text-xs text-on-surface-variant">
                      {r.maLopHp} • {r.maHocPhan} • {r.coGiangVien ? r.tenGiangVien : 'Chưa gán GV'}
                    </p>
                  </div>
                  {r.daDanhGia && (
                    <p className="text-sm text-primary font-semibold">
                      Đã đánh giá: {r.diemTong}/5{r.binhLuan ? ` — ${r.binhLuan}` : ''}
                    </p>
                  )}
                  {r.coGiangVien ? (
                    <div className="space-y-2">
                      <label className="block text-xs font-bold text-on-surface-variant">Điểm (1–5)</label>
                      <select
                        className="w-full border rounded-lg px-3 py-2 text-sm bg-surface"
                        value={f.diemTong}
                        onChange={(e) =>
                          setForms((prev) => ({
                            ...prev,
                            [r.idDangKy]: { ...prev[r.idDangKy], diemTong: Number(e.target.value) }
                          }))
                        }
                      >
                        {[1, 2, 3, 4, 5].map((n) => (
                          <option key={n} value={n}>
                            {n}
                          </option>
                        ))}
                      </select>
                      <label className="block text-xs font-bold text-on-surface-variant">Nhận xét</label>
                      <textarea
                        className="w-full border rounded-lg px-3 py-2 text-sm bg-surface min-h-[80px]"
                        value={f.binhLuan}
                        onChange={(e) =>
                          setForms((prev) => ({
                            ...prev,
                            [r.idDangKy]: { ...prev[r.idDangKy], binhLuan: e.target.value }
                          }))
                        }
                        maxLength={2000}
                      />
                      <button
                        type="button"
                        disabled={f.busy}
                        onClick={() => submitRating(r.idDangKy)}
                        className="px-4 py-2 rounded-full bg-secondary text-on-secondary font-bold text-sm disabled:opacity-50"
                      >
                        {f.busy ? 'Đang gửi…' : r.daDanhGia ? 'Cập nhật đánh giá' : 'Gửi đánh giá'}
                      </button>
                      {f.msg && <p className="text-xs text-on-surface-variant">{f.msg}</p>}
                    </div>
                  ) : (
                    <p className="text-sm text-on-surface-variant">Không thể đánh giá khi lớp chưa có giảng viên.</p>
                  )}
                </div>
              );
            })}
          </div>
        </div>
      </div>
    </main>
  );
};

export default LchThinhGiGv;
