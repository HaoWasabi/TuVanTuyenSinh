package com.tuyensinh;

import com.tuyensinh.model.DiemThi;
import com.tuyensinh.service.DiemThiService;
import java.math.BigDecimal;
import java.util.List;

public class DiemThiTest {

    public static void main(String[] args) {

        DiemThiService service = new DiemThiService();

        // =========================
        // 1. THÊM MỚI
        // =========================
        System.out.println("=== THÊM ĐIỂM THI ===");

        DiemThi diemThi = DiemThi.builder()
                .cccd("012345678901")
                .sobaodanh("01000001")
                .dPhuongthuc("THPT")
                .toan(new BigDecimal("8.50"))
                .vatLi(new BigDecimal("7.75"))
                .hoaHoc(new BigDecimal("8.00"))
                .sinhHoc(new BigDecimal("7.50"))
                .lichSu(new BigDecimal("6.50"))
                .diaLi(new BigDecimal("7.00"))
                .nguVan(new BigDecimal("7.25"))
                .n1Thi(new BigDecimal("8.00"))
                .n1Cc(new BigDecimal("7.50"))
                .tinHoc(new BigDecimal("9.00"))
                .ktpl(new BigDecimal("7.00"))
                .build();

        service.create(diemThi);
        System.out.println("Đã thêm điểm thi! ID = " + diemThi.getIddiemthi());

        // =========================
        // 2. XEM DANH SÁCH
        // =========================
        System.out.println("\n=== DANH SÁCH ĐIỂM THI ===");

        List<DiemThi> list = service.getAll();
        list.forEach(d ->
                System.out.println(d.getIddiemthi()
                        + " | CCCD: " + d.getCccd()
                        + " | SBD: " + d.getSobaodanh()
                        + " | Toán: " + d.getToan()
                        + " | Lý: " + d.getVatLi()
                        + " | Hóa: " + d.getHoaHoc())
        );

        // =========================
        // 3. TÌM THEO ID
        // =========================
        System.out.println("\n=== TÌM THEO ID ===");

        Integer idCanTim = 1;
        service.getById(idCanTim).ifPresentOrElse(
                d -> System.out.println(
                        "ID: " + d.getIddiemthi() +
                        " | CCCD: " + d.getCccd() +
                        " | SBD: " + d.getSobaodanh() +
                        " | Toán: " + d.getToan()
                ),
                () -> System.out.println("Không tìm thấy điểm thi với id = " + idCanTim)
        );

        // =========================
        // 4. TÌM THEO CCCD
        // =========================
        System.out.println("\n=== TÌM THEO CCCD ===");

        String cccdCanTim = "012345678901";
        List<DiemThi> listByCccd = service.getByCccd(cccdCanTim);
        if (listByCccd.isEmpty()) {
            System.out.println("Không tìm thấy điểm thi với CCCD = " + cccdCanTim);
        } else {
            listByCccd.forEach(d ->
                    System.out.println(d.getIddiemthi()
                            + " | CCCD: " + d.getCccd()
                            + " | SBD: " + d.getSobaodanh())
            );
        }

        // =========================
        // 5. CẬP NHẬT
        // =========================
        System.out.println("\n=== CẬP NHẬT ĐIỂM THI ===");

        list.stream().findFirst().ifPresent(d -> {
            d.setToan(new BigDecimal("9.00"));
            d.setVatLi(new BigDecimal("8.50"));
            service.update(d);
            System.out.println("Update thành công! ID = " + d.getIddiemthi());
        });

        // =========================
        // 6. XÓA
        // =========================
        System.out.println("\n=== XÓA ĐIỂM THI ===");

        // Xóa record vừa thêm để test
        if (diemThi.getIddiemthi() != null) {
            boolean deleted = service.delete(diemThi.getIddiemthi());
            if (deleted) {
                System.out.println("Đã xóa điểm thi id = " + diemThi.getIddiemthi());
            } else {
                System.out.println("Không tìm thấy điểm thi để xóa!");
            }
        }

        // =========================
        // 7. THỐNG KÊ ĐIỂM THEO MÔN
        // =========================
        System.out.println("\n=== THỐNG KÊ ĐIỂM THEO MÔN (TOÁN) ===");

        Object[] tkToan = service.thongKeDiemTheoMon("toan");
        if (tkToan != null) {
            System.out.println("Điểm trung bình: " + tkToan[0]);
            System.out.println("Điểm thấp nhất: " + tkToan[1]);
            System.out.println("Điểm cao nhất: " + tkToan[2]);
            System.out.println("Số lượng: " + tkToan[3]);
        } else {
            System.out.println("Không có dữ liệu!");
        }

        // =========================
        // 8. THỐNG KÊ ĐIỂM TB TẤT CẢ MÔN
        // =========================
        System.out.println("\n=== THỐNG KÊ ĐIỂM TRUNG BÌNH TẤT CẢ MÔN ===");

        Object[] tkTatCa = service.thongKeDiemTrungBinhTatCaMon();
        if (tkTatCa != null) {
            System.out.println("TB Toán: " + tkTatCa[0]);
            System.out.println("TB Lý: " + tkTatCa[1]);
            System.out.println("TB Hóa: " + tkTatCa[2]);
            System.out.println("TB Sinh: " + tkTatCa[3]);
            System.out.println("TB Sử: " + tkTatCa[4]);
            System.out.println("TB Địa: " + tkTatCa[5]);
            System.out.println("TB Văn: " + tkTatCa[6]);
            System.out.println("TB Tin: " + tkTatCa[7]);
            System.out.println("TB KTPL: " + tkTatCa[8]);
        } else {
            System.out.println("Không có dữ liệu!");
        }

        // =========================
        // 9. IMPORT EXCEL
        // =========================
        System.out.println("\n=== IMPORT EXCEL ===");

        try {
            String filePath = "src/main/resources/diemthi_test.xlsx";
            List<DiemThi> imported = service.importFromExcel(filePath);
            System.out.println("Import thành công: " + imported.size() + " dòng");
        } catch (Exception e) {
            System.out.println("Lỗi import: " + e.getMessage());
        }

        System.out.println("\n=== KẾT THÚC TEST ===");
    }
}
