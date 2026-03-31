import React from 'react';

const TraCuHSCNhnThTcOnline = () => {
  return (
    <>
      
{/*  SideNavBar  */}

{/*  Main Content  */}
<main className=" min-h-screen">
{/*  TopAppBar  */}

<div className="p-10 space-y-10">
{/*  Page Header  */}
<section className="flex flex-col md:flex-row justify-between items-start md:items-end gap-6">
<div className="space-y-2">
<h2 className="text-4xl font-headline font-extrabold text-on-surface tracking-tight">Hồ Sơ Cá Nhân</h2>
<p className="text-on-surface-variant font-medium max-w-lg">Quản lý thông tin học thuật và hồ sơ định danh chính thức của bạn tại EduPort.</p>
</div>
<div className="flex gap-3">
<button className="px-6 py-2.5 rounded-full bg-white text-primary border border-outline-variant/50 font-semibold text-sm hover:bg-surface-container-low transition-all shadow-sm">
                        Xuất file PDF
                    </button>
<button className="px-6 py-2.5 rounded-full bg-gradient-to-br from-primary to-primary-container text-white font-semibold text-sm hover:shadow-lg transition-all flex items-center gap-2">
<span className="material-symbols-outlined text-lg">edit</span>
                        Cập nhật hồ sơ
                    </button>
</div>
</section>
{/*  Bento Grid Layout  */}
<div className="grid grid-cols-12 gap-6">
{/*  General Info Card (Large)  */}
<div className="col-span-12 lg:col-span-8 rounded-xl bg-surface-container-lowest p-8 shadow-sm transition-all hover:shadow-md border border-outline-variant/10">
<div className="flex flex-col md:flex-row gap-10">
<div className="relative flex-shrink-0">
<div className="w-40 h-52 rounded-2xl overflow-hidden bg-slate-100 border-4 border-white shadow-xl">
<img className="w-full h-full object-cover" data-alt="professional studio portrait of a young man for academic identification, neutral gray background, clean lighting" src="https://lh3.googleusercontent.com/aida-public/AB6AXuBsyV0c1jB2RlR7e8UFt4sihzObZnZNJR23cnh9wovZl5VjOGhD0TOmegVcoc7R9u7qqvnA2sLO3_h3-GY70g0rYhDOOoHUigy9TM8U1iQsnCK7Wvkt2RHnEQfx3EnZ9iybm4RxP3KlxtkVJmlEZNwuileZA6Jhaz_A89ISk8grp__QVUfl4RLtr69cTkUYSxxApOmSwI7oXmS68Dz-5_aKpJ5iObteY9kEmN-3-nINLaoWENIQjwYrWKqjGQPp1-ssYofz77d7cZ85"/>
</div>
<div className="absolute -bottom-3 -right-3 bg-secondary-container text-on-secondary-container px-3 py-1 rounded-full text-[10px] font-bold uppercase tracking-widest shadow-lg">
                                Đang học
                            </div>
</div>
<div className="flex-grow space-y-6">
<div>
<h3 className="text-2xl font-headline font-bold text-on-surface">Nguyễn Văn A</h3>
<p className="text-primary font-semibold tracking-wide">MSSV: 21110123</p>
</div>
<div className="grid grid-cols-1 md:grid-cols-2 gap-y-6 gap-x-12">
<div className="space-y-1">
<p className="text-[10px] font-bold text-on-surface-variant uppercase tracking-widest">Lớp sinh hoạt</p>
<p className="text-sm font-medium text-on-surface">21CNTT1A</p>
</div>
<div className="space-y-1">
<p className="text-[10px] font-bold text-on-surface-variant uppercase tracking-widest">Khoa / Viện</p>
<p className="text-sm font-medium text-on-surface">Công nghệ thông tin</p>
</div>
<div className="space-y-1">
<p className="text-[10px] font-bold text-on-surface-variant uppercase tracking-widest">Chuyên ngành</p>
<p className="text-sm font-medium text-on-surface">Kỹ thuật phần mềm</p>
</div>
<div className="space-y-1">
<p className="text-[10px] font-bold text-on-surface-variant uppercase tracking-widest">Niên khóa</p>
<p className="text-sm font-medium text-on-surface">2021 - 2025</p>
</div>
</div>
</div>
</div>
</div>
{/*  Advisor Info (Small)  */}
<div className="col-span-12 lg:col-span-4 rounded-xl bg-primary-container p-8 text-white relative overflow-hidden shadow-sm">
<div className="relative z-10 space-y-6">
<div className="flex items-center gap-3">
<div className="p-2 bg-white/20 rounded-lg backdrop-blur-md">
<span className="material-symbols-outlined">supervisor_account</span>
</div>
<h4 className="font-headline font-bold text-lg">Cố vấn học tập</h4>
</div>
<div className="space-y-4">
<div>
<p className="text-blue-200 text-xs font-medium uppercase tracking-wider mb-1">Giảng viên hướng dẫn</p>
<p className="text-xl font-bold">ThS. Lê Thị B</p>
</div>
<div className="flex items-center gap-2">
<span className="material-symbols-outlined text-blue-200 text-sm">call</span>
<p className="text-sm font-medium text-blue-50">090x xxx xxx</p>
</div>
<button className="w-full py-2 bg-white/10 hover:bg-white/20 transition-colors rounded-lg text-xs font-bold uppercase tracking-widest">
                                Liên hệ cố vấn
                            </button>
</div>
</div>
{/*  Decorative background element  */}
<div className="absolute -right-10 -bottom-10 w-40 h-40 bg-white/10 rounded-full blur-3xl"></div>
</div>
{/*  Contact Info (Editable)  */}
<div className="col-span-12 md:col-span-6 lg:col-span-5 rounded-xl bg-surface-container-low p-8 border border-outline-variant/10">
<div className="flex justify-between items-center mb-8">
<h4 className="font-headline font-bold text-xl flex items-center gap-2">
<span className="material-symbols-outlined text-primary">contact_mail</span>
                            Liên lạc
                        </h4>
<button className="p-2 rounded-full hover:bg-primary/5 text-primary transition-colors">
<span className="material-symbols-outlined">edit_square</span>
</button>
</div>
<div className="space-y-6">
<div className="flex gap-4">
<div className="w-10 h-10 rounded-full bg-white flex items-center justify-center text-primary-container flex-shrink-0 shadow-sm">
<span className="material-symbols-outlined text-lg">mail</span>
</div>
<div>
<p className="text-[10px] font-bold text-on-surface-variant uppercase tracking-widest mb-1">Email sinh viên</p>
<p className="text-sm font-medium text-on-surface">21110123@student.eduport.edu.vn</p>
</div>
</div>
<div className="flex gap-4">
<div className="w-10 h-10 rounded-full bg-white flex items-center justify-center text-primary-container flex-shrink-0 shadow-sm">
<span className="material-symbols-outlined text-lg">smartphone</span>
</div>
<div>
<p className="text-[10px] font-bold text-on-surface-variant uppercase tracking-widest mb-1">Số điện thoại</p>
<p className="text-sm font-medium text-on-surface">038x xxx xxx</p>
</div>
</div>
<div className="flex gap-4">
<div className="w-10 h-10 rounded-full bg-white flex items-center justify-center text-primary-container flex-shrink-0 shadow-sm">
<span className="material-symbols-outlined text-lg">location_on</span>
</div>
<div>
<p className="text-[10px] font-bold text-on-surface-variant uppercase tracking-widest mb-1">Địa chỉ thường trú</p>
<p className="text-sm font-medium text-on-surface leading-relaxed">123 Đường ABC, Phường 1, Quận 1, TP. Hồ Chí Minh</p>
</div>
</div>
</div>
</div>
{/*  Personal Details (Table-like grid)  */}
<div className="col-span-12 md:col-span-6 lg:col-span-7 rounded-xl bg-surface-container-lowest p-8 border border-outline-variant/10 shadow-sm">
<h4 className="font-headline font-bold text-xl flex items-center gap-2 mb-8">
<span className="material-symbols-outlined text-primary">fingerprint</span>
                        Chi tiết cá nhân
                    </h4>
<div className="grid grid-cols-2 gap-x-8 gap-y-10">
<div className="space-y-1">
<p className="text-[10px] font-bold text-on-surface-variant uppercase tracking-widest">Ngày sinh</p>
<p className="text-sm font-semibold text-on-surface pb-2 border-b border-outline-variant/20">15 / 05 / 2003</p>
</div>
<div className="space-y-1">
<p className="text-[10px] font-bold text-on-surface-variant uppercase tracking-widest">Giới tính</p>
<p className="text-sm font-semibold text-on-surface pb-2 border-b border-outline-variant/20">Nam</p>
</div>
<div className="space-y-1">
<p className="text-[10px] font-bold text-on-surface-variant uppercase tracking-widest">Số CMND / CCCD</p>
<p className="text-sm font-semibold text-on-surface pb-2 border-b border-outline-variant/20">079xxxxxxxxx</p>
</div>
<div className="space-y-1">
<p className="text-[10px] font-bold text-on-surface-variant uppercase tracking-widest">Dân tộc</p>
<p className="text-sm font-semibold text-on-surface pb-2 border-b border-outline-variant/20">Kinh</p>
</div>
<div className="col-span-2 space-y-1 bg-surface-container-low/50 p-4 rounded-xl">
<p className="text-[10px] font-bold text-on-surface-variant uppercase tracking-widest">Tài khoản ngân hàng liên kết</p>
<div className="flex justify-between items-center mt-2">
<div className="flex items-center gap-3">
<div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center">
<span className="material-symbols-outlined text-primary text-sm">account_balance</span>
</div>
<p className="text-sm font-semibold text-on-surface">VCB - 1023xxxx99</p>
</div>
<span className="px-3 py-1 bg-primary-fixed text-on-primary-fixed text-[10px] font-bold rounded-full uppercase tracking-tighter">Đã xác thực</span>
</div>
</div>
</div>
</div>
{/*  Academic Progress (Bonus Section for context)  */}
<div className="col-span-12 rounded-xl glass-card border border-outline-variant/20 p-8 flex flex-col md:flex-row gap-8 items-center justify-between">
<div className="space-y-2">
<p className="text-xs font-bold text-primary uppercase tracking-widest">Tiến độ học tập</p>
<h5 className="text-xl font-headline font-bold">Bạn đã hoàn thành 75% chương trình đào tạo</h5>
<div className="w-full md:w-96 h-2 bg-surface-container-high rounded-full overflow-hidden mt-4">
<div className="w-3/4 h-full bg-gradient-to-r from-primary to-secondary-container"></div>
</div>
</div>
<div className="flex gap-4">
<div className="text-center px-6">
<p className="text-2xl font-black text-on-surface">3.82</p>
<p className="text-[10px] font-bold text-on-surface-variant uppercase tracking-widest">GPA Hiện tại</p>
</div>
<div className="w-px h-12 bg-outline-variant/30 hidden md:block"></div>
<div className="text-center px-6">
<p className="text-2xl font-black text-on-surface">112</p>
<p className="text-[10px] font-bold text-on-surface-variant uppercase tracking-widest">Tín chỉ tích lũy</p>
</div>
</div>
</div>
</div>
{/*  Footer Area  */}
<footer className="pt-12 pb-6 border-t border-outline-variant/10 text-center">
<p className="text-xs text-on-surface-variant/60 font-medium">© 2024 EduPort University Management System. Thông tin được bảo mật theo tiêu chuẩn ISO 27001.</p>
</footer>
</div>
</main>
{/*  Floating Action Button for Support  */}
<div className="fixed bottom-8 right-8 z-50">
<button className="w-14 h-14 rounded-full bg-secondary shadow-2xl flex items-center justify-center text-white hover:scale-105 active:scale-95 transition-all">
<span className="material-symbols-outlined" style={{ /* FIXME: convert style string to object -> font-variation-settings: 'FILL' 1; */ }}>support_agent</span>
</button>
</div>

    </>
  );
};

export default TraCuHSCNhnThTcOnline;
