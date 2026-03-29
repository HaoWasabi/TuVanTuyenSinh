package com.tuyensinh.service;

import com.tuyensinh.model.User;
import com.tuyensinh.repository.UserRepository;

import java.util.Optional;

public class UserService {

    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public User create(User user) {
        validateRoleAndStatus(user);
        return userRepository.save(user);
    }

    public Optional<User> getById(Integer id) {
        return userRepository.findById(id);
    }

    public User update(User user) {
        validateRoleAndStatus(user);
        return userRepository.update(user);
    }

    public boolean deleteById(Integer id) {
        return userRepository.deleteById(id);
    }

    private void validateRoleAndStatus(User user) {
        if (user.getRole() != null) {
            String roleName = user.getRole().getName();
            if (!"admin".equalsIgnoreCase(roleName) && !"user".equalsIgnoreCase(roleName)) {
                throw new IllegalArgumentException("Lỗi: Role chỉ có thể là 'admin' hoặc 'user'. Giá trị nhận được: " + roleName);
            }
        }
        if (user.getStatus() != null && !user.getStatus().equals("active") && !user.getStatus().equals("off")) {
            throw new IllegalArgumentException("Lỗi: Status chỉ có thể là 'active' hoặc 'off'. Giá trị nhận được: " + user.getStatus());
        }
    }
}