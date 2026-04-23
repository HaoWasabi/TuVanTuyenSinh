import React from 'react';
import { useAuth } from '../context/AuthContext';
import { Award, Target } from 'lucide-react';

const DiemThiCaNhan = () => {
  const { user } = useAuth();

  if (!user) return <div className="text-center py-10 text-red-600 font-bold">Vui lòng đăng nhập.</div>;

  // Mock Data dựa đúng vào bảng xt_diemthixettuyen
  const diemThi = { 
    'Toán (TO)': 8.50, 
    'Ngữ Văn (VA)': 7.00, 
    'Vật lý (LI)': 7.75, 
    'Hóa học (HO)': 8.00, 
    'Ngoại ngữ - Thi (N1_THI)': 8.00,
    'Sinh học (SI)': null,
    'Lịch sử (SU)': null,
    'Địa lý (DI)': null,
    'GD Kinh tế Pháp luật (KTPL)': null
  };

  // Mock Data dựa vào bảng xt_diemcongxetuyen
  const diemCong = { 
    diemUtxt: 0.50, // Tổng điểm ưu tiên đối tượng + khu vực
    diemNgoaiNguQuyDoi: 8.50 // Trường N1_CC trong SQL
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div className="bg-white p-6 rounded-2xl shadow-sm border flex justify-between items-center">
        <div>
          <h2 className="text-2xl font-bold text-blue-900">Kết quả Học tập & Tuyển sinh</h2>
          <p className="text-gray-500">Thí sinh: {user.name} | CCCD: {user.cccd}</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Khối Điểm thi */}
        <div className="bg-white p-6 rounded-2xl shadow-sm border">
          <div className="flex items-center gap-2 text-blue-800 font-bold text-lg mb-4 pb-2 border-b">
            <Target size={24} /> Điểm thi THPT Quốc Gia
          </div>
          <div className="space-y-3">
            {Object.entries(diemThi).map(([mon, diem]) => (
              diem !== null && (
                <div key={mon} className="flex justify-between items-center bg-gray-50 p-3 rounded-lg border">
                  <span className="font-semibold text-gray-700">{mon}</span>
                  <span className="font-bold text-blue-700 text-lg">{diem.toFixed(2)}</span>
                </div>
              )
            ))}
          </div>
        </div>

        {/* Khối Điểm ưu tiên quy đổi */}
        <div className="bg-white p-6 rounded-2xl shadow-sm border">
          <div className="flex items-center gap-2 text-orange-600 font-bold text-lg mb-4 pb-2 border-b">
            <Award size={24} /> Điểm Cộng & Quy Đổi
          </div>
          <div className="space-y-4">
            <div>
              <div className="text-sm text-gray-500 font-semibold mb-1">Điểm Ưu tiên (Khu vực / Đối tượng)</div>
              <div className="bg-orange-50 text-orange-700 font-bold px-4 py-3 rounded-lg border border-orange-100 flex justify-between">
                <span>Cộng thêm</span>
                <span>+{diemCong.diemUtxt.toFixed(2)} điểm</span>
              </div>
            </div>
            <div>
              <div className="text-sm text-gray-500 font-semibold mb-1">Ngoại Ngữ Quy đổi (N1_CC)</div>
              <div className="bg-green-50 text-green-700 font-bold px-4 py-3 rounded-lg border border-green-100 flex justify-between">
                <span>Mức điểm quy đổi</span>
                <span>{diemCong.diemNgoaiNguQuyDoi.toFixed(2)} điểm</span>
              </div>
              <p className="text-xs text-gray-400 mt-2 italic">*Hệ thống tự động lấy điểm cao nhất giữa Điểm thi ngoại ngữ và Điểm quy đổi chứng chỉ.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DiemThiCaNhan;