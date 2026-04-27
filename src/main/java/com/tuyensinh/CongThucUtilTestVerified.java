package com.tuyensinh;

import com.tuyensinh.model.DiemThi;
import com.tuyensinh.utilWeb.CongThucUtil;
import java.math.BigDecimal;

/**
 * Test kiểm chứng các công thức tính điểm xét tuyển theo quy định 2025.
 * Các ví dụ dựa trên công thức tài liệu mục 3.1 đến 3.5.
 */
public class CongThucUtilTestVerified {

    public static void main(String[] args) {
        System.out.println("=== TEST CÔNG THỨC TÍNH ĐIỂM XÉT TUYỂN (ĐÃ KIỂM CHỨNG) ===\n");

        testDiemToHopXetTuyenThpt();
        testDiemToHopXetTuyenDgnl();
        testTinhTongDiemCong();
        testTinhDiemUuTien();
        testTinhDiemXetTuyenCuoiCung();

        System.out.println("\n=== KẾT THÚC TEST ===");
    }

    /**
     * Test 1: DTHXT THPT
     * Công thức: [(d1*w1 + d2*w2 + d3*w3) / (w1+w2+w3)] * 3
     * Ví dụ thực: d=(8.5, 7.75, 8.0), w=(1,1,1)
     * => [(8.5+7.75+8.0) / 3] * 3 = [24.25/3] * 3 = 8.0833... * 3 = 24.25 ✓
     */
    private static void testDiemToHopXetTuyenThpt() {
        System.out.println("--- Test 1: Tính DTHXT THPT (Tổ hợp A00) ---");

        DiemThi diemThi = DiemThi.builder()
                .dPhuongthuc("THPT")
                .toan(new BigDecimal("8.50"))
                .vatLi(new BigDecimal("7.75"))
                .hoaHoc(new BigDecimal("8.00"))
                .build();

        BigDecimal result = CongThucUtil.tinhDiemToHopXetTuyen(diemThi, "A00", "THPT", null);
        BigDecimal expected = new BigDecimal("24.25");
        boolean pass = Math.abs(result.doubleValue() - expected.doubleValue()) < 0.01;

        System.out.println("  Tổ hợp: A00 | Điểm: 8.50, 7.75, 8.00 | Hệ số: 1:1:1");
        System.out.println("  Kỳ vọng: " + expected + " | Kết quả: " + result);
        System.out.println("  Trạng thái: " + (pass ? "✓ PASS" : "✗ FAIL\n"));
    }

    /**
     * Test 2: DTHXT DGNL
     * DTHXT = Điểm DGNL đã quy đổi (thang 30)
     */
    private static void testDiemToHopXetTuyenDgnl() {
        System.out.println("\n--- Test 2: Tính DTHXT DGNL ---");

        BigDecimal diemDgnlQuyDoi = new BigDecimal("22.50");
        BigDecimal result = CongThucUtil.tinhDiemToHopXetTuyen(null, "A00", "DGNL", diemDgnlQuyDoi);
        BigDecimal expected = new BigDecimal("22.50");
        boolean pass = Math.abs(result.doubleValue() - expected.doubleValue()) < 0.01;

        System.out.println("  Phương thức: DGNL | Điểm quy đổi: 22.50 (thang 30)");
        System.out.println("  Kỳ vọng: " + expected + " | Kết quả: " + result);
        System.out.println("  Trạng thái: " + (pass ? "✓ PASS" : "✗ FAIL"));
    }

    /**
     * Test 3: Tính tổng điểm cộng (DC)
     * DC = Điểm chứng chỉ + Điểm ưu tiên, max 3.0
     */
    private static void testTinhTongDiemCong() {
        System.out.println("\n--- Test 3: Tính tổng điểm cộng (DC) ---");

        // Case 1: Tổng bình thường
        BigDecimal dc1 = CongThucUtil.tinhTongDiemCong(new BigDecimal("0.50"), new BigDecimal("0.25"));
        BigDecimal exp1 = new BigDecimal("0.75");
        boolean p1 = Math.abs(dc1.doubleValue() - exp1.doubleValue()) < 0.01;

        System.out.println("  Case 1: 0.50 + 0.25");
        System.out.println("    Kỳ vọng: " + exp1 + " | Kết quả: " + dc1);
        System.out.println("    Trạng thái: " + (p1 ? "✓ PASS" : "✗ FAIL"));

        // Case 2: Giới hạn tối đa 3.0
        BigDecimal dc2 = CongThucUtil.tinhTongDiemCong(new BigDecimal("2.00"), new BigDecimal("2.00"));
        BigDecimal exp2 = new BigDecimal("3.00");
        boolean p2 = Math.abs(dc2.doubleValue() - exp2.doubleValue()) < 0.01;

        System.out.println("  Case 2: 2.00 + 2.00 => giới hạn 3.00");
        System.out.println("    Kỳ vọng: " + exp2 + " | Kết quả: " + dc2);
        System.out.println("    Trạng thái: " + (p2 ? "✓ PASS" : "✗ FAIL"));
    }

