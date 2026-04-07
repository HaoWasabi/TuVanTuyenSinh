package com.tuyensinh.view;

import com.tuyensinh.model.ThiSinh;
import com.tuyensinh.service.ThiSinhService;
import com.tuyensinh.model.User;
import com.tuyensinh.service.SessionManager;
import com.tuyensinh.service.UserService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CandidateManagementPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTable table;
    private final ThiSinhService thiSinhService = new ThiSinhService();
    private final UserService userService = new UserService();
    private List<ThiSinh> currentData = new ArrayList<>();
    private final JTextField detailCccdField = new JTextField();
    private final JTextField detailSbdField = new JTextField();
    private final JTextField detailNameField = new JTextField();
    private final JTextField detailDobField = new JTextField();
    private final JTextField detailGenderField = new JTextField();
    private final JTextField detailEmailField = new JTextField();
    private final JTextField detailPhoneField = new JTextField();
    private final JTextField detailAreaField = new JTextField();
    private final JLabel selectedCandidateLabel = new JLabel("Chưa chọn thí sinh");

    public CandidateManagementPanel() {
        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(UIStyles.BG_APP);

        // Title
        JLabel title = new JLabel("Quản Lý Thí Sinh");
        title.setFont(UIStyles.FONT_TITLE);
        title.setForeground(UIStyles.TEXT_DARK);
        add(title, BorderLayout.NORTH);

        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setOpaque(false);
        root.add(createContentSplit(), BorderLayout.CENTER);
        add(root, BorderLayout.CENTER);

        loadDataToTable();
    }

    private JSplitPane createContentSplit() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createListCard(), createDetailCard());
        splitPane.setResizeWeight(0.64);
        splitPane.setDividerSize(8);
        splitPane.setDividerLocation(0.64);
        splitPane.setBorder(null);
        return splitPane;
    }

    private JPanel createListCard() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);

        JTextField searchInput = new JTextField(28);
        String placeholderText = "Tìm CCCD, họ tên...";
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

        String[] cols = {"CCCD", "Số báo danh", "Họ", "Tên", "Ngày sinh", "Giới tính", "Email", "Điện thoại", "Nơi sinh", "Đối tượng", "Khu vực"};
        tableModel = new DefaultTableModel(null, cols) {
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

        JLabel tableTitle = new JLabel("Danh sách thí sinh (Phân trang 20 dòng/trang)");
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

        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(UIStyles.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));
        card.add(toolbar, BorderLayout.NORTH);
        card.add(tableCard, BorderLayout.CENTER);
        return card;
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
        JLabel title = new JLabel("Chi tiết thí sinh");
        title.setFont(UIStyles.FONT_SUBTITLE);
        title.setForeground(UIStyles.TEXT_DARK);
        header.add(title, BorderLayout.WEST);

        JPanel rightHeader = new JPanel(new BorderLayout(0, 6));
        rightHeader.setOpaque(false);
        selectedCandidateLabel.setFont(UIStyles.FONT_SMALL);
        selectedCandidateLabel.setForeground(UIStyles.TEXT_MUTED);
        rightHeader.add(selectedCandidateLabel, BorderLayout.NORTH);
        rightHeader.add(createDetailActions(), BorderLayout.SOUTH);
        header.add(rightHeader, BorderLayout.EAST);

        JPanel fields = new JPanel(new java.awt.GridLayout(0, 1, 0, 8));
        fields.setOpaque(false);
        fields.add(labelWithField("CCCD", detailCccdField));
        fields.add(labelWithField("Số báo danh", detailSbdField));
        fields.add(labelWithField("Họ và tên", detailNameField));
        fields.add(labelWithField("Ngày sinh", detailDobField));
        fields.add(labelWithField("Giới tính", detailGenderField));
        fields.add(labelWithField("Email", detailEmailField));
        fields.add(labelWithField("Điện thoại", detailPhoneField));
        fields.add(labelWithField("Khu vực / Đối tượng", detailAreaField));

        JScrollPane fieldsScroll = new JScrollPane(fields);
        fieldsScroll.setBorder(null);
        fieldsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        fieldsScroll.getVerticalScrollBar().setUnitIncrement(12);

        card.add(header, BorderLayout.NORTH);
        card.add(fieldsScroll, BorderLayout.CENTER);

        configureReadOnlyField(detailCccdField);
        configureReadOnlyField(detailSbdField);
        configureReadOnlyField(detailNameField);
        configureReadOnlyField(detailDobField);
        configureReadOnlyField(detailGenderField);
        configureReadOnlyField(detailEmailField);
        configureReadOnlyField(detailPhoneField);
        configureReadOnlyField(detailAreaField);
        card.setPreferredSize(new Dimension(440, 0));
        card.setMinimumSize(new Dimension(440, 0));

        return card;
    }

    private JPanel createDetailActions() {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        if (SessionManager.hasPermission("THISINH_IMPORT")) {
            JButton importBtn = createButton("Import", UIStyles.SUCCESS);
            importBtn.addActionListener(e -> handleImport());
            actions.add(importBtn);
        }
        if (SessionManager.hasPermission("THISINH_CREATE")) {
            JButton addBtn = createButton("Thêm", UIStyles.INFO);
            addBtn.addActionListener(e -> handleAdd());
            actions.add(addBtn);
        }
        if (SessionManager.hasPermission("THISINH_EDIT")) {
            JButton editBtn = createButton("Sửa", UIStyles.WARNING);
            editBtn.addActionListener(e -> handleEdit());
            actions.add(editBtn);
        }
        if (SessionManager.hasPermission("THISINH_DELETE")) {
            JButton deleteBtn = createButton("Xóa", UIStyles.DANGER);
            deleteBtn.addActionListener(e -> handleDelete());
            actions.add(deleteBtn);
        }
        return actions;
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
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            selectedCandidateLabel.setText("Chưa chọn thí sinh");
            detailCccdField.setText("");
            detailSbdField.setText("");
            detailNameField.setText("");
            detailDobField.setText("");
            detailGenderField.setText("");
            detailEmailField.setText("");
            detailPhoneField.setText("");
            detailAreaField.setText("");
            return;
        }

        String cccd = String.valueOf(tableModel.getValueAt(selectedRow, 0));
        String sbd = String.valueOf(tableModel.getValueAt(selectedRow, 1));
        String fullName = String.valueOf(tableModel.getValueAt(selectedRow, 2)) + " " + String.valueOf(tableModel.getValueAt(selectedRow, 3));
        String dob = String.valueOf(tableModel.getValueAt(selectedRow, 4));
        String gender = String.valueOf(tableModel.getValueAt(selectedRow, 5));
        String email = String.valueOf(tableModel.getValueAt(selectedRow, 6));
        String phone = String.valueOf(tableModel.getValueAt(selectedRow, 7));
        String area = String.valueOf(tableModel.getValueAt(selectedRow, 10)) + " / " + String.valueOf(tableModel.getValueAt(selectedRow, 9));

        selectedCandidateLabel.setText("Đang chọn: " + fullName.trim());
        detailCccdField.setText(cccd);
        detailSbdField.setText(sbd);
        detailNameField.setText(fullName.trim());
        detailDobField.setText(dob);
        detailGenderField.setText(gender);
        detailEmailField.setText(email);
        detailPhoneField.setText(phone);
        detailAreaField.setText(area);
    }

    private void loadDataToTable() {
        if (isCccdOnlyMode()) {
            String loginCccd = getSessionUsernameAsCccd();
            if (loginCccd.isEmpty()) {
                currentData = new ArrayList<>();
            } else {
                Optional<ThiSinh> thiSinh = thiSinhService.getByCccd(loginCccd);
                currentData = thiSinh.map(List::of).orElseGet(ArrayList::new);
            }
            renderTable(currentData);
            return;
        }

        currentData = thiSinhService.getAll();
        renderTable(currentData);
    }

    private void handleRefresh() {
        loadDataToTable();
    }

    private void renderTable(List<ThiSinh> thiSinhList) {
        tableModel.setRowCount(0);
        for (ThiSinh ts : thiSinhList) {
            Object[] row = {
                    ts.getCccd(),
                    ts.getSobaodanh(),
                    ts.getHo(),
                    ts.getTen(),
                    ts.getNgaySinh(),
                    ts.getGioiTinh(),
                    ts.getEmail(),
                    ts.getDienThoai(),
                    ts.getNoiSinh(),
                    ts.getDoiTuong(),
                    ts.getKhuVuc()
            };
            tableModel.addRow(row);
        }
        if (tableModel.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        } else {
            updateDetailFromSelection();
        }
    }

    private void handleAdd() {
        CandidateFormDialog dialog = new CandidateFormDialog(getTopLevelAncestor() instanceof java.awt.Frame ?
                (java.awt.Frame) getTopLevelAncestor() : null, "Thêm Thí Sinh", false);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                ThiSinh thiSinh = ThiSinh.builder()
                        .cccd(dialog.getCCCD())
                        .sobaodanh(dialog.getSbaodanh())
                        .ho(dialog.getHo())
                        .ten(dialog.getTen())
                        .ngaySinh(dialog.getNgaysinh())
                        .gioiTinh(dialog.getGioitinh())
                        .email(dialog.getEmail())
                        .dienThoai(dialog.getDienthoai())
                        .noiSinh(dialog.getNoisinh())
                        .doiTuong(dialog.getDoituong())
                        .khuVuc(dialog.getKhuvuc())
                        .updatedAt(LocalDate.now())
                    .password(generatePasswordFromDOB(dialog.getNgaysinh())) // Mật khẩu mặc định DDMMYY từ ngày sinh
                        .build();

                thiSinhService.create(thiSinh);

                // Tạo tài khoản student tương ứng
                String generatedPassword = generatePasswordFromDOB(dialog.getNgaysinh());
                // Username học sinh dùng CCCD để luôn duy nhất và đồng bộ đăng nhập.
                boolean userCreated = false;
                String username = safeTrim(dialog.getCCCD());
                if (username.isEmpty()) {
                    throw new IllegalArgumentException("CCCD không hợp lệ để tạo username.");
                }
                if (userService.getByUsername(username).isEmpty()) {
                    User studentUser = User.builder()
                        .username(username)
                        .password(generatedPassword)
                        .email(defaultEmailIfBlank(dialog.getEmail(), username))
                        .fullName((safeTrim(dialog.getHo()) + " " + safeTrim(dialog.getTen())).trim())
                        .phoneNumber(blankToNull(dialog.getDienthoai()))
                        .idRoleValue(3) // HOC_SINH = role ID 3
                        .status("active")
                        .build();

                    userService.create(studentUser);
                    userCreated = true;
                }

                String infoMsg = "Thêm thí sinh thành công!\n\n" +
                        "Thông tin sinh viên:\n" +
                        "Họ tên: " + dialog.getHo() + " " + dialog.getTen() + "\n" +
                        "Ngày sinh: " + dialog.getNgaysinh() + "\n" +
                        "Email: " + dialog.getEmail() + "\n" +
                        "Số điện thoại: " + dialog.getDienthoai() + "\n\n";
                
                if (userCreated) {
                    infoMsg += "Tài khoản học sinh được tạo:\n" +
                        "Username: " + username + "\n" +
                        "Mật khẩu: " + generatedPassword;
                } else {
                    infoMsg += "Tài khoản học sinh đã tồn tại.\n" +
                        "Username: " + username;
                }

                loadDataToTable();
                javax.swing.JOptionPane.showMessageDialog(this, infoMsg, "Thành công", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                javax.swing.JOptionPane.showMessageDialog(this, "Lỗi khi thêm thí sinh: " + ex.getMessage(), "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String generatePasswordFromDOB(String ngaySinh) {
        if (ngaySinh == null || ngaySinh.isEmpty()) {
            return "000000";
        }
        try {
            String[] parts = ngaySinh.split("/");
            if (parts.length >= 3) {
                String day = parts[0].length() == 1 ? "0" + parts[0] : parts[0];
                String month = parts[1].length() == 1 ? "0" + parts[1] : parts[1];
                String year = parts[2];
                if (year.length() == 4) {
                    year = year.substring(2);
                }
                return day + month + year;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "000000";
    }

    private String blankToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private String defaultEmailIfBlank(String email, String username) {
        String trimmed = safeTrim(email);
        if (!trimmed.isEmpty()) {
            return trimmed;
        }
        return username + "@hoc_sinh.local";
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
            try {
                String cccd = (String) tableModel.getValueAt(selectedRow, 0);
                Optional<ThiSinh> optThiSinh = thiSinhService.getByCccd(cccd);
                if (optThiSinh.isEmpty()) {
                    javax.swing.JOptionPane.showMessageDialog(this, "Không tìm thấy thí sinh trong CSDL để cập nhật!", "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
                    return;
                }

                ThiSinh thiSinh = optThiSinh.get();
                thiSinh.setSobaodanh(dialog.getSbaodanh());
                thiSinh.setHo(dialog.getHo());
                thiSinh.setTen(dialog.getTen());
                thiSinh.setNgaySinh(dialog.getNgaysinh());
                thiSinh.setGioiTinh(dialog.getGioitinh());
                thiSinh.setEmail(dialog.getEmail());
                thiSinh.setDienThoai(dialog.getDienthoai());
                thiSinh.setNoiSinh(dialog.getNoisinh());
                thiSinh.setDoiTuong(dialog.getDoituong());
                thiSinh.setKhuVuc(dialog.getKhuvuc());
                thiSinh.setUpdatedAt(LocalDate.now());
                if (!dialog.getPassword().isBlank()) {
                    thiSinh.setPassword(dialog.getPassword());
                }

                thiSinhService.update(thiSinh);
                loadDataToTable();
                javax.swing.JOptionPane.showMessageDialog(this, "Cập nhật thí sinh thành công!", "Thành công", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                javax.swing.JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật: " + ex.getMessage(), "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
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
            try {
                String cccd = (String) tableModel.getValueAt(selectedRow, 0);
                boolean deleted = thiSinhService.deleteByCccd(cccd);
                if (deleted) {
                    loadDataToTable();
                    javax.swing.JOptionPane.showMessageDialog(this, "Xóa thí sinh thành công!", "Thành công", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, "Không tìm thấy thí sinh để xóa!", "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                javax.swing.JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + ex.getMessage(), "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleImport() {
        ImportExcelDialog dialog = new ImportExcelDialog(getTopLevelAncestor() instanceof java.awt.Frame ? 
                (java.awt.Frame) getTopLevelAncestor() : null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                List<ThiSinh> imported = thiSinhService.importFromExcel(dialog.getSelectedFile().getAbsolutePath());
                
                // Tạo tài khoản cho mỗi thí sinh được import
                int accountsCreated = 0;
                for (ThiSinh thiSinh : imported) {
                    try {
                        String generatedPassword = generatePasswordFromDOB(thiSinh.getNgaySinh());
                        // Kiểm tra xem người dùng đã tồn tại chưa
                        String username = safeTrim(thiSinh.getCccd());
                        if (username.isEmpty()) {
                            continue;
                        }
                        if (userService.getByUsername(username).isEmpty()) {
                            User studentUser = User.builder()
                                .username(username)
                                .password(generatedPassword)
                                .email(defaultEmailIfBlank(thiSinh.getEmail(), username))
                                .fullName((thiSinh.getHo() != null ? thiSinh.getHo() : "") + " " + (thiSinh.getTen() != null ? thiSinh.getTen() : ""))
                                .phoneNumber(blankToNull(thiSinh.getDienThoai()))
                                .idRoleValue(3) // HOC_SINH = role ID 3
                                .status("active")
                                .build();
                            
                            userService.create(studentUser);
                            accountsCreated++;
                        }
                    } catch (Exception e) {
                        // Tiếp tục tạo các tài khoản khác nếu có lỗi
                        e.printStackTrace();
                    }
                }
                
                loadDataToTable();
                String message = "Đã import " + imported.size() + " thí sinh từ file: " + dialog.getSelectedFile().getName() + "\n" +
                        "Đã tạo " + accountsCreated + " tài khoản học sinh";
                javax.swing.JOptionPane.showMessageDialog(this,
                    message,
                    "Import thành công", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Lỗi khi import file Excel: " + ex.getMessage(),
                    "Import thất bại", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleSearch(String searchTerm) {
        if (isCccdOnlyMode()) {
            loadDataToTable();
            return;
        }

        String keyword = searchTerm == null ? "" : searchTerm.trim().toLowerCase();
        if (keyword.isEmpty() || keyword.equals("tìm cccd, họ tên...")) {
            loadDataToTable();
            return;
        }

        List<ThiSinh> filtered = currentData.stream()
                .filter(ts -> containsIgnoreCase(ts.getCccd(), keyword)
                        || containsIgnoreCase(ts.getHo(), keyword)
                        || containsIgnoreCase(ts.getTen(), keyword)
                        || containsIgnoreCase((ts.getHo() == null ? "" : ts.getHo()) + " " + (ts.getTen() == null ? "" : ts.getTen()), keyword))
                .toList();
        renderTable(filtered);

        if (filtered.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Không tìm thấy thí sinh phù hợp.", "Thông báo", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private boolean containsIgnoreCase(String source, String keyword) {
        return source != null && source.toLowerCase().contains(keyword);
    }

    private boolean isCccdOnlyMode() {
        return !SessionManager.hasPermission("THISINH_VIEW") && SessionManager.hasPermission("THISINH_VIEW_BY_CCCD");
    }

    private String getSessionUsernameAsCccd() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null || currentUser.getUsername() == null) {
            return "";
        }
        return currentUser.getUsername().trim();
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
