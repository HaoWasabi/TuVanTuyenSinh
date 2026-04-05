package com.tuyensinh.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.HashMap;
import java.util.Map;

public class AdmissionsDemoFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final Map<String, LazyPanelCard> lazyCards = new HashMap<>();

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
        registerLazyCard("candidate", CandidateManagementPanel::new);
        registerLazyCard("major", MajorManagementPanel::new);
        registerLazyCard("diem", DiemThiPanel::new);
        registerLazyCard("diemCong", DiemCongPanel::new);
        registerLazyCard("nguyenVong", NguyenVongPanel::new);
        registerLazyCard("user", UserManagementPanel::new);
        registerLazyCard("permission", RoleManagementPanel::new);
        registerLazyCard("report", ReportsPanel::new);

        // Sidebar
        Sidebar sidebar = new Sidebar(cardLayout, contentPanel, this::preparePanel);
        mainPanel.add(sidebar, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        root.add(mainPanel, BorderLayout.CENTER);

        cardLayout.show(contentPanel, "dashboard");
    }

    private void registerLazyCard(String key, java.util.function.Supplier<JPanel> supplier) {
        LazyPanelCard card = new LazyPanelCard(supplier);
        lazyCards.put(key, card);
        contentPanel.add(card, key);
    }

    private void preparePanel(String key) {
        LazyPanelCard card = lazyCards.get(key);
        if (card != null) {
            card.ensureLoaded();
        }
    }
}
