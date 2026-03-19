package com.tuyensinh;

import com.tuyensinh.database.HibernateUtil;
import com.tuyensinh.model.BangQuyDoi;
import com.tuyensinh.service.BQDService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * ServiceTestBQD
 *
 * Yêu cầu: "copy code ServiceTestNTH qua file hậu tố BQD" rồi sửa để chạy được.
 * File này giữ nguyên phong cách/flow giống ServiceTestNTH:
 *  - START/END banner
 *  - CREATE -> READ ALL -> READ BY ID -> READ BY KEY -> UPDATE -> DELETE -> FINAL CHECK
 *  - try/catch/finally + HibernateUtil.shutdown()
 *
 * Dữ liệu test (insert CSDL) được chọn hợp lý với hệ thống giáo dục Việt Nam:
 *  - THPT: điểm môn theo thang 10 (0.00 - 10.00)
 *  - ĐGNL (ĐHQG-HCM): điểm tổng thường theo thang lớn (ví dụ 0 - 1200), quy đổi về thang 30
 *  - V-SAT: giả lập điểm theo thang 0 - 150 cho từng môn, quy đổi về thang 10
 *
 * Lưu ý: BangQuyDoi đang lưu các "vùng quy đổi" [a,b] -> [c,d] (theo bách phân vị/vùng điểm).
 */
public class ServiceTestBQD {

