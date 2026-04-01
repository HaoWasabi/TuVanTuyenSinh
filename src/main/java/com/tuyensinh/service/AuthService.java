package com.tuyensinh.service;

import com.tuyensinh.model.User;
import com.tuyensinh.repository.UserRepository;
import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository = new UserRepository();

    /**
     * Xử lý logic đăng nhập
     */
    public User login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Debug: In ra để kiểm tra giá trị thực tế từ DB
            System.out.println("[DEBUG] DB Password: '" + user.getPassword() + "'");
            System.out.println("[DEBUG] DB Status: '" + user.getStatus() + "'");
            System.out.println("[DEBUG] DB id_role value: " + user.getIdRoleValue());

            if (user.getRole() == null) {
                System.err.println("[WARNING] Hibernate không thể map Role. Kiểm tra xem id=" + user.getIdRoleValue() + " có tồn tại trong bảng roles chưa?");
            }

            // Kiểm tra trạng thái và mật khẩu (So sánh Plain Text theo dữ liệu thực tế DB)
            if ("active".equalsIgnoreCase(user.getStatus()) && password.equals(user.getPassword())) {
                // Khởi tạo Session: Nạp thông tin User và danh sách Quyền (Permissions)
                SessionManager.initialize(user);
                
                // Cập nhật lần đăng nhập cuối
                userRepository.updateLastLogin(user.getId());
                return user;
            } else if (!"active".equalsIgnoreCase(user.getStatus())) {
                throw new RuntimeException("Tài khoản đang bị khóa hoặc chưa kích hoạt!");
            }
        }
        
        throw new RuntimeException("Tên đăng nhập hoặc mật khẩu không chính xác!");
    }
}