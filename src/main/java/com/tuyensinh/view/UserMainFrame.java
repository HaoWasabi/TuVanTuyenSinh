package com.tuyensinh.view;

import com.tuyensinh.model.ThiSinh;
import com.tuyensinh.service.ThiSinhService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class UserMainFrame extends JFrame {
    private final ThiSinhService thiSinhService = new ThiSinhService();
    private ThiSinh currentUser;

    private final JTextField cccdField = new JTextField();
    private final JTextField sobaodanhField = new JTextField();
    private final JTextField hoTenField = new JTextField();
    private final JTextField ngaySinhField = new JTextField();
    private final JTextField gioiTinhField = new JTextField();
    private final JTextField doiTuongField = new JTextField();
    private final JTextField khuVucField = new JTextField();
    private final JTextField noiSinhField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField dienThoaiField = new JTextField();

    private final JPasswordField oldPasswordField = new JPasswordField();
    private final JPasswordField newPasswordField = new JPasswordField();
    private final JPasswordField confirmPasswordField = new JPasswordField();

    public UserMainFrame(ThiSinh thiSinh) {
        this.currentUser = thiSinh;

        setTitle("Trang người dùng");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 680);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UIStyles.BG_APP);
        setContentPane(root);

        root.add(createTopBar(), BorderLayout.NORTH);
        root.add(createContent(), BorderLayout.CENTER);

        bindData();
    }

    private JPanel createTopBar() {
        JPanel top = new JPanel(new BorderLayout(12, 0));
        top.setBackground(UIStyles.BG_TOPBAR);
        top.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIStyles.BORDER),
                BorderFactory.createEmptyBorder(12, 18, 12, 18)
        ));

        JLabel title = new JLabel("Cổng thông tin thí sinh");
        title.setFont(UIStyles.FONT_SUBTITLE);
        title.setForeground(UIStyles.TEXT_DARK);
        top.add(title, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        JLabel userLabel = new JLabel(currentUser.getHo() + " " + currentUser.getTen());
        userLabel.setFont(UIStyles.FONT_BODY);
        userLabel.setForeground(UIStyles.TEXT_DARK);

        JButton logoutBtn = new JButton("Đăng xuất");
        logoutBtn.setFont(UIStyles.FONT_SMALL);
        logoutBtn.setBackground(UIStyles.DANGER);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        logoutBtn.addActionListener(e -> logout());

        right.add(userLabel);
        right.add(logoutBtn);
        top.add(right, BorderLayout.EAST);

        return top;
    }

    private JPanel createContent() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(UIStyles.BG_APP);
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel cards = new JPanel(new GridLayout(1, 2, 16, 0));
        cards.setOpaque(false);
        cards.add(createPersonalInfoCard());
        cards.add(createPasswordCard());

        container.add(cards, BorderLayout.CENTER);
        return container;
    }

    private JPanel createPersonalInfoCard() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(UIStyles.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        JLabel title = new JLabel("Thông tin cá nhân");
        title.setFont(UIStyles.FONT_SUBTITLE);
        title.setForeground(UIStyles.TEXT_DARK);
        card.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 2, 12, 10));
        form.setOpaque(false);

        form.add(label("CCCD"));
        form.add(readOnly(cccdField));
        form.add(label("Số báo danh"));
        form.add(readOnly(sobaodanhField));
        form.add(label("Họ tên"));
        form.add(readOnly(hoTenField));
        form.add(label("Ngày sinh"));
        form.add(readOnly(ngaySinhField));
        form.add(label("Giới tính"));
        form.add(readOnly(gioiTinhField));
        form.add(label("Đối tượng"));
        form.add(readOnly(doiTuongField));
        form.add(label("Khu vực"));
        form.add(readOnly(khuVucField));
        form.add(label("Nơi sinh"));
        form.add(readOnly(noiSinhField));
        form.add(label("Email"));
        form.add(configureEditable(emailField));
        form.add(label("Số điện thoại"));
        form.add(configureEditable(dienThoaiField));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton saveInfoBtn = new JButton("Cập nhật thông tin");
        saveInfoBtn.setBackground(UIStyles.INFO);
        saveInfoBtn.setForeground(Color.WHITE);
        saveInfoBtn.setFocusPainted(false);
        saveInfoBtn.addActionListener(e -> updateContactInfo());
        actions.add(saveInfoBtn);

        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setOpaque(false);
        body.add(form, BorderLayout.CENTER);
        body.add(actions, BorderLayout.SOUTH);

        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel createPasswordCard() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(UIStyles.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        JLabel title = new JLabel("Cập nhật mật khẩu");
        title.setFont(UIStyles.FONT_SUBTITLE);
        title.setForeground(UIStyles.TEXT_DARK);
        card.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 2, 12, 12));
        form.setOpaque(false);

        form.add(label("Mật khẩu hiện tại"));
        form.add(configurePassword(oldPasswordField));
        form.add(label("Mật khẩu mới"));
        form.add(configurePassword(newPasswordField));
        form.add(label("Xác nhận mật khẩu mới"));
        form.add(configurePassword(confirmPasswordField));

        JButton changePasswordBtn = new JButton("Đổi mật khẩu");
        changePasswordBtn.setBackground(UIStyles.SUCCESS);
        changePasswordBtn.setForeground(Color.WHITE);
        changePasswordBtn.setFocusPainted(false);
        changePasswordBtn.addActionListener(e -> updatePassword());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(changePasswordBtn);

        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setOpaque(false);
        body.add(form, BorderLayout.NORTH);
        body.add(actions, BorderLayout.SOUTH);

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

    private void bindData() {
        cccdField.setText(safe(currentUser.getCccd()));
        sobaodanhField.setText(safe(currentUser.getSobaodanh()));
        hoTenField.setText((safe(currentUser.getHo()) + " " + safe(currentUser.getTen())).trim());
        ngaySinhField.setText(safe(currentUser.getNgaySinh()));
        gioiTinhField.setText(safe(currentUser.getGioiTinh()));
        doiTuongField.setText(safe(currentUser.getDoiTuong()));
        khuVucField.setText(safe(currentUser.getKhuVuc()));
        noiSinhField.setText(safe(currentUser.getNoiSinh()));
        emailField.setText(safe(currentUser.getEmail()));
        dienThoaiField.setText(safe(currentUser.getDienThoai()));
    }

    private void updateContactInfo() {
        currentUser.setEmail(blankToNull(emailField.getText()));
        currentUser.setDienThoai(blankToNull(dienThoaiField.getText()));
        currentUser.setUpdatedAt(LocalDate.now());

        try {
            currentUser = thiSinhService.update(currentUser);
            JOptionPane.showMessageDialog(this, "Cập nhật thông tin cá nhân thành công!");
            bindData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Không thể cập nhật thông tin cá nhân: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePassword() {
        String oldPassword = new String(oldPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (oldPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin mật khẩu!");
            return;
        }

        if (!oldPassword.equals(currentUser.getPassword())) {
            JOptionPane.showMessageDialog(this, "Mật khẩu hiện tại không đúng!");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Xác nhận mật khẩu mới không khớp!");
            return;
        }

        if (newPassword.length() < 6) {
            JOptionPane.showMessageDialog(this, "Mật khẩu mới phải có ít nhất 6 ký tự!");
            return;
        }

        currentUser.setPassword(newPassword);
        currentUser.setUpdatedAt(LocalDate.now());

        try {
            currentUser = thiSinhService.update(currentUser);
            oldPasswordField.setText("");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Không thể cập nhật mật khẩu: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        new RoleSelectionFrame().setVisible(true);
        dispose();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String blankToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}