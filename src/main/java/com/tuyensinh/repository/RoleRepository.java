package com.tuyensinh.repository;

import com.tuyensinh.database.HibernateUtil;
import com.tuyensinh.model.Role;
import com.tuyensinh.model.RolePermission;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RoleRepository {
    
    public Optional<Role> findById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Sử dụng JOIN FETCH để lấy quyền (permissions) ngay lập tức
            String hql = "SELECT DISTINCT r FROM Role r LEFT JOIN FETCH r.permissions WHERE r.id = :id and r.status = 'active'";
            return session.createQuery(hql, Role.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();
        }
    }

    public List<Role> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT r FROM Role r LEFT JOIN FETCH r.permissions WHERE r.status = 'ACTIVE' ORDER BY r.id";
            return session.createQuery(hql, Role.class).getResultList();
        }
    }

    public List<String> findAllPermissionCodes() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT rp.permission FROM RolePermission rp ORDER BY rp.permission";
            return session.createQuery(hql, String.class).getResultList();
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

    public Role update(Role role) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Role merged = (Role) session.merge(role);
            tx.commit();
            return merged;
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

    public void replacePermissions(Integer roleId, List<String> permissionCodes) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // 1. SỬA LẠI: Chỉ cho phép thay đổi quyền nếu Role đó còn đang 'ACTIVE'
            Role role = session.createQuery(
                            "from Role r where r.id = :roleId and r.status = 'ACTIVE'", Role.class)
                    .setParameter("roleId", roleId)
                    .uniqueResult();

            if (role == null) {
                throw new IllegalArgumentException("Role không tồn tại hoặc đã bị vô hiệu hóa (INACTIVE): " + roleId);
            }

            // 2. GIỮ NGUYÊN lệnh xóa cứng để làm sạch các quyền cũ của Role này
            session.createNativeQuery("DELETE FROM role_permissions WHERE role_id = :roleId")
                    .setParameter("roleId", roleId)
                    .executeUpdate();

            List<String> codes = permissionCodes == null ? new ArrayList<>() : permissionCodes.stream()
                    .filter(code -> code != null && !code.isBlank())
                    .distinct()
                    .toList();

            // 3. THÊM MỚI các quyền
            for (String code : codes) {
                RolePermission permission = RolePermission.builder()
                        .role(role)
                        .permission(code)
                        // Nếu bảng role_permissions của bạn CŨNG CÓ cột status thì bỏ comment dòng dưới:
                        // .status("ACTIVE")
                        .build();
                session.persist(permission);
            }

            tx.commit();
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            throw ex;
        }
    }
}