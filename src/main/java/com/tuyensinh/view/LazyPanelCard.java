package com.tuyensinh.view;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.function.Supplier;

/**
 * Lazily creates a panel when first displayed and isolates load failures
 * to avoid breaking the whole main frame.
 */
public class LazyPanelCard extends JPanel {
    private final Supplier<JPanel> panelFactory;
    private boolean loaded;

    public LazyPanelCard(Supplier<JPanel> panelFactory) {
        this.panelFactory = panelFactory;
        this.loaded = false;
        setLayout(new BorderLayout());
        setBackground(UIStyles.BG_APP);
    }

    public void ensureLoaded() {
        if (loaded) {
            return;
        }

        removeAll();
        try {
            JPanel content = panelFactory.get();
            add(content, BorderLayout.CENTER);
        } catch (Throwable ex) {
            add(createErrorPanel(ex), BorderLayout.CENTER);
        }
        loaded = true;
        revalidate();
        repaint();
    }

    private JPanel createErrorPanel(Throwable ex) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(244, 245, 248));

        JLabel label = new JLabel("Loi: " + ex.getMessage(), SwingConstants.CENTER);
        label.setBorder(new EmptyBorder(10, 10, 10, 10));
        label.setFont(UIStyles.FONT_BODY);
        label.setForeground(new Color(118, 122, 140));

        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
}