package com.tuyensinh.utilWeb;

import com.tuyensinh.model.DiemThi;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Tien ich tinh toan diem xet tuyen theo quy dinh 2025.
 */
public class CongThucUtil {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal MAX_SCORE = new BigDecimal("30");
    private static final BigDecimal NGUONG_UU_TIEN = new BigDecimal("22.5");
    private static final BigDecimal HE_SO_GIAM_UT = new BigDecimal("7.5");
    private static final BigDecimal MAX_DIEM_CONG = new BigDecimal("3.0");
    private static final int SCALE_SCORE = 2;

    // Ban do cau hinh mon hoc va he so (Muc 3.1)
    private static final Map<String, Map<String, Integer>> TO_HOP_CONFIG = createToHopConfig();

    // Ban do muc chenh lech giua cac to hop (Muc 2.1)
    private static final Map<String, Map<String, BigDecimal>> CHENH_LECH_MAP = createChenhLechMap();

    private CongThucUtil() {
    }

    /**
     * 1. Tinh tong diem cong (DC) - Muc 3.3
     * Bao gom diem chung chi va diem uu tien xet tuyen. Khong vuot qua 3.0.
     */
    public static BigDecimal tinhTongDiemCong(BigDecimal diemCc, BigDecimal diemUtxt) {
        BigDecimal tong = safe(diemCc).add(safe(diemUtxt));
        if (tong.compareTo(MAX_DIEM_CONG) > 0) {
            tong = MAX_DIEM_CONG;
        }
        return tong.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 1b. Tinh tong diem cong (DC) - Chi tiet hon
     * DC = diemCongChungChi + diemCongUuTien, khong vuot qua 3.0
     * 
     * @param diemCongChungChi Diem cong tu chung chi (VD: chung chi tieng Anh)
     * @param diemCongUuTien   Diem cong tu dieu kien uu tien xet tuyen
     * @return Tong diem cong (DC), max 3.0
     */
    public static BigDecimal tinhDiemCongToiDa(BigDecimal diemCongChungChi, BigDecimal diemCongUuTien) {
        BigDecimal tong = safe(diemCongChungChi).add(safe(diemCongUuTien));
        if (tong.compareTo(MAX_DIEM_CONG) > 0) {
            tong = MAX_DIEM_CONG;
        }
        return tong.setScale(SCALE_SCORE, RoundingMode.HALF_UP);
    }

    /**
     * 1c. Tinh muc diem uu tien (MDUT) - Dieu 7 Quy che tuyen sinh
     * MDUT = diem khu vuc + diem doi tuong
     * 
     * @param khuVuc   Gia tri khu vuc uu tien (VD: "0", "0.25", "0.5", "0.75")
     * @param doiTuong Gia tri doi tuong uu tien (VD: "0", "1", "2")
     * @return Muc diem uu tien (MDUT)
     */
    public static BigDecimal tinhMucDiemUuTien(String khuVuc, String doiTuong) {
        BigDecimal khuVucDiem = parseStringToDecimal(khuVuc);
        BigDecimal doiTuongDiem = parseStringToDecimal(doiTuong);
        return khuVucDiem.add(doiTuongDiem).setScale(SCALE_SCORE, RoundingMode.HALF_UP);
    }

    /**
     * 1c-helper. Convert string sang BigDecimal an toan
     */
    private static BigDecimal parseStringToDecimal(String value) {
        if (value == null || value.isBlank()) {
            return ZERO;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (Exception e) {
            return ZERO;
        }
    }

    /**
     * 2. Tinh diem to hop xet tuyen (DTHXT) - Muc 3.1
     * - V-SAT/THPT: [(d1*w1 + d2*w2 + d3*w3) / (w1+w2+w3)] * 3
     * - DGNL: diem DGNL da quy doi tuong duong ve thang 30 (Muc 2.2).
     */
    public static BigDecimal tinhDiemToHopXetTuyen(
            DiemThi diemThi,
            String maToHop,
            String phuongThuc,
            BigDecimal diemDgnlDaQuyDoi) {
        String pt = normalize(phuongThuc);
        if ("DGNL".equals(pt)) {
            return safe(diemDgnlDaQuyDoi).setScale(SCALE_SCORE, RoundingMode.HALF_UP);
        }

        if (diemThi == null || maToHop == null || maToHop.isBlank()) {
            return ZERO;
        }

        Map<String, Integer> config = TO_HOP_CONFIG.get(maToHop.trim().toUpperCase(Locale.ROOT));
        if (config == null) {
            return ZERO;
        }

        BigDecimal tongDiemCoHeSo = ZERO;
        int tongHeSo = 0;

        for (Map.Entry<String, Integer> entry : config.entrySet()) {
            BigDecimal diemMon = getMonScore(diemThi, entry.getKey());
            int heSo = entry.getValue();
            tongDiemCoHeSo = tongDiemCoHeSo.add(diemMon.multiply(new BigDecimal(heSo)));
            tongHeSo += heSo;
        }

        if (tongHeSo == 0) {
            return ZERO;
        }

        return tongDiemCoHeSo.divide(new BigDecimal(tongHeSo), 10, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("3"))
                .setScale(SCALE_SCORE, RoundingMode.HALF_UP);
    }

    public static BigDecimal tinhDiemToHopXetTuyen(DiemThi diemThi, String maToHop) {
        String phuongThuc = diemThi == null ? null : diemThi.getDPhuongthuc();
        BigDecimal diemDgnlFallback = diemThi == null ? ZERO : diemThi.getNl1();
        return tinhDiemToHopXetTuyen(diemThi, maToHop, phuongThuc, diemDgnlFallback);
    }

    /**
     * 3. Quy doi ve diem to hop goc (DTHGXT) - Muc 2.1 & 3.2
     * Neu ta co diem cua toHopXetTuyen va muon quy ve toHopGoc:
     * DTHGXT = DTHXT + ChenhLech_dao_nguoc
     * (dao nguoc vi bang bang co chieu tu toHopGoc -> toHopXetTuyen)
     */
    public static BigDecimal quyDoiVeToHopGoc(BigDecimal diemThxt, String toHopGoc, String toHopXetTuyen) {
        if (toHopGoc.equals(toHopXetTuyen))
            return safe(diemThxt);

        BigDecimal chenhLech = ZERO;
        String gocUpper = toHopGoc.toUpperCase();
        String xetUpper = toHopXetTuyen.toUpperCase();

        if (CHENH_LECH_MAP.containsKey(gocUpper)) {
            Map<String, BigDecimal> subMap = CHENH_LECH_MAP.get(gocUpper);
            if (subMap.containsKey(xetUpper)) {
                BigDecimal fromMap = subMap.get(xetUpper);
                chenhLech = fromMap.negate();
            }
        }
        return safe(diemThxt).add(chenhLech).setScale(SCALE_SCORE, RoundingMode.HALF_UP);
    }

    /**
     * 4. Tinh diem uu tien (DUT) - Muc 3.4 Dieu 7 Quy che tuyen sinh
     * Diem uu tien theo tung phuong thuc, bao gom diem uu tien doi tuong va diem uu
     * tien khu vuc
     * 
     * Cong thuc:
     * - Neu (DTHGXT + DC) < 22.5 diem: DUT = MDUT
     * - Neu (DTHGXT + DC) >= 22.5 diem: DUT = [(30 - DTHGXT - DC) / 7.5] x MDUT
     * 
     * @param diemThgxt     Diem to hop goc xet tuyen (DTHGXT)
     * @param diemCong      Tong diem cong (DC)
     * @param mucDiemUuTien Muc diem uu tien theo Dieu 7 (MDUT) = diem khu vuc +
     *                      diem doi tuong
     * @return Diem uu tien (DUT)
     */
    public static BigDecimal tinhDiemUuTien(BigDecimal diemThgxt, BigDecimal diemCong, BigDecimal mucDiemUuTien) {
        BigDecimal tongDiemHienTai = safe(diemThgxt).add(safe(diemCong));
        BigDecimal mDUT = safe(mucDiemUuTien);

        if (tongDiemHienTai.compareTo(NGUONG_UU_TIEN) < 0) {
            // Truong hop 1: (DTHGXT + DC) < 22.5 => DUT = MDUT
            return mDUT.setScale(SCALE_SCORE, RoundingMode.HALF_UP);
        }

        // Truong hop 2: (DTHGXT + DC) >= 22.5 => DUT = [(30 - DTHGXT - DC) / 7.5] *
        // MDUT
        BigDecimal tyLeGiam = MAX_SCORE.subtract(tongDiemHienTai)
                .divide(HE_SO_GIAM_UT, 10, RoundingMode.HALF_UP);

        BigDecimal diemUuTien = tyLeGiam.multiply(mDUT);
        if (diemUuTien.compareTo(ZERO) < 0) {
            diemUuTien = ZERO;
        }
        return diemUuTien.setScale(SCALE_SCORE, RoundingMode.HALF_UP);
    }

    /**
     * 5. Tinh diem xet tuyen cuoi cung (DXT) - Muc 3.5
     * DXT = DTHGXT + DC + DUT.
     */
    public static BigDecimal tinhDiemXetTuyenCuoiCung(BigDecimal diemThgxt, BigDecimal diemCong, BigDecimal diemUt) {
        BigDecimal tong = safe(diemThgxt).add(safe(diemCong)).add(safe(diemUt));
        if (tong.compareTo(MAX_SCORE) > 0)
            tong = MAX_SCORE;
        return tong.setScale(SCALE_SCORE, RoundingMode.HALF_UP);
    }

    private static BigDecimal getMonScore(DiemThi d, String mon) {
        return switch (mon) {
            case "TO" -> safe(d.getToan());
            case "LI" -> safe(d.getVatLi());
            case "HO" -> safe(d.getHoaHoc());
            case "SI" -> safe(d.getSinhHoc());
            case "SU" -> safe(d.getLichSu());
            case "DI" -> safe(d.getDiaLi());
            case "VA" -> safe(d.getNguVan());
            case "N1" -> safe(d.getN1Thi());
            default -> ZERO;
        };
    }

    private static BigDecimal safe(BigDecimal value) {
        return value == null ? ZERO : value;
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private static Map<String, Map<String, Integer>> createToHopConfig() {
        Map<String, Map<String, Integer>> map = new HashMap<>();
        map.put("A00", Map.of("TO", 1, "LI", 1, "HO", 1));
        map.put("A01", Map.of("TO", 1, "LI", 1, "N1", 1));
        map.put("B00", Map.of("TO", 1, "HO", 1, "SI", 1));
        map.put("C00", Map.of("VA", 1, "SU", 1, "DI", 1));
        map.put("C01", Map.of("VA", 1, "TO", 1, "LI", 1));
        map.put("D01", Map.of("TO", 1, "VA", 1, "N1", 1));
        map.put("D07", Map.of("TO", 1, "HO", 1, "N1", 1));
        return map;
    }

    private static Map<String, Map<String, BigDecimal>> createChenhLechMap() {
        Map<String, Map<String, BigDecimal>> matrix = new HashMap<>();
        matrix.put("A00", Map.of(
                "A01", new BigDecimal("-0.69"),
                "B00", new BigDecimal("-1.21"),
                "C00", new BigDecimal("2.32"),
                "C01", new BigDecimal("0.94"),
                "D01", new BigDecimal("-0.68"),
                "D07", new BigDecimal("-1.62")));
        matrix.put("A01", Map.of(
                "A00", new BigDecimal("0.69"),
                "B00", new BigDecimal("-0.52"),
                "C00", new BigDecimal("3.01"),
                "C01", new BigDecimal("1.63"),
                "D01", new BigDecimal("0.01"),
                "D07", new BigDecimal("-0.93")));
        matrix.put("B00", Map.of(
                "A00", new BigDecimal("1.21"),
                "A01", new BigDecimal("0.52"),
                "C00", new BigDecimal("3.53"),
                "C01", new BigDecimal("2.15"),
                "D01", new BigDecimal("0.53"),
                "D07", new BigDecimal("-0.41")));
        matrix.put("C00", Map.of(
                "A00", new BigDecimal("-2.32"),
                "A01", new BigDecimal("-3.01"),
                "B00", new BigDecimal("-3.53"),
                "C01", new BigDecimal("-1.38"),
                "D01", new BigDecimal("-3.00"),
                "D07", new BigDecimal("-3.94")));
        matrix.put("C01", Map.of(
                "A00", new BigDecimal("-0.94"),
                "A01", new BigDecimal("-1.63"),
                "B00", new BigDecimal("-2.15"),
                "C00", new BigDecimal("1.38"),
                "D01", new BigDecimal("-1.62"),
                "D07", new BigDecimal("-2.56")));
        matrix.put("D01", Map.of(
                "A00", new BigDecimal("0.68"),
                "A01", new BigDecimal("-0.01"),
                "B00", new BigDecimal("-0.53"),
                "C00", new BigDecimal("3.00"),
                "C01", new BigDecimal("1.62"),
                "D07", new BigDecimal("-0.94")));
        matrix.put("D07", Map.of(
                "A00", new BigDecimal("1.62"),
                "A01", new BigDecimal("0.93"),
                "B00", new BigDecimal("0.41"),
                "C00", new BigDecimal("3.94"),
                "C01", new BigDecimal("2.56"),
                "D01", new BigDecimal("0.94")));
        return matrix;
    }

    /**
     * Quy đổi điểm ĐGNL (thang 1200 → 30)
     * Công thức: điểm * 30 / 1200
     */
    public static BigDecimal quyDoiDgnl1200Sang30(BigDecimal diem1200) {
        if (diem1200 == null)
            return ZERO;

        return diem1200
                .multiply(new BigDecimal("30"))
                .divide(new BigDecimal("1200"), SCALE_SCORE, RoundingMode.HALF_UP);
    }

    /**
     * Quy đổi điểm VSAT (thang 150 → 10)
     * Công thức: điểm * 10 / 150
     */
    public static BigDecimal quyDoiVsat150Sang10(BigDecimal diem150) {
        if (diem150 == null)
            return ZERO;

        return diem150
                .multiply(new BigDecimal("10"))
                .divide(new BigDecimal("150"), SCALE_SCORE, RoundingMode.HALF_UP);
    }

    /**
     * Quy đổi điểm bất kỳ về thang 10
     */
    public static BigDecimal quyDoiVeThang10(BigDecimal diem, BigDecimal maxInput) {
        if (diem == null || maxInput == null || maxInput.compareTo(ZERO) == 0) {
            return ZERO;
        }

        return diem
                .multiply(new BigDecimal("10"))
                .divide(maxInput, SCALE_SCORE, RoundingMode.HALF_UP);
    }

    /**
     * Điểm N1 = max(thi, chứng chỉ)
     */
    public static BigDecimal tinhDiemN1(BigDecimal n1Thi, BigDecimal n1Cc) {
        BigDecimal thi = safe(n1Thi);
        BigDecimal cc = safe(n1Cc);

        return thi.max(cc).setScale(SCALE_SCORE, RoundingMode.HALF_UP);
    }

    /**
     * Kiểm tra đạt ngưỡng
     */
    public static boolean datNguong(BigDecimal dxt, BigDecimal nguong) {
        if (dxt == null || nguong == null)
            return false;
        return dxt.compareTo(nguong) >= 0;
    }

    /**
     * Kiểm tra trúng tuyển
     */
    public static boolean datTrungTuyen(BigDecimal dxt, BigDecimal diemChuan) {
        if (dxt == null || diemChuan == null)
            return false;
        return dxt.compareTo(diemChuan) >= 0;
    }

    /**
     * Tổng điểm 3 môn (không hệ số)
     */
    public static BigDecimal tinhTong3Mon(BigDecimal d1, BigDecimal d2, BigDecimal d3) {
        return safe(d1)
                .add(safe(d2))
                .add(safe(d3))
                .setScale(SCALE_SCORE, RoundingMode.HALF_UP);
    }

    /**
     * Giới hạn điểm tối đa
     */
    public static BigDecimal gioiHanDiem(BigDecimal diem, BigDecimal max) {
        if (diem == null)
            return ZERO;
        return diem.compareTo(max) > 0 ? max : diem;
    }

}
