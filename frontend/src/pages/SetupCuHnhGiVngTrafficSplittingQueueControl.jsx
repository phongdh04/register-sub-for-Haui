import React, { useEffect, useMemo, useState } from 'react';

import { API_BASE_URL, authHeaders } from '../config/api';

const fmtTs = (s) => {
  if (!s) return '-';
  const d = new Date(s);
  if (Number.isNaN(d.getTime())) return String(s);
  return d.toLocaleString('vi-VN');
};

const STATUS_STYLE = {
  QUEUED: 'bg-slate-100 text-slate-700',
  RUNNING: 'bg-blue-100 text-blue-700',
  COMPLETED: 'bg-emerald-100 text-emerald-700',
  FAILED: 'bg-red-100 text-red-700',
  TIMEOUT: 'bg-amber-100 text-amber-700'
};

const SetupCuHnhGiVngTrafficSplittingQueueControl = () => {
  const [hocKys, setHocKys] = useState([]);
  const [selectedHocKyId, setSelectedHocKyId] = useState('');
  const [loadingHocKy, setLoadingHocKy] = useState(true);
  const [busy, setBusy] = useState(false);
  const [msg, setMsg] = useState('');
  const [err, setErr] = useState('');
  const [autoRefresh, setAutoRefresh] = useState(true);
  const [currentJob, setCurrentJob] = useState(null);
  const [history, setHistory] = useState([]);

  useEffect(() => {
    let mounted = true;
    const run = async () => {
      setLoadingHocKy(true);
      setErr('');
      try {
        const res = await fetch(`${API_BASE_URL}/api/hoc-ky`, { headers: authHeaders() });
        const body = await res.json().catch(() => []);
        if (!res.ok) throw new Error(body.message || 'Không tải được danh sách học kỳ.');
        if (!mounted) return;
        const rows = Array.isArray(body) ? body : [];
        setHocKys(rows);
        if (rows.length > 0) setSelectedHocKyId(String(rows[0].idHocKy));
      } catch (e) {
        if (mounted) setErr(e.message || 'Lỗi tải học kỳ.');
      } finally {
        if (mounted) setLoadingHocKy(false);
      }
    };
    run();
    return () => {
      mounted = false;
    };
  }, []);

  useEffect(() => {
    if (!autoRefresh || !currentJob?.jobId || !selectedHocKyId) return;
    const done = ['COMPLETED', 'FAILED', 'TIMEOUT'].includes(currentJob.status);
    if (done) return;
    const timer = setInterval(() => {
      refreshJob(currentJob.jobId);
    }, 5000);
    return () => clearInterval(timer);
  }, [autoRefresh, currentJob, selectedHocKyId]);

  const selectedHocKyLabel = useMemo(() => {
    const hit = hocKys.find((h) => String(h.idHocKy) === String(selectedHocKyId));
    return hit ? `HK${hit.kyThu} ${hit.namHoc}` : '-';
  }, [hocKys, selectedHocKyId]);

  const pushHistory = (entry) => {
    setHistory((prev) => [entry, ...prev].slice(0, 20));
  };

  const refreshJob = async (jobId) => {
    if (!selectedHocKyId || !jobId) return;
    try {
      const res = await fetch(
        `${API_BASE_URL}/api/v1/admin/scheduling/hoc-ky/${selectedHocKyId}/solver/jobs/${jobId}`,
        { headers: authHeaders() }
      );
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || 'Không tải được trạng thái job.');
      setCurrentJob(body);
      pushHistory({
        jobId: body.jobId,
        mode: 'FULL_ASYNC',
        status: body.status,
        startedAt: body.startedAt || body.submittedAt,
        finishedAt: body.finishedAt
      });
    } catch (e) {
      setErr(e.message || 'Lỗi refresh job.');
    }
  };

  const runDry = async () => {
    if (!selectedHocKyId) return;
    setBusy(true);
    setErr('');
    setMsg('');
    try {
      const res = await fetch(
        `${API_BASE_URL}/api/v1/admin/scheduling/hoc-ky/${selectedHocKyId}/solver/dry-run`,
        { method: 'POST', headers: authHeaders(), body: JSON.stringify({ scope: 'PER_HOC_KY' }) }
      );
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || 'Dry run thất bại.');
      setMsg(`Dry run: ${body.status} - ${body.detailMessage || ''}`);
      pushHistory({
        jobId: `dry-${Date.now()}`,
        mode: 'DRY_RUN',
        status: body.status === 'INTERNAL_ERROR' ? 'FAILED' : 'COMPLETED',
        startedAt: new Date().toISOString(),
        finishedAt: new Date().toISOString()
      });
    } catch (e) {
      setErr(e.message || 'Lỗi dry run.');
    } finally {
      setBusy(false);
    }
  };

  const runMvp = async () => {
    if (!selectedHocKyId) return;
    setBusy(true);
    setErr('');
    setMsg('');
    try {
      const res = await fetch(
        `${API_BASE_URL}/api/v1/admin/scheduling/hoc-ky/${selectedHocKyId}/solver/mvp-run`,
        {
          method: 'POST',
          headers: authHeaders(),
          body: JSON.stringify({ scope: 'PER_HOC_KY', persist: false })
        }
      );
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || 'MVP run thất bại.');
      setMsg(`MVP: ${body.outcome} - ${body.message || ''}`);
      pushHistory({
        jobId: `mvp-${Date.now()}`,
        mode: 'MVP_RUN',
        status: body.outcome === 'INTERNAL_ERROR' ? 'FAILED' : 'COMPLETED',
        startedAt: new Date().toISOString(),
        finishedAt: new Date().toISOString()
      });
    } catch (e) {
      setErr(e.message || 'Lỗi MVP run.');
    } finally {
      setBusy(false);
    }
  };

  const runFullAsync = async () => {
    if (!selectedHocKyId) return;
    setBusy(true);
    setErr('');
    setMsg('');
    try {
      const res = await fetch(
        `${API_BASE_URL}/api/v1/admin/scheduling/hoc-ky/${selectedHocKyId}/solver/run`,
        {
          method: 'POST',
          headers: authHeaders(),
          body: JSON.stringify({ scope: 'PER_HOC_KY', persist: true })
        }
      );
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || 'Start async run thất bại.');
      setMsg(`Đã tạo job ${body.jobId}`);
      const seedJob = {
        jobId: body.jobId,
        hocKyId: Number(selectedHocKyId),
        status: body.status || 'QUEUED',
        submittedAt: body.submittedAt,
        startedAt: null,
        finishedAt: null,
        detailMessage: 'QUEUED'
      };
      setCurrentJob(seedJob);
      pushHistory({
        jobId: body.jobId,
        mode: 'FULL_ASYNC',
        status: body.status || 'QUEUED',
        startedAt: body.submittedAt,
        finishedAt: null
      });
      await refreshJob(body.jobId);
    } catch (e) {
      setErr(e.message || 'Lỗi chạy đầy đủ.');
    } finally {
      setBusy(false);
    }
  };

  return (
    <main className="min-h-screen p-8 bg-[#f9f9ff]">
      <div className="max-w-7xl mx-auto space-y-8">
        <header>
          <h1 className="text-3xl font-extrabold text-primary">Chạy thuật toán xếp lịch</h1>
          <p className="text-sm text-on-surface-variant mt-1">
            Điều phối solver theo học kỳ, theo dõi trạng thái job, và xem lịch sử chạy gần đây.
          </p>
        </header>

        {err && <div className="rounded-lg bg-red-50 border border-red-200 text-red-800 px-4 py-3 text-sm">{err}</div>}
        {msg && <div className="rounded-lg bg-emerald-50 border border-emerald-200 text-emerald-800 px-4 py-3 text-sm">{msg}</div>}

        <section className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2 bg-white rounded-xl border border-[#dce2f7] p-6 space-y-5">
            <label className="block text-sm font-semibold text-[#334155]">
              Học kỳ áp dụng
              <select
                className="mt-2 w-full border border-[#dce2f7] rounded-lg px-3 py-2"
                value={selectedHocKyId}
                onChange={(e) => setSelectedHocKyId(e.target.value)}
                disabled={loadingHocKy || busy}
              >
                {hocKys.map((h) => (
                  <option key={h.idHocKy} value={h.idHocKy}>
                    HK{h.kyThu} - {h.namHoc}
                  </option>
                ))}
              </select>
            </label>
            <div className="flex flex-wrap gap-3">
              <button
                type="button"
                disabled={!selectedHocKyId || busy}
                onClick={runDry}
                className="px-5 py-2.5 rounded-full bg-slate-200 text-slate-700 font-bold disabled:opacity-50"
              >
                Dry Run
              </button>
              <button
                type="button"
                disabled={!selectedHocKyId || busy}
                onClick={runMvp}
                className="px-5 py-2.5 rounded-full bg-amber-200 text-amber-900 font-bold disabled:opacity-50"
              >
                MVP Run
              </button>
              <button
                type="button"
                disabled={!selectedHocKyId || busy}
                onClick={runFullAsync}
                className="px-5 py-2.5 rounded-full bg-primary text-white font-bold disabled:opacity-50"
              >
                Chạy đầy đủ
              </button>
            </div>
          </div>

          <div className="bg-[#293040] text-white rounded-xl p-6 space-y-4">
            <div className="flex justify-between items-start">
              <div>
                <p className="text-[10px] uppercase tracking-widest text-slate-300">Job hiện tại</p>
                <p className="font-mono text-sm font-bold mt-1">{currentJob?.jobId || '-'}</p>
              </div>
              <span className={`text-[10px] px-2 py-1 rounded-full font-bold ${STATUS_STYLE[currentJob?.status] || 'bg-slate-700 text-slate-200'}`}>
                {currentJob?.status || 'IDLE'}
              </span>
            </div>
            <div className="text-xs text-slate-300 space-y-1">
              <p>Học kỳ: {selectedHocKyLabel}</p>
              <p>Bắt đầu: {fmtTs(currentJob?.startedAt || currentJob?.submittedAt)}</p>
              <p>Kết thúc: {fmtTs(currentJob?.finishedAt)}</p>
            </div>
            <div className="flex items-center justify-between pt-2 border-t border-slate-700">
              <button
                type="button"
                onClick={() => currentJob?.jobId && refreshJob(currentJob.jobId)}
                className="text-xs font-bold hover:text-blue-300"
              >
                Làm mới
              </button>
              <label className="flex items-center gap-2 text-xs">
                Auto 5s
                <input
                  type="checkbox"
                  checked={autoRefresh}
                  onChange={(e) => setAutoRefresh(e.target.checked)}
                />
              </label>
            </div>
          </div>
        </section>

        <section className="bg-white rounded-xl border border-[#dce2f7] overflow-hidden">
          <div className="px-6 py-4 border-b border-[#eef1fa]">
            <h2 className="text-lg font-bold">Lịch sử job</h2>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full min-w-[900px] text-left">
              <thead className="bg-[#f8faff] text-xs uppercase text-slate-500">
                <tr>
                  <th className="px-6 py-3">Job ID</th>
                  <th className="px-6 py-3">Loại chạy</th>
                  <th className="px-6 py-3">Trạng thái</th>
                  <th className="px-6 py-3">Bắt đầu</th>
                  <th className="px-6 py-3">Kết thúc</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-[#eef1fa]">
                {history.length === 0 ? (
                  <tr>
                    <td className="px-6 py-4 text-sm text-slate-500" colSpan={5}>
                      Chưa có lịch sử chạy trong phiên hiện tại.
                    </td>
                  </tr>
                ) : (
                  history.map((h) => (
                    <tr key={`${h.jobId}-${h.startedAt || ''}`}>
                      <td className="px-6 py-4 text-sm font-mono text-primary">{h.jobId}</td>
                      <td className="px-6 py-4 text-sm">{h.mode}</td>
                      <td className="px-6 py-4 text-sm">
                        <span className={`text-[10px] px-2 py-1 rounded-full font-bold ${STATUS_STYLE[h.status] || 'bg-slate-100 text-slate-600'}`}>
                          {h.status}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-sm text-slate-600">{fmtTs(h.startedAt)}</td>
                      <td className="px-6 py-4 text-sm text-slate-600">{fmtTs(h.finishedAt)}</td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </section>
      </div>
    </main>
  );
};

export default SetupCuHnhGiVngTrafficSplittingQueueControl;
