package com.tuyensinh.view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

public class CandidateManagementPanel extends JPanel {
    private final DefaultTableModel tableModel;
    private final JTable table;

    public CandidateManagementPanel() {
        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(UIStyles.BG_APP);

        // Title
        JLabel title = new JLabel("Quản Lý Thí Sinh");
        title.setFont(UIStyles.FONT_TITLE);
        title.setForeground(UIStyles.TEXT_DARK);
        add(title, BorderLayout.NORTH);

        // Search & Actions
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);

        JTextField searchInput = new JTextField(28);
        searchInput.setText("Tìm CCCD, họ tên...");
        searchInput.setFont(UIStyles.FONT_BODY);
        searchInput.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyles.BORDER),
            new EmptyBorder(6, 10, 6, 10)
        ));

        JButton searchBtn = createButton("Tìm kiếm", UIStyles.PRIMARY);
        searchBtn.addActionListener(e -> handleSearch(searchInput.getText()));

        JButton importBtn = createButton("Import", UIStyles.SUCCESS);
        importBtn.addActionListener(e -> handleImport());

        JButton addBtn = createButton("Thêm", UIStyles.INFO);
        addBtn.addActionListener(e -> handleAdd());

        JButton editBtn = createButton("Sửa", UIStyles.WARNING);
        editBtn.addActionListener(e -> handleEdit());

        JButton deleteBtn = createButton("Xóa", UIStyles.DANGER);
        deleteBtn.addActionListener(e -> handleDelete());
        toolbar.add(searchInput);
        toolbar.add(searchBtn);
        toolbar.add(new javax.swing.JSeparator(javax.swing.JSeparator.VERTICAL));
        toolbar.add(importBtn);
        toolbar.add(addBtn);
        toolbar.add(editBtn);
        toolbar.add(deleteBtn);

        // Table - đầy đủ các trường từ database
        String[] cols = {"CCCD", "Số báo danh", "Họ", "Tên", "Ngày sinh", "Giới tính", "Email", "Điện thoại", "Nơi sinh", "Đối tượng", "Khu vực"};
        Object[][] data = {
                {"079123456789", "B0001", "Nguyễn", "Văn A", "12/08/2008", "Nam", "a@gmail.com", "0912 111 222", "TP.HCM", "KV1", "KV1"},
                {"079123456790", "B0002", "Trần", "Thị B", "20/11/2008", "Nữ", "b@gmail.com", "0903 222 333", "Hà Nội", "KV2", "KV2"},
                {"079123456791", "B0003", "Lê", "Văn C", "01/03/2008", "Nam", "c@gmail.com", "0907 333 444", "Đà Nẵng", "KV3", "KV3"},
                {"079123456792", "B0004", "Phạm", "Thị D", "27/05/2008", "Nữ", "d@gmail.com", "0938 555 666", "Cần Thơ", "KV2NT", "KV2NT"},
                {"079123456793", "B0005", "Hoàng", "Văn E", "15/12/2008", "Nam", "e@gmail.com", "0945 777 888", "Hải Phòng", "KV1", "KV1"}
        };

        tableModel = new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.getTableHeader().setFont(UIStyles.FONT_LABEL);
        table.getTableHeader().setBackground(new Color(247, 249, 251));
        table.setFont(UIStyles.FONT_BODY);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JPanel tableCard = new JPanel(new BorderLayout(0, 12));
        tableCard.setBackground(UIStyles.BG_CARD);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel tableTitle = new JLabel("Danh sách thí sinh (Phân trang 20 dòng/trang)");
        tableTitle.setFont(UIStyles.FONT_SUBTITLE);
        tableTitle.setForeground(UIStyles.TEXT_DARK);
        tableCard.add(tableTitle, BorderLayout.NORTH);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        // Pagination
        JPanel pagination = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 8));
        pagination.setOpaque(false);
        pagination.add(createButton("Trước", UIStyles.PRIMARY));
        pagination.add(new JLabel(" Trang 1 / 10 "));
        pagination.add(createButton("Sau", UIStyles.PRIMARY));
        tableCard.add(pagination, BorderLayout.SOUTH);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BorderLayout(0, 12));
        center.add(toolbar, BorderLayout.NORTH);
        center.add(tableCard, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    private void handleAdd() {
        CandidateFormDialog dialog = new CandidateFormDialog(getTopLevelAncestor() instanceof java.awt.Frame ? 
                (java.awt.Frame) getTopLevelAncestor() : null, "Thêm Thí Sinh", false);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Object[] newRow = {
                    dialog.getCCCD(),
                    dialog.getSbaodanh(),
                    dialog.getHo(),
                    dialog.getTen(),
                    dialog.getNgaysinh(),
                    dialog.getGioitinh(),
                    dialog.getEmail(),
                    dialog.getDienthoai(),
                    dialog.getNoisinh(),
                    dialog.getDoituong(),
                    dialog.getKhuvuc()
            };
            tableModel.addRow(newRow);
            javax.swing.JOptionPane.showMessageDialog(this, "Thêm thí sinh thành công!", "Thành công", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleEdit() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng chọn thí sinh để sửa!", "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        CandidateFormDialog dialog = new CandidateFormDialog(getTopLevelAncestor() instanceof java.awt.Frame ? 
                (java.awt.Frame) getTopLevelAncestor() : null, "Sửa Thông Tin Thí Sinh", true);
        
        dialog.setData(
                (String) tableModel.getValueAt(selectedRow, 0),
                (String) tableModel.getValueAt(selectedRow, 1),
                (String) tableModel.getValueAt(selectedRow, 2),
                (String) tableModel.getValueAt(selectedRow, 3),
                (String) tableModel.getValueAt(selectedRow, 4),
                (String) tableModel.getValueAt(selectedRow, 5),
                (String) tableModel.getValueAt(selectedRow, 6),
                (String) tableModel.getValueAt(selectedRow, 7),
                (String) tableModel.getValueAt(selectedRow, 8),
                (String) tableModel.getValueAt(selectedRow, 9),
                (String) tableModel.getValueAt(selectedRow, 10)
        );
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            tableModel.setValueAt(dialog.getSbaodanh(), selectedRow, 1);
            tableModel.setValueAt(dialog.getHo(), selectedRow, 2);
            tableModel.setValueAt(dialog.getTen(), selectedRow, 3);
            tableModel.setValueAt(dialog.getNgaysinh(), selectedRow, 4);
            tableModel.setValueAt(dialog.getGioitinh(), selectedRow, 5);
            tableModel.setValueAt(dialog.getEmail(), selectedRow, 6);
            tableModel.setValueAt(dialog.getDienthoai(), selectedRow, 7);
            tableModel.setValueAt(dialog.getNoisinh(), selectedRow, 8);
            tableModel.setValueAt(dialog.getDoituong(), selectedRow, 9);
            tableModel.setValueAt(dialog.getKhuvuc(), selectedRow, 10);
            javax.swing.JOptionPane.showMessageDialog(this, "Cập nhật thí sinh thành công!", "Thành công", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng chọn thí sinh để xóa!", "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        String candidateName = tableModel.getValueAt(selectedRow, 2) + " " + tableModel.getValueAt(selectedRow, 3);
        DeleteConfirmDialog dialog = new DeleteConfirmDialog(getTopLevelAncestor() instanceof java.awt.Frame ? 
                (java.awt.Frame) getTopLevelAncestor() : null, candidateName);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            tableModel.removeRow(selectedRow);
            javax.swing.JOptionPane.showMessageDialog(this, "Xóa thí sinh thành công!", "Thành công", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleImport() {
        ImportExcelDialog dialog = new ImportExcelDialog(getTopLevelAncestor() instanceof java.awt.Frame ? 
                (java.awt.Frame) getTopLevelAncestor() : null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                    "Import từ file: " + dialog.getSelectedFile().getName() + "\n" +
                    "Lưu ý: Chức năng import thực sự cần kết nối database và Apache POI library.", 
                    "Import", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleSearch(String searchTerm) {
        if (searchTerm.isEmpty() || searchTerm.equals("Tìm CCCD, họ tên...")) {
            javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa tìm kiếm!", "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }
        javax.swing.JOptionPane.showMessageDialog(this, "Tìm kiếm: " + searchTerm + "\n(Cần kết nối database)", "Tìm kiếm", javax.swing.JOptionPane.INFORMATION_MESSAGE);
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
