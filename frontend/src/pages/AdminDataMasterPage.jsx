import React, { useCallback, useEffect, useState } from 'react';
import { API_BASE_URL, authHeaders } from '../config/api';

const safe = (x) => (Array.isArray(x) ? x : []);

/* ─── Tab definitions (config-driven CRUD) ─── */
const TABS = [
  {
    key: 'khoa', label: 'Khoa', icon: 'domain',
    api: '/api/khoa', idField: 'idKhoa',
    columns: [
      { key: 'maKhoa', label: 'Mã Khoa', mono: true },
      { key: 'tenKhoa', label: 'Tên Khoa' },
      { key: 'moTa', label: 'Mô tả' },
    ],
    fields: [
      { key: 'maKhoa', label: 'Mã Khoa', required: true, max: 20 },
      { key: 'tenKhoa', label: 'Tên Khoa', required: true, max: 200 },
      { key: 'moTa', label: 'Mô tả', type: 'textarea' },
    ],
  },
  {
    key: 'nganh', label: 'Ngành ĐT', icon: 'school',
    api: '/api/nganh-dao-tao', idField: 'idNganh',
    columns: [
      { key: 'maNganh', label: 'Mã ngành', mono: true },
      { key: 'tenNganh', label: 'Tên ngành' },
      { key: 'heDaoTao', label: 'Hệ ĐT' },
      { key: 'tenKhoa', label: 'Khoa' },
    ],
    fields: [
      { key: 'maNganh', label: 'Mã ngành', required: true, max: 20 },
      { key: 'tenNganh', label: 'Tên ngành', required: true, max: 200 },
      { key: 'heDaoTao', label: 'Hệ ĐT', type: 'select', options: ['Đại Trà', 'CLC', 'Tài Năng'] },
      { key: 'idKhoa', label: 'Khoa', type: 'ref', refTab: 'khoa', refId: 'idKhoa', refLabel: 'tenKhoa', required: true },
    ],
  },
  {
    key: 'hocphan', label: 'Học Phần', icon: 'menu_book',
    api: '/api/hoc-phan', idField: 'idHocPhan',
    columns: [
      { key: 'maHocPhan', label: 'Mã HP', mono: true },
      { key: 'tenHocPhan', label: 'Tên HP' },
      { key: 'soTinChi', label: 'TC', center: true },
      { key: 'loaiMon', label: 'Loại' },
    ],
    fields: [
      { key: 'maHocPhan', label: 'Mã HP', required: true },
      { key: 'tenHocPhan', label: 'Tên HP', required: true },
      { key: 'maIn', label: 'Mã in' },
      { key: 'soTinChi', label: 'Số tín chỉ', type: 'number', required: true, min: 1 },
      { key: 'loaiMon', label: 'Loại môn', type: 'select', options: ['BAT_BUOC', 'TU_CHON', 'DAI_CUONG', 'CHUYEN_NGANH'] },
    ],
  },
  {
    key: 'giangvien', label: 'Giảng Viên', icon: 'person',
    api: '/api/giang-vien', idField: 'idGiangVien',
    columns: [
      { key: 'maGiangVien', label: 'Mã GV', mono: true },
      { key: 'tenGiangVien', label: 'Họ tên' },
      { key: 'email', label: 'Email' },
      { key: 'hocHamHocVi', label: 'Học hàm' },
      { key: 'tenKhoa', label: 'Khoa' },
    ],
    fields: [
      { key: 'maGiangVien', label: 'Mã GV', required: true, max: 20 },
      { key: 'tenGiangVien', label: 'Họ tên GV', required: true, max: 200 },
      { key: 'email', label: 'Email' },
      { key: 'sdt', label: 'Số ĐT' },
      { key: 'hocHamHocVi', label: 'Học hàm/Học vị', type: 'select', options: ['PGS.TS', 'TS', 'ThS', 'CN', 'KS'] },
      { key: 'idKhoa', label: 'Khoa', type: 'ref', refTab: 'khoa', refId: 'idKhoa', refLabel: 'tenKhoa', required: true },
    ],
  },
  {
    key: 'phong', label: 'Phòng Học', icon: 'meeting_room',
    api: '/api/v1/admin/phong', idField: 'idPhong', paged: true,
    columns: [
      { key: 'maPhong', label: 'Mã phòng', mono: true },
      { key: 'tenPhong', label: 'Tên phòng' },
      { key: 'maCoSo', label: 'Cơ sở' },
      { key: 'loaiPhong', label: 'Loại' },
      { key: 'sucChua', label: 'Sức chứa', center: true },
      { key: 'trangThai', label: 'Trạng thái' },
    ],
    fields: [
      { key: 'maPhong', label: 'Mã phòng', required: true, max: 30 },
      { key: 'tenPhong', label: 'Tên phòng', required: true, max: 200 },
      { key: 'maCoSo', label: 'Mã cơ sở', required: true, max: 50 },
      { key: 'loaiPhong', label: 'Loại phòng', type: 'select', required: true, options: ['LY_THUYET', 'MAY_TINH', 'THI_NGHIEM_HOA', 'THI_NGHIEM_VAT_LY', 'THI_NGHIEM_SINH', 'HOC_TAT', 'KHAC'] },
      { key: 'sucChua', label: 'Sức chứa', type: 'number', required: true, min: 1 },
      { key: 'trangThai', label: 'Trạng thái', type: 'select', options: ['HOAT_DONG', 'BAO_TRI', 'NGUNG_SU_DUNG'] },
      { key: 'ghiChu', label: 'Ghi chú', type: 'textarea' },
    ],
  },
  {
    key: 'lop', label: 'Lớp HC', icon: 'groups',
    api: '/api/lop', idField: 'idLop',
    columns: [
      { key: 'maLop', label: 'Mã lớp', mono: true },
      { key: 'tenLop', label: 'Tên lớp' },
      { key: 'namNhapHoc', label: 'Năm NH', center: true },
      { key: 'tenNganh', label: 'Ngành' },
    ],
    fields: [
      { key: 'maLop', label: 'Mã lớp', required: true, max: 20 },
      { key: 'tenLop', label: 'Tên lớp', required: true, max: 100 },
      { key: 'namNhapHoc', label: 'Năm nhập học', type: 'number' },
      { key: 'idNganh', label: 'Ngành', type: 'ref', refTab: 'nganh', refId: 'idNganh', refLabel: 'tenNganh', required: true },
    ],
  },
  {
    key: 'sinhvien', label: 'Sinh Viên', icon: 'badge',
    api: '/api/sinh-vien', idField: 'idSinhVien',
    columns: [
      { key: 'maSinhVien', label: 'MSSV', mono: true },
      { key: 'hoTen', label: 'Họ tên' },
      { key: 'maLop', label: 'Lớp' },
      { key: 'tenNganh', label: 'Ngành' },
      { key: 'namNhapHoc', label: 'Năm NH', center: true },
    ],
    fields: [
      { key: 'maSinhVien', label: 'Mã SV', required: true, max: 20 },
      { key: 'hoTen', label: 'Họ tên', required: true, max: 200 },
      { key: 'idLop', label: 'Lớp HC', type: 'ref', refTab: 'lop', refId: 'idLop', refLabel: 'tenLop', required: true },
    ],
  },
  {
    key: 'ctdt', label: 'CTĐT', icon: 'auto_stories',
    api: '/api/chuong-trinh-dao-tao', idField: 'idCtdt',
    columns: [
      { key: 'tenNganh', label: 'Ngành' },
      { key: 'tongSoTinChi', label: 'Tổng TC', center: true },
      { key: 'thoiGianGiangDay', label: 'Thời gian' },
      { key: 'namApDung', label: 'Năm ÁD', center: true },
    ],
    fields: [
      { key: 'idNganh', label: 'Ngành', type: 'ref', refTab: 'nganh', refId: 'idNganh', refLabel: 'tenNganh', required: true },
      { key: 'tongSoTinChi', label: 'Tổng tín chỉ', type: 'number', required: true, min: 1 },
      { key: 'mucTieu', label: 'Mục tiêu', type: 'textarea' },
      { key: 'thoiGianGiangDay', label: 'Thời gian giảng dạy', max: 50 },
      { key: 'doiTuongTuyenSinh', label: 'Đối tượng tuyển sinh', type: 'textarea' },
      { key: 'namApDung', label: 'Năm áp dụng', type: 'number' },
    ],
  },
  {
    key: 'ctdthp', label: 'CT-HP', icon: 'link',
    api: '/api/chuong-trinh-dao-tao/hoc-phan', idField: 'idCtdtHp',
    parentApi: '/api/chuong-trinh-dao-tao', parentIdField: 'idCtdt', parentLabel: 'CTĐT',
    columns: [
      { key: 'maHocPhan', label: 'Mã HP', mono: true },
      { key: 'tenHocPhan', label: 'Tên HP' },
      { key: 'soTinChi', label: 'TC', center: true },
      { key: 'khoiKienThuc', label: 'Khối KT' },
      { key: 'batBuoc', label: 'Bắt buộc', center: true, render: (v) => v ? '✓' : '—' },
      { key: 'hocKyGoiY', label: 'HK gợi ý', center: true },
    ],
    fields: [
      { key: 'idCtdt', label: 'CTĐT', type: 'ref', refTab: 'ctdt', refId: 'idCtdt', refLabel: 'tenNganh', required: true },
      { key: 'idHocPhan', label: 'Học phần', type: 'ref', refTab: 'hocphan', refId: 'idHocPhan', refLabel: 'tenHocPhan', required: true },
      { key: 'khoiKienThuc', label: 'Khối kiến thức', type: 'select', required: true, options: ['DAI_CUONG', 'CO_SO_NGANH', 'CHUYEN_NGANH', 'TU_CHON'] },
      { key: 'batBuoc', label: 'Bắt buộc', type: 'select', options: ['true', 'false'] },
      { key: 'hocKyGoiY', label: 'Học kỳ gợi ý (1-8)', type: 'number', min: 1 },
    ],
    /* CT-HP uses POST/DELETE only, custom API paths */
    customCreate: true,
    customDeletePath: (id) => `/api/chuong-trinh-dao-tao/hoc-phan/${id}`,
  },
];

