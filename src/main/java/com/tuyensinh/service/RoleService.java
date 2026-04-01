package com.tuyensinh.service;

import com.tuyensinh.model.Role;
import com.tuyensinh.repository.RoleRepository;
import java.util.List;

public class RoleService {
    private final RoleRepository roleRepository = new RoleRepository();

    /**
     * Chức năng 1: Thêm nhóm quyền mới (Role)
     */
    public Role createRole(String name, String description) {
        Role role = Role.builder().name(name).description(description).isSystem(false).build();
        return roleRepository.save(role);
    }

    /**
     * Chức năng 2: Cập nhật danh sách quyền, chỉ thêm nếu chưa có
     */
    public void updateRolePermissions(Integer roleId, List<String> permissionCodes) {
        roleRepository.addPermissionsIfMissing(roleId, permissionCodes);
    }
}