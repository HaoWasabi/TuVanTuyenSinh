package com.tuyensinh.ModelWeb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response DTO: Thông tin thí sinh + danh sách nguyên vọng xét tuyển
 * Sử dụng cho API: GET /api/thisinh/{cccd}/{ngaySinh}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThiSinhWithNguyenVongResponse {
    
    // ===== Thông tin thí sinh =====
    private String cccd;
    private String sobaodanh;
    private String ho;
    private String ten;
    private String ngaySinh;
    private String dienThoai;
    private String gioiTinh;
    private String email;
    private String noiSinh;
    private String doiTuong;
    private String khuVuc;
    
    // ===== Danh sách nguyên vọng xét tuyển =====
    private List<NguyenVongInfo> nguyenVongList;
    
    /**
     * Inner class: Thông tin chi tiết một nguyên vọng
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NguyenVongInfo {
        private Integer idnv;                    // ID nguyên vọng
        private String nv_manganh;               // Mã ngành xét tuyển
        private Integer nv_tt;                   // Thứ tự nguyên vọng (1, 2, 3...)
        private BigDecimal diem_thxt;            // Điểm tổ hợp xét tuyển (đã cộng điểm môn chính)
        private BigDecimal diem_utqd;            // Điểm ưu tiên quy định theo tổ hợp
        private BigDecimal diem_cong;            // Điểm cộng (chứng chỉ, thành tích...)
        private BigDecimal diem_xettuyen;        // Điểm xét tuyển cuối cùng (đã cộng ưu tiên)
        private String nv_ketqua;                // Kết quả xét tuyển (Đỗ, Trượt, Chờ...)
        private String nv_keys;                  // Chìa khóa (nếu có)
        private String tt_phuongthuc;            // Phương thức xét tuyển (THPT, VSAT, DGNL)
        private String tt_thm;                   // Thêm thông tin (nếu có)
    }
}
