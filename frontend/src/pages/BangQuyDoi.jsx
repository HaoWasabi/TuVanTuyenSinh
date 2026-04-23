import React from 'react';

const BangQuyDoi = () => {
  // Mock data được trích xuất trực tiếp từ bảng xt_bangquydoi
  const mockData = [
    { id: 1, phuongThuc: 'IELTS', mon: 'Ngoại ngữ (N1)', mucDiem: '6.50', maQuyDoi: 'IELTS6.5', phanVi: 'QUYDOI_A' },
    { id: 2, phuongThuc: 'IELTS', mon: 'Ngoại ngữ (N1)', mucDiem: '7.00', maQuyDoi: 'IELTS7.0', phanVi: 'QUYDOI_A' },
    { id: 3, phuongThuc: 'GIAI_QUOCGIA', mon: 'Toán (TO)', mucDiem: '1.00', maQuyDoi: 'HSG_QG', phanVi: 'CONG_DIEM' },
    { id: 4, phuongThuc: 'TOEIC', mon: 'Ngoại ngữ (N1)', mucDiem: '650.00', maQuyDoi: 'TOEIC650', phanVi: 'QUYDOI_B' },
    { id: 5, phuongThuc: 'JLPT', mon: 'Ngoại ngữ (N1)', mucDiem: '120.00', maQuyDoi: 'N2_JAPAN', phanVi: 'QUYDOI_A' },
  ];

  return (
    <div className="max-w-5xl mx-auto bg-white p-8 rounded-2xl shadow-sm border">
      <h2 className="text-2xl font-bold text-blue-900 mb-2">Bảng Tham Số Quy Đổi Điểm Hệ Thống</h2>
      <p className="text-gray-500 mb-6">Dữ liệu quy đổi điểm ưu tiên và chứng chỉ (Tham chiếu SQL: xt_bangquydoi)</p>

      <div className="overflow-x-auto">
        <table className="w-full text-left border-collapse border border-gray-200">
          <thead>
            <tr className="bg-blue-50 text-blue-800">
              <th className="p-4 border font-bold">Phương thức (d_phuongthuc)</th>
              <th className="p-4 border font-bold">Mã quy đổi (d_maquydoi)</th>
              <th className="p-4 border font-bold text-center">Mức điểm yêu cầu (d_diema)</th>
              <th className="p-4 border font-bold text-center">Môn áp dụng (d_mon)</th>
              <th className="p-4 border font-bold text-center">Phân vị (d_phanvi)</th>
            </tr>
          </thead>
          <tbody>
            {mockData.map((item) => (
              <tr key={item.id} className="hover:bg-gray-50 transition">
                <td className="p-4 border font-semibold text-gray-700">{item.phuongThuc}</td>
                <td className="p-4 border text-blue-600 font-mono text-sm font-bold">{item.maQuyDoi}</td>
                <td className="p-4 border text-center font-bold">{item.mucDiem}</td>
                <td className="p-4 border text-center text-gray-600">{item.mon}</td>
                <td className="p-4 border text-center"><span className="px-2 py-1 bg-gray-100 rounded text-xs font-semibold">{item.phanVi}</span></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default BangQuyDoi;