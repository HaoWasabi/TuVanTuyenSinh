package com.tuyensinh.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UserManagementPanel extends JPanel {
    private final DefaultTableModel tableModel;
    private final JTable table;

    public UserManagementPanel() {
        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(UIStyles.BG_APP);

        // Title
        JLabel title = new JLabel("Quản Lý Người Dùng");
        title.setFont(UIStyles.FONT_TITLE);
        title.setForeground(UIStyles.TEXT_DARK);
        add(title, BorderLayout.NORTH);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);

        JTextField searchInput = new JTextField(28);
        searchInput.setText("Tìm họ tên, email...");
        searchInput.setFont(UIStyles.FONT_BODY);
        searchInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(6, 10, 6, 10)
        ));

        JButton searchBtn = createButton("Tìm kiếm", UIStyles.PRIMARY);
        searchBtn.addActionListener(e -> handleSearch(searchInput.getText()));

        JButton addBtn = createButton("Thêm", UIStyles.INFO);
        addBtn.addActionListener(e -> handleAdd());

        JButton editBtn = createButton("Sửa", UIStyles.WARNING);
        editBtn.addActionListener(e -> handleEdit());

        JButton deleteBtn = createButton("Xóa", UIStyles.DANGER);
        deleteBtn.addActionListener(e -> handleDelete());

        toolbar.add(searchInput);
        toolbar.add(searchBtn);
        toolbar.add(new JSeparator(JSeparator.VERTICAL));
        toolbar.add(addBtn);
        toolbar.add(editBtn);
        toolbar.add(deleteBtn);

        // Table
        String[] cols = {"Mã ND", "Họ tên", "Giới tính", "Ngày sinh", "Email", "Điện thoại", "Vai trò", "Trạng thái"};
        Object[][] data = {
                {"ND001", "Nguyễn Văn A", "Nam", "12/08/2006", "a@gmail.com", "0912111222", "Admin", "Hoạt động"},
                {"ND002", "Trần Thị B", "Nữ", "20/11/2006", "b@gmail.com", "0903222333", "Nhân viên", "Hoạt động"},
                {"ND003", "Lê Văn C", "Nam", "01/03/2006", "c@gmail.com", "0907333444", "Thí sinh", "Đã khóa"}
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

        JLabel tableTitle = new JLabel("Danh sách người dùng");
        tableTitle.setFont(UIStyles.FONT_SUBTITLE);
        tableTitle.setForeground(UIStyles.TEXT_DARK);
        tableCard.add(tableTitle, BorderLayout.NORTH);

        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        // 🔥 PHÂN TRANG (đã thêm)
        JPanel pagination = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 8));
        pagination.setOpaque(false);
        pagination.add(createButton("Trước", UIStyles.PRIMARY));
        pagination.add(new JLabel(" Trang 1 / 5 "));
        pagination.add(createButton("Sau", UIStyles.PRIMARY));
        tableCard.add(pagination, BorderLayout.SOUTH);

        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);
        center.add(toolbar, BorderLayout.NORTH);
        center.add(tableCard, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    private void handleAdd() {
        JTextField nameField = new JTextField();
        JTextField genderField = new JTextField();
        JTextField dobField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField roleField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Họ tên:"));
        panel.add(nameField);
        panel.add(new JLabel("Giới tính:"));
        panel.add(genderField);
        panel.add(new JLabel("Ngày sinh:"));
        panel.add(dobField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Điện thoại:"));
        panel.add(phoneField);
        panel.add(new JLabel("Vai trò:"));
        panel.add(roleField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Thêm người dùng", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            Object[] newRow = {
                    "ND00" + (tableModel.getRowCount() + 1),
                    nameField.getText(),
                    genderField.getText(),
                    dobField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    roleField.getText(),
                    "Hoạt động"
            };
            tableModel.addRow(newRow);
        }
    }

    private void handleEdit() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        JTextField nameField = new JTextField((String) tableModel.getValueAt(row, 1));
        JTextField genderField = new JTextField((String) tableModel.getValueAt(row, 2));
        JTextField dobField = new JTextField((String) tableModel.getValueAt(row, 3));
        JTextField emailField = new JTextField((String) tableModel.getValueAt(row, 4));
        JTextField phoneField = new JTextField((String) tableModel.getValueAt(row, 5));
        JTextField roleField = new JTextField((String) tableModel.getValueAt(row, 6));

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Họ tên:"));
        panel.add(nameField);
        panel.add(new JLabel("Giới tính:"));
        panel.add(genderField);
        panel.add(new JLabel("Ngày sinh:"));
        panel.add(dobField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Điện thoại:"));
        panel.add(phoneField);
        panel.add(new JLabel("Vai trò:"));
        panel.add(roleField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Sửa người dùng", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            tableModel.setValueAt(nameField.getText(), row, 1);
            tableModel.setValueAt(genderField.getText(), row, 2);
            tableModel.setValueAt(dobField.getText(), row, 3);
            tableModel.setValueAt(emailField.getText(), row, 4);
            tableModel.setValueAt(phoneField.getText(), row, 5);
            tableModel.setValueAt(roleField.getText(), row, 6);
        }
    }

    private void handleDelete() {
        int row = table.getSelectedRow();
        if (row >= 0) tableModel.removeRow(row);
    }

    private void handleSearch(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return;

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String name = tableModel.getValueAt(i, 1).toString().toLowerCase();
            if (name.contains(keyword.toLowerCase())) {
                table.setRowSelectionInterval(i, i);
                break;
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