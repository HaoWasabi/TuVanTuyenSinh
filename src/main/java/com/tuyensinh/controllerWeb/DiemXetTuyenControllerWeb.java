package com.tuyensinh.controllerWeb;

import com.tuyensinh.ModelWeb.DiemXetTuyenRequest;
import com.tuyensinh.ModelWeb.DiemXetTuyenResponse;
import com.tuyensinh.serviceWeb.DiemXetTuyenServiceWeb;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping({ "/cong-cu-tinh", "/cong-cu-tinh-diem", "/tinh-diem" })
public class DiemXetTuyenControllerWeb {

    private final DiemXetTuyenServiceWeb service = new DiemXetTuyenServiceWeb();
    private final com.tuyensinh.serviceWeb.NganhServiceWeb nganhService = new com.tuyensinh.serviceWeb.NganhServiceWeb();

    /**
     * Tải giao diện mặc định hoặc tính toán qua URL Query String (GET)
     * Ví dụ hỗ trợ link: /cong-cu-tinh?phuongThucForm=thpt&maToHop=A00&toan=8...
     */
    @GetMapping
    public String index(
            @ModelAttribute("request") DiemXetTuyenRequest request,
            @RequestParam(value = "phuongThucForm", required = false) String phuongThucForm,
            Model model) {

        // Nếu không truyền phuongThucForm (lần đầu vào trang), gán request mặc định
        // rỗng
        if (phuongThucForm == null || phuongThucForm.isBlank()) {
            model.addAttribute("request", defaultRequest());
            model.addAttribute("activeTab", "thpt");
        } else {
            // Nếu có truyền phuongThucForm qua GET, tái sử dụng logic tính toán chung
            thucHienLogicTinhDiem(request, phuongThucForm, model);
        }

        model.addAttribute("nganhList", nganhService.getAllNganh());
        return "cong-cu-tinh-diem";
    }

    /**
     * Xử lý tập trung tất cả các yêu cầu Submit Form (POST) gửi về /cong-cu-tinh
     */
    @PostMapping
    public String tinhDiemPost(
            @ModelAttribute("request") DiemXetTuyenRequest request,
            @RequestParam(value = "phuongThucForm", defaultValue = "thpt") String phuongThucForm,
            Model model) {

        thucHienLogicTinhDiem(request, phuongThucForm, model);

        model.addAttribute("nganhList", nganhService.getAllNganh());
        return "cong-cu-tinh-diem";
    }

    /**
     * Hàm trung gian đóng gói logic tính toán để tránh lặp code (DRY Principle)
     */
    private void thucHienLogicTinhDiem(DiemXetTuyenRequest request, String phuongThucForm, Model model) {
        try {
            DiemXetTuyenResponse response = null;

            if ("vsat".equalsIgnoreCase(phuongThucForm)) {
                request.setPhuongThuc("VSAT");
                if (request.getMaToHop() == null || request.getMaToHop().isBlank()) {
                    request.setMaToHop("A00");
                }
                response = service.tinhDiemVSAT(request);
                model.addAttribute("activeTab", "vsat");

            } else if ("dgnl".equalsIgnoreCase(phuongThucForm)) {
                request.setPhuongThuc("DGNL");
                request.setMaToHop("DGNL");
                response = service.tinhDiemDGNL(request);
                model.addAttribute("activeTab", "dgnl");

            } else { // Mặc định hoặc "thpt"
                request.setPhuongThuc("THPT");
                if (request.getMaToHop() == null || request.getMaToHop().isBlank()) {
                    request.setMaToHop("A00");
                }
                response = service.tinhDiemTHPT(request);
                model.addAttribute("activeTab", "thpt");
            }

            model.addAttribute("result", response);

            // Xử lý logic đối sánh Điểm Sàn / Điểm Chuẩn của Ngành đã chọn
            if (request.getManganh() != null && !request.getManganh().isBlank()) {
                final DiemXetTuyenResponse finalResponse = response;
                nganhService.getByMaNganh(request.getManganh()).ifPresent(n -> {
                    model.addAttribute("selectedNganh", n);
                    try {
                        java.math.BigDecimal diemXet = finalResponse.getDiemXetTuyen();
                        java.math.BigDecimal diemsan = n.getnDiemsan() == null ? java.math.BigDecimal.ZERO
                                : n.getnDiemsan();
                        java.math.BigDecimal diemchuan = n.getnDiemtrungtuyen() == null ? java.math.BigDecimal.ZERO
                                : n.getnDiemtrungtuyen();

                        model.addAttribute("datDiemSan", diemXet.compareTo(diemsan) >= 0);
                        model.addAttribute("datDiemChuan", diemXet.compareTo(diemchuan) >= 0);
                    } catch (Exception ex) {
                        // Bỏ qua lỗi so sánh nếu có trường dữ liệu bị null
                    }
                });
            }

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("activeTab", phuongThucForm); // Giữ lại tab đang thao tác khi xảy ra lỗi
        }
    }

    private DiemXetTuyenRequest defaultRequest() {
        return DiemXetTuyenRequest.builder()
                .maToHop("A00")
                .phuongThuc("THPT")
                .khuVuc("0")
                .doiTuong("0")
                .toan(0.0)
                .li(0.0)
                .hoa(0.0)
                .sinh(0.0)
                .su(0.0)
                .di(0.0)
                .va(0.0)
                .n1(0.0)
                .diemTong(0.0)
                .diemCong(0.0)
                .diemUuTien(0.0)
                .build();
    }
}