package com.tuyensinh.view;

import com.tuyensinh.model.ThiSinh;
import com.tuyensinh.service.ThiSinhService;

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
        searchInput.setText("Tìm CCCD, họ tên...");
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

        JPanel pagination = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 8));
        pagination.setOpaque(false);
        pagination.add(createButton("Trước", UIStyles.PRIMARY));
        pagination.add(new JLabel(" Trang 1 / 10 "));
        pagination.add(createButton("Sau", UIStyles.PRIMARY));
        tableCard.add(pagination, BorderLayout.SOUTH);

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
                        .password(dialog.getPassword().isBlank() ? dialog.getCCCD() : dialog.getPassword())
                        .build();

                thiSinhService.create(thiSinh);
                loadDataToTable();
                javax.swing.JOptionPane.showMessageDialog(this, "Thêm thí sinh thành công!", "Thành công", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                javax.swing.JOptionPane.showMessageDialog(this, "Lỗi khi thêm thí sinh: " + ex.getMessage(), "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
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
            loadDataToTable();
            javax.swing.JOptionPane.showMessageDialog(this,
                "Đã import " + imported.size() + " thí sinh từ file: " + dialog.getSelectedFile().getName(),
                "Import thành công", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Lỗi khi import file Excel: " + ex.getMessage(),
                "Import thất bại", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleSearch(String searchTerm) {
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
