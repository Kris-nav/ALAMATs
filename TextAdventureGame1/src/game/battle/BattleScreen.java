package game.battle;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

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

    private JButton fightBtn;
    private JButton bagBtn;
    private JButton creatureBtn;
    private JButton runBtn;

    private JButton move1, move2, move3, move4;

    private JProgressBar userHealth;
    private JProgressBar oppHealth;

    private boolean goingUp = true;
    private int jumpHeight = 5;
    private int step = 2;
    private int offset = 0;

    private int bagSelectedIndex = -1;
    private String pendingItemUse = "";
    private Fighter pendingTarget = null;

    // ✅ Cache battle background
    private BufferedImage battleBg = null;

    public BattleScreen(Fighter defaultMon,
                        Fighter oppMon,
                        ArrayList<Fighter> capturedTeam,
                        int scrollCount,
                        int lunasCount,
                        int potionCount,
                        BattleCallback onBattleEnd,
                        BattleCallback onRun) {
        this.defaultMon   = defaultMon;
        this.userMon      = defaultMon;
        this.oppMon       = oppMon;
        this.capturedTeam = capturedTeam;
        this.scrollCount  = scrollCount;
        this.lunasCount   = lunasCount;
        this.potionCount  = potionCount;
        this.onBattleEnd  = onBattleEnd;
        this.onRun        = onRun;

        // ✅ Load battle background once
        try {
            battleBg = ImageIO.read(new File("resources/Texture/battle.jpg"));
        } catch (Exception e) {
            System.err.println("Could not load battle.jpg: " + e.getMessage());
        }

        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // ✅ Background panel with battle.jpg
        JPanel bgPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                if (battleBg != null) {
                    g2.drawImage(battleBg, 0, 0, getWidth(), getHeight(), null);
                } else {
                    GradientPaint gp = new GradientPaint(
                            0, 0, new Color(20, 40, 20),
                            0, getHeight(), new Color(5, 15, 5));
                    g2.setPaint(gp);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
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
        userNameLbl = new JLabel(userMon.name, SwingConstants.CENTER);
        JLabel oppNameLbl = new JLabel(oppMon.name, SwingConstants.CENTER);
        styleLabel(userNameLbl);
        styleLabel(oppNameLbl);
        namePanel.add(userNameLbl);
        namePanel.add(oppNameLbl);

        // ── HP Labels ─────────────────────────────────────────────
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

        // ── Health bars ───────────────────────────────────────────
        healthPanel = new JPanel(new GridLayout(1, 2));
        healthPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 5, 30));
        healthPanel.setOpaque(false);
        userHealth = buildBar(userMon, new Color(0x55cb0b));
        oppHealth  = buildBar(oppMon,  Color.RED);
        healthPanel.add(userHealth);
        healthPanel.add(oppHealth);

        JPanel topArea = new JPanel(new BorderLayout());
        topArea.setOpaque(false);
        topArea.add(pokePanel, BorderLayout.NORTH);
        topArea.add(namePanel, BorderLayout.CENTER);

        JPanel hpAndBars = new JPanel(new GridLayout(2, 1));
        hpAndBars.setOpaque(false);
        hpAndBars.add(hpLabelPanel);
        hpAndBars.add(healthPanel);
        topArea.add(hpAndBars, BorderLayout.SOUTH);
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
        creatureBtn = menuBtn("CREATURE (" + capturedTeam.size() + "/" + (MAX_TEAM_SIZE - 1) + ")", new Color(0x55cb0b));
        runBtn      = menuBtn("RUN",      new Color(0x9b7b60));
        fightBtn.addActionListener(this);
        bagBtn.addActionListener(this);
        creatureBtn.addActionListener(this);
        runBtn.addActionListener(this);
        menuPanel.add(fightBtn);
        menuPanel.add(bagBtn);
        menuPanel.add(creatureBtn);
        menuPanel.add(runBtn);

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

    // ✅ Load sprite from resources/Texture/ by filename
    private JLabel loadSprite(String path) {
        // Extract just the filename from the path e.g. "/images/Santelmo.png" → "Santelmo.png"
        String fileName = new File(path).getName();

        // ✅ Try file system first
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

        // ✅ Try classpath
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

        // ✅ Fallback
        JLabel lbl = new JLabel("?", SwingConstants.CENTER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.BOLD, 40));
        return lbl;
    }

    // ═══════════════════════════════════════════════════════════════
    // BAG PANEL
    // ═══════════════════════════════════════════════════════════════

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

        JButton scrollBtn = bagItemBtn("Scroll", "x" + scrollCount,
                new Color(140, 70, 200), bagSelectedIndex == 0);
        scrollBtn.addActionListener(e -> {
            bagSelectedIndex = (bagSelectedIndex == 0) ? -1 : 0;
            rebuildBagPanel(); showCard("Bag");
        });
        itemList.add(scrollBtn);
        itemList.add(Box.createVerticalStrut(4));

        JButton lunasBtn = bagItemBtn("Lunas", "x" + lunasCount,
                new Color(60, 180, 100), bagSelectedIndex == 1);
        lunasBtn.addActionListener(e -> {
            bagSelectedIndex = (bagSelectedIndex == 1) ? -1 : 1;
            rebuildBagPanel(); showCard("Bag");
        });
        itemList.add(lunasBtn);
        itemList.add(Box.createVerticalStrut(4));

        JButton potionBtn = bagItemBtn("Potion", "x" + potionCount,
                new Color(60, 140, 220), bagSelectedIndex == 2);
        potionBtn.addActionListener(e -> {
            bagSelectedIndex = (bagSelectedIndex == 2) ? -1 : 2;
            rebuildBagPanel(); showCard("Bag");
        });
        itemList.add(potionBtn);
        itemList.add(Box.createVerticalStrut(8));

        JButton backBtn = menuBtn("BACK", new Color(0x555555));
        backBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        backBtn.addActionListener(e -> { bagSelectedIndex = -1; showCard("Menu"); });
        itemList.add(backBtn);

        JPanel detailPanel = buildBagDetailPanel();
        bagPanel.add(itemList,    BorderLayout.WEST);
        bagPanel.add(detailPanel, BorderLayout.CENTER);
        bagPanel.revalidate();
        bagPanel.repaint();
    }

    private JPanel buildBagDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(15, 10, 4));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 55, 15), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 10)));

        if (bagSelectedIndex == -1) {
            JLabel hint = new JLabel(
                    "<html><center><font color='gray'>Click an item<br>to see details</font></center></html>",
                    SwingConstants.CENTER);
            hint.setFont(new Font("Monospaced", Font.PLAIN, 12));
            panel.add(hint, BorderLayout.CENTER);
            return panel;
        }

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        String[] names  = {"SCROLL", "LUNAS", "POTION"};
        Color[]  colors = {new Color(140,70,200), new Color(60,180,100), new Color(60,140,220)};
        int[]    counts = {scrollCount, lunasCount, potionCount};
        String[][] descs = {
                {"Qty: x" + scrollCount, "Throw at wild creature to catch.", "Catch rate up when HP is low."},
                {"Qty: x" + lunasCount,  "Restores 5 PP to one move.", "Use when moves run out of PP."},
                {"Qty: x" + potionCount, "Restores 30 HP to a creature.", "Cannot revive fainted creatures."}
        };

        JLabel titleLbl = new JLabel(names[bagSelectedIndex]);
        titleLbl.setForeground(colors[bagSelectedIndex]);
        titleLbl.setFont(new Font("Monospaced", Font.BOLD, 14));
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.add(titleLbl);
        top.add(Box.createVerticalStrut(4));

        for (String line : descs[bagSelectedIndex]) {
            JLabel lbl = new JLabel(line);
            lbl.setForeground(line.startsWith("Qty") ? Color.WHITE : new Color(170, 170, 170));
            lbl.setFont(new Font("Monospaced", Font.PLAIN, 11));
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            top.add(lbl);
            top.add(Box.createVerticalStrut(2));
        }

        panel.add(top, BorderLayout.NORTH);

        boolean hasItem = counts[bagSelectedIndex] > 0;
        JButton useBtn = new JButton(hasItem ? "USE" : "NONE LEFT");
        useBtn.setBackground(hasItem ? colors[bagSelectedIndex] : new Color(80, 80, 80));
        useBtn.setForeground(Color.WHITE);
        useBtn.setFont(new Font("Monospaced", Font.BOLD, 13));
        useBtn.setFocusPainted(false);
        useBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        useBtn.setEnabled(hasItem);

        final int sel = bagSelectedIndex;
        useBtn.addActionListener(e -> {
            if (sel == 0) { bagSelectedIndex = -1; useScroll(); }
            else if (sel == 1) { bagSelectedIndex = -1; pendingItemUse = "lunas"; rebuildCreatureSelectPanel(); showCard("CreatureSelect"); }
            else if (sel == 2) { bagSelectedIndex = -1; pendingItemUse = "potion"; rebuildCreatureSelectPanel(); showCard("CreatureSelect"); }
        });

        panel.add(useBtn, BorderLayout.SOUTH);
        return panel;
    }

    // ═══════════════════════════════════════════════════════════════
    // CREATURE SELECT
    // ═══════════════════════════════════════════════════════════════

    private void rebuildCreatureSelectPanel() {
        creatureSelectPanel.removeAll();
        creatureSelectPanel.setLayout(new BorderLayout(0, 6));
        creatureSelectPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        String itemName  = pendingItemUse.equals("lunas") ? "Lunas" : "Potion";
        Color  itemColor = pendingItemUse.equals("lunas")
                ? new Color(60, 180, 100) : new Color(60, 140, 220);

        JLabel titleLbl = new JLabel("Use " + itemName + " on which creature?", SwingConstants.LEFT);
        titleLbl.setForeground(itemColor);
        titleLbl.setFont(new Font("Monospaced", Font.BOLD, 14));
        creatureSelectPanel.add(titleLbl, BorderLayout.NORTH);

        ArrayList<Fighter> fullTeam = new ArrayList<>();
        fullTeam.add(defaultMon);
        fullTeam.addAll(capturedTeam);

        JPanel grid = new JPanel(new GridLayout(2, 3, 6, 6));
        grid.setOpaque(false);

        for (Fighter f : fullTeam) {
            int hp    = (int) Math.max(0, f.stats.get(0).value);
            int maxHp = (int) f.stats.get(0).base;
            boolean fainted = f.isFainted();
            boolean ppFull  = pendingItemUse.equals("lunas")
                    && f.moveset.stream().allMatch(m -> m.pp >= m.maxPp);
            boolean canUse  = !fainted && (pendingItemUse.equals("potion") ? hp < maxHp : !ppFull);

            String label = "<html><center><b>" + f.name + "</b>"
                    + (f == userMon ? " ★" : "")
                    + "<br><font size='3'>HP: " + hp + "/" + maxHp + "</font></center></html>";

            Color bg = !canUse ? new Color(60, 40, 40)
                    : (f == userMon ? new Color(50, 40, 10) : new Color(20, 40, 20));

            JButton btn = menuBtn(label, bg);
            btn.setEnabled(canUse);
            if (canUse) {
                final Fighter target = f;
                btn.addActionListener(e -> { pendingTarget = target; rebuildConfirmPanel(); showCard("Confirm"); });
            }
            grid.add(btn);
        }

        for (int i = fullTeam.size(); i < MAX_TEAM_SIZE - 1; i++) {
            JPanel empty = new JPanel(); empty.setOpaque(false); grid.add(empty);
        }

        creatureSelectPanel.add(grid, BorderLayout.CENTER);

        JButton backBtn = menuBtn("BACK", new Color(0x555555));
        backBtn.addActionListener(e -> { pendingItemUse = ""; bagSelectedIndex = -1; rebuildBagPanel(); showCard("Bag"); });
        JPanel backRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        backRow.setOpaque(false);
        backRow.add(backBtn);
        creatureSelectPanel.add(backRow, BorderLayout.SOUTH);
        creatureSelectPanel.revalidate();
        creatureSelectPanel.repaint();
    }

    // ═══════════════════════════════════════════════════════════════
    // CONFIRM PANEL
    // ═══════════════════════════════════════════════════════════════

    private void rebuildConfirmPanel() {
        confirmPanel.removeAll();
        confirmPanel.setLayout(new BorderLayout(0, 10));
        confirmPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        String itemName  = pendingItemUse.equals("lunas") ? "Lunas" : "Potion";
        Color  itemColor = pendingItemUse.equals("lunas") ? new Color(60, 180, 100) : new Color(60, 140, 220);
        String effect    = pendingItemUse.equals("lunas") ? "restore PP" : "restore 30 HP";

        JLabel msg = new JLabel(
                "<html><center>Use <b><font color='" + colorToHex(itemColor) + "'>"
                        + itemName + "</font></b> on <b>" + pendingTarget.name
                        + "</b><br>to " + effect + "?</center></html>",
                SwingConstants.CENTER);
        msg.setForeground(Color.WHITE);
        msg.setFont(new Font("Monospaced", Font.PLAIN, 15));
        confirmPanel.add(msg, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 16, 0));
        btnRow.setOpaque(false);
        JButton yesBtn = menuBtn("YES", new Color(30, 140, 60));
        JButton noBtn  = menuBtn("NO",  new Color(160, 40, 40));
        yesBtn.addActionListener(e -> applyItemToCreature());
        noBtn.addActionListener(e -> { rebuildCreatureSelectPanel(); showCard("CreatureSelect"); });
        btnRow.add(yesBtn); btnRow.add(noBtn);
        confirmPanel.add(btnRow, BorderLayout.SOUTH);
        confirmPanel.revalidate();
        confirmPanel.repaint();
    }

    // ═══════════════════════════════════════════════════════════════
    // APPLY ITEM
    // ═══════════════════════════════════════════════════════════════

    private void applyItemToCreature() {
        if (pendingTarget == null) return;

        if (pendingItemUse.equals("potion")) {
            potionCount--;
            int before = (int) pendingTarget.stats.get(0).value;
            pendingTarget.stats.get(0).value = Math.min(
                    pendingTarget.stats.get(0).base,
                    pendingTarget.stats.get(0).value + 30);
            int healed = (int) pendingTarget.stats.get(0).value - before;
            if (pendingTarget == userMon) {
                userHealth.setValue((int) userMon.stats.get(0).value);
                userHpLabel.setText(getHpText(userMon));
            }
            String resultMsg = pendingTarget.name + " restored " + healed + " HP!";
            pendingTarget = null; pendingItemUse = "";
            showMessage(resultMsg, () -> {
                int oppMove = oppMon.chooseMove(userMon);
                handleMove(oppMove, oppMove, oppMon, userMon, 1);
            });

        } else if (pendingItemUse.equals("lunas")) {
            lunasCount--;
            Move target = null;
            for (Move m : pendingTarget.moveset) {
                if (m.pp < m.maxPp) { if (target == null || m.pp < target.pp) target = m; }
            }
            String resultMsg;
            if (target == null) {
                resultMsg = pendingTarget.name + "'s moves are all full!";
            } else {
                int restored = Math.min(5, target.maxPp - target.pp);
                target.pp = Math.min(target.maxPp, target.pp + 5);
                resultMsg = pendingTarget.name + "'s " + target.name + " restored " + restored + " PP!";
                if (pendingTarget == userMon) rebuildMovePanel();
            }
            pendingTarget = null; pendingItemUse = "";
            showMessage(resultMsg, () -> {
                int oppMove = oppMon.chooseMove(userMon);
                handleMove(oppMove, oppMove, oppMon, userMon, 1);
            });
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // SCROLL
    // ═══════════════════════════════════════════════════════════════

    private void useScroll() {
        if (scrollCount <= 0) { showMessage("No scrolls left!", () -> showCard("Menu")); return; }
        if (capturedTeam.size() >= MAX_TEAM_SIZE - 1) {
            showMessage("Team is full!", () -> showCard("Menu")); return;
        }
        scrollCount--;
        rebuildBagPanel();

        double hpPercent   = oppMon.stats.get(0).value / oppMon.stats.get(0).base;
        double catchChance = Math.max(0.1, Math.min(0.9, 0.8 - (hpPercent * 0.6)));

        if (Math.random() < catchChance) {
            capturedTeam.add(oppMon);
            creatureBtn.setText("CREATURE (" + capturedTeam.size() + "/" + (MAX_TEAM_SIZE - 1) + ")");
            showMessage(oppMon.name + " was captured!", () ->
                    showMessage("Team: " + capturedTeam.size() + "/" + (MAX_TEAM_SIZE - 1), () -> {
                        if (onBattleEnd != null)
                            onBattleEnd.onComplete(userMon, capturedTeam, scrollCount,
                                    lunasCount, potionCount, false);
                    })
            );
        } else {
            showMessage(oppMon.name + " broke free!", () -> {
                int oppMove = oppMon.chooseMove(userMon);
                handleMove(oppMove, oppMove, oppMon, userMon, 1);
            });
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // SWITCH
    // ═══════════════════════════════════════════════════════════════

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
        fullTeam.add(defaultMon);
        fullTeam.addAll(capturedTeam);

        int displayCount = Math.min(fullTeam.size(), MAX_TEAM_SIZE);
        JPanel btnGrid = new JPanel(new GridLayout(2, 3, 8, 8));
        btnGrid.setOpaque(false);

        for (int i = 0; i < displayCount; i++) {
            Fighter f        = fullTeam.get(i);
            boolean isActive  = (f == userMon);
            boolean isFainted = f.isFainted();
            Color bg = isActive ? new Color(0x888888) : isFainted ? new Color(0x550000) : new Color(0x2a6e2a);
            String label = "<html><center>" + f.name
                    + (isActive ? " ★" : "") + (isFainted ? " ✗" : "")
                    + "<br><font size='3'>HP: " + (int) f.stats.get(0).value
                    + "/" + (int) f.stats.get(0).base + "</font></center></html>";
            JButton btn = menuBtn(label, bg);
            if (isActive || isFainted) { btn.setEnabled(false); }
            else { final Fighter target = f; btn.addActionListener(e -> switchTo(target)); }
            btnGrid.add(btn);
        }
        for (int i = displayCount; i < MAX_TEAM_SIZE; i++) {
            JPanel empty = new JPanel(); empty.setOpaque(false); btnGrid.add(empty);
        }
        switchPanel.add(btnGrid, BorderLayout.CENTER);
        switchPanel.revalidate();
        switchPanel.repaint();
    }

    private void switchTo(Fighter newFighter) {
        if (newFighter.isFainted()) {
            showMessage(newFighter.name + " has fainted!", () -> showCard("Switch")); return;
        }
        userMon = newFighter;
        userNameLbl.setText(userMon.name);
        pokePanel.removeAll();
        userPokemon = loadSprite(userMon.back_sprite);
        pokePanel.add(userPokemon);
        pokePanel.add(oppPokemon);
        pokePanel.revalidate(); pokePanel.repaint();
        userHealth.setMaximum((int) userMon.stats.get(0).base);
        userHealth.setValue((int) userMon.stats.get(0).value);
        userHpLabel.setText(getHpText(userMon));
        rebuildMovePanel();
        creatureBtn.setText("CREATURE (" + capturedTeam.size() + "/" + (MAX_TEAM_SIZE - 1) + ")");
        rebuildSwitchPanel();
        showMessage("Go! " + userMon.name + "!", () -> showCard("Menu"));
    }

    // ═══════════════════════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════════════════════

    private JButton bagItemBtn(String name, String countStr, Color accent, boolean selected) {
        JButton btn = new JButton("<html><b>" + name + "</b>&nbsp;&nbsp;&nbsp;"
                + "<font color='#" + colorToHex(accent) + "'>" + countStr + "</font></html>");
        btn.setBackground(selected ? new Color(60, 40, 10) : new Color(30, 20, 8));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Monospaced", Font.PLAIN, 13));
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createLineBorder(
                selected ? accent : new Color(80, 55, 15), selected ? 2 : 1));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    private String colorToHex(Color c) {
        return String.format("%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    private String getHpText(Fighter f) {
        return "HP: " + (int) f.stats.get(0).value + " / " + (int) f.stats.get(0).base;
    }

    private void styleLabel(JLabel lbl) {
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Monospaced", Font.BOLD, 14));
    }

    private JProgressBar buildBar(Fighter f, Color color) {
        JProgressBar bar = new JProgressBar(0, (int) f.stats.get(0).base);
        bar.setValue((int) f.stats.get(0).value);
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
        ((CardLayout) botPanel.getLayout()).show(botPanel, name);
    }

    private void showMessage(String msg, Runnable after) {
        statUsed.setText(msg);
        showCard("Stat");
        new Timer(1300, e -> {
            ((Timer) e.getSource()).stop();
            if (after != null) after.run();
        }).start();
    }

    private boolean allPlayerFainted() {
        if (!defaultMon.isFainted()) return false;
        for (Fighter f : capturedTeam) { if (!f.isFainted()) return false; }
        return true;
    }

    private Fighter findNextAvailable() {
        if (!defaultMon.isFainted()) return defaultMon;
        for (Fighter f : capturedTeam) { if (!f.isFainted()) return f; }
        return null;
    }

    private void healAllFighters() {
        healFighter(defaultMon);
        for (Fighter f : capturedTeam) healFighter(f);
    }

    private void healFighter(Fighter f) {
        f.stats.get(0).value = f.stats.get(0).base;
        f.fainted = false;
        for (Move m : f.moveset) m.pp = m.maxPp;
    }

    private void rebuildMovePanel() {
        movePanel.removeAll();
        move1 = moveBtnWithPP(userMon.moveset.get(0));
        move2 = moveBtnWithPP(userMon.moveset.get(1));
        move3 = moveBtnWithPP(userMon.moveset.get(2));
        move4 = moveBtnWithPP(userMon.moveset.get(3));
        movePanel.add(move1); movePanel.add(move2);
        movePanel.add(move3); movePanel.add(move4);
        movePanel.revalidate(); movePanel.repaint();
    }

    private JButton moveBtnWithPP(Move move) {
        Color ppColor = move.pp <= 2 ? new Color(0xaa0000) : move.type.color;
        String label  = "<html><center>" + move.name
                + "<br><font size='3'>PP " + move.pp + "/" + move.maxPp
                + "</font></center></html>";
        JButton btn = new JButton(label);
        btn.setBackground(ppColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Monospaced", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        btn.addActionListener(this);
        if (move.pp <= 0) {
            btn.setEnabled(false);
            btn.setText("<html><center>" + move.name
                    + "<br><font size='3'>No PP!</font></center></html>");
        }
        return btn;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == fightBtn) {
            showCard("Moves");
        } else if (src == bagBtn) {
            bagSelectedIndex = -1;
            rebuildBagPanel();
            showCard("Bag");
        } else if (src == creatureBtn) {
            rebuildSwitchPanel();
            showCard("Switch");
        } else if (src == runBtn) {
            showMessage("You ran away safely!", () -> {
                if (onRun != null)
                    onRun.onComplete(userMon, capturedTeam, scrollCount,
                            lunasCount, potionCount, false);
            });
        } else {
            int mi = -1;
            if (src == move1) mi = 0;
            else if (src == move2) mi = 1;
            else if (src == move3) mi = 2;
            else if (src == move4) mi = 3;
            if (mi >= 0) {
                Move chosen = userMon.moveset.get(mi);
                if (chosen.pp <= 0) {
                    showMessage("No PP left for " + chosen.name + "!", () -> showCard("Moves"));
                    return;
                }
                if (userMon.stats.get(3).value >= oppMon.stats.get(3).value) {
                    handleMove(mi, oppMon.chooseMove(userMon), userMon, oppMon, 0);
                } else {
                    handleMove(oppMon.chooseMove(userMon), mi, oppMon, userMon, 0);
                }
            }
        }
    }

    private void handleMove(int userMove, int oppMove,
                            Fighter user, Fighter opp, int turn) {
        Move move = user.moveset.get(userMove);
        if (user == userMon) {
            move.pp = Math.max(0, move.pp - 1);
            rebuildMovePanel();
        }
        String ppInfo = (user == userMon)
                ? " (PP: " + move.pp + "/" + move.maxPp + ")" : "";
        moveUsed.setText(user.name + " used " + move.name + "!" + ppInfo);
        showCard("Used");
        user.useMove(opp, userMove);

        new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ((Timer) evt.getSource()).stop();

                oppHealth.setValue((int) Math.max(0, Math.ceil(oppMon.stats.get(0).value)));
                userHealth.setValue((int) Math.max(0, Math.ceil(userMon.stats.get(0).value)));
                userHpLabel.setText(getHpText(userMon));
                oppHpLabel.setText(getHpText(oppMon));

                double hpRatio = userMon.stats.get(0).value / userMon.stats.get(0).base;
                if (hpRatio <= 0.25)     userHpLabel.setForeground(Color.RED);
                else if (hpRatio <= 0.5) userHpLabel.setForeground(Color.YELLOW);
                else                     userHpLabel.setForeground(new Color(0x55cb0b));

                animatePokemon(user);

                if (oppMon.stats.get(0).value <= 0) {
                    showMessage(oppMon.name + " fainted!", () ->
                            showMessage("You won!", () -> {
                                if (onBattleEnd != null)
                                    onBattleEnd.onComplete(userMon, capturedTeam, scrollCount,
                                            lunasCount, potionCount, false);
                            })
                    );
                    return;
                }

                if (userMon.stats.get(0).value <= 0) {
                    showMessage(userMon.name + " fainted!", () -> {
                        if (allPlayerFainted()) {
                            showMessage("All your creatures fainted!", () ->
                                    showMessage("You blacked out...", () -> {
                                        healAllFighters();
                                        if (onBattleEnd != null)
                                            onBattleEnd.onComplete(defaultMon, capturedTeam,
                                                    scrollCount, lunasCount, potionCount, true);
                                    })
                            );
                        } else {
                            Fighter next = findNextAvailable();
                            showMessage("Choose your next creature!", () -> {
                                if (next != null) { switchTo(next); showCard("Switch"); }
                            });
                        }
                    });
                    return;
                }

                String msg = !userMon.message.isEmpty() ? userMon.message
                        : !oppMon.message.isEmpty() ? oppMon.message : "";
                userMon.message = "";
                oppMon.message  = "";

                if (!msg.isEmpty()) {
                    showMessage(msg, () -> {
                        if (turn == 0) handleMove(oppMove, userMove, opp, user, 1);
                        else showCard("Menu");
                    });
                } else {
                    if (turn == 0) handleMove(oppMove, userMove, opp, user, 1);
                    else showCard("Menu");
                }
            }
        }).start();
    }

    private void animatePokemon(Fighter attacker) {
        JLabel sprite = (attacker == userMon) ? userPokemon : oppPokemon;
        int originalY = sprite.getY();
        goingUp = true; offset = 0;
        Timer t = new Timer(15, null);
        t.addActionListener(e -> {
            if (goingUp) { offset -= step; if (offset <= -jumpHeight) goingUp = false; }
            else { offset += step; if (offset >= 0) { offset = 0; ((Timer) e.getSource()).stop(); } }
            sprite.setLocation(sprite.getX(), originalY + offset);
        });
        t.start();
    }
}