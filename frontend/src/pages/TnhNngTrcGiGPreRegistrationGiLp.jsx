import React from 'react';

const TnhNngTrcGiGPreRegistrationGiLp = () => {
  return (
    <>
      
<div className="flex min-h-screen">
{/*  SideNavBar Component  */}

{/*  Main Content Area  */}
<main className="flex-1 flex flex-col min-w-0">
{/*  TopNavBar Component  */}

{/*  Content Canvas  */}
<div className="p-8 space-y-8 overflow-y-auto">
{/*  Hero/Header Section  */}
<section className="grid grid-cols-1 lg:grid-cols-3 gap-8 items-end">
<div className="lg:col-span-2 space-y-4">
<span className="inline-block py-1 px-3 bg-primary-fixed text-on-primary-fixed rounded-full text-xs font-bold uppercase tracking-wider">Học kỳ 1 - 2024-2025</span>
<h2 className="text-4xl lg:text-5xl font-extrabold text-on-surface tracking-tight leading-tight">
                            Lên kế hoạch <span className="text-primary italic font-serif">trước giờ G.</span>
</h2>
<p className="text-on-surface-variant max-w-2xl leading-relaxed text-lg">
                            Kiểm tra xung đột lịch học và điều kiện tiên quyết tự động để sẵn sàng cho kỳ đăng ký chính thức. Hệ thống sẽ tự động đồng bộ khi cổng đăng ký mở.
                        </p>
</div>
<div className="flex flex-col gap-4">
<div className="bg-surface-container-lowest p-6 rounded-xl shadow-sm space-y-3">
<div className="flex items-center justify-between">
<span className="text-on-surface-variant font-medium">Thời gian mở cổng:</span>
<span className="text-primary font-bold">Còn 14:22:10</span>
</div>
<button className="w-full py-4 rounded-full bg-primary opacity-50 cursor-not-allowed text-on-primary font-bold flex items-center justify-center gap-2 transition-all">
<span className="material-symbols-outlined">send</span>
<span>Xác nhận Nộp Lưới Môn</span>
</button>
<p className="text-[10px] text-center text-on-surface-variant italic">Nút này sẽ hoạt động khi tới giờ đăng ký chính thức.</p>
</div>
</div>
</section>
{/*  Status Cards (Bento Grid Style)  */}
<section className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
<div className="bg-surface-container-lowest p-6 rounded-xl flex flex-col justify-between h-32 group hover:translate-y-[-4px] transition-transform duration-300">
<div className="flex justify-between items-start">
<span className="text-xs font-bold uppercase tracking-widest text-on-surface-variant">Tổng tín chỉ</span>
<span className="material-symbols-outlined text-primary group-hover:scale-110 transition-transform">menu_book</span>
</div>
<span className="text-3xl font-black text-on-surface">18 / 24</span>
</div>
<div className="bg-surface-container-lowest p-6 rounded-xl flex flex-col justify-between h-32 group hover:translate-y-[-4px] transition-transform duration-300">
<div className="flex justify-between items-start">
<span className="text-xs font-bold uppercase tracking-widest text-on-surface-variant">Môn dự kiến</span>
<span className="material-symbols-outlined text-secondary group-hover:scale-110 transition-transform">list_alt</span>
</div>
<span className="text-3xl font-black text-on-surface">06</span>
</div>
<div className="bg-surface-container-lowest p-6 rounded-xl flex flex-col justify-between h-32 border-2 border-error/10 group hover:translate-y-[-4px] transition-transform duration-300">
<div className="flex justify-between items-start">
<span className="text-xs font-bold uppercase tracking-widest text-error">Xung đột lịch</span>
<span className="material-symbols-outlined text-error group-hover:scale-110 transition-transform">event_busy</span>
</div>
<span className="text-3xl font-black text-error">02</span>
</div>
<div className="bg-surface-container-lowest p-6 rounded-xl flex flex-col justify-between h-32 group hover:translate-y-[-4px] transition-transform duration-300">
<div className="flex justify-between items-start">
<span className="text-xs font-bold uppercase tracking-widest text-on-surface-variant">Học phí ước tính</span>
<span className="material-symbols-outlined text-tertiary group-hover:scale-110 transition-transform">payments</span>
</div>
<span className="text-3xl font-black text-on-surface">12.450.000đ</span>
</div>
</section>
{/*  Clean LIST of Selected Subjects (Main Focus)  */}
<section className="bg-surface-container-lowest rounded-xl shadow-sm overflow-hidden border-2 border-surface-container">
<div className="px-8 py-6 bg-surface-container-low flex items-center justify-between border-b border-outline-variant/20">
<h3 className="text-xl font-bold text-on-surface">Danh sách môn đã chọn</h3>
<div className="flex gap-2">
<button className="px-4 py-2 rounded-lg bg-surface-container text-on-surface-variant text-sm font-semibold hover:bg-surface-container-high transition-colors flex items-center gap-2">
<span className="material-symbols-outlined text-sm">print</span>
<span>In bản nháp</span>
</button>
<button className="px-4 py-2 rounded-lg bg-primary text-on-primary text-sm font-semibold hover:opacity-90 transition-colors flex items-center gap-2">
<span className="material-symbols-outlined text-sm">add</span>
<span>Thêm môn học</span>
</button>
</div>
</div>
<div className="overflow-x-auto">
<table className="w-full text-left border-collapse">
<thead>
<tr className="bg-surface-container-low">
<th className="px-8 py-4 text-[11px] font-bold uppercase tracking-[0.1em] text-on-surface-variant/80">Tên lớp (Mã lớp)</th>
<th className="px-8 py-4 text-[11px] font-bold uppercase tracking-[0.1em] text-on-surface-variant/80">Tên học phần</th>
<th className="px-8 py-4 text-[11px] font-bold uppercase tracking-[0.1em] text-on-surface-variant/80 text-center">Tín chỉ</th>
<th className="px-8 py-4 text-[11px] font-bold uppercase tracking-[0.1em] text-on-surface-variant/80 text-center">Sĩ số tối đa</th>
<th className="px-8 py-4 text-[11px] font-bold uppercase tracking-[0.1em] text-on-surface-variant/80 text-right">Học phí</th>
<th className="px-8 py-4 text-[11px] font-bold uppercase tracking-[0.1em] text-on-surface-variant/80 text-right">Thao tác</th>
</tr>
</thead>
<tbody className="divide-y divide-surface-container">
{/*  Row 1: Normal  */}
<tr className="hover:bg-surface-container-low transition-colors group">
<td className="px-8 py-5">
<div className="flex flex-col">
<span className="text-sm font-bold text-primary">CS201.O21</span>
<span className="text-[10px] text-on-surface-variant">Lớp Chất lượng cao</span>
</div>
</td>
<td className="px-8 py-5">
<span className="text-sm font-medium text-on-surface">Cấu trúc dữ liệu và Giải thuật</span>
</td>
<td className="px-8 py-5 text-center">
<span className="text-sm font-medium">4</span>
</td>
<td className="px-8 py-5 text-center">
<span className="text-sm">45/50</span>
</td>
<td className="px-8 py-5 text-right font-medium text-sm">2.800.000đ</td>
<td className="px-8 py-5 text-right">
<button className="p-2 text-outline hover:text-error hover:bg-error-container rounded-full transition-all">
<span className="material-symbols-outlined text-lg">delete</span>
</button>
</td>
</tr>
{/*  Row 2: Conflict (Time)  */}
<tr className="bg-error-container/10 hover:bg-error-container/20 transition-colors">
<td className="px-8 py-5">
<div className="flex flex-col">
<span className="text-sm font-bold text-error">IT302.N12</span>
<span className="text-[10px] text-error font-semibold flex items-center gap-1">
<span className="material-symbols-outlined text-[12px]">warning</span> TRÙNG LỊCH
                                            </span>
</div>
</td>
<td className="px-8 py-5">
<span className="text-sm font-medium text-on-surface">Lập trình Web nâng cao</span>
</td>
<td className="px-8 py-5 text-center">
<span className="text-sm font-medium">3</span>
</td>
<td className="px-8 py-5 text-center">
<span className="text-sm">30/40</span>
</td>
<td className="px-8 py-5 text-right font-medium text-sm">2.100.000đ</td>
<td className="px-8 py-5 text-right">
<button className="p-2 text-outline hover:text-error hover:bg-error-container rounded-full transition-all">
<span className="material-symbols-outlined text-lg">delete</span>
</button>
</td>
</tr>
{/*  Row 3: Prerequisite Warning  */}
<tr className="bg-secondary-container/5 hover:bg-secondary-container/10 transition-colors">
<td className="px-8 py-5">
<div className="flex flex-col">
<span className="text-sm font-bold text-primary">AI401.M11</span>
<span className="text-[10px] text-secondary font-bold flex items-center gap-1">
<span className="material-symbols-outlined text-[12px]">priority_high</span> THIẾU TIÊN QUYẾT
                                            </span>
</div>
</td>
<td className="px-8 py-5">
<span className="text-sm font-medium text-on-surface">Trí tuệ nhân tạo căn bản</span>
</td>
<td className="px-8 py-5 text-center">
<span className="text-sm font-medium">3</span>
</td>
<td className="px-8 py-5 text-center">
<span className="text-sm">15/30</span>
</td>
<td className="px-8 py-5 text-right font-medium text-sm">2.550.000đ</td>
<td className="px-8 py-5 text-right">
<button className="p-2 text-outline hover:text-error hover:bg-error-container rounded-full transition-all">
<span className="material-symbols-outlined text-lg">delete</span>
</button>
</td>
</tr>
{/*  Row 4: Conflict (Overlap)  */}
<tr className="bg-error-container/10 hover:bg-error-container/20 transition-colors">
<td className="px-8 py-5">
<div className="flex flex-col">
<span className="text-sm font-bold text-error">MA102.K15</span>
<span className="text-[10px] text-error font-semibold flex items-center gap-1">
<span className="material-symbols-outlined text-[12px]">warning</span> TRÙNG LỊCH
                                            </span>
</div>
</td>
<td className="px-8 py-5">
<span className="text-sm font-medium text-on-surface">Xác suất Thống kê</span>
</td>
<td className="px-8 py-5 text-center">
<span className="text-sm font-medium">3</span>
</td>
<td className="px-8 py-5 text-center">
<span className="text-sm">120/120</span>
</td>
<td className="px-8 py-5 text-right font-medium text-sm">1.800.000đ</td>
<td className="px-8 py-5 text-right">
<button className="p-2 text-outline hover:text-error hover:bg-error-container rounded-full transition-all">
<span className="material-symbols-outlined text-lg">delete</span>
</button>
</td>
</tr>
{/*  Row 5: Normal  */}
<tr className="hover:bg-surface-container-low transition-colors group">
<td className="px-8 py-5">
<div className="flex flex-col">
<span className="text-sm font-bold text-primary">EN101.B01</span>
<span className="text-[10px] text-on-surface-variant">Lớp tăng cường</span>
</div>
</td>
<td className="px-8 py-5">
<span className="text-sm font-medium text-on-surface">Tiếng Anh chuyên ngành 1</span>
</td>
<td className="px-8 py-5 text-center">
<span className="text-sm font-medium">2</span>
</td>
<td className="px-8 py-5 text-center">
<span className="text-sm">25/40</span>
</td>
<td className="px-8 py-5 text-right font-medium text-sm">1.500.000đ</td>
<td className="px-8 py-5 text-right">
<button className="p-2 text-outline hover:text-error hover:bg-error-container rounded-full transition-all">
<span className="material-symbols-outlined text-lg">delete</span>
</button>
</td>
</tr>
{/*  Row 6: Normal  */}
<tr className="hover:bg-surface-container-low transition-colors group">
<td className="px-8 py-5">
<div className="flex flex-col">
<span className="text-sm font-bold text-primary">PE102.A01</span>
<span className="text-[10px] text-on-surface-variant">Giáo dục thể chất</span>
</div>
</td>
<td className="px-8 py-5">
<span className="text-sm font-medium text-on-surface">Bóng rổ 1</span>
</td>
<td className="px-8 py-5 text-center">
<span className="text-sm font-medium">3</span>
</td>
<td className="px-8 py-5 text-center">
<span className="text-sm">28/30</span>
</td>
<td className="px-8 py-5 text-right font-medium text-sm">1.700.000đ</td>
<td className="px-8 py-5 text-right">
<button className="p-2 text-outline hover:text-error hover:bg-error-container rounded-full transition-all">
<span className="material-symbols-outlined text-lg">delete</span>
</button>
</td>
</tr>
</tbody>
</table>
</div>
{/*  Footer/Summary Widget  */}
<div className="bg-surface-container-low px-8 py-10">
<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
<div className="lg:col-span-2 space-y-3">
<div className="flex items-center gap-2 text-error font-bold">
<span className="material-symbols-outlined">error</span>
<span className="text-sm">Phát hiện 2 xung đột lịch học</span>
</div>
<div className="flex items-center gap-2 text-secondary font-bold">
<span className="material-symbols-outlined">report_problem</span>
<span className="text-sm">1 môn chưa đủ điều kiện tiên quyết: AI401.M11</span>
</div>
<p className="text-on-surface-variant text-xs mt-4">
                                    * Học phí hiển thị chỉ là mức ước tính dựa trên số tín chỉ. Mức phí chính thức có thể thay đổi dựa trên các khoản phụ thu hoặc miễn giảm.
                                </p>
</div>
<div className="lg:col-span-2 flex flex-col items-end gap-2">
<div className="flex items-center gap-8 w-full max-w-md justify-between border-b border-outline-variant/30 pb-2">
<span className="text-on-surface-variant font-medium">Tổng số tín chỉ dự kiến:</span>
<span className="text-2xl font-black text-on-surface">18 Tín chỉ</span>
</div>
<div className="flex items-center gap-8 w-full max-w-md justify-between pt-2">
<span className="text-on-surface-variant font-bold text-lg">Tổng học phí dự kiến:</span>
<span className="text-3xl font-black text-primary">12.450.000đ</span>
</div>
<div className="mt-8 flex gap-4">
<button className="px-8 py-3 rounded-full border-2 border-primary text-primary font-bold hover:bg-primary-container/10 transition-colors">
                                        Hủy giỏ hàng
                                    </button>
<button className="px-10 py-3 rounded-full bg-primary text-on-primary font-bold shadow-lg shadow-primary/20 hover:scale-105 active:scale-95 transition-all">
                                        Lưu thay đổi nháp
                                    </button>
</div>
</div>
</div>
</div>
</section>
{/*  Recommendation Section  */}
<section className="space-y-6">
<h3 className="text-xl font-bold flex items-center gap-2">
<span className="material-symbols-outlined text-secondary">tips_and_updates</span>
                        Gợi ý cho lộ trình của bạn
                    </h3>
<div className="grid grid-cols-1 md:grid-cols-3 gap-6">
<div className="bg-surface-container p-6 rounded-xl border-l-4 border-secondary space-y-3">
<h4 className="font-bold text-on-surface">Thay thế lịch xung đột</h4>
<p className="text-sm text-on-surface-variant">Lớp MA102.K15 đang bị trùng, bạn có thể chọn lớp MA102.K18 vào sáng Thứ 5 để tránh xung đột.</p>
<a className="text-primary text-sm font-bold flex items-center gap-1 hover:underline" href="#">
                                Xem các lớp thay thế <span className="material-symbols-outlined text-sm">arrow_forward</span>
</a>
</div>
<div className="bg-surface-container p-6 rounded-xl border-l-4 border-primary space-y-3">
<h4 className="font-bold text-on-surface">Môn học tương đương</h4>
<p className="text-sm text-on-surface-variant">AI401.M11 đang thiếu tiên quyết, bạn có thể hoàn thành môn 'Logic Toán' trước hoặc đăng ký song hành nếu được phép.</p>
<a className="text-primary text-sm font-bold flex items-center gap-1 hover:underline" href="#">
                                Xem quy định <span className="material-symbols-outlined text-sm">open_in_new</span>
</a>
</div>
<div className="relative overflow-hidden group rounded-xl bg-primary text-on-primary p-6">
<div className="relative z-10 space-y-2">
<h4 className="font-bold">Nhận thông báo tự động</h4>
<p className="text-xs text-on-primary/80">Nhận thông báo ngay khi lớp học có thêm chỗ trống hoặc thay đổi giảng viên.</p>
<button className="mt-4 px-4 py-2 bg-on-primary text-primary rounded-full text-xs font-bold hover:bg-surface transition-colors">Bật thông báo</button>
</div>
<span className="material-symbols-outlined absolute -bottom-4 -right-4 text-8xl opacity-10 rotate-12">notifications_active</span>
</div>
</div>
</section>
</div>
</main>
</div>
{/*  Mobile Bottom Navigation  */}
<nav className="md:hidden fixed bottom-0 left-0 right-0 bg-slate-50 border-t border-slate-200 flex justify-around py-2 z-50">
<a className="flex flex-col items-center p-2 text-slate-400" href="#">
<span className="material-symbols-outlined">dashboard</span>
<span className="text-[10px] font-bold">DASHBOARD</span>
</a>
<a className="flex flex-col items-center p-2 text-primary" href="#">
<span className="material-symbols-outlined" style={{ /* FIXME: convert style string to object -> font-variation-settings: 'FILL' 1; */ }}>app_registration</span>
<span className="text-[10px] font-bold">ĐĂNG KÝ</span>
</a>
<a className="flex flex-col items-center p-2 text-slate-400" href="#">
<span className="material-symbols-outlined">calendar_month</span>
<span className="text-[10px] font-bold">LỊCH</span>
</a>
<a className="flex flex-col items-center p-2 text-slate-400" href="#">
<span className="material-symbols-outlined">person</span>
<span className="text-[10px] font-bold">HỒ SƠ</span>
</a>
</nav>

    </>
  );
};

export default TnhNngTrcGiGPreRegistrationGiLp;
