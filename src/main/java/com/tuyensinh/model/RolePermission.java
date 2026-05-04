package com.tuyensinh.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "role_permissions")
@IdClass(RolePermissionId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePermission {

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private Role role;

    @Id
    @Column(nullable = false, length = 100)
    private String permission;
}