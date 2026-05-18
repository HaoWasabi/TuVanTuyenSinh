package com.tuyensinh.view;

import com.tuyensinh.model.DiemCong;
import com.tuyensinh.model.NguyenVong;
import com.tuyensinh.service.DiemCongService;
import com.tuyensinh.service.NguyenVongService;
import com.tuyensinh.service.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiemCongPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTable table;
    private final JTextField detailIdField = new JTextField();
    private final JTextField detailCccdField = new JTextField();
    private final JTextField detailNganhField = new JTextField();
    private final JTextField detailToHopField = new JTextField();
    private final JTextField detailMethodField = new JTextField();
    private final JTextField detailEnglishPriorityField = new JTextField();
    private final JTextField detailHsgPriorityField = new JTextField();
    private final JTextField detailTotalField = new JTextField();
    private final JTextField detailNoteField = new JTextField();
    private final JTextField detailKeyField = new JTextField();
    private final JLabel selectedLabel = new JLabel("Chưa chọn bản ghi");

    private List<DiemCong> currentDataList = new java.util.ArrayList<>();
    private final Map<String, Integer> nguyenVongOrderLookup = new HashMap<>();


    // GỌI SERVICE Ở ĐÂY
    private final DiemCongService diemCongService = new DiemCongService();
    private final NguyenVongService nguyenVongService = new NguyenVongService();

    public DiemCongPanel() {
        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(UIStyles.BG_APP);

        // Title
        JLabel title = new JLabel("Quản Lý Điểm Cộng");
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
        String placeholderText = "Tìm thí sinh, nguyện vọng...";
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
                "Thí sinh", "Nguyện vọng", "Tổ hợp môn", "Phương thức",
                "Điểm cộng tiếng Anh", "Điểm cộng học sinh giỏi", "Tổng điểm cộng", "Ghi chú"
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
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateDetailFromSelection();
            }
        });

        // Chỉnh độ rộng một số cột quan trọng
        table.getColumnModel().getColumn(0).setPreferredWidth(140); // Thí sinh
        table.getColumnModel().getColumn(1).setPreferredWidth(170); // Nguyện vọng
        table.getColumnModel().getColumn(2).setPreferredWidth(120); // Tổ hợp môn
        table.getColumnModel().getColumn(3).setPreferredWidth(120); // Phương thức
        table.getColumnModel().getColumn(4).setPreferredWidth(150); // Điểm cộng TA
        table.getColumnModel().getColumn(5).setPreferredWidth(180); // Điểm cộng HSG
        table.getColumnModel().getColumn(6).setPreferredWidth(120); // Tổng điểm cộng
        table.getColumnModel().getColumn(7).setPreferredWidth(200); // Ghi chú

        JPanel tableCard = new JPanel(new BorderLayout(0, 12));
        tableCard.setBackground(UIStyles.BG_CARD);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel tableTitle = new JLabel("Danh sách điểm cộng thí sinh");
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
        JLabel title = new JLabel("Chi tiết điểm cộng");
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
        fields.add(labelWithField("ID điểm cộng", detailIdField));
        fields.add(labelWithField("Thí sinh", detailCccdField));
        fields.add(labelWithField("Nguyện vọng", detailNganhField));
        fields.add(labelWithField("Mã tổ hợp", detailToHopField));
        fields.add(labelWithField("Phương thức", detailMethodField));
        fields.add(labelWithField("Điểm cộng tiếng Anh", detailEnglishPriorityField));
        fields.add(labelWithField("Điểm cộng HSG", detailHsgPriorityField));
        fields.add(labelWithField("Tổng điểm cộng", detailTotalField));
        fields.add(labelWithField("Ghi chú", detailNoteField));
        fields.add(labelWithField("Khóa dữ liệu", detailKeyField));

        JScrollPane fieldsScroll = new JScrollPane(fields);
        fieldsScroll.setBorder(null);
        fieldsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        fieldsScroll.getVerticalScrollBar().setUnitIncrement(12);

        configureReadOnlyField(detailIdField);
        configureReadOnlyField(detailCccdField);
        configureReadOnlyField(detailNganhField);
        configureReadOnlyField(detailToHopField);
        configureReadOnlyField(detailMethodField);
        configureReadOnlyField(detailEnglishPriorityField);
        configureReadOnlyField(detailHsgPriorityField);
        configureReadOnlyField(detailTotalField);
        configureReadOnlyField(detailNoteField);
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

        if (SessionManager.hasPermission("DIEMCONG_IMPORT")) {
            JButton importBtn = createButton("Import", UIStyles.SUCCESS);
            importBtn.addActionListener(e -> handleImport());
            actions.add(importBtn);
        }
        if (SessionManager.hasPermission("DIEMCONG_CREATE")) {
            JButton addBtn = createButton("Thêm", UIStyles.INFO);
            addBtn.addActionListener(e -> handleAdd());
            actions.add(addBtn);
        }
        if (SessionManager.hasPermission("DIEMCONG_EDIT")) {
            JButton editBtn = createButton("Sửa", UIStyles.WARNING);
            editBtn.addActionListener(e -> handleEdit());
            actions.add(editBtn);
        }
        if (SessionManager.hasPermission("DIEMCONG_DELETE")) {
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
        DiemCong selected = getSelectedDiemCong();
        if (selected == null) {
            selectedLabel.setText("Chưa chọn bản ghi");
            detailIdField.setText("");
            detailCccdField.setText("");
            detailNganhField.setText("");
            detailToHopField.setText("");
            detailMethodField.setText("");
            detailEnglishPriorityField.setText("");
            detailHsgPriorityField.setText("");
            detailTotalField.setText("");
            detailNoteField.setText("");
            detailKeyField.setText("");
            return;
        }

        selectedLabel.setText("Đang chọn ID: " + safeNumber(selected.getIddiemcong()));
        detailIdField.setText(safeNumber(selected.getIddiemcong()));
        detailCccdField.setText(buildThiSinhDisplay(selected));
        detailNganhField.setText(buildNguyenVongDisplay(selected));
        detailToHopField.setText(safeText(selected.getMatohop()));
        detailMethodField.setText(safeText(selected.getPhuongthuc()));
        detailEnglishPriorityField.setText(formatDecimal(selected.getDiemCC()));
        detailHsgPriorityField.setText(formatDecimal(selected.getDiemUtxt()));
        detailTotalField.setText(formatDecimal(selected.getDiemTong()));
        detailNoteField.setText(safeText(selected.getGhichu()));
        detailKeyField.setText(safeText(selected.getDcKeys()));
    }

    // ================= CÁC HÀM XỬ LÝ LOGIC =================

    private void loadDataToTable() {
        if (isCccdOnlyMode()) {
            String cccd = getLoginUsernameAsCccd();
            currentDataList = cccd.isEmpty() ? new java.util.ArrayList<>() : diemCongService.getByCccd(cccd);
            rebuildNguyenVongLookup(cccd);
            renderTablePage();
            selectedLabel.setText("Đang lọc theo CCCD đăng nhập");
            return;
        }

        // Lấy toàn bộ dữ liệu từ Service cất vào danh sách hiện tại
        currentDataList = diemCongService.getAll();
        rebuildNguyenVongLookup(null);
        renderTablePage();
    }

    private void handleRefresh() {
        loadDataToTable();
    }

    // Vẽ toàn bộ dữ liệu hiện tại
    private void renderTablePage() {
        tableModel.setRowCount(0);

         for (DiemCong dc : currentDataList) {
             Object[] row = {
                     buildThiSinhDisplay(dc),
                     buildNguyenVongDisplay(dc),
                     dc.getMatohop() != null && dc.getMatohop().equals("DGNL") ? "Đa tổ hợp" : safeText(dc.getMatohop()),
                     safeText(dc.getPhuongthuc()),
                     formatDecimal(dc.getDiemCC()),
                     formatDecimal(dc.getDiemUtxt()),
                     formatDecimal(dc.getDiemTong()),
                     safeText(dc.getGhichu())
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
        ImportDCExcelDialog dialog = new ImportDCExcelDialog(getTopLevelAncestor() instanceof Frame ?
                (Frame) getTopLevelAncestor() : null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            File selectedFile = dialog.getSelectedFile();
            try {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                diemCongService.importFromExcel(selectedFile.getAbsolutePath());
                loadDataToTable();
                setCursor(Cursor.getDefaultCursor());

                JOptionPane.showMessageDialog(this, "Import điểm cộng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
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
            currentDataList = cccd.isEmpty() ? new java.util.ArrayList<>() : diemCongService.getByCccd(cccd);
            rebuildNguyenVongLookup(cccd);
            renderTablePage();
            if (currentDataList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không có dữ liệu cho CCCD đăng nhập hiện tại.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
            return;
        }

        // Nếu ô tìm kiếm trống, load lại toàn bộ danh sách gốc
        if (keyword.isEmpty() || keyword.equals("Tìm thí sinh, nguyện vọng...")) {
            loadDataToTable();
            return;
        }

        // Chuyển từ khóa về chữ thường để tìm kiếm không phân biệt hoa thường
        String lowerKeyword = keyword.toLowerCase().trim();

        // Xin lại toàn bộ dữ liệu gốc từ DB (để tránh bị lọc đè lên kết quả cũ)
        List<DiemCong> allData = diemCongService.getAll();

        // DÙNG JAVA STREAM ĐỂ LỌC (TÌM THEO THÍ SINH HOẶC NGUYỆN VỌNG/NGÀNH)
        currentDataList = allData.stream()
                .filter(dc ->
                        (dc.getTsCccd() != null && dc.getTsCccd().toLowerCase().contains(lowerKeyword)) ||
                                (dc.getManganh() != null && dc.getManganh().toLowerCase().contains(lowerKeyword))
                )
                .collect(java.util.stream.Collectors.toList());
        rebuildNguyenVongLookup(isCccdOnlyMode() ? getLoginUsernameAsCccd() : null);

        renderTablePage();

        // Thông báo nếu không tìm thấy
        if (currentDataList.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả nào cho: " + keyword, "Thông báo", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleAdd() {
        DiemCongFormDialog dialog = new DiemCongFormDialog(
                getTopLevelAncestor() instanceof Frame ? (Frame) getTopLevelAncestor() : null,
                "Thêm Điểm Cộng Mới",
                false);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                // Tạo object Model mới từ data của Dialog
                DiemCong dc = new DiemCong();
                dc.setTsCccd(dialog.getCccd());
                dc.setManganh(dialog.getMaNganh());
                dc.setMatohop(dialog.getMaToHop());
                dc.setPhuongthuc(dialog.getPhuongThuc());
                dc.setDiemCC(dialog.getDiemCC());
                dc.setDiemUtxt(dialog.getDiemUtxt());
                dc.setDiemTong(dialog.getDiemTong());
                dc.setGhichu(dialog.getGhiChu());
                dc.setDcKeys(dialog.getKeys()); // Hoặc tự generate kiểu: "DC_" + dialog.getCccd() + "_" + dialog.getMaNganh()

                // GỌI HÀM SAVE/ADD CỦA BACKEND
                diemCongService.add(dc); // LƯU Ý: Đổi tên hàm "add" cho đúng với Service của bạn

                loadDataToTable(); // Load lại bảng
                JOptionPane.showMessageDialog(this, "Thêm thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleEdit() {
        DiemCong selected = getSelectedDiemCong();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 dòng để sửa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Mở hộp thoại Sửa
        DiemCongFormDialog dialog = new DiemCongFormDialog(
                getTopLevelAncestor() instanceof Frame ? (Frame) getTopLevelAncestor() : null,
                "Sửa Thông Tin Điểm Cộng",
                true); // true = isEditing

        dialog.setData(
                safeText(selected.getTsCccd()),
                safeText(selected.getManganh()),
                safeText(selected.getMatohop()),
                safeText(selected.getPhuongthuc()),
                defaultDecimal(selected.getDiemCC()),
                defaultDecimal(selected.getDiemUtxt()),
                defaultDecimal(selected.getDiemTong()),
                safeText(selected.getGhichu()),
                safeText(selected.getDcKeys())
        );
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                // Tạo object Model update
                DiemCong dc = new DiemCong();
                dc.setIddiemcong(selected.getIddiemcong()); // Set ID để update đúng dòng
                dc.setTsCccd(dialog.getCccd());
                dc.setManganh(dialog.getMaNganh());
                dc.setMatohop(dialog.getMaToHop());
                dc.setPhuongthuc(dialog.getPhuongThuc());
                dc.setDiemCC(dialog.getDiemCC());
                dc.setDiemUtxt(dialog.getDiemUtxt());
                dc.setDiemTong(dialog.getDiemTong());
                dc.setGhichu(dialog.getGhiChu());
                dc.setDcKeys(dialog.getKeys());

                // GỌI HÀM UPDATE CỦA BACKEND
                diemCongService.update(dc); // LƯU Ý: Đổi tên hàm "update" cho đúng với Service của bạn

                loadDataToTable(); // Load lại bảng
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDelete() {
        DiemCong selected = getSelectedDiemCong();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer id = selected.getIddiemcong();
        String cccd = safeText(selected.getTsCccd());

        DeleteConfirmDialog dialog = new DeleteConfirmDialog(getTopLevelAncestor() instanceof Frame ?
                (Frame) getTopLevelAncestor() : null, "Điểm cộng của CCCD: " + cccd);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            // GỌI HÀM XÓA BẰNG ID:
            boolean success = diemCongService.delete(id);

            if(success) {
                loadDataToTable();
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
        return !SessionManager.hasPermission("DIEMCONG_VIEW") && SessionManager.hasPermission("DIEMCONG_VIEW_BY_CCCD");
    }

    private String getLoginUsernameAsCccd() {
        if (SessionManager.getCurrentUser() == null || SessionManager.getCurrentUser().getUsername() == null) {
            return "";
        }
        return SessionManager.getCurrentUser().getUsername().trim();
    }

    private void rebuildNguyenVongLookup(String onlyCccd) {
        nguyenVongOrderLookup.clear();
        List<NguyenVong> nguyenVongList = (onlyCccd == null || onlyCccd.trim().isEmpty())
                ? nguyenVongService.getAll()
                : nguyenVongService.getByCccd(onlyCccd.trim());

        for (NguyenVong nv : nguyenVongList) {
            String key = buildLookupKey(nv.getNnCccd(), nv.getNvManganh());
            if (key.isEmpty() || nv.getNvTt() == null) {
                continue;
            }
            Integer existingOrder = nguyenVongOrderLookup.get(key);
            if (existingOrder == null || nv.getNvTt() < existingOrder) {
                nguyenVongOrderLookup.put(key, nv.getNvTt());
            }
        }
    }

    private DiemCong getSelectedDiemCong() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentDataList.size()) {
            return null;
        }
        return currentDataList.get(row);
    }

    private String buildThiSinhDisplay(DiemCong dc) {
        return safeText(dc.getTsCccd());
    }

    private String buildNguyenVongDisplay(DiemCong dc) {
        String maNganh = safeText(dc.getManganh());
        String key = buildLookupKey(dc.getTsCccd(), maNganh);
        Integer nvOrder = nguyenVongOrderLookup.get(key);
        if (nvOrder == null) {
            return maNganh;
        }
        return "NV" + nvOrder + " - " + maNganh;
    }

    private String buildLookupKey(String cccd, String maNganh) {
        String left = safeText(cccd);
        String right = safeText(maNganh);
        if (left.isEmpty() || right.isEmpty()) {
            return "";
        }
        return left + "|" + right;
    }

    private String safeText(String value) {
        return value == null ? "" : value.trim();
    }

    private String safeNumber(Number value) {
        return value == null ? "" : value.toString();
    }

    private String formatDecimal(BigDecimal value) {
        if (value == null) {
            return "";
        }
        return value.stripTrailingZeros().toPlainString();
    }

    private BigDecimal defaultDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
