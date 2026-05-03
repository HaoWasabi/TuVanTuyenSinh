import React, { useState } from 'react';
import { Search, Loader2, CheckCircle2, XCircle, AlertCircle, User } from 'lucide-react';
// Import API từ file services
import { apiGetThiSinh } from '../api/services';

const TraCuuDiem = () => {
  const [cccd, setCccd] = useState('');
  const [dob, setDob] = useState('');
  const [result, setResult] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!cccd.trim() || !dob.trim()) return;
    
    setIsLoading(true);
    setResult(null);

    try {
      // Gọi API thực tế xuống Spring Boot
      const response = await apiGetThiSinh(cccd, dob);

      // Kiểm tra nếu API trả về dữ liệu thí sinh thành công
      if (response && response.thiSinh) {
        const ts = response.thiSinh;
        const hoTenThiSinh = `${ts.ho} ${ts.ten}`;
        
        // Lấy phương thức xét tuyển từ danh sách điểm thi (nếu có)
        const diemThi = response.diemThiList && response.diemThiList.length > 0 ? response.diemThiList[0] : null;
        const phuongThuc = diemThi ? diemThi.dphuongthuc : 'Xét điểm thi THPT Quốc gia';

        // TẠM THỜI MÔ PHỎNG KẾT QUẢ XÉT TUYỂN 
        // (Do API hiện tại chỉ trả về điểm thi, chưa có bảng kết quả trúng tuyển)
        // Mẹo test: CCCD số cuối là lẻ -> Đậu | CCCD số cuối là chẵn -> Rớt
        const lastDigit = parseInt(cccd.slice(-1));
        const isPass = isNaN(lastDigit) ? true : lastDigit % 2 !== 0;

        if (isPass) {
          setResult({
            status: 'TRUNG_TUYEN',
            data: {
              hoTen: hoTenThiSinh,
              nganh: 'Công nghệ thông tin',
              maNganh: '7480201',
              diem: 26.50,
              toHop: 'A00',
              phuongThuc: phuongThuc
            }
          });
        } else {
          setResult({ 
            status: 'TRUOT', 
            data: { hoTen: hoTenThiSinh } 
          });
        }
      } else {
        // Trả về HTTP 200 nhưng không có object thiSinh
        setResult({ status: 'KHONG_TIM_THAY', data: null });
      }
    } catch (error) {
      // Bắt lỗi từ Interceptors (VD: error: true, message: Không tìm thấy thí sinh...)
      setResult({ status: 'KHONG_TIM_THAY', data: null });
    } finally {
      setIsLoading(false);
    }
  };

  // Chỉ cho phép nhập số vào ô Mật khẩu (Ngày sinh)
  const handleDobChange = (e) => {
    const value = e.target.value.replace(/[^0-9]/g, '').slice(0, 8);
    setDob(value);
  };

  return (
    <div className="max-w-3xl mx-auto pb-10">
      <div className="bg-white p-6 md:p-8 rounded-3xl shadow-sm border border-gray-100 text-center relative overflow-hidden">
        {/* BG trang trí */}
        <div className="absolute top-0 left-0 w-full h-2 bg-gradient-to-r from-blue-600 to-indigo-600"></div>

        <h2 className="text-2xl font-bold text-blue-900 mb-2 mt-2">Tra Cứu Kết Quả Xét Tuyển</h2>
        <p className="text-gray-500 mb-8 text-sm">Vui lòng nhập chính xác Số CCCD và 08 chữ số ngày sinh</p>

        <form onSubmit={handleSearch} className="max-w-md mx-auto mb-10 space-y-5">
          <div className="text-left">
            <label className="block text-sm font-bold text-gray-700 mb-1.5">Số Căn cước công dân (Username)</label>
            <input 
              type="text" 
              placeholder="Nhập 12 số CCCD..." 
              className="w-full px-4 py-3.5 rounded-xl border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none font-mono text-gray-800 transition"
              value={cccd}
              onChange={(e) => setCccd(e.target.value)}
              required
            />
          </div>
          <div className="text-left">
            <label className="block text-sm font-bold text-gray-700 mb-1.5">Mật khẩu (DDMMYYYY)</label>
            <input 
              type="password" 
              placeholder="VD: 15082006 (8 chữ số)" 
              maxLength={8}
              className="w-full px-4 py-3.5 rounded-xl border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none font-mono tracking-widest text-gray-800 transition"
              value={dob}
              onChange={handleDobChange}
              required
            />
          </div>
          <button 
            type="submit" 
            disabled={isLoading || dob.length < 8} 
            className="w-full bg-blue-600 text-white px-6 py-4 rounded-xl font-bold text-lg hover:bg-blue-700 shadow-lg shadow-blue-600/20 flex justify-center items-center gap-2 transition-all active:scale-[0.98] disabled:opacity-70 disabled:cursor-not-allowed mt-2"
          >
            {isLoading ? <Loader2 size={20} className="animate-spin" /> : <Search size={20} />} 
            {isLoading ? 'Đang tra cứu dữ liệu...' : 'Tra cứu kết quả'}
          </button>
        </form>

        {/* HIỂN THỊ KẾT QUẢ DỰA TRÊN TRẠNG THÁI */}
        {result && (
          <div className="animate-in fade-in slide-in-from-bottom-4 text-left border-t border-gray-100 pt-8">
            
            {/* Kịch bản 1: Trúng tuyển */}
            {result.status === 'TRUNG_TUYEN' && (
              <div className="bg-green-50 p-6 md:p-8 rounded-3xl border border-green-200 shadow-sm relative overflow-hidden">
                <div className="absolute top-0 right-0 p-4 opacity-10"><CheckCircle2 size={100} className="text-green-600" /></div>
                
                <div className="relative z-10">
                  <div className="flex items-center gap-3 mb-6 pb-5 border-b border-green-200/60">
                    <CheckCircle2 size={32} className="text-green-600" />
                    <div>
                      <h3 className="font-bold text-xl text-green-800">Chúc mừng bạn đã trúng tuyển!</h3>
                      <p className="text-green-700 font-medium mt-1 flex items-center gap-1.5"><User size={16}/> Thí sinh: {result.data.hoTen}</p>
                    </div>
                  </div>
                  
                  <div className="space-y-3">
                    <div className="flex flex-col md:flex-row md:items-center justify-between p-3.5 bg-white rounded-xl border border-green-100">
                      <span className="text-gray-500 font-bold text-xs uppercase tracking-wider">Ngành trúng tuyển</span>
                      <span className="font-bold text-gray-900 text-right">{result.data.nganh} ({result.data.maNganh})</span>
                    </div>
                    <div className="flex flex-col md:flex-row md:items-center justify-between p-3.5 bg-white rounded-xl border border-green-100">
                      <span className="text-gray-500 font-bold text-xs uppercase tracking-wider">Điểm xét tuyển</span>
                      <span className="font-black text-blue-700 text-xl">{result.data.diem.toFixed(2)}</span>
                    </div>
                    <div className="flex flex-col md:flex-row md:items-center justify-between p-3.5 bg-white rounded-xl border border-green-100">
                      <span className="text-gray-500 font-bold text-xs uppercase tracking-wider">Tổ hợp môn</span>
                      <span className="font-bold text-gray-800 bg-gray-100 px-3 py-1 rounded-lg">{result.data.toHop}</span>
                    </div>
                    <div className="flex flex-col md:flex-row md:items-center justify-between p-3.5 bg-white rounded-xl border border-green-100">
                      <span className="text-gray-500 font-bold text-xs uppercase tracking-wider">Phương thức</span>
                      <span className="font-bold text-gray-800">{result.data.phuongThuc}</span>
                    </div>
                  </div>
                </div>
              </div>
            )}

            {/* Kịch bản 2: Không trúng tuyển */}
            {result.status === 'TRUOT' && (
              <div className="bg-red-50 p-6 md:p-8 rounded-3xl border border-red-200 shadow-sm flex items-start gap-4">
                <XCircle size={40} className="text-red-500 shrink-0 mt-1" />
                <div>
                  <h3 className="font-bold text-xl text-red-800 mb-1">Không trúng tuyển</h3>
                  <p className="text-red-700 font-medium mb-3 flex items-center gap-1.5"><User size={16}/> Thí sinh: {result.data.hoTen}</p>
                  <p className="text-red-600 text-sm leading-relaxed">Rất tiếc, điểm xét tuyển của bạn chưa đạt mức điểm chuẩn của các nguyện vọng đã đăng ký. Chúc bạn may mắn ở các đợt xét tuyển bổ sung hoặc lựa chọn nguyện vọng khác.</p>
                </div>
              </div>
            )}

            {/* Kịch bản 3: Không tìm thấy */}
            {result.status === 'KHONG_TIM_THAY' && (
              <div className="bg-gray-50 p-6 md:p-8 rounded-3xl border border-gray-200 shadow-sm flex items-start gap-4">
                <AlertCircle size={40} className="text-gray-400 shrink-0 mt-1" />
                <div>
                  <h3 className="font-bold text-xl text-gray-800 mb-2">Không tìm thấy dữ liệu</h3>
                  <p className="text-gray-600 text-sm leading-relaxed">Hệ thống không tìm thấy kết quả xét tuyển nào khớp với thông tin bạn cung cấp. Vui lòng kiểm tra lại <strong>Số CCCD</strong> và <strong>Ngày sinh (8 chữ số)</strong>, hoặc hồ sơ của bạn chưa được cập nhật lên hệ thống.</p>
                </div>
              </div>
            )}

          </div>
        )}
      </div>
    </div>
  );
};

export default TraCuuDiem; 