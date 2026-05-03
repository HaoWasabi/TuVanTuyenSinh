import React, { useState, useEffect } from 'react';
import { Plus, Trash2, Edit, ArrowUp, ArrowDown, X, Save, FileText } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
// import { apiGetNguyenVong, apiSaveNguyenVong, apiDeleteNguyenVong } from '../api/services'; // TODO: Sẵn sàng Import khi có API

const NguyenVong = () => {
  const { user } = useAuth();
  
  // Dữ liệu mô phỏng bảng xt_nguyenvong
  // TODO: Tương lai sẽ thay bằng useEffect gọi apiGetNguyenVong(user.cccd)
  const [danhSachNV, setDanhSachNV] = useState([
    { id: 1, thuTu: 1, maNganh: '7480201', tenNganh: 'Công nghệ thông tin', maToHop: 'A00' },
    { id: 2, thuTu: 2, maNganh: '7340101', tenNganh: 'Quản trị kinh doanh', maToHop: 'A01' },
  ]);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formData, setFormData] = useState({ id: null, maNganh: '', tenNganh: '', maToHop: '' });

  const handleDelete = (id) => {
    if (window.confirm('Bạn có chắc chắn muốn xóa nguyện vọng này?')) {
      // TODO: Gọi API apiDeleteNguyenVong(id) tại đây trước khi setDanhSachNV
      const newList = danhSachNV.filter(nv => nv.id !== id);
      setDanhSachNV(newList.map((nv, index) => ({ ...nv, thuTu: index + 1 })));
    }
  };

  const handleMove = (index, direction) => {
    if ((direction === -1 && index === 0) || (direction === 1 && index === danhSachNV.length - 1)) return;
    const newList = [...danhSachNV];
    const temp = newList[index];
    newList[index] = newList[index + direction];
    newList[index + direction] = temp;
    
    const updatedList = newList.map((nv, idx) => ({ ...nv, thuTu: idx + 1 }));
    setDanhSachNV(updatedList);
    // TODO: Gọi API apiUpdateThuTuNguyenVong(updatedList) tại đây để đồng bộ xuống DB
  };

  const handleOpenAdd = () => {
    setFormData({ id: null, maNganh: '', tenNganh: '', maToHop: '' });
    setIsModalOpen(true);
  };

  const handleOpenEdit = (nv) => {
    setFormData(nv);
    setIsModalOpen(true);
  };

  const handleSave = (e) => {
    e.preventDefault();
    // TODO: Gọi API apiSaveNguyenVong(formData) tại đây
    if (formData.id) {
      setDanhSachNV(danhSachNV.map(nv => nv.id === formData.id ? { ...nv, ...formData } : nv));
    } else {
      setDanhSachNV([...danhSachNV, { ...formData, id: Date.now(), thuTu: danhSachNV.length + 1 }]);
    }
    setIsModalOpen(false);
  };

  if (!user) return <div className="text-center py-10 text-red-600 font-bold">Vui lòng đăng nhập.</div>;

  return (
    <div className="max-w-5xl mx-auto">
      <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
        
        <div className="bg-blue-800 p-5 md:p-6 flex flex-col md:flex-row justify-between items-start md:items-center text-white gap-4">
          <div>
            <h2 className="text-xl font-bold flex items-center gap-2"><FileText size={22} /> Quản lý Nguyện Vọng</h2>
            <p className="text-blue-200 text-sm mt-1">Hệ thống sẽ dựa vào [Mã Ngành] và [Mã Tổ Hợp] để xét tuyển.</p>
          </div>
          <button onClick={handleOpenAdd} className="flex items-center gap-2 bg-white text-blue-800 px-5 py-2.5 rounded-xl font-bold text-sm hover:bg-blue-50 transition shadow-sm">
            <Plus size={18} /> Đăng ký thêm
          </button>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-gray-50 text-gray-500 text-xs uppercase tracking-wider border-b border-gray-200">
                <th className="p-4 font-bold text-center w-20">Thứ tự</th>
                <th className="p-4 font-bold">Mã Ngành</th>
                <th className="p-4 font-bold">Tên Ngành</th>
                <th className="p-4 font-bold text-center">Mã Tổ Hợp</th>
                <th className="p-4 font-bold text-center">Đổi thứ tự</th>
                <th className="p-4 font-bold text-center">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              {danhSachNV.map((nv, index) => (
                <tr key={nv.id} className="border-b last:border-0 hover:bg-blue-50/30 transition">
                  <td className="p-4 text-center font-black text-blue-700 text-lg">{nv.thuTu}</td>
                  <td className="p-4 font-mono font-bold text-gray-700">{nv.maNganh}</td>
                  <td className="p-4 font-medium text-gray-800">{nv.tenNganh}</td>
                  <td className="p-4 text-center">
                    <span className="bg-blue-100 border border-blue-200 text-blue-800 px-3 py-1 rounded-md font-bold text-xs">{nv.maToHop}</span>
                  </td>
                  <td className="p-4 text-center">
                    <div className="flex justify-center gap-1">
                      <button onClick={() => handleMove(index, -1)} disabled={index === 0} className="p-1.5 text-gray-400 hover:text-blue-600 hover:bg-blue-100 rounded disabled:opacity-20 transition"><ArrowUp size={18} /></button>
                      <button onClick={() => handleMove(index, 1)} disabled={index === danhSachNV.length - 1} className="p-1.5 text-gray-400 hover:text-blue-600 hover:bg-blue-100 rounded disabled:opacity-20 transition"><ArrowDown size={18} /></button>
                    </div>
                  </td>
                  <td className="p-4 text-center">
                    <div className="flex justify-center gap-2">
                      <button onClick={() => handleOpenEdit(nv)} className="p-2 text-blue-600 hover:bg-blue-100 rounded-lg transition"><Edit size={16}/></button>
                      <button onClick={() => handleDelete(nv.id)} className="p-2 text-red-600 hover:bg-red-100 rounded-lg transition"><Trash2 size={16}/></button>
                    </div>
                  </td>
                </tr>
              ))}
              {danhSachNV.length === 0 && (
                <tr><td colSpan="6" className="p-10 text-center text-gray-500">Bạn chưa đăng ký nguyện vọng nào.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal Form */}
      {isModalOpen && (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden animate-in zoom-in-95 duration-200">
            <div className="bg-blue-800 p-4 flex justify-between items-center text-white">
              <h3 className="font-bold text-lg">{formData.id ? 'Sửa Nguyện Vọng' : 'Đăng Ký Nguyện Vọng'}</h3>
              <button onClick={() => setIsModalOpen(false)} className="hover:bg-blue-700 p-1.5 rounded-lg transition"><X size={20}/></button>
            </div>
            <form onSubmit={handleSave} className="p-6 space-y-5">
              <div>
                <label className="block text-sm font-bold text-gray-700 mb-1">Mã ngành (xt_nganh)</label>
                <input required type="text" value={formData.maNganh} onChange={(e) => setFormData({...formData, maNganh: e.target.value.toUpperCase()})} className="w-full px-4 py-2.5 rounded-xl border focus:ring-2 focus:ring-blue-500 outline-none font-mono" placeholder="VD: 7480201"/>
              </div>
              <div>
                <label className="block text-sm font-bold text-gray-700 mb-1">Tên ngành hiển thị</label>
                <input required type="text" value={formData.tenNganh} onChange={(e) => setFormData({...formData, tenNganh: e.target.value})} className="w-full px-4 py-2.5 rounded-xl border focus:ring-2 focus:ring-blue-500 outline-none" placeholder="VD: Công nghệ thông tin"/>
              </div>
              <div>
                <label className="block text-sm font-bold text-gray-700 mb-1">Mã Tổ hợp (xt_tohop_monthi)</label>
                <input required type="text" value={formData.maToHop} onChange={(e) => setFormData({...formData, maToHop: e.target.value.toUpperCase()})} className="w-full px-4 py-2.5 rounded-xl border focus:ring-2 focus:ring-blue-500 outline-none font-mono" placeholder="VD: A00"/>
              </div>
              <div className="pt-4 flex justify-end gap-3">
                <button type="button" onClick={() => setIsModalOpen(false)} className="px-5 py-2.5 text-gray-600 bg-gray-100 rounded-xl font-bold hover:bg-gray-200 transition">Hủy</button>
                <button type="submit" className="px-5 py-2.5 flex items-center gap-2 text-white bg-blue-600 rounded-xl font-bold hover:bg-blue-700 transition shadow-lg shadow-blue-200">
                  <Save size={18} /> Lưu hệ thống
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default NguyenVong;