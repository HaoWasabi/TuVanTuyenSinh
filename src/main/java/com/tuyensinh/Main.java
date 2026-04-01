package com.tuyensinh;

import com.tuyensinh.view.AdmissionsDemoFrame;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdmissionsDemoFrame frame = new AdmissionsDemoFrame();
            frame.setVisible(true);
        });
    }
}