    /**
     * Test 4: Tính điểm ưu tiên (DUT)
     * - Nếu (DTHGXT + DC) < 22.5: DUT = MDUT
     * - Nếu (DTHGXT + DC) >= 22.5: DUT = [(30 - (DTHGXT+DC)) / 7.5] * MDUT
     */
    private static void testTinhDiemUuTien() {
        System.out.println("\n--- Test 4: Tính điểm ưu tiên (DUT) ---");

        BigDecimal mdut = new BigDecimal("1.50");

        // Case 1: Tổng < 22.5 => DUT = MDUT
        BigDecimal dut1 = CongThucUtil.tinhDiemUuTien(
                new BigDecimal("20.00"),
                new BigDecimal("0.75"),
                mdut
        );
        BigDecimal exp1 = new BigDecimal("1.50");
        boolean p1 = Math.abs(dut1.doubleValue() - exp1.doubleValue()) < 0.01;

        System.out.println("  Case 1: (DTHGXT + DC) < 22.5 => DUT = MDUT");
        System.out.println("    DTHGXT=20.00, DC=0.75, Tổng=20.75 < 22.5");
        System.out.println("    Kỳ vọng: " + exp1 + " | Kết quả: " + dut1);
        System.out.println("    Trạng thái: " + (p1 ? "✓ PASS" : "✗ FAIL"));

        // Case 2: Tổng >= 22.5 => áp dụng công thức giảm
        BigDecimal dut2 = CongThucUtil.tinhDiemUuTien(
                new BigDecimal("24.00"),
                new BigDecimal("0.75"),
                mdut
        );
        // DUT = [(30 - 24.75) / 7.5] * 1.5 = [5.25/7.5] * 1.5 = 0.7 * 1.5 = 1.05
        BigDecimal exp2 = new BigDecimal("1.05");
        boolean p2 = Math.abs(dut2.doubleValue() - exp2.doubleValue()) < 0.01;

        System.out.println("\n  Case 2: (DTHGXT + DC) >= 22.5 => áp dụng công thức giảm");
        System.out.println("    DTHGXT=24.00, DC=0.75, Tổng=24.75 >= 22.5");
        System.out.println("    DUT = [(30-24.75)/7.5]*1.5 = 1.05");
        System.out.println("    Kỳ vọng: " + exp2 + " | Kết quả: " + dut2);
        System.out.println("    Trạng thái: " + (p2 ? "✓ PASS" : "✗ FAIL"));
    }

    /**
     * Test 5: Tính điểm xét tuyển cuối cùng (DXT)
     * DXT = DTHGXT + DC + DUT, max 30
     */
    private static void testTinhDiemXetTuyenCuoiCung() {
        System.out.println("\n--- Test 5: Tính điểm xét tuyển cuối cùng (DXT) ---");

        // Case 1: Tổng bình thường
        BigDecimal dxt1 = CongThucUtil.tinhDiemXetTuyenCuoiCung(
                new BigDecimal("24.00"),
                new BigDecimal("0.75"),
                new BigDecimal("1.05")
        );
        BigDecimal exp1 = new BigDecimal("25.80");
        boolean p1 = Math.abs(dxt1.doubleValue() - exp1.doubleValue()) < 0.01;

        System.out.println("  Case 1: 24.00 + 0.75 + 1.05");
        System.out.println("    Kỳ vọng: " + exp1 + " | Kết quả: " + dxt1);
        System.out.println("    Trạng thái: " + (p1 ? "✓ PASS" : "✗ FAIL"));

        // Case 2: Giới hạn tối đa 30
        BigDecimal dxt2 = CongThucUtil.tinhDiemXetTuyenCuoiCung(
                new BigDecimal("28.00"),
                new BigDecimal("2.00"),
                new BigDecimal("1.50")
        );
        // Cộng: 28 + 2 + 1.5 = 31.5 => giới hạn 30
        BigDecimal exp2 = new BigDecimal("30.00");
        boolean p2 = Math.abs(dxt2.doubleValue() - exp2.doubleValue()) < 0.01;

        System.out.println("\n  Case 2: 28.00 + 2.00 + 1.50 = 31.50 => giới hạn 30");
        System.out.println("    Kỳ vọng: " + exp2 + " | Kết quả: " + dxt2);
        System.out.println("    Trạng thái: " + (p2 ? "✓ PASS" : "✗ FAIL"));
    }
}
