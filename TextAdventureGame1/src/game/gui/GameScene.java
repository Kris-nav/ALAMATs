package game.gui;

import game.battle.BattleScreen;
import game.battle.Fighter;
import game.core.ProgressionManager;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

    // ── World 1 persistent state ───────────────────────────────────
    private boolean adminMode      = false;
    private boolean caveSceneShown = false;
    private boolean bossFightDone  = false;
    private boolean portalVisible  = false;

    // ── World 2 persistent state ───────────────────────────────────
    private boolean w2BossDone        = false;
    private boolean w2PortalVisible   = false;
    private boolean quest2Complete    = false;
    private boolean oldWomanCured     = false;
    private boolean peksonGaveAnting2 = false;
    private boolean anting2Active     = false;
    private double  expMultiplier     = 1.0;
    private boolean peksonTalked      = false;
    private boolean treasureFound     = false;
    private boolean hasMap            = false;
    private boolean quest2Triggered   = false;
    private int     superLunasCount   = 0;
    private int     superPotionCount  = 0;
    private int     superScrollCount  = 0;

    // ── World 3 persistent state ───────────────────────────────────
    private boolean w3Quest3Triggered = false;
    private boolean w3Coin1Found      = false;
    private boolean w3Coin2Found      = false;
    private boolean w3Coin3Found      = false;
    private boolean w3Coin4Found      = false;
    private boolean w3Coin5Found      = false;
    private boolean w3Quest3Complete  = false;
    private boolean w3BossDone        = false;

    private Clip bgmClip = null;

    // ── Reference to current WorldPanel for grandpa/credits calls ─
    private WorldPanel currentWorldPanel = null;

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

    public void syncWorld2State(boolean w2BossDone, boolean w2PortalVisible,
                                boolean quest2Complete, boolean oldWomanCured,
                                boolean peksonGaveAnting2, boolean anting2Active,
                                double expMultiplier, boolean peksonTalked,
                                boolean treasureFound, boolean hasMap,
                                boolean quest2Triggered,
                                int superLunas, int superPotion, int superScroll) {
        this.w2BossDone        = w2BossDone;
        this.w2PortalVisible   = w2PortalVisible;
        this.quest2Complete    = quest2Complete;
        this.oldWomanCured     = oldWomanCured;
        this.peksonGaveAnting2 = peksonGaveAnting2;
        this.anting2Active     = anting2Active;
        this.expMultiplier     = expMultiplier;
        this.peksonTalked      = peksonTalked;
        this.treasureFound     = treasureFound;
        this.hasMap            = hasMap;
        this.quest2Triggered   = quest2Triggered;
        this.superLunasCount   = superLunas;
        this.superPotionCount  = superPotion;
        this.superScrollCount  = superScroll;
    }

    public void syncWorld3State(boolean w3Quest3Triggered,
                                boolean w3Coin1Found, boolean w3Coin2Found,
                                boolean w3Coin3Found, boolean w3Coin4Found,
                                boolean w3Coin5Found,
                                boolean w3Quest3Complete, boolean w3BossDone,
                                boolean hasMap,
                                int superLunas, int superPotion, int superScroll) {
        this.w3Quest3Triggered = w3Quest3Triggered;
        this.w3Coin1Found      = w3Coin1Found;
        this.w3Coin2Found      = w3Coin2Found;
        this.w3Coin3Found      = w3Coin3Found;
        this.w3Coin4Found      = w3Coin4Found;
        this.w3Coin5Found      = w3Coin5Found;
        this.w3Quest3Complete  = w3Quest3Complete;
        this.w3BossDone        = w3BossDone;
        this.hasMap            = hasMap;
        this.superLunasCount   = superLunas;
        this.superPotionCount  = superPotion;
        this.superScrollCount  = superScroll;
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

    // ══════════════════════════════════════════════════════════════
    // MUSIC
    // ══════════════════════════════════════════════════════════════

    public void playMusic(String path) {
        try {
            if (bgmClip != null) { bgmClip.stop(); bgmClip.close(); }
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File(path));
            bgmClip = AudioSystem.getClip();
            bgmClip.open(audio);
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            FloatControl gain = (FloatControl) bgmClip.getControl(FloatControl.Type.MASTER_GAIN);
            gain.setValue(-10.0f);
            bgmClip.start();
        } catch (Exception e) { System.err.println("Music error: " + e.getMessage()); }
    }

    public void stopMusic() {
        if (bgmClip != null) { bgmClip.stop(); bgmClip.close(); bgmClip = null; }
    }

    // ══════════════════════════════════════════════════════════════
    // SWITCH TO WORLD (initial entry)
    // ══════════════════════════════════════════════════════════════

    public void switchToWorld() {
        SwingUtilities.invokeLater(() -> {
            this.getContentPane().removeAll();
            this.setLayout(new BorderLayout());
            WorldPanel world = new WorldPanel(this, "?", 0, "?");
            currentWorldPanel = world;
            this.add(world, BorderLayout.CENTER);
            this.pack();
            this.setLocationRelativeTo(null);
            this.revalidate();
            this.repaint();
            SwingUtilities.invokeLater(() -> {
                world.requestFocusInWindow();
                playMusic("resources/music/eterna.wav");
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
                    currentWorldPanel = newWorld;
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

    // ══════════════════════════════════════════════════════════════
    // RETURN TO WORLD AFTER BATTLE
    // ══════════════════════════════════════════════════════════════

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
            currentWorldPanel = world;
            this.add(world, BorderLayout.CENTER);
            this.pack();
            this.setLocationRelativeTo(null);
            this.revalidate();
            this.repaint();
            SwingUtilities.invokeLater(() -> {
                world.requestFocusInWindow();
                playMusic("resources/music/eterna.wav");
                world.start();
            });
        });
    }

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

            if (mapPath.contains("World2")) {
                WorldPanel world = new WorldPanel(
                        this, x, y, team, worldFighter,
                        scrollCount, lunasCount, potionCount,
                        playerName, playerAge, playerGender,
                        playerCoins, gameStartTime, cooldownUntil,
                        adminMode, caveSceneShown, bossFightDone, portalVisible,
                        mapPath, false);
                world.setAntingAntingCount(antingAntingCount);
                world.restoreWorld2State(
                        w2BossDone, w2PortalVisible,
                        quest2Complete, oldWomanCured,
                        peksonGaveAnting2, anting2Active,
                        expMultiplier, peksonTalked,
                        treasureFound, hasMap,
                        quest2Triggered,
                        superLunasCount, superPotionCount, superScrollCount);
                currentWorldPanel = world;
                this.add(world, BorderLayout.CENTER);
                this.pack(); this.setLocationRelativeTo(null);
                this.revalidate(); this.repaint();
                SwingUtilities.invokeLater(() -> {
                    world.requestFocusInWindow();
                    playMusic("resources/music/eterna.wav");
                    world.start();
                });

            } else if (mapPath.contains("World3")) {
                WorldPanel world = new WorldPanel(
                        this, x, y, team, worldFighter,
                        scrollCount, lunasCount, potionCount,
                        playerName, playerAge, playerGender,
                        playerCoins, gameStartTime, cooldownUntil,
                        adminMode, caveSceneShown, bossFightDone, portalVisible,
                        mapPath, false);
                world.setAntingAntingCount(antingAntingCount);
                world.restoreWorld3State(
                        w3Quest3Triggered,
                        w3Coin1Found, w3Coin2Found, w3Coin3Found,
                        w3Coin4Found, w3Coin5Found,
                        w3Quest3Complete, w3BossDone,
                        hasMap,
                        superLunasCount, superPotionCount, superScrollCount);
                currentWorldPanel = world;
                this.add(world, BorderLayout.CENTER);
                this.pack(); this.setLocationRelativeTo(null);
                this.revalidate(); this.repaint();
                SwingUtilities.invokeLater(() -> {
                    world.requestFocusInWindow();
                    playMusic("resources/music/eterna.wav");
                    world.start();
                });

            } else {
                WorldPanel world = new WorldPanel(
                        this, x, y, team, worldFighter,
                        scrollCount, lunasCount, potionCount,
                        playerName, playerAge, playerGender,
                        playerCoins, gameStartTime, cooldownUntil,
                        adminMode, caveSceneShown, bossFightDone, portalVisible,
                        mapPath);
                world.setAntingAntingCount(antingAntingCount);
                currentWorldPanel = world;
                this.add(world, BorderLayout.CENTER);
                this.pack(); this.setLocationRelativeTo(null);
                this.revalidate(); this.repaint();
                SwingUtilities.invokeLater(() -> {
                    world.requestFocusInWindow();
                    playMusic("resources/music/eterna.wav");
                    world.start();
                });
            }
        });
    }

    // ══════════════════════════════════════════════════════════════
    // MAX FIGHTER HELPER
    // ══════════════════════════════════════════════════════════════

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

    // ══════════════════════════════════════════════════════════════
    // WILD BATTLE
    // ══════════════════════════════════════════════════════════════

    public void switchToBattle(Fighter playerFighter,
                               Fighter wildFighter,
                               ArrayList<Fighter> team,
                               int scrollCount, int lunasCount, int potionCount,
                               int savedX, int savedY) {
        this.switchToBattleOnMap(playerFighter, wildFighter, team,
                scrollCount, lunasCount, potionCount,
                savedX, savedY, "resources/World1.tmx");
    }

    public void switchToBattleOnMap(Fighter playerFighter,
                                    Fighter wildFighter,
                                    ArrayList<Fighter> team,
                                    int scrollCount, int lunasCount, int potionCount,
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
            playMusic("resources/music/red.wav");
            this.revalidate();
            this.repaint();
        });
    }

    // ══════════════════════════════════════════════════════════════
    // BOSS BATTLE
    // ══════════════════════════════════════════════════════════════

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

                    // Win callback
                    (updatedFighter, updatedTeam, updatedScrolls,
                     updatedLunas, updatedPotions, blackout) -> {
                        long cooldown = System.currentTimeMillis() + 5000L;
                        playerCoins += 900;
                        antingAntingCount = Math.min(4, antingAntingCount + 1);
                        int bossExp = 200;
                        updatedFighter.gainExp(bossExp);
                        for (Fighter f : updatedTeam) f.gainExp(bossExp);

                        if (blackout) {
                            if (returnMapPath.contains("World2") || returnMapPath.contains("World3")) {
                                switchToWorldAtMap(SPAWN_X, SPAWN_Y, updatedTeam, updatedFighter,
                                        updatedScrolls, updatedLunas, updatedPotions, playerCoins, 0L, returnMapPath);
                            } else {
                                switchToWorldAt(SPAWN_X, SPAWN_Y, updatedTeam, updatedFighter,
                                        updatedScrolls, updatedLunas, updatedPotions, playerCoins, 0L);
                            }
                        } else {
                            if (returnMapPath.contains("World2")) {
                                w2BossDone = true; w2PortalVisible = true;
                                if (onBossDefeated != null) onBossDefeated.run();
                                switchToWorldAtMap(savedX, savedY, updatedTeam, updatedFighter,
                                        updatedScrolls, updatedLunas, updatedPotions, playerCoins, cooldown, returnMapPath);
                            } else if (returnMapPath.contains("World3")) {
                                w3BossDone = true;
                                if (onBossDefeated != null) onBossDefeated.run();
                                switchToWorldAtMap(savedX, savedY, updatedTeam, updatedFighter,
                                        updatedScrolls, updatedLunas, updatedPotions, playerCoins, cooldown, returnMapPath);
                            } else {
                                bossFightDone = true; portalVisible = true;
                                if (onBossDefeated != null) onBossDefeated.run();
                                switchToWorldAt(savedX, savedY, updatedTeam, updatedFighter,
                                        updatedScrolls, updatedLunas, updatedPotions, playerCoins, cooldown);
                            }
                        }
                    },

                    // Run callback
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

                    true, "The Witch Doctor", bossTeamRest
            );

            this.add(battle, BorderLayout.CENTER);
            this.pack();
            this.setLocationRelativeTo(null);
            playMusic("resources/music/red.wav");
            this.revalidate();
            this.repaint();
        });
    }

    // ══════════════════════════════════════════════════════════════
    // KHAIBALANG BOSS BATTLE  (single definition)
    // ══════════════════════════════════════════════════════════════

    public void switchToKhaiBoss(Fighter playerFighter,
                                 Fighter khaibalang,
                                 ArrayList<Fighter> team,
                                 int scrollCount, int lunasCount, int potionCount,
                                 int savedX, int savedY,
                                 Runnable onDefeated,
                                 String mapPath,
                                 String pName) {
        if (starterFighter == null) starterFighter = playerFighter;

        SwingUtilities.invokeLater(() -> {
            this.getContentPane().removeAll();
            this.setLayout(new BorderLayout());

            BattleScreen battle = new BattleScreen(
                    playerFighter, khaibalang, team,
                    scrollCount, lunasCount, potionCount,

                    // Win
                    (updatedFighter, updatedTeam, updatedScrolls,
                     updatedLunas, updatedPotions, blackout) -> {
                        if (blackout) {
                            switchToKhaiRespawn(updatedFighter, khaibalang,
                                    updatedTeam, updatedScrolls, updatedLunas,
                                    updatedPotions, mapPath, pName);
                        } else {
                            playerCoins += 1000;
                            antingAntingCount = Math.min(4, antingAntingCount + 1);
                            if (onDefeated != null) onDefeated.run();
                            switchToWorldAtMap(savedX, savedY, updatedTeam, updatedFighter,
                                    updatedScrolls, updatedLunas, updatedPotions,
                                    playerCoins, 0L, mapPath);
                            new Timer(1500, e -> {
                                ((Timer) e.getSource()).stop();
                                showGrandpaDialog(pName);
                            }).start();
                        }
                    },

                    // Lose / blackout — respawn at pond
                    (updatedFighter, updatedTeam, updatedScrolls,
                     updatedLunas, updatedPotions, blackout) -> {
                        switchToKhaiRespawn(updatedFighter, khaibalang,
                                updatedTeam, updatedScrolls, updatedLunas,
                                updatedPotions, mapPath, pName);
                    },

                    true, "KHAIBALANG", new ArrayList<>()
            );

            this.add(battle, BorderLayout.CENTER);
            this.pack();
            this.setLocationRelativeTo(null);
            playMusic("resources/music/red.wav");
            this.revalidate();
            this.repaint();
        });
    }

    // ══════════════════════════════════════════════════════════════
    // KHAIBALANG RESPAWN  (single definition)
    // ══════════════════════════════════════════════════════════════

    private void switchToKhaiRespawn(Fighter playerFighter,
                                     Fighter khaibalang,
                                     ArrayList<Fighter> team,
                                     int scrolls, int lunas, int potions,
                                     String mapPath, String pName) {
        // Heal all creatures
        healFighter(playerFighter);
        for (Fighter f : team) healFighter(f);
        // Reset Khaibalang for rematch
        khaibalang.stats.get(0).value = khaibalang.stats.get(0).base;
        khaibalang.fainted = false;
        for (game.battle.Move m : khaibalang.moveset) m.pp = m.maxPp;

        // Pond spawn coords in World 3
        int pondX = 24 * (16 * 9);
        int pondY = 38 * (16 * 9);

        Fighter worldFighter = (starterFighter != null) ? starterFighter : playerFighter;
        team.remove(worldFighter);

        SwingUtilities.invokeLater(() -> {
            this.getContentPane().removeAll();
            this.setLayout(new BorderLayout());

            WorldPanel world = new WorldPanel(
                    this, pondX, pondY, team, worldFighter,
                    scrolls, lunas, potions,
                    playerName, playerAge, playerGender,
                    playerCoins, gameStartTime, 0L,
                    adminMode, caveSceneShown, bossFightDone, portalVisible,
                    mapPath, false);
            world.setAntingAntingCount(antingAntingCount);
            world.restoreWorld3State(
                    w3Quest3Triggered,
                    w3Coin1Found, w3Coin2Found, w3Coin3Found,
                    w3Coin4Found, w3Coin5Found,
                    w3Quest3Complete, true,
                    hasMap,
                    superLunasCount, superPotionCount, superScrollCount);
            world.setPendingKhaibalang(khaibalang, pName);
            currentWorldPanel = world;
            this.add(world, BorderLayout.CENTER);
            this.pack();
            this.setLocationRelativeTo(null);
            this.revalidate();
            this.repaint();
            SwingUtilities.invokeLater(() -> {
                world.requestFocusInWindow();
                playMusic("resources/music/eterna.wav");
                world.start();
                new Timer(900, e -> {
                    ((Timer) e.getSource()).stop();
                    world.showFloatingMessagePublic(
                            "You were defeated! Train more and challenge KHAIBALANG again!",
                            new Color(255, 100, 100));
                }).start();
            });
        });
    }

    private void healFighter(Fighter f) {
        f.stats.get(0).value = f.stats.get(0).base;
        f.fainted = false;
        for (game.battle.Move m : f.moveset) { if (!m.isLocked()) m.pp = m.maxPp; }
    }

    // ══════════════════════════════════════════════════════════════
    // GRANDPA DIALOG  (single definition)
    // ══════════════════════════════════════════════════════════════

    public void showGrandpaDialog(String pName) {
        if (currentWorldPanel != null) {
            currentWorldPanel.showGrandpaEndScene(pName);
        }
    }

    // ══════════════════════════════════════════════════════════════
    // CREDITS SCENE  (single definition) - FIXED VERSION
    // ══════════════════════════════════════════════════════════════

    public void showCredits(String pName) {
        SwingUtilities.invokeLater(() -> {
            this.getContentPane().removeAll();
            this.setLayout(new BorderLayout());

            String[] credits = {
                    "~ THE END ~", "",
                    pName + " returned to the city with Sir Khai.",
                    "The creatures were set free to roam the land.",
                    "Grandpa continued his life as the village Albularyo.",
                    "", "~ ALAMAT ~", "",
                    "DEVELOPED BY:",
                    "Project Manager: Kristoferson S. Navarro",
                    "Front End / Designer: Kristoferson S. Navarro",
                    "Backend: Kristoferson S. Navarro, John Joshua Montebon",
                    "",
                    "SPECIAL THANKS:",
                    "Sir Kenn Migan Vincent Gumonan A.K.A \"Sir KHAI\"",
                    "Claude AI",
                    "Deepseek",
                    "Github",
                    "Youtube",
                    "PUNY_MYTH-CREATURES",
                    "",
                    "Thank you for playing ALAMAT!"
            };

            JPanel creditsPanel = new JPanel(null) {
                private int scrollY = 820;
                private Timer scrollTimer;

                @Override
                public void addNotify() {
                    super.addNotify();
                    scrollTimer = new Timer(16, e -> {
                        scrollY -= 2;
                        repaint();
                        if (scrollY < -credits.length * 55) {
                            scrollTimer.stop();
                        }
                    });
                    scrollTimer.start();
                }

                @Override
                public void removeNotify() {
                    if (scrollTimer != null) {
                        scrollTimer.stop();
                    }
                    super.removeNotify();
                }

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.BLACK);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    int y = scrollY;
                    for (String line : credits) {
                        if (line.isEmpty()) { y += 20; continue; }
                        if (line.equals("~ THE END ~") || line.equals("~ ALAMAT ~")) {
                            g2.setColor(new Color(255, 215, 90));
                            g2.setFont(new Font("Monospaced", Font.BOLD, 30));
                        } else if (line.equals("DEVELOPED BY:") || line.equals("SPECIAL THANKS:")) {
                            g2.setColor(new Color(180, 180, 255));
                            g2.setFont(new Font("Monospaced", Font.BOLD, 18));
                        } else if (line.startsWith("Project Manager:") || line.startsWith("Front End:") || line.startsWith("Backend:")) {
                            g2.setColor(new Color(150, 220, 150));
                            g2.setFont(new Font("Monospaced", Font.PLAIN, 14));
                        } else if (line.contains("Sir KHAI") || line.equals("Claude AI") || line.equals("Deepseek") || line.equals("Github") || line.equals("PUNY_MYTH-CREATURES")) {
                            g2.setColor(new Color(255, 200, 100));
                            g2.setFont(new Font("Monospaced", Font.PLAIN, 14));
                        } else {
                            g2.setColor(new Color(220, 220, 220));
                            g2.setFont(new Font("Monospaced", Font.PLAIN, 16));
                        }
                        FontMetrics fm = g2.getFontMetrics();
                        g2.drawString(line, (getWidth() - fm.stringWidth(line)) / 2, y);
                        y += 55;
                    }
                }
            };
            creditsPanel.setBackground(Color.BLACK);
            creditsPanel.setPreferredSize(new Dimension(W, H));
            stopMusic();
            currentWorldPanel = null;
            this.add(creditsPanel, BorderLayout.CENTER);
            this.pack();
            this.setLocationRelativeTo(null);
            this.revalidate();
            this.repaint();

            // AFTER 10 SECONDS, RETURN TO START SCREEN
            new Timer(10000, e -> {
                ((Timer) e.getSource()).stop();
                returnToStartScreen();
            }).start();
        });
    }
    // ══════════════════════════════════════════════════════════════
    // RETURN TO START SCREEN
    // ══════════════════════════════════════════════════════════════
    private void returnToStartScreen() {
        SwingUtilities.invokeLater(() -> {
            this.getContentPane().removeAll();
            this.setLayout(new BorderLayout());

            JPanel startPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(8, 6, 16));
                    g2.fillRect(0, 0, getWidth(), getHeight());

                    g2.setColor(new Color(255, 215, 90));
                    g2.setFont(new Font("Monospaced", Font.BOLD, 52));
                    FontMetrics fm = g2.getFontMetrics();
                    String title = "ALAMAT";
                    g2.drawString(title, (getWidth() - fm.stringWidth(title)) / 2, getHeight() / 2 - 60);

                    g2.setColor(new Color(180, 150, 80));
                    g2.setFont(new Font("Monospaced", Font.PLAIN, 20));
                    fm = g2.getFontMetrics();
                    String subtitle = "A Filipino Folklore Adventure";
                    g2.drawString(subtitle, (getWidth() - fm.stringWidth(subtitle)) / 2, getHeight() / 2 - 10);

                    g2.setColor(new Color(100, 200, 100));
                    g2.setFont(new Font("Monospaced", Font.BOLD, 18));
                    fm = g2.getFontMetrics();
                    String playText = "Click anywhere to start your journey!";
                    g2.drawString(playText, (getWidth() - fm.stringWidth(playText)) / 2, getHeight() / 2 + 60);

                    long time = System.currentTimeMillis();
                    if ((time / 500) % 2 == 0) {
                        g2.setColor(new Color(255, 100, 100));
                        String arrow = "▼";
                        fm = g2.getFontMetrics();
                        g2.drawString(arrow, (getWidth() - fm.stringWidth(arrow)) / 2, getHeight() / 2 + 110);
                    }
                }
            };
            startPanel.setPreferredSize(new Dimension(W, H));
            startPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    switchToWorld();
                }
            });

            this.add(startPanel, BorderLayout.CENTER);
            this.pack();
            this.setLocationRelativeTo(null);
            this.revalidate();
            this.repaint();
        });
    }

    // ══════════════════════════════════════════════════════════════
    // WORLD 2 ENTRY
    // ══════════════════════════════════════════════════════════════

    public void switchToWorld2(Fighter playerFighter,
                               ArrayList<Fighter> team,
                               int scrollCount, int lunasCount, int potionCount,
                               int coins, long startTime,
                               boolean adminMode, boolean caveSceneShown,
                               int antingAntingCount) {
        Fighter worldFighter = (starterFighter != null) ? starterFighter : playerFighter;
        team.remove(worldFighter);
        if (adminMode) { for (Fighter f : team) maxFighterIfNeeded(f); maxFighterIfNeeded(worldFighter); }

        this.playerCoins = coins; this.antingAntingCount = antingAntingCount;
        this.caveSceneShown = caveSceneShown; this.adminMode = adminMode;
        this.gameStartTime = startTime;
        this.bossFightDone = false; this.portalVisible = false;
        this.w2BossDone = false; this.w2PortalVisible = false;
        this.quest2Complete = false; this.oldWomanCured = false;
        this.peksonGaveAnting2 = false; this.anting2Active = false;
        this.expMultiplier = 1.0; this.peksonTalked = false;
        this.treasureFound = false; this.hasMap = false;
        this.quest2Triggered = false;
        this.superLunasCount = 0; this.superPotionCount = 0; this.superScrollCount = 0;

        SwingUtilities.invokeLater(() -> {
            this.getContentPane().removeAll();
            this.setLayout(new BorderLayout());
            WorldPanel world = new WorldPanel(
                    this, 4205, 5125, team, worldFighter,
                    scrollCount, lunasCount, potionCount,
                    playerName, playerAge, playerGender,
                    coins, startTime, 0L,
                    adminMode, caveSceneShown, false, false,
                    "resources/World2.tmx", true);
            world.setAntingAntingCount(antingAntingCount);
            currentWorldPanel = world;
            this.add(world, BorderLayout.CENTER);
            this.pack(); this.setLocationRelativeTo(null);
            this.revalidate(); this.repaint();
            SwingUtilities.invokeLater(() -> {
                world.requestFocusInWindow();
                playMusic("resources/music/eterna.wav");
                world.start();
            });
        });
    }

    // ══════════════════════════════════════════════════════════════
    // WORLD 3 ENTRY
    // ══════════════════════════════════════════════════════════════

    public void switchToWorld3(Fighter playerFighter,
                               ArrayList<Fighter> team,
                               int scrollCount, int lunasCount, int potionCount,
                               int coins, long startTime,
                               boolean adminMode, boolean caveSceneShown,
                               int antingAntingCount) {
        Fighter worldFighter = (starterFighter != null) ? starterFighter : playerFighter;
        team.remove(worldFighter);
        if (adminMode) { for (Fighter f : team) maxFighterIfNeeded(f); maxFighterIfNeeded(worldFighter); }

        this.playerCoins = coins; this.antingAntingCount = antingAntingCount;
        this.caveSceneShown = caveSceneShown; this.adminMode = adminMode;
        this.gameStartTime = startTime;
        this.w3Quest3Triggered = false; this.w3Coin1Found = false;
        this.w3Coin2Found = false; this.w3Coin3Found = false;
        this.w3Coin4Found = false; this.w3Coin5Found = false;
        this.w3Quest3Complete = false; this.w3BossDone = false;
        this.hasMap = true;

        SwingUtilities.invokeLater(() -> {
            this.getContentPane().removeAll();
            this.setLayout(new BorderLayout());
            WorldPanel world = new WorldPanel(
                    this, 4205, 5125, team, worldFighter,
                    scrollCount, lunasCount, potionCount,
                    playerName, playerAge, playerGender,
                    coins, startTime, 0L,
                    adminMode, caveSceneShown, false, false,
                    "resources/World3.tmx", true);
            world.setAntingAntingCount(antingAntingCount);
            world.restoreWorld3State(
                    false, false, false, false, false, false,
                    false, false, hasMap,
                    superLunasCount, superPotionCount, superScrollCount);
            currentWorldPanel = world;
            this.add(world, BorderLayout.CENTER);
            this.pack(); this.setLocationRelativeTo(null);
            this.revalidate(); this.repaint();
            SwingUtilities.invokeLater(() -> {
                world.requestFocusInWindow();
                playMusic("resources/music/eterna.wav");
                world.start();
            });
        });
    }

    // ══════════════════════════════════════════════════════════════
    // CHEAT COMMANDS — called from WorldPanel.executeCommand()
    // ══════════════════════════════════════════════════════════════

    public void cheatSkipTown3Boss(WorldPanel world) {
        w3Quest3Complete = true;
        w3BossDone       = true;
        w3Coin1Found = w3Coin2Found = w3Coin3Found =
                w3Coin4Found = w3Coin5Found = true;
        world.syncStateToGameScenePublic();
        world.showFloatingMessagePublic(
                "Skiptown3boss — Albularyo skipped! Khaibalang awaits...",
                new Color(180, 130, 255));
        new Timer(1800, e -> {
            ((Timer) e.getSource()).stop();
            world.showKhaiDialogPublic();
        }).start();
    }

    public void cheatSkipKhaibalang(WorldPanel world) {
        antingAntingCount = 4;
        w3BossDone        = true;
        world.syncStateToGameScenePublic();
        world.showFloatingMessagePublic(
                "SkipKhaibalang — Teleporting to final choice...",
                new Color(100, 255, 200));
        new Timer(1800, e -> {
            ((Timer) e.getSource()).stop();
            Fighter wf = (starterFighter != null) ? starterFighter : world.getPlayerFighter();
            ArrayList<Fighter> team = new ArrayList<>(world.getCapturedTeam());
            switchToWorldAt(SPAWN_X, SPAWN_Y, team, wf,
                    world.getScrollCount(), world.getLunasCount(), world.getPotionCount(),
                    playerCoins, 0L);
            new Timer(1200, e2 -> {
                ((Timer) e2.getSource()).stop();
                if (currentWorldPanel != null) {
                    currentWorldPanel.showFinalChoicePublic(playerName);
                }
            }).start();
        }).start();
    }

    // ══════════════════════════════════════════════════════════════
    // MISC
    // ══════════════════════════════════════════════════════════════

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