// Package: com.tuyensinh.daoWeb
package com.tuyensinh.ModelWeb;

import com.tuyensinh.ModelWeb.DiemThi;
import com.tuyensinh.ModelWeb.ThiSinh;

public interface ThiSinhDAO {
    ThiSinh findByCccdAndPassword(String cccd, String password);
    DiemThi findDiemThiBySobaodanh(String sobaodanh);
}