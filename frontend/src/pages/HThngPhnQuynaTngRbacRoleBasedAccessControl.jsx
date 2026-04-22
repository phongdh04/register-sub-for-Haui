import React from 'react';

const HThngPhnQuynaTngRbacRoleBasedAccessControl = () => {
  return (
    <>
      
{/*  SideNavBar (Authority: JSON & Design System)  */}

{/*  TopNavBar  */}

{/*  Main Content  */}
<main className=" p-12 bg-surface min-h-screen">
<div className="max-w-7xl mx-auto space-y-12">
{/*  Header & Stats Section  */}
<div className="flex flex-col md:flex-row justify-between items-end gap-8">
<div className="space-y-2">
<span className="text-primary font-bold tracking-widest text-xs uppercase">Security Protocol</span>
<h2 className="text-5xl font-extrabold tracking-tighter text-on-surface">Phân Quyền Đa Tầng</h2>
<p className="text-on-surface-variant max-w-lg font-body">Quản lý cấu trúc vai trò và ma trận quyền hạn hệ thống EduPort. Đảm bảo tính bảo mật và minh bạch trong mọi tác vụ.</p>
</div>
<div className="flex gap-4">
<div className="bg-surface-container-low p-6 rounded-xl space-y-1 min-w-[140px]">
<p className="text-xs font-bold text-primary tracking-wider uppercase">Vai trò</p>
<p className="text-3xl font-black text-on-surface">08</p>
</div>
<div className="bg-primary-container p-6 rounded-xl space-y-1 min-w-[140px] text-on-primary-container">
<p className="text-xs font-bold tracking-wider uppercase opacity-80">Module</p>
<p className="text-3xl font-black">12</p>
</div>
</div>
</div>
{/*  Bento Grid Content  */}
<div className="grid grid-cols-12 gap-8">
{/*  Roles Selector (Asymmetric Column)  */}
<div className="col-span-12 lg:col-span-4 space-y-6">
<div className="bg-surface-container-lowest rounded-xl p-6 shadow-sm">
<div className="flex justify-between items-center mb-6">
<h3 className="text-xl font-bold tracking-tight">Danh sách Vai trò</h3>
<button className="text-primary hover:bg-primary/5 p-2 rounded-full transition-colors">
<span className="material-symbols-outlined">add_circle</span>
</button>
</div>
<div className="space-y-3">
{/*  Admin Role  */}
<div className="p-4 rounded-xl bg-primary/5 border-l-4 border-primary flex items-center justify-between group cursor-pointer">
<div className="flex items-center gap-4">
<div className="w-10 h-10 rounded-full bg-primary flex items-center justify-center text-white">
<span className="material-symbols-outlined text-xl">shield_person</span>
</div>
<div>
<p className="font-bold text-on-surface">Admin</p>
<p className="text-xs text-on-surface-variant">Toàn quyền hệ thống</p>
</div>
</div>
<span className="material-symbols-outlined text-primary opacity-0 group-hover:opacity-100 transition-opacity">chevron_right</span>
</div>
{/*  Lecturer Role  */}
<div className="p-4 rounded-xl bg-surface-container hover:bg-surface-container-high transition-colors flex items-center justify-between group cursor-pointer">
<div className="flex items-center gap-4">
<div className="w-10 h-10 rounded-full bg-secondary-container flex items-center justify-center text-on-secondary-container">
<span className="material-symbols-outlined text-xl">history_edu</span>
</div>
<div>
<p className="font-bold text-on-surface">Giảng viên</p>
<p className="text-xs text-on-surface-variant">Quản lý đào tạo</p>
</div>
</div>
<span className="material-symbols-outlined text-on-surface-variant opacity-0 group-hover:opacity-100 transition-opacity">chevron_right</span>
</div>
{/*  Student Role  */}
<div className="p-4 rounded-xl bg-surface-container hover:bg-surface-container-high transition-colors flex items-center justify-between group cursor-pointer">
<div className="flex items-center gap-4">
<div className="w-10 h-10 rounded-full bg-surface-variant flex items-center justify-center text-on-surface-variant">
<span className="material-symbols-outlined text-xl">person</span>
</div>
<div>
<p className="font-bold text-on-surface">Sinh viên</p>
<p className="text-xs text-on-surface-variant">Truy cập học tập</p>
</div>
</div>
<span className="material-symbols-outlined text-on-surface-variant opacity-0 group-hover:opacity-100 transition-opacity">chevron_right</span>
</div>
{/*  Accountant Role  */}
<div className="p-4 rounded-xl bg-surface-container hover:bg-surface-container-high transition-colors flex items-center justify-between group cursor-pointer">
<div className="flex items-center gap-4">
<div className="w-10 h-10 rounded-full bg-tertiary-fixed flex items-center justify-center text-on-tertiary-fixed">
<span className="material-symbols-outlined text-xl">account_balance_wallet</span>
</div>
<div>
<p className="font-bold text-on-surface">Tài vụ</p>
<p className="text-xs text-on-surface-variant">Quản lý học phí</p>
</div>
</div>
<span className="material-symbols-outlined text-on-surface-variant opacity-0 group-hover:opacity-100 transition-opacity">chevron_right</span>
</div>
</div>
</div>
{/*  Informational Card  */}
<div className="primary-gradient p-8 rounded-xl text-white space-y-4 shadow-xl">
<span className="material-symbols-outlined text-4xl">verified_user</span>
<h4 className="text-xl font-bold leading-tight">Ghi chú Bảo mật</h4>
<p className="text-sm opacity-80 leading-relaxed">Mọi thay đổi trong ma trận phân quyền sẽ được ghi lại trong Audit Logs và yêu cầu xác thực OTP từ Admin cấp cao nhất.</p>
<button className="bg-white/20 hover:bg-white/30 px-4 py-2 rounded-lg text-xs font-bold transition-colors">Xem hướng dẫn</button>
</div>
</div>
{/*  Permission Matrix (Main Editorial Content)  */}
<div className="col-span-12 lg:col-span-8 bg-surface-container-lowest rounded-xl p-8 shadow-sm">
<div className="flex justify-between items-center mb-8">
<div>
<h3 className="text-2xl font-black tracking-tight text-on-surface">Ma trận Quyền hạn: <span className="text-primary">Giảng viên</span></h3>
<p className="text-sm text-on-surface-variant">Tùy chỉnh các module mà vai trò này được phép tương tác</p>
</div>
<div className="flex gap-2">
<button className="px-6 py-2.5 rounded-full border border-outline-variant text-on-surface font-semibold text-sm hover:bg-surface-container-low transition-all">Hủy bỏ</button>
<button className="px-6 py-2.5 rounded-full primary-gradient text-white font-semibold text-sm shadow-md active:scale-95 transition-all">Lưu thay đổi</button>
</div>
</div>
<div className="overflow-hidden">
<table className="w-full text-left">
<thead>
<tr className="bg-surface-container-low">
<th className="py-4 px-6 rounded-l-xl text-[10px] font-bold tracking-widest uppercase text-on-surface-variant">Module Hệ thống</th>
<th className="py-4 px-6 text-[10px] font-bold tracking-widest uppercase text-on-surface-variant text-center">Xem</th>
<th className="py-4 px-6 text-[10px] font-bold tracking-widest uppercase text-on-surface-variant text-center">Sửa</th>
<th className="py-4 px-6 text-[10px] font-bold tracking-widest uppercase text-on-surface-variant text-center">Xóa</th>
<th className="py-4 px-6 rounded-r-xl text-[10px] font-bold tracking-widest uppercase text-on-surface-variant text-center">Phê duyệt</th>
</tr>
</thead>
<tbody className="divide-y-0">
{/*  Row 1  */}
<tr className="hover:bg-surface-container-low/50 transition-colors group">
<td className="py-6 px-6">
<div className="flex items-center gap-3">
<div className="w-8 h-8 rounded-lg bg-surface-container-high flex items-center justify-center text-primary group-hover:bg-primary-fixed transition-colors">
<span className="material-symbols-outlined text-lg">menu_book</span>
</div>
<span className="font-semibold text-on-surface">Quản lý Giáo trình</span>
</div>
</td>
<td className="py-6 px-6 text-center">
<input checked="" className="w-5 h-5 rounded border-outline-variant text-primary focus:ring-primary/20" type="checkbox"/>
</td>
<td className="py-6 px-6 text-center">
<input checked="" className="w-5 h-5 rounded border-outline-variant text-primary focus:ring-primary/20" type="checkbox"/>
</td>
<td className="py-6 px-6 text-center">
<input className="w-5 h-5 rounded border-outline-variant text-primary focus:ring-primary/20" type="checkbox"/>
</td>
<td className="py-6 px-6 text-center">
<input className="w-5 h-5 rounded border-outline-variant text-primary focus:ring-primary/20" type="checkbox"/>
</td>
</tr>
{/*  Row 2 (Zebra)  */}
<tr className="bg-surface-container-low/30 hover:bg-surface-container-low/50 transition-colors group">
<td className="py-6 px-6">
<div className="flex items-center gap-3">
<div className="w-8 h-8 rounded-lg bg-surface-container-high flex items-center justify-center text-primary group-hover:bg-primary-fixed transition-colors">
<span className="material-symbols-outlined text-lg">grade</span>
</div>
<span className="font-semibold text-on-surface">Nhập &amp; Sửa Điểm</span>
</div>
</td>
<td className="py-6 px-6 text-center">
<input checked="" className="w-5 h-5 rounded border-outline-variant text-primary focus:ring-primary/20" type="checkbox"/>
</td>
<td className="py-6 px-6 text-center">
<input checked="" className="w-5 h-5 rounded border-outline-variant text-primary focus:ring-primary/20" type="checkbox"/>
</td>
<td className="py-6 px-6 text-center">
<input className="w-5 h-5 rounded border-outline-variant text-primary focus:ring-primary/20" type="checkbox"/>
</td>
<td className="py-6 px-6 text-center">
<input checked="" className="w-5 h-5 rounded border-outline-variant text-primary focus:ring-primary/20" type="checkbox"/>
</td>
</tr>
{/*  Row 3  */}
<tr className="hover:bg-surface-container-low/50 transition-colors group">
<td className="py-6 px-6">
<div className="flex items-center gap-3">
<div className="w-8 h-8 rounded-lg bg-surface-container-high flex items-center justify-center text-primary group-hover:bg-primary-fixed transition-colors">
<span className="material-symbols-outlined text-lg">event_available</span>
</div>
<span className="font-semibold text-on-surface">Điểm danh Sinh viên</span>
</div>
</td>
<td className="py-6 px-6 text-center">
<input checked="" className="w-5 h-5 rounded border-outline-variant text-primary focus:ring-primary/20" type="checkbox"/>
</td>
<td className="py-6 px-6 text-center">
<input checked="" className="w-5 h-5 rounded border-outline-variant text-primary focus:ring-primary/20" type="checkbox"/>
</td>
<td className="py-6 px-6 text-center">
<input checked="" className="w-5 h-5 rounded border-outline-variant text-primary focus:ring-primary/20" type="checkbox"/>
</td>
<td className="py-6 px-6 text-center">
<input className="w-5 h-5 rounded border-outline-variant text-primary focus:ring-primary/20" type="checkbox"/>
</td>
</tr>
{/*  Row 4  */}
<tr className="bg-surface-container-low/30 hover:bg-surface-container-low/50 transition-colors group">
<td className="py-6 px-6">
<div className="flex items-center gap-3">
<div className="w-8 h-8 rounded-lg bg-surface-container-high flex items-center justify-center text-primary group-hover:bg-primary-fixed transition-colors">
<span className="material-symbols-outlined text-lg">campaign</span>
</div>
<span className="font-semibold text-on-surface">Đăng Thông báo Lớp</span>
</div>
</td>
<td className="py-6 px-6 text-center">
<input checked="" className="w-5 h-5 rounded border-outline-variant text-primary focus:ring-primary/20" type="checkbox"/>
</td>
<td className="py-6 px-6 text-center">
<input checked="" className="w-5 h-5 rounded border-outline-variant text-primary focus:ring-primary/20" type="checkbox"/>
</td>
<td className="py-6 px-6 text-center">
<input checked="" className="w-5 h-5 rounded border-outline-variant text-primary focus:ring-primary/20" type="checkbox"/>
</td>
<td className="py-6 px-6 text-center">
<input checked="" className="w-5 h-5 rounded border-outline-variant text-primary focus:ring-primary/20" type="checkbox"/>
</td>
</tr>
{/*  Row 5  */}
<tr className="hover:bg-surface-container-low/50 transition-colors group">
<td className="py-6 px-6">
<div className="flex items-center gap-3">
<div className="w-8 h-8 rounded-lg bg-surface-container-high flex items-center justify-center text-primary group-hover:bg-primary-fixed transition-colors">
<span className="material-symbols-outlined text-lg">payments</span>
</div>
<span className="font-semibold text-on-surface">Quản lý Tài chính</span>
</div>
</td>
<td className="py-6 px-6 text-center">
<input className="w-5 h-5 rounded border-outline-variant text-primary focus:ring-primary/20" type="checkbox"/>
</td>
<td className="py-6 px-6 text-center">
<input className="w-5 h-5 rounded border-outline-variant text-primary focus:ring-primary/20" type="checkbox"/>
</td>
<td className="py-6 px-6 text-center">
<input className="w-5 h-5 rounded border-outline-variant text-primary focus:ring-primary/20" type="checkbox"/>
</td>
<td className="py-6 px-6 text-center">
<input className="w-5 h-5 rounded border-outline-variant text-primary focus:ring-primary/20" type="checkbox"/>
</td>
</tr>
</tbody>
</table>
</div>
</div>
</div>
{/*  Lower Action Area  */}
<div className="grid grid-cols-1 md:grid-cols-3 gap-8">
{/*  User Impact Card  */}
<div className="bg-surface-container rounded-xl p-8 flex flex-col justify-between group overflow-hidden relative">
<div className="relative z-10">
<h4 className="text-xl font-bold mb-2">Người dùng bị ảnh hưởng</h4>
<p className="text-sm text-on-surface-variant">Hiện có 142 người dùng đang được gán vai trò <span className="text-primary font-bold">Giảng viên</span>.</p>
</div>
<div className="flex -space-x-3 mt-8 relative z-10">
<img className="w-10 h-10 rounded-full border-4 border-surface-container object-cover" data-alt="portrait of a professional female lecturer in a brightly lit campus environment" src="https://lh3.googleusercontent.com/aida-public/AB6AXuDvZQOo3gGVGMt5YEP_vjyc2T2cBJE0qmF_wga_DU7d3jwGwsssvErTl8Y5Qy6OTD_gfmK1shVbgu9SglZ19Iq1aPJZnAb2158nnDqQiCL-UaIyAG8V1fw-1iYVqZxu5bPU0gSMp4bMgxeHXXAdPb-66NGKSE_3rpeCPt9fDeNTVw2Gsg3Rwa_8cIyrgoIO0UgqGahscRY-hCbakqIWnNAlYo9L4Ikqw7YADb_qHXQxJO_YnA3NPxrLvEcTB0ryqsoxydTFkOzc63_J"/>
<img className="w-10 h-10 rounded-full border-4 border-surface-container object-cover" data-alt="smiling male professor in academic library setting with soft warm light" src="https://lh3.googleusercontent.com/aida-public/AB6AXuA1npbv9MnEcQQ25dd3HhoUwp69RxYl7CO5P98XwgWJTh0DDhjsCmhaUeHK5eEDyYq_AHZSdLQmRFPyzytZZkVXar-u93_prisnMP8COmqpScEbz5xoLDno808EQSJCk7aTteOfOKt_gEKBGy6pE7Fq5oT7jRXRlii0Ut9DM8csu_W6GoGqJp_mhMQZRmR1ydgePfzunQhwBqhMcnbgllgAuWvEjh_sR9XX81xUqqdGSQ-gKrmqwbU3Gzz_VaizDpD0sMTrO6sLqz0G"/>
<img className="w-10 h-10 rounded-full border-4 border-surface-container object-cover" data-alt="professional woman in corporate attire looking at computer screen in modern university office" src="https://lh3.googleusercontent.com/aida-public/AB6AXuAA9PrLQduxQVCkiyBZ6EKlwPSWo2SH7GoPHoAv9mwf1k9d6CjaFEnS2sbk4q0c3R5gGFWG75rH9PaMDQuc8iZK199wBGWL-5C3uB-F3x9ApJCeCLbnT6TAi3QSvXMQfrb5NuIdW11JmmuzOZBnB_5mJD0CF0Bwar199Mk_WNyKVuHanjQD1rxQm_WPDa496Yaf15spqHzEQCldZoE6GxjvUQ-dwlDPwJJ-xpJEtrog-QCd4Oq0cg1VzB0XS9s-xPvXyLLcfttMWuin"/>
<div className="w-10 h-10 rounded-full border-4 border-surface-container bg-primary-fixed flex items-center justify-center text-xs font-bold text-on-primary-fixed">+139</div>
</div>
<span className="material-symbols-outlined absolute -right-4 -bottom-4 text-9xl opacity-5 group-hover:scale-110 transition-transform duration-500">groups</span>
</div>
{/*  System Logs Card  */}
<div className="md:col-span-2 bg-surface-container-high rounded-xl p-8">
<div className="flex items-center justify-between mb-6">
<h4 className="text-xl font-bold">Lịch sử thay đổi gần đây</h4>
<button className="text-xs font-bold text-primary flex items-center gap-1">TẤT CẢ LOGS <span className="material-symbols-outlined text-sm">open_in_new</span></button>
</div>
<div className="space-y-4">
<div className="flex items-start gap-4 p-3 bg-surface-container-lowest rounded-lg">
<div className="p-2 rounded bg-primary/10 text-primary">
<span className="material-symbols-outlined text-sm">edit</span>
</div>
<div className="flex-1">
<p className="text-sm font-semibold">Cập nhật quyền 'Phê duyệt' cho Sinh viên</p>
<p className="text-[10px] text-on-surface-variant font-medium">Bởi Admin: Trần Tuấn • 15 phút trước</p>
</div>
<span className="text-[10px] font-bold py-1 px-2 rounded-full bg-secondary-fixed text-on-secondary-fixed">Security</span>
</div>
<div className="flex items-start gap-4 p-3 bg-surface-container-lowest rounded-lg">
<div className="p-2 rounded bg-primary/10 text-primary">
<span className="material-symbols-outlined text-sm">add</span>
</div>
<div className="flex-1">
<p className="text-sm font-semibold">Tạo vai trò mới: 'Thanh tra đào tạo'</p>
<p className="text-[10px] text-on-surface-variant font-medium">Bởi Admin: Nguyễn Quản Trị • 2 giờ trước</p>
</div>
<span className="text-[10px] font-bold py-1 px-2 rounded-full bg-primary-fixed text-on-primary-fixed">Audit</span>
</div>
</div>
</div>
</div>
</div>
</main>
{/*  Contextual FAB (Only on Home/Dashboard) - Hidden on this detail/settings page per instructions  */}
{/*  The FAB is suppressed because this is a "Task-Focused" permissions matrix screen  */}

    </>
  );
};

export default HThngPhnQuynaTngRbacRoleBasedAccessControl;

