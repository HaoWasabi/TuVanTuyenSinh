package com.tuyensinh;

import com.tuyensinh.database.HibernateUtil;
import com.tuyensinh.model.Role;
import com.tuyensinh.model.User;
import com.tuyensinh.repository.UserRepository;
import org.hibernate.Session;

import java.util.Optional;

public class SeedLoginAccount {
    public static void main(String[] args) {
        final String username = "demo_user";
        final String password = "123456";
        final String email = "demo_user@local";
        final String fullName = "Demo User";

        UserRepository userRepository = new UserRepository();
        Integer roleId = resolveRoleId();

        try {
            Optional<User> existing = userRepository.findByUsername(username);
            if (existing.isPresent()) {
                User user = existing.get();
                user.setPassword(password);
                user.setEmail(email);
                user.setFullName(fullName);
                user.setStatus("active");
                user.setIdRoleValue(roleId);
                userRepository.update(user);
                System.out.println("Updated existing account: " + username);
            } else {
                User user = User.builder()
                        .username(username)
                        .password(password)
                        .email(email)
                        .fullName(fullName)
                        .status("active")
                        .idRoleValue(roleId)
                        .build();
                userRepository.save(user);
                System.out.println("Created new account: " + username);
            }

            System.out.println("LOGIN_USERNAME=" + username);
            System.out.println("LOGIN_PASSWORD=" + password);
        } finally {
            HibernateUtil.shutdown();
        }
    }

    private static Integer resolveRoleId() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Role adminRole = session.createQuery("from Role r where lower(r.name) = :name", Role.class)
                    .setParameter("name", "admin")
                    .setMaxResults(1)
                    .uniqueResult();
            if (adminRole != null) {
                return adminRole.getId();
            }

            Role userRole = session.createQuery("from Role r where lower(r.name) = :name", Role.class)
                    .setParameter("name", "user")
                    .setMaxResults(1)
                    .uniqueResult();
            if (userRole != null) {
                return userRole.getId();
            }
        }
        return 1;
    }
}