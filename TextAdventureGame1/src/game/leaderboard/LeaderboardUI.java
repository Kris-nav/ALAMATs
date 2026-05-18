package game.leaderboard;

import game.gui.GameScene;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.util.List;

/**
 * LeaderboardUI - Display the leaderboard screen after game completion
 */
public class LeaderboardUI {
    private Leaderboard leaderboard;
    private GameScene gameScene;
    private String newPlayerName;
    private long newPlayerTime;

    public LeaderboardUI(Leaderboard leaderboard, GameScene gameScene, String playerName, long playerTime) {
        this.leaderboard = leaderboard;
        this.gameScene = gameScene;
        this.newPlayerName = playerName;
        this.newPlayerTime = playerTime;
    }

    /**
     * Display leaderboard screen with animation
     */
    public void display() {
        SwingUtilities.invokeLater(this::showLeaderboard);
    }

    private void showLeaderboard() {
        gameScene.getContentPane().removeAll();
        gameScene.setLayout(new BorderLayout());

        JPanel leaderboardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawLeaderboard(g);
            }
        };
        leaderboardPanel.setBackground(new Color(8, 6, 16));
        leaderboardPanel.setPreferredSize(new Dimension(1280, 720));

        gameScene.add(leaderboardPanel, BorderLayout.CENTER);
        gameScene.pack();
        gameScene.setLocationRelativeTo(null);
        gameScene.revalidate();
        gameScene.repaint();

        // Auto-return to start after 15 seconds
        Timer autoReturn = new Timer(15000, e -> {
            ((Timer) e.getSource()).stop();
            gameScene.returnToStartScreen();
        });
        autoReturn.start();
    }

    private void drawLeaderboard(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = 1280, height = 720;

        // Background with stars
        g2.setColor(new Color(8, 6, 16));
        g2.fillRect(0, 0, width, height);
        drawStars(g2, width, height);

        // Title
        g2.setColor(new Color(255, 215, 90));
        g2.setFont(new Font("Monospaced", Font.BOLD, 52));
        FontMetrics fm = g2.getFontMetrics();
        String title = "⭐ LEADERBOARD ⭐";
        g2.drawString(title, (width - fm.stringWidth(title)) / 2, 80);

        // Subtitle
        g2.setColor(new Color(180, 150, 80));
        g2.setFont(new Font("Monospaced", Font.PLAIN, 14));
        fm = g2.getFontMetrics();
        String subtitle = "Best Times (Fastest Wins!)";
        g2.drawString(subtitle, (width - fm.stringWidth(subtitle)) / 2, 110);

        // Divider line
        g2.setColor(new Color(255, 215, 90));
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(100, 130, width - 100, 130);

        // Leaderboard entries
        List<Leaderboard.LeaderboardEntry> entries = leaderboard.getEntries();
        int startY = 180;
        int entryHeight = 50;

        // Check if new player made the board
        int newPlayerRank = leaderboard.getRank(newPlayerTime);
        boolean newPlayerOnBoard = newPlayerRank <= 10 && newPlayerTime > 0;

        for (int i = 0; i < Math.min(10, entries.size()); i++) {
            Leaderboard.LeaderboardEntry entry = entries.get(i);
            int entryY = startY + (i * entryHeight);

            // Highlight new player
            boolean isNewPlayer = newPlayerOnBoard && newPlayerRank == (i + 1);
            Color bgColor = isNewPlayer ? new Color(100, 150, 50, 100) : new Color(30, 20, 60, 80);
            Color textColor = isNewPlayer ? new Color(255, 255, 100) : new Color(200, 200, 255);

            // Entry background
            g2.setColor(bgColor);
            g2.fillRoundRect(120, entryY, width - 240, entryHeight - 8, 8, 8);

            // Border
            g2.setColor(new Color(100, 80, 180));
            g2.setStroke(new BasicStroke(isNewPlayer ? 3 : 1));
            g2.drawRoundRect(120, entryY, width - 240, entryHeight - 8, 8, 8);

            // Rank
            g2.setColor(new Color(255, 215, 90));
            g2.setFont(new Font("Monospaced", Font.BOLD, 16));
            String rankStr = "#" + (i + 1);
            fm = g2.getFontMetrics();
            g2.drawString(rankStr, 150, entryY + 32);

            // Player name
            g2.setColor(textColor);
            g2.setFont(new Font("Monospaced", Font.BOLD, 16));
            String name = entry.getPlayerName();
            if (name.length() > 25) name = name.substring(0, 25) + "...";
            g2.drawString(name, 250, entryY + 32);

            // Time
            g2.setColor(textColor);
            g2.setFont(new Font("Monospaced", Font.BOLD, 16));
            String timeStr = entry.getFormattedTime();
            fm = g2.getFontMetrics();
            g2.drawString(timeStr, width - 250, entryY + 32);

            // NEW tag
            if (isNewPlayer) {
                g2.setColor(new Color(255, 100, 100));
                g2.setFont(new Font("Monospaced", Font.BOLD, 12));
                g2.drawString("★ NEW", width - 150, entryY + 15);
            }
        }

        // If no entries yet
        if (entries.isEmpty()) {
            g2.setColor(new Color(150, 150, 150));
            g2.setFont(new Font("Monospaced", Font.PLAIN, 18));
            fm = g2.getFontMetrics();
            String msg = "No entries yet. Be the first!";
            g2.drawString(msg, (width - fm.stringWidth(msg)) / 2, 400);
        }

        // New player info (if didn't make board)
        if (newPlayerOnBoard) {
            g2.setColor(new Color(255, 200, 100));
            g2.setFont(new Font("Monospaced", Font.BOLD, 14));
            fm = g2.getFontMetrics();
            String newMsg = "🎉 " + newPlayerName + " made the leaderboard!";
            g2.drawString(newMsg, (width - fm.stringWidth(newMsg)) / 2, 650);
        } else if (newPlayerTime > 0) {
            g2.setColor(new Color(150, 150, 150));
            g2.setFont(new Font("Monospaced", Font.PLAIN, 13));
            fm = g2.getFontMetrics();
            String missedMsg = newPlayerName + " finished in " + formatTime(newPlayerTime) + " (Rank #" + newPlayerRank + ")";
            g2.drawString(missedMsg, (width - fm.stringWidth(missedMsg)) / 2, 650);
        }

        // Instructions
        g2.setColor(new Color(100, 150, 200));
        g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
        fm = g2.getFontMetrics();
        String hint = "Returning to menu in 15 seconds...";
        g2.drawString(hint, (width - fm.stringWidth(hint)) / 2, 690);
    }

    private void drawStars(Graphics2D g2, int width, int height) {
        g2.setColor(new Color(255, 255, 255, 50));
        for (int i = 0; i < 50; i++) {
            int x = (int) (Math.random() * width);
            int y = (int) (Math.random() * height);
            g2.fillOval(x, y, 2, 2);
        }
    }

    private String formatTime(long millis) {
        long totalSeconds = millis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
}