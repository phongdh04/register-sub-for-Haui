import React from 'react';
import { Link, Outlet } from 'react-router-dom';

const TeacherLayout = () => {
  return (
    <div className="flex bg-gray-100 min-h-screen">
      <aside className="w-64 bg-white shadow-md flex-shrink-0 flex flex-col hidden md:flex border-r border-[#dce2f7]">
        <div className="p-4 border-b border-[#dce2f7] font-bold text-xl text-[#00288e] flex items-center gap-2">
          <span className="material-symbols-outlined">school</span>
          EduPort Teacher
        </div>
        <div className="overflow-y-auto flex-1 p-4 space-y-1 text-sm">
          <Link to="/teacher/qunllpgingdyimdanh" className="flex items-center gap-2 p-2 hover:bg-[#dde1ff] text-[#141b2b] rounded transition">
            <span className="material-symbols-outlined shrink-0 text-base">arrow_right</span>
            <span className="truncate">Quản lý Lớp Giảng dạy & Điểm danh</span>
          </Link>
          <Link to="/teacher/mnglinhpqunlimgradingsystem" className="flex items-center gap-2 p-2 hover:bg-[#dde1ff] text-[#141b2b] rounded transition">
            <span className="material-symbols-outlined shrink-0 text-base">arrow_right</span>
            <span className="truncate">Mạng lưới Nhập & Quản Lý Điểm (Grading System)</span>
          </Link>
          <Link to="/teacher/gcthixlkhiuniphckho" className="flex items-center gap-2 p-2 hover:bg-[#dde1ff] text-[#141b2b] rounded transition">
            <span className="material-symbols-outlined shrink-0 text-base">arrow_right</span>
            <span className="truncate">Gác thi & Xử lý Khiếu nại (Phúc khảo)</span>
          </Link>
          <Link to="/teacher/cnhtayphicvnhctpacademicadvising" className="flex items-center gap-2 p-2 hover:bg-[#dde1ff] text-[#141b2b] rounded transition">
            <span className="material-symbols-outlined shrink-0 text-base">arrow_right</span>
            <span className="truncate">Cánh Tay Phải "Cố Vấn Học Tập" (Academic Advising)</span>
          </Link>
        </div>
        <div className="p-4 border-t border-[#dce2f7]">
          <Link to="/" className="text-[#ba1a1a] flex items-center gap-2 px-2 py-2 hover:bg-[#ffdad6] rounded transition">
            <span className="material-symbols-outlined">logout</span> Thoát
          </Link>
        </div>
      </aside>
      
      <main className="flex-1 overflow-auto bg-[#f9f9ff]">
        <header className="bg-white shadow-sm p-4 flex items-center justify-between">
           <h1 className="text-xl font-bold text-[#141b2b]">Teacher Portal</h1>
        </header>
        <div className="p-4">
           <Outlet />
        </div>
      </main>
    </div>
  );
};

export default TeacherLayout;