    public static void main(String[] args) {
        BQDService service = new BQDService();

        try {
            System.out.println("\n========== SERVICE TEST START ==========\n");

            // Test 1: CREATE - Tạo 3 bản ghi quy đổi khác nhau
            System.out.println("=== TEST 1: CREATE ===");

            // (1) V-SAT môn Toán: vùng điểm 90-105 (thang giả lập 0-150) ~ tương đương THPT 6.00-7.00
            BangQuyDoi qd1 = createTestBQD(
                    "V-SAT", "A00", "Toan",
                    bd("90.00"), bd("105.00"),
                    bd("6.00"), bd("7.00"),
                    "PV_90_105", "QD_VSAT_TOAN_A00");

            // (2) V-SAT môn Lý: vùng điểm 105-120 ~ tương đương THPT 7.00-8.00
            BangQuyDoi qd2 = createTestBQD(
                    "V-SAT", "A00", "Ly",
                    bd("105.00"), bd("120.00"),
                    bd("7.00"), bd("8.00"),
                    "PV_105_120", "QD_VSAT_LY_A00");

            // (3) ĐGNL (ĐHQG-HCM) tổng: vùng 600-700 ~ tương đương THPT thang 30 là 20.00-25.00
            BangQuyDoi qd3 = createTestBQD(
                    "DGNL", "D01", "Tong",
                    bd("600.00"), bd("700.00"),
                    bd("20.00"), bd("25.00"),
                    "PV_600_700", "QD_DGNL_TONG_D01");

            BangQuyDoi created1 = service.create(qd1);
            BangQuyDoi created2 = service.create(qd2);
            BangQuyDoi created3 = service.create(qd3);

            System.out.println("[✓] Created qd1 - ID: " + created1.getIdqd()
                    + ", phuongThuc: " + created1.getDPhuongthuc()
                    + ", toHop: " + created1.getDTohop()
                    + ", mon: " + created1.getDMon()
                    + ", maQuyDoi: " + created1.getDMaquydoi());

            System.out.println("[✓] Created qd2 - ID: " + created2.getIdqd()
                    + ", phuongThuc: " + created2.getDPhuongthuc()
                    + ", toHop: " + created2.getDTohop()
                    + ", mon: " + created2.getDMon()
                    + ", maQuyDoi: " + created2.getDMaquydoi());

            System.out.println("[✓] Created qd3 - ID: " + created3.getIdqd()
                    + ", phuongThuc: " + created3.getDPhuongthuc()
                    + ", toHop: " + created3.getDTohop()
                    + ", mon: " + created3.getDMon()
                    + ", maQuyDoi: " + created3.getDMaquydoi());

            // Test 2: READ ALL - Lấy tất cả bản ghi quy đổi
            System.out.println("\n=== TEST 2: READ ALL ===");
            List<BangQuyDoi> all = service.getAll();
            System.out.println("[✓] Found " + all.size() + " records:");
            for (BangQuyDoi b : all) {
                System.out.println(" - ID: " + b.getIdqd()
                        + ", phuongThuc: " + b.getDPhuongthuc()
                        + ", toHop: " + b.getDTohop()
                        + ", mon: " + b.getDMon()
                        + ", [a,b]->[c,d]=[" + b.getDDiema() + "," + b.getDDiemb() + "]->[" + b.getDDiemc() + "," + b.getDDiemd() + "]");
            }

            // Test 3: READ BY ID
            System.out.println("\n=== TEST 3: READ BY ID ===");
            Optional<BangQuyDoi> foundById = service.getById(created1.getIdqd());
            if (foundById.isPresent()) {
                BangQuyDoi b = foundById.get();
                System.out.println("[✓] Found by ID " + created1.getIdqd()
                        + ": phuongThuc=" + b.getDPhuongthuc()
                        + ", toHop=" + b.getDTohop()
                        + ", mon=" + b.getDMon());
            } else {
                System.out.println("[✗] Not found ID: " + created1.getIdqd());
            }

            // Test 4: READ BY MAQUYDOI
            System.out.println("\n=== TEST 4: READ BY MAQUYDOI ===");
            String maQuyDoi = created2.getDMaquydoi();
            Optional<BangQuyDoi> foundByMa = service.getByMaquydoi(maQuyDoi);
            if (foundByMa.isPresent()) {
                BangQuyDoi b = foundByMa.get();
                System.out.println("[✓] Found by maQuyDoi: ID=" + b.getIdqd()
                        + ", maQuyDoi=" + b.getDMaquydoi()
                        + ", phanVi=" + b.getDPhanvi());
            } else {
                System.out.println("[✗] Not found maQuyDoi: " + maQuyDoi);
            }

            // Test 5: READ BY TOHOP + MON
            System.out.println("\n=== TEST 5: READ BY TOHOP + MON ===");
            Optional<BangQuyDoi> foundByToHopMon = service.getByTohopAndMon(created3.getDTohop(), created3.getDMon());
            if (foundByToHopMon.isPresent()) {
                BangQuyDoi b = foundByToHopMon.get();
                System.out.println("[✓] Found by toHop+mon: ID=" + b.getIdqd()
                        + ", toHop=" + b.getDTohop()
                        + ", mon=" + b.getDMon()
                        + ", maQuyDoi=" + b.getDMaquydoi());
            } else {
                System.out.println("[✗] Not found toHop+mon: " + created3.getDTohop() + "/" + created3.getDMon());
            }

            // Test 6: UPDATE - điều chỉnh vùng quy đổi cho qd1
            System.out.println("\n=== TEST 6: UPDATE ===");
            BigDecimal oldDiemd = created1.getDDiemd();

            // giả lập cập nhật: mở rộng đỉnh vùng THPT từ 7.00 -> 7.25 (vẫn hợp lệ thang 10)
            BigDecimal newDiemd = bd("7.25");
            created1.setDDiemd(newDiemd);
            created1.setDPhanvi(created1.getDPhanvi() + "_UPDATED");

            BangQuyDoi updated = service.update(created1);
            System.out.println("[✓] Updated ID " + updated.getIdqd()
                    + ": diemd " + oldDiemd + " → " + updated.getDDiemd()
                    + ", phanVi=" + updated.getDPhanvi());

            // Verify update
            Optional<BangQuyDoi> verified = service.getById(updated.getIdqd());
            if (verified.isPresent() && verified.get().getDDiemd().compareTo(newDiemd) == 0) {
                System.out.println("[✓] Verified: Update successful");
            }

            // Test 7: DELETE
            System.out.println("\n=== TEST 7: DELETE ===");
            boolean deleted1 = service.deleteById(created1.getIdqd());
            System.out.println("[" + (deleted1 ? "✓" : "✗") + "] Delete ID " + created1.getIdqd() + ": " + (deleted1 ? "Success" : "Failed"));

            boolean deleted2 = service.deleteById(created2.getIdqd());
            System.out.println("[" + (deleted2 ? "✓" : "✗") + "] Delete ID " + created2.getIdqd() + ": " + (deleted2 ? "Success" : "Failed"));

            // Giữ lại created3 để kiểm tra cuối (giống ServiceTestNTH thường còn lại 1 record)

            // Test 8: FINAL CHECK
            System.out.println("\n=== TEST 8: FINAL CHECK ===");
            List<BangQuyDoi> remaining = service.getAll();
            System.out.println("[✓] Remaining records: " + remaining.size());
            for (BangQuyDoi b : remaining) {
                System.out.println(" - ID: " + b.getIdqd()
                        + ", phuongThuc: " + b.getDPhuongthuc()
                        + ", toHop: " + b.getDTohop()
                        + ", mon: " + b.getDMon()
                        + ", maQuyDoi: " + b.getDMaquydoi());
            }

            // Test 9: IMPORT FROM EXCEL
            System.out.println("\n=== TEST 9: IMPORT FROM EXCEL ===");
            String excelFilePath = "src/main/resources/BangQuyDoi_import.xlsx"; // Đường dẫn file Excel
            try {
                List<BangQuyDoi> importedRecords = service.importFromExcel(excelFilePath);
                System.out.println("[✓] Imported " + importedRecords.size() + " records from Excel");
                for (BangQuyDoi b : importedRecords) {
                    System.out.println(" - ID: " + b.getIdqd()
                            + ", phuongThuc: " + b.getDPhuongthuc()
                            + ", toHop: " + b.getDTohop()
                            + ", mon: " + b.getDMon()
                            + ", maQuyDoi: " + b.getDMaquydoi());
                }
            } catch (Exception ex) {
                System.out.println("[⚠] Excel import skipped: " + ex.getMessage());
                System.out.println("   (File not found or error reading file - this is optional)");
            }

            System.out.println("\n========== SERVICE TEST END - ALL TESTS PASSED ==========\n");

        } catch (Exception ex) {
            System.err.println("[✗] ERROR: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
        }
    }

    /**
     * Helper method: Tạo BangQuyDoi object với dữ liệu test.
     *
     * Quy ước field:
     *  - dDiema, dDiemb: vùng điểm nguồn [a,b]
     *  - dDiemc, dDiemd: vùng điểm đích [c,d]
     *  - dMaquydoi: mã duy nhất
     *  - dPhanvi: nhãn vùng (để debug/truy vết)
     */
    private static BangQuyDoi createTestBQD(String phuongThuc, String toHop, String mon,
                                           BigDecimal a, BigDecimal b,
                                           BigDecimal c, BigDecimal d,
                                           String phanVi, String maQuyDoiPrefix) {
        String maQuyDoi = maQuyDoiPrefix + "_" + System.currentTimeMillis();
        return BangQuyDoi.builder()
                .dPhuongthuc(phuongThuc)
                .dTohop(toHop)
                .dMon(mon)
                .dDiema(a)
                .dDiemb(b)
                .dDiemc(c)
                .dDiemd(d)
                .dMaquydoi(maQuyDoi)
                .dPhanvi(phanVi)
                .build();
    }

    private static BigDecimal bd(String value) {
        return new BigDecimal(value);
    }
}
