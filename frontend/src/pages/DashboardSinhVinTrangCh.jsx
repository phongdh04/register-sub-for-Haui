import React from 'react';

const DashboardSinhVinTrangCh = () => {
  return (
    <>
      
{/*  SideNavBar (Shared Component)  */}

{/*  Main Content  */}
<main className=" p-8">
{/*  TopNavBar (Shared Component Inspired)  */}

{/*  Bento Grid Layout  */}
<div className="grid grid-cols-12 gap-6 items-start">
{/*  Dashboard Headline (Asymmetric Layout Start)  */}
<div className="col-span-12 mb-4">
<h3 className="text-4xl font-extrabold text-on-surface font-headline tracking-tight">Chào buổi sáng, <span className="text-primary">Văn A</span>!</h3>
<p className="text-on-surface-variant mt-2 max-w-2xl font-body">Hôm nay bạn có 1 lớp học và 1 lịch thi cần lưu ý. Hãy kiểm tra lại học phí để chuẩn bị cho kỳ đăng ký học phần sắp tới.</p>
</div>
{/*  Left Column: Key Widgets  */}
<div className="col-span-12 lg:col-span-8 grid grid-cols-1 md:grid-cols-2 gap-6">
{/*  Financial Status Widget  */}
<div className="col-span-1 md:col-span-2 bg-surface-container-lowest rounded-full p-8 shadow-sm relative overflow-hidden">
<div className="absolute top-0 right-0 w-48 h-48 bg-primary-container/5 rounded-full -mr-24"></div>
<div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-6">
<div>
<p className="text-sm font-label font-bold text-on-surface-variant uppercase tracking-widest mb-4">Tình trạng tài chính</p>
<div className="flex items-baseline gap-4">
<h4 className="text-3xl font-black text-on-surface">5,000,000đ</h4>
<span className="text-sm text-on-surface-variant font-medium">Số dư ví hiện tại</span>
</div>
</div>
<div className="flex flex-col items-end">
<div className="bg-error-container text-on-error-container px-4 py-2 rounded-full flex items-center gap-2 font-bold mb-2">
<span className="material-symbols-outlined text-lg" data-icon="warning">warning</span>
                                1,200,000đ
                            </div>
<p className="text-xs font-semibold text-error uppercase tracking-tighter">Tổng nợ học phí</p>
</div>
</div>
</div>
{/*  Today's Schedule  */}
<div className="bg-primary text-white rounded-full p-8 shadow-xl relative group">
<div className="absolute top-4 right-8 opacity-20 group-hover:scale-110 transition-transform duration-500">
<span className="material-symbols-outlined text-6xl" data-icon="menu_book">menu_book</span>
</div>
<p className="text-xs font-bold opacity-70 uppercase tracking-widest mb-6">Môn học hôm nay</p>
<h4 className="text-2xl font-bold mb-2 font-headline leading-tight">Lập trình Java</h4>
<div className="space-y-3 mt-6">
<div className="flex items-center gap-3">
<span className="material-symbols-outlined text-sm bg-white/20 p-1.5 rounded-lg" data-icon="location_on">location_on</span>
<span className="font-medium">Phòng 402 - Tòa A1</span>
</div>
<div className="flex items-center gap-3">
<span className="material-symbols-outlined text-sm bg-white/20 p-1.5 rounded-lg" data-icon="schedule">schedule</span>
<span className="font-medium">Ca 2 (09:00 - 11:30)</span>
</div>
</div>
</div>
{/*  Exam Countdown  */}
<div className="bg-secondary-container text-on-secondary-container rounded-full p-8 shadow-lg relative">
<p className="text-xs font-bold text-on-secondary-fixed-variant uppercase tracking-widest mb-6">Lịch thi gần nhất</p>
<h4 className="text-2xl font-bold mb-2 font-headline leading-tight">Cơ sở dữ liệu</h4>
<div className="flex items-center gap-3 mt-6">
<span className="material-symbols-outlined bg-white/30 p-2 rounded-xl" data-icon="event">event</span>
<div>
<p className="text-lg font-bold">15/05/2026</p>
<p className="text-xs opacity-75 font-semibold">Còn 12 ngày</p>
</div>
</div>
</div>
{/*  Quick Actions Grid  */}
<div className="col-span-1 md:col-span-2 grid grid-cols-4 gap-4 mt-4">
<div className="bg-surface-container-high hover:bg-primary hover:text-white p-4 rounded-xl text-center transition-all cursor-pointer group">
<span className="material-symbols-outlined mb-2 block group-hover:scale-110 transition-transform" data-icon="assignment_turned_in">assignment_turned_in</span>
<span className="text-xs font-bold">Kết quả</span>
</div>
<div className="bg-surface-container-high hover:bg-primary hover:text-white p-4 rounded-xl text-center transition-all cursor-pointer group">
<span className="material-symbols-outlined mb-2 block group-hover:scale-110 transition-transform" data-icon="history_edu">history_edu</span>
<span className="text-xs font-bold">Rèn luyện</span>
</div>
<div className="bg-surface-container-high hover:bg-primary hover:text-white p-4 rounded-xl text-center transition-all cursor-pointer group">
<span className="material-symbols-outlined mb-2 block group-hover:scale-110 transition-transform" data-icon="receipt_long">receipt_long</span>
<span className="text-xs font-bold">Biên lai</span>
</div>
<div className="bg-surface-container-high hover:bg-primary hover:text-white p-4 rounded-xl text-center transition-all cursor-pointer group">
<span className="material-symbols-outlined mb-2 block group-hover:scale-110 transition-transform" data-icon="qr_code_scanner">qr_code_scanner</span>
<span className="text-xs font-bold">Điểm danh</span>
</div>
</div>
</div>
{/*  Right Column: Bulletin Board  */}
<div className="col-span-12 lg:col-span-4 space-y-6">
<div className="bg-white rounded-full p-8 shadow-sm border border-slate-100/50">
<div className="flex items-center justify-between mb-8">
<h4 className="text-lg font-extrabold font-headline uppercase tracking-tight">Thông báo mới</h4>
<span className="material-symbols-outlined text-primary" data-icon="campaign">campaign</span>
</div>
<div className="space-y-6">
<div className="group cursor-pointer">
<div className="flex items-start gap-4">
<div className="w-2 h-2 rounded-full bg-primary mt-2"></div>
<div>
<p className="text-sm font-bold group-hover:text-primary transition-colors">Thông báo về việc đóng học phí Học kỳ II (2025-2026)</p>
<p className="text-xs text-on-surface-variant mt-1">2 giờ trước • Phòng Đào tạo</p>
</div>
</div>
</div>
<div className="group cursor-pointer">
<div className="flex items-start gap-4">
<div className="w-2 h-2 rounded-full bg-primary mt-2"></div>
<div>
<p className="text-sm font-bold group-hover:text-primary transition-colors">Danh sách sinh viên đủ điều kiện nhận học bổng Khuyến khích</p>
<p className="text-xs text-on-surface-variant mt-1">Hôm qua • Ban CTSV</p>
</div>
</div>
</div>
<div className="group cursor-pointer opacity-70">
<div className="flex items-start gap-4">
<div className="w-2 h-2 rounded-full bg-slate-300 mt-2"></div>
<div>
<p className="text-sm font-bold group-hover:text-primary transition-colors">Kế hoạch tổ chức kỳ thi chứng chỉ CNTT cơ bản</p>
<p className="text-xs text-on-surface-variant mt-1">01/05/2026 • Trung tâm Tin học</p>
</div>
</div>
</div>
<div className="group cursor-pointer opacity-70">
<div className="flex items-start gap-4">
<div className="w-2 h-2 rounded-full bg-slate-300 mt-2"></div>
<div>
<p className="text-sm font-bold group-hover:text-primary transition-colors">Cập nhật quy định về việc sử dụng thẻ sinh viên trong thư viện</p>
<p className="text-xs text-on-surface-variant mt-1">30/04/2026 • Thư viện trung tâm</p>
</div>
</div>
</div>
</div>
<button className="w-full mt-10 py-3 bg-surface-container-low text-primary font-bold text-xs rounded-xl uppercase tracking-widest hover:bg-primary hover:text-white transition-all">
                        Xem tất cả thông báo
                    </button>
</div>
{/*  Feature Illustration / University Brand Section  */}
<div className="relative h-48 rounded-full overflow-hidden shadow-lg group">
<img alt="University Life" className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-700" data-alt="modern university campus architecture with contemporary library and green space in bright daylight" src="https://lh3.googleusercontent.com/aida-public/AB6AXuBre1DtJpfI8_hB-N2BckAp7z-7qoIpwRhu3lGpiXEVLsZBuKGM5U_p_5u4s5vHXmzX8f_ImkjfQvFk-FIEgvUd2AsRDuA4wVR2znhXlUD_a5JxMisoQ-Q7uY14aohF7D5EpO3ZIcKH_eqS8H_3xya9voUL-ahvLmIXPzvx9ZNN9rEhLu4JuiOxVTJbvKAj-_fH6qa0ugcpR1tFBoTk-Ysfxba6rR6zHAevkb01pyRxUIv1lrj-YMt3uO5F3THdgFBt-PAp8jJFWnDK"/>
<div className="absolute inset-0 bg-gradient-to-t from-primary/90 to-transparent flex flex-col justify-end p-6">
<p className="text-white text-sm font-bold leading-tight">Mùa hè tình nguyện 2026</p>
<p className="text-white/80 text-xs mt-1">Đăng ký tham gia ngay hôm nay để nhận điểm rèn luyện!</p>
</div>
</div>
</div>
</div>
</main>
{/*  Contextual FAB (Restricted to Dashboard)  */}
<button className="fixed bottom-8 right-8 w-14 h-14 bg-primary text-white rounded-full flex items-center justify-center shadow-2xl hover:scale-110 active:scale-95 transition-all z-40">
<span className="material-symbols-outlined text-2xl" data-icon="support_agent">support_agent</span>
</button>

    </>
  );
};

export default DashboardSinhVinTrangCh;