/* ─── Main Component ─── */
const AdminDataMasterPage = () => {
  const [activeTab, setActiveTab] = useState('khoa');
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(false);
  const [msg, setMsg] = useState('');
  const [err, setErr] = useState('');
  const [panelOpen, setPanelOpen] = useState(false);
  const [form, setForm] = useState({});
  const [editId, setEditId] = useState(null);
  const [saving, setSaving] = useState(false);
  const [refData, setRefData] = useState({});

  const tab = TABS.find((t) => t.key === activeTab) || TABS[0];

  /* Load ref data for dropdowns (Khoa, Ngành, Lớp, HP, CTĐT) */
  const loadRefData = useCallback(async () => {
    const refs = [
      { key: 'khoa', url: '/api/khoa' },
      { key: 'nganh', url: '/api/nganh-dao-tao' },
      { key: 'lop', url: '/api/lop' },
      { key: 'hocphan', url: '/api/hoc-phan' },
      { key: 'ctdt', url: '/api/chuong-trinh-dao-tao' },
    ];
    for (const r of refs) {
      try {
        const res = await fetch(`${API_BASE_URL}${r.url}`, { headers: authHeaders() });
        const body = await res.json().catch(() => []);
        if (res.ok) setRefData((prev) => ({ ...prev, [r.key]: safe(body) }));
      } catch { /* ignore */ }
    }
  }, []);

  useEffect(() => { loadRefData(); }, [loadRefData]);

  /* Load rows for current tab */
  /* CT-HP tab: needs parentId (idCtdt) to fetch — use first CTDT or selected */
  const [ctdtFilter, setCtdtFilter] = useState(null);
  const loadRows = useCallback(async () => {
    setLoading(true);
    setErr('');
    try {
      let url = `${API_BASE_URL}${tab.api}`;
      /* CT-HP: GET /api/chuong-trinh-dao-tao/{id}/hoc-phan */
      if (tab.key === 'ctdthp') {
        if (!ctdtFilter) { setRows([]); setLoading(false); return; }
        url = `${API_BASE_URL}/api/chuong-trinh-dao-tao/${ctdtFilter}/hoc-phan`;
      }
      const res = await fetch(url, { headers: authHeaders() });
      const body = await res.json().catch(() => []);
      if (!res.ok) throw new Error(body.message || `Lỗi ${res.status}`);
      setRows(tab.paged ? safe(body.content) : safe(body));
    } catch (e) {
      setErr(e.message);
    } finally {
      setLoading(false);
    }
  }, [tab.api, tab.paged, tab.key, ctdtFilter]);

  useEffect(() => { loadRows(); }, [loadRows]);

  /* Form helpers */
  const resetForm = () => { setPanelOpen(false); setEditId(null); setForm({}); };

  const openCreate = () => {
    const init = {};
    tab.fields.forEach((f) => { init[f.key] = ''; });
    setForm(init);
    setEditId(null);
    setPanelOpen(true);
  };

  const openEdit = (row) => {
    const init = {};
    tab.fields.forEach((f) => { init[f.key] = row[f.key] ?? ''; });
    setEditId(row[tab.idField]);
    setForm(init);
    setPanelOpen(true);
  };

  const saveForm = async () => {
    setSaving(true); setErr(''); setMsg('');
    try {
      const payload = { ...form };
      tab.fields.forEach((f) => {
        if (f.type === 'number' && payload[f.key] !== '' && payload[f.key] != null) {
          payload[f.key] = Number(payload[f.key]);
        }
        if (f.key === 'batBuoc') {
          payload[f.key] = payload[f.key] === 'true' || payload[f.key] === true;
        }
      });
      const isEdit = editId != null;
      /* CT-HP tab: always POST, no PUT */
      let url, method;
      if (tab.customCreate) {
        url = `${API_BASE_URL}${tab.api}`;
        method = 'POST';
      } else {
        url = isEdit ? `${API_BASE_URL}${tab.api}/${editId}` : `${API_BASE_URL}${tab.api}`;
        method = isEdit ? 'PUT' : 'POST';
      }
      const res = await fetch(url, { method, headers: authHeaders(), body: JSON.stringify(payload) });
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || 'Lưu thất bại.');
      setMsg(isEdit ? 'Đã cập nhật thành công.' : 'Đã tạo mới thành công.');
      resetForm();
      await loadRows();
      await loadRefData();
    } catch (e) {
      setErr(e.message);
    } finally {
      setSaving(false);
    }
  };

  const deleteRow = async (id) => {
    if (!window.confirm('Bạn chắc chắn muốn xóa?')) return;
    setErr(''); setMsg('');
    try {
      const delUrl = tab.customDeletePath
        ? `${API_BASE_URL}${tab.customDeletePath(id)}`
        : `${API_BASE_URL}${tab.api}/${id}`;
      const res = await fetch(delUrl, { method: 'DELETE', headers: authHeaders() });
      if (!res.ok && res.status !== 204) {
        const body = await res.json().catch(() => ({}));
        throw new Error(body.message || 'Xóa thất bại.');
      }
      setMsg('Đã xóa thành công.');
      await loadRows();
      await loadRefData();
    } catch (e) {
      setErr(e.message);
    }
  };

  /* Render field input */
  const renderField = (f) => {
    const val = form[f.key] ?? '';
    const base = 'mt-1 w-full bg-[#f1f3ff] rounded-xl py-2.5 px-3 text-sm border border-[#dce2f7] focus:border-[#00288e] focus:outline-none';
    if (f.type === 'textarea') {
      return <textarea rows={3} className={base} value={val} onChange={(e) => setForm((v) => ({ ...v, [f.key]: e.target.value }))} />;
    }
    if (f.type === 'select') {
      return (
        <select className={base} value={val} onChange={(e) => setForm((v) => ({ ...v, [f.key]: e.target.value }))}>
          <option value="">— Chọn —</option>
          {(f.options || []).map((o) => <option key={o} value={o}>{o}</option>)}
        </select>
      );
    }
    if (f.type === 'ref') {
      const items = safe(refData[f.refTab]);
      return (
        <select className={base} value={val} onChange={(e) => setForm((v) => ({ ...v, [f.key]: e.target.value ? Number(e.target.value) : '' }))}>
          <option value="">— Chọn —</option>
          {items.map((it) => <option key={it[f.refId]} value={it[f.refId]}>{it[f.refLabel]}</option>)}
        </select>
      );
    }
    if (f.type === 'number') {
      return <input type="number" min={f.min} className={base} value={val} onChange={(e) => setForm((v) => ({ ...v, [f.key]: e.target.value }))} />;
    }
    return <input type="text" maxLength={f.max} className={base} value={val} onChange={(e) => setForm((v) => ({ ...v, [f.key]: e.target.value }))} />;
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-end justify-between gap-4">
        <div>
          <h1 className="text-3xl font-black text-[#00288e] tracking-tight">Quản lý Danh mục</h1>
          <p className="text-sm text-[#64748b] mt-1">CRUD 9 entity master data — Khoa, Ngành, Học Phần, Giảng Viên, Phòng Học, Lớp HC, Sinh Viên, CTĐT, CT-HP.</p>
        </div>
        <button type="button" onClick={openCreate} className="px-6 py-2.5 bg-[#00288e] text-white font-bold rounded-full text-sm hover:bg-[#001a5c] transition">
          <span className="material-symbols-outlined text-base align-middle mr-1">add</span>Thêm mới
        </button>
      </div>

      {/* Alerts */}
      {err && <div className="rounded-lg bg-red-50 border border-red-200 text-red-800 px-4 py-3 text-sm">{err}</div>}
      {msg && <div className="rounded-lg bg-emerald-50 border border-emerald-200 text-emerald-800 px-4 py-3 text-sm">{msg}</div>}

      {/* Tabs */}
      <div className="flex border-b border-[#dce2f7] gap-1 overflow-x-auto">
        {TABS.map((t) => (
          <button key={t.key} type="button" onClick={() => { setActiveTab(t.key); setMsg(''); setErr(''); }}
            className={`flex items-center gap-1.5 px-4 pb-3 pt-1 text-sm font-semibold whitespace-nowrap transition border-b-2 ${activeTab === t.key ? 'text-[#00288e] border-[#00288e]' : 'text-[#64748b] border-transparent hover:text-[#141b2b]'}`}>
            <span className="material-symbols-outlined text-base">{t.icon}</span>{t.label}
          </button>
        ))}
      </div>

      {/* CT-HP filter */}
      {tab.key === 'ctdthp' && (
        <div className="flex items-center gap-3 bg-[#f1f3ff] rounded-xl px-4 py-3 border border-[#dce2f7]">
          <span className="text-sm font-semibold text-[#334155]">Chọn CTĐT:</span>
          <select className="bg-white rounded-lg py-2 px-3 text-sm border border-[#dce2f7] min-w-[300px]"
            value={ctdtFilter || ''} onChange={(e) => setCtdtFilter(e.target.value ? Number(e.target.value) : null)}>
            <option value="">— Chọn CTĐT —</option>
            {safe(refData.ctdt).map((c) => <option key={c.idCtdt} value={c.idCtdt}>{c.tenNganh} (TC: {c.tongSoTinChi}, Năm: {c.namApDung || '—'})</option>)}
          </select>
        </div>
      )}

      {/* Table */}
      <div className="bg-white rounded-xl border border-[#dce2f7] shadow-sm overflow-hidden">
        <div className="px-5 py-3 bg-[#f1f3ff] flex items-center justify-between">
          <span className="text-sm font-semibold text-[#334155]">{rows.length} bản ghi</span>
          <button type="button" onClick={loadRows} className="text-xs text-[#00288e] font-semibold hover:underline">Làm mới</button>
        </div>
        {loading ? (
          <div className="p-6 text-sm text-[#64748b]">Đang tải…</div>
        ) : rows.length === 0 ? (
          <div className="p-6 text-sm text-[#64748b]">Chưa có dữ liệu.</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead>
                <tr className="bg-[#f1f3ff]">
                  {tab.columns.map((c) => (
                    <th key={c.key} className={`px-4 py-3 text-[10px] font-bold uppercase tracking-widest text-[#64748b] ${c.center ? 'text-center' : ''}`}>{c.label}</th>
                  ))}
                  <th className="px-4 py-3 text-[10px] font-bold uppercase tracking-widest text-[#64748b] text-right">Thao tác</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-[#eef1fa]">
                {rows.map((r) => (
                  <tr key={r[tab.idField]} className="hover:bg-[#f9f9ff] transition">
                    {tab.columns.map((c) => (
                      <td key={c.key} className={`px-4 py-3 ${c.mono ? 'font-mono text-[#00288e] font-semibold' : ''} ${c.center ? 'text-center' : ''}`}>
                        {c.render ? c.render(r[c.key]) : (r[c.key] ?? '—')}
                      </td>
                    ))}
                    <td className="px-4 py-3 text-right space-x-2">
                      <button type="button" onClick={() => openEdit(r)} className="px-3 py-1 text-xs rounded-lg border border-[#dce2f7] hover:bg-[#dde1ff] font-semibold">Sửa</button>
                      <button type="button" onClick={() => deleteRow(r[tab.idField])} className="px-3 py-1 text-xs rounded-lg bg-red-50 text-red-700 hover:bg-red-100 font-semibold">Xóa</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Slide Panel */}
      {panelOpen && (
        <div className="fixed inset-y-0 right-0 w-[480px] bg-white shadow-2xl z-50 border-l border-[#dce2f7] overflow-y-auto">
          <div className="sticky top-0 bg-white px-6 py-4 flex justify-between items-center border-b border-[#dce2f7]">
            <h2 className="text-xl font-black text-[#00288e]">{editId != null ? 'Cập nhật' : 'Thêm mới'} {tab.label}</h2>
            <button type="button" onClick={resetForm} className="w-8 h-8 rounded-full hover:bg-[#f1f3ff] flex items-center justify-center">
              <span className="material-symbols-outlined">close</span>
            </button>
          </div>
          <div className="p-6 space-y-4">
            {tab.fields.map((f) => (
              <label key={f.key} className="block text-sm font-semibold text-[#334155]">
                {f.label} {f.required && <span className="text-red-500">*</span>}
                {renderField(f)}
              </label>
            ))}
            <div className="pt-3 flex gap-3">
              <button type="button" disabled={saving} onClick={saveForm}
                className="flex-1 py-3 bg-[#00288e] text-white font-bold rounded-full disabled:opacity-50 hover:bg-[#001a5c] transition text-sm">
                {saving ? 'Đang lưu…' : editId != null ? 'Lưu cập nhật' : 'Xác nhận tạo mới'}
              </button>
              <button type="button" onClick={resetForm} className="px-6 py-3 bg-[#f1f3ff] text-[#334155] font-bold rounded-full text-sm">Hủy</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminDataMasterPage;
