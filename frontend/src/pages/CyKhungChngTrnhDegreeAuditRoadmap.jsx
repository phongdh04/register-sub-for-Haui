import React, { useEffect, useMemo, useState } from 'react';

import { API_BASE_URL } from '../config/api';

const prettyKhoi = (khoi) => {
  switch (khoi) {
    case 'DAI_CUONG':
      return 'I. Khối Kiến Thức Đại Cương';
    case 'CO_SO_NGANH':
      return 'II. Khối Kiến Thức Cơ Sở Ngành';
    case 'CHUYEN_NGANH':
      return 'III. Khối Kiến Thức Chuyên Ngành';
    case 'TU_CHON':
      return 'IV. Học phần Tự chọn';
    default:
      return khoi || 'Khối kiến thức';
  }
};

const chipLoaiMon = (batBuoc) =>
  batBuoc
    ? 'px-3 py-1 bg-primary-fixed text-on-primary-fixed rounded-full text-[10px] font-bold uppercase'
    : 'px-3 py-1 bg-secondary-container text-on-secondary-container rounded-full text-[10px] font-bold uppercase';

const CyKhungChngTrnhDegreeAuditRoadmap = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const controller = new AbortController();

    const load = async () => {
      const token = localStorage.getItem('jwt_token');
      if (!token) {
        setError('Bạn chưa đăng nhập. Vui lòng đăng nhập để xem khung chương trình.');
        setLoading(false);
        return;
      }

      try {
        setLoading(true);
        setError('');

        const response = await fetch(`${API_BASE_URL}/api/v1/degree-audit/me`, {
          method: 'GET',
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          signal: controller.signal
        });

        if (!response.ok) {
          const body = await response.json().catch(() => ({}));
          throw new Error(body.message || 'Không tải được dữ liệu khung chương trình.');
        }

        const payload = await response.json();
        setData(payload);
      } catch (err) {
        if (err.name !== 'AbortError') {
          setError(err.message || 'Có lỗi xảy ra.');
        }
      } finally {
        setLoading(false);
      }
    };

    load();
    return () => controller.abort();
  }, []);

  const khois = useMemo(() => data?.khois || [], [data]);

  return (
    <>
      
{/*  SideNavBar (Authority: JSON)  */}

{/*  TopAppBar (Authority: JSON)  */}

{/*  Main Content Canvas  */}
<main className="pb-12 px-12 max-w-7xl">
{/*  Header Section: Editorial Style  */}
<section className="mb-12 flex flex-col md:flex-row md:items-end justify-between gap-8">
<div className="max-w-2xl">
<span className="text-secondary font-bold text-xs uppercase tracking-[0.2em] mb-3 block">Hệ thống EduPort</span>
<h1 className="text-5xl font-extrabold text-primary tracking-tighter leading-tight mb-4">Khung Chương Trình Đào Tạo</h1>
<div className="flex flex-wrap gap-4 items-center">
<div className="flex items-center gap-2 bg-surface-container px-3 py-1.5 rounded-full text-primary font-semibold text-sm">
<span className="material-symbols-outlined text-sm" data-icon="school">school</span>
                        {data?.tenKhoa || 'Khoa'}
                    </div>
<div className="flex items-center gap-2 bg-surface-container px-3 py-1.5 rounded-full text-primary font-semibold text-sm">
<span className="material-symbols-outlined text-sm" data-icon="verified">verified</span>
                        {data?.heDaoTao || 'Hệ đào tạo'}
                    </div>
<div className="flex items-center gap-2 bg-surface-container px-3 py-1.5 rounded-full text-primary font-semibold text-sm">
<span className="material-symbols-outlined text-sm" data-icon="computer">computer</span>
                        {data?.tenNganh || 'Ngành đào tạo'}
                    </div>
</div>
</div>
<div className="hidden lg:block relative w-48 h-48 rounded-2xl overflow-hidden shadow-2xl rotate-3">
<img alt="Academic Illustration" className="w-full h-full object-cover" data-alt="abstract composition of code lines on a screen and a classic leather-bound book, editorial photography style, moody academic lighting" src="https://lh3.googleusercontent.com/aida-public/AB6AXuAAQQUugYnrUd1nf6wwTlEs1l0p_taYYs6Pfoy_Ean4ssz0YiNmvnd4YjnK1TyKLULWhF4tjf9vLjbcT_fu88QuhbFc-2RkHvcsqzhbWk8jyfNbfQFvTv3cu-ef-EaVSGABUUshavPoMcjQUoNx_Wh25MnLQjpB0B2qsiBxoniFVDFfnause5aDXj7xqgyZFLgXgsr0MPTgZ9sc5oqJMbCPJRxQaQiyySRSFd3bKinMTet2UgMXnRZYhSkv6w_jN149W4NT9keqI_C0"/>
<div className="absolute inset-0 bg-primary/20 mix-blend-multiply"></div>
</div>
</section>
{/*  Overview Bento Grid  */}
<section className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-16">
{/*  Main Goal (Large Card)  */}
<div className="md:col-span-2 bg-surface-container-lowest p-8 rounded-xl relative overflow-hidden">
<div className="relative z-10">
<h3 className="text-xl font-bold text-primary mb-4 flex items-center gap-2">
<span className="material-symbols-outlined" data-icon="target">target</span>
                        Mục tiêu đào tạo
                    </h3>
<p className="text-on-surface-variant leading-relaxed font-body">
                        {loading ? 'Đang tải...' : (data?.mucTieu || 'Chưa có mục tiêu chương trình.')}
                    </p>
</div>
<div className="absolute -bottom-8 -right-8 opacity-5">
<span className="material-symbols-outlined text-[160px]" data-icon="architecture">architecture</span>
</div>
</div>
{/*  Stats Column  */}
<div className="space-y-6">
<div className="bg-primary-container text-on-primary p-6 rounded-xl flex items-center justify-between shadow-lg shadow-primary/10">
<div>
<p className="text-on-primary-container text-xs font-bold uppercase tracking-wider mb-1">Thời gian học</p>
<h4 className="text-3xl font-extrabold tracking-tight">{data?.thoiGianGiangDay || '—'}</h4>
<p className="text-xs mt-1 opacity-80">{data?.namApDung ? `Áp dụng từ ${data.namApDung}` : '—'}</p>
</div>
<span className="material-symbols-outlined text-4xl opacity-50" data-icon="history_edu">history_edu</span>
</div>
<div className="bg-secondary-container text-on-secondary-container p-6 rounded-xl flex items-center justify-between">
<div>
<p className="text-on-secondary-fixed-variant text-xs font-bold uppercase tracking-wider mb-1">Tổng tín chỉ</p>
<h4 className="text-3xl font-extrabold tracking-tight">{data?.tongSoTinChiToanKhoa ?? '—'} TC</h4>
<p className="text-xs mt-1 opacity-80">{data?.tongTinChiDaHoanThanh != null ? `Đã hoàn thành ${data.tongTinChiDaHoanThanh} TC` : '—'}</p>
</div>
<span className="material-symbols-outlined text-4xl opacity-50" data-icon="auto_awesome">auto_awesome</span>
</div>
</div>
</section>
{/*  Curriculum Table Section  */}
<section className="space-y-12">
{loading && (
  <div className="bg-surface-container-lowest rounded-xl p-6 text-sm font-medium text-on-surface-variant">
    Đang tải dữ liệu khung chương trình...
  </div>
)}
{!loading && error && (
  <div className="bg-error-container/40 border border-error/30 rounded-xl p-6 text-sm font-medium text-error">
    {error}
  </div>
)}
{!loading && !error && khois.map((khoi) => (
  <div className="space-y-6" key={khoi.khoiKienThuc}>
    <div className="flex items-center gap-4">
      <h3 className="text-2xl font-extrabold text-primary tracking-tight">{prettyKhoi(khoi.khoiKienThuc)}</h3>
      <div className="h-px flex-1 bg-surface-container-high"></div>
      <span className="text-xs font-bold text-outline uppercase tracking-widest">
        {khoi.tinChiDaHoanThanh}/{khoi.tongTinChi} TC
      </span>
    </div>
    <div className="bg-surface-container-lowest rounded-xl overflow-hidden shadow-sm">
      <table className="w-full border-collapse text-sm">
        <thead className="bg-surface-container-low text-on-surface-variant font-bold">
          <tr>
            <th className="text-left py-4 px-6 uppercase tracking-wider text-[11px]">Mã HP</th>
            <th className="text-left py-4 px-6 uppercase tracking-wider text-[11px]">Tên môn học</th>
            <th className="text-center py-4 px-6 uppercase tracking-wider text-[11px]">Tín chỉ</th>
            <th className="text-left py-4 px-6 uppercase tracking-wider text-[11px]">Loại môn</th>
            <th className="text-left py-4 px-6 uppercase tracking-wider text-[11px]">Trạng thái</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-surface-container">
          {(khoi.hocPhans || []).map((hp) => (
            <tr className="hover:bg-surface-container-low transition-colors group" key={hp.maHocPhan}>
              <td className="py-4 px-6 font-mono font-medium text-primary">{hp.maHocPhan}</td>
              <td className="py-4 px-6 font-semibold text-on-surface">{hp.tenHocPhan}</td>
              <td className="py-4 px-6 text-center">{hp.soTinChi ?? '-'}</td>
              <td className="py-4 px-6">
                <span className={chipLoaiMon(hp.batBuoc)}>{hp.batBuoc ? 'Bắt buộc' : 'Tự chọn'}</span>
              </td>
              <td className="py-4 px-6">
                {hp.daHoanThanh ? (
                  <span className="px-2.5 py-1 bg-primary-fixed text-on-primary-fixed rounded-full text-[10px] font-bold uppercase">
                    Đã hoàn thành
                  </span>
                ) : (
                  <span className="px-2.5 py-1 bg-surface-container text-on-surface-variant rounded-full text-[10px] font-bold uppercase">
                    Chưa đạt
                  </span>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  </div>
))}
</section>
{/*  Footer Note  */}
<footer className=" pt-8 border-t border-surface-container text-center">
<p className="text-on-surface-variant text-xs font-medium">
                © 2024 EduPort - Hệ thống Quản lý Đào tạo Đại học. Tài liệu lưu hành nội bộ.
            </p>
</footer>
</main>
{/*  Floating Action Button (FAB) suppressed on Details/Curriculum screen as per rules  */}

    </>
  );
};

export default CyKhungChngTrnhDegreeAuditRoadmap;
