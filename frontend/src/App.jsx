import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link, Navigate } from 'react-router-dom';

import RequireAuth from './components/RequireAuth';
import DashboardSinhVinTrangCh from './pages/DashboardSinhVinTrangCh';
import TraCuHSCNhnThTcOnline from './pages/TraCuHSCNhnThTcOnline';
import CyKhungChngTrnhDegreeAuditRoadmap from './pages/CyKhungChngTrnhDegreeAuditRoadmap';
import KimTraTinHcTpTranscriptDashboard from './pages/KimTraTinHcTpTranscriptDashboard';
import TnhNngTrcGiGPreRegistrationGiLp from './pages/TnhNngTrcGiGPreRegistrationGiLp';
import StudentRegistrationPage from './pages/StudentRegistrationPage';
import TnhNngLcMnnhCao from './pages/TnhNngLcMnnhCao';
import ThutTonLogicngChtValidationRulesEngine from './pages/ThutTonLogicngChtValidationRulesEngine';
import VSinhVinStudentWallet from './pages/VSinhVinStudentWallet';
import ThanhTonQrCodeOpenApi from './pages/ThanhTonQrCodeOpenApi';
import DchVThiKhaBiuThngMinh from './pages/DchVThiKhaBiuThngMinh';
import LchThinhGiGv from './pages/LchThinhGiGv';
import StudentLayout from './layouts/StudentLayout';
import SetupCuHnhGiVngTrafficSplittingQueueControl from './pages/SetupCuHnhGiVngTrafficSplittingQueueControl';
import AdminHocKyScheduleConfigPage from './pages/AdminHocKyScheduleConfigPage';
import QunLDanhMcKhungMLpDataMaster from './pages/QunLDanhMcKhungMLpDataMaster';
import GimStTiChnhKTonAdmin from './pages/GimStTiChnhKTonAdmin';
import BoCoPhnTchAnalytics from './pages/BoCoPhnTchAnalytics';
import HThngPhnQuynaTngRbacRoleBasedAccessControl from './pages/HThngPhnQuynaTngRbacRoleBasedAccessControl';
import XcThcaYuTMfa2FaVChKS from './pages/XcThcaYuTMfa2FaVChKS';
import LchSNhtKDuChnAuditTrailsLogging from './pages/LchSNhtKDuChnAuditTrailsLogging';
import AdminRegistrationWindowsPage from './pages/AdminRegistrationWindowsPage';
import AdminClassPublishPage from './pages/AdminClassPublishPage';
import AdminPreRegistrationDemandPage from './pages/AdminPreRegistrationDemandPage';
import AdminTimetableProjectionToolsPage from './pages/AdminTimetableProjectionToolsPage';
import AdminRegistrationMonitoringPage from './pages/AdminRegistrationMonitoringPage';
import AdminLayout from './layouts/AdminLayout';
import QunLLpGingDyimDanh from './pages/QunLLpGingDyimDanh';
import MngLiNhpQunLimGradingSystem from './pages/MngLiNhpQunLimGradingSystem';
import GcThiXLKhiuNiPhcKho from './pages/GcThiXLKhiuNiPhcKho';
import CnhTayPhiCVnHcTpAcademicAdvising from './pages/CnhTayPhiCVnHcTpAcademicAdvising';
import TeacherLayout from './layouts/TeacherLayout';
import NgNhpTruynThngQunLPhin from './pages/ngNhpTruynThngQunLPhin';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<NgNhpTruynThngQunLPhin />} />
        <Route path="/all/ngnhptruynthngqunlphin" element={<Navigate to="/login" replace />} />

        <Route path="/" element={<Navigate to="/login" replace />} />

        <Route
          path="/student"
          element={
            <RequireAuth roles={['ROLE_STUDENT']}>
              <StudentLayout />
            </RequireAuth>
          }
        >
          <Route index element={<Navigate to="/student/dashboardsinhvintrangch" replace />} />
          <Route path="dashboardsinhvintrangch" element={<DashboardSinhVinTrangCh />} />
          <Route path="tracuhscnhnthtconline" element={<TraCuHSCNhnThTcOnline />} />
          <Route path="cykhungchngtrnhdegreeauditroadmap" element={<CyKhungChngTrnhDegreeAuditRoadmap />} />
          <Route path="kimtratinhctptranscriptdashboard" element={<KimTraTinHcTpTranscriptDashboard />} />
          <Route path="registration" element={<StudentRegistrationPage />} />
          <Route path="pre-registration" element={<TnhNngTrcGiGPreRegistrationGiLp />} />
          <Route path="tnhnngtrcgigpreregistrationgilp" element={<Navigate to="/student/pre-registration" replace />} />
          <Route path="intents-legacy" element={<Navigate to="/student/pre-registration" replace />} />
          <Route path="tnhnnglcmnnhcao" element={<TnhNngLcMnnhCao />} />
          <Route path="thuttonlogicngchtvalidationrulesengine" element={<ThutTonLogicngChtValidationRulesEngine />} />
          <Route path="vsinhvinstudentwallet" element={<VSinhVinStudentWallet />} />
          <Route path="thanhtonqrcodeopenapi" element={<ThanhTonQrCodeOpenApi />} />
          <Route path="dchvthikhabiuthngminh" element={<DchVThiKhaBiuThngMinh />} />
          <Route path="timetable" element={<DchVThiKhaBiuThngMinh />} />
          <Route path="lchthinhgigv" element={<LchThinhGiGv />} />
        </Route>
        <Route
          path="/admin"
          element={
            <RequireAuth roles={['ROLE_ADMIN']}>
              <AdminLayout />
            </RequireAuth>
          }
        >
          <Route index element={<Navigate to="/admin/bocophntchanalytics" replace />} />
          <Route path="setupcuhnhgivngtrafficsplittingqueuecontrol" element={<SetupCuHnhGiVngTrafficSplittingQueueControl />} />
          <Route path="lichdangkyhockyconfig" element={<AdminHocKyScheduleConfigPage />} />
          <Route path="qunldanhmckhungmlpdatamaster" element={<QunLDanhMcKhungMLpDataMaster />} />
          <Route path="gimsttichnhktonadmin" element={<GimStTiChnhKTonAdmin />} />
          <Route path="bocophntchanalytics" element={<BoCoPhnTchAnalytics />} />
          <Route path="hthngphnquynatngrbacrolebasedaccesscontrol" element={<HThngPhnQuynaTngRbacRoleBasedAccessControl />} />
          <Route path="xcthcayutmfa2favchks" element={<XcThcaYuTMfa2FaVChKS />} />
          <Route path="lchsnhtkduchnaudittrailslogging" element={<LchSNhtKDuChnAuditTrailsLogging />} />
          <Route path="registration-windows" element={<AdminRegistrationWindowsPage />} />
          <Route path="pre-registration-demand" element={<AdminPreRegistrationDemandPage />} />
          <Route path="class-publish" element={<AdminClassPublishPage />} />
          <Route path="timetable-projection-tools" element={<AdminTimetableProjectionToolsPage />} />
          <Route path="registration-monitoring" element={<AdminRegistrationMonitoringPage />} />
        </Route>
        <Route
          path="/teacher"
          element={
            <RequireAuth roles={['ROLE_LECTURER']}>
              <TeacherLayout />
            </RequireAuth>
          }
        >
          <Route index element={<Navigate to="/teacher/qunllpgingdyimdanh" replace />} />
          <Route path="qunllpgingdyimdanh" element={<QunLLpGingDyimDanh />} />
          <Route path="mnglinhpqunlimgradingsystem" element={<MngLiNhpQunLimGradingSystem />} />
          <Route path="gcthixlkhiuniphckho" element={<GcThiXLKhiuNiPhcKho />} />
          <Route path="cnhtayphicvnhctpacademicadvising" element={<CnhTayPhiCVnHcTpAcademicAdvising />} />
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Router>
  );
}

export default App;
