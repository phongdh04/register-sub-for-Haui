import React from 'react';
import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom';
import { clearSession } from '../config/api';

const adminNav = [
  { to: '/admin/bocophntchanalytics', label: 'Analytics', icon: 'monitoring' },
  { to: '/admin/gimsttichnhktonadmin', label: 'Giám sát tài chính', icon: 'payments' },
  { to: '/admin/qunldanhmckhungmlpdatamaster', label: 'Data master', icon: 'dataset' },
  { to: '/admin/setupcuhnhgivngtrafficsplittingqueuecontrol', label: 'Lịch đăng ký HK', icon: 'hub' },
  { to: '/admin/hthngphnquynatngrbacrolebasedaccesscontrol', label: 'RBAC', icon: 'admin_panel_settings' },
  { to: '/admin/xcthcayutmfa2favchks', label: 'MFA / 2FA', icon: 'verified_user' },
  { to: '/admin/lchsnhtkduchnaudittrailslogging', label: 'Audit trail', icon: 'history' }
];

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
          {adminNav.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                `flex items-center gap-2 p-2 rounded-lg transition ${
                  isActive ? 'bg-[#00288e] text-white shadow-sm' : 'text-[#141b2b] hover:bg-[#dde1ff]'
                }`
              }
            >
              <span className="material-symbols-outlined shrink-0 text-base">{item.icon}</span>
              <span className="truncate">{item.label}</span>
            </NavLink>
          ))}
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
