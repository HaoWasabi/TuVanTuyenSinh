
package com.tuyensinh.service;

import com.tuyensinh.model.User;
import com.tuyensinh.repository.UserRepository;

import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public List<User> searchByKeyword(String keyword) {
        return userRepository.searchByKeyword(keyword);
    }

    public User create(User user) {
        validateStatus(user);
        return userRepository.save(user);
    }

    public Optional<User> getById(Integer id) {
        return userRepository.findById(id);
    }

    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User update(User user) {
        validateStatus(user);
        return userRepository.update(user);
    }

    public boolean deleteById(Integer id) {
        return userRepository.deleteById(id);
    }

    private void validateStatus(User user) {
        if (user.getStatus() != null) {
            String status = user.getStatus().toUpperCase();
            if (!status.equals("ACTIVE") && !status.equals("OFF") && !status.equals("INACTIVE")) {
                throw new IllegalArgumentException("Lỗi: Status chỉ có thể là 'ACTIVE', 'OFF' hoặc 'INACTIVE'. Giá trị nhận được: " + user.getStatus());
            }
        }
    }
}