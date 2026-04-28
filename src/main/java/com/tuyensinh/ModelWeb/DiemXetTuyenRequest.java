package com.tuyensinh.ModelWeb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiemXetTuyenRequest {

    private String maToHop;      // Tổ hợp xét tuyển: A00, A01, B00, C00, C01, D01, D07
    private String phuongThuc;  // Phương thức: THPT, VSAT, DGNL
    
    // Điểm 6 môn (bắt buộc cho THPT và VSAT)
    private Double toan;   // Môn 1
    private Double li;     // Môn 2
    private Double hoa;   // Môn 3
    private Double sinh;  // Môn 4
    private Double su;    // Môn 5
    private Double di;    // Môn 6
    // Mở rộng cho các tổ hợp khác
    private Double va;    // Văn
    private Double n1;    // Ngoại ngữ 1
    
    // Điểm tổng (cho DGNL)
    private Double diemTong;
    
    // Điểm cộng và ưu tiên
    private Double diemCong;    // Điểm cộng (chứng chỉ, thành tích...)
    private Double diemUuTien;  // Điểm ưu tiên (khu vực, đối tượng...)
}