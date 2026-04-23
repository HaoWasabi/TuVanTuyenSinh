import axios from 'axios';

const axiosClient = axios.create({
  baseURL: '/api', // Vite proxy sẽ chuyển cái này thành http://localhost:8080/api
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor xử lý response chung
axiosClient.interceptors.response.use(
  (response) => response.data,
  (error) => {
    console.error("Lỗi gọi API:", error);
    return Promise.reject(error);
  }
);

export default axiosClient;