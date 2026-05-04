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
@Table(name = "xt_diemcongxetuyen")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiemCong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`iddiemcong`")
    private Integer iddiemcong;

    @Column(name = "`ts_cccd`", length = 45)
    private String tsCccd;

    @Column(name = "`manganh`", length = 20)
    private String manganh;

    @Column(name = "`matohop`", length = 10)
    private String matohop;

    @Column(name = "`phuongthuc`", length = 45)
    private String phuongthuc;

    @Column(name = "`diemCC`", precision = 6, scale = 2)
    private BigDecimal diemCC;

    @Column(name = "`diemUtxt`", precision = 6, scale = 2)
    private BigDecimal diemUtxt;

    @Column(name = "`diemTong`", precision = 6, scale = 2)
    private BigDecimal diemTong;

    @Column(name = "`ghichu`", columnDefinition = "TEXT")
    private String ghichu;

    @Column(name = "`dc_keys`", length = 45)
    private String dcKeys;

    @Column(name = "`status`", length = 20)
    private String status = "active";
}