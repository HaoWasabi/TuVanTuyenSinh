package com.tuyensinh;

import com.tuyensinh.model.DiemThi;
import com.tuyensinh.utilWeb.CongThucUtil;
import java.math.BigDecimal;

/**
 * Test kiểm chứng các công thức tính điểm xét tuyển theo quy định 2025.
 * Theo tài liệu: Mục 3.1 đến 3.5
 */
public class CongThucUtilTest {

    public static void main(String[] args) {
        System.out.println("=== TEST CÔNG THỨC TÍNH ĐIỂM XÉT TUYỂN ===\n");

        // Test 1: Tính điểm tổ hợp xét tuyển THPT (Mục 3.1)
        testDiemToHopXetTuyenThpt();

        // Test 2: Tính điểm tổ hợp xét tuyển DGNL (Mục 3.1)
        testDiemToHopXetTuyenDgnl();

        // Test 3: Quy đổi về tổ hợp gốc (Mục 2.1 & 3.2)
        testQuyDoiVeToHopGoc();

        // Test 4: Tính tổng điểm cộng (Mục 3.3)
        testTinhTongDiemCong();

        // Test 5: Tính điểm ưu tiên (Mục 3.4)
        testTinhDiemUuTien();

        // Test 6: Tính điểm xét tuyển cuối cùng (Mục 3.5)
        testTinhDiemXetTuyenCuoiCung();

        System.out.println("\n=== KẾT THÚC TEST ===");
    }

    /**
     * Test 1: Công thức DTHXT cho phương thức THPT/V-SAT
     * DTHXT = [(d1*w1 + d2*w2 + d3*w3) / (w1+w2+w3)] * 3
     * Ví dụ: Tổ hợp A00 (Toán, Lý, Hóa) với hệ số 1:1:1
    * d1=8.5, d2=7.75, d3=8.0 => DTHXT = [(8.5+7.75+8.0)/3] * 3 = 24.25
     */
    private static void testDiemToHopXetTuyenThpt() {
        System.out.println("--- Test 1: Tính DTHXT THPT (Tổ hợp A00) ---");

        DiemThi diemThi = DiemThi.builder()
                .cccd("012345678901")
                .dPhuongthuc("THPT")
                .toan(new BigDecimal("8.50"))
                .vatLi(new BigDecimal("7.75"))
                .hoaHoc(new BigDecimal("8.00"))
                .sinhHoc(new BigDecimal("7.50"))
                .lichSu(new BigDecimal("6.50"))
                .diaLi(new BigDecimal("7.00"))
                .nguVan(new BigDecimal("7.25"))
                .build();

        BigDecimal resultA00 = CongThucUtil.tinhDiemToHopXetTuyen(diemThi, "A00", "THPT", null);
        // Kỳ vọng: (8.5 + 7.75 + 8.0) / 3 * 3 = 24.25
        BigDecimal expected = new BigDecimal("24.25");
        boolean pass = Math.abs(resultA00.doubleValue() - expected.doubleValue()) < 0.01;

        System.out.println("  Tổ hợp: A00 (Toán:8.50, Lý:7.75, Hóa:8.00)");
        System.out.println("  Kỳ vọng: " + expected);
        System.out.println("  Kết quả: " + resultA00);
        System.out.println("  Trạng thái: " + (pass ? "✓ PASS" : "✗ FAIL"));
    }

    /**
     * Test 2: Công thức DTHXT cho phương thức DGNL
     * DTHXT = Điểm DGNL đã quy đổi (thang 30)
     * Ví dụ: Điểm DGNL quy đổi = 22.5
     */
    private static void testDiemToHopXetTuyenDgnl() {
        System.out.println("\n--- Test 2: Tính DTHXT DGNL ---");

        DiemThi diemThi = DiemThi.builder()
                .cccd("012345678901")
                .dPhuongthuc("DGNL")
                .nl1(new BigDecimal("22.50"))
                .build();

        BigDecimal resultDGNL = CongThucUtil.tinhDiemToHopXetTuyen(
                diemThi, "A00", "DGNL", new BigDecimal("22.50"));
        // Kỳ vọng: 22.50 (lấy trực tiếp điểm DGNL đã quy đổi)
        BigDecimal expected = new BigDecimal("22.50");
        boolean pass = Math.abs(resultDGNL.doubleValue() - expected.doubleValue()) < 0.01;

        System.out.println("  Phương thức: DGNL");
        System.out.println("  Điểm DGNL quy đổi: 22.50 (thang 30)");
        System.out.println("  Kỳ vọng: " + expected);
        System.out.println("  Kết quả: " + resultDGNL);
        System.out.println("  Trạng thái: " + (pass ? "✓ PASS" : "✗ FAIL"));
    }

