package com.tuyensinh.view;

import com.tuyensinh.model.User;
import com.tuyensinh.service.AuthService;

import javax.swing.*;
import java.awt.*;

public class RoleSelectionFrame extends JFrame {
    private final AuthService authService = new AuthService();
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public RoleSelectionFrame() {
        setTitle("Đăng nhập hệ thống");
        setSize(460, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        mainPanel.setBackground(Color.WHITE);

        JLabel title = new JLabel("ĐĂNG NHẬP HỆ THỐNG", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(25, 55, 109));

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 12, 12));
        formPanel.setBackground(Color.WHITE);

        JLabel lblUsername = new JLabel("Tên đăng nhập:");
        JLabel lblPassword = new JLabel("Mật khẩu:");

        lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();

        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        formPanel.add(lblUsername);
        formPanel.add(txtUsername);
        formPanel.add(lblPassword);
        formPanel.add(txtPassword);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton loginButton = new JButton("Đăng nhập");
        JButton exitButton = new JButton("Thoát");

        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        exitButton.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        loginButton.setFocusPainted(false);
        exitButton.setFocusPainted(false);

        loginButton.addActionListener(e -> handleLogin());
        exitButton.addActionListener(e -> dispose());

        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);

        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isBlank() || password.isBlank()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!");
            return;
        }

        try {
            User user = authService.login(username, password);
            JOptionPane.showMessageDialog(this, "Đăng nhập thành công: " + user.getUsername());
            new AdmissionsDemoFrame().setVisible(true);
            dispose();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
        }
    }
}