import React from 'react';

const KimTraTinHcTpTranscriptDashboard = () => {
  return (
    <>
      
{/*  SideNavBar (Shared Component)  */}

{/*  Main Content Canvas  */}
<main className=" min-h-screen">
{/*  TopAppBar (Shared Component)  */}

<div className="p-8 max-w-7xl mx-auto space-y-12">
{/*  Page Header & Semester Selector  */}
<section className="flex flex-col md:flex-row justify-between items-end gap-6">
<div className="space-y-2">
<span className="text-primary font-bold tracking-widest text-xs uppercase">Academic Records</span>
<h2 className="text-4xl font-extrabold tracking-tight text-on-surface font-headline">Bảng Điểm Sinh Viên</h2>
<p className="text-on-surface-variant max-w-md">Theo dõi tiến độ học tập và kết quả các học kỳ trong suốt quá trình đào tạo.</p>
</div>
<div className="relative group">
<label className="block text-[10px] font-bold text-slate-500 uppercase tracking-wider mb-2 ml-1">Chọn học kỳ</label>
<div className="flex items-center gap-3 px-5 py-3 bg-surface-container-lowest shadow-sm rounded-xl cursor-pointer hover:bg-surface-container-low transition-colors min-w-[240px]">
<span className="material-symbols-outlined text-primary">calendar_today</span>
<span className="flex-1 font-semibold text-sm">Học kỳ 1 2025-2026</span>
<span className="material-symbols-outlined text-slate-400">expand_more</span>
</div>
</div>
</section>
{/*  GPA Summary Cards (Bento Style)  */}
<section className="grid grid-cols-1 md:grid-cols-3 gap-6">
{/*  Semester GPA  */}
<div className="bg-surface-container-lowest p-8 rounded-xl relative overflow-hidden group">
<div className="absolute top-0 right-0 p-4 opacity-10 group-hover:scale-110 transition-transform">
<span className="material-symbols-outlined text-7xl text-primary">analytics</span>
</div>
<p className="text-sm font-semibold text-on-surface-variant mb-1">GPA Học Kỳ</p>
<div className="flex items-baseline gap-2">
<h3 className="text-5xl font-black text-primary font-headline">3.82</h3>
<span className="text-xs font-bold text-slate-400">/ 4.0</span>
</div>
<div className="mt-4 flex items-center gap-2">
<span className="flex items-center text-xs font-bold text-emerald-600 bg-emerald-50 px-2 py-1 rounded-full">
<span className="material-symbols-outlined text-sm">trending_up</span> 0.15
                        </span>
<span className="text-[10px] text-slate-400 font-medium">so với kỳ trước</span>
</div>
</div>
{/*  Cumulative GPA  */}
<div className="bg-primary bg-gradient-to-br from-primary to-primary-container p-8 rounded-xl text-white shadow-xl shadow-primary/20 relative overflow-hidden">
<div className="absolute -bottom-6 -right-6 w-32 h-32 bg-white/10 rounded-full blur-2xl"></div>
<p className="text-sm font-medium text-white/70 mb-1">GPA Tích Lũy</p>
<div className="flex items-baseline gap-2">
<h3 className="text-5xl font-black font-headline">3.65</h3>
<span className="text-xs font-bold text-white/50">/ 4.0</span>
</div>
<div className="mt-6 h-1.5 w-full bg-white/20 rounded-full overflow-hidden">
<div className="h-full bg-secondary-container w-[91%] rounded-full shadow-[0_0_10px_rgba(254,166,25,0.5)]"></div>
</div>
<p className="mt-3 text-[10px] font-bold uppercase tracking-wider text-white/60">Xếp loại: Giỏi</p>
</div>
{/*  Credits Earned  */}
<div className="bg-surface-container-lowest p-8 rounded-xl relative overflow-hidden group">
<div className="absolute top-0 right-0 p-4 opacity-10 group-hover:scale-110 transition-transform">
<span className="material-symbols-outlined text-7xl text-secondary">verified</span>
</div>
<p className="text-sm font-semibold text-on-surface-variant mb-1">Tín Chỉ Tích Lũy</p>
<div className="flex items-baseline gap-2">
<h3 className="text-5xl font-black text-secondary font-headline">94</h3>
<span className="text-xs font-bold text-slate-400">/ 132</span>
</div>
<p className="mt-4 text-xs text-on-surface-variant font-medium">Còn 38 tín chỉ để tốt nghiệp</p>
</div>
</section>
{/*  Main Table Section  */}
<section className="bg-surface-container-lowest rounded-xl shadow-sm overflow-hidden">
<div className="px-8 py-6 flex items-center justify-between bg-surface-container-low/50">
<h4 className="font-bold text-on-surface flex items-center gap-2">
<span className="material-symbols-outlined text-primary">list_alt</span>
                        Chi Tiết Kết Quả Học Tập
                    </h4>
<div className="flex gap-2">
<button className="px-4 py-2 text-xs font-bold text-primary hover:bg-primary/5 rounded-lg transition-colors flex items-center gap-2">
<span className="material-symbols-outlined text-sm">download</span> Tải PDF
                        </button>
<button className="px-4 py-2 text-xs font-bold text-primary hover:bg-primary/5 rounded-lg transition-colors flex items-center gap-2">
<span className="material-symbols-outlined text-sm">print</span> In Bảng Điểm
                        </button>
</div>
</div>
<div className="overflow-x-auto">
<table className="w-full border-collapse">
<thead>
<tr className="bg-surface-container-low text-left">
<th className="px-8 py-4 text-[11px] font-black uppercase tracking-wider text-on-surface-variant">Tên Học Phần</th>
<th className="px-4 py-4 text-[11px] font-black uppercase tracking-wider text-on-surface-variant text-center">Tín Chỉ</th>
<th className="px-4 py-4 text-[11px] font-black uppercase tracking-wider text-on-surface-variant text-center">Chuyên Cần</th>
<th className="px-4 py-4 text-[11px] font-black uppercase tracking-wider text-on-surface-variant text-center">Giữa Kỳ</th>
<th className="px-4 py-4 text-[11px] font-black uppercase tracking-wider text-on-surface-variant text-center">Cuối Kỳ</th>
<th className="px-4 py-4 text-[11px] font-black uppercase tracking-wider text-on-surface-variant text-center">Tổng Điểm</th>
<th className="px-4 py-4 text-[11px] font-black uppercase tracking-wider text-on-surface-variant text-center">Kết Quả</th>
<th className="px-8 py-4 text-[11px] font-black uppercase tracking-wider text-on-surface-variant">Ghi Chú</th>
</tr>
</thead>
<tbody className="divide-y divide-surface-container-low">
{/*  Course 1  */}
<tr className="hover:bg-slate-50/50 transition-colors">
<td className="px-8 py-5">
<p className="font-bold text-on-surface text-sm">Cấu trúc dữ liệu và Giải thuật</p>
<p className="text-[10px] text-slate-400">IT3011 - Nhóm 12</p>
</td>
<td className="px-4 py-5 text-center text-sm font-medium">3</td>
<td className="px-4 py-5 text-center text-sm">10.0</td>
<td className="px-4 py-5 text-center text-sm">8.5</td>
<td className="px-4 py-5 text-center text-sm">9.0</td>
<td className="px-4 py-5 text-center">
<div className="inline-flex flex-col items-center">
<span className="text-sm font-bold text-primary">9.0</span>
<span className="text-[10px] font-black text-slate-400">A</span>
</div>
</td>
<td className="px-4 py-5 text-center">
<span className="inline-flex px-3 py-1 rounded-full bg-primary-fixed text-on-primary-fixed text-[10px] font-bold uppercase tracking-wide">Pass</span>
</td>
<td className="px-8 py-5 text-xs text-on-surface-variant italic">Hoàn thành xuất sắc</td>
</tr>
{/*  Course 2  */}
<tr className="hover:bg-slate-50/50 transition-colors">
<td className="px-8 py-5">
<p className="font-bold text-on-surface text-sm">Kiến trúc Máy tính</p>
<p className="text-[10px] text-slate-400">IT3022 - Nhóm 04</p>
</td>
<td className="px-4 py-5 text-center text-sm font-medium">3</td>
<td className="px-4 py-5 text-center text-sm">9.0</td>
<td className="px-4 py-5 text-center text-sm">7.0</td>
<td className="px-4 py-5 text-center text-sm">8.0</td>
<td className="px-4 py-5 text-center">
<div className="inline-flex flex-col items-center">
<span className="text-sm font-bold text-primary">7.8</span>
<span className="text-[10px] font-black text-slate-400">B+</span>
</div>
</td>
<td className="px-4 py-5 text-center">
<span className="inline-flex px-3 py-1 rounded-full bg-primary-fixed text-on-primary-fixed text-[10px] font-bold uppercase tracking-wide">Pass</span>
</td>
<td className="px-8 py-5 text-xs text-on-surface-variant italic">—</td>
</tr>
{/*  Course 3  */}
<tr className="hover:bg-slate-50/50 transition-colors">
<td className="px-8 py-5">
<p className="font-bold text-on-surface text-sm">Xác suất Thống kê</p>
<p className="text-[10px] text-slate-400">MA2031 - Nhóm 08</p>
</td>
<td className="px-4 py-5 text-center text-sm font-medium">3</td>
<td className="px-4 py-5 text-center text-sm">10.0</td>
<td className="px-4 py-5 text-center text-sm">9.5</td>
<td className="px-4 py-5 text-center text-sm">9.5</td>
<td className="px-4 py-5 text-center">
<div className="inline-flex flex-col items-center">
<span className="text-sm font-bold text-primary">9.6</span>
<span className="text-[10px] font-black text-slate-400">A+</span>
</div>
</td>
<td className="px-4 py-5 text-center">
<span className="inline-flex px-3 py-1 rounded-full bg-primary-fixed text-on-primary-fixed text-[10px] font-bold uppercase tracking-wide">Pass</span>
</td>
<td className="px-8 py-5 text-xs text-on-surface-variant italic">Khen thưởng cấp viện</td>
</tr>
{/*  Course 4  */}
<tr className="hover:bg-slate-50/50 transition-colors">
<td className="px-8 py-5">
<p className="font-bold text-on-surface text-sm">Triết học Mác-Lênin</p>
<p className="text-[10px] text-slate-400">SS1011 - Nhóm 25</p>
</td>
<td className="px-4 py-5 text-center text-sm font-medium">2</td>
<td className="px-4 py-5 text-center text-sm">8.0</td>
<td className="px-4 py-5 text-center text-sm">6.5</td>
<td className="px-4 py-5 text-center text-sm">7.0</td>
<td className="px-4 py-5 text-center">
<div className="inline-flex flex-col items-center">
<span className="text-sm font-bold text-primary">7.0</span>
<span className="text-[10px] font-black text-slate-400">B</span>
</div>
</td>
<td className="px-4 py-5 text-center">
<span className="inline-flex px-3 py-1 rounded-full bg-primary-fixed text-on-primary-fixed text-[10px] font-bold uppercase tracking-wide">Pass</span>
</td>
<td className="px-8 py-5 text-xs text-on-surface-variant italic">—</td>
</tr>
</tbody>
</table>
</div>
<div className="p-6 bg-surface-container-lowest border-t border-surface-container-low flex justify-between items-center">
<p className="text-xs text-on-surface-variant italic font-medium">
                        * Ghi chú: Điểm tổng kết hệ 4 được tính dựa trên quy chế đào tạo hiện hành.
                    </p>
<div className="flex items-center gap-4">
<div className="text-right">
<span className="block text-[10px] font-black text-slate-400 uppercase tracking-widest">Trung bình học kỳ</span>
<span className="text-lg font-black text-primary">8.48 (Hệ 10)</span>
</div>
</div>
</div>
</section>
{/*  Bottom Layout Visual: Performance Chart Placeholder  */}
<section className="grid grid-cols-1 md:grid-cols-2 gap-8">
<div className="bg-surface-container-low p-8 rounded-xl border-none">
<h3 className="font-bold mb-6 flex items-center gap-2">
<span className="material-symbols-outlined text-secondary">trending_up</span>
                        Phân tích kết quả học tập
                    </h3>
<div className="space-y-6">
<div className="flex justify-between items-end gap-2 h-40">
<div className="w-12 bg-primary/20 rounded-t-lg relative group">
<div className="absolute inset-x-0 bottom-0 bg-primary rounded-t-lg" style={{ /* FIXME: convert style string to object -> height: 75%; */ }}></div>
<span className="absolute -top-6 left-1/2 -translate-x-1/2 text-[10px] font-bold">3.2</span>
<span className="absolute -bottom-6 left-1/2 -translate-x-1/2 text-[10px] text-slate-400 font-bold whitespace-nowrap">K1 23-24</span>
</div>
<div className="w-12 bg-primary/20 rounded-t-lg relative group">
<div className="absolute inset-x-0 bottom-0 bg-primary rounded-t-lg" style={{ /* FIXME: convert style string to object -> height: 82%; */ }}></div>
<span className="absolute -top-6 left-1/2 -translate-x-1/2 text-[10px] font-bold">3.45</span>
<span className="absolute -bottom-6 left-1/2 -translate-x-1/2 text-[10px] text-slate-400 font-bold whitespace-nowrap">K2 23-24</span>
</div>
<div className="w-12 bg-primary/20 rounded-t-lg relative group">
<div className="absolute inset-x-0 bottom-0 bg-primary rounded-t-lg" style={{ /* FIXME: convert style string to object -> height: 88%; */ }}></div>
<span className="absolute -top-6 left-1/2 -translate-x-1/2 text-[10px] font-bold">3.67</span>
<span className="absolute -bottom-6 left-1/2 -translate-x-1/2 text-[10px] text-slate-400 font-bold whitespace-nowrap">K1 24-25</span>
</div>
<div className="w-12 bg-primary/20 rounded-t-lg relative group">
<div className="absolute inset-x-0 bottom-0 bg-primary rounded-t-lg" style={{ /* FIXME: convert style string to object -> height: 94%; */ }}></div>
<span className="absolute -top-6 left-1/2 -translate-x-1/2 text-[10px] font-bold">3.82</span>
<span className="absolute -bottom-6 left-1/2 -translate-x-1/2 text-[10px] text-slate-400 font-bold whitespace-nowrap">K2 24-25</span>
</div>
<div className="w-12 bg-secondary/20 rounded-t-lg relative group">
<div className="absolute inset-x-0 bottom-0 bg-secondary rounded-t-lg" style={{ /* FIXME: convert style string to object -> height: 60%; */ }}></div>
<span className="absolute -top-6 left-1/2 -translate-x-1/2 text-[10px] font-bold">T.Trình</span>
<span className="absolute -bottom-6 left-1/2 -translate-x-1/2 text-[10px] text-slate-400 font-bold whitespace-nowrap">Hiện tại</span>
</div>
</div>
</div>
</div>
<div className="bg-surface-container-lowest p-8 rounded-xl relative overflow-hidden flex items-center gap-8">
<div className="w-32 h-32 flex-shrink-0">
<svg className="w-full h-full rotate-[-90deg]" viewbox="0 0 36 36">
<path d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" fill="none" stroke="#e9edff" stroke-width="3"></path>
<path d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" fill="none" stroke="#00288e" stroke-dasharray="71, 100" stroke-width="3"></path>
</svg>
<div className="absolute left-[calc(2rem+1.5rem)] top-1/2 -translate-y-1/2 text-center w-32 ml-4">
<span className="block text-2xl font-black text-on-surface">71%</span>
<span className="text-[8px] uppercase tracking-wider font-bold text-slate-400">Hoàn thành</span>
</div>
</div>
<div>
<h4 className="font-bold text-on-surface mb-2">Lộ trình đào tạo</h4>
<p className="text-sm text-on-surface-variant mb-4">Bạn đã hoàn thành 94 trên tổng số 132 tín chỉ của chương trình Cử nhân Công nghệ Thông tin.</p>
<button className="px-6 py-2 bg-surface-container-high text-primary font-bold text-xs rounded-full hover:bg-primary hover:text-white transition-all">
                            Xem chi tiết lộ trình
                        </button>
</div>
</div>
</section>
</div>
{/*  FAB for Mobile View / Quick Actions  */}
<button className="fixed bottom-8 right-8 w-14 h-14 bg-primary text-white rounded-full shadow-2xl flex items-center justify-center hover:scale-110 active:scale-95 transition-transform z-50">
<span className="material-symbols-outlined">feedback</span>
</button>
{/*  Footer  */}
<footer className=" px-8 py-10 bg-slate-100 dark:bg-slate-900 border-none">
<div className="max-w-7xl mx-auto flex flex-col md:flex-row justify-between items-center gap-6">
<div className="flex items-center gap-3 grayscale opacity-70">
<span className="material-symbols-outlined text-3xl">school</span>
<span className="text-xl font-black text-blue-900 dark:text-blue-100">EduPort</span>
</div>
<div className="text-slate-500 text-xs font-medium text-center md:text-right">
<p>© 2025 Đại học Công nghệ EduPort. All rights reserved.</p>
<p className="mt-1">Hệ thống Quản lý Đào tạo - Phiên bản 4.2.0-stable</p>
</div>
</div>
</footer>
</main>
{/*  Bottom Nav for Mobile  */}
<nav className="md:hidden fixed bottom-0 left-0 right-0 bg-white shadow-[0_-5px_20px_rgba(0,0,0,0.05)] px-6 py-3 flex justify-between items-center z-50">
<a className="flex flex-col items-center gap-1 text-slate-400" href="#">
<span className="material-symbols-outlined">dashboard</span>
<span className="text-[10px] font-bold">Home</span>
</a>
<a className="flex flex-col items-center gap-1 text-slate-400" href="#">
<span className="material-symbols-outlined">calendar_month</span>
<span className="text-[10px] font-bold">Lịch</span>
</a>
<a className="flex flex-col items-center gap-1 text-primary" href="#">
<span className="material-symbols-outlined" style={{ /* FIXME: convert style string to object -> font-variation-settings: 'FILL' 1; */ }}>grade</span>
<span className="text-[10px] font-bold">Điểm</span>
</a>
<a className="flex flex-col items-center gap-1 text-slate-400" href="#">
<span className="material-symbols-outlined">person</span>
<span className="text-[10px] font-bold">Hồ sơ</span>
</a>
</nav>

    </>
  );
};

export default KimTraTinHcTpTranscriptDashboard;
