package com.tuyensinh.view;

import com.tuyensinh.model.User;
import com.tuyensinh.service.SessionManager;
import com.tuyensinh.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;

public class PersonalInfoPanel extends JPanel {
    private final UserService userService = new UserService();

    private final JTextField usernameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextField fullNameField = new JTextField();
    private final JTextField roleField = new JTextField();
    private final JTextField statusField = new JTextField();

    private final JPasswordField oldPasswordField = new JPasswordField();
    private final JPasswordField newPasswordField = new JPasswordField();
    private final JPasswordField confirmPasswordField = new JPasswordField();

    public PersonalInfoPanel() {
        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(UIStyles.BG_APP);

        JLabel title = new JLabel("Thông Tin Cá Nhân");
        title.setFont(UIStyles.FONT_TITLE);
        title.setForeground(UIStyles.TEXT_DARK);
        add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 16, 0));
        content.setOpaque(false);
        content.add(createInfoCard());
        content.add(createPasswordCard());

        add(content, BorderLayout.CENTER);

        loadUserInfo();
    }

    private JPanel createInfoCard() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(UIStyles.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel cardTitle = new JLabel("Thông tin tài khoản");
        cardTitle.setFont(UIStyles.FONT_SUBTITLE);
        cardTitle.setForeground(UIStyles.TEXT_DARK);

        JPanel form = new JPanel(new GridLayout(0, 2, 12, 10));
        form.setOpaque(false);

        form.add(label("Tên đăng nhập"));
        form.add(readOnly(usernameField));
        form.add(label("Vai trò"));
        form.add(readOnly(roleField));
        form.add(label("Trạng thái"));
        form.add(readOnly(statusField));
        form.add(label("Họ tên"));
        form.add(configureEditable(fullNameField));
        form.add(label("Email"));
        form.add(configureEditable(emailField));
        form.add(label("Số điện thoại"));
        form.add(configureEditable(phoneField));

        JButton saveBtn = new JButton("Cập nhật thông tin");
        saveBtn.setBackground(UIStyles.INFO);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(e -> updateUserInfo());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(saveBtn);

        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setOpaque(false);
        body.add(form, BorderLayout.NORTH);
        body.add(actions, BorderLayout.SOUTH);

        card.add(cardTitle, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel createPasswordCard() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(UIStyles.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel cardTitle = new JLabel("Cập nhật mật khẩu");
        cardTitle.setFont(UIStyles.FONT_SUBTITLE);
        cardTitle.setForeground(UIStyles.TEXT_DARK);

        JPanel form = new JPanel(new GridLayout(0, 2, 12, 12));
        form.setOpaque(false);

        form.add(label("Mật khẩu hiện tại"));
        form.add(configurePassword(oldPasswordField));
        form.add(label("Mật khẩu mới"));
        form.add(configurePassword(newPasswordField));
        form.add(label("Xác nhận mật khẩu mới"));
        form.add(configurePassword(confirmPasswordField));

        JButton changeBtn = new JButton("Đổi mật khẩu");
        changeBtn.setBackground(UIStyles.SUCCESS);
        changeBtn.setForeground(Color.WHITE);
        changeBtn.setFocusPainted(false);
        changeBtn.addActionListener(e -> updatePassword());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(changeBtn);

        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setOpaque(false);
        body.add(form, BorderLayout.NORTH);
        body.add(actions, BorderLayout.SOUTH);

        card.add(cardTitle, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JLabel label(String text) {
        JLabel label = new JLabel(text + ":");
        label.setFont(UIStyles.FONT_LABEL);
        label.setForeground(UIStyles.TEXT_DARK);
        return label;
    }

    private JTextField readOnly(JTextField field) {
        field.setEditable(false);
        field.setBackground(new Color(246, 248, 252));
        field.setBorder(BorderFactory.createLineBorder(UIStyles.BORDER));
        field.setFont(UIStyles.FONT_BODY);
        return field;
    }

    private JTextField configureEditable(JTextField field) {
        field.setBorder(BorderFactory.createLineBorder(UIStyles.BORDER));
        field.setFont(UIStyles.FONT_BODY);
        return field;
    }

    private JPasswordField configurePassword(JPasswordField field) {
        field.setBorder(BorderFactory.createLineBorder(UIStyles.BORDER));
        field.setFont(UIStyles.FONT_BODY);
        return field;
    }

    private void loadUserInfo() {
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Chưa có user đăng nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        usernameField.setText(safe(user.getUsername()));
        emailField.setText(safe(user.getEmail()));
        phoneField.setText(safe(user.getPhoneNumber()));
        fullNameField.setText(safe(user.getFullName()));
        roleField.setText(toRoleLabel(user));
        statusField.setText(toStatusLabel(user.getStatus()));
    }

    private void updateUserInfo() {
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Không có user đang đăng nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        user.setFullName(blankToNull(fullNameField.getText()));
        user.setEmail(blankToNull(emailField.getText()));
        user.setPhoneNumber(blankToNull(phoneField.getText()));
        user.setUpdatedAt(LocalDateTime.now());

        try {
            userService.update(user);
            SessionManager.initialize(user);
            JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không thể cập nhật: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePassword() {
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Không có user đang đăng nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String oldPassword = new String(oldPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (oldPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!oldPassword.equals(user.getPassword())) {
            JOptionPane.showMessageDialog(this, "Mật khẩu hiện tại không đúng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Xác nhận mật khẩu không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (newPassword.length() < 6) {
            JOptionPane.showMessageDialog(this, "Mật khẩu mới phải có ít nhất 6 ký tự!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        user.setPassword(newPassword);
        user.setUpdatedAt(LocalDateTime.now());

        try {
            userService.update(user);
            SessionManager.initialize(user);
            oldPasswordField.setText("");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không thể đổi mật khẩu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String toRoleLabel(User user) {
        if (user.getRole() != null && user.getRole().getName() != null) {
            return user.getRole().getName();
        }
        return "N/A";
    }

    private String toStatusLabel(String status) {
        if ("active".equalsIgnoreCase(status)) {
            return "Hoạt động";
        }
        if ("off".equalsIgnoreCase(status)) {
            return "Đã khóa";
        }
        return safe(status);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String blankToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
