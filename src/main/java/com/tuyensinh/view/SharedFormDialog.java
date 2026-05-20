package com.tuyensinh.view;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

/**
 * Form dialog dùng chung cho các chức năng Thêm/Sửa.
 * Đã sửa lỗi hiển thị bằng GridBagLayout giúp hiển thị 4 CheckBox hàng dọc rõ
 * ràng,
 * không bao giờ bị lỗi mất chữ, dính chữ hay méo mó form.
 */
public class SharedFormDialog extends JDialog {
    private boolean confirmed = false;
    private final List<JComponent> inputFields = new ArrayList<>();
    private final String[] columns;

    // Khai báo các CheckBox thành phần để quản lý dữ liệu
    private JCheckBox chkTuyenThang;
    private JCheckBox chkDGNL;
    private JCheckBox chkTHPT;
    private JCheckBox chkVSAT;

    public SharedFormDialog(Frame owner, String title, String[] columns) {
        super(owner, title, true);
        this.columns = columns;

        // Kích thước chuẩn rộng rãi ôm trọn danh sách hàng dọc
        setSize(620, 580);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout(0, 16));
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPane.setBackground(UIStyles.BG_CARD);
        setContentPane(contentPane);

        // Chuyển sang dùng GridBagLayout để kiểm soát kích thước từng dòng linh hoạt
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0); // Khoảng cách giữa các hàng
        gbc.fill = GridBagConstraints.HORIZONTAL;

        for (int i = 0; i < columns.length; i++) {
            String col = columns[i];

            // 1. Add Label (Cột bên trái)
            JLabel lbl = new JLabel(col + " *:");
            lbl.setFont(UIStyles.FONT_LABEL);
            lbl.setForeground(UIStyles.TEXT_DARK);
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0.3; // Chiếm 30% độ rộng form
            formPanel.add(lbl, gbc);

            // 2. Add Input Component (Cột bên phải)
            gbc.gridx = 1;
            gbc.weightx = 0.7; // Chiếm 70% độ rộng form

            if (col.trim().toLowerCase().startsWith("id")) {
                JTextField txt = new JTextField("(Tự tăng)");
                txt.setFont(UIStyles.FONT_BODY);
                txt.setEditable(false);
                txt.setForeground(UIStyles.TEXT_MUTED);
                inputFields.add(txt);
                formPanel.add(txt, gbc);

            } else if (col.equalsIgnoreCase("Trạng Thái")) {
                JComboBox<String> combo = new JComboBox<>(new String[] { "Đang tuyển", "Dừng tuyển" });
                combo.setFont(UIStyles.FONT_BODY);
                inputFields.add(combo);
                formPanel.add(combo, gbc);

            } else if (col.equalsIgnoreCase("Phương Thức Xét Tuyển")) {
                // Sử dụng Lưới 4 hàng 1 cột biệt lập hoàn toàn cho cụm Checkbox chọn phương
                // thức
                JPanel methodPanel = new JPanel(new java.awt.GridLayout(4, 1, 0, 6));
                methodPanel.setOpaque(false);

                chkTuyenThang = new JCheckBox("Tuyển thẳng");
                chkDGNL = new JCheckBox("ĐGNL");
                chkTHPT = new JCheckBox("THPT");
                chkVSAT = new JCheckBox("V-SAT");

                // Cấu hình phông chữ & kiểu dáng sạch đẹp
                chkTuyenThang.setFont(UIStyles.FONT_BODY);
                chkDGNL.setFont(UIStyles.FONT_BODY);
                chkTHPT.setFont(UIStyles.FONT_BODY);
                chkVSAT.setFont(UIStyles.FONT_BODY);

                chkTuyenThang.setFocusPainted(false);
                chkDGNL.setFocusPainted(false);
                chkTHPT.setFocusPainted(false);
                chkVSAT.setFocusPainted(false);

                methodPanel.add(chkTuyenThang);
                methodPanel.add(chkDGNL);
                methodPanel.add(chkTHPT);
                methodPanel.add(chkVSAT);

                inputFields.add(methodPanel);
                formPanel.add(methodPanel, gbc);

            } else {
                JTextField txt = new JTextField();
                txt.setFont(UIStyles.FONT_BODY);
                inputFields.add(txt);
                formPanel.add(txt, gbc);
            }
        }

        contentPane.add(formPanel, BorderLayout.CENTER);

        // Khối Panel điều khiển Nút bấm (Lưu / Hủy) dưới đáy màn hình
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
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

    private boolean validateForm() {
        for (int i = 0; i < columns.length; i++) {
            JComponent comp = inputFields.get(i);
            String colName = columns[i];

            if (comp instanceof JTextField) {
                JTextField txt = (JTextField) comp;
                if (!txt.isEditable()) {
                    continue;
                }
                if (txt.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } else if (colName.equalsIgnoreCase("Phương Thức Xét Tuyển")) {
                if (!chkTuyenThang.isSelected() && !chkDGNL.isSelected() && !chkTHPT.isSelected()
                        && !chkVSAT.isSelected()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một Phương thức xét tuyển!", "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Object[] getData() {
        Object[] data = new Object[columns.length];
        for (int i = 0; i < inputFields.size(); i++) {
            JComponent comp = inputFields.get(i);
            if (comp instanceof JComboBox) {
                data[i] = ((JComboBox<?>) comp).getSelectedItem();
            } else if (comp instanceof JTextField) {
                JTextField txt = (JTextField) comp;
                data[i] = txt.isEditable() ? txt.getText().trim() : null;
            } else if (columns[i].equalsIgnoreCase("Phương Thức Xét Tuyển")) {
                List<String> methods = new ArrayList<>();
                if (chkTuyenThang.isSelected())
                    methods.add("Tuyển thẳng");
                if (chkDGNL.isSelected())
                    methods.add("ĐGNL");
                if (chkTHPT.isSelected())
                    methods.add("THPT");
                if (chkVSAT.isSelected())
                    methods.add("V-SAT");

                data[i] = String.join(", ", methods);
            }
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    public void setData(Object[] rowData) {
        for (int i = 0; i < inputFields.size(); i++) {
            JComponent comp = inputFields.get(i);
            if (rowData[i] == null)
                continue;

            if (comp instanceof JComboBox) {
                ((JComboBox<String>) comp).setSelectedItem(rowData[i].toString());
            } else if (comp instanceof JTextField) {
                ((JTextField) comp).setText(rowData[i].toString());
            } else if (columns[i].equalsIgnoreCase("Phương Thức Xét Tuyển")) {
                String val = rowData[i].toString().toLowerCase();
                if (chkTuyenThang != null)
                    chkTuyenThang.setSelected(
                            val.contains("tuyển thẳng") || val.contains("tuyenthang") || val.contains("xtt"));
                if (chkDGNL != null)
                    chkDGNL.setSelected(val.contains("đgnl") || val.contains("dgnl"));
                if (chkTHPT != null)
                    chkTHPT.setSelected(val.contains("thpt"));
                if (chkVSAT != null)
                    chkVSAT.setSelected(val.contains("v-sat") || val.contains("vsat"));
            }
        }
    }
}