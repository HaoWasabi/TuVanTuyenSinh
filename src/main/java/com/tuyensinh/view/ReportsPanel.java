package com.tuyensinh.view;

import com.tuyensinh.model.DiemCong;
import com.tuyensinh.model.ThiSinh;
import com.tuyensinh.service.DiemCongService;
import com.tuyensinh.service.DiemThiService;
import com.tuyensinh.service.ThiSinhService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ReportsPanel extends JPanel {

    private static final String TYPE_THPT = "Điểm THPT";
    private static final String TYPE_DGNL = "Điểm ĐGNL";
    private static final String TYPE_VSAT = "Điểm V-SAT";
    private static final String TYPE_DIEM_CONG = "Điểm cộng xét tuyển";
    private static final String TYPE_THI_SINH_KV = "Thí sinh theo Khu vực";
    private static final String TYPE_THI_SINH_DT = "Thí sinh theo Đối tượng";

    private final DiemThiService diemThiService = new DiemThiService();
    private final DiemCongService diemCongService = new DiemCongService();
    private final ThiSinhService thiSinhService = new ThiSinhService();

    private final JComboBox<String> loaiDiemCombo;
    private final JComboBox<String> monCombo;
    private final JLabel statusLabel;

    private final JLabel avgValueLabel;
    private final JLabel maxValueLabel;
    private final JLabel minValueLabel;
    private final JLabel countValueLabel;

    private final JPanel avgCard;
    private final JPanel maxCard;
    private final JPanel minCard;
    private final JPanel countCard;
    private final JPanel reportsPanel;

    private final DefaultTableModel distributionModel;
    private final BarChartPanel chartPanel;
    private final JLabel tableTitle;
    private final JLabel chartTitle;

    private final Map<String, String> monToFieldMap;

    public ReportsPanel() {
        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(UIStyles.BG_APP);

        monToFieldMap = buildMonToFieldMap();

        JLabel title = new JLabel("Báo cáo & Thống kê");
        title.setFont(UIStyles.FONT_TITLE);
        title.setForeground(UIStyles.TEXT_DARK);
        add(title, BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);

        JLabel loaiDiemLabel = new JLabel("Loại thống kê");
        loaiDiemLabel.setFont(UIStyles.FONT_LABEL);
        loaiDiemLabel.setForeground(UIStyles.TEXT_DARK);

        loaiDiemCombo = new JComboBox<>(new String[]{TYPE_THPT, TYPE_DGNL, TYPE_VSAT, TYPE_DIEM_CONG, TYPE_THI_SINH_KV, TYPE_THI_SINH_DT});
        loaiDiemCombo.setFont(UIStyles.FONT_BODY);
        loaiDiemCombo.setPreferredSize(new Dimension(190, 32));

        JLabel monLabel = new JLabel("Môn");
        monLabel.setFont(UIStyles.FONT_LABEL);
        monLabel.setForeground(UIStyles.TEXT_DARK);

        monCombo = new JComboBox<>(monToFieldMap.keySet().toArray(new String[0]));
        monCombo.setFont(UIStyles.FONT_BODY);
        monCombo.setPreferredSize(new Dimension(170, 32));

        JButton thongKeBtn = createActionButton("Thống kê", UIStyles.PRIMARY);
        thongKeBtn.addActionListener(e -> runStatistics());

        toolbar.add(loaiDiemLabel);
        toolbar.add(loaiDiemCombo);
        toolbar.add(monLabel);
        toolbar.add(monCombo);
        toolbar.add(thongKeBtn);
        toolbar.add(Box.createHorizontalStrut(12));

        statusLabel = new JLabel("Chọn điều kiện và nhấn Thống kê.");
        statusLabel.setFont(UIStyles.FONT_BODY);
        statusLabel.setForeground(UIStyles.TEXT_MUTED);
        toolbar.add(statusLabel);

        JPanel north = new JPanel(new BorderLayout(0, 10));
        north.setOpaque(false);
        north.add(title, BorderLayout.NORTH);
        north.add(toolbar, BorderLayout.CENTER);
        add(north, BorderLayout.NORTH);

        reportsPanel = new JPanel(new java.awt.GridLayout(2, 2, 12, 12));
        reportsPanel.setOpaque(false);
        avgValueLabel = new JLabel("-");
        maxValueLabel = new JLabel("-");
        minValueLabel = new JLabel("-");
        countValueLabel = new JLabel("-");

        reportsPanel.add(avgCard = createReportCard("Điểm trung bình", avgValueLabel, UIStyles.PRIMARY));
        reportsPanel.add(maxCard = createReportCard("Điểm cao nhất", maxValueLabel, UIStyles.SUCCESS));
        reportsPanel.add(minCard = createReportCard("Điểm thấp nhất", minValueLabel, UIStyles.WARNING));
        reportsPanel.add(countCard = createReportCard("Số lượng thí sinh", countValueLabel, UIStyles.INFO));

        String[] cols = {"Khoảng điểm", "Số lượng thí sinh"};
        distributionModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(distributionModel);
        table.setRowHeight(32);
        table.getTableHeader().setFont(UIStyles.FONT_LABEL);
        table.getTableHeader().setBackground(new Color(247, 249, 251));
        table.setFont(UIStyles.FONT_BODY);

        JPanel tableCard = new JPanel(new BorderLayout(0, 12));
        tableCard.setBackground(UIStyles.BG_CARD);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));

        tableTitle = new JLabel("Phân bố số lượng theo khoảng điểm");
        tableTitle.setFont(UIStyles.FONT_SUBTITLE);
        tableTitle.setForeground(UIStyles.TEXT_DARK);
        tableCard.add(tableTitle, BorderLayout.NORTH);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        chartPanel = new BarChartPanel();
        chartPanel.setPreferredSize(new Dimension(500, 280));

        JPanel chartCard = new JPanel(new BorderLayout(0, 12));
        chartCard.setBackground(UIStyles.BG_CARD);
        chartCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));

        chartTitle = new JLabel("Biểu đồ cột số lượng thí sinh/điểm");
        chartTitle.setFont(UIStyles.FONT_SUBTITLE);
        chartTitle.setForeground(UIStyles.TEXT_DARK);
        chartCard.add(chartTitle, BorderLayout.NORTH);
        chartCard.add(chartPanel, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new java.awt.GridLayout(1, 2, 12, 0));
        bottom.setOpaque(false);
        bottom.add(tableCard);
        bottom.add(chartCard);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BorderLayout(0, 12));
        center.add(reportsPanel, BorderLayout.NORTH);
        center.add(bottom, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        loaiDiemCombo.addActionListener(e -> {
            updateMonStateByLoaiDiem();
            runStatistics();
        });
        monCombo.addActionListener(e -> runStatistics());

        updateMonStateByLoaiDiem();
        runStatistics();
    }

    private JPanel createReportCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UIStyles.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIStyles.FONT_BODY);
        titleLabel.setForeground(UIStyles.TEXT_MUTED);

        valueLabel.setText("-");
        valueLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 22));
        valueLabel.setForeground(color);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(titleLabel, BorderLayout.NORTH);
        top.add(valueLabel, BorderLayout.CENTER);

        card.add(top, BorderLayout.CENTER);
        return card;
    }

    private JButton createActionButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(UIStyles.FONT_LABEL);
        btn.setBorder(new EmptyBorder(7, 14, 7, 14));
        return btn;
    }

    private void updateMonStateByLoaiDiem() {
        String loaiDiem = Objects.toString(loaiDiemCombo.getSelectedItem(), "");
        boolean isDiemThi = TYPE_THPT.equals(loaiDiem) || TYPE_VSAT.equals(loaiDiem);
        monCombo.setEnabled(isDiemThi);
        if (TYPE_DGNL.equals(loaiDiem)) {
            monCombo.setSelectedItem("NL1");
        } else if (!isDiemThi) {
            monCombo.setSelectedIndex(0);
        }
    }

    private void runStatistics() {
        String loaiDiem = Objects.toString(loaiDiemCombo.getSelectedItem(), "");
        try {
            if (TYPE_THPT.equals(loaiDiem)) {
                runDiemThiStatistics("THPT", "THPT", new BigDecimal("10"), new String[]{"0-2", "2-4", "4-6", "6-8", "8-10"}, new BigDecimal[]{new BigDecimal("2"), new BigDecimal("4"), new BigDecimal("6"), new BigDecimal("8")});
            } else if (TYPE_DGNL.equals(loaiDiem)) {
                runDiemThiStatistics("ĐGNL", "DGNL", new BigDecimal("1200"), new String[]{"0-400", "400-600", "600-800", "800-1000", "1000-1200"}, new BigDecimal[]{new BigDecimal("400"), new BigDecimal("600"), new BigDecimal("800"), new BigDecimal("1000")});
            } else if (TYPE_VSAT.equals(loaiDiem)) {
                runDiemThiStatistics("V-SAT", "VSAT", new BigDecimal("150"), new String[]{"0-30", "30-60", "60-90", "90-120", "120-150"}, new BigDecimal[]{new BigDecimal("30"), new BigDecimal("60"), new BigDecimal("90"), new BigDecimal("120")});
            } else if (TYPE_DIEM_CONG.equals(loaiDiem)) {
                runDiemCongStatistics();
            } else if (TYPE_THI_SINH_KV.equals(loaiDiem)) {
                runThiSinhStatistics(true);
            } else if (TYPE_THI_SINH_DT.equals(loaiDiem)) {
                runThiSinhStatistics(false);
            }
        } catch (Exception ex) {
            resetView();
            statusLabel.setText("Lỗi thống kê: " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Không thể thực hiện thống kê. Chi tiết: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void runThiSinhStatistics(boolean isKhuVuc) {
        reportsPanel.removeAll();
        reportsPanel.setLayout(new java.awt.GridLayout(1, 1, 12, 12));
        reportsPanel.add(countCard);
        reportsPanel.revalidate();
        reportsPanel.repaint();

        List<ThiSinh> dsThiSinh = thiSinhService.getAll();
        if (dsThiSinh == null || dsThiSinh.isEmpty()) {
            showInsufficientData("Không có dữ liệu thí sinh.");
            return;
        }

        Map<String, Long> bins = new LinkedHashMap<>();
        for (ThiSinh ts : dsThiSinh) {
            String key = isKhuVuc ? ts.getKhuVuc() : ts.getDoiTuong();
            if (key == null || key.isBlank()) {
                key = "Chưa rõ";
            }
            bins.put(key, bins.getOrDefault(key, 0L) + 1);
        }

        countValueLabel.setText(String.valueOf(dsThiSinh.size()));

        String[] cols = {isKhuVuc ? "Mã Khu vực" : "Mã Đối tượng", isKhuVuc ? "Tên Khu vực" : "Tên Đối tượng", "Số lượng thí sinh"};
        distributionModel.setColumnIdentifiers(cols);
        tableTitle.setText("Phân bố số lượng theo " + (isKhuVuc ? "khu vực" : "đối tượng"));
        chartTitle.setText("Biểu đồ cột số lượng thí sinh / " + (isKhuVuc ? "khu vực" : "đối tượng"));

        distributionModel.setRowCount(0);
        for (Map.Entry<String, Long> entry : bins.entrySet()) {
            String ma = entry.getKey();
            String ten = isKhuVuc ? getTenKhuVuc(ma) : getTenDoiTuong(ma);
            distributionModel.addRow(new Object[]{ma, ten, entry.getValue()});
        }
        chartPanel.setData(bins);

        statusLabel.setText("Đã thống kê thành công cho Thí sinh theo " + (isKhuVuc ? "Khu vực" : "Đối tượng") + ".");
    }

    private String getTenKhuVuc(String ma) {
        if (ma == null) return "Chưa rõ";
        return switch (ma.trim().toUpperCase()) {
            case "KV1", "1" -> "Khu vực 1";
            case "KV2", "2" -> "Khu vực 2";
            case "KV2-NT", "2NT" -> "Khu vực 2 - Nông thôn";
            case "KV3", "3" -> "Khu vực 3";
            default -> "Chưa rõ";
        };
    }

    private String getTenDoiTuong(String ma) {
        if (ma == null) return "Chưa rõ";
        return switch (ma.trim()) {
            case "01", "1" -> "Dân tộc thiểu số (KV1)";
            case "02", "2" -> "Công nhân ưu tú";
            case "03", "3" -> "Thương binh, bệnh binh";
            case "04", "4" -> "Con liệt sĩ, con TB nặng";
            case "05", "5" -> "Thanh niên xung phong";
            case "06", "6" -> "Dân tộc thiểu số (Ngoài KV1)";
            case "07", "7" -> "Người khuyết tật nặng";
            default -> "Chưa rõ";
        };
    }

    private void runDiemThiStatistics(String loaiDiemDisplay, String phuongThuc, BigDecimal maxScore, String[] binNames, BigDecimal[] binLimits) {
        reportsPanel.removeAll();
        reportsPanel.setLayout(new java.awt.GridLayout(2, 2, 12, 12));
        reportsPanel.add(avgCard);
        reportsPanel.add(maxCard);
        reportsPanel.add(minCard);
        reportsPanel.add(countCard);
        reportsPanel.revalidate();
        reportsPanel.repaint();

        distributionModel.setColumnIdentifiers(new String[]{"Khoảng điểm", "Số lượng thí sinh"});
        tableTitle.setText("Phân bố số lượng theo khoảng điểm");
        chartTitle.setText("Biểu đồ cột số lượng thí sinh/điểm");

        String monDisplay = Objects.toString(monCombo.getSelectedItem(), "");
        String monField = monToFieldMap.get(monDisplay);
        if (monField == null || monField.isBlank()) {
            showInsufficientData("Vui lòng chọn môn để thống kê.");
            return;
        }

        List<BigDecimal> points = diemThiService.layDiemTheoMonVaPhuongThuc(monField, phuongThuc);
        if (points == null || points.isEmpty()) {
            showInsufficientData("Không có dữ liệu điểm " + loaiDiemDisplay + " cho môn " + monDisplay + ".");
            return;
        }

        BigDecimal tong = BigDecimal.ZERO;
        BigDecimal min = points.get(0);
        BigDecimal max = points.get(0);

        for (BigDecimal p : points) {
            if (p != null) {
                tong = tong.add(p);
                if (p.compareTo(min) < 0) min = p;
                if (p.compareTo(max) > 0) max = p;
            }
        }

        long count = points.size();
        BigDecimal avg = count > 0 ? tong.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        Map<String, Long> bins = new LinkedHashMap<>();
        for (String b : binNames) {
            bins.put(b, 0L);
        }

        for (BigDecimal p : points) {
            if (p != null) {
                boolean added = false;
                for (int i = 0; i < binLimits.length; i++) {
                    if (p.compareTo(binLimits[i]) < 0) {
                        bins.put(binNames[i], bins.get(binNames[i]) + 1);
                        added = true;
                        break;
                    }
                }
                if (!added) {
                    bins.put(binNames[binNames.length - 1], bins.get(binNames[binNames.length - 1]) + 1);
                }
            }
        }

        updateSummary(avg, max, min, count);
        updateDistributionView(bins);
        statusLabel.setText("Đã thống kê thành công cho loại điểm " + loaiDiemDisplay + " - môn " + monDisplay + ".");
    }

    private void runDiemCongStatistics() {
        reportsPanel.removeAll();
        reportsPanel.setLayout(new java.awt.GridLayout(2, 2, 12, 12));
        reportsPanel.add(avgCard);
        reportsPanel.add(maxCard);
        reportsPanel.add(minCard);
        reportsPanel.add(countCard);
        reportsPanel.revalidate();
        reportsPanel.repaint();

        distributionModel.setColumnIdentifiers(new String[]{"Khoảng điểm", "Số lượng thí sinh"});
        tableTitle.setText("Phân bố số lượng theo khoảng điểm");
        chartTitle.setText("Biểu đồ cột số lượng thí sinh/điểm");

        List<DiemCong> dsDiemCong = diemCongService.getAll();
        List<BigDecimal> diemTongList = new ArrayList<>();

        for (DiemCong diemCong : dsDiemCong) {
            if (diemCong != null && diemCong.getDiemTong() != null) {
                diemTongList.add(diemCong.getDiemTong());
            }
        }

        if (diemTongList.isEmpty()) {
            showInsufficientData("Không đủ dữ kiện để tiến hành thống kê điểm cộng xét tuyển.");
            return;
        }

        BigDecimal tong = BigDecimal.ZERO;
        BigDecimal min = diemTongList.get(0);
        BigDecimal max = diemTongList.get(0);
        for (BigDecimal diem : diemTongList) {
            tong = tong.add(diem);
            if (diem.compareTo(min) < 0) {
                min = diem;
            }
            if (diem.compareTo(max) > 0) {
                max = diem;
            }
        }

        long count = diemTongList.size();
        BigDecimal avg = tong.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);

        Map<String, Long> bins = new LinkedHashMap<>();
        bins.put("<18", 0L);
        bins.put("18-21", 0L);
        bins.put("21-24", 0L);
        bins.put("24-27", 0L);
        bins.put(">=27", 0L);

        for (BigDecimal diem : diemTongList) {
            if (diem.compareTo(BigDecimal.valueOf(18)) < 0) {
                bins.put("<18", bins.get("<18") + 1);
            } else if (diem.compareTo(BigDecimal.valueOf(21)) < 0) {
                bins.put("18-21", bins.get("18-21") + 1);
            } else if (diem.compareTo(BigDecimal.valueOf(24)) < 0) {
                bins.put("21-24", bins.get("21-24") + 1);
            } else if (diem.compareTo(BigDecimal.valueOf(27)) < 0) {
                bins.put("24-27", bins.get("24-27") + 1);
            } else {
                bins.put(">=27", bins.get(">=27") + 1);
            }
        }

        updateSummary(avg, max, min, count);
        updateDistributionView(bins);
        statusLabel.setText("Đã thống kê thành công cho loại điểm cộng xét tuyển.");
    }

    private void showInsufficientData(String message) {
        resetView();
        statusLabel.setText(message);
    }

    private void resetView() {
        avgValueLabel.setText("-");
        maxValueLabel.setText("-");
        minValueLabel.setText("-");
        countValueLabel.setText("0");

        distributionModel.setRowCount(0);
        chartPanel.setData(Map.of());
    }

    private void updateSummary(BigDecimal avg, BigDecimal max, BigDecimal min, long count) {
        avgValueLabel.setText(formatDecimal(avg));
        maxValueLabel.setText(formatDecimal(max));
        minValueLabel.setText(formatDecimal(min));
        countValueLabel.setText(String.valueOf(count));
    }

    private void updateDistributionView(Map<String, Long> bins) {
        distributionModel.setRowCount(0);
        for (Map.Entry<String, Long> entry : bins.entrySet()) {
            distributionModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }
        chartPanel.setData(bins);
    }

    private boolean isStatDataValid(Object[] data) {
        if (data == null || data.length < 4) {
            return false;
        }
        if (!(data[3] instanceof Number)) {
            return false;
        }
        return ((Number) data[3]).longValue() > 0 && data[0] != null && data[1] != null && data[2] != null;
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal decimal) {
            return decimal.setScale(2, RoundingMode.HALF_UP);
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue()).setScale(2, RoundingMode.HALF_UP);
        }
        return new BigDecimal(value.toString()).setScale(2, RoundingMode.HALF_UP);
    }

    private String formatDecimal(BigDecimal value) {
        return value == null ? "-" : value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private Map<String, String> buildMonToFieldMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("Toán", "toan");
        map.put("Vật lý", "vatLi");
        map.put("Hóa học", "hoaHoc");
        map.put("Sinh học", "sinhHoc");
        map.put("Lịch sử", "lichSu");
        map.put("Địa lý", "diaLi");
        map.put("Ngữ văn", "nguVan");
        map.put("Tin học", "tinHoc");
        map.put("KTPL", "ktpl");
        map.put("Ngoại ngữ 1 (Thi)", "n1Thi");
        map.put("Ngoại ngữ 1 (CC)", "n1Cc");
        map.put("CNCN", "cncn");
        map.put("CNNN", "cnnn");
        map.put("NL1", "nl1");
        map.put("NK1", "nk1");
        map.put("NK2", "nk2");
        return map;
    }

    private static class BarChartPanel extends JPanel {
        private Map<String, Long> data = Map.of();

        BarChartPanel() {
            setBackground(Color.WHITE);
        }

        void setData(Map<String, Long> data) {
            this.data = new LinkedHashMap<>(data);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) {
                drawEmptyMessage(g);
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int leftPadding = 52;
            int rightPadding = 24;
            int topPadding = 18;
            int bottomPadding = 42;

            int chartWidth = width - leftPadding - rightPadding;
            int chartHeight = height - topPadding - bottomPadding;

            long maxValue = 1;
            for (Long value : data.values()) {
                if (value != null && value > maxValue) {
                    maxValue = value;
                }
            }

            g2.setColor(new Color(240, 242, 246));
            g2.fillRect(leftPadding, topPadding, chartWidth, chartHeight);

            g2.setColor(UIStyles.TEXT_MUTED);
            g2.drawLine(leftPadding, topPadding + chartHeight, leftPadding + chartWidth, topPadding + chartHeight);
            g2.drawLine(leftPadding, topPadding, leftPadding, topPadding + chartHeight);

            int barCount = data.size();
            int gap = Math.max(8, chartWidth / Math.max(1, barCount * 5));
            int barWidth = Math.max(18, (chartWidth - (barCount + 1) * gap) / Math.max(1, barCount));

            int x = leftPadding + gap;
            FontMetrics fm = g2.getFontMetrics();
            for (Map.Entry<String, Long> entry : data.entrySet()) {
                long value = entry.getValue() == null ? 0 : entry.getValue();
                int barHeight = (int) ((double) value / maxValue * (chartHeight - 8));
                int y = topPadding + chartHeight - barHeight;

                g2.setColor(UIStyles.PRIMARY);
                g2.fillRoundRect(x, y, barWidth, barHeight, 8, 8);

                g2.setColor(UIStyles.TEXT_DARK);
                String valueText = String.valueOf(value);
                int valueWidth = fm.stringWidth(valueText);
                g2.drawString(valueText, x + (barWidth - valueWidth) / 2, y - 6);

                g2.setColor(UIStyles.TEXT_MUTED);
                String label = entry.getKey();
                int labelWidth = fm.stringWidth(label);
                g2.drawString(label, x + (barWidth - labelWidth) / 2, topPadding + chartHeight + 18);

                x += barWidth + gap;
            }

            g2.dispose();
        }

        private void drawEmptyMessage(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(UIStyles.TEXT_MUTED);
            g2.setFont(UIStyles.FONT_BODY);
            String text = "Không có dữ liệu biểu đồ";
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = getHeight() / 2;
            g2.drawString(text, x, y);
            g2.dispose();
        }
    }
}