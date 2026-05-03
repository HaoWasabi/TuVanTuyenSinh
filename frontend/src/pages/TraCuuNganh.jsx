import React, { useState, useEffect } from 'react';
import { Search, BookOpen, Loader2 } from 'lucide-react';
import { apiGetDanhSachNganh, apiGetChiTietNganh } from '../api/services';

const TraCuuNganh = () => {
  const [search, setSearch] = useState('');
  const [nganhList, setNganhList] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  // Lấy dữ liệu ngành từ API khi component được mount
  useEffect(() => {
    const fetchNganhData = async () => {
      setIsLoading(true);
      try {
        // Bước 1: Lấy danh sách tên ngành
        const danhSachTenNganh = await apiGetDanhSachNganh();
        
        // Bước 2: Gọi API lấy chi tiết cho từng ngành cùng một lúc
        const promises = danhSachTenNganh.map(tenNganh => apiGetChiTietNganh(tenNganh));
        const chiTietNganhList = await Promise.all(promises);

        // Bước 3: Format dữ liệu trả về để khớp với chuẩn UI đang có
        const formattedData = chiTietNganhList.map((detail, index) => {
          // Xử lý chuỗi tổ hợp môn (Ví dụ API trả về "A00" hoặc "A00, A01, D01")
          let toHopArray = [];
          if (detail.ntohopgoc) {
            toHopArray = detail.ntohopgoc.split(',').map(th => th.trim());
          }

          return {
            id: detail.idnganh || index,
            maNganh: detail.manganh || 'N/A',
            tenNganh: detail.tennganh || 'N/A',
            chiTieu: detail.nchitieu || 0,
            toHop: toHopArray
          };
        });

        setNganhList(formattedData);
      } catch (error) {
        console.error("Lỗi khi tải dữ liệu ngành:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchNganhData();
  }, []);

  // Tính năng tìm kiếm động (Search/Filter)
  const filteredData = nganhList.filter(item => 
    item.tenNganh.toLowerCase().includes(search.toLowerCase()) || 
    item.maNganh.toLowerCase().includes(search.toLowerCase())
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
          placeholder="Nhập tên ngành hoặc mã ngành (VD: CNTT01)..."
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
            {isLoading ? (
              // Trạng thái đang tải dữ liệu API
              <tr>
                <td colSpan="4" className="p-10 text-center text-gray-500">
                  <Loader2 className="animate-spin mx-auto mb-2 text-blue-500" size={32} />
                  Đang tải dữ liệu từ CSDL...
                </td>
              </tr>
            ) : filteredData.length > 0 ? (
              // Trạng thái đã có dữ liệu
              filteredData.map((item) => (
                <tr key={item.id} className="border-b last:border-0 hover:bg-gray-50 transition-colors">
                  <td className="p-4 font-mono font-semibold text-blue-700">{item.maNganh}</td>
                  <td className="p-4 font-medium text-gray-800">{item.tenNganh}</td>
                  <td className="p-4 text-center text-gray-600 font-medium">{item.chiTieu}</td>
                  <td className="p-4 flex flex-wrap gap-2">
                    {item.toHop.map((th, index) => (
                      <span key={index} className="px-2.5 py-1 bg-white text-gray-700 rounded text-xs font-bold border shadow-sm">
                        {th}
                      </span>
                    ))}
                  </td>
                </tr>
              ))
            ) : (
              // Trạng thái tìm kiếm không thấy
              <tr>
                <td colSpan="4" className="p-8 text-center text-gray-500">
                  Không tìm thấy mã ngành hoặc tên ngành phù hợp.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default TraCuuNganh;