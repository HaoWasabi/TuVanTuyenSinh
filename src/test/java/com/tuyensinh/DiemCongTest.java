package com.tuyensinh;

import com.tuyensinh.model.DiemCong;
import com.tuyensinh.service.DiemCongService;
import java.math.BigDecimal;
import java.util.List;

public class DiemCongTest {

    public static void main(String[] args) {

        DiemCongService service = new DiemCongService();

        // =========================
        // 1. THÊM MỚI
        // =========================
        System.out.println("=== THÊM ĐIỂM CỘNG ===");

        DiemCong diemCong = DiemCong.builder()
                .tsCccd("012345678901")
                .manganh("7480201")
                .matohop("A00")
                .phuongthuc("THPT")
                .diemCC(new BigDecimal("0.50"))
                .diemUtxt(new BigDecimal("0.25"))
                .diemTong(new BigDecimal("0.75"))
                .ghichu("Test điểm cộng")
                .dcKeys("KEY001")
                .build();

        service.create(diemCong);
        System.out.println("Đã thêm điểm cộng! ID = " + diemCong.getIddiemcong());

        // =========================
        // 2. XEM DANH SÁCH
        // =========================
        System.out.println("\n=== DANH SÁCH ĐIỂM CỘNG ===");

        List<DiemCong> list = service.getAll();
        list.forEach(d ->
                System.out.println(d.getIddiemcong()
                        + " | CCCD: " + d.getTsCccd()
                        + " | Ngành: " + d.getManganh()
                        + " | Tổ hợp: " + d.getMatohop()
                        + " | Điểm CC: " + d.getDiemCC()
                        + " | Điểm ƯT: " + d.getDiemUtxt()
                        + " | Tổng: " + d.getDiemTong())
        );

        // =========================
        // 3. TÌM THEO ID
        // =========================
        System.out.println("\n=== TÌM THEO ID ===");

        Integer idCanTim = 1;
        service.getById(idCanTim).ifPresentOrElse(
                d -> System.out.println(
                        "ID: " + d.getIddiemcong() +
                        " | CCCD: " + d.getTsCccd() +
                        " | Ngành: " + d.getManganh() +
                        " | Điểm tổng: " + d.getDiemTong()
                ),
                () -> System.out.println("Không tìm thấy điểm cộng với id = " + idCanTim)
        );

        // =========================
        // 4. TÌM THEO CCCD
        // =========================
        System.out.println("\n=== TÌM THEO CCCD ===");

        String cccdCanTim = "012345678901";
        List<DiemCong> listByCccd = service.getByCccd(cccdCanTim);
        if (listByCccd.isEmpty()) {
            System.out.println("Không tìm thấy điểm cộng với CCCD = " + cccdCanTim);
        } else {
            listByCccd.forEach(d ->
                    System.out.println(d.getIddiemcong()
                            + " | CCCD: " + d.getTsCccd()
                            + " | Ngành: " + d.getManganh()
                            + " | Điểm tổng: " + d.getDiemTong())
            );
        }

        // =========================
        // 5. CẬP NHẬT
        // =========================
        System.out.println("\n=== CẬP NHẬT ĐIỂM CỘNG ===");

        list.stream().findFirst().ifPresent(d -> {
            d.setDiemCC(new BigDecimal("1.00"));
            d.setDiemTong(new BigDecimal("1.25"));
            service.update(d);
            System.out.println("Update thành công! ID = " + d.getIddiemcong());
        });

        // =========================
        // 6. XÓA
        // =========================
        System.out.println("\n=== XÓA ĐIỂM CỘNG ===");

        if (diemCong.getIddiemcong() != null) {
            boolean deleted = service.delete(diemCong.getIddiemcong());
            if (deleted) {
                System.out.println("Đã xóa điểm cộng id = " + diemCong.getIddiemcong());
            } else {
                System.out.println("Không tìm thấy điểm cộng để xóa!");
            }
        }

        // =========================
        // 7. IMPORT EXCEL
        // =========================
        System.out.println("\n=== IMPORT EXCEL ===");

        try {
            String filePath = "src/main/resources/diemcong_test.xlsx";
            List<DiemCong> imported = service.importFromExcel(filePath);
            System.out.println("Import thành công: " + imported.size() + " dòng");
        } catch (Exception e) {
            System.out.println("Lỗi import: " + e.getMessage());
        }

        System.out.println("\n=== KẾT THÚC TEST ===");
    }
}
