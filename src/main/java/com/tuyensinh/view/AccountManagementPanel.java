package com.tuyensinh.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AccountManagementPanel extends JPanel {
    private final DefaultTableModel tableModel;
    private final JTable table;

    public AccountManagementPanel() {
        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(UIStyles.BG_APP);

        // Title
        JLabel title = new JLabel("Quản Lý Tài Khoản");
        title.setFont(UIStyles.FONT_TITLE);
        title.setForeground(UIStyles.TEXT_DARK);
        add(title, BorderLayout.NORTH);

        // Search & Actions
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);

        JTextField searchInput = new JTextField(28);
        searchInput.setText("Tìm username, email...");
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

        JButton lockBtn = createButton("Khóa", UIStyles.SUCCESS);
        lockBtn.addActionListener(e -> handleLock());

        JButton deleteBtn = createButton("Xóa", UIStyles.DANGER);
        deleteBtn.addActionListener(e -> handleDelete());

        toolbar.add(searchInput);
        toolbar.add(searchBtn);
        toolbar.add(new javax.swing.JSeparator(javax.swing.JSeparator.VERTICAL));
        toolbar.add(addBtn);
        toolbar.add(editBtn);
        toolbar.add(lockBtn);
        toolbar.add(deleteBtn);

        // Table
        String[] cols = {"Mã TK", "Username", "Email", "Vai trò", "Trạng thái", "Ngày tạo"};
        Object[][] data = {
                {"TK001", "admin", "admin@sgu.edu.vn", "Admin", "Hoạt động", "20/03/2026"},
                {"TK002", "trinh", "trinh@sgu.edu.vn", "Nhân viên", "Hoạt động", "21/03/2026"},
                {"TK003", "hao", "hao@sgu.edu.vn", "Nhân viên", "Hoạt động", "21/03/2026"},
                {"TK004", "user01", "user01@gmail.com", "Thí sinh", "Đã khóa", "22/03/2026"},
                {"TK005", "user02", "user02@gmail.com", "Thí sinh", "Hoạt động", "23/03/2026"}
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

        JLabel tableTitle = new JLabel("Danh sách tài khoản");
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
        JTextField maTkField = new JTextField();
        JTextField usernameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField vaiTroField = new JTextField();
        JComboBox<String> trangThaiBox = new JComboBox<>(new String[]{"Hoạt động", "Đã khóa"});
        JTextField ngayTaoField = new JTextField("28/03/2026");

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Mã tài khoản:"));
        panel.add(maTkField);
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Vai trò:"));
        panel.add(vaiTroField);
        panel.add(new JLabel("Trạng thái:"));
        panel.add(trangThaiBox);
        panel.add(new JLabel("Ngày tạo:"));
        panel.add(ngayTaoField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Thêm tài khoản",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            if (maTkField.getText().trim().isEmpty() ||
                    usernameField.getText().trim().isEmpty() ||
                    emailField.getText().trim().isEmpty() ||
                    vaiTroField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Object[] newRow = {
                    maTkField.getText().trim(),
                    usernameField.getText().trim(),
                    emailField.getText().trim(),
                    vaiTroField.getText().trim(),
                    trangThaiBox.getSelectedItem(),
                    ngayTaoField.getText().trim()
            };
            tableModel.addRow(newRow);
            JOptionPane.showMessageDialog(this, "Thêm tài khoản thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleEdit() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản để sửa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField maTkField = new JTextField((String) tableModel.getValueAt(selectedRow, 0));
        JTextField usernameField = new JTextField((String) tableModel.getValueAt(selectedRow, 1));
        JTextField emailField = new JTextField((String) tableModel.getValueAt(selectedRow, 2));
        JTextField vaiTroField = new JTextField((String) tableModel.getValueAt(selectedRow, 3));
        JComboBox<String> trangThaiBox = new JComboBox<>(new String[]{"Hoạt động", "Đã khóa"});
        trangThaiBox.setSelectedItem(tableModel.getValueAt(selectedRow, 4));
        JTextField ngayTaoField = new JTextField((String) tableModel.getValueAt(selectedRow, 5));

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Mã tài khoản:"));
        panel.add(maTkField);
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Vai trò:"));
        panel.add(vaiTroField);
        panel.add(new JLabel("Trạng thái:"));
        panel.add(trangThaiBox);
        panel.add(new JLabel("Ngày tạo:"));
        panel.add(ngayTaoField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Sửa tài khoản",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            tableModel.setValueAt(maTkField.getText().trim(), selectedRow, 0);
            tableModel.setValueAt(usernameField.getText().trim(), selectedRow, 1);
            tableModel.setValueAt(emailField.getText().trim(), selectedRow, 2);
            tableModel.setValueAt(vaiTroField.getText().trim(), selectedRow, 3);
            tableModel.setValueAt(trangThaiBox.getSelectedItem(), selectedRow, 4);
            tableModel.setValueAt(ngayTaoField.getText().trim(), selectedRow, 5);

            JOptionPane.showMessageDialog(this, "Cập nhật tài khoản thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleLock() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản để khóa/mở khóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String currentStatus = (String) tableModel.getValueAt(selectedRow, 4);
        String newStatus = currentStatus.equals("Hoạt động") ? "Đã khóa" : "Hoạt động";
        tableModel.setValueAt(newStatus, selectedRow, 4);

        JOptionPane.showMessageDialog(
                this,
                "Đã cập nhật trạng thái tài khoản thành: " + newStatus,
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void handleDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String username = (String) tableModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa tài khoản \"" + username + "\" không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Xóa tài khoản thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleSearch(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty() || searchTerm.equals("Tìm username, email...")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa tìm kiếm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String keyword = searchTerm.trim().toLowerCase();
        boolean found = false;

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String username = tableModel.getValueAt(i, 1).toString().toLowerCase();
            String email = tableModel.getValueAt(i, 2).toString().toLowerCase();

            if (username.contains(keyword) || email.contains(keyword)) {
                table.setRowSelectionInterval(i, i);
                table.scrollRectToVisible(table.getCellRect(i, 0, true));
                found = true;
                break;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy tài khoản phù hợp!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
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