// Package: com.tuyensinh.serviceWeb
package com.tuyensinh.serviceWeb;

import com.tuyensinh.ModelWeb.ThiSinhDAO;
import com.tuyensinh.ModelWeb.DiemThi;
import com.tuyensinh.ModelWeb.LoginRequest;
import com.tuyensinh.ModelWeb.LoginResponse;
import com.tuyensinh.ModelWeb.ThiSinh;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThiSinhServiceImpl implements ThiSinhService {

    @Autowired
    private ThiSinhDAO thiSinhDAO;

    @Override
    public LoginResponse login(LoginRequest request) {
        ThiSinh thiSinh = thiSinhDAO.findByCccdAndPassword(request.getUsername(), request.getPassword());
        
        if (thiSinh == null) {
            return new LoginResponse(false, "Không tìm thấy thí sinh", null, null);
        }
        
        DiemThi diemThi = thiSinhDAO.findDiemThiBySobaodanh(thiSinh.getSobaodanh());
        
        return new LoginResponse(true, null, thiSinh, diemThi);
    }
}