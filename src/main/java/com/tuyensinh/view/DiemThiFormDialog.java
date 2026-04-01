package com.tuyensinh.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;

public class DiemThiFormDialog extends JDialog {
    private final JTextField cccdField = new JTextField();
    private final JTextField soBaoDanhField = new JTextField();
    private final JTextField phuongThucField = new JTextField();

    // Các môn học cơ bản & chuyên ngành
    private final JTextField toanField = new JTextField("0.0");
    private final JTextField vanField = new JTextField("0.0");
    private final JTextField ngoaiNguThiField = new JTextField("0.0"); // N1_THI
    private final JTextField ngoaiNguCcField = new JTextField("0.0");  // N1_CC
    private final JTextField lyField = new JTextField("0.0");
    private final JTextField hoaField = new JTextField("0.0");
    private final JTextField sinhField = new JTextField("0.0");
    private final JTextField suField = new JTextField("0.0");
    private final JTextField diaField = new JTextField("0.0");
    private final JTextField ktplField = new JTextField("0.0");
    private final JTextField tinHocField = new JTextField("0.0");

    // Các điểm đặc thù
    private final JTextField cncnField = new JTextField("0.0");
    private final JTextField cnnnField = new JTextField("0.0");
    private final JTextField nl1Field = new JTextField("0.0"); // Năng lực 1
    private final JTextField nk1Field = new JTextField("0.0"); // Năng khiếu 1
    private final JTextField nk2Field = new JTextField("0.0"); // Năng khiếu 2

    private boolean confirmed = false;

