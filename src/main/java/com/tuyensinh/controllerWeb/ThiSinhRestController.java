package com.tuyensinh.controllerWeb;

import com.tuyensinh.model.ThiSinh;
import com.tuyensinh.model.DiemThi;
import com.tuyensinh.model.NguyenVong;
import com.tuyensinh.service.ThiSinhService;
import com.tuyensinh.service.DiemThiService;
import com.tuyensinh.service.NguyenVongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API Controller để lấy dữ liệu thí sinh cho frontend React
 * Endpoints:
 * - GET /api/thisinh/{cccd}/{dob} - Lấy thông tin & điểm của thí sinh
 * - GET /api/thisinh/{cccd}/{dob}/nguyenvong - Lấy danh sách nguyện vọng xét
 * tuyển
 */
@RestController
@RequestMapping("/api/thisinh")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ThiSinhRestController {

    @Autowired
    private ThiSinhService thiSinhService;

    @Autowired
    private DiemThiService diemThiService;

    @Autowired
    private NguyenVongService nguyenVongService;

    /**
     * Lấy thông tin & điểm của thí sinh (Dùng cho trang Tra cứu điểm)
     * 
     * @param cccd - CCCD của thí sinh
     * @param dob  - Ngày sinh (format: yyyy-MM-dd)
     * @return Map chứa thông tin thí sinh và điểm thi
     */
    @GetMapping("/{cccd}/{dob}")
    public Map<String, Object> getThiSinh(@PathVariable String cccd, @PathVariable String dob) {
        try {
            Map<String, Object> result = new HashMap<>();

            // Xác thực ngày sinh và lấy thí sinh
            if (thiSinhService.getByCccdAndNgaySinh(cccd, dob).isEmpty()) {
                throw new RuntimeException("Không tìm thấy thí sinh với CCCD: " + cccd + " và ngày sinh: " + dob);
            }

            ThiSinh thiSinh = thiSinhService.getByCccdAndNgaySinh(cccd, dob).get();
            result.put("thiSinh", thiSinh);

            // Lấy điểm thi
            List<DiemThi> diemThiList = diemThiService.getByCccd(cccd);
            if (!diemThiList.isEmpty()) {
                result.put("diemThi", diemThiList.get(0));
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy thông tin thí sinh: " + e.getMessage());
        }
    }

    /**
     * Lấy danh sách nguyện vọng xét tuyển của thí sinh (Dùng cho kết quả xét tuyển)
     * 
     * @param cccd - CCCD của thí sinh
     * @param dob  - Ngày sinh (format: yyyy-MM-dd)
     * @return Map chứa danh sách nguyện vọng
     */
    @GetMapping("/{cccd}/{dob}/nguyenvong")
    public Map<String, Object> getNguyenVong(@PathVariable String cccd, @PathVariable String dob) {
        try {
            Map<String, Object> result = new HashMap<>();

            // Xác thực thí sinh
            if (thiSinhService.getByCccdAndNgaySinh(cccd, dob).isEmpty()) {
                throw new RuntimeException("Không tìm thấy thí sinh với CCCD: " + cccd + " và ngày sinh: " + dob);
            }

            ThiSinh thiSinh = thiSinhService.getByCccdAndNgaySinh(cccd, dob).get();

            // Lấy danh sách nguyện vọng
            List<NguyenVong> nguyenVongs = nguyenVongService.getByCccd(cccd);
            result.put("nguyenVongs", nguyenVongs);
            result.put("thiSinh", thiSinh);

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy nguyện vọng: " + e.getMessage());
        }
    }
}
