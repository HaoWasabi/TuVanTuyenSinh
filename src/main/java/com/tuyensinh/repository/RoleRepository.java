package com.tuyensinh.repository;

import com.tuyensinh.database.HibernateUtil;
import com.tuyensinh.model.Role;
import com.tuyensinh.model.RolePermission;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RoleRepository {
    
    public Optional<Role> findById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Sử dụng JOIN FETCH để lấy quyền (permissions) ngay lập tức
            String hql = "SELECT DISTINCT r FROM Role r LEFT JOIN FETCH r.permissions WHERE r.id = :id";
            return session.createQuery(hql, Role.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();
        }
    }

    public Role save(Role role) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            
            // Tìm ID lớn nhất hiện có, nếu bảng trống thì mặc định là 0
            Integer maxId = session.createQuery("SELECT COALESCE(MAX(r.id), 0) FROM Role r", Integer.class)
                    .uniqueResult();
            role.setId(maxId + 1); // Đánh số ID tiếp theo dựa trên thực tế

            session.persist(role);
            tx.commit();
            return role;
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            throw ex;
        }
    }

    public void addPermissionsIfMissing(Integer roleId, List<String> permissionCodes) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            
            // Lấy Role kèm theo danh sách quyền hiện tại
            String hql = "SELECT DISTINCT r FROM Role r LEFT JOIN FETCH r.permissions WHERE r.id = :id";
            Role role = session.createQuery(hql, Role.class)
                    .setParameter("id", roleId)
                    .uniqueResult();

            if (role != null) {
                List<String> existing = role.getPermissions().stream()
                        .map(RolePermission::getPermission)
                        .collect(Collectors.toList());

                for (String code : permissionCodes) {
                    // Chỉ thêm nếu permission chưa tồn tại cho Role này
                    if (!existing.contains(code)) {
                        RolePermission rp = RolePermission.builder()
                                .role(role)
                                .permission(code)
                                .build();
                        session.persist(rp);
                    }
                }
            }
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            throw ex;
        }
    }
}