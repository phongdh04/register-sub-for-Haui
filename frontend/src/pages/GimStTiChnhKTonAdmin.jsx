import React from 'react';

const GimStTiChnhKTonAdmin = () => {
  return (
    <>
      
{/*  TopNavBar (Shared Component)  */}

{/*  SideNavBar (Shared Component)  */}

{/*  Main Content Canvas  */}
<main className="  px-8 pb-12">
{/*  Header Section  */}
<div className="flex flex-col lg:flex-row lg:items-end justify-between gap-6 mb-12">
<div>
<nav className="flex items-center gap-2 text-xs font-semibold uppercase tracking-widest text-on-surface-variant mb-4">
<span>Tài chính</span>
<span className="material-symbols-outlined text-[14px]">chevron_right</span>
<span className="text-primary">Quản lý Học phí</span>
</nav>
<h1 className="text-5xl font-extrabold font-headline tracking-tight text-on-surface">Quản lý Công nợ &amp; Tài chính</h1>
<p className="mt-2 text-on-surface-variant max-w-2xl font-body">Giám sát doanh thu tổ chức, quản lý nghĩa vụ tài chính cá nhân của sinh viên và thực hiện khóa hệ thống đối với các tài khoản nợ quá hạn.</p>
</div>
<div className="flex flex-wrap gap-3">
<button className="flex items-center gap-2 px-6 py-3 border border-outline-variant hover:bg-surface-container-high transition-colors rounded-full font-semibold text-sm">
<span className="material-symbols-outlined text-xl">upload_file</span> Xuất Excel
                </button>
<button className="jewel-gradient flex items-center gap-2 px-8 py-3 text-white rounded-full font-bold text-sm shadow-lg hover:opacity-90 transition-all">
<span className="material-symbols-outlined text-xl">account_balance_wallet</span> Bù trừ thủ công
                </button>
</div>
</div>
{/*  High-Level KPI Grid  */}
<div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-12">
<div className="bg-surface-container-lowest p-8 rounded-xl editorial-shadow border-l-4 border-primary">
<p className="label-sm font-bold uppercase tracking-widest text-on-surface-variant mb-2">Tổng nợ sinh viên</p>
<div className="flex items-end gap-2">
<span className="text-3xl font-black text-on-surface">1.420.500.000₫</span>
<span className="text-error font-bold text-sm mb-1 flex items-center"><span className="material-symbols-outlined text-sm">trending_up</span> +4.2%</span>
</div>
</div>
<div className="bg-surface-container-lowest p-8 rounded-xl editorial-shadow border-l-4 border-secondary">
<p className="label-sm font-bold uppercase tracking-widest text-on-surface-variant mb-2">Số dư ví sinh viên</p>
<div className="flex items-end gap-2">
<span className="text-3xl font-black text-on-surface">842.120.000₫</span>
<span className="text-primary font-bold text-sm mb-1 flex items-center"><span className="material-symbols-outlined text-sm">trending_down</span> -1.5%</span>
</div>
</div>
<div className="bg-surface-container-lowest p-8 rounded-xl editorial-shadow border-l-4 border-tertiary">
<p className="label-sm font-bold uppercase tracking-widest text-on-surface-variant mb-2">Sinh viên bị khóa thi</p>
<div className="flex items-end gap-2">
<span className="text-3xl font-black text-on-surface">142</span>
<span className="text-on-surface-variant font-medium text-sm mb-1">Sinh viên bị hạn chế</span>
</div>
</div>
</div>
{/*  Main Content Area  */}
<div className="bg-surface-container-lowest rounded-xl editorial-shadow overflow-hidden">
{/*  Table Controls  */}
<div className="p-6 bg-surface-container-low flex flex-col md:flex-row md:items-center justify-between gap-4">
<div className="flex items-center gap-4">
<h3 className="font-headline font-bold text-lg text-on-surface">Sổ cái Phải thu</h3>
<div className="h-4 w-px bg-outline-variant"></div>
<span className="text-sm font-medium text-on-surface-variant">Đang hiển thị 1.240 bản ghi</span>
</div>
<div className="flex items-center gap-3">
<div className="relative">
<select className="appearance-none pl-4 pr-10 py-2 bg-white rounded-full border border-outline-variant text-sm font-medium focus:ring-primary focus:border-primary">
<option>Lọc theo lớp</option>
<option>CS-2024</option>
<option>ECON-2023</option>
</select>
<span className="material-symbols-outlined absolute right-3 top-1/2 -translate-y-1/2 text-on-surface-variant pointer-events-none">expand_more</span>
</div>
<button className="bg-error text-white px-6 py-2 rounded-full font-bold text-sm flex items-center gap-2 hover:bg-error/90 transition-colors">
<span className="material-symbols-outlined text-lg">lock</span> Khóa thi hàng loạt
                    </button>
</div>
</div>
{/*  Data Table  */}
<div className="overflow-x-auto">
<table className="w-full text-left border-collapse">
<thead>
<tr className="bg-surface-container-low/50">
<th className="px-6 py-4 label-md text-[11px] font-bold uppercase tracking-widest text-on-surface-variant">Mã SV</th>
<th className="px-6 py-4 label-md text-[11px] font-bold uppercase tracking-widest text-on-surface-variant">Họ tên</th>
<th className="px-6 py-4 label-md text-[11px] font-bold uppercase tracking-widest text-on-surface-variant">Lớp</th>
<th className="px-6 py-4 label-md text-[11px] font-bold uppercase tracking-widest text-on-surface-variant">Số dư ví</th>
<th className="px-6 py-4 label-md text-[11px] font-bold uppercase tracking-widest text-on-surface-variant">Tổng nợ</th>
<th className="px-6 py-4 label-md text-[11px] font-bold uppercase tracking-widest text-on-surface-variant">Trạng thái khóa</th>
<th className="px-6 py-4 label-md text-[11px] font-bold uppercase tracking-widest text-on-surface-variant text-right">Thao tác</th>
</tr>
</thead>
<tbody className="divide-y divide-surface-container">
{/*  Row 1  */}
<tr className="hover:bg-surface-container-low transition-colors group">
<td className="px-6 py-4 font-mono text-sm font-semibold text-primary">202400125</td>
<td className="px-6 py-4 font-semibold text-on-surface">Nguyễn Văn Anh</td>
<td className="px-6 py-4 text-sm text-on-surface-variant">CS-K47-A</td>
<td className="px-6 py-4 text-sm font-medium text-on-surface">1.250.000₫</td>
<td className="px-6 py-4 text-sm font-bold text-on-surface">0₫</td>
<td className="px-6 py-4">
<span className="px-3 py-1 bg-primary-fixed text-on-primary-fixed rounded-full text-[10px] font-bold uppercase tracking-wider">Mở khóa</span>
</td>
<td className="px-6 py-4 text-right">
<button className="p-2 text-on-surface-variant hover:text-primary transition-colors">
<span className="material-symbols-outlined">more_vert</span>
</button>
</td>
</tr>
{/*  Row 2  */}
<tr className="bg-surface-container-low/30 hover:bg-surface-container-low transition-colors group">
<td className="px-6 py-4 font-mono text-sm font-semibold text-primary">202400129</td>
<td className="px-6 py-4 font-semibold text-on-surface">Lê Thị Bích</td>
<td className="px-6 py-4 text-sm text-on-surface-variant">ECON-K48-B</td>
<td className="px-6 py-4 text-sm font-medium text-on-surface">50.000₫</td>
<td className="px-6 py-4 text-sm font-bold text-error">4.200.000₫</td>
<td className="px-6 py-4">
<span className="px-3 py-1 bg-error-container text-on-error-container rounded-full text-[10px] font-bold uppercase tracking-wider">Đã khóa</span>
</td>
<td className="px-6 py-4 text-right">
<button className="p-2 text-on-surface-variant hover:text-primary transition-colors">
<span className="material-symbols-outlined">more_vert</span>
</button>
</td>
</tr>
{/*  Row 3  */}
<tr className="hover:bg-surface-container-low transition-colors group">
<td className="px-6 py-4 font-mono text-sm font-semibold text-primary">202400142</td>
<td className="px-6 py-4 font-semibold text-on-surface">Trần Minh Quân</td>
<td className="px-6 py-4 text-sm text-on-surface-variant">CS-K47-A</td>
<td className="px-6 py-4 text-sm font-medium text-on-surface">300.000₫</td>
<td className="px-6 py-4 text-sm font-bold text-secondary">850.000₫</td>
<td className="px-6 py-4">
<span className="px-3 py-1 bg-primary-fixed text-on-primary-fixed rounded-full text-[10px] font-bold uppercase tracking-wider">Mở khóa</span>
</td>
<td className="px-6 py-4 text-right">
<button className="p-2 text-on-surface-variant hover:text-primary transition-colors">
<span className="material-symbols-outlined">more_vert</span>
</button>
</td>
</tr>
{/*  Row 4  */}
<tr className="bg-surface-container-low/30 hover:bg-surface-container-low transition-colors group">
<td className="px-6 py-4 font-mono text-sm font-semibold text-primary">202400155</td>
<td className="px-6 py-4 font-semibold text-on-surface">Phạm Hoàng Nam</td>
<td className="px-6 py-4 text-sm text-on-surface-variant">LAW-K46-C</td>
<td className="px-6 py-4 text-sm font-medium text-on-surface">0₫</td>
<td className="px-6 py-4 text-sm font-bold text-error">12.450.000₫</td>
<td className="px-6 py-4">
<span className="px-3 py-1 bg-error-container text-on-error-container rounded-full text-[10px] font-bold uppercase tracking-wider">Đã khóa</span>
</td>
<td className="px-6 py-4 text-right">
<button className="p-2 text-on-surface-variant hover:text-primary transition-colors">
<span className="material-symbols-outlined">more_vert</span>
</button>
</td>
</tr>
{/*  Row 5  */}
<tr className="hover:bg-surface-container-low transition-colors group">
<td className="px-6 py-4 font-mono text-sm font-semibold text-primary">202400168</td>
<td className="px-6 py-4 font-semibold text-on-surface">Vương Thu Thủy</td>
<td className="px-6 py-4 text-sm text-on-surface-variant">MED-K45-D</td>
<td className="px-6 py-4 text-sm font-medium text-on-surface">2.100.000₫</td>
<td className="px-6 py-4 text-sm font-bold text-on-surface">0₫</td>
<td className="px-6 py-4">
<span className="px-3 py-1 bg-primary-fixed text-on-primary-fixed rounded-full text-[10px] font-bold uppercase tracking-wider">Mở khóa</span>
</td>
<td className="px-6 py-4 text-right">
<button className="p-2 text-on-surface-variant hover:text-primary transition-colors">
<span className="material-symbols-outlined">more_vert</span>
</button>
</td>
</tr>
</tbody>
</table>
</div>
{/*  Pagination  */}
<div className="p-6 border-t border-surface-container flex items-center justify-between">
<span className="text-xs font-semibold text-on-surface-variant uppercase tracking-widest">Trang 1 của 62</span>
<div className="flex gap-2">
<button className="w-10 h-10 flex items-center justify-center rounded-full border border-outline-variant text-on-surface-variant hover:bg-surface-container transition-colors disabled:opacity-50" disabled="">
<span className="material-symbols-outlined">chevron_left</span>
</button>
<button className="w-10 h-10 flex items-center justify-center rounded-full bg-primary text-white font-bold shadow-sm">1</button>
<button className="w-10 h-10 flex items-center justify-center rounded-full hover:bg-surface-container transition-colors text-on-surface font-semibold">2</button>
<button className="w-10 h-10 flex items-center justify-center rounded-full hover:bg-surface-container transition-colors text-on-surface font-semibold">3</button>
<button className="w-10 h-10 flex items-center justify-center rounded-full border border-outline-variant text-on-surface-variant hover:bg-surface-container transition-colors">
<span className="material-symbols-outlined">chevron_right</span>
</button>
</div>
</div>
</div>
{/*  Asymmetric Footer Insight Card  */}
<div className="mt-12 grid grid-cols-1 lg:grid-cols-12 gap-8">
<div className="lg:col-span-4 bg-primary text-white p-10 rounded-xl relative overflow-hidden flex flex-col justify-between">
<div className="relative z-10">
<h4 className="text-2xl font-bold font-headline mb-4">Chỉ số Sức khỏe Tài chính</h4>
<p className="text-primary-fixed-dim text-sm leading-relaxed mb-8">Tỷ lệ thu hồi học phí hiện tại là 88,4%. Chúng tôi khuyên bạn nên kích hoạt lời nhắc hàng loạt cho tất cả các tài khoản có nợ trên 500.000₫ trước giai đoạn thi giữa kỳ.</p>
</div>
<div className="relative z-10">
<button className="bg-white text-primary px-6 py-3 rounded-full font-bold text-sm hover:bg-primary-fixed transition-colors">Phân tích Mô hình Rủi ro</button>
</div>
{/*  Decorative background elements  */}
<div className="absolute -bottom-10 -right-10 w-40 h-40 bg-primary-container rounded-full opacity-50 blur-3xl"></div>
<div className="absolute top-0 right-0 p-8 opacity-20">
<span className="material-symbols-outlined text-[120px]" style={{ /* FIXME: convert style string to object -> font-variation-settings: 'FILL' 1; */ }}>analytics</span>
</div>
</div>
<div className="lg:col-span-8 bg-surface-container-highest p-8 rounded-xl flex flex-col md:flex-row gap-8 items-center">
<div className="flex-1">
<h4 className="text-xl font-bold font-headline text-on-surface mb-2">Kiểm toán Giải ngân Thủ công</h4>
<p className="text-on-surface-variant text-sm mb-6">Lần bù trừ thủ công cuối cùng được thực hiện vào ngày 24 tháng 10 năm 2023 bởi Admin-ID: 4421. Đảm bảo tất cả các biên lai vật lý được quét và đính kèm vào hồ sơ sinh viên trong vòng 24 giờ.</p>
<div className="flex gap-4">
<div className="flex flex-col">
<span className="text-[10px] font-bold uppercase tracking-widest text-on-surface-variant">Đang chờ kiểm toán</span>
<span className="text-2xl font-black text-secondary">24 Trường hợp</span>
</div>
<div className="w-px h-10 bg-outline-variant mx-4 self-center"></div>
<div className="flex flex-col">
<span className="text-[10px] font-bold uppercase tracking-widest text-on-surface-variant">Đã xác minh hôm nay</span>
<span className="text-2xl font-black text-primary">112 Trường hợp</span>
</div>
</div>
</div>
<div className="w-full md:w-48 h-48 rounded-xl overflow-hidden shadow-lg">
<img alt="Tài liệu kinh doanh và biểu đồ tài chính" className="w-full h-full object-cover" src="https://lh3.googleusercontent.com/aida-public/AB6AXuDXUQO6AuoGo5zvOHFyUO39jWKpYTYfxGsjInlihNF_keM3Rr76Usd_RAJP3t5_BMET5JHR4vaJ35VxOFGLV6Pd3JWG2PlLNHr-dijXIAwWBli6DtuFs79SdG3XTrr8m6ybg_VnZ3DcoS2s1i9_xmWnj0VbMlTN8obqM08ksqNLIn5wX5cdgHnUVF1k5yghv97s3tlgsPB1kKKYup7bPWCOQAOyEyfG3qEhnHQCR7nPviW37Mh_cHtG6fONDnf35ZzghUMg-44tjae7"/>
</div>
</div>
</div>
</main>

    </>
  );
};

export default GimStTiChnhKTonAdmin;
