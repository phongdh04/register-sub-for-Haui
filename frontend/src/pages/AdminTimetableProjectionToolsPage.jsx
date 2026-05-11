import React, { useEffect, useMemo, useState } from 'react';

import { API_BASE_URL, authHeaders } from '../config/api';

async function parseBody(res) {
  const t = await res.text();
  if (!t) return {};
  try {
    return JSON.parse(t);
  } catch {
    return { message: t };
  }
}

function getErr(body, fallback) {
  if (body?.message) return String(body.message);
  return fallback;
}

const AdminTimetableProjectionToolsPage = () => {
  const [hocKys, setHocKys] = useState([]);
  const [hocKyId, setHocKyId] = useState('');
  const [sinhVienIdInput, setSinhVienIdInput] = useState('');
  const [ack, setAck] = useState(false);
  const [busy, setBusy] = useState(false);

  const [successMsg, setSuccessMsg] = useState('');
  const [errorMsg, setErrorMsg] = useState('');

  useEffect(() => {
    (async () => {
      try {
        const res = await fetch(`${API_BASE_URL}/api/hoc-ky`, { headers: authHeaders() });
        const body = await parseBody(res);
        if (!res.ok) throw new Error(getErr(body, 'Không tải được danh sách học kỳ.'));
        const list = Array.isArray(body) ? body : [];
        setHocKys(list);
        if (list.length) {
          const current = list.find((h) => h.trangThaiHienHanh === true) || list[0];
          setHocKyId(String(current.idHocKy ?? current.id));
        }
      } catch (e) {
        setErrorMsg(e.message || 'Lỗi tải dữ liệu học kỳ.');
      }
    })();
  }, []);

  const canSubmit = useMemo(() => {
    const id = Number(sinhVienIdInput.trim());
    return ack && Number.isFinite(id) && id > 0 && !!hocKyId && !busy;
  }, [ack, sinhVienIdInput, hocKyId, busy]);

  const onSubmit = async () => {
    setSuccessMsg('');
    setErrorMsg('');

    const sinhVienId = Number(sinhVienIdInput.trim());
    const hk = Number(hocKyId);
    if (!Number.isFinite(sinhVienId) || sinhVienId <= 0) {
      setErrorMsg('Mã sinh viên hiện dùng dạng số (sinhVienId). Vui lòng nhập ID hợp lệ.');
      return;
    }
    if (!Number.isFinite(hk) || hk <= 0) {
      setErrorMsg('Vui lòng chọn học kỳ hợp lệ.');
      return;
    }

    setBusy(true);
    try {
      const url = `${API_BASE_URL}/api/v1/admin/timetable-projection/rebuild?sinhVienId=${sinhVienId}&hocKyId=${hk}`;
      const res = await fetch(url, {
        method: 'POST',
        headers: authHeaders()
      });
      const body = await parseBody(res);
      if (!res.ok) throw new Error(getErr(body, 'Build lại projection thất bại.'));

      const slots = body.rebuiltSlots ?? 0;
      setSuccessMsg(`Thành công: Đã build lại ${slots} slot thời khóa biểu cho sinh viên ID ${body.sinhVienId ?? sinhVienId}.`);
    } catch (e) {
      setErrorMsg(e.message || 'Không thể build lại projection.');
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="mx-auto max-w-3xl py-2">
      <nav className="mb-6 flex items-center gap-2 text-sm text-on-surface-variant">
        <span>Tiện ích</span>
        <span className="material-symbols-outlined text-sm">chevron_right</span>
        <span className="font-medium text-on-surface">Projection TKB</span>
      </nav>

      <header className="mb-8 text-center">
        <h1 className="mb-2 text-3xl font-bold tracking-tight text-on-surface">Build lại projection thời khóa biểu</h1>
        <p className="text-sm text-on-surface-variant">Tiện ích phục hồi cấu trúc dữ liệu lịch học cho sinh viên.</p>
      </header>

      <div className="mb-6 flex items-start gap-3 rounded-xl border border-secondary-container/30 bg-secondary-container/20 p-4">
        <span className="material-symbols-outlined text-secondary">warning</span>
        <div>
          <h3 className="text-sm font-bold uppercase tracking-wide text-on-secondary-container">Cảnh báo hệ thống</h3>
          <p className="mt-1 text-sm text-on-surface">
            Đây là thao tác khôi phục dữ liệu nhạy cảm. Chỉ thực hiện khi thật sự cần thiết và có chỉ đạo kỹ thuật.
          </p>
        </div>
      </div>

      <section className="rounded-xl bg-surface-container-lowest p-6 shadow-[0_20px_40px_rgba(20,27,43,0.05)]">
        <div className="space-y-5">
          <div>
            <label className="mb-2 block text-xs font-semibold uppercase tracking-wide text-on-surface">Mã sinh viên (ID)</label>
            <div className="relative">
              <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-outline">badge</span>
              <input
                type="text"
                value={sinhVienIdInput}
                onChange={(e) => setSinhVienIdInput(e.target.value)}
                placeholder="Nhập sinhVienId cần build lại..."
                className="w-full rounded-lg border border-outline-variant/30 bg-surface-container-low py-3 pl-10 pr-4 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-primary/30"
              />
            </div>
          </div>

          <div>
            <label className="mb-2 block text-xs font-semibold uppercase tracking-wide text-on-surface">Mã học kỳ</label>
            <div className="relative">
              <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-outline">calendar_month</span>
              <select
                value={hocKyId}
                onChange={(e) => setHocKyId(e.target.value)}
                className="w-full appearance-none rounded-lg border border-outline-variant/30 bg-surface-container-low py-3 pl-10 pr-10 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-primary/30"
              >
                <option value="">Chọn học kỳ...</option>
                {hocKys.map((hk) => {
                  const id = hk.idHocKy ?? hk.id;
                  return (
                    <option key={id} value={String(id)}>
                      {hk.tenHocKy || `HK${hk.kyThu} ${hk.namHoc}`} (id {id})
                    </option>
                  );
                })}
              </select>
              <span className="material-symbols-outlined pointer-events-none absolute right-3 top-1/2 -translate-y-1/2 text-outline">expand_more</span>
            </div>
          </div>

          <div className="flex items-start gap-3 border-t border-surface-variant pt-4">
            <input
              id="ack-projection-risk"
              type="checkbox"
              checked={ack}
              onChange={(e) => setAck(e.target.checked)}
              className="mt-0.5 h-5 w-5 rounded border-outline-variant bg-surface-container-low text-primary focus:ring-primary"
            />
            <label htmlFor="ack-projection-risk" className="text-sm text-on-surface-variant">
              Tôi hiểu đây là thao tác khôi phục và chấp nhận rủi ro ghi đè dữ liệu.
            </label>
          </div>

          <button
            type="button"
            onClick={onSubmit}
            disabled={!canSubmit}
            className={`w-full rounded-full px-6 py-3.5 text-sm font-bold transition ${
              canSubmit
                ? 'bg-gradient-to-r from-primary to-primary-container text-on-primary hover:shadow-md'
                : 'cursor-not-allowed bg-surface-container-high text-outline-variant'
            }`}
          >
            {busy ? 'Đang build lại projection...' : 'Build lại projection'}
          </button>
        </div>
      </section>

      <div className="mt-6 space-y-3">
        {successMsg && (
          <div className="flex items-center gap-2 rounded-full bg-primary-fixed/30 px-4 py-3 text-sm text-on-primary-fixed">
            <span className="material-symbols-outlined text-primary">check_circle</span>
            <span>{successMsg}</span>
          </div>
        )}
        {errorMsg && (
          <div className="flex items-center gap-2 rounded-full bg-error-container px-4 py-3 text-sm text-on-error-container">
            <span className="material-symbols-outlined text-error">error</span>
            <span>{errorMsg}</span>
          </div>
        )}
      </div>
    </div>
  );
};

export default AdminTimetableProjectionToolsPage;
