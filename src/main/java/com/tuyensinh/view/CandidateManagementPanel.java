package com.tuyensinh.view;

import com.tuyensinh.model.ThiSinh;
import com.tuyensinh.service.ThiSinhService;

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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CandidateManagementPanel extends JPanel {
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final ThiSinhService thiSinhService = new ThiSinhService();
    private List<ThiSinh> currentData = new ArrayList<>();

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

        loadDataToTable();
    }

    private void loadDataToTable() {
        currentData = thiSinhService.getAll();
        renderTable(currentData);
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
