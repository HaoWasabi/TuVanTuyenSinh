package com.tuyensinh.serviceWeb;

import com.tuyensinh.ModelWeb.DiemXetTuyenRequest;
import com.tuyensinh.ModelWeb.DiemXetTuyenResponse;
import com.tuyensinh.ModelWeb.DiemXetTuyenResponse.MonQuyDoi;
import com.tuyensinh.model.DiemThi;
import com.tuyensinh.utilWeb.CongThucUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class DiemXetTuyenServiceWeb {

        private static final BigDecimal NGUONG_TUYEN_SINH = new BigDecimal("15.0"); // Ngưỡng tuyển sinh mặc định

        /**
         * API 1: Tính điểm theo phương thức THPT
         * Input: 6 môn (thang 10) + Tổ hợp + Điểm cộng + Điểm ưu tiên
         * Output: Điểm quy đổi về thang 30
         */
        public DiemXetTuyenResponse tinhDiemTHPT(DiemXetTuyenRequest request) {
                // NOTE: removed strict 6-môn validation — inputs default to 0 if omitted;
                // Toán/Văn always considered.

                // Tạo đối tượng DiemThi giả lập từ request
                DiemThi diemThi = DiemThi.builder()
                                .toan(toBigDecimal(request.getToan()))
                                .vatLi(toBigDecimal(request.getLi()))
                                .hoaHoc(toBigDecimal(request.getHoa()))
                                .sinhHoc(toBigDecimal(request.getSinh()))
                                .lichSu(toBigDecimal(request.getSu()))
                                .diaLi(toBigDecimal(request.getDi()))
                                .nguVan(toBigDecimal(request.getVa()))
                                .n1Thi(toBigDecimal(request.getN1()))
                                .dPhuongthuc("THPT")
                                .build();

                // Tính điểm tổ hợp xét tuyển (thang 30) = DTHXT
                BigDecimal diemToHop = CongThucUtil.tinhDiemToHopXetTuyen(diemThi, request.getMaToHop());

                // Tính tổng Điểm cộng (DC) từ diemCongChungChi + diemCongUuTien, max 3.0
                BigDecimal diemCongChungChi = toBigDecimal(request.getDiemCongChungChi());
                BigDecimal diemCongUuTien = toBigDecimal(request.getDiemCongUuTien());
                BigDecimal diemCong = CongThucUtil.tinhDiemCongToiDa(diemCongChungChi, diemCongUuTien);

                // Tính Mức Điểm Ưu Tiên (MDUT) từ khuVuc + doiTuong
                BigDecimal mucDiemUuTien = CongThucUtil.tinhMucDiemUuTien(
                                request.getKhuVuc(),
                                request.getDoiTuong());

                // Tính Điểm Ưu Tiên (ĐƯT)
                BigDecimal diemUuTien = CongThucUtil.tinhDiemUuTien(diemToHop, diemCong, mucDiemUuTien);

                // Tính điểm xét tuyển cuối cùng: ĐXT = DTHXT + DC + DUT
                BigDecimal diemXetTuyen = CongThucUtil.tinhDiemXetTuyenCuoiCung(
                                diemToHop,
                                diemCong,
                                diemUuTien);

                boolean datNguong = CongThucUtil.datNguong(diemXetTuyen, NGUONG_TUYEN_SINH);

                // Nếu frontend gửi chế độ 4-môn (CSV danh sách môn được tính),
                // kiểm tra điều kiện 'liệt' cho Toán, Văn và 2 môn tự chọn.
                boolean fourModeFail = false;
                String csv = request.getFourSelectedSubjects();
                if (csv != null && !csv.isBlank()) {
                        String[] parts = csv.split(",");
                        java.util.Set<String> set = new java.util.HashSet<>();
                        for (String p : parts) {
                                if (p != null && !p.isBlank())
                                        set.add(p.trim().toUpperCase());
                        }
                        // Ensure we only check TO and VA plus up to 2 selected optional subjects
                        java.util.List<String> toCheck = new java.util.ArrayList<>();
                        if (set.contains("TO"))
                                toCheck.add("TO");
                        else
                                toCheck.add("TO"); // always include Toán
                        if (set.contains("VA"))
                                toCheck.add("VA");
                        else
                                toCheck.add("VA"); // always include Văn

                        // add other selections (excluding TO/VA)
                        for (String s : set) {
                                if (!"TO".equals(s) && !"VA".equals(s) && toCheck.size() < 4) {
                                        toCheck.add(s);
                                }
                        }

                        for (String code : toCheck) {
                                BigDecimal score = getScoreForCode(request, code);
                                // nếu điểm null hoặc <= 1 => liệt
                                if (score == null || score.compareTo(new BigDecimal("1")) <= 0) {
                                        fourModeFail = true;
                                        break;
                                }
                        }
                }

                if (fourModeFail) {
                        datNguong = false;
                }

                String thongBao = "Tính điểm THPT thành công - Thang điểm 30";
                if (fourModeFail)
                        thongBao += " | Không đạt ngưỡng do có môn bị liệt (<=1) trong chế độ 4-môn";

                return DiemXetTuyenResponse.builder()
                                .phuongThuc("THPT")
                                .maToHop(request.getMaToHop())
                                .diemToHop(diemToHop)
                                .diemQuyDoi(diemToHop)
                                .diemCongChungChi(diemCongChungChi)
                                .diemCongUuTien(diemCongUuTien)
                                .diemCong(diemCong)
                                .doiTuong(request.getDoiTuong())
                                .khuVuc(request.getKhuVuc())
                                .mucDiemUuTien(mucDiemUuTien)
                                .diemUuTien(diemUuTien)
                                .diemXetTuyen(diemXetTuyen)
                                .thongBao(thongBao)
                                .datNguong(datNguong)
                                .build();
        }

        /**
         * API 2: Tính điểm theo phương thức VSAT
         * Input: 6 môn (thang 150) + Tổ hợp + Điểm cộng + Điểm ưu tiên
         * Output: Điểm quy đổi về thang 30 + Chi tiết từng môn
         */
        public DiemXetTuyenResponse tinhDiemVSAT(DiemXetTuyenRequest request) {
                // Tạo danh sách chi tiết quy đổi từng môn
                List<MonQuyDoi> chiTietQuyDoi = new ArrayList<>();

                // Quy đổi từng môn từ thang 150 → thang 10
                BigDecimal toanQD = CongThucUtil.quyDoiVsat150Sang10(toBigDecimal(request.getToan()));
                BigDecimal liQD = CongThucUtil.quyDoiVsat150Sang10(toBigDecimal(request.getLi()));
                BigDecimal hoaQD = CongThucUtil.quyDoiVsat150Sang10(toBigDecimal(request.getHoa()));
                BigDecimal sinhQD = CongThucUtil.quyDoiVsat150Sang10(toBigDecimal(request.getSinh()));
                BigDecimal suQD = CongThucUtil.quyDoiVsat150Sang10(toBigDecimal(request.getSu()));
                BigDecimal diQD = CongThucUtil.quyDoiVsat150Sang10(toBigDecimal(request.getDi()));

                // Thêm chi tiết từng môn
                chiTietQuyDoi.add(MonQuyDoi.builder().tenMon("Toán").diemGoc(toBigDecimal(request.getToan()))
                                .diemQuyDoi(toanQD).thangDiem("150→10").build());
                chiTietQuyDoi.add(MonQuyDoi.builder().tenMon("Lý").diemGoc(toBigDecimal(request.getLi()))
                                .diemQuyDoi(liQD).thangDiem("150→10").build());
                chiTietQuyDoi.add(MonQuyDoi.builder().tenMon("Hóa").diemGoc(toBigDecimal(request.getHoa()))
                                .diemQuyDoi(hoaQD).thangDiem("150→10").build());
                chiTietQuyDoi.add(MonQuyDoi.builder().tenMon("Sinh").diemGoc(toBigDecimal(request.getSinh()))
                                .diemQuyDoi(sinhQD).thangDiem("150→10").build());
                chiTietQuyDoi.add(MonQuyDoi.builder().tenMon("Sử").diemGoc(toBigDecimal(request.getSu()))
                                .diemQuyDoi(suQD).thangDiem("150→10").build());
                chiTietQuyDoi.add(MonQuyDoi.builder().tenMon("Địa").diemGoc(toBigDecimal(request.getDi()))
                                .diemQuyDoi(diQD).thangDiem("150→10").build());

                // Tính điểm trung bình 6 môn (thang 10)
                BigDecimal diemTrungBinh = tinhDiemTrungBinh6Mon(request);
                BigDecimal diemTrungBinhQuyDoi = CongThucUtil.quyDoiVsat150Sang10(diemTrungBinh);

                // Nhân 3 để ra thang 30
                BigDecimal diemToHop = diemTrungBinhQuyDoi.multiply(new BigDecimal("3"))
                                .setScale(2, RoundingMode.HALF_UP);

                // Tính tổng Điểm cộng (DC) từ diemCongChungChi + diemCongUuTien, max 3.0
                BigDecimal diemCongChungChi = toBigDecimal(request.getDiemCongChungChi());
                BigDecimal diemCongUuTien = toBigDecimal(request.getDiemCongUuTien());
                BigDecimal diemCong = CongThucUtil.tinhDiemCongToiDa(diemCongChungChi, diemCongUuTien);

                // Tính Mức Điểm Ưu Tiên (MDUT) từ khuVuc + doiTuong
                BigDecimal mucDiemUuTien = CongThucUtil.tinhMucDiemUuTien(
                                request.getKhuVuc(),
                                request.getDoiTuong());

                // Tính Điểm Ưu Tiên (ĐƯT)
                BigDecimal diemUuTien = CongThucUtil.tinhDiemUuTien(diemToHop, diemCong, mucDiemUuTien);

                // Tính điểm xét tuyển cuối cùng: ĐXT = DTHXT + DC + DUT
                BigDecimal diemXetTuyen = CongThucUtil.tinhDiemXetTuyenCuoiCung(
                                diemToHop,
                                diemCong,
                                diemUuTien);

                boolean datNguong = CongThucUtil.datNguong(diemXetTuyen, NGUONG_TUYEN_SINH);

                return DiemXetTuyenResponse.builder()
                                .phuongThuc("VSAT")
                                .maToHop(request.getMaToHop())
                                .diemToHop(diemToHop)
                                .diemQuyDoi(diemToHop)
                                .diemCongChungChi(diemCongChungChi)
                                .diemCongUuTien(diemCongUuTien)
                                .diemCong(diemCong)
                                .doiTuong(request.getDoiTuong())
                                .khuVuc(request.getKhuVuc())
                                .mucDiemUuTien(mucDiemUuTien)
                                .diemUuTien(diemUuTien)
                                .diemXetTuyen(diemXetTuyen)
                                .chiTietQuyDoi(chiTietQuyDoi)
                                .thongBao("VSAT: TB 6 môn = " + diemTrungBinh + " (150) → "
                                                + diemTrungBinhQuyDoi + " (10) → " + diemToHop + " (30)")
                                .datNguong(datNguong)
                                .build();
        }

        /**
         * API 3: Tính điểm theo phương thức DGNL
         * Input: Điểm ĐGNL (thang 1200) + Điểm cộng + Điểm ưu tiên
         * Output: Điểm quy đổi về thang 30
         */
        public DiemXetTuyenResponse tinhDiemDGNL(DiemXetTuyenRequest request) {
                BigDecimal diemDgnl = toBigDecimal(request.getDiemTong());

                // Quy đổi từ thang 1200 → thang 30
                BigDecimal diemQuyDoi = CongThucUtil.quyDoiDgnl1200Sang30(diemDgnl);

                // Điểm tổ hợp = điểm quy đổi
                BigDecimal diemToHop = diemQuyDoi;

                // Tính tổng Điểm cộng (DC) từ diemCongChungChi + diemCongUuTien, max 3.0
                BigDecimal diemCongChungChi = toBigDecimal(request.getDiemCongChungChi());
                BigDecimal diemCongUuTien = toBigDecimal(request.getDiemCongUuTien());
                BigDecimal diemCong = CongThucUtil.tinhDiemCongToiDa(diemCongChungChi, diemCongUuTien);

                // Tính Mức Điểm Ưu Tiên (MDUT) từ khuVuc + doiTuong
                BigDecimal mucDiemUuTien = CongThucUtil.tinhMucDiemUuTien(
                                request.getKhuVuc(),
                                request.getDoiTuong());

                // Tính Điểm Ưu Tiên (ĐƯT)
                BigDecimal diemUuTien = CongThucUtil.tinhDiemUuTien(diemToHop, diemCong, mucDiemUuTien);

                // Tính điểm xét tuyển cuối cùng: ĐXT = DTHXT + DC + DUT
                BigDecimal diemXetTuyen = CongThucUtil.tinhDiemXetTuyenCuoiCung(
                                diemQuyDoi,
                                diemCong,
                                diemUuTien);

                boolean datNguong = CongThucUtil.datNguong(diemXetTuyen, NGUONG_TUYEN_SINH);

                return DiemXetTuyenResponse.builder()
                                .phuongThuc("DGNL")
                                .maToHop(request.getMaToHop())
                                .diemToHop(diemToHop)
                                .diemQuyDoi(diemQuyDoi)
                                .diemCongChungChi(diemCongChungChi)
                                .diemCongUuTien(diemCongUuTien)
                                .diemCong(diemCong)
                                .doiTuong(request.getDoiTuong())
                                .khuVuc(request.getKhuVuc())
                                .mucDiemUuTien(mucDiemUuTien)
                                .diemUuTien(diemUuTien)
                                .diemXetTuyen(diemXetTuyen)
                                .thongBao("DGNL: " + diemDgnl + " (1200) → " + diemQuyDoi + " (30) + DC:" + diemCong
                                                + " + DUT:" + diemUuTien)
                                .datNguong(datNguong)
                                .build();
        }

        /**
         * Tính điểm trung bình 6 môn
         */
        private BigDecimal tinhDiemTrungBinh6Mon(DiemXetTuyenRequest request) {
                BigDecimal tong = toBigDecimal(request.getToan())
                                .add(toBigDecimal(request.getLi()))
                                .add(toBigDecimal(request.getHoa()))
                                .add(toBigDecimal(request.getSinh()))
                                .add(toBigDecimal(request.getSu()))
                                .add(toBigDecimal(request.getDi()));
                return tong.divide(new BigDecimal("6"), 4, RoundingMode.HALF_UP);
        }

        /**
         * Validate 6 môn bắt buộc
         */
        private boolean validate6Mon(DiemXetTuyenRequest request) {
                return request.getToan() != null && request.getLi() != null
                                && request.getHoa() != null && request.getSinh() != null
                                && request.getSu() != null && request.getDi() != null;
        }

        // Trả về điểm tương ứng cho mã môn (TO, VA, LI, HO, SI, SU, DI, N1)
        private BigDecimal getScoreForCode(DiemXetTuyenRequest request, String code) {
                if (code == null)
                        return BigDecimal.ZERO;
                switch (code.trim().toUpperCase()) {
                        case "TO":
                                return toBigDecimal(request.getToan());
                        case "VA":
                                return toBigDecimal(request.getVa());
                        case "LI":
                                return toBigDecimal(request.getLi());
                        case "HO":
                                return toBigDecimal(request.getHoa());
                        case "SI":
                                return toBigDecimal(request.getSinh());
                        case "SU":
                                return toBigDecimal(request.getSu());
                        case "DI":
                                return toBigDecimal(request.getDi());
                        case "N1":
                                return toBigDecimal(request.getN1());
                        default:
                                return BigDecimal.ZERO;
                }
        }

        private DiemXetTuyenResponse buildErrorResponse(String phuongThuc, String message) {
                return DiemXetTuyenResponse.builder()
                                .phuongThuc(phuongThuc)
                                .maToHop(null)
                                .diemToHop(BigDecimal.ZERO)
                                .diemQuyDoi(BigDecimal.ZERO)
                                .diemCong(BigDecimal.ZERO)
                                .diemUuTien(BigDecimal.ZERO)
                                .diemXetTuyen(BigDecimal.ZERO)
                                .thongBao(message)
                                .datNguong(false)
                                .error(true)
                                .errorMessage(message)
                                .build();
        }

        private BigDecimal toBigDecimal(Double value) {
                return value == null ? BigDecimal.ZERO : BigDecimal.valueOf(value);
        }
}