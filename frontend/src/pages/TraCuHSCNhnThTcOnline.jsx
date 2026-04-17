import React, { useCallback, useEffect, useState } from 'react';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const fmt = (v) => (v != null && v !== '' ? String(v) : '—');

const nienKhoa = (nam) => {
  if (nam == null || nam === '') return '—';
  const n = Number(nam);
  if (!Number.isFinite(n)) return '—';
  return `${n} - ${n + 4}`;
};

const trangThaiThuTuc = (code) => {
  switch (code) {
    case 'HOAN_THANH':
      return { label: 'Hoàn thành', cls: 'bg-emerald-100 text-emerald-900' };
    case 'CHO_BO_SUNG':
      return { label: 'Chờ bổ sung', cls: 'bg-amber-100 text-amber-900' };
    case 'KHONG_AP_DUNG':
      return { label: 'Không áp dụng', cls: 'bg-slate-100 text-slate-600' };
    default:
      return { label: code || '—', cls: 'bg-slate-100 text-slate-700' };
  }
};

const TraCuHSCNhnThTcOnline = () => {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [email, setEmail] = useState('');
  const [sdt, setSdt] = useState('');
  const [diaChi, setDiaChi] = useState('');
  const [saving, setSaving] = useState(false);
  const [saveMsg, setSaveMsg] = useState('');

  const load = useCallback(async () => {
    const token = localStorage.getItem('jwt_token');
    if (!token) {
      setError('Vui lòng đăng nhập tài khoản sinh viên.');
      setProfile(null);
      setLoading(false);
      return;
    }
    setLoading(true);
    setError('');
    setSaveMsg('');
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/student-profile/me`, {
        headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' }
      });
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || 'Không tải được hồ sơ.');
      setProfile(body);
      setEmail(body.email || '');
      setSdt(body.sdt || '');
      setDiaChi(body.diaChi || '');
    } catch (e) {
      setError(e.message || 'Lỗi tải dữ liệu.');
      setProfile(null);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const saveContact = async (e) => {
    e.preventDefault();
    const token = localStorage.getItem('jwt_token');
    if (!token) return;
    if (!email.trim() && !sdt.trim() && !diaChi.trim()) {
      setError('Nhập ít nhất một trường (email, SĐT hoặc địa chỉ) trước khi lưu.');
      return;
    }
    setSaving(true);
    setError('');
    setSaveMsg('');
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/student-profile/me/contact`, {
        method: 'PATCH',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          email: email.trim() || undefined,
          sdt: sdt.trim() || undefined,
          diaChi: diaChi.trim() || undefined
        })
      });
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || 'Không lưu được.');
      setProfile(body);
      setSaveMsg('Đã cập nhật liên hệ.');
    } catch (err) {
      setError(err.message || 'Lỗi lưu.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <main className="min-h-screen bg-surface px-6 pb-12">
      <div className="max-w-5xl mx-auto pt-8 space-y-8">
        <header className="flex flex-col md:flex-row md:items-end justify-between gap-4">
          <div>
            <h1 className="text-3xl font-extrabold text-on-surface tracking-tight">Hồ sơ cá nhân</h1>
            <p className="text-on-surface-variant text-sm mt-2 max-w-xl">
              Task 2 — <code className="text-xs bg-surface-container-high px-1 rounded">GET /api/v1/student-profile/me</code>
              , <code className="text-xs bg-surface-container-high px-1 rounded">PATCH .../me/contact</code>
              . Thủ tục trực tuyến (minh họa) kèm theo phản hồi API.
            </p>
          </div>
          <button
            type="button"
            onClick={load}
            disabled={loading}
            className="px-5 py-2.5 rounded-full border border-primary text-primary font-semibold text-sm hover:bg-primary/10 disabled:opacity-50"
          >
            {loading ? 'Đang tải…' : 'Làm mới'}
          </button>
        </header>

        {error && (
          <div className="rounded-xl border border-error/30 bg-error-container/30 p-4 text-sm text-error">
            {error}
          </div>
        )}
        {saveMsg && (
          <div className="rounded-xl border border-emerald-200 bg-emerald-50 p-4 text-sm text-emerald-900">
            {saveMsg}
          </div>
        )}

        {loading && <p className="text-sm text-on-surface-variant">Đang tải hồ sơ…</p>}

        {!loading && profile && (
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <section className="lg:col-span-2 rounded-xl bg-surface-container-lowest p-6 shadow-sm border border-outline-variant/20 space-y-4">
              <h2 className="text-xl font-bold text-on-surface">{profile.hoTen}</h2>
              <p className="text-primary font-semibold text-sm">MSSV: {profile.maSinhVien}</p>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-sm">
                <div>
                  <p className="text-[10px] font-bold text-on-surface-variant uppercase tracking-wider">Lớp sinh hoạt</p>
                  <p className="font-medium">{fmt(profile.tenLop)} ({fmt(profile.maLop)})</p>
                </div>
                <div>
                  <p className="text-[10px] font-bold text-on-surface-variant uppercase tracking-wider">Khoa / Viện</p>
                  <p className="font-medium">{fmt(profile.tenKhoa)}</p>
                </div>
                <div>
                  <p className="text-[10px] font-bold text-on-surface-variant uppercase tracking-wider">Chuyên ngành</p>
                  <p className="font-medium">{fmt(profile.tenNganh)}</p>
                  <p className="text-xs text-on-surface-variant">{fmt(profile.heDaoTao)}</p>
                </div>
                <div>
                  <p className="text-[10px] font-bold text-on-surface-variant uppercase tracking-wider">Niên khóa (ước tính)</p>
                  <p className="font-medium">{nienKhoa(profile.namNhapHoc)}</p>
                </div>
              </div>
            </section>

            <section className="rounded-xl bg-[#00288e] p-6 text-white shadow-sm space-y-4">
              <h3 className="font-bold text-lg flex items-center gap-2">
                <span className="material-symbols-outlined shrink-0">supervisor_account</span>
                Cố vấn học tập
              </h3>
              {profile.tenCoVan ? (
                <>
                  <p className="text-lg font-bold">{profile.tenCoVan}</p>
                  <p className="text-sm text-white/80">{fmt(profile.emailCoVan)}</p>
                  <p className="text-sm text-white/80">{fmt(profile.sdtCoVan)}</p>
                </>
              ) : (
                <p className="text-sm text-white/70">Chưa gán cố vấn trong dữ liệu.</p>
              )}
            </section>

            <section className="lg:col-span-2 rounded-xl bg-surface-container-low p-6 border border-outline-variant/20">
              <h3 className="font-bold text-lg mb-4">Liên hệ (cập nhật được)</h3>
              <form onSubmit={saveContact} className="space-y-4 max-w-lg">
                <div>
                  <label className="block text-xs font-bold text-on-surface-variant uppercase mb-1" htmlFor="em">Email</label>
                  <input
                    id="em"
                    type="text"
                    className="w-full rounded-lg border border-outline-variant/30 px-3 py-2 text-sm"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                  />
                </div>
                <div>
                  <label className="block text-xs font-bold text-on-surface-variant uppercase mb-1" htmlFor="phone">Số điện thoại</label>
                  <input
                    id="phone"
                    type="text"
                    className="w-full rounded-lg border border-outline-variant/30 px-3 py-2 text-sm"
                    value={sdt}
                    onChange={(e) => setSdt(e.target.value)}
                  />
                </div>
                <div>
                  <label className="block text-xs font-bold text-on-surface-variant uppercase mb-1" htmlFor="addr">Địa chỉ</label>
                  <textarea
                    id="addr"
                    rows={2}
                    className="w-full rounded-lg border border-outline-variant/30 px-3 py-2 text-sm"
                    value={diaChi}
                    onChange={(e) => setDiaChi(e.target.value)}
                  />
                </div>
                <button
                  type="submit"
                  disabled={saving}
                  className="px-5 py-2 rounded-full bg-primary text-on-primary font-semibold text-sm disabled:opacity-50"
                >
                  {saving ? 'Đang lưu…' : 'Lưu liên hệ'}
                </button>
              </form>
            </section>

            <section className="rounded-xl bg-surface-container-lowest p-6 border border-outline-variant/20">
              <h3 className="font-bold text-lg mb-4 flex items-center gap-2">
                <span className="material-symbols-outlined text-primary">fingerprint</span>
                Chi tiết (đọc)
              </h3>
              <dl className="grid grid-cols-1 gap-3 text-sm">
                <div className="flex justify-between gap-2 border-b border-outline-variant/10 pb-2">
                  <dt className="text-on-surface-variant">Ngày sinh</dt>
                  <dd className="font-medium">{fmt(profile.ngaySinh)}</dd>
                </div>
                <div className="flex justify-between gap-2 border-b border-outline-variant/10 pb-2">
                  <dt className="text-on-surface-variant">Giới tính</dt>
                  <dd className="font-medium">{fmt(profile.gioiTinh)}</dd>
                </div>
                <div className="flex justify-between gap-2 border-b border-outline-variant/10 pb-2">
                  <dt className="text-on-surface-variant">CCCD</dt>
                  <dd className="font-medium">{fmt(profile.soCccd)}</dd>
                </div>
                <div className="flex justify-between gap-2 border-b border-outline-variant/10 pb-2">
                  <dt className="text-on-surface-variant">BHYT</dt>
                  <dd className="font-medium">{fmt(profile.maTheBhyt)}</dd>
                </div>
                <div className="flex justify-between gap-2">
                  <dt className="text-on-surface-variant">TK ngân hàng</dt>
                  <dd className="font-medium text-right">
                    {fmt(profile.tenNganHang)} {profile.soTkNganHang ? `· …${String(profile.soTkNganHang).slice(-4)}` : ''}
                  </dd>
                </div>
              </dl>
            </section>

            <section className="lg:col-span-3 rounded-xl bg-surface-container-lowest p-6 border border-outline-variant/20">
              <h3 className="font-bold text-lg mb-4">Thủ tục trực tuyến (minh họa)</h3>
              <ul className="space-y-3">
                {(profile.thuTucTrucTuyen || []).map((t) => {
                  const st = trangThaiThuTuc(t.trangThai);
                  return (
                    <li
                      key={t.ma}
                      className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2 rounded-lg border border-outline-variant/15 px-4 py-3"
                    >
                      <div>
                        <p className="font-semibold text-on-surface">{t.ten}</p>
                        {t.ghiChu && <p className="text-xs text-on-surface-variant mt-1">{t.ghiChu}</p>}
                      </div>
                      <span className={`text-[10px] font-bold uppercase px-2 py-1 rounded-full self-start ${st.cls}`}>
                        {st.label}
                      </span>
                    </li>
                  );
                })}
              </ul>
            </section>
          </div>
        )}
      </div>
    </main>
  );
};

export default TraCuHSCNhnThTcOnline;
