package game.battle;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;



public class BattleScreen extends JPanel implements ActionListener {

    private Fighter userMon;
    private Fighter oppMon;
    private Runnable onBattleEnd;

    private JPanel botPanel;
    private JPanel pokePanel;
    private JPanel movePanel;
    private JPanel menuPanel;
    private JPanel textPanel;
    private JPanel statPanel;
    private JPanel healthPanel;

    private JLabel moveUsed;
    private JLabel userPokemon;
    private JLabel oppPokemon;
    private JLabel statUsed;

    // Main 4-button menu
    private JButton fightBtn;
    private JButton bagBtn;
    private JButton creatureBtn;
    private JButton runBtn;

    // Move buttons
    private JButton move1, move2, move3, move4;
    private JButton backBtn;

    // Health bars
    private JProgressBar userHealth;
    private JProgressBar oppHealth;

    // Animation
    private boolean goingUp = true;
    private int jumpHeight = 5;
    private int step = 2;
    private int offset = 0;

    public BattleScreen(Fighter userMon, Fighter oppMon, Runnable onBattleEnd) {
        this.userMon = userMon;
        this.oppMon = oppMon;
        this.onBattleEnd = onBattleEnd;

        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // Background
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

        // ── Pokemon sprites ───────────────────────────────────────
        pokePanel = new JPanel(new GridLayout(1, 2));
        pokePanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
        pokePanel.setOpaque(false);
        userPokemon = loadSprite(userMon.back_sprite);
        oppPokemon  = loadSprite(oppMon.sprite);
        pokePanel.add(userPokemon);
        pokePanel.add(oppPokemon);

        // ── Name labels ───────────────────────────────────────────
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

        // ── Top area ──────────────────────────────────────────────
        JPanel topArea = new JPanel(new BorderLayout());
        topArea.setOpaque(false);
        topArea.add(pokePanel,   BorderLayout.NORTH);
        topArea.add(namePanel,   BorderLayout.CENTER);
        topArea.add(healthPanel, BorderLayout.SOUTH);

        bgPanel.add(topArea, BorderLayout.CENTER);

        // ── Bottom CardLayout panel ───────────────────────────────
        botPanel = new JPanel(new CardLayout());
        botPanel.setBackground(new Color(30, 30, 30));
        botPanel.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 3));
        botPanel.setPreferredSize(new Dimension(1280, 180));

        // Card: MENU (FIGHT / BAG / CREATURE / RUN)
        menuPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        fightBtn    = menuBtn("FIGHT",    new Color(0xba1010));
        bagBtn      = menuBtn("BAG",      new Color(0x329dfc));
        creatureBtn = menuBtn("CREATURE", new Color(0x55cb0b));
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

        // Card: TEXT (move used)
        textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        moveUsed = new JLabel("", SwingConstants.CENTER);
        moveUsed.setForeground(Color.WHITE);
        moveUsed.setFont(new Font("Monospaced", Font.BOLD, 18));
        textPanel.add(moveUsed, BorderLayout.CENTER);

        // Card: STAT (messages)
        statPanel = new JPanel(new BorderLayout());
        statPanel.setOpaque(false);
        statUsed = new JLabel("", SwingConstants.CENTER);
        statUsed.setForeground(Color.YELLOW);
        statUsed.setFont(new Font("Monospaced", Font.BOLD, 18));
        statPanel.add(statUsed, BorderLayout.CENTER);

        botPanel.add(menuPanel, "Menu");
        botPanel.add(movePanel, "Moves");
        botPanel.add(textPanel, "Used");
        botPanel.add(statPanel, "Stat");

        bgPanel.add(botPanel, BorderLayout.SOUTH);
        add(bgPanel, BorderLayout.CENTER);

        showCard("Menu");
    }

    private JButton moveBtn(game.core.Move move) {
        return null;
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
                Image scaled = icon.getImage().getScaledInstance(130, 130, Image.SCALE_SMOOTH);
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
        btn.setFont(new Font("Monospaced", Font.BOLD, 18));
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

    // ── Action Handling ───────────────────────────────────────────

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == fightBtn) {
            showCard("Moves");
        } else if (src == bagBtn) {
            showMessage("Your bag is empty!", () -> showCard("Menu"));
        } else if (src == creatureBtn) {
            showMessage("You only have one creature!", () -> showCard("Menu"));
        } else if (src == runBtn) {
            showMessage("You ran away safely!", () -> {
                if (onBattleEnd != null) onBattleEnd.run();
            });
        } else {
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

                // Update bars
                oppHealth.setValue((int) Math.max(0, Math.ceil(oppMon.stats.get(0).value)));
                userHealth.setValue((int) Math.max(0, Math.ceil(userMon.stats.get(0).value)));

                animatePokemon(user);

                // Check faint
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

                // Show stat message
                String msg = !userMon.message.isEmpty() ? userMon.message
                        : !oppMon.message.isEmpty()  ? oppMon.message : "";
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