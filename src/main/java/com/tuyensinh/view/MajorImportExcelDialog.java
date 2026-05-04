package com.tuyensinh.view;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.io.File;

/**
 * Dialog import dữ liệu từ file Excel dành riêng cho module Ngành & Tổ Hợp.
 * Đã fix lỗi nút bị kéo giãn, đồng bộ form dáng 100% với DeleteConfirmDialog.
 */
public class MajorImportExcelDialog extends JDialog {
    private File selectedFile;
    private boolean confirmed = false;
    private final JLabel fileLabel = new JLabel("Chưa chọn file");
    private final JProgressBar progressBar = new JProgressBar();

    public MajorImportExcelDialog(Frame owner, String moduleName) {
        super(owner, "Import Dữ Liệu", true);
        setSize(450, 220); // Kích thước gọn gàng, chuẩn form
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout(0, 12));
        contentPane.setBorder(new EmptyBorder(16, 16, 16, 16));
        contentPane.setBackground(UIStyles.BG_CARD);
        setContentPane(contentPane);

        // --- KHU VỰC TRUNG TÂM (Chứa chữ, nút Chọn File và Progress Bar) ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new javax.swing.BoxLayout(centerPanel, javax.swing.BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // 1. Text hướng dẫn (Sử dụng HTML để tự động rớt dòng gọn gàng)
        String htmlText = "<html><div style='width: 380px; line-height: 1.4;'>"
                        + "Chọn file Excel (.xlsx, .xls) để import dữ liệu cho:<br/><b>" 
                        + moduleName + "</b></div></html>";
        JLabel instructionLabel = new JLabel(htmlText);
        instructionLabel.setFont(UIStyles.FONT_BODY);
        instructionLabel.setForeground(UIStyles.TEXT_DARK);
        instructionLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        centerPanel.add(instructionLabel);
        
        centerPanel.add(javax.swing.Box.createVerticalStrut(12));

        // 2. Nút Chọn File & Tên File (Dùng FlowLayout để nút giữ đúng kích thước tự nhiên)
        JPanel fileRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        fileRow.setOpaque(false);
        fileRow.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        JButton selectBtn = new JButton("Chọn File...");
        selectBtn.setFont(UIStyles.FONT_BODY);
        selectBtn.setBackground(UIStyles.PRIMARY);
        selectBtn.setForeground(Color.WHITE);
        selectBtn.setFocusPainted(false);
        selectBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                chooseFile();
            }
        });

        fileLabel.setFont(UIStyles.FONT_BODY);
        fileLabel.setForeground(UIStyles.TEXT_MUTED);
        fileLabel.setBorder(new EmptyBorder(0, 12, 0, 0)); // Tạo khoảng cách với nút

        fileRow.add(selectBtn);
        fileRow.add(fileLabel);
        centerPanel.add(fileRow);

        centerPanel.add(javax.swing.Box.createVerticalStrut(12));

        // 3. Progress Bar
        progressBar.setStringPainted(true);
        progressBar.setFont(UIStyles.FONT_SMALL);
        progressBar.setForeground(UIStyles.SUCCESS);
        progressBar.setVisible(false);
        progressBar.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 20));
        progressBar.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        centerPanel.add(progressBar);

        contentPane.add(centerPanel, BorderLayout.CENTER);

        // --- KHU VỰC NÚT BẤM (Góc dưới cùng bên phải) ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        JButton importBtn = new JButton("Import");
        importBtn.setFont(UIStyles.FONT_BODY);
        importBtn.setBackground(UIStyles.SUCCESS);
        importBtn.setForeground(Color.WHITE);
        importBtn.setFocusPainted(false);
        importBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (selectedFile != null) {
                    progressBar.setVisible(true);
                    progressBar.setValue(100); 
                    confirmed = true;
                    dispose();
                } else {
                    javax.swing.JOptionPane.showMessageDialog(MajorImportExcelDialog.this, "Vui lòng chọn file!", "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
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

        btnPanel.add(importBtn);
        btnPanel.add(cancelBtn);
        contentPane.add(btnPanel, BorderLayout.SOUTH);
    }

    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx, *.xls)", "xlsx", "xls"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            fileLabel.setText(selectedFile.getName());
            fileLabel.setForeground(UIStyles.SUCCESS);
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public File getSelectedFile() {
        return selectedFile;
    }

    public void setProgress(int value) {
        progressBar.setValue(value);
    }
}