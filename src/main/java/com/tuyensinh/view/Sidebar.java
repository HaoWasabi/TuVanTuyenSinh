package com.tuyensinh.view;

import javax.swing.*;
import java.awt.*;

public class Sidebar extends JPanel {
    private final JPanel navContainer;
    private JButton selectedButton;

    public Sidebar(CardLayout cardLayout, JPanel contentPanel) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(260, 0));
        setBackground(UIStyles.BG_SIDEBAR);

        JLabel header = new JLabel("MENU");
        header.setFont(UIStyles.FONT_LABEL);
        header.setForeground(UIStyles.TEXT_MUTED);
        header.setBorder(BorderFactory.createEmptyBorder(16, 16, 12, 16));
        add(header, BorderLayout.NORTH);

        navContainer = new JPanel();
        navContainer.setLayout(new BoxLayout(navContainer, BoxLayout.Y_AXIS));
        navContainer.setBackground(UIStyles.BG_SIDEBAR);
        navContainer.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        addMenuItem("Dashboard", "dashboard", cardLayout, contentPanel);
        addMenuItem("Quản lý thí sinh", "candidate", cardLayout, contentPanel);
        addMenuItem("Ngành & tổ hợp", "major", cardLayout, contentPanel);
        addMenuItem("Quản lý điểm", "dashboard", cardLayout, contentPanel);
        addMenuItem("Nguyện vọng & xét", "dashboard", cardLayout, contentPanel);

        JScrollPane scrollPane = new JScrollPane(navContainer);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(UIStyles.BG_SIDEBAR);
        scrollPane.setBackground(UIStyles.BG_SIDEBAR);
        add(scrollPane, BorderLayout.CENTER);

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
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            cardLayout.show(contentPanel, pageKey);
            selectButton(btn);
        });

        navContainer.add(btn);

        if (selectedButton == null) {
            selectButton(btn);
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