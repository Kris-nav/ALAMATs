package game.battle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class BattleScreen extends JPanel implements ActionListener {

    private Fighter userMon;
    private Fighter oppMon;
    private Runnable onBattleEnd;

    // ✅ Team for capture and switch
    private ArrayList<Fighter> capturedTeam;
    private int scrollCount = 3;

    private JPanel botPanel;
    private JPanel pokePanel;
    private JPanel movePanel;
    private JPanel menuPanel;
    private JPanel textPanel;
    private JPanel statPanel;
    private JPanel healthPanel;
    private JPanel bagPanel;    // ✅ new
    private JPanel switchPanel; // ✅ new

    private JLabel moveUsed;
    private JLabel userPokemon;
    private JLabel oppPokemon;
    private JLabel statUsed;

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

    // ✅ Updated constructor - takes team
    public BattleScreen(Fighter userMon, Fighter oppMon,
                        ArrayList<Fighter> capturedTeam, Runnable onBattleEnd) {
        this.userMon = userMon;
        this.oppMon = oppMon;
        this.capturedTeam = capturedTeam;
        this.onBattleEnd = onBattleEnd;

        // ✅ Add player's fighter to team if not already there
        if (!capturedTeam.contains(userMon)) {
            capturedTeam.add(0, userMon);
        }

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
        JLabel userNameLbl = new JLabel(userMon.name, SwingConstants.CENTER);
        JLabel oppNameLbl  = new JLabel(oppMon.name,  SwingConstants.CENTER);
        styleLabel(userNameLbl);
        styleLabel(oppNameLbl);
        namePanel.add(userNameLbl);
        namePanel.add(oppNameLbl);

        // ── Health bars ───────────────────────────────────────────
        healthPanel = new JPanel(new GridLayout(1, 2));
        healthPanel.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 30));
        healthPanel.setOpaque(false);
        userHealth = buildBar(userMon, new Color(0x55cb0b));
        oppHealth  = buildBar(oppMon,  Color.RED);
        healthPanel.add(userHealth);
        healthPanel.add(oppHealth);

        JPanel topArea = new JPanel(new BorderLayout());
        topArea.setOpaque(false);
        topArea.add(pokePanel,   BorderLayout.NORTH);
        topArea.add(namePanel,   BorderLayout.CENTER);
        topArea.add(healthPanel, BorderLayout.SOUTH);
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
        bagBtn      = menuBtn("BAG (" + scrollCount + " Scrolls)", new Color(0x329dfc));
        creatureBtn = menuBtn("CREATURE (" + capturedTeam.size() + ")", new Color(0x55cb0b));
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
        move1 = moveBtn(userMon.moveset.get(0));
        move2 = moveBtn(userMon.moveset.get(1));
        move3 = moveBtn(userMon.moveset.get(2));
        move4 = moveBtn(userMon.moveset.get(3));
        movePanel.add(move1);
        movePanel.add(move2);
        movePanel.add(move3);
        movePanel.add(move4);

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

        // Card: BAG ✅
        bagPanel = new JPanel();
        bagPanel.setOpaque(false);
        rebuildBagPanel();

        // Card: SWITCH ✅
        switchPanel = new JPanel();
        switchPanel.setOpaque(false);
        rebuildSwitchPanel();

        botPanel.add(menuPanel,   "Menu");
        botPanel.add(movePanel,   "Moves");
        botPanel.add(textPanel,   "Used");
        botPanel.add(statPanel,   "Stat");
        botPanel.add(bagPanel,    "Bag");    // ✅
        botPanel.add(switchPanel, "Switch"); // ✅

        bgPanel.add(botPanel, BorderLayout.SOUTH);
        add(bgPanel, BorderLayout.CENTER);

        showCard("Menu");
    }

    // ✅ Bag panel with scroll button
    private void rebuildBagPanel() {
        bagPanel.removeAll();
        bagPanel.setLayout(new GridLayout(1, 2, 10, 10));
        bagPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JButton scrollBtn = menuBtn("SCROLL (" + scrollCount + ")", new Color(0x9b3fcf));
        JButton backBtn   = menuBtn("BACK", new Color(0x555555));

        scrollBtn.addActionListener(e -> useScroll());
        backBtn.addActionListener(e -> showCard("Menu"));

        bagPanel.add(scrollBtn);
        bagPanel.add(backBtn);
        bagPanel.revalidate();
        bagPanel.repaint();
    }

    // ✅ Switch panel with all captured creatures
    private void rebuildSwitchPanel() {
        switchPanel.removeAll();
        switchPanel.setLayout(new BorderLayout());
        switchPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel btnGrid = new JPanel(new GridLayout(
                1, Math.max(1, capturedTeam.size() + 1), 5, 5));
        btnGrid.setOpaque(false);

        for (int i = 0; i < capturedTeam.size(); i++) {
            Fighter f = capturedTeam.get(i);
            boolean isActive = (f == userMon);
            Color bg = isActive ? new Color(0x888888) : new Color(0x55cb0b);
            JButton btn = menuBtn(f.name + (isActive ? " (Active)" : ""), bg);
            if (!isActive) {
                final int idx = i;
                btn.addActionListener(e -> switchTo(idx));
            } else {
                btn.setEnabled(false);
            }
            btnGrid.add(btn);
        }

        JButton backBtn = menuBtn("BACK", new Color(0x555555));
        backBtn.addActionListener(e -> showCard("Menu"));
        btnGrid.add(backBtn);

        switchPanel.add(btnGrid, BorderLayout.CENTER);
        switchPanel.revalidate();
        switchPanel.repaint();
    }

    // ✅ Switch active fighter
    private void switchTo(int index) {
        userMon = capturedTeam.get(index);

        // Update sprite
        pokePanel.removeAll();
        userPokemon = loadSprite(userMon.back_sprite);
        pokePanel.add(userPokemon);
        pokePanel.add(oppPokemon);
        pokePanel.revalidate();
        pokePanel.repaint();

        // Update health bar
        userHealth.setMaximum((int) userMon.stats.get(0).base);
        userHealth.setValue((int) userMon.stats.get(0).value);

        // Update move buttons
        movePanel.removeAll();
        move1 = moveBtn(userMon.moveset.get(0));
        move2 = moveBtn(userMon.moveset.get(1));
        move3 = moveBtn(userMon.moveset.get(2));
        move4 = moveBtn(userMon.moveset.get(3));
        movePanel.add(move1);
        movePanel.add(move2);
        movePanel.add(move3);
        movePanel.add(move4);
        movePanel.revalidate();
        movePanel.repaint();

        creatureBtn.setText("CREATURE (" + capturedTeam.size() + ")");
        rebuildSwitchPanel();

        showMessage("Go! " + userMon.name + "!", () -> showCard("Menu"));
    }

    // ✅ Use scroll to capture
    private void useScroll() {
        if (scrollCount <= 0) {
            showMessage("No scrolls left!", () -> showCard("Menu"));
            return;
        }
        scrollCount--;
        rebuildBagPanel();
        bagBtn.setText("BAG (" + scrollCount + " Scrolls)");

        // Catch chance: lower opp HP = higher chance
        double hpPercent = oppMon.stats.get(0).value / oppMon.stats.get(0).base;
        double catchChance = Math.max(0.1, Math.min(0.9, 0.8 - (hpPercent * 0.6)));

        if (Math.random() < catchChance) {
            // ✅ Captured!
            capturedTeam.add(oppMon);
            creatureBtn.setText("CREATURE (" + capturedTeam.size() + ")");
            showMessage(oppMon.name + " was captured!", () ->
                    showMessage("Added to your team!", () -> {
                        if (onBattleEnd != null) onBattleEnd.run();
                    })
            );
        } else {
            // ✅ Broke free - opp gets a free attack
            showMessage(oppMon.name + " broke free!", () -> {
                int oppMove = oppMon.chooseMove(userMon);
                handleMove(oppMove, oppMove, oppMon, userMon, 1);
            });
        }
    }

    // ── Helpers ───────────────────────────────────────────────────
    private void styleLabel(JLabel lbl) {
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Monospaced", Font.BOLD, 14));
    }

    private JProgressBar buildBar(Fighter f, Color color) {
        JProgressBar bar = new JProgressBar(0, (int) f.stats.get(0).value);
        bar.setValue((int) f.stats.get(0).value);
        bar.setStringPainted(true);
        bar.setForeground(color);
        return bar;
    }

    private JLabel loadSprite(String path) {
        try {
            java.net.URL url = getClass().getResource(path);
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                Image scaled = icon.getImage()
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
        btn.setFont(new Font("Monospaced", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        return btn;
    }

    private JButton moveBtn(Move move) {
        JButton btn = new JButton(move.toString());
        btn.setBackground(move.type.color); // ✅ fixed
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Monospaced", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.addActionListener(this);
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
    // ✅ Add this field to BattleScreen
    private Runnable onRun;

    // ✅ Add setter
    public void setOnRun(Runnable onRun) {
        this.onRun = onRun;
    }

    // ── Action Handling ───────────────────────────────────────────
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == fightBtn) {
            showCard("Moves");
        } else if (src == bagBtn) {
            showCard("Bag"); // ✅ opens bag with scroll
        } else if (src == creatureBtn) {
            rebuildSwitchPanel();
            showCard("Switch"); // ✅ opens switch menu
        } else if (src == runBtn) {
            showMessage("You ran away safely!", () -> {
                // ✅ Call onRun instead of onBattleEnd if set
                if (onRun != null) onRun.run();
                else if (onBattleEnd != null) onBattleEnd.run();
            });
        }else {
            int mi = -1;
            if (src == move1) mi = 0;
            else if (src == move2) mi = 1;
            else if (src == move3) mi = 2;
            else if (src == move4) mi = 3;

            if (mi >= 0) {
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
        moveUsed.setText(user.name + " used " + user.moveset.get(userMove).name + "!");
        showCard("Used");
        user.useMove(opp, userMove);

        new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ((Timer) evt.getSource()).stop();

                oppHealth.setValue((int) Math.max(0, Math.ceil(oppMon.stats.get(0).value)));
                userHealth.setValue((int) Math.max(0, Math.ceil(userMon.stats.get(0).value)));

                animatePokemon(user);

                if (userMon.stats.get(0).value <= 0 || oppMon.stats.get(0).value <= 0) {
                    String faintMsg = userMon.stats.get(0).value <= 0
                            ? userMon.name + " fainted!"
                            : oppMon.name + " fainted!";
                    String resultMsg = userMon.stats.get(0).value <= 0
                            ? "You lost..." : "You won!";
                    showMessage(faintMsg, () ->
                            showMessage(resultMsg, () -> {
                                if (onBattleEnd != null) onBattleEnd.run();
                            })
                    );
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