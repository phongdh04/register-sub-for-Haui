import React, { useCallback, useEffect, useMemo, useState } from 'react';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const formatGpa = (v) => (v != null && v !== '' ? Number(v).toFixed(2) : '—');

const KimTraTinHcTpTranscriptDashboard = () => {
  const [allData, setAllData] = useState(null);
  const [data, setData] = useState(null);
  const [semesterId, setSemesterId] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchTranscript = useCallback(async (hocKyId) => {
    const token = localStorage.getItem('jwt_token');
    if (!token) {
      throw new Error('Bạn chưa đăng nhập. Vui lòng đăng nhập để xem bảng điểm.');
    }
    const qs = hocKyId ? `?hocKyId=${encodeURIComponent(hocKyId)}` : '';
    const response = await fetch(`${API_BASE_URL}/api/v1/transcript/me${qs}`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    if (!response.ok) {
      const body = await response.json().catch(() => ({}));
      throw new Error(body.message || 'Không tải được bảng điểm.');
    }
    return response.json();
  }, []);

  useEffect(() => {
    const run = async () => {
      try {
        setLoading(true);
        setError('');
        const full = await fetchTranscript(null);
        setAllData(full);
        setData(full);
      } catch (e) {
        setError(e.message || 'Lỗi tải dữ liệu.');
      } finally {
        setLoading(false);
      }
    };
    run();
  }, [fetchTranscript]);

  useEffect(() => {
    if (!allData) return;
    if (semesterId === '') {
      setData(allData);
      return;
    }
    let cancelled = false;
    (async () => {
      try {
        setLoading(true);
        setError('');
        const partial = await fetchTranscript(semesterId);
        if (!cancelled) setData(partial);
      } catch (e) {
        if (!cancelled) setError(e.message || 'Lỗi tải dữ liệu.');
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => { cancelled = true; };
  }, [semesterId, allData, fetchTranscript]);

  const semesterOptions = useMemo(() => {
    if (!allData?.hocKys?.length) return [];
    return allData.hocKys.map((hk) => ({
      id: String(hk.idHocKy),
      label: hk.tenHocKy || `Học kỳ ${hk.idHocKy}`
    }));
  }, [allData]);

  const firstSemester = data?.hocKys?.[0];
  const semesterGpa = semesterId ? firstSemester?.gpaHocKy : null;

  const flatRows = useMemo(() => {
    if (!data?.hocKys?.length) return [];
    return data.hocKys.flatMap((hk) =>
      (hk.monHoc || []).map((m) => ({
        ...m,
        tenHocKy: hk.tenHocKy
      }))
    );
  }, [data]);

  return (
    <main className="min-h-screen bg-surface">
      <div className="p-8 max-w-7xl mx-auto space-y-8">
        <section className="flex flex-col md:flex-row justify-between items-end gap-6">
          <div className="space-y-2">
            <span className="text-primary font-bold tracking-widest text-xs uppercase">Academic Records</span>
            <h2 className="text-4xl font-extrabold tracking-tight text-on-surface font-headline">Bảng điểm sinh viên</h2>
            <p className="text-on-surface-variant max-w-md">
              Dữ liệu từ đăng ký học phần thành công và bảng điểm hệ 4 (Task 4).
            </p>
          </div>
          <div className="w-full md:w-auto">
            <label className="block text-[10px] font-bold text-on-surface-variant uppercase tracking-wider mb-2 ml-1" htmlFor="hk-select">
              Chọn học kỳ
            </label>
            <select
              id="hk-select"
              className="w-full md:min-w-[260px] px-5 py-3 bg-surface-container-lowest shadow-sm rounded-xl font-semibold text-sm border-none focus:ring-2 focus:ring-primary/30"
              value={semesterId}
              onChange={(e) => setSemesterId(e.target.value)}
            >
              <option value="">Tất cả học kỳ</option>
              {semesterOptions.map((o) => (
                <option key={o.id} value={o.id}>{o.label}</option>
              ))}
            </select>
          </div>
        </section>

        {loading && (
          <div className="bg-surface-container-lowest rounded-xl p-6 text-sm text-on-surface-variant">
            Đang tải bảng điểm...
          </div>
        )}
        {!loading && error && (
          <div className="bg-error-container/40 border border-error/30 rounded-xl p-6 text-sm text-error font-medium">
            {error}
          </div>
        )}

        {!loading && !error && data && (
          <>
            <section className="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div className="bg-surface-container-lowest p-8 rounded-xl">
                <p className="text-sm font-semibold text-on-surface-variant mb-1">GPA học kỳ (đang xem)</p>
                <div className="flex items-baseline gap-2">
                  <h3 className="text-5xl font-black text-primary font-headline">{formatGpa(semesterGpa)}</h3>
                  <span className="text-xs font-bold text-on-surface-variant">/ 4.0</span>
                </div>
                <p className="mt-2 text-xs text-on-surface-variant">
                  {semesterId ? (firstSemester?.tenHocKy || '') : 'Chọn một học kỳ để xem GPA riêng kỳ đó'}
                </p>
              </div>
              <div className="bg-primary bg-gradient-to-br from-primary to-primary-container p-8 rounded-xl text-white shadow-xl">
                <p className="text-sm font-medium text-white/70 mb-1">GPA tích lũy (phạm vi đang xem)</p>
                <div className="flex items-baseline gap-2">
                  <h3 className="text-5xl font-black font-headline">{formatGpa(data.gpaTichLuy)}</h3>
                  <span className="text-xs font-bold text-white/50">/ 4.0</span>
                </div>
                <p className="mt-3 text-[10px] font-bold uppercase tracking-wider text-white/60">
                  Tín chỉ có điểm: {data.tongTinChiCoDiem} / Đăng ký: {data.tongTinChiDangKy}
                </p>
              </div>
              <div className="bg-surface-container-lowest p-8 rounded-xl">
                <p className="text-sm font-semibold text-on-surface-variant mb-1">Sinh viên</p>
                <p className="text-lg font-bold text-on-surface">{data.hoTenSinhVien}</p>
                <p className="text-sm text-on-surface-variant">{data.maSinhVien}</p>
              </div>
            </section>

            <section className="bg-surface-container-lowest rounded-xl shadow-sm overflow-hidden">
              <div className="px-8 py-6 flex items-center justify-between bg-surface-container-low/50">
                <h4 className="font-bold text-on-surface flex items-center gap-2">
                  <span className="material-symbols-outlined text-primary">list_alt</span>
                  Chi tiết kết quả học tập
                </h4>
              </div>
              <div className="overflow-x-auto">
                <table className="w-full border-collapse min-w-[720px]">
                  <thead>
                    <tr className="bg-surface-container-low text-left">
                      <th className="px-6 py-4 text-[11px] font-black uppercase text-on-surface-variant">Học kỳ</th>
                      <th className="px-6 py-4 text-[11px] font-black uppercase text-on-surface-variant">Môn học</th>
                      <th className="px-4 py-4 text-[11px] font-black uppercase text-on-surface-variant text-center">TC</th>
                      <th className="px-4 py-4 text-[11px] font-black uppercase text-on-surface-variant text-center">Điểm hệ 4</th>
                      <th className="px-4 py-4 text-[11px] font-black uppercase text-on-surface-variant text-center">Điểm chữ</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-outline-variant/10">
                    {flatRows.length === 0 ? (
                      <tr>
                        <td colSpan={5} className="px-6 py-8 text-sm text-on-surface-variant text-center">
                          Chưa có dữ liệu đăng ký hoặc chưa có điểm.
                        </td>
                      </tr>
                    ) : (
                      flatRows.map((row) => (
                        <tr key={row.idDangKy} className="hover:bg-surface-container-low/40">
                          <td className="px-6 py-4 text-xs font-medium text-on-surface-variant">{row.tenHocKy}</td>
                          <td className="px-6 py-4">
                            <p className="font-bold text-on-surface text-sm">{row.tenHocPhan}</p>
                            <p className="text-[10px] text-on-surface-variant font-mono">{row.maHocPhan} · {row.maLopHp}</p>
                          </td>
                          <td className="px-4 py-4 text-center text-sm">{row.soTinChi ?? '—'}</td>
                          <td className="px-4 py-4 text-center text-sm font-semibold">
                            {row.daCoDiem ? formatGpa(row.diemHe4) : '—'}
                          </td>
                          <td className="px-4 py-4 text-center text-sm">{row.diemChu || '—'}</td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>
              <div className="p-6 border-t border-outline-variant/10 text-xs text-on-surface-variant">
                * Chỉ tính GPA cho các học phần đã có điểm hệ 4 trong bảng <code className="font-mono">Bang_Diem_Mon</code>.
              </div>
            </section>
          </>
        )}
      </div>
    </main>
  );
};

export default KimTraTinHcTpTranscriptDashboard;
