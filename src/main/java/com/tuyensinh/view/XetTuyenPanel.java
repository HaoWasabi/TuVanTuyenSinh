package com.tuyensinh.view;

import com.tuyensinh.model.Nganh;
import com.tuyensinh.service.NganhService;
import com.tuyensinh.service.XetTuyenService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class XetTuyenPanel extends JPanel {

    private final XetTuyenService xetTuyenService = new XetTuyenService();
    private final NganhService nganhService = new NganhService();

    private JComboBox<String> phamViCombo;
    private JComboBox<String> nganhCombo;
    private JCheckBox uuTienNVCheck;
    private JCheckBox motNVCheck;
    private JButton validateBtn;
    private JButton runBtn;
    private JButton rollbackBtn;
    private JButton lockBtn;

    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JLabel timeLabel;
    private JLabel lblProcessed;
    private JLabel lblCandidates;
    private JLabel lblTrungTuyen;
    private JLabel lblTruot;
    private JLabel roundLabel;
    private JLabel quotaStatusLabel;

    private DefaultTableModel logTableModel;
    private JTable logTable;
    private JComboBox<String> logFilterCombo;

    private Timer progressTimer;
    private long startTimeMillis;

    public XetTuyenPanel() {
        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(UIStyles.BG_APP);

        JLabel title = new JLabel("Thực hiện xét tuyển");
        title.setFont(UIStyles.FONT_TITLE);
        title.setForeground(UIStyles.TEXT_DARK);
        add(title, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, createConfigPanel(), createBottomSplit());
        splitPane.setResizeWeight(0.35);
        splitPane.setDividerSize(6);
        splitPane.setBorder(null);
        add(splitPane, BorderLayout.CENTER);

        updateRoundInfo();
    }

    private JPanel createConfigPanel() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(UIStyles.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JPanel headerPanel = new JPanel(new BorderLayout(8, 4));
        headerPanel.setOpaque(false);

        JLabel cardTitle = new JLabel("Cấu hình xét tuyển");
        cardTitle.setFont(UIStyles.FONT_SUBTITLE);
        cardTitle.setForeground(UIStyles.TEXT_DARK);
        headerPanel.add(cardTitle, BorderLayout.WEST);

        roundLabel = new JLabel("");
        roundLabel.setFont(UIStyles.FONT_BODY);
        roundLabel.setForeground(UIStyles.TEXT_DARK);
        headerPanel.add(roundLabel, BorderLayout.EAST);

        quotaStatusLabel = new JLabel("");
        quotaStatusLabel.setFont(UIStyles.FONT_SMALL);
        quotaStatusLabel.setForeground(UIStyles.TEXT_MUTED);

        JPanel northPanel = new JPanel(new BorderLayout(0, 4));
        northPanel.setOpaque(false);
        northPanel.add(headerPanel, BorderLayout.NORTH);
        northPanel.add(quotaStatusLabel, BorderLayout.SOUTH);

        card.add(northPanel, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 2, 12, 12));
        form.setOpaque(false);

        form.add(createLabeledPanel("Phạm vi xét tuyển:", createPhamViPanel()));
        form.add(createLabeledPanel("Ngành áp dụng:", createNganhPanel()));
        form.add(createLabeledPanel("Quy tắc xét tuyển:", createRulesPanel()));

        card.add(form, BorderLayout.CENTER);
        card.add(createActionButtons(), BorderLayout.SOUTH);
        return card;
    }

    private JPanel createLabeledPanel(String labelText, JPanel component) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(UIStyles.FONT_LABEL);
        label.setForeground(UIStyles.TEXT_DARK);
        panel.add(label, BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPhamViPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setOpaque(false);
        phamViCombo = new JComboBox<>(new String[]{"Toàn trường", "Theo ngành"});
        phamViCombo.setFont(UIStyles.FONT_BODY);
        phamViCombo.addActionListener(e -> updateScopeFields());
        panel.add(phamViCombo);
        return panel;
    }

    private JPanel createNganhPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setOpaque(false);
        nganhCombo = new JComboBox<>();
        nganhCombo.setFont(UIStyles.FONT_BODY);
        nganhCombo.setEnabled(false);
        loadNganhCombo();
        panel.add(nganhCombo);
        return panel;
    }

    private JPanel createRulesPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 6));
        panel.setOpaque(false);
        uuTienNVCheck = new JCheckBox("Ưu tiên nguyện vọng theo thứ tự (NV1 -> NV2 -> ...)");
        uuTienNVCheck.setFont(UIStyles.FONT_BODY);
        uuTienNVCheck.setSelected(true);
        motNVCheck = new JCheckBox("Mỗi thí sinh chỉ trúng tuyển 1 nguyện vọng cao nhất");
        motNVCheck.setFont(UIStyles.FONT_BODY);
        motNVCheck.setSelected(true);
        panel.add(uuTienNVCheck);
        panel.add(motNVCheck);
        return panel;
    }

    private JPanel createActionButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        panel.setOpaque(false);

        validateBtn = createButton("Kiểm tra dữ liệu", UIStyles.INFO);
        validateBtn.addActionListener(e -> handleValidate());

        runBtn = createButton("Chạy xét tuyển", UIStyles.SUCCESS);
        runBtn.addActionListener(e -> handleRun());

        rollbackBtn = createButton("Rollback", UIStyles.WARNING);
        rollbackBtn.addActionListener(e -> handleRollback());
        rollbackBtn.setEnabled(false);

        lockBtn = createButton("Khóa kết quả", UIStyles.PRIMARY);
        lockBtn.addActionListener(e -> handleLock());
        lockBtn.setEnabled(false);

        panel.add(validateBtn);
        panel.add(runBtn);
        panel.add(rollbackBtn);
        panel.add(lockBtn);
        return panel;
    }

    private JSplitPane createBottomSplit() {
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, createProgressPanel(), createLogPanel());
        split.setResizeWeight(0.45);
        split.setDividerSize(6);
        split.setBorder(null);
        return split;
    }

    private JPanel createProgressPanel() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(UIStyles.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel cardTitle = new JLabel("Tiến trình xét tuyển");
        cardTitle.setFont(UIStyles.FONT_SUBTITLE);
        cardTitle.setForeground(UIStyles.TEXT_DARK);
        card.add(cardTitle, BorderLayout.NORTH);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("Chưa chạy");
        progressBar.setPreferredSize(new Dimension(0, 28));
        card.add(progressBar, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new GridLayout(2, 3, 12, 8));
        infoPanel.setOpaque(false);

        statusLabel = createInfoLabel("Trạng thái: Sẵn sàng");
        timeLabel = createInfoLabel("Thời gian: -");
        lblProcessed = createInfoLabel("Đã xử lý: 0");
        lblCandidates = createInfoLabel("Thí sinh: 0");
        lblTrungTuyen = createInfoLabel("Trúng tuyển: 0");
        lblTruot = createInfoLabel("Trượt: 0");

        infoPanel.add(statusLabel);
        infoPanel.add(timeLabel);
        infoPanel.add(lblProcessed);
        infoPanel.add(lblCandidates);
        infoPanel.add(lblTrungTuyen);
        infoPanel.add(lblTruot);

        card.add(infoPanel, BorderLayout.CENTER);
        return card;
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIStyles.FONT_SMALL);
        label.setForeground(UIStyles.TEXT_DARK);
        return label;
    }

    private JPanel createLogPanel() {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(UIStyles.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel cardTitle = new JLabel("Log xét tuyển");
        cardTitle.setFont(UIStyles.FONT_SUBTITLE);
        cardTitle.setForeground(UIStyles.TEXT_DARK);
        header.add(cardTitle, BorderLayout.WEST);

        logFilterCombo = new JComboBox<>(new String[]{"Tất cả", "INFO", "WARN", "ERROR"});
        logFilterCombo.setFont(UIStyles.FONT_SMALL);
        logFilterCombo.addActionListener(e -> filterLogs());
        header.add(logFilterCombo, BorderLayout.EAST);

        card.add(header, BorderLayout.NORTH);

        String[] logCols = {"Thời gian", "Mức độ", "Nội dung"};
        logTableModel = new DefaultTableModel(null, logCols) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        logTable = new JTable(logTableModel);
        logTable.setRowHeight(26);
        logTable.getTableHeader().setFont(UIStyles.FONT_LABEL);
        logTable.getTableHeader().setBackground(new Color(247, 249, 251));
        logTable.setFont(UIStyles.FONT_SMALL);
        logTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        logTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        logTable.getColumnModel().getColumn(1).setPreferredWidth(70);
        logTable.getColumnModel().getColumn(2).setPreferredWidth(500);

        card.add(new JScrollPane(logTable), BorderLayout.CENTER);
        return card;
    }

    private void loadNganhCombo() {
        nganhCombo.removeAllItems();
        nganhCombo.addItem("Tất cả");
        List<Nganh> nganhList = nganhService.getAll();
        for (Nganh nganh : nganhList) {
            nganhCombo.addItem(nganh.getManganh() + " - " + nganh.getTennganh());
        }
    }

    private void updateScopeFields() {
        int idx = phamViCombo.getSelectedIndex();
        nganhCombo.setEnabled(idx == 1);
    }

    private void handleValidate() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        validateBtn.setEnabled(false);

        SwingWorker<java.util.List<XetTuyenService.ValidationResult>, Void> worker = new SwingWorker<>() {
            @Override
            protected java.util.List<XetTuyenService.ValidationResult> doInBackground() {
                return xetTuyenService.validateData();
            }

            @Override
            protected void done() {
                validateBtn.setEnabled(true);
                setCursor(Cursor.getDefaultCursor());
                try {
                    java.util.List<XetTuyenService.ValidationResult> results = get();
                    logTableModel.setRowCount(0);
                    for (XetTuyenService.ValidationResult r : results) {
                        logTableModel.addRow(new Object[]{
                                java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")),
                                r.getLevel().name(),
                                r.getMessage()
                        });
                    }
                    long errors = results.stream().filter(r -> r.getLevel() == XetTuyenService.ValidationLevel.ERROR).count();
                    if (errors > 0) {
                        JOptionPane.showMessageDialog(XetTuyenPanel.this,
                                "Phát hiện " + errors + " lỗi nghiêm trọng. Vui lòng sửa trước khi chạy xét tuyển.",
                                "Kết quả kiểm tra", JOptionPane.WARNING_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(XetTuyenPanel.this,
                                "Không có lỗi nghiêm trọng. Có thể chạy xét tuyển.",
                                "Kết quả kiểm tra", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(XetTuyenPanel.this,
                            "Lỗi kiểm tra: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void handleRun() {
        if (!xetTuyenService.isLocked()) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn chạy xét tuyển?\nQuá trình này sẽ cập nhật trạng thái tất cả nguyện vọng.",
                    "Xác nhận chạy xét tuyển", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;
        }

        XetTuyenService.XetTuyenConfig config = new XetTuyenService.XetTuyenConfig();

        int phamViIdx = phamViCombo.getSelectedIndex();
        if (phamViIdx == 1) {
            config.setPhamVi(XetTuyenService.XetTuyenConfig.PhamVi.THEO_NGANH);
            String selected = (String) nganhCombo.getSelectedItem();
            if (selected != null && !selected.equals("Tất cả")) {
                String maNganh = selected.split(" - ")[0];
                config.setNganhApDung(maNganh);
            }
        }

        config.setUuTienThuTuNV(uuTienNVCheck.isSelected());
        config.setMotNguyenVongDuyNhat(motNVCheck.isSelected());

        runBtn.setEnabled(false);
        validateBtn.setEnabled(false);
        progressBar.setValue(0);
        progressBar.setString("Đang chạy...");
        statusLabel.setText("Trạng thái: Đang chạy...");
        startTimeMillis = System.currentTimeMillis();

        java.util.concurrent.CompletableFuture<XetTuyenService.XetTuyenResult> future =
                xetTuyenService.runXetTuyenAsync(config);

        progressTimer = new Timer(500, e -> {
            XetTuyenService.XetTuyenJob job = xetTuyenService.getCurrentJob();
            if (job != null) {
                progressBar.setValue(job.getProgress());
                progressBar.setString(job.getStatus());
                statusLabel.setText("Trạng thái: " + job.getStatus());
                long elapsed = (System.currentTimeMillis() - startTimeMillis) / 1000;
                timeLabel.setText("Thời gian: " + elapsed + "s");

                logTableModel.setRowCount(0);
                String filter = (String) logFilterCombo.getSelectedItem();
                for (XetTuyenService.XetTuyenLogEntry log : xetTuyenService.getLogs()) {
                    if (filter.equals("Tất cả") || filter.equals(log.getLevel().name())) {
                        logTableModel.addRow(new Object[]{
                                log.getFormattedTime(),
                                log.getLevel().name(),
                                log.getMessage()
                        });
                    }
                }
            }
        });
        progressTimer.start();

        future.whenComplete((result, ex) -> {
            progressTimer.stop();
            SwingUtilities.invokeLater(() -> {
                runBtn.setEnabled(true);
                validateBtn.setEnabled(true);
                rollbackBtn.setEnabled(true);
                lockBtn.setEnabled(true);

                if (ex != null) {
                    progressBar.setString("Lỗi!");
                    progressBar.setForeground(UIStyles.DANGER);
                    statusLabel.setText("Trạng thái: Lỗi - " + ex.getMessage());
                    JOptionPane.showMessageDialog(this,
                            "Lỗi xét tuyển: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                } else if (result.isSuccess()) {
                    progressBar.setForeground(UIStyles.SUCCESS);
                    progressBar.setString("Hoàn tất!");
                    statusLabel.setText("Trạng thái: Hoàn tất");
                    lblProcessed.setText("Đã xử lý: " + result.getTotalProcessed());
                    lblCandidates.setText("Thí sinh: " + result.getTotalCandidates());
                    lblTrungTuyen.setText("Trúng tuyển: " + result.getTotalTrungTuyen());
                    lblTruot.setText("Trượt: " + result.getTotalTruot());
                    updateRoundInfo();

                    JOptionPane.showMessageDialog(this,
                            "Xét tuyển hoàn tất!\n" +
                                    "Tổng xử lý: " + result.getTotalProcessed() + "\n" +
                                    "Thí sinh: " + result.getTotalCandidates() + "\n" +
                                    "Trúng tuyển: " + result.getTotalTrungTuyen() + "\n" +
                                    "Trượt: " + result.getTotalTruot(),
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else if (result.isCancelled()) {
                    progressBar.setForeground(UIStyles.WARNING);
                    progressBar.setString("Đã hủy");
                    statusLabel.setText("Trạng thái: Đã hủy");
                } else {
                    progressBar.setForeground(UIStyles.DANGER);
                    progressBar.setString("Thất bại");
                    statusLabel.setText("Trạng thái: " + result.getErrorMessage());
                    JOptionPane.showMessageDialog(this,
                            "Xét tuyển thất bại: " + result.getErrorMessage(),
                            "Thất bại", JOptionPane.ERROR_MESSAGE);
                }
            });
        });
    }

    private void handleRollback() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn rollback?\nTất cả kết quả xét tuyển sẽ được đặt lại về CHUA_XET.",
                "Xác nhận rollback", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        boolean success = xetTuyenService.rollbackXetTuyen();
        setCursor(Cursor.getDefaultCursor());

        if (success) {
            progressBar.setValue(0);
            progressBar.setString("Đã rollback");
            progressBar.setForeground(UIStyles.WARNING);
            statusLabel.setText("Trạng thái: Đã rollback");
            lockBtn.setEnabled(false);
            rollbackBtn.setEnabled(false);
            lblProcessed.setText("Đã xử lý: 0");
            lblCandidates.setText("Thí sinh: 0");
            lblTrungTuyen.setText("Trúng tuyển: 0");
            lblTruot.setText("Trượt: 0");
            updateRoundInfo();
            JOptionPane.showMessageDialog(this, "Đã rollback thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Rollback thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleLock() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn khóa kết quả?\nSau khi khóa, không thể rollback.",
                "Xác nhận khóa kết quả", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        xetTuyenService.lockKetQua();
        lockBtn.setEnabled(false);
        rollbackBtn.setEnabled(false);
        runBtn.setEnabled(false);
        statusLabel.setText("Trạng thái: Đã khóa kết quả");
        JOptionPane.showMessageDialog(this, "Đã khóa kết quả xét tuyển!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void filterLogs() {
        logTableModel.setRowCount(0);
        String filter = (String) logFilterCombo.getSelectedItem();
        for (XetTuyenService.XetTuyenLogEntry log : xetTuyenService.getLogs()) {
            if (filter.equals("Tất cả") || filter.equals(log.getLevel().name())) {
                logTableModel.addRow(new Object[]{
                        log.getFormattedTime(),
                        log.getLevel().name(),
                        log.getMessage()
                });
            }
        }
    }

    private void updateRoundInfo() {
        int round = xetTuyenService.getCurrentRound();
        if (round == 0) {
            roundLabel.setText("Lần xét tuyển thứ 1");
            quotaStatusLabel.setText("");
            runBtn.setEnabled(true);
            rollbackBtn.setEnabled(false);
            lockBtn.setEnabled(false);
        } else {
            roundLabel.setText("Lần xét tuyển thứ " + (round + 1));
            boolean full = xetTuyenService.isFullQuota();
            if (full) {
                quotaStatusLabel.setText("✓ Tất cả ngành đã đủ chỉ tiêu");
                quotaStatusLabel.setForeground(UIStyles.SUCCESS);
                runBtn.setEnabled(false);
                rollbackBtn.setEnabled(true);
            } else {
                quotaStatusLabel.setText("! Một số ngành chưa đủ chỉ tiêu. Có thể xét lần " + (round + 1));
                quotaStatusLabel.setForeground(UIStyles.WARNING);
                runBtn.setEnabled(true);
                rollbackBtn.setEnabled(true);
            }
        }
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(UIStyles.FONT_BODY);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
