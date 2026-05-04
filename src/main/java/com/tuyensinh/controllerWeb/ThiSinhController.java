package com.tuyensinh.controllerWeb;

import com.tuyensinh.model.ThiSinh;
import com.tuyensinh.service.ThiSinhService;
import com.tuyensinh.serviceWeb.ThiSinhWithNguyenVongServiceWeb;
import com.tuyensinh.ModelWeb.ThiSinhWithNguyenVongResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@RestController
@RequestMapping("/api/thisinh")
public class ThiSinhController {

    private final ThiSinhService thiSinhService = new ThiSinhService();
    private final ThiSinhWithNguyenVongServiceWeb thiSinhWithNguyenVongService = 
        new ThiSinhWithNguyenVongServiceWeb();

    /**
     * API: Lấy danh sách tất cả thí sinh
     * Method: GET
     * Endpoint: /api/thisinh
     */
    @GetMapping
    public ResponseEntity<List<ThiSinh>> getAllThiSinh() {
        List<ThiSinh> list = thiSinhService.getAll();
        return ResponseEntity.ok(list);
    }

    /**
     * API: Lấy thông tin thí sinh + điểm thi
     * Method: GET
     * Endpoint: /api/thisinh/{cccd}/{ngaySinh}
     * Input: CCCD và ngày sinh (định dạng: ddMMyyyy hoặc dd/MM/yyyy)
     */
    @GetMapping("/{cccd}/{ngaySinh}")
    public ResponseEntity<?> getByCccdAndNgaySinh(
            @PathVariable("cccd") String cccd,
            @PathVariable("ngaySinh") String ngaySinh) {
        
        Optional<ThiSinhService.ThiSinhWithDiemThi> result = 
            thiSinhService.getThiSinhWithDiemThi(cccd, ngaySinh);
        
        if (result.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("errorMessage", "Không tìm thấy điểm thí sinh với CCCD: " + cccd + " và ngày sinh: " + ngaySinh);
            return ResponseEntity.status(404).body(errorResponse);
        }
        
        return ResponseEntity.ok(result.get());
    }

    /**
     * API: Lấy thông tin thí sinh + danh sách nguyên vọng xét tuyển
     * Method: GET
     * Endpoint: /api/thisinh/{cccd}/{ngaySinh}/nguyenvong
     * Input: CCCD và ngày sinh (định dạng: ddMMyyyy hoặc dd/MM/yyyy)
     * Output: Thông tin thí sinh kèm toàn bộ dữ liệu xt_nguyenvongxettuyen qua nn_cccd
     */
    @GetMapping("/{cccd}/{ngaySinh}/nguyenvong")
    public ResponseEntity<?> getByCccdAndNgaySinhWithNguyenVong(
            @PathVariable("cccd") String cccd,
            @PathVariable("ngaySinh") String ngaySinh) {
        
        Optional<ThiSinhWithNguyenVongResponse> result = 
            thiSinhWithNguyenVongService.getThiSinhWithNguyenVong(cccd, ngaySinh);
        
        if (result.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("errorMessage", "Không tìm thấy thí sinh với CCCD: " + cccd + " và ngày sinh: " + ngaySinh);
            return ResponseEntity.status(404).body(errorResponse);
        }
        
        return ResponseEntity.ok(result.get());
    }
}