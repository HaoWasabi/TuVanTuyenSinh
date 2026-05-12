package com.tuyensinh.controllerWeb;

import com.tuyensinh.ModelWeb.DiemXetTuyenRequest;
import com.tuyensinh.ModelWeb.DiemXetTuyenResponse;
import com.tuyensinh.serviceWeb.DiemXetTuyenServiceWeb;
import org.springframework.web.bind.annotation.*;

/**
 * REST API Controller để tính điểm xét tuyển theo các phương thức
 * Endpoints:
 * - POST /diemxettuyenweb/DGNL - Đánh giá năng lực
 * - POST /diemxettuyenweb/vsat - Kỳ thi V-SAT
 * - POST /diemxettuyenweb/thpt - Thi THPT Quốc gia
 */
@RestController
@RequestMapping("/diemxettuyenweb")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DiemXetTuyenRestController {

    private final DiemXetTuyenServiceWeb service = new DiemXetTuyenServiceWeb();

    /**
     * Tính điểm DGNL (Đánh giá năng lực)
     * Input: Điểm ĐGNL (thang 1200) + Điểm cộng + Điểm ưu tiên
     */
    @PostMapping("/DGNL")
    public DiemXetTuyenResponse tinhDiemDGNL(@RequestBody DiemXetTuyenRequest request) {
        try {
            request.setPhuongThuc("DGNL");
            if (request.getMaToHop() == null || request.getMaToHop().isBlank()) {
                request.setMaToHop("DGNL");
            }
            return service.tinhDiemDGNL(request);
        } catch (Exception e) {
            return buildErrorResponse("DGNL", "Lỗi khi tính điểm DGNL: " + e.getMessage());
        }
    }

    /**
     * Tính điểm V-SAT
     * Input: 6 môn (thang 150) + Tổ hợp + Điểm cộng + Điểm ưu tiên
     */
    @PostMapping("/vsat")
    public DiemXetTuyenResponse tinhDiemVSAT(@RequestBody DiemXetTuyenRequest request) {
        try {
            request.setPhuongThuc("VSAT");
            if (request.getMaToHop() == null || request.getMaToHop().isBlank()) {
                request.setMaToHop("A00");
            }
            return service.tinhDiemVSAT(request);
        } catch (Exception e) {
            return buildErrorResponse("VSAT", "Lỗi khi tính điểm V-SAT: " + e.getMessage());
        }
    }

    /**
     * Tính điểm THPT Quốc gia
     * Input: 6 môn (thang 10) + Tổ hợp + Điểm cộng + Điểm ưu tiên
     */
    @PostMapping("/thpt")
    public DiemXetTuyenResponse tinhDiemTHPT(@RequestBody DiemXetTuyenRequest request) {
        try {
            request.setPhuongThuc("THPT");
            if (request.getMaToHop() == null || request.getMaToHop().isBlank()) {
                request.setMaToHop("A00");
            }
            return service.tinhDiemTHPT(request);
        } catch (Exception e) {
            return buildErrorResponse("THPT", "Lỗi khi tính điểm THPT: " + e.getMessage());
        }
    }

    private DiemXetTuyenResponse buildErrorResponse(String phuongThuc, String message) {
        return DiemXetTuyenResponse.builder()
                .phuongThuc(phuongThuc)
                .maToHop(null)
                .diemToHop(java.math.BigDecimal.ZERO)
                .diemQuyDoi(java.math.BigDecimal.ZERO)
                .diemCong(java.math.BigDecimal.ZERO)
                .diemUuTien(java.math.BigDecimal.ZERO)
                .diemXetTuyen(java.math.BigDecimal.ZERO)
                .thongBao(message)
                .datNguong(false)
                .error(true)
                .errorMessage(message)
                .build();
    }
}
