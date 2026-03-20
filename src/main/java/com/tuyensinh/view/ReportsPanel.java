package com.tuyensinh.view;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;

public class ReportsPanel extends JPanel {
    public ReportsPanel() {
        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(UIStyles.BG_APP);

        // Title
        JLabel title = new JLabel("Báo cáo & Thống kê");
        title.setFont(UIStyles.FONT_TITLE);
        title.setForeground(UIStyles.TEXT_DARK);
        add(title, BorderLayout.NORTH);

        // Report cards
        JPanel reportsPanel = new JPanel(new java.awt.GridLayout(2, 2, 12, 12));
        reportsPanel.setOpaque(false);
        reportsPanel.add(createReportCard("Thống kê thí sinh", "12,458 đơn đăng ký", UIStyles.PRIMARY));
        reportsPanel.add(createReportCard("Tỉ lệ trúng tuyển", "28.5%", UIStyles.SUCCESS));
        reportsPanel.add(createReportCard("Số ngành mở", "28 ngành", UIStyles.INFO));
        reportsPanel.add(createReportCard("Chỉ tiêu tuyển", "3,200 chỉ tiêu", UIStyles.WARNING));

        // Stats table
        String[] cols = {"Ngành", "Chỉ tiêu", "Đơn đăng ký", "Dự tính trúng", "Tỉ lệ"};
        Object[][] data = {
                {"CTT - Công nghệ thông tin", 250, 1180, 250, "4.72:1"},
                {"QTK - Quản trị kinh doanh", 180, 810, 180, "4.50:1"},
                {"NNA - Ngôn ngữ Anh", 150, 520, 150, "3.47:1"},
                {"XHH - Xã hội học", 120, 245, 120, "2.04:1"},
                {"QLDC - Quản lý dịch vụ du lịch", 100, 330, 100, "3.30:1"}
        };

        JTable table = new JTable(new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
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

        JLabel tableTitle = new JLabel("Thống kê theo ngành");
        tableTitle.setFont(UIStyles.FONT_SUBTITLE);
        tableTitle.setForeground(UIStyles.TEXT_DARK);
        tableCard.add(tableTitle, BorderLayout.NORTH);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BorderLayout(0, 12));
        center.add(reportsPanel, BorderLayout.NORTH);
        center.add(tableCard, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    private JPanel createReportCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UIStyles.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIStyles.FONT_BODY);
        titleLabel.setForeground(UIStyles.TEXT_MUTED);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 22));
        valueLabel.setForeground(color);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(titleLabel, BorderLayout.NORTH);
        top.add(valueLabel, BorderLayout.CENTER);

        card.add(top, BorderLayout.CENTER);
        return card;
    }
}
