import React from 'react';

const QunLDanhMcKhungMLpDataMaster = () => {
  return (
    <>
      
{/*  SideNavBar  */}

{/*  Main Content  */}
<main className=" flex-1 min-h-screen">
{/*  TopAppBar  */}

<div className="p-8 max-w-7xl mx-auto space-y-8">
{/*  Hero Header Section  */}
<div className="flex flex-col md:flex-row md:items-end justify-between gap-6">
<div className="space-y-2">
<h1 className="text-4xl font-extrabold tracking-tight text-on-surface">Tự Động Xếp Lịch &amp; Phòng Học</h1>
<p className="text-on-surface-variant max-w-2xl font-body">Tối ưu hóa tài nguyên giảng đường và thời gian của giảng viên bằng thuật toán AI tiên tiến. Giảm thiểu xung đột lịch trình lên đến 98%.</p>
</div>
<div className="flex items-center gap-3">
<button className="px-6 py-2.5 rounded-full font-bold text-sm bg-surface-container-high text-on-surface-variant hover:bg-surface-container-highest transition-all flex items-center gap-2">
<span className="material-symbols-outlined text-lg" data-icon="undo">undo</span>
                        Hoàn tác
                    </button>
<button className="px-8 py-2.5 rounded-full font-bold text-sm bg-gradient-to-br from-primary to-primary-container text-white shadow-lg hover:scale-[1.02] active:scale-95 transition-all flex items-center gap-2">
<span className="material-symbols-outlined text-lg" data-icon="save" style={{ /* FIXME: convert style string to object -> font-variation-settings: 'FILL' 1; */ }}>save</span>
                        Lưu TKB
                    </button>
</div>
</div>
{/*  Bento Grid - Action & Status  */}
<div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
{/*  Upload Section  */}
<div className="lg:col-span-2 bg-surface-container-lowest rounded-full p-8 space-y-6 shadow-sm flex flex-col justify-center">
<h2 className="text-xl font-bold flex items-center gap-2">
<span className="material-symbols-outlined text-primary" data-icon="upload_file">upload_file</span>
                        Nhập dữ liệu đầu vào
                    </h2>
<div className="grid grid-cols-1 md:grid-cols-2 gap-4">
<label className="flex flex-col items-center justify-center p-6 border-2 border-dashed border-outline-variant rounded-xl hover:bg-blue-50/50 cursor-pointer transition-colors group">
<span className="material-symbols-outlined text-3xl text-slate-400 group-hover:text-primary mb-2 transition-colors" data-icon="meeting_room">meeting_room</span>
<span className="text-sm font-bold text-on-surface">Danh sách Phòng rảnh</span>
<span className="text-[10px] text-on-surface-variant mt-1">Excel, CSV (Tối đa 5MB)</span>
<input className="hidden" type="file"/>
</label>
<label className="flex flex-col items-center justify-center p-6 border-2 border-dashed border-outline-variant rounded-xl hover:bg-blue-50/50 cursor-pointer transition-colors group">
<span className="material-symbols-outlined text-3xl text-slate-400 group-hover:text-secondary mb-2 transition-colors" data-icon="person_search">person_search</span>
<span className="text-sm font-bold text-on-surface">Giảng viên rảnh</span>
<span className="text-[10px] text-on-surface-variant mt-1">Excel, CSV (Tối đa 5MB)</span>
<input className="hidden" type="file"/>
</label>
</div>
<button className="w-full py-4 bg-primary text-white rounded-xl font-bold text-lg flex items-center justify-center gap-3 hover:shadow-xl hover:shadow-primary/20 transition-all">
<span className="material-symbols-outlined animate-pulse" data-icon="bolt" style={{ /* FIXME: convert style string to object -> font-variation-settings: 'FILL' 1; */ }}>bolt</span>
                        Bắt đầu xếp lịch thuật toán AI
                    </button>
</div>
{/*  Progress / Status Card  */}
<div className="bg-surface-container p-8 rounded-full shadow-sm flex flex-col items-center justify-center text-center space-y-6">
<div className="relative w-32 h-32 flex items-center justify-center">
<svg className="w-full h-full -rotate-90">
<circle className="text-surface-container-highest" cx="64" cy="64" fill="transparent" r="58" stroke="currentColor" stroke-width="8"></circle>
<circle className="text-primary" cx="64" cy="64" fill="transparent" r="58" stroke="currentColor" stroke-dasharray="364.4" stroke-dashoffset="100" stroke-width="8"></circle>
</svg>
<div className="absolute inset-0 flex flex-col items-center justify-center">
<span className="text-3xl font-black text-on-surface">72%</span>
<span className="text-[10px] font-bold text-on-surface-variant uppercase">Đang xử lý</span>
</div>
</div>
<div className="space-y-2">
<p className="font-bold text-on-surface">Đang phân bổ GĐ 2</p>
<p className="text-xs text-on-surface-variant italic">Dự kiến hoàn thành trong 15 giây...</p>
</div>
<div className="w-full bg-surface-container-highest h-2 rounded-full overflow-hidden">
<div className="bg-primary h-full w-[72%]"></div>
</div>
</div>
</div>
{/*  Error Alerts Section  */}
<div className="bg-error-container/30 rounded-xl p-6 flex flex-col md:flex-row items-center justify-between gap-6 border-l-8 border-error">
<div className="flex items-start gap-4">
<div className="p-3 bg-error/10 rounded-full text-error">
<span className="material-symbols-outlined text-3xl" data-icon="warning" style={{ /* FIXME: convert style string to object -> font-variation-settings: 'FILL' 1; */ }}>warning</span>
</div>
<div className="space-y-1">
<h3 className="text-lg font-bold text-on-error-container">Phát hiện xung đột tài nguyên (12 Lỗi)</h3>
<p className="text-sm text-on-error-container/80">Có 8 giảng viên bị trùng lịch dạy và 4 lớp học chưa được phân bổ phòng trong khung giờ Cao điểm (Sáng Thứ 2).</p>
</div>
</div>
<button className="whitespace-nowrap px-6 py-3 bg-error text-white rounded-full font-bold text-sm flex items-center gap-2 hover:bg-red-700 transition-colors shadow-lg shadow-error/20">
<span className="material-symbols-outlined text-lg" data-icon="engineering">engineering</span>
                    Yêu cầu can thiệp
                </button>
</div>
{/*  Results Table Section  */}
<div className="bg-surface-container-lowest rounded-xl shadow-sm overflow-hidden">
<div className="p-6 border-b border-surface-container flex flex-col md:flex-row justify-between items-center gap-4">
<h2 className="text-xl font-bold">Bảng TKB dự kiến - Học kỳ II (2023-2024)</h2>
<div className="flex items-center gap-2">
<button className="p-2 rounded-lg hover:bg-surface-container-low text-on-surface-variant transition-colors">
<span className="material-symbols-outlined" data-icon="filter_list">filter_list</span>
</button>
<button className="p-2 rounded-lg hover:bg-surface-container-low text-on-surface-variant transition-colors">
<span className="material-symbols-outlined" data-icon="download">download</span>
</button>
<div className="flex bg-surface-container rounded-lg p-1">
<button className="px-3 py-1 bg-white rounded shadow-sm text-xs font-bold">Xem theo Lớp</button>
<button className="px-3 py-1 text-xs font-medium text-on-surface-variant">Xem theo Giảng viên</button>
</div>
</div>
</div>
<div className="overflow-x-auto">
<table className="w-full text-left border-collapse">
<thead>
<tr className="bg-surface-container-low">
<th className="px-6 py-4 text-[10px] font-black uppercase tracking-widest text-on-surface-variant">Mã Lớp / Môn Học</th>
<th className="px-6 py-4 text-[10px] font-black uppercase tracking-widest text-on-surface-variant">Giảng Viên</th>
<th className="px-6 py-4 text-[10px] font-black uppercase tracking-widest text-on-surface-variant">Thời Gian</th>
<th className="px-6 py-4 text-[10px] font-black uppercase tracking-widest text-on-surface-variant">Phòng Học</th>
<th className="px-6 py-4 text-[10px] font-black uppercase tracking-widest text-on-surface-variant">Trạng Thái AI</th>
<th className="px-6 py-4"></th>
</tr>
</thead>
<tbody className="divide-y divide-surface-container">
<tr className="hover:bg-surface-container-lowest/50 transition-colors">
<td className="px-6 py-5">
<div className="font-bold text-on-surface">IT4402 - Trí tuệ nhân tạo</div>
<div className="text-[10px] text-on-surface-variant">Nhóm 01 • 65 Sinh viên</div>
</td>
<td className="px-6 py-5">
<div className="flex items-center gap-3">
<div className="w-8 h-8 rounded-full bg-slate-200">
<img alt="Avatar" className="w-full h-full rounded-full object-cover" data-alt="Portrait of a middle-aged male professor with glasses, academic attire, soft focus library background" src="https://lh3.googleusercontent.com/aida-public/AB6AXuB9u18DBdyECBrFE8rrbkrxp2mn2HN4NYfNzE8MRwgzRVXWaneMB2VbWebhzUuByreA-LHT3_cLXcTZdcoo0SmiUMd9NBWFpcIQWo75athM1hoypd6nCAno0MHEieNS3nv7rbort6cY553La6NueNUbOuSBbzgWiq_15gO7vv4VQ850ZwJ-WepDkcsZ363gQKSqaznmRxkhW1MWVF1VZUc1EO6GCE0UEbq9KyMQUrUEBTeWcQGuk2gvjyaIwrV9LwPHKLYHnfdkexVn"/>
</div>
<span className="text-sm font-medium">GS. TS Nguyễn Văn An</span>
</div>
</td>
<td className="px-6 py-5">
<div className="text-sm">Thứ 2 (Tiết 1-3)</div>
<div className="text-[10px] text-on-surface-variant">07:00 - 09:30</div>
</td>
<td className="px-6 py-5 font-bold text-primary">P.502 - Nhà D3</td>
<td className="px-6 py-5">
<span className="px-3 py-1 bg-primary-fixed text-on-primary-fixed rounded-full text-[10px] font-bold">Tối Ưu 100%</span>
</td>
<td className="px-6 py-5 text-right">
<button className="text-on-surface-variant hover:text-primary"><span className="material-symbols-outlined" data-icon="edit">edit</span></button>
</td>
</tr>
<tr className="bg-error/5 hover:bg-error/10 transition-colors">
<td className="px-6 py-5">
<div className="font-bold text-error">ME3001 - Cơ học kỹ thuật</div>
<div className="text-[10px] text-error">Nhóm 04 • 120 Sinh viên</div>
</td>
<td className="px-6 py-5">
<div className="flex items-center gap-3">
<div className="w-8 h-8 rounded-full bg-slate-200">
<img alt="Avatar" className="w-full h-full rounded-full object-cover" data-alt="Female teacher in professional business attire, holding a tablet, bright classroom background" src="https://lh3.googleusercontent.com/aida-public/AB6AXuDcfFepC5dVci1RbLZTvF0OGSS53QnbAVYMUhUasAcbhH77LP91uATesNDSTEnX6Umn8qDbRBJnkSq2ssOrRO1QvL6T3j0pOwIJU1MW-v68ipYdWpENIVleZi9pA0jbb747qrhT22mCp8nJkfP-ovI3kt_SN4WFr_cJPoZtEl-9TzAL-IsiS8xD7ANT-bqnLWJw9laD2zj8Gso15z7pMoOTB2joKcte-qfvtX7TW1ig7W-5o6i_QrIayANAHIt5AgNv33wpQp9OA23h"/>
</div>
<span className="text-sm font-medium text-error">ThS. Lê Thị Bình</span>
</div>
</td>
<td className="px-6 py-5">
<div className="text-sm text-error font-bold">Thứ 2 (Tiết 1-3)</div>
<div className="text-[10px] text-error/80">Trùng lịch giảng dạy</div>
</td>
<td className="px-6 py-5">
<div className="px-3 py-1 border border-error/20 bg-error-container text-on-error-container rounded text-[10px] font-bold inline-block">CHƯA PHÂN BỔ</div>
</td>
<td className="px-6 py-5">
<span className="px-3 py-1 bg-error text-white rounded-full text-[10px] font-bold">XUNG ĐỘT</span>
</td>
<td className="px-6 py-5 text-right">
<button className="text-error hover:scale-110 transition-transform"><span className="material-symbols-outlined" data-icon="priority_high">priority_high</span></button>
</td>
</tr>
<tr className="hover:bg-surface-container-lowest/50 transition-colors">
<td className="px-6 py-5">
<div className="font-bold text-on-surface">FL2011 - Tiếng Anh Chuyên Ngành</div>
<div className="text-[10px] text-on-surface-variant">Nhóm 12 • 35 Sinh viên</div>
</td>
<td className="px-6 py-5">
<div className="flex items-center gap-3">
<div className="w-8 h-8 rounded-full bg-slate-200">
<img alt="Avatar" className="w-full h-full rounded-full object-cover" data-alt="Portrait of a young diverse lecturer smiling, modern academic setting" src="https://lh3.googleusercontent.com/aida-public/AB6AXuBwhmSUvmF3JYoamQiTIGvXuMYapV9ODK4NHoWjq-gt-QnXIRoc1W-3mghi6o3KBA8-rZK_zwOmWpYzURxaWzzV87AguCK1scqaxUaqh_c-BZUBuDWX1lH-CWv6_mMKdCrKPfBnB_ldxhqQIXvGHnDV21G_k5UirDuYOtYouI-vWiRir43jref-3YenXuDfDuKtivEOQal5zmSmam422U_-BfEZgjqgkLW_8ZIXV2L7LS319qSzq2qHVJhR8KrBKihVOXe71TIffaii"/>
</div>
<span className="text-sm font-medium">ThS. Trần Minh Tâm</span>
</div>
</td>
<td className="px-6 py-5">
<div className="text-sm">Thứ 3 (Tiết 4-6)</div>
<div className="text-[10px] text-on-surface-variant">09:45 - 12:15</div>
</td>
<td className="px-6 py-5 font-bold text-primary">P.201 - Nhà B1</td>
<td className="px-6 py-5">
<span className="px-3 py-1 bg-secondary-fixed text-on-secondary-fixed rounded-full text-[10px] font-bold">Hợp lý</span>
</td>
<td className="px-6 py-5 text-right">
<button className="text-on-surface-variant hover:text-primary"><span className="material-symbols-outlined" data-icon="edit">edit</span></button>
</td>
</tr>
</tbody>
</table>
</div>
<div className="p-4 bg-surface-container-low/50 flex justify-between items-center">
<span className="text-xs text-on-surface-variant font-medium">Hiển thị 25 trên 1.450 bản ghi dự kiến</span>
<div className="flex gap-1">
<button className="w-8 h-8 flex items-center justify-center rounded bg-white shadow-sm hover:bg-primary-container hover:text-white transition-all"><span className="material-symbols-outlined text-sm" data-icon="chevron_left">chevron_left</span></button>
<button className="w-8 h-8 flex items-center justify-center rounded bg-primary text-white shadow-md text-xs font-bold">1</button>
<button className="w-8 h-8 flex items-center justify-center rounded bg-white shadow-sm hover:bg-primary-container hover:text-white transition-all text-xs font-bold">2</button>
<button className="w-8 h-8 flex items-center justify-center rounded bg-white shadow-sm hover:bg-primary-container hover:text-white transition-all text-xs font-bold">3</button>
<button className="w-8 h-8 flex items-center justify-center rounded bg-white shadow-sm hover:bg-primary-container hover:text-white transition-all"><span className="material-symbols-outlined text-sm" data-icon="chevron_right">chevron_right</span></button>
</div>
</div>
</div>
{/*  AI Insights Footer  */}
<div className="grid grid-cols-1 md:grid-cols-3 gap-6">
<div className="bg-primary/5 p-6 rounded-xl border-l-4 border-primary space-y-2">
<p className="text-xs font-black text-primary uppercase tracking-tighter">Độ tin cậy thuật toán</p>
<p className="text-2xl font-black text-on-surface">94.8%</p>
<p className="text-[10px] text-on-surface-variant">Dựa trên 120 tiêu chí ràng buộc đã thiết lập.</p>
</div>
<div className="bg-secondary/5 p-6 rounded-xl border-l-4 border-secondary-container space-y-2">
<p className="text-xs font-black text-secondary uppercase tracking-tighter">Tỷ lệ sử dụng phòng</p>
<p className="text-2xl font-black text-on-surface">82.1%</p>
<p className="text-[10px] text-on-surface-variant">Tăng 15% so với học kỳ trước.</p>
</div>
<div className="bg-tertiary/5 p-6 rounded-xl border-l-4 border-tertiary space-y-2">
<p className="text-xs font-black text-tertiary uppercase tracking-tighter">Xung đột dự kiến</p>
<p className="text-2xl font-black text-on-surface">12</p>
<p className="text-[10px] text-on-surface-variant">Cần xử lý thủ công hoặc nới lỏng ràng buộc.</p>
</div>
</div>
</div>
</main>

    </>
  );
};

export default QunLDanhMcKhungMLpDataMaster;
