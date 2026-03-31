import React from 'react';

const VSinhVinStudentWallet = () => {
  return (
    <>
      
{/*  TopNavBar  */}

{/*  SideNavBar  */}

{/*  Main Content Canvas  */}
<main className="  px-6 pb-12 min-h-screen">
<div className="max-w-7xl mx-auto space-y-8">
{/*  Welcome Header Section  */}
<div className="flex flex-col md:flex-row md:items-end justify-between gap-6">
<div>
<h1 className="text-4xl font-extrabold text-on-surface tracking-tight mb-2">Ví Sinh Viên</h1>
<p className="text-on-surface-variant font-medium">Chào mừng trở lại, <span className="text-primary font-bold">Nguyễn Văn A</span>. Quản lý tài chính cá nhân và học phí của bạn.</p>
</div>
<div className="flex gap-3">
<button className="flex items-center gap-2 bg-surface-container-highest px-6 py-3 rounded-full font-bold text-primary hover:bg-primary-container hover:text-white transition-all">
<span className="material-symbols-outlined">receipt_long</span> Xuất báo cáo
                    </button>
<button className="flex items-center gap-2 bg-primary text-white px-8 py-3 rounded-full font-bold shadow-lg shadow-primary/30 hover:scale-105 transition-transform">
<span className="material-symbols-outlined">add</span> Nạp tiền ngay
                    </button>
</div>
</div>
{/*  Dashboard Bento Grid  */}
<div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
{/*  Balance & Debt Card  */}
<div className="lg:col-span-2 space-y-6">
<div className="grid grid-cols-1 md:grid-cols-2 gap-6">
{/*  Wallet Balance  */}
<div className="bg-surface-container-lowest p-8 rounded-full shadow-sm relative overflow-hidden group">
<div className="absolute -right-10 -top-10 w-40 h-40 bg-primary/5 rounded-full group-hover:scale-110 transition-transform duration-500"></div>
<div className="flex flex-col h-full justify-between relative z-10">
<div>
<div className="flex items-center gap-2 text-primary mb-4">
<span className="material-symbols-outlined">account_balance_wallet</span>
<span className="font-bold text-sm tracking-widest uppercase">Số dư ví hiện tại</span>
</div>
<div className="text-5xl font-extrabold text-on-surface flex items-baseline gap-1">
                                        5,000,000 <span className="text-lg font-bold text-on-surface-variant">₫</span>
</div>
</div>
<div className="mt-8 flex items-center gap-2 text-primary font-semibold text-sm">
<span className="material-symbols-outlined text-sm">trending_up</span>
<span>+1,200,000đ từ tháng trước</span>
</div>
</div>
</div>
{/*  Tuition Debt  */}
<div className="bg-surface-container-lowest p-8 rounded-full shadow-sm relative overflow-hidden group">
<div className="absolute -right-10 -top-10 w-40 h-40 bg-error/5 rounded-full group-hover:scale-110 transition-transform duration-500"></div>
<div className="flex flex-col h-full justify-between relative z-10">
<div>
<div className="flex items-center gap-2 text-error mb-4">
<span className="material-symbols-outlined">assignment_late</span>
<span className="font-bold text-sm tracking-widest uppercase">Tổng nợ học phí</span>
</div>
<div className="flex items-center gap-3">
<div className="text-4xl font-extrabold text-error">1,200,000 <span className="text-sm">₫</span></div>
<span className="bg-error-container text-on-error-container text-[10px] font-black px-2 py-0.5 rounded-full uppercase">Cần thanh toán</span>
</div>
</div>
<div className="mt-8">
<button className="text-sm font-bold text-primary hover:underline flex items-center gap-1">
                                        Chi tiết công nợ <span className="material-symbols-outlined text-sm">arrow_forward</span>
</button>
</div>
</div>
</div>
</div>
{/*  History Table Card  */}
<div className="bg-surface-container-lowest p-8 rounded-xl shadow-sm">
<div className="flex justify-between items-center mb-8">
<h3 className="text-xl font-extrabold text-blue-900 tracking-tight">Lịch sử giao dịch gần đây</h3>
<button className="text-primary font-bold text-sm hover:bg-primary-fixed-dim/20 px-4 py-2 rounded-full transition-colors">Xem tất cả</button>
</div>
<div className="overflow-x-auto">
<table className="w-full text-left border-collapse">
<thead>
<tr className="text-on-surface-variant">
<th className="py-4 px-2 font-bold text-[10px] tracking-[0.1em] uppercase border-b border-outline-variant/10">Mã giao dịch</th>
<th className="py-4 px-2 font-bold text-[10px] tracking-[0.1em] uppercase border-b border-outline-variant/10">Loại giao dịch</th>
<th className="py-4 px-2 font-bold text-[10px] tracking-[0.1em] uppercase border-b border-outline-variant/10">Số tiền</th>
<th className="py-4 px-2 font-bold text-[10px] tracking-[0.1em] uppercase border-b border-outline-variant/10">Ngày giờ</th>
<th className="py-4 px-2 font-bold text-[10px] tracking-[0.1em] uppercase border-b border-outline-variant/10 text-right">Trạng thái</th>
</tr>
</thead>
<tbody className="divide-y divide-outline-variant/10">
<tr className="hover:bg-surface-container-low/50 transition-colors group">
<td className="py-5 px-2 font-mono text-xs font-bold text-blue-800">#EP-TX98231</td>
<td className="py-5 px-2">
<div className="flex items-center gap-2">
<div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center text-primary">
<span className="material-symbols-outlined text-sm">add_card</span>
</div>
<span className="font-semibold text-sm">Nạp tiền vào ví</span>
</div>
</td>
<td className="py-5 px-2 font-bold text-sm text-primary">+2,000,000đ</td>
<td className="py-5 px-2 text-xs font-medium text-on-surface-variant italic">14:30 - 20/05/2024</td>
<td className="py-5 px-2 text-right">
<span className="inline-flex items-center gap-1 bg-primary-fixed text-on-primary-fixed text-[10px] font-bold px-2.5 py-1 rounded-full uppercase">
<span className="w-1 h-1 rounded-full bg-primary"></span> Thành công
                                            </span>
</td>
</tr>
<tr className="hover:bg-surface-container-low/50 transition-colors group">
<td className="py-5 px-2 font-mono text-xs font-bold text-blue-800">#EP-TX98210</td>
<td className="py-5 px-2">
<div className="flex items-center gap-2">
<div className="w-8 h-8 rounded-full bg-secondary/10 flex items-center justify-center text-secondary">
<span className="material-symbols-outlined text-sm">school</span>
</div>
<span className="font-semibold text-sm">Thanh toán HP</span>
</div>
</td>
<td className="py-5 px-2 font-bold text-sm text-error">-1,500,000đ</td>
<td className="py-5 px-2 text-xs font-medium text-on-surface-variant italic">09:15 - 18/05/2024</td>
<td className="py-5 px-2 text-right">
<span className="inline-flex items-center gap-1 bg-primary-fixed text-on-primary-fixed text-[10px] font-bold px-2.5 py-1 rounded-full uppercase">
<span className="w-1 h-1 rounded-full bg-primary"></span> Thành công
                                            </span>
</td>
</tr>
<tr className="hover:bg-surface-container-low/50 transition-colors group">
<td className="py-5 px-2 font-mono text-xs font-bold text-blue-800">#EP-TX98199</td>
<td className="py-5 px-2">
<div className="flex items-center gap-2">
<div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center text-primary">
<span className="material-symbols-outlined text-sm">add_card</span>
</div>
<span className="font-semibold text-sm">Nạp tiền vào ví</span>
</div>
</td>
<td className="py-5 px-2 font-bold text-sm text-primary">+1,000,000đ</td>
<td className="py-5 px-2 text-xs font-medium text-on-surface-variant italic">16:45 - 15/05/2024</td>
<td className="py-5 px-2 text-right">
<span className="inline-flex items-center gap-1 bg-primary-fixed text-on-primary-fixed text-[10px] font-bold px-2.5 py-1 rounded-full uppercase">
<span className="w-1 h-1 rounded-full bg-primary"></span> Thành công
                                            </span>
</td>
</tr>
</tbody>
</table>
</div>
</div>
</div>
{/*  Right Sidebar: QR & Payment Info  */}
<div className="space-y-6">
{/*  VietQR Section  */}
<div className="bg-surface-container p-1 rounded-xl shadow-inner">
<div className="bg-surface-container-lowest p-6 rounded-full flex flex-col items-center text-center">
<div className="mb-6">
<img alt="Mã QR VietQR" className="w-48 h-48 p-2 border-2 border-primary-fixed rounded-xl" data-alt="clean digital QR code with a university logo in the center and blue stylistic framing for bank payment" src="https://lh3.googleusercontent.com/aida-public/AB6AXuADH5e4Hootgd7IucjjuLiWWynkxxPbNWWZou-G0rrmVELzl50Fvl4iJH_CKUl6MO5FDApJ5KbxInQH8foV15Uqk-oy15AzBj8vN_QT3OLadkwxbO1C_1LqhOmK_Uibf_8CRl2HTHQ5DFtoSBrD8BfT9UG6biPw8HgJm93hs4lbJ33zyxmLl0wpEh7YlM0BExkykro52jGRIQcOuyoOBok852s3Ek-SBWtzdzDvilrOyMevPmCSXrXkAwQkOxQM7gGY-5EhGrWiTRb7"/>
</div>
<h4 className="text-lg font-extrabold text-primary mb-2">Quét mã nạp tiền</h4>
<p className="text-xs text-on-surface-variant font-medium px-4 mb-6 leading-relaxed">Sử dụng ứng dụng Ngân hàng (VietQR) hoặc ví điện tử để nạp tiền nhanh chóng vào tài khoản.</p>
<div className="w-full space-y-3 mb-4">
<div className="bg-surface-container-low rounded-xl p-3 flex justify-between items-center text-xs font-bold">
<span className="text-on-surface-variant">Ngân hàng:</span>
<span className="text-primary uppercase tracking-tight">BIDV - CN TP.HCM</span>
</div>
<div className="bg-surface-container-low rounded-xl p-3 flex justify-between items-center text-xs font-bold">
<span className="text-on-surface-variant">Số tài khoản:</span>
<span className="text-primary">1234 5678 9999</span>
</div>
<div className="bg-surface-container-low rounded-xl p-3 flex justify-between items-center text-xs font-bold">
<span className="text-on-surface-variant">Chủ TK:</span>
<span className="text-primary uppercase">EDUPORT - UNIVERSITY</span>
</div>
</div>
<button className="w-full bg-primary-container text-white py-3 rounded-full font-bold text-sm hover:opacity-90 transition-opacity">
                                Tải mã QR về máy
                            </button>
</div>
</div>
{/*  Payment Support Widget  */}
<div className="bg-secondary-container/10 p-6 rounded-xl relative overflow-hidden">
<div className="absolute -bottom-4 -right-4 opacity-10">
<span className="material-symbols-outlined text-[100px]" style={{ /* FIXME: convert style string to object -> font-variation-settings: 'FILL' 1; */ }}>help_center</span>
</div>
<h4 className="text-sm font-black text-secondary uppercase tracking-widest mb-3">Hỗ trợ thanh toán</h4>
<p className="text-xs text-on-surface font-medium leading-relaxed mb-4">Mọi thắc mắc về giao dịch hoặc công nợ học phí, vui lòng liên hệ Phòng Tài vụ tại Tòa nhà A1.</p>
<div className="flex items-center gap-2 text-xs font-bold text-primary">
<span className="material-symbols-outlined text-sm">call</span> 028.1234.5678
                        </div>
</div>
</div>
</div>
</div>
</main>
{/*  BottomNavBar (Mobile Only)  */}
<nav className="md:hidden fixed bottom-0 left-0 right-0 bg-slate-50/95 backdrop-blur-lg flex justify-around items-center py-4 px-2 shadow-2xl z-50">
<a className="flex flex-col items-center gap-1 text-slate-500" href="#">
<span className="material-symbols-outlined">dashboard</span>
<span className="text-[10px] font-bold">Home</span>
</a>
<a className="flex flex-col items-center gap-1 text-slate-500" href="#">
<span className="material-symbols-outlined">edit_calendar</span>
<span className="text-[10px] font-bold">Học phần</span>
</a>
<a className="flex flex-col items-center gap-1 text-blue-700" href="#">
<span className="material-symbols-outlined" style={{ /* FIXME: convert style string to object -> font-variation-settings: 'FILL' 1; */ }}>payments</span>
<span className="text-[10px] font-bold">Ví SV</span>
</a>
<a className="flex flex-col items-center gap-1 text-slate-500" href="#">
<span className="material-symbols-outlined">grade</span>
<span className="text-[10px] font-bold">Điểm</span>
</a>
<a className="flex flex-col items-center gap-1 text-slate-500" href="#">
<span className="material-symbols-outlined">person</span>
<span className="text-[10px] font-bold">Tôi</span>
</a>
</nav>

    </>
  );
};

export default VSinhVinStudentWallet;
