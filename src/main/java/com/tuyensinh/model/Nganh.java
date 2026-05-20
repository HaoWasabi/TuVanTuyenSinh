package com.tuyensinh.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "xt_nganh")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Nganh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idnganh")
    private Integer idnganh;

    @Column(name = "manganh", length = 45)
    private String manganh;

    @Column(name = "tennganh", length = 100)
    private String tennganh;

    @Column(name = "n_tohopgoc", length = 3)
    private String nTohopgoc;

    @Column(name = "n_chitieu")
    private Integer nChitieu;

    @Column(name = "n_diemsan", precision = 10, scale = 2)
    private BigDecimal nDiemsan;

    @Column(name = "n_diemtrungtuyen", precision = 10, scale = 2)
    private BigDecimal nDiemtrungtuyen;

    @Column(name = "n_tuyenthang", length = 1)
    private String nTuyenthang;

    @Column(name = "n_dgnl", length = 1)
    private String nDgnl;

    @Column(name = "n_thpt", length = 1)
    private String nThpt;

    @Column(name = "n_vsat", length = 1)
    private String nVsat;

    @Column(name = "sl_xtt")
    private Integer slXtt;

    @Column(name = "sl_dgnl")
    private Integer slDgnl;

    @Column(name = "sl_vsat")
    private Integer slVsat;

    @Column(name = "sl_thpt", length = 45)
    private Integer slThpt;

    @Column(name = "`status`", length = 20)
    private String status = "active";
}
