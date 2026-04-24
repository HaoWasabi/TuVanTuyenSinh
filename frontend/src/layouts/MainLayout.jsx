import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { User, BookOpen, LogOut, ChevronDown } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

const MainLayout = ({ children }) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col font-sans">
      {/* Header */}
      <header className="bg-white shadow-sm border-b sticky top-0 z-50">
        <div className="container mx-auto px-4 h-16 flex justify-between items-center">
          <Link to="/" className="flex items-center gap-2">
            <div className="bg-blue-600 p-1.5 rounded-lg text-white">
              <BookOpen size={24} />
            </div>
            <span className="text-xl font-bold text-blue-900 tracking-tight">TVTS <span className="text-blue-500">2026</span></span>
          </Link>

          {/* Menu Điều hướng - Đã được cập nhật thêm các tính năng mới */}
          <nav className="hidden md:flex items-center gap-8 text-sm font-medium text-gray-600">
            <Link to="/" className="hover:text-blue-600">Trang chủ</Link>
            
            {/* Phân vùng Public (Ai cũng xem được) */}
            <Link to="/tra-cuu-nganh" className="hover:text-blue-600">Tra cứu Ngành</Link>
            <Link to="/tra-cuu-diem" className="hover:text-blue-600">Tra cứu Điểm</Link>
            <Link to="/bang-quy-doi" className="hover:text-blue-600">Bảng quy đổi</Link>
            <Link to="/cong-cu-tinh-diem" className="hover:text-blue-600">Công cụ tính điểm</Link>
            {/* Phân vùng Private (Chỉ hiện menu này khi đã đăng nhập) */}
            {user && (
              <>
                <Link to="/diem-cua-toi" className="hover:text-blue-600 text-blue-700 font-bold">Điểm cá nhân</Link>
                <Link to="/nguyen-vong" className="hover:text-blue-600 text-blue-700 font-bold">Nguyện vọng</Link>
                <Link to="/profile" className="hover:text-blue-600 text-blue-700 font-bold">Hồ sơ</Link>
              </>
            )}
          </nav>

          <div className="flex items-center gap-4">
            {!user ? (
              <Link to="/login" className="flex items-center gap-2 text-sm font-semibold text-gray-700 bg-gray-100 px-4 py-2 rounded-full hover:bg-gray-200">
                <User size={18} /> Đăng nhập CCCD
              </Link>
            ) : (
              <div className="flex items-center gap-4">
                <div className="flex items-center gap-2 text-sm font-semibold text-blue-800 bg-blue-50 px-4 py-2 rounded-full border border-blue-100">
                  <User size={18} /> {user.name} 
                </div>
                <button onClick={handleLogout} className="text-gray-500 hover:text-red-500 transition" title="Đăng xuất">
                  <LogOut size={20} />
                </button>
              </div>
            )}
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="flex-grow container mx-auto px-4 py-6">
        {children}
      </main>

      {/* Footer */}
      <footer className="bg-blue-900 text-white py-10 mt-10">
        <div className="container mx-auto px-4 text-center">
          <p className="text-blue-300 text-sm">Hệ thống Tư vấn Tuyển sinh Web_MHPL</p>
        </div>
      </footer>
    </div>
  );
};

export default MainLayout;