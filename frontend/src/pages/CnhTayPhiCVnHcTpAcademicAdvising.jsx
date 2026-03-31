import React from 'react';

const CnhTayPhiCVnHcTpAcademicAdvising = () => {
  return (
    <>
      
{/*  SideNavBar (Authority Source: JSON)  */}

{/*  Main Content Area  */}
<main className=" min-h-screen">
{/*  TopNavBar (Authority Source: JSON)  */}

{/*  Content Canvas  */}
<section className="p-8 space-y-12">
{/*  Hero / Header Section  */}
<div className="flex justify-between items-end">
<div className="max-w-2xl">
<span className="text-secondary font-bold tracking-widest text-xs uppercase mb-2 block">Quản lý cố vấn</span>
<h1 className="text-5xl font-black font-headline text-on-surface leading-tight tracking-tight">Danh sách lớp<br/><span className="text-primary">CNTT-K21-A</span></h1>
<p className="mt-4 text-on-surface-variant font-body leading-relaxed">
                        Theo dõi tiến độ học tập, cảnh báo học vụ và hỗ trợ sinh viên trong lộ trình đào tạo. Dữ liệu được cập nhật từ Học kỳ I - Năm học 2023-2024.
                    </p>
</div>
<div className="flex gap-3">
<button className="px-6 py-3 rounded-full bg-surface-container-high text-primary font-bold text-sm hover:bg-surface-container-highest transition-colors flex items-center gap-2">
<span className="material-symbols-outlined" data-icon="file_download">file_download</span> Xuất báo cáo
                    </button>
<button className="px-6 py-3 rounded-full bg-primary text-white font-bold text-sm shadow-lg hover:shadow-primary/20 transition-all flex items-center gap-2">
<span className="material-symbols-outlined" data-icon="mail">mail</span> Gửi mail hàng loạt
                    </button>
</div>
</div>
{/*  Stats Grid  */}
<div className="grid grid-cols-4 gap-6">
<div className="bg-surface-container-lowest p-6 rounded-xl shadow-sm border-l-4 border-primary">
<p className="text-sm font-medium text-on-surface-variant mb-1">Tổng số sinh viên</p>
<p className="text-3xl font-black text-on-surface">45</p>
</div>
<div className="bg-surface-container-lowest p-6 rounded-xl shadow-sm border-l-4 border-secondary">
<p className="text-sm font-medium text-on-surface-variant mb-1">GPA Trung bình lớp</p>
<p className="text-3xl font-black text-on-surface">3.12</p>
</div>
<div className="bg-surface-container-lowest p-6 rounded-xl shadow-sm border-l-4 border-error">
<p className="text-sm font-medium text-on-surface-variant mb-1">Cảnh báo học vụ</p>
<p className="text-3xl font-black text-error">03</p>
</div>
<div className="bg-surface-container-lowest p-6 rounded-xl shadow-sm border-l-4 border-primary-container">
<p className="text-sm font-medium text-on-surface-variant mb-1">Nợ môn (Tích lũy)</p>
<p className="text-3xl font-black text-on-surface">12</p>
</div>
</div>
{/*  Main Students Table Card  */}
<div className="bg-surface-container-lowest rounded-xl shadow-sm overflow-hidden">
<div className="px-8 py-6 flex justify-between items-center border-b border-surface-container">
<h3 className="text-lg font-bold text-on-surface font-headline">Thông tin chi tiết sinh viên</h3>
<div className="flex gap-4">
<select className="bg-surface-container-low border-none rounded-full text-xs font-bold px-4 py-2 focus:ring-primary/10">
<option>Tất cả trạng thái</option>
<option>Cảnh báo đỏ</option>
<option>Học lực giỏi</option>
</select>
</div>
</div>
<div className="overflow-x-auto">
<table className="w-full text-left border-collapse">
<thead>
<tr className="bg-surface-container-low/50">
<th className="px-8 py-4 text-[0.6875rem] font-bold text-on-surface-variant uppercase tracking-[0.05em]">Họ và Tên</th>
<th className="px-6 py-4 text-[0.6875rem] font-bold text-on-surface-variant uppercase tracking-[0.05em]">Mã Sinh Viên</th>
<th className="px-6 py-4 text-[0.6875rem] font-bold text-on-surface-variant uppercase tracking-[0.05em]">GPA Tích Lũy</th>
<th className="px-6 py-4 text-[0.6875rem] font-bold text-on-surface-variant uppercase tracking-[0.05em]">Số môn nợ</th>
<th className="px-6 py-4 text-[0.6875rem] font-bold text-on-surface-variant uppercase tracking-[0.05em]">Trạng thái</th>
<th className="px-8 py-4 text-[0.6875rem] font-bold text-on-surface-variant uppercase tracking-[0.05em] text-right">Thao tác</th>
</tr>
</thead>
<tbody className="divide-y-0">
{/*  Student Row 1: Danger State  */}
<tr className="hover:bg-surface-container-low transition-colors group">
<td className="px-8 py-4">
<div className="flex items-center gap-3">
<div className="w-10 h-10 rounded-full bg-slate-100 flex-shrink-0 overflow-hidden">
<img className="w-full h-full object-cover" data-alt="Portrait of a young Asian male university student with a bright smile in a campus outdoor setting" src="https://lh3.googleusercontent.com/aida-public/AB6AXuBfsYqoMiV1rtlVOPiSQ4y7jGsYidDQGEqjoyL0uOdd3sHEc7OPS28vfyMB5d6j6q22Gcx08MJIZW-4NXaxo2whgkp9y7joSuHzURsIT0JCUXdT0ZrQcGgLDG-y3QTnxqQhbjQz1SOLEI9jR8jHqPA0hvNJ_BwlR1Mjpa3aGjMi9_8XB3aQ00Ct8GLx_kigDSGi3TIAdIduiAmCIRdOIpM6t9JQukAaE4Tm0QdgoC4vzp0UI-9V-sjXrsQDP2ei5fWwFPslbbTldO26"/>
</div>
<div className="font-bold text-on-surface">Nguyễn Minh Hoàng</div>
</div>
</td>
<td className="px-6 py-4 text-on-surface-variant font-mono text-sm">21120456</td>
<td className="px-6 py-4">
<span className="font-bold text-error">1.82</span>
</td>
<td className="px-6 py-4">
<span className="px-3 py-1 rounded-full bg-error-container text-on-error-container text-xs font-bold">04 môn</span>
</td>
<td className="px-6 py-4">
<span className="px-3 py-1 rounded-full bg-error-container text-on-error-container text-[10px] font-bold uppercase tracking-wider flex items-center w-fit gap-1">
<span className="material-symbols-outlined text-[12px]" data-icon="warning">warning</span>
                                        Cảnh báo mức 2
                                    </span>
</td>
<td className="px-8 py-4 text-right">
<button className="px-4 py-2 rounded-full bg-primary text-white text-xs font-bold hover:scale-95 transition-transform">Xem hồ sơ chi tiết</button>
</td>
</tr>
{/*  Student Row 2: Good State  */}
<tr className="bg-surface-container-low/30 hover:bg-surface-container-low transition-colors group">
<td className="px-8 py-4">
<div className="flex items-center gap-3">
<div className="w-10 h-10 rounded-full bg-slate-100 flex-shrink-0 overflow-hidden">
<img className="w-full h-full object-cover" data-alt="Close-up of a young woman with natural look in a bright library workspace with bokeh background" src="https://lh3.googleusercontent.com/aida-public/AB6AXuALmPg5RF7z_jkVakK_gEJRl98ZxV132HPTCVo3-ShjI7KAd1g5cKQiIMyBWJWWsK6Z8OfYoKVsHk52k0bSoo-kcq7MEDebuwKZLLyXBinP_VkSOFDR7okvd_HIJpgLsuqmyxyin9NLKOVzktLS9t8D6r-IUOF9uWX8Aa1k8TuqZMhYi0zaMN8hxVwcIH5nKghbRTLez_xb0QF8cTBnLHijvwiQFqY5_6WtoMEXe-zoOtfP25bRavMu89_2oEQFjiGpbNTopF8pwiCa"/>
</div>
<div className="font-bold text-on-surface">Trần Thị Thanh Vân</div>
</div>
</td>
<td className="px-6 py-4 text-on-surface-variant font-mono text-sm">21120512</td>
<td className="px-6 py-4">
<span className="font-bold text-primary">3.85</span>
</td>
<td className="px-6 py-4">
<span className="text-on-surface-variant text-xs">00 môn</span>
</td>
<td className="px-6 py-4">
<span className="px-3 py-1 rounded-full bg-primary-fixed text-on-primary-fixed text-[10px] font-bold uppercase tracking-wider w-fit">Ổn định</span>
</td>
<td className="px-8 py-4 text-right">
<button className="px-4 py-2 rounded-full bg-primary text-white text-xs font-bold hover:scale-95 transition-transform">Xem hồ sơ chi tiết</button>
</td>
</tr>
{/*  Student Row 3  */}
<tr className="hover:bg-surface-container-low transition-colors group">
<td className="px-8 py-4">
<div className="flex items-center gap-3">
<div className="w-10 h-10 rounded-full bg-slate-100 flex-shrink-0 overflow-hidden">
<img className="w-full h-full object-cover" data-alt="Portrait of a determined young man wearing a grey hoodie in an urban setting during overcast daylight" src="https://lh3.googleusercontent.com/aida-public/AB6AXuBPwXjrmHk_D0iR4PZZ7JAvlaXGtmbdz-pCeaxleAsE9ZITE0QQNvHfYLmYkU_Far2u4sFQJskt-_a4dWFOBJmGpZPYOtLLsbYPzDZggXU4k0OpGMMzMfvxbwOsvTputOD2P4W3gj7INDIBnNvoW57zo63PKaV6hVsxi_OMflU_Y0nc2WiefsPKtIEHCNWb57-8chJPFVh1WBRtKqmkTzXttKeWKPu8VfT2PiaipjRSGwXwuMntUozP-u7WAZKYgL7VyMlW7bmhc2Vh"/>
</div>
<div className="font-bold text-on-surface">Lê Quốc Bảo</div>
</div>
</td>
<td className="px-6 py-4 text-on-surface-variant font-mono text-sm">21120008</td>
<td className="px-6 py-4">
<span className="font-bold text-on-surface">2.45</span>
</td>
<td className="px-6 py-4">
<span className="px-3 py-1 rounded-full bg-secondary-fixed text-on-secondary-fixed text-xs font-bold">01 môn</span>
</td>
<td className="px-6 py-4">
<span className="px-3 py-1 rounded-full bg-primary-fixed text-on-primary-fixed text-[10px] font-bold uppercase tracking-wider w-fit">Ổn định</span>
</td>
<td className="px-8 py-4 text-right">
<button className="px-4 py-2 rounded-full bg-primary text-white text-xs font-bold hover:scale-95 transition-transform">Xem hồ sơ chi tiết</button>
</td>
</tr>
{/*  Student Row 4: Warning  */}
<tr className="bg-surface-container-low/30 hover:bg-surface-container-low transition-colors group">
<td className="px-8 py-4">
<div className="flex items-center gap-3">
<div className="w-10 h-10 rounded-full bg-slate-100 flex-shrink-0 overflow-hidden">
<img className="w-full h-full object-cover" data-alt="Young female student laughing while looking to the side in a modern architecture background with soft light" src="https://lh3.googleusercontent.com/aida-public/AB6AXuCxrMp_EEBWNqn4HbsEjMN9IQrfa7XOzPIl7iY4smQMsxytgZTaKg29KPXQjJ_XmSsG_jKU8Mqijs-nTbexjetI2VH-dNOl_Ttf6cHulogyMGTrWXvTqOFAXPEuN4ni5TEqYyfLI0rbPjFepQyl5DjaYPcufUuhwwMNmgSXUWuilBTNKeX3ljdXg-cvGKqXM3TvpYUVMGEK5RH2a0P2Z789yDteYbBjrkC3QKAh7fONsXS1ApqLADqZ73NSXpwHo4Y2RVKVA-Zv22Yj"/>
</div>
<div className="font-bold text-on-surface">Phạm Ngọc Anh</div>
</div>
</td>
<td className="px-6 py-4 text-on-surface-variant font-mono text-sm">21120223</td>
<td className="px-6 py-4">
<span className="font-bold text-error">2.10</span>
</td>
<td className="px-6 py-4">
<span className="px-3 py-1 rounded-full bg-error-container text-on-error-container text-xs font-bold">03 môn</span>
</td>
<td className="px-6 py-4">
<span className="px-3 py-1 rounded-full bg-error-container text-on-error-container text-[10px] font-bold uppercase tracking-wider flex items-center w-fit gap-1">
<span className="material-symbols-outlined text-[12px]" data-icon="report">report</span>
                                        Cảnh báo mức 1
                                    </span>
</td>
<td className="px-8 py-4 text-right">
<button className="px-4 py-2 rounded-full bg-primary text-white text-xs font-bold hover:scale-95 transition-transform">Xem hồ sơ chi tiết</button>
</td>
</tr>
</tbody>
</table>
</div>
{/*  Pagination / Footer of Table  */}
<div className="px-8 py-6 bg-surface-container-low/30 flex justify-between items-center">
<p className="text-xs text-on-surface-variant">Hiển thị 1-4 trong tổng số 45 sinh viên</p>
<div className="flex gap-2">
<button className="w-8 h-8 rounded-full bg-white shadow-sm flex items-center justify-center text-on-surface hover:bg-primary hover:text-white transition-colors">
<span className="material-symbols-outlined text-sm" data-icon="chevron_left">chevron_left</span>
</button>
<button className="w-8 h-8 rounded-full bg-primary text-white shadow-sm flex items-center justify-center font-bold text-xs">1</button>
<button className="w-8 h-8 rounded-full bg-white shadow-sm flex items-center justify-center text-on-surface hover:bg-primary hover:text-white transition-colors font-bold text-xs">2</button>
<button className="w-8 h-8 rounded-full bg-white shadow-sm flex items-center justify-center text-on-surface hover:bg-primary hover:text-white transition-colors font-bold text-xs">3</button>
<button className="w-8 h-8 rounded-full bg-white shadow-sm flex items-center justify-center text-on-surface hover:bg-primary hover:text-white transition-colors">
<span className="material-symbols-outlined text-sm" data-icon="chevron_right">chevron_right</span>
</button>
</div>
</div>
</div>
{/*  Bento Layout for Extra Contextual Info  */}
<div className="grid grid-cols-12 gap-6 pb-12">
{/*  Upcoming Advising Sessions  */}
<div className="col-span-8 bg-surface-container rounded-xl p-8">
<div className="flex justify-between items-center mb-6">
<h4 className="font-headline font-bold text-xl">Lịch hẹn tư vấn sắp tới</h4>
<button className="text-primary font-bold text-sm">Xem tất cả</button>
</div>
<div className="space-y-4">
<div className="bg-surface-container-lowest p-4 rounded-xl flex justify-between items-center shadow-sm">
<div className="flex items-center gap-4">
<div className="w-12 h-12 bg-primary-fixed rounded-xl flex flex-col items-center justify-center text-primary leading-tight">
<span className="text-xs font-bold">Th2</span>
<span className="text-lg font-black">15</span>
</div>
<div>
<p className="font-bold text-on-surface">Tư vấn chọn chuyên ngành</p>
<p className="text-xs text-on-surface-variant">Sinh viên: Nguyễn Minh Hoàng • 09:00 AM</p>
</div>
</div>
<span className="material-symbols-outlined text-on-surface-variant" data-icon="more_vert">more_vert</span>
</div>
<div className="bg-surface-container-lowest p-4 rounded-xl flex justify-between items-center shadow-sm">
<div className="flex items-center gap-4">
<div className="w-12 h-12 bg-secondary-fixed rounded-xl flex flex-col items-center justify-center text-secondary leading-tight">
<span className="text-xs font-bold">Th4</span>
<span className="text-lg font-black">17</span>
</div>
<div>
<p className="font-bold text-on-surface">Giải quyết nợ môn và lộ trình học bù</p>
<p className="text-xs text-on-surface-variant">Sinh viên: Phạm Ngọc Anh • 14:30 PM</p>
</div>
</div>
<span className="material-symbols-outlined text-on-surface-variant" data-icon="more_vert">more_vert</span>
</div>
</div>
</div>
{/*  Academic Trends Chart Placeholder (Glassmorphism)  */}
<div className="col-span-4 bg-primary rounded-xl p-8 text-white relative overflow-hidden">
<div className="relative z-10">
<h4 className="font-headline font-bold text-xl mb-2">Hiệu suất học tập</h4>
<p className="text-primary-fixed text-sm mb-6">Biểu đồ trung bình GPA của lớp qua 3 học kỳ gần nhất.</p>
<div className="flex items-end gap-2 h-32 mb-4">
<div className="w-full bg-white/20 rounded-t-lg transition-all hover:bg-white/40" style={{ /* FIXME: convert style string to object -> height: 60% */ }}></div>
<div className="w-full bg-white/20 rounded-t-lg transition-all hover:bg-white/40" style={{ /* FIXME: convert style string to object -> height: 75% */ }}></div>
<div className="w-full bg-white rounded-t-lg shadow-lg" style={{ /* FIXME: convert style string to object -> height: 90% */ }}></div>
</div>
<div className="flex justify-between text-[10px] font-bold text-primary-fixed uppercase tracking-wider">
<span>HK1-22</span>
<span>HK2-22</span>
<span>HK1-23</span>
</div>
</div>
{/*  Background Decoration  */}
<div className="absolute -bottom-10 -right-10 w-40 h-40 bg-white/10 rounded-full blur-3xl"></div>
</div>
</div>
</section>
</main>

    </>
  );
};

export default CnhTayPhiCVnHcTpAcademicAdvising;
