package com.tuyensinh.view;

import com.tuyensinh.model.User;
import com.tuyensinh.service.SessionManager;
import com.tuyensinh.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserManagementPanel extends JPanel {
    private final UserService userService = new UserService();
    private final List<User> currentUsers = new ArrayList<>();

    private DefaultTableModel tableModel;
    private JTable table;
    private final JTextField detailIdField = new JTextField();
    private final JTextField detailUsernameField = new JTextField();
    private final JTextField detailNameField = new JTextField();
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

        loadUsers(userService.getAll());
    }

    private JPanel createListCard() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);

        JTextField searchInput = new JTextField(28);
        String placeholderText = "Tìm họ tên, email...";
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

        // Table
        String[] cols = {"ID", "Username", "Họ tên", "Email", "Điện thoại", "Vai trò", "Trạng thái"};

        tableModel = new DefaultTableModel(cols, 0) {
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
        fields.add(labelWithField("ID", detailIdField));
        fields.add(labelWithField("Username", detailUsernameField));
        fields.add(labelWithField("Họ tên", detailNameField));
        fields.add(labelWithField("Email", detailEmailField));
        fields.add(labelWithField("Điện thoại", detailPhoneField));
        fields.add(labelWithField("Vai trò", detailRoleField));
        fields.add(labelWithField("Trạng thái", detailStatusField));

        JScrollPane fieldsScroll = new JScrollPane(fields);
        fieldsScroll.setBorder(null);
        fieldsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        fieldsScroll.getVerticalScrollBar().setUnitIncrement(12);

        configureReadOnlyField(detailIdField);
        configureReadOnlyField(detailUsernameField);
        configureReadOnlyField(detailNameField);
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

        if (SessionManager.hasPermission("USER_CREATE")) {
            JButton addBtn = createButton("Thêm", UIStyles.INFO);
            addBtn.addActionListener(e -> handleAdd());
            actions.add(addBtn);
        }
        if (SessionManager.hasPermission("USER_EDIT")) {
            JButton editBtn = createButton("Sửa", UIStyles.WARNING);
            editBtn.addActionListener(e -> handleEdit());
            actions.add(editBtn);
        }
        if (SessionManager.hasPermission("USER_TOGGLE")) {
            JButton lockBtn = createButton("Khóa/Mở khóa", new Color(108, 117, 125));
            lockBtn.addActionListener(e -> handleToggleLock());
            actions.add(lockBtn);
        }
        if (SessionManager.hasPermission("USER_DELETE")) {
            JButton deleteBtn = createButton("Xóa", UIStyles.DANGER);
            deleteBtn.addActionListener(e -> handleDelete());
            actions.add(deleteBtn);
        }
        return actions;
    }

    private void handleImport() {
        JOptionPane.showMessageDialog(this, "Trang quản lý người dùng chưa hỗ trợ import.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleRefresh() {
        loadUsers(userService.getAll());
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
            detailIdField.setText("");
            detailUsernameField.setText("");
            detailNameField.setText("");
            detailEmailField.setText("");
            detailPhoneField.setText("");
            detailRoleField.setText("");
            detailStatusField.setText("");
            return;
        }

        User user = getSelectedUser();
        if (user == null) {
            return;
        }

        detailIdField.setText(String.valueOf(user.getId()));
        detailUsernameField.setText(safe(user.getUsername()));
        detailNameField.setText(safe(user.getFullName()));
        detailEmailField.setText(safe(user.getEmail()));
        detailPhoneField.setText(safe(user.getPhoneNumber()));
        detailRoleField.setText(toRoleLabel(user));
        detailStatusField.setText(toStatusLabel(user.getStatus()));
        selectedLabel.setText("Đang chọn: " + safe(user.getUsername()));
    }

    private void handleAdd() {
        User currentUser = SessionManager.getCurrentUser();
        Integer currentRoleId = currentUser != null ? currentUser.getIdRoleValue() : null;

        // Xác định danh sách vai trò cho phép tạo
        String[] availableRoles;
        if (currentRoleId != null && currentRoleId == 1) {
            // Admin: chỉ được tạo GIAM_THI và HOC_SINH (không được tạo ADMIN khác)
            availableRoles = new String[]{"GIAM_THI", "HOC_SINH"};
        } else if (currentRoleId != null && currentRoleId == 2) {
            // Giám thị: chỉ được tạo HOC_SINH
            availableRoles = new String[]{"HOC_SINH"};
        } else {
            availableRoles = new String[]{"HOC_SINH"};
        }

        JTextField usernameField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JComboBox<String> roleBox = new JComboBox<>(availableRoles);
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Hoạt động", "Đã khóa"});

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Username *:"));
        panel.add(usernameField);
        panel.add(new JLabel("Họ tên:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Điện thoại:"));
        panel.add(phoneField);
        panel.add(new JLabel("Vai trò:"));
        panel.add(roleBox);
        panel.add(new JLabel("Trạng thái:"));
        panel.add(statusBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Thêm người dùng", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            if (usernameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String generatedPassword = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));

            User user = User.builder()
                    .username(usernameField.getText().trim())
                    .password(generatedPassword)
                    .fullName(blankToNull(nameField.getText()))
                    .email(defaultEmailIfBlank(emailField.getText(), usernameField.getText().trim()))
                    .phoneNumber(blankToNull(phoneField.getText()))
                    .idRoleValue(mapRoleOptionToId((String) roleBox.getSelectedItem()))
                    .status("Hoạt động".equals(statusBox.getSelectedItem()) ? "ACTIVE" : "OFF")
                    .build();

            try {
                userService.create(user);
                loadUsers(userService.getAll());
                JOptionPane.showMessageDialog(
                        this,
                        "Đã tạo tài khoản thành công.\nUsername: " + user.getUsername() + "\nMật khẩu mặc định: " + generatedPassword,
                        "Tạo tài khoản thành công",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Không thể thêm người dùng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn người dùng để sửa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = getSelectedUser();
        if (user == null) {
            return;
        }

        String denyReason = canModifyUser(user, "sửa");
        if (denyReason != null) {
            JOptionPane.showMessageDialog(this, denyReason, "Từ chối truy cập", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField nameField = new JTextField(safe(user.getFullName()));
        JTextField emailField = new JTextField(safe(user.getEmail()));
        JTextField phoneField = new JTextField(safe(user.getPhoneNumber()));

        // Xác định danh sách vai trò cho phép chỉnh sửa
        User currentUser = SessionManager.getCurrentUser();
        Integer currentRoleId = currentUser != null ? currentUser.getIdRoleValue() : null;
        String[] editableRoles;
        if (currentRoleId != null && currentRoleId == 1) {
            editableRoles = new String[]{"GIAM_THI", "HOC_SINH"};
        } else if (currentRoleId != null && currentRoleId == 2) {
            editableRoles = new String[]{"HOC_SINH"};
        } else {
            editableRoles = new String[]{"HOC_SINH"};
        }

        JComboBox<String> roleBox = new JComboBox<>(editableRoles);
        String currentRoleOption = roleOptionFromId(user.getIdRoleValue());
        // Nếu vai trò hiện tại nằm trong danh sách thì chọn, không thì để mặc định
        for (int i = 0; i < editableRoles.length; i++) {
            if (editableRoles[i].equals(currentRoleOption)) {
                roleBox.setSelectedItem(currentRoleOption);
                break;
            }
        }
        roleBox.setEnabled(SessionManager.hasPermission("USER_CHANGE_ROLE"));
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Hoạt động", "Đã khóa"});
        statusBox.setSelectedItem(toStatusLabel(user.getStatus()));

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Username:"));
        JTextField usernameField = new JTextField(safe(user.getUsername()));
        usernameField.setEditable(false);
        panel.add(usernameField);
        panel.add(new JLabel("Họ tên:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Điện thoại:"));
        panel.add(phoneField);
        panel.add(new JLabel("Vai trò:"));
        panel.add(roleBox);
        panel.add(new JLabel("Trạng thái:"));
        panel.add(statusBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Sửa người dùng", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            user.setFullName(blankToNull(nameField.getText()));
            user.setEmail(defaultEmailIfBlank(emailField.getText(), user.getUsername()));
            user.setPhoneNumber(blankToNull(phoneField.getText()));
            if (SessionManager.hasPermission("USER_CHANGE_ROLE")) {
                user.setIdRoleValue(mapRoleOptionToId((String) roleBox.getSelectedItem()));
            }
            user.setStatus("Hoạt động".equals(statusBox.getSelectedItem()) ? "ACTIVE" : "OFF");

            try {
                userService.update(user);
                loadUsers(userService.getAll());
                JOptionPane.showMessageDialog(
                        this,
                        "Đã sửa thông tin người dùng '" + safe(user.getUsername()) + "' thành công.",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Không thể cập nhật người dùng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleToggleLock() {
        User user = getSelectedUser();
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn người dùng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String denyReason = canToggleLock(user);
        if (denyReason != null) {
            JOptionPane.showMessageDialog(this, denyReason, "Từ chối truy cập", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean isCurrentlyActive = "ACTIVE".equalsIgnoreCase(user.getStatus());
        String newStatus = isCurrentlyActive ? "OFF" : "ACTIVE";
        String actionLabel = isCurrentlyActive ? "khóa" : "mở khóa";

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn " + actionLabel + " user '" + safe(user.getUsername()) + "' không?",
                "Xác nhận " + actionLabel,
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            user.setStatus(newStatus);
            userService.update(user);
            loadUsers(userService.getAll());
            JOptionPane.showMessageDialog(
                    this,
                    "Đã " + actionLabel + " tài khoản '" + safe(user.getUsername()) + "' thành công.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không thể " + actionLabel + " người dùng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Kiểm tra quyền khóa/mở khóa.
     * - Admin (role_id=1): không được khóa/mở khóa admin khác
     * - Giám thị (role_id=2): không được khóa/mở khóa admin hoặc giám thị khác
     * @return null nếu được phép, hoặc chuỗi lý do từ chối
     */
    private String canToggleLock(User targetUser) {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            return "Không xác định được người dùng hiện tại.";
        }

        Integer currentRoleId = currentUser.getIdRoleValue();
        Integer targetRoleId = targetUser.getIdRoleValue();

        // Không được tự khóa chính mình
        if (currentUser.getId() != null && currentUser.getId().equals(targetUser.getId())) {
            return "Bạn không thể khóa/mở khóa chính mình.";
        }

        // Admin (role_id=1): không được khóa/mở khóa admin khác
        if (currentRoleId != null && currentRoleId == 1) {
            if (targetRoleId != null && targetRoleId == 1) {
                return "Admin không được phép khóa/mở khóa admin khác.";
            }
            return null; // Admin được phép khóa GIAM_THI và HOC_SINH
        }

        // Giám thị (role_id=2): không được khóa/mở khóa admin hoặc giám thị khác
        if (currentRoleId != null && currentRoleId == 2) {
            if (targetRoleId != null && targetRoleId == 1) {
                return "Giám thị không được phép khóa/mở khóa admin.";
            }
            if (targetRoleId != null && targetRoleId == 2) {
                return "Giám thị không được phép khóa/mở khóa giám thị khác.";
            }
            return null; // Giám thị được phép khóa HOC_SINH
        }

        // Các role khác (HOC_SINH, v.v.) không có quyền khóa/mở khóa
        return "Bạn không có quyền khóa/mở khóa người dùng.";
    }

    private void handleDelete() {
        User user = getSelectedUser();
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn người dùng để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String denyReason = canModifyUser(user, "xóa");
        if (denyReason != null) {
            JOptionPane.showMessageDialog(this, denyReason, "Từ chối truy cập", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa user '" + safe(user.getUsername()) + "' không?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            userService.deleteById(user.getId());
            loadUsers(userService.getAll());
            JOptionPane.showMessageDialog(
                    this,
                    "Đã xóa người dùng '" + safe(user.getUsername()) + "' thành công.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không thể xóa người dùng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSearch(String keyword) {
        if (keyword == null || keyword.trim().isEmpty() || keyword.equals("Tìm họ tên, email...")) {
            loadUsers(userService.getAll());
            return;
        }

        loadUsers(userService.searchByKeyword(keyword.trim()));
    }

    private void loadUsers(List<User> users) {
        currentUsers.clear();
        currentUsers.addAll(users);
        tableModel.setRowCount(0);

        for (User user : currentUsers) {
            tableModel.addRow(new Object[]{
                    user.getId(),
                    safe(user.getUsername()),
                    safe(user.getFullName()),
                    safe(user.getEmail()),
                    safe(user.getPhoneNumber()),
                    toRoleLabel(user),
                    toStatusLabel(user.getStatus())
            });
        }

        if (tableModel.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        } else {
            updateDetailFromSelection();
        }
    }

    private User getSelectedUser() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentUsers.size()) {
            return null;
        }
        Integer userId = currentUsers.get(row).getId();
        Optional<User> latest = userService.getById(userId);
        return latest.orElse(currentUsers.get(row));
    }

    private String toRoleLabel(User user) {
        if (user.getRole() != null && user.getRole().getName() != null) {
            return user.getRole().getName();
        }
        return roleFromId(user.getIdRoleValue());
    }

    private String roleFromId(Integer roleId) {
        if (roleId == null) {
            return "N/A";
        }
        if (roleId == 1) {
            return "ADMIN";
        }
        if (roleId == 2) {
            return "GIAM_THI";
        }
        if (roleId == 3) {
            return "HOC_SINH";
        }
        return "role_id=" + roleId;
    }

    private String roleOptionFromId(Integer roleId) {
        if (roleId == null) {
            return "GIAM_THI";
        }
        if (roleId == 1) {
            return "ADMIN";
        }
        if (roleId == 2) {
            return "GIAM_THI";
        }
        if (roleId == 3) {
            return "HOC_SINH";
        }
        return "GIAM_THI";
    }

    private Integer mapRoleOptionToId(String roleOption) {
        if ("ADMIN".equalsIgnoreCase(roleOption)) {
            return 1;
        }
        if ("GIAM_THI".equalsIgnoreCase(roleOption)) {
            return 2;
        }
        return 3;
    }

    /**
     * Kiểm tra quyền thêm/sửa/xóa người dùng.
     * - Admin (role_id=1): không được thêm/sửa/xóa admin khác
     * - Giám thị (role_id=2): không được thêm/sửa/xóa admin hoặc giám thị khác
     * @param targetUser người dùng bị tác động
     * @param action tên hành động (để hiển thị thông báo)
     * @return null nếu được phép, hoặc chuỗi lý do từ chối
     */
    private String canModifyUser(User targetUser, String action) {
        if (targetUser == null) {
            return null;
        }
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            return "Không xác định được người dùng hiện tại.";
        }

        Integer currentRoleId = currentUser.getIdRoleValue();
        Integer targetRoleId = targetUser.getIdRoleValue();

        // Admin (role_id=1): không được thêm/sửa/xóa admin khác
        if (currentRoleId != null && currentRoleId == 1) {
            if (targetRoleId != null && targetRoleId == 1
                    && !currentUser.getId().equals(targetUser.getId())) {
                return "Admin không được phép " + action + " admin khác.";
            }
            return null;
        }

        // Giám thị (role_id=2): không được thêm/sửa/xóa admin hoặc giám thị khác
        if (currentRoleId != null && currentRoleId == 2) {
            if (targetRoleId != null && targetRoleId == 1) {
                return "Giám thị không được phép " + action + " admin.";
            }
            if (targetRoleId != null && targetRoleId == 2
                    && !currentUser.getId().equals(targetUser.getId())) {
                return "Giám thị không được phép " + action + " giám thị khác.";
            }
            return null;
        }

        // Các role khác không có quyền
        return "Bạn không có quyền " + action + " người dùng này.";
    }

    private String toStatusLabel(String status) {
        if ("active".equalsIgnoreCase(status)) {
            return "Hoạt động";
        }
        if ("off".equalsIgnoreCase(status)) {
            return "Đã khóa";
        }
        if ("inactive".equalsIgnoreCase(status)) {
            return "Đã xóa";
        }
        return safe(status);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String blankToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private String defaultEmailIfBlank(String email, String username) {
        String trimmed = email == null ? "" : email.trim();
        if (!trimmed.isEmpty()) {
            return trimmed;
        }
        return username + "@local";
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