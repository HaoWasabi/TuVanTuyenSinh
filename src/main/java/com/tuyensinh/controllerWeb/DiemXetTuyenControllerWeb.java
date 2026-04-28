package com.tuyensinh.controllerWeb;

import com.tuyensinh.ModelWeb.DiemXetTuyenRequest;
import com.tuyensinh.ModelWeb.DiemXetTuyenResponse;
import com.tuyensinh.serviceWeb.DiemXetTuyenServiceWeb;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diemxettuyenweb")
public class DiemXetTuyenControllerWeb {

    private final DiemXetTuyenServiceWeb service = new DiemXetTuyenServiceWeb();

    /**
     * API 1: Tính điểm THPT
     * Method: POST
     * Endpoint: /api/diemxettuyenweb/thpt
     * Input: Điểm môn (thang 10) + Tổ hợp
     * Output: Điểm quy đổi (thang 30)
     */
    @PostMapping("/thpt")
    public ResponseEntity<DiemXetTuyenResponse> tinhDiemTHPT(@RequestBody DiemXetTuyenRequest request) {
        return ResponseEntity.ok(service.tinhDiemTHPT(request));
    }

    /**
     * API 2: Tính điểm VSAT
     * Method: POST
     * Endpoint: /api/diemxettuyenweb/vsat
     * Input: Điểm VSAT (thang 150) + Tổ hợp
     * Output: Điểm quy đổi (thang 30)
     */
    @PostMapping("/vsat")
    public ResponseEntity<DiemXetTuyenResponse> tinhDiemVSAT(@RequestBody DiemXetTuyenRequest request) {
        return ResponseEntity.ok(service.tinhDiemVSAT(request));
    }

    /**
     * API 3: Tính điểm DGNL
     * Method: POST
     * Endpoint: /api/diemxettuyenweb/dgnl
     * Input: Điểm ĐGNL (thang 1200)
     * Output: Điểm quy đổi (thang 30)
     */
    @PostMapping("/dgnl")
    public ResponseEntity<DiemXetTuyenResponse> tinhDiemDGNL(@RequestBody DiemXetTuyenRequest request) {
        return ResponseEntity.ok(service.tinhDiemDGNL(request));
    }
}