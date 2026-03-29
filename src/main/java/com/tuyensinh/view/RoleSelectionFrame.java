package com.tuyensinh.view;

import javax.swing.*;
import java.awt.*;

public class RoleSelectionFrame extends JFrame {

    public RoleSelectionFrame() {
        setTitle("Chọn vai trò đăng nhập");
        setSize(420, 240);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        mainPanel.setBackground(Color.WHITE);

        JLabel title = new JLabel("ĐĂNG NHẬP HỆ THỐNG", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(33, 37, 41));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton adminButton = new JButton("Admin");
        JButton userButton = new JButton("Người dùng");

        adminButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        userButton.setFont(new Font("Segoe UI", Font.BOLD, 16));

        adminButton.setFocusPainted(false);
        userButton.setFocusPainted(false);

        adminButton.addActionListener(e -> {
            new AdminLoginFrame().setVisible(true);
            dispose();
        });

        userButton.addActionListener(e -> {
            new UserLoginFrame().setVisible(true);
            dispose();
        });

        buttonPanel.add(adminButton);
        buttonPanel.add(userButton);

        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);
    }
}