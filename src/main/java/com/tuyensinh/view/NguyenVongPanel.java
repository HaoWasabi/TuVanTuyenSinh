package com.tuyensinh.view;

import com.tuyensinh.model.NguyenVong;
import com.tuyensinh.service.NguyenVongService;
import com.tuyensinh.service.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class NguyenVongPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTable table;
    private final JTextField detailIdField = new JTextField();
    private final JTextField detailCccdField = new JTextField();
    private final JTextField detailNganhField = new JTextField();
    private final JTextField detailOrderField = new JTextField();
    private final JTextField detailMethodField = new JTextField();
    private final JTextField detailToHopField = new JTextField();
    private final JTextField detailScoreField = new JTextField();
    private final JTextField detailResultField = new JTextField();
    private final JTextField detailKeyField = new JTextField();
    private final JLabel selectedLabel = new JLabel("Chưa chọn bản ghi");

    private List<NguyenVong> currentDataList = new java.util.ArrayList<>();

    // GỌI SERVICE Ở ĐÂY
    private final NguyenVongService nguyenVongService = new NguyenVongService();

    public NguyenVongPanel() {
        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(UIStyles.BG_APP);

        // Title
        JLabel title = new JLabel("Quản Lý Nguyện Vọng");
        title.setFont(UIStyles.FONT_TITLE);
        title.setForeground(UIStyles.TEXT_DARK);
        add(title, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createListCard(), createDetailCard());
        splitPane.setResizeWeight(0.64);
        splitPane.setDividerSize(8);
        splitPane.setDividerLocation(0.64);
        splitPane.setBorder(null);
        add(splitPane, BorderLayout.CENTER);

        // TỰ ĐỘNG LOAD DỮ LIỆU KHI MỞ PANEL
        loadDataToTable();
    }

    private JPanel createListCard() {

        // Search & Actions Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);

        JTextField searchInput = new JTextField(28);
        String placeholderText = "Tìm CCCD, mã ngành...";
        searchInput.setText(placeholderText);
        searchInput.setFont(UIStyles.FONT_BODY);
        searchInput.setForeground(UIStyles.TEXT_MUTED);
        searchInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(6, 10, 6, 10)
        ));
        applySearchPlaceholder(searchInput, placeholderText);

        JButton searchBtn = createButton("Tìm kiếm", UIStyles.PRIMARY);
        searchBtn.addActionListener(e -> handleSearch(searchInput.getText()));
        JButton refreshBtn = createButton("Làm mới", UIStyles.INFO);
        refreshBtn.addActionListener(e -> handleRefresh());

        toolbar.add(searchInput);
        toolbar.add(searchBtn);
        toolbar.add(new JSeparator(JSeparator.VERTICAL));

        // Cấu hình Cột cho Bảng Điểm Cộng
        String[] cols = {
                "ID", "CCCD", "Mã Ngành", "Thứ tự NV", "Điểm THXT", "Điểm UTQĐ",
                "Điểm Cộng", "Điểm Xét Tuyển", "Kết Quả", "Keys", "Phương thức", "Mã THM"
        };

        tableModel = new DefaultTableModel(null, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho sửa trực tiếp trên ô
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.getTableHeader().setFont(UIStyles.FONT_LABEL);
        table.getTableHeader().setBackground(new Color(247, 249, 251));
        table.setFont(UIStyles.FONT_BODY);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Có thanh cuộn ngang vì rất nhiều cột
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateDetailFromSelection();
            }
        });

        // Chỉnh độ rộng một số cột quan trọng
        table.getColumnModel().getColumn(0).setPreferredWidth(50); // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(120); // CCCD
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Mã ngành

        JPanel tableCard = new JPanel(new BorderLayout(0, 12));
        tableCard.setBackground(UIStyles.BG_CARD);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel tableTitle = new JLabel("Danh sách nguyện vọng thí sinh");
        tableTitle.setFont(UIStyles.FONT_SUBTITLE);
        tableTitle.setForeground(UIStyles.TEXT_DARK);

        JPanel listHeader = new JPanel(new BorderLayout());
        listHeader.setOpaque(false);
        listHeader.add(tableTitle, BorderLayout.WEST);
        JPanel listActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        listActions.setOpaque(false);
        listActions.add(refreshBtn);
        listHeader.add(listActions, BorderLayout.EAST);
        tableCard.add(listHeader, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(table);
        tableCard.add(scrollPane, BorderLayout.CENTER);


        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BorderLayout(0, 12));
        center.add(toolbar, BorderLayout.NORTH);
        center.add(tableCard, BorderLayout.CENTER);
        return center;
    }

    private JPanel createDetailCard() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(UIStyles.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JPanel header = new JPanel(new BorderLayout(0, 6));
        header.setOpaque(false);
        JLabel title = new JLabel("Chi tiết nguyện vọng");
        title.setFont(UIStyles.FONT_SUBTITLE);
        title.setForeground(UIStyles.TEXT_DARK);
        header.add(title, BorderLayout.WEST);

        JPanel rightHeader = new JPanel(new BorderLayout(0, 6));
        rightHeader.setOpaque(false);
        selectedLabel.setFont(UIStyles.FONT_SMALL);
        selectedLabel.setForeground(UIStyles.TEXT_MUTED);
        rightHeader.add(selectedLabel, BorderLayout.NORTH);
        rightHeader.add(createDetailActions(), BorderLayout.SOUTH);
        header.add(rightHeader, BorderLayout.EAST);

        JPanel fields = new JPanel(new GridLayout(0, 1, 0, 8));
        fields.setOpaque(false);
        fields.add(labelWithField("ID nguyện vọng", detailIdField));
        fields.add(labelWithField("CCCD", detailCccdField));
        fields.add(labelWithField("Mã ngành", detailNganhField));
        fields.add(labelWithField("Thứ tự nguyện vọng", detailOrderField));
        fields.add(labelWithField("Phương thức xét tuyển", detailMethodField));
        fields.add(labelWithField("Mã tổ hợp môn", detailToHopField));
        fields.add(labelWithField("Điểm (THXT/UTQĐ/Cộng/Xét tuyển)", detailScoreField));
        fields.add(labelWithField("Kết quả", detailResultField));
        fields.add(labelWithField("Khóa dữ liệu", detailKeyField));

        JScrollPane fieldsScroll = new JScrollPane(fields);
        fieldsScroll.setBorder(null);
        fieldsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        fieldsScroll.getVerticalScrollBar().setUnitIncrement(12);

        configureReadOnlyField(detailIdField);
        configureReadOnlyField(detailCccdField);
        configureReadOnlyField(detailNganhField);
        configureReadOnlyField(detailOrderField);
        configureReadOnlyField(detailMethodField);
        configureReadOnlyField(detailToHopField);
        configureReadOnlyField(detailScoreField);
        configureReadOnlyField(detailResultField);
        configureReadOnlyField(detailKeyField);
        card.setPreferredSize(new Dimension(440, 0));
        card.setMinimumSize(new Dimension(440, 0));

        card.add(header, BorderLayout.NORTH);
        card.add(fieldsScroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel createDetailActions() {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        if (SessionManager.hasPermission("NGUYENVONG_MANAGE")) {
            JButton importBtn = createButton("Import", UIStyles.SUCCESS);
            importBtn.addActionListener(e -> handleImport());
            actions.add(importBtn);

            JButton addBtn = createButton("Thêm", UIStyles.INFO);
            addBtn.addActionListener(e -> handleAdd());
            actions.add(addBtn);

            JButton editBtn = createButton("Sửa", UIStyles.WARNING);
            editBtn.addActionListener(e -> handleEdit());
            actions.add(editBtn);

            JButton deleteBtn = createButton("Xóa", UIStyles.DANGER);
            deleteBtn.addActionListener(e -> handleDelete());
            actions.add(deleteBtn);
        }
        return actions;
    }

    private JPanel labelWithField(String label, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        JLabel text = new JLabel(label);
        text.setFont(UIStyles.FONT_LABEL);
        text.setForeground(UIStyles.TEXT_DARK);
        panel.add(text, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private void configureReadOnlyField(JTextField field) {
        field.setEditable(false);
        field.setBackground(new Color(247, 249, 251));
        field.setFont(UIStyles.FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    private void updateDetailFromSelection() {
        int row = table.getSelectedRow();
        if (row < 0) {
            selectedLabel.setText("Chưa chọn bản ghi");
            detailIdField.setText("");
            detailCccdField.setText("");
            detailNganhField.setText("");
            detailOrderField.setText("");
            detailMethodField.setText("");
            detailToHopField.setText("");
            detailScoreField.setText("");
            detailResultField.setText("");
            detailKeyField.setText("");
            return;
        }

        selectedLabel.setText("Đang chọn ID: " + String.valueOf(tableModel.getValueAt(row, 0)));
        detailIdField.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        detailCccdField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        detailNganhField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        detailOrderField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        detailScoreField.setText(
                String.valueOf(tableModel.getValueAt(row, 4)) + " / " +
                        String.valueOf(tableModel.getValueAt(row, 5)) + " / " +
                        String.valueOf(tableModel.getValueAt(row, 6)) + " / " +
                        String.valueOf(tableModel.getValueAt(row, 7))
        );
        detailResultField.setText(String.valueOf(tableModel.getValueAt(row, 8)));
        detailKeyField.setText(String.valueOf(tableModel.getValueAt(row, 9)));
        detailMethodField.setText(String.valueOf(tableModel.getValueAt(row, 10)));
        detailToHopField.setText(String.valueOf(tableModel.getValueAt(row, 11)));
    }

    // ================= CÁC HÀM XỬ LÝ LOGIC =================

    private void loadDataToTable() {
        if (isCccdOnlyMode()) {
            String cccd = getLoginUsernameAsCccd();
            currentDataList = cccd.isEmpty() ? new java.util.ArrayList<>() : nguyenVongService.getByCccd(cccd);
            renderTablePage();
            selectedLabel.setText("Đang lọc theo CCCD đăng nhập");
            return;
        }

        // Lấy toàn bộ dữ liệu từ Service cất vào danh sách hiện tại
        currentDataList = nguyenVongService.getAll();
        renderTablePage();
    }

    private void handleRefresh() {
        loadDataToTable();
    }

    // Vẽ toàn bộ dữ liệu hiện tại
    private void renderTablePage() {
        tableModel.setRowCount(0);

        for (NguyenVong nv : currentDataList) {
            Object[] row = {
                    nv.getIdnv(), // ID
                    nv.getNnCccd(), nv.getNvManganh(), nv.getNvTt(), nv.getDiemThxt(),
                    nv.getDiemUtqd(), nv.getDiemCong(), nv.getDiemXettuyen(),
                    nv.getNvKetqua(), nv.getNvKeys(), nv.getTtPhuongthuc(), nv.getTtThm()
            };
            tableModel.addRow(row);
        }

        if (tableModel.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        } else {
            updateDetailFromSelection();
        }
    }

    private void handleImport() {
        ImportExcelDialog dialog = new ImportExcelDialog(getTopLevelAncestor() instanceof Frame ?
                (Frame) getTopLevelAncestor() : null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            File selectedFile = dialog.getSelectedFile();
            try {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                nguyenVongService.importFromExcel(selectedFile.getAbsolutePath());
                loadDataToTable();
                setCursor(Cursor.getDefaultCursor());

                JOptionPane.showMessageDialog(this, "Import nguyện vọng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                setCursor(Cursor.getDefaultCursor());
                JOptionPane.showMessageDialog(this, "Lỗi Import: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void handleSearch(String keyword) {
        if (isCccdOnlyMode()) {
            String cccd = getLoginUsernameAsCccd();
            currentDataList = cccd.isEmpty() ? new java.util.ArrayList<>() : nguyenVongService.getByCccd(cccd);
            renderTablePage();
            if (currentDataList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không có dữ liệu cho CCCD đăng nhập hiện tại.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
            return;
        }

        // Nếu ô tìm kiếm trống, load lại toàn bộ danh sách gốc
        if (keyword.isEmpty() || keyword.equals("Tìm CCCD, mã ngành...")) {
            loadDataToTable();
            return;
        }

        // Chuyển từ khóa về chữ thường để tìm kiếm không phân biệt hoa thường
        String lowerKeyword = keyword.toLowerCase().trim();

        // Xin lại toàn bộ dữ liệu gốc từ DB (để tránh bị lọc đè lên kết quả cũ)
        List<NguyenVong> allData = nguyenVongService.getAll();

        // DÙNG JAVA STREAM ĐỂ LỌC (TÌM THEO CCCD HOẶC MÃ NGÀNH)
        currentDataList = allData.stream()
                .filter(nv ->
                        (nv.getNnCccd() != null && nv.getNnCccd().toLowerCase().contains(lowerKeyword)) ||
                                (nv.getNvManganh() != null && nv.getNvManganh().toLowerCase().contains(lowerKeyword))
                )
                .collect(java.util.stream.Collectors.toList());

        renderTablePage();

        // Thông báo nếu không tìm thấy
        if (currentDataList.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả nào cho: " + keyword, "Thông báo", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleAdd() {
        NguyenVongFormDialog dialog = new NguyenVongFormDialog(
                getTopLevelAncestor() instanceof Frame ? (Frame) getTopLevelAncestor() : null,
                "Thêm Nguyện Vọng Mới", false);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                NguyenVong nv = new NguyenVong();
                nv.setNnCccd(dialog.getCccd());
                nv.setNvManganh(dialog.getMaNganh());
                nv.setNvTt(dialog.getThuTuNv());
                nv.setTtPhuongthuc(dialog.getPhuongThuc());
                nv.setTtThm(dialog.getMaThm());
                nv.setDiemThxt(dialog.getDiemThxt());
                nv.setDiemUtqd(dialog.getDiemUtqd());
                nv.setDiemCong(dialog.getDiemCong());
                nv.setDiemXettuyen(dialog.getDiemXetTuyen());
                nv.setNvKetqua(dialog.getKetQua());
                // Khóa nv_keys sẽ do NguyenVongService tự động generate như ta đã code trước đó

                nguyenVongService.add(nv); // Gọi Backend lưu

                loadDataToTable(); // Load lại toàn bộ data và render lại bảng phân trang
                JOptionPane.showMessageDialog(this, "Thêm nguyện vọng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleEdit() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 nguyện vọng để sửa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Lấy dữ liệu từ bảng (Đối chiếu chính xác index cột với mảng String[] cols của bạn)
        Integer idnv = (Integer) tableModel.getValueAt(selectedRow, 0); // Khóa chính idnv
        String cccd = (String) tableModel.getValueAt(selectedRow, 1);
        String maNganh = (String) tableModel.getValueAt(selectedRow, 2);
        Integer thuTuNv = (Integer) tableModel.getValueAt(selectedRow, 3);

        // Ép kiểu các cột Điểm sang BigDecimal
        java.math.BigDecimal diemThxt = new java.math.BigDecimal(tableModel.getValueAt(selectedRow, 4).toString());
        java.math.BigDecimal diemUtqd = new java.math.BigDecimal(tableModel.getValueAt(selectedRow, 5).toString());
        java.math.BigDecimal diemCong = new java.math.BigDecimal(tableModel.getValueAt(selectedRow, 6).toString());
        java.math.BigDecimal diemXetTuyen = new java.math.BigDecimal(tableModel.getValueAt(selectedRow, 7).toString());

        String ketQua = (String) tableModel.getValueAt(selectedRow, 8);
        String keys = (String) tableModel.getValueAt(selectedRow, 9);
        String phuongThuc = (String) tableModel.getValueAt(selectedRow, 10);
        String maThm = (String) tableModel.getValueAt(selectedRow, 11);

        // Mở hộp thoại truyền dữ liệu
        NguyenVongFormDialog dialog = new NguyenVongFormDialog(
                getTopLevelAncestor() instanceof Frame ? (Frame) getTopLevelAncestor() : null,
                "Sửa Thông Tin Nguyện Vọng", true);
        dialog.setData(cccd, maNganh, thuTuNv, diemThxt, diemUtqd, diemCong, diemXetTuyen, ketQua, keys, phuongThuc, maThm);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                NguyenVong nv = new NguyenVong();
                nv.setIdnv(idnv); // Set Khóa chính để Hibernate biết là update dòng nào
                nv.setNnCccd(dialog.getCccd());
                nv.setNvManganh(dialog.getMaNganh());
                nv.setNvTt(dialog.getThuTuNv());
                nv.setTtPhuongthuc(dialog.getPhuongThuc());
                nv.setTtThm(dialog.getMaThm());
                nv.setDiemThxt(dialog.getDiemThxt());
                nv.setDiemUtqd(dialog.getDiemUtqd());
                nv.setDiemCong(dialog.getDiemCong());
                nv.setDiemXettuyen(dialog.getDiemXetTuyen());
                nv.setNvKetqua(dialog.getKetQua());
                nv.setNvKeys(dialog.getKeys());

                nguyenVongService.update(nv); // Gọi Backend cập nhật

                loadDataToTable();
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //Lấy CCCD ở cột 0, Mã ngành ở cột 1
        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
        String cccd = (String) tableModel.getValueAt(selectedRow, 1);

        DeleteConfirmDialog dialog = new DeleteConfirmDialog(getTopLevelAncestor() instanceof Frame ?
                (Frame) getTopLevelAncestor() : null, "Nguyện vọng của CCCD: " + cccd);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            // GỌI HÀM XÓA BẰNG ID:
            boolean success = nguyenVongService.delete(id);

            if(success) {
                tableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Xóa thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
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

    private void applySearchPlaceholder(JTextField field, String placeholderText) {
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholderText)) {
                    field.setText("");
                    field.setForeground(UIStyles.TEXT_DARK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(placeholderText);
                    field.setForeground(UIStyles.TEXT_MUTED);
                }
            }
        });
    }

    private boolean isCccdOnlyMode() {
        return !SessionManager.hasPermission("NGUYENVONG_VIEW") && SessionManager.hasPermission("NGUYENVONG_VIEW_BY_CCCD");
    }

    private String getLoginUsernameAsCccd() {
        if (SessionManager.getCurrentUser() == null || SessionManager.getCurrentUser().getUsername() == null) {
            return "";
        }
        return SessionManager.getCurrentUser().getUsername().trim();
    }
}