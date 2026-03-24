package com.tuyensinh.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;

public class AdmissionsDemoFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    public AdmissionsDemoFrame() {
        setTitle("Hệ thống xét tuyển sinh SGU 2026 - Adminty Theme");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        setContentPane(root);

        // Top bar
        root.add(new TopBar(), BorderLayout.NORTH);

        // Main layout: sidebar + content
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(UIStyles.BG_APP);

        // Content panel with card layout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIStyles.BG_APP);

        // Add available panels
        contentPanel.add(new DashboardPanel(), "dashboard");
        contentPanel.add(new CandidateManagementPanel(), "candidate");
        contentPanel.add(new MajorManagementPanel(), "major");

        // Sidebar
        Sidebar sidebar = new Sidebar(cardLayout, contentPanel);
        mainPanel.add(sidebar, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        root.add(mainPanel, BorderLayout.CENTER);
    }
}
