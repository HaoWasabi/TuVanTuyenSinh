package com.tuyensinh.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "xt_tohop_monthi")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TohopMonthi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idtohop")
    private Integer idtohop;

    @Column(name = "matohop", length = 45)
    private String matohop;

    @Column(name = "mon1", length = 10)
    private String mon1;

    @Column(name = "mon2", length = 10)
    private String mon2;

    @Column(name = "mon3", length = 10)
    private String mon3;

    @Column(name = "tentohop", length = 100)
    private String tentohop;
}
