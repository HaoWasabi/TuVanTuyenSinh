package com.tuyensinh.view;

import com.tuyensinh.model.Role;
import com.tuyensinh.model.RolePermission;
import com.tuyensinh.service.PermissionCatalog;
import com.tuyensinh.service.RoleService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RoleManagementPanel extends JPanel {
    private static final Color TAB_BG = new Color(238, 240, 244);

    private final RoleService roleService = new RoleService();
    private DefaultTableModel roleTableModel;
    private JTable roleTable;
    private final Map<String, JCheckBox> permissionBoxes = new LinkedHashMap<>();
    private final JTextField roleIdField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextArea descriptionArea = new JTextArea(4, 24);
    private final JTextField permissionCountField = new JTextField();
    private final JCheckBox systemRoleBox = new JCheckBox("Vai trò hệ thống");
    private final JLabel selectedRoleLabel = new JLabel("Chưa chọn vai trò");
    private final JLabel totalRolesValueLabel = new JLabel("0");
    private final JLabel totalSystemRolesValueLabel = new JLabel("0");
    private final JLabel totalPermissionsValueLabel = new JLabel("0");

    private Role selectedRole;

    public RoleManagementPanel() {
        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(UIStyles.BG_APP);

        JLabel title = new JLabel("Quản Lý Phân Quyền");
        title.setFont(UIStyles.FONT_TITLE);
        title.setForeground(UIStyles.TEXT_DARK);
        add(title, BorderLayout.NORTH);

        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setOpaque(false);
        root.add(createStatsRow(), BorderLayout.NORTH);
        root.add(createContentSplit(), BorderLayout.CENTER);
        add(root, BorderLayout.CENTER);

        loadRoles(null);
    }

    private JPanel createStatsRow() {
        JPanel stats = new JPanel(new GridLayout(1, 3, 12, 12));
        stats.setOpaque(false);
        stats.add(createStatCard("Tổng vai trò", totalRolesValueLabel, UIStyles.PRIMARY));
        stats.add(createStatCard("Vai trò hệ thống", totalSystemRolesValueLabel, UIStyles.WARNING));
        stats.add(createStatCard("Quyền chuẩn", totalPermissionsValueLabel, UIStyles.SUCCESS));
        return stats;
    }

    private JSplitPane createContentSplit() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createRoleListCard(), createEditorCard());
        splitPane.setResizeWeight(0.42);
        splitPane.setDividerSize(8);
        splitPane.setBorder(null);
        return splitPane;
    }

    private JPanel createRoleListCard() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(UIStyles.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Danh sách vai trò");
        title.setFont(UIStyles.FONT_SUBTITLE);
        title.setForeground(UIStyles.TEXT_DARK);
        header.add(title, BorderLayout.WEST);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        toolbar.setOpaque(false);
        JButton refreshBtn = createButton("Làm mới", UIStyles.PRIMARY);
        refreshBtn.addActionListener(e -> loadRoles(selectedRole == null ? null : selectedRole.getId()));
        JButton addBtn = createButton("Thêm vai trò", UIStyles.INFO);
        addBtn.addActionListener(e -> handleCreateRole());
        toolbar.add(refreshBtn);
        toolbar.add(addBtn);
        header.add(toolbar, BorderLayout.EAST);

        card.add(header, BorderLayout.NORTH);

        String[] columns = {"ID", "Tên", "Mô tả", "Hệ thống", "Số quyền"};
        roleTableModel = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        roleTable = new JTable(roleTableModel);
        roleTable.setRowHeight(30);
        roleTable.setFont(UIStyles.FONT_BODY);
        roleTable.getTableHeader().setFont(UIStyles.FONT_LABEL);
        roleTable.getTableHeader().setBackground(new Color(247, 249, 251));
        roleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roleTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = roleTable.getSelectedRow();
                if (row >= 0) {
                    Integer roleId = (Integer) roleTableModel.getValueAt(row, 0);
                    loadRoleDetail(roleId);
                }
            }
        });

        card.add(new JScrollPane(roleTable), BorderLayout.CENTER);
        card.setPreferredSize(new Dimension(480, 0));
        return card;
    }

    private JPanel createEditorCard() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(UIStyles.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Chi tiết phân quyền");
        title.setFont(UIStyles.FONT_SUBTITLE);
        title.setForeground(UIStyles.TEXT_DARK);
        header.add(title, BorderLayout.WEST);

        JPanel rightHeader = new JPanel(new BorderLayout(0, 6));
        rightHeader.setOpaque(false);
        selectedRoleLabel.setFont(UIStyles.FONT_SMALL);
        selectedRoleLabel.setForeground(UIStyles.TEXT_MUTED);
        rightHeader.add(selectedRoleLabel, BorderLayout.NORTH);
        rightHeader.add(createHeaderActions(), BorderLayout.SOUTH);
        header.add(rightHeader, BorderLayout.EAST);

        card.add(header, BorderLayout.NORTH);

        card.add(createEditorTabs(), BorderLayout.CENTER);
        return card;
    }

    private JTabbedPane createEditorTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIStyles.FONT_BODY);
        tabs.setBackground(TAB_BG);
        tabs.setForeground(UIStyles.TEXT_DARK);
        tabs.setOpaque(true);

        JPanel overviewTab = new JPanel(new BorderLayout());
        overviewTab.setOpaque(true);
        overviewTab.setBackground(TAB_BG);
        overviewTab.setBorder(new EmptyBorder(10, 10, 10, 10));
        overviewTab.add(createRoleInfoForm(), BorderLayout.NORTH);

        JPanel permissionsTab = new JPanel(new BorderLayout(0, 12));
        permissionsTab.setOpaque(true);
        permissionsTab.setBackground(TAB_BG);
        permissionsTab.setBorder(new EmptyBorder(10, 10, 10, 10));
        permissionsTab.add(createPermissionSelector(), BorderLayout.CENTER);

        tabs.addTab("Tổng quan", overviewTab);
        tabs.addTab("Danh sách phân quyền", permissionsTab);
        return tabs;
    }

    private JPanel createRoleInfoForm() {
        JPanel form = new JPanel(new BorderLayout(0, 10));
        form.setOpaque(false);

        JPanel fields = new JPanel(new GridLayout(0, 2, 10, 10));
        fields.setOpaque(false);

        configureReadOnlyField(roleIdField);
        configureTextField(nameField);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(UIStyles.FONT_BODY);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(6, 10, 6, 10)
        ));
        permissionCountField.setEditable(false);
        permissionCountField.setBackground(new Color(247, 249, 251));
        permissionCountField.setFont(UIStyles.FONT_BODY);
        permissionCountField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(6, 10, 6, 10)
        ));
        systemRoleBox.setFont(UIStyles.FONT_BODY);
        systemRoleBox.setEnabled(false);

        fields.add(labelWithField("ID vai trò", roleIdField));
        fields.add(labelWithField("Tên vai trò", nameField));
        fields.add(labelWithField("Số quyền đang chọn", permissionCountField));
        fields.add(labelWithField("Loại vai trò", systemRoleBox));

        JPanel descriptionPanel = new JPanel(new BorderLayout(0, 6));
        descriptionPanel.setOpaque(false);
        JLabel descriptionLabel = new JLabel("Mô tả");
        descriptionLabel.setFont(UIStyles.FONT_LABEL);
        descriptionLabel.setForeground(UIStyles.TEXT_DARK);
        descriptionPanel.add(descriptionLabel, BorderLayout.NORTH);
        descriptionPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);

        form.add(fields, BorderLayout.NORTH);
        form.add(descriptionPanel, BorderLayout.CENTER);
        return form;
    }

    private JScrollPane createPermissionSelector() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);

        permissionBoxes.clear();
        for (PermissionCatalog.PermissionGroup group : PermissionCatalog.groups()) {
            JPanel groupPanel = new JPanel(new BorderLayout(0, 8));
            groupPanel.setOpaque(false);
            groupPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

            JLabel groupTitle = new JLabel(group.title());
            groupTitle.setFont(UIStyles.FONT_LABEL);
            groupTitle.setForeground(UIStyles.TEXT_DARK);
            groupPanel.add(groupTitle, BorderLayout.NORTH);

            JPanel grid = new JPanel(new GridLayout(0, 1, 8, 8));
            grid.setOpaque(false);

            for (PermissionCatalog.PermissionItem item : group.items()) {
                JCheckBox checkBox = new JCheckBox(item.code() + " - " + item.label());
                checkBox.setFont(UIStyles.FONT_BODY_SMALL);
                checkBox.setOpaque(false);
                checkBox.addActionListener(e -> updatePermissionCount());
                permissionBoxes.put(item.code(), checkBox);
                grid.add(checkBox);
            }

            groupPanel.add(grid, BorderLayout.CENTER);
            groupPanel.setAlignmentX(LEFT_ALIGNMENT);
            container.add(groupPanel);
            container.add(Box.createVerticalStrut(10));
        }

        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Danh sách quyền"));
        scrollPane.setPreferredSize(new Dimension(0, 340));
        scrollPane.getViewport().setBackground(TAB_BG);
        scrollPane.setBackground(TAB_BG);
        return scrollPane;
    }

    private JPanel createHeaderActions() {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        JButton selectAllBtn = createButton("Chọn tất cả", UIStyles.SUCCESS);
        selectAllBtn.addActionListener(e -> setAllPermissionsSelected(true));

        JButton clearBtn = createButton("Bỏ chọn", UIStyles.WARNING);
        clearBtn.addActionListener(e -> setAllPermissionsSelected(false));

        JButton saveBtn = createButton("Lưu thay đổi", UIStyles.PRIMARY);
        saveBtn.addActionListener(e -> handleSaveRole());

        actions.add(selectAllBtn);
        actions.add(clearBtn);
        actions.add(saveBtn);
        return actions;
    }

    private void loadRoles(Integer preferredRoleId) {
        List<Role> roles = roleService.getAllRoles();
        roleTableModel.setRowCount(0);

        int totalSystemRoles = 0;
        for (Role role : roles) {
            if (role.isSystem()) {
                totalSystemRoles++;
            }
            int permissionCount = role.getPermissions() == null ? 0 : role.getPermissions().size();
            roleTableModel.addRow(new Object[]{role.getId(), role.getName(), role.getDescription(), role.isSystem() ? "Có" : "Không", permissionCount});
        }

        updateStatCards(roles.size(), totalSystemRoles, PermissionCatalog.allCodes().size());

        Integer roleIdToSelect = preferredRoleId;
        if (roleIdToSelect == null && roleTableModel.getRowCount() > 0) {
            roleIdToSelect = (Integer) roleTableModel.getValueAt(0, 0);
        }

        if (roleIdToSelect != null) {
            selectRoleInTable(roleIdToSelect);
            loadRoleDetail(roleIdToSelect);
        } else {
            clearEditor();
        }
    }

    private void loadRoleDetail(Integer roleId) {
        Role role = roleService.getRoleById(roleId);
        if (role == null) {
            clearEditor();
            return;
        }

        selectedRole = role;
        selectedRoleLabel.setText("Đang chỉnh sửa: " + role.getName());
        roleIdField.setText(String.valueOf(role.getId()));
        nameField.setText(role.getName());
        descriptionArea.setText(role.getDescription() == null ? "" : role.getDescription());
        systemRoleBox.setSelected(role.isSystem());
        nameField.setEditable(!role.isSystem());
        nameField.setBackground(role.isSystem() ? new Color(247, 249, 251) : Color.WHITE);

        setAllPermissionsSelected(false);
        if (role.getPermissions() != null) {
            for (RolePermission permission : role.getPermissions()) {
                JCheckBox checkBox = permissionBoxes.get(permission.getPermission());
                if (checkBox != null) {
                    checkBox.setSelected(true);
                }
            }
        }
        updatePermissionCount();
    }

    private void handleCreateRole() {
        JTextField nameInput = new JTextField();
        JTextArea descriptionInput = new JTextArea(4, 20);
        descriptionInput.setLineWrap(true);
        descriptionInput.setWrapStyleWord(true);

        JPanel form = new JPanel(new BorderLayout(0, 10));
        JPanel fields = new JPanel(new GridLayout(0, 2, 10, 10));
        fields.add(new JLabel("Tên vai trò"));
        fields.add(nameInput);
        fields.add(new JLabel("Mô tả"));
        fields.add(new JScrollPane(descriptionInput));
        form.add(fields, BorderLayout.NORTH);

        int result = JOptionPane.showConfirmDialog(this, form, "Tạo vai trò mới", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            Role created = roleService.createRole(nameInput.getText(), descriptionInput.getText());
            loadRoles(created.getId());
            JOptionPane.showMessageDialog(this, "Đã tạo vai trò mới: " + created.getName(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không thể tạo vai trò: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSaveRole() {
        if (selectedRole == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một vai trò để lưu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Role updated = roleService.updateRole(selectedRole.getId(), nameField.getText(), descriptionArea.getText());
            roleService.replaceRolePermissions(updated.getId(), collectSelectedPermissions());
            loadRoles(updated.getId());
            JOptionPane.showMessageDialog(this, "Đã lưu phân quyền cho vai trò " + updated.getName(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không thể lưu thay đổi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<String> collectSelectedPermissions() {
        List<String> selected = new ArrayList<>();
        for (Map.Entry<String, JCheckBox> entry : permissionBoxes.entrySet()) {
            if (entry.getValue().isSelected()) {
                selected.add(entry.getKey());
            }
        }
        return selected;
    }

    private void setAllPermissionsSelected(boolean selected) {
        permissionBoxes.values().forEach(box -> box.setSelected(selected));
        updatePermissionCount();
    }

    private void updatePermissionCount() {
        long count = permissionBoxes.values().stream().filter(JCheckBox::isSelected).count();
        permissionCountField.setText(String.valueOf(count));
    }

    private void clearEditor() {
        selectedRole = null;
        selectedRoleLabel.setText("Chưa chọn vai trò");
        roleIdField.setText("");
        nameField.setText("");
        descriptionArea.setText("");
        systemRoleBox.setSelected(false);
        nameField.setEditable(true);
        setAllPermissionsSelected(false);
    }

    private void updateStatCards(int totalRoles, int totalSystemRoles, int totalPermissions) {
        totalRolesValueLabel.setText(String.valueOf(totalRoles));
        totalSystemRolesValueLabel.setText(String.valueOf(totalSystemRoles));
        totalPermissionsValueLabel.setText(String.valueOf(totalPermissions));
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UIStyles.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIStyles.FONT_SMALL);
        titleLabel.setForeground(UIStyles.TEXT_MUTED);

        valueLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 28));
        valueLabel.setForeground(color);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(titleLabel);
        content.add(Box.createRigidArea(new Dimension(0, 8)));
        content.add(valueLabel);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel labelWithField(String labelText, java.awt.Component field) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(UIStyles.FONT_LABEL);
        label.setForeground(UIStyles.TEXT_DARK);
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private void configureTextField(JTextField field) {
        field.setFont(UIStyles.FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    private void configureReadOnlyField(JTextField field) {
        configureTextField(field);
        field.setEditable(false);
        field.setBackground(new Color(247, 249, 251));
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

    private void selectRoleInTable(Integer roleId) {
        for (int i = 0; i < roleTableModel.getRowCount(); i++) {
            Integer currentId = (Integer) roleTableModel.getValueAt(i, 0);
            if (Objects.equals(currentId, roleId)) {
                final int row = i;
                SwingUtilities.invokeLater(() -> {
                    roleTable.getSelectionModel().setSelectionInterval(row, row);
                    roleTable.scrollRectToVisible(roleTable.getCellRect(row, 0, true));
                });
                return;
            }
        }
    }
}