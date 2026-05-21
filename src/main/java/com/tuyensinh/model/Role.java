package com.tuyensinh.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    @Id
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_system")
    private boolean isSystem;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<RolePermission> permissions;

    @Builder.Default
    @Column(name = "status", length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'active'")
    private String status = "active";
}