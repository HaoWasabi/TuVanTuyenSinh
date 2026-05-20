package com.tuyensinh.view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.time.LocalDate;
import java.time.ZoneId;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Form dialog để thêm/sửa thông tin thí sinh
 * Các trường: CCCD, Số báo danh, Họ, Tên, Ngày sinh, Giới tính, Email, Điện
 * thoại,
 * Mật khẩu, Nơi sinh, Đối tượng, Khu vực
 */
public class CandidateFormDialog extends JDialog {
    private final JTextField cccdField = new JTextField(20);
    private final JTextField sbaodanhField = new JTextField(20);
    private final JTextField hoField = new JTextField(20);
    private final JTextField tenField = new JTextField(20);
    private final JSpinner ngaysinhSpinner = new JSpinner(new javax.swing.SpinnerDateModel());
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final JComboBox<String> gioitinhCombo = new JComboBox<>(new String[] { "Nam", "Nữ" });
    private final JTextField emailField = new JTextField(20);
    private final JTextField dienthoaiField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JTextField noisinhField = new JTextField(20);
    private final JComboBox<ComboItem> doituongCombo = new JComboBox<>(new ComboItem[] {
            new ComboItem("UT0", "Không thuộc diện ưu tiên"),
            new ComboItem("UT1", "Nhóm UT1 (Dân tộc thiểu số, khuyết tật...) (+2.0)"),
            new ComboItem("UT2", "Nhóm UT2 (Thương binh, bđ xuất ngũ...) (+1.0)")
    });
    private final JComboBox<ComboItem> khuvucCombo = new JComboBox<>(new ComboItem[] {
            new ComboItem("KV1", "Khu vực 1 (KV1) (+0.75)"),
            new ComboItem("KV2", "Khu vực 2 (KV2) (+0.25)"),
            new ComboItem("KV2NT", "Khu vực 2 - Nông thôn (KV2-NT) (+0.5)"),
            new ComboItem("KV3", "Khu vực 3 (KV3)")
    });

    private boolean confirmed = false;

    public CandidateFormDialog(Frame owner, String title, boolean isEditing) {
        super(owner, title, true);
        dateFormat.setLenient(false);
        setSize(600, isEditing ? 520 : 480);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout(0, 12));
        contentPane.setBorder(new EmptyBorder(16, 16, 16, 16));
        contentPane.setBackground(UIStyles.BG_CARD);
        setContentPane(contentPane);

        // Form fields
        int gridRows = isEditing ? 12 : 11; // 1 row less when not editing (no password field)
        JPanel formPanel = new JPanel(new java.awt.GridLayout(gridRows, 2, 12, 8));
        formPanel.setOpaque(false);

        addFormField(formPanel, "CCCD *:", cccdField);
        addFormField(formPanel, "Số báo danh *:", sbaodanhField);
        addFormField(formPanel, "Họ *:", hoField);
        addFormField(formPanel, "Tên *:", tenField);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(ngaysinhSpinner, "dd/MM/yyyy");
        ngaysinhSpinner.setEditor(dateEditor);
        Date yesterday = Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        javax.swing.SpinnerDateModel model = (javax.swing.SpinnerDateModel) ngaysinhSpinner.getModel();
        model.setEnd(yesterday);
        ngaysinhSpinner.setValue(new Date());
        addFormField(formPanel, "Ngày sinh:", ngaysinhSpinner);
        addFormField(formPanel, "Giới tính:", gioitinhCombo);
        addFormField(formPanel, "Email:", emailField);
        addFormField(formPanel, "Điện thoại:", dienthoaiField);

        // Chỉ hiển thị trường mật khẩu khi sửa (isEditing=true)
        if (isEditing) {
            addFormField(formPanel, "Mật khẩu (để trống nếu không đổi):", passwordField);
        }

        addFormField(formPanel, "Nơi sinh:", noisinhField);
        addFormField(formPanel, "Đối tượng:", doituongCombo);
        addFormField(formPanel, "Khu vực:", khuvucCombo);

        contentPane.add(formPanel, BorderLayout.CENTER);

