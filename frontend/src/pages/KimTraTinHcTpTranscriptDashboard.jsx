import React, { useCallback, useEffect, useMemo, useState } from 'react';

import { API_BASE_URL } from '../config/api';

const formatGpa = (v) => (v != null && v !== '' ? Number(v).toFixed(2) : '—');

const KimTraTinHcTpTranscriptDashboard = () => {
  const [allData, setAllData] = useState(null);
  const [data, setData] = useState(null);
  const [semesterId, setSemesterId] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [appealRow, setAppealRow] = useState(null);
  const [appealLyDo, setAppealLyDo] = useState('');
  const [appealBusy, setAppealBusy] = useState(false);
  const [appealMsg, setAppealMsg] = useState('');

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

  const submitAppeal = async () => {
    const token = localStorage.getItem('jwt_token');
    if (!token || !appealRow) return;
    if (appealLyDo.trim().length < 10) {
      setAppealMsg('Lý do tối thiểu 10 ký tự.');
      return;
    }
    setAppealBusy(true);
    setAppealMsg('');
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/retake-appeals`, {
        method: 'POST',
        headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
        body: JSON.stringify({ idDangKy: appealRow.idDangKy, lyDoSinhVien: appealLyDo.trim() })
      });
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || 'Không nộp được yêu cầu.');
      setAppealMsg('Đã gửi yêu cầu phúc khảo. Giảng viên sẽ xử lý trên cổng Teacher Portal.');
      setAppealRow(null);
      setAppealLyDo('');
    } catch (e) {
      setAppealMsg(e.message || 'Lỗi.');
    } finally {
      setAppealBusy(false);
    }
  };

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
            {appealMsg && (
              <div className="rounded-xl bg-primary-container/30 border border-primary/20 px-4 py-3 text-sm text-on-surface">
                {appealMsg}
              </div>
            )}
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
                      <th className="px-4 py-4 text-[11px] font-black uppercase text-on-surface-variant text-center">Phúc khảo</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-outline-variant/10">
                    {flatRows.length === 0 ? (
                      <tr>
                        <td colSpan={6} className="px-6 py-8 text-sm text-on-surface-variant text-center">
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
                            {row.diemHe4 != null ? (
                              <span>
                                {formatGpa(row.diemHe4)}
                                {!row.congBoChinhThuc && (
                                  <span className="block text-[10px] font-medium text-amber-800 mt-0.5">Chờ công bố</span>
                                )}
                              </span>
                            ) : (
                              '—'
                            )}
                          </td>
                          <td className="px-4 py-4 text-center text-sm">
                            {row.diemHe4 != null ? row.diemChu || '—' : '—'}
                          </td>
                          <td className="px-4 py-4 text-center">
                            {row.congBoChinhThuc && row.diemHe4 != null ? (
                              <button
                                type="button"
                                onClick={() => {
                                  setAppealRow(row);
                                  setAppealLyDo('');
                                  setAppealMsg('');
                                }}
                                className="text-xs font-bold text-primary hover:underline"
                              >
                                Nộp ĐK
                              </button>
                            ) : (
                              <span className="text-xs text-on-surface-variant">—</span>
                            )}
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>
              <div className="p-6 border-t border-outline-variant/10 text-xs text-on-surface-variant">
                * GPA chỉ tính điểm đã <strong>công bố chính thức</strong> (trạng thái <code className="font-mono">DA_CONG_BO</code> hoặc bản ghi cũ không trạng thái). Điểm nháp vẫn hiển thị kèm nhãn &quot;Chờ công bố&quot;.
                {' '}
                Phúc khảo: chỉ khi điểm đã công bố; mỗi học phần chỉ một yêu cầu <strong>chờ xử lý</strong> tại một thời điểm.
              </div>
            </section>

            {appealRow && (
              <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40" role="dialog" aria-modal="true">
                <div className="bg-surface-container-lowest rounded-2xl shadow-xl max-w-lg w-full p-6 space-y-4">
                  <h3 className="text-lg font-black text-on-surface">Yêu cầu phúc khảo điểm</h3>
                  <p className="text-sm text-on-surface-variant">
                    {appealRow.tenHocPhan} ({appealRow.maHocPhan}) — điểm hiện tại:{' '}
                    <strong>{formatGpa(appealRow.diemHe4)}</strong>
                  </p>
                  <label className="block text-xs font-bold text-on-surface-variant uppercase">Lý do (tối thiểu 10 ký tự)</label>
                  <textarea
                    className="w-full rounded-xl bg-surface-container border-none p-3 text-sm min-h-[120px] focus:ring-2 focus:ring-primary/30"
                    value={appealLyDo}
                    onChange={(e) => setAppealLyDo(e.target.value)}
                    placeholder="Trình bày lý do khiếu nại / xin phúc khảo…"
                  />
                  <div className="flex justify-end gap-2 pt-2">
                    <button
                      type="button"
                      className="px-4 py-2 rounded-full text-sm font-bold text-on-surface-variant hover:bg-surface-container-high"
                      onClick={() => {
                        setAppealRow(null);
                        setAppealLyDo('');
                      }}
                    >
                      Hủy
                    </button>
                    <button
                      type="button"
                      disabled={appealBusy}
                      onClick={submitAppeal}
                      className="px-5 py-2 rounded-full text-sm font-bold bg-primary text-white disabled:opacity-50"
                    >
                      {appealBusy ? 'Đang gửi…' : 'Gửi yêu cầu'}
                    </button>
                  </div>
                </div>
              </div>
            )}
          </>
        )}
      </div>
    </main>
  );
};

export default KimTraTinHcTpTranscriptDashboard;
