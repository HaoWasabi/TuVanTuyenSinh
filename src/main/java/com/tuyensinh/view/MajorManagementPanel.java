package com.tuyensinh.view;

import com.tuyensinh.model.BangQuyDoi;
import com.tuyensinh.model.Nganh;
import com.tuyensinh.model.NganhToHop;
import com.tuyensinh.service.BQDService;
import com.tuyensinh.service.NTHService;
import com.tuyensinh.service.NganhService;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Panel Quản lý Ngành & Tổ hợp - Bao gồm 4 tab chức năng con
 * Đã đồng bộ hoàn toàn kích thước, bố cục với CandidateManagementPanel
 * Sử dụng MajorDeleteConfirmDialog và MajorImportExcelDialog chuẩn của hệ thống.
 */
public class MajorManagementPanel extends JPanel {

    private final NTHService nthService;
    private final NganhService nganhService;
    private final BQDService bqdService;

    public MajorManagementPanel() {
        this.nthService = new NTHService();
        this.nganhService = new NganhService();
        this.bqdService = new BQDService();

        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(UIStyles.BG_APP);

        // Header
        JLabel title = new JLabel("Hệ Thống Quản Lý Ngành & Tổ Hợp Xét Tuyển");
        title.setFont(UIStyles.FONT_TITLE);
        title.setForeground(UIStyles.TEXT_DARK);
        add(title, BorderLayout.NORTH);

        // TabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIStyles.FONT_BODY);
        tabbedPane.setBackground(UIStyles.BG_APP);
        tabbedPane.setForeground(UIStyles.TEXT_DARK);

        // --- TAB 1: QL Ngành Tuyển Sinh ---
        String[] colsNganh = {"Mã Ngành", "Tên Ngành", "Chỉ Tiêu", "Ngưỡng Đảm Bảo", "Trạng Thái"};
        Object[][] dataNganh = {
                {"7140201", "Giáo dục Mầm non", "100", "19.0", "Đang tuyển"},
                {"7140202", "Giáo dục Tiểu học", "150", "19.0", "Đang tuyển"},
                {"7480201", "Công nghệ thông tin", "250", "18.0", "Đang tuyển"},
                {"7340101", "Quản trị kinh doanh", "200", "16.0", "Đang tuyển"},
                {"7220201", "Ngôn ngữ Anh", "150", "16.0", "Đang tuyển"},
                {"7380101", "Luật", "200", "16.0", "Đang tuyển"}
        };
        tabbedPane.addTab("QL Ngành Tuyển Sinh", createTabPanel("Danh sách Ngành đào tạo (Trích: Nguong dau vao 2025)", colsNganh, dataNganh, new String[]{"Tất cả trạng thái", "Đang tuyển", "Dừng tuyển"}));

        // --- TAB 2: QL Tổ Hợp Môn ---
        String[] colsToHop = {"Mã Tổ Hợp", "Môn 1", "Môn 2", "Môn 3", "Ghi Chú"};
        Object[][] dataToHop = {
                {"A00", "Toán", "Vật lí", "Hóa học", "Khối A truyền thống"},
                {"A01", "Toán", "Vật lí", "Tiếng Anh", "Khối A1"},
                {"B00", "Toán", "Hóa học", "Sinh học", "Khối B"},
                {"C00", "Ngữ văn", "Lịch sử", "Địa lí", "Khối C truyền thống"},
                {"D01", "Toán", "Ngữ văn", "Tiếng Anh", "Khối D1"},
                {"M01", "Ngữ văn", "Toán", "Năng khiếu", "Đọc, kể diễn cảm và Hát"}
        };
        tabbedPane.addTab("QL Tổ Hợp Môn", createTabPanel("Danh sách Tổ hợp môn xét tuyển (Trích: tohopmon.xlsx)", colsToHop, dataToHop, null));

        // --- TAB 3: QL Danh Sách Ngành - Tổ Hợp ---
        String[] colsNganhToHop = {
            "ID bản ghi",
            "Mã ngành",
            "Mã tổ hợp",
            "Môn 1",
            "Hệ số môn 1",
            "Môn 2",
            "Hệ số môn 2",
            "Môn 3",
            "Hệ số môn 3",
            "Mã tổ hợp - ngành",
            "Độ lệch"
        };
        Object[][] dataNganhToHop = buildNganhToHopData();
        tabbedPane.addTab("QL Ngành - Tổ Hợp", createTabPanel("Map Tổ hợp môn vào Ngành", colsNganhToHop, dataNganhToHop, buildNganhToHopFilterOptions()));

        // --- TAB 4: QL Bảng Quy Đổi ---
        String[] colsQuyDoi = {
            "ID Quy Đổi",
            "Phương Thức",
            "Tổ Hợp",
            "Môn",
            "Điểm A",
            "Điểm B",
            "Điểm C",
            "Điểm D",
            "Mã Quy Đổi",
            "Phân Vị"
        };
        Object[][] dataQuyDoi = buildQuyDoiData();
        tabbedPane.addTab("QL Bảng Quy Đổi", createTabPanel("Bảng quy đổi điểm Ngoại ngữ & V-SAT", colsQuyDoi, dataQuyDoi, new String[]{"Tất cả", "Ngoại Ngữ (IELTS/VSTEP)", "Kỳ thi V-SAT"}));

        add(tabbedPane, BorderLayout.CENTER);
        
    }