    /**
     * Test 3: Quy đổi về tổ hợp gốc
    * DTHGXT = DTHXT + Chenh lech_dao_nguoc
     * Ví dụ: DTHXT = 24.75 (A01), quy về A00 với chenh lech = 0.69
    * DTHGXT = 24.75 + 0.69 = 25.44
     */
    private static void testQuyDoiVeToHopGoc() {
        System.out.println("\n--- Test 3: Quy đổi DTHXT về tổ hợp gốc ---");

        BigDecimal diemThxt = new BigDecimal("24.75");
        String toHopGoc = "A00";
        String toHopXetTuyen = "A01";

        BigDecimal resultQuyDoi = CongThucUtil.quyDoiVeToHopGoc(diemThxt, toHopGoc, toHopXetTuyen);
        // Kỳ vọng: 24.75 + 0.69 = 25.44
        BigDecimal expected = new BigDecimal("25.44");
        boolean pass = Math.abs(resultQuyDoi.doubleValue() - expected.doubleValue()) < 0.01;

        System.out.println("  DTHXT hiện tại: " + diemThxt + " (Tổ hợp " + toHopXetTuyen + ")");
        System.out.println("  Quy về tổ hợp: " + toHopGoc);
        System.out.println("  Chenh lech: 0.69");
        System.out.println("  Kỳ vọng: " + expected);
        System.out.println("  Kết quả: " + resultQuyDoi);
        System.out.println("  Trạng thái: " + (pass ? "✓ PASS" : "✗ FAIL"));

        // Sub-test với tổ hợp trước đây chưa có dữ liệu trong map
        System.out.println("\n  Sub-test: Quy đổi từ D07 về C00");
        BigDecimal diemThxtD07 = new BigDecimal("20.00");
        BigDecimal resultQuyDoiC00 = CongThucUtil.quyDoiVeToHopGoc(diemThxtD07, "C00", "D07");
        BigDecimal expectedC00 = new BigDecimal("23.94"); // 20.00 - (-3.94)
        boolean passC00 = Math.abs(resultQuyDoiC00.doubleValue() - expectedC00.doubleValue()) < 0.01;

        System.out.println("  DTHXT hiện tại: " + diemThxtD07 + " (Tổ hợp D07)");
        System.out.println("  Quy về tổ hợp: C00");
        System.out.println("  Chenh lech C00->D07: -3.94");
        System.out.println("  Kỳ vọng: " + expectedC00);
        System.out.println("  Kết quả: " + resultQuyDoiC00);
        System.out.println("  Trạng thái: " + (passC00 ? "✓ PASS" : "✗ FAIL"));
    }

    /**
     * Test 4: Tính tổng điểm cộng
     * DC = Điểm chứng chỉ + Điểm ưu tiên xét tuyển
     * Giới hạn tối đa: 3.0
     * Ví dụ: DC = 0.50 + 0.25 = 0.75
     */
    private static void testTinhTongDiemCong() {
        System.out.println("\n--- Test 4: Tính tổng điểm cộng (DC) ---");

        BigDecimal diemCc = new BigDecimal("0.50");
        BigDecimal diemUtxt = new BigDecimal("0.25");

        BigDecimal resultDc = CongThucUtil.tinhTongDiemCong(diemCc, diemUtxt);
        // Kỳ vọng: 0.50 + 0.25 = 0.75
        BigDecimal expected = new BigDecimal("0.75");
        boolean pass = Math.abs(resultDc.doubleValue() - expected.doubleValue()) < 0.01;

        System.out.println("  Điểm chứng chỉ: " + diemCc);
        System.out.println("  Điểm ưu tiên xét tuyển: " + diemUtxt);
        System.out.println("  Kỳ vọng: " + expected);
        System.out.println("  Kết quả: " + resultDc);
        System.out.println("  Trạng thái: " + (pass ? "✓ PASS" : "✗ FAIL"));

        // Test giới hạn tối đa
        System.out.println("\n  Sub-test: Kiểm tra giới hạn tối đa 3.0");
        BigDecimal diemCcMax = new BigDecimal("2.00");
        BigDecimal diemUtxtMax = new BigDecimal("2.00");
        BigDecimal resultDcMax = CongThucUtil.tinhTongDiemCong(diemCcMax, diemUtxtMax);
        BigDecimal expectedMax = new BigDecimal("3.00");
        boolean passMax = Math.abs(resultDcMax.doubleValue() - expectedMax.doubleValue()) < 0.01;

        System.out.println("    Cộng: 2.00 + 2.00 = 4.00 => giới hạn tối đa = 3.00");
        System.out.println("    Kết quả: " + resultDcMax);
        System.out.println("    Trạng thái: " + (passMax ? "✓ PASS" : "✗ FAIL"));
    }

