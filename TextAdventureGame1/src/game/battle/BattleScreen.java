package game.battle;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BattleScreen extends JPanel implements ActionListener {

    private Fighter userMon;
    private Fighter oppMon;
    private Fighter defaultMon;

    private BattleCallback onBattleEnd;
    private BattleCallback onRun;

    private ArrayList<Fighter> capturedTeam;
    private int scrollCount;
    private int lunasCount;
    private int potionCount;

    private static final int MAX_TEAM_SIZE = 6;

    private boolean isBossFight = false;
    private String  bossName    = "";
    private ArrayList<Fighter> bossTeamAll = new ArrayList<>();
    private int bossTeamIndex = 0;

    private JPanel botPanel;
    private JPanel pokePanel;
    private JPanel movePanel;
    private JPanel menuPanel;
    private JPanel textPanel;
    private JPanel statPanel;
    private JPanel healthPanel;
    private JPanel bagPanel;
    private JPanel switchPanel;
    private JPanel creatureSelectPanel;
    private JPanel confirmPanel;

    private JLabel moveUsed;
    private JLabel userPokemon;
    private JLabel oppPokemon;
    private JLabel statUsed;
    private JLabel userHpLabel;
    private JLabel oppHpLabel;
    private JLabel userNameLbl;
    private JLabel oppLevelLabel;

    private JProgressBar userExpBar;
    private JLabel       userExpLabel;

    private JButton fightBtn;
    private JButton bagBtn;
    private JButton creatureBtn;
    private JButton runBtn;

    private JButton move1, move2, move3, move4;

    private JProgressBar userHealth;
    private JProgressBar oppHealth;

    private boolean goingUp  = true;
    private int jumpHeight   = 5;
    private int step         = 2;
    private int offset       = 0;

    private int     bagSelectedIndex = -1;
    private String  pendingItemUse   = "";
    private Fighter pendingTarget    = null;

    private BufferedImage battleBg = null;

    // ✅ Combined center scroll icons panel
    private JPanel scrollIconsPanel = null;

    // ── Normal battle constructor ──────────────────────────────────
    public BattleScreen(Fighter defaultMon,
                        Fighter oppMon,
                        ArrayList<Fighter> capturedTeam,
                        int scrollCount,
                        int lunasCount,
                        int potionCount,
                        BattleCallback onBattleEnd,
                        BattleCallback onRun) {
        this(defaultMon, oppMon, capturedTeam, scrollCount, lunasCount, potionCount,
                onBattleEnd, onRun, false, "", new ArrayList<>());
    }

    // ── Boss battle constructor (no team) ─────────────────────────
    public BattleScreen(Fighter defaultMon,
                        Fighter oppMon,
                        ArrayList<Fighter> capturedTeam,
                        int scrollCount,
                        int lunasCount,
                        int potionCount,
                        BattleCallback onBattleEnd,
                        BattleCallback onRun,
                        boolean isBossFight,
                        String bossName) {
        this(defaultMon, oppMon, capturedTeam, scrollCount, lunasCount, potionCount,
                onBattleEnd, onRun, isBossFight, bossName, new ArrayList<>());
    }

    // ── Full constructor ───────────────────────────────────────────
    public BattleScreen(Fighter defaultMon,
                        Fighter oppMon,
                        ArrayList<Fighter> capturedTeam,
                        int scrollCount,
                        int lunasCount,
                        int potionCount,
                        BattleCallback onBattleEnd,
                        BattleCallback onRun,
                        boolean isBossFight,
                        String bossName,
                        ArrayList<Fighter> bossTeamRest) {
        this.defaultMon   = defaultMon;
        this.userMon      = defaultMon;
        this.oppMon       = oppMon;
        this.capturedTeam = capturedTeam;
        this.scrollCount  = scrollCount;
        this.lunasCount   = lunasCount;
        this.potionCount  = potionCount;
        this.onBattleEnd  = onBattleEnd;
        this.onRun        = onRun;
        this.isBossFight  = isBossFight;
        this.bossName     = bossName;

        if (isBossFight) {
            bossTeamAll.add(oppMon);
            bossTeamAll.addAll(bossTeamRest);
        }

        try {
            battleBg = ImageIO.read(new File("resources/Texture/battle.jpg"));
        } catch (Exception e) {
            System.err.println("Could not load battle.jpg: " + e.getMessage());
        }

        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // ── Background ────────────────────────────────────────────
        JPanel bgPanel = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                if (battleBg != null) {
                    g2.drawImage(battleBg, 0, 0, getWidth(), getHeight(), null);
                } else {
                    GradientPaint gp = new GradientPaint(0,0,new Color(20,40,20),0,getHeight(),new Color(5,15,5));
                    g2.setPaint(gp); g2.fillRect(0,0,getWidth(),getHeight());
                }
                if (isBossFight) drawBossBanner(g2, getWidth(), getHeight());
            }
        };
        bgPanel.setOpaque(false);

        // ── Sprites ───────────────────────────────────────────────
        pokePanel = new JPanel(new GridLayout(1, 2));
        pokePanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
        pokePanel.setOpaque(false);
        userPokemon = loadSprite(userMon.back_sprite);
        oppPokemon  = loadSprite(oppMon.sprite);
        pokePanel.add(userPokemon);
        pokePanel.add(oppPokemon);

        // ── Names ─────────────────────────────────────────────────
        JPanel namePanel = new JPanel(new GridLayout(1, 2));
        namePanel.setOpaque(false);
        namePanel.setBorder(BorderFactory.createEmptyBorder(2, 40, 2, 40));
        userNameLbl   = new JLabel(userMon.name + "  Lv." + userMon.level, SwingConstants.CENTER);
        oppLevelLabel = new JLabel(oppMon.name  + "  Lv." + oppMon.level,  SwingConstants.CENTER);
        styleLabel(userNameLbl);
        if (isBossFight) {
            oppLevelLabel.setForeground(new Color(255, 80, 80));
            oppLevelLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        } else {
            styleLabel(oppLevelLabel);
        }
        namePanel.add(userNameLbl);
        namePanel.add(oppLevelLabel);

        // ── HP labels ─────────────────────────────────────────────
        JPanel hpLabelPanel = new JPanel(new GridLayout(1, 2));
        hpLabelPanel.setOpaque(false);
        hpLabelPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));
        userHpLabel = new JLabel(getHpText(userMon), SwingConstants.CENTER);
        oppHpLabel  = new JLabel(getHpText(oppMon),  SwingConstants.CENTER);
        userHpLabel.setForeground(new Color(0x55cb0b));
        oppHpLabel.setForeground(Color.RED);
        userHpLabel.setFont(new Font("Monospaced", Font.BOLD, 13));
        oppHpLabel.setFont(new Font("Monospaced", Font.BOLD, 13));
        hpLabelPanel.add(userHpLabel);
        hpLabelPanel.add(oppHpLabel);

        // ── HP bars ───────────────────────────────────────────────
        healthPanel = new JPanel(new GridLayout(1, 2));
        healthPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 2, 30));
        healthPanel.setOpaque(false);
        userHealth = buildBar(userMon, new Color(0x55cb0b));
        oppHealth  = buildBar(oppMon, isBossFight ? new Color(255, 60, 60) : Color.RED);
        healthPanel.add(userHealth);
        healthPanel.add(oppHealth);

        // ── EXP bar ───────────────────────────────────────────────
        JPanel expPanel = new JPanel(null);
        expPanel.setOpaque(false);
        expPanel.setPreferredSize(new Dimension(1280, 20));

        JLabel expTitleLbl = new JLabel("EXP");
        expTitleLbl.setForeground(new Color(140, 200, 255));
        expTitleLbl.setFont(new Font("Monospaced", Font.BOLD, 10));
        expTitleLbl.setBounds(32, 3, 30, 14);
        expPanel.add(expTitleLbl);

        userExpBar = new JProgressBar(0, Math.max(1, userMon.expToNext));
        userExpBar.setValue(userMon.exp);
        userExpBar.setStringPainted(false);
        userExpBar.setForeground(new Color(80, 180, 255));
        userExpBar.setBackground(new Color(30, 30, 50));
        userExpBar.setBounds(64, 4, 500, 12);
        expPanel.add(userExpBar);

        userExpLabel = new JLabel(userMon.exp + "/" + userMon.expToNext);
        userExpLabel.setForeground(new Color(140, 200, 255));
        userExpLabel.setFont(new Font("Monospaced", Font.PLAIN, 10));
        userExpLabel.setBounds(570, 3, 100, 14);
        expPanel.add(userExpLabel);

        // ✅ Center scroll icons panel — drawn below HP bars, centered
        JPanel scrollsPanel = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // painted by children only
            }
        };
        scrollsPanel.setOpaque(false);
        scrollsPanel.setPreferredSize(new Dimension(1280, 28));
        scrollIconsPanel = scrollsPanel;
        rebuildScrollIcons(scrollsPanel);

        // ── Assemble stats area ───────────────────────────────────
        JPanel statsArea = new JPanel(new GridLayout(5, 1));
        statsArea.setOpaque(false);
        statsArea.add(hpLabelPanel);
        statsArea.add(healthPanel);
        statsArea.add(expPanel);
        statsArea.add(scrollsPanel);

        JPanel topArea = new JPanel(new BorderLayout());
        topArea.setOpaque(false);
        topArea.add(pokePanel,   BorderLayout.NORTH);
        topArea.add(namePanel,   BorderLayout.CENTER);
        topArea.add(statsArea,   BorderLayout.SOUTH);

        bgPanel.add(topArea, BorderLayout.CENTER);

        // ── Bottom CardLayout ─────────────────────────────────────
        botPanel = new JPanel(new CardLayout());
        botPanel.setBackground(new Color(30, 30, 30));
        botPanel.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 3));
        botPanel.setPreferredSize(new Dimension(1280, 240));

        menuPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        fightBtn    = menuBtn("FIGHT",    new Color(0xba1010));
        bagBtn      = menuBtn("BAG",      new Color(0x329dfc));
        creatureBtn = menuBtn("CREATURE (" + (1 + capturedTeam.size()) + "/" + MAX_TEAM_SIZE + ")", new Color(0x55cb0b));
        runBtn      = menuBtn("RUN",      new Color(0x9b7b60));
        if (isBossFight) runBtn.setEnabled(false);
        fightBtn.addActionListener(this);
        bagBtn.addActionListener(this);
        creatureBtn.addActionListener(this);
        runBtn.addActionListener(this);
        menuPanel.add(fightBtn); menuPanel.add(bagBtn);
        menuPanel.add(creatureBtn); menuPanel.add(runBtn);

        movePanel = new JPanel(new GridLayout(2, 2, 5, 5));
        movePanel.setOpaque(false);
        movePanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        rebuildMovePanel();

        textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        moveUsed = new JLabel("", SwingConstants.CENTER);
        moveUsed.setForeground(Color.WHITE);
        moveUsed.setFont(new Font("Monospaced", Font.BOLD, 18));
        textPanel.add(moveUsed, BorderLayout.CENTER);

        statPanel = new JPanel(new BorderLayout());
        statPanel.setOpaque(false);
        statUsed = new JLabel("", SwingConstants.CENTER);
        statUsed.setForeground(Color.YELLOW);
        statUsed.setFont(new Font("Monospaced", Font.BOLD, 18));
        statPanel.add(statUsed, BorderLayout.CENTER);

        bagPanel = new JPanel();
        bagPanel.setOpaque(false);
        rebuildBagPanel();

        switchPanel = new JPanel();
        switchPanel.setOpaque(false);
        rebuildSwitchPanel();

        creatureSelectPanel = new JPanel();
        creatureSelectPanel.setOpaque(false);

        confirmPanel = new JPanel();
        confirmPanel.setOpaque(false);

        botPanel.add(menuPanel,           "Menu");
        botPanel.add(movePanel,           "Moves");
        botPanel.add(textPanel,           "Used");
        botPanel.add(statPanel,           "Stat");
        botPanel.add(bagPanel,            "Bag");
        botPanel.add(switchPanel,         "Switch");
        botPanel.add(creatureSelectPanel, "CreatureSelect");
        botPanel.add(confirmPanel,        "Confirm");

        bgPanel.add(botPanel, BorderLayout.SOUTH);
        add(bgPanel, BorderLayout.CENTER);

        showCard("Menu");
    }

    // ✅ Boss banner
    private void drawBossBanner(Graphics2D g2, int w, int h) {
        g2.setColor(new Color(80, 0, 0, 60));
        g2.fillRect(0, 0, w, h);

        int bw = 520, bh = 44, bx = (w - bw) / 2, by = 8;
        for (int glow = 4; glow >= 0; glow--) {
            g2.setColor(new Color(200, 0, 0, 15 + glow * 10));
            g2.fillRoundRect(bx-glow*2, by-glow*2, bw+glow*4, bh+glow*4, 12, 12);
        }
        g2.setColor(new Color(30, 0, 0));
        g2.fillRoundRect(bx, by, bw, bh, 10, 10);
        g2.setColor(new Color(180, 40, 40));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(bx, by, bw, bh, 10, 10);

        g2.setColor(new Color(255, 80, 80));
        g2.setFont(new Font("Monospaced", Font.BOLD, 11));
        g2.drawString("★  BOSS BATTLE  ★", bx + 16, by + 16);

        g2.setColor(new Color(255, 200, 200));
        g2.setFont(new Font("Monospaced", Font.BOLD, 18));
        FontMetrics fm = g2.getFontMetrics();
        String name = "ALBULARYO — " + (bossName.isEmpty() ? "The Witch Doctor" : bossName);
        g2.drawString(name, bx + (bw - fm.stringWidth(name)) / 2, by + 36);
    }

    // ✅ Scroll icons — CENTERED below HP bars
    private void rebuildScrollIcons(JPanel panel) {
        panel.removeAll();

        ArrayList<Fighter> playerTeam = new ArrayList<>();
        playerTeam.add(defaultMon);
        playerTeam.addAll(capturedTeam);

        int dotSize = 14, gap = 5;

        // Count alive
        int alivePlayer = 0;
        for (Fighter f : playerTeam) if (!f.isFainted()) alivePlayer++;
        int aliveBoss = 0;
        if (isBossFight) for (Fighter f : bossTeamAll) if (!f.isFainted()) aliveBoss++;

        // ✅ Calculate total width to center everything
        // Player: dots + counter label
        // vs label
        // Boss: dots + counter + name (if boss)
        int playerDotsW  = playerTeam.size() * (dotSize + gap);
        int counterW     = 34;
        int vsW          = 28;
        int bossDotsW    = isBossFight ? bossTeamAll.size() * (dotSize + gap) : 0;
        int bossCounterW = isBossFight ? 34 : 0;
        int bossNameW    = isBossFight ? 140 : 0;

        int totalW = playerDotsW + counterW + vsW + bossDotsW + bossCounterW + bossNameW;
        int panelW = 1280;
        int startX = (panelW - totalW) / 2; // ✅ Center everything

        int curX = startX;

        // ── Player dots ───────────────────────────────────────────
        for (int i = 0; i < playerTeam.size(); i++) {
            Fighter f       = playerTeam.get(i);
            boolean fainted = f.isFainted();
            boolean active  = (f == userMon);
            Color   dc      = fainted ? new Color(80, 40, 40)
                    : active  ? new Color(100, 255, 100)
                      : new Color(60, 180, 60);
            final Color fdc = dc;
            final boolean fac = active;

            JPanel dot = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(fdc);
                    g2.fillOval(1, 1, dotSize - 2, dotSize - 2);
                    if (fac) {
                        g2.setColor(Color.WHITE);
                        g2.setStroke(new BasicStroke(1.5f));
                        g2.drawOval(1, 1, dotSize - 3, dotSize - 3);
                    }
                }
            };
            dot.setOpaque(false);
            dot.setBounds(curX + i * (dotSize + gap), 7, dotSize, dotSize);
            dot.setToolTipText(f.name);
            panel.add(dot);
        }
        curX += playerDotsW;

        // ── Player counter ────────────────────────────────────────
        JLabel playerCount = new JLabel(alivePlayer + "/" + playerTeam.size());
        playerCount.setForeground(new Color(140, 255, 140));
        playerCount.setFont(new Font("Monospaced", Font.BOLD, 11));
        playerCount.setBounds(curX, 7, counterW, dotSize);
        panel.add(playerCount);
        curX += counterW;

        // ── "vs" ──────────────────────────────────────────────────
        JLabel sep = new JLabel("vs", SwingConstants.CENTER);
        sep.setForeground(new Color(200, 200, 200));
        sep.setFont(new Font("Monospaced", Font.BOLD, 11));
        sep.setBounds(curX, 7, vsW, dotSize);
        panel.add(sep);
        curX += vsW;

        // ── Boss dots ─────────────────────────────────────────────
        if (isBossFight && !bossTeamAll.isEmpty()) {
            for (int i = 0; i < bossTeamAll.size(); i++) {
                Fighter f       = bossTeamAll.get(i);
                boolean fainted = f.isFainted();
                boolean current = (f == oppMon);
                Color   dc      = fainted ? new Color(80, 30, 30)
                        : current ? new Color(255, 80, 80)
                          : new Color(180, 60, 60);
                final Color fdc = dc;
                final boolean fcc = current;

                JPanel dot = new JPanel() {
                    @Override protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(fdc);
                        g2.fillOval(1, 1, dotSize - 2, dotSize - 2);
                        if (fcc) {
                            g2.setColor(Color.WHITE);
                            g2.setStroke(new BasicStroke(1.5f));
                            g2.drawOval(1, 1, dotSize - 3, dotSize - 3);
                        }
                    }
                };
                dot.setOpaque(false);
                dot.setBounds(curX + i * (dotSize + gap), 7, dotSize, dotSize);
                dot.setToolTipText(f.name + " Lv." + f.level + (fainted ? " ✗" : current ? " ◀" : ""));
                panel.add(dot);
            }
            curX += bossDotsW;

            // ── Boss counter ──────────────────────────────────────
            JLabel bossCount = new JLabel(aliveBoss + "/" + bossTeamAll.size());
            bossCount.setForeground(new Color(255, 140, 140));
            bossCount.setFont(new Font("Monospaced", Font.BOLD, 11));
            bossCount.setBounds(curX, 7, bossCounterW, dotSize);
            panel.add(bossCount);
            curX += bossCounterW;

            // ── Active boss name ──────────────────────────────────
            JLabel bossActiveName = new JLabel(" " + oppMon.name + " Lv." + oppMon.level);
            bossActiveName.setForeground(new Color(255, 180, 180));
            bossActiveName.setFont(new Font("Monospaced", Font.ITALIC, 10));
            bossActiveName.setBounds(curX, 7, bossNameW, dotSize);
            panel.add(bossActiveName);
        } else if (!isBossFight) {
            // ── Wild creature name for normal battle ──────────────
            JLabel wildName = new JLabel(oppMon.name + " Lv." + oppMon.level);
            wildName.setForeground(new Color(200, 200, 200));
            wildName.setFont(new Font("Monospaced", Font.ITALIC, 10));
            wildName.setBounds(curX, 7, 160, dotSize);
            panel.add(wildName);
        }

        panel.revalidate();
        panel.repaint();
    }

    private void updateExpUI() {
        if (userExpBar != null) {
            userExpBar.setMaximum(Math.max(1, userMon.expToNext));
            userExpBar.setValue(userMon.exp);
        }
        if (userExpLabel != null)
            userExpLabel.setText(userMon.exp + "/" + userMon.expToNext);
        if (userNameLbl != null)
            userNameLbl.setText(userMon.name + "  Lv." + userMon.level);
        if (scrollIconsPanel != null)
            rebuildScrollIcons(scrollIconsPanel);
    }

    private JLabel loadSprite(String path) {
        String fileName = new File(path).getName();
        try {
            File imgFile = new File("resources/Texture/" + fileName);
            if (imgFile.exists()) {
                BufferedImage img = ImageIO.read(imgFile);
                Image scaled = img.getScaledInstance(160, 160, Image.SCALE_SMOOTH);
                JLabel lbl = new JLabel(new ImageIcon(scaled));
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                return lbl;
            }
        } catch (Exception ignored) {}
        try {
            java.net.URL url = getClass().getResource(path);
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                Image scaled   = icon.getImage().getScaledInstance(160, 160, Image.SCALE_SMOOTH);
                JLabel lbl = new JLabel(new ImageIcon(scaled));
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                return lbl;
            }
        } catch (Exception ignored) {}
        JLabel lbl = new JLabel("?", SwingConstants.CENTER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.BOLD, 40));
        return lbl;
    }

    // ══════════════════════════════════════════════════════════════
    // BAG
    // ══════════════════════════════════════════════════════════════

    private void rebuildBagPanel() {
        bagPanel.removeAll();
        bagPanel.setLayout(new BorderLayout(8, 0));
        bagPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JPanel itemList = new JPanel();
        itemList.setLayout(new BoxLayout(itemList, BoxLayout.Y_AXIS));
        itemList.setOpaque(false);
        itemList.setPreferredSize(new Dimension(360, 220));

        JLabel title = new JLabel("  ITEMS");
        title.setForeground(new Color(255, 215, 90));
        title.setFont(new Font("Monospaced", Font.BOLD, 13));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        itemList.add(title);
        itemList.add(Box.createVerticalStrut(4));

        JButton scrollBtn = bagItemBtn("Scroll", "x" + scrollCount, new Color(140,70,200), bagSelectedIndex == 0);
        scrollBtn.addActionListener(e -> { bagSelectedIndex = (bagSelectedIndex==0)?-1:0; rebuildBagPanel(); showCard("Bag"); });
        itemList.add(scrollBtn); itemList.add(Box.createVerticalStrut(4));

        JButton lunasBtn = bagItemBtn("Lunas", "x" + lunasCount, new Color(60,180,100), bagSelectedIndex == 1);
        lunasBtn.addActionListener(e -> { bagSelectedIndex = (bagSelectedIndex==1)?-1:1; rebuildBagPanel(); showCard("Bag"); });
        itemList.add(lunasBtn); itemList.add(Box.createVerticalStrut(4));

        JButton potionBtn = bagItemBtn("Potion", "x" + potionCount, new Color(60,140,220), bagSelectedIndex == 2);
        potionBtn.addActionListener(e -> { bagSelectedIndex = (bagSelectedIndex==2)?-1:2; rebuildBagPanel(); showCard("Bag"); });
        itemList.add(potionBtn); itemList.add(Box.createVerticalStrut(8));

        JButton backBtn = menuBtn("BACK", new Color(0x555555));
        backBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        backBtn.addActionListener(e -> { bagSelectedIndex = -1; showCard("Menu"); });
        itemList.add(backBtn);

        JPanel detailPanel = buildBagDetailPanel();
        bagPanel.add(itemList, BorderLayout.WEST);
        bagPanel.add(detailPanel, BorderLayout.CENTER);
        bagPanel.revalidate(); bagPanel.repaint();
    }

    private JPanel buildBagDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(15, 10, 4));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80,55,15),1),
                BorderFactory.createEmptyBorder(8,12,8,10)));

        if (bagSelectedIndex == -1) {
            JLabel hint = new JLabel("<html><center><font color='gray'>Click an item<br>to see details</font></center></html>", SwingConstants.CENTER);
            hint.setFont(new Font("Monospaced", Font.PLAIN, 12));
            panel.add(hint, BorderLayout.CENTER);
            return panel;
        }

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        String[] names  = {"SCROLL","LUNAS","POTION"};
        Color[]  colors = {new Color(140,70,200),new Color(60,180,100),new Color(60,140,220)};
        int[]    counts = {scrollCount,lunasCount,potionCount};
        String[][] descs = {
                {"Qty: x"+scrollCount, "Throw at wild creature to catch.", "Catch rate up when HP is low."},
                {"Qty: x"+lunasCount,  "Restores 5 PP to one move.", "Use when moves run out of PP."},
                {"Qty: x"+potionCount, "Restores 30 HP to a creature.", "Cannot revive fainted creatures."}
        };

        JLabel titleLbl = new JLabel(names[bagSelectedIndex]);
        titleLbl.setForeground(colors[bagSelectedIndex]);
        titleLbl.setFont(new Font("Monospaced", Font.BOLD, 14));
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.add(titleLbl); top.add(Box.createVerticalStrut(4));

        for (String line : descs[bagSelectedIndex]) {
            JLabel lbl = new JLabel(line);
            lbl.setForeground(line.startsWith("Qty") ? Color.WHITE : new Color(170,170,170));
            lbl.setFont(new Font("Monospaced", Font.PLAIN, 11));
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            top.add(lbl); top.add(Box.createVerticalStrut(2));
        }
        panel.add(top, BorderLayout.NORTH);

        boolean hasItem = counts[bagSelectedIndex] > 0;
        JButton useBtn = new JButton(hasItem ? "USE" : "NONE LEFT");
        useBtn.setBackground(hasItem ? colors[bagSelectedIndex] : new Color(80,80,80));
        useBtn.setForeground(Color.WHITE);
        useBtn.setFont(new Font("Monospaced", Font.BOLD, 13));
        useBtn.setFocusPainted(false);
        useBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        useBtn.setEnabled(hasItem);

        final int sel = bagSelectedIndex;
        useBtn.addActionListener(e -> {
            if (sel==0) { bagSelectedIndex=-1; useScroll(); }
            else if (sel==1) { bagSelectedIndex=-1; pendingItemUse="lunas"; rebuildCreatureSelectPanel(); showCard("CreatureSelect"); }
            else if (sel==2) { bagSelectedIndex=-1; pendingItemUse="potion"; rebuildCreatureSelectPanel(); showCard("CreatureSelect"); }
        });
        panel.add(useBtn, BorderLayout.SOUTH);
        return panel;
    }

    private void rebuildCreatureSelectPanel() {
        creatureSelectPanel.removeAll();
        creatureSelectPanel.setLayout(new BorderLayout(0, 6));
        creatureSelectPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        String itemName  = pendingItemUse.equals("lunas") ? "Lunas" : "Potion";
        Color  itemColor = pendingItemUse.equals("lunas") ? new Color(60,180,100) : new Color(60,140,220);

        JLabel titleLbl = new JLabel("Use " + itemName + " on which creature?", SwingConstants.LEFT);
        titleLbl.setForeground(itemColor);
        titleLbl.setFont(new Font("Monospaced", Font.BOLD, 14));
        creatureSelectPanel.add(titleLbl, BorderLayout.NORTH);

        ArrayList<Fighter> fullTeam = new ArrayList<>();
        fullTeam.add(defaultMon); fullTeam.addAll(capturedTeam);

        JPanel grid = new JPanel(new GridLayout(2, 3, 6, 6));
        grid.setOpaque(false);

        for (Fighter f : fullTeam) {
            int hp    = (int) Math.max(0, f.stats.get(0).value);
            int maxHp = (int) f.stats.get(0).base;
            boolean fainted = f.isFainted();
            boolean ppFull  = pendingItemUse.equals("lunas") && f.moveset.stream().allMatch(m -> m.pp >= m.maxPp || m.isLocked());
            boolean canUse  = !fainted && (pendingItemUse.equals("potion") ? hp < maxHp : !ppFull);

            String label = "<html><center><b>"+f.name+"</b>"+(f==userMon?" ★":"")+"<br><font size='3'>HP: "+hp+"/"+maxHp+"</font></center></html>";
            Color bg = !canUse ? new Color(60,40,40) : (f==userMon ? new Color(50,40,10) : new Color(20,40,20));

            JButton btn = menuBtn(label, bg);
            btn.setEnabled(canUse);
            if (canUse) {
                final Fighter target = f;
                btn.addActionListener(e -> { pendingTarget = target; rebuildConfirmPanel(); showCard("Confirm"); });
            }
            grid.add(btn);
        }
        for (int i = fullTeam.size(); i < MAX_TEAM_SIZE; i++) {
            JPanel empty = new JPanel(); empty.setOpaque(false); grid.add(empty);
        }
        creatureSelectPanel.add(grid, BorderLayout.CENTER);

        JButton backBtn = menuBtn("BACK", new Color(0x555555));
        backBtn.addActionListener(e -> { pendingItemUse=""; bagSelectedIndex=-1; rebuildBagPanel(); showCard("Bag"); });
        JPanel backRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        backRow.setOpaque(false); backRow.add(backBtn);
        creatureSelectPanel.add(backRow, BorderLayout.SOUTH);
        creatureSelectPanel.revalidate(); creatureSelectPanel.repaint();
    }

    private void rebuildConfirmPanel() {
        confirmPanel.removeAll();
        confirmPanel.setLayout(new BorderLayout(0, 10));
        confirmPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        String itemName  = pendingItemUse.equals("lunas") ? "Lunas" : "Potion";
        Color  itemColor = pendingItemUse.equals("lunas") ? new Color(60,180,100) : new Color(60,140,220);
        String effect    = pendingItemUse.equals("lunas") ? "restore PP" : "restore 30 HP";

        JLabel msg = new JLabel("<html><center>Use <b><font color='#"+colorToHex(itemColor)+"'>"+itemName+"</font></b> on <b>"+pendingTarget.name+"</b><br>to "+effect+"?</center></html>", SwingConstants.CENTER);
        msg.setForeground(Color.WHITE);
        msg.setFont(new Font("Monospaced", Font.PLAIN, 15));
        confirmPanel.add(msg, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 16, 0));
        btnRow.setOpaque(false);
        JButton yesBtn = menuBtn("YES", new Color(30,140,60));
        JButton noBtn  = menuBtn("NO",  new Color(160,40,40));
        yesBtn.addActionListener(e -> applyItemToCreature());
        noBtn.addActionListener(e -> { rebuildCreatureSelectPanel(); showCard("CreatureSelect"); });
        btnRow.add(yesBtn); btnRow.add(noBtn);
        confirmPanel.add(btnRow, BorderLayout.SOUTH);
        confirmPanel.revalidate(); confirmPanel.repaint();
    }

    private void applyItemToCreature() {
        if (pendingTarget == null) return;
        if (pendingItemUse.equals("potion")) {
            potionCount--;
            int before = (int) pendingTarget.stats.get(0).value;
            pendingTarget.stats.get(0).value = Math.min(pendingTarget.stats.get(0).base, pendingTarget.stats.get(0).value + 30);
            int healed = (int) pendingTarget.stats.get(0).value - before;
            if (pendingTarget == userMon) { userHealth.setValue((int)userMon.stats.get(0).value); userHpLabel.setText(getHpText(userMon)); }
            String rm = pendingTarget.name + " restored " + healed + " HP!";
            pendingTarget = null; pendingItemUse = "";
            showMessage(rm, () -> { int om = oppMon.chooseMove(userMon); handleMove(om,om,oppMon,userMon,1); });
        } else if (pendingItemUse.equals("lunas")) {
            lunasCount--;
            Move target = null;
            for (Move m : pendingTarget.moveset) { if (!m.isLocked() && m.pp < m.maxPp) { if (target==null||m.pp<target.pp) target=m; } }
            String rm;
            if (target==null) { rm = pendingTarget.name+"'s moves are all full!"; }
            else { int r=Math.min(5,target.maxPp-target.pp); target.pp=Math.min(target.maxPp,target.pp+5); rm=pendingTarget.name+"'s "+target.name+" restored "+r+" PP!"; if (pendingTarget==userMon) rebuildMovePanel(); }
            pendingTarget = null; pendingItemUse = "";
            showMessage(rm, () -> { int om = oppMon.chooseMove(userMon); handleMove(om,om,oppMon,userMon,1); });
        }
    }

    private void useScroll() {
        if (scrollCount <= 0) { showMessage("No scrolls left!", () -> showCard("Menu")); return; }
        if (capturedTeam.size() >= MAX_TEAM_SIZE - 1) { showMessage("Team is full!", () -> showCard("Menu")); return; }
        scrollCount--;
        rebuildBagPanel();

        boolean isAdmin = defaultMon.stats.get(0).base >= 9999;
        double catchChance = isAdmin ? 1.0
                : Math.max(0.1, Math.min(0.9, 0.8 - (oppMon.stats.get(0).value / oppMon.stats.get(0).base * 0.6)));

        if (Math.random() < catchChance) {
            capturedTeam.add(oppMon);
            creatureBtn.setText("CREATURE (" + (1+capturedTeam.size()) + "/" + MAX_TEAM_SIZE + ")");
            rebuildScrollIcons(scrollIconsPanel);
            showMessage(oppMon.name + " was captured!", () ->
                    showMessage("Team: " + (1+capturedTeam.size()) + "/" + MAX_TEAM_SIZE, () -> {
                        if (onBattleEnd != null) onBattleEnd.onComplete(userMon, capturedTeam, scrollCount, lunasCount, potionCount, false);
                    })
            );
        } else {
            showMessage(oppMon.name + " broke free!", () -> { int om = oppMon.chooseMove(userMon); handleMove(om,om,oppMon,userMon,1); });
        }
    }

    private void rebuildSwitchPanel() {
        switchPanel.removeAll();
        switchPanel.setLayout(new BorderLayout());
        switchPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        JLabel titleLbl = new JLabel("Your Team  |  Active: " + userMon.name, SwingConstants.LEFT);
        titleLbl.setForeground(Color.YELLOW);
        titleLbl.setFont(new Font("Monospaced", Font.BOLD, 13));
        topRow.add(titleLbl, BorderLayout.WEST);
        JButton backBtn = menuBtn("BACK", new Color(0x555555));
        backBtn.addActionListener(e -> showCard("Menu"));
        topRow.add(backBtn, BorderLayout.EAST);
        switchPanel.add(topRow, BorderLayout.NORTH);

        ArrayList<Fighter> fullTeam = new ArrayList<>();
        fullTeam.add(defaultMon); fullTeam.addAll(capturedTeam);

        JPanel listPanel = new JPanel();
        listPanel.setOpaque(false);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        for (Fighter f : fullTeam) {
            boolean isActive  = (f == userMon);
            boolean isFainted = f.isFainted();
            int hp    = (int) Math.max(0, f.stats.get(0).value);
            int maxHp = (int) f.stats.get(0).base;
            float ratio    = maxHp > 0 ? (float)hp/maxHp : 0;
            Color barColor = ratio>0.5f?new Color(60,200,60):ratio>0.25f?new Color(220,180,0):new Color(200,50,50);
            Color bg       = isActive?new Color(0x888888):isFainted?new Color(0x550000):new Color(0x2a6e2a);

            JPanel row = new JPanel(null) {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(bg); g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                    if (isActive) { g2.setColor(new Color(255,215,90)); g2.setFont(new Font("Monospaced",Font.BOLD,14)); g2.drawString("★",8,22); }
                    g2.setColor(isActive?new Color(255,215,90):isFainted?new Color(200,100,100):Color.WHITE);
                    g2.setFont(new Font("Monospaced",Font.BOLD,13));
                    g2.drawString(f.name+" Lv."+f.level+(isFainted?" ✗":""), isActive?28:10, 22);
                    g2.setColor(new Color(170,170,170)); g2.setFont(new Font("Monospaced",Font.PLAIN,10));
                    g2.drawString("HP: "+hp+"/"+maxHp, 10, 36);
                    int bx=10,by=40,bw=getWidth()-80,bh=6;
                    g2.setColor(new Color(50,50,50)); g2.fillRoundRect(bx,by,bw,bh,3,3);
                    g2.setColor(barColor); g2.fillRoundRect(bx,by,(int)(bw*ratio),bh,3,3);
                }
            };
            row.setOpaque(false);
            row.setPreferredSize(new Dimension(100, 54));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));

            if (!isActive && !isFainted) {
                final Fighter target = f;
                row.setCursor(new Cursor(Cursor.HAND_CURSOR));
                row.addMouseListener(new MouseAdapter() {
                    @Override public void mouseClicked(MouseEvent e) { switchTo(target); }
                });
            }
            listPanel.add(row);
            listPanel.add(Box.createVerticalStrut(4));
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setOpaque(false); scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        switchPanel.add(scrollPane, BorderLayout.CENTER);
        switchPanel.revalidate(); switchPanel.repaint();
    }

    private void switchTo(Fighter newFighter) {
        if (newFighter.isFainted()) { showMessage(newFighter.name + " has fainted!", () -> showCard("Switch")); return; }
        userMon = newFighter;
        userNameLbl.setText(userMon.name + "  Lv." + userMon.level);
        pokePanel.removeAll();
        userPokemon = loadSprite(userMon.back_sprite);
        pokePanel.add(userPokemon); pokePanel.add(oppPokemon);
        pokePanel.revalidate(); pokePanel.repaint();
        userHealth.setMaximum((int)userMon.stats.get(0).base);
        userHealth.setValue((int)userMon.stats.get(0).value);
        userHpLabel.setText(getHpText(userMon));
        updateExpUI();
        rebuildMovePanel();
        creatureBtn.setText("CREATURE (" + (1+capturedTeam.size()) + "/" + MAX_TEAM_SIZE + ")");
        rebuildSwitchPanel();
        showMessage("Go! " + userMon.name + "!", () -> showCard("Menu"));
    }

    private JButton bagItemBtn(String name, String countStr, Color accent, boolean selected) {
        JButton btn = new JButton("<html><b>"+name+"</b>&nbsp;&nbsp;&nbsp;<font color='#"+colorToHex(accent)+"'>"+countStr+"</font></html>");
        btn.setBackground(selected?new Color(60,40,10):new Color(30,20,8));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Monospaced", Font.PLAIN, 13));
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createLineBorder(selected?accent:new Color(80,55,15),selected?2:1));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    private String colorToHex(Color c) {
        return String.format("%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    private String getHpText(Fighter f) {
        return "HP: " + (int)f.stats.get(0).value + " / " + (int)f.stats.get(0).base;
    }

    private void styleLabel(JLabel lbl) {
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Monospaced", Font.BOLD, 14));
    }

    private JProgressBar buildBar(Fighter f, Color color) {
        JProgressBar bar = new JProgressBar(0, (int)f.stats.get(0).base);
        bar.setValue((int)f.stats.get(0).value);
        bar.setStringPainted(false);
        bar.setForeground(color);
        bar.setBackground(new Color(60, 60, 60));
        return bar;
    }

    private JButton menuBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Monospaced", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        return btn;
    }

    private void showCard(String name) {
        ((CardLayout)botPanel.getLayout()).show(botPanel, name);
    }

    private void showMessage(String msg, Runnable after) {
        statUsed.setText(msg);
        showCard("Stat");
        new Timer(1300, e -> { ((Timer)e.getSource()).stop(); if (after!=null) after.run(); }).start();
    }

    private boolean allPlayerFainted() {
        if (!defaultMon.isFainted()) return false;
        for (Fighter f : capturedTeam) { if (!f.isFainted()) return false; }
        return true;
    }

    private void healAllFighters() {
        healFighter(defaultMon);
        for (Fighter f : capturedTeam) healFighter(f);
    }

    private void healFighter(Fighter f) {
        f.stats.get(0).value = f.stats.get(0).base;
        f.fainted = false;
        for (Move m : f.moveset) { if (!m.isLocked()) m.pp = m.maxPp; }
    }

    private void rebuildMovePanel() {
        movePanel.removeAll();
        while (userMon.moveset.size() < 4) {
            Type normal = new Type("Normal", 0xAAAAAA);
            Move pad = new Move("---", normal, 0, new ArrayList<>(), 1);
            pad.lockedUntilLevel = 999;
            userMon.moveset.add(pad);
        }
        move1 = moveBtnWithPP(userMon.moveset.get(0));
        move2 = moveBtnWithPP(userMon.moveset.get(1));
        move3 = moveBtnWithPP(userMon.moveset.get(2));
        move4 = moveBtnWithPP(userMon.moveset.get(3));
        movePanel.add(move1); movePanel.add(move2);
        movePanel.add(move3); movePanel.add(move4);
        movePanel.revalidate(); movePanel.repaint();
    }

    private JButton moveBtnWithPP(Move move) {
        if (move.isLocked()) {
            String lockText = move.lockedUntilLevel==999?"---":"Lv."+move.lockedUntilLevel+" to unlock";
            String label = "<html><center>"+move.name+"<br><font size='3'>"+lockText+"</font></center></html>";
            JButton btn = new JButton(label);
            btn.setBackground(new Color(50,50,50)); btn.setForeground(new Color(160,160,160));
            btn.setFont(new Font("Monospaced",Font.BOLD,13)); btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(new Color(90,90,90),2));
            return btn;
        }
        Color ppColor = move.pp<=2?new Color(0xaa0000):move.type.color;
        String label = "<html><center>"+move.name+"<br><font size='3'>PP "+move.pp+"/"+move.maxPp+"</font></center></html>";
        JButton btn = new JButton(label);
        btn.setBackground(ppColor); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Monospaced",Font.BOLD,13)); btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE,2));
        btn.addActionListener(this);
        if (move.pp<=0) { btn.setEnabled(false); btn.setText("<html><center>"+move.name+"<br><font size='3'>No PP!</font></center></html>"); }
        return btn;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src==fightBtn) { showCard("Moves"); }
        else if (src==bagBtn) { bagSelectedIndex=-1; rebuildBagPanel(); showCard("Bag"); }
        else if (src==creatureBtn) { rebuildSwitchPanel(); showCard("Switch"); }
        else if (src==runBtn) {
            if (isBossFight) { showMessage("You can't run from a boss battle!", ()->showCard("Menu")); return; }
            showMessage("You ran away safely!", ()->{if(onRun!=null)onRun.onComplete(userMon,capturedTeam,scrollCount,lunasCount,potionCount,false);});
        } else {
            int mi=-1;
            if (src==move1) mi=0; else if (src==move2) mi=1;
            else if (src==move3) mi=2; else if (src==move4) mi=3;
            if (mi>=0) {
                Move chosen=userMon.moveset.get(mi);
                if (chosen.isLocked()) { showMessage("Move is locked!", ()->showCard("Moves")); return; }
                if (chosen.pp<=0) { showMessage("No PP left for "+chosen.name+"!", ()->showCard("Moves")); return; }
                if (userMon.stats.get(3).value>=oppMon.stats.get(3).value)
                    handleMove(mi,oppMon.chooseMove(userMon),userMon,oppMon,0);
                else
                    handleMove(oppMon.chooseMove(userMon),mi,oppMon,userMon,0);
            }
        }
    }

    // ✅ Boss creature fainted — switch to next
    private void onBossCreatureFainted(Runnable allDefeated) {
        oppMon.fainted = true;
        rebuildScrollIcons(scrollIconsPanel);
        bossTeamIndex++;

        if (bossTeamIndex < bossTeamAll.size()) {
            Fighter nextBoss = bossTeamAll.get(bossTeamIndex);
            showMessage(oppMon.name + " fainted!", () ->
                    showMessage("Albularyo sends out " + nextBoss.name + "!", () -> {
                        oppMon = nextBoss;
                        oppPokemon.setIcon(loadSprite(oppMon.sprite).getIcon());
                        oppLevelLabel.setText(oppMon.name + "  Lv." + oppMon.level);
                        oppLevelLabel.setForeground(new Color(255, 80, 80));
                        oppHealth.setMaximum((int)oppMon.stats.get(0).base);
                        oppHealth.setValue((int)oppMon.stats.get(0).value);
                        oppHpLabel.setText(getHpText(oppMon));
                        rebuildScrollIcons(scrollIconsPanel);
                        showCard("Menu");
                    })
            );
        } else {
            allDefeated.run();
        }
    }

    // ✅ Boss victory messages
    private void showBossVictorySequence() {
        showMessage("You defeated all of Albularyo's creatures!", () ->
                showMessage("You have defeated the first Albularyo!", () ->
                        showMessage("Now go explore and find the other Albularyo!", () ->
                                showMessage("Collect all 4 Anting-Anting to save your Grandpa!", () ->
                                        showMessage("REWARD: +900 Coins  +200 EXP to all creatures!", () -> {
                                            if (onBattleEnd != null)
                                                onBattleEnd.onComplete(userMon, capturedTeam, scrollCount, lunasCount, potionCount, false);
                                        })
                                )
                        )
                )
        );
    }

    private void handleMove(int userMove, int oppMove, Fighter user, Fighter opp, int turn) {
        Move move = user.moveset.get(userMove);
        if (user==userMon && !move.isLocked()) { move.pp=Math.max(0,move.pp-1); rebuildMovePanel(); }
        String ppInfo = (user==userMon&&!move.isLocked())?" (PP: "+move.pp+"/"+move.maxPp+")":"";
        moveUsed.setText(user.name+" used "+move.name+"!"+ppInfo);
        showCard("Used");
        user.useMove(opp, userMove);

        new Timer(1000, new ActionListener() {
            @Override public void actionPerformed(ActionEvent evt) {
                ((Timer)evt.getSource()).stop();
                oppHealth.setValue((int)Math.max(0,Math.ceil(oppMon.stats.get(0).value)));
                userHealth.setValue((int)Math.max(0,Math.ceil(userMon.stats.get(0).value)));
                userHpLabel.setText(getHpText(userMon));
                oppHpLabel.setText(getHpText(oppMon));
                double hpRatio = userMon.stats.get(0).value/userMon.stats.get(0).base;
                if (hpRatio<=0.25) userHpLabel.setForeground(Color.RED);
                else if (hpRatio<=0.5) userHpLabel.setForeground(Color.YELLOW);
                else userHpLabel.setForeground(new Color(0x55cb0b));
                animatePokemon(user);

                // Opponent fainted
                if (oppMon.stats.get(0).value <= 0) {
                    if (isBossFight) {
                        onBossCreatureFainted(this::showBossVictory);
                    } else {
                        showMessage(oppMon.name+" fainted!", () -> {
                            int expEarned = calcExp();
                            List<String> newMoves = userMon.gainExp(expEarned);
                            updateExpUI();
                            String winMsg = "You won!  +"+expEarned+" EXP!";
                            if (!newMoves.isEmpty()) winMsg += "  Lv."+userMon.level+"!";
                            final String fm = winMsg;
                            final List<String> fmv = newMoves;
                            showMessage(fm, () -> {
                                rebuildMovePanel();
                                if (!fmv.isEmpty()) {
                                    showMessage(userMon.name+" learned "+fmv.get(0)+"!", () -> {
                                        if (onBattleEnd!=null) onBattleEnd.onComplete(userMon,capturedTeam,scrollCount,lunasCount,potionCount,false);
                                    });
                                } else {
                                    if (onBattleEnd!=null) onBattleEnd.onComplete(userMon,capturedTeam,scrollCount,lunasCount,potionCount,false);
                                }
                            });
                        });
                    }
                    return;
                }

                // Player fainted
                if (userMon.stats.get(0).value <= 0) {
                    showMessage(userMon.name+" fainted!", () -> {
                        if (allPlayerFainted()) {
                            showMessage("All your creatures fainted!", () ->
                                    showMessage("You blacked out...", () -> {
                                        healAllFighters();
                                        if (onBattleEnd!=null) onBattleEnd.onComplete(defaultMon,capturedTeam,scrollCount,lunasCount,potionCount,true);
                                    })
                            );
                        } else {
                            rebuildSwitchPanel(); showCard("Switch");
                        }
                    });
                    return;
                }

                String msg = !userMon.message.isEmpty()?userMon.message:!oppMon.message.isEmpty()?oppMon.message:"";
                userMon.message=""; oppMon.message="";
                if (!msg.isEmpty()) {
                    showMessage(msg, () -> { if (turn==0) handleMove(oppMove,userMove,opp,user,1); else showCard("Menu"); });
                } else {
                    if (turn==0) handleMove(oppMove,userMove,opp,user,1); else showCard("Menu");
                }
            }

            private void showBossVictory() {
                showBossVictorySequence();
            }
        }).start();
    }

    private int calcExp() {
        int baseExp = 15;
        double typeBonus = 1.0;
        if (userMon.types!=null && oppMon.types!=null) {
            for (Type ut:userMon.types) for (Type dt:oppMon.types) {
                double mult = userMon.getTypeMultiplier(ut.name, dt.name);
                if (mult>1.0) typeBonus=2.0;
                else if (mult<1.0 && typeBonus<2.0) typeBonus=Math.random()<0.20?1.267:1.0;
            }
        }
        return (int)(baseExp*typeBonus);
    }

    private void animatePokemon(Fighter attacker) {
        JLabel sprite = (attacker==userMon)?userPokemon:oppPokemon;
        int originalY = sprite.getY();
        goingUp=true; offset=0;
        Timer t = new Timer(15, null);
        t.addActionListener(e -> {
            if (goingUp) { offset-=step; if (offset<=-jumpHeight) goingUp=false; }
            else { offset+=step; if (offset>=0) { offset=0; ((Timer)e.getSource()).stop(); } }
            sprite.setLocation(sprite.getX(), originalY+offset);
        });
        t.start();
    }
}