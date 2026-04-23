import axiosClient from './axiosClient';

export const publicService = {
  // Lấy danh sách ngành - tổ hợp môn
  getNganhToHop: async () => {
    // Dữ liệu giả (Mock)
    return [
      { id: 1, maNganh: '7480201', tenNganh: 'Công nghệ thông tin', toHop: ['A00', 'A01', 'D01'] },
      { id: 2, maNganh: '7340101', tenNganh: 'Quản trị kinh doanh', toHop: ['A00', 'A01', 'D01', 'D07'] },
      { id: 3, maNganh: '7220201', tenNganh: 'Ngôn ngữ Anh', toHop: ['D01', 'D14', 'D15'] },
    ];
  },
  // Bảng quy đổi điểm
  getBangQuyDoi: async () => {
    return [
      { id: 1, loaiCC: 'IELTS', mucDiem: '6.5', diemQuyDoi: 10.0, monQuyDoi: 'Tiếng Anh' },
      { id: 2, loaiCC: 'IELTS', mucDiem: '6.0', diemQuyDoi: 9.5, monQuyDoi: 'Tiếng Anh' },
    ];
  }
};

export const authService = {
  login: async (cccd, password) => {
    // Giả lập API Login thành công nếu nhập đúng
    if (cccd && password === '123456') {
      return { token: 'fake-jwt-token', user: { cccd, name: 'Nguyễn Văn A', role: 'STUDENT' } };
    }
    throw new Error('Sai CCCD hoặc mật khẩu (Thử pass: 123456)');
  }
};