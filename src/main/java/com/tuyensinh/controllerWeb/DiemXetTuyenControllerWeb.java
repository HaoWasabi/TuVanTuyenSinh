package com.tuyensinh.controllerWeb;

import com.tuyensinh.ModelWeb.DiemXetTuyenRequest;
import com.tuyensinh.ModelWeb.DiemXetTuyenResponse;
import com.tuyensinh.serviceWeb.DiemXetTuyenServiceWeb;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping({ "/cong-cu-tinh-diem", "/tinh-diem", "/cong-cu-tinh" })
public class DiemXetTuyenControllerWeb {

    private final DiemXetTuyenServiceWeb service = new DiemXetTuyenServiceWeb();
    private final com.tuyensinh.serviceWeb.NganhServiceWeb nganhService = new com.tuyensinh.serviceWeb.NganhServiceWeb();

    @GetMapping
    public String index(Model model) {
        model.addAttribute("request", defaultRequest());
        model.addAttribute("nganhList", nganhService.getAllNganh());
        model.addAttribute("activeTab", "thpt");
        return "cong-cu-tinh-diem";
    }

    /**
     * Tính điểm THPT
     */
    @PostMapping("/thpt")
    public String tinhDiemTHPT(@ModelAttribute("request") DiemXetTuyenRequest request, Model model) {

        try {
            request.setPhuongThuc("THPT");
            if (request.getMaToHop() == null || request.getMaToHop().isBlank()) {
                request.setMaToHop("A00");
            }

            DiemXetTuyenResponse response = service.tinhDiemTHPT(request);
            model.addAttribute("result", response);
            // Nếu chọn ngành, đưa thông tin ngành và so sánh với điểm
            if (request.getManganh() != null && !request.getManganh().isBlank()) {
                nganhService.getByMaNganh(request.getManganh()).ifPresent(n -> {
                    model.addAttribute("selectedNganh", n);
                    try {
                        java.math.BigDecimal diemXet = response.getDiemXetTuyen();
                        java.math.BigDecimal diemsan = n.getnDiemsan() == null ? java.math.BigDecimal.ZERO
                                : n.getnDiemsan();
                        java.math.BigDecimal diemchuan = n.getnDiemtrungtuyen() == null ? java.math.BigDecimal.ZERO
                                : n.getnDiemtrungtuyen();
                        model.addAttribute("datDiemSan", diemXet.compareTo(diemsan) >= 0);
                        model.addAttribute("datDiemChuan", diemXet.compareTo(diemchuan) >= 0);
                    } catch (Exception ex) {
                        // ignore comparison errors
                    }
                });
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        model.addAttribute("nganhList", nganhService.getAllNganh());
        model.addAttribute("activeTab", "thpt");
        return "cong-cu-tinh-diem";
    }

    // Support GET-based calculation so the tool works via query-string links
    @GetMapping("/thpt")
    public String tinhDiemTHPTGet(@ModelAttribute("request") DiemXetTuyenRequest request, Model model) {
        // Reuse POST logic behavior
        try {
            request.setPhuongThuc("THPT");
            if (request.getMaToHop() == null || request.getMaToHop().isBlank()) {
                request.setMaToHop("A00");
            }

            DiemXetTuyenResponse response = service.tinhDiemTHPT(request);
            model.addAttribute("result", response);
            if (request.getManganh() != null && !request.getManganh().isBlank()) {
                nganhService.getByMaNganh(request.getManganh()).ifPresent(n -> {
                    model.addAttribute("selectedNganh", n);
                    try {
                        java.math.BigDecimal diemXet = response.getDiemXetTuyen();
                        java.math.BigDecimal diemsan = n.getnDiemsan() == null ? java.math.BigDecimal.ZERO
                                : n.getnDiemsan();
                        java.math.BigDecimal diemchuan = n.getnDiemtrungtuyen() == null ? java.math.BigDecimal.ZERO
                                : n.getnDiemtrungtuyen();
                        model.addAttribute("datDiemSan", diemXet.compareTo(diemsan) >= 0);
                        model.addAttribute("datDiemChuan", diemXet.compareTo(diemchuan) >= 0);
                    } catch (Exception ex) {
                        // ignore
                    }
                });
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        model.addAttribute("nganhList", nganhService.getAllNganh());
        model.addAttribute("activeTab", "thpt");
        return "cong-cu-tinh-diem";
    }

    @PostMapping("/vsat")
    public String tinhDiemVSAT(@ModelAttribute("request") DiemXetTuyenRequest request, Model model) {

        try {
            request.setPhuongThuc("VSAT");
            if (request.getMaToHop() == null || request.getMaToHop().isBlank()) {
                request.setMaToHop("A00");
            }

            DiemXetTuyenResponse response = service.tinhDiemVSAT(request);
            model.addAttribute("result", response);
            if (request.getManganh() != null && !request.getManganh().isBlank()) {
                nganhService.getByMaNganh(request.getManganh()).ifPresent(n -> {
                    model.addAttribute("selectedNganh", n);
                    try {
                        java.math.BigDecimal diemXet = response.getDiemXetTuyen();
                        java.math.BigDecimal diemsan = n.getnDiemsan() == null ? java.math.BigDecimal.ZERO
                                : n.getnDiemsan();
                        java.math.BigDecimal diemchuan = n.getnDiemtrungtuyen() == null ? java.math.BigDecimal.ZERO
                                : n.getnDiemtrungtuyen();
                        model.addAttribute("datDiemSan", diemXet.compareTo(diemsan) >= 0);
                        model.addAttribute("datDiemChuan", diemXet.compareTo(diemchuan) >= 0);
                    } catch (Exception ex) {
                    }
                });
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        model.addAttribute("nganhList", nganhService.getAllNganh());
        model.addAttribute("activeTab", "vsat");
        return "cong-cu-tinh-diem";
    }

    @GetMapping("/vsat")
    public String tinhDiemVSATGet(@ModelAttribute("request") DiemXetTuyenRequest request, Model model) {
        try {
            request.setPhuongThuc("VSAT");
            if (request.getMaToHop() == null || request.getMaToHop().isBlank()) {
                request.setMaToHop("A00");
            }

            DiemXetTuyenResponse response = service.tinhDiemVSAT(request);
            model.addAttribute("result", response);
            if (request.getManganh() != null && !request.getManganh().isBlank()) {
                nganhService.getByMaNganh(request.getManganh()).ifPresent(n -> {
                    model.addAttribute("selectedNganh", n);
                    try {
                        java.math.BigDecimal diemXet = response.getDiemXetTuyen();
                        java.math.BigDecimal diemsan = n.getnDiemsan() == null ? java.math.BigDecimal.ZERO
                                : n.getnDiemsan();
                        java.math.BigDecimal diemchuan = n.getnDiemtrungtuyen() == null ? java.math.BigDecimal.ZERO
                                : n.getnDiemtrungtuyen();
                        model.addAttribute("datDiemSan", diemXet.compareTo(diemsan) >= 0);
                        model.addAttribute("datDiemChuan", diemXet.compareTo(diemchuan) >= 0);
                    } catch (Exception ex) {
                    }
                });
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        model.addAttribute("nganhList", nganhService.getAllNganh());
        model.addAttribute("activeTab", "vsat");
        return "cong-cu-tinh-diem";
    }

    @PostMapping("/dgnl")
    public String tinhDiemDGNL(@ModelAttribute("request") DiemXetTuyenRequest request, Model model) {

        try {
            request.setPhuongThuc("DGNL");
            request.setMaToHop("DGNL");

            DiemXetTuyenResponse response = service.tinhDiemDGNL(request);
            model.addAttribute("result", response);
            if (request.getManganh() != null && !request.getManganh().isBlank()) {
                nganhService.getByMaNganh(request.getManganh()).ifPresent(n -> {
                    model.addAttribute("selectedNganh", n);
                    try {
                        java.math.BigDecimal diemXet = response.getDiemXetTuyen();
                        java.math.BigDecimal diemsan = n.getnDiemsan() == null ? java.math.BigDecimal.ZERO
                                : n.getnDiemsan();
                        java.math.BigDecimal diemchuan = n.getnDiemtrungtuyen() == null ? java.math.BigDecimal.ZERO
                                : n.getnDiemtrungtuyen();
                        model.addAttribute("datDiemSan", diemXet.compareTo(diemsan) >= 0);
                        model.addAttribute("datDiemChuan", diemXet.compareTo(diemchuan) >= 0);
                    } catch (Exception ex) {
                    }
                });
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        model.addAttribute("nganhList", nganhService.getAllNganh());
        model.addAttribute("activeTab", "dgnl");
        return "cong-cu-tinh-diem";
    }

    @GetMapping("/dgnl")
    public String tinhDiemDGNLGet(@ModelAttribute("request") DiemXetTuyenRequest request, Model model) {
        try {
            request.setPhuongThuc("DGNL");
            request.setMaToHop("DGNL");

            DiemXetTuyenResponse response = service.tinhDiemDGNL(request);
            model.addAttribute("result", response);
            if (request.getManganh() != null && !request.getManganh().isBlank()) {
                nganhService.getByMaNganh(request.getManganh()).ifPresent(n -> {
                    model.addAttribute("selectedNganh", n);
                    try {
                        java.math.BigDecimal diemXet = response.getDiemXetTuyen();
                        java.math.BigDecimal diemsan = n.getnDiemsan() == null ? java.math.BigDecimal.ZERO
                                : n.getnDiemsan();
                        java.math.BigDecimal diemchuan = n.getnDiemtrungtuyen() == null ? java.math.BigDecimal.ZERO
                                : n.getnDiemtrungtuyen();
                        model.addAttribute("datDiemSan", diemXet.compareTo(diemsan) >= 0);
                        model.addAttribute("datDiemChuan", diemXet.compareTo(diemchuan) >= 0);
                    } catch (Exception ex) {
                    }
                });
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        model.addAttribute("nganhList", nganhService.getAllNganh());
        model.addAttribute("activeTab", "dgnl");
        return "cong-cu-tinh-diem";
    }

    private DiemXetTuyenRequest defaultRequest() {
        return DiemXetTuyenRequest.builder()
                .maToHop("A00")
                .phuongThuc("THPT")
                .khuVuc("0")
                .doiTuong("0")
                .build();
    }
}