package game.gui;

import game.battle.BattleScreen;
import game.battle.Fighter;
import game.core.ProgressionManager;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class GameScene extends JFrame {
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JLabel backgroundLabel;
    private JButton nextButton;
    private JButton skipButton;
    private ProgressionManager progressionManager;
    private volatile boolean isTyping = false;

    private final int W = 1280;
    private final int H = 720;

    private static final int SPAWN_X = 4205;
    private static final int SPAWN_Y = 5125;

    public GameScene(ProgressionManager pm) {
        this.progressionManager = pm;
        setTitle("ALAMAT - Journey");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(W, H));
        setContentPane(layeredPane);

        backgroundLabel = new JLabel();
        backgroundLabel.setBounds(0, 0, W, H);
        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);

        JPanel textPanel = new JPanel(null);
        textPanel.setBounds(10, H - 200, W - 20, 185);
        textPanel.setBackground(new Color(0, 0, 0, 230));
        textPanel.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 4));

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(new Color(230, 230, 200));
        textArea.setFont(new Font("Monospaced", Font.BOLD, 18));
        textArea.setMargin(new Insets(10, 15, 10, 15));

        scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(15, 15, W - 180, 155);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        textPanel.add(scrollPane);

        nextButton = new JButton("Next");
        nextButton.setBounds(W - 155, 120, 130, 50);
        nextButton.setForeground(Color.RED);
        nextButton.setBackground(Color.BLACK);
        nextButton.setFont(new Font("Arial", Font.BOLD, 18));
        nextButton.setFocusPainted(false);
        nextButton.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        nextButton.addActionListener(e -> { if (!isTyping) progressionManager.handleNextScene(); });
        textPanel.add(nextButton);

        skipButton = new JButton("SKIP");
        skipButton.setBounds(W - 155, 60, 130, 50);
        skipButton.setForeground(Color.RED);
        skipButton.setBackground(Color.BLACK);
        skipButton.setFont(new Font("Arial", Font.BOLD, 18));
        skipButton.setFocusPainted(false);
        skipButton.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        skipButton.addActionListener(e -> switchToWorld());
        textPanel.add(skipButton);

        layeredPane.add(textPanel, JLayeredPane.PALETTE_LAYER);
        pack();
        setLocationRelativeTo(null);
    }

    public void updateDisplay(String imagePath, String text) {
        File imgFile = new File(imagePath);
        if (imgFile.exists()) {
            ImageIcon icon = new ImageIcon(imagePath);
            Image img = icon.getImage().getScaledInstance(W, H, Image.SCALE_SMOOTH);
            backgroundLabel.setIcon(new ImageIcon(img));
        }
        if (!text.isEmpty()) startTyping(text);
    }

    private void startTyping(String text) {
        isTyping = true;
        textArea.setText("");
        nextButton.setEnabled(false);
        new Thread(() -> {
            for (char c : text.toCharArray()) {
                addChar(String.valueOf(c));
                try { Thread.sleep(30); } catch (InterruptedException ex) {}
            }
            isTyping = false;
            SwingUtilities.invokeLater(() -> {
                if (!nextButton.getText().equals("Wait...")) nextButton.setEnabled(true);
            });
        }).start();
    }

    // ✅ First time - default item counts
    public void switchToWorld() {
        SwingUtilities.invokeLater(() -> {
            this.getContentPane().removeAll();
            this.setLayout(new BorderLayout());
            WorldPanel world = new WorldPanel(this);
            this.add(world, BorderLayout.CENTER);
            this.pack();
            this.setLocationRelativeTo(null);
            this.revalidate();
            this.repaint();
            SwingUtilities.invokeLater(() -> {
                world.requestFocusInWindow();
                world.start();
            });
        });
    }

    // ✅ Return to world - now carries lunas and potions too
    public void switchToWorldAt(int x, int y,
                                ArrayList<Fighter> team,
                                Fighter playerFighter,
                                int scrollCount,
                                int lunasCount,
                                int potionCount,
                                long cooldownUntil) {
        SwingUtilities.invokeLater(() -> {
            this.getContentPane().removeAll();
            this.setLayout(new BorderLayout());
            WorldPanel world = new WorldPanel(
                    this, x, y, team, playerFighter,
                    scrollCount, lunasCount, potionCount, cooldownUntil);
            this.add(world, BorderLayout.CENTER);
            this.pack();
            this.setLocationRelativeTo(null);
            this.revalidate();
            this.repaint();
            SwingUtilities.invokeLater(() -> {
                world.requestFocusInWindow();
                world.start();
            });
        });
    }

    // ✅ Launch battle - now passes lunas and potions in, reads them back out
    public void switchToBattle(Fighter playerFighter,
                               Fighter wildFighter,
                               ArrayList<Fighter> team,
                               int scrollCount,
                               int lunasCount,
                               int potionCount,
                               int savedX, int savedY) {
        SwingUtilities.invokeLater(() -> {
            this.getContentPane().removeAll();
            this.setLayout(new BorderLayout());

            BattleScreen battle = new BattleScreen(
                    playerFighter,
                    wildFighter,
                    team,
                    scrollCount,
                    lunasCount,
                    potionCount,

                    // ✅ onBattleEnd - receive updated counts back
                    (updatedFighter, updatedTeam, updatedScrolls,
                     updatedLunas, updatedPotions, blackout) -> {
                        long cooldownUntil = System.currentTimeMillis() + 5000L;
                        if (blackout) {
                            switchToWorldAt(SPAWN_X, SPAWN_Y,
                                    updatedTeam, updatedFighter,
                                    updatedScrolls, updatedLunas, updatedPotions, 0L);
                        } else {
                            switchToWorldAt(savedX, savedY,
                                    updatedTeam, updatedFighter,
                                    updatedScrolls, updatedLunas, updatedPotions, cooldownUntil);
                        }
                    },

                    // ✅ onRun - receive updated counts back
                    (updatedFighter, updatedTeam, updatedScrolls,
                     updatedLunas, updatedPotions, blackout) -> {
                        long cooldownUntil = System.currentTimeMillis() + 5000L;
                        switchToWorldAt(savedX, savedY,
                                updatedTeam, updatedFighter,
                                updatedScrolls, updatedLunas, updatedPotions, cooldownUntil);
                    }
            );

            this.add(battle, BorderLayout.CENTER);
            this.pack();
            this.setLocationRelativeTo(null);
            this.revalidate();
            this.repaint();
        });
    }

    public void addChar(String c) {
        SwingUtilities.invokeLater(() -> {
            textArea.append(c);
            textArea.setCaretPosition(textArea.getDocument().getLength());
        });
    }

    public void clearText() {
        SwingUtilities.invokeLater(() -> textArea.setText(""));
    }

    public void setNextButtonEnabled(boolean enabled) {
        SwingUtilities.invokeLater(() -> nextButton.setEnabled(enabled));
    }

    public void setNextButtonText(String text) {
        SwingUtilities.invokeLater(() -> nextButton.setText(text));
    }

    public void setSkipButtonVisible(boolean visible) {
        SwingUtilities.invokeLater(() -> skipButton.setVisible(visible));
    }
}