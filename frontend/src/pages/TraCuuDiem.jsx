import React, { useState } from 'react';
import { Search, Loader2, CheckCircle2, XCircle, AlertCircle } from 'lucide-react';

const TraCuuDiem = () => {
  const [cccd, setCccd] = useState('');
  const [dob, setDob] = useState('');
  const [result, setResult] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  const handleSearch = (e) => {
    e.preventDefault();
    if (!cccd.trim() || !dob.trim()) return;
    
    setIsLoading(true);
    setResult(null);

    // Giả lập API kiểm tra kết quả xét tuyển
    setTimeout(() => {
      setIsLoading(false);
      
      // HARDCODE MÔ PHỎNG 3 KỊCH BẢN:
      // Kịch bản 1: Trúng tuyển
      if (cccd === '001205000123' && dob === '15082008') {
        setResult({
          status: 'TRUNG_TUYEN',
          data: {
            nganh: 'Công nghệ thông tin (7480201)',
            diem: 26.50,
            toHop: 'A00 (Toán, Lý, Hóa)',
            phuongThuc: 'Xét điểm thi THPT Quốc gia'
          }
        });
      } 
      // Kịch bản 2: Không trúng tuyển
      else if (cccd === '001205000124' && dob === '16082008') {
        setResult({ status: 'TRUOT', data: null });
      } 
      // Kịch bản 3: Không tìm thấy
      else {
        setResult({ status: 'KHONG_TIM_THAY', data: null });
      }
    }, 800);
  };

  return (
    <div className="max-w-3xl mx-auto">
      <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-100 text-center relative overflow-hidden">
        {/* BG trang trí */}
        <div className="absolute top-0 left-0 w-full h-2 bg-gradient-to-r from-blue-500 to-blue-700"></div>

        <h2 className="text-2xl font-bold text-blue-900 mb-2 mt-2">Tra Cứu Kết Quả Xét Tuyển</h2>
        <p className="text-gray-500 mb-8">Vui lòng nhập chính xác Số CCCD và 08 chữ số ngày sinh</p>

        <form onSubmit={handleSearch} className="max-w-md mx-auto mb-10 space-y-4">
          <div className="text-left">
            <label className="block text-sm font-bold text-gray-700 mb-1">Số Căn cước công dân</label>
            <input 
              type="text" 
              placeholder="Nhập số CCCD..." 
              className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none font-mono"
              value={cccd}
              onChange={(e) => setCccd(e.target.value)}
              required
            />
          </div>
          <div className="text-left">
            <label className="block text-sm font-bold text-gray-700 mb-1">Ngày tháng năm sinh (8 chữ số)</label>
            <input 
              type="password" 
              placeholder="VD: 15082008" 
              maxLength={8}
              className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none font-mono"
              value={dob}
              onChange={(e) => setDob(e.target.value)}
              required
            />
          </div>
          <button type="submit" disabled={isLoading} className="w-full bg-blue-600 text-white px-6 py-3.5 rounded-xl font-bold hover:bg-blue-700 flex justify-center items-center gap-2 transition-all disabled:opacity-70 mt-2">
            {isLoading ? <Loader2 size={18} className="animate-spin" /> : <Search size={18} />} 
            Tra cứu kết quả
          </button>
        </form>

        {/* HIỂN THỊ KẾT QUẢ DỰA TRÊN TRẠNG THÁI */}
        {result && (
          <div className="animate-in fade-in slide-in-from-bottom-4 text-left">
            
            {/* Kịch bản 1: Trúng tuyển */}
            {result.status === 'TRUNG_TUYEN' && (
              <div className="bg-green-50 p-6 rounded-2xl border border-green-200 shadow-sm">
                <div className="flex items-center gap-3 mb-4 pb-4 border-b border-green-200">
                  <CheckCircle2 size={28} className="text-green-600" />
                  <h3 className="font-bold text-xl text-green-800">Chúc mừng bạn đã trúng tuyển!</h3>
                </div>
                <div className="space-y-3">
                  <div className="flex flex-col md:flex-row md:justify-between p-3 bg-white rounded-lg border border-green-100">
                    <span className="text-gray-500 font-semibold text-sm uppercase">Ngành trúng tuyển</span>
                    <span className="font-bold text-gray-800">{result.data.nganh}</span>
                  </div>
                  <div className="flex flex-col md:flex-row md:justify-between p-3 bg-white rounded-lg border border-green-100">
                    <span className="text-gray-500 font-semibold text-sm uppercase">Điểm xét tuyển</span>
                    <span className="font-bold text-blue-700 text-lg">{result.data.diem.toFixed(2)}</span>
                  </div>
                  <div className="flex flex-col md:flex-row md:justify-between p-3 bg-white rounded-lg border border-green-100">
                    <span className="text-gray-500 font-semibold text-sm uppercase">Tổ hợp xét tuyển</span>
                    <span className="font-semibold text-gray-800">{result.data.toHop}</span>
                  </div>
                  <div className="flex flex-col md:flex-row md:justify-between p-3 bg-white rounded-lg border border-green-100">
                    <span className="text-gray-500 font-semibold text-sm uppercase">Phương thức</span>
                    <span className="font-semibold text-gray-800">{result.data.phuongThuc}</span>
                  </div>
                </div>
              </div>
            )}

            {/* Kịch bản 2: Không trúng tuyển */}
            {result.status === 'TRUOT' && (
              <div className="bg-red-50 p-6 rounded-2xl border border-red-200 shadow-sm flex items-center gap-4">
                <XCircle size={36} className="text-red-500 shrink-0" />
                <div>
                  <h3 className="font-bold text-lg text-red-800">Rất tiếc, bạn không trúng tuyển.</h3>
                  <p className="text-red-600 text-sm mt-1">Chúc bạn may mắn ở các đợt xét tuyển bổ sung hoặc nguyện vọng khác.</p>
                </div>
              </div>
            )}

            {/* Kịch bản 3: Không tìm thấy */}
            {result.status === 'KHONG_TIM_THAY' && (
              <div className="bg-gray-50 p-6 rounded-2xl border border-gray-200 shadow-sm flex items-center gap-4">
                <AlertCircle size={36} className="text-gray-400 shrink-0" />
                <div>
                  <h3 className="font-bold text-lg text-gray-700">Không tìm thấy dữ liệu.</h3>
                  <p className="text-gray-500 text-sm mt-1">Vui lòng kiểm tra lại Số CCCD và Ngày sinh (phải đủ 8 chữ số, VD: 01012006).</p>
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