import React from 'react';

const XcThcaYuTMfa2FaVChKS = () => {
  return (
    <>
      
{/*  TopNavBar  */}

<div className="flex">
{/*  SideNavBar  */}

{/*  Main Content  */}
<main className="flex-1  p-8 min-h-screen">

<div className="grid grid-cols-1 xl:grid-cols-12 gap-8">
{/*  MFA Section (Bento Grid Style)  */}
<section className="xl:col-span-7 space-y-8">
<div className="bg-surface-container-lowest p-8 rounded-xl shadow-sm">
<div className="flex items-center justify-between mb-8">
<h2 className="text-2xl font-bold flex items-center gap-3">
<span className="material-symbols-outlined text-primary" data-icon="vibration">vibration</span>
                                Xác thực Đa Yếu Tố (MFA)
                            </h2>
<span className="px-4 py-1 bg-primary-fixed text-on-primary-fixed rounded-full text-xs font-bold uppercase tracking-wider">Đang hoạt động</span>
</div>
<div className="space-y-6">
{/*  Google Authenticator  */}
<div className="flex items-center justify-between p-6 bg-surface-container-low rounded-xl group hover:bg-surface-container transition-colors">
<div className="flex items-center gap-6">
<div className="w-14 h-14 bg-white rounded-xl shadow-sm flex items-center justify-center">
<span className="material-symbols-outlined text-3xl text-primary" data-icon="qr_code_2">qr_code_2</span>
</div>
<div>
<h3 className="text-lg font-bold text-on-surface">Google Authenticator</h3>
<p className="text-sm text-on-surface-variant">Sử dụng mã OTP từ ứng dụng di động để đăng nhập.</p>
</div>
</div>
<div className="flex items-center gap-4">
<span className="text-sm font-semibold text-primary">Đã thiết lập</span>
<button className="px-6 py-2 bg-surface-container-highest rounded-full text-sm font-bold hover:bg-primary-container hover:text-white transition-all">Cấu hình</button>
</div>
</div>
{/*  SMS OTP  */}
<div className="flex items-center justify-between p-6 bg-surface-container-low rounded-xl group hover:bg-surface-container transition-colors">
<div className="flex items-center gap-6">
<div className="w-14 h-14 bg-white rounded-xl shadow-sm flex items-center justify-center">
<span className="material-symbols-outlined text-3xl text-secondary" data-icon="sms">sms</span>
</div>
<div>
<h3 className="text-lg font-bold text-on-surface">SMS OTP</h3>
<p className="text-sm text-on-surface-variant">Nhận mã qua số điện thoại: +84 ••• ••• 888</p>
</div>
</div>
<div className="flex items-center gap-4">
<span className="text-sm font-semibold text-secondary">Ưu tiên</span>
<button className="px-6 py-2 bg-surface-container-highest rounded-full text-sm font-bold hover:bg-secondary-container hover:text-on-secondary-container transition-all">Thay đổi</button>
</div>
</div>
{/*  Email OTP  */}
<div className="flex items-center justify-between p-6 bg-surface-container-low rounded-xl group hover:bg-surface-container transition-colors">
<div className="flex items-center gap-6">
<div className="w-14 h-14 bg-white rounded-xl shadow-sm flex items-center justify-center">
<span className="material-symbols-outlined text-3xl text-tertiary" data-icon="mail">mail</span>
</div>
<div>
<h3 className="text-lg font-bold text-on-surface">Email OTP</h3>
<p className="text-sm text-on-surface-variant">Nhận mã xác thực qua email công vụ.</p>
</div>
</div>
<div className="flex items-center gap-4">
<button className="px-6 py-2 bg-primary text-white rounded-full text-sm font-bold shadow-md hover:scale-105 transition-transform">Kích hoạt</button>
</div>
</div>
</div>
</div>
{/*  Digital Certificate Status (Asymmetric Layout)  */}
<div className="bg-surface-container-lowest overflow-hidden rounded-xl shadow-sm relative">
<div className="absolute top-0 right-0 w-32 h-32 bg-primary/5 rounded-full -mr-16 - blur-3xl"></div>
<div className="p-8 relative z-10">
<h2 className="text-2xl font-bold flex items-center gap-3 mb-8">
<span className="material-symbols-outlined text-primary" data-icon="verified_user">verified_user</span>
                                Trạng thái Chữ ký số
                            </h2>
<div className="flex flex-col md:flex-row gap-10">
<div className="flex-shrink-0 flex flex-col items-center justify-center p-8 bg-surface-container-high rounded-2xl w-full md:w-56">
<div className="w-24 h-24 rounded-full border-4 border-primary-container flex items-center justify-center mb-4">
<span className="material-symbols-outlined text-4xl text-primary" data-icon="draw" style={{ /* FIXME: convert style string to object -> font-variation-settings: 'FILL' 1; */ }}>draw</span>
</div>
<span className="text-xs font-bold text-primary uppercase tracking-widest mb-1">Hiệu lực</span>
<p className="font-bold text-lg">Hợp lệ</p>
</div>
<div className="flex-grow space-y-4">
<div className="grid grid-cols-2 gap-6">
<div>
<label className="text-[0.6875rem] font-bold uppercase tracking-wider text-on-surface-variant">Chủ sở hữu</label>
<p className="font-bold text-on-surface">TS. Nguyễn Văn A</p>
</div>
<div>
<label className="text-[0.6875rem] font-bold uppercase tracking-wider text-on-surface-variant">Cơ quan cấp</label>
<p className="font-bold text-on-surface">VNPT-CA Authority</p>
</div>
<div>
<label className="text-[0.6875rem] font-bold uppercase tracking-wider text-on-surface-variant">Ngày hiệu lực</label>
<p className="text-on-surface">15/05/2023</p>
</div>
<div>
<label className="text-[0.6875rem] font-bold uppercase tracking-wider text-on-surface-variant">Ngày hết hạn</label>
<p className="text-on-surface text-error font-bold">15/05/2025</p>
</div>
</div>
<div className="pt-6 border-t border-outline-variant/30 flex gap-4">
<button className="text-sm font-bold text-primary flex items-center gap-2 hover:underline">
<span className="material-symbols-outlined text-sm">download</span> Tải chứng chỉ
                                        </button>
<button className="text-sm font-bold text-primary flex items-center gap-2 hover:underline">
<span className="material-symbols-outlined text-sm">refresh</span> Gia hạn ngay
                                        </button>
</div>
</div>
</div>
</div>
</div>
</section>
{/*  Signing History (Glassmorphism Sidebar Style)  */}

</div>
</main>
</div>
{/*  Mobile Bottom NavBar  */}
<nav className="md:hidden fixed bottom-0 left-0 right-0 bg-white/95 backdrop-blur-lg border-t-0 shadow-[0_-4px_20px_rgba(0,0,0,0.05)] px-6 py-3 flex justify-between items-center z-50">
<button className="flex flex-col items-center gap-1 text-slate-500">
<span className="material-symbols-outlined">dashboard</span>
<span className="text-[10px] font-medium">Dashboard</span>
</button>
<button className="flex flex-col items-center gap-1 text-primary">
<span className="material-symbols-outlined" style={{ /* FIXME: convert style string to object -> font-variation-settings: 'FILL' 1; */ }}>security</span>
<span className="text-[10px] font-bold">Bảo mật</span>
</button>
<button className="w-12 h-12 -mt-10 bg-primary rounded-full shadow-xl flex items-center justify-center text-white border-4 border-white">
<span className="material-symbols-outlined">add</span>
</button>
<button className="flex flex-col items-center gap-1 text-slate-500">
<span className="material-symbols-outlined">description</span>
<span className="text-[10px] font-medium">Ký số</span>
</button>
<button className="flex flex-col items-center gap-1 text-slate-500">
<span className="material-symbols-outlined">settings</span>
<span className="text-[10px] font-medium">Cài đặt</span>
</button>
</nav>

    </>
  );
};

export default XcThcaYuTMfa2FaVChKS;
