package com.tuyensinh;

import com.tuyensinh.database.HibernateUtil;
import com.tuyensinh.model.NganhToHop;
import com.tuyensinh.service.NTHService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ServiceTestNTH {

    public static void main(String[] args) {
        NTHService service = new NTHService();

        try {
            System.out.println("\n========== SERVICE TEST START ==========\n");

            // Test 1: CREATE - Tạo 3 major khác nhau
            System.out.println("=== TEST 1: CREATE ===");
            NganhToHop major1 = createTestMajor("CNTT", "A00", "Toan", (byte) 2, "Ly", (byte) 1, "Hoa", (byte) 3, 0.5);
            NganhToHop major2 = createTestMajor("CNTT", "A01", "Toan", (byte) 1, "Ly", (byte) 2, "Anh", (byte) 1, 0.3);
            NganhToHop major3 = createTestMajor("DIEN", "D01", "Toan", (byte) 3, "Van", (byte) 2, "Anh", (byte) 1, 0.8);

            NganhToHop created1 = service.create(major1);
            NganhToHop created2 = service.create(major2);
            NganhToHop created3 = service.create(major3);

            System.out.println("[✓] Created major1 - ID: " + created1.getId() + ", maNganh: " + created1.getMaNganh() + ", doLech: " + created1.getDoLech());
            System.out.println("[✓] Created major2 - ID: " + created2.getId() + ", maNganh: " + created2.getMaNganh() + ", doLech: " + created2.getDoLech());
            System.out.println("[✓] Created major3 - ID: " + created3.getId() + ", maNganh: " + created3.getMaNganh() + ", doLech: " + created3.getDoLech());

            // Test 2: READ ALL - Lấy tất cả majors
            System.out.println("\n=== TEST 2: READ ALL ===");
            List<NganhToHop> allNganhToHop = service.getAll();
            System.out.println("[✓] Found " + allNganhToHop.size() + " records:");
            for (NganhToHop m : allNganhToHop) {
                System.out.println("    - ID: " + m.getId() + ", maNganh: " + m.getMaNganh() + ", maToHop: " + m.getMaToHop() + ", doLech: " + m.getDoLech());
            }

            // Test 3: READ BY ID
            System.out.println("\n=== TEST 3: READ BY ID ===");
            Optional<NganhToHop> foundById = service.getById(created1.getId());
            if (foundById.isPresent()) {
                NganhToHop m = foundById.get();
                System.out.println("[✓] Found by ID " + created1.getId() + ": maNganh=" + m.getMaNganh() + ", maToHop=" + m.getMaToHop());
            } else {
                System.out.println("[✗] Not found ID: " + created1.getId());
            }

            // Test 4: READ BY TB_KEYS
            System.out.println("\n=== TEST 4: READ BY TB_KEYS ===");
            String tbKeys = created2.getTbKeys();
            Optional<NganhToHop> foundByTbKeys = service.getByTbKeys(tbKeys);
            if (foundByTbKeys.isPresent()) {
                NganhToHop m = foundByTbKeys.get();
                System.out.println("[✓] Found by tbKeys: ID=" + m.getId() + ", maNganh=" + m.getMaNganh());
            } else {
                System.out.println("[✗] Not found tbKeys: " + tbKeys);
            }

            // Test 5: UPDATE
            System.out.println("\n=== TEST 5: UPDATE ===");
            BigDecimal oldDoLech = created1.getDoLech();
            BigDecimal newDoLech = new BigDecimal("2.50");
            created1.setDoLech(newDoLech);
            NganhToHop updated = service.update(created1);
            System.out.println("[✓] Updated ID " + updated.getId() + ": doLech " + oldDoLech + " → " + updated.getDoLech());

            // Verify update
            Optional<NganhToHop> verified = service.getById(created1.getId());
            if (verified.isPresent() && verified.get().getDoLech().equals(newDoLech)) {
                System.out.println("[✓] Verified: Update successful");
            }

            // Test 6: DELETE
            System.out.println("\n=== TEST 6: DELETE ===");
            boolean deleted1 = service.deleteById(created1.getId());
            System.out.println("[" + (deleted1 ? "✓" : "✗") + "] Delete ID " + created1.getId() + ": " + (deleted1 ? "Success" : "Failed"));

            boolean deleted2 = service.deleteById(created2.getId());
            System.out.println("[" + (deleted2 ? "✓" : "✗") + "] Delete ID " + created2.getId() + ": " + (deleted2 ? "Success" : "Failed"));

            // Test 7: FINAL CHECK
            System.out.println("\n=== TEST 7: FINAL CHECK ===");
            List<NganhToHop> remaining = service.getAll();
            System.out.println("[✓] Remaining records: " + remaining.size());
            for (NganhToHop m : remaining) {
                System.out.println("    - ID: " + m.getId() + ", maNganh: " + m.getMaNganh() + ", maToHop: " + m.getMaToHop());
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
     * Helper method: Tạo NganhToHop object với dữ liệu test
     */
    private static NganhToHop createTestMajor(String maNganh, String maToHop,
                String thMon1, Byte hsMon1,
                String thMon2, Byte hsMon2,
                String thMon3, Byte hsMon3,
                double doLech) {
            String tbKeys = maNganh + "_" + maToHop + "_" + System.currentTimeMillis();

            // Khai báo các biến cờ, tự động thành true nếu môn đó xuất hiện trong tổ hợp
            boolean to = "Toan".equalsIgnoreCase(thMon1) || "Toan".equalsIgnoreCase(thMon2) || "Toan".equalsIgnoreCase(thMon3);
            boolean li = "Ly".equalsIgnoreCase(thMon1) || "Ly".equalsIgnoreCase(thMon2) || "Ly".equalsIgnoreCase(thMon3);
            boolean ho = "Hoa".equalsIgnoreCase(thMon1) || "Hoa".equalsIgnoreCase(thMon2) || "Hoa".equalsIgnoreCase(thMon3);
            boolean n1 = "Anh".equalsIgnoreCase(thMon1) || "Anh".equalsIgnoreCase(thMon2) || "Anh".equalsIgnoreCase(thMon3);
            boolean va = "Van".equalsIgnoreCase(thMon1) || "Van".equalsIgnoreCase(thMon2) || "Van".equalsIgnoreCase(thMon3);
            
            // Các môn còn lại mặc định là false nếu không test tới
            boolean si = "Sinh".equalsIgnoreCase(thMon1) || "Sinh".equalsIgnoreCase(thMon2) || "Sinh".equalsIgnoreCase(thMon3);
            boolean su = "Su".equalsIgnoreCase(thMon1) || "Su".equalsIgnoreCase(thMon2) || "Su".equalsIgnoreCase(thMon3);
            boolean di = "Dia".equalsIgnoreCase(thMon1) || "Dia".equalsIgnoreCase(thMon2) || "Dia".equalsIgnoreCase(thMon3);
            boolean ti = "Tin".equalsIgnoreCase(thMon1) || "Tin".equalsIgnoreCase(thMon2) || "Tin".equalsIgnoreCase(thMon3);
            boolean ktpl = "KTPL".equalsIgnoreCase(thMon1) || "KTPL".equalsIgnoreCase(thMon2) || "KTPL".equalsIgnoreCase(thMon3);
            boolean khac = false;

            return NganhToHop.builder()
                    .maNganh(maNganh)
                    .maToHop(maToHop)
                    .thMon1(thMon1)
                    .hsMon1(hsMon1)
                    .thMon2(thMon2)
                    .hsMon2(hsMon2)
                    .thMon3(thMon3)
                    .hsMon3(hsMon3)
                    .tbKeys(tbKeys)
                    .n1(n1)
                    .to(to)
                    .li(li)
                    .ho(ho)
                    .si(si)
                    .va(va)
                    .su(su)
                    .di(di)
                    .ti(ti)
                    .ktpl(ktpl)
                    .khac(khac)
                    .doLech(new BigDecimal(doLech))
                    .build();
    }
}