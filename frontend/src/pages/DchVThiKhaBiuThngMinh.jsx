import React from 'react';

const DchVThiKhaBiuThngMinh = () => {
  return (
    <>
      
{/*  SideNavBar  */}

{/*  Main Content Canvas  */}
<main className=" flex-1 min-h-screen bg-surface flex flex-col">
{/*  TopAppBar  */}

{/*  Content Area  */}
<div className="p-8 flex flex-col gap-8">
{/*  Page Header with Tabs  */}
<div className="flex flex-col md:flex-row md:items-end justify-between gap-6">
<div>
<h2 className="text-3xl font-black tracking-tight text-on-surface mb-2">Thời khóa biểu</h2>
<p className="text-on-surface-variant max-w-2xl font-body">Theo dõi lịch học tập và các kỳ thi sắp tới của bạn. Dữ liệu được cập nhật theo thời gian thực từ Phòng Đào tạo.</p>
</div>
<div className="bg-surface-container p-1.5 rounded-full flex items-center gap-1 self-start">
<button className="px-6 py-2 bg-surface-container-lowest text-primary font-bold rounded-full shadow-sm transition-all text-sm">Lịch Học</button>
<button className="px-6 py-2 text-on-surface-variant font-medium hover:text-on-surface transition-all text-sm">Lịch Thi</button>
</div>
</div>
{/*  Dashboard Grid View  */}
<div className="grid grid-cols-1 lg:grid-cols-4 gap-8">
{/*  Left Column: Weekly Schedule  */}
<div className="lg:col-span-3 flex flex-col gap-6">
<div className="bg-surface-container-lowest rounded-full p-6 shadow-sm shadow-on-surface/5">
<div className="flex items-center justify-between mb-8">
<div className="flex items-center gap-4">
<button className="p-2 hover:bg-surface-container-low rounded-xl transition-colors">
<span className="material-symbols-outlined">chevron_left</span>
</button>
<h3 className="text-lg font-bold font-headline">Học kỳ 1 - Năm học 2023-2024</h3>
<button className="p-2 hover:bg-surface-container-low rounded-xl transition-colors">
<span className="material-symbols-outlined">chevron_right</span>
</button>
</div>
<div className="flex gap-2">
<button className="px-4 py-2 bg-primary-container text-on-primary-container font-semibold rounded-xl text-xs flex items-center gap-2">
<span className="material-symbols-outlined text-sm">download</span> Tải PDF
                                </button>
<button className="px-4 py-2 border border-outline-variant/30 text-on-surface font-semibold rounded-xl text-xs flex items-center gap-2">
<span className="material-symbols-outlined text-sm">print</span> In lịch
                                </button>
</div>
</div>
{/*  Schedule Grid  */}
<div className="overflow-x-auto">
<div className="min-w-[800px]">
{/*  Header Row  */}
<div className="schedule-grid border-b border-outline-variant/20 pb-4">
<div className="text-xs font-bold text-on-surface-variant uppercase tracking-widest text-center">Tiết</div>
<div className="flex flex-col items-center">
<span className="text-xs font-bold text-on-surface-variant uppercase tracking-widest">Thứ 2</span>
<span className="text-xl font-black text-primary/40 mt-1">12</span>
</div>
<div className="flex flex-col items-center">
<span className="text-xs font-bold text-on-surface-variant uppercase tracking-widest">Thứ 3</span>
<span className="text-xl font-black text-primary mt-1">13</span>
</div>
<div className="flex flex-col items-center">
<span className="text-xs font-bold text-on-surface-variant uppercase tracking-widest">Thứ 4</span>
<span className="text-xl font-black text-primary/40 mt-1">14</span>
</div>
<div className="flex flex-col items-center">
<span className="text-xs font-bold text-on-surface-variant uppercase tracking-widest">Thứ 5</span>
<span className="text-xl font-black text-primary/40 mt-1">15</span>
</div>
<div className="flex flex-col items-center">
<span className="text-xs font-bold text-on-surface-variant uppercase tracking-widest">Thứ 6</span>
<span className="text-xl font-black text-primary/40 mt-1">16</span>
</div>
<div className="flex flex-col items-center">
<span className="text-xs font-bold text-on-surface-variant uppercase tracking-widest">Thứ 7</span>
<span className="text-xl font-black text-primary/40 mt-1">17</span>
</div>
<div className="flex flex-col items-center">
<span className="text-xs font-bold text-on-surface-variant uppercase tracking-widest">Chủ nhật</span>
<span className="text-xl font-black text-error/40 mt-1">18</span>
</div>
</div>
{/*  Rows  */}
<div className="divide-y divide-outline-variant/10">
{/*  AM Session  */}
<div className="schedule-grid py-4 min-h-[140px]">
<div className="flex flex-col items-center justify-center text-[10px] text-on-surface-variant font-bold">
<span>SÁNG</span>
<span className="mt-1 opacity-60">1-5</span>
</div>
<div className="p-2">
{/*  Empty slot  */}
</div>
<div className="p-2">
<div className="bg-primary/5 border-l-4 border-primary rounded-xl p-3 h-full flex flex-col justify-between hover:bg-primary/10 transition-colors cursor-pointer group">
<div>
<h4 className="text-xs font-bold text-primary mb-1 group-hover:underline">Phát triển ứng dụng Mobile</h4>
<p className="text-[10px] text-on-surface-variant font-medium">Phòng: A2-301</p>
</div>
<div className="flex items-center gap-2 mt-2">
<img alt="Giảng viên" className="w-5 h-5 rounded-full" data-alt="headshot of a mature professor with graying hair and glasses in a professional setting" src="https://lh3.googleusercontent.com/aida-public/AB6AXuDS9jv35i55C04AhARtrMuFdCP_G4t3at8m2Bat8EFbxxrK4qotF7BhbF6LxNEWQXtwy_CeAld6D9WWQ4erEtC58Q4MpVUNaIOcQKCeEPHmMGuek7R2v-RRwSjiLkVb_6imMufMwI3vkkLJWImMxY2nhhJGUgJ2xVGivlnufBzvFev65axFGSvxPYIN8wdCdGls0m2lv1dl7QDSc-DbbFJi-3v6T6yJkD_rD59rBA0jjHgv8Hv1uqUf_6P8tKnZMe7tHNArM29ujuEl"/>
<span className="text-[9px] text-on-surface font-semibold">TS. Lê Hoàng Nam</span>
</div>
</div>
</div>
<div className="p-2">
<div className="bg-secondary/5 border-l-4 border-secondary rounded-xl p-3 h-full flex flex-col justify-between hover:bg-secondary/10 transition-colors cursor-pointer">
<div>
<h4 className="text-xs font-bold text-secondary-container mb-1">Cơ sở dữ liệu nâng cao</h4>
<p className="text-[10px] text-on-surface-variant font-medium">Phòng: C3-502</p>
</div>
<div className="flex items-center gap-2 mt-2">
<img alt="Giảng viên" className="w-5 h-5 rounded-full" data-alt="professional portrait of a female university lecturer with dark hair smiling politely" src="https://lh3.googleusercontent.com/aida-public/AB6AXuDn4dPCquY5oaUjlWS5aDIinHPTcWJ3VmFOInCPsFUHNokz1ht1q3tuVG1-4Y5ydLTGNe_dML5NOB8CBurOa0jOJW0AwjiGDnd5ynkp-OzdNwDMCHTnk82noH4yHohBx8SfxKG7pN-ZGxXUtUFqM0Xk5aq_hS4DBU67M1WQvj-t9L08HE_qvrcYAI2SBMJIkUe1oC4Jng4oN6tDjARhRaSprUJoJr3_d5XBE8EyUlUC8UkMmAyzDCctpGAuX9OIy5fur2ht8rLyvD35"/>
<span className="text-[9px] text-on-surface font-semibold">ThS. Phan Kim Chi</span>
</div>
</div>
</div>
<div className="p-2"></div>
<div className="p-2">
<div className="bg-primary/5 border-l-4 border-primary rounded-xl p-3 h-full flex flex-col justify-between hover:bg-primary/10 transition-colors cursor-pointer">
<div>
<h4 className="text-xs font-bold text-primary mb-1">Kiến trúc phần mềm</h4>
<p className="text-[10px] text-on-surface-variant font-medium">Phòng: A1-205</p>
</div>
<div className="flex items-center gap-2 mt-2">
<img alt="Giảng viên" className="w-5 h-5 rounded-full" data-alt="thoughtful academic researcher man with glasses sitting in a light filled office" src="https://lh3.googleusercontent.com/aida-public/AB6AXuCU6Y3BiYaaE4_6ilINtG3Vz8vfKQnCYst337iZfLSjOnKoduSRuKrZ2YDuJUX-Z1gHSZi-oHcuBqanmWdQkTYOc9AO4-dhfDpdgthbPSpYf9lI2qSu5dY9F8wqslpw4oJUHpiDIfBTEQiuzN77zw94LipLdkZTYqK4whIXjxbgASNVw424WCWDzDmwtVdA1Zk2TC3jFe4wkf73EM4oX5DuiUBxHF5WivNKmKseIAQjPsLDPWLTPIkwuENFJ1RfX3ZnftaBfTxQBN93"/>
<span className="text-[9px] text-on-surface font-semibold">PGS. Nguyễn Trung Kiên</span>
</div>
</div>
</div>
<div className="p-2"></div>
<div className="p-2"></div>
</div>
{/*  PM Session  */}
<div className="schedule-grid py-4 min-h-[140px]">
<div className="flex flex-col items-center justify-center text-[10px] text-on-surface-variant font-bold">
<span>CHIỀU</span>
<span className="mt-1 opacity-60">6-10</span>
</div>
<div className="p-2">
<div className="bg-tertiary/5 border-l-4 border-tertiary rounded-xl p-3 h-full flex flex-col justify-between hover:bg-tertiary/10 transition-colors cursor-pointer">
<div>
<h4 className="text-xs font-bold text-tertiary mb-1">Tiếng Anh chuyên ngành</h4>
<p className="text-[10px] text-on-surface-variant font-medium">Phòng: E5-104</p>
</div>
<div className="flex items-center gap-2 mt-2">
<img alt="Giảng viên" className="w-5 h-5 rounded-full" data-alt="cheerful young female educator with glasses in an bright modern classroom" src="https://lh3.googleusercontent.com/aida-public/AB6AXuA8gk9yYprYRCtTD8S1ImKxuzjOZTYPHGQ5h_zxvv3ZYN6or8iTKyWOXPXeGBa5vH0UFUF8MRb8Gl_c3-enjyEgp1wXUPC0ET5XNrdvEXra615-oAMg0AYJkiv89OTC80F8iwlsFGZW3zXlpCawm3FYqCof2a-xdHUOKY7D_WgHzTfz-9olx6FMOZvqHms6g1-P3HtNn1rRc9lFTIGIB1XXoofGBwqwNj7Yi5r3ZFHS7ck_2So0Zu9js40QxK5FWY7neOmvdgY8nt24"/>
<span className="text-[9px] text-on-surface font-semibold">Cô Sarah Johnson</span>
</div>
</div>
</div>
<div className="p-2"></div>
<div className="p-2"></div>
<div className="p-2">
<div className="bg-primary/5 border-l-4 border-primary rounded-xl p-3 h-full flex flex-col justify-between hover:bg-primary/10 transition-colors cursor-pointer">
<div>
<h4 className="text-xs font-bold text-primary mb-1">Xử lý ảnh số</h4>
<p className="text-[10px] text-on-surface-variant font-medium">Phòng: Lab-404</p>
</div>
<div className="flex items-center gap-2 mt-2">
<img alt="Giảng viên" className="w-5 h-5 rounded-full" data-alt="confident professional man in a business casual shirt with blurred bookshelf background" src="https://lh3.googleusercontent.com/aida-public/AB6AXuCHpxq7M9fdWNILOrGWdl-O_EgxH5QWMY0td2KxwPkBluFBRADvryUS2iHIIc9l1jvFsyS0hsHnR40uvAfJFpJR5y02ExpAxLPfoLErDo-jvzVuiyCc1uZ6cJL6XEr2sMFqAYHByZJhpzKqVu4faWL-PS8fE1ViC9ZQ2FGghWCw4ODGOOQAjMabdQoI9EL5xQQy5YfozdrwVkEFyeWF26Wqcsyc82SlRKB7ooOJb7kLw0B9y-IjwWjaRmPWhVP0O7OYqhJWzHXWl-fF"/>
<span className="text-[9px] text-on-surface font-semibold">TS. Trần Quang Huy</span>
</div>
</div>
</div>
<div className="p-2"></div>
<div className="p-2"></div>
<div className="p-2"></div>
</div>
</div>
</div>
</div>
</div>
</div>
{/*  Right Column: Sidebar Widgets  */}
<div className="flex flex-col gap-6">
{/*  Today Overview Card  */}
<div className="bg-primary text-white p-6 rounded-full shadow-xl shadow-primary/20 relative overflow-hidden group">
<div className="relative z-10">
<h4 className="text-xs font-bold uppercase tracking-widest opacity-80 mb-4">Hôm nay - Thứ 3</h4>
<div className="flex flex-col gap-4">
<div className="bg-white/10 backdrop-blur-md rounded-2xl p-4 border border-white/10">
<p className="text-[10px] font-bold text-secondary-fixed mb-1 uppercase">SẮP DIỄN RA (08:00)</p>
<h5 className="text-sm font-bold mb-1">Phát triển ứng dụng Mobile</h5>
<div className="flex items-center gap-2 opacity-80 text-[11px]">
<span className="material-symbols-outlined text-sm">location_on</span>
<span>Phòng A2-301</span>
</div>
</div>
<div className="bg-white/5 rounded-2xl p-4 border border-white/5">
<p className="text-[10px] font-bold opacity-60 mb-1 uppercase">TIẾP THEO (13:00)</p>
<h5 className="text-sm font-bold opacity-70">Sinh hoạt lớp định kỳ</h5>
<div className="flex items-center gap-2 opacity-50 text-[11px]">
<span className="material-symbols-outlined text-sm">videocam</span>
<span>Họp trực tuyến</span>
</div>
</div>
</div>
<button className="w-full mt-4 py-3 bg-white text-primary rounded-xl font-bold text-xs hover:bg-slate-100 transition-colors">
                                Điểm danh ngay
                            </button>
</div>
<span className="material-symbols-outlined absolute -right-4 -bottom-4 text-9xl opacity-5 transform rotate-12">calendar_today</span>
</div>
{/*  Exam Reminders (Preview of Module 8)  */}
<div className="bg-surface-container-low p-6 rounded-full border border-outline-variant/10">
<div className="flex items-center justify-between mb-6">
<h4 className="text-sm font-extrabold font-headline">Lịch thi sắp tới</h4>
<span className="px-2 py-0.5 bg-error-container text-on-error-container rounded-full text-[10px] font-bold">2 Môn</span>
</div>
<div className="flex flex-col gap-5">
<div className="flex gap-4 items-start">
<div className="bg-white p-2 rounded-xl text-center min-w-[50px] shadow-sm">
<p className="text-[10px] font-bold text-error uppercase">Th12</p>
<p className="text-lg font-black text-on-surface">25</p>
</div>
<div>
<h5 className="text-xs font-bold text-on-surface leading-tight">Cấu trúc dữ liệu &amp; Giải thuật</h5>
<p className="text-[10px] text-on-surface-variant mt-1">Ca 1 (07:30) • Phòng B2-101</p>
</div>
</div>
<div className="flex gap-4 items-start">
<div className="bg-white p-2 rounded-xl text-center min-w-[50px] shadow-sm opacity-60">
<p className="text-[10px] font-bold text-on-surface-variant uppercase">Th12</p>
<p className="text-lg font-black text-on-surface">28</p>
</div>
<div>
<h5 className="text-xs font-bold text-on-surface leading-tight">Mạng máy tính cơ bản</h5>
<p className="text-[10px] text-on-surface-variant mt-1">Ca 3 (13:30) • Phòng C1-204</p>
</div>
</div>
</div>
<button className="w-full mt-6 py-3 border border-primary/20 text-primary rounded-xl font-bold text-xs hover:bg-primary/5 transition-colors">
                            Xem tất cả lịch thi
                        </button>
</div>
{/*  Statistics Card  */}
<div className="bg-white p-6 rounded-full shadow-sm">
<h4 className="text-sm font-extrabold font-headline mb-4">Chuyên cần</h4>
<div className="flex items-center gap-4">
<div className="relative w-16 h-16 flex items-center justify-center">
<svg className="w-16 h-16 transform -rotate-90">
<circle className="text-slate-100" cx="32" cy="32" fill="transparent" r="28" stroke="currentColor" stroke-width="6"></circle>
<circle className="text-primary" cx="32" cy="32" fill="transparent" r="28" stroke="currentColor" stroke-dasharray="176" stroke-dashoffset="35" stroke-width="6"></circle>
</svg>
<span className="absolute text-xs font-black">82%</span>
</div>
<div>
<p className="text-[11px] text-on-surface-variant leading-relaxed">Bạn đã tham gia <span className="text-primary font-bold">18/22</span> buổi học trong tháng này. Tốt hơn 12% so với tháng trước.</p>
</div>
</div>
</div>
</div>
</div>
{/*  List View (Lịch Thi Hidden Tab Example)  */}
<div className="hidden bg-surface-container-lowest rounded-full p-8 shadow-sm">
<div className="flex items-center justify-between mb-8">
<h3 className="text-xl font-black font-headline">Chi tiết lịch thi - Học kỳ 1</h3>
<div className="flex gap-4">
<select className="bg-surface-container-low border-none rounded-xl px-4 py-2 text-xs font-semibold focus:ring-primary/20">
<option>Học kỳ 1, 2023-2024</option>
<option>Học kỳ 2, 2022-2023</option>
</select>
</div>
</div>
<table className="w-full text-left">
<thead>
<tr className="bg-surface-container-low">
<th className="px-6 py-4 label-sm font-bold text-on-surface-variant uppercase tracking-wider rounded-l-xl">Môn thi</th>
<th className="px-6 py-4 label-sm font-bold text-on-surface-variant uppercase tracking-wider">Ngày thi</th>
<th className="px-6 py-4 label-sm font-bold text-on-surface-variant uppercase tracking-wider">Ca thi</th>
<th className="px-6 py-4 label-sm font-bold text-on-surface-variant uppercase tracking-wider">SBD</th>
<th className="px-6 py-4 label-sm font-bold text-on-surface-variant uppercase tracking-wider">Phòng thi</th>
<th className="px-6 py-4 label-sm font-bold text-on-surface-variant uppercase tracking-wider rounded-r-xl text-right">Trạng thái</th>
</tr>
</thead>
<tbody className="divide-y divide-outline-variant/5">
<tr className="hover:bg-surface-container-lowest transition-colors">
<td className="px-6 py-5">
<p className="text-sm font-bold text-on-surface">Cấu trúc dữ liệu &amp; Giải thuật</p>
<p className="text-[10px] text-on-surface-variant">IT3011 - 3 Tín chỉ</p>
</td>
<td className="px-6 py-5 text-sm font-medium">25/12/2023</td>
<td className="px-6 py-5 text-sm font-medium">Ca 1 (07:30)</td>
<td className="px-6 py-5 text-sm font-bold text-primary">A25-102</td>
<td className="px-6 py-5 text-sm font-medium">B2-101</td>
<td className="px-6 py-5 text-right">
<span className="px-3 py-1 bg-secondary-fixed text-on-secondary-fixed rounded-full text-[10px] font-bold">Sắp thi</span>
</td>
</tr>
<tr className="bg-surface-container-low/30 hover:bg-surface-container-lowest transition-colors">
<td className="px-6 py-5">
<p className="text-sm font-bold text-on-surface">Mạng máy tính cơ bản</p>
<p className="text-[10px] text-on-surface-variant">IT3080 - 2 Tín chỉ</p>
</td>
<td className="px-6 py-5 text-sm font-medium">28/12/2023</td>
<td className="px-6 py-5 text-sm font-medium">Ca 3 (13:30)</td>
<td className="px-6 py-5 text-sm font-bold text-primary">A25-089</td>
<td className="px-6 py-5 text-sm font-medium">C1-204</td>
<td className="px-6 py-5 text-right">
<span className="px-3 py-1 bg-secondary-fixed text-on-secondary-fixed rounded-full text-[10px] font-bold">Sắp thi</span>
</td>
</tr>
</tbody>
</table>
</div>
</div>
{/*  FAB - Fixed Action Button for support  */}
<div className="fixed bottom-8 right-8">
<button className="w-14 h-14 bg-gradient-to-br from-primary to-primary-container text-white rounded-full shadow-2xl flex items-center justify-center hover:scale-110 transition-transform">
<span className="material-symbols-outlined text-2xl" style={{ /* FIXME: convert style string to object -> font-variation-settings: 'FILL' 1; */ }}>help_center</span>
</button>
</div>
</main>

    </>
  );
};

export default DchVThiKhaBiuThngMinh;
