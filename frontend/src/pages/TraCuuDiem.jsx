import React, { useState } from 'react';
import { Search, Loader2 } from 'lucide-react';

const TraCuuDiem = () => {
  const [sbd, setSbd] = useState('');
  const [result, setResult] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  const handleSearch = (e) => {
    e.preventDefault();
    if (!sbd.trim()) return;
    
    setIsLoading(true);
    setResult(null);

    // Giả lập API gọi vào bảng xt_diemthixettuyen
    setTimeout(() => {
      setIsLoading(false);
      setResult({
        sbd: sbd,
        diem: {
          'Toán (TO)': 8.40,
          'Ngữ Văn (VA)': 7.50,
          'Vật lý (LI)': 8.25,
          'Hóa học (HO)': 7.75,
          'Ngoại ngữ (N1_THI)': 9.00,
          'Sinh học (SI)': null, // null là môn không thi
          'Lịch sử (SU)': null,
          'Địa lý (DI)': null,
          'GD KTPL (KTPL)': null,
        }
      });
    }, 800);
  };

  return (
    <div className="max-w-3xl mx-auto">
      <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-100 text-center relative overflow-hidden">
        {/* BG trang trí */}
        <div className="absolute top-0 left-0 w-full h-2 bg-gradient-to-r from-blue-500 to-blue-700"></div>

        <h2 className="text-2xl font-bold text-blue-900 mb-2 mt-2">Tra Cứu Điểm Thi THPT 2026</h2>
        <p className="text-gray-500 mb-8">Dữ liệu được truy xuất trực tiếp từ CSDL Tuyển sinh</p>

        <form onSubmit={handleSearch} className="flex gap-3 max-w-md mx-auto mb-10">
          <input 
            type="text" 
            placeholder="Nhập Số báo danh (SBD)..." 
            className="flex-grow px-4 py-3 rounded-xl border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none font-mono"
            value={sbd}
            onChange={(e) => setSbd(e.target.value)}
            required
          />
          <button type="submit" disabled={isLoading} className="bg-blue-600 text-white px-6 py-3 rounded-xl font-bold hover:bg-blue-700 flex items-center gap-2 transition-all disabled:opacity-70">
            {isLoading ? <Loader2 size={18} className="animate-spin" /> : <Search size={18} />} 
            Tra cứu
          </button>
        </form>

        {result && (
          <div className="text-left bg-blue-50/50 p-6 rounded-2xl border border-blue-100 shadow-sm animate-in fade-in slide-in-from-bottom-4">
            <h3 className="font-bold text-lg text-blue-800 mb-4 border-b border-blue-100 pb-3">
              Kết quả thi của SBD: <span className="font-mono text-xl">{result.sbd}</span>
            </h3>
            <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
              {Object.entries(result.diem).map(([mon, diem]) => (
                diem !== null && (
                  <div key={mon} className="bg-white p-4 rounded-xl border border-gray-100 shadow-sm text-center">
                    <div className="text-xs text-gray-500 font-bold mb-2 uppercase">{mon}</div>
                    <div className="text-2xl font-black text-blue-700">{diem.toFixed(2)}</div>
                  </div>
                )
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default TraCuuDiem;