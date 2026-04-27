package com.tuyensinh.ModelWeb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiemXetTuyenResponse {

    private String phuongThuc;       // Phương thức: THPT, VSAT, DGNL
    private String maToHop;         // Tổ hợp xét tuyển
    private BigDecimal diemToHop;   // Điểm tổ hợp xét tuyển (DTHXT)
    private BigDecimal diemQuyDoi;  // Điểm quy đổi về thang chuẩn
    private BigDecimal diemCong;   // Điểm cộng (nếu có)
    private BigDecimal diemUuTien; // Điểm ưu tiên (nếu có)
    private BigDecimal diemXetTuyen; // Điểm xét tuyển cuối cùng
    
    // Thông tin chi tiết quy đổi từng môn (cho VSAT)
    private List<MonQuyDoi> chiTietQuyDoi;
    
    // Thông tin bổ sung
    private String thongBao;
    private Boolean datNguong;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonQuyDoi {
        private String tenMon;
        private BigDecimal diemGoc;      // Điểm gốc (thang 150 hoặc 10)
        private BigDecimal diemQuyDoi;  // Điểm quy đổi
        private String thangDiem;       // Thang điểm gốc
    }
}