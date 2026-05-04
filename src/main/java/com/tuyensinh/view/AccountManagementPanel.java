package com.tuyensinh.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AccountManagementPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTable table;
    private final JTextField detailCodeField = new JTextField();
    private final JTextField detailUsernameField = new JTextField();
    private final JTextField detailEmailField = new JTextField();
    private final JTextField detailRoleField = new JTextField();
    private final JTextField detailStatusField = new JTextField();
    private final JTextField detailCreatedField = new JTextField();
    private final JLabel selectedLabel = new JLabel("Chưa chọn tài khoản");

    public AccountManagementPanel() {
        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(UIStyles.BG_APP);

        // Title
        JLabel title = new JLabel("Quản Lý Tài Khoản");
        title.setFont(UIStyles.FONT_TITLE);
        title.setForeground(UIStyles.TEXT_DARK);
        add(title, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createListCard(), createDetailCard());
        splitPane.setResizeWeight(0.64);
        splitPane.setDividerSize(8);
        splitPane.setDividerLocation(0.64);
        splitPane.setBorder(null);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createListCard() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);

        JTextField searchInput = new JTextField(28);
        String placeholderText = "Tìm username, email...";
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
        toolbar.add(new javax.swing.JSeparator(javax.swing.JSeparator.VERTICAL));

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
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateDetailFromSelection();
            }
        });

        JPanel tableCard = new JPanel(new BorderLayout(0, 12));
        tableCard.setBackground(UIStyles.BG_CARD);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel tableTitle = new JLabel("Danh sách tài khoản");
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
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BorderLayout(0, 12));
        center.add(toolbar, BorderLayout.NORTH);
        center.add(tableCard, BorderLayout.CENTER);
        if (tableModel.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        }
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
        JLabel title = new JLabel("Chi tiết tài khoản");
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
        fields.add(labelWithField("Mã tài khoản", detailCodeField));
        fields.add(labelWithField("Username", detailUsernameField));
        fields.add(labelWithField("Email", detailEmailField));
        fields.add(labelWithField("Vai trò", detailRoleField));
        fields.add(labelWithField("Trạng thái", detailStatusField));
        fields.add(labelWithField("Ngày tạo", detailCreatedField));

        JScrollPane fieldsScroll = new JScrollPane(fields);
        fieldsScroll.setBorder(null);
        fieldsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        fieldsScroll.getVerticalScrollBar().setUnitIncrement(12);

        configureReadOnlyField(detailCodeField);
        configureReadOnlyField(detailUsernameField);
        configureReadOnlyField(detailEmailField);
        configureReadOnlyField(detailRoleField);
        configureReadOnlyField(detailStatusField);
        configureReadOnlyField(detailCreatedField);
        card.setPreferredSize(new Dimension(440, 0));
        card.setMinimumSize(new Dimension(440, 0));

        card.add(header, BorderLayout.NORTH);
        card.add(fieldsScroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel createDetailActions() {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        JButton importBtn = createButton("Import", UIStyles.SUCCESS);
        importBtn.addActionListener(e -> handleImport());
        JButton addBtn = createButton("Thêm", UIStyles.INFO);
        addBtn.addActionListener(e -> handleAdd());
        JButton editBtn = createButton("Sửa", UIStyles.WARNING);
        editBtn.addActionListener(e -> handleEdit());
        JButton deleteBtn = createButton("Xóa", UIStyles.DANGER);
        deleteBtn.addActionListener(e -> handleDelete());

        actions.add(importBtn);
        actions.add(addBtn);
        actions.add(editBtn);
        actions.add(deleteBtn);
        return actions;
    }

    private void handleImport() {
        JOptionPane.showMessageDialog(this, "Trang quản lý tài khoản chưa hỗ trợ import.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleRefresh() {
        if (tableModel.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        } else {
            updateDetailFromSelection();
        }
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
            selectedLabel.setText("Chưa chọn tài khoản");
            detailCodeField.setText("");
            detailUsernameField.setText("");
            detailEmailField.setText("");
            detailRoleField.setText("");
            detailStatusField.setText("");
            detailCreatedField.setText("");
            return;
        }

        detailCodeField.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        detailUsernameField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        detailEmailField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        detailRoleField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        detailStatusField.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        detailCreatedField.setText(String.valueOf(tableModel.getValueAt(row, 5)));
        selectedLabel.setText("Đang chọn: " + detailUsernameField.getText());
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
            table.setRowSelectionInterval(tableModel.getRowCount() - 1, tableModel.getRowCount() - 1);
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
            updateDetailFromSelection();

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
        updateDetailFromSelection();

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
            if (tableModel.getRowCount() > 0) {
                table.setRowSelectionInterval(0, 0);
            } else {
                updateDetailFromSelection();
            }
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
}