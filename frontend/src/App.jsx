import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import { Search, GraduationCap, FileText, Calculator, ArrowRight, CalendarDays, CheckCircle2 } from 'lucide-react';

import MainLayout from './layouts/MainLayout';
import Login from './pages/Login';
import TraCuuNganh from './pages/TraCuuNganh';
import Profile from './pages/Profile';
import NguyenVong from './pages/NguyenVong';
import BangQuyDoi from './pages/BangQuyDoi';
import TraCuuDiem from './pages/TraCuuDiem';
import DiemThiCaNhan from './pages/DiemThiCaNhan';
import CongCuTinhDiem from './pages/CongCuTinhDiem';

// Trang chủ được thiết kế lại
const Home = () => (
  <div className="py-6">
    {/* Hero Section */}
    <div className="relative bg-gradient-to-br from-blue-700 via-blue-800 to-blue-900 rounded-3xl p-10 md:p-16 text-white mb-12 shadow-2xl overflow-hidden">
      {/* Background Pattern mờ */}
      <div className="absolute top-0 right-0 -mt-10 -mr-10 opacity-10">
        <GraduationCap size={300} />
      </div>
      
      <div className="relative z-10 max-w-3xl">
        <div className="inline-block px-4 py-1.5 bg-blue-600/50 backdrop-blur-sm rounded-full text-blue-100 text-sm font-semibold mb-6 border border-blue-500/30">
          Kỳ tuyển sinh đại học chính quy 2026
        </div>
        <h1 className="text-4xl md:text-5xl font-extrabold mb-6 leading-tight">
          Cổng Thông Tin <br className="hidden md:block"/> 
          <span className="text-yellow-400">Tuyển Sinh Đào Tạo</span>
        </h1>
        <p className="text-blue-100 text-lg mb-10 leading-relaxed max-w-2xl">
          Nền tảng hỗ trợ thí sinh tra cứu điểm thi, tìm hiểu thông tin ngành nghề, bảng quy đổi điểm và quản lý nguyện vọng xét tuyển trực tuyến một cách nhanh chóng, minh bạch.
        </p>
        <div className="flex flex-wrap gap-4">
          <Link to="/tra-cuu-diem" className="bg-white text-blue-800 px-8 py-3.5 rounded-xl font-bold hover:bg-gray-50 hover:shadow-lg transition flex items-center gap-2">
            <Search size={20} /> Tra cứu điểm ngay
          </Link>
          <Link to="/login" className="bg-blue-600/30 border border-blue-400/50 backdrop-blur-sm text-white px-8 py-3.5 rounded-xl font-bold hover:bg-blue-600/50 transition flex items-center gap-2">
            Đăng nhập hệ thống <ArrowRight size={20} />
          </Link>
        </div>
      </div>
    </div>
    
    {/* Quick Access Cards */}
    <div className="mb-12">
      <h2 className="text-2xl font-bold text-gray-800 mb-6 px-2">Tiện ích Tra cứu & Đăng ký</h2>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {/* Card 1 */}
        <Link to="/tra-cuu-nganh" className="group bg-white p-6 rounded-2xl shadow-sm border border-gray-100 hover:shadow-xl hover:border-blue-200 transition-all flex flex-col h-full">
          <div className="bg-blue-50 w-14 h-14 rounded-xl flex items-center justify-center text-blue-600 mb-5 group-hover:scale-110 transition-transform">
            <GraduationCap size={28} />
          </div>
          <h3 className="font-bold text-gray-800 mb-2 text-lg group-hover:text-blue-700">Ngành & Tổ hợp</h3>
          <p className="text-gray-500 text-sm flex-grow">Tra cứu mã ngành, tên ngành và các tổ hợp môn xét tuyển năm 2026.</p>
        </Link>

        {/* Card 2 */}
        <Link to="/tra-cuu-diem" className="group bg-white p-6 rounded-2xl shadow-sm border border-gray-100 hover:shadow-xl hover:border-green-200 transition-all flex flex-col h-full">
          <div className="bg-green-50 w-14 h-14 rounded-xl flex items-center justify-center text-green-600 mb-5 group-hover:scale-110 transition-transform">
            <Search size={28} />
          </div>
          <h3 className="font-bold text-gray-800 mb-2 text-lg group-hover:text-green-700">Tra cứu Điểm thi</h3>
          <p className="text-gray-500 text-sm flex-grow">Xem điểm thi THPT quốc gia nhanh chóng qua Số báo danh hoặc CCCD.</p>
        </Link>

        {/* Card 3 */}
        <Link to="/bang-quy-doi" className="group bg-white p-6 rounded-2xl shadow-sm border border-gray-100 hover:shadow-xl hover:border-orange-200 transition-all flex flex-col h-full">
          <div className="bg-orange-50 w-14 h-14 rounded-xl flex items-center justify-center text-orange-600 mb-5 group-hover:scale-110 transition-transform">
            <Calculator size={28} />
          </div>
          <h3 className="font-bold text-gray-800 mb-2 text-lg group-hover:text-orange-700">Bảng Quy đổi</h3>
          <p className="text-gray-500 text-sm flex-grow">Xem chi tiết quy định quy đổi điểm cho các chứng chỉ ngoại ngữ (IELTS, TOEFL...).</p>
        </Link>

        {/* Card 4 */}
        <Link to="/nguyen-vong" className="group bg-white p-6 rounded-2xl shadow-sm border border-gray-100 hover:shadow-xl hover:border-purple-200 transition-all flex flex-col h-full">
          <div className="bg-purple-50 w-14 h-14 rounded-xl flex items-center justify-center text-purple-600 mb-5 group-hover:scale-110 transition-transform">
            <FileText size={28} />
          </div>
          <h3 className="font-bold text-gray-800 mb-2 text-lg group-hover:text-purple-700">Đăng ký Nguyện vọng</h3>
          <p className="text-gray-500 text-sm flex-grow">Thêm, sửa, xóa và sắp xếp thứ tự các nguyện vọng xét tuyển đại học.</p>
        </Link>
      </div>
    </div>

    {/* Information & Timeline Section */}
    <div className="bg-white rounded-2xl shadow-sm border p-8">
      <div className="flex items-center gap-3 mb-6">
        <CalendarDays className="text-blue-600" size={28} />
        <h2 className="text-2xl font-bold text-gray-800">Mốc thời gian quan trọng</h2>
      </div>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="border-l-4 border-blue-500 pl-4 py-1">
          <div className="text-sm font-bold text-blue-600 mb-1">Tháng 4 - 2026</div>
          <div className="font-semibold text-gray-800">Đăng ký dự thi THPT</div>
          <p className="text-sm text-gray-500 mt-1">Thí sinh nộp hồ sơ và đăng ký các môn thi THPT Quốc gia.</p>
        </div>
        <div className="border-l-4 border-yellow-400 pl-4 py-1">
          <div className="text-sm font-bold text-yellow-600 mb-1">Tháng 7 - 2026</div>
          <div className="font-semibold text-gray-800">Công bố điểm thi</div>
          <p className="text-sm text-gray-500 mt-1">Tra cứu điểm thi và điểm quy đổi chứng chỉ trên hệ thống.</p>
        </div>
        <div className="border-l-4 border-gray-200 pl-4 py-1 opacity-60">
          <div className="text-sm font-bold text-gray-500 mb-1">Tháng 8 - 2026</div>
          <div className="font-semibold text-gray-800">Đăng ký Nguyện vọng</div>
          <p className="text-sm text-gray-500 mt-1">Mở cổng đăng ký và điều chỉnh nguyện vọng trực tuyến.</p>
        </div>
      </div>
    </div>
  </div>
);

function App() {
  return (
    <Router>
      <MainLayout>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/tra-cuu-nganh" element={<TraCuuNganh />} />
          <Route path="/nguyen-vong" element={<NguyenVong />} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/bang-quy-doi" element={<BangQuyDoi />} />
          <Route path="/tra-cuu-diem" element={<TraCuuDiem />} />
          <Route path="/diem-cua-toi" element={<DiemThiCaNhan />} />
          <Route path="/cong-cu-tinh-diem" element={<CongCuTinhDiem />} />
          <Route path="*" element={<div className="p-10 text-center text-gray-500 font-bold text-xl">404 - Trang không tồn tại</div>} />
        </Routes>
      </MainLayout>
    </Router>
  );
}

export default App;