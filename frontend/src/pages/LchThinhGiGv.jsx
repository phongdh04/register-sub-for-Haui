import React from 'react';

const LchThinhGiGv = () => {
  return (
    <>
      
{/*  Side Navigation Bar  */}

{/*  Top Navigation Bar  */}

{/*  Main Content Area  */}
<main className="  min-h-screen">
<div className="p-12 max-w-7xl mx-auto space-y-12">
{/*  Page Header Section  */}
<div className="flex flex-col md:flex-row md:items-end justify-between gap-6">
<div>
<h2 className="text-4xl font-black text-on-surface tracking-tight mb-2">Lịch thi học kỳ</h2>
<p className="text-on-surface-variant font-medium">Học kỳ 1 - Năm học 2023-2024 • Khoa Công nghệ Thông tin</p>
</div>
<button className="flex items-center gap-2 bg-gradient-to-br from-primary to-primary-container text-white px-8 py-3 rounded-full font-bold shadow-[0_10px_20px_rgba(0,40,142,0.15)] hover:scale-[1.02] active:scale-95 transition-all">
<span className="material-symbols-outlined text-xl">print</span>
<span>In lịch thi</span>
</button>
</div>
{/*  Summary Bento Grid  */}
<div className="grid grid-cols-1 md:grid-cols-3 gap-6">
<div className="bg-surface-container-lowest p-8 rounded-xl shadow-[0_20px_40px_rgba(20,27,43,0.03)] flex flex-col justify-between">
<div className="flex items-center gap-4 mb-4">
<div className="w-12 h-12 bg-primary-fixed rounded-full flex items-center justify-center text-primary">
<span className="material-symbols-outlined" style={{ /* FIXME: convert style string to object -> font-variation-settings: 'FILL' 1; */ }}>auto_stories</span>
</div>
<span className="text-sm font-bold uppercase tracking-wider text-on-surface-variant">Tổng số môn thi</span>
</div>
<div>
<span className="text-5xl font-black text-on-surface">06</span>
<span className="text-on-surface-variant font-medium ml-2">học phần</span>
</div>
</div>
<div className="bg-surface-container-lowest p-8 rounded-xl shadow-[0_20px_40px_rgba(20,27,43,0.03)] flex flex-col justify-between col-span-1 md:col-span-2 relative overflow-hidden">
<div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-6 h-full">
<div>
<div className="flex items-center gap-4 mb-4">
<div className="w-12 h-12 bg-secondary-fixed rounded-full flex items-center justify-center text-secondary">
<span className="material-symbols-outlined" style={{ /* FIXME: convert style string to object -> font-variation-settings: 'FILL' 1; */ }}>verified_user</span>
</div>
<span className="text-sm font-bold uppercase tracking-wider text-on-surface-variant">Trạng thái dự thi chung</span>
</div>
<h3 className="text-2xl font-extrabold text-on-surface">Đủ điều kiện dự thi</h3>
<p className="text-sm text-on-surface-variant mt-1">Hoàn thành đầy đủ học phí và nghĩa vụ học tập.</p>
</div>
<div className="bg-primary/5 p-6 rounded-2xl border border-primary/10 flex items-center gap-6">
<div className="text-center">
<p className="text-[10px] uppercase font-bold text-slate-400">Tín chỉ</p>
<p className="text-2xl font-black text-primary">18</p>
</div>
<div className="w-px h-10 bg-slate-200"></div>
<div className="text-center">
<p className="text-[10px] uppercase font-bold text-slate-400">Điểm TB</p>
<p className="text-2xl font-black text-primary">3.42</p>
</div>
</div>
</div>
{/*  Background Decoration  */}
<div className="absolute -right-8 -bottom-8 w-48 h-48 bg-secondary/5 rounded-full blur-3xl"></div>
</div>
</div>
{/*  Exam Schedule Table  */}
<div className="bg-surface-container-lowest rounded-xl shadow-[0_20px_40px_rgba(20,27,43,0.03)] overflow-hidden">
<div className="overflow-x-auto">
<table className="w-full text-left border-collapse">
<thead>
<tr className="bg-surface-container-low">
<th className="px-8 py-6 text-[11px] font-black uppercase tracking-[0.1em] text-on-surface-variant">Môn thi</th>
<th className="px-6 py-6 text-[11px] font-black uppercase tracking-[0.1em] text-on-surface-variant">Ngày thi</th>
<th className="px-6 py-6 text-[11px] font-black uppercase tracking-[0.1em] text-on-surface-variant">Ca thi</th>
<th className="px-6 py-6 text-[11px] font-black uppercase tracking-[0.1em] text-on-surface-variant">SBD</th>
<th className="px-6 py-6 text-[11px] font-black uppercase tracking-[0.1em] text-on-surface-variant text-center">Lần thi</th>
<th className="px-6 py-6 text-[11px] font-black uppercase tracking-[0.1em] text-on-surface-variant">Địa điểm</th>
<th className="px-8 py-6 text-[11px] font-black uppercase tracking-[0.1em] text-on-surface-variant">Tình trạng</th>
</tr>
</thead>
<tbody className="divide-y divide-slate-100">
{/*  Row 1  */}
<tr className="hover:bg-slate-50/50 transition-colors">
<td className="px-8 py-6">
<div className="flex flex-col">
<span className="font-bold text-on-surface">Giải tích 1</span>
<span className="text-xs text-on-surface-variant font-medium">MA101 • 3 Tín chỉ</span>
</div>
</td>
<td className="px-6 py-6 font-semibold text-on-surface">12/01/2024</td>
<td className="px-6 py-6">
<div className="flex items-center gap-2">
<span className="w-2 h-2 rounded-full bg-secondary"></span>
<span className="text-sm font-medium">Ca 1 (07:30)</span>
</div>
</td>
<td className="px-6 py-6 font-mono font-bold text-primary">CNTT-042</td>
<td className="px-6 py-6 text-center font-medium">1</td>
<td className="px-6 py-6">
<span className="text-sm font-medium">Phòng 402 - Tòa A - Cơ sở 1</span>
</td>
<td className="px-8 py-6">
<span className="inline-flex items-center px-3 py-1 rounded-full text-[10px] font-bold uppercase tracking-wide bg-primary-fixed text-on-primary-fixed-variant">
                                        Được thi
                                    </span>
</td>
</tr>
{/*  Row 2  */}
<tr className="bg-surface-container-low/30 hover:bg-slate-50/50 transition-colors">
<td className="px-8 py-6">
<div className="flex flex-col">
<span className="font-bold text-on-surface">Cấu trúc dữ liệu</span>
<span className="text-xs text-on-surface-variant font-medium">CS201 • 4 Tín chỉ</span>
</div>
</td>
<td className="px-6 py-6 font-semibold text-on-surface">15/01/2024</td>
<td className="px-6 py-6">
<div className="flex items-center gap-2">
<span className="w-2 h-2 rounded-full bg-slate-400"></span>
<span className="text-sm font-medium">Ca 3 (13:30)</span>
</div>
</td>
<td className="px-6 py-6 font-mono font-bold text-primary">CNTT-042</td>
<td className="px-6 py-6 text-center font-medium">1</td>
<td className="px-6 py-6">
<span className="text-sm font-medium">Phòng 105 - Tòa C - Cơ sở 1</span>
</td>
<td className="px-8 py-6">
<span className="inline-flex items-center px-3 py-1 rounded-full text-[10px] font-bold uppercase tracking-wide bg-primary-fixed text-on-primary-fixed-variant">
                                        Được thi
                                    </span>
</td>
</tr>
{/*  Row 3 - Banned  */}
<tr className="hover:bg-slate-50/50 transition-colors">
<td className="px-8 py-6">
<div className="flex flex-col">
<span className="font-bold text-on-surface">Kỹ thuật lập trình</span>
<span className="text-xs text-on-surface-variant font-medium">CS202 • 4 Tín chỉ</span>
</div>
</td>
<td className="px-6 py-6 font-semibold text-on-surface">18/01/2024</td>
<td className="px-6 py-6">
<div className="flex items-center gap-2">
<span className="w-2 h-2 rounded-full bg-secondary"></span>
<span className="text-sm font-medium">Ca 1 (07:30)</span>
</div>
</td>
<td className="px-6 py-6 font-mono font-bold text-slate-300">---</td>
<td className="px-6 py-6 text-center font-medium">1</td>
<td className="px-6 py-6">
<span className="text-sm font-medium text-slate-400">---</span>
</td>
<td className="px-8 py-6">
<div className="flex flex-col gap-1">
<span className="inline-flex items-center w-fit px-3 py-1 rounded-full text-[10px] font-bold uppercase tracking-wide bg-error-container text-on-error-container">
                                            Bị Cấm Thi
                                        </span>
<span className="text-[10px] text-error font-medium italic">Nghỉ quá phép (25%)</span>
</div>
</td>
</tr>
{/*  Row 4  */}
<tr className="bg-surface-container-low/30 hover:bg-slate-50/50 transition-colors">
<td className="px-8 py-6">
<div className="flex flex-col">
<span className="font-bold text-on-surface">Linh kiện điện tử</span>
<span className="text-xs text-on-surface-variant font-medium">EE105 • 2 Tín chỉ</span>
</div>
</td>
<td className="px-6 py-6 font-semibold text-on-surface">20/01/2024</td>
<td className="px-6 py-6">
<div className="flex items-center gap-2">
<span className="w-2 h-2 rounded-full bg-slate-400"></span>
<span className="text-sm font-medium">Ca 2 (09:45)</span>
</div>
</td>
<td className="px-6 py-6 font-mono font-bold text-primary">CNTT-042</td>
<td className="px-6 py-6 text-center font-medium">1</td>
<td className="px-6 py-6">
<span className="text-sm font-medium">Phòng 202 - Tòa B - Cơ sở 2</span>
</td>
<td className="px-8 py-6">
<span className="inline-flex items-center px-3 py-1 rounded-full text-[10px] font-bold uppercase tracking-wide bg-primary-fixed text-on-primary-fixed-variant">
                                        Được thi
                                    </span>
</td>
</tr>
</tbody>
</table>
</div>
</div>
{/*  Footer Legend/Info  */}
<div className="bg-tertiary-container/10 p-8 rounded-xl border border-tertiary-container/20 flex flex-col md:flex-row gap-8 items-center justify-between">
<div className="flex gap-6 items-center">
<span className="material-symbols-outlined text-tertiary-container text-4xl">info</span>
<div>
<h4 className="font-bold text-on-surface">Lưu ý sinh viên</h4>
<p className="text-sm text-on-surface-variant max-w-xl">Sinh viên có mặt trước giờ thi 15 phút. Xuất trình thẻ sinh viên hoặc giấy tờ tùy thân có ảnh khi vào phòng thi. Trường hợp "Bị cấm thi" vui lòng liên hệ Văn phòng khoa trước 48h để khiếu nại.</p>
</div>
</div>
<div className="flex flex-wrap gap-4">
<div className="flex items-center gap-2">
<span className="w-3 h-3 rounded-full bg-secondary"></span>
<span className="text-[10px] font-bold uppercase tracking-wider text-on-surface-variant">Sáng (Ca 1,2)</span>
</div>
<div className="flex items-center gap-2">
<span className="w-3 h-3 rounded-full bg-slate-400"></span>
<span className="text-[10px] font-bold uppercase tracking-wider text-on-surface-variant">Chiều (Ca 3,4)</span>
</div>
</div>
</div>
</div>
</main>

    </>
  );
};

export default LchThinhGiGv;
