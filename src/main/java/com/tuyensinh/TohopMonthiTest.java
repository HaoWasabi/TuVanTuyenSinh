package com.tuyensinh;

import com.tuyensinh.model.TohopMonthi;
import com.tuyensinh.service.TohopMonthiService;

import java.util.List;

public class TohopMonthiTest {

    public static void main(String[] args) {

        TohopMonthiService service =
                new TohopMonthiService();

        // ===== THÊM =====
        System.out.println("=== THÊM TỔ HỢP ===");

        String maTohop = "A04";

        service.getByMaTohop(maTohop)
                .ifPresentOrElse(
                    t -> System.out.println("Mã tổ hợp đã tồn tại "),
                    () -> {
                        TohopMonthi newTohop = TohopMonthi.builder()
                                .matohop(maTohop)
                                .mon1("TO")
                                .mon2("LY")
                                .mon3("HO")
                                .tentohop("Toán, Lý, Hóa")
                                .build();

                        service.create(newTohop);
                        System.out.println("Đã thêm tổ hợp mới");
                    }
                );

        // ===== DANH SÁCH =====
        System.out.println("\n=== DANH SÁCH ===");

        List<TohopMonthi> list = service.getAll();

        list.forEach(x ->
                System.out.println(
                        x.getIdtohop() + " | "
                                + x.getMatohop() + " | "
                                + x.getTentohop()
                )
        );

        // ===== UPDATE =====
        System.out.println("\n=== UPDATE ===");

        list.stream().findFirst().ifPresent(x -> {
            x.setTentohop("TO - Đã cập nhật");
            service.update(x);
        });

        /*
        // ===== DELETE =====
        System.out.println("\n=== DELETE ===");

        list.stream().findFirst().ifPresent(x -> {
            service.delete(x.getIdtohop());
            System.out.println("Đã xóa id = " + x.getIdtohop());
        });
        */
        // =========================
        // IMPORT EXCEL
        // =========================
        System.out.println("=== IMPORT EXCEL ===");

        try {
            String path =
                    "src/main/resources/tohop_monthi_test.xlsx";

            List<TohopMonthi> imported =
                    service.importFromExcel(path);

            System.out.println("Import thành công: "
                    + imported.size() + " dòng");

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // =========================
        // EXPORT EXCEL
        // =========================
        //Xuất ra thư mục mình chỉ định
        System.out.println("\n=== EXPORT EXCEL ===");

        try {
            String path = "src/main/resources/tohop_export.xlsx";
            service.exportToExcel(path);

            System.out.println("Xuất file thành công!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // ===== FIND =====
        System.out.println("\n=== FIND ===");

        service.getByMaTohop("A00")
                .ifPresentOrElse(
                        x -> System.out.println(
                                x.getMatohop() + " | "
                                        + x.getTentohop()
                        ),
                        () -> System.out.println("Không tồn tại!")
                );
    }
    
    
}
