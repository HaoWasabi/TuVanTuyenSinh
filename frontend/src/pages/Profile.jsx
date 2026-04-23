import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { User, KeyRound, Save, BadgeCheck, Contact, MapPin, CheckCircle2, Loader2, AlertCircle } from 'lucide-react';

const Profile = () => {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('info');

  // Quản lý trạng thái dữ liệu Form Hồ Sơ
  const [profileData, setProfileData] = useState({
    hoTen: user?.name || 'Nguyễn Văn An',
    cccd: user?.cccd || '001205000123',
    sbd: 'BKA0001',
    ngaySinh: '15/08/2008',
    gioiTinh: 'Nam',
    email: 'an.nguyen@gmail.com',
    sdt: '0333444555',
    tinhThanh: 'Hà Nội',
    khuVuc: 'KV1',
  });

  // Quản lý trạng thái dữ liệu Form Mật khẩu
  const [passwordData, setPasswordData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  });

  // Trạng thái UI (Đang lưu, Thông báo thành công/lỗi)
  const [isSaving, setIsSaving] = useState(false);
  const [message, setMessage] = useState({ type: '', text: '' });

  // Xử lý khi người dùng gõ vào ô input (Hồ sơ)
  const handleProfileChange = (e) => {
    const { name, value } = e.target;
    setProfileData(prev => ({ ...prev, [name]: value }));
  };

  // Xử lý khi người dùng gõ vào ô input (Mật khẩu)
  const handlePasswordChange = (e) => {
    const { name, value } = e.target;
    setPasswordData(prev => ({ ...prev, [name]: value }));
  };

  // Hàm mô phỏng việc lưu Hồ sơ lên Server
  const handleSaveProfile = (e) => {
    e.preventDefault();
    setIsSaving(true);
    setMessage({ type: '', text: '' });

    // Giả lập API call mất 1 giây
    setTimeout(() => {
      setIsSaving(false);
      setMessage({ type: 'success', text: 'Cập nhật hồ sơ cá nhân thành công!' });
      
      // Tự động ẩn thông báo sau 3 giây
      setTimeout(() => setMessage({ type: '', text: '' }), 3000);
    }, 1000);
  };

  // Hàm mô phỏng việc Đổi mật khẩu
  const handleSavePassword = (e) => {
    e.preventDefault();
    setMessage({ type: '', text: '' });

    // Validate cơ bản
    if (passwordData.newPassword !== passwordData.confirmPassword) {
      setMessage({ type: 'error', text: 'Mật khẩu xác nhận không khớp!' });
      return;
    }
    if (passwordData.newPassword.length < 6) {
      setMessage({ type: 'error', text: 'Mật khẩu mới phải có ít nhất 6 ký tự!' });
      return;
    }

    setIsSaving(true);
    // Giả lập API call mất 1 giây
    setTimeout(() => {
      setIsSaving(false);
      setMessage({ type: 'success', text: 'Đổi mật khẩu thành công!' });
      setPasswordData({ currentPassword: '', newPassword: '', confirmPassword: '' }); // Xóa trắng form
      
      setTimeout(() => setMessage({ type: '', text: '' }), 3000);
    }, 1000);
  };

  return (
    <div className="max-w-4xl mx-auto flex flex-col md:flex-row gap-6">
      
      {/* Sidebar Menu */}
      <div className="w-full md:w-1/4">
        <div className="bg-white rounded-2xl shadow-sm border p-4 flex flex-col gap-2">
          <button 
            onClick={() => { setActiveTab('info'); setMessage({ type: '', text: '' }); }}
            className={`flex items-center gap-3 p-3 rounded-xl text-left font-semibold transition ${activeTab === 'info' ? 'bg-blue-50 text-blue-700' : 'text-gray-600 hover:bg-gray-50'}`}
          >
            <User size={18} /> Hồ sơ cá nhân
          </button>
          <button 
            onClick={() => { setActiveTab('password'); setMessage({ type: '', text: '' }); }}
            className={`flex items-center gap-3 p-3 rounded-xl text-left font-semibold transition ${activeTab === 'password' ? 'bg-blue-50 text-blue-700' : 'text-gray-600 hover:bg-gray-50'}`}
          >
            <KeyRound size={18} /> Đổi mật khẩu
          </button>
        </div>
      </div>

      {/* Main Content Area */}
      <div className="w-full md:w-3/4 bg-white rounded-2xl shadow-sm border p-6 md:p-8 relative">
        
        {/* Khu vực hiển thị thông báo chung */}
        {message.text && (
          <div className={`mb-6 p-4 rounded-xl flex items-center gap-3 font-medium border ${
            message.type === 'success' ? 'bg-green-50 text-green-700 border-green-200' : 'bg-red-50 text-red-700 border-red-200'
          }`}>
            {message.type === 'success' ? <CheckCircle2 size={20} /> : <AlertCircle size={20} />}
            {message.text}
          </div>
        )}

        {/* TAB: THÔNG TIN CÁ NHÂN */}
        {activeTab === 'info' && (
          <div className="space-y-8">
            <div>
              <h2 className="text-2xl font-bold text-gray-800 mb-2">Hồ sơ Thí sinh</h2>
              <p className="text-sm text-gray-500">Thông tin được đồng bộ từ cơ sở dữ liệu tuyển sinh.</p>
            </div>

            <form onSubmit={handleSaveProfile} className="space-y-6">
              {/* PHẦN 1: THÔNG TIN ĐỊNH DANH (READ-ONLY) */}
              <div className="p-4 bg-gray-50 rounded-xl border border-gray-100">
                <div className="flex items-center gap-2 mb-4 text-blue-800 font-bold text-sm uppercase tracking-wider">
                  <BadgeCheck size={16} /> Thông tin định danh
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-xs font-bold text-gray-500 mb-1">SỐ CCCD (TÀI KHOẢN)</label>
                    <input type="text" disabled value={profileData.cccd} className="w-full px-4 py-2 rounded-lg border bg-white text-gray-400 cursor-not-allowed font-mono" />
                  </div>
                  <div>
                    <label className="block text-xs font-bold text-gray-500 mb-1">SỐ BÁO DANH (SBD)</label>
                    <input type="text" disabled value={profileData.sbd} className="w-full px-4 py-2 rounded-lg border bg-white text-gray-400 cursor-not-allowed font-mono" />
                  </div>
                </div>
              </div>

              {/* PHẦN 2: THÔNG TIN CÁ NHÂN & LIÊN LẠC */}
              <div>
                <div className="flex items-center gap-2 mb-4 text-gray-700 font-bold text-sm uppercase tracking-wider">
                  <Contact size={16} /> Thông tin cá nhân
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="md:col-span-2">
                    <label className="block text-sm font-semibold text-gray-600 mb-1">Họ và tên</label>
                    <input type="text" name="hoTen" value={profileData.hoTen} onChange={handleProfileChange} required className="w-full px-4 py-2 rounded-lg border focus:ring-2 focus:ring-blue-500 outline-none" />
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-gray-600 mb-1">Giới tính</label>
                    <select name="gioiTinh" value={profileData.gioiTinh} onChange={handleProfileChange} className="w-full px-4 py-2 rounded-lg border focus:ring-2 focus:ring-blue-500 outline-none">
                      <option value="Nam">Nam</option>
                      <option value="Nữ">Nữ</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-gray-600 mb-1">Ngày sinh</label>
                    <input type="text" name="ngaySinh" value={profileData.ngaySinh} onChange={handleProfileChange} required className="w-full px-4 py-2 rounded-lg border focus:ring-2 focus:ring-blue-500 outline-none" />
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-gray-600 mb-1">Số điện thoại</label>
                    <input type="text" name="sdt" value={profileData.sdt} onChange={handleProfileChange} required className="w-full px-4 py-2 rounded-lg border focus:ring-2 focus:ring-blue-500 outline-none" />
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-gray-600 mb-1">Email</label>
                    <input type="email" name="email" value={profileData.email} onChange={handleProfileChange} required className="w-full px-4 py-2 rounded-lg border focus:ring-2 focus:ring-blue-500 outline-none" />
                  </div>
                </div>
              </div>

              {/* PHẦN 3: ĐỊA BÀN XÉT TUYỂN (READ-ONLY) */}
              <div>
                <div className="flex items-center gap-2 mb-4 text-gray-700 font-bold text-sm uppercase tracking-wider">
                  <MapPin size={16} /> Địa bàn Xét tuyển
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-semibold text-gray-600 mb-1">Tỉnh/Thành phố</label>
                    <input type="text" disabled value={profileData.tinhThanh} className="w-full px-4 py-2 rounded-lg border bg-gray-50 text-gray-500 cursor-not-allowed" />
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-gray-600 mb-1">Khu vực ưu tiên</label>
                    <input type="text" disabled value={profileData.khuVuc} className="w-full px-4 py-2 rounded-lg border bg-gray-50 text-gray-500 cursor-not-allowed" />
                  </div>
                </div>
              </div>
              
              <div className="pt-4 border-t">
                <button type="submit" disabled={isSaving} className="flex items-center gap-2 bg-blue-600 text-white px-8 py-2.5 rounded-xl font-bold hover:bg-blue-700 transition-all shadow-lg shadow-blue-100 disabled:opacity-70 disabled:cursor-not-allowed">
                  {isSaving ? <Loader2 size={18} className="animate-spin" /> : <Save size={18} />}
                  {isSaving ? 'Đang lưu hệ thống...' : 'Lưu thay đổi hồ sơ'}
                </button>
              </div>
            </form>
          </div>
        )}

        {/* TAB: ĐỔI MẬT KHẨU */}
        {activeTab === 'password' && (
          <div>
            <div className="mb-8">
              <h2 className="text-2xl font-bold text-gray-800 mb-2">Bảo mật tài khoản</h2>
              <p className="text-sm text-gray-500">Thay đổi mật khẩu định kỳ để bảo vệ thông tin cá nhân</p>
            </div>
            <form onSubmit={handleSavePassword} className="max-w-md space-y-5">
              <div>
                <label className="block text-sm font-semibold text-gray-600 mb-1">Mật khẩu hiện tại</label>
                <input type="password" name="currentPassword" value={passwordData.currentPassword} onChange={handlePasswordChange} required placeholder="••••••••" className="w-full px-4 py-2 rounded-lg border focus:ring-2 focus:ring-blue-500 outline-none" />
              </div>
              <div className="border-t pt-5">
                <label className="block text-sm font-semibold text-gray-600 mb-1">Mật khẩu mới</label>
                <input type="password" name="newPassword" value={passwordData.newPassword} onChange={handlePasswordChange} required placeholder="••••••••" className="w-full px-4 py-2 rounded-lg border focus:ring-2 focus:ring-blue-500 outline-none" />
              </div>
              <div>
                <label className="block text-sm font-semibold text-gray-600 mb-1">Xác nhận mật khẩu mới</label>
                <input type="password" name="confirmPassword" value={passwordData.confirmPassword} onChange={handlePasswordChange} required placeholder="••••••••" className="w-full px-4 py-2 rounded-lg border focus:ring-2 focus:ring-blue-500 outline-none" />
              </div>
              
              <div className="pt-2">
                <button type="submit" disabled={isSaving} className="flex items-center justify-center gap-2 w-full md:w-auto bg-gray-800 text-white px-8 py-2.5 rounded-xl font-bold hover:bg-gray-900 transition-all disabled:opacity-70 disabled:cursor-not-allowed">
                  {isSaving ? <Loader2 size={18} className="animate-spin" /> : <KeyRound size={18} />}
                  {isSaving ? 'Đang cập nhật...' : 'Cập nhật mật khẩu'}
                </button>
              </div>
            </form>
          </div>
        )}

      </div>
    </div>
  );
};

export default Profile;