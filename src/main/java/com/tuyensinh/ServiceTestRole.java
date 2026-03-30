package com.tuyensinh;

import com.tuyensinh.database.HibernateUtil;
import com.tuyensinh.model.Role;
import com.tuyensinh.service.RoleService;
import java.util.Arrays;
import java.util.List;

public class ServiceTestRole {
    public static void main(String[] args) {
        RoleService roleService = new RoleService();

        try {
            System.out.println("========== TEST QUẢN LÝ NHÓM QUYỀN ==========");

            // 1. Test Thêm nhóm quyền mới
            System.out.println("\n[TEST 1] Tạo nhóm quyền mới...");
            String roleName = "CanBoTuyenSinh";
            Role newRole = roleService.createRole(roleName, "Nhóm quyền dành cho cán bộ tuyển sinh");
            System.out.println("[✓] Đã tạo Role: " + newRole.getName() + " (ID: " + newRole.getId() + ")");

            // 2. Test Cập nhật quyền (Thêm nếu chưa có)
            System.out.println("\n[TEST 2] Cập nhật danh sách quyền cho Role ID: " + newRole.getId());
            List<String> permissions = Arrays.asList("THISINH_VIEW", "THISINH_IMPORT", "DIEM_VIEW");
            
            roleService.updateRolePermissions(newRole.getId(), permissions);
            System.out.println("[✓] Đã gửi yêu cầu cập nhật 3 quyền.");

            // Kiểm tra lại (Giả lập lần 2 với 1 quyền cũ và 1 quyền mới)
            System.out.println("\n[TEST 3] Kiểm tra tính năng 'chỉ thêm nếu chưa có'...");
            List<String> nextPermissions = Arrays.asList("THISINH_VIEW", "QUYDOI_MANAGE"); 
            // "THISINH_VIEW" đã có từ bước trước, "QUYDOI_MANAGE" là quyền mới.
            
            roleService.updateRolePermissions(newRole.getId(), nextPermissions);
            System.out.println("[✓] Đã gửi yêu cầu cập nhật thêm (1 cũ, 1 mới).");

            // 3. Hiển thị kết quả cuối cùng của Role này để kiểm chứng
            new com.tuyensinh.repository.RoleRepository().findById(newRole.getId()).ifPresent(r -> {
                System.out.println("\nDANH SÁCH QUYỀN HIỆN TẠI CỦA ROLE '" + r.getName() + "':");
                r.getPermissions().forEach(p -> System.out.println(" - " + p.getPermission()));
                System.out.println("Tổng cộng: " + r.getPermissions().size() + " quyền (Kỳ vọng là 4).");
            });

        } catch (Exception e) {
            System.err.println("[✗] LỖI: " + e.getMessage());
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
            System.out.println("\n========== TEST HOÀN TẤT ==========");
        }
    }
}