    private Object[][] buildNganhToHopData() {
        try {
            List<NganhToHop> nganhToHopList = nthService.getAll();

            Object[][] rows = new Object[nganhToHopList.size()][11];
            for (int i = 0; i < nganhToHopList.size(); i++) {
                NganhToHop item = nganhToHopList.get(i);
                rows[i] = new Object[] {
                        safeNumber(item.getId()),
                        safeText(item.getMaNganh()),
                        safeText(item.getMaToHop()),
                        safeText(item.getThMon1()),
                        safeNumber(item.getHsMon1()),
                        safeText(item.getThMon2()),
                        safeNumber(item.getHsMon2()),
                        safeText(item.getThMon3()),
                        safeNumber(item.getHsMon3()),
                        safeText(item.getTbKeys()),
                        formatDecimal(item.getDoLech())
                };
            }
            return rows;
        } catch (Exception ex) {
            return new Object[0][0];
        }
    }

    private Object[][] buildQuyDoiData() {
        try {
            List<BangQuyDoi> quyDoiList = bqdService.getAll();
            Object[][] rows = new Object[quyDoiList.size()][10];

            for (int i = 0; i < quyDoiList.size(); i++) {
                BangQuyDoi item = quyDoiList.get(i);
                rows[i] = new Object[] {
                        safeNumber(item.getIdqd()),
                        safeText(item.getDPhuongthuc()),
                        safeText(item.getDTohop()),
                        safeText(item.getDMon()),
                        formatDecimal(item.getDDiema()),
                        formatDecimal(item.getDDiemb()),
                        formatDecimal(item.getDDiemc()),
                        formatDecimal(item.getDDiemd()),
                        safeText(item.getDMaquydoi()),
                        safeText(item.getDPhanvi())
                };
            }
            return rows;
        } catch (Exception ex) {
            return new Object[0][0];
        }
    }

    private String formatDecimal(BigDecimal value) {
        if (value == null) {
            return "";
        }
        return value.stripTrailingZeros().toPlainString();
    }

    private String safeNumber(Number value) {
        return value == null ? "" : value.toString();
    }

    private String toCoKhong(boolean value) {
        return value ? "Có" : "Không";
    }

    private String safeText(String value) {
        return value == null ? "" : value;
    }

