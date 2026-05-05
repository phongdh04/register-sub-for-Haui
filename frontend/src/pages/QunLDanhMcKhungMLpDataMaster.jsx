import React, { useEffect, useMemo, useState } from 'react';

import { API_BASE_URL, authHeaders } from '../config/api';

const safeArray = (x) => (Array.isArray(x) ? x : []);

const toJsonSlots = (text) => {
  const raw = String(text || '').trim();
  if (!raw) return [];
  return JSON.parse(raw);
};

const toHocPhanIds = (text) => {
  return String(text || '')
    .split(',')
    .map((v) => Number(v.trim()))
    .filter((n) => Number.isFinite(n) && n > 0);
};

const prettyJson = (x) => JSON.stringify(x ?? [], null, 2);

const initialBlockForm = {
  idTkbBlock: null,
  maBlock: '',
  tenBlock: '',
  jsonSlotsText: '[]',
  hocPhanIdsText: '',
  batBuocChonCaBlock: false
};

const QunLDanhMcKhungMLpDataMaster = () => {
  const [activeTab, setActiveTab] = useState('block');
  const [hocKys, setHocKys] = useState([]);
  const [selectedHocKyId, setSelectedHocKyId] = useState('');
  const [msg, setMsg] = useState('');
  const [err, setErr] = useState('');

  const [blocks, setBlocks] = useState([]);
  const [loadingBlocks, setLoadingBlocks] = useState(false);
  const [blockPanelOpen, setBlockPanelOpen] = useState(false);
  const [blockForm, setBlockForm] = useState(initialBlockForm);
  const [savingBlock, setSavingBlock] = useState(false);

  const [forecastForm, setForecastForm] = useState({
    idChuongTrinhDaoTao: '',
    siSoToiDaMacDinh: '',
    heSoDuPhong: '',
    tyLeSvHocLaiThamGia: ''
  });
  const [forecastBusy, setForecastBusy] = useState(false);
  const [forecastRun, setForecastRun] = useState(null);
  const [forecastVersionBusy, setForecastVersionBusy] = useState(false);

  useEffect(() => {
    const loadHocKy = async () => {
      try {
        const res = await fetch(`${API_BASE_URL}/api/hoc-ky`, { headers: authHeaders() });
        const body = await res.json().catch(() => []);
        if (!res.ok) throw new Error(body.message || 'Không tải được danh sách học kỳ.');
        const rows = safeArray(body);
        setHocKys(rows);
        if (rows.length) setSelectedHocKyId(String(rows[0].idHocKy));
      } catch (e) {
        setErr(e.message || 'Lỗi tải học kỳ.');
      }
    };
    loadHocKy();
  }, []);

  const loadBlocks = async () => {
    if (!selectedHocKyId) return;
    setLoadingBlocks(true);
    setErr('');
    try {
      const res = await fetch(
        `${API_BASE_URL}/api/v1/admin/scheduling/hoc-ky/${selectedHocKyId}/tkb-blocks`,
        { headers: authHeaders() }
      );
      const body = await res.json().catch(() => []);
      if (!res.ok) throw new Error(body.message || 'Không tải được danh sách block.');
      setBlocks(safeArray(body));
    } catch (e) {
      setErr(e.message || 'Lỗi tải block.');
    } finally {
      setLoadingBlocks(false);
    }
  };

  useEffect(() => {
    loadBlocks();
  }, [selectedHocKyId]);

  const resetBlockForm = () => {
    setBlockForm(initialBlockForm);
    setBlockPanelOpen(false);
  };

  const openCreateBlock = () => {
    setBlockForm(initialBlockForm);
    setBlockPanelOpen(true);
  };

  const openEditBlock = (b) => {
    setBlockForm({
      idTkbBlock: b.idTkbBlock,
      maBlock: b.maBlock || '',
      tenBlock: b.tenBlock || '',
      jsonSlotsText: prettyJson(b.jsonSlots || []),
      hocPhanIdsText: safeArray(b.danhSachIdHocPhan).join(', '),
      batBuocChonCaBlock: !!b.batBuocChonCaBlock
    });
    setBlockPanelOpen(true);
  };

  const saveBlock = async () => {
    if (!selectedHocKyId) return;
    setSavingBlock(true);
    setErr('');
    setMsg('');
    try {
      const payload = {
        maBlock: blockForm.maBlock.trim(),
        tenBlock: blockForm.tenBlock.trim(),
        jsonSlots: toJsonSlots(blockForm.jsonSlotsText),
        danhSachIdHocPhan: toHocPhanIds(blockForm.hocPhanIdsText),
        batBuocChonCaBlock: !!blockForm.batBuocChonCaBlock
      };
      const isEdit = !!blockForm.idTkbBlock;
      const url = isEdit
        ? `${API_BASE_URL}/api/v1/admin/scheduling/hoc-ky/${selectedHocKyId}/tkb-blocks/${blockForm.idTkbBlock}`
        : `${API_BASE_URL}/api/v1/admin/scheduling/hoc-ky/${selectedHocKyId}/tkb-blocks`;
      const res = await fetch(url, {
        method: isEdit ? 'PUT' : 'POST',
        headers: authHeaders(),
        body: JSON.stringify(payload)
      });
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || 'Lưu block thất bại.');
      setMsg(isEdit ? 'Đã cập nhật block.' : 'Đã tạo block mới.');
      resetBlockForm();
      await loadBlocks();
    } catch (e) {
      setErr(e.message || 'Lỗi lưu block.');
    } finally {
      setSavingBlock(false);
    }
  };

  const deleteBlock = async (idTkbBlock) => {
    if (!selectedHocKyId || !window.confirm('Xóa block này?')) return;
    setErr('');
    setMsg('');
    try {
      const res = await fetch(
        `${API_BASE_URL}/api/v1/admin/scheduling/hoc-ky/${selectedHocKyId}/tkb-blocks/${idTkbBlock}`,
        { method: 'DELETE', headers: authHeaders() }
      );
      if (!res.ok && res.status !== 204) {
        const body = await res.json().catch(() => ({}));
        throw new Error(body.message || 'Xóa block thất bại.');
      }
      setMsg('Đã xóa block.');
      await loadBlocks();
    } catch (e) {
      setErr(e.message || 'Lỗi xóa block.');
    }
  };

  const runForecast = async () => {
    if (!selectedHocKyId) return;
    setForecastBusy(true);
    setErr('');
    setMsg('');
    try {
      const payload = {
        idChuongTrinhDaoTao: Number(forecastForm.idChuongTrinhDaoTao)
      };
      if (forecastForm.siSoToiDaMacDinh) payload.siSoToiDaMacDinh = Number(forecastForm.siSoToiDaMacDinh);
      if (forecastForm.heSoDuPhong) payload.heSoDuPhong = Number(forecastForm.heSoDuPhong);
      if (forecastForm.tyLeSvHocLaiThamGia) payload.tyLeSvHocLaiThamGia = Number(forecastForm.tyLeSvHocLaiThamGia);

      const res = await fetch(
        `${API_BASE_URL}/api/v1/admin/scheduling/hoc-ky/${selectedHocKyId}/forecast`,
        { method: 'POST', headers: authHeaders(), body: JSON.stringify(payload) }
      );
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || 'Forecast thất bại.');
      setForecastRun(body);
      setMsg('Đã chạy forecast.');
    } catch (e) {
      setErr(e.message || 'Lỗi forecast.');
    } finally {
      setForecastBusy(false);
    }
  };

  const runVersionAction = async (action) => {
    if (!selectedHocKyId || !forecastRun?.idDuBaoVersion) return;
    setForecastVersionBusy(true);
    setErr('');
    setMsg('');
    try {
      const res = await fetch(
        `${API_BASE_URL}/api/v1/admin/scheduling/hoc-ky/${selectedHocKyId}/forecast-versions/${forecastRun.idDuBaoVersion}/${action}`,
        { method: 'POST', headers: authHeaders() }
      );
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || `Thao tác ${action} thất bại.`);
      setMsg(`Đã ${action} version ${forecastRun.idDuBaoVersion}.`);
      if (body.trangThai) {
        setForecastRun((prev) => ({ ...prev, trangThai: body.trangThai }));
      }
    } catch (e) {
      setErr(e.message || `Lỗi thao tác ${action}.`);
    } finally {
      setForecastVersionBusy(false);
    }
  };

  const blockStats = useMemo(() => {
    const total = blocks.length;
    const mandatory = blocks.filter((b) => b.batBuocChonCaBlock).length;
    const hocPhanCount = blocks.reduce((acc, b) => acc + safeArray(b.danhSachIdHocPhan).length, 0);
    return { total, mandatory, hocPhanCount };
  }, [blocks]);

  return (
    <main className="ml-64 min-h-screen bg-background p-8">
      <div className="max-w-7xl mx-auto space-y-8">
        <div className="flex flex-col md:flex-row md:items-end justify-between gap-4">
          <div>
            <h1 className="text-4xl font-black text-primary tracking-tight">Academic Framework</h1>
            <p className="text-on-surface-variant max-w-2xl mt-1">
              Quản lý Block TKB và dự báo mở lớp theo học kỳ, kết nối trực tiếp API backend phase scheduling.
            </p>
          </div>
          <div className="flex gap-2">
            <select
              className="bg-white border border-[#dce2f7] rounded-xl px-4 py-2 text-sm font-semibold"
              value={selectedHocKyId}
              onChange={(e) => setSelectedHocKyId(e.target.value)}
            >
              {hocKys.map((h) => (
                <option key={h.idHocKy} value={h.idHocKy}>
                  HK{h.kyThu} - {h.namHoc}
                </option>
              ))}
            </select>
            <button
              type="button"
              onClick={openCreateBlock}
              className="px-6 py-2.5 bg-primary text-white font-bold rounded-full"
            >
              Tạo Block mới
            </button>
          </div>
        </div>

        {err && <div className="rounded-lg bg-red-50 border border-red-200 text-red-800 px-4 py-3 text-sm">{err}</div>}
        {msg && <div className="rounded-lg bg-emerald-50 border border-emerald-200 text-emerald-800 px-4 py-3 text-sm">{msg}</div>}

        <div className="flex border-b border-surface-container gap-10 overflow-x-auto">
          <button
            type="button"
            onClick={() => setActiveTab('block')}
            className={`pb-4 text-lg ${activeTab === 'block' ? 'text-primary font-bold border-b-2 border-primary' : 'text-on-surface-variant'}`}
          >
            Quản lý Block TKB
          </button>
          <button
            type="button"
            onClick={() => setActiveTab('forecast')}
            className={`pb-4 text-lg ${activeTab === 'forecast' ? 'text-primary font-bold border-b-2 border-primary' : 'text-on-surface-variant'}`}
          >
            Dự báo mở lớp (Forecast)
          </button>
        </div>

        {activeTab === 'block' && (
          <section className="grid grid-cols-12 gap-8">
            <div className="col-span-12 lg:col-span-8 space-y-4">
              {loadingBlocks ? (
                <div className="bg-white rounded-xl p-6 text-sm text-on-surface-variant">Đang tải block...</div>
              ) : blocks.length === 0 ? (
                <div className="bg-white rounded-xl p-6 text-sm text-on-surface-variant">Chưa có block cho học kỳ này.</div>
              ) : (
                blocks.map((b) => (
                  <div key={b.idTkbBlock} className="bg-white rounded-2xl p-6 shadow-sm border border-surface-container">
                    <div className="flex items-start justify-between gap-4">
                      <div>
                        <p className="text-xs font-bold uppercase text-primary">{b.maBlock}</p>
                        <h3 className="text-xl font-bold mt-1">{b.tenBlock}</h3>
                        <p className="text-sm text-on-surface-variant mt-2">
                          Học phần: {safeArray(b.danhSachIdHocPhan).length} | Slot mẫu: {safeArray(b.jsonSlots).length}
                        </p>
                        {b.batBuocChonCaBlock && (
                          <span className="inline-block mt-2 px-3 py-1 text-[10px] bg-secondary-container text-on-secondary-container rounded-full font-bold">
                            Bắt buộc chọn cả block
                          </span>
                        )}
                      </div>
                      <div className="flex gap-2">
                        <button type="button" onClick={() => openEditBlock(b)} className="px-3 py-1.5 text-sm rounded-lg border border-[#dce2f7]">
                          Sửa
                        </button>
                        <button type="button" onClick={() => deleteBlock(b.idTkbBlock)} className="px-3 py-1.5 text-sm rounded-lg bg-red-50 text-red-700">
                          Xóa
                        </button>
                      </div>
                    </div>
                  </div>
                ))
              )}
            </div>
            <div className="col-span-12 lg:col-span-4 space-y-4">
              <div className="bg-primary text-white p-6 rounded-2xl">
                <h4 className="text-lg font-bold">Tình trạng phân bổ block</h4>
                <div className="mt-4 space-y-2 text-sm">
                  <p>Tổng block: {blockStats.total}</p>
                  <p>Block bắt buộc: {blockStats.mandatory}</p>
                  <p>Tổng học phần trong block: {blockStats.hocPhanCount}</p>
                </div>
              </div>
              <button type="button" onClick={loadBlocks} className="w-full py-3 rounded-xl bg-white border border-[#dce2f7] font-semibold">
                Làm mới danh sách block
              </button>
            </div>
          </section>
        )}

        {activeTab === 'forecast' && (
          <section className="space-y-6">
            <div className="bg-white border border-surface-container rounded-2xl p-6 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
              <label className="text-sm font-semibold">
                idChuongTrinhDaoTao
                <input
                  type="number"
                  min={1}
                  value={forecastForm.idChuongTrinhDaoTao}
                  onChange={(e) => setForecastForm((v) => ({ ...v, idChuongTrinhDaoTao: e.target.value }))}
                  className="mt-1 w-full border border-[#dce2f7] rounded-lg px-3 py-2"
                />
              </label>
              <label className="text-sm font-semibold">
                Sĩ số tối đa mặc định
                <input
                  type="number"
                  min={1}
                  value={forecastForm.siSoToiDaMacDinh}
                  onChange={(e) => setForecastForm((v) => ({ ...v, siSoToiDaMacDinh: e.target.value }))}
                  className="mt-1 w-full border border-[#dce2f7] rounded-lg px-3 py-2"
                />
              </label>
              <label className="text-sm font-semibold">
                Hệ số dự phòng
                <input
                  type="number"
                  step="0.01"
                  value={forecastForm.heSoDuPhong}
                  onChange={(e) => setForecastForm((v) => ({ ...v, heSoDuPhong: e.target.value }))}
                  className="mt-1 w-full border border-[#dce2f7] rounded-lg px-3 py-2"
                />
              </label>
              <label className="text-sm font-semibold">
                Tỷ lệ SV học lại
                <input
                  type="number"
                  step="0.01"
                  value={forecastForm.tyLeSvHocLaiThamGia}
                  onChange={(e) => setForecastForm((v) => ({ ...v, tyLeSvHocLaiThamGia: e.target.value }))}
                  className="mt-1 w-full border border-[#dce2f7] rounded-lg px-3 py-2"
                />
              </label>
              <div className="md:col-span-2 lg:col-span-4 flex flex-wrap gap-3">
                <button type="button" disabled={forecastBusy} onClick={runForecast} className="px-5 py-2.5 bg-primary text-white rounded-full font-bold disabled:opacity-50">
                  {forecastBusy ? 'Đang chạy...' : 'Chạy forecast'}
                </button>
                <button type="button" disabled={!forecastRun?.idDuBaoVersion || forecastVersionBusy} onClick={() => runVersionAction('approve')} className="px-5 py-2.5 rounded-full bg-emerald-100 text-emerald-800 font-bold disabled:opacity-50">
                  Approve version
                </button>
                <button type="button" disabled={!forecastRun?.idDuBaoVersion || forecastVersionBusy} onClick={() => runVersionAction('reject')} className="px-5 py-2.5 rounded-full bg-amber-100 text-amber-800 font-bold disabled:opacity-50">
                  Reject version
                </button>
                <button type="button" disabled={!forecastRun?.idDuBaoVersion || forecastVersionBusy} onClick={() => runVersionAction('spawn-shell')} className="px-5 py-2.5 rounded-full bg-blue-100 text-blue-800 font-bold disabled:opacity-50">
                  Spawn shell LHP
                </button>
              </div>
            </div>

            {forecastRun && (
              <div className="bg-white border border-surface-container rounded-2xl p-6 space-y-4">
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                  <div><p className="text-xs text-on-surface-variant">Version ID</p><p className="font-bold">{forecastRun.idDuBaoVersion}</p></div>
                  <div><p className="text-xs text-on-surface-variant">Trạng thái</p><p className="font-bold">{forecastRun.trangThai}</p></div>
                  <div><p className="text-xs text-on-surface-variant">Tổng SV dự kiến</p><p className="font-bold">{forecastRun.tongSoSvDuKien ?? 0}</p></div>
                  <div><p className="text-xs text-on-surface-variant">Tổng lớp đề xuất</p><p className="font-bold">{forecastRun.tongSoLopDeXuat ?? 0}</p></div>
                </div>
                <div className="overflow-x-auto">
                  <table className="w-full min-w-[900px] text-left">
                    <thead className="bg-surface-container-low text-xs uppercase text-on-surface-variant">
                      <tr>
                        <th className="px-4 py-3">Mã HP</th>
                        <th className="px-4 py-3">Tên học phần</th>
                        <th className="px-4 py-3">HK gợi ý</th>
                        <th className="px-4 py-3">On-track</th>
                        <th className="px-4 py-3">Học lại</th>
                        <th className="px-4 py-3">SV dự kiến</th>
                        <th className="px-4 py-3">Lớp đề xuất</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-surface-container">
                      {safeArray(forecastRun.lines).map((line) => (
                        <tr key={`${line.idHocPhan}-${line.maHocPhan}`}>
                          <td className="px-4 py-3 text-sm font-mono text-primary">{line.maHocPhan}</td>
                          <td className="px-4 py-3 text-sm">{line.tenHocPhan}</td>
                          <td className="px-4 py-3 text-sm">{line.hocKyGoiYCtdt ?? '-'}</td>
                          <td className="px-4 py-3 text-sm">{line.soSvOnTrack ?? 0}</td>
                          <td className="px-4 py-3 text-sm">{line.soSvHocLai ?? 0}</td>
                          <td className="px-4 py-3 text-sm">{line.soSvDuKien ?? 0}</td>
                          <td className="px-4 py-3 text-sm font-bold">{line.soLopDeXuat ?? 0}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            )}
          </section>
        )}
      </div>

      {blockPanelOpen && (
        <div className="fixed inset-y-0 right-0 w-[520px] bg-white shadow-2xl z-50 border-l border-surface-container overflow-y-auto">
          <div className="sticky top-0 bg-white px-6 py-5 flex justify-between items-center border-b border-surface-container">
            <h2 className="text-2xl font-black text-primary">
              {blockForm.idTkbBlock ? 'Cập nhật Block' : 'Thiết lập Block mới'}
            </h2>
            <button type="button" onClick={resetBlockForm} className="w-9 h-9 rounded-full hover:bg-surface-container">
              <span className="material-symbols-outlined">close</span>
            </button>
          </div>
          <div className="p-6 space-y-4">
            <label className="block text-sm font-semibold">
              Mã Block
              <input
                className="mt-1 w-full bg-surface-container-low rounded-xl py-2.5 px-3"
                value={blockForm.maBlock}
                onChange={(e) => setBlockForm((v) => ({ ...v, maBlock: e.target.value }))}
              />
            </label>
            <label className="block text-sm font-semibold">
              Tên Block
              <input
                className="mt-1 w-full bg-surface-container-low rounded-xl py-2.5 px-3"
                value={blockForm.tenBlock}
                onChange={(e) => setBlockForm((v) => ({ ...v, tenBlock: e.target.value }))}
              />
            </label>
            <label className="block text-sm font-semibold">
              JSON slots
              <textarea
                rows={6}
                className="mt-1 w-full bg-surface-container-low rounded-xl py-2.5 px-3 font-mono text-xs"
                value={blockForm.jsonSlotsText}
                onChange={(e) => setBlockForm((v) => ({ ...v, jsonSlotsText: e.target.value }))}
              />
            </label>
            <label className="block text-sm font-semibold">
              Danh sách id học phần (phân tách dấu phẩy)
              <input
                className="mt-1 w-full bg-surface-container-low rounded-xl py-2.5 px-3"
                value={blockForm.hocPhanIdsText}
                onChange={(e) => setBlockForm((v) => ({ ...v, hocPhanIdsText: e.target.value }))}
              />
            </label>
            <label className="flex items-center gap-3 text-sm font-semibold">
              <input
                type="checkbox"
                checked={blockForm.batBuocChonCaBlock}
                onChange={(e) => setBlockForm((v) => ({ ...v, batBuocChonCaBlock: e.target.checked }))}
              />
              Bắt buộc chọn cả block
            </label>
            <div className="pt-2 flex gap-3">
              <button
                type="button"
                disabled={savingBlock}
                onClick={saveBlock}
                className="flex-1 py-3 bg-primary text-white font-bold rounded-full disabled:opacity-50"
              >
                {savingBlock ? 'Đang lưu...' : blockForm.idTkbBlock ? 'Lưu cập nhật' : 'Xác nhận tạo Block'}
              </button>
              <button type="button" onClick={resetBlockForm} className="px-6 py-3 bg-surface-container text-on-surface-variant font-bold rounded-full">
                Hủy
              </button>
            </div>
          </div>
        </div>
      )}
    </main>
  );
};

export default QunLDanhMcKhungMLpDataMaster;

