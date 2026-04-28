package game.battle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class BattleScreen extends JPanel implements ActionListener {

    private Fighter userMon;
    private Fighter oppMon;
    private Fighter defaultMon;

    private BattleCallback onBattleEnd;
    private BattleCallback onRun;

    private ArrayList<Fighter> capturedTeam;
    private int scrollCount;
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

    public BattleScreen(Fighter defaultMon,
                        Fighter oppMon,
                        ArrayList<Fighter> capturedTeam,
                        int scrollCount,
                        BattleCallback onBattleEnd,
                        BattleCallback onRun) {
        this.defaultMon  = defaultMon;
        this.userMon     = defaultMon;
        this.oppMon      = oppMon;
        this.capturedTeam = capturedTeam;
        this.scrollCount = scrollCount;
        this.onBattleEnd = onBattleEnd;
        this.onRun       = onRun;

        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        JPanel bgPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(20, 40, 20),
                        0, getHeight(), new Color(5, 15, 5));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
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
        botPanel.setPreferredSize(new Dimension(1280, 180));

        // Card: MENU
        menuPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        fightBtn    = menuBtn("FIGHT",    new Color(0xba1010));
        bagBtn      = menuBtn("BAG - Scrolls: " + scrollCount, new Color(0x329dfc));
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

        // Card: MOVES
        movePanel = new JPanel(new GridLayout(2, 2, 5, 5));
        movePanel.setOpaque(false);
        movePanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        rebuildMovePanel();

        // Card: TEXT
        textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        moveUsed = new JLabel("", SwingConstants.CENTER);
        moveUsed.setForeground(Color.WHITE);
        moveUsed.setFont(new Font("Monospaced", Font.BOLD, 18));
        textPanel.add(moveUsed, BorderLayout.CENTER);

        // Card: STAT
        statPanel = new JPanel(new BorderLayout());
        statPanel.setOpaque(false);
        statUsed = new JLabel("", SwingConstants.CENTER);
        statUsed.setForeground(Color.YELLOW);
        statUsed.setFont(new Font("Monospaced", Font.BOLD, 18));
        statPanel.add(statUsed, BorderLayout.CENTER);

        // Card: BAG
        bagPanel = new JPanel();
        bagPanel.setOpaque(false);
        rebuildBagPanel();

        // Card: SWITCH
        switchPanel = new JPanel();
        switchPanel.setOpaque(false);
        rebuildSwitchPanel();

        botPanel.add(menuPanel,   "Menu");
        botPanel.add(movePanel,   "Moves");
        botPanel.add(textPanel,   "Used");
        botPanel.add(statPanel,   "Stat");
        botPanel.add(bagPanel,    "Bag");
        botPanel.add(switchPanel, "Switch");

        bgPanel.add(botPanel, BorderLayout.SOUTH);
        add(bgPanel, BorderLayout.CENTER);

        showCard("Menu");
    }

    // ── Helpers ───────────────────────────────────────────────────

    private String getHpText(Fighter f) {
        return "HP: " + (int) f.stats.get(0).value + " / " + (int) f.stats.get(0).base;
    }

    private void rebuildMovePanel() {
        movePanel.removeAll();
        move1 = moveBtnWithPP(userMon.moveset.get(0));
        move2 = moveBtnWithPP(userMon.moveset.get(1));
        move3 = moveBtnWithPP(userMon.moveset.get(2));
        move4 = moveBtnWithPP(userMon.moveset.get(3));
        movePanel.add(move1);
        movePanel.add(move2);
        movePanel.add(move3);
        movePanel.add(move4);
        movePanel.revalidate();
        movePanel.repaint();
    }

    private JButton moveBtnWithPP(Move move) {
        Color ppColor = move.pp <= 2 ? new Color(0xaa0000) : move.type.color;
        String label  = "<html><center>" + move.name +
                "<br><font size='3'>PP " + move.pp + "/" + move.maxPp +
                "</font></center></html>";
        JButton btn = new JButton(label);
        btn.setBackground(ppColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Monospaced", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        btn.addActionListener(this);
        if (move.pp <= 0) {
            btn.setEnabled(false);
            btn.setText("<html><center>" + move.name +
                    "<br><font size='3'>No PP!</font></center></html>");
        }
        return btn;
    }

    private void rebuildBagPanel() {
        bagPanel.removeAll();
        bagPanel.setLayout(new GridLayout(1, 2, 10, 10));
        bagPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JButton scrollBtn = menuBtn("SCROLL (" + scrollCount + " left)", new Color(0x9b3fcf));
        JButton backBtn   = menuBtn("BACK", new Color(0x555555));
        scrollBtn.addActionListener(e -> useScroll());
        backBtn.addActionListener(e -> showCard("Menu"));

        bagPanel.add(scrollBtn);
        bagPanel.add(backBtn);
        bagPanel.revalidate();
        bagPanel.repaint();
    }

    private void rebuildSwitchPanel() {
        switchPanel.removeAll();
        switchPanel.setLayout(new BorderLayout());
        switchPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        JLabel titleLbl = new JLabel(
                "Your Team  |  Active: " + userMon.name,
                SwingConstants.LEFT);
        titleLbl.setForeground(Color.YELLOW);
        titleLbl.setFont(new Font("Monospaced", Font.BOLD, 13));
        topRow.add(titleLbl, BorderLayout.WEST);

        JButton backBtn = menuBtn("BACK", new Color(0x555555));
        backBtn.addActionListener(e -> showCard("Menu"));
        topRow.add(backBtn, BorderLayout.EAST);
        switchPanel.add(topRow, BorderLayout.NORTH);

        // ✅ Santelmo first, then captured
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

            Color bg = isActive  ? new Color(0x888888)
                    : isFainted ? new Color(0x550000)
                      :             new Color(0x2a6e2a);

            String label = "<html><center>" + f.name +
                    (isActive  ? " ★" : "") +
                    (isFainted ? " ✗" : "") +
                    "<br><font size='3'>HP: " + (int) f.stats.get(0).value +
                    "/" + (int) f.stats.get(0).base +
                    "</font></center></html>";

            JButton btn = menuBtn(label, bg);

            if (isActive || isFainted) {
                btn.setEnabled(false);
            } else {
                final Fighter target = f; // ✅ capture Fighter directly not index
                btn.addActionListener(e -> switchTo(target));
            }
            btnGrid.add(btn);
        }

        // Fill empty slots
        for (int i = displayCount; i < MAX_TEAM_SIZE; i++) {
            JPanel empty = new JPanel();
            empty.setOpaque(false);
            btnGrid.add(empty);
        }

        switchPanel.add(btnGrid, BorderLayout.CENTER);
        switchPanel.revalidate();
        switchPanel.repaint();
    }

    private void switchTo(Fighter newFighter) {
        if (newFighter.isFainted()) {
            showMessage(newFighter.name + " has fainted and can't battle!",
                    () -> showCard("Switch"));
            return;
        }

        userMon = newFighter;
        userNameLbl.setText(userMon.name);

        pokePanel.removeAll();
        userPokemon = loadSprite(userMon.back_sprite);
        pokePanel.add(userPokemon);
        pokePanel.add(oppPokemon);
        pokePanel.revalidate();
        pokePanel.repaint();

        userHealth.setMaximum((int) userMon.stats.get(0).base);
        userHealth.setValue((int) userMon.stats.get(0).value);
        userHpLabel.setText(getHpText(userMon));

        rebuildMovePanel();

        creatureBtn.setText("CREATURE (" + capturedTeam.size() +
                "/" + (MAX_TEAM_SIZE - 1) + ")");
        rebuildSwitchPanel();

        showMessage("Go! " + userMon.name + "!", () -> showCard("Menu"));
    }

    private void useScroll() {
        if (scrollCount <= 0) {
            showMessage("No scrolls left!", () -> showCard("Menu"));
            return;
        }
        if (capturedTeam.size() >= MAX_TEAM_SIZE - 1) {
            showMessage("Team is full! (Max " + (MAX_TEAM_SIZE - 1) + " captured)",
                    () -> showCard("Menu"));
            return;
        }

        scrollCount--;
        rebuildBagPanel();
        bagBtn.setText("BAG - Scrolls: " + scrollCount);

        double hpPercent   = oppMon.stats.get(0).value / oppMon.stats.get(0).base;
        double catchChance = Math.max(0.1, Math.min(0.9, 0.8 - (hpPercent * 0.6)));

        if (Math.random() < catchChance) {
            capturedTeam.add(oppMon);
            creatureBtn.setText("CREATURE (" + capturedTeam.size() +
                    "/" + (MAX_TEAM_SIZE - 1) + ")");
            showMessage(oppMon.name + " was captured!", () ->
                    showMessage("Team: " + capturedTeam.size() +
                            "/" + (MAX_TEAM_SIZE - 1), () -> {
                        if (onBattleEnd != null)
                            onBattleEnd.onComplete(
                                    userMon, capturedTeam, scrollCount, false);
                    })
            );
        } else {
            showMessage(oppMon.name + " broke free!", () -> {
                int oppMove = oppMon.chooseMove(userMon);
                handleMove(oppMove, oppMove, oppMon, userMon, 1);
            });
        }
    }

    private boolean allPlayerFainted() {
        if (!defaultMon.isFainted()) return false;
        for (Fighter f : capturedTeam) {
            if (!f.isFainted()) return false;
        }
        return true;
    }

    private Fighter findNextAvailable() {
        if (!defaultMon.isFainted()) return defaultMon;
        for (Fighter f : capturedTeam) {
            if (!f.isFainted()) return f;
        }
        return null;
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

    private JLabel loadSprite(String path) {
        try {
            java.net.URL url = getClass().getResource(path);
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                Image scaled   = icon.getImage()
                        .getScaledInstance(130, 130, Image.SCALE_SMOOTH);
                return new JLabel(new ImageIcon(scaled));
            }
        } catch (Exception ignored) {}
        JLabel lbl = new JLabel("?", SwingConstants.CENTER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.BOLD, 40));
        return lbl;
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

    // ── Action Handling ───────────────────────────────────────────

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == fightBtn) {
            showCard("Moves");

        } else if (src == bagBtn) {
            showCard("Bag");

        } else if (src == creatureBtn) {
            rebuildSwitchPanel();
            showCard("Switch");

        } else if (src == runBtn) {
            // ✅ run - blackout false
            showMessage("You ran away safely!", () -> {
                if (onRun != null)
                    onRun.onComplete(userMon, capturedTeam, scrollCount, false);
            });

        } else {
            int mi = -1;
            if (src == move1)      mi = 0;
            else if (src == move2) mi = 1;
            else if (src == move3) mi = 2;
            else if (src == move4) mi = 3;

            if (mi >= 0) {
                Move chosen = userMon.moveset.get(mi);
                if (chosen.pp <= 0) {
                    showMessage("No PP left for " + chosen.name + "!",
                            () -> showCard("Moves"));
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

        // ✅ Deduct PP only for player moves
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

                // ✅ Update bars and labels
                oppHealth.setValue((int) Math.max(0, Math.ceil(oppMon.stats.get(0).value)));
                userHealth.setValue((int) Math.max(0, Math.ceil(userMon.stats.get(0).value)));
                userHpLabel.setText(getHpText(userMon));
                oppHpLabel.setText(getHpText(oppMon));

                // ✅ HP color warning
                double hpRatio = userMon.stats.get(0).value / userMon.stats.get(0).base;
                if (hpRatio <= 0.25)     userHpLabel.setForeground(Color.RED);
                else if (hpRatio <= 0.5) userHpLabel.setForeground(Color.YELLOW);
                else                     userHpLabel.setForeground(new Color(0x55cb0b));

                animatePokemon(user);

                // ✅ Opp fainted - player wins
                if (oppMon.stats.get(0).value <= 0) {
                    showMessage(oppMon.name + " fainted!", () ->
                            showMessage("You won!", () -> {
                                if (onBattleEnd != null)
                                    onBattleEnd.onComplete(
                                            userMon, capturedTeam, scrollCount, false);
                            })
                    );
                    return;
                }

                // ✅ Active fighter fainted
                if (userMon.stats.get(0).value <= 0) {
                    showMessage(userMon.name + " fainted!", () -> {
                        if (allPlayerFainted()) {
                            // ✅ Blackout - heal everyone, go to start
                            showMessage("All your creatures fainted!", () ->
                                    showMessage("You blacked out...", () -> {
                                        healAllFighters();
                                        if (onBattleEnd != null)
                                            onBattleEnd.onComplete(
                                                    defaultMon, capturedTeam,
                                                    scrollCount, true); // ✅ blackout=true
                                    })
                            );
                        } else {
                            // ✅ Still have fighters - force switch
                            Fighter next = findNextAvailable();
                            showMessage("Choose your next creature!", () -> {
                                if (next != null) {
                                    switchTo(next);
                                    showCard("Switch");
                                }
                            });
                        }
                    });
                    return;
                }

                // ✅ Show stat message then continue
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

    private void healAllFighters() {
        healFighter(defaultMon);
        for (Fighter f : capturedTeam) {
            healFighter(f);
        }
    }

    private void healFighter(Fighter f) {
        f.stats.get(0).value = f.stats.get(0).base;
        f.fainted = false;
        for (Move m : f.moveset) {
            m.pp = m.maxPp;
        }
    }

    private void animatePokemon(Fighter attacker) {
        JLabel sprite    = (attacker == userMon) ? userPokemon : oppPokemon;
        int originalY    = sprite.getY();
        goingUp = true;
        offset  = 0;
        Timer t = new Timer(15, null);
        t.addActionListener(e -> {
            if (goingUp) {
                offset -= step;
                if (offset <= -jumpHeight) goingUp = false;
            } else {
                offset += step;
                if (offset >= 0) {
                    offset = 0;
                    ((Timer) e.getSource()).stop();
                }
            }
            sprite.setLocation(sprite.getX(), originalY + offset);
        });
        t.start();
    }
}