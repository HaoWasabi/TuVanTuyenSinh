package com.tuyensinh.view;

import com.tuyensinh.model.DiemCong;
import com.tuyensinh.service.DiemCongService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class DiemCongPanel extends JPanel {
    private final DefaultTableModel tableModel;
    private final JTable table;

    // CÁC BIẾN NÀY ĐỂ QUẢN LÝ PHÂN TRANG
    private List<DiemCong> currentDataList = new java.util.ArrayList<>();
    private int currentPage = 1;
    private final int pageSize = 20; // 20 dòng 1 trang
    private int totalPages = 1;

    private JLabel pageLabel;
    private JButton prevBtn;
    private JButton nextBtn;


    // GỌI SERVICE Ở ĐÂY
    private final DiemCongService diemCongService = new DiemCongService();

    public DiemCongPanel() {
        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(UIStyles.BG_APP);

        // Title
        JLabel title = new JLabel("Quản Lý Điểm Cộng");
        title.setFont(UIStyles.FONT_TITLE);
        title.setForeground(UIStyles.TEXT_DARK);
        add(title, BorderLayout.NORTH);

        // Search & Actions Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);

        JTextField searchInput = new JTextField(28);
        searchInput.setText("Tìm CCCD, mã ngành...");
        searchInput.setFont(UIStyles.FONT_BODY);
        searchInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(6, 10, 6, 10)
        ));

        JButton searchBtn = createButton("Tìm kiếm", UIStyles.PRIMARY);
        searchBtn.addActionListener(e -> handleSearch(searchInput.getText()));

        JButton importBtn = createButton("Import Excel", UIStyles.SUCCESS);
        importBtn.addActionListener(e -> handleImport());

        JButton addBtn = createButton("Thêm", UIStyles.INFO);
        addBtn.addActionListener(e -> handleAdd());

        JButton editBtn = createButton("Sửa", UIStyles.WARNING);
        editBtn.addActionListener(e -> handleEdit());

        JButton deleteBtn = createButton("Xóa", UIStyles.DANGER);
        deleteBtn.addActionListener(e -> handleDelete());

        toolbar.add(searchInput);
        toolbar.add(searchBtn);
        toolbar.add(new JSeparator(JSeparator.VERTICAL));
        toolbar.add(importBtn);
        toolbar.add(addBtn);
        toolbar.add(editBtn);
        toolbar.add(deleteBtn);

        // Cấu hình Cột cho Bảng Điểm Cộng
        String[] cols = {
                "ID", "CCCD", "Mã Ngành", "Mã Tổ Hợp", "Phương thức",
                "Điểm CC", "Điểm UTXT", "Điểm Tổng", "Ghi chú", "Keys"
        };

        tableModel = new DefaultTableModel(null, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho sửa trực tiếp trên ô
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.getTableHeader().setFont(UIStyles.FONT_LABEL);
        table.getTableHeader().setBackground(new Color(247, 249, 251));
        table.setFont(UIStyles.FONT_BODY);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Có thanh cuộn ngang vì rất nhiều cột

        // Chỉnh độ rộng một số cột quan trọng
        table.getColumnModel().getColumn(0).setPreferredWidth(50); // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(120); // CCCD
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Mã ngành

        JPanel tableCard = new JPanel(new BorderLayout(0, 12));
        tableCard.setBackground(UIStyles.BG_CARD);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel tableTitle = new JLabel("Danh sách điểm cộng thí sinh");
        tableTitle.setFont(UIStyles.FONT_SUBTITLE);
        tableTitle.setForeground(UIStyles.TEXT_DARK);
        tableCard.add(tableTitle, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(table);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        // Pagination UI
        JPanel pagination = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 8));
        pagination.setOpaque(false);

        prevBtn = createButton("Trước", UIStyles.PRIMARY);
        nextBtn = createButton("Sau", UIStyles.PRIMARY);
        pageLabel = new JLabel(" Trang 1 / 1 ");
        pageLabel.setFont(UIStyles.FONT_BODY);

        // Sự kiện chuyển trang
        prevBtn.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                renderTablePage();
            }
        });

        nextBtn.addActionListener(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                renderTablePage();
            }
        });

        pagination.add(prevBtn);
        pagination.add(pageLabel);
        pagination.add(nextBtn);
        tableCard.add(pagination, BorderLayout.SOUTH);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BorderLayout(0, 12));
        center.add(toolbar, BorderLayout.NORTH);
        center.add(tableCard, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        // TỰ ĐỘNG LOAD DỮ LIỆU KHI MỞ PANEL
        loadDataToTable();
    }

    // ================= CÁC HÀM XỬ LÝ LOGIC =================

    private void loadDataToTable() {
        // Lấy toàn bộ dữ liệu từ Service cất vào danh sách hiện tại
        currentDataList = diemCongService.getAll();
        currentPage = 1; // Reset về trang 1
        renderTablePage();
    }

    // Chỉ vẽ đúng 20 dòng của trang hiện tại
    private void renderTablePage() {
        tableModel.setRowCount(0);

        // Tính toán tổng số trang
        totalPages = (int) Math.ceil((double) currentDataList.size() / pageSize);
        if (totalPages == 0) totalPages = 1;

        // Cập nhật text và trạng thái nút bấm
        pageLabel.setText(" Trang " + currentPage + " / " + totalPages + " ");
        prevBtn.setEnabled(currentPage > 1);
        nextBtn.setEnabled(currentPage < totalPages);

        // Tính vị trí cắt list
        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, currentDataList.size());

        // Đổ 20 dòng ra bảng
        for (int i = start; i < end; i++) {
            DiemCong dc = currentDataList.get(i);
            Object[] row = {
                    dc.getIddiemcong(), dc.getTsCccd(), dc.getManganh(), dc.getMatohop(), dc.getPhuongthuc(),
                    dc.getDiemCC(), dc.getDiemUtxt(), dc.getDiemTong(), dc.getGhichu(), dc.getDcKeys()
            };
            tableModel.addRow(row);
        }
    }

    private void handleImport() {
        ImportExcelDialog dialog = new ImportExcelDialog(getTopLevelAncestor() instanceof Frame ?
                (Frame) getTopLevelAncestor() : null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            File selectedFile = dialog.getSelectedFile();
            try {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                diemCongService.importFromExcel(selectedFile.getAbsolutePath());
                loadDataToTable();
                setCursor(Cursor.getDefaultCursor());

                JOptionPane.showMessageDialog(this, "Import điểm cộng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                setCursor(Cursor.getDefaultCursor());
                JOptionPane.showMessageDialog(this, "Lỗi Import: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void handleSearch(String keyword) {
        // Nếu ô tìm kiếm trống, load lại toàn bộ danh sách gốc
        if (keyword.isEmpty() || keyword.equals("Tìm CCCD, mã ngành...")) {
            loadDataToTable();
            return;
        }

        // Chuyển từ khóa về chữ thường để tìm kiếm không phân biệt hoa thường
        String lowerKeyword = keyword.toLowerCase().trim();

        // Xin lại toàn bộ dữ liệu gốc từ DB (để tránh bị lọc đè lên kết quả cũ)
        List<DiemCong> allData = diemCongService.getAll();

        // DÙNG JAVA STREAM ĐỂ LỌC (TÌM THEO CCCD HOẶC MÃ NGÀNH)
        currentDataList = allData.stream()
                .filter(dc ->
                        (dc.getTsCccd() != null && dc.getTsCccd().toLowerCase().contains(lowerKeyword)) ||
                                (dc.getManganh() != null && dc.getManganh().toLowerCase().contains(lowerKeyword))
                )
                .collect(java.util.stream.Collectors.toList());

        // Reset về trang 1 và vẽ lại bảng
        currentPage = 1;
        renderTablePage();

        // Thông báo nếu không tìm thấy
        if (currentDataList.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả nào cho: " + keyword, "Thông báo", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleAdd() {
        DiemCongFormDialog dialog = new DiemCongFormDialog(
                getTopLevelAncestor() instanceof Frame ? (Frame) getTopLevelAncestor() : null,
                "Thêm Điểm Cộng Mới",
                false);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                // Tạo object Model mới từ data của Dialog
                DiemCong dc = new DiemCong();
                dc.setTsCccd(dialog.getCccd());
                dc.setManganh(dialog.getMaNganh());
                dc.setMatohop(dialog.getMaToHop());
                dc.setPhuongthuc(dialog.getPhuongThuc());
                dc.setDiemCC(dialog.getDiemCC());
                dc.setDiemUtxt(dialog.getDiemUtxt());
                dc.setDiemTong(dialog.getDiemTong());
                dc.setGhichu(dialog.getGhiChu());
                dc.setDcKeys(dialog.getKeys()); // Hoặc tự generate kiểu: "DC_" + dialog.getCccd() + "_" + dialog.getMaNganh()

                // GỌI HÀM SAVE/ADD CỦA BACKEND
                diemCongService.add(dc); // LƯU Ý: Đổi tên hàm "add" cho đúng với Service của bạn

                loadDataToTable(); // Load lại bảng
                JOptionPane.showMessageDialog(this, "Thêm thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleEdit() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 dòng để sửa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Lấy dữ liệu từ bảng (Lưu ý index cột phải khớp với mảng String[] cols của bạn)
        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0); // Giả sử cột 0 là ID
        String cccd = (String) tableModel.getValueAt(selectedRow, 1);
        String manganh = (String) tableModel.getValueAt(selectedRow, 2);
        String matohop = (String) tableModel.getValueAt(selectedRow, 3);
        String phuongthuc = (String) tableModel.getValueAt(selectedRow, 4);
        java.math.BigDecimal diemCC = new java.math.BigDecimal(tableModel.getValueAt(selectedRow, 5).toString());
        java.math.BigDecimal diemUtxt = new java.math.BigDecimal(tableModel.getValueAt(selectedRow, 6).toString());
        java.math.BigDecimal diemTong = new java.math.BigDecimal(tableModel.getValueAt(selectedRow, 7).toString());
        String ghichu = (String) tableModel.getValueAt(selectedRow, 8);
        String keys = (String) tableModel.getValueAt(selectedRow, 9);

        // Mở hộp thoại Sửa
        DiemCongFormDialog dialog = new DiemCongFormDialog(
                getTopLevelAncestor() instanceof Frame ? (Frame) getTopLevelAncestor() : null,
                "Sửa Thông Tin Điểm Cộng",
                true); // true = isEditing

        dialog.setData(cccd, manganh, matohop, phuongthuc, diemCC, diemUtxt, diemTong, ghichu, keys);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                // Tạo object Model update
                DiemCong dc = new DiemCong();
                dc.setIddiemcong(id); // Set ID để update đúng dòng
                dc.setTsCccd(dialog.getCccd());
                dc.setManganh(dialog.getMaNganh());
                dc.setMatohop(dialog.getMaToHop());
                dc.setPhuongthuc(dialog.getPhuongThuc());
                dc.setDiemCC(dialog.getDiemCC());
                dc.setDiemUtxt(dialog.getDiemUtxt());
                dc.setDiemTong(dialog.getDiemTong());
                dc.setGhichu(dialog.getGhiChu());
                dc.setDcKeys(dialog.getKeys());

                // GỌI HÀM UPDATE CỦA BACKEND
                diemCongService.update(dc); // LƯU Ý: Đổi tên hàm "update" cho đúng với Service của bạn

                loadDataToTable(); // Load lại bảng
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //Lấy CCCD ở cột 0, Mã ngành ở cột 1
        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
        String cccd = (String) tableModel.getValueAt(selectedRow, 1);

        DeleteConfirmDialog dialog = new DeleteConfirmDialog(getTopLevelAncestor() instanceof Frame ?
                (Frame) getTopLevelAncestor() : null, "Điểm cộng của CCCD: " + cccd);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            // GỌI HÀM XÓA BẰNG ID:
            boolean success = diemCongService.delete(id);

            if(success) {
                tableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Xóa thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
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