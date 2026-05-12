package com.tuyensinh.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "xt_nguyenvongxettuyen")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NguyenVong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`idnv`")
    private Integer idnv;

    @Column(name = "`nn_cccd`", length = 45)
    private String nnCccd;

    @Column(name = "`nv_manganh`", length = 45)
    private String nvManganh;

    @Column(name = "`nv_tt`")
    private Integer nvTt;

    @Column(name = "`diem_thxt`", precision = 10, scale = 5)
    private BigDecimal diemThxt;

    @Column(name = "`diem_utqd`", precision = 10, scale = 5)
    private BigDecimal diemUtqd;

    @Column(name = "`diem_cong`", precision = 6, scale = 2)
    private BigDecimal diemCong;

    @Column(name = "`diem_xettuyen`", precision = 10, scale = 5)
    private BigDecimal diemXettuyen;

    @Column(name = "`nv_ketqua`", length = 45)
    private String nvKetqua;

    @Column(name = "`nv_keys`", length = 45)
    private String nvKeys;

    @Column(name = "`tt_phuongthuc`", length = 45)
    private String ttPhuongthuc;

    @Column(name = "`tt_thm`", length = 45)
    private String ttThm;

    @Transient
    private String tenNganh;

    public Integer getId() {
        return idnv;
    }

    public String getKetQua() {
        return nvKetqua;
    }

    public void setKetQua(String ketQua) {
        this.nvKetqua = ketQua;
    }
}