    private String[] buildNganhToHopFilterOptions() {
        List<NganhToHop> list = nthService.getAll();
        Set<String> khoiSet = new LinkedHashSet<>();
        Set<String> nganhSet = new LinkedHashSet<>();

        for (NganhToHop item : list) {
            String khoi = safeText(item.getMaToHop()).trim();
            String nganh = safeText(item.getMaNganh()).trim();
            if (!khoi.isEmpty()) {
                khoiSet.add(khoi);
            }
            if (!nganh.isEmpty()) {
                nganhSet.add(nganh);
            }
        }

        List<String> options = new ArrayList<>();
        options.add("Tất cả");
        for (String khoi : khoiSet) {
            options.add("Khối: " + khoi);
        }
        for (String nganh : nganhSet) {
            options.add("Ngành: " + nganh);
        }

        return options.toArray(new String[0]);
    }

    private JPanel createTabPanel(String titleStr, String[] columns, Object[][] data, String[] filterOptions) {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(8, 0, 0, 0));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);

        // Kích thước ô tìm kiếm (28) đồng bộ chuẩn với CandidateManagementPanel
        String placeholderText = "Từ khóa tìm kiếm...";
        JTextField searchInput = new JTextField(28); 
        searchInput.setText(placeholderText);
        searchInput.setFont(UIStyles.FONT_BODY);
        searchInput.setForeground(UIStyles.TEXT_MUTED);
        searchInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(6, 10, 6, 10)
        ));

        searchInput.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchInput.getText().equals(placeholderText)) {
                    searchInput.setText("");
                    searchInput.setForeground(UIStyles.TEXT_DARK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchInput.getText().trim().isEmpty()) {
                    searchInput.setText(placeholderText);
                    searchInput.setForeground(UIStyles.TEXT_MUTED);
                }
            }
        });

        toolbar.add(searchInput);

        final JComboBox<String>[] comboFilterRef = new JComboBox[] {null};

        if (filterOptions != null && filterOptions.length > 0) {
            JComboBox<String> comboFilter = new JComboBox<>(filterOptions);
            comboFilter.setFont(UIStyles.FONT_BODY_SMALL);
            toolbar.add(new JLabel("Bộ lọc:"));
            toolbar.add(comboFilter);
            comboFilterRef[0] = comboFilter;
        }

        JButton searchBtn = createButton("Tìm kiếm", UIStyles.PRIMARY);
        JButton importBtn = createButton("Import", UIStyles.SUCCESS); 
        JButton addBtn = createButton("Thêm", UIStyles.INFO);
        JButton editBtn = createButton("Sửa", UIStyles.WARNING);
        JButton deleteBtn = createButton("Xóa", UIStyles.DANGER);

        toolbar.add(searchBtn);
        toolbar.add(new javax.swing.JSeparator(javax.swing.JSeparator.VERTICAL));
        toolbar.add(importBtn);
        toolbar.add(addBtn);
        toolbar.add(editBtn);
        toolbar.add(deleteBtn);

        // Table
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        JTable table = new JTable(model);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        table.setRowHeight(32);
        table.getTableHeader().setFont(UIStyles.FONT_LABEL);
        table.getTableHeader().setBackground(new Color(247, 249, 251));
        table.setFont(UIStyles.FONT_BODY);

        // --- SỰ KIỆN NÚT BẤM ---
        
        searchBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                String term = searchInput.getText();
                String selectedFilter = comboFilterRef[0] == null ? null : (String) comboFilterRef[0].getSelectedItem();
                applyTableFilter(sorter, titleStr, term, placeholderText, selectedFilter);
            }
        });

        // Intentionally do not auto-filter on combo change.
        // Filtering is applied only when user clicks the Search button.

        importBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                MajorImportExcelDialog dialog = new MajorImportExcelDialog(
                        (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(panel),
                        titleStr
                );
                dialog.setVisible(true);

                if (dialog.isConfirmed()) {
                    try {
                        if (isQuyDoiTab(titleStr)) {
                            List<BangQuyDoi> imported = bqdService.importFromExcel(dialog.getSelectedFile().getAbsolutePath());
                            refreshTableData(model, buildQuyDoiData());
                            JOptionPane.showMessageDialog(panel,
                                    "Import thành công " + imported.size() + " dòng từ file: " + dialog.getSelectedFile().getName(),
                                    "Import Dữ Liệu", JOptionPane.INFORMATION_MESSAGE);
                        } else if (isNganhToHopTab(titleStr)) {
                            List<NganhToHop> imported = nthService.importFromExcel(dialog.getSelectedFile().getAbsolutePath());
                            refreshTableData(model, buildNganhToHopData());
                            JOptionPane.showMessageDialog(panel,
                                "Import thành công " + imported.size() + " dòng từ file: " + dialog.getSelectedFile().getName(),
                                "Import Dữ Liệu", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(panel,
                                    "Import dữ liệu thành công từ file: " + dialog.getSelectedFile().getName(),
                                    "Import Dữ Liệu", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(panel,
                                "Không đọc được file import: " + ex.getMessage(),
                                "Lỗi Import", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(panel,
                                "Import thất bại: " + ex.getMessage(),
                                "Lỗi Import", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        addBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                SharedFormDialog dialog = new SharedFormDialog(
                        (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(panel), 
                        "Thêm Mới - " + titleStr, 
                        columns
                );
                dialog.setVisible(true);

                if (dialog.isConfirmed()) {
                    Object[] formData = dialog.getData();
                    try {
                        if (isQuyDoiTab(titleStr)) {
                            BangQuyDoi created = mapToBangQuyDoi(formData);
                            created.setIdqd(null);
                            bqdService.create(created);
                            refreshTableData(model, buildQuyDoiData());
                        } else if (isNganhToHopTab(titleStr)) {
                            NganhToHop created = mapToNganhToHop(formData);
                            created.setId(null);
                            nthService.create(created);
                            refreshTableData(model, buildNganhToHopData());
                        } else {
                            model.addRow(formData);
                        }
                        JOptionPane.showMessageDialog(panel, "Thêm dữ liệu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(panel,
                                "Thêm dữ liệu thất bại: " + ex.getMessage(),
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        editBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    SharedFormDialog dialog = new SharedFormDialog(
                            (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(panel), 
                            "Chỉnh Sửa - " + titleStr, 
                            columns
                    );
                    
                    Object[] rowData = new Object[columns.length];
                    for (int i = 0; i < columns.length; i++) {
                        rowData[i] = table.getValueAt(selectedRow, i);
                    }
                    
                    dialog.setData(rowData);
                    dialog.setVisible(true);

                    if (dialog.isConfirmed()) {
                        Object[] newData = dialog.getData();
                        try {
                            if (isQuyDoiTab(titleStr)) {
                                BangQuyDoi updated = mapToBangQuyDoi(newData);
                                if (updated.getIdqd() == null) {
                                    updated.setIdqd(parseInteger(rowData[0]));
                                }
                                bqdService.update(updated);
                                refreshTableData(model, buildQuyDoiData());
                            } else if (isNganhToHopTab(titleStr)) {
                                NganhToHop updated = mapToNganhToHop(newData);
                                if (updated.getId() == null) {
                                    updated.setId(parseInteger(rowData[0]));
                                }
                                nthService.update(updated);
                                refreshTableData(model, buildNganhToHopData());
                            } else {
                                for (int i = 0; i < columns.length; i++) {
                                    model.setValueAt(newData[i], selectedRow, i);
                                }
                            }
                            JOptionPane.showMessageDialog(panel, "Cập nhật dữ liệu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(panel,
                                    "Cập nhật thất bại: " + ex.getMessage(),
                                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(panel, "Vui lòng chọn một dòng từ bảng để chỉnh sửa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Xóa sử dụng MajorDeleteConfirmDialog
        deleteBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    // Ghép cột 1 và cột 2 để tạo tên item hiển thị trong câu hỏi xóa
                    String itemName = table.getValueAt(selectedRow, 0).toString();
                    if (table.getColumnCount() > 1) {
                        itemName += " - " + table.getValueAt(selectedRow, 1).toString();
                    }

                    MajorDeleteConfirmDialog dialog = new MajorDeleteConfirmDialog(
                            (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(panel), 
                            itemName
                    );
                    dialog.setVisible(true);

                    if (dialog.isConfirmed()) {
                        try {
                            if (isQuyDoiTab(titleStr)) {
                                Integer id = parseInteger(table.getValueAt(selectedRow, 0));
                                boolean deleted = bqdService.deleteById(id);
                                if (!deleted) {
                                    throw new IllegalStateException("Không tìm thấy bản ghi để xóa.");
                                }
                                refreshTableData(model, buildQuyDoiData());
                            } else if (isNganhToHopTab(titleStr)) {
                                Integer id = parseInteger(table.getValueAt(selectedRow, 0));
                                boolean deleted = nthService.deleteById(id);
                                if (!deleted) {
                                    throw new IllegalStateException("Không tìm thấy bản ghi để xóa.");
                                }
                                refreshTableData(model, buildNganhToHopData());
                            } else {
                                model.removeRow(selectedRow);
                            }
                            JOptionPane.showMessageDialog(panel, "Đã xóa thành công mục: " + itemName, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(panel,
                                    "Xóa dữ liệu thất bại: " + ex.getMessage(),
                                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(panel, "Vui lòng chọn một dòng từ bảng để xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Table Card
        JPanel tableCard = new JPanel(new BorderLayout(0, 12));
        tableCard.setBackground(UIStyles.BG_CARD);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel tableTitle = new JLabel(titleStr);
        tableTitle.setFont(UIStyles.FONT_SUBTITLE);
        tableTitle.setForeground(UIStyles.TEXT_DARK);
        tableCard.add(tableTitle, BorderLayout.NORTH);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        // Pagination
        JPanel pagination = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 8));
        pagination.setOpaque(false);
        pagination.add(createButton("Trước", UIStyles.PRIMARY));
        pagination.add(new JLabel(" Trang 1 / 1 "));
        pagination.add(createButton("Sau", UIStyles.PRIMARY));
        tableCard.add(pagination, BorderLayout.SOUTH);

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(tableCard, BorderLayout.CENTER);

        return panel;
    }

    private boolean isQuyDoiTab(String titleStr) {
        return titleStr != null && titleStr.contains("Bảng quy đổi");
    }

    private boolean isNganhToHopTab(String titleStr) {
        return titleStr != null && titleStr.contains("Map Tổ hợp môn vào Ngành");
    }

    private void refreshTableData(DefaultTableModel model, Object[][] rows) {
        model.setRowCount(0);
        for (Object[] row : rows) {
            model.addRow(row);
        }
    }

    private void applyTableFilter(TableRowSorter<DefaultTableModel> sorter, String titleStr,
                                  String term, String placeholderText, String selectedFilter) {
        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        String keyword = term == null ? "" : term.trim();
        if (!keyword.isEmpty() && !keyword.equals(placeholderText)) {
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(keyword)));
        }

        if (selectedFilter != null) {
            String filter = selectedFilter.trim();
            boolean hasUsefulFilter = !filter.isEmpty()
                    && !filter.equalsIgnoreCase("Tất cả")
                    && !filter.equalsIgnoreCase("Loại chứng chỉ")
                    && !filter.toLowerCase().startsWith("lọc theo");

            if (hasUsefulFilter) {
                if (isNganhToHopTab(titleStr) && filter.startsWith("Khối: ")) {
                    String khoi = filter.substring("Khối: ".length()).trim();
                    filters.add(RowFilter.regexFilter("(?i)^" + Pattern.quote(khoi) + "$", 2));
                } else if (isNganhToHopTab(titleStr) && filter.startsWith("Ngành: ")) {
                    String nganh = filter.substring("Ngành: ".length()).trim();
                    filters.add(RowFilter.regexFilter("(?i)^" + Pattern.quote(nganh) + "$", 1));
                } else if (isQuyDoiTab(titleStr) && filter.toLowerCase().contains("ngoại ngữ")) {
                    // Lọc theo cột Phương Thức (index 1) cho dữ liệu quy đổi chứng chỉ ngoại ngữ.
                    filters.add(RowFilter.regexFilter("(?i)(ngoai|ielts|vstep)", 1));
                } else if (isQuyDoiTab(titleStr) && filter.toLowerCase().contains("v-sat")) {
                    // Lọc theo cột Phương Thức (index 1) cho dữ liệu kỳ thi V-SAT.
                    filters.add(RowFilter.regexFilter("(?i)(v[- ]?sat)", 1));
                } else {
                    filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(filter)));
                }
            }
        }

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    private BangQuyDoi mapToBangQuyDoi(Object[] data) {
        BangQuyDoi item = new BangQuyDoi();
        item.setIdqd(parseInteger(data[0]));
        item.setDPhuongthuc(safeText(data[1] == null ? null : data[1].toString()));
        item.setDTohop(safeText(data[2] == null ? null : data[2].toString()));
        item.setDMon(safeText(data[3] == null ? null : data[3].toString()));
        item.setDDiema(parseBigDecimal(data[4]));
        item.setDDiemb(parseBigDecimal(data[5]));
        item.setDDiemc(parseBigDecimal(data[6]));
        item.setDDiemd(parseBigDecimal(data[7]));
        item.setDMaquydoi(safeText(data[8] == null ? null : data[8].toString()));
        item.setDPhanvi(safeText(data[9] == null ? null : data[9].toString()));
        return item;
    }

    private NganhToHop mapToNganhToHop(Object[] data) {
        NganhToHop item = new NganhToHop();
        item.setId(parseInteger(data[0]));
        item.setMaNganh(safeText(data[1] == null ? null : data[1].toString()));
        item.setMaToHop(safeText(data[2] == null ? null : data[2].toString()));
        item.setThMon1(safeText(data[3] == null ? null : data[3].toString()));
        item.setHsMon1(parseByte(data[4]));
        item.setThMon2(safeText(data[5] == null ? null : data[5].toString()));
        item.setHsMon2(parseByte(data[6]));
        item.setThMon3(safeText(data[7] == null ? null : data[7].toString()));
        item.setHsMon3(parseByte(data[8]));
        item.setTbKeys(safeText(data[9] == null ? null : data[9].toString()));

        // UI hiện tại chỉ có 11 cột: sau tb_keys là dolech.
        String mon1 = normalizeSubject(item.getThMon1());
        String mon2 = normalizeSubject(item.getThMon2());
        String mon3 = normalizeSubject(item.getThMon3());
        String maToHop = safeText(item.getMaToHop()).toLowerCase();
        String tbKeys = safeText(item.getTbKeys()).toLowerCase();

        item.setN1(maToHop.contains("n1") || tbKeys.contains("n1"));
        item.setTo(hasSubject(mon1, mon2, mon3, "toan"));
        item.setLi(hasSubject(mon1, mon2, mon3, "vatly"));
        item.setHo(hasSubject(mon1, mon2, mon3, "hoahoc"));
        item.setSi(hasSubject(mon1, mon2, mon3, "sinhhoc"));
        item.setVa(hasSubject(mon1, mon2, mon3, "nguvan"));
        item.setSu(hasSubject(mon1, mon2, mon3, "lichsu"));
        item.setDi(hasSubject(mon1, mon2, mon3, "dialy"));
        item.setTi(hasSubject(mon1, mon2, mon3, "tienganh"));
        item.setKtpl(hasSubject(mon1, mon2, mon3, "kinhtephapluat"));

        boolean hasKnownSubject = Boolean.TRUE.equals(item.getTo()) || Boolean.TRUE.equals(item.getLi())
            || Boolean.TRUE.equals(item.getHo()) || Boolean.TRUE.equals(item.getSi())
            || Boolean.TRUE.equals(item.getVa()) || Boolean.TRUE.equals(item.getSu())
            || Boolean.TRUE.equals(item.getDi()) || Boolean.TRUE.equals(item.getTi())
            || Boolean.TRUE.equals(item.getKtpl());
        item.setKhac(!hasKnownSubject);
        item.setDoLech(parseBigDecimal(data != null && data.length > 10 ? data[10] : null));
        return item;
    }

    private String normalizeSubject(String value) {
        if (value == null) {
            return "";
        }
        String text = value.toLowerCase();
        text = text.replace("á", "a").replace("à", "a").replace("ả", "a").replace("ã", "a").replace("ạ", "a");
        text = text.replace("ă", "a").replace("ắ", "a").replace("ằ", "a").replace("ẳ", "a").replace("ẵ", "a").replace("ặ", "a");
        text = text.replace("â", "a").replace("ấ", "a").replace("ầ", "a").replace("ẩ", "a").replace("ẫ", "a").replace("ậ", "a");
        text = text.replace("é", "e").replace("è", "e").replace("ẻ", "e").replace("ẽ", "e").replace("ẹ", "e");
        text = text.replace("ê", "e").replace("ế", "e").replace("ề", "e").replace("ể", "e").replace("ễ", "e").replace("ệ", "e");
        text = text.replace("í", "i").replace("ì", "i").replace("ỉ", "i").replace("ĩ", "i").replace("ị", "i");
        text = text.replace("ó", "o").replace("ò", "o").replace("ỏ", "o").replace("õ", "o").replace("ọ", "o");
        text = text.replace("ô", "o").replace("ố", "o").replace("ồ", "o").replace("ổ", "o").replace("ỗ", "o").replace("ộ", "o");
        text = text.replace("ơ", "o").replace("ớ", "o").replace("ờ", "o").replace("ở", "o").replace("ỡ", "o").replace("ợ", "o");
        text = text.replace("ú", "u").replace("ù", "u").replace("ủ", "u").replace("ũ", "u").replace("ụ", "u");
        text = text.replace("ư", "u").replace("ứ", "u").replace("ừ", "u").replace("ử", "u").replace("ữ", "u").replace("ự", "u");
        text = text.replace("ý", "y").replace("ỳ", "y").replace("ỷ", "y").replace("ỹ", "y").replace("ỵ", "y");
        text = text.replace("đ", "d");
        return text.replaceAll("[^a-z0-9]", "");
    }

    private boolean hasSubject(String mon1, String mon2, String mon3, String key) {
        return mon1.contains(key) || mon2.contains(key) || mon3.contains(key);
    }

    private Integer parseInteger(Object value) {
        if (value == null) {
            return null;
        }
        String text = value.toString().trim();
        if (text.isEmpty()) {
            return null;
        }
        return Integer.valueOf(text);
    }

    private Byte parseByte(Object value) {
        if (value == null) {
            return null;
        }
        String text = value.toString().trim();
        if (text.isEmpty()) {
            return null;
        }
        return Byte.valueOf(text);
    }

    private BigDecimal parseBigDecimal(Object value) {
        if (value == null) {
            return null;
        }
        String text = value.toString().trim();
        if (text.isEmpty()) {
            return null;
        }
        return new BigDecimal(text);
    }

    private boolean parseBoolean(Object value) {
        if (value == null) {
            return false;
        }
        String text = value.toString().trim().toLowerCase();
        return "1".equals(text)
                || "true".equals(text)
                || "yes".equals(text)
                || "y".equals(text)
                || "co".equals(text)
                || "có".equals(text)
                || "x".equals(text);
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(UIStyles.FONT_BODY);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        return btn;
    }
}