import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ShieldCheck, Loader2 } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { apiGetThiSinh } from '../api/services'; // Import API

const Login = () => {
  // Để trống mặc định để người dùng tự nhập
  const [cccd, setCccd] = useState(''); 
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false); 
  
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      // Gọi API xác thực bằng CCCD và Ngày sinh (đóng vai trò là password)
      const response = await apiGetThiSinh(cccd, password);

      // Kiểm tra nếu API trả về dữ liệu thành công
      if (response && response.thiSinh) {
        const ts = response.thiSinh;
        
        // Gọi hàm login từ Context để lưu trạng thái đăng nhập
        login({ 
          cccd: ts.cccd,
          password: password, // Lưu lại password (ngày sinh) để các trang khác gọi lại API nếu cần
          name: `${ts.ho} ${ts.ten}`, 
          role: 'STUDENT'
        });
        
        // Chuyển hướng về trang profile
        navigate('/profile');
      } else {
        // Fallback trong trường hợp API trả Http 200 nhưng không có object thiSinh
        setError('Tài khoản không tồn tại hoặc sai mật khẩu.');
      }
    } catch (err) {
      // Bắt lỗi từ Interceptors (VD: error: true, errorMessage: 'Không tìm thấy thí sinh...')
      setError(err.errorMessage || 'Sai Số CCCD hoặc Ngày sinh (8 chữ số).');
    } finally {
      setIsLoading(false);
    }
  };

  // Hàm tiện ích hỗ trợ format Ngày sinh nhập vào
  const handlePasswordChange = (e) => {
    // Chỉ cho phép nhập số, tối đa 8 ký tự
    const value = e.target.value.replace(/[^0-9]/g, '').slice(0, 8);
    setPassword(value);
  };

  return (
    <div className="max-w-md mx-auto mt-10">
      <div className="bg-white p-8 rounded-2xl shadow-xl border border-gray-100">
        <div className="text-center mb-8">
          <div className="bg-blue-100 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
            <ShieldCheck className="text-blue-600" size={32} />
          </div>
          <h2 className="text-2xl font-bold text-gray-800">Cổng Thí Sinh</h2>
          <p className="text-gray-500 text-sm mt-1">Đăng nhập để xem điểm và tra cứu xét tuyển</p>
        </div>

        {error && (
          <div className="bg-red-50 text-red-600 p-3 rounded-lg text-sm mb-4 text-center font-medium border border-red-100">
            {error}
          </div>
        )}

        <form onSubmit={handleLogin} className="space-y-5">
          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-1">Số Căn cước công dân</label>
            <input 
              type="text" 
              className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none font-mono"
              placeholder="Nhập 12 số CCCD"
              value={cccd}
              onChange={(e) => setCccd(e.target.value)}
              required
            />
          </div>
          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-1">Mật khẩu (Ngày sinh)</label>
            <input 
              type="password" 
              className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none font-mono tracking-widest"
              placeholder="VD: 01012007 (8 chữ số)"
              value={password}
              onChange={handlePasswordChange}
              required
            />
          </div>
          <button 
            type="submit"
            disabled={isLoading || password.length < 8}
            className="w-full bg-blue-600 text-white py-3.5 rounded-xl font-bold hover:bg-blue-700 transition-all shadow-lg shadow-blue-200 flex justify-center items-center gap-2 disabled:opacity-70 disabled:cursor-not-allowed mt-2"
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