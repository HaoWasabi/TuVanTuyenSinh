package com.tuyensinh;

import com.tuyensinh.database.HibernateUtil;
import com.tuyensinh.model.Role;
import com.tuyensinh.model.User;
import com.tuyensinh.service.UserService;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Optional;

public class ServiceTestUser {

    public static void main(String[] args) {
        UserService service = new UserService();

        try {
            System.out.println("\n========== SERVICE TEST USER START ==========\n");

            // Đảm bảo Role tồn tại trong DB và lấy đối tượng "managed" từ Hibernate
            Role roleUser = getOrCreateRole("user");
            Role roleAdmin = getOrCreateRole("admin");
            Role roleInvalid = Role.builder().name("superadmin").build(); // Dùng để test lỗi

            // 1. Thêm User mới hợp lệ
            System.out.println("=== TEST 1: CREATE USER ===");
            User newUser = User.builder()
                    .username("nguyenvana_" + System.currentTimeMillis() % 1000)
                    .email("nguyenvana" + System.currentTimeMillis() % 1000 + "@gmail.com")
                    .password("hashed_password_abc")
                    .role(roleUser) // Truyền đối tượng Role thay vì String
                    .fullName("Nguyễn Văn A")
                    .status("active") // Đúng quy định
                    .build();
            User createdUser = service.create(newUser);
            System.out.println("[✓] Tạo user thành công: ID=" + createdUser.getId() + " - Role: " + createdUser.getRole() + " - Status: " + createdUser.getStatus());

            // 2. Test Sửa User hợp lệ
            System.out.println("\n=== TEST 2: UPDATE USER (VALID) ===");
            createdUser.setRole(roleAdmin);
            createdUser.setStatus("off");
            User updatedUser = service.update(createdUser);
            System.out.println("[✓] Sửa user thành công. Role mới: " + updatedUser.getRole().getName() + " - Status mới: " + updatedUser.getStatus());

            // 3. Test Sửa User với ROLE sai
            System.out.println("\n=== TEST 3: UPDATE USER (INVALID ROLE) ===");
            try {
                updatedUser.setRole(roleInvalid); // Lỗi logic nghiệp vụ trong service
                service.update(updatedUser);
            } catch (IllegalArgumentException e) {
                System.out.println("[✓] Hệ thống đã chặn thành công: " + e.getMessage());
            }
            
            // 4. Test Sửa User với STATUS sai
            System.out.println("\n=== TEST 4: UPDATE USER (INVALID STATUS) ===");
            try {
                updatedUser.setRole(roleAdmin); // Trả lại đúng role
                updatedUser.setStatus("online"); // Lỗi (phải là active hoặc off)
                service.update(updatedUser);
            } catch (IllegalArgumentException e) {
                System.out.println("[✓] Hệ thống đã chặn thành công: " + e.getMessage());
            }

            // Đưa về giá trị đúng để test xóa
            updatedUser.setStatus("active");
            service.update(updatedUser);

            // 5. Test Xóa User
            System.out.println("\n=== TEST 5: DELETE USER ===");
            boolean isDeleted = service.deleteById(updatedUser.getId());
            System.out.println("[✓] Đã xóa user ID " + updatedUser.getId() + ": " + (isDeleted ? "Thành công" : "Thất bại"));

            Optional<User> checkDeleted = service.getById(updatedUser.getId());
            if (checkDeleted.isEmpty()) {
                System.out.println("[✓] Xác nhận dữ liệu không còn trong DB.");
            }

            System.out.println("\n========== SERVICE TEST USER END ==========\n");

        } catch (Exception ex) {
            System.err.println("[✗] ERROR: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
        }
    }

    /**
     * Hàm bổ trợ: Tìm Role theo tên, nếu chưa có thì tạo mới và lưu vào DB.
     */
    private static Role getOrCreateRole(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Role role = session.createQuery("from Role where name = :name", Role.class)
                    .setParameter("name", name)
                    .uniqueResult();
            
            if (role == null) {
                Transaction tx = session.beginTransaction();
                role = Role.builder().name(name).isSystem(true).build();
                session.persist(role);
                tx.commit();
                System.out.println("[ℹ] Đã khởi tạo Role mới: " + name);
            }
            return role;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy/tạo Role: " + e.getMessage());
        }
    }
}