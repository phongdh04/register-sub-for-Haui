import React from 'react';

const GcThiXLKhiuNiPhcKho = () => {
  return (
    <>
      
{/*  Side Navigation Bar (Shared Component)  */}

{/*  Main Canvas  */}
<main className=" min-h-screen p-12">
{/*  Header Section  */}

{/*  Bento Grid Layout  */}
<div className="grid grid-cols-12 gap-8">
{/*  Left Column: Request List  */}
<section className="col-span-12 lg:col-span-5 flex flex-col gap-6">
<div className="bg-surface-container-lowest rounded-xl custom-shadow overflow-hidden flex flex-col h-full">
<div className="p-6 border-b border-surface-container-high flex justify-between items-center bg-surface-container-lowest">
<h3 className="font-headline text-lg font-bold">Danh sách yêu cầu</h3>
<div className="relative">
<span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-outline text-sm">search</span>
<input className="pl-9 pr-4 py-2 bg-surface-container rounded-full text-xs border-none focus:ring-2 focus:ring-primary w-48" placeholder="Tìm sinh viên..." type="text"/>
</div>
</div>
<div className="flex-1 overflow-y-auto max-h-[700px]">
{/*  Request Item 1 (Selected/Active State)  */}
<div className="p-6 bg-primary-container/10 border-l-4 border-primary cursor-pointer transition-colors">
<div className="flex justify-between items-start mb-2">
<span className="text-xs font-bold text-primary-container px-2 py-1 bg-primary/10 rounded-full">Kỹ thuật Phần mềm</span>
<span className="text-[10px] font-bold text-on-surface-variant">14:20 - 20/10/2023</span>
</div>
<h4 className="font-headline font-bold text-on-surface">Nguyễn Văn An - 20110456</h4>
<p className="text-sm text-on-surface-variant mt-1 line-clamp-1">Môn: Phát triển ứng dụng Web (IT4052)</p>
<div className="flex items-center gap-4 mt-3">
<div className="flex items-center gap-1">
<span className="text-[10px] text-on-surface-variant font-medium">ĐIỂM CŨ:</span>
<span className="text-sm font-black text-on-surface">6.5</span>
</div>
<span className="material-symbols-outlined text-outline text-sm">arrow_forward</span>
<span className="text-[10px] font-bold text-secondary px-2 py-0.5 border border-secondary/20 rounded-full">CHỜ XỬ LÝ</span>
</div>
</div>
{/*  Request Item 2  */}
<div className="p-6 border-b border-surface-container-high hover:bg-surface-container-low transition-colors cursor-pointer">
<div className="flex justify-between items-start mb-2">
<span className="text-xs font-bold text-on-surface-variant px-2 py-1 bg-surface-container-high rounded-full">Hệ thống Thông tin</span>
<span className="text-[10px] font-bold text-on-surface-variant">09:15 - 20/10/2023</span>
</div>
<h4 className="font-headline font-bold text-on-surface">Trần Thị Bích Ngọc - 20110512</h4>
<p className="text-sm text-on-surface-variant mt-1 line-clamp-1">Môn: Cơ sở dữ liệu nâng cao (IT3020)</p>
<div className="flex items-center gap-4 mt-3">
<div className="flex items-center gap-1">
<span className="text-[10px] text-on-surface-variant font-medium">ĐIỂM CŨ:</span>
<span className="text-sm font-black text-on-surface">4.0</span>
</div>
<span className="material-symbols-outlined text-outline text-sm">arrow_forward</span>
<span className="text-[10px] font-bold text-secondary px-2 py-0.5 border border-secondary/20 rounded-full">CHỜ XỬ LÝ</span>
</div>
</div>
{/*  Request Item 3  */}
<div className="p-6 border-b border-surface-container-high hover:bg-surface-container-low transition-colors cursor-pointer">
<div className="flex justify-between items-start mb-2">
<span className="text-xs font-bold text-on-surface-variant px-2 py-1 bg-surface-container-high rounded-full">Khoa học Máy tính</span>
<span className="text-[10px] font-bold text-on-surface-variant">Hôm qua</span>
</div>
<h4 className="font-headline font-bold text-on-surface">Lê Hoàng Nam - 19110234</h4>
<p className="text-sm text-on-surface-variant mt-1 line-clamp-1">Môn: Trí tuệ nhân tạo (IT4010)</p>
<div className="flex items-center gap-4 mt-3">
<div className="flex items-center gap-1">
<span className="text-[10px] text-on-surface-variant font-medium">ĐIỂM CŨ:</span>
<span className="text-sm font-black text-on-surface">8.0</span>
</div>
<span className="material-symbols-outlined text-outline text-sm">arrow_forward</span>
<span className="text-[10px] font-bold text-secondary px-2 py-0.5 border border-secondary/20 rounded-full">CHỜ XỬ LÝ</span>
</div>
</div>
{/*  Request Item 4  */}
<div className="p-6 border-b border-surface-container-high hover:bg-surface-container-low transition-colors cursor-pointer">
<div className="flex justify-between items-start mb-2">
<span className="text-xs font-bold text-on-surface-variant px-2 py-1 bg-surface-container-high rounded-full">Mạng máy tính</span>
<span className="text-[10px] font-bold text-on-surface-variant">18/10/2023</span>
</div>
<h4 className="font-headline font-bold text-on-surface">Phạm Minh Tuấn - 20110998</h4>
<p className="text-sm text-on-surface-variant mt-1 line-clamp-1">Môn: Quản trị mạng (IT3090)</p>
<div className="flex items-center gap-4 mt-3">
<div className="flex items-center gap-1">
<span className="text-[10px] text-on-surface-variant font-medium">ĐIỂM CŨ:</span>
<span className="text-sm font-black text-on-surface">5.5</span>
</div>
<span className="material-symbols-outlined text-outline text-sm">arrow_forward</span>
<span className="text-[10px] font-bold text-secondary px-2 py-0.5 border border-secondary/20 rounded-full">CHỜ XỬ LÝ</span>
</div>
</div>
</div>
</div>
</section>
{/*  Right Column: Details & Processing Form  */}
<section className="col-span-12 lg:col-span-7 space-y-8">
{/*  Student Info Card  */}
<div className="bg-surface-container-lowest rounded-xl custom-shadow p-8 flex gap-8 items-start relative overflow-hidden">
<div className="absolute top-0 right-0 p-8 opacity-10">
<span className="material-symbols-outlined text-9xl">person_search</span>
</div>
<div className="w-32 h-32 rounded-2xl overflow-hidden flex-shrink-0 border-4 border-surface-container-high">
<img alt="Student Portrait" className="w-full h-full object-cover" data-alt="professional headshot of a young male university student in a simple blue shirt against a neutral studio background" src="https://lh3.googleusercontent.com/aida-public/AB6AXuCA-pLxM1XzHPJ38ET7-2rVVowtzD71oEv9IdxJnZ_vHS90RzV0088j9Y7nvQ2A9ZP6KyH-_ooVMQ8-IvtFJJS2e5GTQv88Ct0GzFIug4x73BcB1x1uNrMhJKXVGMrYlBtHgR5rxU6cPJKs4TgLm0l4LhhQyeqKnmK2N17Y-RE0BzDBFi-8yzjtKyqAnX-v-3oD7al5jWx2PfqF9tk16TT0bB3QZkEG1vK7LxUtAuxyDJ6tlQx4sss64WkWxIXchW-j0FIHjun2yBMG"/>
</div>
<div className="flex-1">
<div className="flex items-center gap-3 mb-1">
<h3 className="text-3xl font-black text-on-surface tracking-tight">Nguyễn Văn An</h3>
<span className="px-3 py-1 bg-primary text-white text-[10px] font-bold rounded-full">SINH VIÊN NĂM 4</span>
</div>
<p className="text-on-surface-variant font-medium mb-4">MSSV: 20110456 | Lớp: 20KTPM1</p>
<div className="grid grid-cols-2 gap-y-4 gap-x-8 mt-6">
<div>
<p className="text-[10px] uppercase font-bold text-outline mb-1">Học phần phúc khảo</p>
<p className="text-sm font-bold text-on-surface">Phát triển ứng dụng Web (IT4052)</p>
</div>
<div>
<p className="text-[10px] uppercase font-bold text-outline mb-1">Kỳ thi</p>
<p className="text-sm font-bold text-on-surface">Cuối kỳ - Học kỳ 1 (2023-2024)</p>
</div>
<div>
<p className="text-[10px] uppercase font-bold text-outline mb-1">Ngày thi</p>
<p className="text-sm font-bold text-on-surface">15/10/2023</p>
</div>
<div>
<p className="text-[10px] uppercase font-bold text-outline mb-1">Mã phách</p>
<p className="text-sm font-bold text-on-surface">W-4052-AN-99</p>
</div>
</div>
</div>
</div>
{/*  Processing Form  */}
<div className="bg-surface-container-lowest rounded-xl custom-shadow p-8">
<div className="flex items-center gap-3 mb-8">
<div className="w-10 h-10 bg-secondary-container rounded-lg flex items-center justify-center text-on-secondary-container">
<span className="material-symbols-outlined" style={{ /* FIXME: convert style string to object -> font-variation-settings: 'FILL' 1; */ }}>edit_note</span>
</div>
<h3 className="font-headline text-xl font-bold">Xử lý cập nhật điểm</h3>
</div>
<div className="space-y-8">
{/*  Score Comparison  */}
<div className="flex items-center gap-8 bg-surface-container-low p-6 rounded-2xl">
<div className="flex-1 text-center border-r border-outline-variant/30">
<p className="text-xs font-bold text-outline-variant uppercase tracking-widest mb-2">Điểm hiện tại</p>
<p className="text-5xl font-black text-on-surface-variant">6.5</p>
</div>
<div className="w-12 h-12 flex items-center justify-center rounded-full bg-white text-primary custom-shadow">
<span className="material-symbols-outlined text-3xl">trending_up</span>
</div>
<div className="flex-1 text-center">
<p className="text-xs font-bold text-primary uppercase tracking-widest mb-2">Điểm sau phúc khảo</p>
<div className="relative inline-block">
<input className="text-5xl font-black text-primary bg-transparent border-none text-center focus:ring-0 p-0 w-24" max="10" min="0" placeholder="0.0" step="0.1" type="number"/>
<div className="absolute -bottom-1 left-0 right-0 h-1 bg-primary/20 rounded-full"></div>
</div>
</div>
</div>
{/*  Explanation Area  */}
<div className="space-y-2">
<div className="flex justify-between items-center">
<label className="text-xs font-bold text-on-surface-variant uppercase tracking-widest">Lý do / Giải trình thay đổi</label>
<span className="text-[10px] text-outline italic">Tối thiểu 20 ký tự</span>
</div>
<textarea className="w-full bg-surface-container rounded-xl border-none focus:ring-2 focus:ring-primary text-sm p-4 placeholder:text-outline/50" placeholder="Nhập chi tiết lý do thay đổi điểm (ví dụ: Chấm sót ý 2 câu 3, cộng nhầm điểm tổng...)" rows="4"></textarea>
</div>
{/*  Actions  */}
<div className="flex items-center justify-between pt-4 border-t border-surface-container-high">
<div className="flex items-center gap-2 text-on-surface-variant">
<span className="material-symbols-outlined text-sm">info</span>
<span className="text-[10px] font-medium uppercase tracking-tight">Thao tác này sẽ ghi lại lịch sử chỉnh sửa</span>
</div>
<div className="flex gap-4">
<button className="px-6 py-3 rounded-full font-bold text-sm text-outline hover:bg-surface-container-high transition-colors">
                                    Hủy bỏ
                                </button>
<button className="px-8 py-3 rounded-full font-bold text-sm text-white bg-gradient-to-br from-primary to-primary-container shadow-lg shadow-primary/20 hover:scale-105 active:scale-95 transition-all flex items-center gap-2">
<span className="material-symbols-outlined text-sm">check_circle</span>
                                    Xác nhận cập nhật
                                </button>
</div>
</div>
</div>
</div>
{/*  Student Comment Section  */}
<div className="bg-surface-container-low rounded-xl p-6 border border-dashed border-outline-variant/50">
<h4 className="text-xs font-bold text-on-surface-variant uppercase tracking-widest mb-3 flex items-center gap-2">
<span className="material-symbols-outlined text-sm">chat_bubble</span>
                        Nội dung SV khiếu nại
                    </h4>
<p className="text-sm italic text-on-surface-variant leading-relaxed">
                        "Em xin phép phúc khảo bài thi cuối kỳ ạ. Theo em tự đối chiếu với đáp án trên cổng học tập, câu 3 phần tối ưu hóa em làm đầy đủ các bước nhưng điểm thành phần phần này thấp hơn dự kiến. Kính mong thầy/cô xem xét lại giúp em. Em xin cảm ơn ạ."
                    </p>
</div>
</section>
</div>
</main>
{/*  Navigation logic helpers: Active state determined by Page Intent  */}


    </>
  );
};

export default GcThiXLKhiuNiPhcKho;
