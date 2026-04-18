package game.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JFrame {

    private boolean playPressed = false;

    public MainMenu() {
        setTitle("ALAMAT");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        ImageIcon icon = new ImageIcon("resources/Texture/prkson.png");
        Image scaledBg = icon.getImage().getScaledInstance(1280, 720, Image.SCALE_SMOOTH);
        int width = 1280;
        int height = 720;

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(width, height));

        JLabel background = new JLabel(new ImageIcon(scaledBg));
        background.setBounds(0, 0, width, height);
        layeredPane.add(background, Integer.valueOf(0));

        JLabel instructionLabel = new JLabel("Click Play to Start", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        instructionLabel.setForeground(Color.WHITE);
        instructionLabel.setBounds(0, height / 2 + 50, width, 40);
        layeredPane.add(instructionLabel, Integer.valueOf(1));

        ImageIcon playIcon = new ImageIcon("resources/Texture/play_buttons2.png");
        Image scaledImage = playIcon.getImage().getScaledInstance(220, 100, Image.SCALE_SMOOTH);
        JButton playButton = new JButton(new ImageIcon(scaledImage));
        playButton.setContentAreaFilled(false);
        playButton.setBorderPainted(false);
        playButton.setFocusPainted(false);
        playButton.setOpaque(false);
        playButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        playButton.setBounds(width / 2 - 110, height / 2 + 100, 220, 100);

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (MainMenu.this) {
                    playPressed = true;
                    MainMenu.this.notify();
                }
            }
        });
        layeredPane.add(playButton, Integer.valueOf(1));

        add(layeredPane);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void waitForPlay() {
        synchronized (this) {
            while (!playPressed) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        setVisible(false);
        dispose();
    }
}