        // Buttons
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
    }

    private void addFormField(JPanel panel, String label, Object component) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIStyles.FONT_LABEL);
        lbl.setForeground(UIStyles.TEXT_DARK);
        panel.add(lbl);

        if (component instanceof JComboBox) {
            panel.add((JComboBox<?>) component);
        } else if (component instanceof JSpinner) {
            panel.add((JSpinner) component);
        } else if (component instanceof JPasswordField) {
            panel.add((JPasswordField) component);
        } else {
            panel.add((JTextField) component);
        }
    }

    private boolean validateForm() {
        if (cccdField.getText().trim().isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "CCCD không được để trống!", "Lỗi",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (sbaodanhField.getText().trim().isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Số báo danh không được để trống!", "Lỗi",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (hoField.getText().trim().isEmpty() || tenField.getText().trim().isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Họ và tên không được để trống!", "Lỗi",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
        Date selectedDate = (Date) ngaysinhSpinner.getValue();
        LocalDate dob = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (!dob.isBefore(LocalDate.now())) {
            javax.swing.JOptionPane.showMessageDialog(this, "Ngày sinh phải thuộc quá khứ!", "Lỗi",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getCCCD() {
        return cccdField.getText();
    }

    public String getSbaodanh() {
        return sbaodanhField.getText();
    }

    public String getHo() {
        return hoField.getText();
    }

    public String getTen() {
        return tenField.getText();
    }

    public String getNgaysinh() {
        Date date = (Date) ngaysinhSpinner.getValue();
        return dateFormat.format(date);
    }

    public String getGioitinh() {
        return (String) gioitinhCombo.getSelectedItem();
    }

    public String getEmail() {
        return emailField.getText();
    }

    public String getDienthoai() {
        return dienthoaiField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public String getNoisinh() {
        return noisinhField.getText();
    }

    public String getDoituong() {
        ComboItem item = (ComboItem) doituongCombo.getSelectedItem();
        return (item != null) ? item.getValue() : ""; // Trả về "UT0", "UT1"... thay vì tên dài
    }

    public String getKhuvuc() {
        ComboItem item = (ComboItem) khuvucCombo.getSelectedItem();
        return (item != null) ? item.getValue() : ""; // Trả về "KV1", "KV2NT"...
    }

    // Setters for editing mode
    public void setData(String cccd, String sbaodanh, String ho, String ten, String ngaysinh,
            String gioitinh, String email, String dienthoai, String noisinh,
            String doituong, String khuvuc) {
        cccdField.setText(cccd);
        sbaodanhField.setText(sbaodanh);
        hoField.setText(ho);
        tenField.setText(ten);
        try {
            if (ngaysinh != null && !ngaysinh.trim().isEmpty()) {
                ngaysinhSpinner.setValue(dateFormat.parse(ngaysinh));
            }
        } catch (ParseException ignored) {
            ngaysinhSpinner.setValue(new Date());
        }
        gioitinhCombo.setSelectedItem(gioitinh);
        emailField.setText(email);
        dienthoaiField.setText(dienthoai);
        noisinhField.setText(noisinh);
        setSelectedComboValue(doituongCombo, doituong);
        setSelectedComboValue(khuvucCombo, khuvuc);
        cccdField.setEnabled(false); // Không cho sửa CCCD (primary key)
    }

    private static class ComboItem {
        private final String value; // Giá trị ẩn (Ví dụ: UT1)
        private final String label; // Tên hiển thị (Ví dụ: Đối tượng ưu tiên 1)

        public ComboItem(String value, String label) {
            this.value = value;
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public String getLabel() {
            return label;
        }

        @Override
        public String toString() {
            return label; // Quyết định JComboBox sẽ hiển thị chữ gì trên giao diện
        }
    }

    private void setSelectedComboValue(JComboBox<ComboItem> comboBox, String value) {
        if (value == null)
            return;
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            ComboItem item = comboBox.getItemAt(i);
            if (item.getValue().equals(value)) {
                comboBox.setSelectedItem(item);
                break;
            }
        }
    }
}
