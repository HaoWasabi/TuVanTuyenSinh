import React, { useState, useEffect } from 'react';
import { Calculator, CheckCircle, XCircle, Info, GraduationCap, ClipboardList, ChevronRight, Loader2 } from 'lucide-react';
import { apiTinhDiemDGNL, apiTinhDiemVSAT, apiTinhDiemTHPT, apiGetDanhSachNganh, apiGetChiTietNganh } from '../api/services';

const CongCuTinhDiem = () => {
  const [phuongThuc, setPhuongThuc] = useState('DGNL'); 
  const [isCalculating, setIsCalculating] = useState(false);
  const [result, setResult] = useState(null);

  const [majors, setMajors] = useState([]);
  const [isLoadingMajors, setIsLoadingMajors] = useState(true);

  const subjectDict = {
    'A00': ['toan', 'li', 'hoa'],
    'A01': ['toan', 'li', 'anh'],
    'A02': ['toan', 'li', 'sinh'],
    'B00': ['toan', 'hoa', 'sinh'],
    'C00': ['van', 'su', 'di'],
    'D01': ['toan', 'van', 'anh'],
    'D07': ['toan', 'hoa', 'anh'],
    'D14': ['van', 'su', 'anh']
  };

  const subjectLabels = {
    toan: 'Toán học', li: 'Vật lý', hoa: 'Hóa học', 
    sinh: 'Sinh học', su: 'Lịch sử', di: 'Địa lý', 
    van: 'Ngữ văn', anh: 'Tiếng Anh'
  };

  const [formData, setFormData] = useState({
    major: '',
    khuVuc: '0',
    doiTuong: '0',
    dgnlScore: '',
    bonusDGNL: '',
    englishConverted: '',
    subjects: {
      toan: { score: '', bonus: '' },
      li:   { score: '', bonus: '' },
      hoa:  { score: '', bonus: '' },
      sinh: { score: '', bonus: '' },
      su:   { score: '', bonus: '' },
      di:   { score: '', bonus: '' },
      van:  { score: '', bonus: '' },
      anh:  { score: '', bonus: '' },
    }
  });

  useEffect(() => {
    const fetchMajorsData = async () => {
      setIsLoadingMajors(true);
      try {
        const danhSachTenNganh = await apiGetDanhSachNganh();
        const promises = danhSachTenNganh.map(tenNganh => apiGetChiTietNganh(tenNganh));
        const chiTietNganhList = await Promise.all(promises);

        const formattedMajors = chiTietNganhList.map(detail => ({
          id: detail.manganh,
          name: detail.tennganh,
          diemNguong: detail.ndiemsan || 0,
          toHop: detail.ntohopgoc ? detail.ntohopgoc.split(',').map(s => s.trim()) : ['A00']
        }));

        setMajors(formattedMajors);
        if (formattedMajors.length > 0) {
          setFormData(prev => ({ ...prev, major: formattedMajors[0].id }));
        }
      } catch (error) {
        console.error("Lỗi khi tải dữ liệu ngành:", error);
      } finally {
        setIsLoadingMajors(false);
      }
    };

    fetchMajorsData();
  }, []);

  const handleGeneralChange = (e) => {
    setFormData(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubjectChange = (subject, field, value) => {
    setFormData(prev => ({
      ...prev,
      subjects: {
        ...prev.subjects,
        [subject]: { ...prev.subjects[subject], [field]: value }
      }
    }));
  };

  const handleCalculate = async () => {
    setIsCalculating(true);
    setResult(null);

    const selectedMajor = majors.find(m => m.id === formData.major);
    if (!selectedMajor) {
      alert("Vui lòng chọn một ngành hợp lệ!");
      setIsCalculating(false);
      return;
    }

    const uuTien = Number(formData.khuVuc) + Number(formData.doiTuong);

    try {
      if (phuongThuc === 'DGNL') {
        const res = await apiTinhDiemDGNL({
          maToHop: "DGNL",
          phuongThuc: "DGNL",
          diemTong: Number(formData.dgnlScore) || 0,
          diemCong: Number(formData.bonusDGNL) || 0,
          diemUuTien: uuTien
        });
        
        setResult({
          type: 'DGNL',
          data: res,
          diemNguong: selectedMajor.diemNguong
        });
      } 
      else {
        const toHopPromises = selectedMajor.toHop.map(async (th) => {
          const monInToHop = subjectDict[th] || [];
          let tongBonusMon = 0;
          
          monInToHop.forEach(mon => {
            tongBonusMon += (Number(formData.subjects[mon].bonus) || 0);
          });

          const payload = {
            maToHop: th,
            phuongThuc: phuongThuc,
            toan: Number(formData.subjects.toan.score) || 0,
            li:   Number(formData.subjects.li.score) || 0,
            hoa:  Number(formData.subjects.hoa.score) || 0,
            sinh: Number(formData.subjects.sinh.score) || 0,
            su:   Number(formData.subjects.su.score) || 0,
            di:   Number(formData.subjects.di.score) || 0,
            van:  Number(formData.subjects.van.score) || 0,
            anh:  formData.englishConverted !== '' ? Number(formData.englishConverted) : (Number(formData.subjects.anh.score) || 0),
            diemCong: tongBonusMon,
            diemUuTien: uuTien
          };

          const res = phuongThuc === 'VSAT' ? await apiTinhDiemVSAT(payload) : await apiTinhDiemTHPT(payload);
          return res;
        });

        const listResults = await Promise.all(toHopPromises);
        
        listResults.sort((a,b) => b.diemXetTuyen - a.diemXetTuyen);
        const bestResult = listResults[0];

        setResult({
          type: 'THPT',
          bestData: bestResult,
          allResults: listResults,
          diemNguong: selectedMajor.diemNguong
        });
      }
    } catch (error) {
      console.error("Lỗi API tính điểm:", error);
      alert(error.errorMessage || "Có lỗi xảy ra khi kết nối hệ thống tính điểm!");
    } finally {
      setIsCalculating(false);
    }
  };

  return (
    <div className="max-w-5xl mx-auto space-y-6 pb-10">
      <div className="bg-white p-6 md:p-8 rounded-3xl shadow-sm border border-gray-100">
        <h2 className="text-2xl font-bold text-blue-900 mb-8 flex items-center gap-3 border-b border-gray-100 pb-5">
          <Calculator className="text-blue-600" size={28} /> Mô Phỏng Cách Tính Điểm Xét Tuyển
        </h2>

        {/* Tabs chọn Phương thức */}
        <div className="flex flex-col md:flex-row bg-gray-50 p-1.5 rounded-2xl mb-8">
          {[
            { id: 'DGNL', label: 'A. Đánh giá năng lực' },
            { id: 'VSAT', label: 'B. Kỳ thi V-SAT' },
            { id: 'THPT', label: 'C. Thi THPT Quốc gia' }
          ].map(pt => (
            <button 
              key={pt.id}
              onClick={() => { setPhuongThuc(pt.id); setResult(null); }} 
              className={`flex-1 py-3 rounded-xl font-bold transition-all ${phuongThuc === pt.id ? 'bg-white text-blue-700 shadow-sm border border-gray-200' : 'text-gray-500 hover:text-gray-700 hover:bg-gray-100'}`}
            >
              {pt.label}
            </button>
          ))}
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
          {/* CỘT TRÁI: FORM NHẬP LIỆU */}
          <div className="lg:col-span-7 space-y-6">
            
            <div className="bg-blue-50/40 p-6 rounded-2xl border border-blue-100/60 space-y-5">
              <h3 className="font-bold text-blue-900 flex items-center gap-2">
                <ClipboardList size={20} className="text-blue-600"/> 1. Thông tin nguyện vọng
              </h3>
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-1.5">Chọn Ngành xét tuyển</label>
                <select 
                  name="major" 
                  value={formData.major} 
                  onChange={handleGeneralChange} 
                  disabled={isLoadingMajors}
                  className="w-full px-4 py-3 rounded-xl border border-gray-200 bg-white focus:ring-2 focus:ring-blue-500 outline-none font-bold text-blue-900 disabled:opacity-70 disabled:cursor-not-allowed transition"
                >
                  {isLoadingMajors ? (
                    <option>Đang tải dữ liệu ngành từ hệ thống...</option>
                  ) : (
                    majors.map(m => <option key={m.id} value={m.id}>{m.name}</option>)
                  )}
                </select>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-1.5">Khu vực ưu tiên</label>
                  <select name="khuVuc" value={formData.khuVuc} onChange={handleGeneralChange} className="w-full px-4 py-2.5 rounded-xl border border-gray-200 bg-white focus:ring-2 focus:ring-blue-500 outline-none transition">
                    <option value="0">KV3 (+0)</option>
                    <option value="0.25">KV2 (+0.25)</option>
                    <option value="0.5">KV2-NT (+0.5)</option>
                    <option value="0.75">KV1 (+0.75)</option>
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-1.5">Đối tượng ưu tiên</label>
                  <select name="doiTuong" value={formData.doiTuong} onChange={handleGeneralChange} className="w-full px-4 py-2.5 rounded-xl border border-gray-200 bg-white focus:ring-2 focus:ring-blue-500 outline-none transition">
                    <option value="0">Không có (+0)</option>
                    <option value="1">Nhóm ƯT 2 (+1.0)</option>
                    <option value="2">Nhóm ƯT 1 (+2.0)</option>
                  </select>
                </div>
              </div>
            </div>

            <div className="bg-green-50/40 p-6 rounded-2xl border border-green-100/60 space-y-5">
              <h3 className="font-bold text-green-900 flex items-center gap-2">
                <GraduationCap size={20} className="text-green-600"/> 2. Kết quả thi cử
              </h3>

              {phuongThuc === 'DGNL' && (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                  <div>
                    <label className="block text-sm font-bold text-gray-700 mb-2">Điểm thi ĐGNL (Thang 1200)</label>
                    <input type="number" name="dgnlScore" value={formData.dgnlScore} onChange={handleGeneralChange} className="w-full px-4 py-3 rounded-xl border border-gray-200 font-bold text-blue-700 text-lg text-center focus:ring-2 focus:ring-blue-500 outline-none transition" placeholder="VD: 850" />
                  </div>
                  <div>
                    <label className="block text-sm font-bold text-gray-700 mb-2">Điểm cộng thêm (Nếu có)</label>
                    <input type="number" name="bonusDGNL" value={formData.bonusDGNL} onChange={handleGeneralChange} className="w-full px-4 py-3 rounded-xl border border-gray-200 font-bold text-green-600 text-lg text-center focus:ring-2 focus:ring-green-500 outline-none transition" placeholder="+0.0" />
                  </div>
                </div>
              )}

              {(phuongThuc === 'VSAT' || phuongThuc === 'THPT') && (
                <div className="space-y-5">
                  <div className="bg-white p-4 rounded-xl border border-gray-200 flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                    <label className="text-sm font-bold text-gray-700 shrink-0">Ngoại ngữ quy đổi (Thang 10):</label>
                    <input type="number" name="englishConverted" value={formData.englishConverted} onChange={handleGeneralChange} className="w-full sm:w-1/2 px-4 py-2.5 rounded-lg border border-gray-200 focus:ring-2 focus:ring-blue-500 font-bold text-blue-700 outline-none transition" placeholder="IELTS / TOEFL..." />
                  </div>

                  <div>
                    <p className="text-sm font-bold text-gray-800 mb-3 flex items-center gap-2">
                      Nhập điểm thi thành phần 
                      <span className="text-xs font-semibold text-blue-600 bg-blue-50 px-2 py-0.5 rounded-md border border-blue-100">
                        {phuongThuc === 'VSAT' ? 'Thang 150' : 'Thang 10'}
                      </span>
                    </p>
                    
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                      {['toan', 'li', 'hoa', 'sinh', 'su', 'di', 'van', 'anh'].map(mon => (
                        <div key={mon} className="flex items-center justify-between bg-white p-2.5 rounded-xl border border-gray-200 hover:border-blue-300 transition-colors">
                          <label className="w-20 text-sm font-bold text-gray-600 pl-1">{subjectLabels[mon]}</label>
                          <div className="flex gap-2">
                            <input 
                              type="number" value={formData.subjects[mon].score} onChange={(e) => handleSubjectChange(mon, 'score', e.target.value)} 
                              className="w-[70px] px-2 py-1.5 rounded-lg border border-gray-200 bg-gray-50 font-bold text-center text-sm outline-none focus:border-blue-500 focus:bg-white transition" 
                              placeholder="Điểm"
                            />
                            <input 
                              type="number" value={formData.subjects[mon].bonus} onChange={(e) => handleSubjectChange(mon, 'bonus', e.target.value)} 
                              className="w-[70px] px-2 py-1.5 rounded-lg border border-gray-200 bg-green-50 text-green-700 font-bold text-center text-sm outline-none focus:border-green-500 focus:bg-white transition" 
                              placeholder="+Cộng"
                            />
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                </div>
              )}
            </div>

            <button 
              onClick={handleCalculate} disabled={isCalculating || isLoadingMajors}
              className="w-full bg-blue-600 text-white py-4 rounded-2xl font-bold text-lg hover:bg-blue-700 shadow-xl shadow-blue-600/20 transition-all active:scale-[0.98] flex justify-center items-center gap-2 disabled:opacity-70 disabled:cursor-not-allowed"
            >
              {isCalculating ? <Loader2 size={22} className="animate-spin" /> : <Calculator size={22}/>}
              {isCalculating ? 'Hệ thống đang phân tích...' : 'Xem Phân Tích Điểm'}
            </button>
          </div>

          {/* CỘT PHẢI: HIỂN THỊ KẾT QUẢ ĐÃ ĐƯỢC TỐI ƯU */}
          <div className="lg:col-span-5 bg-gray-900 rounded-3xl p-6 md:p-8 text-white flex flex-col relative overflow-hidden shadow-2xl">
            <div className="absolute top-0 right-0 p-4 opacity-5"><GraduationCap size={140} /></div>
            
            <h3 className="font-bold text-xl text-yellow-400 mb-8 border-b border-gray-700 pb-4 flex items-center gap-2">
              <CheckCircle size={24} className="text-yellow-400"/> Kết quả Xét tuyển
            </h3>
            
            {!result ? (
              <div className="flex-1 flex flex-col items-center justify-center text-gray-400 opacity-60">
                <Info size={56} className="mb-4" />
                <p className="text-center">Vui lòng nhập điểm và bấm nút<br/>để xem phân tích kết quả.</p>
              </div>
            ) : (
              <div className="space-y-6 animate-in fade-in slide-in-from-right-4 z-10">
                
                {/* 1. KẾT QUẢ ĐGNL */}
                {result.type === 'DGNL' && (
                  <>
                    <div className="bg-gray-800 p-6 rounded-2xl border border-gray-700 shadow-inner">
                      <p className="text-sm text-gray-400 font-medium mb-2 uppercase tracking-wide">Điểm quy đổi (Thang 30)</p>
                      <p className="text-6xl font-black text-white tracking-tight mb-3">{result.data.diemXetTuyen?.toFixed(2)}</p>
                      <p className="text-sm text-blue-300 bg-blue-900/30 px-3 py-2 rounded-lg border border-blue-800/50 leading-relaxed">{result.data.thongBao}</p>
                    </div>

                    <div className={`p-5 rounded-2xl flex items-center gap-4 border ${result.data.datNguong ? 'bg-green-900/30 border-green-500/40' : 'bg-red-900/30 border-red-500/40'} shadow-lg`}>
                      {result.data.datNguong ? <CheckCircle size={32} className="text-green-400" /> : <XCircle size={32} className="text-red-400" />}
                      <div>
                        <p className="font-bold text-lg text-white">So với Điểm Ngưỡng ({result.diemNguong})</p>
                        <p className={`text-sm mt-1 font-medium ${result.data.datNguong ? 'text-green-300' : 'text-red-300'}`}>
                          {result.data.datNguong ? 'ĐẠT điều kiện nộp hồ sơ' : 'KHÔNG ĐẠT điều kiện nộp hồ sơ'}
                        </p>
                      </div>
                    </div>
                  </>
                )}

                {/* 2. KẾT QUẢ VSAT / THPT */}
                {result.type === 'THPT' && (
                  <>
                    <div className="bg-gray-800 p-6 rounded-2xl border border-gray-700 shadow-inner">
                      <p className="text-sm text-gray-400 font-medium mb-2 uppercase tracking-wide">Tổ hợp cao điểm nhất ({result.bestData.maToHop})</p>
                      <p className="text-6xl font-black text-white tracking-tight mb-3">{result.bestData.diemXetTuyen?.toFixed(2)}</p>
                      <p className="text-sm text-blue-300 bg-blue-900/30 px-3 py-2 rounded-lg border border-blue-800/50 leading-relaxed">{result.bestData.thongBao}</p>
                    </div>

                    <div className={`p-5 rounded-2xl flex items-center gap-4 border ${result.bestData.datNguong ? 'bg-green-900/30 border-green-500/40' : 'bg-red-900/30 border-red-500/40'} shadow-lg`}>
                      {result.bestData.datNguong ? <CheckCircle size={32} className="text-green-400" /> : <XCircle size={32} className="text-red-400" />}
                      <div>
                        <p className="font-bold text-lg text-white">So với Điểm Ngưỡng ({result.diemNguong})</p>
                        <p className={`text-sm mt-1 font-medium ${result.bestData.datNguong ? 'text-green-300' : 'text-red-300'}`}>
                          {result.bestData.datNguong ? 'ĐẠT điều kiện nộp hồ sơ' : 'KHÔNG ĐẠT điều kiện nộp hồ sơ'}
                        </p>
                      </div>
                    </div>

                    {result.bestData.chiTietQuyDoi && result.bestData.chiTietQuyDoi.length > 0 && (
                      <div className="bg-gray-800/80 p-5 rounded-2xl border border-gray-700">
                        <p className="text-xs font-bold text-gray-400 mb-3 uppercase tracking-wider">Bảng quy đổi về Thang 10:</p>
                        <div className="grid grid-cols-2 gap-3 text-sm">
                          {result.bestData.chiTietQuyDoi.map(ct => (
                            <div key={ct.tenMon} className="flex justify-between items-center bg-gray-900 p-2.5 rounded-xl border border-gray-700/50">
                              <span className="text-gray-300">{ct.tenMon}</span>
                              <span className="text-green-400 font-bold">{ct.diemQuyDoi}</span>
                            </div>
                          ))}
                        </div>
                      </div>
                    )}

                    <div>
                      <p className="text-xs font-bold uppercase text-gray-400 mb-3 tracking-wider">Điểm chi tiết các Tổ hợp hợp lệ:</p>
                      <div className="space-y-2">
                        {result.allResults.map((th, idx) => (
                          <div key={idx} className={`px-4 py-3 rounded-xl flex justify-between items-center ${idx === 0 ? 'bg-blue-600 border border-blue-500 text-white shadow-md' : 'bg-gray-800 border border-gray-700 text-gray-300 hover:bg-gray-700 transition'}`}>
                            <span className="font-bold text-sm">Tổ hợp {th.maToHop}</span>
                            <span className="font-bold text-base">{th.diemXetTuyen?.toFixed(2)}</span>
                          </div>
                        ))}
                      </div>
                    </div>
                  </>
                )}

              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default CongCuTinhDiem;