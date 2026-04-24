import React, { useState } from 'react';
import { Calculator, CheckCircle, XCircle, Info, ChevronRight } from 'lucide-react';

const CongCuTinhDiem = () => {
  const [method, setMethod] = useState('DGNL'); // 'DGNL' hoặc 'THPT'
  const [result, setResult] = useState(null);

  // 1. Dữ liệu Ngành (Bổ sung Điểm Ngưỡng, Điểm Chuẩn và Tổ hợp môn)
  const majors = [
    { id: '7480201', name: 'Công nghệ thông tin', diemNguong: 20.0, diemChuan: 24.5, toHop: ['A00', 'A01', 'D01'] },
    { id: '7340101', name: 'Quản trị kinh doanh', diemNguong: 18.0, diemChuan: 22.0, toHop: ['A00', 'A01', 'D01', 'D07'] },
    { id: '7220201', name: 'Ngôn ngữ Anh', diemNguong: 18.0, diemChuan: 23.0, toHop: ['D01', 'D14'] },
  ];

  // 2. Từ điển môn học trong các Tổ hợp
  const subjectDict = {
    'A00': ['toan', 'ly', 'hoa'],
    'A01': ['toan', 'ly', 'anh'],
    'D01': ['toan', 'van', 'anh'],
    'D07': ['toan', 'hoa', 'anh'],
    'D14': ['van', 'su', 'anh']
  };

  const subjectNames = { toan: 'Toán', ly: 'Vật lý', hoa: 'Hóa học', van: 'Ngữ văn', anh: 'Tiếng Anh', su: 'Lịch sử' };

  // 3. State quản lý form
  const [formData, setFormData] = useState({
    major: '7480201',
    kv: '0',
    dt: '0',
    bonusDGNL: 0, // Điểm cộng tự bổ sung cho ĐGNL
    dgnlScore: 0,
    // VSAT/THPT Data
    subjects: {
      toan: { score: 0, scale: 10, bonus: 0 },
      ly: { score: 0, scale: 10, bonus: 0 },
      hoa: { score: 0, scale: 10, bonus: 0 },
      van: { score: 0, scale: 10, bonus: 0 },
      anh: { score: 0, scale: 10, bonus: 0 },
      su: { score: 0, scale: 10, bonus: 0 },
    },
    englishConverted: 0, // Điểm quy đổi Tiếng Anh chứng chỉ
  });

  const handleSubjectChange = (subject, field, value) => {
    setFormData(prev => ({
      ...prev,
      subjects: {
        ...prev.subjects,
        [subject]: { ...prev.subjects[subject], [field]: parseFloat(value) || 0 }
      }
    }));
  };

  const handleCalculate = () => {
    const selectedMajor = majors.find(m => m.id === formData.major);
    const uuTien = parseFloat(formData.kv) + parseFloat(formData.dt);

    if (method === 'DGNL') {
      const quyDoi = (formData.dgnlScore * 30) / 1200;
      const tongDiem = quyDoi + uuTien + parseFloat(formData.bonusDGNL);
      
      setResult({
        type: 'DGNL',
        tong: tongDiem.toFixed(2),
        quyDoi: quyDoi.toFixed(2),
        uuTien: uuTien.toFixed(2),
        bonus: parseFloat(formData.bonusDGNL).toFixed(2),
        diemNguong: selectedMajor.diemNguong,
        diemChuan: selectedMajor.diemChuan,
        datNguong: tongDiem >= selectedMajor.diemNguong,
        datChuan: tongDiem >= selectedMajor.diemChuan,
      });

    } else {
      // Phương thức THPT/VSAT: Tính cho TỪNG tổ hợp môn của ngành
      let toHopResults = [];
      let maxScore = 0;

      selectedMajor.toHop.forEach(th => {
        const monInToHop = subjectDict[th];
        let tongToHop = uuTien;
        let cthietDiem = [];

        monInToHop.forEach(mon => {
          let diemMon = 0;
          
          // Xử lý ưu tiên Ngoại ngữ quy đổi nếu là môn Tiếng Anh
          if (mon === 'anh' && formData.englishConverted > 0) {
             diemMon = formData.englishConverted;
          } else {
             // Quy đổi VSAT thang 150 về thang 10
             let raw = formData.subjects[mon].score;
             let scale = formData.subjects[mon].scale;
             diemMon = scale === 150 ? (raw / 150) * 10 : raw;
          }

          // Cộng điểm thưởng của từng môn
          let bonusMon = formData.subjects[mon].bonus;
          tongToHop += (diemMon + bonusMon);
          cthietDiem.push(`${subjectNames[mon]}: ${(diemMon+bonusMon).toFixed(2)}`);
        });

        if (tongToHop > maxScore) maxScore = tongToHop;

        toHopResults.push({
          maTH: th,
          diem: tongToHop.toFixed(2),
          chiTiet: cthietDiem.join(' | ')
        });
      });

      setResult({
        type: 'THPT',
        toHopResults: toHopResults.sort((a,b) => b.diem - a.diem), // Sắp xếp điểm cao nhất lên đầu
        maxScore: maxScore.toFixed(2),
        diemNguong: selectedMajor.diemNguong,
        diemChuan: selectedMajor.diemChuan,
        datNguong: maxScore >= selectedMajor.diemNguong,
        datChuan: maxScore >= selectedMajor.diemChuan,
      });
    }
  };

  return (
    <div className="max-w-5xl mx-auto space-y-6 pb-10">
      <div className="bg-white p-6 md:p-8 rounded-2xl shadow-sm border border-blue-100">
        <h2 className="text-2xl font-bold text-blue-900 mb-6 flex items-center gap-2 border-b pb-4">
          <Calculator className="text-blue-600" /> Mô phỏng Tính điểm Xét tuyển
        </h2>

        {/* Tabs chọn phương thức */}
        <div className="flex flex-col md:flex-row bg-gray-100 p-1 rounded-xl mb-8">
          <button onClick={() => {setMethod('DGNL'); setResult(null);}} className={`flex-1 py-3 rounded-lg font-bold transition ${method === 'DGNL' ? 'bg-white text-blue-700 shadow-sm' : 'text-gray-500 hover:text-gray-700'}`}>
            A. Phương thức ĐGNL
          </button>
          <button onClick={() => {setMethod('THPT'); setResult(null);}} className={`flex-1 py-3 rounded-lg font-bold transition ${method === 'THPT' ? 'bg-white text-blue-700 shadow-sm' : 'text-gray-500 hover:text-gray-700'}`}>
            B. Phương thức VSAT/THPT
          </button>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
          {/* CỘT TRÁI: FORM NHẬP LIỆU */}
          <div className="lg:col-span-7 space-y-6">
            
            {/* THÔNG TIN CHUNG */}
            <div className="p-5 bg-gray-50 rounded-xl border border-gray-200 space-y-4">
              <h3 className="font-bold text-gray-800 border-b pb-2">1. Thông tin chung</h3>
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-1">Chọn Ngành xét tuyển</label>
                <select className="w-full px-4 py-2 rounded-lg border bg-white focus:ring-2 focus:ring-blue-500 outline-none font-medium text-blue-900"
                  value={formData.major} onChange={(e) => setFormData({...formData, major: e.target.value})}>
                  {majors.map(m => <option key={m.id} value={m.id}>{m.name} (Chuẩn: {m.diemChuan})</option>)}
                </select>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-1">Khu vực ưu tiên</label>
                  <select className="w-full px-3 py-2 rounded-lg border bg-white" onChange={(e) => setFormData({...formData, kv: e.target.value})}>
                    <option value="0">KV3 (0)</option>
                    <option value="0.25">KV2 (+0.25)</option>
                    <option value="0.5">KV2-NT (+0.5)</option>
                    <option value="0.75">KV1 (+0.75)</option>
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-1">Đối tượng ưu tiên</label>
                  <select className="w-full px-3 py-2 rounded-lg border bg-white" onChange={(e) => setFormData({...formData, dt: e.target.value})}>
                    <option value="0">Không có (0)</option>
                    <option value="1">Nhóm ƯT 2 (+1.0)</option>
                    <option value="2">Nhóm ƯT 1 (+2.0)</option>
                  </select>
                </div>
              </div>
            </div>

            {/* ĐGNL FORM */}
            {method === 'DGNL' && (
              <div className="p-5 bg-blue-50 rounded-xl border border-blue-100 space-y-4">
                <h3 className="font-bold text-blue-900 border-b border-blue-200 pb-2">2. Điểm thi ĐGNL</h3>
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-1">Điểm bài thi (Thang 1200)</label>
                    <input type="number" className="w-full px-4 py-2.5 rounded-lg border focus:ring-2 focus:ring-blue-500 font-bold text-blue-700" placeholder="VD: 850"
                      onChange={(e) => setFormData({...formData, dgnlScore: e.target.value})} />
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-1">Điểm cộng thêm (nếu có)</label>
                    <input type="number" className="w-full px-4 py-2.5 rounded-lg border focus:ring-2 focus:ring-blue-500" placeholder="VD: 1.5"
                      onChange={(e) => setFormData({...formData, bonusDGNL: e.target.value})} />
                  </div>
                </div>
              </div>
            )}

            {/* VSAT/THPT FORM */}
            {method === 'THPT' && (
              <div className="p-5 bg-green-50 rounded-xl border border-green-100 space-y-4">
                <h3 className="font-bold text-green-900 border-b border-green-200 pb-2">2. Nhập điểm môn học & Điểm cộng</h3>
                
                <div className="mb-4">
                  <label className="block text-sm font-bold text-gray-700 mb-1">Tiếng Anh quy đổi chứng chỉ (Thang 10)</label>
                  <input type="number" className="w-full md:w-1/2 px-4 py-2 rounded-lg border focus:ring-2 focus:ring-green-500" placeholder="Nếu có IELTS/TOEFL..."
                    onChange={(e) => setFormData({...formData, englishConverted: parseFloat(e.target.value)||0})} />
                </div>

                <div className="space-y-3">
                  <div className="grid grid-cols-12 gap-2 text-xs font-bold text-gray-500 text-center mb-1">
                    <div className="col-span-3 text-left">Môn thi</div>
                    <div className="col-span-3">Thang điểm</div>
                    <div className="col-span-3">Điểm đạt</div>
                    <div className="col-span-3">Điểm cộng thêm</div>
                  </div>
                  
                  {['toan', 'ly', 'hoa', 'van', 'anh', 'su'].map(mon => (
                    <div key={mon} className="grid grid-cols-12 gap-2 items-center bg-white p-2 rounded-lg border border-green-100">
                      <div className="col-span-3 font-semibold text-sm capitalize">{subjectNames[mon]}</div>
                      <div className="col-span-3">
                        <select className="w-full text-sm border rounded p-1.5 bg-gray-50" 
                          onChange={(e) => handleSubjectChange(mon, 'scale', e.target.value)}>
                          <option value="10">THPT (10)</option>
                          <option value="150">VSAT (150)</option>
                        </select>
                      </div>
                      <div className="col-span-3">
                        <input type="number" className="w-full border rounded p-1.5 text-center font-bold text-blue-700" placeholder="0"
                          onChange={(e) => handleSubjectChange(mon, 'score', e.target.value)} />
                      </div>
                      <div className="col-span-3">
                        <input type="number" className="w-full border rounded p-1.5 text-center text-green-600" placeholder="+0"
                          onChange={(e) => handleSubjectChange(mon, 'bonus', e.target.value)} />
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}

            <button onClick={handleCalculate} className="w-full bg-blue-600 text-white py-3.5 rounded-xl font-bold text-lg hover:bg-blue-700 shadow-lg shadow-blue-200 transition-all active:scale-95 flex justify-center items-center gap-2">
              <Calculator size={20}/> Tính điểm Xét tuyển
            </button>
          </div>

          {/* CỘT PHẢI: KẾT QUẢ MÔ PHỎNG */}
          <div className="lg:col-span-5 bg-gray-800 rounded-2xl p-6 text-white flex flex-col relative overflow-hidden shadow-2xl">
            <div className="absolute top-0 right-0 p-3 opacity-10"><Calculator size={100} /></div>
            
            <h3 className="font-bold text-xl text-yellow-400 mb-6 border-b border-gray-700 pb-3">Kết quả Mô phỏng</h3>
            
            {!result ? (
              <div className="flex-1 flex flex-col items-center justify-center text-gray-400 opacity-60">
                <Info size={48} className="mb-3" />
                <p className="text-center">Vui lòng nhập điểm và bấm nút<br/>để xem phân tích xét tuyển.</p>
              </div>
            ) : (
              <div className="animate-in fade-in slide-in-from-right-4 space-y-6 z-10">
                
                {/* HIỂN THỊ KẾT QUẢ ĐGNL */}
                {result.type === 'DGNL' && (
                  <>
                    <div className="bg-gray-700/50 p-4 rounded-xl border border-gray-600">
                      <p className="text-sm text-gray-400 mb-1">Tổng điểm quy đổi (Thang 30)</p>
                      <p className="text-4xl font-black text-white">{result.tong}</p>
                      <p className="text-xs text-gray-400 mt-2">Công thức: ({result.quyDoi} quy đổi) + ({result.uuTien} ưu tiên) + ({result.bonus} điểm cộng)</p>
                    </div>

                    <div className="space-y-3">
                      <div className={`p-4 rounded-xl flex items-start gap-3 ${result.datNguong ? 'bg-green-900/40 border border-green-500/50' : 'bg-red-900/40 border border-red-500/50'}`}>
                        {result.datNguong ? <CheckCircle className="text-green-400 shrink-0 mt-0.5" /> : <XCircle className="text-red-400 shrink-0 mt-0.5" />}
                        <div>
                          <p className="font-bold text-sm">Điểm Ngưỡng Sàn: {result.diemNguong}</p>
                          <p className={`text-xs mt-1 ${result.datNguong ? 'text-green-300' : 'text-red-300'}`}>
                            {result.datNguong ? 'ĐẠT điều kiện nộp hồ sơ.' : 'KHÔNG ĐẠT điều kiện nộp hồ sơ.'}
                          </p>
                        </div>
                      </div>

                      <div className={`p-4 rounded-xl flex items-start gap-3 ${result.datChuan ? 'bg-green-900/40 border border-green-500/50' : 'bg-gray-700/50 border border-gray-600'}`}>
                        {result.datChuan ? <CheckCircle className="text-green-400 shrink-0 mt-0.5" /> : <XCircle className="text-gray-400 shrink-0 mt-0.5" />}
                        <div>
                          <p className="font-bold text-sm">Điểm Chuẩn Trúng tuyển: {result.diemChuan}</p>
                          <p className={`text-xs mt-1 ${result.datChuan ? 'text-green-300' : 'text-gray-400'}`}>
                            {result.datChuan ? 'ĐẠT điểm trúng tuyển ngành này.' : 'Chưa đạt điểm trúng tuyển năm nay.'}
                          </p>
                        </div>
                      </div>
                    </div>
                  </>
                )}

                {/* HIỂN THỊ KẾT QUẢ VSAT/THPT */}
                {result.type === 'THPT' && (
                  <>
                    <div className="bg-gray-700/50 p-4 rounded-xl border border-gray-600 mb-4">
                      <p className="text-sm text-gray-400 mb-1">Tổ hợp môn cao điểm nhất</p>
                      <p className="text-4xl font-black text-white">{result.maxScore} <span className="text-lg text-yellow-400">{result.toHopResults[0].maTH}</span></p>
                      <p className="text-xs text-gray-400 mt-2">Đã bao gồm Quy đổi (nếu có) + Ưu tiên + Điểm cộng môn.</p>
                    </div>

                    <div className="mb-4">
                      <p className="text-xs font-bold uppercase text-gray-500 mb-2">Chi tiết các Tổ hợp hợp lệ</p>
                      <div className="space-y-2">
                        {result.toHopResults.map((th, idx) => (
                          <div key={idx} className={`p-2.5 rounded-lg flex justify-between items-center ${idx === 0 ? 'bg-blue-900/50 border border-blue-500/50' : 'bg-gray-700/30'}`}>
                            <div>
                              <span className="font-bold text-blue-300 mr-2">{th.maTH}</span>
                              <span className="text-xs text-gray-400 block md:inline">{th.chiTiet}</span>
                            </div>
                            <span className="font-bold">{th.diem}</span>
                          </div>
                        ))}
                      </div>
                    </div>

                    <div className="grid grid-cols-2 gap-3">
                       <div className={`p-3 rounded-lg border ${result.datNguong ? 'bg-green-900/20 border-green-500/30 text-green-300' : 'bg-red-900/20 border-red-500/30 text-red-300'}`}>
                          <p className="text-xs text-gray-400 mb-1">Điểm Ngưỡng ({result.diemNguong})</p>
                          <p className="font-bold text-sm flex items-center gap-1">
                            {result.datNguong ? <><CheckCircle size={14}/> ĐẠT</> : <><XCircle size={14}/> KHÔNG ĐẠT</>}
                          </p>
                       </div>
                       <div className={`p-3 rounded-lg border ${result.datChuan ? 'bg-green-900/20 border-green-500/30 text-green-300' : 'bg-gray-800 border-gray-600 text-gray-400'}`}>
                          <p className="text-xs text-gray-400 mb-1">Điểm Chuẩn ({result.diemChuan})</p>
                          <p className="font-bold text-sm flex items-center gap-1">
                            {result.datChuan ? <><CheckCircle size={14}/> TRÚNG TUYỂN</> : <><XCircle size={14}/> TRƯỢT</>}
                          </p>
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