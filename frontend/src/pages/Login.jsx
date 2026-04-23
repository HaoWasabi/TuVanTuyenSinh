import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ShieldCheck, Loader2 } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

const Login = () => {
  // Cập nhật default CCCD khớp với dữ liệu mock của Nguyễn Văn An bên Profile
  const [cccd, setCccd] = useState('001205000123'); 
  const [password, setPassword] = useState('123456');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false); // State tạo hiệu ứng loading
  
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleLogin = (e) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    // Giả lập độ trễ khi gọi API xác thực tới Spring Boot (bảng users)
    setTimeout(() => {
      setIsLoading(false);
      
      // HARDCODE UI: Mật khẩu mặc định là 123456
      if (password === '123456' && cccd.length >= 9) {
        // Gọi hàm login từ Context để lưu trạng thái
        // Dữ liệu này mô phỏng kết quả trả về từ bảng users
        login({ 
          cccd: cccd,                  // Tương đương cột 'username'
          name: 'Nguyễn Văn An',       // Tương đương cột 'full_name'
          role: 'STUDENT'              // Quyền lấy từ bảng 'roles'
        });
        
        // Chuyển hướng về trang profile
        navigate('/profile');
      } else {
        setError('Sai Số CCCD hoặc mật khẩu (Mẹo: dùng pass 123456)');
      }
    }, 800); // Trễ 0.8s cho giống thực tế
  };

  return (
    <div className="max-w-md mx-auto mt-10">
      <div className="bg-white p-8 rounded-2xl shadow-xl border border-gray-100">
        <div className="text-center mb-8">
          <div className="bg-blue-100 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
            <ShieldCheck className="text-blue-600" size={32} />
          </div>
          <h2 className="text-2xl font-bold text-gray-800">Cổng Thí Sinh</h2>
          <p className="text-gray-500 text-sm mt-1">Sử dụng Số CCCD để đăng nhập hệ thống</p>
        </div>

        {error && (
          <div className="bg-red-50 text-red-600 p-3 rounded-lg text-sm mb-4 text-center font-medium border border-red-100">
            {error}
          </div>
        )}

        <form onSubmit={handleLogin} className="space-y-5">
          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-1">Số CCCD (Username)</label>
            <input 
              type="text" 
              className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none font-mono"
              value={cccd}
              onChange={(e) => setCccd(e.target.value)}
              required
            />
          </div>
          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-1">Mật khẩu</label>
            <input 
              type="password" 
              className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
          <button 
            type="submit"
            disabled={isLoading}
            className="w-full bg-blue-600 text-white py-3 rounded-xl font-bold hover:bg-blue-700 transition-all shadow-lg shadow-blue-200 flex justify-center items-center gap-2 disabled:opacity-70 disabled:cursor-not-allowed"
          >
            {isLoading && <Loader2 size={20} className="animate-spin" />}
            {isLoading ? 'Đang xác thực...' : 'Đăng nhập hệ thống'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default Login;