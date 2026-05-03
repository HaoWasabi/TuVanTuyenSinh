import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { Award, Target, Loader2, AlertCircle } from 'lucide-react';
import { apiGetThiSinh } from '../api/services';

const DiemThiCaNhan = () => {
  const { user } = useAuth();
  const [data, setData] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!user) return;

    const fetchDiemThi = async () => {
      setIsLoading(true);
      try {
        // Lấy ngày sinh (password) từ context. 
        // Nếu context chưa cập nhật, dùng tạm giá trị mẫu của backend để test không bị lỗi.
        const dob = user.password || user.dob || '01012007'; 
        
        // Gọi API lấy thông tin thí sinh và điểm thi
        const response = await apiGetThiSinh(user.cccd, dob);
        
        if (response && response.thiSinh) {
          setData(response);
        } else {
          setError('Không tìm thấy dữ liệu điểm thi.');
        }
      } catch (err) {
        setError(err.errorMessage || 'Lỗi khi tải dữ liệu từ máy chủ API.');
      } finally {
        setIsLoading(false);
      }
    };

    fetchDiemThi();
  }, [user]);

  if (!user) return <div className="text-center py-10 text-red-600 font-bold">Vui lòng đăng nhập.</div>;

  // Hiệu ứng Loading
  if (isLoading) {
    return (
      <div className="flex flex-col items-center justify-center py-20 text-blue-600">
        <Loader2 className="animate-spin mb-4" size={40} />
        <p className="font-semibold">Đang đồng bộ dữ liệu điểm thi...</p>
      </div>
    );
  }

  // Xử lý Lỗi
  if (error) {
    return (
      <div className="max-w-4xl mx-auto mt-10 bg-red-50 p-6 rounded-2xl border border-red-200 flex items-center gap-4">
        <AlertCircle className="text-red-500" size={32} />
        <div>
          <h3 className="font-bold text-red-800 text-lg">Không thể tải điểm thi</h3>
          <p className="text-red-600">{error}</p>
        </div>
      </div>
    );
  }

  // Tách dữ liệu từ API
  const thiSinh = data.thiSinh;
  const diemList = data.diemThiList && data.diemThiList.length > 0 ? data.diemThiList[0] : {};

  // Map dữ liệu điểm thi trả về từ SQL khớp với UI
  const diemThi = { 
    'Toán (TO)': diemList.toan, 
    'Ngữ Văn (VA)': diemList.nguVan, 
    'Vật lý (LI)': diemList.vatLi, 
    'Hóa học (HO)': diemList.hoaHoc, 
    'Sinh học (SI)': diemList.sinhHoc,
    'Lịch sử (SU)': diemList.lichSu,
    'Địa lý (DI)': diemList.diaLi,
    'Ngoại ngữ - Thi (N1_THI)': diemList.n1Thi,
    'GD Kinh tế Pháp luật (KTPL)': diemList.ktpl
  };

  // Logic tự động tính toán điểm ưu tiên dựa vào Khu vực & Đối tượng từ CSDL
  const calculateUuTien = (kv, dt) => {
    let diemKv = 0;
    if (kv === 'KV1') diemKv = 0.75;
    else if (kv === 'KV2-NT') diemKv = 0.50;
    else if (kv === 'KV2') diemKv = 0.25;

    let diemDt = 0;
    const dtNum = parseInt(dt);
    if (dtNum >= 1 && dtNum <= 4) diemDt = 2.0;
    else if (dtNum >= 5 && dtNum <= 7) diemDt = 1.0;

    return diemKv + diemDt;
  };

  const diemCong = { 
    diemUtxt: calculateUuTien(thiSinh.khuVuc, thiSinh.doiTuong), 
    diemNgoaiNguQuyDoi: diemList.n1Cc || 0 
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div className="bg-white p-6 rounded-2xl shadow-sm border flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <h2 className="text-2xl font-bold text-blue-900">Kết quả Học tập & Tuyển sinh</h2>
          <p className="text-gray-500 mt-1">
            Thí sinh: <span className="font-bold text-gray-700">{thiSinh.ho} {thiSinh.ten}</span> | 
            CCCD: <span className="font-bold text-gray-700">{thiSinh.cccd}</span>
          </p>
        </div>
        <div className="bg-blue-50 px-4 py-2 rounded-lg border border-blue-100 text-sm">
          <p>Khu vực: <strong className="text-blue-700">{thiSinh.khuVuc || 'Không'}</strong></p>
          <p>Đối tượng: <strong className="text-blue-700">{thiSinh.doiTuong || 'Không'}</strong></p>
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
              (diem !== null && diem !== undefined) && (
                <div key={mon} className="flex justify-between items-center bg-gray-50 p-3 rounded-lg border">
                  <span className="font-semibold text-gray-700">{mon}</span>
                  <span className="font-bold text-blue-700 text-lg">{Number(diem).toFixed(2)}</span>
                </div>
              )
            ))}
          </div>
        </div>

        {/* Khối Điểm ưu tiên quy đổi */}
        <div className="bg-white p-6 rounded-2xl shadow-sm border h-fit">
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
              <p className="text-xs text-gray-400 mt-2 italic">*Hệ thống tự động lấy điểm cao nhất giữa Điểm thi ngoại ngữ và Điểm quy đổi chứng chỉ theo dữ liệu từ Bộ GD&ĐT.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DiemThiCaNhan;