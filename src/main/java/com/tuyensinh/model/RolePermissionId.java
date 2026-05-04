package com.tuyensinh.model;

import java.io.Serializable;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionId implements Serializable {
    private Integer role; // Khớp với tên thuộc tính 'role' trong entity
    private String permission;
}