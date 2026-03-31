import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';

import DashboardSinhVinTrangCh from './pages/DashboardSinhVinTrangCh';
import TraCuHSCNhnThTcOnline from './pages/TraCuHSCNhnThTcOnline';
import CyKhungChngTrnhDegreeAuditRoadmap from './pages/CyKhungChngTrnhDegreeAuditRoadmap';
import KimTraTinHcTpTranscriptDashboard from './pages/KimTraTinHcTpTranscriptDashboard';
import TnhNngTrcGiGPreRegistrationGiLp from './pages/TnhNngTrcGiGPreRegistrationGiLp';
import TnhNngLcMnnhCao from './pages/TnhNngLcMnnhCao';
import ThutTonLogicngChtValidationRulesEngine from './pages/ThutTonLogicngChtValidationRulesEngine';
import VSinhVinStudentWallet from './pages/VSinhVinStudentWallet';
import ThanhTonQrCodeOpenApi from './pages/ThanhTonQrCodeOpenApi';
import DchVThiKhaBiuThngMinh from './pages/DchVThiKhaBiuThngMinh';
import LchThinhGiGv from './pages/LchThinhGiGv';
import StudentLayout from './layouts/StudentLayout';
import SetupCuHnhGiVngTrafficSplittingQueueControl from './pages/SetupCuHnhGiVngTrafficSplittingQueueControl';
import QunLDanhMcKhungMLpDataMaster from './pages/QunLDanhMcKhungMLpDataMaster';
import GimStTiChnhKTonAdmin from './pages/GimStTiChnhKTonAdmin';
import BoCoPhnTchAnalytics from './pages/BoCoPhnTchAnalytics';
import HThngPhnQuynaTngRbacRoleBasedAccessControl from './pages/HThngPhnQuynaTngRbacRoleBasedAccessControl';
import XcThcaYuTMfa2FaVChKS from './pages/XcThcaYuTMfa2FaVChKS';
import LchSNhtKDuChnAuditTrailsLogging from './pages/LchSNhtKDuChnAuditTrailsLogging';
import AdminLayout from './layouts/AdminLayout';
import QunLLpGingDyimDanh from './pages/QunLLpGingDyimDanh';
import MngLiNhpQunLimGradingSystem from './pages/MngLiNhpQunLimGradingSystem';
import GcThiXLKhiuNiPhcKho from './pages/GcThiXLKhiuNiPhcKho';
import CnhTayPhiCVnHcTpAcademicAdvising from './pages/CnhTayPhiCVnHcTpAcademicAdvising';
import TeacherLayout from './layouts/TeacherLayout';
import ngNhpTruynThngQunLPhin from './pages/ngNhpTruynThngQunLPhin';
import AllLayout from './layouts/AllLayout';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={
          <div className="min-h-screen bg-[#f1f3ff] flex flex-col items-center justify-center p-4">
            <h1 className="text-4xl font-black text-[#001453] mb-8">Hệ Thống Phân Quyền EduPort</h1>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 w-full max-w-4xl">
              <Link to="/student" className="block p-4 border rounded bg-white shadow hover:-translate-y-1 transition text-center font-bold text-lg text-[#00288e]">Student Portal</Link>
<Link to="/admin" className="block p-4 border rounded bg-white shadow hover:-translate-y-1 transition text-center font-bold text-lg text-[#00288e]">Admin Portal</Link>
<Link to="/teacher" className="block p-4 border rounded bg-white shadow hover:-translate-y-1 transition text-center font-bold text-lg text-[#00288e]">Teacher Portal</Link>
<Link to="/all" className="block p-4 border rounded bg-white shadow hover:-translate-y-1 transition text-center font-bold text-lg text-[#00288e]">All Portal</Link>
            </div>
          </div>
        } />
        
        <Route path="/student" element={<StudentLayout />}>
          <Route index element={<div className="p-8 text-center text-gray-500">Welcome to Student Portal. Select a module from the sidebar.</div>} />
          <Route path="dashboardsinhvintrangch" element={<DashboardSinhVinTrangCh />} />
          <Route path="tracuhscnhnthtconline" element={<TraCuHSCNhnThTcOnline />} />
          <Route path="cykhungchngtrnhdegreeauditroadmap" element={<CyKhungChngTrnhDegreeAuditRoadmap />} />
          <Route path="kimtratinhctptranscriptdashboard" element={<KimTraTinHcTpTranscriptDashboard />} />
          <Route path="tnhnngtrcgigpreregistrationgilp" element={<TnhNngTrcGiGPreRegistrationGiLp />} />
          <Route path="tnhnnglcmnnhcao" element={<TnhNngLcMnnhCao />} />
          <Route path="thuttonlogicngchtvalidationrulesengine" element={<ThutTonLogicngChtValidationRulesEngine />} />
          <Route path="vsinhvinstudentwallet" element={<VSinhVinStudentWallet />} />
          <Route path="thanhtonqrcodeopenapi" element={<ThanhTonQrCodeOpenApi />} />
          <Route path="dchvthikhabiuthngminh" element={<DchVThiKhaBiuThngMinh />} />
          <Route path="lchthinhgigv" element={<LchThinhGiGv />} />
        </Route>
        <Route path="/admin" element={<AdminLayout />}>
          <Route index element={<div className="p-8 text-center text-gray-500">Welcome to Admin Portal. Select a module from the sidebar.</div>} />
          <Route path="setupcuhnhgivngtrafficsplittingqueuecontrol" element={<SetupCuHnhGiVngTrafficSplittingQueueControl />} />
          <Route path="qunldanhmckhungmlpdatamaster" element={<QunLDanhMcKhungMLpDataMaster />} />
          <Route path="gimsttichnhktonadmin" element={<GimStTiChnhKTonAdmin />} />
          <Route path="bocophntchanalytics" element={<BoCoPhnTchAnalytics />} />
          <Route path="hthngphnquynatngrbacrolebasedaccesscontrol" element={<HThngPhnQuynaTngRbacRoleBasedAccessControl />} />
          <Route path="xcthcayutmfa2favchks" element={<XcThcaYuTMfa2FaVChKS />} />
          <Route path="lchsnhtkduchnaudittrailslogging" element={<LchSNhtKDuChnAuditTrailsLogging />} />
        </Route>
        <Route path="/teacher" element={<TeacherLayout />}>
          <Route index element={<div className="p-8 text-center text-gray-500">Welcome to Teacher Portal. Select a module from the sidebar.</div>} />
          <Route path="qunllpgingdyimdanh" element={<QunLLpGingDyimDanh />} />
          <Route path="mnglinhpqunlimgradingsystem" element={<MngLiNhpQunLimGradingSystem />} />
          <Route path="gcthixlkhiuniphckho" element={<GcThiXLKhiuNiPhcKho />} />
          <Route path="cnhtayphicvnhctpacademicadvising" element={<CnhTayPhiCVnHcTpAcademicAdvising />} />
        </Route>
        <Route path="/all" element={<AllLayout />}>
          <Route index element={<div className="p-8 text-center text-gray-500">Welcome to All Portal. Select a module from the sidebar.</div>} />
          <Route path="ngnhptruynthngqunlphin" element={<ngNhpTruynThngQunLPhin />} />
        </Route>
      </Routes>
    </Router>
  );
}

export default App;
