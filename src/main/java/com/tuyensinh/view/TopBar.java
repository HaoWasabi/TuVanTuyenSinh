package com.tuyensinh.view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

public class TopBar extends JPanel {
    public TopBar() {
        setLayout(new BorderLayout(16, 0));
        setBackground(UIStyles.BG_TOPBAR);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIStyles.BORDER),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        setPreferredSize(new Dimension(0, 60));

        // Left: Logo/Title
        JLabel logo = new JLabel("TUYEN SINH SGU");
        logo.setFont(UIStyles.FONT_SUBTITLE);
        logo.setForeground(UIStyles.PRIMARY);
        JLabel subtitle = new JLabel("Hệ thống xét tuyển 2026");
        subtitle.setFont(UIStyles.FONT_SMALL);
        subtitle.setForeground(UIStyles.TEXT_MUTED);

        JPanel logoPanel = new JPanel(new java.awt.GridLayout(2, 1, 0, 2));
        logoPanel.setOpaque(false);
        logoPanel.add(logo);
        logoPanel.add(subtitle);
        add(logoPanel, BorderLayout.WEST);

        // Center spacer (search moved to management pages)
        JPanel centerSpacer = new JPanel();
        centerSpacer.setOpaque(false);
        add(centerSpacer, BorderLayout.CENTER);

        // Right: User actions
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setOpaque(false);

        JButton notifBtn = new JButton("Thông báo");
        notifBtn.setFont(UIStyles.FONT_BODY);
        notifBtn.setBorderPainted(false);
        notifBtn.setContentAreaFilled(false);

        JLabel userLabel = new JLabel("Admin User");
        userLabel.setFont(UIStyles.FONT_BODY);
        userLabel.setForeground(UIStyles.TEXT_DARK);

        JButton logoutBtn = new JButton("Đăng xuất");
        logoutBtn.setFont(UIStyles.FONT_SMALL);
        logoutBtn.setBackground(UIStyles.DANGER);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        rightPanel.add(notifBtn);
        rightPanel.add(userLabel);
        rightPanel.add(logoutBtn);

        add(rightPanel, BorderLayout.EAST);
    }
}
