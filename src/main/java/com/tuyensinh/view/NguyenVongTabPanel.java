package com.tuyensinh.view;

import javax.swing.*;
import java.awt.*;

public class NguyenVongTabPanel extends JPanel {

    private final NguyenVongPanel nguyenVongPanel;
    private final XetTuyenPanel xetTuyenPanel;
    private final KetQuaPanel ketQuaPanel;
    private final JTabbedPane tabbedPane;

    public NguyenVongTabPanel() {
        setLayout(new BorderLayout());
        setBackground(UIStyles.BG_APP);

        nguyenVongPanel = new NguyenVongPanel();
        xetTuyenPanel = new XetTuyenPanel();
        ketQuaPanel = new KetQuaPanel();

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIStyles.FONT_BODY);
        tabbedPane.addTab("  Dữ liệu nguyện vọng  ", createTabIcon(UIStyles.PRIMARY), nguyenVongPanel, "Quản lý danh sách nguyện vọng");
        tabbedPane.addTab("  Thực hiện xét tuyển  ", createTabIcon(UIStyles.SUCCESS), xetTuyenPanel, "Cấu hình và chạy xét tuyển");
        tabbedPane.addTab("  Kết quả & Thống kê  ", createTabIcon(UIStyles.INFO), ketQuaPanel, "Xem kết quả và thống kê trúng tuyển");

        tabbedPane.addChangeListener(e -> {
            int idx = tabbedPane.getSelectedIndex();
            if (idx == 0) {
                nguyenVongPanel.refreshData();
            } else if (idx == 2) {
                ketQuaPanel.refreshData();
            }
        });

        add(tabbedPane, BorderLayout.CENTER);
    }

    private Icon createTabIcon(Color color) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(12, 12));
        panel.setBackground(color);
        return null;
    }

    public void refreshAll() {
        nguyenVongPanel.refreshData();
        ketQuaPanel.refreshData();
    }
}
