package com.tuyensinh.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UserManagementPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTable table;
    private final JTextField detailCodeField = new JTextField();
    private final JTextField detailNameField = new JTextField();
    private final JTextField detailGenderField = new JTextField();
    private final JTextField detailDobField = new JTextField();
    private final JTextField detailEmailField = new JTextField();
    private final JTextField detailPhoneField = new JTextField();
    private final JTextField detailRoleField = new JTextField();
    private final JTextField detailStatusField = new JTextField();
    private final JLabel selectedLabel = new JLabel("Chưa chọn người dùng");

    public UserManagementPanel() {
        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(UIStyles.BG_APP);

        // Title
        JLabel title = new JLabel("Quản Lý Người Dùng");
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
        searchInput.setText("Tìm họ tên, email...");
        searchInput.setFont(UIStyles.FONT_BODY);
        searchInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(6, 10, 6, 10)
        ));

        JButton searchBtn = createButton("Tìm kiếm", UIStyles.PRIMARY);
        searchBtn.addActionListener(e -> handleSearch(searchInput.getText()));
        JButton refreshBtn = createButton("Làm mới", UIStyles.INFO);
        refreshBtn.addActionListener(e -> handleRefresh());

        toolbar.add(searchInput);
        toolbar.add(searchBtn);
        toolbar.add(new JSeparator(JSeparator.VERTICAL));

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

        JLabel tableTitle = new JLabel("Danh sách người dùng");
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
        JLabel title = new JLabel("Chi tiết người dùng");
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
        fields.add(labelWithField("Mã người dùng", detailCodeField));
        fields.add(labelWithField("Họ tên", detailNameField));
        fields.add(labelWithField("Giới tính", detailGenderField));
        fields.add(labelWithField("Ngày sinh", detailDobField));
        fields.add(labelWithField("Email", detailEmailField));
        fields.add(labelWithField("Điện thoại", detailPhoneField));
        fields.add(labelWithField("Vai trò", detailRoleField));
        fields.add(labelWithField("Trạng thái", detailStatusField));

        JScrollPane fieldsScroll = new JScrollPane(fields);
        fieldsScroll.setBorder(null);
        fieldsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        fieldsScroll.getVerticalScrollBar().setUnitIncrement(12);

        configureReadOnlyField(detailCodeField);
        configureReadOnlyField(detailNameField);
        configureReadOnlyField(detailGenderField);
        configureReadOnlyField(detailDobField);
        configureReadOnlyField(detailEmailField);
        configureReadOnlyField(detailPhoneField);
        configureReadOnlyField(detailRoleField);
        configureReadOnlyField(detailStatusField);
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
        JOptionPane.showMessageDialog(this, "Trang quản lý người dùng chưa hỗ trợ import.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
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
            selectedLabel.setText("Chưa chọn người dùng");
            detailCodeField.setText("");
            detailNameField.setText("");
            detailGenderField.setText("");
            detailDobField.setText("");
            detailEmailField.setText("");
            detailPhoneField.setText("");
            detailRoleField.setText("");
            detailStatusField.setText("");
            return;
        }

        detailCodeField.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        detailNameField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        detailGenderField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        detailDobField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        detailEmailField.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        detailPhoneField.setText(String.valueOf(tableModel.getValueAt(row, 5)));
        detailRoleField.setText(String.valueOf(tableModel.getValueAt(row, 6)));
        detailStatusField.setText(String.valueOf(tableModel.getValueAt(row, 7)));
        selectedLabel.setText("Đang chọn: " + detailNameField.getText());
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
            table.setRowSelectionInterval(tableModel.getRowCount() - 1, tableModel.getRowCount() - 1);
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
            updateDetailFromSelection();
        }
    }

    private void handleDelete() {
        int row = table.getSelectedRow();
        if (row >= 0) tableModel.removeRow(row);
        if (tableModel.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        } else {
            updateDetailFromSelection();
        }
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