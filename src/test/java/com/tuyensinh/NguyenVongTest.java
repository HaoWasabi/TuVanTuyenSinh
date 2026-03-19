package com.tuyensinh;

import com.tuyensinh.model.NguyenVong;
import com.tuyensinh.service.NguyenVongService;
import java.math.BigDecimal;
import java.util.List;

public class NguyenVongTest {

    public static void main(String[] args) {

        NguyenVongService service = new NguyenVongService();

        // =========================
        // 1. THÊM MỚI
        // =========================
        System.out.println("=== THÊM NGUYỆN VỌNG ===");

        NguyenVong nguyenVong = NguyenVong.builder()
                .nnCccd("012345678901")
                .nvManganh("7480201")
                .nvTt(1)
                .diemThxt(new BigDecimal("25.50"))
                .diemUtqd(new BigDecimal("0.50"))
                .diemCong(new BigDecimal("0.25"))
                .diemXettuyen(new BigDecimal("26.25"))
                .nvKetqua("Chờ xét")
                .nvKeys("KEY001")
                .ttPhuongthuc("THPT")
                .ttThm("A00")
                .build();

        service.create(nguyenVong);
        System.out.println("Đã thêm nguyện vọng! ID = " + nguyenVong.getIdnv());

        // =========================
        // 2. XEM DANH SÁCH
        // =========================
        System.out.println("\n=== DANH SÁCH NGUYỆN VỌNG ===");

        List<NguyenVong> list = service.getAll();
        list.forEach(nv ->
                System.out.println(nv.getIdnv()
                        + " | CCCD: " + nv.getNnCccd()
                        + " | Ngành: " + nv.getNvManganh()
                        + " | TT: " + nv.getNvTt()
                        + " | Điểm XT: " + nv.getDiemXettuyen()
                        + " | Kết quả: " + nv.getNvKetqua())
        );

        // =========================
        // 3. TÌM THEO ID
        // =========================
        System.out.println("\n=== TÌM THEO ID ===");

        Integer idCanTim = 1;
        service.getById(idCanTim).ifPresentOrElse(
                nv -> System.out.println(
                        "ID: " + nv.getIdnv() +
                        " | CCCD: " + nv.getNnCccd() +
                        " | Ngành: " + nv.getNvManganh() +
                        " | Điểm XT: " + nv.getDiemXettuyen()
                ),
                () -> System.out.println("Không tìm thấy nguyện vọng với id = " + idCanTim)
        );

        // =========================
        // 4. TÌM THEO CCCD
        // =========================
        System.out.println("\n=== TÌM THEO CCCD ===");

        String cccdCanTim = "012345678901";
        List<NguyenVong> listByCccd = service.getByCccd(cccdCanTim);
        if (listByCccd.isEmpty()) {
            System.out.println("Không tìm thấy nguyện vọng với CCCD = " + cccdCanTim);
        } else {
            listByCccd.forEach(nv ->
                    System.out.println(nv.getIdnv()
                            + " | CCCD: " + nv.getNnCccd()
                            + " | Ngành: " + nv.getNvManganh()
                            + " | TT: " + nv.getNvTt())
            );
        }

        // =========================
        // 5. CẬP NHẬT
        // =========================
        System.out.println("\n=== CẬP NHẬT NGUYỆN VỌNG ===");

        list.stream().findFirst().ifPresent(nv -> {
            nv.setDiemXettuyen(new BigDecimal("27.00"));
            nv.setNvKetqua("Trúng tuyển");
            service.update(nv);
            System.out.println("Update thành công! ID = " + nv.getIdnv());
        });

        // =========================
        // 6. XÓA
        // =========================
        System.out.println("\n=== XÓA NGUYỆN VỌNG ===");

        if (nguyenVong.getIdnv() != null) {
            boolean deleted = service.delete(nguyenVong.getIdnv());
            if (deleted) {
                System.out.println("Đã xóa nguyện vọng id = " + nguyenVong.getIdnv());
            } else {
                System.out.println("Không tìm thấy nguyện vọng để xóa!");
            }
        }

        // =========================
        // 7. IMPORT EXCEL
        // =========================
        System.out.println("\n=== IMPORT EXCEL ===");

        try {
            String filePath = "src/main/resources/nguyenvong_test.xlsx";
            List<NguyenVong> imported = service.importFromExcel(filePath);
            System.out.println("Import thành công: " + imported.size() + " dòng");
        } catch (Exception e) {
            System.out.println("Lỗi import: " + e.getMessage());
        }

        System.out.println("\n=== KẾT THÚC TEST ===");
    }
}
