package com.tuyensinh.view;

import com.tuyensinh.service.SessionManager;

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
        root.add(new TopBar(this::handleLogout), BorderLayout.NORTH);

        // Main layout: sidebar + content
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(UIStyles.BG_APP);

        // Content panel with card layout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIStyles.BG_APP);

        // Add available panels
        contentPanel.add(new DashboardPanel(), "dashboard");
        contentPanel.add(new PersonalInfoPanel(), "personal");
        registerLazyCard("candidate", CandidateManagementPanel::new);
        registerLazyCard("major", MajorManagementPanel::new);
        registerLazyCard("diem", DiemThiPanel::new);
        registerLazyCard("diemCong", DiemCongPanel::new);
        registerLazyCard("nguyenVong", NguyenVongTabPanel::new);
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
        if (!canAccessPage(key)) {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Bạn không có quyền truy cập chức năng này.",
                    "Từ chối truy cập",
                    javax.swing.JOptionPane.WARNING_MESSAGE
            );
            cardLayout.show(contentPanel, "dashboard");
            return;
        }

        LazyPanelCard card = lazyCards.get(key);
        if (card != null) {
            card.ensureLoaded();
        }
    }

    private boolean canAccessPage(String key) {
        return switch (key) {
            case "candidate" -> SessionManager.hasAnyPermission("THISINH_VIEW", "THISINH_VIEW_BY_CCCD");
            case "major" -> SessionManager.hasAnyPermission("NGANH_VIEW", "NGANH_TOHOP_VIEW", "TOHOP_VIEW", "QUYDOI_VIEW");
            case "diem" -> SessionManager.hasAnyPermission("DIEM_VIEW", "DIEM_VIEW_BY_CCCD");
            case "nguyenVong" -> SessionManager.hasAnyPermission("NGUYENVONG_VIEW", "NGUYENVONG_VIEW_BY_CCCD");
            case "diemCong" -> SessionManager.hasAnyPermission("DIEMCONG_VIEW", "DIEMCONG_VIEW_BY_CCCD");
            case "user" -> SessionManager.hasAnyPermission("USER_VIEW");
            case "permission" -> SessionManager.hasAnyPermission("USER_CHANGE_ROLE");
            case "report" -> SessionManager.hasAnyPermission("DIEM_THONGKE");
            default -> true;
        };
    }

    private void handleLogout() {
        int confirm = javax.swing.JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn đăng xuất không?",
                "Xác nhận đăng xuất",
                javax.swing.JOptionPane.YES_NO_OPTION
        );
        if (confirm == javax.swing.JOptionPane.YES_OPTION) {
            new RoleSelectionFrame().setVisible(true);
            dispose();
        }
    }
}
