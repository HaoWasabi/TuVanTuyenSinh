package com.tuyensinh.view;

import java.awt.Color;
import java.awt.Font;

/**
 * Adminty Theme Colors & Styles
 * Reference: https://colorlib.com/polygon/adminty/
 */
public final class UIStyles {
    // Adminty Color Palette
    public static final Color PRIMARY = new Color(54, 150, 240);        // Bright Blue
    public static final Color PRIMARY_DARK = new Color(39, 110, 188);   // Dark Blue
    public static final Color SECONDARY = new Color(247, 249, 251);     // Very Light Gray
    public static final Color BG_APP = new Color(242, 244, 248);        // Light Background
    public static final Color BG_SIDEBAR = new Color(45, 52, 66);       // Dark Sidebar
    public static final Color BG_TOPBAR = Color.WHITE;                  // White Topbar
    public static final Color BG_CARD = Color.WHITE;                    // Card Background
    
    // Status Colors
    public static final Color SUCCESS = new Color(39, 182, 107);        // Green
    public static final Color WARNING = new Color(255, 159, 64);        // Orange
    public static final Color DANGER = new Color(255, 99, 132);         // Red
    public static final Color INFO = new Color(54, 150, 240);           // Blue
    
    // Text Colors
    public static final Color TEXT_DARK = new Color(33, 37, 50);        // Dark Text
    public static final Color TEXT_LIGHT = new Color(245, 249, 252);    // Light Text
    public static final Color TEXT_MUTED = new Color(133, 135, 150);    // Gray Text
    public static final Color BORDER = new Color(229, 232, 239);        // Light Border

    // Typography - Segoe UI (Adminty style)
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BODY_SMALL = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_TINY = new Font("Segoe UI", Font.PLAIN, 11);

    private UIStyles() {
    }
}