    public DiemThiFormDialog(Frame owner, String title, boolean isEditing) {
        super(owner, title, true);
        setSize(550, 650); // Tăng chiều cao để chứa nhiều form hơn
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout(0, 12));
        contentPane.setBorder(new EmptyBorder(16, 16, 16, 16));
        contentPane.setBackground(UIStyles.BG_CARD);
        setContentPane(contentPane);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 12, 12));
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(5, 5, 5, 15));

        // Thông tin cơ bản
        addFormField(formPanel, "CCCD *:", cccdField);
        addFormField(formPanel, "Số báo danh:", soBaoDanhField);
        addFormField(formPanel, "Phương thức:", phuongThucField);

        // Điểm các môn
        addFormField(formPanel, "Điểm Toán:", toanField);
        addFormField(formPanel, "Điểm Ngữ Văn:", vanField);
        addFormField(formPanel, "Điểm N1 (Thi):", ngoaiNguThiField);
        addFormField(formPanel, "Điểm N1 (Quy đổi CC):", ngoaiNguCcField);
        addFormField(formPanel, "Điểm Vật Lý:", lyField);
        addFormField(formPanel, "Điểm Hóa Học:", hoaField);
        addFormField(formPanel, "Điểm Sinh Học:", sinhField);
        addFormField(formPanel, "Điểm Lịch Sử:", suField);
        addFormField(formPanel, "Điểm Địa Lý:", diaField);
        addFormField(formPanel, "Điểm KTPL:", ktplField);
        addFormField(formPanel, "Điểm Tin Học:", tinHocField);
        addFormField(formPanel, "Điểm CNCN:", cncnField);
        addFormField(formPanel, "Điểm CNNN:", cnnnField);

        // Điểm đánh giá năng lực & năng khiếu
        addFormField(formPanel, "Điểm ĐGNL 1:", nl1Field);
        addFormField(formPanel, "Điểm Năng khiếu 1:", nk1Field);
        addFormField(formPanel, "Điểm Năng khiếu 2:", nk2Field);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIStyles.BORDER));
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // Nút Lưu và Hủy
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        JButton saveBtn = new JButton("Lưu Điểm Thi");
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
        }
    }

    private void addFormField(JPanel panel, String label, JComponent component) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIStyles.FONT_LABEL);
        panel.add(lbl);
        panel.add(component);
    }

    private boolean validateForm() {
        if (cccdField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập CCCD!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Mảng TẤT CẢ các ô điểm để kiểm tra
        JTextField[] scoreFields = {
                toanField, vanField, ngoaiNguThiField, ngoaiNguCcField, lyField, hoaField,
                sinhField, suField, diaField, ktplField, tinHocField,
                cncnField, cnnnField, nl1Field, nk1Field, nk2Field
        };

        try {
            for (JTextField field : scoreFields) {
                if (!field.getText().trim().isEmpty()) {
                    BigDecimal score = new BigDecimal(field.getText().trim());
                    // Nếu trường bạn có thang điểm ĐGNL khác 10 (ví dụ 1200), bạn cần bọc logic riêng cho ô nl1Field
                    if (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(BigDecimal.TEN) > 0 && field != nl1Field) {
                        JOptionPane.showMessageDialog(this, "Điểm số các môn cơ bản phải nằm trong khoảng từ 0.0 đến 10.0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập điểm dưới dạng số (ví dụ: 8.5)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public boolean isConfirmed() { return confirmed; }

    // Getters
    public String getCccd() { return cccdField.getText().trim(); }
    public String getSoBaoDanh() { return soBaoDanhField.getText().trim(); }
    public String getDPhuongThuc() { return phuongThucField.getText().trim(); }
    public BigDecimal getToan() { return new BigDecimal(toanField.getText().trim()); }
    public BigDecimal getVan() { return new BigDecimal(vanField.getText().trim()); }
    public BigDecimal getN1Thi() { return new BigDecimal(ngoaiNguThiField.getText().trim()); }
    public BigDecimal getN1Cc() { return new BigDecimal(ngoaiNguCcField.getText().trim()); }
    public BigDecimal getLy() { return new BigDecimal(lyField.getText().trim()); }
    public BigDecimal getHoa() { return new BigDecimal(hoaField.getText().trim()); }
    public BigDecimal getSinh() { return new BigDecimal(sinhField.getText().trim()); }
    public BigDecimal getSu() { return new BigDecimal(suField.getText().trim()); }
    public BigDecimal getDia() { return new BigDecimal(diaField.getText().trim()); }
    public BigDecimal getKtpl() { return new BigDecimal(ktplField.getText().trim()); }
    public BigDecimal getTinHoc() { return new BigDecimal(tinHocField.getText().trim()); }
    public BigDecimal getCncn() { return new BigDecimal(cncnField.getText().trim()); }
    public BigDecimal getCnnn() { return new BigDecimal(cnnnField.getText().trim()); }
    public BigDecimal getNl1() { return new BigDecimal(nl1Field.getText().trim()); }
    public BigDecimal getNk1() { return new BigDecimal(nk1Field.getText().trim()); }
    public BigDecimal getNk2() { return new BigDecimal(nk2Field.getText().trim()); }

    // Set Data cho chế độ Edit
    public void setData(String cccd, String soBaoDanh, String dPhuongThuc, BigDecimal toan, BigDecimal van,
                        BigDecimal n1Thi, BigDecimal n1Cc, BigDecimal ly, BigDecimal hoa, BigDecimal sinh,
                        BigDecimal su, BigDecimal dia, BigDecimal ktpl, BigDecimal tinHoc,
                        BigDecimal cncn, BigDecimal cnnn, BigDecimal nl1, BigDecimal nk1, BigDecimal nk2) {
        cccdField.setText(cccd);
        soBaoDanhField.setText(soBaoDanh);
        phuongThucField.setText(dPhuongThuc);
        toanField.setText(String.valueOf(toan));
        vanField.setText(String.valueOf(van));
        ngoaiNguThiField.setText(String.valueOf(n1Thi));
        ngoaiNguCcField.setText(String.valueOf(n1Cc));
        lyField.setText(String.valueOf(ly));
        hoaField.setText(String.valueOf(hoa));
        sinhField.setText(String.valueOf(sinh));
        suField.setText(String.valueOf(su));
        diaField.setText(String.valueOf(dia));
        ktplField.setText(String.valueOf(ktpl));
        tinHocField.setText(String.valueOf(tinHoc));
        cncnField.setText(String.valueOf(cncn));
        cnnnField.setText(String.valueOf(cnnn));
        nl1Field.setText(String.valueOf(nl1));
        nk1Field.setText(String.valueOf(nk1));
        nk2Field.setText(String.valueOf(nk2));
    }
}