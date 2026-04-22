import React from 'react';

const TnhNngLcMnnhCao = () => {
  return (
    <>
      
{/*  SideNavBar (Fixed Anchor)  */}

{/*  Main Content Area  */}
<main className=" min-h-screen">
{/*  TopAppBar (Execution from JSON)  */}

{/*  Page Canvas  */}
<div className="p-12 space-y-12 max-w-7xl mx-auto">
{/*  Hero Header Section (Editorial Style)  */}
<section className="grid grid-cols-1 lg:grid-cols-2 gap-8 items-end">
<div className="space-y-4">
<span className="text-primary font-bold tracking-widest uppercase text-xs">Học kỳ 1 • 2024-2025</span>
<h2 className="text-6xl font-headline font-extrabold tracking-tight text-on-surface">Đăng ký <br/><span className="text-primary">Học phần</span></h2>
<p className="text-on-surface-variant max-w-md text-lg leading-relaxed">
                        Lựa chọn các khóa học học thuật của bạn. Hệ thống sẽ tự động kiểm tra điều kiện tiên quyết và tính toán lộ trình học tập tối ưu.
                    </p>
</div>
<div className="bg-surface-container-high p-8 rounded-xl flex items-center justify-between">
<div className="flex flex-col">
<span className="text-label-sm uppercase font-bold text-outline">Giai đoạn đăng ký</span>
<span className="text-xl font-headline font-bold text-primary">Còn lại 03 ngày 14:20</span>
</div>
<div className="w-16 h-16 rounded-full bg-primary flex items-center justify-center text-white">
<span className="material-symbols-outlined text-3xl">timer</span>
</div>
</div>
</section>
{/*  Smart Recommendations (Priority Alert)  */}
<section className="bg-secondary-fixed/30 border border-secondary-fixed p-6 rounded-xl space-y-4">
<div className="flex items-center gap-2 text-secondary font-bold">
<span className="material-symbols-outlined">priority_high</span>
<h3 className="font-headline">Môn Cần Học Lại / Cải Thiện</h3>
</div>
<div className="grid grid-cols-1 md:grid-cols-2 gap-4">
<div className="bg-white/80 dark:bg-slate-900/80 p-4 rounded-lg flex items-center justify-between shadow-sm border border-secondary-fixed/50">
<div>
<div className="flex items-center gap-2">
<span className="text-xs font-mono font-bold text-primary">MAT1101</span>
<span className="bg-error/10 text-error text-[10px] px-1.5 py-0.5 rounded font-bold">Grade: 4.2</span>
</div>
<h4 className="text-sm font-bold text-on-surface mt-1">Giải tích 1</h4>
</div>
<button className="flex items-center gap-1 bg-secondary text-white text-xs font-bold py-2 px-4 rounded-full hover:bg-secondary-fixed-variant transition-colors shadow-sm">
<span className="material-symbols-outlined text-sm">add_shopping_cart</span>
Thêm vào giỏ
</button>
</div>
<div className="bg-white/80 dark:bg-slate-900/80 p-4 rounded-lg flex items-center justify-between shadow-sm border border-secondary-fixed/50">
<div>
<div className="flex items-center gap-2">
<span className="text-xs font-mono font-bold text-primary">PHY1101</span>
<span className="bg-error/10 text-error text-[10px] px-1.5 py-0.5 rounded font-bold">Grade: 4.8</span>
</div>
<h4 className="text-sm font-bold text-on-surface mt-1">Vật lý đại cương 1</h4>
</div>
<button className="flex items-center gap-1 bg-secondary text-white text-xs font-bold py-2 px-4 rounded-full hover:bg-secondary-fixed-variant transition-colors shadow-sm">
<span className="material-symbols-outlined text-sm">add_shopping_cart</span>
Thêm vào giỏ
</button>
</div>
</div>
</section>
{/*  Search and Smart Filter Bar  */}
<section className="space-y-4">
<div className="bg-surface-container-low p-6 rounded-xl flex flex-wrap items-center gap-6">
<div className="flex-1 min-w-[300px] relative">
<span className="absolute inset-y-0 left-4 flex items-center text-outline">
<span className="material-symbols-outlined">search</span>
</span>
<select className="w-full bg-surface-container-lowest border-none py-3 pl-12 pr-4 rounded-lg text-sm font-medium focus:ring-2 focus:ring-primary shadow-sm appearance-none">
<option>Tất cả khoa (All Departments)</option>
<option>Công nghệ thông tin</option>
<option>Kinh tế đối ngoại</option>
<option>Ngôn ngữ học</option>
</select>
</div>
<div className="flex items-center gap-2">
<button className="px-6 py-3 rounded-full bg-surface-container-highest text-on-surface font-semibold text-sm hover:bg-primary hover:text-white transition-all">Chuyên ngành</button>
<button className="px-6 py-3 rounded-full bg-surface-container-highest text-on-surface font-semibold text-sm hover:bg-primary hover:text-white transition-all">Đại cương</button>
<button className="px-6 py-3 rounded-full bg-surface-container-highest text-on-surface font-semibold text-sm hover:bg-primary hover:text-white transition-all">Kỹ năng mềm</button>
</div>
</div>
{/*  Smart Filters Row  */}
<div className="flex flex-wrap items-center gap-8 px-2">
<div className="flex items-center gap-6">
<label className="flex items-center gap-2 cursor-pointer group">
<div className="relative flex items-center">
<input className="w-5 h-5 rounded border-outline text-primary focus:ring-primary focus:ring-offset-0 transition-all cursor-pointer" type="checkbox"/>
</div>
<span className="text-sm font-medium text-on-surface-variant group-hover:text-primary transition-colors">Lọc môn thuộc CTĐT</span>
</label>
<label className="flex items-center gap-2 cursor-pointer group">
<div className="relative flex items-center">
<input className="w-5 h-5 rounded border-outline text-primary focus:ring-primary focus:ring-offset-0 transition-all cursor-pointer" type="checkbox"/>
</div>
<span className="text-sm font-medium text-on-surface-variant group-hover:text-primary transition-colors">Ẩn môn đã Pass</span>
</label>
</div>
<div className="h-6 w-px bg-outline-variant hidden md:block"></div>
<div className="flex items-center gap-3">
<span className="text-sm font-bold text-outline uppercase tracking-wider text-[10px]">Lọc theo thời gian rảnh:</span>
<div className="flex gap-2">
<select className="bg-surface-container-highest border-none rounded-lg text-xs font-bold py-2 pl-3 pr-8 focus:ring-1 focus:ring-primary transition-all appearance-none cursor-pointer">
<option>Thứ trong tuần</option>
<option>Thứ 2</option>
<option>Thứ 3</option>
<option>Thứ 4</option>
<option>Thứ 5</option>
<option>Thứ 6</option>
<option>Thứ 7</option>
</select>
<select className="bg-surface-container-highest border-none rounded-lg text-xs font-bold py-2 pl-3 pr-8 focus:ring-1 focus:ring-primary transition-all appearance-none cursor-pointer">
<option>Ca học</option>
<option>Sáng</option>
<option>Chiều</option>
</select>
</div>
</div>
</div>
</section>
{/*  Available Courses Table  */}
<section className="bg-surface-container-lowest rounded-xl shadow-sm overflow-hidden">
<div className="overflow-x-auto">
<table className="w-full text-left border-collapse">
<thead>
<tr className="bg-surface-container">
<th className="px-6 py-4 text-[11px] font-bold tracking-wider text-outline uppercase font-label">Mã HP</th>
<th className="px-6 py-4 text-[11px] font-bold tracking-wider text-outline uppercase font-label">Tên HP</th>
<th className="px-6 py-4 text-[11px] font-bold tracking-wider text-outline uppercase font-label">Tín chỉ</th>
<th className="px-6 py-4 text-[11px] font-bold tracking-wider text-outline uppercase font-label">Học phần tiên quyết</th>
<th className="px-6 py-4 text-[11px] font-bold tracking-wider text-outline uppercase font-label">Giảng viên</th>
<th className="px-6 py-4 text-[11px] font-bold tracking-wider text-outline uppercase font-label">Học phí</th>
<th className="px-6 py-4 text-[11px] font-bold tracking-wider text-outline uppercase font-label">Lịch học</th>
<th className="px-6 py-4 text-[11px] font-bold tracking-wider text-outline uppercase font-label">Còn lại</th>
<th className="px-6 py-4 text-[11px] font-bold tracking-wider text-outline uppercase font-label text-right">Thao tác</th>
</tr>
</thead>
<tbody className="divide-y divide-surface-container-low">
{/*  Course Row 1  */}
<tr className="hover:bg-surface-container-low transition-colors group">
<td className="px-6 py-5 font-mono text-sm font-semibold text-primary">INT3306</td>
<td className="px-6 py-5 font-semibold text-on-surface">Phát triển ứng dụng Web</td>
<td className="px-6 py-5 text-sm">3</td>
<td className="px-6 py-5 text-xs text-outline italic">Lập trình hướng đối tượng</td>
<td className="px-6 py-5">
<div className="flex items-center gap-2">
<div className="w-6 h-6 rounded-full bg-tertiary-fixed overflow-hidden">
<img alt="Lecturer" className="w-full h-full object-cover" data-alt="Portrait of a male university professor in business casual attire, neutral background, soft academic lighting" src="https://lh3.googleusercontent.com/aida-public/AB6AXuABH2YWgUsOoUpbxKyvH4FJqVUjrKuwtHwFGSfkTBOnwxlb29Grw554SuMZrKzaa-tFaB3HLVE_k9elZ9l37C4IZOc3yj_0ebwqJ8RzI0ha_OVuKWryhH9HDG0vVDQtz0RhDPk6QTcZhchY9yjSzu2Y57B9GMCYjYb3ei5_v6SQVP1Zj-XPke4_tbwrnOVLPyfEhPeX4aQWsu_GCWvenPmFENAhvNtVZomFv3gwzoCAB5nj0t9Ud-Qm_DACV8jK73J60M_rlhh5jU_x"/>
</div>
<span className="text-sm">TS. Trần Văn A</span>
</div>
</td>
<td className="px-6 py-5 text-sm font-medium">1,250,000đ</td>
<td className="px-6 py-5">
<div className="text-xs space-y-1">
<div className="flex items-center gap-1"><span className="material-symbols-outlined text-[14px]">calendar_today</span> Thứ 3 (Tiết 1-3)</div>
<div className="flex items-center gap-1"><span className="material-symbols-outlined text-[14px]">location_on</span> G2-301</div>
</div>
</td>
<td className="px-6 py-5">
<span className="inline-flex items-center rounded-full bg-primary-fixed px-2.5 py-0.5 text-xs font-semibold text-on-primary-fixed">12/40</span>
</td>
<td className="px-6 py-5 text-right">
<button className="bg-primary hover:bg-primary-container text-white text-xs font-bold py-2 px-4 rounded-full transition-all transform active:scale-95">Đăng ký</button>
</td>
</tr>
{/*  Course Row 2  */}
<tr className="hover:bg-surface-container-low transition-colors group">
<td className="px-6 py-5 font-mono text-sm font-semibold text-primary">MAT1102</td>
<td className="px-6 py-5 font-semibold text-on-surface">Xác suất thống kê</td>
<td className="px-6 py-5 text-sm">3</td>
<td className="px-6 py-5 text-xs text-outline italic">Toán cao cấp A1</td>
<td className="px-6 py-5">
<div className="flex items-center gap-2">
<div className="w-6 h-6 rounded-full bg-secondary-fixed overflow-hidden">
<img alt="Lecturer" className="w-full h-full object-cover" data-alt="Professional headshot of a female lecturer with glasses, scholarly appearance, library background blurred" src="https://lh3.googleusercontent.com/aida-public/AB6AXuAJaXZqhAWBWOEARQLJgXpb6fZejlGlA_cVgLS_0ncO8bqy9kALc3NFcTouGf7S2tcsW3jZdfe8BUisab5ztq7bszymQZMpvxZEt4XA4MnJ1U1o-sq8392bb38ErZRTYDjjwQwzarrSGF4J_3vCrLoO42oQUFXI6zrRQ8kpRhQ9w5xerRYjcDHMg3kshOlANwzucsQxJtaScXsoB3qtc0nTm_06OcSPELAFi36RsErcBl2gDKs7BERpN6guZqz6d482rbwwlGrITfrv"/>
</div>
<span className="text-sm">PGS. Nguyễn Thị B</span>
</div>
</td>
<td className="px-6 py-5 text-sm font-medium">1,250,000đ</td>
<td className="px-6 py-5">
<div className="text-xs space-y-1">
<div className="flex items-center gap-1"><span className="material-symbols-outlined text-[14px]">calendar_today</span> Thứ 5 (Tiết 6-8)</div>
<div className="flex items-center gap-1"><span className="material-symbols-outlined text-[14px]">location_on</span> G3-105</div>
</div>
</td>
<td className="px-6 py-5">
<span className="inline-flex items-center rounded-full bg-error-container px-2.5 py-0.5 text-xs font-semibold text-on-error-container">2/60</span>
</td>
<td className="px-6 py-5 text-right">
<button className="bg-primary hover:bg-primary-container text-white text-xs font-bold py-2 px-4 rounded-full transition-all transform active:scale-95">Đăng ký</button>
</td>
</tr>
{/*  Course Row 3  */}
<tr className="hover:bg-surface-container-low transition-colors group">
<td className="px-6 py-5 font-mono text-sm font-semibold text-primary">HIS1001</td>
<td className="px-6 py-5 font-semibold text-on-surface">Lịch sử Đảng CSVN</td>
<td className="px-6 py-5 text-sm">2</td>
<td className="px-6 py-5 text-xs text-outline italic">—</td>
<td className="px-6 py-5">
<div className="flex items-center gap-2">
<div className="w-6 h-6 rounded-full bg-tertiary-container overflow-hidden">
<img alt="Lecturer" className="w-full h-full object-cover" data-alt="Mature professor giving a lecture, natural lighting, thoughtful academic expression" src="https://lh3.googleusercontent.com/aida-public/AB6AXuDF57cCCppH6ZeixjuHtBselSF9UV7te8FprLcHwA-4EZiwGhF95QVW5wcKvQbFNiOv595d94JX-LYTQtjGeFcTgu74ha7ZEY8GCJRzotErAFa3usdskKvmK0hmDQMF20j3NPh59o0UdgpC4L72VQV5swVCm_stBVlp_xvBrStQOX_ZoH-7ghBa12DAllo6DqYQBvwIRajYLzgTXprZG8sbJyMDv-C_ukbF5RgIPj9I8GkYZ9GgdxIcmdcc-5wPygzcRHkZCvufGIiP"/>
</div>
<span className="text-sm">TS. Lê Mạnh C</span>
</div>
</td>
<td className="px-6 py-5 text-sm font-medium">850,000đ</td>
<td className="px-6 py-5">
<div className="text-xs space-y-1">
<div className="flex items-center gap-1"><span className="material-symbols-outlined text-[14px]">calendar_today</span> Thứ 2 (Tiết 4-5)</div>
<div className="flex items-center gap-1"><span className="material-symbols-outlined text-[14px]">location_on</span> Online MS Teams</div>
</div>
</td>
<td className="px-6 py-5">
<span className="inline-flex items-center rounded-full bg-primary-fixed px-2.5 py-0.5 text-xs font-semibold text-on-primary-fixed">150/200</span>
</td>
<td className="px-6 py-5 text-right">
<button className="bg-primary hover:bg-primary-container text-white text-xs font-bold py-2 px-4 rounded-full transition-all transform active:scale-95">Đăng ký</button>
</td>
</tr>
</tbody>
</table>
</div>
</section>
{/*  Registration Summary & Fixed Footer  */}
<section className="grid grid-cols-1 md:grid-cols-3 gap-6">
<div className="bg-surface-container-high p-8 rounded-xl space-y-6 md:col-span-2">
<div className="flex items-center justify-between border-b border-outline-variant pb-4">
<h3 className="text-xl font-headline font-bold text-on-surface">Danh sách đã chọn</h3>
<span className="text-sm font-medium text-primary">02 môn học đã lưu</span>
</div>
<ul className="space-y-4">
<li className="flex items-center justify-between">
<div className="flex flex-col">
<span className="text-sm font-bold">INT3306 - Phát triển ứng dụng Web</span>
<span className="text-xs text-outline">3 Tín chỉ • 1,250,000đ</span>
</div>
<button className="text-error hover:bg-error-container p-2 rounded-full transition-colors">
<span className="material-symbols-outlined">delete</span>
</button>
</li>
<li className="flex items-center justify-between">
<div className="flex flex-col">
<span className="text-sm font-bold">MAT1102 - Xác suất thống kê</span>
<span className="text-xs text-outline">3 Tín chỉ • 1,250,000đ</span>
</div>
<button className="text-error hover:bg-error-container p-2 rounded-full transition-colors">
<span className="material-symbols-outlined">delete</span>
</button>
</li>
</ul>
</div>
<div className="bg-primary text-white p-8 rounded-xl space-y-8 shadow-xl flex flex-col justify-between">
<div className="space-y-4">
<h3 className="text-lg font-headline font-bold text-primary-fixed">Tổng kết đăng ký</h3>
<div className="flex justify-between items-end">
<span className="text-sm opacity-80">Tổng số tín chỉ:</span>
<span className="text-2xl font-black">06</span>
</div>
<div className="flex justify-between items-end border-t border-white/20 pt-4">
<span className="text-sm opacity-80">Học phí dự tính:</span>
<div className="text-right">
<span className="text-2xl font-black block">2,500,000đ</span>
<span className="text-[10px] opacity-60">Chưa bao gồm phụ phí</span>
</div>
</div>
</div>
<button className="w-full bg-secondary-container text-on-secondary-container font-bold py-4 rounded-full flex items-center justify-center gap-2 hover:scale-[1.02] active:scale-95 transition-all shadow-lg">
                        Xác nhận đăng ký
                        <span className="material-symbols-outlined">arrow_forward</span>
</button>
</div>
</section>
</div>
</main>
{/*  Contextual FAB (Execution for Task-specific action)  */}
<button className="fixed bottom-8 right-8 w-14 h-14 bg-secondary shadow-2xl rounded-full flex items-center justify-center text-white hover:scale-110 active:scale-90 transition-transform z-50">
<span className="material-symbols-outlined">add</span>
</button>

    </>
  );
};

export default TnhNngLcMnnhCao;

