import React from 'react';

const BoCoPhnTchAnalytics = () => {
  return (
    <>
      
{/*  Sidebar Navigation  */}

{/*  Main Content Area  */}
<main className=" min-h-screen p-8 lg:p-12">
{/*  Header Section  */}

{/*  Top Row: Line Chart & Stats  */}
<div className="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-8">
{/*  Section 3: Tiến độ Đăng ký (Featured Bento)  */}
<section className="lg:col-span-2 bg-surface-container-lowest rounded-xl p-8 shadow-sm">
<div className="flex items-center justify-between mb-8">
<div>
<h3 className="font-headline text-xl font-bold text-on-surface">Tiến độ Đăng ký Toàn Kỳ</h3>
<p className="text-on-surface-variant text-sm">Phân tích xu hướng lấp đầy slot theo thời gian</p>
</div>
<div className="flex items-center gap-4">
<div className="flex items-center gap-2">
<span className="w-3 h-3 rounded-full bg-primary"></span>
<span className="text-xs font-semibold text-on-surface-variant">Thực tế</span>
</div>
<div className="flex items-center gap-2">
<span className="w-3 h-3 rounded-full bg-outline-variant"></span>
<span className="text-xs font-semibold text-on-surface-variant">Dự báo</span>
</div>
</div>
</div>
{/*  Mock Chart Visualization  */}
<div className="relative h-64 w-full flex items-end justify-between px-2 pt-4">
<div className="absolute inset-0 grid grid-rows-4 pointer-events-none">
<div className="border-b border-surface-container-high w-full"></div>
<div className="border-b border-surface-container-high w-full"></div>
<div className="border-b border-surface-container-high w-full"></div>
<div className="border-b border-surface-container-high w-full"></div>
</div>
{/*  Chart Bars/Lines representation  */}
<div className="relative w-full h-full flex items-end justify-between px-4 pb-2">
<div className="h-1/4 w-1 bg-primary rounded-t-full relative group">
<div className="absolute -top-10 left-1/2 -translate-x-1/2 bg-on-surface text-white text-[10px] px-2 py-1 rounded hidden group-hover:block">25%</div>
</div>
<div className="h-2/5 w-1 bg-primary rounded-t-full"></div>
<div className="h-3/5 w-1 bg-primary rounded-t-full"></div>
<div className="h-4/6 w-1 bg-primary rounded-t-full"></div>
<div className="h-5/6 w-1 bg-primary rounded-t-full"></div>
<div className="h-full w-1 bg-primary rounded-t-full"></div>
<div className="h-5/6 w-1 bg-primary rounded-t-full opacity-40"></div>
<div className="h-4/6 w-1 bg-primary rounded-t-full opacity-30"></div>
<div className="h-3/4 w-1 bg-primary rounded-t-full opacity-20"></div>
</div>
</div>
<div className="flex justify-between mt-4 text-[10px] font-bold text-outline uppercase tracking-widest">
<span>Thứ 2</span>
<span>Thứ 3</span>
<span>Thứ 4</span>
<span>Thứ 5</span>
<span>Thứ 6</span>
<span>Thứ 7</span>
<span>CN</span>
</div>
</section>
{/*  Quick Stats Card  */}
<div className="flex flex-col gap-6">
<div className="bg-primary-container text-on-primary rounded-xl p-6 flex flex-col justify-between h-1/2 shadow-lg editorial-gradient">
<span className="material-symbols-outlined text-4xl opacity-50 mb-4" data-weight="fill">person_add</span>
<div>
<p className="text-sm font-medium opacity-80 mb-1">Tổng SV Đăng Ký</p>
<h4 className="text-4xl font-extrabold tracking-tight">12,840</h4>
</div>
</div>
<div className="bg-secondary-container text-on-secondary-container rounded-xl p-6 flex flex-col justify-between h-1/2 shadow-md">
<span className="material-symbols-outlined text-4xl opacity-50 mb-4">warning</span>
<div>
<p className="text-sm font-medium opacity-80 mb-1">Môn Cần Xử Lý</p>
<h4 className="text-4xl font-extrabold tracking-tight">24</h4>
</div>
</div>
</div>
</div>
{/*  Content Grid: Hot and Low Enrollment Courses  */}
<div className="grid grid-cols-1 xl:grid-cols-2 gap-8">
{/*  Section 1: Top Môn 'Cháy Vé' (Hot)  */}
<section className="bg-surface-container-lowest rounded-xl overflow-hidden shadow-sm">
<div className="p-6 border-b-0 flex items-center justify-between">
<div className="flex items-center gap-3">
<div className="w-10 h-10 rounded-lg bg-orange-100 flex items-center justify-center text-orange-600">
<span className="material-symbols-outlined" style={{ /* FIXME: convert style string to object -> font-variation-settings: 'FILL' 1; */ }}>local_fire_department</span>
</div>
<h3 className="font-headline text-lg font-bold">Top Môn 'Cháy Vé' (Tỉ lệ &gt; 95%)</h3>
</div>
<span className="bg-orange-100 text-orange-800 text-[10px] font-extrabold px-3 py-1 rounded-full uppercase">Cực Hot</span>
</div>
<div className="overflow-x-auto">
<table className="w-full text-left border-collapse">
<thead>
<tr className="bg-surface-container-low">
<th className="px-6 py-4 text-[10px] font-bold text-on-surface-variant uppercase tracking-widest">Tên Môn Học</th>
<th className="px-6 py-4 text-[10px] font-bold text-on-surface-variant uppercase tracking-widest text-center">Đăng ký/Slot</th>
<th className="px-6 py-4 text-[10px] font-bold text-on-surface-variant uppercase tracking-widest text-center">Queue</th>
<th className="px-6 py-4 text-right"></th>
</tr>
</thead>
<tbody className="divide-y divide-surface-container">
<tr className="hover:bg-surface-container-low transition-colors">
<td className="px-6 py-4">
<div className="font-bold text-on-surface">Kỹ thuật lập trình (N01)</div>
<div className="text-[10px] text-on-surface-variant">IT4010 • PGS.TS Nguyễn Văn A</div>
</td>
<td className="px-6 py-4 text-center">
<span className="inline-block bg-primary-fixed text-on-primary-fixed font-bold px-2 py-0.5 rounded text-xs">40/40</span>
</td>
<td className="px-6 py-4 text-center">
<span className="text-secondary font-bold">12</span>
</td>
<td className="px-6 py-4 text-right">
<button className="text-primary hover:bg-primary-fixed p-2 rounded-lg transition-all group">
<span className="material-symbols-outlined text-sm">add_circle</span>
<span className="hidden group-hover:inline ml-1 text-xs font-bold">Mở Lớp Tăng Cường</span>
</button>
</td>
</tr>
<tr className="hover:bg-surface-container-low transition-colors">
<td className="px-6 py-4">
<div className="font-bold text-on-surface">Quản trị Dự án (N04)</div>
<div className="text-[10px] text-on-surface-variant">BA2022 • ThS. Lê Thị B</div>
</td>
<td className="px-6 py-4 text-center">
<span className="inline-block bg-primary-fixed text-on-primary-fixed font-bold px-2 py-0.5 rounded text-xs">58/60</span>
</td>
<td className="px-6 py-4 text-center">
<span className="text-on-surface-variant font-bold">4</span>
</td>
<td className="px-6 py-4 text-right">
<button className="text-primary hover:bg-primary-fixed p-2 rounded-lg transition-all">
<span className="material-symbols-outlined text-sm">add_circle</span>
</button>
</td>
</tr>
<tr className="hover:bg-surface-container-low transition-colors">
<td className="px-6 py-4">
<div className="font-bold text-on-surface">Cơ sở dữ liệu (N02)</div>
<div className="text-[10px] text-on-surface-variant">IT3010 • TS. Trần Văn C</div>
</td>
<td className="px-6 py-4 text-center">
<span className="inline-block bg-primary-fixed text-on-primary-fixed font-bold px-2 py-0.5 rounded text-xs">45/45</span>
</td>
<td className="px-6 py-4 text-center">
<span className="text-secondary font-bold">28</span>
</td>
<td className="px-6 py-4 text-right">
<button className="text-primary hover:bg-primary-fixed p-2 rounded-lg transition-all">
<span className="material-symbols-outlined text-sm">add_circle</span>
</button>
</td>
</tr>
</tbody>
</table>
</div>
<div className="p-4 bg-surface-container-low/50 text-center">
<button className="text-primary text-xs font-bold uppercase tracking-widest hover:underline">Xem tất cả 18 lớp đầy</button>
</div>
</section>
{/*  Section 2: Báo động Môn 'Vắng Khách'  */}
<section className="bg-surface-container-lowest rounded-xl overflow-hidden shadow-sm">
<div className="p-6 border-b-0 flex items-center justify-between">
<div className="flex items-center gap-3">
<div className="w-10 h-10 rounded-lg bg-red-100 flex items-center justify-center text-red-600">
<span className="material-symbols-outlined">person_off</span>
</div>
<h3 className="font-headline text-lg font-bold">Báo động Môn 'Vắng Khách' (&lt; 15 SV)</h3>
</div>
<span className="bg-red-100 text-red-800 text-[10px] font-extrabold px-3 py-1 rounded-full uppercase">Nguy cấp</span>
</div>
<div className="overflow-x-auto">
<table className="w-full text-left border-collapse">
<thead>
<tr className="bg-surface-container-low">
<th className="px-6 py-4 text-[10px] font-bold text-on-surface-variant uppercase tracking-widest">Lớp Học</th>
<th className="px-6 py-4 text-[10px] font-bold text-on-surface-variant uppercase tracking-widest text-center">Số SV Hiện Tại</th>
<th className="px-6 py-4 text-right text-[10px] font-bold text-on-surface-variant uppercase tracking-widest">Thao tác điều phối</th>
</tr>
</thead>
<tbody className="divide-y divide-surface-container">
<tr className="hover:bg-surface-container-low transition-colors">
<td className="px-6 py-4">
<div className="font-bold text-on-surface">Văn hóa Doanh nghiệp (N08)</div>
<div className="text-[10px] text-on-surface-variant">BA1050 • P.402 Tòa A1</div>
</td>
<td className="px-6 py-4 text-center">
<span className="text-error font-extrabold text-lg">8</span>
<span className="text-on-surface-variant text-[10px]">/ 40</span>
</td>
<td className="px-6 py-4 text-right">
<div className="flex items-center justify-end gap-2">
<button className="bg-surface-container-high hover:bg-primary-container hover:text-white text-primary text-[10px] font-bold px-3 py-1.5 rounded-lg transition-all">Dồn Lớp</button>
<button className="bg-error-container hover:bg-error hover:text-white text-error text-[10px] font-bold px-3 py-1.5 rounded-lg transition-all">Đóng Lớp</button>
</div>
</td>
</tr>
<tr className="hover:bg-surface-container-low transition-colors">
<td className="px-6 py-4">
<div className="font-bold text-on-surface">Phương pháp NCKH (N12)</div>
<div className="text-[10px] text-on-surface-variant">SC2001 • P.205 Tòa B3</div>
</td>
<td className="px-6 py-4 text-center">
<span className="text-error font-extrabold text-lg">5</span>
<span className="text-on-surface-variant text-[10px]">/ 30</span>
</td>
<td className="px-6 py-4 text-right">
<div className="flex items-center justify-end gap-2">
<button className="bg-surface-container-high hover:bg-primary-container hover:text-white text-primary text-[10px] font-bold px-3 py-1.5 rounded-lg transition-all">Dồn Lớp</button>
<button className="bg-error-container hover:bg-error hover:text-white text-error text-[10px] font-bold px-3 py-1.5 rounded-lg transition-all">Đóng Lớp</button>
</div>
</td>
</tr>
<tr className="hover:bg-surface-container-low transition-colors">
<td className="px-6 py-4">
<div className="font-bold text-on-surface">Tiếng Anh Chuyên Ngành (N03)</div>
<div className="text-[10px] text-on-surface-variant">EN3020 • P.601 Tòa C1</div>
</td>
<td className="px-6 py-4 text-center">
<span className="text-error font-extrabold text-lg">11</span>
<span className="text-on-surface-variant text-[10px]">/ 25</span>
</td>
<td className="px-6 py-4 text-right">
<div className="flex items-center justify-end gap-2">
<button className="bg-surface-container-high hover:bg-primary-container hover:text-white text-primary text-[10px] font-bold px-3 py-1.5 rounded-lg transition-all">Dồn Lớp</button>
<button className="bg-error-container hover:bg-error hover:text-white text-error text-[10px] font-bold px-3 py-1.5 rounded-lg transition-all">Đóng Lớp</button>
</div>
</td>
</tr>
</tbody>
</table>
</div>
<div className="p-4 bg-surface-container-low/50 text-center">
<button className="text-primary text-xs font-bold uppercase tracking-widest hover:underline">Phân tích gợi ý dồn lớp tự động</button>
</div>
</section>
</div>
{/*  System Message / Bottom Alert  */}
<div className="mt-8 bg-tertiary text-on-tertiary-container/30 p-6 rounded-xl flex items-center gap-6 relative overflow-hidden">
<div className="absolute right-0 top-0 opacity-10 scale-150 pointer-events-none">
<span className="material-symbols-outlined text-[120px]">psychology</span>
</div>
<div className="flex-shrink-0 w-12 h-12 bg-white/10 rounded-full flex items-center justify-center backdrop-blur-sm">
<span className="material-symbols-outlined text-white">auto_awesome</span>
</div>
<div>
<h4 className="text-white font-bold mb-1">Đề xuất AI: Mở thêm 02 lớp 'Kỹ thuật lập trình'</h4>
<p className="text-white/70 text-sm max-w-2xl">Dựa trên tốc độ đăng ký và danh sách chờ, hệ thống đề xuất mở thêm 2 lớp vào ca tối Thứ 3 và Thứ 5 để giảm tải cho các phân viện chính.</p>
</div>
<button className="ml-auto bg-white text-primary font-bold px-6 py-2 rounded-full text-sm hover:bg-primary-fixed transition-colors">
                Thực hiện ngay
            </button>
</div>
</main>
{/*  Floating Global Help (Contextual FAB Suppression Check - Only relevant for specific actions)  */}
<div className="fixed bottom-8 right-8 flex flex-col gap-3">
<button className="w-14 h-14 editorial-gradient text-white rounded-full shadow-2xl flex items-center justify-center hover:scale-110 active:scale-95 transition-all">
<span className="material-symbols-outlined text-2xl">chat_bubble</span>
</button>
</div>

    </>
  );
};

export default BoCoPhnTchAnalytics;
