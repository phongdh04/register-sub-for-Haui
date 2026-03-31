import React from 'react';

const LchSNhtKDuChnAuditTrailsLogging = () => {
  return (
    <>
      
{/*  SideNavBar  */}

{/*  Main Content  */}
<main className=" flex-1 flex flex-col">
{/*  TopNavBar  */}

<section className="p-8 space-y-8">
{/*  Header Section  */}
<div className="flex flex-col md:flex-row md:items-end justify-between gap-6">
<div>
<h2 className="text-4xl font-extrabold text-blue-900 dark:text-blue-100 tracking-tight font-headline">Nhật Ký Dấu Chân</h2>
<p className="text-on-surface-variant mt-2 font-medium">Theo dõi và giám sát mọi hoạt động bảo mật trên toàn hệ thống.</p>
</div>
<div className="flex items-center gap-3">
<button className="flex items-center gap-2 px-5 py-2.5 bg-surface-container-high rounded-full text-primary font-semibold hover:bg-surface-container-highest transition-all text-sm">
<span className="material-symbols-outlined text-lg" data-icon="file_download">file_download</span>
                        Xuất dữ liệu (CSV)
                    </button>
<button className="flex items-center gap-2 px-5 py-2.5 bg-primary text-on-primary rounded-full font-semibold hover:opacity-90 transition-all text-sm shadow-md">
<span className="material-symbols-outlined text-lg" data-icon="refresh">refresh</span>
                        Làm mới
                    </button>
</div>
</div>
{/*  Bento Filter Grid  */}
<div className="grid grid-cols-1 md:grid-cols-4 gap-4">
<div className="md:col-span-1 bg-surface-container-lowest p-5 rounded-xl shadow-sm space-y-3">
<label className="text-xs font-bold uppercase tracking-widest text-slate-500 font-label">Người dùng</label>
<div className="relative">
<span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 text-sm" data-icon="person">person</span>
<input className="w-full bg-surface-container-low border-none rounded-lg py-2.5 pl-9 pr-4 text-sm focus:ring-2 focus:ring-primary/20" placeholder="Tên hoặc Email..." type="text"/>
</div>
</div>
<div className="md:col-span-1 bg-surface-container-lowest p-5 rounded-xl shadow-sm space-y-3">
<label className="text-xs font-bold uppercase tracking-widest text-slate-500 font-label">Loại hành động</label>
<div className="relative">
<span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 text-sm" data-icon="category">category</span>
<select className="w-full bg-surface-container-low border-none rounded-lg py-2.5 pl-9 pr-4 text-sm focus:ring-2 focus:ring-primary/20 appearance-none">
<option>Tất cả hành động</option>
<option>Sửa điểm số</option>
<option>Duyệt hồ sơ</option>
<option>Đăng nhập</option>
<option>Xóa dữ liệu</option>
</select>
</div>
</div>
<div className="md:col-span-2 bg-surface-container-lowest p-5 rounded-xl shadow-sm space-y-3">
<label className="text-xs font-bold uppercase tracking-widest text-slate-500 font-label">Khoảng thời gian</label>
<div className="flex items-center gap-3">
<div className="relative flex-1">
<span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 text-sm" data-icon="calendar_today">calendar_today</span>
<input className="w-full bg-surface-container-low border-none rounded-lg py-2.5 pl-9 pr-4 text-sm focus:ring-2 focus:ring-primary/20" type="date"/>
</div>
<span className="text-slate-400">đến</span>
<div className="relative flex-1">
<span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 text-sm" data-icon="event">event</span>
<input className="w-full bg-surface-container-low border-none rounded-lg py-2.5 pl-9 pr-4 text-sm focus:ring-2 focus:ring-primary/20" type="date"/>
</div>
</div>
</div>
</div>
{/*  Stats Overview - Tonal Layering  */}
<div className="flex gap-6 overflow-x-auto pb-2 scrollbar-hide">
<div className="flex-none w-64 bg-surface-container-low p-6 rounded-xl space-y-2 border-l-4 border-primary">
<p className="text-xs font-bold text-slate-500 uppercase tracking-tighter">Tổng số bản ghi</p>
<p className="text-3xl font-black text-on-surface">12,845</p>
<div className="flex items-center gap-1 text-primary text-xs font-bold">
<span className="material-symbols-outlined text-sm" data-icon="trending_up">trending_up</span>
                        +12% hôm nay
                    </div>
</div>
<div className="flex-none w-64 bg-surface-container-low p-6 rounded-xl space-y-2 border-l-4 border-secondary">
<p className="text-xs font-bold text-slate-500 uppercase tracking-tighter">Cảnh báo bảo mật</p>
<p className="text-3xl font-black text-on-surface">24</p>
<div className="flex items-center gap-1 text-secondary text-xs font-bold">
<span className="material-symbols-outlined text-sm" data-icon="warning">warning</span>
                        Cần kiểm tra ngay
                    </div>
</div>
<div className="flex-none w-64 bg-surface-container-low p-6 rounded-xl space-y-2 border-l-4 border-primary-container">
<p className="text-xs font-bold text-slate-500 uppercase tracking-tighter">Đăng nhập thành công</p>
<p className="text-3xl font-black text-on-surface">98.2%</p>
<div className="flex items-center gap-1 text-blue-600 text-xs font-bold">
<span className="material-symbols-outlined text-sm" data-icon="check_circle">check_circle</span>
                        Hệ thống ổn định
                    </div>
</div>
</div>
{/*  Audit Log Table - Editorial Grid Style  */}
<div className="bg-surface-container-lowest rounded-xl overflow-hidden shadow-sm">
<div className="px-8 py-5 flex items-center justify-between border-b border-surface-container">
<h3 className="text-lg font-bold text-on-surface font-headline">Bản ghi chi tiết</h3>
<div className="flex items-center gap-4">
<span className="text-sm text-on-surface-variant font-medium">Hiển thị 1 - 20 trong 12,845</span>
<div className="flex gap-1">
<button className="p-1.5 hover:bg-surface-container rounded-lg"><span className="material-symbols-outlined text-lg" data-icon="chevron_left">chevron_left</span></button>
<button className="p-1.5 hover:bg-surface-container rounded-lg"><span className="material-symbols-outlined text-lg" data-icon="chevron_right">chevron_right</span></button>
</div>
</div>
</div>
<div className="overflow-x-auto">
<table className="w-full text-left border-collapse">
<thead>
<tr className="bg-surface-container-low/50">
<th className="px-8 py-4 text-[11px] font-bold uppercase tracking-[0.1em] text-slate-500 font-label">Thời gian</th>
<th className="px-8 py-4 text-[11px] font-bold uppercase tracking-[0.1em] text-slate-500 font-label">Người thực hiện</th>
<th className="px-8 py-4 text-[11px] font-bold uppercase tracking-[0.1em] text-slate-500 font-label">Hành động</th>
<th className="px-8 py-4 text-[11px] font-bold uppercase tracking-[0.1em] text-slate-500 font-label">Đối tượng</th>
<th className="px-8 py-4 text-[11px] font-bold uppercase tracking-[0.1em] text-slate-500 font-label">Địa chỉ IP</th>
<th className="px-8 py-4 text-[11px] font-bold uppercase tracking-[0.1em] text-slate-500 font-label">Trạng thái</th>
<th className="px-8 py-4"></th>
</tr>
</thead>
<tbody className="divide-y divide-surface-container">
<tr className="hover:bg-surface-container-low transition-colors">
<td className="px-8 py-5">
<p className="text-sm font-semibold text-on-surface">14:23:05</p>
<p className="text-[11px] text-slate-500 font-medium">20/10/2023</p>
</td>
<td className="px-8 py-5">
<div className="flex items-center gap-3">
<div className="w-8 h-8 rounded-full bg-primary-fixed flex items-center justify-center text-primary text-[10px] font-bold">NV</div>
<div>
<p className="text-sm font-bold text-on-surface">Nguyễn Văn A</p>
<p className="text-[11px] text-slate-500">Giảng viên - IT</p>
</div>
</div>
</td>
<td className="px-8 py-5">
<span className="text-sm font-medium text-on-surface flex items-center gap-2">
<span className="material-symbols-outlined text-primary text-lg" data-icon="edit_square">edit_square</span>
                                        Sửa điểm số
                                    </span>
</td>
<td className="px-8 py-5">
<p className="text-sm font-medium text-on-surface-variant">MSSV: 20216045</p>
<p className="text-[11px] text-slate-500 italic">Môn: Giải tích 1</p>
</td>
<td className="px-8 py-5">
<span className="font-mono text-xs text-slate-600 bg-surface-container px-2 py-1 rounded">192.168.1.45</span>
</td>
<td className="px-8 py-5">
<span className="px-3 py-1 bg-primary-fixed text-on-primary-fixed text-[10px] font-bold rounded-full uppercase tracking-tighter">Thành công</span>
</td>
<td className="px-8 py-5 text-right">
<button className="p-2 text-slate-400 hover:text-primary transition-colors"><span className="material-symbols-outlined" data-icon="info">info</span></button>
</td>
</tr>
<tr className="hover:bg-surface-container-low transition-colors">
<td className="px-8 py-5">
<p className="text-sm font-semibold text-on-surface">14:15:22</p>
<p className="text-[11px] text-slate-500 font-medium">20/10/2023</p>
</td>
<td className="px-8 py-5">
<div className="flex items-center gap-3">
<img className="w-8 h-8 rounded-full border border-surface-container" data-alt="portrait of a professional woman with a friendly smile" src="https://lh3.googleusercontent.com/aida-public/AB6AXuDlXuhFx3RcVEuFzPx80GEeIMok_GD_AqeBte-5CleILBccCFyGAm0PIEbsxuV6XrMSMvK2sfdKsql4Att-zW9Bl7333YIdvhckdFVb9AhC3H8WtfLpqPG30qCF4HGdY6OvopRG7_jhuflN4jffdB1ZCFo9rRocwnxd8I0JfpXh99KCf7Zcfe7a0fStF-ob-PpAkEBj4xHCAHKYnoGtOxN7eAGyWz9IwyXlLHvvjLNSVYxY24bjZSiEKKBd0_9Fyi9traSJc78kWRqB"/>
<div>
<p className="text-sm font-bold text-on-surface">Lê Thị B</p>
<p className="text-[11px] text-slate-500">Phòng Đào tạo</p>
</div>
</div>
</td>
<td className="px-8 py-5">
<span className="text-sm font-medium text-on-surface flex items-center gap-2">
<span className="material-symbols-outlined text-secondary text-lg" data-icon="verified">verified</span>
                                        Duyệt hồ sơ
                                    </span>
</td>
<td className="px-8 py-5">
<p className="text-sm font-medium text-on-surface-variant">ĐK Xét tuyển #982</p>
<p className="text-[11px] text-slate-500 italic">Ứng viên: Trần Văn C</p>
</td>
<td className="px-8 py-5">
<span className="font-mono text-xs text-slate-600 bg-surface-container px-2 py-1 rounded">172.16.2.110</span>
</td>
<td className="px-8 py-5">
<span className="px-3 py-1 bg-primary-fixed text-on-primary-fixed text-[10px] font-bold rounded-full uppercase tracking-tighter">Thành công</span>
</td>
<td className="px-8 py-5 text-right">
<button className="p-2 text-slate-400 hover:text-primary transition-colors"><span className="material-symbols-outlined" data-icon="info">info</span></button>
</td>
</tr>
<tr className="hover:bg-surface-container-low transition-colors">
<td className="px-8 py-5">
<p className="text-sm font-semibold text-on-surface">14:02:10</p>
<p className="text-[11px] text-slate-500 font-medium">20/10/2023</p>
</td>
<td className="px-8 py-5">
<div className="flex items-center gap-3">
<div className="w-8 h-8 rounded-full bg-slate-200 flex items-center justify-center text-slate-600 text-[10px] font-bold">??</div>
<div>
<p className="text-sm font-bold text-on-surface">Unknown User</p>
<p className="text-[11px] text-slate-500">Khách</p>
</div>
</div>
</td>
<td className="px-8 py-5">
<span className="text-sm font-medium text-on-surface flex items-center gap-2">
<span className="material-symbols-outlined text-slate-400 text-lg" data-icon="login">login</span>
                                        Đăng nhập
                                    </span>
</td>
<td className="px-8 py-5">
<p className="text-sm font-medium text-on-surface-variant">Hệ thống Admin</p>
<p className="text-[11px] text-slate-500 italic">Login Attempt</p>
</td>
<td className="px-8 py-5">
<span className="font-mono text-xs text-slate-600 bg-surface-container px-2 py-1 rounded">103.25.14.8</span>
</td>
<td className="px-8 py-5">
<span className="px-3 py-1 bg-error-container text-on-error-container text-[10px] font-bold rounded-full uppercase tracking-tighter">Thất bại</span>
</td>
<td className="px-8 py-5 text-right">
<button className="p-2 text-slate-400 hover:text-primary transition-colors"><span className="material-symbols-outlined" data-icon="info">info</span></button>
</td>
</tr>
<tr className="hover:bg-surface-container-low transition-colors">
<td className="px-8 py-5">
<p className="text-sm font-semibold text-on-surface">13:45:00</p>
<p className="text-[11px] text-slate-500 font-medium">20/10/2023</p>
</td>
<td className="px-8 py-5">
<div className="flex items-center gap-3">
<img className="w-8 h-8 rounded-full border border-surface-container" data-alt="portrait of an experienced male administrator with glasses" src="https://lh3.googleusercontent.com/aida-public/AB6AXuAADAMrDRBpr4I7TVbALpIgQMjc2gFBLlXVjxhs1gRNPWbAxv-2PpoTR5-gzgMrPGZJ0CqnPD77p0AwoNwAsJI4hD76kS4YhE_uE6i1K5qHeVCTNTFhpXaswginElqieVrKH6GKK6ArNZHLSBj72OGXKyEf650wrtkW1vXDeftHcllNhhWUk7LpERBgIXkale5_k1k3GC9YtLwkPt80dNej93yQ-jtXXTzF156K61ZbCYJUyEmySdxvQ5EStQFgqbrzDxeyUvo5zFHr"/>
<div>
<p className="text-sm font-bold text-on-surface">Phạm Minh C</p>
<p className="text-[11px] text-slate-500">Quản trị viên IT</p>
</div>
</div>
</td>
<td className="px-8 py-5">
<span className="text-sm font-medium text-on-surface flex items-center gap-2">
<span className="material-symbols-outlined text-tertiary text-lg" data-icon="delete_forever">delete_forever</span>
                                        Xóa dữ liệu
                                    </span>
</td>
<td className="px-8 py-5">
<p className="text-sm font-medium text-on-surface-variant">Lớp học: 2023.2.IT4010</p>
<p className="text-[11px] text-slate-500 italic">Hành động nhạy cảm</p>
</td>
<td className="px-8 py-5">
<span className="font-mono text-xs text-slate-600 bg-surface-container px-2 py-1 rounded">192.168.1.2</span>
</td>
<td className="px-8 py-5">
<span className="px-3 py-1 bg-secondary-fixed text-on-secondary-fixed text-[10px] font-bold rounded-full uppercase tracking-tighter">Cần duyệt</span>
</td>
<td className="px-8 py-5 text-right">
<button className="p-2 text-slate-400 hover:text-primary transition-colors"><span className="material-symbols-outlined" data-icon="info">info</span></button>
</td>
</tr>
</tbody>
</table>
</div>
<div className="px-8 py-5 bg-surface-container-low/30 border-t border-surface-container flex items-center justify-between">
<div className="flex items-center gap-2">
<span className="text-sm text-on-surface-variant">Số hàng mỗi trang:</span>
<select className="bg-transparent border-none text-sm font-bold focus:ring-0">
<option>20</option>
<option>50</option>
<option>100</option>
</select>
</div>
<div className="flex items-center gap-2">
<button className="px-4 py-1.5 rounded-lg border border-outline-variant/30 text-sm font-semibold hover:bg-surface-container transition-all">Trước</button>
<div className="flex gap-1">
<button className="w-8 h-8 flex items-center justify-center rounded-lg bg-primary text-on-primary text-sm font-bold">1</button>
<button className="w-8 h-8 flex items-center justify-center rounded-lg hover:bg-surface-container text-sm font-medium">2</button>
<button className="w-8 h-8 flex items-center justify-center rounded-lg hover:bg-surface-container text-sm font-medium">3</button>
<span className="w-8 h-8 flex items-center justify-center text-slate-400">...</span>
<button className="w-8 h-8 flex items-center justify-center rounded-lg hover:bg-surface-container text-sm font-medium">642</button>
</div>
<button className="px-4 py-1.5 rounded-lg border border-outline-variant/30 text-sm font-semibold hover:bg-surface-container transition-all">Sau</button>
</div>
</div>
</div>
{/*  Focus Module: Activity Heatmap (Asymmetric Layout)  */}
<div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
<div className="lg:col-span-2 bg-surface-container p-8 rounded-2xl relative overflow-hidden">
<div className="relative z-10">
<h4 className="text-xl font-bold text-on-surface font-headline mb-6">Mật độ hoạt động trong tuần</h4>
<div className="flex items-end justify-between h-48 gap-2">
<div className="flex-1 flex flex-col items-center gap-3">
<div className="w-full bg-primary/20 rounded-t-lg relative" style={{ /* FIXME: convert style string to object -> height: 60%; */ }}>
<div className="absolute bottom-0 w-full bg-primary rounded-t-lg transition-all" style={{ /* FIXME: convert style string to object -> height: 40%; */ }}></div>
</div>
<span className="text-[10px] font-bold text-slate-500 uppercase">T2</span>
</div>
<div className="flex-1 flex flex-col items-center gap-3">
<div className="w-full bg-primary/20 rounded-t-lg relative" style={{ /* FIXME: convert style string to object -> height: 75%; */ }}>
<div className="absolute bottom-0 w-full bg-primary rounded-t-lg transition-all" style={{ /* FIXME: convert style string to object -> height: 65%; */ }}></div>
</div>
<span className="text-[10px] font-bold text-slate-500 uppercase">T3</span>
</div>
<div className="flex-1 flex flex-col items-center gap-3">
<div className="w-full bg-primary/20 rounded-t-lg relative" style={{ /* FIXME: convert style string to object -> height: 90%; */ }}>
<div className="absolute bottom-0 w-full bg-primary rounded-t-lg transition-all" style={{ /* FIXME: convert style string to object -> height: 85%; */ }}></div>
</div>
<span className="text-[10px] font-bold text-slate-500 uppercase">T4</span>
</div>
<div className="flex-1 flex flex-col items-center gap-3">
<div className="w-full bg-primary/20 rounded-t-lg relative" style={{ /* FIXME: convert style string to object -> height: 40%; */ }}>
<div className="absolute bottom-0 w-full bg-primary rounded-t-lg transition-all" style={{ /* FIXME: convert style string to object -> height: 25%; */ }}></div>
</div>
<span className="text-[10px] font-bold text-slate-500 uppercase">T5</span>
</div>
<div className="flex-1 flex flex-col items-center gap-3">
<div className="w-full bg-primary/20 rounded-t-lg relative" style={{ /* FIXME: convert style string to object -> height: 65%; */ }}>
<div className="absolute bottom-0 w-full bg-primary rounded-t-lg transition-all" style={{ /* FIXME: convert style string to object -> height: 50%; */ }}></div>
</div>
<span className="text-[10px] font-bold text-slate-500 uppercase">T6</span>
</div>
<div className="flex-1 flex flex-col items-center gap-3">
<div className="w-full bg-primary/20 rounded-t-lg relative" style={{ /* FIXME: convert style string to object -> height: 25%; */ }}>
<div className="absolute bottom-0 w-full bg-primary rounded-t-lg transition-all" style={{ /* FIXME: convert style string to object -> height: 15%; */ }}></div>
</div>
<span className="text-[10px] font-bold text-slate-500 uppercase">T7</span>
</div>
<div className="flex-1 flex flex-col items-center gap-3">
<div className="w-full bg-primary/20 rounded-t-lg relative" style={{ /* FIXME: convert style string to object -> height: 15%; */ }}>
<div className="absolute bottom-0 w-full bg-primary rounded-t-lg transition-all" style={{ /* FIXME: convert style string to object -> height: 10%; */ }}></div>
</div>
<span className="text-[10px] font-bold text-slate-500 uppercase">CN</span>
</div>
</div>
</div>
<div className="absolute top-0 right-0 w-64 h-64 bg-primary/5 rounded-full -mr-20 - blur-3xl"></div>
</div>
<div className="bg-primary-container p-8 rounded-2xl text-on-primary flex flex-col justify-between shadow-xl">
<div>
<span className="material-symbols-outlined text-4xl mb-4" data-icon="shield_with_heart" style={{ /* FIXME: convert style string to object -> font-variation-settings: 'FILL' 1; */ }}>shield_with_heart</span>
<h4 className="text-xl font-bold font-headline leading-tight mb-2">Báo cáo An ninh Hệ thống</h4>
<p className="text-on-primary-container text-sm leading-relaxed opacity-80">Trong 24h qua, hệ thống đã ngăn chặn 12 nỗ lực truy cập trái phép từ IP nước ngoài.</p>
</div>
<button className="mt-8 py-3 bg-white text-primary rounded-full font-bold text-sm hover:bg-opacity-90 transition-all">
                        Xem chi tiết bảo mật
                    </button>
</div>
</div>
</section>
{/*  Footer  */}
<footer className="mt-auto p-8 border-t border-surface-container flex flex-col md:flex-row justify-between items-center gap-4 text-on-surface-variant text-xs font-medium">
<p>© 2023 EduPort Admin Terminal. All rights reserved.</p>
<div className="flex gap-6">
<a className="hover:text-primary" href="#">Chính sách bảo mật</a>
<a className="hover:text-primary" href="#">Điều khoản sử dụng</a>
<a className="hover:text-primary" href="#">Liên hệ hỗ trợ</a>
</div>
</footer>
</main>
{/*  FAB Contextual Action  */}
<div className="fixed bottom-8 right-8">
<button className="w-14 h-14 bg-secondary rounded-full flex items-center justify-center text-on-secondary shadow-2xl hover:scale-110 transition-transform active:scale-95 group relative">
<span className="material-symbols-outlined text-2xl" data-icon="add_moderator">add_moderator</span>
<span className="absolute right-16 bg-on-surface text-surface px-3 py-1 rounded text-[11px] whitespace-nowrap opacity-0 group-hover:opacity-100 transition-opacity font-bold">Thêm quy tắc mới</span>
</button>
</div>

    </>
  );
};

export default LchSNhtKDuChnAuditTrailsLogging;
