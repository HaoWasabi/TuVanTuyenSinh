import React, { useState } from 'react';
import { Search, BookOpen } from 'lucide-react';

const TraCuuNganh = () => {
  const [search, setSearch] = useState('');

  // Mock data mô phỏng JOIN từ bảng xt_nganh và xt_tohop_monthi
  const mockData = [
    { id: 1, maNganh: '7480201', tenNganh: 'Công nghệ thông tin', chiTieu: 500, toHop: ['A00', 'A01', 'D01'] },
    { id: 2, maNganh: '7340101', tenNganh: 'Quản trị kinh doanh', chiTieu: 300, toHop: ['A00', 'A01', 'D01', 'D07'] },
    { id: 3, maNganh: '7220201', tenNganh: 'Ngôn ngữ Anh', chiTieu: 200, toHop: ['D01', 'D14'] },
    { id: 4, maNganh: '7140209', tenNganh: 'Sư phạm Toán học', chiTieu: 150, toHop: ['A00', 'A01'] },
  ];

  const filteredData = mockData.filter(item => 
    item.tenNganh.toLowerCase().includes(search.toLowerCase()) || 
    item.maNganh.includes(search)
  );

  return (
    <div className="max-w-6xl mx-auto bg-white p-6 md:p-8 rounded-2xl shadow-sm border border-gray-100">
      <div className="mb-8">
        <h2 className="text-2xl font-bold text-blue-900 flex items-center gap-2 mb-2">
          <BookOpen className="text-blue-600" /> Tra cứu Ngành & Tổ hợp Xét tuyển
        </h2>
        <p className="text-gray-500 text-sm">Danh mục mã ngành và mã tổ hợp áp dụng cho kỳ tuyển sinh 2026.</p>
      </div>
      
      {/* Thanh tìm kiếm */}
      <div className="relative mb-6 max-w-2xl">
        <input 
          type="text" 
          className="w-full pl-11 pr-4 py-3.5 rounded-xl border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none shadow-sm transition-all"
          placeholder="Nhập tên ngành hoặc mã ngành (VD: 7480201)..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        <Search className="absolute left-4 top-4 text-gray-400" size={20} />
      </div>

      {/* Bảng dữ liệu */}
      <div className="overflow-x-auto rounded-xl border border-gray-200">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr className="bg-blue-50 text-blue-800 border-b border-gray-200">
              <th className="p-4 font-bold w-32">Mã Ngành</th>
              <th className="p-4 font-bold">Tên Ngành</th>
              <th className="p-4 font-bold text-center">Chỉ tiêu</th>
              <th className="p-4 font-bold">Mã Tổ hợp môn (xt_tohop_monthi)</th>
            </tr>
          </thead>
          <tbody>
            {filteredData.map((item) => (
              <tr key={item.id} className="border-b last:border-0 hover:bg-gray-50 transition-colors">
                <td className="p-4 font-mono font-semibold text-blue-700">{item.maNganh}</td>
                <td className="p-4 font-medium text-gray-800">{item.tenNganh}</td>
                <td className="p-4 text-center text-gray-600 font-medium">{item.chiTieu}</td>
                <td className="p-4 flex flex-wrap gap-2">
                  {item.toHop.map(th => (
                    <span key={th} className="px-2.5 py-1 bg-white text-gray-700 rounded text-xs font-bold border shadow-sm">
                      {th}
                    </span>
                  ))}
                </td>
              </tr>
            ))}
            {filteredData.length === 0 && (
              <tr><td colSpan="4" className="p-8 text-center text-gray-500">Không tìm thấy mã ngành hoặc tên ngành phù hợp.</td></tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default TraCuuNganh;