package com.tuyensinh.controllerWeb;

import com.tuyensinh.utilWeb.CongThucUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.math.RoundingMode;

@Controller
@RequestMapping({ "/quy-doi-to-hop", "/quy-doi-tohop" })
public class QuyDoiToHopController {

    // Simple data holder for template access
    public static class MatrixRow {
        public String rowKey;
        public List<String> values;

        public MatrixRow(String rowKey, List<String> values) {
            this.rowKey = rowKey;
            this.values = values;
        }

        public String getRowKey() {
            return rowKey;
        }

        public List<String> getValues() {
            return values;
        }

        public String getValue(int index) {
            return (index >= 0 && index < values.size()) ? values.get(index) : "-";
        }
    }

    @GetMapping
    public String page(Model model) {
        Map<String, Map<String, BigDecimal>> matrix = CongThucUtil.getChenhLechMap();
        List<String> toHopList = CongThucUtil.getToHopList();
        model.addAttribute("toHopList", toHopList);

        // Build matrix rows as lists for easier Thymeleaf access
        List<MatrixRow> matrixRows = new java.util.ArrayList<>();
        java.math.RoundingMode rm = java.math.RoundingMode.HALF_UP;

        for (String rowKey : toHopList) {
            // Bỏ hàng D07 theo yêu cầu (ma trận gốc chỉ có cột D07, không có hàng D07)
            if ("D07".equalsIgnoreCase(rowKey)) {
                continue;
            }
            List<String> rowValues = new java.util.ArrayList<>();
            Map<String, BigDecimal> sub = matrix.get(rowKey);

            for (String colKey : toHopList) {
                BigDecimal val = null;
                // Same combination -> 0
                if (rowKey.equals(colKey)) {
                    val = BigDecimal.ZERO;
                } else if (sub != null && sub.containsKey(colKey)) {
                    val = sub.get(colKey);
                }

                if (val != null) {
                    val = val.setScale(2, rm);
                    String s = (val.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "") + val.toString();
                    rowValues.add(s);
                } else {
                    rowValues.add("N/A");
                }
            }
            matrixRows.add(new MatrixRow(rowKey, rowValues));
        }

        model.addAttribute("matrixRows", matrixRows);

        return "quy-doi-to-hop";
    }

    @GetMapping(path = "/api/convert", params = { "score", "from", "to" }, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object convert(@RequestParam("score") String scoreStr,
            @RequestParam("from") String from,
            @RequestParam("to") String to) {
        try {
            BigDecimal score = new BigDecimal(scoreStr.trim());
            String row = from.trim().toUpperCase();
            String col = to.trim().toUpperCase();

            // Kiểm tra nếu tổ hợp xuất phát là D07 (không hợp lệ vì không có hàng D07)
            if ("D07".equals(row)) {
                return Map.of("error", "Tổ hợp D07 không hỗ trợ làm tổ hợp gốc để quy đổi.");
            }

            // According to rules: converted = score - diff(row, col)
            // Use existing utility to convert score of 'col' (source) to target 'row'
            BigDecimal converted = CongThucUtil.quyDoiVeToHopGoc(score, col, row);
            return Map.of("converted", converted.setScale(2, java.math.RoundingMode.HALF_UP).toString());
        } catch (Exception e) {
            return Map.of("error", "invalid input");
        }
    }

    @GetMapping(path = "/api/convert", params = { "score", "from" }, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object convertAll(@RequestParam("score") String scoreStr, @RequestParam("from") String from) {
        try {
            if (scoreStr == null || from == null || from.isBlank()) {
                return Map.of("error", "Dữ liệu đầu vào không hợp lệ");
            }

            BigDecimal score = new BigDecimal(scoreStr.trim());
            String sourceToHop = from.trim().toUpperCase(); // Đây là tổ hợp gốc người dùng đang có điểm

            // Kiểm tra nếu tổ hợp xuất phát là D07
            if ("D07".equals(sourceToHop)) {
                return Map.of("error", "Tổ hợp D07 không hỗ trợ làm tổ hợp gốc để quy đổi.");
            }

            List<String> toHopList = CongThucUtil.getToHopList();
            Map<String, String> result = new LinkedHashMap<>();

            for (String targetToHop : toHopList) {
                String target = targetToHop.trim().toUpperCase();

                // ĐÃ SỬA: Gọi hàm đúng chuẩn: (Điểm gốc, Tổ hợp gốc, Tổ hợp muốn chuyển đổi
                // tới)
                BigDecimal converted = CongThucUtil.quyDoiVeToHopGoc(score, sourceToHop, target);

                // ĐÃ SỬA: Kiểm tra null an toàn trước khi setScale tránh gây crash luồng kết
                // nối
                if (converted != null) {
                    BigDecimal finalizedScore = converted.setScale(2, RoundingMode.HALF_UP);
                    result.put(targetToHop, finalizedScore.toString());
                } else {
                    result.put(targetToHop, "N/A");
                }
            }
            return result;
        } catch (Throwable t) {
            // Bắt Throwable để ghi nhận tất cả các lỗi nghiêm trọng (nếu có) vào Console hệ
            // thống
            t.printStackTrace();
            return Map.of("error", "Lỗi hệ thống trong quá trình tính toán ma trận quy đổi.");
        }
    }
}
