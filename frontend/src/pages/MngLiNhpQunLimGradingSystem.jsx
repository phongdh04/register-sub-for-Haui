import React from 'react';

const MngLiNhpQunLimGradingSystem = () => {
  return (
    <>
      
{/*  SideNavBar Anchor  */}

{/*  TopNavBar Anchor  */}

{/*  Main Content Canvas  */}
<main className="  p-8 min-h-screen">
{/*  Header Section with Actions  */}
<div className="flex flex-col md:flex-row md:items-end justify-between gap-6 mb-10">
<div>
<nav className="flex items-center gap-2 text-xs font-semibold text-slate-400 mb-2 uppercase tracking-wider">
<span>Quản lý lớp</span>
<span className="material-symbols-outlined text-[12px]">chevron_right</span>
<span className="text-primary">Bảng điểm lớp</span>
</nav>
<h1 className="font-headline text-4xl font-extrabold text-on-surface tracking-tight">Bảng Điểm Lớp CS101.M11</h1>
<p className="text-on-surface-variant mt-2 max-w-2xl font-medium">Cập nhật và quản lý kết quả học tập của sinh viên. Hệ thống tự động tính toán điểm tổng kết dựa trên trọng số môn học.</p>
</div>
<div className="flex items-center gap-3">
<button className="flex items-center gap-2 px-5 py-2.5 bg-surface-container-high text-on-surface-variant font-semibold text-sm rounded-full hover:bg-surface-container-highest transition-colors">
<span className="material-symbols-outlined text-lg" data-icon="upload_file">upload_file</span>
                    Import Excel
                </button>
<button className="flex items-center gap-2 px-5 py-2.5 bg-secondary-container/10 text-secondary font-bold text-sm rounded-full hover:bg-secondary-container/20 transition-colors">
<span className="material-symbols-outlined text-lg" data-icon="save">save</span>
                    Lưu nháp
                </button>
<button className="jewel-button flex items-center gap-2 px-6 py-2.5 text-white font-bold text-sm rounded-full shadow-lg hover:opacity-90 transition-all active:scale-95">
<span className="material-symbols-outlined text-lg" data-icon="verified">verified</span>
                    Nộp điểm (Ký số)
                </button>
</div>
</div>
{/*  Grade Grid Section (The "Academic Ledger")  */}
<section className="bg-surface-container-lowest rounded-xl shadow-[0_20px_40px_rgba(20,27,43,0.03)] overflow-hidden border border-transparent">
{/*  Grid Context Info Bar  */}
<div className="px-6 py-4 bg-surface-container-low flex items-center justify-between border-b border-white">
<div className="flex gap-8">
<div className="flex flex-col">
<span className="text-[10px] uppercase tracking-widest text-on-surface-variant font-bold">Sĩ số</span>
<span className="text-sm font-bold text-primary">45 Sinh viên</span>
</div>
<div className="flex flex-col">
<span className="text-[10px] uppercase tracking-widest text-on-surface-variant font-bold">Trạng thái</span>
<span className="inline-flex items-center gap-1.5 text-sm font-bold text-secondary">
<span className="w-2 h-2 rounded-full bg-secondary"></span>
                            Đang cập nhật
                        </span>
</div>
<div className="flex flex-col">
<span className="text-[10px] uppercase tracking-widest text-on-surface-variant font-bold">Trọng số</span>
<span className="text-sm font-bold text-on-surface">CC: 10% | TX: 10% | GK: 30% | CK: 50%</span>
</div>
</div>
<div className="flex items-center gap-2">
<button className="p-2 text-slate-400 hover:text-primary transition-colors">
<span className="material-symbols-outlined">filter_list</span>
</button>
<button className="p-2 text-slate-400 hover:text-primary transition-colors">
<span className="material-symbols-outlined">fullscreen</span>
</button>
</div>
</div>
{/*  The Grid Spreadsheet  */}
<div className="academic-grid overflow-x-auto">
<table className="w-full text-left border-collapse">
<thead className="bg-surface-container-low">
<tr>
<th className="px-6 py-4 text-[11px] font-bold text-on-surface-variant uppercase tracking-wider sticky left-0 bg-surface-container-low z-10 w-32">Mã SV</th>
<th className="px-6 py-4 text-[11px] font-bold text-on-surface-variant uppercase tracking-wider sticky left-32 bg-surface-container-low z-10 min-w-[200px]">Họ và Tên</th>
<th className="px-4 py-4 text-[11px] font-bold text-on-surface-variant uppercase tracking-wider text-center w-20">CC</th>
<th className="px-4 py-4 text-[11px] font-bold text-on-surface-variant uppercase tracking-wider text-center w-20">TX</th>
<th className="px-4 py-4 text-[11px] font-bold text-on-surface-variant uppercase tracking-wider text-center w-20">GK</th>
<th className="px-4 py-4 text-[11px] font-bold text-on-surface-variant uppercase tracking-wider text-center w-20">CK</th>
<th className="px-6 py-4 text-[11px] font-bold text-primary uppercase tracking-wider text-center w-32 bg-primary-fixed/30">Điểm TK</th>
<th className="px-6 py-4 text-[11px] font-bold text-on-surface-variant uppercase tracking-wider min-w-[200px]">Ghi chú</th>
</tr>
</thead>
<tbody className="divide-y divide-surface-container">
{/*  Student Row 1  */}
<tr className="hover:bg-surface-container-low/50 transition-colors group">
<td className="px-6 py-3 font-semibold text-sm text-slate-500 sticky left-0 bg-white group-hover:bg-surface-container-low/50 z-10">SV00124</td>
<td className="px-6 py-3 font-bold text-sm text-on-surface sticky left-32 bg-white group-hover:bg-surface-container-low/50 z-10">Lê Minh Anh</td>
<td className="px-2 py-2 text-center">
<input className="w-16 text-center py-2 text-sm font-semibold rounded-lg bg-surface border-none focus:bg-white transition-all text-on-surface" type="text" value="10.0"/>
</td>
<td className="px-2 py-2 text-center">
<input className="w-16 text-center py-2 text-sm font-semibold rounded-lg bg-surface border-none focus:bg-white transition-all text-on-surface" type="text" value="9.0"/>
</td>
<td className="px-2 py-2 text-center">
<input className="w-16 text-center py-2 text-sm font-semibold rounded-lg bg-surface border-none focus:bg-white transition-all text-on-surface" type="text" value="8.5"/>
</td>
<td className="px-2 py-2 text-center">
<input className="w-16 text-center py-2 text-sm font-semibold rounded-lg bg-surface border-none focus:bg-white transition-all text-on-surface" type="text" value="9.5"/>
</td>
<td className="px-6 py-3 text-center">
<span className="px-3 py-1 bg-primary text-white text-xs font-bold rounded-full">9.2</span>
</td>
<td className="px-6 py-3">
<input className="w-full py-2 text-sm font-medium bg-transparent border-none italic text-slate-400 focus:text-on-surface focus:not-italic" placeholder="Thêm ghi chú..." type="text"/>
</td>
</tr>
{/*  Student Row 2  */}
<tr className="bg-surface-container-low/20 hover:bg-surface-container-low/50 transition-colors group">
<td className="px-6 py-3 font-semibold text-sm text-slate-500 sticky left-0 bg-[#fbfbff] group-hover:bg-surface-container-low/50 z-10">SV00125</td>
<td className="px-6 py-3 font-bold text-sm text-on-surface sticky left-32 bg-[#fbfbff] group-hover:bg-surface-container-low/50 z-10">Trần Đức Bình</td>
<td className="px-2 py-2 text-center">
<input className="w-16 text-center py-2 text-sm font-semibold rounded-lg bg-surface border-none focus:bg-white transition-all text-on-surface" type="text" value="8.0"/>
</td>
<td className="px-2 py-2 text-center">
<input className="w-16 text-center py-2 text-sm font-semibold rounded-lg bg-surface border-none focus:bg-white transition-all text-on-surface" type="text" value="7.5"/>
</td>
<td className="px-2 py-2 text-center">
<input className="w-16 text-center py-2 text-sm font-semibold rounded-lg bg-surface border-none focus:bg-white transition-all text-on-surface" type="text" value="6.0"/>
</td>
<td className="px-2 py-2 text-center">
<input className="w-16 text-center py-2 text-sm font-semibold rounded-lg bg-surface border-none focus:bg-white transition-all text-on-surface" type="text" value="4.5"/>
</td>
<td className="px-6 py-3 text-center">
<span className="px-3 py-1 bg-error-container text-on-error-container text-xs font-bold rounded-full">5.6</span>
</td>
<td className="px-6 py-3">
<input className="w-full py-2 text-sm font-medium bg-transparent border-none text-on-surface-variant italic" type="text" value="Vắng 1 buổi thực hành"/>
</td>
</tr>
{/*  Student Row 3  */}
<tr className="hover:bg-surface-container-low/50 transition-colors group">
<td className="px-6 py-3 font-semibold text-sm text-slate-500 sticky left-0 bg-white group-hover:bg-surface-container-low/50 z-10">SV00126</td>
<td className="px-6 py-3 font-bold text-sm text-on-surface sticky left-32 bg-white group-hover:bg-surface-container-low/50 z-10">Nguyễn Thị Chi</td>
<td className="px-2 py-2 text-center">
<input className="w-16 text-center py-2 text-sm font-semibold rounded-lg bg-surface border-none focus:bg-white transition-all text-on-surface" type="text" value="9.0"/>
</td>
<td className="px-2 py-2 text-center">
<input className="w-16 text-center py-2 text-sm font-semibold rounded-lg bg-surface border-none focus:bg-white transition-all text-on-surface" type="text" value="10.0"/>
</td>
<td className="px-2 py-2 text-center">
<input className="w-16 text-center py-2 text-sm font-semibold rounded-lg bg-surface border-none focus:bg-white transition-all text-on-surface" type="text" value="9.0"/>
</td>
<td className="px-2 py-2 text-center">
<input className="w-16 text-center py-2 text-sm font-semibold rounded-lg bg-surface border-none focus:bg-white transition-all text-on-surface" type="text" value="8.0"/>
</td>
<td className="px-6 py-3 text-center">
<span className="px-3 py-1 bg-primary text-white text-xs font-bold rounded-full">8.6</span>
</td>
<td className="px-6 py-3">
<input className="w-full py-2 text-sm font-medium bg-transparent border-none italic text-slate-400 focus:text-on-surface focus:not-italic" placeholder="Thêm ghi chú..." type="text"/>
</td>
</tr>
{/*  Student Row 4  */}
<tr className="bg-surface-container-low/20 hover:bg-surface-container-low/50 transition-colors group">
<td className="px-6 py-3 font-semibold text-sm text-slate-500 sticky left-0 bg-[#fbfbff] group-hover:bg-surface-container-low/50 z-10">SV00127</td>
<td className="px-6 py-3 font-bold text-sm text-on-surface sticky left-32 bg-[#fbfbff] group-hover:bg-surface-container-low/50 z-10">Phạm Hoàng Dũng</td>
<td className="px-2 py-2 text-center">
<input className="w-16 text-center py-2 text-sm font-semibold rounded-lg bg-surface border-none focus:bg-white transition-all text-on-surface" type="text" value="10.0"/>
</td>
<td className="px-2 py-2 text-center">
<input className="w-16 text-center py-2 text-sm font-semibold rounded-lg bg-surface border-none focus:bg-white transition-all text-on-surface" type="text" value="8.0"/>
</td>
<td className="px-2 py-2 text-center">
<input className="w-16 text-center py-2 text-sm font-semibold rounded-lg bg-surface border-none focus:bg-white transition-all text-on-surface" type="text" value="7.5"/>
</td>
<td className="px-2 py-2 text-center">
<input className="w-16 text-center py-2 text-sm font-semibold rounded-lg bg-surface border-none focus:bg-white transition-all text-on-surface" type="text" value="8.5"/>
</td>
<td className="px-6 py-3 text-center">
<span className="px-3 py-1 bg-primary text-white text-xs font-bold rounded-full">8.3</span>
</td>
<td className="px-6 py-3">
<input className="w-full py-2 text-sm font-medium bg-transparent border-none italic text-slate-400 focus:text-on-surface focus:not-italic" placeholder="Thêm ghi chú..." type="text"/>
</td>
</tr>
{/*  Student Row 5  */}
<tr className="hover:bg-surface-container-low/50 transition-colors group">
<td className="px-6 py-3 font-semibold text-sm text-slate-500 sticky left-0 bg-white group-hover:bg-surface-container-low/50 z-10">SV00128</td>
<td className="px-6 py-3 font-bold text-sm text-on-surface sticky left-32 bg-white group-hover:bg-surface-container-low/50 z-10">Đặng Thu Hà</td>
<td className="px-2 py-2 text-center">
<input className="w-16 text-center py-2 text-sm font-semibold rounded-lg bg-surface border-none focus:bg-white transition-all text-on-surface" type="text" value="5.0"/>
</td>
<td className="px-2 py-2 text-center">
<input className="w-16 text-center py-2 text-sm font-semibold rounded-lg bg-surface border-none focus:bg-white transition-all text-on-surface" type="text" value="6.0"/>
</td>
<td className="px-2 py-2 text-center">
<input className="w-16 text-center py-2 text-sm font-semibold rounded-lg bg-surface border-none focus:bg-white transition-all text-on-surface" type="text" value="4.0"/>
</td>
<td className="px-2 py-2 text-center">
<input className="w-16 text-center py-2 text-sm font-semibold rounded-lg bg-surface border-none focus:bg-white transition-all text-on-surface" type="text" value="0.0"/>
</td>
<td className="px-6 py-3 text-center">
<span className="px-3 py-1 bg-error text-white text-xs font-bold rounded-full">2.3</span>
</td>
<td className="px-6 py-3">
<input className="w-full py-2 text-sm font-medium bg-transparent border-none text-error italic" type="text" value="Vắng thi không lý do"/>
</td>
</tr>
</tbody>
</table>
</div>
{/*  Grid Footer / Stats  */}
<div className="px-6 py-4 bg-surface-container-low flex items-center justify-between">
<div className="flex items-center gap-4 text-xs font-bold text-on-surface-variant">
<span className="flex items-center gap-1"><span className="w-2 h-2 rounded-full bg-primary"></span> Giỏi: 12</span>
<span className="flex items-center gap-1"><span className="w-2 h-2 rounded-full bg-secondary-container"></span> Khá: 20</span>
<span className="flex items-center gap-1"><span className="w-2 h-2 rounded-full bg-outline"></span> Trung bình: 8</span>
<span className="flex items-center gap-1"><span className="w-2 h-2 rounded-full bg-error"></span> Yếu: 5</span>
</div>
<div className="flex items-center gap-4">
<span className="text-sm font-medium text-slate-500 italic">Tự động lưu lúc 14:35...</span>
<div className="flex items-center border border-outline-variant/30 rounded-lg overflow-hidden">
<button className="px-3 py-1 text-xs font-bold border-r border-outline-variant/30 hover:bg-white">1</button>
<button className="px-3 py-1 text-xs font-bold border-r border-outline-variant/30 hover:bg-white bg-white">2</button>
<button className="px-3 py-1 text-xs font-bold hover:bg-white">3</button>
</div>
</div>
</div>
</section>
{/*  Insights Bento Grid (Asymmetric Content)  */}
<div className="mt-12 grid grid-cols-1 md:grid-cols-3 gap-6">
{/*  Learning Trend Card  */}
<div className="md:col-span-2 bg-surface-container-highest/40 rounded-xl p-8 flex flex-col justify-between overflow-hidden relative">
<div className="z-10">
<h3 className="font-headline text-xl font-bold mb-2">Phân tích Phổ điểm</h3>
<p className="text-sm text-on-surface-variant max-w-sm mb-6 font-medium">Lớp có xu hướng tập trung ở mức điểm Khá. Cần chú ý nhóm sinh viên có nguy cơ trượt (điểm CK &lt; 4.0).</p>
<div className="flex items-end gap-2 h-24 mb-4">
<div className="w-8 bg-error/40 rounded-t-lg h-[20%]"></div>
<div className="w-8 bg-outline/40 rounded-t-lg h-[40%]"></div>
<div className="w-8 bg-secondary-container/60 rounded-t-lg h-[80%]"></div>
<div className="w-8 bg-primary/80 rounded-t-lg h-[100%]"></div>
<div className="w-8 bg-primary rounded-t-lg h-[60%]"></div>
</div>
<div className="flex justify-between text-[10px] font-bold text-on-surface-variant uppercase tracking-widest">
<span>Yếu</span>
<span>TB</span>
<span>Khá</span>
<span>Giỏi</span>
<span>Xuất Sắc</span>
</div>
</div>
{/*  Abstract visual element  */}
<div className="absolute -right-20 -bottom-20 w-64 h-64 bg-primary/5 rounded-full blur-3xl"></div>
</div>
{/*  Shortcut Card  */}
<div className="bg-primary-container text-white rounded-xl p-8 flex flex-col justify-between shadow-xl">
<div>
<span className="material-symbols-outlined text-3xl mb-4" data-icon="auto_awesome" data-weight="fill">auto_awesome</span>
<h3 className="font-headline text-xl font-bold mb-2">Hỗ trợ Chấm điểm</h3>
<p className="text-sm text-on-primary-container font-medium opacity-80">Sử dụng công cụ AI để dự đoán điểm tổng kết dựa trên lịch sử chuyên cần.</p>
</div>
<button className="mt-8 px-4 py-2 bg-white text-primary font-bold text-sm rounded-full w-fit hover:scale-105 transition-transform">
                    Khám phá ngay
                </button>
</div>
</div>
</main>
{/*  Contextual FAB Suppression: No FAB on focused data entry screen per rules  */}

    </>
  );
};

export default MngLiNhpQunLimGradingSystem;
