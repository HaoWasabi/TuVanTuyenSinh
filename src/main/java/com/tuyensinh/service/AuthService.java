package com.tuyensinh.service;

import com.tuyensinh.model.User;
import com.tuyensinh.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
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
            
            // Kiểm tra trạng thái và mật khẩu (Sử dụng BCrypt)
            if ("active".equalsIgnoreCase(user.getStatus()) && BCrypt.checkpw(password, user.getPassword())) {
                // Cập nhật lần đăng nhập cuối
                userRepository.updateLastLogin(user.getId());
                return user;
            }
        }
        
        throw new RuntimeException("Tên đăng nhập hoặc mật khẩu không chính xác!");
    }
}