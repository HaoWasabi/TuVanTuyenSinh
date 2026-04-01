package com.tuyensinh.view;

import com.tuyensinh.model.DiemThi;
import com.tuyensinh.service.DiemThiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;

public class DiemThiPanel extends JPanel {
    private final DefaultTableModel tableModel;
    private final JTable table;

    // CÁC BIẾN NÀY ĐỂ QUẢN LÝ PHÂN TRANG
    private List<DiemThi> currentDataList = new java.util.ArrayList<>();
    private int currentPage = 1;
    private final int pageSize = 20; // 20 dòng 1 trang
    private int totalPages = 1;

    private JLabel pageLabel;
    private JButton prevBtn;
    private JButton nextBtn;

    // GỌI SERVICE Ở ĐÂY
    private final DiemThiService diemThiService = new DiemThiService();

    public DiemThiPanel() {
        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(UIStyles.BG_APP);

        // Title
        JLabel title = new JLabel("Quản Lý Điểm Thi");
        title.setFont(UIStyles.FONT_TITLE);
        title.setForeground(UIStyles.TEXT_DARK);
        add(title, BorderLayout.NORTH);

        // Search & Actions Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);

        JTextField searchInput = new JTextField(28);
        searchInput.setText("Tìm CCCD, SBD...");
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

        // Cấu hình Cột cho Bảng Điểm Thi
        String[] cols = {
                "ID", "CCCD", "SBD", "Phương thức", "Toán", "Lý", "Hóa", "Sinh", "Sử", "Địa", "Văn",
                "N1_Thi", "N1_CC", "CNCN", "CNNN", "Tin Học", "KTPL", "NL1", "NK1", "NK2"
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
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // SBD

        JPanel tableCard = new JPanel(new BorderLayout(0, 12));
        tableCard.setBackground(UIStyles.BG_CARD);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel tableTitle = new JLabel("Danh sách điểm thi thí sinh");
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
        currentDataList = diemThiService.getAll();
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
            DiemThi dt = currentDataList.get(i);
            Object[] row = {
                    dt.getIddiemthi(), dt.getCccd(), dt.getSobaodanh(), dt.getDPhuongthuc(),
                    dt.getToan(), dt.getVatLi(), dt.getHoaHoc(), dt.getSinhHoc(), dt.getLichSu(),
                    dt.getDiaLi(), dt.getNguVan(), dt.getN1Thi(), dt.getN1Cc(), dt.getCncn(),
                    dt.getCnnn(), dt.getTinHoc(), dt.getKtpl(), dt.getNl1(), dt.getNk1(), dt.getNk2()
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
                // GỌI HÀM IMPORT TỪ BACKEND CỦA BẠN
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)); // Đổi chuột thành hình chờ
                diemThiService.importFromExcel(selectedFile.getAbsolutePath());
                loadDataToTable(); // Nạp lại bảng
                setCursor(Cursor.getDefaultCursor());

                JOptionPane.showMessageDialog(this, "Import điểm thi thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                setCursor(Cursor.getDefaultCursor());
                JOptionPane.showMessageDialog(this, "Lỗi Import: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void handleSearch(String keyword) {
        // Nếu ô tìm kiếm trống, load lại toàn bộ danh sách gốc
        if (keyword.isEmpty() || keyword.equals("Tìm CCCD, SBD...")) {
            loadDataToTable();
            return;
        }

        // Chuyển từ khóa về chữ thường để tìm kiếm không phân biệt hoa thường
        String lowerKeyword = keyword.toLowerCase().trim();

        // Xin lại toàn bộ dữ liệu gốc từ DB (để tránh bị lọc đè lên kết quả cũ)
        List<DiemThi> allData = diemThiService.getAll();

        // DÙNG JAVA STREAM ĐỂ LỌC (TÌM THEO CCCD HOẶC SỐ BÁO DANH)
        currentDataList = allData.stream()
                .filter(dt ->
                        (dt.getCccd() != null && dt.getCccd().toLowerCase().contains(lowerKeyword)) ||
                                (dt.getSobaodanh() != null && dt.getSobaodanh().toLowerCase().contains(lowerKeyword))
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
        DiemThiFormDialog dialog = new DiemThiFormDialog(
                getTopLevelAncestor() instanceof Frame ? (Frame) getTopLevelAncestor() : null,
                "Thêm Điểm Thi Thí Sinh", false);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                // Sử dụng Lombok Builder để tạo object sạch sẽ và dễ nhìn (vì class DiemThi có @Builder)
                DiemThi dt = DiemThi.builder()
                        .cccd(dialog.getCccd())
                        .sobaodanh(dialog.getSoBaoDanh())
                        .dPhuongthuc(dialog.getDPhuongThuc())
                        .toan(dialog.getToan())
                        .nguVan(dialog.getVan())
                        .n1Thi(dialog.getN1Thi())
                        .n1Cc(dialog.getN1Cc())
                        .vatLi(dialog.getLy())
                        .hoaHoc(dialog.getHoa())
                        .sinhHoc(dialog.getSinh())
                        .lichSu(dialog.getSu())
                        .diaLi(dialog.getDia())
                        .ktpl(dialog.getKtpl())
                        .tinHoc(dialog.getTinHoc())
                        .cncn(dialog.getCncn())
                        .cnnn(dialog.getCnnn())
                        .nl1(dialog.getNl1())
                        .nk1(dialog.getNk1())
                        .nk2(dialog.getNk2())
                        .build();

                diemThiService.add(dt);

                loadDataToTable();
                JOptionPane.showMessageDialog(this, "Thêm điểm thi thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleEdit() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 dòng điểm thi để sửa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // CHÚ Ý: Các chỉ số (0, 1, 2...) dưới đây PHẢI KHỚP với thứ tự các cột
            // trong mảng String[] columnNames mà bạn truyền vào JTable.
            Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
            String cccd = (String) tableModel.getValueAt(selectedRow, 1);
            String sbd = (String) tableModel.getValueAt(selectedRow, 2);
            String phuongThuc = (String) tableModel.getValueAt(selectedRow, 3);

            BigDecimal toan = new BigDecimal(tableModel.getValueAt(selectedRow, 4).toString());
            BigDecimal van = new BigDecimal(tableModel.getValueAt(selectedRow, 5).toString());
            BigDecimal n1Thi = new BigDecimal(tableModel.getValueAt(selectedRow, 6).toString());
            BigDecimal n1Cc = new BigDecimal(tableModel.getValueAt(selectedRow, 7).toString());
            BigDecimal ly = new BigDecimal(tableModel.getValueAt(selectedRow, 8).toString());
            BigDecimal hoa = new BigDecimal(tableModel.getValueAt(selectedRow, 9).toString());
            BigDecimal sinh = new BigDecimal(tableModel.getValueAt(selectedRow, 10).toString());
            BigDecimal su = new BigDecimal(tableModel.getValueAt(selectedRow, 11).toString());
            BigDecimal dia = new BigDecimal(tableModel.getValueAt(selectedRow, 12).toString());
            BigDecimal ktpl = new BigDecimal(tableModel.getValueAt(selectedRow, 13).toString());
            BigDecimal tinHoc = new BigDecimal(tableModel.getValueAt(selectedRow, 14).toString());
            BigDecimal cncn = new BigDecimal(tableModel.getValueAt(selectedRow, 15).toString());
            BigDecimal cnnn = new BigDecimal(tableModel.getValueAt(selectedRow, 16).toString());
            BigDecimal nl1 = new BigDecimal(tableModel.getValueAt(selectedRow, 17).toString());
            BigDecimal nk1 = new BigDecimal(tableModel.getValueAt(selectedRow, 18).toString());
            BigDecimal nk2 = new BigDecimal(tableModel.getValueAt(selectedRow, 19).toString());

            DiemThiFormDialog dialog = new DiemThiFormDialog(
                    getTopLevelAncestor() instanceof Frame ? (Frame) getTopLevelAncestor() : null,
                    "Sửa Thông Tin Điểm Thi", true);

            dialog.setData(cccd, sbd, phuongThuc, toan, van, n1Thi, n1Cc, ly, hoa, sinh, su, dia, ktpl, tinHoc, cncn, cnnn, nl1, nk1, nk2);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                DiemThi dt = DiemThi.builder()
                        .iddiemthi(id) // Quan trọng để Entity Framework / JPA biết là lệnh Update
                        .cccd(dialog.getCccd())
                        .sobaodanh(dialog.getSoBaoDanh())
                        .dPhuongthuc(dialog.getDPhuongThuc())
                        .toan(dialog.getToan())
                        .nguVan(dialog.getVan())
                        .n1Thi(dialog.getN1Thi())
                        .n1Cc(dialog.getN1Cc())
                        .vatLi(dialog.getLy())
                        .hoaHoc(dialog.getHoa())
                        .sinhHoc(dialog.getSinh())
                        .lichSu(dialog.getSu())
                        .diaLi(dialog.getDia())
                        .ktpl(dialog.getKtpl())
                        .tinHoc(dialog.getTinHoc())
                        .cncn(dialog.getCncn())
                        .cnnn(dialog.getCnnn())
                        .nl1(dialog.getNl1())
                        .nk1(dialog.getNk1())
                        .nk2(dialog.getNk2())
                        .build();

                diemThiService.update(dt);

                loadDataToTable();
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật. Vui lòng kiểm tra lại thứ tự cột JTable!\nChi tiết: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0); // Lấy ID ở cột 0
        String cccd = (String) tableModel.getValueAt(selectedRow, 1);

        DeleteConfirmDialog dialog = new DeleteConfirmDialog(getTopLevelAncestor() instanceof Frame ?
                (Frame) getTopLevelAncestor() : null, "Điểm thi của CCCD: " + cccd);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            boolean success = diemThiService.delete(id);
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