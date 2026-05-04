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
@Table(name = "xt_nganh_tohop")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NganhToHop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    private Integer id;

    @Column(name = "`manganh`", length = 45)
    private String maNganh;

    @Column(name = "`matohop`", length = 45)
    private String maToHop;

    @Column(name = "`th_mon1`", length = 10)
    private String thMon1;

    @Column(name = "`hsmon1`")
    private Byte hsMon1;

    @Column(name = "`th_mon2`", length = 10)
    private String thMon2;

    @Column(name = "`hsmon2`")
    private Byte hsMon2;

    @Column(name = "`th_mon3`", length = 10)
    private String thMon3;

    @Column(name = "`hsmon3`")
    private Byte hsMon3;

    @Column(name = "`tb_keys`", length = 45)
    private String tbKeys;

    @Column(name = "`N1`")
    private boolean n1;

    @Column(name = "`TO`")
    private boolean to;

    @Column(name = "`LI`")
    private boolean li;

    @Column(name = "`HO`")
    private boolean ho;

    @Column(name = "`SI`")
    private boolean si;

    @Column(name = "`VA`")
    private boolean va;

    @Column(name = "`SU`")
    private boolean su;

    @Column(name = "`DI`")
    private boolean di;

    @Column(name = "`TI`")
    private boolean ti;

    @Column(name = "`KHAC`")
    private boolean khac;

    @Column(name = "`KTPL`")
    private boolean ktpl;

    @Column(name = "`dolech`", precision = 6, scale = 2)
    private BigDecimal doLech;

    @Column(name = "`status`", length = 20)
    private String status = "active";
}
