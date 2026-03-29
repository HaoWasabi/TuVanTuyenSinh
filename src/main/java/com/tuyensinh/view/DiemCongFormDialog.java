package com.tuyensinh.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DiemCongFormDialog extends JDialog {
    private final JTextField cccdField = new JTextField(20);
    private final JTextField maNganhField = new JTextField(20);
    private final JTextField maToHopField = new JTextField(20);
    private final JComboBox<String> phuongThucCombo = new JComboBox<>(new String[]{"PT1", "PT2", "PT3", "PT4"});
    private final JTextField diemCCField = new JTextField("0.0", 20);
    private final JTextField diemUtxtField = new JTextField("0.0", 20);
    private final JTextField diemTongField = new JTextField("0.0", 20);
    private final JTextField ghiChuField = new JTextField(20);
    private final JTextField keysField = new JTextField(20);

    private boolean confirmed = false;

    public DiemCongFormDialog(Frame owner, String title, boolean isEditing) {
        super(owner, title, true);
        setSize(500, 450); // Thu gọn size lại cho vừa đẹp
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout(0, 12));
        contentPane.setBorder(new EmptyBorder(16, 16, 16, 16));
        contentPane.setBackground(UIStyles.BG_CARD);
        setContentPane(contentPane);

        // Form fields (Chia 9 hàng, 2 cột)
        JPanel formPanel = new JPanel(new GridLayout(9, 2, 12, 8));
        formPanel.setOpaque(false);

        addFormField(formPanel, "CCCD *:", cccdField);
        addFormField(formPanel, "Mã Ngành *:", maNganhField);
        addFormField(formPanel, "Mã Tổ Hợp:", maToHopField);
        addFormField(formPanel, "Phương thức:", phuongThucCombo);
        addFormField(formPanel, "Điểm Chứng chỉ:", diemCCField);
        addFormField(formPanel, "Điểm Ưu tiên XT:", diemUtxtField);
        addFormField(formPanel, "Điểm Tổng:", diemTongField);
        addFormField(formPanel, "Ghi chú:", ghiChuField);
        addFormField(formPanel, "Keys (DC_CCCD_Nganh):", keysField);

        contentPane.add(formPanel, BorderLayout.CENTER);

        // Nút bấm
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        JButton saveBtn = new JButton("Lưu");
        saveBtn.setFont(UIStyles.FONT_BODY);
        saveBtn.setBackground(UIStyles.SUCCESS);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(e -> {
            if (validateForm()) {
                confirmed = true;
                dispose();
            }
        });

        JButton cancelBtn = new JButton("Hủy");
        cancelBtn.setFont(UIStyles.FONT_BODY);
        cancelBtn.setBackground(UIStyles.TEXT_MUTED);
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(e -> dispose());

        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        contentPane.add(btnPanel, BorderLayout.SOUTH);

        // Nếu là chế độ sửa, khóa không cho sửa CCCD để tránh lỗi CSDL
        if (isEditing) {
            cccdField.setEnabled(false);
            keysField.setEnabled(false);
        }
    }

    private void addFormField(JPanel panel, String label, Object component) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIStyles.FONT_LABEL);
        lbl.setForeground(UIStyles.TEXT_DARK);
        panel.add(lbl);

        if (component instanceof JComboBox) {
            panel.add((JComboBox<?>) component);
        } else {
            panel.add((JTextField) component);
        }
    }

    private boolean validateForm() {
        if (cccdField.getText().trim().isEmpty() || maNganhField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "CCCD và Mã ngành không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            Double.parseDouble(diemCCField.getText());
            Double.parseDouble(diemUtxtField.getText());
            Double.parseDouble(diemTongField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Các ô Điểm phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public boolean isConfirmed() { return confirmed; }

    // Getters
    public String getCccd() { return cccdField.getText().trim(); }
    public String getMaNganh() { return maNganhField.getText().trim(); }
    public String getMaToHop() { return maToHopField.getText().trim(); }
    public String getPhuongThuc() { return (String) phuongThucCombo.getSelectedItem(); }
    public java.math.BigDecimal getDiemCC() {
        return new java.math.BigDecimal(diemCCField.getText().trim());
    }
    public java.math.BigDecimal getDiemUtxt() {
        return new java.math.BigDecimal(diemUtxtField.getText().trim());
    }
    public java.math.BigDecimal getDiemTong() {
        return new java.math.BigDecimal(diemTongField.getText().trim());
    }
    public String getGhiChu() { return ghiChuField.getText().trim(); }
    public String getKeys() { return keysField.getText().trim(); }

    // Hàm set data khi bấm Sửa
    // Đổi chữ Double thành java.math.BigDecimal
    public void setData(String cccd, String maNganh, String maToHop, String phuongThuc,
                        java.math.BigDecimal diemCC, java.math.BigDecimal diemUtxt, java.math.BigDecimal diemTong, String ghiChu, String keys) {
        cccdField.setText(cccd);
        maNganhField.setText(maNganh);
        maToHopField.setText(maToHop);
        phuongThucCombo.setSelectedItem(phuongThuc);
        diemCCField.setText(String.valueOf(diemCC));
        diemUtxtField.setText(String.valueOf(diemUtxt));
        diemTongField.setText(String.valueOf(diemTong));
        ghiChuField.setText(ghiChu != null ? ghiChu : "");
        keysField.setText(keys);
    }
}