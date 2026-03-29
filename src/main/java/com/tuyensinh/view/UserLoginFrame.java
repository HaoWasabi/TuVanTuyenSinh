package com.tuyensinh.view;

import javax.swing.*;
import java.awt.*;

public class UserLoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public UserLoginFrame() {
        setTitle("Đăng nhập Người dùng");
        setSize(460, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        mainPanel.setBackground(Color.WHITE);

        JLabel title = new JLabel("ĐĂNG NHẬP NGƯỜI DÙNG", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
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

        JButton btnLogin = new JButton("Đăng nhập");
        JButton btnBack = new JButton("Quay lại");

        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        btnLogin.setFocusPainted(false);
        btnBack.setFocusPainted(false);

        btnLogin.addActionListener(e -> loginUser());
        btnBack.addActionListener(e -> {
            new RoleSelectionFrame().setVisible(true);
            dispose();
        });

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnBack);

        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loginUser() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.equals("user") && password.equals("123")) {
            JOptionPane.showMessageDialog(this, "Đăng nhập người dùng thành công!");

            JFrame frame = new JFrame("Trang người dùng");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);

            JPanel panel = new JPanel(new BorderLayout());
            JLabel label = new JLabel("TRANG NGƯỜI DÙNG", SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 28));

            JButton btnLogout = new JButton("Đăng xuất");
            btnLogout.addActionListener(e -> {
                new RoleSelectionFrame().setVisible(true);
                frame.dispose();
            });

            JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            top.add(btnLogout);

            panel.add(top, BorderLayout.NORTH);
            panel.add(label, BorderLayout.CENTER);

            frame.setContentPane(panel);
            frame.setVisible(true);

            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu người dùng!");
        }
    }
}