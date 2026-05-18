package com.tuyensinh.view;

import com.tuyensinh.model.User;
import com.tuyensinh.service.SessionManager;
import com.tuyensinh.service.DiemThiService;
import com.tuyensinh.service.DiemCongService;
import com.tuyensinh.service.TohopMonthiService;
import com.tuyensinh.model.DiemThi;
import com.tuyensinh.model.DiemCong;
import com.tuyensinh.model.TohopMonthi;

import javax.swing.*;
import java.util.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class NguyenVongFormDialog extends JDialog {
    private final JTextField cccdField = new JTextField(15);
    private final JTextField maNganhField = new JTextField(15);
    private final JTextField thuTuNvField = new JTextField(15);
	
	private final Map<String, String> phuongThuc = new HashMap<>();
	
    private final JComboBox<String> phuongThucCombo;
    
	private final JComboBox<String> thmCombo = new JComboBox<>(new String[]{
		"",
		"A00", "A01", "A02", "A03", "A04", "A05", "A06", "A07",
		"B00", "B01", "B02", "B03", "B08",
		"C00", "C01", "C02", "C03", "C04", "C05", "C06", "C07", "C08", "C09", "C10", "C11", "C12", "C13",
		"D01", "D07", "D09", "D10", "D11", "D12", "D13", "D14", "D15",
		"H00",
		"M01", "M02",
		"N01",
		"X01", "X02", "X03", "X04", "X05", "X06", "X07", "X08", "X09", "X10",
		"X11", "X12", "X13", "X14", "X15", "X16", "X17", "X18", "X19", "X20",
		"X21", "X22", "X23", "X24", "X25", "X26", "X27", "X28",
		"X53", "X54", "X55", "X56", "X57", "X58", "X59", "X60", "X61", "X62",
		"X63", "X64", "X65", "X66", "X67", "X68", "X69", "X70", "X71", "X72",
		"X73", "X74", "X75", "X76", "X77", "X78", "X79", "X80", "X81",
		"Y07", "Y08", "Y09", "Y10", "Y11"
	});


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
		
		User currentUser = SessionManager.getCurrentUser();

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
		
		phuongThuc.put("Xét tuyển thẳng","XTT");
		phuongThuc.put("Kỳ thi Đánh Giá Năng Lực","DGNL");
		phuongThuc.put("Kỳ thi V-SAT","V-SAT");
		phuongThuc.put("Kỳ thi tốt nghiệp THPT Quốc Gia","THPT");
		
		String[] keys = phuongThuc.keySet().toArray(new String[0]);

        phuongThucCombo = new JComboBox<>(keys);
		
        addFormField(formPanel, "Phương thức:", phuongThucCombo);

        addFormField(formPanel, "Mã Tổ hợp:", thmCombo);
        addFormField(formPanel, "Điểm THXT:", diemThxtField);

        addFormField(formPanel, "Điểm Ưu tiên QĐ:", diemUtqdField);
        addFormField(formPanel, "Điểm Cộng:", diemCongField);

        addFormField(formPanel, "Điểm Xét Tuyển:", diemXetTuyenField);
        addFormField(formPanel, "Kết Quả:", ketQuaCombo);

        addFormField(formPanel, "Keys (Tự động):", keysField);
        keysField.setEditable(false); // Keys thường tự sinh, không cho người dùng tự gõ
		
		// Khi thí sinh thêm nguyện vọng thì sẽ tự động hiển thị cccd của thí sinh.
		if(currentUser.getIdRoleValue() == 3){
			cccdField.setText(currentUser.getUsername());
			cccdField.setEditable(false);
            diemThxtField.setEditable(false);
            diemUtqdField.setEditable(false);
            diemCongField.setEditable(false);
            diemXetTuyenField.setEditable(false);
		}

        // Add listeners to trigger autoFillScores
        if (currentUser.getIdRoleValue() == 3) {
            javax.swing.event.DocumentListener docListener = new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) { autoFillScores(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { autoFillScores(); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) { autoFillScores(); }
            };
            cccdField.getDocument().addDocumentListener(docListener);
            thmCombo.addActionListener(e -> autoFillScores());
            phuongThucCombo.addActionListener(e -> autoFillScores());
        }

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
    public String getMaThm() { return (String) thmCombo.getSelectedItem(); }
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
        thmCombo.setSelectedItem(maThm);
    }

    private void autoFillScores() {
        String cccd = cccdField.getText().trim();
        String maThm = (String) thmCombo.getSelectedItem();
        String maNganh = maNganhField.getText().trim();
        String pThucDisplay = (String) phuongThucCombo.getSelectedItem();
        String pThuc = phuongThuc.get(pThucDisplay);

        BigDecimal diemThxt = BigDecimal.ZERO;
        BigDecimal diemUtqd = BigDecimal.ZERO;
        BigDecimal diemCong = BigDecimal.ZERO;

        if (!cccd.isEmpty() && pThuc != null) {
            DiemThiService diemThiService = new DiemThiService();
            List<DiemThi> diemThiList = diemThiService.getByCccd(cccd);
            DiemThi dt = null;
            for(DiemThi d : diemThiList) {
                if(pThuc.equals(d.getDPhuongthuc())) {
                    dt = d;
                    break;
                }
            }

            if (dt != null && maThm != null && !maThm.isEmpty()) {
                TohopMonthiService thmService = new TohopMonthiService();
                Optional<TohopMonthi> thmOpt = thmService.getByMaTohop(maThm);
                if (thmOpt.isPresent()) {
                    TohopMonthi thm = thmOpt.get();
                    diemThxt = getDiemMon(dt, thm.getMon1()).add(getDiemMon(dt, thm.getMon2())).add(getDiemMon(dt, thm.getMon3()));
                }
            }

            if (maThm != null && !maThm.isEmpty()) {
                DiemCongService diemCongService = new DiemCongService();
                List<DiemCong> diemCongList = diemCongService.getByCccd(cccd);
                DiemCong dc = null;
                for(DiemCong d : diemCongList) {
                    if(maThm.equals(d.getMatohop()) && pThuc.equals(d.getPhuongthuc())) {
                        dc = d;
                        break;
                    }
                }

                if (dc != null) {
                    diemUtqd = dc.getDiemUtxt() != null ? dc.getDiemUtxt() : BigDecimal.ZERO;
                    diemCong = dc.getDiemCC() != null ? dc.getDiemCC() : BigDecimal.ZERO;
                }
            }
        }

        BigDecimal diemXetTuyen = diemThxt.add(diemUtqd).add(diemCong);

        diemThxtField.setText(diemThxt.toString());
        diemUtqdField.setText(diemUtqd.toString());
        diemCongField.setText(diemCong.toString());
        diemXetTuyenField.setText(diemXetTuyen.toString());
    }

    private BigDecimal getDiemMon(DiemThi dt, String mon) {
        if (mon == null || dt == null) return BigDecimal.ZERO;
        switch(mon.toUpperCase()) {
            case "TO": return dt.getToan() != null ? dt.getToan() : BigDecimal.ZERO;
            case "LI": case "LY": return dt.getVatLi() != null ? dt.getVatLi() : BigDecimal.ZERO;
            case "HO": return dt.getHoaHoc() != null ? dt.getHoaHoc() : BigDecimal.ZERO;
            case "SI": return dt.getSinhHoc() != null ? dt.getSinhHoc() : BigDecimal.ZERO;
            case "SU": return dt.getLichSu() != null ? dt.getLichSu() : BigDecimal.ZERO;
            case "DI": return dt.getDiaLi() != null ? dt.getDiaLi() : BigDecimal.ZERO;
            case "VA": return dt.getNguVan() != null ? dt.getNguVan() : BigDecimal.ZERO;
            case "N1_THI": return dt.getN1Thi() != null ? dt.getN1Thi() : BigDecimal.ZERO;
            case "N1_CC": return dt.getN1Cc() != null ? dt.getN1Cc() : BigDecimal.ZERO;
            case "CNCN": return dt.getCncn() != null ? dt.getCncn() : BigDecimal.ZERO;
            case "CNNN": return dt.getCnnn() != null ? dt.getCnnn() : BigDecimal.ZERO;
            case "TI": return dt.getTinHoc() != null ? dt.getTinHoc() : BigDecimal.ZERO;
            case "KTPL": return dt.getKtpl() != null ? dt.getKtpl() : BigDecimal.ZERO;
            case "NL1": return dt.getNl1() != null ? dt.getNl1() : BigDecimal.ZERO;
            case "NK1": return dt.getNk1() != null ? dt.getNk1() : BigDecimal.ZERO;
            case "NK2": return dt.getNk2() != null ? dt.getNk2() : BigDecimal.ZERO;
            default: return BigDecimal.ZERO;
        }
    }
}