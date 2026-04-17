import React from 'react';
import { Link, Outlet, useNavigate } from 'react-router-dom';
import { clearSession } from '../config/api';

const AdminLayout = () => {
  const navigate = useNavigate();
  const username = localStorage.getItem('username') || '';

  const logout = () => {
    clearSession();
    navigate('/login', { replace: true });
  };

  return (
    <div className="flex bg-gray-100 min-h-screen">
      <aside className="w-64 bg-white shadow-md flex-shrink-0 flex flex-col hidden md:flex border-r border-[#dce2f7]">
        <div className="p-4 border-b border-[#dce2f7] font-bold text-xl text-[#00288e] flex items-center gap-2">
          <span className="material-symbols-outlined">school</span>
          EduPort Admin
        </div>
        <div className="overflow-y-auto flex-1 p-4 space-y-1 text-sm">
          <Link to="/admin/setupcuhnhgivngtrafficsplittingqueuecontrol" className="flex items-center gap-2 p-2 hover:bg-[#dde1ff] text-[#141b2b] rounded transition">
            <span className="material-symbols-outlined shrink-0 text-base">arrow_right</span>
            <span className="truncate">Setup Cấu Hình Giờ Vàng (Traffic Splitting & Queue Control)</span>
          </Link>
          <Link to="/admin/qunldanhmckhungmlpdatamaster" className="flex items-center gap-2 p-2 hover:bg-[#dde1ff] text-[#141b2b] rounded transition">
            <span className="material-symbols-outlined shrink-0 text-base">arrow_right</span>
            <span className="truncate">Quản Lý Danh Mục Khung & Mở Lớp (Data Master)</span>
          </Link>
          <Link to="/admin/gimsttichnhktonadmin" className="flex items-center gap-2 p-2 hover:bg-[#dde1ff] text-[#141b2b] rounded transition">
            <span className="material-symbols-outlined shrink-0 text-base">arrow_right</span>
            <span className="truncate">Giám Sát Tài Chính (Kế Toán Admin)</span>
          </Link>
          <Link to="/admin/bocophntchanalytics" className="flex items-center gap-2 p-2 hover:bg-[#dde1ff] text-[#141b2b] rounded transition">
            <span className="material-symbols-outlined shrink-0 text-base">arrow_right</span>
            <span className="truncate">Báo Cáo Phân Tích (Analytics)</span>
          </Link>
          <Link to="/admin/hthngphnquynatngrbacrolebasedaccesscontrol" className="flex items-center gap-2 p-2 hover:bg-[#dde1ff] text-[#141b2b] rounded transition">
            <span className="material-symbols-outlined shrink-0 text-base">arrow_right</span>
            <span className="truncate">Hệ thống Phân Quyền Đa Tầng (RBAC - Role Based Access Control)</span>
          </Link>
          <Link to="/admin/xcthcayutmfa2favchks" className="flex items-center gap-2 p-2 hover:bg-[#dde1ff] text-[#141b2b] rounded transition">
            <span className="material-symbols-outlined shrink-0 text-base">arrow_right</span>
            <span className="truncate">Xác thực Đa Yếu Tố (MFA / 2FA) và Chữ ký số</span>
          </Link>
          <Link to="/admin/lchsnhtkduchnaudittrailslogging" className="flex items-center gap-2 p-2 hover:bg-[#dde1ff] text-[#141b2b] rounded transition">
            <span className="material-symbols-outlined shrink-0 text-base">arrow_right</span>
            <span className="truncate">Lịch sử Nhật Ký Dấu Chân (Audit Trails & Logging)</span>
          </Link>
        </div>
        <div className="p-4 border-t border-[#dce2f7] space-y-2">
          {username && (
            <p className="text-xs text-[#334155] px-2 truncate" title={username}>
              <span className="font-semibold">Đã đăng nhập:</span> {username}
            </p>
          )}
          <button
            type="button"
            onClick={logout}
            className="w-full text-left text-[#ba1a1a] flex items-center gap-2 px-2 py-2 hover:bg-[#ffdad6] rounded transition"
          >
            <span className="material-symbols-outlined">logout</span> Đăng xuất
          </button>
          <Link to="/" className="block text-sm text-[#00288e] px-2 py-1 hover:underline">
            Về trang chủ
          </Link>
        </div>
      </aside>
      
      <main className="flex-1 overflow-auto bg-[#f9f9ff]">
        <header className="bg-white shadow-sm p-4 flex items-center justify-between gap-4">
          <h1 className="text-xl font-bold text-[#141b2b]">Admin Portal</h1>
          {username && <span className="text-sm text-[#334155] hidden sm:inline truncate max-w-[12rem]">{username}</span>}
        </header>
        <div className="p-4">
           <Outlet />
        </div>
      </main>
    </div>
  );
};

export default AdminLayout;