    /**
     * Test 5: Tính điểm ưu tiên (DUT)
     * Nếu (DTHGXT + DC) < 22.5: DUT = MDUT
     * Nếu (DTHGXT + DC) >= 22.5: DUT = [(30 - (DTHGXT + DC)) / 7.5] * MDUT
     *
     * Ví dụ 1: DTHGXT=20, DC=0.75, MDUT=1.5 => tổng=20.75 < 22.5 => DUT=1.5
     * Ví dụ 2: DTHGXT=24, DC=0.75, MDUT=1.5 => tổng=24.75 >= 22.5
     *          DUT = [(30-24.75)/7.5]*1.5 = [5.25/7.5]*1.5 = 0.7*1.5 = 1.05
     */
    private static void testTinhDiemUuTien() {
        System.out.println("\n--- Test 5: Tính điểm ưu tiên (DUT) ---");

        BigDecimal mdut = new BigDecimal("1.50");

        // Trường hợp 1: Tổng < 22.5 => DUT = MDUT
        System.out.println("\n  Trường hợp 1: (DTHGXT + DC) < 22.5");
        BigDecimal diemThgxt1 = new BigDecimal("20.00");
        BigDecimal diemCong1 = new BigDecimal("0.75");
        BigDecimal result1 = CongThucUtil.tinhDiemUuTien(diemThgxt1, diemCong1, mdut);
        BigDecimal expected1 = mdut;
        boolean pass1 = Math.abs(result1.doubleValue() - expected1.doubleValue()) < 0.01;

        System.out.println("    DTHGXT: " + diemThgxt1 + ", DC: " + diemCong1);
        System.out.println("    Tổng: " + diemThgxt1.add(diemCong1) + " < 22.5");
        System.out.println("    MDUT: " + mdut);
        System.out.println("    Kỳ vọng: " + expected1);
        System.out.println("    Kết quả: " + result1);
        System.out.println("    Trạng thái: " + (pass1 ? "✓ PASS" : "✗ FAIL"));

        // Trường hợp 2: Tổng >= 22.5 => áp dụng công thức giảm
        System.out.println("\n  Trường hợp 2: (DTHGXT + DC) >= 22.5");
        BigDecimal diemThgxt2 = new BigDecimal("24.00");
        BigDecimal diemCong2 = new BigDecimal("0.75");
        BigDecimal result2 = CongThucUtil.tinhDiemUuTien(diemThgxt2, diemCong2, mdut);
        // DUT = [(30 - 24.75) / 7.5] * 1.5 = [5.25 / 7.5] * 1.5 = 0.7 * 1.5 = 1.05
        BigDecimal expected2 = new BigDecimal("1.05");
        boolean pass2 = Math.abs(result2.doubleValue() - expected2.doubleValue()) < 0.01;

        System.out.println("    DTHGXT: " + diemThgxt2 + ", DC: " + diemCong2);
        System.out.println("    Tổng: " + diemThgxt2.add(diemCong2) + " >= 22.5");
        System.out.println("    DUT = [(30 - 24.75) / 7.5] * 1.5 = 1.05");
        System.out.println("    Kỳ vọng: " + expected2);
        System.out.println("    Kết quả: " + result2);
        System.out.println("    Trạng thái: " + (pass2 ? "✓ PASS" : "✗ FAIL"));
    }

    /**
     * Test 6: Tính điểm xét tuyển cuối cùng
     * DXT = DTHGXT + DC + DUT
     * Giới hạn tối đa: 30
     *
     * Ví dụ: DTHGXT=24.06, DC=0.75, DUT=1.05
     *        DXT = 24.06 + 0.75 + 1.05 = 25.86
     */
    private static void testTinhDiemXetTuyenCuoiCung() {
        System.out.println("\n--- Test 6: Tính điểm xét tuyển cuối cùng (DXT) ---");

        BigDecimal diemThgxt = new BigDecimal("24.06");
        BigDecimal diemCong = new BigDecimal("0.75");
        BigDecimal diemUt = new BigDecimal("1.05");

        BigDecimal resultDxt = CongThucUtil.tinhDiemXetTuyenCuoiCung(diemThgxt, diemCong, diemUt);
        // Kỳ vọng: 24.06 + 0.75 + 1.05 = 25.86
        BigDecimal expected = new BigDecimal("25.86");
        boolean pass = Math.abs(resultDxt.doubleValue() - expected.doubleValue()) < 0.01;

        System.out.println("  DTHGXT: " + diemThgxt);
        System.out.println("  DC: " + diemCong);
        System.out.println("  DUT: " + diemUt);
        System.out.println("  Kỳ vọng: " + expected);
        System.out.println("  Kết quả: " + resultDxt);
        System.out.println("  Trạng thái: " + (pass ? "✓ PASS" : "✗ FAIL"));

        // Test giới hạn tối đa 30
        System.out.println("\n  Sub-test: Kiểm tra giới hạn tối đa 30");
        BigDecimal diemThgxtMax = new BigDecimal("28.00");
        BigDecimal diemCongMax = new BigDecimal("2.00");
        BigDecimal diemUtMax = new BigDecimal("1.50");
        BigDecimal resultDxtMax = CongThucUtil.tinhDiemXetTuyenCuoiCung(diemThgxtMax, diemCongMax, diemUtMax);
        // Cộng: 28 + 2 + 1.5 = 31.5 => giới hạn = 30
        BigDecimal expectedMax = new BigDecimal("30.00");
        boolean passMax = Math.abs(resultDxtMax.doubleValue() - expectedMax.doubleValue()) < 0.01;

        System.out.println("    Cộng: 28.00 + 2.00 + 1.50 = 31.50 => giới hạn tối đa = 30.00");
        System.out.println("    Kết quả: " + resultDxtMax);
        System.out.println("    Trạng thái: " + (passMax ? "✓ PASS" : "✗ FAIL"));
    }
}
