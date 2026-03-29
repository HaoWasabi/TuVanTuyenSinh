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
        add(new TopBar(this), BorderLayout.NORTH);

        // Content dùng CardLayout
        CardLayout cardLayout = new CardLayout();
        JPanel contentPanel = new JPanel(cardLayout);

        contentPanel.add(new DashboardPanel(), "dashboard");
        contentPanel.add(new CandidateManagementPanel(), "candidate");
        contentPanel.add(new AccountManagementPanel(), "account");
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
}