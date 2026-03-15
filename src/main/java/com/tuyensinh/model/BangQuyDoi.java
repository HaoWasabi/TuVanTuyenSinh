package com.tuyensinh.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "xt_bangquydoi")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BangQuyDoi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`idqd`")
    private Integer idqd;

    @Column(name = "`d_phuongthuc`", length = 45)
    private String dPhuongthuc;

    @Column(name = "`d_tohop`", length = 45)
    private String dTohop;

    @Column(name = "`d_mon`", length = 45)
    private String dMon;

    @Column(name = "`d_diema`", precision = 6, scale = 2)
    private BigDecimal dDiema;

    @Column(name = "`d_diemb`", precision = 6, scale = 2)
    private BigDecimal dDiemb;

    @Column(name = "`d_diemc`", precision = 6, scale = 2)
    private BigDecimal dDiemc;

    @Column(name = "`d_diemd`", precision = 6, scale = 2)
    private BigDecimal dDiemd;

    @Column(name = "`d_maquydoi`", length = 255)
    private String dMaquydoi;

    @Column(name = "`d_phanvi`", length = 255)
    private String dPhanvi;
}
