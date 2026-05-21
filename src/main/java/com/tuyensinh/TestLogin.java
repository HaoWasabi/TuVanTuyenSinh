package com.tuyensinh;

import com.tuyensinh.service.AuthService;
import com.tuyensinh.model.User;

public class TestLogin {
    public static void main(String[] args) {
        AuthService authService = new AuthService();
        try {
            User user = authService.login("giamthi1", "anduong");
            System.out.println("Login success");
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }
        System.exit(0);
    }
}
