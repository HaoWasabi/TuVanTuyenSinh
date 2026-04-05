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
    private DefaultTableModel tableModel;
    private JTable table;
    private final JTextField detailIdField = new JTextField();
    private final JTextField detailCccdField = new JTextField();
    private final JTextField detailSbdField = new JTextField();
    private final JTextField detailMethodField = new JTextField();
    private final JTextField detailNaturalField = new JTextField();
    private final JTextField detailSocialField = new JTextField();
    private final JTextField detailForeignField = new JTextField();
    private final JTextField detailExtraField = new JTextField();
    private final JLabel selectedLabel = new JLabel("Chưa chọn bản ghi");

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

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createListCard(), createDetailCard());
        splitPane.setResizeWeight(0.64);
        splitPane.setDividerSize(8);
        splitPane.setDividerLocation(0.64);
        splitPane.setBorder(null);
        add(splitPane, BorderLayout.CENTER);

        // TỰ ĐỘNG LOAD DỮ LIỆU KHI MỞ PANEL
        loadDataToTable();
    }

    private JPanel createListCard() {

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
        JButton refreshBtn = createButton("Làm mới", UIStyles.INFO);
        refreshBtn.addActionListener(e -> handleRefresh());

        toolbar.add(searchInput);
        toolbar.add(searchBtn);
        toolbar.add(new JSeparator(JSeparator.VERTICAL));

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
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateDetailFromSelection();
            }
        });

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

        JPanel listHeader = new JPanel(new BorderLayout());
        listHeader.setOpaque(false);
        listHeader.add(tableTitle, BorderLayout.WEST);
        JPanel listActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        listActions.setOpaque(false);
        listActions.add(refreshBtn);
        listHeader.add(listActions, BorderLayout.EAST);
        tableCard.add(listHeader, BorderLayout.NORTH);

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
        JLabel title = new JLabel("Chi tiết điểm thi");
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
        fields.add(labelWithField("ID điểm thi", detailIdField));
        fields.add(labelWithField("CCCD", detailCccdField));
        fields.add(labelWithField("Số báo danh", detailSbdField));
        fields.add(labelWithField("Phương thức", detailMethodField));
        fields.add(labelWithField("Nhóm tự nhiên (Toán/Lý/Hóa)", detailNaturalField));
        fields.add(labelWithField("Nhóm xã hội (Sinh/Sử/Địa/Văn)", detailSocialField));
        fields.add(labelWithField("Ngoại ngữ - N1 thi/N1 CC", detailForeignField));
        fields.add(labelWithField("Môn khác (CNCN/CNNN/Tin/KTPL/NL1/NK1/NK2)", detailExtraField));

        JScrollPane fieldsScroll = new JScrollPane(fields);
        fieldsScroll.setBorder(null);
        fieldsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        fieldsScroll.getVerticalScrollBar().setUnitIncrement(12);

        configureReadOnlyField(detailIdField);
        configureReadOnlyField(detailCccdField);
        configureReadOnlyField(detailSbdField);
        configureReadOnlyField(detailMethodField);
        configureReadOnlyField(detailNaturalField);
        configureReadOnlyField(detailSocialField);
        configureReadOnlyField(detailForeignField);
        configureReadOnlyField(detailExtraField);
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
            selectedLabel.setText("Chưa chọn bản ghi");
            detailIdField.setText("");
            detailCccdField.setText("");
            detailSbdField.setText("");
            detailMethodField.setText("");
            detailNaturalField.setText("");
            detailSocialField.setText("");
            detailForeignField.setText("");
            detailExtraField.setText("");
            return;
        }

        selectedLabel.setText("Đang chọn ID: " + String.valueOf(tableModel.getValueAt(row, 0)));
        detailIdField.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        detailCccdField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        detailSbdField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        detailMethodField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        detailNaturalField.setText(
            String.valueOf(tableModel.getValueAt(row, 4)) + " / " +
                String.valueOf(tableModel.getValueAt(row, 5)) + " / " +
                String.valueOf(tableModel.getValueAt(row, 6))
        );
        detailSocialField.setText(
            String.valueOf(tableModel.getValueAt(row, 7)) + " / " +
                String.valueOf(tableModel.getValueAt(row, 8)) + " / " +
                String.valueOf(tableModel.getValueAt(row, 9)) + " / " +
                String.valueOf(tableModel.getValueAt(row, 10))
        );
        detailForeignField.setText(
            String.valueOf(tableModel.getValueAt(row, 11)) + " / " +
                String.valueOf(tableModel.getValueAt(row, 12))
        );
        detailExtraField.setText(
            String.valueOf(tableModel.getValueAt(row, 13)) + " / " +
                String.valueOf(tableModel.getValueAt(row, 14)) + " / " +
                String.valueOf(tableModel.getValueAt(row, 15)) + " / " +
                String.valueOf(tableModel.getValueAt(row, 16)) + " / " +
                String.valueOf(tableModel.getValueAt(row, 17)) + " / " +
                String.valueOf(tableModel.getValueAt(row, 18)) + " / " +
                String.valueOf(tableModel.getValueAt(row, 19))
        );
    }

    // ================= CÁC HÀM XỬ LÝ LOGIC =================

    private void loadDataToTable() {
        // Lấy toàn bộ dữ liệu từ Service cất vào danh sách hiện tại
        currentDataList = diemThiService.getAll();
        currentPage = 1; // Reset về trang 1
        renderTablePage();
    }

    private void handleRefresh() {
        loadDataToTable();
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

        if (tableModel.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        } else {
            updateDetailFromSelection();
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