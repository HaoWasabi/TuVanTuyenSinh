package com.tuyensinh.controllerWeb;

import com.tuyensinh.model.ThiSinh;
import com.tuyensinh.model.DiemThi;
import com.tuyensinh.service.ThiSinhService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/thisinh")
public class ThiSinhController {

    private final ThiSinhService thiSinhService = new ThiSinhService();

    @GetMapping
    public ResponseEntity<List<ThiSinh>> getAllThiSinh() {
        List<ThiSinh> list = thiSinhService.getAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{cccd}/{ngaySinh}")
    public ResponseEntity<?> getByCccdAndNgaySinh(
            @PathVariable("cccd") String cccd,
            @PathVariable("ngaySinh") String ngaySinh) {
        
        Optional<ThiSinhService.ThiSinhWithDiemThi> result = 
            thiSinhService.getThiSinhWithDiemThi(cccd, ngaySinh);
        
        return result.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}