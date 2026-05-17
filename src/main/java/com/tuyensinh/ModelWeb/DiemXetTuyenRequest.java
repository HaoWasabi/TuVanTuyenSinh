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

    private String maToHop; // Tổ hợp xét tuyển: A00, A01, B00, C00, C01, D01, D07
    private String phuongThuc; // Phương thức: THPT, VSAT, DGNL
    // Mã ngành (để đối chiếu điểm sàn / điểm trúng tuyển)
    private String manganh;

    // Điểm 6 môn (bắt buộc cho THPT và VSAT)
    private Double toan; // Môn 1
    private Double li; // Môn 2
    private Double hoa; // Môn 3
    private Double sinh; // Môn 4
    private Double su; // Môn 5
    private Double di; // Môn 6
    // Mở rộng cho các tổ hợp khác
    private Double va; // Văn
    private Double n1; // Ngoại ngữ 1

    // Điểm tổng (cho DGNL)
    private Double diemTong;

    // Điểm cộng và ưu tiên
    private Double diemCongChungChi; // Điểm cộng từ chứng chỉ tiếng Anh quy đổi
    private Double diemCongUuTien; // Điểm cộng từ đạt điều kiện ưu tiên xét tuyển
    private Double diemCong; // Tổng điểm cộng (DC = diemCongChungChi + diemCongUuTien, max 3)

    // Thông tin ưu tiên
    private String doiTuong; // Đối tượng ưu tiên: "0" (không), "1" (Nhóm ƯT 2, +1.0), "2" (Nhóm ƯT 1, +2.0)
    private String khuVuc; // Khu vực ưu tiên: "0" (KV3, +0), "0.25" (KV2, +0.25), "0.5" (KV2-NT, +0.5),
                           // "0.75" (KV1, +0.75)
    private Double mucDiemUuTien; // Mức điểm ưu tiên theo Điều 7 Quy chế tuyển sinh

    // Dự phòng: nếu frontend tính sẵn
    private Double diemUuTien; // Điểm ưu tiên tính toán (ĐƯT)

    // Khi bật chế độ 4-môn frontend gửi danh sách môn được tính (CSV), ví dụ: "TO,VA,LI,HO"
    // Nếu không null: server sẽ ưu tiên tuân thủ danh sách này và áp dụng quy tắc liệt cho các môn bắt buộc/tự chọn.
    private String fourSelectedSubjects;
}