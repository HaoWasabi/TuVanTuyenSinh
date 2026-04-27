// Package: com.tuyensinh.modelWeb
package com.tuyensinh.ModelWeb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private boolean success;
    private String message;
    private ThiSinh thisinh;
    private DiemThi diemThi;
}