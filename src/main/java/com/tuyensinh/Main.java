package com.tuyensinh;

import com.tuyensinh.view.RoleSelectionFrame;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RoleSelectionFrame frame = new RoleSelectionFrame();
            frame.setVisible(true);
        });
    }
}
