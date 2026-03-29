package com.tuyensinh;

import com.tuyensinh.view.RoleSelectionFrame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RoleSelectionFrame().setVisible(true));
    }
}