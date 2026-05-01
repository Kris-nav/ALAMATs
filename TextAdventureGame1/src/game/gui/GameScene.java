package game.gui;

import game.battle.BattleScreen;
import game.battle.Fighter;
import game.core.ProgressionManager;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    private String playerName    = "";
    private int    playerAge     = 0;
    private String playerGender  = "";
    private long   gameStartTime = 0L;

    private int playerCoins       = 500;
    private int antingAntingCount = 0;
    private static final Random rand = new Random();

    private Fighter starterFighter = null;

    private boolean adminMode      = false;
    private boolean caveSceneShown = false;
    private boolean bossFightDone  = false;
    private boolean portalVisible  = false;

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

    public void setPlayerProfile(String name, int age, String gender) {
        this.playerName   = name;
        this.playerAge    = age;
        this.playerGender = gender;
    }

    public void syncPersistentState(boolean adminMode, boolean caveSceneShown,
                                    boolean bossFightDone, boolean portalVisible) {
        this.adminMode      = adminMode;
        this.caveSceneShown = caveSceneShown;
        this.bossFightDone  = bossFightDone;
        this.portalVisible  = portalVisible;
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

    public void switchToWorld() {
        SwingUtilities.invokeLater(() -> {
            this.getContentPane().removeAll();
            this.setLayout(new BorderLayout());
            WorldPanel world = new WorldPanel(this, "?", 0, "?");
            this.add(world, BorderLayout.CENTER);
            this.pack();
            this.setLocationRelativeTo(null);
            this.revalidate();
            this.repaint();
            SwingUtilities.invokeLater(() -> {
                world.requestFocusInWindow();
                world.start();
                new Timer(300, e -> {
                    ((Timer) e.getSource()).stop();
                    world.showCreaturePopup(() -> openCharacterCreation(world));
                }).start();
            });
        });
    }

    private void openCharacterCreation(WorldPanel world) {
        SwingUtilities.invokeLater(() -> {
            CharacterCreationScene creation = new CharacterCreationScene();
            creation.setLocationRelativeTo(this);
            creation.setVisible(true);

            new Thread(() -> {
                creation.waitForConfirmation();
                playerName   = creation.getPlayerName();
                playerAge    = creation.getPlayerAge();
                playerGender = creation.getPlayerGender();

                SwingUtilities.invokeLater(() -> {
                    creation.dispose();
                    gameStartTime     = System.currentTimeMillis();
                    playerCoins       = 500;
                    antingAntingCount = 0;
                    starterFighter    = game.battle.Create.createPlayerStarter();

                    this.getContentPane().removeAll();
                    this.setLayout(new BorderLayout());
                    WorldPanel newWorld = new WorldPanel(this, playerName, playerAge, playerGender);
                    this.add(newWorld, BorderLayout.CENTER);
                    this.pack();
                    this.setLocationRelativeTo(null);
                    this.revalidate();
                    this.repaint();
                    SwingUtilities.invokeLater(() -> {
                        newWorld.requestFocusInWindow();
                        newWorld.start();
                    });
                });
            }).start();
        });
    }

    // ✅ Return to world after any battle — restores ALL persistent state
    public void switchToWorldAt(int x, int y,
                                ArrayList<Fighter> team,
                                Fighter playerFighter,
                                int scrollCount,
                                int lunasCount,
                                int potionCount,
                                int updatedCoins,
                                long cooldownUntil) {
        playerCoins = updatedCoins;
        Fighter worldFighter = (starterFighter != null) ? starterFighter : playerFighter;
        team.remove(worldFighter);

        if (adminMode) {
            for (Fighter f : team) maxFighterIfNeeded(f);
            maxFighterIfNeeded(worldFighter);
        }

        SwingUtilities.invokeLater(() -> {
            this.getContentPane().removeAll();
            this.setLayout(new BorderLayout());
            WorldPanel world = new WorldPanel(
                    this, x, y, team, worldFighter,
                    scrollCount, lunasCount, potionCount,
                    playerName, playerAge, playerGender,
                    playerCoins, gameStartTime, cooldownUntil,
                    adminMode, caveSceneShown, bossFightDone, portalVisible);
            world.setAntingAntingCount(antingAntingCount);
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

    // ✅ Return to world with custom map path (World 2+)
    public void switchToWorldAtMap(int x, int y,
                                   ArrayList<Fighter> team,
                                   Fighter playerFighter,
                                   int scrollCount,
                                   int lunasCount,
                                   int potionCount,
                                   int updatedCoins,
                                   long cooldownUntil,
                                   String mapPath) {
        playerCoins = updatedCoins;
        Fighter worldFighter = (starterFighter != null) ? starterFighter : playerFighter;
        team.remove(worldFighter);

        if (adminMode) {
            for (Fighter f : team) maxFighterIfNeeded(f);
            maxFighterIfNeeded(worldFighter);
        }

        SwingUtilities.invokeLater(() -> {
            this.getContentPane().removeAll();
            this.setLayout(new BorderLayout());
            WorldPanel world = new WorldPanel(
                    this, x, y, team, worldFighter,
                    scrollCount, lunasCount, potionCount,
                    playerName, playerAge, playerGender,
                    playerCoins, gameStartTime, cooldownUntil,
                    adminMode, caveSceneShown, bossFightDone, portalVisible,
                    mapPath);
            world.setAntingAntingCount(antingAntingCount);
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

    private void maxFighterIfNeeded(Fighter f) {
        if (f.level >= 99) return;
        f.level     = 99;
        f.exp       = 0;
        f.expToNext = game.battle.Fighter.expNeeded(99);
        f.stats.get(0).base = 9999; f.stats.get(0).value = 9999;
        f.stats.get(1).base = 999;  f.stats.get(1).value = 999;
        f.stats.get(2).base = 999;  f.stats.get(2).value = 999;
        if (f.stats.size() > 3) { f.stats.get(3).base = 999; f.stats.get(3).value = 999; }
        for (game.battle.Move m : f.moveset) { m.lockedUntilLevel = 0; m.pp = m.maxPp; }
    }

    public void switchToBattle(Fighter playerFighter,
                               Fighter wildFighter,
                               ArrayList<Fighter> team,
                               int scrollCount,
                               int lunasCount,
                               int potionCount,
                               int savedX, int savedY) {
        this.switchToBattleOnMap(playerFighter, wildFighter, team,
                scrollCount, lunasCount, potionCount,
                savedX, savedY, "resources/World1.tmx");
    }

    // ✅ Battle that returns to a specific map
    public void switchToBattleOnMap(Fighter playerFighter,
                                    Fighter wildFighter,
                                    ArrayList<Fighter> team,
                                    int scrollCount,
                                    int lunasCount,
                                    int potionCount,
                                    int savedX, int savedY,
                                    String returnMapPath) {
        if (starterFighter == null) starterFighter = playerFighter;

        SwingUtilities.invokeLater(() -> {
            this.getContentPane().removeAll();
            this.setLayout(new BorderLayout());

            BattleScreen battle = new BattleScreen(
                    playerFighter, wildFighter, team,
                    scrollCount, lunasCount, potionCount,

                    (updatedFighter, updatedTeam, updatedScrolls,
                     updatedLunas, updatedPotions, blackout) -> {
                        long cooldownUntil = System.currentTimeMillis() + 5000L;
                        if (!blackout) playerCoins += 50 + rand.nextInt(151);
                        if (blackout) {
                            switchToWorldAt(SPAWN_X, SPAWN_Y, updatedTeam, updatedFighter,
                                    updatedScrolls, updatedLunas, updatedPotions, playerCoins, 0L);
                        } else {
                            if (returnMapPath.equals("resources/World1.tmx")) {
                                switchToWorldAt(savedX, savedY, updatedTeam, updatedFighter,
                                        updatedScrolls, updatedLunas, updatedPotions, playerCoins, cooldownUntil);
                            } else {
                                switchToWorldAtMap(savedX, savedY, updatedTeam, updatedFighter,
                                        updatedScrolls, updatedLunas, updatedPotions, playerCoins, cooldownUntil, returnMapPath);
                            }
                        }
                    },

                    (updatedFighter, updatedTeam, updatedScrolls,
                     updatedLunas, updatedPotions, blackout) -> {
                        long cooldownUntil = System.currentTimeMillis() + 5000L;
                        if (returnMapPath.equals("resources/World1.tmx")) {
                            switchToWorldAt(savedX, savedY, updatedTeam, updatedFighter,
                                    updatedScrolls, updatedLunas, updatedPotions, playerCoins, cooldownUntil);
                        } else {
                            switchToWorldAtMap(savedX, savedY, updatedTeam, updatedFighter,
                                    updatedScrolls, updatedLunas, updatedPotions, playerCoins, cooldownUntil, returnMapPath);
                        }
                    }
            );

            this.add(battle, BorderLayout.CENTER);
            this.pack();
            this.setLocationRelativeTo(null);
            this.revalidate();
            this.repaint();
        });
    }

    public void switchToBossAt(Fighter playerFighter,
                               Fighter bossFirst,
                               ArrayList<Fighter> bossTeamRest,
                               ArrayList<Fighter> playerTeam,
                               int scrollCount, int lunasCount, int potionCount,
                               int savedX, int savedY,
                               Runnable onBossDefeated) {
        this.switchToBossAtOnMap(playerFighter, bossFirst, bossTeamRest, playerTeam,
                scrollCount, lunasCount, potionCount,
                savedX, savedY, onBossDefeated, "resources/World1.tmx");
    }

    // ✅ Boss battle that returns to a specific map
    public void switchToBossAtOnMap(Fighter playerFighter,
                                    Fighter bossFirst,
                                    ArrayList<Fighter> bossTeamRest,
                                    ArrayList<Fighter> playerTeam,
                                    int scrollCount, int lunasCount, int potionCount,
                                    int savedX, int savedY,
                                    Runnable onBossDefeated,
                                    String returnMapPath) {
        if (starterFighter == null) starterFighter = playerFighter;

        SwingUtilities.invokeLater(() -> {
            this.getContentPane().removeAll();
            this.setLayout(new BorderLayout());

            BattleScreen battle = new BattleScreen(
                    playerFighter, bossFirst, playerTeam,
                    scrollCount, lunasCount, potionCount,

                    (updatedFighter, updatedTeam, updatedScrolls,
                     updatedLunas, updatedPotions, blackout) -> {
                        long cooldown = System.currentTimeMillis() + 5000L;

                        // ✅ Boss rewards: 900 coins, +200 EXP, +1 anting-anting
                        playerCoins += 900;
                        antingAntingCount = Math.min(4, antingAntingCount + 1);
                        int bossExp = 200;
                        updatedFighter.gainExp(bossExp);
                        for (Fighter f : updatedTeam) f.gainExp(bossExp);

                        if (blackout) {
                            switchToWorldAt(SPAWN_X, SPAWN_Y, updatedTeam, updatedFighter,
                                    updatedScrolls, updatedLunas, updatedPotions, playerCoins, 0L);
                        } else {
                            bossFightDone = true;
                            portalVisible = true;
                            if (onBossDefeated != null) onBossDefeated.run();
                            if (returnMapPath.equals("resources/World1.tmx")) {
                                switchToWorldAt(savedX, savedY, updatedTeam, updatedFighter,
                                        updatedScrolls, updatedLunas, updatedPotions, playerCoins, cooldown);
                            } else {
                                switchToWorldAtMap(savedX, savedY, updatedTeam, updatedFighter,
                                        updatedScrolls, updatedLunas, updatedPotions, playerCoins, cooldown, returnMapPath);
                            }
                        }
                    },

                    (updatedFighter, updatedTeam, updatedScrolls,
                     updatedLunas, updatedPotions, blackout) -> {
                        long cooldown = System.currentTimeMillis() + 5000L;
                        if (returnMapPath.equals("resources/World1.tmx")) {
                            switchToWorldAt(savedX, savedY, updatedTeam, updatedFighter,
                                    updatedScrolls, updatedLunas, updatedPotions, playerCoins, cooldown);
                        } else {
                            switchToWorldAtMap(savedX, savedY, updatedTeam, updatedFighter,
                                    updatedScrolls, updatedLunas, updatedPotions, playerCoins, cooldown, returnMapPath);
                        }
                    },

                    true,
                    "The Witch Doctor",
                    bossTeamRest
            );

            this.add(battle, BorderLayout.CENTER);
            this.pack();
            this.setLocationRelativeTo(null);
            this.revalidate();
            this.repaint();
        });
    }

    // ✅ Switch to World 2
    public void switchToWorld2(Fighter playerFighter,
                               ArrayList<Fighter> team,
                               int scrollCount,
                               int lunasCount,
                               int potionCount,
                               int coins,
                               long startTime,
                               boolean adminMode,
                               boolean caveSceneShown,
                               int antingAntingCount) {
        Fighter worldFighter = (starterFighter != null) ? starterFighter : playerFighter;
        team.remove(worldFighter);

        if (adminMode) {
            for (Fighter f : team) maxFighterIfNeeded(f);
            maxFighterIfNeeded(worldFighter);
        }

        this.playerCoins       = coins;
        this.antingAntingCount = antingAntingCount;
        this.caveSceneShown    = caveSceneShown;
        this.adminMode         = adminMode;
        this.gameStartTime     = startTime;
        // ✅ Reset boss/portal for World 2
        this.bossFightDone  = false;
        this.portalVisible  = false;

        // ✅ World 2 spawn — adjust to your World2.tmx spawn point
        int world2SpawnX = 4205;
        int world2SpawnY = 5125;

        SwingUtilities.invokeLater(() -> {
            this.getContentPane().removeAll();
            this.setLayout(new BorderLayout());

            WorldPanel world = new WorldPanel(
                    this, world2SpawnX, world2SpawnY,
                    team, worldFighter,
                    scrollCount, lunasCount, potionCount,
                    playerName, playerAge, playerGender,
                    coins, startTime, 0L,
                    adminMode, caveSceneShown, false, false,
                    "resources/World2.tmx");
            world.setAntingAntingCount(antingAntingCount);

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