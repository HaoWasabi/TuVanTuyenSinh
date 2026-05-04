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
@Table(name = "xt_diemthixettuyen")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiemThi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`iddiemthi`")
    private Integer iddiemthi;

    @Column(name = "`cccd`", length = 20)
    private String cccd;

    @Column(name = "`sobaodanh`", length = 45)
    private String sobaodanh;

    @Column(name = "`d_phuongthuc`", length = 10)
    private String dPhuongthuc;

    @Column(name = "`TO`", precision = 8, scale = 2)
    private BigDecimal toan;

    @Column(name = "`LI`", precision = 8, scale = 2)
    private BigDecimal vatLi;

    @Column(name = "`HO`", precision = 8, scale = 2)
    private BigDecimal hoaHoc;

    @Column(name = "`SI`", precision = 8, scale = 2)
    private BigDecimal sinhHoc;

    @Column(name = "`SU`", precision = 8, scale = 2)
    private BigDecimal lichSu;

    @Column(name = "`DI`", precision = 8, scale = 2)
    private BigDecimal diaLi;

    @Column(name = "`VA`", precision = 8, scale = 2)
    private BigDecimal nguVan;

    @Column(name = "`N1_THI`", precision = 8, scale = 2)
    private BigDecimal n1Thi;

    @Column(name = "`N1_CC`", precision = 8, scale = 2)
    private BigDecimal n1Cc;

    @Column(name = "`CNCN`", precision = 8, scale = 2)
    private BigDecimal cncn;

    @Column(name = "`CNNN`", precision = 8, scale = 2)
    private BigDecimal cnnn;

    @Column(name = "`TI`", precision = 8, scale = 2)
    private BigDecimal tinHoc;

    @Column(name = "`KTPL`", precision = 8, scale = 2)
    private BigDecimal ktpl;

    @Column(name = "`NL1`", precision = 8, scale = 2)
    private BigDecimal nl1;

    @Column(name = "`NK1`", precision = 8, scale = 2)
    private BigDecimal nk1;

    @Column(name = "`NK2`", precision = 8, scale = 2)
    private BigDecimal nk2;

    @Column(name = "`status`", length = 20)
    private String status = "active";
}