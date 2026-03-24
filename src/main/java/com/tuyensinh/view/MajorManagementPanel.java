package com.tuyensinh.view;

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
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

/**
 * Panel Quản lý Ngành & Tổ hợp - Bao gồm 4 tab chức năng con
 * Đã đồng bộ hoàn toàn kích thước, bố cục với CandidateManagementPanel
 * Sử dụng MajorDeleteConfirmDialog và MajorImportExcelDialog chuẩn của hệ thống.
 */
public class MajorManagementPanel extends JPanel {

    public MajorManagementPanel() {
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
        String[] colsNganhToHop = {"Mã Ngành", "Tên Ngành", "Tổ Hợp Áp Dụng", "Môn Chính", "Ghi Chú"};
        Object[][] dataNganhToHop = {
                {"7480201", "Công nghệ thông tin", "A00, A01, D01, D07", "Toán", "Nhân hệ số 2 môn chính"},
                {"7220201", "Ngôn ngữ Anh", "D01, D14, D15", "Tiếng Anh", "Nhân hệ số 2 môn chính"},
                {"7340101", "Quản trị kinh doanh", "A00, A01, D01", "Không", ""},
                {"7140201", "Giáo dục Mầm non", "M01, M02", "Năng khiếu", "Yêu cầu thi năng khiếu"}
        };
        tabbedPane.addTab("QL Ngành - Tổ Hợp", createTabPanel("Map Tổ hợp môn vào Ngành", colsNganhToHop, dataNganhToHop, new String[]{"Lọc theo Khối", "A00", "A01", "D01", "C00", "Năng Khiếu"}));

        // --- TAB 4: QL Bảng Quy Đổi ---
        String[] colsQuyDoi = {"Loại Kỳ Thi/Chứng Chỉ", "Mức Điểm Đạt Được", "Điểm Quy Đổi (Thang 10)", "Ghi Chú"};
        Object[][] dataQuyDoi = {
                {"IELTS", "5.0", "7.0", "Trích: Ds quy doi tieng Anh"},
                {"IELTS", "5.5", "8.0", "Trích: Ds quy doi tieng Anh"},
                {"IELTS", "6.0", "9.0", "Trích: Ds quy doi tieng Anh"},
                {"IELTS", ">= 6.5", "10.0", "Trích: Ds quy doi tieng Anh"},
                {"VSTEP", "Bậc 3", "7.0", "Trích: Ds quy doi tieng Anh"},
                {"V-SAT", "75 / 150", "5.0", "Trích: Quy doi diem thi V-SAT"},
                {"V-SAT", "120 / 150", "8.0", "Trích: Quy doi diem thi V-SAT"},
                {"V-SAT", "150 / 150", "10.0", "Trích: Quy doi diem thi V-SAT"}
        };
        tabbedPane.addTab("QL Bảng Quy Đổi", createTabPanel("Bảng quy đổi điểm Ngoại ngữ & V-SAT", colsQuyDoi, dataQuyDoi, new String[]{"Loại chứng chỉ", "Ngoại Ngữ (IELTS/VSTEP)", "Kỳ thi V-SAT"}));

        add(tabbedPane, BorderLayout.CENTER);
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

        if (filterOptions != null && filterOptions.length > 0) {
            JComboBox<String> comboFilter = new JComboBox<>(filterOptions);
            comboFilter.setFont(UIStyles.FONT_BODY_SMALL);
            toolbar.add(new JLabel("Bộ lọc:"));
            toolbar.add(comboFilter);
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
        table.setRowHeight(32);
        table.getTableHeader().setFont(UIStyles.FONT_LABEL);
        table.getTableHeader().setBackground(new Color(247, 249, 251));
        table.setFont(UIStyles.FONT_BODY);

        // --- SỰ KIỆN NÚT BẤM ---
        
        searchBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                String term = searchInput.getText();
                if (term.equals(placeholderText) || term.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Vui lòng nhập từ khóa để tìm kiếm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });

        importBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                MajorImportExcelDialog dialog = new MajorImportExcelDialog(
                        (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(panel),
                        titleStr
                );
                dialog.setVisible(true);

                if (dialog.isConfirmed()) {
                    JOptionPane.showMessageDialog(panel, 
                            "Import dữ liệu thành công từ file: " + dialog.getSelectedFile().getName(), 
                            "Import Dữ Liệu", JOptionPane.INFORMATION_MESSAGE);
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
                    model.addRow(dialog.getData());
                    JOptionPane.showMessageDialog(panel, "Thêm dữ liệu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
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
                        for (int i = 0; i < columns.length; i++) {
                            model.setValueAt(newData[i], selectedRow, i);
                        }
                        JOptionPane.showMessageDialog(panel, "Cập nhật dữ liệu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
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
                        model.removeRow(selectedRow); 
                        JOptionPane.showMessageDialog(panel, "Đã xóa thành công mục: " + itemName, "Thành công", JOptionPane.INFORMATION_MESSAGE);
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