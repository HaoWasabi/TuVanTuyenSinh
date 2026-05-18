package com.tuyensinh.view;

import com.tuyensinh.model.NguyenVong;
import com.tuyensinh.model.ThiSinh;
import com.tuyensinh.model.DiemCong;
import com.tuyensinh.service.NguyenVongService;
import com.tuyensinh.service.SessionManager;
import com.tuyensinh.service.ThiSinhService;
import com.tuyensinh.service.DiemCongService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class DiemThiPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTable table;
    private final JTextField detailCccdField = new JTextField();
    private final JTextField detailHoField = new JTextField();
    private final JTextField detailTenField = new JTextField();
    private final JTextField detailNguyenVongField = new JTextField();
    private final JTextField detailThmCaoNhatField = new JTextField();
    private final JTextField detailDiemThmField = new JTextField();
    private final JTextField detailDiemCongField = new JTextField();
    private final JTextField detailDiemUuTienField = new JTextField();
    private final JTextField detailDiemXetTuyenField = new JTextField();
    private final JTextField detailPhuongThucField = new JTextField();
    private final JTextField detailGhiChuField = new JTextField();
    private final JLabel selectedLabel = new JLabel("Chưa chọn bản ghi");

    private List<NguyenVong> currentDataList = new java.util.ArrayList<>();
    private Map<String, ThiSinh> thiSinhMap = new HashMap<>();
    private Map<String, DiemCong> diemCongMap = new HashMap<>();

    // GỌI SERVICE Ở ĐÂY
    private final NguyenVongService nguyenVongService = new NguyenVongService();
    private final ThiSinhService thiSinhService = new ThiSinhService();
    private final DiemCongService diemCongService = new DiemCongService();

    public DiemThiPanel() {
        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(UIStyles.BG_APP);

        // Title
        JLabel title = new JLabel("Danh sách điểm xét tuyển theo nguyện vọng");
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
        String placeholderText = "Tìm CCCD, nguyện vọng...";
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

        // Cấu hình Cột
        String[] cols = {
                "CCCD", "Họ", "Tên", "Nguyện vọng", "THM cao nhất", "Điểm THM", "Điểm cộng", "Điểm ưu tiên", "Điểm xét tuyển", "Phương thức", "Ghi chú"
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
        table.getColumnModel().getColumn(0).setPreferredWidth(120); // CCCD
        table.getColumnModel().getColumn(1).setPreferredWidth(120); // Họ
        table.getColumnModel().getColumn(2).setPreferredWidth(80);  // Tên
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Nguyện vọng
        table.getColumnModel().getColumn(4).setPreferredWidth(120); // THM cao nhất
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // Điểm THM
        table.getColumnModel().getColumn(6).setPreferredWidth(100); // Điểm cộng
        table.getColumnModel().getColumn(7).setPreferredWidth(100); // Điểm ưu tiên
        table.getColumnModel().getColumn(8).setPreferredWidth(120); // Điểm xét tuyển
        table.getColumnModel().getColumn(9).setPreferredWidth(100); // Phương thức
        table.getColumnModel().getColumn(10).setPreferredWidth(150); // Ghi chú

        JPanel tableCard = new JPanel(new BorderLayout(0, 12));
        tableCard.setBackground(UIStyles.BG_CARD);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel tableTitle = new JLabel("Danh sách kết quả đánh giá");
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
        JLabel title = new JLabel("Chi tiết điểm xét tuyển");
        title.setFont(UIStyles.FONT_SUBTITLE);
        title.setForeground(UIStyles.TEXT_DARK);
        header.add(title, BorderLayout.WEST);

        JPanel rightHeader = new JPanel(new BorderLayout(0, 6));
        rightHeader.setOpaque(false);
        selectedLabel.setFont(UIStyles.FONT_SMALL);
        selectedLabel.setForeground(UIStyles.TEXT_MUTED);
        rightHeader.add(selectedLabel, BorderLayout.NORTH);
        // rightHeader.add(createDetailActions(), BorderLayout.SOUTH);
        header.add(rightHeader, BorderLayout.EAST);

        JPanel fields = new JPanel(new GridLayout(0, 1, 0, 8));
        fields.setOpaque(false);
        fields.add(labelWithField("CCCD", detailCccdField));
        fields.add(labelWithField("Họ", detailHoField));
        fields.add(labelWithField("Tên", detailTenField));
        fields.add(labelWithField("Nguyện vọng", detailNguyenVongField));
        fields.add(labelWithField("THM điểm cao nhất", detailThmCaoNhatField));
        fields.add(labelWithField("Điểm THM", detailDiemThmField));
        fields.add(labelWithField("Điểm cộng", detailDiemCongField));
        fields.add(labelWithField("Điểm ưu tiên", detailDiemUuTienField));
        fields.add(labelWithField("Điểm xét tuyển", detailDiemXetTuyenField));
        fields.add(labelWithField("Phương thức", detailPhuongThucField));
        fields.add(labelWithField("Ghi chú", detailGhiChuField));

        JScrollPane fieldsScroll = new JScrollPane(fields);
        fieldsScroll.setBorder(null);
        fieldsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        fieldsScroll.getVerticalScrollBar().setUnitIncrement(12);

        configureReadOnlyField(detailCccdField);
        configureReadOnlyField(detailHoField);
        configureReadOnlyField(detailTenField);
        configureReadOnlyField(detailNguyenVongField);
        configureReadOnlyField(detailThmCaoNhatField);
        configureReadOnlyField(detailDiemThmField);
        configureReadOnlyField(detailDiemCongField);
        configureReadOnlyField(detailDiemUuTienField);
        configureReadOnlyField(detailDiemXetTuyenField);
        configureReadOnlyField(detailPhuongThucField);
        configureReadOnlyField(detailGhiChuField);
        
        card.setPreferredSize(new Dimension(440, 0));
        card.setMinimumSize(new Dimension(440, 0));

        card.add(header, BorderLayout.NORTH);
        card.add(fieldsScroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel labelWithField(String labelText, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(UIStyles.FONT_LABEL);
        label.setForeground(UIStyles.TEXT_DARK);
        panel.add(label, BorderLayout.NORTH);
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
            detailCccdField.setText("");
            detailHoField.setText("");
            detailTenField.setText("");
            detailNguyenVongField.setText("");
            detailThmCaoNhatField.setText("");
            detailDiemThmField.setText("");
            detailDiemCongField.setText("");
            detailDiemUuTienField.setText("");
            detailDiemXetTuyenField.setText("");
            detailPhuongThucField.setText("");
            detailGhiChuField.setText("");
            return;
        }

        selectedLabel.setText("Đang chọn CCCD: " + String.valueOf(tableModel.getValueAt(row, 0)));
        detailCccdField.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        detailHoField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        detailTenField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        detailNguyenVongField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        detailThmCaoNhatField.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        detailDiemThmField.setText(String.valueOf(tableModel.getValueAt(row, 5)));
        detailDiemCongField.setText(String.valueOf(tableModel.getValueAt(row, 6)));
        detailDiemUuTienField.setText(String.valueOf(tableModel.getValueAt(row, 7)));
        detailDiemXetTuyenField.setText(String.valueOf(tableModel.getValueAt(row, 8)));
        detailPhuongThucField.setText(String.valueOf(tableModel.getValueAt(row, 9)));
        detailGhiChuField.setText(String.valueOf(tableModel.getValueAt(row, 10)));
    }

    // ================= CÁC HÀM XỬ LÝ LOGIC =================

    private void loadDataToTable() {
        // Load all ThiSinh for quick lookup
        thiSinhMap.clear();
        for (ThiSinh ts : thiSinhService.getAll()) {
            thiSinhMap.put(ts.getCccd(), ts);
        }
        
        // Load all DiemCong for quick lookup by keys
        diemCongMap.clear();
        for (DiemCong dc : diemCongService.getAll()) {
            if (dc.getDcKeys() != null) {
                diemCongMap.put(dc.getDcKeys(), dc);
            }
        }

        if (isCccdOnlyMode()) {
            String cccd = getLoginUsernameAsCccd();
            currentDataList = cccd.isEmpty() ? new java.util.ArrayList<>() : nguyenVongService.getByCccd(cccd);
            renderTablePage();
            selectedLabel.setText("Đang lọc theo CCCD đăng nhập");
            return;
        }

        currentDataList = nguyenVongService.getAll();
        renderTablePage();
    }

    private void handleRefresh() {
        loadDataToTable();
    }

    // Vẽ toàn bộ dữ liệu hiện tại
    private void renderTablePage() {
        tableModel.setRowCount(0);

        Map<String, NguyenVong> bestNvMap = new HashMap<>();
        for (NguyenVong nv : currentDataList) {
            String cccd = nv.getNnCccd() != null ? nv.getNnCccd() : "";
            String nganh = nv.getNvManganh() != null ? nv.getNvManganh() : "";
            String key = cccd + "_" + nganh;
            
            if (!bestNvMap.containsKey(key)) {
                bestNvMap.put(key, nv);
            } else {
                NguyenVong existing = bestNvMap.get(key);
                BigDecimal currentScore = nv.getDiemXettuyen() != null ? nv.getDiemXettuyen() : BigDecimal.ZERO;
                BigDecimal existingScore = existing.getDiemXettuyen() != null ? existing.getDiemXettuyen() : BigDecimal.ZERO;
                
                if (currentScore.compareTo(existingScore) > 0) {
                    bestNvMap.put(key, nv);
                }
            }
        }

        List<NguyenVong> groupedList = new java.util.ArrayList<>(bestNvMap.values());
        groupedList.sort(java.util.Comparator
                .comparing((NguyenVong nv) -> nv.getNnCccd() != null ? nv.getNnCccd() : "")
                .thenComparing(nv -> nv.getNvManganh() != null ? nv.getNvManganh() : ""));

        for (NguyenVong nv : groupedList) {
            String cccd = nv.getNnCccd();
            ThiSinh ts = thiSinhMap.get(cccd);
            String ho = ts != null ? ts.getHo() : "";
            String ten = ts != null ? ts.getTen() : "";
            
            // Get note from DiemCong lookup
            DiemCong dc_info = diemCongMap.get(nv.getNvKeys());
            String ghiChu = dc_info != null ? dc_info.getGhichu() : "";

             Object[] row = {
                     cccd, 
                     ho, 
                     ten, 
                     (nv.getNvTt() != null ? "NV" + nv.getNvTt() + " - " : "") + (nv.getNvManganh() != null && nv.getNvManganh().equals("DGNL") ? "Đa tổ hợp" : nv.getNvManganh()), 
                     (nv.getTtThm() != null && nv.getTtThm().equals("DGNL") ? "Đa tổ hợp" : nv.getTtThm()), 
                     formatDecimal(nv.getDiemThxt()), 
                     formatDecimal(nv.getDiemCong()), 
                     formatDecimal(nv.getDiemUtqd()), 
                     formatDecimal(nv.getDiemXettuyen()),
                     nv.getTtPhuongthuc(),
                     ghiChu
             };
            tableModel.addRow(row);
        }

        if (tableModel.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        } else {
            updateDetailFromSelection();
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

        if (keyword.isEmpty() || keyword.equals("Tìm CCCD, nguyện vọng...")) {
            loadDataToTable();
            return;
        }

        String lowerKeyword = keyword.toLowerCase().trim();
        List<NguyenVong> allData = nguyenVongService.getAll();

        currentDataList = allData.stream()
                .filter(nv ->
                        (nv.getNnCccd() != null && nv.getNnCccd().toLowerCase().contains(lowerKeyword)) ||
                                (nv.getNvManganh() != null && nv.getNvManganh().toLowerCase().contains(lowerKeyword))
                )
                .collect(java.util.stream.Collectors.toList());

        renderTablePage();

        if (currentDataList.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả nào cho: " + keyword, "Thông báo", javax.swing.JOptionPane.INFORMATION_MESSAGE);
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
        return !SessionManager.hasPermission("DIEM_VIEW") && SessionManager.hasPermission("DIEM_VIEW_BY_CCCD");
    }

    private String getLoginUsernameAsCccd() {
        if (SessionManager.getCurrentUser() == null || SessionManager.getCurrentUser().getUsername() == null) {
            return "";
        }
        return SessionManager.getCurrentUser().getUsername().trim();
    }
    
    private String formatDecimal(BigDecimal value) {
        if (value == null) {
            return "";
        }
        return value.stripTrailingZeros().toPlainString();
    }
}