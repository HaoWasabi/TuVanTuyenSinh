package com.tuyensinh.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;

public class NguyenVongFormDialog extends JDialog {
    private final JTextField cccdField = new JTextField(15);
    private final JTextField maNganhField = new JTextField(15);
    private final JTextField thuTuNvField = new JTextField(15);
    private final JComboBox<String> phuongThucCombo = new JComboBox<>(new String[]{"PT1", "PT2", "PT3", "PT4", "PT5", "PT6"});
    private final JTextField maThmField = new JTextField(15);

    private final JTextField diemThxtField = new JTextField("0.0", 15);
    private final JTextField diemUtqdField = new JTextField("0.0", 15);
    private final JTextField diemCongField = new JTextField("0.0", 15);
    private final JTextField diemXetTuyenField = new JTextField("0.0", 15);

    private final JComboBox<String> ketQuaCombo = new JComboBox<>(new String[]{"Chờ xét", "Trúng tuyển", "Trượt"});
    private final JTextField keysField = new JTextField(15);

    private boolean confirmed = false;

    public NguyenVongFormDialog(Frame owner, String title, boolean isEditing) {
        super(owner, title, true);
        setSize(700, 400); // Form rộng hơn để chứa 2 cột
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout(0, 12));
        contentPane.setBorder(new EmptyBorder(16, 16, 16, 16));
        contentPane.setBackground(UIStyles.BG_CARD);
        setContentPane(contentPane);

        // Chia form thành lưới 6 hàng, 4 cột (Label1, Input1, Label2, Input2)
        JPanel formPanel = new JPanel(new GridLayout(6, 4, 12, 12));
        formPanel.setOpaque(false);

        addFormField(formPanel, "CCCD *:", cccdField);
        addFormField(formPanel, "Mã Ngành *:", maNganhField);

        addFormField(formPanel, "Thứ tự NV *:", thuTuNvField);
        addFormField(formPanel, "Phương thức:", phuongThucCombo);

        addFormField(formPanel, "Mã Tổ hợp:", maThmField);
        addFormField(formPanel, "Điểm THXT:", diemThxtField);

        addFormField(formPanel, "Điểm Ưu tiên QĐ:", diemUtqdField);
        addFormField(formPanel, "Điểm Cộng:", diemCongField);

        addFormField(formPanel, "Điểm Xét Tuyển:", diemXetTuyenField);
        addFormField(formPanel, "Kết Quả:", ketQuaCombo);

        addFormField(formPanel, "Keys (Tự động):", keysField);
        keysField.setEditable(false); // Keys thường tự sinh, không cho người dùng tự gõ

        contentPane.add(formPanel, BorderLayout.CENTER);

        // Nút Lưu và Hủy
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        JButton saveBtn = new JButton("Lưu Nguyện Vọng");
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

        if (isEditing) {
            cccdField.setEnabled(false);
            thuTuNvField.setEnabled(false); // Tránh đổi thứ tự NV gây loạn CSDL
        }
    }

    private void addFormField(JPanel panel, String label, JComponent component) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIStyles.FONT_LABEL);
        panel.add(lbl);
        panel.add(component);
    }

    private boolean validateForm() {
        if (cccdField.getText().trim().isEmpty() || maNganhField.getText().trim().isEmpty() || thuTuNvField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ CCCD, Mã Ngành và Thứ tự NV!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            Integer.parseInt(thuTuNvField.getText().trim());
            new BigDecimal(diemThxtField.getText().trim());
            new BigDecimal(diemUtqdField.getText().trim());
            new BigDecimal(diemCongField.getText().trim());
            new BigDecimal(diemXetTuyenField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Thứ tự NV phải là số nguyên. Các ô Điểm phải là số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public boolean isConfirmed() { return confirmed; }

    // Getters chuẩn BigDecimal
    public String getCccd() { return cccdField.getText().trim(); }
    public String getMaNganh() { return maNganhField.getText().trim(); }
    public Integer getThuTuNv() { return Integer.parseInt(thuTuNvField.getText().trim()); }
    public String getPhuongThuc() { return (String) phuongThucCombo.getSelectedItem(); }
    public String getMaThm() { return maThmField.getText().trim(); }
    public BigDecimal getDiemThxt() { return new BigDecimal(diemThxtField.getText().trim()); }
    public BigDecimal getDiemUtqd() { return new BigDecimal(diemUtqdField.getText().trim()); }
    public BigDecimal getDiemCong() { return new BigDecimal(diemCongField.getText().trim()); }
    public BigDecimal getDiemXetTuyen() { return new BigDecimal(diemXetTuyenField.getText().trim()); }
    public String getKetQua() { return (String) ketQuaCombo.getSelectedItem(); }
    public String getKeys() { return keysField.getText().trim(); }

    public void setData(String cccd, String maNganh, Integer thuTuNv, BigDecimal diemThxt, BigDecimal diemUtqd,
                        BigDecimal diemCong, BigDecimal diemXetTuyen, String ketQua, String keys, String phuongThuc, String maThm) {
        cccdField.setText(cccd);
        maNganhField.setText(maNganh);
        thuTuNvField.setText(String.valueOf(thuTuNv));
        diemThxtField.setText(String.valueOf(diemThxt));
        diemUtqdField.setText(String.valueOf(diemUtqd));
        diemCongField.setText(String.valueOf(diemCong));
        diemXetTuyenField.setText(String.valueOf(diemXetTuyen));
        ketQuaCombo.setSelectedItem(ketQua);
        keysField.setText(keys);
        phuongThucCombo.setSelectedItem(phuongThuc);
        maThmField.setText(maThm);
    }
}