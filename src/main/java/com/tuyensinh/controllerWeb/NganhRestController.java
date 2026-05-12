package com.tuyensinh.controllerWeb;

import com.tuyensinh.model.Nganh;
import com.tuyensinh.serviceWeb.NganhServiceWeb;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST API Controller để cấp dữ liệu ngành cho frontend React
 * Endpoints:
 * - GET /api/nganh - Lấy danh sách tên các ngành
 * - GET /api/nganh/{tenNganh} - Lấy chi tiết 1 ngành
 */
@RestController
@RequestMapping("/api/nganh")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class NganhRestController {

    private final NganhServiceWeb nganhServiceWeb = new NganhServiceWeb();

    /**
     * Lấy danh sách tên các ngành (dùng cho dropdown chọn ngành)
     * 
     * @return List<String> - Danh sách tên ngành
     */
    @GetMapping
    public List<String> getDanhSachNganh() {
        try {
            List<Nganh> nganhs = nganhServiceWeb.searchByKeyword(null);
            return nganhs.stream()
                    .map(Nganh::getTenNganh)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách ngành: " + e.getMessage());
        }
    }

    /**
     * Lấy chi tiết 1 ngành theo tên
     * 
     * @param tenNganh - Tên ngành
     * @return Nganh - Thông tin chi tiết ngành (điểm sàn, tổ hợp, v.v.)
     */
    @GetMapping("/{tenNganh}")
    public Nganh getChiTietNganh(@PathVariable String tenNganh) {
        try {
            List<Nganh> nganhs = nganhServiceWeb.searchByKeyword(tenNganh);
            if (nganhs.isEmpty()) {
                throw new RuntimeException("Không tìm thấy ngành: " + tenNganh);
            }
            return nganhs.get(0);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy chi tiết ngành: " + e.getMessage());
        }
    }
}
