import React, { useCallback, useEffect, useMemo, useState } from 'react';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const QunLLpGingDyimDanh = () => {
  const [classes, setClasses] = useState([]);
  const [selectedId, setSelectedId] = useState('');
  const [session, setSession] = useState(null);
  const [loading, setLoading] = useState(true);
  const [sessionLoading, setSessionLoading] = useState(false);
  const [error, setError] = useState('');
  const [rowBusyId, setRowBusyId] = useState(null);

  const token = typeof localStorage !== 'undefined' ? localStorage.getItem('jwt_token') : null;

  const loadClasses = useCallback(async () => {
    if (!token) {
      setError('Bạn chưa đăng nhập. Vui lòng đăng nhập tài khoản giảng viên (ví dụ gv01) từ All Portal.');
      setClasses([]);
      setLoading(false);
      return;
    }
    setLoading(true);
    setError('');
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/lecturer/attendance/my-classes`, {
        headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' }
      });
      const body = await res.json().catch(() => ({}));
      if (!res.ok) {
        throw new Error(body.message || 'Không tải được danh sách lớp phụ trách.');
      }
      setClasses(Array.isArray(body) ? body : []);
    } catch (e) {
      setError(e.message || 'Lỗi tải dữ liệu.');
      setClasses([]);
    } finally {
      setLoading(false);
    }
  }, [token]);

  useEffect(() => {
    loadClasses();
  }, [loadClasses]);

  const openTodaySession = async () => {
    if (!token || !selectedId) return;
    setSessionLoading(true);
    setError('');
    try {
      const res = await fetch(
        `${API_BASE_URL}/api/v1/lecturer/attendance/classes/${selectedId}/sessions`,
        {
          method: 'POST',
          headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
          body: JSON.stringify({})
        }
      );
      const body = await res.json().catch(() => ({}));
      if (!res.ok) {
        throw new Error(body.message || 'Không tạo/tải được buổi điểm danh.');
      }
      setSession(body);
    } catch (e) {
      setError(e.message || 'Lỗi buổi điểm danh.');
    } finally {
      setSessionLoading(false);
    }
  };

  const refreshSession = async (idBuoi) => {
    if (!token || !idBuoi) return;
    setSessionLoading(true);
    setError('');
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/lecturer/attendance/sessions/${idBuoi}`, {
        headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' }
      });
      const body = await res.json().catch(() => ({}));
      if (!res.ok) {
        throw new Error(body.message || 'Không tải được buổi điểm danh.');
      }
      setSession(body);
    } catch (e) {
      setError(e.message || 'Lỗi làm mới.');
    } finally {
      setSessionLoading(false);
    }
  };

  const patchRow = async (idDiemDanh, trangThai) => {
    if (!token) return;
    setRowBusyId(idDiemDanh);
    setError('');
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/lecturer/attendance/rows/${idDiemDanh}`, {
        method: 'PATCH',
        headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
        body: JSON.stringify({ trangThai })
      });
      const body = await res.json().catch(() => ({}));
      if (!res.ok) {
        throw new Error(body.message || 'Cập nhật điểm danh thất bại.');
      }
      if (session?.idBuoi) {
        await refreshSession(session.idBuoi);
      }
    } catch (e) {
      setError(e.message || 'Lỗi cập nhật.');
    } finally {
      setRowBusyId(null);
    }
  };

  const presentRows = useMemo(
    () => (session?.rows || []).filter((r) => r.trangThai === 'CO_MAT'),
    [session]
  );
  const absentRows = useMemo(
    () => (session?.rows || []).filter((r) => r.trangThai !== 'CO_MAT'),
    [session]
  );

  const selectedClass = useMemo(
    () => classes.find((c) => String(c.idLopHp) === String(selectedId)),
    [classes, selectedId]
  );

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div>
        <h2 className="text-2xl font-bold text-[#141b2b]">Quản lý lớp giảng dạy & điểm danh</h2>
        <p className="text-sm text-slate-600 mt-1">
          API: <code className="text-xs bg-slate-100 px-1 rounded">/api/v1/lecturer/attendance/*</code>
          — sinh viên check-in:{' '}
          <code className="text-xs bg-slate-100 px-1 rounded">POST /api/v1/attendance/me/check-in</code> với{' '}
          <code className="text-xs bg-slate-100 px-1 rounded">token</code> hiển thị bên dưới.
        </p>
      </div>

      {error && (
        <div className="rounded-lg border border-red-200 bg-red-50 text-red-800 px-4 py-3 text-sm">{error}</div>
      )}

      <section className="bg-white rounded-xl border border-[#dce2f7] p-6 shadow-sm space-y-4">
        <h3 className="font-semibold text-[#00288e]">Chọn lớp phụ trách</h3>
        {loading ? (
          <p className="text-sm text-slate-500">Đang tải…</p>
        ) : classes.length === 0 ? (
          <p className="text-sm text-slate-600">
            Không có lớp nào gán cho giảng viên này, hoặc tài khoản chưa liên kết hồ sơ GV. Hãy gán{' '}
            <strong>Giảng viên</strong> cho lớp học phần trong Data Master (Admin), hoặc dùng tài khoản{' '}
            <code className="text-xs bg-slate-100 px-1 rounded">gv01</code> sau khi seed <code className="text-xs bg-slate-100 px-1 rounded">GV_SEED</code>.
          </p>
        ) : (
          <div className="flex flex-col sm:flex-row gap-3 sm:items-end">
            <div className="flex-1">
              <label className="block text-xs font-medium text-slate-500 mb-1">Lớp học phần</label>
              <select
                className="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm"
                value={selectedId}
                onChange={(e) => {
                  setSelectedId(e.target.value);
                  setSession(null);
                }}
              >
                <option value="">— Chọn —</option>
                {classes.map((c) => (
                  <option key={c.idLopHp} value={c.idLopHp}>
                    {c.maLopHp} — {c.tenHocPhan} ({c.hocKyLabel})
                  </option>
                ))}
              </select>
            </div>
            <button
              type="button"
              disabled={!selectedId || sessionLoading}
              onClick={openTodaySession}
              className="px-4 py-2 rounded-lg bg-[#00288e] text-white text-sm font-semibold disabled:opacity-50"
            >
              {sessionLoading ? 'Đang xử lý…' : 'Mở / tải buổi điểm danh hôm nay'}
            </button>
            {session?.idBuoi && (
              <button
                type="button"
                disabled={sessionLoading}
                onClick={() => refreshSession(session.idBuoi)}
                className="px-4 py-2 rounded-lg border border-[#00288e] text-[#00288e] text-sm font-semibold disabled:opacity-50"
              >
                Làm mới
              </button>
            )}
          </div>
        )}
      </section>

      {session && (
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
          <div className="lg:col-span-5 space-y-4">
            <div className="bg-white rounded-xl border border-[#dce2f7] p-6 shadow-sm text-center space-y-3">
              <p className="text-xs uppercase tracking-wide text-slate-500 font-semibold">Mã buổi (QR)</p>
              {session.qrImageUrl ? (
                <img
                  alt="QR điểm danh"
                  className="mx-auto w-56 h-56 object-contain border border-slate-100 rounded-lg"
                  src={session.qrImageUrl}
                />
              ) : null}
              <p className="text-[11px] text-slate-500 break-all font-mono px-2">{session.publicToken}</p>
              <p className="text-xs text-slate-500">
                {session.maLopHp} · {session.tenHocPhan} · {session.ngayBuoi}
              </p>
            </div>
            <div className="bg-[#f0f4ff] rounded-xl border border-[#dce2f7] p-4 flex justify-between text-center text-sm">
              <div className="flex-1 border-r border-slate-200">
                <div className="text-xs text-slate-500 font-bold uppercase">Tổng</div>
                <div className="text-2xl font-black text-[#00288e]">{session.tongSo}</div>
              </div>
              <div className="flex-1 border-r border-slate-200">
                <div className="text-xs text-slate-500 font-bold uppercase">Có mặt</div>
                <div className="text-2xl font-black text-emerald-700">{session.coMat}</div>
              </div>
              <div className="flex-1">
                <div className="text-xs text-slate-500 font-bold uppercase">Vắng / phép</div>
                <div className="text-2xl font-black text-amber-800">
                  {session.vang + session.phep}
                </div>
              </div>
            </div>
          </div>

          <div className="lg:col-span-7 space-y-6">
            <section className="bg-white rounded-xl border border-[#dce2f7] p-6 shadow-sm">
              <h3 className="text-lg font-bold text-[#141b2b] mb-4">Đã điểm danh ({presentRows.length})</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-3 max-h-72 overflow-y-auto">
                {presentRows.length === 0 ? (
                  <p className="text-sm text-slate-500 col-span-2">Chưa có sinh viên có mặt.</p>
                ) : (
                  presentRows.map((r) => (
                    <div
                      key={r.idDiemDanh}
                      className="flex items-center justify-between p-3 rounded-lg bg-slate-50 border border-slate-100"
                    >
                      <div>
                        <p className="text-sm font-semibold text-[#141b2b]">{r.hoTen}</p>
                        <p className="text-[11px] text-slate-500">
                          {r.maSinhVien}
                          {r.thoiGianCapNhat ? ` · ${r.thoiGianCapNhat}` : ''}
                        </p>
                      </div>
                      <button
                        type="button"
                        disabled={rowBusyId === r.idDiemDanh}
                        onClick={() => patchRow(r.idDiemDanh, 'VANG')}
                        className="text-xs text-amber-800 hover:underline disabled:opacity-50"
                      >
                        Đánh vắng
                      </button>
                    </div>
                  ))
                )}
              </div>
            </section>

            <section className="bg-white rounded-xl border border-[#dce2f7] p-6 shadow-sm">
              <h3 className="text-lg font-bold text-slate-600 mb-4">Chưa có mặt / vắng ({absentRows.length})</h3>
              <div className="space-y-2 max-h-80 overflow-y-auto">
                {absentRows.length === 0 ? (
                  <p className="text-sm text-slate-500">Tất cả đã có mặt.</p>
                ) : (
                  absentRows.map((r) => (
                    <div
                      key={r.idDiemDanh}
                      className="grid grid-cols-12 items-center gap-2 px-3 py-2 rounded-lg border border-slate-100 bg-slate-50"
                    >
                      <div className="col-span-6 text-sm font-medium truncate">{r.hoTen}</div>
                      <div className="col-span-3 text-xs text-slate-500">{r.maSinhVien}</div>
                      <div className="col-span-3 flex justify-end gap-1">
                        <button
                          type="button"
                          disabled={rowBusyId === r.idDiemDanh}
                          onClick={() => patchRow(r.idDiemDanh, 'CO_MAT')}
                          className="text-xs px-2 py-1 rounded bg-emerald-600 text-white disabled:opacity-50"
                        >
                          Có mặt
                        </button>
                        <button
                          type="button"
                          disabled={rowBusyId === r.idDiemDanh}
                          onClick={() => patchRow(r.idDiemDanh, 'PHEP')}
                          className="text-xs px-2 py-1 rounded border border-slate-300 disabled:opacity-50"
                        >
                          Phép
                        </button>
                      </div>
                    </div>
                  ))
                )}
              </div>
            </section>
          </div>
        </div>
      )}

      {selectedClass && !session && !loading && (
        <p className="text-xs text-slate-500">
          Đang chọn: <strong>{selectedClass.maLopHp}</strong> — nhấn &quot;Mở / tải buổi điểm danh hôm nay&quot; để tạo buổi theo ngày hệ thống.
        </p>
      )}
    </div>
  );
};

export default QunLLpGingDyimDanh;
