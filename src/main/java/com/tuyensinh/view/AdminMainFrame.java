package com.tuyensinh.view;

import javax.swing.*;
import java.awt.*;

public class AdminMainFrame extends JFrame {

    public AdminMainFrame() {
        setTitle("Hệ thống quản trị tuyển sinh");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // TopBar
        add(new TopBar(this::handleLogout), BorderLayout.NORTH);

        // Content dùng CardLayout
        CardLayout cardLayout = new CardLayout();
        JPanel contentPanel = new JPanel(cardLayout);

        contentPanel.add(new DashboardPanel(), "dashboard");
        contentPanel.add(new CandidateManagementPanel(), "candidate");
        contentPanel.add(new MajorManagementPanel(), "major");
        contentPanel.add(new DiemThiPanel(), "diem");
        contentPanel.add(new NguyenVongPanel(), "nguyenVong");
        contentPanel.add(new DiemCongPanel(), "diemCong");
        contentPanel.add(new AccountManagementPanel(), "account");
        contentPanel.add(new RoleManagementPanel(), "permission");
        contentPanel.add(new UserManagementPanel(), "user");
        contentPanel.add(new ReportsPanel(), "report");

        // Sidebar
        Sidebar sidebar = new Sidebar(cardLayout, contentPanel);
        add(sidebar, BorderLayout.WEST);

        // Nội dung
        add(contentPanel, BorderLayout.CENTER);

        // Mặc định hiện dashboard
        cardLayout.show(contentPanel, "dashboard");
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn đăng xuất không?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            new RoleSelectionFrame().setVisible(true);
            dispose();
        }
    }
}