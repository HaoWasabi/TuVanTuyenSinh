import axios from 'axios';

const axiosClient = axios.create({
  baseURL: 'http://localhost:8081/api', // Trỏ đúng vào backend Spring Boot
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptors (Bộ đánh chặn response)
axiosClient.interceptors.response.use(
  (response) => {
    // Nếu backend trả về HTTP 200 nhưng bên trong có báo lỗi (dựa theo tài liệu API)
    if (response.data && response.data.error === true) {
      return Promise.reject(response.data);
    }
    return response.data;
  },
  (error) => {
    // Xử lý lỗi HTTP (400, 401, 500...)
    if (error.response && error.response.data) {
      return Promise.reject(error.response.data);
    }
    return Promise.reject(error);
  }
);

export default axiosClient;