package com.breakout;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Breakout Ball Game");
        Gameplay gameplay = new Gameplay();

        frame.setBounds(10, 10, 700, 600); // Set window size
        frame.setResizable(false); // Disable resizing
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close on exit
        frame.add(gameplay); // Add gameplay panel
        frame.setVisible(true); // Make the frame visible
    }
}
