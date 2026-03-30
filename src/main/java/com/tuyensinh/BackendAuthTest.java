package com.tuyensinh;

import com.tuyensinh.database.HibernateUtil;
import com.tuyensinh.model.User;
import com.tuyensinh.model.RolePermission;
import com.tuyensinh.service.AuthService;
import com.tuyensinh.service.SessionManager;
import java.util.List;
import java.util.stream.Collectors;

public class BackendAuthTest {
    public static void main(String[] args) {
        System.out.println("========== KIỂM THỬ HỆ THỐNG PHÂN QUYỀN (BACKEND) ==========");

        // 1. Khởi tạo Service
        AuthService authService = new AuthService();
        
        // 2. Giả lập thông tin đăng nhập (Đảm bảo tài khoản này có trong DB xettuyen2026)
        String testUser = "admin2"; 
        String testPass = "$2y$12$examplehash3"; 

        System.out.println("[1] Đang tiến hành đăng nhập với Username: " + testUser);
        
        try {
            // 3. Gọi Service Login
            User user = authService.login(testUser, testPass);
            
            if (user != null) {
                System.out.println("=> ĐĂNG NHẬP THÀNH CÔNG!");
                
                System.out.println("--------------------------------------------------");
                System.out.println("THÔNG TIN USER:");
                System.out.println("- Họ tên: " + user.getFullName());
                System.out.println("- Vai trò (Role): " + (user.getRole() != null ? user.getRole().getName() : "CHƯA GÁN ROLE"));
                System.out.println("- Trạng thái: " + user.getStatus());
                System.out.println("- Lần đăng nhập cuối: " + user.getLastLogin());
                
                // 5. Kiểm tra danh sách Quyền (Permissions)
                List<String> permissions = new java.util.ArrayList<>();
                if (user.getRole() != null && user.getRole().getPermissions() != null) {
                    permissions = user.getRole().getPermissions()
                            .stream()
                            .map(RolePermission::getPermission)
                            .collect(Collectors.toList());
                }
                        
                System.out.println("--------------------------------------------------");
                System.out.println("DANH SÁCH QUYỀN TRUY XUẤT ĐƯỢC (" + permissions.size() + " quyền):");
                
                // Kiểm tra các nhóm quyền quan trọng
                check(permissions, "USER_VIEW");
                check(permissions, "THISINH_IMPORT");
                check(permissions, "DIEM_THONGKE");
                check(permissions, "NGUYENVONG_MANAGE");
                check(permissions, "QUYDOI_DELETE");

                // 6. Giả lập logic kiểm tra quyền tại Frontend Swing
                System.out.println("--------------------------------------------------");
                System.out.println("GIẢ LẬP LOGIC GIAO DIỆN (UI LOGIC):");
                
                if (SessionManager.hasPermission("USER_CHANGE_ROLE")) {
                    System.out.println("[UI] TRẠNG THÁI: Hiển thị Menu Quản lý phân quyền.");
                } else {
                    System.out.println("[UI] TRẠNG THÁI: Ẩn Menu Quản lý phân quyền.");
                }

                if (SessionManager.hasPermission("DIEM_EDIT")) {
                    System.out.println("[UI] TRẠNG THÁI: Cho phép nhấn nút 'Sửa điểm'.");
                }

            } else {
                System.out.println("=> ĐĂNG NHẬP THẤT BẠI: Sai tài khoản hoặc User bị khóa.");
            }
            
        } catch (Exception e) {
            System.err.println("=> LỖI HỆ THỐNG: " + e.getMessage());
        } finally {
            System.out.println("============================================================");
            HibernateUtil.shutdown();
        }
    }

    private static void check(List<String> permissions, String code) {
        if (permissions != null && permissions.contains(code)) {
            System.out.println("[OK] Đã load quyền: " + code);
        } else {
            System.out.println("[MISSING] Thiếu quyền: " + code);
        }
    }
}