// Package: com.tuyensinh.serviceWeb
package com.tuyensinh.serviceWeb;

import com.tuyensinh.ModelWeb.LoginRequest;
import com.tuyensinh.ModelWeb.LoginResponse;

public interface ThiSinhService {
    LoginResponse login(LoginRequest request);
}