package com.tuyensinh.view;

import javax.swing.JButton;
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
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Form dialog dùng chung cho các chức năng Thêm/Sửa.
 * Đồng bộ kích thước 600x500 và phong cách với CandidateFormDialog.
 */
public class SharedFormDialog extends JDialog {
    private boolean confirmed = false;
    private final List<JComponent> inputFields = new ArrayList<>();
    private final String[] columns;

    public SharedFormDialog(Frame owner, String title, String[] columns) {
        super(owner, title, true);
        this.columns = columns;
        
        // Kích thước chuẩn y hệt CandidateFormDialog
        setSize(600, 500);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout(0, 12));
        contentPane.setBorder(new EmptyBorder(16, 16, 16, 16));
        contentPane.setBackground(UIStyles.BG_CARD);
        setContentPane(contentPane);

        // Form fields: Khoảng cách dọc/ngang (12, 8) chuẩn theo CandidateFormDialog
        JPanel formPanel = new JPanel(new GridLayout(Math.max(12, columns.length), 2, 12, 8));
        formPanel.setOpaque(false);

        for (String col : columns) {
            JLabel lbl = new JLabel(col + " *:");
            lbl.setFont(UIStyles.FONT_LABEL);
            lbl.setForeground(UIStyles.TEXT_DARK);
            formPanel.add(lbl);

            if (col.equalsIgnoreCase("Trạng Thái")) {
                JComboBox<String> combo = new JComboBox<>(new String[]{"Đang tuyển", "Dừng tuyển"});
                combo.setFont(UIStyles.FONT_BODY);
                inputFields.add(combo);
                formPanel.add(combo);
            } else {
                JTextField txt = new JTextField();
                txt.setFont(UIStyles.FONT_BODY);
                inputFields.add(txt);
                formPanel.add(txt);
            }
        }

        // Đổ các ô trống nếu số cột ít hơn 12 để giữ Form đẩy lên top đẹp mắt
        for (int i = columns.length; i < 12; i++) {
            formPanel.add(new JLabel(""));
            formPanel.add(new JLabel(""));
        }

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

    private boolean validateForm() {
        for (JComponent comp : inputFields) {
            if (comp instanceof JTextField) {
                if (((JTextField) comp).getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
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
                data[i] = ((JTextField) comp).getText().trim();
            }
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    public void setData(Object[] rowData) {
        for (int i = 0; i < inputFields.size(); i++) {
            JComponent comp = inputFields.get(i);
            if (comp instanceof JComboBox) {
                ((JComboBox<String>) comp).setSelectedItem(rowData[i].toString());
            } else if (comp instanceof JTextField) {
                ((JTextField) comp).setText(rowData[i] != null ? rowData[i].toString() : "");
            }
        }
    }
}