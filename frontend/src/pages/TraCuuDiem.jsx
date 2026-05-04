import React, { useState } from 'react';
import { Search, Loader2, CheckCircle2, XCircle, AlertCircle, User, ListOrdered } from 'lucide-react';
// Thay vì apiGetThiSinh, bạn nên gọi hàm mới gọi tới API /nguyenvong
import { apiTraCuuNguyenVong } from '../api/services';

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
      // Gọi API thực tế xuống Spring Boot (API .../nguyenvong)
      const response = await apiTraCuuNguyenVong(cccd, dob);

      // Kiểm tra nếu API trả về dữ liệu thí sinh thành công (dựa vào cccd trả về ở root)
      if (response && response.cccd) {
        const hoTenThiSinh = `${response.ho} ${response.ten}`;
        const danhSachNV = response.nguyenVongList || [];

        if (danhSachNV.length === 0) {
           setResult({ status: 'KHONG_CO_NV', data: { hoTen: hoTenThiSinh } });
           setIsLoading(false);
           return;
        }

        // Tìm nguyện vọng đầu tiên có kết quả là "Trúng tuyển" (Bỏ qua phân biệt hoa/thường)
        const nvTrungTuyen = danhSachNV.find(nv => 
          nv.nv_ketqua && nv.nv_ketqua.toLowerCase().includes('trúng tuyển')
        );

        if (nvTrungTuyen) {
          setResult({
            status: 'TRUNG_TUYEN',
            data: {
              hoTen: hoTenThiSinh,
              // API hiện tại chỉ trả về nv_manganh, tạm thời hiển thị mã ngành
              nganh: `Ngành (Mã: ${nvTrungTuyen.nv_manganh})`, 
              diem: nvTrungTuyen.diem_xettuyen,
              toHop: nvTrungTuyen.tt_thm,
              phuongThuc: nvTrungTuyen.tt_phuongthuc,
              tatCaNV: danhSachNV // Lưu lại để hiển thị bảng phụ
            }
          });
        } else {
          // Không có NV nào trúng tuyển -> Rớt hoặc đang chờ xét
          setResult({ 
            status: 'TRUOT', 
            data: { 
              hoTen: hoTenThiSinh,
              tatCaNV: danhSachNV 
            } 
          });
        }
      } else {
        // Trả về HTTP 200 nhưng không đúng định dạng dữ liệu
        setResult({ status: 'KHONG_TIM_THAY', data: null });
      }
    } catch (error) {
      // Bắt lỗi 404 hoặc lỗi backend
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
    <div className="max-w-4xl mx-auto pb-10">
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
                    <div className="flex flex-col md:flex-row md:items-center justify-between p-3.5 bg-white rounded-xl border border-green-100 shadow-sm">
                      <span className="text-gray-500 font-bold text-xs uppercase tracking-wider">Ngành trúng tuyển</span>
                      <span className="font-bold text-gray-900 text-right">{result.data.nganh}</span>
                    </div>
                    <div className="flex flex-col md:flex-row md:items-center justify-between p-3.5 bg-white rounded-xl border border-green-100 shadow-sm">
                      <span className="text-gray-500 font-bold text-xs uppercase tracking-wider">Điểm xét tuyển</span>
                      <span className="font-black text-blue-700 text-xl">{result.data.diem?.toFixed(2)}</span>
                    </div>
                    <div className="flex flex-col md:flex-row md:items-center justify-between p-3.5 bg-white rounded-xl border border-green-100 shadow-sm">
                      <span className="text-gray-500 font-bold text-xs uppercase tracking-wider">Tổ hợp môn</span>
                      <span className="font-bold text-gray-800 bg-gray-100 px-3 py-1 rounded-lg">{result.data.toHop}</span>
                    </div>
                    <div className="flex flex-col md:flex-row md:items-center justify-between p-3.5 bg-white rounded-xl border border-green-100 shadow-sm">
                      <span className="text-gray-500 font-bold text-xs uppercase tracking-wider">Phương thức</span>
                      <span className="font-bold text-gray-800">{result.data.phuongThuc}</span>
                    </div>
                  </div>
                </div>
              </div>
            )}

            {/* Kịch bản 2: Không trúng tuyển / Chờ xét */}
            {result.status === 'TRUOT' && (
              <div className="bg-red-50 p-6 md:p-8 rounded-3xl border border-red-200 shadow-sm flex items-start gap-4 mb-6">
                <XCircle size={40} className="text-red-500 shrink-0 mt-1" />
                <div>
                  <h3 className="font-bold text-xl text-red-800 mb-1">Không trúng tuyển / Đang chờ xét</h3>
                  <p className="text-red-700 font-medium mb-3 flex items-center gap-1.5"><User size={16}/> Thí sinh: {result.data.hoTen}</p>
                  <p className="text-red-600 text-sm leading-relaxed">Rất tiếc, bạn chưa trúng tuyển vào các nguyện vọng đã đăng ký ở đợt này (hoặc hồ sơ đang trong trạng thái "Chờ xét").</p>
                </div>
              </div>
            )}

            {/* Bảng liệt kê tất cả các nguyện vọng (Hiển thị cho cả Đậu và Rớt) */}
            {(result.status === 'TRUNG_TUYEN' || result.status === 'TRUOT') && result.data.tatCaNV && result.data.tatCaNV.length > 0 && (
              <div className="mt-8">
                <h4 className="font-bold text-gray-800 mb-4 flex items-center gap-2">
                  <ListOrdered size={20} className="text-blue-600"/> Danh sách Nguyện vọng đã đăng ký
                </h4>
                <div className="overflow-x-auto rounded-xl border border-gray-200">
                  <table className="w-full text-sm text-left text-gray-600">
                    <thead className="text-xs text-gray-700 uppercase bg-gray-50 border-b border-gray-200">
                      <tr>
                        <th className="px-4 py-3">NV</th>
                        <th className="px-4 py-3">Mã Ngành</th>
                        <th className="px-4 py-3">Phương thức</th>
                        <th className="px-4 py-3 text-center">Điểm XT</th>
                        <th className="px-4 py-3 text-right">Kết quả</th>
                      </tr>
                    </thead>
                    <tbody>
                      {result.data.tatCaNV.map((nv, idx) => (
                        <tr key={idx} className="bg-white border-b border-gray-100 hover:bg-gray-50 transition">
                          <td className="px-4 py-3 font-bold text-gray-900">{nv.nv_tt}</td>
                          <td className="px-4 py-3">{nv.nv_manganh}</td>
                          <td className="px-4 py-3">{nv.tt_phuongthuc} ({nv.tt_thm})</td>
                          <td className="px-4 py-3 text-center font-semibold text-blue-600">{nv.diem_xettuyen?.toFixed(2)}</td>
                          <td className="px-4 py-3 text-right">
                            <span className={`px-2.5 py-1 rounded-md text-xs font-bold ${
                              nv.nv_ketqua?.toLowerCase().includes('trúng') ? 'bg-green-100 text-green-700' : 
                              nv.nv_ketqua?.toLowerCase().includes('trượt') ? 'bg-red-100 text-red-700' : 'bg-yellow-100 text-yellow-700'
                            }`}>
                              {nv.nv_ketqua}
                            </span>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            )}

            {/* Kịch bản 3: Không có Nguyện vọng */}
            {result.status === 'KHONG_CO_NV' && (
              <div className="bg-yellow-50 p-6 md:p-8 rounded-3xl border border-yellow-200 shadow-sm flex items-start gap-4">
                <AlertCircle size={40} className="text-yellow-500 shrink-0 mt-1" />
                <div>
                  <h3 className="font-bold text-xl text-yellow-800 mb-2">Chưa đăng ký nguyện vọng</h3>
                  <p className="text-yellow-700 text-sm leading-relaxed">Hệ thống tìm thấy thí sinh <strong>{result.data.hoTen}</strong> nhưng bạn chưa đăng ký nguyện vọng xét tuyển nào.</p>
                </div>
              </div>
            )}

            {/* Kịch bản 4: Không tìm thấy thí sinh */}
            {result.status === 'KHONG_TIM_THAY' && (
              <div className="bg-gray-50 p-6 md:p-8 rounded-3xl border border-gray-200 shadow-sm flex items-start gap-4">
                <AlertCircle size={40} className="text-gray-400 shrink-0 mt-1" />
                <div>
                  <h3 className="font-bold text-xl text-gray-800 mb-2">Không tìm thấy dữ liệu</h3>
                  <p className="text-gray-600 text-sm leading-relaxed">Hệ thống không tìm thấy kết quả xét tuyển nào khớp với thông tin bạn cung cấp. Vui lòng kiểm tra lại <strong>Số CCCD</strong> và <strong>Ngày sinh (8 chữ số)</strong>.</p>
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