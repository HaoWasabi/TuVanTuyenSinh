package com.tuyensinh.ModelWeb;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NganhTraCuuResponse {

    private String maNganh;
    private String tenNganh;
    private String maToHopGoc;
    private String tenToHopGoc;
    private Integer chiTieu;
    private BigDecimal diemSan;
    private BigDecimal diemTrungTuyen;
    private Integer slXtt; // Số lượng tuyển thẳng
    private Integer slThpt; // Số lượng THPT
    private Integer slVsat; // Số lượng VSAT
    private Integer slDgnl; // Số lượng ĐGNL
    private List<ToHopDiemInfo> toHopKhacList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ToHopDiemInfo {
        private String maToHop;
        private String tenToHop;
        private BigDecimal doLech;
        private BigDecimal diemSanQuyDoi;
        private BigDecimal diemTrungTuyenQuyDoi;
        private Boolean laToHopGoc;
    }
}
