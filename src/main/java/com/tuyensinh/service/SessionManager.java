package com.tuyensinh.service;

import com.tuyensinh.model.User;
import com.tuyensinh.model.RolePermission;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lớp Static lưu trữ thông tin phiên làm việc cho Java Swing
 */
public class SessionManager {
    private static User currentUser;
    private static List<String> permissions = new ArrayList<>();

    /**
     * Khởi tạo session sau khi đăng nhập thành công
     */
    public static void initialize(User user) {
        currentUser = user;
        if (user != null && user.getRole() != null && user.getRole().getPermissions() != null) {
            permissions = user.getRole().getPermissions().stream()
                    .map(RolePermission::getPermission)
                    .collect(Collectors.toList());
        } else {
            permissions = new ArrayList<>();
        }
    }

    public static void logout() {
        currentUser = null;
        permissions.clear();
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean hasPermission(String code) {
        return permissions.contains(code);
    }
}