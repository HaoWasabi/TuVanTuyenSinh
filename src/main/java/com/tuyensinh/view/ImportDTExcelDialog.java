package com.tuyensinh.view;

import javax.swing.BorderFactory;
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
 * Dialog cho import danh sách điểm thi từ file Excel
 */
public class ImportDTExcelDialog extends JDialog {
    private File selectedFile;
    private boolean confirmed = false;
    private final JLabel fileLabel = new JLabel("Chưa chọn file");
    private final JProgressBar progressBar = new JProgressBar();

    public ImportDTExcelDialog(Frame owner) {
        super(owner, "Import Điểm Thi Thí Sinh", true);
        setSize(500, 250);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout(0, 12));
        contentPane.setBorder(new EmptyBorder(16, 16, 16, 16));
        contentPane.setBackground(UIStyles.BG_CARD);
        setContentPane(contentPane);

        // Info panel đã được cập nhật lại tên các cột giống với bảng trong DiemThiPanel
        JLabel infoLabel = new JLabel("<html><b>Import danh sách điểm thi từ file Excel</b><br/>" +
                "Định dạng: .xlsx hoặc .xls<br/>" +
                "Cột dữ liệu: ID, CCCD, SBD, Phương thức, Toán, Lý, Hóa, Sinh, Sử, Địa, Văn, " +
                "N1_Thi, N1_CC, CNCN, CNNN, Tin Học, KTPL, NL1, NK1, NK2</html>");
        infoLabel.setFont(UIStyles.FONT_BODY);
        infoLabel.setForeground(UIStyles.TEXT_DARK);

        // File selection
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filePanel.setOpaque(false);

        fileLabel.setFont(UIStyles.FONT_BODY);
        fileLabel.setForeground(UIStyles.TEXT_MUTED);

        JButton browseBtn = new JButton("Chọn File");
        browseBtn.setFont(UIStyles.FONT_BODY);
        browseBtn.setBackground(UIStyles.PRIMARY);
        browseBtn.setForeground(Color.WHITE);
        browseBtn.setFocusPainted(false);
        browseBtn.addActionListener(e -> chooseFile());

        filePanel.add(browseBtn);
        filePanel.add(fileLabel);

        // Progress
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);

        // Center
        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);
        center.add(infoLabel, BorderLayout.NORTH);
        center.add(filePanel, BorderLayout.CENTER);
        center.add(progressBar, BorderLayout.SOUTH);

        contentPane.add(center, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        JButton importBtn = new JButton("Import");
        importBtn.setFont(UIStyles.FONT_BODY);
        importBtn.setBackground(UIStyles.SUCCESS);
        importBtn.setForeground(Color.WHITE);
        importBtn.setFocusPainted(false);
        importBtn.addActionListener(e -> {
            if (selectedFile != null) {
                confirmed = true;
                dispose();
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng chọn file!", "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelBtn = new JButton("Hủy");
        cancelBtn.setFont(UIStyles.FONT_BODY);
        cancelBtn.setBackground(UIStyles.TEXT_MUTED);
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(e -> dispose());

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

    public void showProgress(boolean show) {
        progressBar.setVisible(show);
    }
}