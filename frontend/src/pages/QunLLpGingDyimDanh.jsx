import React from 'react';

const QunLLpGingDyimDanh = () => {
  return (
    <>
      
{/*  Top Navigation Bar  */}
<nav className="fixed top-0 w-full z-40 bg-slate-50/80 dark:bg-slate-950/80 backdrop-blur-xl shadow-sm dark:shadow-none h-16 flex justify-between items-center px-8 font-manrope text-sm tracking-tight">
<div className="flex items-center gap-8">
<span className="text-2xl font-bold tracking-tighter text-blue-900 dark:text-blue-200">EduPort</span>
<div className="hidden md:flex gap-6">
<a className="text-slate-600 dark:text-slate-400 hover:text-blue-900 dark:hover:text-blue-100 transition-colors" href="#">Dashboard</a>
<a className="text-blue-700 dark:text-blue-300 font-semibold border-b-2 border-blue-700 dark:border-blue-300 pb-1" href="#">Courses</a>
<a className="text-slate-600 dark:text-slate-400 hover:text-blue-900 dark:hover:text-blue-100 transition-colors" href="#">Research</a>
<a className="text-slate-600 dark:text-slate-400 hover:text-blue-900 dark:hover:text-blue-100 transition-colors" href="#">Schedule</a>
</div>
</div>
<div className="flex items-center gap-4">
<button className="p-2 hover:bg-slate-100 dark:hover:bg-slate-900 rounded-lg transition-all">
<span className="material-symbols-outlined text-blue-900 dark:text-blue-400">notifications</span>
</button>
<button className="p-2 hover:bg-slate-100 dark:hover:bg-slate-900 rounded-lg transition-all">
<span className="material-symbols-outlined text-blue-900 dark:text-blue-400">help_outline</span>
</button>
<img alt="User Profile Avatar" className="w-8 h-8 rounded-full object-cover ml-2" data-alt="professional portrait of a university professor in a modern library setting, warm natural lighting" src="https://lh3.googleusercontent.com/aida-public/AB6AXuB8O5eIuIH2gCxB7COEZeFXLC1cQd7JQ9IO1HsjnJgRI0pkAwwJD0P7OVNReTvaMTxD2i2GAI03XYrdoBoc0S8uwpqxU-zlzdsiZsQTulzU-SnWs8PS2aNd_YR-R60V3gmsiNcOHruHfxPFzmbHJEZ4luHFn0f0LmZB4Y8pDjdNqbLw4apFFRDmzrt6BpX8INbANWbtCwkE3wLRb02InsY6XcwDCRQCMaFiUibqMpSOCqZ1-blpbfu7CaCEUqO6v5q6LDtMpEXrp-9W"/>
</div>
</nav>
{/*  Side Navigation Bar  */}

{/*  Main Content Canvas  */}
<main className="  px-8 pb-12 min-h-screen">
{/*  Header Section  */}

{/*  Main Layout: Bento Grid Style  */}
<div className="grid grid-cols-12 gap-8">
{/*  Center QR Section (The Intellectual Core)  */}
<div className="col-span-12 lg:col-span-5 flex flex-col gap-6">
<div className="bg-surface-container-lowest p-10 rounded-full shadow-2xl flex flex-col items-center justify-center relative overflow-hidden group">
{/*  Background Accent  */}
<div className="absolute inset-0 bg-primary-container/5 opacity-0 group-hover:opacity-100 transition-opacity"></div>
<div className="relative z-10 w-full aspect-square bg-white p-6 rounded-xl border border-slate-100 flex items-center justify-center">
<img alt="Mã QR Điểm danh" className="w-full h-full" data-alt="a large detailed QR code displayed on a modern white card, crisp and high-contrast for digital verification" src="https://lh3.googleusercontent.com/aida-public/AB6AXuCXvg4uO1LrdjASs3rIq8ZibW-bsNQ1e9rwMJla7I6FhG9t_ViKwpTSwhhfzfdcJlrZnc4gC66awV9rqSwtBAOX36xQHzBShQ0_Bws0zG5ShdVu4pVsyjT2itDPAZGr2Y8YJ_QeOdKFIHIE2xS-SnnPLzFuh1Jssm8oPPBpKtSvNI2LqLsTQUG2_cKncKPq3Vrm_wvTYHuIcePca6QnQG4gsolTeLu7UMIheE6LofHsCr9A0Dd_mCsRbSkHDIX21LIxbYSFGNLnu0hY"/>
</div>
<div className="mt-8 text-center space-y-4 relative z-10">
<div className="flex items-center justify-center gap-3">
<span className="text-primary font-bold text-2xl font-headline tracking-tighter">REFRESHING IN</span>
<div className="w-12 h-12 rounded-full bg-surface-container-high flex items-center justify-center text-primary font-black text-xl border-4 border-primary/20">
                                08
                            </div>
</div>
<p className="text-on-surface-variant text-sm font-medium italic">Vui lòng yêu cầu sinh viên sử dụng ứng dụng EduPort để quét mã</p>
</div>
</div>
{/*  Session Stats Card  */}
<div className="bg-surface-container p-6 rounded-xl flex justify-between items-center">
<div className="text-center flex-1 border-r border-outline-variant/30">
<p className="text-xs text-on-surface-variant uppercase tracking-widest font-bold">Tổng số</p>
<p className="text-3xl font-black text-primary font-headline">45</p>
</div>
<div className="text-center flex-1 border-r border-outline-variant/30">
<p className="text-xs text-on-surface-variant uppercase tracking-widest font-bold">Đã đến</p>
<p className="text-3xl font-black text-primary font-headline">32</p>
</div>
<div className="text-center flex-1">
<p className="text-xs text-on-surface-variant uppercase tracking-widest font-bold">Vắng</p>
<p className="text-3xl font-black text-secondary font-headline">13</p>
</div>
</div>
</div>
{/*  Side Lists Section  */}
<div className="col-span-12 lg:col-span-7 space-y-8">
{/*  Real-time Attendance List  */}
<section className="bg-surface-container-lowest rounded-xl p-8 shadow-sm">
<div className="flex justify-between items-center mb-6">
<h3 className="text-xl font-bold font-headline flex items-center gap-3">
<span className="material-symbols-outlined text-primary" style={{ /* FIXME: convert style string to object -> font-variation-settings: 'FILL' 1; */ }}>check_circle</span>
                            Sinh viên đã điểm danh
                        </h3>
<span className="bg-primary-fixed text-on-primary-fixed px-3 py-1 rounded-full text-xs font-bold">MỚI NHẤT</span>
</div>
<div className="grid grid-cols-1 md:grid-cols-2 gap-4">
{/*  Student Item  */}
<div className="flex items-center justify-between p-3 bg-surface-container-low rounded-full hover:bg-surface-container-high transition-colors">
<div className="flex items-center gap-3">
<img alt="Student" className="w-10 h-10 rounded-full" data-alt="portrait of a male student smiling, professional lighting, outdoor campus background" src="https://lh3.googleusercontent.com/aida-public/AB6AXuCRcM2d9Bo759f3SBB8xHXT1Fey3vLFN4vCsLe5-EQ9isKKaceCbNtv0Z2MCtDOGpXnI_5740gU3uGONuzLXHr02w38Z6fkDx5ajGdLxDMDMDgvjW2QQjYX7LYwINxq39VNR_xJ28AINTh7arMNq8Rdev-iHQ2xgTbJHmzOb7T7JiL_oXqG8L5XECldez3-xPVpIvVfLEDQ5QkFRutT167Ot_8gJnpujAWwPUK8hpgrWDPV1ybSbAknDoGYUhKG782hVQbzsJN8I02n"/>
<div>
<p className="text-sm font-bold text-on-surface">Lê Minh Tuấn</p>
<p className="text-[10px] text-on-surface-variant">21127001 • 14:02:45</p>
</div>
</div>
<span className="material-symbols-outlined text-primary mr-2" style={{ /* FIXME: convert style string to object -> font-variation-settings: 'FILL' 1; */ }}>check_circle</span>
</div>
{/*  Student Item  */}
<div className="flex items-center justify-between p-3 bg-surface-container-low rounded-full hover:bg-surface-container-high transition-colors">
<div className="flex items-center gap-3">
<img alt="Student" className="w-10 h-10 rounded-full" data-alt="portrait of a young female student, modern studio lighting, soft neutral background" src="https://lh3.googleusercontent.com/aida-public/AB6AXuBJU90z_sFRjsIQdOikdILF27xfDh32MfRgywMbAwPVWyfFGFwf19un7tFmmL2YvPXiCj3eI8Ci9NccFzsJYU0hPR0EX7N3IZSVJkPHC5oKj6xv3oWJVoGnEOd1a_g3oox1g-_dpJ2mnO9k2WLiAb76ffXcD2KNw71pjkCTzvdI0yk4mTwaQlUKdpwZsSO2Ov0tmZquz3chbgtGpUYk916SDpcT8E9PokjcuEq_NaiXuV2452bX7As_XOA-AnWLt23mi_c5AzeitnN8"/>
<div>
<p className="text-sm font-bold text-on-surface">Nguyễn Thu Thảo</p>
<p className="text-[10px] text-on-surface-variant">21127045 • 14:03:12</p>
</div>
</div>
<span className="material-symbols-outlined text-primary mr-2" style={{ /* FIXME: convert style string to object -> font-variation-settings: 'FILL' 1; */ }}>check_circle</span>
</div>
{/*  Student Item  */}
<div className="flex items-center justify-between p-3 bg-surface-container-low rounded-full hover:bg-surface-container-high transition-colors">
<div className="flex items-center gap-3">
<img alt="Student" className="w-10 h-10 rounded-full" data-alt="close up headshot of a student wearing glasses, bright classroom lighting" src="https://lh3.googleusercontent.com/aida-public/AB6AXuCEbX0kZXHUv7NFv4ORmOqopoaal_jAI8n0p7fdSMy79-Fkrdr6vUWXHTLVUkwS5rNDbjavAipv5ZWpz_ptmyAy6CwMkYDVDTI2nDvhxpTP_t9_NnbU1xgSrqPx-ddt-4jJQ5uAVATGoSMiVTvqpYyn9mgN-__YJr94DfNqgG2eAk9yKexS13ztydpnAqVkWqdqZT7wYe53rVYzFele5w6VzI7IuFBlJ9fFsKymjueLq9zHyntG1-DNJRpY7a0vs5t2xUTO8DIu1pId"/>
<div>
<p className="text-sm font-bold text-on-surface">Trần Hoàng Long</p>
<p className="text-[10px] text-on-surface-variant">21127209 • 14:05:01</p>
</div>
</div>
<span className="material-symbols-outlined text-primary mr-2" style={{ /* FIXME: convert style string to object -> font-variation-settings: 'FILL' 1; */ }}>check_circle</span>
</div>
{/*  Student Item  */}
<div className="flex items-center justify-between p-3 bg-surface-container-low rounded-full hover:bg-surface-container-high transition-colors">
<div className="flex items-center gap-3">
<img alt="Student" className="w-10 h-10 rounded-full" data-alt="professional portrait of a young woman smiling, daylight coming from window" src="https://lh3.googleusercontent.com/aida-public/AB6AXuDVb04nMrYQB774RjFk8UIsydpxH5L3WY2rEAS-T5H-BwuBUJO2Lc--Ikp8yCOC4hBUYy03Cp5Nx3Ffd6V29r7pLMC3BvSylyT-SVLeobshk0KDnXs2bc7xmXuDBDf4TWvaMk-Y0EvS41TfNoUKvoBZskWneVSkyE6ptHhQBnucsad4VVq-7eNjXGDp0NuvyGv6EAuxXPiNkVugzIl-ayJtoEOWKRN-GPwwCZVlcy_JoxvpLXg8Bd73f8PgOqFXw9a6ZlKT486yCLjd"/>
<div>
<p className="text-sm font-bold text-on-surface">Phạm Mỹ Linh</p>
<p className="text-[10px] text-on-surface-variant">21127334 • 14:05:22</p>
</div>
</div>
<span className="material-symbols-outlined text-primary mr-2" style={{ /* FIXME: convert style string to object -> font-variation-settings: 'FILL' 1; */ }}>check_circle</span>
</div>
</div>
<button className="w-full mt-6 py-2 text-primary font-semibold text-sm hover:underline flex items-center justify-center gap-2">
                        Xem tất cả 32 sinh viên <span className="material-symbols-outlined text-sm">arrow_forward</span>
</button>
</section>
{/*  Pending Attendance List  */}
<section className="bg-surface-container-low rounded-xl p-8">
<div className="flex justify-between items-center mb-6">
<h3 className="text-xl font-bold font-headline flex items-center gap-3 text-on-surface-variant">
<span className="material-symbols-outlined">pending_actions</span>
                            Danh sách chưa điểm danh
                        </h3>
</div>
<div className="space-y-3">
{/*  Table Style for Absent Students  */}
<div className="grid grid-cols-12 px-4 py-2 text-[10px] font-bold uppercase tracking-widest text-on-surface-variant/60">
<div className="col-span-6">SINH VIÊN</div>
<div className="col-span-4">MSSV</div>
<div className="col-span-2 text-right">THAO TÁC</div>
</div>
<div className="grid grid-cols-12 items-center px-4 py-3 bg-surface-container-lowest rounded-xl shadow-sm">
<div className="col-span-6 flex items-center gap-3">
<div className="w-8 h-8 rounded-full bg-surface-container-highest flex items-center justify-center text-on-surface-variant text-xs font-bold">VP</div>
<span className="text-sm font-medium">Vương Quốc Phước</span>
</div>
<div className="col-span-4 text-sm text-on-surface-variant">21127882</div>
<div className="col-span-2 text-right">
<button className="text-primary-container hover:bg-primary-fixed p-1 rounded transition-colors">
<span className="material-symbols-outlined text-lg">edit</span>
</button>
</div>
</div>
<div className="grid grid-cols-12 items-center px-4 py-3 bg-surface-container-lowest rounded-xl shadow-sm">
<div className="col-span-6 flex items-center gap-3">
<div className="w-8 h-8 rounded-full bg-surface-container-highest flex items-center justify-center text-on-surface-variant text-xs font-bold">KA</div>
<span className="text-sm font-medium">Kim Anh Thư</span>
</div>
<div className="col-span-4 text-sm text-on-surface-variant">21127901</div>
<div className="col-span-2 text-right">
<button className="text-primary-container hover:bg-primary-fixed p-1 rounded transition-colors">
<span className="material-symbols-outlined text-lg">edit</span>
</button>
</div>
</div>
</div>
</section>
</div>
</div>
</main>
{/*  Floating Tooltip / Status  */}
<div className="fixed bottom-8 right-8 bg-inverse-surface text-inverse-on-surface px-6 py-3 rounded-full shadow-2xl flex items-center gap-3 z-50">
<span className="relative flex h-3 w-3">
<span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-secondary opacity-75"></span>
<span className="relative inline-flex rounded-full h-3 w-3 bg-secondary"></span>
</span>
<span className="text-sm font-medium">Đang đồng bộ dữ liệu Realtime...</span>
</div>

    </>
  );
};

export default QunLLpGingDyimDanh;
