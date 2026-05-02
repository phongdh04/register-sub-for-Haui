import React, { useCallback, useEffect, useState } from 'react';

import { API_BASE_URL, authHeaders } from '../config/api';

const toLocalInput = (iso) => {
  if (!iso) return '';
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return '';
  const pad = (n) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
};

const fromLocalInput = (localStr) => {
  const s = (localStr || '').trim();
  if (!s) return null;
  const t = new Date(s).getTime();
  if (!Number.isFinite(t)) return null;
  return new Date(t).toISOString();
};

const SetupCuHnhGiVngTrafficSplittingQueueControl = () => {
  const [hocKys, setHocKys] = useState([]);
  const [selectedId, setSelectedId] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [msg, setMsg] = useState('');
  const [err, setErr] = useState('');

  const [preTu, setPreTu] = useState('');
  const [preDen, setPreDen] = useState('');
  const [ctTu, setCtTu] = useState('');
  const [ctDen, setCtDen] = useState('');

  const loadList = useCallback(async () => {
    setErr('');
    const res = await fetch(`${API_BASE_URL}/api/hoc-ky`, { headers: authHeaders() });
    const body = await res.json().catch(() => []);
    if (!res.ok) {
      throw new Error(body.message || 'Không tải danh sách học kỳ (cần quyền admin).');
    }
    setHocKys(Array.isArray(body) ? body : []);
  }, []);

  useEffect(() => {
    (async () => {
      setLoading(true);
      try {
        await loadList();
      } catch (e) {
        setErr(e.message || 'Lỗi tải học kỳ.');
      } finally {
        setLoading(false);
      }
    })();
  }, [loadList]);

  const applySelection = async (idStr) => {
    setSelectedId(idStr);
    setMsg('');
    setErr('');
    if (!idStr) {
      setPreTu('');
      setPreDen('');
      setCtTu('');
      setCtDen('');
      return;
    }
    const id = Number(idStr);
    try {
      const res = await fetch(`${API_BASE_URL}/api/hoc-ky/${id}`, { headers: authHeaders() });
      const hk = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(hk.message || 'Không tải chi tiết học kỳ.');
      setPreTu(toLocalInput(hk.preDangKyMoTu));
      setPreDen(toLocalInput(hk.preDangKyMoDen));
      setCtTu(toLocalInput(hk.dangKyChinhThucTu));
      setCtDen(toLocalInput(hk.dangKyChinhThucDen));
    } catch (e) {
      setErr(e.message || 'Lỗi.');
    }
  };

  const save = async () => {
    setMsg('');
    setErr('');
    const id = Number(selectedId);
    if (!Number.isFinite(id) || id <= 0) {
      setErr('Chọn học kỳ.');
      return;
    }
    const pTu = fromLocalInput(preTu);
    const pDe = fromLocalInput(preDen);
    const cTu = fromLocalInput(ctTu);
    const cDe = fromLocalInput(ctDen);
    const hasP = !!(pTu || pDe);
    const hasC = !!(cTu || cDe);
    if (hasP && (!pTu || !pDe)) {
      setErr('Đăng ký trước giờ G: nhập đủ mốc bắt đầu và kết thúc, hoặc để trống cả hai.');
      return;
    }
    if (hasC && (!cTu || !cDe)) {
      setErr('Đăng ký chính thức: nhập đủ mốc bắt đầu và kết thúc, hoặc để trống cả hai.');
      return;
    }
    setSaving(true);
    try {
      const res = await fetch(`${API_BASE_URL}/api/hoc-ky/${id}/lich-dang-ky`, {
        method: 'PUT',
        headers: authHeaders(),
        body: JSON.stringify({
          preDangKyMoTu: pTu,
          preDangKyMoDen: pDe,
          dangKyChinhThucTu: cTu,
          dangKyChinhThucDen: cDe
        })
      });
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || 'Lưu thất bại.');
      setMsg('Đã cập nhật lịch đăng ký.');
      await loadList();
      await applySelection(String(id));
    } catch (e) {
      setErr(e.message || 'Lỗi lưu.');
    } finally {
      setSaving(false);
    }
  };

  const clearPre = () => {
    setPreTu('');
    setPreDen('');
  };
  const clearOfficial = () => {
    setCtTu('');
    setCtDen('');
  };

  return (
    <div className="max-w-3xl mx-auto p-6 space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-[#141b2b]">Lịch đăng ký theo học kỳ</h1>
        <p className="text-sm text-[#64748b] mt-1">
          Admin cấu hình <strong>phiên đăng ký trước giờ G</strong> (giỏ nháp) và{' '}
          <strong>phiên đăng ký chính thức</strong> (ghi nhận qua Kafka sau hàng đợi). Để trống cả cặp mốc của một
          pha = không khóa thời gian cho pha đó (hành vi cũ).
        </p>
      </div>

      {err && <div className="rounded-lg bg-red-50 text-red-800 text-sm px-4 py-3 border border-red-200">{err}</div>}
      {msg && <div className="rounded-lg bg-emerald-50 text-emerald-900 text-sm px-4 py-3 border border-emerald-200">{msg}</div>}

      <section className="bg-white rounded-xl border border-[#dce2f7] p-6 shadow-sm space-y-4">
        <label className="block text-sm font-semibold text-[#334155]">
          Học kỳ
          <select
            className="mt-1 w-full border border-[#dce2f7] rounded-lg px-3 py-2 text-sm"
            value={selectedId}
            onChange={(e) => applySelection(e.target.value)}
            disabled={loading}
          >
            <option value="">— Chọn —</option>
            {hocKys.map((h) => (
              <option key={h.idHocKy} value={h.idHocKy}>
                HK{h.kyThu} {h.namHoc} (id {h.idHocKy}){h.trangThaiHienHanh ? ' · hiện hành' : ''}
              </option>
            ))}
          </select>
        </label>

        <div className="border-t border-[#eef1fa] pt-4 space-y-3">
          <h2 className="text-sm font-bold text-[#00288e] uppercase tracking-wide">Đăng ký trước giờ G (pre-reg)</h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <label className="text-xs font-semibold text-[#64748b]">
              Mở từ
              <input
                type="datetime-local"
                value={preTu}
                onChange={(e) => setPreTu(e.target.value)}
                className="mt-1 w-full border border-[#dce2f7] rounded-lg px-3 py-2 text-sm"
              />
            </label>
            <label className="text-xs font-semibold text-[#64748b]">
              Đến
              <input
                type="datetime-local"
                value={preDen}
                onChange={(e) => setPreDen(e.target.value)}
                className="mt-1 w-full border border-[#dce2f7] rounded-lg px-3 py-2 text-sm"
              />
            </label>
          </div>
          <button type="button" onClick={clearPre} className="text-xs text-[#00288e] font-semibold hover:underline">
            Xóa lịch pre-reg (để trống cả hai)
          </button>
        </div>

        <div className="border-t border-[#eef1fa] pt-4 space-y-3">
          <h2 className="text-sm font-bold text-[#00288e] uppercase tracking-wide">Đăng ký chính thức</h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <label className="text-xs font-semibold text-[#64748b]">
              Mở từ
              <input
                type="datetime-local"
                value={ctTu}
                onChange={(e) => setCtTu(e.target.value)}
                className="mt-1 w-full border border-[#dce2f7] rounded-lg px-3 py-2 text-sm"
              />
            </label>
            <label className="text-xs font-semibold text-[#64748b]">
              Đến
              <input
                type="datetime-local"
                value={ctDen}
                onChange={(e) => setCtDen(e.target.value)}
                className="mt-1 w-full border border-[#dce2f7] rounded-lg px-3 py-2 text-sm"
              />
            </label>
          </div>
          <button type="button" onClick={clearOfficial} className="text-xs text-[#00288e] font-semibold hover:underline">
            Xóa lịch đăng ký chính thức
          </button>
        </div>

        <div className="flex gap-3 pt-2">
          <button
            type="button"
            disabled={saving || !selectedId}
            onClick={save}
            className="px-5 py-2.5 rounded-lg bg-[#00288e] text-white text-sm font-semibold disabled:opacity-50 hover:bg-[#001a5c]"
          >
            {saving ? 'Đang lưu…' : 'Lưu lịch'}
          </button>
          <button
            type="button"
            onClick={() => loadList()}
            className="px-5 py-2.5 rounded-lg border border-[#dce2f7] text-sm font-semibold text-[#334155] hover:bg-[#f8fafc]"
          >
            Tải lại danh sách
          </button>
        </div>
      </section>
    </div>
  );
};

export default SetupCuHnhGiVngTrafficSplittingQueueControl;
