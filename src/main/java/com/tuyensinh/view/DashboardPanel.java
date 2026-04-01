package com.tuyensinh.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DashboardPanel extends JPanel {

    public DashboardPanel() {
        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(UIStyles.BG_APP);

        // ===== STATS =====
        JPanel statsContainer = new JPanel(new GridLayout(1, 4, 12, 12));
        statsContainer.setOpaque(false);
        statsContainer.add(createStatCard("Thí sinh", "12,458", UIStyles.PRIMARY));
        statsContainer.add(createStatCard("Ngành mở", "28", UIStyles.SUCCESS));
        statsContainer.add(createStatCard("Tổ hợp", "18", UIStyles.INFO));
        statsContainer.add(createStatCard("Công việc", "156", UIStyles.WARNING));

        // ===== TABLE =====
        String[] columns = {"Mã ngành", "Tên ngành", "Chỉ tiêu", "Đăng ký", "Tỉ lệ"};
        Object[][] rows = {
                {"7480201", "Công nghệ thông tin", 250, 1180, "4.72:1"},
                {"7340101", "Quản trị kinh doanh", 180, 810, "4.50:1"},
                {"7220201", "Ngôn ngữ Anh", 150, 520, "3.47:1"},
                {"7310301", "Xã hội học", 120, 245, "2.04:1"},
                {"7810103", "Quản lý dịch vụ du lịch", 100, 330, "3.30:1"}
        };

        JTable table = new JTable(new DefaultTableModel(rows, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        table.setRowHeight(32);
        table.getTableHeader().setFont(UIStyles.FONT_LABEL);
        table.getTableHeader().setBackground(new Color(247, 249, 251));
        table.setFont(UIStyles.FONT_BODY);

        JPanel tableCard = createTableCard("Top Ngành Theo Số Lượng Đăng Ký", table);

        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setOpaque(false);
        center.add(statsContainer, BorderLayout.NORTH);
        center.add(tableCard, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UIStyles.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIStyles.FONT_SMALL);
        titleLabel.setForeground(UIStyles.TEXT_MUTED);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(8));
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
}