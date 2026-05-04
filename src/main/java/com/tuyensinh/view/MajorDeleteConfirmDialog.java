package com.tuyensinh.view;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;

/**
 * Dialog xác nhận xóa thông tin cho module Ngành & Tổ hợp
 * Đã fix lỗi hiển thị khi tên item quá dài.
 */
public class MajorDeleteConfirmDialog extends JDialog {
    private boolean confirmed = false;

    public MajorDeleteConfirmDialog(Frame owner, String itemName) {
        super(owner, "Xác Nhận Xóa", true);
        
        // Tăng nhẹ chiều cao để có chỗ cho chữ rớt xuống dòng
        setSize(420, 180); 
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout(0, 12));
        contentPane.setBorder(new EmptyBorder(16, 16, 16, 16));
        contentPane.setBackground(UIStyles.BG_CARD);
        setContentPane(contentPane);

        // 1. Thuật toán cắt chuỗi: Nếu tên quá 80 ký tự thì cắt gọn thêm "..."
        String displayName = itemName;
        if (displayName.length() > 80) {
            displayName = displayName.substring(0, 77) + "...";
        }

        // 2. Dùng thẻ <div> có thuộc tính width để ép JLabel tự động xuống dòng (Word-wrap)
        String htmlText = "<html><div style='width: 350px; line-height: 1.5;'>"
                        + "Bạn có chắc muốn xóa mục: <b>" + displayName + "</b>?<br/>"
                        + "Hành động này không thể hoàn tác."
                        + "</div></html>";

        JLabel msgLabel = new JLabel(htmlText);
        msgLabel.setFont(UIStyles.FONT_BODY);
        msgLabel.setForeground(UIStyles.TEXT_DARK);
        contentPane.add(msgLabel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        JButton deleteBtn = new JButton("Xóa");
        deleteBtn.setFont(UIStyles.FONT_BODY);
        deleteBtn.setBackground(UIStyles.DANGER);
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false);
        deleteBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                confirmed = true;
                dispose();
            }
        });

        JButton cancelBtn = new JButton("Hủy");
        cancelBtn.setFont(UIStyles.FONT_BODY);
        cancelBtn.setBackground(UIStyles.TEXT_MUTED);
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });

        btnPanel.add(deleteBtn);
        btnPanel.add(cancelBtn);
        contentPane.add(btnPanel, BorderLayout.SOUTH);
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}