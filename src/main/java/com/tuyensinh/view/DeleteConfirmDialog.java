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
 * Dialog xác nhận xóa thông tin
 */
public class DeleteConfirmDialog extends JDialog {
    private boolean confirmed = false;

    public DeleteConfirmDialog(Frame owner, String candidateName) {
        super(owner, "Xác Nhận Xóa", true);
        setSize(400, 150);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout(0, 12));
        contentPane.setBorder(new EmptyBorder(16, 16, 16, 16));
        contentPane.setBackground(UIStyles.BG_CARD);
        setContentPane(contentPane);

        JLabel msgLabel = new JLabel("<html>Bạn có chắc muốn xóa thí sinh: <b>" + candidateName + "</b>?<br/>Hành động này không thể hoàn tác.</html>");
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
        deleteBtn.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        JButton cancelBtn = new JButton("Hủy");
        cancelBtn.setFont(UIStyles.FONT_BODY);
        cancelBtn.setBackground(UIStyles.TEXT_MUTED);
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(e -> dispose());

        btnPanel.add(deleteBtn);
        btnPanel.add(cancelBtn);
        contentPane.add(btnPanel, BorderLayout.SOUTH);
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
