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
    @GetMapping(value = "/{maNganh}/to-hop", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public List<com.tuyensinh.ModelWeb.NganhTraCuuResponse.ToHopDiemInfo> getToHopByNganh(
            @PathVariable("maNganh") String maNganh) {
        try {
            List<com.tuyensinh.ModelWeb.NganhTraCuuResponse.ToHopDiemInfo> resultList = nganhServiceWeb
                    .searchTraCuuNganh(maNganh).stream()
                    .filter(n -> n.getMaNganh().equalsIgnoreCase(maNganh))
                    .findFirst()
                    .map(n -> {
                        List<com.tuyensinh.ModelWeb.NganhTraCuuResponse.ToHopDiemInfo> list = new java.util.ArrayList<>();
                        // Thêm tổ hợp gốc
                        if (n.getMaToHopGoc() != null) {
                            list.add(com.tuyensinh.ModelWeb.NganhTraCuuResponse.ToHopDiemInfo.builder()
                                    .maToHop(n.getMaToHopGoc())
                                    .tenToHop(n.getTenToHopGoc())
                                    .laToHopGoc(true)
                                    .build());
                        }
                        // Thêm các tổ hợp phụ
                        if (n.getToHopKhacList() != null) {
                            list.addAll(n.getToHopKhacList());
                        }
                        return list;
                    })
                    .orElse(java.util.Collections.emptyList());

            // Log ra terminal để kiểm tra
            System.out.println("DEBUG API: Danh sách tổ hợp cho ngành " + maNganh + " là:");
            resultList.forEach(t -> System.out.println("  - " + t.getMaToHop() + ": " + t.getTenToHop()
                    + (t.getLaToHopGoc() != null && t.getLaToHopGoc() ? " (Gốc)" : "")));

            return resultList;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách tổ hợp cho ngành: " + e.getMessage());
        }
    }
}
