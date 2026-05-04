package com.tuyensinh.view;

import com.tuyensinh.service.SessionManager;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.function.Consumer;

public class Sidebar extends JPanel {
    private final JPanel navContainer;
    private JButton selectedButton;
    private final Consumer<String> beforeShow;

    public Sidebar(CardLayout cardLayout, JPanel contentPanel) {
        this(cardLayout, contentPanel, null);
    }

    public Sidebar(CardLayout cardLayout, JPanel contentPanel, Consumer<String> beforeShow) {
        this.beforeShow = beforeShow;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(260, 0));
        setBackground(UIStyles.BG_SIDEBAR);

        // Header
        JLabel header = new JLabel("MENU");
        header.setFont(UIStyles.FONT_LABEL);
        header.setForeground(UIStyles.TEXT_MUTED);
        header.setBorder(BorderFactory.createEmptyBorder(16, 16, 12, 16));
        add(header, BorderLayout.NORTH);

        // Navigation container
        navContainer = new JPanel();
        navContainer.setLayout(new javax.swing.BoxLayout(navContainer, javax.swing.BoxLayout.Y_AXIS));
        navContainer.setBackground(UIStyles.BG_SIDEBAR);
        navContainer.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        // Add menu items based on permissions
        addMenuItem("Dashboard", "dashboard", cardLayout, contentPanel);
        addMenuItem("Thông tin cá nhân", "personal", cardLayout, contentPanel);
        addMenuItemIfAllowed("Ngành & tổ hợp", "major", cardLayout, contentPanel, "NGANH_VIEW", "NGANH_TOHOP_VIEW", "TOHOP_VIEW", "QUYDOI_VIEW");
        addMenuItemIfAllowed("Quản lý thí sinh", "candidate", cardLayout, contentPanel, "THISINH_VIEW", "THISINH_VIEW_BY_CCCD");
        addMenuItemIfAllowed("Quản lý điểm thi", "diem", cardLayout, contentPanel, "DIEM_VIEW", "DIEM_VIEW_BY_CCCD");
        addMenuItemIfAllowed("Quản lý điểm cộng", "diemCong", cardLayout, contentPanel, "DIEMCONG_VIEW", "DIEMCONG_VIEW_BY_CCCD");
        addMenuItemIfAllowed("Nguyện vọng & xét tuyển", "nguyenVong", cardLayout, contentPanel, "NGUYENVONG_VIEW", "NGUYENVONG_VIEW_BY_CCCD");
        addMenuItemIfAllowed("Quản lý người dùng", "user", cardLayout, contentPanel, "USER_VIEW");
        addMenuItemIfAllowed("Báo cáo thống kê", "report", cardLayout, contentPanel, "DIEM_THONGKE");
        addMenuItemIfAllowed("Phân quyền", "permission", cardLayout, contentPanel, "USER_CHANGE_ROLE");

        JScrollPane scrollPane = new JScrollPane(navContainer);
        scrollPane.setBorder(null);
        scrollPane.setBackground(UIStyles.BG_SIDEBAR);
        add(scrollPane, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(new Color(35, 42, 56));
        JLabel footerText = new JLabel("v1.0 Admin");
        footerText.setFont(UIStyles.FONT_TINY);
        footerText.setForeground(UIStyles.TEXT_MUTED);
        footer.add(footerText);
        add(footer, BorderLayout.SOUTH);
    }

    private void addMenuItem(String title, String pageKey, CardLayout cardLayout, JPanel contentPanel) {
        JButton btn = new JButton(title);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setFont(UIStyles.FONT_BODY);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setForeground(UIStyles.TEXT_LIGHT);
        btn.setBackground(UIStyles.BG_SIDEBAR);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(55, 62, 76)),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            if (beforeShow != null) {
                beforeShow.accept(pageKey);
            }
            cardLayout.show(contentPanel, pageKey);
            selectButton(btn);
        });

        navContainer.add(btn);

        // Set first button as selected
        if (selectedButton == null) {
            selectButton(btn);
        }
    }

    private void addMenuItemIfAllowed(String title, String pageKey, CardLayout cardLayout, JPanel contentPanel, String... requiredPermissions) {
        if (SessionManager.hasAnyPermission(requiredPermissions)) {
            addMenuItem(title, pageKey, cardLayout, contentPanel);
        }
    }

    private void selectButton(JButton btn) {
        if (selectedButton != null) {
            selectedButton.setBackground(UIStyles.BG_SIDEBAR);
            selectedButton.setForeground(UIStyles.TEXT_LIGHT);
        }
        selectedButton = btn;
        selectedButton.setBackground(UIStyles.PRIMARY);
        selectedButton.setForeground(Color.WHITE);
    }
}
