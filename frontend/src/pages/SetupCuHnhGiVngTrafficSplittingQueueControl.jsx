import React from 'react';

const SetupCuHnhGiVngTrafficSplittingQueueControl = () => {
  return (
    <>
      
{/*  SideNavBar Anchor  */}

<main className=" min-h-screen">
{/*  TopNavBar Anchor  */}

<section className="p-8 max-w-7xl mx-auto space-y-12">
{/*  Page Header & Kill Switch Area  */}
<div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-6">
<div>
<h2 className="text-4xl font-headline font-extrabold tracking-tight text-primary">Cấu Hình Thời Gian Đăng Ký</h2>
<p className="text-on-surface-variant mt-2 font-medium">Module 20: Quản lý đợt đăng ký và lưu lượng truy cập (Traffic Splitting)</p>
</div>
{/*  Kill Switch Button  */}
<button className="group flex items-center gap-3 bg-error text-on-error px-8 py-4 rounded-full font-bold shadow-lg hover:bg-red-700 transition-all hover:scale-105 active:scale-95">
<span className="material-symbols-outlined animate-pulse" style={{ /* FIXME: convert style string to object -> font-variation-settings: 'FILL' 1; */ }}>emergency_home</span>
<span className="uppercase tracking-widest text-sm">Kill Switch: Dừng Khẩn Cấp</span>
</button>
</div>
{/*  Main Layout Grid (Editorial Style)  */}
<div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
{/*  Registration Form Section (Left Column)  */}
<div className="lg:col-span-4 space-y-6">
<div className="bg-surface-container-lowest p-8 rounded-xl shadow-sm space-y-6 sticky top-24">
<div className="flex items-center justify-between">
<h3 className="text-xl font-headline font-bold text-on-surface">Thêm ca đăng ký mới</h3>
<span className="material-symbols-outlined text-primary-container">schedule</span>
</div>
<form className="space-y-4">
<div className="space-y-1.5">
<label className="text-xs font-bold uppercase tracking-wider text-on-surface-variant">Tên nhóm sinh viên</label>
<input className="w-full border-none bg-surface-container-low rounded-xl px-4 py-3 text-sm focus:ring-2 focus:ring-primary/20" placeholder="VD: Khóa 2021 - Kỹ thuật" type="text"/>
</div>
<div className="grid grid-cols-2 gap-4">
<div className="space-y-1.5">
<label className="text-xs font-bold uppercase tracking-wider text-on-surface-variant">Giờ mở cổng</label>
<input className="w-full border-none bg-surface-container-low rounded-xl px-4 py-3 text-sm focus:ring-2 focus:ring-primary/20" type="datetime-local"/>
</div>
<div className="space-y-1.5">
<label className="text-xs font-bold uppercase tracking-wider text-on-surface-variant">Giờ đóng cổng</label>
<input className="w-full border-none bg-surface-container-low rounded-xl px-4 py-3 text-sm focus:ring-2 focus:ring-primary/20" type="datetime-local"/>
</div>
</div>
<div className="space-y-1.5">
<label className="text-xs font-bold uppercase tracking-wider text-on-surface-variant">Học kỳ áp dụng</label>
<select className="w-full border-none bg-surface-container-low rounded-xl px-4 py-3 text-sm focus:ring-2 focus:ring-primary/20">
<option>Học kỳ 1 - 2024-2025</option>
<option>Học kỳ 2 - 2023-2024</option>
<option>Học kỳ Hè - 2024</option>
</select>
</div>
<div className="space-y-1.5">
<label className="text-xs font-bold uppercase tracking-wider text-on-surface-variant">Phân bổ lưu lượng (%)</label>
<div className="flex items-center gap-4">
<input className="flex-1 accent-primary" type="range"/>
<span className="font-bold text-primary">25%</span>
</div>
</div>
<button className="w-full bg-primary text-on-primary py-4 rounded-xl font-bold text-sm shadow-md hover:translate-y-[-2px] transition-all duration-300" type="button">
                                Kích hoạt đợt đăng ký
                            </button>
</form>
</div>
</div>
{/*  Timeline & Monitoring Section (Right Column)  */}
<div className="lg:col-span-8 space-y-8">
{/*  Visual Timeline Card  */}
<div className="bg-surface-container p-8 rounded-xl">
<div className="flex justify-between items-end mb-8">
<h3 className="text-2xl font-headline font-bold text-primary">Timeline trực quan</h3>
<div className="flex gap-4">
<div className="flex items-center gap-2">
<div className="w-3 h-3 rounded-full bg-primary-container"></div>
<span className="text-xs font-medium">Đang diễn ra</span>
</div>
<div className="flex items-center gap-2">
<div className="w-3 h-3 rounded-full bg-outline-variant"></div>
<span className="text-xs font-medium">Chờ kích hoạt</span>
</div>
</div>
</div>
<div className="relative pb-4 overflow-x-auto custom-scrollbar">
<div className="min-w-[800px] h-24 flex items-center relative">
{/*  Baseline  */}
<div className="absolute w-full h-1 bg-surface-container-highest rounded-full"></div>
{/*  Timeline Item 1  */}
<div className="absolute left-0 w-1/4 group cursor-pointer">
<div className="bg-primary-container h-4 rounded-full shadow-sm group-hover:h-6 transition-all duration-300"></div>
<div className="mt-4">
<p className="text-xs font-bold text-on-surface">Khóa 2021 (Ưu tiên)</p>
<p className="text-[10px] text-on-surface-variant">08:00 - 10:00 AM</p>
</div>
<div className="absolute -top-10 left-0 bg-primary text-white text-[10px] px-2 py-1 rounded-lg opacity-0 group-hover:opacity-100 transition-opacity">
                                        Trạng thái: Hoàn tất
                                    </div>
</div>
{/*  Timeline Item 2 (Active)  */}
<div className="absolute left-[30%] w-1/3 group cursor-pointer">
<div className="bg-secondary-container h-8 rounded-full shadow-lg ring-4 ring-secondary-container/20 group-hover:h-10 transition-all duration-300"></div>
<div className="mt-4">
<p className="text-xs font-bold text-primary">Khóa 2022 &amp; Đào tạo từ xa</p>
<p className="text-[10px] text-on-surface-variant">10:30 AM - 02:30 PM</p>
</div>
<div className="absolute -top-10 left-0 bg-secondary text-white text-[10px] px-2 py-1 rounded-lg shadow-sm">
                                        ĐANG HOẠT ĐỘNG
                                    </div>
</div>
{/*  Timeline Item 3  */}
<div className="absolute left-[70%] w-1/5 group cursor-pointer">
<div className="bg-surface-container-highest h-4 rounded-full group-hover:bg-primary-fixed-dim transition-all"></div>
<div className="mt-4">
<p className="text-xs font-bold text-on-surface">Khóa 2023</p>
<p className="text-[10px] text-on-surface-variant">03:00 PM - 05:00 PM</p>
</div>
</div>
</div>
</div>
</div>
{/*  Detailed Management Table  */}
<div className="bg-surface-container-lowest rounded-xl overflow-hidden shadow-sm">
<div className="px-8 py-6 flex justify-between items-center bg-surface-container-low/50">
<h4 className="text-sm font-headline font-bold uppercase tracking-wider">Danh sách các ca đăng ký</h4>
<button className="text-primary text-sm font-bold flex items-center gap-1 hover:underline">
<span className="material-symbols-outlined text-sm">filter_list</span>
                                Lọc danh sách
                            </button>
</div>
<table className="w-full text-left border-collapse">
<thead>
<tr className="bg-surface-container-low/20">
<th className="px-8 py-4 text-[11px] font-bold text-on-surface-variant uppercase tracking-widest">Đối tượng</th>
<th className="px-8 py-4 text-[11px] font-bold text-on-surface-variant uppercase tracking-widest">Thời gian</th>
<th className="px-8 py-4 text-[11px] font-bold text-on-surface-variant uppercase tracking-widest text-center">Tải hệ thống</th>
<th className="px-8 py-4 text-[11px] font-bold text-on-surface-variant uppercase tracking-widest text-right">Thao tác</th>
</tr>
</thead>
<tbody className="divide-y divide-surface-container-low">
<tr className="hover:bg-surface-container-low/30 transition-colors">
<td className="px-8 py-5">
<div className="flex flex-col">
<span className="font-bold text-on-surface">Khóa 2022 - Kỹ thuật</span>
<span className="text-xs text-on-surface-variant">Học kỳ 1 / 2024-2025</span>
</div>
</td>
<td className="px-8 py-5">
<div className="flex items-center gap-2">
<span className="material-symbols-outlined text-sm text-on-surface-variant">event</span>
<span className="text-sm">Hôm nay, 10:30 - 14:30</span>
</div>
</td>
<td className="px-8 py-5">
<div className="flex flex-col items-center gap-1">
<div className="w-24 h-1.5 bg-surface-container-highest rounded-full overflow-hidden">
<div className="w-[65%] h-full bg-primary-container"></div>
</div>
<span className="text-[10px] font-bold">65% Traffic</span>
</div>
</td>
<td className="px-8 py-5 text-right">
<div className="flex justify-end gap-2">
<button className="p-2 hover:bg-surface-container-high rounded-lg text-primary transition-colors">
<span className="material-symbols-outlined">edit</span>
</button>
<button className="p-2 hover:bg-error-container rounded-lg text-error transition-colors">
<span className="material-symbols-outlined">block</span>
</button>
</div>
</td>
</tr>
<tr className="bg-surface-container-low/20 hover:bg-surface-container-low/30 transition-colors">
<td className="px-8 py-5">
<div className="flex flex-col">
<span className="font-bold text-on-surface">Khóa 2021 - Kinh tế</span>
<span className="text-xs text-on-surface-variant">Học kỳ 1 / 2024-2025</span>
</div>
</td>
<td className="px-8 py-5">
<div className="flex items-center gap-2">
<span className="material-symbols-outlined text-sm text-on-surface-variant">event</span>
<span className="text-sm">Đã kết thúc (08:00 - 10:00)</span>
</div>
</td>
<td className="px-8 py-5 text-center">
<span className="px-3 py-1 bg-primary-fixed text-on-primary-fixed text-[10px] font-bold rounded-full">HOÀN TẤT</span>
</td>
<td className="px-8 py-5 text-right">
<button className="text-xs font-bold text-primary-container hover:underline">Xem báo cáo</button>
</td>
</tr>
<tr className="hover:bg-surface-container-low/30 transition-colors">
<td className="px-8 py-5">
<div className="flex flex-col">
<span className="font-bold text-on-surface">Khóa 2023 - Toàn khóa</span>
<span className="text-xs text-on-surface-variant">Học kỳ 1 / 2024-2025</span>
</div>
</td>
<td className="px-8 py-5">
<div className="flex items-center gap-2 text-on-surface-variant">
<span className="material-symbols-outlined text-sm">upcoming</span>
<span className="text-sm italic">Sắp diễn ra (15:00)</span>
</div>
</td>
<td className="px-8 py-5 text-center text-on-surface-variant">
<span className="text-[10px]">Chưa bắt đầu</span>
</td>
<td className="px-8 py-5 text-right">
<button className="p-2 hover:bg-surface-container-high rounded-lg text-primary transition-colors">
<span className="material-symbols-outlined">settings_suggest</span>
</button>
</td>
</tr>
</tbody>
</table>
</div>
</div>
</div>
{/*  Dashboard Analytics Preview (Editorial Detail)  */}
<div className="grid grid-cols-1 md:grid-cols-3 gap-6">
<div className="bg-surface-container-lowest p-6 rounded-xl flex items-center gap-6">
<div className="w-14 h-14 rounded-full bg-blue-50 flex items-center justify-center text-primary">
<span className="material-symbols-outlined text-3xl">groups</span>
</div>
<div>
<p className="text-xs font-bold text-on-surface-variant uppercase tracking-wider">Sinh viên trực tuyến</p>
<p className="text-3xl font-headline font-black text-on-surface">1,248</p>
</div>
</div>
<div className="bg-surface-container-lowest p-6 rounded-xl flex items-center gap-6">
<div className="w-14 h-14 rounded-full bg-amber-50 flex items-center justify-center text-secondary">
<span className="material-symbols-outlined text-3xl">bolt</span>
</div>
<div>
<p className="text-xs font-bold text-on-surface-variant uppercase tracking-wider">Độ trễ trung bình</p>
<p className="text-3xl font-headline font-black text-on-surface">42ms</p>
</div>
</div>
<div className="bg-surface-container-lowest p-6 rounded-xl flex items-center gap-6">
<div className="w-14 h-14 rounded-full bg-green-50 flex items-center justify-center text-green-700">
<span className="material-symbols-outlined text-3xl">check_circle</span>
</div>
<div>
<p className="text-xs font-bold text-on-surface-variant uppercase tracking-wider">Tỷ lệ thành công</p>
<p className="text-3xl font-headline font-black text-on-surface">99.8%</p>
</div>
</div>
</div>
</section>
{/*  Contextual Support Info  */}
<footer className="p-8 mt-12 border-t border-surface-container text-center">
<p className="text-sm text-on-surface-variant font-medium">© 2024 EduPort Editorial Intelligence. Tất cả thay đổi được lưu vết theo ID quản trị viên.</p>
</footer>
</main>
{/*  Floating Action for Quick Help (Suppressed per instructions on details pages, but shown here as a 'Global Status' mini-widget)  */}
<div className="fixed bottom-8 right-8 flex flex-col items-end gap-3 z-50">
<div className="bg-on-surface text-surface py-2 px-4 rounded-full text-xs font-bold flex items-center gap-2 shadow-xl">
<span className="w-2 h-2 rounded-full bg-green-500 animate-ping"></span>
            Hệ thống ổn định
        </div>
</div>

    </>
  );
};

export default SetupCuHnhGiVngTrafficSplittingQueueControl;
