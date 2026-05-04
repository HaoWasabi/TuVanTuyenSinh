import axiosClient from './axiosClient';

// 1. Lấy thông tin & điểm của Thí sinh (Dùng cho trang Tra cứu điểm)
export const apiGetThiSinh = (cccd, dob) => {
  return axiosClient.get(`/thisinh/${cccd}/${dob}`);
};

// MỚI THÊM: Lấy thông tin & danh sách nguyện vọng xét tuyển (Dùng cho kết quả xét tuyển)
export const apiTraCuuNguyenVong = (cccd, dob) => {
  return axiosClient.get(`/thisinh/${cccd}/${dob}/nguyenvong`);
};

// 2. Tính điểm - Phương thức Đánh giá năng lực (DGNL)
export const apiTinhDiemDGNL = (payload) => {
  return axiosClient.post('/diemxettuyenweb/DGNL', payload);
};

// 3. Tính điểm - Phương thức VSAT
export const apiTinhDiemVSAT = (payload) => {
  return axiosClient.post('/diemxettuyenweb/vsat', payload);
};

// 4. Tính điểm - Phương thức THPT
export const apiTinhDiemTHPT = (payload) => {
  return axiosClient.post('/diemxettuyenweb/thpt', payload);
};

// 5. Lấy danh sách tất cả các ngành
export const apiGetDanhSachNganh = () => {
  return axiosClient.get('/nganh');
};

// 6. Lấy thông tin chi tiết của 1 ngành (Điểm sàn, điểm chuẩn, tổ hợp...)
export const apiGetChiTietNganh = (tenNganh) => {
  // Cần encodeURIComponent để xử lý các dấu cách tiếng Việt (VD: Công%20nghệ)
  return axiosClient.get(`/nganh/${encodeURIComponent(tenNganh)}`);
};

// 7. Dịch vụ xác thực (Mock Login)
export const authService = {
  login: async (cccd, password) => {
    // Giả lập API Login thành công nếu nhập đúng
    if (cccd && password === '123456') {
      return { token: 'fake-jwt-token', user: { cccd, name: 'Nguyễn Văn A', role: 'STUDENT' } };
    }
    throw new Error('Sai CCCD hoặc mật khẩu (Thử pass: 123456)');
  }
};