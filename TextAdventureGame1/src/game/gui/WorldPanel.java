package game.gui;

import game.battle.Fighter;
import game.battle.Move;
import game.core.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class WorldPanel extends JPanel implements Runnable {
    private Thread gameThread;
    private KeyHandler keyH = new KeyHandler();

    private ArrayList<Fighter> capturedTeam;
    private Fighter playerFighter;
    private int scrollCount;
    private int lunasCount;
    private int potionCount;

    // ✅ Player profile
    private String playerName   = "";
    private int    playerAge    = 0;
    private String playerGender = "";
    private int    antingAntingCount = 0;

    // ✅ Speedrun timer
    private long gameStartTime = System.currentTimeMillis();
    private boolean timerRunning = true;

    private final int TILE_SIZE = 16;
    private final int SCALE = 9;
    private final int TILE_DISPLAY_SIZE = TILE_SIZE * SCALE;

    private int playerX;
    private int playerY;
    private final int PLAYER_SPEED = 4;
    private final int PLAYER_SIZE_W = 100;
    private final int PLAYER_SIZE_H = 120;
    private BufferedImage playerSheet;

    private int frameCounter = 100;
    private int frameDelay   = 100;
    private int currentFrame = 0;
    private int currentRow   = 0;

    private int cameraX = 0;
    private int cameraY = 0;

    private Set<Integer> solidTiles = new HashSet<>();
    private GameScene gameScene;

    private boolean inBattle = false;
    private long encounterCooldownUntil;

    private boolean showBag = false;
    private int selectedIndex         = -1;
    private int selectedCreatureIndex = -1;
    private JPanel bagOverlay = null;
    private ArrayList<Rectangle> bagRowRects = new ArrayList<>();

    // ✅ Welcome message state
    private boolean showWelcome  = false;
    private JPanel  welcomePanel = null;

    private int[][][] allLayerData;
    private int mapWidth;
    private int mapHeight;
    private HashMap<Integer, BufferedImage> tileCache = new HashMap<>();

    private String pendingItemUse = "";
    private Fighter pendingTarget = null;

    // ✅ First time constructor - with profile, shows welcome
    public WorldPanel(GameScene gameScene,
                      String playerName, int playerAge, String playerGender) {
        this(gameScene, 4205, 5125,
                new ArrayList<>(),
                game.battle.Create.createPlayerStarter(),
                3, 3, 3,
                playerName, playerAge, playerGender,
                System.currentTimeMillis(),
                0L);
        SwingUtilities.invokeLater(() -> new Timer(400, e -> {
            ((Timer) e.getSource()).stop();
            showWelcomeMessage();
        }).start());
    }

    // ✅ Full constructor - restores all state after battle
    public WorldPanel(GameScene gameScene,
                      int startX, int startY,
                      ArrayList<Fighter> team,
                      Fighter playerFighter,
                      int scrollCount,
                      int lunasCount,
                      int potionCount,
                      String playerName,
                      int playerAge,
                      String playerGender,
                      long gameStartTime,
                      long cooldownUntil) {
        this.gameScene              = gameScene;
        this.playerX                = startX;
        this.playerY                = startY;
        this.capturedTeam           = team;
        this.playerFighter          = playerFighter;
        this.scrollCount            = scrollCount;
        this.lunasCount             = lunasCount;
        this.potionCount            = potionCount;
        this.playerName             = playerName;
        this.playerAge              = playerAge;
        this.playerGender           = playerGender;
        this.gameStartTime          = gameStartTime;
        this.timerRunning           = true;
        this.encounterCooldownUntil = cooldownUntil;

        this.setLayout(null);
        this.setPreferredSize(new Dimension(1280, 720));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        try {
            playerSheet = ImageIO.read(new File("resources/Texture/Avatar1.png"));
        } catch (Exception e) {
            System.err.println("Could not load player: " + e.getMessage());
        }

        loadMap("resources/World1.tmx");
        initSolidTiles();
    }

    // ══════════════════════════════════════════════════════════════
    // WELCOME MESSAGE
    // ══════════════════════════════════════════════════════════════

    private void showWelcomeMessage() {
        if (welcomePanel != null) this.remove(welcomePanel);

        int pw = 700, ph = 190;
        int px = (1280 - pw) / 2;
        int py = (720  - ph) / 2;

        welcomePanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 200));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        welcomePanel.setOpaque(false);
        welcomePanel.setBounds(0, 0, 1280, 720);

        JPanel box = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(18, 12, 5));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(new Color(170, 120, 50));
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            }
        };
        box.setOpaque(false);
        box.setBounds(px, py, pw, ph);

        JLabel l1 = welcomeLabel("You have been left with a creature", new Color(255, 215, 90), 18, true);
        JLabel l2 = welcomeLabel("of your Grandpa — a Santelmo!", Color.WHITE, 16, false);
        JLabel l3 = welcomeLabel("\"Take care of it. Your adventure begins now, "
                + playerName + ".\"", new Color(180, 180, 180), 13, false);
        JLabel l4 = welcomeLabel("Good luck on your speedrun!", new Color(100, 200, 255), 12, false);

        l1.setBounds(20, 16, pw - 40, 28);
        l2.setBounds(20, 50, pw - 40, 24);
        l3.setBounds(20, 80, pw - 40, 22);
        l4.setBounds(20, 106, pw - 40, 20);
        box.add(l1); box.add(l2); box.add(l3); box.add(l4);

        JButton okBtn = new JButton("LET'S GO!");
        okBtn.setBackground(new Color(40, 130, 55));
        okBtn.setForeground(Color.WHITE);
        okBtn.setFont(new Font("Monospaced", Font.BOLD, 14));
        okBtn.setFocusPainted(false);
        okBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        okBtn.setBounds(pw / 2 - 80, ph - 52, 160, 36);
        okBtn.addActionListener(e -> dismissWelcome());
        box.add(okBtn);

        welcomePanel.add(box);
        this.add(welcomePanel);
        this.setComponentZOrder(welcomePanel, 0);
        showWelcome = true;
        this.revalidate();
        this.repaint();
    }

    private JLabel welcomeLabel(String text, Color color, int size, boolean bold) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setForeground(color);
        lbl.setFont(new Font("Monospaced", bold ? Font.BOLD : Font.PLAIN, size));
        return lbl;
    }

    private void dismissWelcome() {
        if (welcomePanel != null) {
            this.remove(welcomePanel);
            welcomePanel = null;
        }
        showWelcome = false;
        this.revalidate();
        this.repaint();
        requestFocusInWindow();
    }

    // ══════════════════════════════════════════════════════════════
    // HUD — top-left profile card with timer
    // ══════════════════════════════════════════════════════════════

    private void drawHUD(Graphics2D g2) {
        int x = 10, y = 10;
        int w = 200, h = 126;

        // Card background
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(x, y, w, h, 12, 12);
        g2.setColor(new Color(170, 120, 50));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(x, y, w, h, 12, 12);

        int tx = x + 10;
        int ty = y + 18;
        int lineH = 18;

        // Name
        g2.setColor(new Color(255, 215, 90));
        g2.setFont(new Font("Monospaced", Font.BOLD, 13));
        g2.drawString(playerName.isEmpty() ? "Player" : playerName, tx, ty);
        ty += lineH;

        // Age & Gender
        g2.setColor(new Color(200, 200, 200));
        g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
        g2.drawString("Age: " + playerAge + "  |  " + playerGender, tx, ty);
        ty += lineH;

        // Divider
        g2.setColor(new Color(100, 70, 15));
        g2.setStroke(new BasicStroke(1));
        g2.drawLine(tx, ty - 4, x + w - 10, ty - 4);
        ty += 4;

        // Anting-Anting label
        g2.setColor(new Color(200, 160, 60));
        g2.setFont(new Font("Monospaced", Font.BOLD, 11));
        g2.drawString("Anting-Anting:", tx, ty + 4);
        ty += lineH;

        // Dots
        for (int i = 0; i < 4; i++) {
            boolean has = (i < antingAntingCount);
            g2.setColor(has ? new Color(255, 200, 50) : new Color(60, 50, 30));
            g2.fillOval(tx + i * 22, ty - 12, 16, 16);
            g2.setColor(has ? new Color(200, 150, 20) : new Color(80, 60, 20));
            g2.setStroke(new BasicStroke(1));
            g2.drawOval(tx + i * 22, ty - 12, 16, 16);
        }
        g2.setColor(new Color(160, 160, 160));
        g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
        g2.drawString(antingAntingCount + "/4", tx + 96, ty);
        ty += lineH;

        // Divider before timer
        g2.setColor(new Color(100, 70, 15));
        g2.setStroke(new BasicStroke(1));
        g2.drawLine(tx, ty - 6, x + w - 10, ty - 6);
        ty += 2;

        // ✅ Timer
        long elapsed     = System.currentTimeMillis() - gameStartTime;
        long totalSecs   = elapsed / 1000;
        long hours       = totalSecs / 3600;
        long minutes     = (totalSecs % 3600) / 60;
        long seconds     = totalSecs % 60;

        String timeStr = hours > 0
                ? String.format("%02d:%02d:%02d", hours, minutes, seconds)
                : String.format("%02d:%02d", minutes, seconds);

        // Clock icon background pill
        g2.setColor(new Color(20, 20, 40, 200));
        g2.fillRoundRect(tx - 2, ty - 2, w - 18, 18, 6, 6);

        g2.setColor(new Color(100, 200, 255));
        g2.setFont(new Font("Monospaced", Font.BOLD, 12));
        g2.drawString("TIME  " + timeStr, tx + 2, ty + 12);
    }

    // ══════════════════════════════════════════════════════════════
    // BAG OVERLAY
    // ══════════════════════════════════════════════════════════════

    private void openBag() {
        showBag               = true;
        selectedIndex         = -1;
        selectedCreatureIndex = -1;
        pendingItemUse        = "";
        pendingTarget         = null;
        buildBagOverlay("main");
    }

    private void closeBag() {
        showBag               = false;
        selectedIndex         = -1;
        selectedCreatureIndex = -1;
        pendingItemUse        = "";
        pendingTarget         = null;
        if (bagOverlay != null) {
            this.remove(bagOverlay);
            bagOverlay = null;
            this.repaint();
        }
    }

    private void buildBagOverlay(String screen) {
        if (bagOverlay != null) {
            this.remove(bagOverlay);
            bagOverlay = null;
        }

        int screenW = 1280, screenH = 720;
        int winW = 860, winH = 520;
        int winX = (screenW - winW) / 2;
        int winY = (screenH - winH) / 2;

        bagOverlay = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 190));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(22, 14, 6));
                g2.fillRoundRect(winX, winY, winW, winH, 18, 18);
                g2.setColor(new Color(170, 120, 50));
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(winX, winY, winW, winH, 18, 18);
                g2.setColor(new Color(70, 44, 8));
                g2.fillRoundRect(winX, winY, winW, 48, 18, 18);
                g2.fillRect(winX, winY + 28, winW, 20);
                g2.setColor(new Color(255, 215, 90));
                g2.setFont(new Font("Monospaced", Font.BOLD, 22));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("BAG",
                        winX + (winW - fm.stringWidth("BAG")) / 2, winY + 34);
                g2.setColor(new Color(170, 120, 50));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(winX + 16, winY + 50, winX + winW - 16, winY + 50);
                g2.setColor(new Color(120, 120, 120));
                g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
                FontMetrics hfm = g2.getFontMetrics();
                String hint = "Press [B] to close";
                g2.drawString(hint,
                        winX + (winW - hfm.stringWidth(hint)) / 2,
                        winY + winH - 14);
            }
        };
        bagOverlay.setOpaque(false);
        bagOverlay.setBounds(0, 0, screenW, screenH);

        if (screen.equals("main")) {
            buildMainBagScreen(bagOverlay, winX, winY, winW, winH);
        } else if (screen.equals("creatureSelect")) {
            buildCreatureSelectScreen(bagOverlay, winX, winY, winW, winH);
        } else if (screen.equals("confirm")) {
            buildConfirmScreen(bagOverlay, winX, winY, winW, winH);
        }

        this.add(bagOverlay);
        this.setComponentZOrder(bagOverlay, 0);
        this.revalidate();
        this.repaint();
    }

    private void buildMainBagScreen(JPanel overlay, int winX, int winY,
                                    int winW, int winH) {
        int leftX = winX + 20;
        int leftY = winY + 60;
        int leftW = 360;
        int rowH  = 46;
        int gap   = 5;

        JPanel leftPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(200, 155, 60));
                g2.setFont(new Font("Monospaced", Font.BOLD, 12));
                g2.drawString("ITEMS", 0, 14);
                g2.setColor(new Color(100, 70, 15));
                g2.setStroke(new BasicStroke(1));
                g2.drawLine(0, 18, leftW, 18);
                int y = 22 + rowH * 3 + gap * 3 + 16;
                int teamSize = capturedTeam.size() + 1;
                g2.setColor(new Color(200, 155, 60));
                g2.setFont(new Font("Monospaced", Font.BOLD, 12));
                g2.drawString("TEAM  (" + teamSize + "/6)", 0, y + 14);
                g2.setColor(new Color(100, 70, 15));
                g2.drawLine(0, y + 18, leftW, y + 18);
            }
        };
        leftPanel.setOpaque(false);
        leftPanel.setBounds(leftX, leftY, leftW, winH - 80);
        overlay.add(leftPanel);

        String[] itemNames  = {"Scroll", "Lunas", "Potion"};
        int[]    itemCounts = {scrollCount, lunasCount, potionCount};
        Color[]  itemColors = {
                new Color(140, 70, 200),
                new Color(60, 180, 100),
                new Color(60, 140, 220)
        };

        int iy = 22;
        for (int i = 0; i < 3; i++) {
            final int idx = i;
            boolean sel = (selectedIndex == i && selectedCreatureIndex == -1);
            JButton btn = styledItemBtn(itemNames[i], itemCounts[i], itemColors[i], sel);
            btn.setBounds(0, iy, leftW, rowH);
            btn.addActionListener(e -> {
                selectedIndex         = (selectedIndex == idx && selectedCreatureIndex == -1) ? -1 : idx;
                selectedCreatureIndex = -1;
                buildBagOverlay("main");
            });
            leftPanel.add(btn);
            iy += rowH + gap;
        }

        ArrayList<Fighter> fullTeam = new ArrayList<>();
        fullTeam.add(playerFighter);
        fullTeam.addAll(capturedTeam);

        int teamStartY = iy + 16 + 22;
        for (int i = 0; i < fullTeam.size(); i++) {
            final int creatureIdx = i;
            Fighter f        = fullTeam.get(i);
            boolean isActive = (f == playerFighter);
            boolean isSel    = (selectedCreatureIndex == i);

            JButton row = buildTeamRowBtn(f, isActive, isSel, leftW, rowH);
            row.setBounds(0, teamStartY, leftW, rowH);
            row.addActionListener(e -> {
                selectedCreatureIndex = (selectedCreatureIndex == creatureIdx) ? -1 : creatureIdx;
                selectedIndex         = -1;
                buildBagOverlay("main");
            });
            leftPanel.add(row);
            teamStartY += rowH + gap;
        }

        int rightX = winX + leftW + 48;
        int rightW = winW - leftW - 68;
        int rightY = winY + 60;
        int rightH = winH - 80;

        JPanel rightPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(15, 10, 4));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(100, 70, 20));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
        };
        rightPanel.setOpaque(false);
        rightPanel.setBounds(rightX, rightY, rightW, rightH);
        overlay.add(rightPanel);

        if (selectedCreatureIndex >= 0 && selectedCreatureIndex < fullTeam.size()) {
            buildCreatureDetailContent(rightPanel, rightW, rightH,
                    fullTeam.get(selectedCreatureIndex));
        } else {
            buildDetailContent(rightPanel, rightW, rightH);
        }
    }

    private void buildDetailContent(JPanel panel, int w, int h) {
        panel.removeAll();

        if (selectedIndex == -1) {
            JLabel hint = new JLabel(
                    "<html><center><font color='gray'>Click an item or<br>creature to see details</font></center></html>",
                    SwingConstants.CENTER);
            hint.setFont(new Font("Monospaced", Font.PLAIN, 13));
            hint.setBounds(0, h / 2 - 30, w, 60);
            panel.add(hint);
            return;
        }

        String[] names  = {"SCROLL", "LUNAS", "POTION"};
        Color[]  colors = {
                new Color(140, 70, 200),
                new Color(60, 180, 100),
                new Color(60, 140, 220)
        };
        int[] counts = {scrollCount, lunasCount, potionCount};
        String[][] descs = {
                {"Quantity: x" + scrollCount, "", "Used to capture wild",
                        "creatures in battle.", "", "Catch rate increases", "when enemy HP is low."},
                {"Quantity: x" + lunasCount,  "", "Restores 5 PP to one",
                        "move of a creature.", "", "Use when your moves", "are running out of PP."},
                {"Quantity: x" + potionCount, "", "Restores 30 HP to",
                        "one of your creatures.", "", "Cannot revive fainted", "creatures."}
        };

        int pad = 16, cy = 16;

        JLabel titleLbl = new JLabel(names[selectedIndex]);
        titleLbl.setForeground(colors[selectedIndex]);
        titleLbl.setFont(new Font("Monospaced", Font.BOLD, 18));
        titleLbl.setBounds(pad, cy, w - pad * 2, 24);
        panel.add(titleLbl);
        cy += 30;

        for (String line : descs[selectedIndex]) {
            JLabel lbl = new JLabel(line);
            lbl.setForeground(line.startsWith("Quantity") ? Color.WHITE
                    : line.isEmpty() ? Color.WHITE : new Color(180, 180, 180));
            lbl.setFont(new Font("Monospaced", Font.PLAIN, 13));
            lbl.setBounds(pad, cy, w - pad * 2, 18);
            panel.add(lbl);
            cy += 18;
        }
        cy += 12;

        boolean hasItem = counts[selectedIndex] > 0;
        JButton useBtn = new JButton(hasItem ? "USE" : "NONE LEFT");
        useBtn.setBackground(hasItem ? colors[selectedIndex] : new Color(80, 80, 80));
        useBtn.setForeground(Color.WHITE);
        useBtn.setFont(new Font("Monospaced", Font.BOLD, 14));
        useBtn.setFocusPainted(false);
        useBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        useBtn.setEnabled(hasItem);
        useBtn.setBounds(pad, cy, w - pad * 2, 38);

        final int sel = selectedIndex;
        useBtn.addActionListener(e -> {
            if (sel == 0) useScrollWorld();
            else if (sel == 1) { pendingItemUse = "lunas"; buildBagOverlay("creatureSelect"); }
            else if (sel == 2) { pendingItemUse = "potion"; buildBagOverlay("creatureSelect"); }
        });
        panel.add(useBtn);
        panel.revalidate();
        panel.repaint();
    }

    private void buildCreatureDetailContent(JPanel panel, int w, int h, Fighter f) {
        panel.removeAll();

        int pad = 16, cy = 14;

        boolean isActive = (f == playerFighter);
        JLabel nameLbl = new JLabel(f.name.toUpperCase() + (isActive ? "  ★" : ""));
        nameLbl.setForeground(isActive ? new Color(255, 215, 90) : new Color(100, 200, 255));
        nameLbl.setFont(new Font("Monospaced", Font.BOLD, 16));
        nameLbl.setBounds(pad, cy, w - pad * 2, 22);
        panel.add(nameLbl);
        cy += 26;

        JPanel divider = new JPanel();
        divider.setBackground(new Color(80, 55, 15));
        divider.setBounds(pad, cy, w - pad * 2, 1);
        panel.add(divider);
        cy += 8;

        if (f.types != null && !f.types.isEmpty()) {
            StringBuilder typeStr = new StringBuilder("Type: ");
            for (int t = 0; t < f.types.size(); t++) {
                typeStr.append(f.types.get(t).name);
                if (t < f.types.size() - 1) typeStr.append(" / ");
            }
            JLabel typeLbl = new JLabel(typeStr.toString());
            typeLbl.setForeground(new Color(180, 180, 180));
            typeLbl.setFont(new Font("Monospaced", Font.PLAIN, 12));
            typeLbl.setBounds(pad, cy, w - pad * 2, 16);
            panel.add(typeLbl);
            cy += 20;
        }

        int hp    = (int) Math.max(0, f.stats.get(0).value);
        int maxHp = (int) f.stats.get(0).base;
        int atk   = (int) f.stats.get(1).value;
        int def   = (int) f.stats.get(2).value;
        int spd   = (int) f.stats.get(3).value;

        cy = addStatBar(panel, pad, cy, w, "HP",  hp,  maxHp, new Color(60,  210, 60));
        cy = addStatBar(panel, pad, cy, w, "ATK", atk, 300,   new Color(220, 80,  80));
        cy = addStatBar(panel, pad, cy, w, "DEF", def, 300,   new Color(80,  120, 220));
        cy = addStatBar(panel, pad, cy, w, "SPD", spd, 300,   new Color(220, 180, 0));
        cy += 8;

        JLabel movesTitle = new JLabel("MOVES");
        movesTitle.setForeground(new Color(200, 160, 60));
        movesTitle.setFont(new Font("Monospaced", Font.BOLD, 12));
        movesTitle.setBounds(pad, cy, w - pad * 2, 16);
        panel.add(movesTitle);
        cy += 18;

        for (int m = 0; m < f.moveset.size() && m < 4; m++) {
            Move move    = f.moveset.get(m);
            boolean noPP = move.pp <= 0;

            JLabel moveLbl = new JLabel("• " + move.name);
            moveLbl.setForeground(noPP ? new Color(150, 50, 50) : Color.WHITE);
            moveLbl.setFont(new Font("Monospaced", Font.PLAIN, 11));
            moveLbl.setBounds(pad, cy, w - pad * 2 - 70, 16);
            panel.add(moveLbl);

            JLabel ppLbl = new JLabel("PP " + move.pp + "/" + move.maxPp);
            ppLbl.setForeground(noPP ? new Color(150, 50, 50) : new Color(140, 140, 140));
            ppLbl.setFont(new Font("Monospaced", Font.PLAIN, 11));
            ppLbl.setBounds(w - pad - 70, cy, 70, 16);
            panel.add(ppLbl);
            cy += 18;
        }

        panel.revalidate();
        panel.repaint();
    }

    private int addStatBar(JPanel panel, int pad, int cy, int w,
                           String label, int value, int max, Color barColor) {
        JLabel lbl = new JLabel(label);
        lbl.setForeground(new Color(160, 160, 160));
        lbl.setFont(new Font("Monospaced", Font.BOLD, 11));
        lbl.setBounds(pad, cy, 36, 14);
        panel.add(lbl);

        JLabel valLbl = new JLabel(String.valueOf(value));
        valLbl.setForeground(Color.WHITE);
        valLbl.setFont(new Font("Monospaced", Font.PLAIN, 11));
        valLbl.setBounds(pad + 38, cy, 34, 14);
        panel.add(valLbl);

        int barX = pad + 76;
        int barW = w - pad * 2 - 76;

        JPanel barBg = new JPanel(null);
        barBg.setBackground(new Color(40, 40, 40));
        barBg.setBounds(barX, cy + 2, barW, 10);
        panel.add(barBg);

        float ratio = max > 0 ? Math.min(1f, (float) value / max) : 0;
        JPanel barFill = new JPanel();
        barFill.setBackground(barColor);
        barFill.setBounds(0, 0, (int)(barW * ratio), 10);
        barBg.add(barFill);

        return cy + 20;
    }

    private JButton buildTeamRowBtn(Fighter f, boolean isActive,
                                    boolean isSelected, int w, int h) {
        int hp    = (int) Math.max(0, f.stats.get(0).value);
        int maxHp = (int) f.stats.get(0).base;
        float ratio = maxHp > 0 ? (float) hp / maxHp : 0;
        Color barColor = ratio > 0.5f ? new Color(60, 200, 60)
                : ratio > 0.25f ? new Color(220, 180, 0)
                  : new Color(200, 50, 50);
        Color borderColor = isSelected ? new Color(100, 180, 255)
                : isActive ? new Color(200, 160, 60)
                  : new Color(70, 50, 10);

        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isSelected ? new Color(20, 40, 70)
                        : isActive ? new Color(60, 40, 10)
                          : new Color(28, 18, 5));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(isSelected || isActive ? 2f : 1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                if (isActive) {
                    g2.setColor(new Color(255, 215, 90));
                    g2.setFont(new Font("Monospaced", Font.BOLD, 14));
                    g2.drawString("★", 8, 22);
                }
                g2.setColor(isSelected ? new Color(100, 200, 255)
                        : isActive ? new Color(255, 215, 90) : Color.WHITE);
                g2.setFont(new Font("Monospaced", Font.BOLD, 13));
                g2.drawString(f.name, isActive ? 28 : 12, 22);
                g2.setColor(new Color(170, 170, 170));
                g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
                g2.drawString(hp + "/" + maxHp, getWidth() - 70, 16);
                int bx = 12, by = 28, bw = getWidth() - 80, bh = 7;
                g2.setColor(new Color(50, 50, 50));
                g2.fillRoundRect(bx, by, bw, bh, 3, 3);
                g2.setColor(barColor);
                g2.fillRoundRect(bx, by, (int)(bw * ratio), bh, 3, 3);
                if (!isSelected) {
                    g2.setColor(new Color(90, 90, 90));
                    g2.setFont(new Font("Monospaced", Font.PLAIN, 9));
                    g2.drawString("click for info", getWidth() - 82, getHeight() - 4);
                }
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void buildCreatureSelectScreen(JPanel overlay, int winX, int winY,
                                           int winW, int winH) {
        String itemName  = pendingItemUse.equals("lunas") ? "Lunas" : "Potion";
        Color  itemColor = pendingItemUse.equals("lunas")
                ? new Color(60, 180, 100) : new Color(60, 140, 220);

        int cx = winX + 24, cy = winY + 62, cw = winW - 48;

        JLabel titleLbl = new JLabel("Use " + itemName + " on which creature?");
        titleLbl.setForeground(itemColor);
        titleLbl.setFont(new Font("Monospaced", Font.BOLD, 15));
        titleLbl.setBounds(cx, cy, cw, 24);
        overlay.add(titleLbl);
        cy += 36;

        ArrayList<Fighter> fullTeam = new ArrayList<>();
        fullTeam.add(playerFighter);
        fullTeam.addAll(capturedTeam);

        int cols = 3, btnW = (cw - (cols - 1) * 10) / cols;
        int btnH = 64, col = 0, rowY = cy;

        for (Fighter f : fullTeam) {
            int hp    = (int) Math.max(0, f.stats.get(0).value);
            int maxHp = (int) f.stats.get(0).base;
            boolean fainted = f.isFainted();
            boolean ppFull  = pendingItemUse.equals("lunas")
                    && f.moveset.stream().allMatch(m -> m.pp >= m.maxPp);
            boolean canUse  = !fainted
                    && (pendingItemUse.equals("potion") ? hp < maxHp : !ppFull);

            String label = "<html><center><b>" + f.name + "</b>"
                    + (f == playerFighter ? " ★" : "")
                    + "<br><font size='2'>HP: " + hp + "/" + maxHp
                    + "</font></center></html>";

            Color bg = !canUse ? new Color(60, 40, 40)
                    : (f == playerFighter ? new Color(50, 40, 10) : new Color(20, 40, 20));

            JButton btn = new JButton(label);
            btn.setBackground(bg);
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Monospaced", Font.BOLD, 12));
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(
                    canUse ? itemColor : new Color(80, 40, 40), 2));
            btn.setEnabled(canUse);
            btn.setBounds(cx + col * (btnW + 10), rowY, btnW, btnH);

            if (canUse) {
                final Fighter target = f;
                btn.addActionListener(ev -> { pendingTarget = target; buildBagOverlay("confirm"); });
            }
            overlay.add(btn);
            col++;
            if (col >= cols) { col = 0; rowY += btnH + 10; }
        }

        JButton backBtn = new JButton("BACK");
        backBtn.setBackground(new Color(80, 80, 80));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("Monospaced", Font.BOLD, 14));
        backBtn.setFocusPainted(false);
        backBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        backBtn.setBounds(winX + winW - 140, winY + winH - 56, 120, 36);
        backBtn.addActionListener(e -> {
            pendingItemUse = "";
            selectedIndex  = -1;
            buildBagOverlay("main");
        });
        overlay.add(backBtn);
    }

    private void buildConfirmScreen(JPanel overlay, int winX, int winY,
                                    int winW, int winH) {
        String itemName  = pendingItemUse.equals("lunas") ? "Lunas" : "Potion";
        Color  itemColor = pendingItemUse.equals("lunas")
                ? new Color(60, 180, 100) : new Color(60, 140, 220);
        String effect    = pendingItemUse.equals("lunas") ? "restore PP" : "restore 30 HP";

        int cx = winX + winW / 2, cy = winY + winH / 2 - 60;

        JLabel msg = new JLabel(
                "<html><center>Use <b>" + itemName + "</b> on <b>"
                        + pendingTarget.name + "</b><br>to " + effect + "?</center></html>",
                SwingConstants.CENTER);
        msg.setForeground(Color.WHITE);
        msg.setFont(new Font("Monospaced", Font.PLAIN, 16));
        msg.setBounds(winX + 60, cy, winW - 120, 60);
        overlay.add(msg);

        int btnY = cy + 80, btnW = 160, btnH = 46;

        JButton yesBtn = new JButton("YES");
        yesBtn.setBackground(new Color(30, 140, 60));
        yesBtn.setForeground(Color.WHITE);
        yesBtn.setFont(new Font("Monospaced", Font.BOLD, 16));
        yesBtn.setFocusPainted(false);
        yesBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        yesBtn.setBounds(cx - btnW - 10, btnY, btnW, btnH);
        yesBtn.addActionListener(e -> applyItemWorld());
        overlay.add(yesBtn);

        JButton noBtn = new JButton("NO");
        noBtn.setBackground(new Color(160, 40, 40));
        noBtn.setForeground(Color.WHITE);
        noBtn.setFont(new Font("Monospaced", Font.BOLD, 16));
        noBtn.setFocusPainted(false);
        noBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        noBtn.setBounds(cx + 10, btnY, btnW, btnH);
        noBtn.addActionListener(e -> buildBagOverlay("creatureSelect"));
        overlay.add(noBtn);
    }

    private void applyItemWorld() {
        if (pendingTarget == null) return;

        if (pendingItemUse.equals("potion")) {
            potionCount--;
            int before = (int) pendingTarget.stats.get(0).value;
            pendingTarget.stats.get(0).value = Math.min(
                    pendingTarget.stats.get(0).base,
                    pendingTarget.stats.get(0).value + 30);
            int healed = (int)(pendingTarget.stats.get(0).value - before);
            showResultAndClose(pendingTarget.name + " restored " + healed + " HP!");

        } else if (pendingItemUse.equals("lunas")) {
            lunasCount--;
            Move target = null;
            for (Move m : pendingTarget.moveset) {
                if (m.pp < m.maxPp) {
                    if (target == null || m.pp < target.pp) target = m;
                }
            }
            String msg;
            if (target == null) {
                msg = pendingTarget.name + "'s moves are all full!";
            } else {
                int restored = Math.min(5, target.maxPp - target.pp);
                target.pp = Math.min(target.maxPp, target.pp + 5);
                msg = pendingTarget.name + "'s " + target.name
                        + " restored " + restored + " PP!";
            }
            showResultAndClose(msg);
        }
    }

    private void useScrollWorld() {
        showResultAndClose("Scrolls can only be used in battle!");
    }

    private void showResultAndClose(String message) {
        if (bagOverlay != null) this.remove(bagOverlay);

        JPanel resultOverlay = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 0, 0, 180));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        resultOverlay.setOpaque(false);
        resultOverlay.setBounds(0, 0, 1280, 720);

        int rw = 500, rh = 100;
        int rx = (1280 - rw) / 2, ry = (720 - rh) / 2;

        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(new Color(22, 14, 6));
        box.setBorder(BorderFactory.createLineBorder(new Color(170, 120, 50), 2));
        box.setBounds(rx, ry, rw, rh);

        JLabel lbl = new JLabel(message, SwingConstants.CENTER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Monospaced", Font.BOLD, 15));
        box.add(lbl, BorderLayout.CENTER);
        resultOverlay.add(box);

        bagOverlay = resultOverlay;
        this.add(bagOverlay);
        this.setComponentZOrder(bagOverlay, 0);
        this.revalidate();
        this.repaint();

        new Timer(1500, e -> {
            ((Timer) e.getSource()).stop();
            closeBag();
        }).start();
    }

    private JButton styledItemBtn(String name, int count, Color accent, boolean selected) {
        JButton btn = new JButton("<html><b>" + name + "</b>&nbsp;&nbsp;x" + count + "</html>");
        btn.setBackground(selected ? new Color(60, 40, 10) : new Color(32, 20, 6));
        btn.setForeground(selected ? accent : Color.WHITE);
        btn.setFont(new Font("Monospaced", Font.PLAIN, 13));
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createLineBorder(
                selected ? accent : new Color(80, 55, 15), selected ? 2 : 1));
        return btn;
    }

    // ══════════════════════════════════════════════════════════════
    // GAME LOOP
    // ══════════════════════════════════════════════════════════════

    private void initSolidTiles() {
        solidTiles.add(838); solidTiles.add(201); solidTiles.add(202);
        solidTiles.add(203); solidTiles.add(230); solidTiles.add(228);
        solidTiles.add(257); solidTiles.add(255); solidTiles.add(703);
    }

    private boolean isSolid(int worldX, int worldY) {
        int tileCol = worldX / TILE_DISPLAY_SIZE;
        int tileRow = worldY / TILE_DISPLAY_SIZE;
        if (tileCol < 0 || tileRow < 0 ||
                tileCol >= mapWidth || tileRow >= mapHeight) return true;
        for (int layer = 2; layer < allLayerData.length; layer++) {
            if (solidTiles.contains(allLayerData[layer][tileRow][tileCol])) return true;
        }
        return false;
    }

    private void checkEncounter() {
        if (inBattle) return;
        if (allLayerData == null || allLayerData.length < 3) return;
        if (System.currentTimeMillis() < encounterCooldownUntil) return;

        int feetX   = playerX + PLAYER_SIZE_W / 2;
        int feetY   = playerY + PLAYER_SIZE_H;
        int tileCol = feetX / TILE_DISPLAY_SIZE;
        int tileRow = feetY / TILE_DISPLAY_SIZE;

        if (tileCol < 0 || tileRow < 0 ||
                tileCol >= mapWidth || tileRow >= mapHeight) return;

        if (allLayerData[2][tileRow][tileCol] == 758) {
            if (Math.random() > 0.05) return;
            inBattle = true;
            int savedX = playerX, savedY = playerY;
            game.battle.Fighter wildFighter = game.battle.Create.randomWildCreature();
            SwingUtilities.invokeLater(() ->
                    gameScene.switchToBattle(playerFighter, wildFighter,
                            capturedTeam, scrollCount,
                            lunasCount, potionCount,
                            savedX, savedY));
        }
    }

    public void start() {
        if (gameThread == null) {
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    private void update() {
        if (keyH.bagJustPressed) {
            if (showBag) closeBag();
            else if (!showWelcome) openBag();
            keyH.bagJustPressed = false;
        }
        if (showWelcome || showBag) return;

        boolean moving = false;
        int newX = playerX, newY = playerY;

        if (keyH.up)    { newY -= PLAYER_SPEED; currentRow = 3; moving = true; }
        if (keyH.down)  { newY += PLAYER_SPEED; currentRow = 0; moving = true; }
        if (keyH.left)  { newX -= PLAYER_SPEED; currentRow = 1; moving = true; }
        if (keyH.right) { newX += PLAYER_SPEED; currentRow = 2; moving = true; }

        if (!isSolid(newX, newY) &&
                !isSolid(newX + PLAYER_SIZE_W - 1, newY) &&
                !isSolid(newX, newY + PLAYER_SIZE_H - 1) &&
                !isSolid(newX + PLAYER_SIZE_W - 1, newY + PLAYER_SIZE_H - 1)) {
            playerX = newX;
            playerY = newY;
        }

        if (moving) {
            frameCounter++;
            if (frameCounter >= frameDelay) {
                frameCounter = 1;
                currentFrame = (currentFrame + 1) % 50;
            }
        } else {
            currentFrame = 0;
            frameCounter = 0;
        }

        int mapPixelWidth  = mapWidth  * TILE_DISPLAY_SIZE;
        int mapPixelHeight = mapHeight * TILE_DISPLAY_SIZE;
        playerX = Math.max(0, Math.min(playerX, mapPixelWidth  - PLAYER_SIZE_W));
        playerY = Math.max(0, Math.min(playerY, mapPixelHeight - PLAYER_SIZE_H));

        int screenWidth  = getWidth()  > 0 ? getWidth()  : 1280;
        int screenHeight = getHeight() > 0 ? getHeight() : 720;
        cameraX = playerX - screenWidth  / 2 + PLAYER_SIZE_W / 2;
        cameraY = playerY - screenHeight / 2 + PLAYER_SIZE_H / 2;
        cameraX = Math.max(0, Math.min(cameraX, mapPixelWidth  - screenWidth));
        cameraY = Math.max(0, Math.min(cameraY, mapPixelHeight - screenHeight));

        checkEncounter();
    }

    @Override
    public void run() {
        while (gameThread != null) {
            update();
            repaint();
            try { Thread.sleep(16); }
            catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        if (allLayerData != null) {
            for (int layer = 0; layer < allLayerData.length; layer++) {
                for (int row = 0; row < mapHeight; row++) {
                    for (int col = 0; col < mapWidth; col++) {
                        int tileId = allLayerData[layer][row][col];
                        if (tileId <= 0) continue;
                        int drawX = col * TILE_DISPLAY_SIZE - cameraX;
                        int drawY = row * TILE_DISPLAY_SIZE - cameraY;
                        if (drawX + TILE_DISPLAY_SIZE < 0 || drawX > getWidth()) continue;
                        if (drawY + TILE_DISPLAY_SIZE < 0 || drawY > getHeight()) continue;
                        BufferedImage img = tileCache.get(tileId);
                        if (img != null)
                            g2.drawImage(img, drawX, drawY,
                                    TILE_DISPLAY_SIZE, TILE_DISPLAY_SIZE, null);
                    }
                }
            }

            int px = playerX - cameraX, py = playerY - cameraY;
            if (playerSheet != null) {
                int fw = 25, fh = 25;
                g2.drawImage(playerSheet, px, py,
                        px + PLAYER_SIZE_W, py + PLAYER_SIZE_H,
                        currentFrame * fw, currentRow * fh,
                        currentFrame * fw + fw, currentRow * fh + fh, null);
            } else {
                g2.setColor(Color.RED);
                g2.fillRect(px, py, PLAYER_SIZE_W, PLAYER_SIZE_H);
            }
        } else {
            g2.setColor(Color.RED);
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.drawString("Map failed to load!", 50, 50);
        }

        // ✅ Always draw HUD on top
        drawHUD(g2);
    }

    // ── Map loading ───────────────────────────────────────────────

    private void loadMap(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) { System.err.println("Map not found: " + path); return; }
            String content = new java.util.Scanner(file).useDelimiter("\\Z").next();
            mapWidth  = Integer.parseInt(content.split("width=\"")[1].split("\"")[0]);
            mapHeight = Integer.parseInt(content.split("height=\"")[1].split("\"")[0]);
            String[] layers = content.split("<data encoding=\"csv\">");
            allLayerData = new int[layers.length - 1][mapHeight][mapWidth];
            for (int l = 1; l < layers.length; l++) {
                String csvData = layers[l].split("</data>")[0].trim();
                String[] values = csvData.split(",");
                for (int i = 0; i < values.length && i < mapWidth * mapHeight; i++) {
                    int r = i / mapWidth, c = i % mapWidth;
                    try { allLayerData[l-1][r][c] = Integer.parseInt(values[i].trim()); }
                    catch (NumberFormatException e) { allLayerData[l-1][r][c] = 0; }
                }
            }
            loadTilesets(content);
        } catch (Exception e) { System.err.println("Error loading map: " + e.getMessage()); }
    }

    private void loadTilesets(String tmxContent) {
        try {
            String[] tilesetBlocks = tmxContent.split("<tileset ");
            for (int i = 1; i < tilesetBlocks.length; i++) {
                String block  = tilesetBlocks[i];
                int firstGid  = Integer.parseInt(block.split("firstgid=\"")[1].split("\"")[0]);
                String tsxSrc = block.split("source=\"")[1].split("\"")[0];
                File tsxFile  = new File("resources/" + tsxSrc);
                if (!tsxFile.exists()) tsxFile = new File(tsxSrc);
                if (!tsxFile.exists()) continue;
                String tsxContent = new java.util.Scanner(tsxFile).useDelimiter("\\Z").next();
                int tw = TILE_SIZE, th = TILE_SIZE;
                if (tsxContent.contains("tilewidth=\"")) {
                    tw = Integer.parseInt(tsxContent.split("tilewidth=\"")[1].split("\"")[0]);
                    th = Integer.parseInt(tsxContent.split("tileheight=\"")[1].split("\"")[0]);
                }
                String imgSrc = tsxContent.split("source=\"")[1].split("\"")[0];
                File imgFile  = new File("resources/" + imgSrc);
                if (!imgFile.exists())
                    imgFile = new File("resources/Texture/" + new File(imgSrc).getName());
                if (!imgFile.exists()) continue;
                BufferedImage tilesetImg = ImageIO.read(imgFile);
                int cols = tilesetImg.getWidth()  / tw;
                int rows = tilesetImg.getHeight() / th;
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        int gid = firstGid + (r * cols + c);
                        int sx = c * tw, sy = r * th;
                        if (sx + tw > tilesetImg.getWidth() ||
                                sy + th > tilesetImg.getHeight()) continue;
                        BufferedImage tile = tilesetImg.getSubimage(sx, sy, tw, th);
                        BufferedImage copy = new BufferedImage(tw, th, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D tg = copy.createGraphics();
                        tg.drawImage(tile, 0, 0, null);
                        tg.dispose();
                        tileCache.put(gid, copy);
                    }
                }
            }
        } catch (Exception e) { System.err.println("Error loading tilesets: " + e.getMessage()); }
    }
}