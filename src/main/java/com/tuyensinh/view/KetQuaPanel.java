package com.tuyensinh.view;

import com.tuyensinh.model.Nganh;
import com.tuyensinh.model.NguyenVong;
import com.tuyensinh.model.ThiSinh;
import com.tuyensinh.service.NganhService;
import com.tuyensinh.service.ThiSinhService;
import com.tuyensinh.service.XetTuyenService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class KetQuaPanel extends JPanel {

    private final XetTuyenService xetTuyenService = new XetTuyenService();
    private final NganhService nganhService = new NganhService();
    private final ThiSinhService thiSinhService = new ThiSinhService();

    private DefaultTableModel pivotTableModel;
    private JTable pivotTable;
    private DefaultTableModel resultTableModel;
    private JTable resultTable;

    private JComboBox<String> nganhFilterCombo;
    private JComboBox<String> phuongThucFilterCombo;
    private JTextField searchField;
    private JCheckBox onlyTrungTuyenCheck;

    private JPanel detailCard;
    private final JTextField detailCccdField = new JTextField();
    private final JTextField detailHoTenField = new JTextField();
    private final JTextField detailNganhField = new JTextField();
    private final JTextField detailPhuongThucField = new JTextField();
    private final JTextField detailToHopField = new JTextField();
    private final JTextField detailDiemField = new JTextField();
    private final JTextField detailKetQuaField = new JTextField();

    private List<NguyenVong> currentResultList = new ArrayList<>();
    private Map<String, ThiSinh> thiSinhMap = new HashMap<>();
    private Map<String, Nganh> nganhMap = new HashMap<>();

    public KetQuaPanel() {
        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(UIStyles.BG_APP);

        JLabel title = new JLabel("Kết quả & Thống kê xét tuyển");
        title.setFont(UIStyles.FONT_TITLE);
        title.setForeground(UIStyles.TEXT_DARK);
        add(title, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, createPivotPanel(), createBottomSplit());
        splitPane.setResizeWeight(0.4);
        splitPane.setDividerSize(6);
        splitPane.setBorder(null);
        add(splitPane, BorderLayout.CENTER);

        loadData();
    }

    private void loadData() {
        thiSinhMap.clear();
        for (ThiSinh ts : thiSinhService.getAll()) {
            thiSinhMap.put(ts.getCccd(), ts);
        }
        nganhMap.clear();
        for (Nganh nganh : nganhService.getAll()) {
            nganhMap.put(nganh.getManganh(), nganh);
        }
        loadPivotTable();
        loadResultTable();
        loadFilterCombos();
    }

    public void refreshData() {
        loadData();
    }

    private JPanel createPivotPanel() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(UIStyles.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel cardTitle = new JLabel("Thống kê trúng tuyển theo Ngành × Phương thức");
        cardTitle.setFont(UIStyles.FONT_SUBTITLE);
        cardTitle.setForeground(UIStyles.TEXT_DARK);
        header.add(cardTitle, BorderLayout.WEST);

        JButton exportBtn = createButton("Xuất Excel thống kê", UIStyles.SUCCESS);
        exportBtn.addActionListener(e -> exportPivotExcel());
        header.add(exportBtn, BorderLayout.EAST);

        card.add(header, BorderLayout.NORTH);

        pivotTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        pivotTable = new JTable(pivotTableModel);
        pivotTable.setRowHeight(30);
        pivotTable.getTableHeader().setFont(UIStyles.FONT_LABEL);
        pivotTable.getTableHeader().setBackground(new java.awt.Color(247, 249, 251));
        pivotTable.setFont(UIStyles.FONT_BODY);
        pivotTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        pivotTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = pivotTable.rowAtPoint(e.getPoint());
                int col = pivotTable.columnAtPoint(e.getPoint());
                if (row >= 0 && col >= 0) {
                    handlePivotClick(row, col);
                }
            }
        });

        card.add(new JScrollPane(pivotTable), BorderLayout.CENTER);
        return card;
    }

    private JSplitPane createBottomSplit() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createResultPanel(), createDetailPanel());
        split.setResizeWeight(0.65);
        split.setDividerSize(6);
        split.setBorder(null);
        return split;
    }

    private JPanel createResultPanel() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(UIStyles.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel cardTitle = new JLabel("Danh sách trúng tuyển chi tiết");
        cardTitle.setFont(UIStyles.FONT_SUBTITLE);
        cardTitle.setForeground(UIStyles.TEXT_DARK);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filterPanel.setOpaque(false);

        filterPanel.add(new JLabel("Ngành:"));
        nganhFilterCombo = new JComboBox<>();
        nganhFilterCombo.setFont(UIStyles.FONT_SMALL);
        nganhFilterCombo.addActionListener(e -> applyResultFilter());
        filterPanel.add(nganhFilterCombo);

        filterPanel.add(new JLabel("PT:"));
        phuongThucFilterCombo = new JComboBox<>(new String[]{"Tất cả", "THPT", "DGNL", "VSAT", "XTT"});
        phuongThucFilterCombo.setFont(UIStyles.FONT_SMALL);
        phuongThucFilterCombo.addActionListener(e -> applyResultFilter());
        filterPanel.add(phuongThucFilterCombo);

        searchField = new JTextField(15);
        searchField.setFont(UIStyles.FONT_SMALL);
        searchField.addActionListener(e -> applyResultFilter());
        filterPanel.add(searchField);

        onlyTrungTuyenCheck = new JCheckBox("Chỉ TT");
        onlyTrungTuyenCheck.setFont(UIStyles.FONT_SMALL);
        onlyTrungTuyenCheck.setSelected(true);
        onlyTrungTuyenCheck.addActionListener(e -> applyResultFilter());
        filterPanel.add(onlyTrungTuyenCheck);

        JButton exportBtn = createButton("Xuất Excel", UIStyles.SUCCESS);
        exportBtn.addActionListener(e -> exportResultExcel());
        filterPanel.add(exportBtn);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(cardTitle, BorderLayout.WEST);
        topPanel.add(filterPanel, BorderLayout.SOUTH);

        card.add(topPanel, BorderLayout.NORTH);

        String[] cols = {"CCCD", "Họ tên", "Mã ngành", "Tên ngành", "NV", "Phương thức", "Tổ hợp", "Điểm XT", "Kết quả"};
        resultTableModel = new DefaultTableModel(null, cols) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        resultTable = new JTable(resultTableModel);
        resultTable.setRowHeight(30);
        resultTable.getTableHeader().setFont(UIStyles.FONT_LABEL);
        resultTable.getTableHeader().setBackground(new java.awt.Color(247, 249, 251));
        resultTable.setFont(UIStyles.FONT_BODY);
        resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        resultTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        resultTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        resultTable.getColumnModel().getColumn(2).setPreferredWidth(90);
        resultTable.getColumnModel().getColumn(3).setPreferredWidth(180);
        resultTable.getColumnModel().getColumn(4).setPreferredWidth(50);
        resultTable.getColumnModel().getColumn(5).setPreferredWidth(90);
        resultTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        resultTable.getColumnModel().getColumn(7).setPreferredWidth(80);
        resultTable.getColumnModel().getColumn(8).setPreferredWidth(100);
        resultTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateDetailFromSelection();
        });

        card.add(new JScrollPane(resultTable), BorderLayout.CENTER);
        return card;
    }

    private JPanel createDetailPanel() {
        detailCard = new JPanel(new BorderLayout(0, 12));
        detailCard.setBackground(UIStyles.BG_CARD);
        detailCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));
        detailCard.setPreferredSize(new Dimension(380, 0));
        detailCard.setMinimumSize(new Dimension(380, 0));

        JLabel cardTitle = new JLabel("Chi tiết kết quả");
        cardTitle.setFont(UIStyles.FONT_SUBTITLE);
        cardTitle.setForeground(UIStyles.TEXT_DARK);
        detailCard.add(cardTitle, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridLayout(0, 1, 0, 8));
        fields.setOpaque(false);
        fields.add(labelWithField("CCCD", detailCccdField));
        fields.add(labelWithField("Họ tên", detailHoTenField));
        fields.add(labelWithField("Ngành", detailNganhField));
        fields.add(labelWithField("Phương thức", detailPhuongThucField));
        fields.add(labelWithField("Tổ hợp", detailToHopField));
        fields.add(labelWithField("Điểm xét tuyển", detailDiemField));
        fields.add(labelWithField("Kết quả", detailKetQuaField));

        JScrollPane scroll = new JScrollPane(fields);
        scroll.setBorder(null);
        detailCard.add(scroll, BorderLayout.CENTER);

        configureReadOnlyField(detailCccdField);
        configureReadOnlyField(detailHoTenField);
        configureReadOnlyField(detailNganhField);
        configureReadOnlyField(detailPhuongThucField);
        configureReadOnlyField(detailToHopField);
        configureReadOnlyField(detailDiemField);
        configureReadOnlyField(detailKetQuaField);

        return detailCard;
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
        field.setBackground(new java.awt.Color(247, 249, 251));
        field.setFont(UIStyles.FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    private void loadPivotTable() {
        pivotTableModel.setRowCount(0);
        pivotTableModel.setColumnCount(0);

        Map<String, Map<String, Long>> pivotData = xetTuyenService.getThongKePivot();
        Set<String> allPhuongThuc = new TreeSet<>();
        for (Map<String, Long> ptMap : pivotData.values()) {
            allPhuongThuc.addAll(ptMap.keySet());
        }

        List<String> ptList = new ArrayList<>(allPhuongThuc);
        ptList.add("Tổng");

        String[] headers = new String[ptList.size() + 1];
        headers[0] = "Ngành";
        for (int i = 0; i < ptList.size(); i++) {
            headers[i + 1] = ptList.get(i);
        }
        for (String h : headers) {
            pivotTableModel.addColumn(h);
        }

        Map<String, Long> columnTotals = new LinkedHashMap<>();
        for (String pt : ptList) columnTotals.put(pt, 0L);
        long grandTotal = 0;

        for (Map.Entry<String, Map<String, Long>> entry : pivotData.entrySet()) {
            String nganh = entry.getKey();
            Map<String, Long> ptMap = entry.getValue();
            Object[] row = new Object[headers.length];
            Nganh n = nganhMap.get(nganh);
            row[0] = nganh + (n != null ? " - " + n.getTennganh() : "");

            long rowTotal = 0;
            for (int i = 0; i < ptList.size() - 1; i++) {
                String pt = ptList.get(i);
                Long count = ptMap.getOrDefault(pt, 0L);
                row[i + 1] = count;
                rowTotal += count;
                columnTotals.put(pt, columnTotals.getOrDefault(pt, 0L) + count);
            }
            row[ptList.size()] = rowTotal;
            columnTotals.put("Tổng", columnTotals.get("Tổng") + rowTotal);
            grandTotal += rowTotal;
            pivotTableModel.addRow(row);
        }

        Object[] totalRow = new Object[headers.length];
        totalRow[0] = "Tổng toàn trường";
        for (int i = 0; i < ptList.size(); i++) {
            totalRow[i + 1] = columnTotals.get(ptList.get(i));
        }
        pivotTableModel.addRow(totalRow);
    }

    private void loadResultTable() {
        currentResultList = xetTuyenService.getTrungTuyenByFilter(null, null);
        renderResultTable(currentResultList);
    }

    private void renderResultTable(List<NguyenVong> list) {
        resultTableModel.setRowCount(0);
        for (NguyenVong nv : list) {
            ThiSinh ts = thiSinhMap.get(nv.getNnCccd());
            String hoTen = ts != null ? ts.getHo() + " " + ts.getTen() : "";
            Nganh nganh = nganhMap.get(nv.getNvManganh());
            String tenNganh = nganh != null ? nganh.getTennganh() : nv.getNvManganh();

            resultTableModel.addRow(new Object[]{
                    nv.getNnCccd(),
                    hoTen,
                    nv.getNvManganh(),
                    tenNganh,
                    nv.getNvTt() != null ? "NV" + nv.getNvTt() : "",
                    nv.getTtPhuongthuc(),
                    nv.getTtThm(),
                    formatDecimal(nv.getDiemXettuyen()),
                    nv.getNvKetqua()
            });
        }
    }

    private void loadFilterCombos() {
        nganhFilterCombo.removeAllItems();
        nganhFilterCombo.addItem("Tất cả");
        for (Nganh nganh : nganhService.getAll()) {
            nganhFilterCombo.addItem(nganh.getManganh() + " - " + nganh.getTennganh());
        }
    }

    private void applyResultFilter() {
        String selectedNganh = (String) nganhFilterCombo.getSelectedItem();
        String manganh = null;
        if (selectedNganh != null && !selectedNganh.equals("Tất cả")) {
            manganh = selectedNganh.split(" - ")[0];
        }

        String selectedPT = (String) phuongThucFilterCombo.getSelectedItem();
        String phuongThuc = (selectedPT != null && !selectedPT.equals("Tất cả")) ? selectedPT : null;

        String keyword = searchField.getText().trim().toLowerCase();
        boolean onlyTT = onlyTrungTuyenCheck.isSelected();

        List<NguyenVong> filtered = xetTuyenService.getTrungTuyenByFilter(manganh, phuongThuc).stream()
                .filter(nv -> !onlyTT || "TRUNG_TUYEN".equals(nv.getNvKetqua()))
                .filter(nv -> {
                    if (keyword.isEmpty()) return true;
                    ThiSinh ts = thiSinhMap.get(nv.getNnCccd());
                    String hoTen = ts != null ? (ts.getHo() + " " + ts.getTen()).toLowerCase() : "";
                    return nv.getNnCccd().toLowerCase().contains(keyword) ||
                            nv.getNvManganh().toLowerCase().contains(keyword) ||
                            hoTen.contains(keyword);
                })
                .collect(Collectors.toList());

        renderResultTable(filtered);
    }

    private void handlePivotClick(int row, int col) {
        String nganhCell = (String) pivotTableModel.getValueAt(row, 0);
        String colHeader = pivotTableModel.getColumnName(col);

        if (nganhCell == null || colHeader == null) return;

        String manganh = null;
        if (!nganhCell.equals("Tổng toàn trường")) {
            manganh = nganhCell.split(" - ")[0];
        }

        String phuongThuc = null;
        if (!colHeader.equals("Tổng")) {
            phuongThuc = colHeader;
        }

        nganhFilterCombo.setSelectedItem(manganh != null ?
                nganhMap.containsKey(manganh) ? manganh + " - " + nganhMap.get(manganh).getTennganh() : manganh : "Tất cả");
        int ptIdx = phuongThucFilterCombo.getSelectedIndex();
        for (int i = 0; i < phuongThucFilterCombo.getItemCount(); i++) {
            if (phuongThucFilterCombo.getItemAt(i).equals(phuongThuc != null ? phuongThuc : "Tất cả")) {
                phuongThucFilterCombo.setSelectedIndex(i);
                break;
            }
        }
        applyResultFilter();
    }

    private void updateDetailFromSelection() {
        int row = resultTable.getSelectedRow();
        if (row < 0) {
            clearDetail();
            return;
        }

        String cccd = (String) resultTableModel.getValueAt(row, 0);
        String manganh = (String) resultTableModel.getValueAt(row, 2);
        String phuongThuc = (String) resultTableModel.getValueAt(row, 5);

        NguyenVong selectedNV = xetTuyenService.getTrungTuyenByFilter(manganh, phuongThuc).stream()
                .filter(nv -> cccd.equals(nv.getNnCccd()))
                .findFirst().orElse(null);

        if (selectedNV != null) {
            ThiSinh ts = thiSinhMap.get(cccd);
            Nganh nganh = nganhMap.get(manganh);

            detailCccdField.setText(cccd);
            detailHoTenField.setText(ts != null ? ts.getHo() + " " + ts.getTen() : "");
            detailNganhField.setText(manganh + (nganh != null ? " - " + nganh.getTennganh() : ""));
            detailPhuongThucField.setText(selectedNV.getTtPhuongthuc());
            detailToHopField.setText(selectedNV.getTtThm());
            detailDiemField.setText(formatDecimal(selectedNV.getDiemXettuyen()));
            detailKetQuaField.setText(selectedNV.getNvKetqua());
        }
    }

    private void clearDetail() {
        detailCccdField.setText("");
        detailHoTenField.setText("");
        detailNganhField.setText("");
        detailPhuongThucField.setText("");
        detailToHopField.setText("");
        detailDiemField.setText("");
        detailKetQuaField.setText("");
    }

    private void exportPivotExcel() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Xuất thống kê");
        chooser.setSelectedFile(new File("ThongKeXetTuyen.xlsx"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        if (!file.getName().endsWith(".xlsx")) {
            file = new File(file.getAbsolutePath() + ".xlsx");
        }

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Thống kê");
            for (int r = 0; r < pivotTableModel.getRowCount(); r++) {
                Row row = sheet.createRow(r);
                for (int c = 0; c < pivotTableModel.getColumnCount(); c++) {
                    Cell cell = row.createCell(c);
                    Object val = pivotTableModel.getValueAt(r, c);
                    if (val instanceof Number) {
                        cell.setCellValue(((Number) val).doubleValue());
                    } else {
                        cell.setCellValue(val != null ? val.toString() : "");
                    }
                }
            }
            for (int c = 0; c < pivotTableModel.getColumnCount(); c++) {
                sheet.autoSizeColumn(c);
            }
            try (FileOutputStream fos = new FileOutputStream(file)) {
                wb.write(fos);
            }
            JOptionPane.showMessageDialog(this, "Xuất thành công: " + file.getName(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi xuất: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportResultExcel() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Xuất danh sách trúng tuyển");
        chooser.setSelectedFile(new File("DanhSachTrungTuyen.xlsx"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        if (!file.getName().endsWith(".xlsx")) {
            file = new File(file.getAbsolutePath() + ".xlsx");
        }

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Danh sách trúng tuyển");
            Row headerRow = sheet.createRow(0);
            String[] headers = {"CCCD", "Họ tên", "Mã ngành", "Tên ngành", "NV", "Phương thức", "Tổ hợp", "Điểm XT", "Kết quả"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            for (int r = 0; r < resultTableModel.getRowCount(); r++) {
                Row row = sheet.createRow(r + 1);
                for (int c = 0; c < resultTableModel.getColumnCount(); c++) {
                    Cell cell = row.createCell(c);
                    Object val = resultTableModel.getValueAt(r, c);
                    if (val instanceof Number) {
                        cell.setCellValue(((Number) val).doubleValue());
                    } else {
                        cell.setCellValue(val != null ? val.toString() : "");
                    }
                }
            }
            for (int c = 0; c < headers.length; c++) {
                sheet.autoSizeColumn(c);
            }
            try (FileOutputStream fos = new FileOutputStream(file)) {
                wb.write(fos);
            }
            JOptionPane.showMessageDialog(this, "Xuất thành công: " + file.getName(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi xuất: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton createButton(String text, java.awt.Color color) {
        JButton btn = new JButton(text);
        btn.setFont(UIStyles.FONT_SMALL);
        btn.setBackground(color);
        btn.setForeground(java.awt.Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private String formatDecimal(java.math.BigDecimal value) {
        if (value == null) return "";
        return value.stripTrailingZeros().toPlainString();
    }
}
