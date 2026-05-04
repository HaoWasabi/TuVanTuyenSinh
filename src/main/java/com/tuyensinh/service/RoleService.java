package com.tuyensinh.service;

import com.tuyensinh.model.Role;
import com.tuyensinh.repository.RoleRepository;
import java.util.List;

public class RoleService {
    private final RoleRepository roleRepository = new RoleRepository();

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public List<String> getAllPermissionCodes() {
        return roleRepository.findAllPermissionCodes();
    }

    public Role getRoleById(Integer roleId) {
        return roleRepository.findById(roleId).orElse(null);
    }

    /**
     * Chức năng 1: Thêm nhóm quyền mới (Role)
     */
    public Role createRole(String name, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên nhóm quyền không được để trống");
        }
        Role role = Role.builder().name(name.trim()).description(description == null ? null : description.trim()).isSystem(false).build();
        return roleRepository.save(role);
    }

    public Role updateRole(Integer roleId, String name, String description) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhóm quyền có ID: " + roleId));
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên nhóm quyền không được để trống");
        }
        role.setName(name.trim());
        role.setDescription(description == null ? null : description.trim());
        return roleRepository.update(role);
    }

    /**
     * Chức năng 2: Cập nhật danh sách quyền, chỉ thêm nếu chưa có
     */
    public void updateRolePermissions(Integer roleId, List<String> permissionCodes) {
        roleRepository.addPermissionsIfMissing(roleId, permissionCodes);
    }

    public void replaceRolePermissions(Integer roleId, List<String> permissionCodes) {
        roleRepository.replacePermissions(roleId, permissionCodes);
    }
}