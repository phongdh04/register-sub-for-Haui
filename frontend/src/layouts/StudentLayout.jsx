import React from 'react';
import { Link, Outlet, useNavigate } from 'react-router-dom';
import { clearSession } from '../config/api';

const StudentLayout = () => {
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
          EduPort Student
        </div>
        <div className="overflow-y-auto flex-1 p-4 space-y-1 text-sm">
          <Link to="/student/dashboardsinhvintrangch" className="flex items-center gap-2 p-2 hover:bg-[#dde1ff] text-[#141b2b] rounded transition">
            <span className="material-symbols-outlined shrink-0 text-base">arrow_right</span>
            <span className="truncate">Dashboard Sinh viên (Trang chủ)</span>
          </Link>
          <Link to="/student/tracuhscnhnthtconline" className="flex items-center gap-2 p-2 hover:bg-[#dde1ff] text-[#141b2b] rounded transition">
            <span className="material-symbols-outlined shrink-0 text-base">arrow_right</span>
            <span className="truncate">Tra cứu Hồ sơ Cá nhân & Thủ tục online</span>
          </Link>
          <Link to="/student/cykhungchngtrnhdegreeauditroadmap" className="flex items-center gap-2 p-2 hover:bg-[#dde1ff] text-[#141b2b] rounded transition">
            <span className="material-symbols-outlined shrink-0 text-base">arrow_right</span>
            <span className="truncate">Cây Khung Chương Trình (Degree Audit / Roadmap)</span>
          </Link>
          <Link to="/student/kimtratinhctptranscriptdashboard" className="flex items-center gap-2 p-2 hover:bg-[#dde1ff] text-[#141b2b] rounded transition">
            <span className="material-symbols-outlined shrink-0 text-base">arrow_right</span>
            <span className="truncate">Kiểm tra tiến độ học tập (Transcript Dashboard)</span>
          </Link>
          <Link to="/student/tnhnngtrcgigpreregistrationgilp" className="flex items-center gap-2 p-2 hover:bg-[#dde1ff] text-[#141b2b] rounded transition">
            <span className="material-symbols-outlined shrink-0 text-base">arrow_right</span>
            <span className="truncate">Tính năng Trước Giờ G (Pre-Registration / Giả lập)</span>
          </Link>
          <Link to="/student/tnhnnglcmnnhcao" className="flex items-center gap-2 p-2 hover:bg-[#dde1ff] text-[#141b2b] rounded transition">
            <span className="material-symbols-outlined shrink-0 text-base">arrow_right</span>
            <span className="truncate">Tính năng Lọc Môn Đỉnh Cao</span>
          </Link>
          <Link to="/student/thuttonlogicngchtvalidationrulesengine" className="flex items-center gap-2 p-2 hover:bg-[#dde1ff] text-[#141b2b] rounded transition">
            <span className="material-symbols-outlined shrink-0 text-base">arrow_right</span>
            <span className="truncate">Thuật Toán Logic Đóng Chốt (Validation Rules Engine)</span>
          </Link>
          <Link to="/student/vsinhvinstudentwallet" className="flex items-center gap-2 p-2 hover:bg-[#dde1ff] text-[#141b2b] rounded transition">
            <span className="material-symbols-outlined shrink-0 text-base">arrow_right</span>
            <span className="truncate">Ví Sinh Viên (Student Wallet)</span>
          </Link>
          <Link to="/student/thanhtonqrcodeopenapi" className="flex items-center gap-2 p-2 hover:bg-[#dde1ff] text-[#141b2b] rounded transition">
            <span className="material-symbols-outlined shrink-0 text-base">arrow_right</span>
            <span className="truncate">Thanh Toán QR Code (Open API)</span>
          </Link>
          <Link to="/student/dchvthikhabiuthngminh" className="flex items-center gap-2 p-2 hover:bg-[#dde1ff] text-[#141b2b] rounded transition">
            <span className="material-symbols-outlined shrink-0 text-base">arrow_right</span>
            <span className="truncate">Dịch vụ Thời Khóa Biểu thông minh</span>
          </Link>
          <Link to="/student/lchthinhgigv" className="flex items-center gap-2 p-2 hover:bg-[#dde1ff] text-[#141b2b] rounded transition">
            <span className="material-symbols-outlined shrink-0 text-base">arrow_right</span>
            <span className="truncate">Lịch Thi & Đánh Giá GV</span>
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
          <h1 className="text-xl font-bold text-[#141b2b]">Student Portal</h1>
          {username && <span className="text-sm text-[#334155] hidden sm:inline truncate max-w-[12rem]">{username}</span>}
        </header>
        <div className="p-4">
           <Outlet />
        </div>
      </main>
    </div>
  );
};

export default StudentLayout;
