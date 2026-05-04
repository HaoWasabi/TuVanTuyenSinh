package com.tuyensinh.view;

import com.tuyensinh.model.ThiSinh;
import com.tuyensinh.service.ThiSinhService;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class UserLoginFrame extends JFrame {
    private JTextField txtCccd;
    private JPasswordField txtPassword;
    private final ThiSinhService thiSinhService = new ThiSinhService();

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

        JLabel lblUsername = new JLabel("CCCD:");
        JLabel lblPassword = new JLabel("Mật khẩu:");

        lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        txtCccd = new JTextField();
        txtPassword = new JPasswordField();

        txtCccd.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        formPanel.add(lblUsername);
        formPanel.add(txtCccd);
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
        String cccd = txtCccd.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (cccd.isBlank() || password.isBlank()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ CCCD và mật khẩu!");
            return;
        }

        Optional<ThiSinh> optThiSinh = thiSinhService.getByCccd(cccd);
        if (optThiSinh.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy tài khoản thí sinh với CCCD này!");
            return;
        }

        ThiSinh thiSinh = optThiSinh.get();
        if (!password.equals(thiSinh.getPassword())) {
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu người dùng!");
            return;
        }

        JOptionPane.showMessageDialog(this, "Đăng nhập người dùng thành công!");
        new UserMainFrame(thiSinh).setVisible(true);
        dispose();
    }
}