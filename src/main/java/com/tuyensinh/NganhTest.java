package com.tuyensinh;
import com.tuyensinh.model.Nganh;
import com.tuyensinh.service.NganhService;
import java.math.BigDecimal;
import java.util.List;

public class NganhTest {

    public static void main(String[] args) {

        NganhService service = new NganhService();

        // =========================
        // 1. THÊM MỚI
        // =========================
        System.out.println("=== THÊM NGÀNH ===");

        Nganh nganh = Nganh.builder()
                .manganh("999999")
                .tennganh("Test ngành")
                .nTohopgoc("A00")
                .nChitieu(50)
                .nDiemsan(new BigDecimal("18.5"))
                .nDgnl("Y")
                .nThpt("Y")
                .build();

        service.create(nganh);
        System.out.println("Đã thêm ngành!");

        // =========================
        // 2. XEM DANH SÁCH
        // =========================
        System.out.println("\n=== DANH SÁCH NGÀNH ===");

        List<Nganh> list = service.getAll();
        list.forEach(n ->
                System.out.println(n.getIdnganh()
                        + " | " + n.getManganh()
                        + " | " + n.getTennganh())
        );

        // =========================
        // 3. SỬA PHẦN TỬ THỨ 2
        // =========================
        System.out.println("\n=== CẬP NHẬT ===");

        list.stream().skip(1).findFirst().ifPresent(n -> {
            n.setTennganh("Ngành đã update");
            service.update(n);
            System.out.println("Update thành công!");
        });

        // =========================
        // 4. XÓA
        // =========================
        
        System.out.println("\n=== Xoá nghành đầu tiên của danh sách ===");

        list.stream().findFirst().ifPresent(n -> {
            service.delete(n.getIdnganh());
            System.out.println("Đã xóa id = " + n.getIdnganh());
        });
        
        // =========================
        // 5. TÌM KIẾM
        // =========================
       
        // Nhập dữ liệu cần tìm
        
        Integer id = 6;
        String MaNganh = "7140114";
        
        
        System.out.println("\n=== TÌM THEO ID ===");

        service.getById(id).ifPresentOrElse(
                n -> System.out.println(
                        n.getIdnganh() + " | " +
                        n.getManganh() + " | " +
                        n.getTennganh()
                ),
                () -> System.out.println("Không tìm thấy id ngành!")
        );   
        
        System.out.println("\n=== TÌM THEO MÃ NGÀNH ===");

        service.getByMaNganh(MaNganh).ifPresentOrElse(
                n -> System.out.println(
                        n.getManganh() + " | " +
                        n.getTennganh()
                ),
                () -> System.out.println("Không tồn tại mã ngành!")
        );
        
        
        // =========================
        // 6. IMPORT EXCEL
        // =========================
        System.out.println("\n=== IMPORT EXCEL ===");

        try {
            String filePath = "src/main/resources/nganh_test.xlsx"; //link file 
            List<Nganh> imported = service.importFromExcel(filePath);

            System.out.println("Import thành công: "
                    + imported.size() + " dòng");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
