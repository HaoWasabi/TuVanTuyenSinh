package com.tuyensinh.view;

import com.tuyensinh.model.Nganh;
import com.tuyensinh.service.NganhService;
import com.tuyensinh.service.NguyenVongService;
import com.tuyensinh.service.ThiSinhService;
import com.tuyensinh.service.TohopMonthiService;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class DashboardPanel extends JPanel {
    private final ThiSinhService thiSinhService = new ThiSinhService();
    private final NganhService nganhService = new NganhService();
    private final TohopMonthiService tohopMonthiService = new TohopMonthiService();
    private final NguyenVongService nguyenVongService = new NguyenVongService();

    public DashboardPanel() {
        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(UIStyles.BG_APP);

        JLabel title = new JLabel("Dashboard Xét Tuyển Sinh 2026");
        title.setFont(UIStyles.FONT_TITLE);
        title.setForeground(UIStyles.TEXT_DARK);
        add(title, BorderLayout.NORTH);

        JPanel statsContainer = new JPanel(new GridLayout(1, 4, 12, 12));
        statsContainer.setOpaque(false);
        statsContainer.add(createStatCard("Thí sinh", formatNumber(thiSinhService.getAll().size()), UIStyles.PRIMARY));
        statsContainer.add(createStatCard("Ngành mở", formatNumber(nganhService.getAll().size()), UIStyles.SUCCESS));
        statsContainer.add(createStatCard("Tổ hợp", formatNumber(tohopMonthiService.getAll().size()), UIStyles.INFO));
        statsContainer.add(createStatCard("Nguyện vọng", formatNumber(nguyenVongService.getAll().size()), UIStyles.WARNING));

        String[] columns = {"Mã ngành", "Tên ngành", "Chỉ tiêu", "Đăng ký", "Tỉ lệ"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable topMajorsTable = new JTable(tableModel);
        topMajorsTable.setRowHeight(32);
        topMajorsTable.getTableHeader().setFont(UIStyles.FONT_LABEL);
        topMajorsTable.getTableHeader().setBackground(new Color(247, 249, 251));
        topMajorsTable.setFont(UIStyles.FONT_BODY);

        loadTopMajors(tableModel);

        JPanel topMajorsCard = createTableCard("Top Ngành Theo Số Lượng Đăng Ký", topMajorsTable);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BorderLayout(0, 14));
        center.add(statsContainer, BorderLayout.NORTH);
        center.add(topMajorsCard, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    private void loadTopMajors(DefaultTableModel tableModel) {
        List<Nganh> nganhList = nganhService.getAll();
        Map<String, Long> nvCountMap = nganhService.getNguyenVongCountByMaNganh();

        nganhList.sort((a, b) -> {
            Long countA = nvCountMap.getOrDefault(a.getManganh(), 0L);
            Long countB = nvCountMap.getOrDefault(b.getManganh(), 0L);
            return Long.compare(countB, countA);
        });

        int limit = Math.min(nganhList.size(), 10);
        for (int i = 0; i < limit; i++) {
            Nganh nganh = nganhList.get(i);
            String maNganh = nganh.getManganh() != null ? nganh.getManganh() : "";
            String tenNganh = nganh.getTennganh() != null ? nganh.getTennganh() : "";
            Integer chiTieu = nganh.getNChitieu() != null ? nganh.getNChitieu() : 0;
            Long dangKy = nvCountMap.getOrDefault(maNganh, 0L);
            int dkInt = dangKy.intValue();
            String tiLe = chiTieu > 0 ? String.format("%.2f:1", (double) dkInt / chiTieu) : "0.00:1";

            tableModel.addRow(new Object[]{maNganh, tenNganh, chiTieu, dkInt, tiLe});
        }
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UIStyles.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));
        card.setPreferredSize(new Dimension(250, 120));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIStyles.FONT_SMALL);
        titleLabel.setForeground(UIStyles.TEXT_MUTED);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 28));
        valueLabel.setForeground(color);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(titleLabel);
        content.add(new javax.swing.Box.Filler(
                new Dimension(0, 8), new Dimension(0, 8), new Dimension(0, 8)));
        content.add(valueLabel);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel createTableCard(String title, JTable table) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(UIStyles.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel cardTitle = new JLabel(title);
        cardTitle.setFont(UIStyles.FONT_SUBTITLE);
        cardTitle.setForeground(UIStyles.TEXT_DARK);
        card.add(cardTitle, BorderLayout.NORTH);

        card.add(new JScrollPane(table), BorderLayout.CENTER);

        return card;
    }

    private String formatNumber(int value) {
        return String.format("%,d", value);
    }
}
