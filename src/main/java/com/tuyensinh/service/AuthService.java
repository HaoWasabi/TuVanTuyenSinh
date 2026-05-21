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

            // Kiểm tra mật khẩu trước
            if (!password.equals(user.getPassword())) {
                throw new RuntimeException("Tên đăng nhập hoặc mật khẩu không chính xác!");
            }

            // Mật khẩu đúng -> Kiểm tra trạng thái tài khoản
            if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
                throw new RuntimeException("Tài khoản của bạn đã bị khóa. Vui lòng liên hệ Admin!");
            }

            // Đăng nhập thành công
            SessionManager.initialize(user);
            userRepository.updateLastLogin(user.getId());
            return user;
        }
        
        throw new RuntimeException("Tên đăng nhập hoặc mật khẩu không chính xác!");
    }
}