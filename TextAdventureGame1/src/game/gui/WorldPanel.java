package game.gui;

import game.battle.Create;
import game.battle.Fighter;
import game.battle.Move;
import game.battle.Stat;
import game.battle.Type;
import game.core.KeyHandler;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.awt.Color;
import javax.swing.Timer;


public class WorldPanel extends JPanel implements Runnable {
    private Thread gameThread;
    private KeyHandler keyH = new KeyHandler();
    private ArrayList<Fighter> capturedTeam;
    private Fighter playerFighter;
    private int scrollCount;
    private int lunasCount;
    private int potionCount;
    private String playerName        = "";
    private int    playerAge         = 0;
    private String playerGender      = "";
    private int    antingAntingCount = 0;
    private int playerCoins = 500;
    private static final Random coinRand = new Random();
    private long    gameStartTime = System.currentTimeMillis();
    private boolean timerRunning  = true;
    private final int TILE_SIZE         = 16;
    private final int SCALE             = 9;
    private final int TILE_DISPLAY_SIZE = TILE_SIZE * SCALE;
    private int playerX;
    private int playerY;
    private final int PLAYER_SPEED  = 4;
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
    private boolean inBattle              = false;
    private long    encounterCooldownUntil;
    private boolean showBag       = false;
    private boolean showShop      = false;
    private boolean healTriggered = false;
    private int    selectedIndex         = -1;
    private int    selectedCreatureIndex = -1;
    private JPanel bagOverlay            = null;
    private JPanel shopOverlay           = null;
    private boolean showWelcome  = false;
    private JPanel  welcomePanel = null;
    private boolean adminMode      = false;
    private boolean caveSceneShown = false;
    private boolean bossFightDone  = false;
    private boolean portalVisible  = false;
    private boolean showCaveScene = false;
    private JPanel  cavePanel     = null;
    private boolean bossTriggered = false;
    private int     questCreaturesCaptured = 1;
    private boolean questComplete          = false;
    private String  hudCodeInput   = "";
    private boolean hudCodeFocused = false;
    private int[][][] allLayerData;
    private int mapWidth;
    private int mapHeight;
    private HashMap<Integer, BufferedImage> tileCache = new HashMap<>();
    private String  pendingItemUse = "";
    private Fighter pendingTarget  = null;
    private int lastDebugTileId = -1;
    private String currentMapPath  = "resources/World1.tmx";
    private String currentTownName = "TOWN 1 — USA Village";

    // ── World 2 feature fields ─────────────────────────────────────
    private boolean treasureFound      = false;
    private boolean hasMap             = false;
    private boolean showMapOverlay     = false;
    private JPanel  mapOverlayPanel    = null;
    private boolean peksonTalked       = false;
    private boolean peksonGaveAnting2  = false;
    private boolean anting2Active      = false;
    private double  expMultiplier      = 1.0;
    private boolean oldWomanCured      = false;
    private boolean quest2Triggered    = false;
    private boolean quest2Complete     = false;
    private boolean w2HealTriggered    = false;
    private boolean w2ShopOpen         = false;
    private String  hoverMessage       = "";
    private long    hoverMessageUntil  = 0;

    // Super items (World 2 shop)
    private int superLunasCount  = 0;
    private int superPotionCount = 0;
    private int superScrollCount = 0;

    // ── World 2 boss and portal state ─────────────────────────────
    private boolean w2BossTriggered    = false;
    private boolean w2BossDone         = false;
    private boolean w2PortalVisible    = false;

    // NPC dialog open guards
    private boolean peksonDialogOpen   = false;
    private boolean oldWomanDialogOpen = false;
    private String  lastNpcTileKey     = "";

    // ── World 3 persistent state ───────────────────────────────────
    private boolean w3HealTriggered    = false;
    private boolean w3Quest3Triggered  = false;
    private boolean w3Coin1Found       = false;
    private boolean w3Coin2Found       = false;
    private boolean w3Coin3Found       = false;
    private boolean w3Coin4Found       = false;
    private boolean w3Coin5Found       = false;
    private boolean w3Quest3Complete   = false;
    private boolean w3BossDone        = false;
    private boolean khaibalangDefeated = false;

    // ══════════════════════════════════════════════════════════════
    // CONSTRUCTORS
    // ══════════════════════════════════════════════════════════════

    public WorldPanel(GameScene gameScene,
                      String playerName, int playerAge, String playerGender) {
        this(gameScene, 4205, 5125,
                new ArrayList<>(),
                Create.createPlayerStarter(),
                3, 3, 3,
                playerName, playerAge, playerGender,
                500, System.currentTimeMillis(), 0L,
                false, false, false, false,
                "resources/World1.tmx",
                false);
    }

    public WorldPanel(GameScene gameScene,
                      int startX, int startY,
                      ArrayList<Fighter> team,
                      Fighter playerFighter,
                      int scrollCount, int lunasCount, int potionCount,
                      String playerName, int playerAge, String playerGender,
                      int playerCoins, long gameStartTime, long cooldownUntil) {
        this(gameScene, startX, startY, team, playerFighter,
                scrollCount, lunasCount, potionCount,
                playerName, playerAge, playerGender,
                playerCoins, gameStartTime, cooldownUntil,
                false, false, false, false,
                "resources/World1.tmx",
                false);
    }

    public WorldPanel(GameScene gameScene,
                      int startX, int startY,
                      ArrayList<Fighter> team,
                      Fighter playerFighter,
                      int scrollCount, int lunasCount, int potionCount,
                      String playerName, int playerAge, String playerGender,
                      int playerCoins, long gameStartTime, long cooldownUntil,
                      boolean adminMode, boolean caveSceneShown,
                      boolean bossFightDone, boolean portalVisible) {
        this(gameScene, startX, startY, team, playerFighter,
                scrollCount, lunasCount, potionCount,
                playerName, playerAge, playerGender,
                playerCoins, gameStartTime, cooldownUntil,
                adminMode, caveSceneShown, bossFightDone, portalVisible,
                "resources/World1.tmx",
                false);
    }

    public WorldPanel(GameScene gameScene,
                      int startX, int startY,
                      ArrayList<Fighter> team,
                      Fighter playerFighter,
                      int scrollCount, int lunasCount, int potionCount,
                      String playerName, int playerAge, String playerGender,
                      int playerCoins, long gameStartTime, long cooldownUntil,
                      boolean adminMode, boolean caveSceneShown,
                      boolean bossFightDone, boolean portalVisible,
                      String mapPath) {
        this(gameScene, startX, startY, team, playerFighter,
                scrollCount, lunasCount, potionCount,
                playerName, playerAge, playerGender,
                playerCoins, gameStartTime, cooldownUntil,
                adminMode, caveSceneShown, bossFightDone, portalVisible,
                mapPath,
                false);
    }

    // ── MASTER CONSTRUCTOR ─────────────────────────────────────────
    public WorldPanel(GameScene gameScene,
                      int startX, int startY,
                      ArrayList<Fighter> team,
                      Fighter playerFighter,
                      int scrollCount, int lunasCount, int potionCount,
                      String playerName, int playerAge, String playerGender,
                      int playerCoins, long gameStartTime, long cooldownUntil,
                      boolean adminMode, boolean caveSceneShown,
                      boolean bossFightDone, boolean portalVisible,
                      String mapPath,
                      boolean isFirstEntry) {
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
        this.playerCoins            = playerCoins;
        this.gameStartTime          = gameStartTime;
        this.timerRunning           = true;
        this.encounterCooldownUntil = cooldownUntil;
        this.questCreaturesCaptured = 1 + team.size();
        this.adminMode              = adminMode;
        this.caveSceneShown         = caveSceneShown;
        this.bossFightDone          = bossFightDone;
        this.portalVisible          = portalVisible;
        this.currentMapPath         = mapPath;

        if (mapPath.contains("World2")) {
            this.currentTownName = "TOWN 2 — Maghaway";
        } else if (mapPath.contains("World3")) {
            this.currentTownName = "TOWN 3 — Manolo Fortich";
        } else {
            this.currentTownName = "TOWN 1 — USA Village";
        }

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

        loadMap(mapPath);
        initSolidTiles();

        if (mapPath.contains("World2") && isFirstEntry) {
            SwingUtilities.invokeLater(this::showWorld2WelcomeMessage);
        }
    }
    public void showFloatingMessagePublic(String msg, Color color) {
        showFloatingMessage(msg, color);
    }
    public void syncStateToGameScenePublic() {
        syncStateToGameScene();
    }
    public void showKhaiDialogPublic() {
        showKhaiDialog();
    }

    public void showFinalChoicePublic(String pName) {
        showFinalChoice(pName);
    }

    public Fighter getPlayerFighter() {
        return playerFighter;
    }

    public ArrayList<Fighter> getCapturedTeam() {
        return capturedTeam;
    }

    public int getScrollCount()  { return scrollCount; }
    public int getLunasCount()   { return lunasCount; }
    public int getPotionCount()  { return potionCount; }

    public int  getPlayerCoins()                { return playerCoins; }
    public void addCoins(int amount)            { playerCoins += amount; }
    public void setAntingAntingCount(int count) { this.antingAntingCount = count; }

    // ══════════════════════════════════════════════════════════════
    // WORLD 2 STATE RESTORE
    // ══════════════════════════════════════════════════════════════
    public void restoreWorld2State(boolean w2BossDone, boolean w2PortalVisible,
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

    // ══════════════════════════════════════════════════════════════
    // WORLD 3 STATE RESTORE
    // ══════════════════════════════════════════════════════════════
    public void restoreWorld3State(boolean w3Quest3Triggered,
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

    // ══════════════════════════════════════════════════════════════
    // STATE SYNC TO GAMESCENE
    // ══════════════════════════════════════════════════════════════
    private void syncStateToGameScene() {
        gameScene.syncPersistentState(adminMode, caveSceneShown, bossFightDone, portalVisible);
        if (isWorld2()) {
            gameScene.syncWorld2State(
                    w2BossDone, w2PortalVisible,
                    quest2Complete, oldWomanCured,
                    peksonGaveAnting2, anting2Active,
                    expMultiplier, peksonTalked,
                    treasureFound, hasMap,
                    quest2Triggered,
                    superLunasCount, superPotionCount, superScrollCount);
        }
        if (isWorld3()) {
            gameScene.syncWorld3State(
                    w3Quest3Triggered,
                    w3Coin1Found, w3Coin2Found, w3Coin3Found,
                    w3Coin4Found, w3Coin5Found,
                    w3Quest3Complete, w3BossDone,
                    hasMap,
                    superLunasCount, superPotionCount, superScrollCount);
        }
    }

    private boolean isWorld2() { return currentMapPath.contains("World2"); }
    private boolean isWorld3() { return currentMapPath.contains("World3"); }

    // ══════════════════════════════════════════════════════════════
    // WORLD 2 WELCOME MESSAGE
    // ══════════════════════════════════════════════════════════════
    private void showWorld2WelcomeMessage() {
        int pw = 800, ph = 160;
        int px = (1280 - pw) / 2, py = (720 - ph) / 2;
        JPanel overlay = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlay.setOpaque(false);
        overlay.setBounds(0, 0, 1280, 720);

        JPanel box = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(14, 10, 28));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(new Color(120, 80, 200));
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
            }
        };
        box.setOpaque(false);
        box.setBounds(px, py, pw, ph);

        JLabel l1 = new JLabel("Yes you have defeated the first albularyo", SwingConstants.CENTER);
        l1.setForeground(new Color(255, 200, 80));
        l1.setFont(new Font("Monospaced", Font.BOLD, 16));
        l1.setBounds(20, 18, pw - 40, 24);
        box.add(l1);

        JLabel l2 = new JLabel("but be careful the other albularyos are stronger and very evil", SwingConstants.CENTER);
        l2.setForeground(new Color(255, 140, 140));
        l2.setFont(new Font("Monospaced", Font.PLAIN, 14));
        l2.setBounds(20, 48, pw - 40, 22);
        box.add(l2);

        JButton btn = new JButton("Continue");
        btn.setBackground(new Color(60, 30, 100));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Monospaced", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(120, 80, 200), 2));
        btn.setBounds(pw / 2 - 80, ph - 52, 160, 36);
        btn.addActionListener(e -> {
            WorldPanel.this.remove(overlay);
            WorldPanel.this.revalidate();
            WorldPanel.this.repaint();
            requestFocusInWindow();
        });
        box.add(btn);
        overlay.add(box);
        this.add(overlay);
        this.setComponentZOrder(overlay, 0);
        this.revalidate();
        this.repaint();
    }

    // ══════════════════════════════════════════════════════════════
    // CREATURE POPUP
    // ══════════════════════════════════════════════════════════════
    public void showCreaturePopup(Runnable onLetsGo) {
        SwingUtilities.invokeLater(() -> {
            if (welcomePanel != null) this.remove(welcomePanel);
            int pw = 700, ph = 210;
            int px = (1280 - pw) / 2, py = (720 - ph) / 2;
            welcomePanel = new JPanel(null) {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(0, 0, 0, 180));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
            };
            welcomePanel.setOpaque(false);
            welcomePanel.setBounds(0, 0, 1280, 720);

            JPanel box = new JPanel(null) {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(18, 12, 5));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                    g2.setColor(new Color(170, 120, 50));
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                }
            };
            box.setOpaque(false);
            box.setBounds(px, py, pw, ph);

            JLabel l1 = wLabel("You have been left with a creature", new Color(255,215,90), 18, true);
            JLabel l2 = wLabel("of your Grandpa — a Santelmo!", Color.WHITE, 15, false);
            JLabel l3 = wLabel("Create your character to begin the adventure!", new Color(180,180,180), 12, false);
            JLabel l4 = wLabel("You start with 500 coins. Good luck!", new Color(100,200,255), 12, false);
            l1.setBounds(20,16,pw-40,26); l2.setBounds(20,50,pw-40,22);
            l3.setBounds(20,80,pw-40,20); l4.setBounds(20,106,pw-40,20);
            box.add(l1); box.add(l2); box.add(l3); box.add(l4);

            JButton btn = new JButton("LET'S GO!");
            btn.setBackground(new Color(40,130,55)); btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Monospaced",Font.BOLD,14)); btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(Color.WHITE,2));
            btn.setBounds(pw/2-100, ph-62, 200, 44);
            btn.addActionListener(e -> {
                if (welcomePanel!=null) { WorldPanel.this.remove(welcomePanel); welcomePanel=null; }
                showWelcome=false;
                WorldPanel.this.revalidate(); WorldPanel.this.repaint();
                if (onLetsGo!=null) onLetsGo.run();
            });
            box.add(btn);
            welcomePanel.add(box);
            this.add(welcomePanel);
            this.setComponentZOrder(welcomePanel, 0);
            showWelcome = true;
            this.revalidate(); this.repaint();
        });
    }

    private JLabel wLabel(String text, Color color, int size, boolean bold) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setForeground(color);
        lbl.setFont(new Font("Monospaced", bold ? Font.BOLD : Font.PLAIN, size));
        return lbl;
    }

    public void updateProfile(String name, int age, String gender, long startTime) {
        this.playerName=name; this.playerAge=age;
        this.playerGender=gender; this.gameStartTime=startTime;
    }

    // ══════════════════════════════════════════════════════════════
    // HUD
    // ══════════════════════════════════════════════════════════════
    private void drawHUD(Graphics2D g2) {
        int x=10, y=10, w=200, h=186;
        g2.setColor(new Color(0,0,0,180));
        g2.fillRoundRect(x,y,w,h,12,12);
        g2.setColor(new Color(170,120,50));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(x,y,w,h,12,12);
        int tx=x+10, ty=y+18, lineH=18;
        g2.setColor(new Color(255,215,90));
        g2.setFont(new Font("Monospaced",Font.BOLD,13));
        g2.drawString(playerName.isEmpty()?"...":playerName, tx, ty); ty+=lineH;
        g2.setColor(new Color(200,200,200));
        g2.setFont(new Font("Monospaced",Font.PLAIN,11));
        g2.drawString(playerAge==0?"...":"Age: "+playerAge+"  |  "+playerGender, tx, ty); ty+=lineH;
        divider(g2,tx,ty-4,x+w-10); ty+=4;
        g2.setColor(new Color(200,160,60));
        g2.setFont(new Font("Monospaced",Font.BOLD,11));
        g2.drawString("Anting-Anting:", tx, ty+4); ty+=lineH;
        for (int i=0;i<4;i++) {
            boolean has=(i<antingAntingCount);
            g2.setColor(has?new Color(255,200,50):new Color(60,50,30));
            g2.fillOval(tx+i*22,ty-12,16,16);
            g2.setColor(has?new Color(200,150,20):new Color(80,60,20));
            g2.setStroke(new BasicStroke(1));
            g2.drawOval(tx+i*22,ty-12,16,16);
        }
        g2.setColor(new Color(160,160,160));
        g2.setFont(new Font("Monospaced",Font.PLAIN,10));
        g2.drawString(antingAntingCount+"/4", tx+96, ty); ty+=lineH;
        divider(g2,tx,ty-6,x+w-10); ty+=2;
        g2.setColor(new Color(20,20,10,200));
        g2.fillRoundRect(tx-2,ty-2,w-18,18,6,6);
        g2.setColor(new Color(255,215,90));
        g2.setFont(new Font("Monospaced",Font.BOLD,12));
        g2.drawString("COINS "+playerCoins, tx+2, ty+12); ty+=lineH+2;
        divider(g2,tx,ty-4,x+w-10); ty+=2;
        long elapsed=System.currentTimeMillis()-gameStartTime;
        long totalSecs=elapsed/1000;
        long hours=totalSecs/3600, minutes=(totalSecs%3600)/60, seconds=totalSecs%60;
        String timeStr=hours>0
                ?String.format("%02d:%02d:%02d",hours,minutes,seconds)
                :String.format("%02d:%02d",minutes,seconds);
        g2.setColor(new Color(20,20,40,200));
        g2.fillRoundRect(tx-2,ty-2,w-18,18,6,6);
        g2.setColor(new Color(100,200,255));
        g2.setFont(new Font("Monospaced",Font.BOLD,12));
        g2.drawString("TIME  "+timeStr, tx+2, ty+12); ty+=lineH+2;
        divider(g2,tx,ty-4,x+w-10); ty+=4;
        g2.setColor(new Color(160,120,60));
        g2.setFont(new Font("Monospaced",Font.PLAIN,10));
        g2.drawString("CODE:", tx, ty+10);
        Color boxBorder = adminMode ? new Color(100,255,100)
                : hudCodeFocused ? new Color(200,180,80)
                  : new Color(80,60,20);
        Color boxBg = adminMode ? new Color(20,60,20,200)
                : hudCodeFocused ? new Color(40,30,5,220)
                  : new Color(20,20,20,200);
        g2.setColor(boxBg);
        g2.fillRoundRect(tx+42,ty-1,w-56,16,4,4);
        g2.setColor(boxBorder);
        g2.setStroke(new BasicStroke(hudCodeFocused?2:1));
        g2.drawRoundRect(tx+42,ty-1,w-56,16,4,4);
        String displayCode;
        if (hudCodeFocused) {
            g2.setColor(new Color(220,190,100));
            long blink=System.currentTimeMillis()/500;
            displayCode=hudCodeInput+(blink%2==0?"|":" ");
        } else if (adminMode && hudCodeInput.isEmpty()) {
            g2.setColor(new Color(100,255,100));
            displayCode="ADMIN ON";
        } else if (hudCodeInput.isEmpty()) {
            g2.setColor(new Color(100,80,40));
            displayCode="click to type";
        } else {
            g2.setColor(new Color(200,160,80));
            displayCode=hudCodeInput;
        }
        g2.setFont(new Font("Monospaced",Font.PLAIN,9));
        g2.drawString(displayCode, tx+46, ty+11);
        if (isWorld2() && anting2Active) {
            int ax=x, ay=y+h+6, aw=w, ah=22;
            g2.setColor(new Color(0,0,0,170));
            g2.fillRoundRect(ax,ay,aw,ah,8,8);
            g2.setColor(new Color(255,180,50));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(ax,ay,aw,ah,8,8);
            g2.setColor(new Color(255,215,100));
            g2.setFont(new Font("Monospaced",Font.BOLD,10));
            g2.drawString("Anting2: x2 EXP ACTIVE", ax+6, ay+15);
        }
        if (isWorld3() && anting2Active) {
            int ax=x, ay=y+h+6, aw=w, ah=22;
            g2.setColor(new Color(0,0,0,170));
            g2.fillRoundRect(ax,ay,aw,ah,8,8);
            g2.setColor(new Color(255,180,50));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(ax,ay,aw,ah,8,8);
            g2.setColor(new Color(255,215,100));
            g2.setFont(new Font("Monospaced",Font.BOLD,10));
            g2.drawString("Anting2: x2 EXP ACTIVE", ax+6, ay+15);
        }
    }

    private void divider(Graphics2D g2, int x1, int y, int x2) {
        g2.setColor(new Color(100,70,15));
        g2.setStroke(new BasicStroke(1));
        g2.drawLine(x1,y,x2,y);
    }

    // ══════════════════════════════════════════════════════════════
    // TOWN SIGN
    // ══════════════════════════════════════════════════════════════
    private void drawTownSign(Graphics2D g2) {
        int sw=280, sh=54;
        int sx=(1280-sw)/2;
        int sy=10;
        g2.setColor(new Color(101,67,33));
        g2.fillRoundRect(sx, sy+sh/2, sw, sh/2+4, 6, 6);
        g2.setColor(new Color(139,90,43));
        g2.fillRoundRect(sx, sy, sw, sh/2+6, 6, 6);
        g2.setColor(new Color(120,78,36,55));
        g2.setStroke(new BasicStroke(1));
        for (int i=0;i<6;i++) {
            int lineY=sy+5+i*8;
            if (lineY<sy+sh-4) g2.drawLine(sx+16, lineY, sx+sw-16, lineY);
        }
        g2.setColor(new Color(60,38,14));
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawRoundRect(sx, sy, sw, sh, 6, 6);
        g2.setColor(new Color(180,130,70,100));
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(sx+4, sy+4, sw-8, sh-8, 4, 4);
        int[] boltX={sx+16,sx+sw-16,sx+16,sx+sw-16};
        int[] boltY={sy+10, sy+10, sy+sh-10, sy+sh-10};
        for (int i=0;i<4;i++) {
            g2.setColor(new Color(70,60,48)); g2.fillOval(boltX[i]-5,boltY[i]-5,10,10);
            g2.setColor(new Color(120,105,80)); g2.setStroke(new BasicStroke(1)); g2.drawOval(boltX[i]-5,boltY[i]-5,10,10);
            g2.setColor(new Color(200,175,130,160)); g2.fillOval(boltX[i]-2,boltY[i]-3,4,3);
        }
        drawChainLink(g2, sx+36, sy-2); drawChainLink(g2, sx+36, sy-14);
        drawChainLink(g2, sx+sw-36, sy-2); drawChainLink(g2, sx+sw-36, sy-14);
        String[] parts=currentTownName.split("—");
        if (parts.length==2) {
            String topLine=parts[0].trim(), bottomLine=parts[1].trim();
            g2.setFont(new Font("Monospaced",Font.BOLD,9));
            FontMetrics sfm=g2.getFontMetrics();
            int topX=(1280-sfm.stringWidth(topLine))/2;
            g2.setColor(new Color(40,22,5,200)); g2.drawString(topLine,topX+1,sy+18+1);
            g2.setColor(new Color(255,215,90)); g2.drawString(topLine,topX,sy+18);
            g2.setFont(new Font("Monospaced",Font.BOLD,15));
            FontMetrics bfm=g2.getFontMetrics();
            int botX=(1280-bfm.stringWidth(bottomLine))/2;
            g2.setColor(new Color(40,22,5,200)); g2.drawString(bottomLine,botX+1,sy+40+1);
            g2.setColor(new Color(255,242,210)); g2.drawString(bottomLine,botX,sy+40);
        } else {
            g2.setFont(new Font("Monospaced",Font.BOLD,13));
            FontMetrics fm=g2.getFontMetrics();
            int tx2=(1280-fm.stringWidth(currentTownName))/2;
            g2.setColor(new Color(40,22,5,200)); g2.drawString(currentTownName,tx2+1,sy+33+1);
            g2.setColor(new Color(255,242,210)); g2.drawString(currentTownName,tx2,sy+33);
        }
    }

    private void drawChainLink(Graphics2D g2, int cx, int cy) {
        g2.setColor(new Color(95,82,60));
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawOval(cx-5,cy-4,10,8);
        g2.setColor(new Color(150,132,100,160));
        g2.setStroke(new BasicStroke(1));
        g2.drawLine(cx-2,cy-1,cx+2,cy-1);
    }

    // ══════════════════════════════════════════════════════════════
    // HOVER MESSAGE
    // ══════════════════════════════════════════════════════════════
    private void setHoverMessage(String msg) {
        hoverMessage = msg;
        hoverMessageUntil = Long.MAX_VALUE;
    }

    private void clearHoverMessage() {
        hoverMessage = "";
        hoverMessageUntil = 0;
    }

    private void drawHoverMessage(Graphics2D g2) {
        if (hoverMessage == null || hoverMessage.isEmpty()) return;
        if (System.currentTimeMillis() > hoverMessageUntil) return;

        String[] lines = hoverMessage.split("\n");
        int lineH = 20;
        int pad   = 12;
        int bw    = 900;
        int bh    = pad * 2 + lines.length * lineH + 4;
        int bx    = (1280 - bw) / 2;
        int by    = 720 - bh - 8;

        if (by < 500) by = 500;

        g2.setColor(new Color(10, 8, 20, 230));
        g2.fillRoundRect(bx, by, bw, bh, 10, 10);
        g2.setColor(new Color(140, 100, 220));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(bx, by, bw, bh, 10, 10);

        g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
        g2.setColor(new Color(230, 220, 255));
        for (int i = 0; i < lines.length; i++) {
            FontMetrics fm = g2.getFontMetrics();
            int lx = bx + (bw - fm.stringWidth(lines[i])) / 2;
            int ly = by + pad + (i + 1) * lineH - 2;
            g2.drawString(lines[i], lx, ly);
        }
    }

    // ══════════════════════════════════════════════════════════════
    // MAP OVERLAY
    // ══════════════════════════════════════════════════════════════
    private void openMapOverlay() {
        if (!hasMap) return;
        if (showMapOverlay) { closeMapOverlay(); return; }
        showMapOverlay = true;
        SwingUtilities.invokeLater(this::buildMapOverlay);
    }

    private void closeMapOverlay() {
        showMapOverlay = false;
        if (mapOverlayPanel != null) { this.remove(mapOverlayPanel); mapOverlayPanel = null; }
        this.revalidate(); this.repaint();
        requestFocusInWindow();
    }

    private void buildMapOverlay() {
        if (mapOverlayPanel != null) { this.remove(mapOverlayPanel); mapOverlayPanel = null; }
        mapOverlayPanel = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 200));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mapOverlayPanel.setOpaque(false);
        mapOverlayPanel.setBounds(0, 0, 1280, 720);

        int bw = 900, bh = 560, bx = (1280 - bw) / 2, by = (720 - bh) / 2;

        JPanel box = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(10, 8, 18));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(120, 80, 200));
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.setColor(new Color(40, 20, 70));
                g2.fillRoundRect(0, 0, getWidth(), 48, 16, 16);
                g2.fillRect(0, 28, getWidth(), 20);
                g2.setColor(new Color(200, 170, 255));
                g2.setFont(new Font("Monospaced", Font.BOLD, 20));
                FontMetrics fm = g2.getFontMetrics();
                String title = "✦  MAP  ✦";
                g2.drawString(title, (getWidth() - fm.stringWidth(title)) / 2, 34);
            }
        };
        box.setOpaque(false);
        box.setBounds(bx, by, bw, bh);

        int defaultTab = isWorld3() ? 2 : isWorld2() ? 1 : 0;
        final int[] activeTab = {defaultTab};

        JButton m1Btn = buildMapTabBtn("M1 — Town 1", activeTab[0] == 0);
        JButton m2Btn = buildMapTabBtn("M2 — Town 2", activeTab[0] == 1);
        JButton m3Btn = buildMapTabBtn("M3 — Town 3", activeTab[0] == 2);
        m1Btn.setBounds(40,  60, 180, 36);
        m2Btn.setBounds(240, 60, 180, 36);
        m3Btn.setBounds(440, 60, 180, 36);
        box.add(m1Btn); box.add(m2Btn); box.add(m3Btn);

        JPanel mapCanvas = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                drawMapDiagram(g2, getWidth(), getHeight(), activeTab[0]);
            }
        };
        mapCanvas.setOpaque(false);
        mapCanvas.setBounds(20, 108, bw - 40, bh - 160);
        box.add(mapCanvas);

        m1Btn.addActionListener(e -> {
            activeTab[0] = 0;
            m1Btn.setBackground(new Color(80, 50, 150));
            m2Btn.setBackground(new Color(30, 20, 60));
            m3Btn.setBackground(new Color(30, 20, 60));
            mapCanvas.repaint();
        });
        m2Btn.addActionListener(e -> {
            activeTab[0] = 1;
            m2Btn.setBackground(new Color(80, 50, 150));
            m1Btn.setBackground(new Color(30, 20, 60));
            m3Btn.setBackground(new Color(30, 20, 60));
            mapCanvas.repaint();
        });
        m3Btn.addActionListener(e -> {
            activeTab[0] = 2;
            m3Btn.setBackground(new Color(80, 50, 150));
            m1Btn.setBackground(new Color(30, 20, 60));
            m2Btn.setBackground(new Color(30, 20, 60));
            mapCanvas.repaint();
        });

        JButton closeBtn = new JButton("CLOSE [M]");
        closeBtn.setBackground(new Color(80, 30, 30));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFont(new Font("Monospaced", Font.BOLD, 13));
        closeBtn.setFocusPainted(false);
        closeBtn.setBorder(BorderFactory.createLineBorder(new Color(180, 60, 60), 2));
        closeBtn.setBounds(bw / 2 - 90, bh - 46, 180, 34);
        closeBtn.addActionListener(e -> closeMapOverlay());
        box.add(closeBtn);

        mapOverlayPanel.add(box);
        this.add(mapOverlayPanel);
        this.setComponentZOrder(mapOverlayPanel, 0);
        this.revalidate(); this.repaint();
    }

    private JButton buildMapTabBtn(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setBackground(active ? new Color(80, 50, 150) : new Color(30, 20, 60));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Monospaced", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(120, 80, 200), 2));
        return btn;
    }

    private void drawMapDiagram(Graphics2D g2, int w, int h, int tab) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color bgColor   = tab == 0 ? new Color(20, 60, 20)
                : tab == 1 ? new Color(10, 30, 60)
                  : new Color(30, 20, 10);
        Color rimColor  = tab == 0 ? new Color(60, 160, 60)
                : tab == 1 ? new Color(60, 100, 200)
                  : new Color(180, 120, 40);
        g2.setColor(bgColor);
        g2.fillRoundRect(10, 10, w - 20, h - 20, 10, 10);
        g2.setColor(rimColor);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(10, 10, w - 20, h - 20, 10, 10);

        String[] imgPaths  = {"resources/World1.png", "resources/World2.png", "resources/World3.png"};
        String[] fallbacks = {"Add resources/World1.png to show map",
                "Add resources/World2.png to show map",
                "Add resources/World3.png to show map"};

        File imgFile = new File(imgPaths[tab]);
        if (imgFile.exists()) {
            try {
                BufferedImage mapImg = ImageIO.read(imgFile);
                g2.drawImage(mapImg, 14, 14, w - 28, h - 28, null);
            } catch (Exception ex) {
                drawMapFallbackLabel(g2, w, h, fallbacks[tab]);
            }
        } else {
            drawMapFallbackLabel(g2, w, h, fallbacks[tab]);
        }

        int currentTab = isWorld3() ? 2 : isWorld2() ? 1 : 0;
        if (tab == currentTab) {
            FontMetrics fm = g2.getFontMetrics(new Font("Monospaced", Font.BOLD, 13));
            String label = "★ YOU ARE HERE";
            int lw = fm.stringWidth(label) + 16;
            int lx = (w - lw) / 2;
            int ly = h - 28;
            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRoundRect(lx - 4, ly - 14, lw + 8, 20, 6, 6);
            g2.setColor(new Color(100, 255, 100));
            g2.setFont(new Font("Monospaced", Font.BOLD, 13));
            g2.drawString(label, lx + 4, ly);
        }
    }

    private void drawMapFallbackLabel(Graphics2D g2, int w, int h, String msg) {
        g2.setColor(new Color(180, 180, 180));
        g2.setFont(new Font("Monospaced", Font.BOLD, 13));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(msg, (w - fm.stringWidth(msg)) / 2, h / 2);
    }

    // ══════════════════════════════════════════════════════════════
    // WORLD 2 NPC: PEKSON
    // ══════════════════════════════════════════════════════════════
    private void showPeksonDialog() {
        if (peksonTalked && peksonGaveAnting2) {
            peksonDialogOpen = false;
            lastNpcTileKey = "";
            showFloatingMessage("Pekson: Stay safe on your journey, " + playerName + "!", new Color(100, 220, 255));
            return;
        }
        peksonTalked = false;
        JPanel overlay = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(0, 0, 0, 200));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlay.setOpaque(false);
        overlay.setBounds(0, 0, 1280, 720);

        int bw = 860, bh = 380, bx = (1280 - bw) / 2, by = (720 - bh) / 2;
        JPanel box = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(8, 14, 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(60, 120, 220));
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.setColor(new Color(20, 40, 80));
                g2.fillRoundRect(0, 0, getWidth(), 46, 16, 16);
                g2.fillRect(0, 26, getWidth(), 20);
                g2.setColor(new Color(150, 200, 255));
                g2.setFont(new Font("Monospaced", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                String t = "Pekson";
                g2.drawString(t, (getWidth() - fm.stringWidth(t)) / 2, 33);
            }
        };
        box.setOpaque(false);
        box.setBounds(bx, by, bw, bh);

        String msg = "Pekson: Hi " + playerName + "! Its been a long time since we\n" +
                "last saw each other. How's school? I miss going back to\n" +
                "Cit-U. I hope maybe next school year I can enroll.\n" +
                "Enough about me — why are you here?";
        int ly = 58;
        for (String line : msg.split("\n")) {
            JLabel lbl = new JLabel(line);
            lbl.setForeground(new Color(210, 230, 255));
            lbl.setFont(new Font("Monospaced", Font.PLAIN, 13));
            lbl.setBounds(20, ly, bw - 40, 20);
            box.add(lbl);
            ly += 22;
        }

        JButton visit = new JButton("I'm just visiting.");
        visit.setBackground(new Color(30, 60, 120));
        visit.setForeground(Color.WHITE);
        visit.setFont(new Font("Monospaced", Font.BOLD, 13));
        visit.setFocusPainted(false);
        visit.setBorder(BorderFactory.createLineBorder(new Color(80, 140, 220), 2));
        visit.setBounds(40, bh - 100, 360, 40);

        JButton grandpa = new JButton("My grandpa has been captured by an tikbalang can you help me find him");
        grandpa.setBackground(new Color(80, 30, 30));
        grandpa.setForeground(Color.WHITE);
        grandpa.setFont(new Font("Monospaced", Font.BOLD, 11));
        grandpa.setFocusPainted(false);
        grandpa.setBorder(BorderFactory.createLineBorder(new Color(200, 80, 80), 2));
        grandpa.setBounds(40, bh - 54, bw - 80, 40);

        final JPanel fOverlay = overlay;
        visit.addActionListener(e -> {
            box.removeAll();
            JLabel resp = new JLabel("Pekson: Ohh okay i hope you enjoy your travel");
            resp.setForeground(new Color(200, 230, 255));
            resp.setFont(new Font("Monospaced", Font.BOLD, 14));
            resp.setBounds(20, bh / 2 - 20, bw - 40, 30);
            box.add(resp);
            JButton close = new JButton("Goodbye!");
            close.setBackground(new Color(40, 80, 40));
            close.setForeground(Color.WHITE);
            close.setFont(new Font("Monospaced", Font.BOLD, 13));
            close.setFocusPainted(false);
            close.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            close.setBounds(bw / 2 - 80, bh - 70, 160, 36);
            close.addActionListener(ev -> {
                WorldPanel.this.remove(fOverlay);
                WorldPanel.this.revalidate(); WorldPanel.this.repaint();
                peksonTalked = false;
                peksonDialogOpen = false;
                lastNpcTileKey = "";
                requestFocusInWindow();
            });
            box.add(close);
            box.revalidate(); box.repaint();
        });

        grandpa.addActionListener(e -> {
            box.removeAll();
            String r2 = "Pekson: ohh no really? i can help you ohh i see you already have one\n" +
                    "anting-anting let me give you my grandpas anting2 that will help\n" +
                    "you lvl up your creature a bit faster";
            int rly = 50;
            for (String line : r2.split("\n")) {
                JLabel lbl = new JLabel(line);
                lbl.setForeground(new Color(200, 255, 200));
                lbl.setFont(new Font("Monospaced", Font.PLAIN, 13));
                lbl.setBounds(20, rly, bw - 40, 20);
                box.add(lbl); rly += 22;
            }
            JButton accept = new JButton("Receive Anting2 (x2 EXP for all creatures)");
            accept.setBackground(new Color(60, 130, 60));
            accept.setForeground(Color.WHITE);
            accept.setFont(new Font("Monospaced", Font.BOLD, 13));
            accept.setFocusPainted(false);
            accept.setBorder(BorderFactory.createLineBorder(new Color(100, 220, 100), 2));
            accept.setBounds(bw / 2 - 200, bh - 80, 400, 40);
            accept.addActionListener(ev -> {
                peksonGaveAnting2 = true;
                peksonTalked = true;
                anting2Active = true;
                expMultiplier = 2.0;
                quest2Triggered = true;
                peksonDialogOpen = false; lastNpcTileKey = "";
                syncStateToGameScene();
                WorldPanel.this.remove(fOverlay);
                WorldPanel.this.revalidate(); WorldPanel.this.repaint();
                requestFocusInWindow();
                showFloatingMessage("Received Anting2! All creatures gain x2 EXP!", new Color(100, 255, 150));
                new Timer(2800, ev2 -> {
                    ((Timer) ev2.getSource()).stop();
                    showFloatingMessage("QUEST 2: Level 2 creatures to Lv.25 & cure the old woman!", new Color(255, 200, 80));
                }).start();
            });
            box.add(accept);
            box.revalidate(); box.repaint();
        });

        box.add(visit);
        box.add(grandpa);
        overlay.add(box);
        this.add(overlay);
        this.setComponentZOrder(overlay, 0);
        this.revalidate(); this.repaint();
    }
    // ── Pending Khaibalang rematch ─────────────────────────────────
    private Fighter pendingKhaibalang = null;
    private String  pendingKhaiPName  = null;

    public void setPendingKhaibalang(Fighter khai, String pName) {
        this.pendingKhaibalang = khai;
        this.pendingKhaiPName  = pName;
    }


    // ══════════════════════════════════════════════════════════════
    // WORLD 2 NPC: OLD WOMAN
    // ══════════════════════════════════════════════════════════════
    private void showOldWomanDialog() {
        if (oldWomanCured) {
            oldWomanDialogOpen = true;
            JPanel overlay = new JPanel(null);
            overlay.setOpaque(false);
            overlay.setBounds(0, 0, 1280, 720);
            int rw = 680, rh = 80, rx = (1280 - rw) / 2, ry = (720 - rh) / 2;
            JPanel msgBox = new JPanel(new BorderLayout());
            msgBox.setBackground(new Color(12, 25, 12));
            msgBox.setBorder(BorderFactory.createLineBorder(new Color(200, 255, 200), 2));
            msgBox.setBounds(rx, ry, rw, rh);
            JLabel lbl = new JLabel("Old Woman: Thank you again, young one. May you find your grandpa!", SwingConstants.CENTER);
            lbl.setForeground(new Color(200, 255, 200));
            lbl.setFont(new Font("Monospaced", Font.BOLD, 13));
            msgBox.add(lbl, BorderLayout.CENTER);
            overlay.add(msgBox);
            this.add(overlay);
            this.setComponentZOrder(overlay, 0);
            this.revalidate();
            this.repaint();
            new Timer(3000, ev -> {
                ((Timer) ev.getSource()).stop();
                SwingUtilities.invokeLater(() -> {
                    this.remove(overlay);
                    oldWomanDialogOpen = false;
                    lastNpcTileKey = "";
                    this.revalidate();
                    this.repaint();
                    requestFocusInWindow();
                });
            }).start();
            return;
        }

        JPanel overlay = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ((Graphics2D) g).setColor(new Color(0, 0, 0, 190));
                ((Graphics2D) g).fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlay.setOpaque(false);
        overlay.setBounds(0, 0, 1280, 720);

        int bw = 820, bh = 340, bx = (1280 - bw) / 2, by = (720 - bh) / 2;
        JPanel box = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(20, 10, 10));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(160, 60, 60));
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.setColor(new Color(60, 10, 10));
                g2.fillRoundRect(0, 0, getWidth(), 46, 16, 16);
                g2.fillRect(0, 26, getWidth(), 20);
                g2.setColor(new Color(255, 160, 160));
                g2.setFont(new Font("Monospaced", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                String t = "Cursed Old Woman";
                g2.drawString(t, (getWidth() - fm.stringWidth(t)) / 2, 33);
            }
        };
        box.setOpaque(false);
        box.setBounds(bx, by, bw, bh);

        String msg = "help me young man the albularyo cursed me and he wants me to\n" +
                "pay him 1k coins to heal me when he was the one who cursed me\n" +
                "in the first place";
        int ly = 56;
        for (String line : msg.split("\n")) {
            JLabel lbl = new JLabel(line);
            lbl.setForeground(new Color(255, 200, 200));
            lbl.setFont(new Font("Monospaced", Font.PLAIN, 14));
            lbl.setBounds(20, ly, bw - 40, 22);
            box.add(lbl); ly += 24;
        }

        final JPanel fOverlay = overlay;
        JButton cureBtn = new JButton("use mega potion to cure the curse");
        cureBtn.setBackground(new Color(60, 100, 60));
        cureBtn.setForeground(Color.WHITE);
        cureBtn.setFont(new Font("Monospaced", Font.BOLD, 13));
        cureBtn.setFocusPainted(false);
        cureBtn.setBorder(BorderFactory.createLineBorder(new Color(100, 200, 100), 2));
        cureBtn.setBounds(bw / 2 - 200, bh - 90, 400, 40);
        cureBtn.addActionListener(e -> {
            boolean twoAtLv25 = checkTwoCreaturesAtLevel(25);
            if (!twoAtLv25) {
                box.removeAll();
                JLabel notReady = new JLabel("<html><center>You need 2 creatures at Lv.25 first!<br>Keep training and come back.</center></html>", SwingConstants.CENTER);
                notReady.setForeground(new Color(255, 120, 120));
                notReady.setFont(new Font("Monospaced", Font.BOLD, 14));
                notReady.setBounds(20, bh / 2 - 40, bw - 40, 60);
                box.add(notReady);
                JButton okBtn = new JButton("OK");
                okBtn.setBackground(new Color(80, 40, 40));
                okBtn.setForeground(Color.WHITE);
                okBtn.setFont(new Font("Monospaced", Font.BOLD, 13));
                okBtn.setFocusPainted(false);
                okBtn.setBorder(BorderFactory.createLineBorder(new Color(160, 80, 80), 2));
                okBtn.setBounds(bw / 2 - 60, bh - 60, 120, 36);
                okBtn.addActionListener(ev -> {
                    oldWomanDialogOpen = false; lastNpcTileKey = "";
                    WorldPanel.this.remove(fOverlay);
                    WorldPanel.this.revalidate(); WorldPanel.this.repaint();
                    requestFocusInWindow();
                });
                box.add(okBtn);
                box.revalidate(); box.repaint();
                return;
            }
            oldWomanCured = true;
            quest2Complete = true;
            oldWomanDialogOpen = false; lastNpcTileKey = "";
            syncStateToGameScene();
            box.removeAll();
            JLabel success = new JLabel("<html><center>Old Woman cured! Quest 2 Complete!<br>+200 Coins! You can now battle the Albularyo!</center></html>", SwingConstants.CENTER);
            success.setForeground(new Color(100, 255, 150));
            success.setFont(new Font("Monospaced", Font.BOLD, 14));
            success.setBounds(20, bh / 2 - 40, bw - 40, 60);
            box.add(success);
            playerCoins += 200;
            JButton doneBtn = new JButton("Great!");
            doneBtn.setBackground(new Color(30, 100, 50));
            doneBtn.setForeground(Color.WHITE);
            doneBtn.setFont(new Font("Monospaced", Font.BOLD, 13));
            doneBtn.setFocusPainted(false);
            doneBtn.setBorder(BorderFactory.createLineBorder(new Color(80, 200, 100), 2));
            doneBtn.setBounds(bw / 2 - 60, bh - 60, 120, 36);
            doneBtn.addActionListener(ev -> {
                WorldPanel.this.remove(fOverlay);
                WorldPanel.this.revalidate(); WorldPanel.this.repaint();
                requestFocusInWindow();
            });
            box.add(doneBtn);
            box.revalidate(); box.repaint();
        });
        box.add(cureBtn);

        JButton leaveBtn = new JButton("I'll come back later.");
        leaveBtn.setBackground(new Color(60, 40, 40));
        leaveBtn.setForeground(Color.WHITE);
        leaveBtn.setFont(new Font("Monospaced", Font.BOLD, 12));
        leaveBtn.setFocusPainted(false);
        leaveBtn.setBorder(BorderFactory.createLineBorder(new Color(160, 80, 80), 2));
        leaveBtn.setBounds(bw / 2 - 100, bh - 44, 200, 32);
        leaveBtn.addActionListener(e -> {
            oldWomanDialogOpen = false; lastNpcTileKey = "";
            WorldPanel.this.remove(fOverlay);
            WorldPanel.this.revalidate(); WorldPanel.this.repaint();
            requestFocusInWindow();
        });
        box.add(leaveBtn);
        overlay.add(box);
        this.add(overlay);
        this.setComponentZOrder(overlay, 0);
        this.revalidate(); this.repaint();
    }

    private boolean checkTwoCreaturesAtLevel(int level) {
        int count = 0;
        if (playerFighter.level >= level) count++;
        for (Fighter f : capturedTeam) if (f.level >= level) count++;
        return count >= 2;
    }

    // ══════════════════════════════════════════════════════════════
    // WORLD 2 SHOP (Dominic)
    // ══════════════════════════════════════════════════════════════
    private void openWorld2Shop() {
        if (w2ShopOpen) return;
        w2ShopOpen = true;
        buildWorld2ShopOverlay();
    }

    private void closeWorld2Shop() {
        w2ShopOpen = false;
        if (shopOverlay != null) { this.remove(shopOverlay); shopOverlay = null; this.revalidate(); this.repaint(); }
        requestFocusInWindow();
    }

    private void buildWorld2ShopOverlay() {
        if (shopOverlay != null) { this.remove(shopOverlay); shopOverlay = null; }
        int screenW = 1280, screenH = 720, winW = 740, winH = 560;
        int winX = (screenW - winW) / 2, winY = (screenH - winH) / 2;
        final int fwinX = winX, fwinW = winW, fwinH = winH, fwinY = winY;
        shopOverlay = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 190)); g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(14, 10, 28)); g2.fillRoundRect(fwinX, fwinY, fwinW, fwinH, 18, 18);
                g2.setColor(new Color(120, 80, 200)); g2.setStroke(new BasicStroke(3)); g2.drawRoundRect(fwinX, fwinY, fwinW, fwinH, 18, 18);
                g2.setColor(new Color(50, 30, 100)); g2.fillRoundRect(fwinX, fwinY, fwinW, 48, 18, 18); g2.fillRect(fwinX, fwinY + 28, fwinW, 20);
                g2.setColor(new Color(200, 170, 255)); g2.setFont(new Font("Monospaced", Font.BOLD, 20));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("DOMINIC'S SHOP", fwinX + (fwinW - fm.stringWidth("DOMINIC'S SHOP")) / 2, fwinY + 33);
                g2.setFont(new Font("Monospaced", Font.BOLD, 13));
                g2.setColor(new Color(255, 215, 90));
                g2.drawString("Coins: " + playerCoins, fwinX + fwinW - 160, fwinY + 33);
                g2.setColor(new Color(120, 80, 200)); g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(fwinX + 16, fwinY + 50, fwinX + fwinW - 16, fwinY + 50);
                g2.setColor(new Color(255, 250, 230)); g2.fillRoundRect(fwinX + 14, fwinY + 390, fwinW - 28, 50, 12, 12);
                g2.setColor(new Color(120, 80, 200)); g2.setStroke(new BasicStroke(2)); g2.drawRoundRect(fwinX + 14, fwinY + 390, fwinW - 28, 50, 12, 12);
                g2.setColor(new Color(60, 30, 100)); g2.setFont(new Font("Monospaced", Font.BOLD, 13));
                g2.drawString("Hi! I'm Dominic — welcome to Town 2's finest shop!", fwinX + 24, fwinY + 412);
                g2.setColor(new Color(100, 60, 160)); g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
                g2.drawString("What would you like to buy today?", fwinX + 24, fwinY + 432);
                g2.setColor(new Color(120, 120, 120)); g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
                g2.drawString("Press [E] to close", fwinX + (fwinW - 120) / 2, fwinY + fwinH - 10);
            }
        };
        shopOverlay.setOpaque(false);
        shopOverlay.setBounds(0, 0, screenW, screenH);

        int itemY = winY + 60, itemX = winX + 20, itemW = winW - 40, rowH = 52, gap = 5;
        Object[][] items = {
                {"Scroll",       "Capture wild creatures in battle.",               30,  "scroll"},
                {"Lunas",        "Restore 5 PP to one creature move.",              25,  "lunas"},
                {"Potion",       "Restore 30 HP to one creature.",                  25,  "potion"},
                {"Super Lunas",  "Restore 10 PP to one creature move.",             50,  "super_lunas"},
                {"Super Potion", "Restore 70 HP to one creature.",                  70,  "super_potion"},
                {"Super Scroll", "Powerful scroll — effective at mid HP too.",      80,  "super_scroll"},
        };
        for (Object[] item : items) {
            shopOverlay.add(buildW2ShopRow(itemX, itemY, itemW, rowH, (String)item[0], (String)item[1], (int)item[2], (String)item[3]));
            itemY += rowH + gap;
        }

        JButton leaveBtn = new JButton("LEAVE SHOP");
        leaveBtn.setBackground(new Color(60, 30, 100));
        leaveBtn.setForeground(Color.WHITE);
        leaveBtn.setFont(new Font("Monospaced", Font.BOLD, 14));
        leaveBtn.setFocusPainted(false);
        leaveBtn.setBorder(BorderFactory.createLineBorder(new Color(120, 80, 200), 2));
        leaveBtn.setBounds(winX + winW / 2 - 100, winY + winH - 44, 200, 34);
        leaveBtn.addActionListener(e -> closeWorld2Shop());
        shopOverlay.add(leaveBtn);

        this.add(shopOverlay);
        this.setComponentZOrder(shopOverlay, 0);
        this.revalidate(); this.repaint();
    }

    private JPanel buildW2ShopRow(int x, int y, int w, int h, String name, String desc, int cost, String key) {
        JPanel row = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(20, 12, 40)); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(80, 50, 140)); g2.setStroke(new BasicStroke(1)); g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
            }
        };
        row.setOpaque(false); row.setBounds(x, y, w, h);
        JLabel nl = new JLabel(name); nl.setForeground(new Color(200, 170, 255)); nl.setFont(new Font("Monospaced", Font.BOLD, 13)); nl.setBounds(12, 6, 220, 18); row.add(nl);
        JLabel dl = new JLabel(desc); dl.setForeground(new Color(150, 140, 180)); dl.setFont(new Font("Monospaced", Font.PLAIN, 11)); dl.setBounds(12, 26, w - 230, 16); row.add(dl);
        JLabel cl = new JLabel(cost + " Coins"); cl.setForeground(new Color(255, 215, 90)); cl.setFont(new Font("Monospaced", Font.BOLD, 12)); cl.setBounds(w - 230, 6, 100, 18); row.add(cl);
        JLabel sl = new JLabel("Have: " + getW2ItemCount(key)); sl.setForeground(new Color(140, 200, 140)); sl.setFont(new Font("Monospaced", Font.PLAIN, 11)); sl.setBounds(w - 230, 26, 100, 16); row.add(sl);
        JButton buy1 = w2ShopBtn("Buy x1", new Color(50, 30, 100)); buy1.setBounds(w - 120, 6, 104, 18);
        buy1.addActionListener(e -> { buyW2Item(key, cost, 1); buildWorld2ShopOverlay(); }); row.add(buy1);
        JButton buy5 = w2ShopBtn("Buy x5", new Color(35, 18, 70)); buy5.setBounds(w - 120, 28, 104, 18);
        buy5.addActionListener(e -> { buyW2Item(key, cost, 5); buildWorld2ShopOverlay(); }); row.add(buy5);
        return row;
    }

    private JButton w2ShopBtn(String text, Color bg) {
        JButton btn = new JButton(text); btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Monospaced", Font.BOLD, 10)); btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(100, 60, 180), 1)); return btn;
    }

    private int getW2ItemCount(String key) {
        switch (key) {
            case "scroll":       return scrollCount;
            case "lunas":        return lunasCount;
            case "potion":       return potionCount;
            case "super_lunas":  return superLunasCount;
            case "super_potion": return superPotionCount;
            case "super_scroll": return superScrollCount;
            default: return 0;
        }
    }

    private void buyW2Item(String key, int cost, int qty) {
        int total = cost * qty;
        if (playerCoins < total) { showFloatingMessage("Not enough Coins! Need " + total, new Color(220, 60, 60)); return; }
        playerCoins -= total;
        switch (key) {
            case "scroll":       scrollCount      += qty; break;
            case "lunas":        lunasCount       += qty; break;
            case "potion":       potionCount      += qty; break;
            case "super_lunas":  superLunasCount  += qty; break;
            case "super_potion": superPotionCount += qty; break;
            case "super_scroll": superScrollCount += qty; break;
        }
        showFloatingMessage("Bought " + qty + "x " + key + " for " + total + " coins!", new Color(100, 255, 150));
    }

    // ══════════════════════════════════════════════════════════════
    // WORLD 2 QUEST PANEL
    // ══════════════════════════════════════════════════════════════
    private void drawWorld2QuestPanel(Graphics2D g2) {
        if (!quest2Triggered) return;
        int qx = 1280 - 230, qy = 10, qw = 220, qh = quest2Complete ? 80 : 110;
        g2.setColor(new Color(0, 0, 0, 180)); g2.fillRoundRect(qx, qy, qw, qh, 12, 12);
        g2.setColor(quest2Complete ? new Color(80, 200, 80) : new Color(200, 120, 50));
        g2.setStroke(new BasicStroke(2)); g2.drawRoundRect(qx, qy, qw, qh, 12, 12);
        g2.setColor(new Color(255, 180, 80)); g2.setFont(new Font("Monospaced", Font.BOLD, 11));
        g2.drawString("QUEST 2", qx + 8, qy + 18);
        if (quest2Complete) {
            g2.setColor(new Color(100, 255, 100)); g2.setFont(new Font("Monospaced", Font.BOLD, 10));
            g2.drawString("✓ Quest Complete!", qx + 8, qy + 38);
            if (w2BossDone) {
                g2.setColor(new Color(100, 255, 100));
                g2.drawString("✓ Albularyo defeated!", qx + 8, qy + 56);
            } else {
                g2.setColor(new Color(255, 160, 80)); g2.setFont(new Font("Monospaced", Font.BOLD, 10));
                g2.drawString("Go fight the Albularyo!", qx + 8, qy + 56);
            }
        } else {
            boolean twoLv25 = checkTwoCreaturesAtLevel(25);
            g2.setColor(twoLv25 ? new Color(100, 255, 100) : new Color(200, 200, 200));
            g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
            g2.drawString((twoLv25 ? "✓ " : "• ") + "2 creatures Lv.25", qx + 8, qy + 36);
            g2.setColor(oldWomanCured ? new Color(100, 255, 100) : new Color(200, 200, 200));
            g2.drawString((oldWomanCured ? "✓ " : "• ") + "Cure old woman", qx + 8, qy + 54);
            if (!twoLv25 || !oldWomanCured) {
                g2.setColor(new Color(255, 160, 80)); g2.setFont(new Font("Monospaced", Font.BOLD, 10));
                g2.drawString("Battle the Albularyo!", qx + 8, qy + 78);
            }
        }
    }

    // ══════════════════════════════════════════════════════════════
    // ADMIN / CODE INPUT
    // ══════════════════════════════════════════════════════════════
    private void processHudCodeInput(char c) {
        if (c=='\b') {
            if (!hudCodeInput.isEmpty()) hudCodeInput=hudCodeInput.substring(0,hudCodeInput.length()-1);
        } else if (c=='\n'||c=='\r') {
            executeCommand(hudCodeInput.trim());
            hudCodeInput="";
            hudCodeFocused=false;
        } else if (c==27) {
            hudCodeFocused=false; hudCodeInput="";
        } else if (c>=32&&c<127&&hudCodeInput.length()<20) {
            hudCodeInput+=c;
            if (hudCodeInput.equals("PeksonMaster153")) { executeCommand("PeksonMaster153"); hudCodeInput=""; }
            if (hudCodeInput.equals("Skiptown1"))       { executeCommand("Skiptown1"); hudCodeInput=""; }
            if (hudCodeInput.equals("Skiptown2"))       { executeCommand("Skiptown2"); hudCodeInput=""; }
        }
    }

    private void executeCommand(String cmd) {
        // NEW CHEAT COMMANDS - ADD THESE FIRST
        if (cmd.equalsIgnoreCase("skipTown3Boss") || cmd.equalsIgnoreCase("town3boss")) {
            gameScene.cheatSkipTown3Boss(this);
            hudCodeFocused = false;
            return;
        }

        if (cmd.equalsIgnoreCase("skipKhaibalang") || cmd.equalsIgnoreCase("khaibalang")) {
            gameScene.cheatSkipKhaibalang(this);
            hudCodeFocused = false;
            return;
        }

        // Existing commands
        switch (cmd) {
            case "PeksonMaster153": activateAdminMode(); break;
            case "Skiptown1":       skipToTown2();       break;
            case "Skiptown2":       skipToTown3();       break;
            default:
                if (!cmd.isEmpty()) showFloatingMessage("Unknown command: " + cmd, new Color(200,100,100));
                break;
        }
        hudCodeFocused=false;
    }

    private void activateAdminMode() {
        adminMode=true;
        hudCodeInput=""; hudCodeFocused=false;
        playerCoins=9999; potionCount=9999; lunasCount=9999; scrollCount=9999;
        superLunasCount=99; superPotionCount=99; superScrollCount=99;
        maxCreatureLevel(playerFighter);
        for (Fighter f:capturedTeam) maxCreatureLevel(f);
        syncStateToGameScene();
        showLegendaryMessage();
    }

    private void skipToTown2() {
        hudCodeInput=""; hudCodeFocused=false;
        caveSceneShown=true;
        bossFightDone=true;
        portalVisible=true;
        questCreaturesCaptured=6;
        antingAntingCount = Math.min(4, antingAntingCount + 1);
        playerCoins += 900;
        syncStateToGameScene();
        showFloatingMessage("Skiptown1 — Teleporting to Town 2...", new Color(180,130,255));
        new Timer(1500, e -> {
            ((Timer) e.getSource()).stop();
            inBattle = true;
            SwingUtilities.invokeLater(this::enterPortalToWorld2);
        }).start();
    }

    private void skipToTown3() {
        hudCodeInput=""; hudCodeFocused=false;
        caveSceneShown=true;
        bossFightDone=true;
        portalVisible=true;
        questCreaturesCaptured=6;
        w2BossDone=true;
        w2PortalVisible=true;
        quest2Complete=true;
        oldWomanCured=true;
        antingAntingCount = Math.min(4, antingAntingCount + 1);
        playerCoins += 900;
        syncStateToGameScene();
        showFloatingMessage("Skiptown2 — Teleporting to Town 3...", new Color(100,200,255));
        new Timer(1500, e -> {
            ((Timer) e.getSource()).stop();
            inBattle = true;
            SwingUtilities.invokeLater(this::enterPortalToWorld3);
        }).start();
    }

    private void maxCreatureLevel(Fighter f) {
        f.level=99; f.exp=0; f.expToNext=Fighter.expNeeded(99);
        f.stats.get(0).base=9999; f.stats.get(0).value=9999;
        f.stats.get(1).base=999;  f.stats.get(1).value=999;
        f.stats.get(2).base=999;  f.stats.get(2).value=999;
        if (f.stats.size()>3) { f.stats.get(3).base=999; f.stats.get(3).value=999; }
        for (Move m:f.moveset) { m.lockedUntilLevel=0; m.pp=m.maxPp; }
    }

    private void showLegendaryMessage() {
        JPanel overlay = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0,0,0,200)); g2.fillRect(0,0,getWidth(),getHeight());
                int bw=900,bh=220,bx=(1280-bw)/2,by=(720-bh)/2;
                for (int glow=6;glow>=0;glow--) { g2.setColor(new Color(255,200,0,20+glow*8)); g2.fillRoundRect(bx-glow*2,by-glow*2,bw+glow*4,bh+glow*4,24,24); }
                g2.setColor(new Color(10,8,2)); g2.fillRoundRect(bx,by,bw,bh,18,18);
                g2.setColor(new Color(255,200,50)); g2.setStroke(new BasicStroke(3)); g2.drawRoundRect(bx,by,bw,bh,18,18);
                g2.setColor(new Color(255,220,80)); g2.setFont(new Font("Monospaced",Font.BOLD,20));
                g2.drawString("★",bx+20,by+40); g2.drawString("★",bx+bw-44,by+40);
                g2.drawString("★",bx+20,by+bh-16); g2.drawString("★",bx+bw-44,by+bh-16);
                g2.setColor(new Color(255,215,0)); g2.setFont(new Font("Monospaced",Font.BOLD,28));
                FontMetrics fm=g2.getFontMetrics(); String line1="YOU HAVE BECOME THE";
                g2.drawString(line1,bx+(bw-fm.stringWidth(line1))/2,by+72);
                g2.setFont(new Font("Monospaced",Font.BOLD,36)); fm=g2.getFontMetrics(); String line2="LEGENDARY ALAMAT";
                g2.setColor(new Color(180,120,0)); g2.drawString(line2,bx+(bw-fm.stringWidth(line2))/2+2,by+124);
                g2.setColor(new Color(255,240,80)); g2.drawString(line2,bx+(bw-fm.stringWidth(line2))/2,by+122);
                g2.setColor(new Color(180,160,80)); g2.setFont(new Font("Monospaced",Font.PLAIN,14)); fm=g2.getFontMetrics();
                String sub="All creatures maxed to Lv.99  •  9999 Coins  •  9999 Items";
                g2.drawString(sub,bx+(bw-fm.stringWidth(sub))/2,by+162);
                g2.setColor(new Color(120,110,60)); g2.setFont(new Font("Monospaced",Font.PLAIN,11));
                String hint="[ Click anywhere to continue ]"; fm=g2.getFontMetrics();
                g2.drawString(hint,bx+(bw-fm.stringWidth(hint))/2,by+bh-16);
            }
        };
        overlay.setOpaque(false); overlay.setBounds(0,0,1280,720);
        overlay.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                WorldPanel.this.remove(overlay); WorldPanel.this.revalidate(); WorldPanel.this.repaint(); requestFocusInWindow();
            }
        });
        this.add(overlay); this.setComponentZOrder(overlay,0); this.revalidate(); this.repaint();
    }

    // ══════════════════════════════════════════════════════════════
    // QUEST PANEL
    // ══════════════════════════════════════════════════════════════
    private void drawQuestPanel(Graphics2D g2) {
        if (isWorld2()) { drawWorld2QuestPanel(g2); return; }
        if (isWorld3()) { drawWorld3QuestPanel(g2); return; }

        if (caveSceneShown && !bossFightDone) {
            int qx=1280-220,qy=10,qw=210,qh=100;
            g2.setColor(new Color(0,0,0,180)); g2.fillRoundRect(qx,qy,qw,qh,12,12);
            g2.setColor(new Color(200,80,80)); g2.setStroke(new BasicStroke(2)); g2.drawRoundRect(qx,qy,qw,qh,12,12);
            g2.setColor(new Color(255,100,100)); g2.setFont(new Font("Monospaced",Font.BOLD,11));
            g2.drawString("QUEST",qx+8,qy+18);
            int captured=1+capturedTeam.size();
            boolean q1done=captured>=6, q2done=playerFighter.level>=15;
            g2.setColor(q1done?new Color(100,255,100):new Color(200,200,200));
            g2.setFont(new Font("Monospaced",Font.PLAIN,10));
            g2.drawString((q1done?"✓ ":"• ")+"Capture 6 creatures ("+Math.min(captured,6)+"/6)",qx+8,qy+38);
            g2.setColor(q2done?new Color(100,255,100):new Color(200,200,200));
            g2.drawString((q2done?"✓ ":"• ")+"Reach Lv.15 (Lv."+playerFighter.level+"/15)",qx+8,qy+56);
            if (q1done&&q2done) {
                g2.setColor(new Color(255,200,50)); g2.setFont(new Font("Monospaced",Font.BOLD,10));
                g2.drawString("Go to the BOSS area!",qx+8,qy+76);
                g2.drawString("(east of the map)",qx+8,qy+90);
            }
        }
        if (portalVisible) {
            int qx=1280-220,qy=10,qw=210,qh=50;
            g2.setColor(new Color(0,0,0,180)); g2.fillRoundRect(qx,qy,qw,qh,12,12);
            g2.setColor(new Color(150,100,255)); g2.setStroke(new BasicStroke(2)); g2.drawRoundRect(qx,qy,qw,qh,12,12);
            g2.setColor(new Color(200,150,255)); g2.setFont(new Font("Monospaced",Font.BOLD,11));
            g2.drawString("A portal has appeared!",qx+8,qy+22);
            g2.setColor(new Color(180,140,220)); g2.setFont(new Font("Monospaced",Font.PLAIN,10));
            g2.drawString("Head south of boss area.",qx+8,qy+40);
        }
    }

    // ══════════════════════════════════════════════════════════════
    // CAVE SCENE
    // ══════════════════════════════════════════════════════════════
    private void showCaveScene() {
        if (caveSceneShown||showCaveScene) return;
        showCaveScene=true; caveSceneShown=true;
        SwingUtilities.invokeLater(() -> {
            if (cavePanel!=null) this.remove(cavePanel);
            cavePanel=new JPanel(null) {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2=(Graphics2D)g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(0,0,0,210)); g2.fillRect(0,0,getWidth(),getHeight());
                }
            };
            cavePanel.setOpaque(false); cavePanel.setBounds(0,0,1280,720);
            int bw=940, bh=360, bx=(1280-bw)/2, by=(720-bh)/2-20;
            JPanel box=new JPanel(null) {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2=(Graphics2D)g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(10,8,18)); g2.fillRoundRect(0,0,getWidth(),getHeight(),16,16);
                    g2.setColor(new Color(80,60,140)); g2.setStroke(new BasicStroke(3)); g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,16,16);
                    g2.setColor(new Color(40,20,70)); g2.fillRoundRect(0,0,getWidth(),46,16,16); g2.fillRect(0,26,getWidth(),20);
                    g2.setColor(new Color(160,130,255)); g2.setFont(new Font("Monospaced",Font.BOLD,18));
                    FontMetrics fm=g2.getFontMetrics(); String title="Inside the Cave";
                    g2.drawString(title,(getWidth()-fm.stringWidth(title))/2,33);
                    drawOldMan(g2,16,52,68,120);
                }
            };
            box.setOpaque(false); box.setBounds(bx,by,bw,bh);
            String[] lines={"Ah... "+(playerName.isEmpty()?"young one":playerName)+"! I've been waiting for you.","I am Lolo Andres — your grandfather's best friend.","He left me here to guide you when you arrived.","But I cannot let you pass just yet...","Your creatures are still too weak for what lies ahead.","","Complete these tasks before facing the Albularyo boss:"};
            int ly=52;
            for (String line:lines) {
                JLabel lbl=new JLabel(line.isEmpty()?" ":line);
                lbl.setForeground(line.startsWith("Ah")||line.startsWith("I am")||line.startsWith("He left")?new Color(255,215,90):line.startsWith("But")||line.startsWith("Your")?new Color(255,140,140):line.startsWith("Complete")?new Color(100,200,255):new Color(210,210,210));
                lbl.setFont(new Font("Monospaced",line.startsWith("Complete")?Font.BOLD:Font.PLAIN,12));
                lbl.setBounds(100,ly,bw-116,18); box.add(lbl); ly+=20;
            }
            int q1count=1+capturedTeam.size();
            JPanel q1=questBox("Capture 6 creatures (have "+Math.min(q1count,6)+"/6)",q1count>=6,bw-116,30);
            q1.setBounds(100,ly+4,bw-116,30); box.add(q1); ly+=40;
            JPanel q2=questBox("Level your main creature to Lv.15 (now Lv."+playerFighter.level+")",playerFighter.level>=15,bw-116,30);
            q2.setBounds(100,ly+4,bw-116,30); box.add(q2); ly+=42;
            JButton closeBtn=new JButton("I understand!");
            closeBtn.setBackground(new Color(60,40,110)); closeBtn.setForeground(Color.WHITE);
            closeBtn.setFont(new Font("Monospaced",Font.BOLD,13)); closeBtn.setFocusPainted(false);
            closeBtn.setBorder(BorderFactory.createLineBorder(new Color(150,100,255),2));
            closeBtn.setBounds(bw/2-90,ly+4,180,34);
            closeBtn.addActionListener(e->closeCaveScene()); box.add(closeBtn);
            cavePanel.add(box); this.add(cavePanel); this.setComponentZOrder(cavePanel,0);
            this.revalidate(); this.repaint();
        });
    }

    private JPanel questBox(String text, boolean done, int w, int h) {
        JPanel p=new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(done?new Color(20,50,20):new Color(40,20,20)); g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g2.setColor(done?new Color(80,200,80):new Color(200,80,80)); g2.setStroke(new BasicStroke(1.5f)); g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
            }
        };
        p.setOpaque(false);
        JLabel lbl=new JLabel((done?"✓  ":"•  ")+text);
        lbl.setForeground(done?new Color(100,255,100):new Color(255,180,180));
        lbl.setFont(new Font("Monospaced",Font.BOLD,12)); lbl.setBounds(10,8,w-20,20); p.add(lbl);
        return p;
    }

    private void drawOldMan(Graphics2D g2, int x, int y, int w, int h) {
        double sx=w/40.0,sy=h/80.0;
        g2.setColor(new Color(220,220,220)); g2.fillRoundRect(x+(int)(8*sx),y+(int)(2*sy),(int)(24*sx),(int)(14*sy),6,6);
        g2.fillRect(x+(int)(6*sx),y+(int)(5*sy),(int)(5*sx),(int)(10*sy)); g2.fillRect(x+(int)(29*sx),y+(int)(5*sy),(int)(5*sx),(int)(10*sy));
        g2.setColor(new Color(200,200,200)); g2.fillRoundRect(x+(int)(12*sx),y+(int)(24*sy),(int)(16*sx),(int)(10*sy),6,6);
        g2.setColor(new Color(200,160,110)); g2.fillRoundRect(x+(int)(10*sx),y+(int)(12*sy),(int)(20*sx),(int)(18*sy),8,8);
        g2.setColor(new Color(80,60,40)); g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(x+(int)(14*sx),y+(int)(17*sy),x+(int)(18*sx),y+(int)(17*sy));
        g2.drawLine(x+(int)(22*sx),y+(int)(17*sy),x+(int)(26*sx),y+(int)(17*sy));
        g2.setColor(new Color(170,120,80)); g2.setStroke(new BasicStroke(1));
        g2.drawLine(x+(int)(13*sx),y+(int)(22*sy),x+(int)(16*sx),y+(int)(21*sy));
        g2.drawLine(x+(int)(24*sx),y+(int)(21*sy),x+(int)(27*sx),y+(int)(22*sy));
        g2.setColor(new Color(80,50,130)); g2.fillRoundRect(x+(int)(8*sx),y+(int)(34*sy),(int)(24*sx),(int)(28*sy),6,6);
        g2.setColor(new Color(120,80,30)); g2.setStroke(new BasicStroke(3));
        g2.drawLine(x+(int)(36*sx),y+(int)(10*sy),x+(int)(36*sx),y+(int)(70*sy));
        g2.setColor(new Color(180,150,50)); g2.fillOval(x+(int)(32*sx),y+(int)(6*sy),(int)(8*sx),(int)(8*sy));
        g2.setColor(new Color(60,40,100));
        g2.fillRect(x+(int)(8*sx),y+(int)(60*sy),(int)(10*sx),(int)(16*sy));
        g2.fillRect(x+(int)(22*sx),y+(int)(60*sy),(int)(10*sx),(int)(16*sy));
    }

    private void closeCaveScene() {
        showCaveScene=false;
        if (cavePanel!=null) { this.remove(cavePanel); cavePanel=null; }
        this.revalidate(); this.repaint(); requestFocusInWindow();
    }

    // ══════════════════════════════════════════════════════════════
    // BOSS BATTLE (World 1)
    // ══════════════════════════════════════════════════════════════
    private void triggerBossFight() {
        if (bossTriggered||bossFightDone) return;
        int captured=1+capturedTeam.size();
        if (captured<6||playerFighter.level<15) {
            showFloatingMessage("Your creatures are not ready! Complete the quest first.",new Color(255,100,100)); return;
        }
        bossTriggered=true; inBattle=true;
        int savedX=playerX, savedY=playerY;
        Fighter bossKapre=Create.createKapre(15);
        Fighter bossEkek=Create.createEkek(15);
        Fighter bossSerina=createSerina(15);
        ArrayList<Fighter> bossTeam=new ArrayList<>();
        bossTeam.add(bossEkek); bossTeam.add(bossSerina);
        syncStateToGameScene();
        SwingUtilities.invokeLater(()->
                gameScene.switchToBossAt(playerFighter,bossKapre,bossTeam,
                        capturedTeam,scrollCount,lunasCount,potionCount,
                        savedX,savedY,()->{bossFightDone=true;portalVisible=true;bossTriggered=false;})
        );
    }

    private Fighter createSerina(int level) {
        Type SPIRIT=new Type("Spirit",0xAADDFF); Type WATER=new Type("Water",0x4488FF);
        ArrayList<Type> types=new ArrayList<>(Arrays.asList(SPIRIT,WATER));
        ArrayList<Move> allMoves=new ArrayList<>(Arrays.asList(
                new Move("Spirit Wave",SPIRIT,55,new ArrayList<>(),10),
                new Move("Aqua Dance",WATER,60,new ArrayList<>(),10),
                new Move("Soul Surge",SPIRIT,80,new ArrayList<>(),8),
                new Move("Tidal Crash",WATER,90,new ArrayList<>(),6)
        ));
        ArrayList<Integer> unlockLevels=new ArrayList<>(Arrays.asList(1,1,8,13));
        ArrayList<Stat> stats=new ArrayList<>();
        int extra=level-5;
        stats.add(new Stat("HP",180+extra*10)); stats.add(new Stat("ATK",88+extra*4));
        stats.add(new Stat("DEF",78+extra*3));  stats.add(new Stat("SPD",100+extra*3));
        return new Fighter("Serina","/images/Serina.png","/images/Serina.png",types,stats,allMoves,unlockLevels,level);
    }

    // ══════════════════════════════════════════════════════════════
    // WORLD 2 BOSS FIGHT
    // ══════════════════════════════════════════════════════════════
    private void triggerWorld2Boss() {
        if (w2BossTriggered || w2BossDone) return;
        if (!quest2Complete) {
            showFloatingMessage("Complete Quest 2 first! (Lv.25 creatures + cure old woman)", new Color(255, 100, 100));
            return;
        }
        w2BossTriggered = true;
        inBattle = true;
        int savedX = playerX, savedY = playerY;
        Fighter bossFirst = Create.createSigbin(25);
        Fighter boss2     = createBungisngis25();
        Fighter boss3     = createAmongmongo25();
        Fighter boss4     = createAmaninhig25();
        Fighter boss5     = createMangkukulam(25);
        ArrayList<Fighter> bossRest = new ArrayList<>();
        bossRest.add(boss2); bossRest.add(boss3); bossRest.add(boss4); bossRest.add(boss5);
        syncStateToGameScene();
        SwingUtilities.invokeLater(() ->
                gameScene.switchToBossAtOnMap(playerFighter, bossFirst, bossRest,
                        capturedTeam, scrollCount, lunasCount, potionCount,
                        savedX, savedY,
                        () -> { w2BossTriggered = false; antingAntingCount = Math.min(4, antingAntingCount + 1); },
                        "resources/World2.tmx")
        );
    }

    private void triggerWorld3Boss() {
        if (w3BossDone) return;
        if (!w3Quest3Complete) {
            showFloatingMessage("Complete Quest 3 first!", new Color(255, 100, 100));
            return;
        }

        inBattle = true;
        int savedX = playerX, savedY = playerY;

        Fighter bossFirst = Create.createBusaw(35);
        Fighter boss2     = Create.createKaperosa(35);
        Fighter boss3     = Create.createKolyog(35);
        Fighter boss4     = Create.createManananggal(35);
        Fighter boss5     = Create.createTikbalang(35);

        ArrayList<Fighter> bossRest = new ArrayList<>();
        bossRest.add(boss2);
        bossRest.add(boss3);
        bossRest.add(boss4);
        bossRest.add(boss5);

        syncStateToGameScene();
        SwingUtilities.invokeLater(() ->
                gameScene.switchToBossAtOnMap(
                        playerFighter, bossFirst, bossRest,
                        capturedTeam, scrollCount, lunasCount, potionCount,
                        savedX, savedY,
                        () -> { w3BossDone = true; antingAntingCount = Math.min(4, antingAntingCount + 1); },
                        "resources/World3.tmx")
        );
    }
    private void showKhaiDialog() {
        JPanel overlay = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(0, 0, 0, 220));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlay.setOpaque(false);
        overlay.setBounds(0, 0, 1280, 720);

        int bw = 900, bh = 320, bx = (1280-bw)/2, by = (720-bh)/2;
        JPanel box = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(10, 8, 20));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(180, 60, 60));
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
            }
        };
        box.setOpaque(false);
        box.setBounds(bx, by, bw, bh);

        String[] lines = {
                "Hey " + playerName + "... it's me, Sir Khai.",
                "I'm sorry for what I have done.",
                "I regret everything now...",
                "",
                "*A horse screams in the distance*",
                "",
                "Tikbalang: YOU HAVE FAILED ME KHAI!!!",
                "I will eat your soul and finish " + playerName + " myself!!!"
        };
        Color[] colors = {
                new Color(200, 220, 255), new Color(180, 180, 255),
                new Color(180, 180, 255), Color.WHITE,
                new Color(255, 215, 90), Color.WHITE,
                new Color(255, 80, 80), new Color(255, 80, 80)
        };

        int ly = 24;
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].isEmpty()) { ly += 10; continue; }
            JLabel lbl = new JLabel(lines[i]);
            lbl.setForeground(colors[i]);
            lbl.setFont(new Font("Monospaced",
                    lines[i].startsWith("Tikbalang") ? Font.BOLD : Font.PLAIN,
                    lines[i].startsWith("Tikbalang") ? 15 : 13));
            lbl.setBounds(20, ly, bw-40, 20);
            box.add(lbl);
            ly += 22;
        }

        final JPanel fOverlay = overlay;
        JButton btn = new JButton("FACE KHAIBALANG!");
        btn.setBackground(new Color(140, 20, 20));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Monospaced", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(255, 80, 80), 2));
        btn.setBounds(bw/2-120, bh-56, 240, 38);
        btn.addActionListener(e -> {
            WorldPanel.this.remove(fOverlay);
            WorldPanel.this.revalidate();
            WorldPanel.this.repaint();
            triggerKhaibalang();
        });
        box.add(btn);
        overlay.add(box);
        this.add(overlay);
        this.setComponentZOrder(overlay, 0);
        this.revalidate();
        this.repaint();
    }
    private void triggerKhaibalang() {
        inBattle = true;
        // Heal player first for the rematch mechanic
        healAllCreatures();

        Fighter khaibalang = Create.createKhaibalang();
        int savedX = playerX, savedY = playerY;

        syncStateToGameScene();
        SwingUtilities.invokeLater(() ->
                gameScene.switchToKhaiBoss(
                        playerFighter, khaibalang,
                        capturedTeam, scrollCount, lunasCount, potionCount,
                        savedX, savedY,
                        () -> { khaibalangDefeated = true; },
                        "resources/World3.tmx",
                        playerName
                )
        );
    }
    public void showGrandpaEndScene(String pName) {
        SwingUtilities.invokeLater(() -> {
            JPanel overlay = new JPanel(null) {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(new Color(0, 0, 0, 230));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
            };
            overlay.setOpaque(false);
            overlay.setBounds(0, 0, 1280, 720);

            int bw = 860, bh = 380, bx = (1280-bw)/2, by = (720-bh)/2;
            JPanel box = new JPanel(null) {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(8, 20, 8));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                    g2.setColor(new Color(80, 200, 80));
                    g2.setStroke(new BasicStroke(3));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                }
            };
            box.setOpaque(false);
            box.setBounds(bx, by, bw, bh);

            String[] lines = {
                    "Grandpa: " + pName + "! I'm here! I'm okay!",
                    "Here, take my Anting-Anting and save your teacher!",
                    "",
                    "[ You received Grandpa's Anting-Anting! ]",
                    "[ Anting-Anting: 4/4 — All collected! ]",
                    "",
                    "Khaibalang: NOOO!!! I WILL HAVE MY REVENGE!!!",
                    "\"poof\"",
                    "Sir Khai: ... are you okay, " + pName + "?",
                    "\"Sir Khai fainted from exhaustion\""
            };
            Color[] colors = {
                    new Color(255, 215, 90), new Color(200, 255, 200),
                    Color.WHITE,
                    new Color(100, 255, 150), new Color(100, 255, 150),
                    Color.WHITE,
                    new Color(255, 80, 80), new Color(200, 200, 200),
                    new Color(180, 220, 255), new Color(160, 160, 160)
            };

            int ly = 20;
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].isEmpty()) { ly += 8; continue; }
                JLabel lbl = new JLabel(lines[i]);
                lbl.setForeground(colors[i]);
                lbl.setFont(new Font("Monospaced", Font.PLAIN, 13));
                lbl.setBounds(20, ly, bw-40, 20);
                box.add(lbl);
                ly += 22;
            }

            antingAntingCount = 4;

            final JPanel fOverlay = overlay;
            JButton btn = new JButton("Continue...");
            btn.setBackground(new Color(20, 80, 20));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Monospaced", Font.BOLD, 14));
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(new Color(80, 200, 80), 2));
            btn.setBounds(bw/2-100, bh-52, 200, 36);
            btn.addActionListener(e -> {
                WorldPanel.this.remove(fOverlay);
                WorldPanel.this.revalidate();
                WorldPanel.this.repaint();
                showFinalChoice(pName);
            });
            box.add(btn);
            overlay.add(box);
            this.add(overlay);
            this.setComponentZOrder(overlay, 0);
            this.revalidate();
            this.repaint();
        });
    }
    private void showFinalChoice(String pName) {
        JPanel overlay = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(0, 0, 0, 240));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 215, 90));
                g2.setFont(new Font("Monospaced", Font.BOLD, 26));
                FontMetrics fm = g2.getFontMetrics();
                String line1 = "YOU SAVED YOUR GRANDPA AND SIR KHAI!";
                g2.drawString(line1, (1280-fm.stringWidth(line1))/2, 220);
                g2.setColor(new Color(200, 200, 200));
                g2.setFont(new Font("Monospaced", Font.PLAIN, 15));
                fm = g2.getFontMetrics();
                String line2 = "What will you do now, " + pName + "?";
                g2.drawString(line2, (1280-fm.stringWidth(line2))/2, 270);
            }
        };
        overlay.setOpaque(false);
        overlay.setBounds(0, 0, 1280, 720);

        // STAY button
        JButton stayBtn = new JButton("STAY — Become an Albularyo with Grandpa");
        stayBtn.setBackground(new Color(40, 100, 40));
        stayBtn.setForeground(Color.WHITE);
        stayBtn.setFont(new Font("Monospaced", Font.BOLD, 14));
        stayBtn.setFocusPainted(false);
        stayBtn.setBorder(BorderFactory.createLineBorder(new Color(80, 200, 80), 2));
        stayBtn.setBounds(240, 320, 800, 54);
        stayBtn.addActionListener(e -> {
            WorldPanel.this.remove(overlay);
            WorldPanel.this.revalidate();
            WorldPanel.this.repaint();
            showFloatingMessage("You stayed with your Grandpa. A new adventure begins!", new Color(100, 255, 150));
            // Reset boss flags so player can fight again
            bossFightDone = false; w2BossDone = false; w3BossDone = false;
            syncStateToGameScene();
        });

        // RETURN button
        JButton returnBtn = new JButton("RETURN — Go back to the city with Sir Khai");
        returnBtn.setBackground(new Color(40, 40, 120));
        returnBtn.setForeground(Color.WHITE);
        returnBtn.setFont(new Font("Monospaced", Font.BOLD, 14));
        returnBtn.setFocusPainted(false);
        returnBtn.setBorder(BorderFactory.createLineBorder(new Color(80, 120, 255), 2));
        returnBtn.setBounds(240, 390, 800, 54);
        returnBtn.addActionListener(e -> {
            WorldPanel.this.remove(overlay);
            WorldPanel.this.revalidate();
            WorldPanel.this.repaint();
            gameScene.showCredits(pName);
        });

        overlay.add(stayBtn);
        overlay.add(returnBtn);
        this.add(overlay);
        this.setComponentZOrder(overlay, 0);
        this.revalidate();
        this.repaint();
    }



    private Fighter createBungisngis25() {
        Type EARTH = new Type("Earth", 0xBB8833);
        ArrayList<Type> types = new ArrayList<>(Arrays.asList(EARTH));
        ArrayList<Move> allMoves = new ArrayList<>(Arrays.asList(
                new Move("Tackle",     new Type("Normal",0xAAAAAA), 35, new ArrayList<>(), 12),
                new Move("Rock Throw", EARTH, 50, new ArrayList<>(), 10),
                new Move("Stone Edge", EARTH, 80, new ArrayList<>(), 8),
                new Move("Rock Smash", EARTH, 95, new ArrayList<>(), 6)
        ));
        ArrayList<Integer> ul = new ArrayList<>(Arrays.asList(1, 1, 6, 10));
        ArrayList<Stat> stats = new ArrayList<>();
        int extra = 25 - 5;
        stats.add(new Stat("HP",  210 + extra * 10)); stats.add(new Stat("ATK", 100 + extra * 4));
        stats.add(new Stat("DEF", 110 + extra * 3));  stats.add(new Stat("SPD",  55 + extra * 3));
        return new Fighter("Bungisngis", "/images/Bungisngis.png", "/images/Bungisngis.png", types, stats, allMoves, ul, 25);
    }

    private Fighter createAmongmongo25() {
        Type EARTH = new Type("Earth", 0xBB8833);
        Type NORMAL = new Type("Normal", 0xAAAAAA);
        ArrayList<Type> types = new ArrayList<>(Arrays.asList(EARTH, NORMAL));
        ArrayList<Move> allMoves = new ArrayList<>(Arrays.asList(
                new Move("Scratch", NORMAL, 30, new ArrayList<>(), 12),
                new Move("Dig",     EARTH,  60, new ArrayList<>(), 10),
                new Move("Slash",   NORMAL, 70, new ArrayList<>(), 8),
                new Move("Fissure", EARTH,  90, new ArrayList<>(), 6)
        ));
        ArrayList<Integer> ul = new ArrayList<>(Arrays.asList(1, 1, 5, 11));
        ArrayList<Stat> stats = new ArrayList<>();
        int extra = 25 - 5;
        stats.add(new Stat("HP",  195 + extra * 10)); stats.add(new Stat("ATK",  90 + extra * 4));
        stats.add(new Stat("DEF",  95 + extra * 3));  stats.add(new Stat("SPD",  80 + extra * 3));
        return new Fighter("Amongmongo", "/images/Amongmongo.png", "/images/Amongmongo.png", types, stats, allMoves, ul, 25);
    }

    private Fighter createAmaninhig25() {
        Type SHADOW = new Type("Shadow", 0x6644AA);
        Type SPIRIT = new Type("Spirit", 0xAADDFF);
        ArrayList<Type> types = new ArrayList<>(Arrays.asList(SHADOW, SPIRIT));
        ArrayList<Move> allMoves = new ArrayList<>(Arrays.asList(
                new Move("Shadow Claw",  SHADOW, 45, new ArrayList<>(), 10),
                new Move("Spirit Pulse", SPIRIT, 55, new ArrayList<>(), 10),
                new Move("Night Slash",  SHADOW, 75, new ArrayList<>(), 8),
                new Move("Soul Drain",   SPIRIT, 90, new ArrayList<>(), 6)
        ));
        ArrayList<Integer> ul = new ArrayList<>(Arrays.asList(1, 1, 6, 12));
        ArrayList<Stat> stats = new ArrayList<>();
        int extra = 25 - 5;
        stats.add(new Stat("HP",  165 + extra * 10)); stats.add(new Stat("ATK",  95 + extra * 4));
        stats.add(new Stat("DEF",  70 + extra * 3));  stats.add(new Stat("SPD", 100 + extra * 3));
        return new Fighter("Amaninhig", "/images/Amaranhig.png", "/images/Amaranhig.png", types, stats, allMoves, ul, 25);
    }

    // ══════════════════════════════════════════════════════════════
    // WORLD 1 PORTAL
    // ══════════════════════════════════════════════════════════════
    private void drawPortal(Graphics2D g2) {
        if (!portalVisible) return;
        int tileX=39*TILE_DISPLAY_SIZE-cameraX;
        int tileY=36*TILE_DISPLAY_SIZE-cameraY;
        if (tileX+TILE_DISPLAY_SIZE<0||tileX>1280||tileY+TILE_DISPLAY_SIZE<0||tileY>720) return;
        long t=System.currentTimeMillis();
        float bounce=(float)(Math.sin(t/300.0)*5);
        float pulse=(float)(Math.sin(t/400.0)*0.3+0.7);
        int cx=tileX+TILE_DISPLAY_SIZE/2;
        int cy=tileY+TILE_DISPLAY_SIZE/2+(int)bounce;
        g2.setColor(new Color(100,50,220,(int)(70*pulse)));
        g2.fillOval(cx-22,cy-22,44,44);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(220,180,255));
        g2.setStroke(new BasicStroke(3,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g2.drawLine(cx-10,cy,cx+8,cy);
        int[] arrowX={cx+8,cx+1,cx+1};
        int[] arrowY={cy,cy-7,cy+7};
        g2.setColor(new Color(200,160,255));
        g2.fillPolygon(arrowX,arrowY,3);
        g2.setColor(new Color(180,130,255,(int)(200*pulse)));
        int sparkR=18;
        for (int i=0;i<4;i++) {
            double angle=Math.toRadians((t/6+i*90)%360);
            int sx2=cx+(int)(sparkR*Math.cos(angle));
            int sy2=cy+(int)(sparkR*Math.sin(angle));
            g2.fillOval(sx2-3,sy2-3,6,6);
        }
        g2.setColor(new Color(220,180,255));
        g2.setFont(new Font("Monospaced",Font.BOLD,10));
        FontMetrics fm=g2.getFontMetrics();
        String label="WORLD 2 ▶";
        g2.drawString(label,cx-fm.stringWidth(label)/2,tileY-4+(int)bounce);
    }

    // ══════════════════════════════════════════════════════════════
    // WORLD 2 PORTAL
    // ══════════════════════════════════════════════════════════════
    private void drawWorld2Portal(Graphics2D g2) {
        if (!w2PortalVisible) return;
        int tileX = 21 * TILE_DISPLAY_SIZE - cameraX;
        int tileY = 11 * TILE_DISPLAY_SIZE - cameraY;
        if (tileX + TILE_DISPLAY_SIZE < 0 || tileX > 1280 || tileY + TILE_DISPLAY_SIZE < 0 || tileY > 720) return;
        long t = System.currentTimeMillis();
        float bounce = (float)(Math.sin(t / 300.0) * 5);
        float pulse  = (float)(Math.sin(t / 400.0) * 0.3 + 0.7);
        int cx = tileX + TILE_DISPLAY_SIZE / 2;
        int cy = tileY + TILE_DISPLAY_SIZE / 2 + (int)bounce;
        g2.setColor(new Color(50, 200, 255, (int)(70 * pulse)));
        g2.fillOval(cx - 24, cy - 24, 48, 48);
        g2.setColor(new Color(100, 220, 255, (int)(40 * pulse)));
        g2.fillOval(cx - 32, cy - 32, 64, 64);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(180, 240, 255));
        g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(cx - 10, cy, cx + 8, cy);
        int[] arrowX = {cx + 8, cx + 1, cx + 1};
        int[] arrowY = {cy, cy - 7, cy + 7};
        g2.setColor(new Color(150, 220, 255));
        g2.fillPolygon(arrowX, arrowY, 3);
        g2.setColor(new Color(100, 220, 255, (int)(200 * pulse)));
        int sparkR = 20;
        for (int i = 0; i < 4; i++) {
            double angle = Math.toRadians((t / 6 + i * 90) % 360);
            int sx = cx + (int)(sparkR * Math.cos(angle));
            int sy = cy + (int)(sparkR * Math.sin(angle));
            g2.fillOval(sx - 3, sy - 3, 6, 6);
        }
        g2.setColor(new Color(180, 240, 255));
        g2.setFont(new Font("Monospaced", Font.BOLD, 10));
        FontMetrics fm = g2.getFontMetrics();
        String label = "WORLD 3 ▶";
        String sublabel = w2PortalVisible ? "(Enter)" : "(Locked)";
        g2.drawString(label, cx - fm.stringWidth(label) / 2, tileY - 16 + (int)bounce);
        g2.setColor(new Color(120, 200, 220));
        g2.setFont(new Font("Monospaced", Font.PLAIN, 9));
        fm = g2.getFontMetrics();
        g2.drawString(sublabel, cx - fm.stringWidth(sublabel) / 2, tileY - 4 + (int)bounce);
    }

    // ══════════════════════════════════════════════════════════════
    // PORTAL CINEMATIC — World 2
    // ══════════════════════════════════════════════════════════════
    private void enterPortalToWorld2() {
        JPanel overlay=new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(10,0,25)); g2.fillRect(0,0,getWidth(),getHeight());
                long t=System.currentTimeMillis();
                for (int ring=5;ring>=0;ring--) {
                    float pulse=(float)(Math.sin((t/300.0)+ring*0.8)*0.3+0.7);
                    int alpha=(int)(40*pulse), margin=ring*30;
                    g2.setColor(new Color(120,60,255,alpha));
                    g2.fillOval(margin,margin,getWidth()-margin*2,getHeight()-margin*2);
                }
                int cx=getWidth()/2, cy=getHeight()/2-80;
                float pulse2=(float)(Math.sin(t/500.0)*0.25+0.75);
                g2.setColor(new Color(80,30,180,(int)(120*pulse2))); g2.fillOval(cx-80,cy-80,160,160);
                g2.setColor(new Color(160,100,255,(int)(200*pulse2))); g2.fillOval(cx-50,cy-50,100,100);
                g2.setColor(new Color(220,180,255)); g2.fillOval(cx-20,cy-20,40,40);
                g2.setColor(new Color(200,150,255)); g2.setFont(new Font("Monospaced",Font.BOLD,22));
                FontMetrics fm=g2.getFontMetrics(); String portalTitle="✦ ENTERING THE PORTAL ✦";
                g2.drawString(portalTitle,cx-fm.stringWidth(portalTitle)/2,cy+100);
                int bw=900,bh=170,bx=cx-bw/2,by=cy+120;
                g2.setColor(new Color(15,5,35,230)); g2.fillRoundRect(bx,by,bw,bh,16,16);
                g2.setColor(new Color(120,70,220)); g2.setStroke(new BasicStroke(2)); g2.drawRoundRect(bx,by,bw,bh,16,16);
                String[] lines={"You are now traveling to the next town...","You'll face tons of new creatures","and defeat the next Albularyo!","Collect all 4 Anting-Anting to save your Grandpa!"};
                Color[] lineColors={new Color(220,200,255),new Color(180,160,255),new Color(255,160,160),new Color(255,215,90)};
                int lineY=by+30;
                for (int i=0;i<lines.length;i++) {
                    g2.setColor(lineColors[i]);
                    g2.setFont(new Font("Monospaced",i==0?Font.BOLD:Font.PLAIN,i==0?16:14));
                    FontMetrics lfm=g2.getFontMetrics();
                    g2.drawString(lines[i],cx-lfm.stringWidth(lines[i])/2,lineY); lineY+=26;
                }
                g2.setColor(new Color(140,110,200)); g2.setFont(new Font("Monospaced",Font.PLAIN,11));
                String hint="[ Click anywhere to enter World 2 ]"; FontMetrics hfm=g2.getFontMetrics();
                g2.drawString(hint,cx-hfm.stringWidth(hint)/2,by+bh-14);
            }
        };
        overlay.setOpaque(true); overlay.setBackground(new Color(10,0,25)); overlay.setBounds(0,0,1280,720);
        Timer animTimer=new Timer(16,e->overlay.repaint());
        animTimer.start();
        overlay.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                animTimer.stop();
                WorldPanel.this.remove(overlay);
                WorldPanel.this.revalidate(); WorldPanel.this.repaint();
                gameScene.switchToWorld2(playerFighter,capturedTeam,scrollCount,lunasCount,potionCount,playerCoins,gameStartTime,adminMode,caveSceneShown,antingAntingCount);
            }
        });
        this.add(overlay); this.setComponentZOrder(overlay,0);
        this.revalidate(); this.repaint();
    }

    // ══════════════════════════════════════════════════════════════
    // PORTAL CINEMATIC — World 3
    // ══════════════════════════════════════════════════════════════
    private void enterPortalToWorld3() {
        JPanel overlay = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 10, 25)); g2.fillRect(0, 0, getWidth(), getHeight());
                long t = System.currentTimeMillis();
                for (int ring = 5; ring >= 0; ring--) {
                    float pulse = (float)(Math.sin((t / 300.0) + ring * 0.8) * 0.3 + 0.7);
                    int alpha = (int)(40 * pulse), margin = ring * 30;
                    g2.setColor(new Color(50, 180, 255, alpha));
                    g2.fillOval(margin, margin, getWidth() - margin * 2, getHeight() - margin * 2);
                }
                int cx = getWidth() / 2, cy = getHeight() / 2 - 80;
                float pulse2 = (float)(Math.sin(t / 500.0) * 0.25 + 0.75);
                g2.setColor(new Color(20, 80, 180, (int)(120 * pulse2)));
                g2.fillOval(cx - 80, cy - 80, 160, 160);
                g2.setColor(new Color(80, 180, 255, (int)(200 * pulse2)));
                g2.fillOval(cx - 50, cy - 50, 100, 100);
                g2.setColor(new Color(200, 240, 255));
                g2.fillOval(cx - 20, cy - 20, 40, 40);
                g2.setColor(new Color(150, 220, 255));
                g2.setFont(new Font("Monospaced", Font.BOLD, 22));
                FontMetrics fm = g2.getFontMetrics();
                String portalTitle = "✦ ENTERING WORLD 3 ✦";
                g2.drawString(portalTitle, cx - fm.stringWidth(portalTitle) / 2, cy + 100);
                int bw = 900, bh = 170, bx = cx - bw / 2, by = cy + 120;
                g2.setColor(new Color(5, 15, 35, 230));
                g2.fillRoundRect(bx, by, bw, bh, 16, 16);
                g2.setColor(new Color(60, 120, 220));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(bx, by, bw, bh, 16, 16);
                String[] lines = {
                        "You are now traveling to the third town...",
                        "Stronger creatures and challenges await you!",
                        "Defeat the next Albularyo to save your Grandpa!",
                        "Collect all 4 Anting-Anting — you're almost there!"
                };
                Color[] lineColors = {
                        new Color(200, 230, 255),
                        new Color(150, 200, 255),
                        new Color(255, 160, 160),
                        new Color(255, 215, 90)
                };
                int lineY = by + 30;
                for (int i = 0; i < lines.length; i++) {
                    g2.setColor(lineColors[i]);
                    g2.setFont(new Font("Monospaced", i == 0 ? Font.BOLD : Font.PLAIN, i == 0 ? 16 : 14));
                    FontMetrics lfm = g2.getFontMetrics();
                    g2.drawString(lines[i], cx - lfm.stringWidth(lines[i]) / 2, lineY);
                    lineY += 26;
                }
                g2.setColor(new Color(100, 150, 200));
                g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
                String hint = "[ Click anywhere to enter World 3 ]";
                FontMetrics hfm = g2.getFontMetrics();
                g2.drawString(hint, cx - hfm.stringWidth(hint) / 2, by + bh - 14);
            }
        };
        overlay.setOpaque(true);
        overlay.setBackground(new Color(0, 10, 25));
        overlay.setBounds(0, 0, 1280, 720);
        Timer animTimer = new Timer(16, e -> overlay.repaint());
        animTimer.start();
        overlay.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                animTimer.stop();
                WorldPanel.this.remove(overlay);
                WorldPanel.this.revalidate();
                WorldPanel.this.repaint();
                gameScene.switchToWorld3(
                        playerFighter, capturedTeam,
                        scrollCount, lunasCount, potionCount,
                        playerCoins, gameStartTime,
                        adminMode, caveSceneShown, antingAntingCount);
            }
        });
        this.add(overlay);
        this.setComponentZOrder(overlay, 0);
        this.revalidate();
        this.repaint();
    }

    // ══════════════════════════════════════════════════════════════
    // SHOP (World 1 — Joshua)
    // ══════════════════════════════════════════════════════════════
    private void openShop() { if (showShop) return; showShop=true; buildShopOverlay(); }
    private void closeShop() {
        showShop=false;
        if (shopOverlay!=null) { this.remove(shopOverlay); shopOverlay=null; this.revalidate(); this.repaint(); }
        requestFocusInWindow();
    }

    private void buildShopOverlay() {
        if (shopOverlay!=null) { this.remove(shopOverlay); shopOverlay=null; }
        int screenW=1280,screenH=720,winW=720,winH=520;
        int winX=(screenW-winW)/2,winY=(screenH-winH)/2;
        final int fwinX=winX,fwinW=winW,fwinH=winH,fwinY=winY;
        shopOverlay=new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0,0,0,190)); g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(new Color(18,12,5)); g2.fillRoundRect(fwinX,fwinY,fwinW,fwinH,18,18);
                g2.setColor(new Color(170,120,50)); g2.setStroke(new BasicStroke(3)); g2.drawRoundRect(fwinX,fwinY,fwinW,fwinH,18,18);
                g2.setColor(new Color(70,44,8)); g2.fillRoundRect(fwinX,fwinY,fwinW,48,18,18); g2.fillRect(fwinX,fwinY+28,fwinW,20);
                g2.setColor(new Color(255,215,90)); g2.setFont(new Font("Monospaced",Font.BOLD,20));
                FontMetrics fm=g2.getFontMetrics(); g2.drawString("SHOP",fwinX+(fwinW-fm.stringWidth("SHOP"))/2,fwinY+33);
                g2.setFont(new Font("Monospaced",Font.BOLD,13)); g2.drawString("Coins: "+playerCoins,fwinX+fwinW-150,fwinY+33);
                g2.setColor(new Color(170,120,50)); g2.setStroke(new BasicStroke(1.5f)); g2.drawLine(fwinX+16,fwinY+50,fwinX+fwinW-16,fwinY+50);
                g2.setColor(new Color(120,120,120)); g2.setFont(new Font("Monospaced",Font.PLAIN,11));
                FontMetrics hfm=g2.getFontMetrics(); String hint="Press [E] to close";
                g2.drawString(hint,fwinX+(fwinW-hfm.stringWidth(hint))/2,fwinY+fwinH-12);
                drawJoshua(g2,fwinX+14,fwinY+230,110,200);
            }
        };
        shopOverlay.setOpaque(false); shopOverlay.setBounds(0,0,screenW,screenH);

        int bubbleX=winX+130, bubbleY=winY+240, bubbleW=winW-150, bubbleH=60;
        JPanel bubble=new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,250,230)); g2.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
                g2.setColor(new Color(170,120,50)); g2.setStroke(new BasicStroke(2)); g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,14,14);
                int[] tx={0,-14,0}; int[] ty={getHeight()/2-8,getHeight()/2,getHeight()/2+8};
                g2.setColor(new Color(255,250,230)); g2.fillPolygon(tx,ty,3);
                g2.setColor(new Color(170,120,50)); g2.setStroke(new BasicStroke(2));
                g2.drawLine(0,getHeight()/2-8,-14,getHeight()/2); g2.drawLine(-14,getHeight()/2,0,getHeight()/2+8);
            }
        };
        bubble.setOpaque(false); bubble.setBounds(bubbleX,bubbleY,bubbleW,bubbleH);
        JLabel bl1=new JLabel("Hi! I'm Joshua, your local merchant!");
        bl1.setForeground(new Color(60,30,5)); bl1.setFont(new Font("Monospaced",Font.BOLD,13));
        bl1.setBounds(12,8,bubbleW-20,20); bubble.add(bl1);
        JLabel bl2=new JLabel("What would you like to buy today?");
        bl2.setForeground(new Color(100,60,10)); bl2.setFont(new Font("Monospaced",Font.PLAIN,12));
        bl2.setBounds(12,30,bubbleW-20,18); bubble.add(bl2);
        shopOverlay.add(bubble);

        int itemY=winY+60, itemX=winX+24, itemW=winW-48, rowH=56, gap=6;
        Object[][] items={{"Scroll","Capture wild creatures in battle.",30,"scroll"},{"Lunas","Restore 5 PP to one creature move.",25,"lunas"},{"Potion","Restore 30 HP to one creature.",25,"potion"}};
        for (Object[] item:items) {
            shopOverlay.add(buildShopRow(itemX,itemY,itemW,rowH,(String)item[0],(String)item[1],(int)item[2],(String)item[3]));
            itemY+=rowH+gap;
        }
        JButton leaveBtn=new JButton("LEAVE SHOP");
        leaveBtn.setBackground(new Color(100,50,10)); leaveBtn.setForeground(Color.WHITE);
        leaveBtn.setFont(new Font("Monospaced",Font.BOLD,14)); leaveBtn.setFocusPainted(false);
        leaveBtn.setBorder(BorderFactory.createLineBorder(new Color(170,120,50),2));
        leaveBtn.setBounds(winX+winW/2-100,winY+winH-50,200,36);
        leaveBtn.addActionListener(e->closeShop()); shopOverlay.add(leaveBtn);
        this.add(shopOverlay); this.setComponentZOrder(shopOverlay,0); this.revalidate(); this.repaint();
    }

    private void drawJoshua(Graphics2D g2, int x, int y, int w, int h) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        double sx=w/40.0,sy=h/80.0;
        g2.setColor(new Color(60,35,10)); g2.fillRoundRect(x+(int)(10*sx),y+(int)(2*sy),(int)(20*sx),(int)(12*sy),6,6);
        g2.fillRect(x+(int)(8*sx),y+(int)(5*sy),(int)(4*sx),(int)(8*sy)); g2.fillRect(x+(int)(28*sx),y+(int)(5*sy),(int)(4*sx),(int)(8*sy));
        g2.setColor(new Color(230,185,140)); g2.fillRoundRect(x+(int)(10*sx),y+(int)(10*sy),(int)(20*sx),(int)(18*sy),8,8);
        g2.setColor(new Color(50,80,160)); g2.fillOval(x+(int)(13*sx),y+(int)(15*sy),(int)(4*sx),(int)(4*sy)); g2.fillOval(x+(int)(23*sx),y+(int)(15*sy),(int)(4*sx),(int)(4*sy));
        g2.setColor(Color.BLACK); g2.fillOval(x+(int)(14*sx),y+(int)(16*sy),(int)(2*sx),(int)(2*sy)); g2.fillOval(x+(int)(24*sx),y+(int)(16*sy),(int)(2*sx),(int)(2*sy));
        g2.setColor(new Color(60,35,10)); g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(x+(int)(12*sx),y+(int)(13*sy),x+(int)(18*sx),y+(int)(12*sy)); g2.drawLine(x+(int)(22*sx),y+(int)(12*sy),x+(int)(28*sx),y+(int)(13*sy));
        g2.setColor(new Color(180,100,80)); g2.setStroke(new BasicStroke(1.5f)); g2.drawArc(x+(int)(15*sx),y+(int)(22*sy),(int)(10*sx),(int)(5*sy),200,140);
        g2.setColor(new Color(200,150,110)); g2.fillOval(x+(int)(18*sx),y+(int)(19*sy),(int)(4*sx),(int)(3*sy));
        g2.setColor(new Color(230,185,140)); g2.fillRect(x+(int)(16*sx),y+(int)(27*sy),(int)(8*sx),(int)(5*sy));
        g2.setColor(new Color(60,110,60)); g2.fillRoundRect(x+(int)(8*sx),y+(int)(32*sy),(int)(24*sx),(int)(26*sy),6,6);
        g2.setColor(new Color(220,200,160)); g2.fillRect(x+(int)(15*sx),y+(int)(32*sy),(int)(10*sx),(int)(10*sy));
        g2.setColor(new Color(80,50,10));
        int[] lx={x+(int)(15*sx),x+(int)(10*sx),x+(int)(15*sx)}; int[] ly={y+(int)(32*sy),y+(int)(38*sy),y+(int)(42*sy)}; g2.fillPolygon(lx,ly,3);
        int[] rx2={x+(int)(25*sx),x+(int)(30*sx),x+(int)(25*sx)}; int[] ry2={y+(int)(32*sy),y+(int)(38*sy),y+(int)(42*sy)}; g2.fillPolygon(rx2,ry2,3);
        g2.setColor(new Color(60,110,60)); g2.fillRoundRect(x+(int)(2*sx),y+(int)(32*sy),(int)(8*sx),(int)(22*sy),4,4); g2.fillRoundRect(x+(int)(30*sx),y+(int)(30*sy),(int)(8*sx),(int)(22*sy),4,4);
        g2.setColor(new Color(230,185,140)); g2.fillOval(x+(int)(3*sx),y+(int)(52*sy),(int)(7*sx),(int)(6*sy)); g2.fillOval(x+(int)(30*sx),y+(int)(50*sy),(int)(7*sx),(int)(6*sy));
        g2.setColor(new Color(180,140,40)); g2.fillOval(x+(int)(31*sx),y+(int)(44*sy),(int)(9*sx),(int)(11*sy));
        g2.setColor(new Color(220,180,60)); g2.fillOval(x+(int)(33*sx),y+(int)(42*sy),(int)(5*sx),(int)(5*sy));
        g2.setColor(new Color(50,60,100)); g2.fillRect(x+(int)(8*sx),y+(int)(57*sy),(int)(10*sx),(int)(18*sy)); g2.fillRect(x+(int)(22*sx),y+(int)(57*sy),(int)(10*sx),(int)(18*sy));
        g2.setColor(new Color(60,40,20)); g2.fillRoundRect(x+(int)(6*sx),y+(int)(72*sy),(int)(13*sx),(int)(7*sy),4,4); g2.fillRoundRect(x+(int)(21*sx),y+(int)(72*sy),(int)(13*sx),(int)(7*sy),4,4);
    }

    private JPanel buildShopRow(int x, int y, int w, int h, String name, String desc, int cost, String key) {
        JPanel row=new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30,20,6)); g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g2.setColor(new Color(100,70,20)); g2.setStroke(new BasicStroke(1)); g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
            }
        };
        row.setOpaque(false); row.setBounds(x,y,w,h);
        JLabel nl=new JLabel(name); nl.setForeground(Color.WHITE); nl.setFont(new Font("Monospaced",Font.BOLD,14)); nl.setBounds(12,6,220,20); row.add(nl);
        JLabel dl=new JLabel(desc); dl.setForeground(new Color(160,160,160)); dl.setFont(new Font("Monospaced",Font.PLAIN,11)); dl.setBounds(12,26,w-230,16); row.add(dl);
        JLabel cl=new JLabel(cost+" Coins"); cl.setForeground(new Color(255,215,90)); cl.setFont(new Font("Monospaced",Font.BOLD,12)); cl.setBounds(w-230,6,100,20); row.add(cl);
        JLabel sl=new JLabel("Have: "+getCurrentItemCount(key)); sl.setForeground(new Color(140,200,140)); sl.setFont(new Font("Monospaced",Font.PLAIN,11)); sl.setBounds(w-230,26,100,16); row.add(sl);
        JButton buy1=shopBtn("Buy x1",new Color(40,110,40)); buy1.setBounds(w-120,6,104,20); buy1.addActionListener(e->{buyItem(key,cost,1);buildShopOverlay();}); row.add(buy1);
        JButton buy5=shopBtn("Buy x5",new Color(30,80,30)); buy5.setBounds(w-120,30,104,20); buy5.addActionListener(e->{buyItem(key,cost,5);buildShopOverlay();}); row.add(buy5);
        return row;
    }

    private JButton shopBtn(String text, Color bg) {
        JButton btn=new JButton(text); btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Monospaced",Font.BOLD,11)); btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(60,160,60),1)); return btn;
    }

    private int getCurrentItemCount(String key) {
        switch (key) { case "scroll": return scrollCount; case "lunas": return lunasCount; case "potion": return potionCount; default: return 0; }
    }

    private void buyItem(String key, int cost, int qty) {
        int total=cost*qty;
        if (playerCoins<total) { showFloatingMessage("Not enough Coins! Need "+total,new Color(220,60,60)); return; }
        playerCoins-=total;
        switch (key) { case "scroll": scrollCount+=qty; break; case "lunas": lunasCount+=qty; break; case "potion": potionCount+=qty; break; }
        showFloatingMessage("Bought "+qty+"x "+key+" for "+total+" coins!",new Color(100,255,150));
    }

    // ══════════════════════════════════════════════════════════════
    // HEAL
    // ══════════════════════════════════════════════════════════════
    private void healAllCreatures() { healFighter(playerFighter); for (Fighter f:capturedTeam) healFighter(f); }
    private void healFighter(Fighter f) {
        f.stats.get(0).value=f.stats.get(0).base; f.fainted=false;
        for (Move m:f.moveset) { if (!m.isLocked()) m.pp=m.maxPp; }
    }

    // ══════════════════════════════════════════════════════════════
    // FLOATING MESSAGE
    // ══════════════════════════════════════════════════════════════
    private void showFloatingMessage(String message, Color color) {
        JPanel overlay=new JPanel(null) { @Override protected void paintComponent(Graphics g) { super.paintComponent(g); g.setColor(new Color(0,0,0,150)); g.fillRect(0,0,getWidth(),getHeight()); }};
        overlay.setOpaque(false); overlay.setBounds(0,0,1280,720);
        int rw=560,rh=80,rx=(1280-rw)/2,ry=(720-rh)/2;
        JPanel box=new JPanel(new BorderLayout()); box.setBackground(new Color(12,25,12)); box.setBorder(BorderFactory.createLineBorder(color,2)); box.setBounds(rx,ry,rw,rh);
        JLabel lbl=new JLabel(message,SwingConstants.CENTER); lbl.setForeground(color); lbl.setFont(new Font("Monospaced",Font.BOLD,13)); box.add(lbl,BorderLayout.CENTER);
        overlay.add(box); this.add(overlay); this.setComponentZOrder(overlay,0); this.revalidate(); this.repaint();
        new Timer(2500,e->{((Timer)e.getSource()).stop();this.remove(overlay);this.revalidate();this.repaint();}).start();
    }

    // ══════════════════════════════════════════════════════════════
    // BAG OVERLAY
    // ══════════════════════════════════════════════════════════════
    private void openBag() {
        showBag=true; selectedIndex=-1; selectedCreatureIndex=-1;
        pendingItemUse=""; pendingTarget=null; buildBagOverlay("main");
    }

    private void closeBag() {
        showBag=false; selectedIndex=-1; selectedCreatureIndex=-1;
        pendingItemUse=""; pendingTarget=null;
        if (bagOverlay!=null) { this.remove(bagOverlay); bagOverlay=null; this.repaint(); }
    }

    private void buildBagOverlay(String screen) {
        if (bagOverlay!=null) { this.remove(bagOverlay); bagOverlay=null; }
        int screenW=1280,screenH=720,winW=860,winH=520;
        int winX=(screenW-winW)/2,winY=(screenH-winH)/2;
        bagOverlay=new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0,0,0,190)); g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(new Color(22,14,6)); g2.fillRoundRect(winX,winY,winW,winH,18,18);
                g2.setColor(new Color(170,120,50)); g2.setStroke(new BasicStroke(3)); g2.drawRoundRect(winX,winY,winW,winH,18,18);
                g2.setColor(new Color(70,44,8)); g2.fillRoundRect(winX,winY,winW,48,18,18); g2.fillRect(winX,winY+28,winW,20);
                g2.setColor(new Color(255,215,90)); g2.setFont(new Font("Monospaced",Font.BOLD,22));
                FontMetrics fm=g2.getFontMetrics(); g2.drawString("BAG",winX+(winW-fm.stringWidth("BAG"))/2,winY+34);
                g2.setFont(new Font("Monospaced",Font.BOLD,12)); g2.drawString("Coins: "+playerCoins,winX+winW-140,winY+34);
                g2.setColor(new Color(170,120,50)); g2.setStroke(new BasicStroke(1.5f)); g2.drawLine(winX+16,winY+50,winX+winW-16,winY+50);
                g2.setColor(new Color(120,120,120)); g2.setFont(new Font("Monospaced",Font.PLAIN,12));
                FontMetrics hfm=g2.getFontMetrics(); String hint="Press [B] to close";
                g2.drawString(hint,winX+(winW-hfm.stringWidth(hint))/2,winY+winH-14);
            }
        };
        bagOverlay.setOpaque(false); bagOverlay.setBounds(0,0,screenW,screenH);
        if (screen.equals("main")) buildMainBagScreen(bagOverlay,winX,winY,winW,winH);
        else if (screen.equals("creatureSelect")) buildCreatureSelectScreen(bagOverlay,winX,winY,winW,winH);
        else if (screen.equals("confirm")) buildConfirmScreen(bagOverlay,winX,winY,winW,winH);
        this.add(bagOverlay); this.setComponentZOrder(bagOverlay,0); this.revalidate(); this.repaint();
    }

    private void buildMainBagScreen(JPanel overlay, int winX, int winY, int winW, int winH) {
        final int leftX   = winX + 14;
        final int leftW   = 370;
        final int rightX  = leftX + leftW + 14;
        final int rightW  = winW - leftW - 42;
        final int topY    = winY + 56;
        final int bottomY = winY + winH - 36;
        final int totalH  = bottomY - topY;
        final int splitH     = totalH / 2;
        final int itemsTop   = topY;
        final int itemsH     = splitH - 10;
        final int teamTop    = topY + splitH;
        final int teamH      = totalH - splitH;
        final int rowH = 40, gap = 4;

        JLabel itemsHeader = new JLabel("ITEMS");
        itemsHeader.setForeground(new Color(200,155,60));
        itemsHeader.setFont(new Font("Monospaced", Font.BOLD, 12));
        itemsHeader.setBounds(leftX, itemsTop, leftW, 16);
        overlay.add(itemsHeader);

        JPanel itemListPanel = new JPanel();
        itemListPanel.setOpaque(false);
        itemListPanel.setLayout(new BoxLayout(itemListPanel, BoxLayout.Y_AXIS));

        String[] iNames  = {"Scroll","Lunas","Potion"};
        int[]    iCounts = {scrollCount, lunasCount, potionCount};
        Color[]  iColors = {new Color(140,70,200), new Color(60,180,100), new Color(60,140,220)};
        for (int i = 0; i < 3; i++) {
            final int idx = i;
            boolean sel = (selectedIndex == idx && selectedCreatureIndex == -1);
            JButton btn = styledItemBtn(iNames[i], iCounts[i], iColors[i], sel);
            btn.setMaximumSize(new Dimension(leftW, rowH));
            btn.setPreferredSize(new Dimension(leftW, rowH));
            btn.addActionListener(e -> {
                selectedIndex = (selectedIndex == idx && selectedCreatureIndex == -1) ? -1 : idx;
                selectedCreatureIndex = -1;
                buildBagOverlay("main");
            });
            itemListPanel.add(btn);
            itemListPanel.add(Box.createVerticalStrut(gap));
        }

        if (isWorld2() || isWorld3()) {
            String[] w2Names  = {"Super Lunas","Super Potion","Super Scroll"};
            int[]    w2Counts = {superLunasCount, superPotionCount, superScrollCount};
            Color[]  w2Colors = {new Color(80,200,160), new Color(80,160,220), new Color(160,80,220)};
            int[]    w2Idx    = {3, 4, 5};
            for (int i = 0; i < 3; i++) {
                final int idx = w2Idx[i];
                boolean sel = (selectedIndex == idx && selectedCreatureIndex == -1);
                JButton btn = styledItemBtn(w2Names[i], w2Counts[i], w2Colors[i], sel);
                btn.setMaximumSize(new Dimension(leftW, rowH));
                btn.setPreferredSize(new Dimension(leftW, rowH));
                btn.addActionListener(e -> {
                    selectedIndex = (selectedIndex == idx && selectedCreatureIndex == -1) ? -1 : idx;
                    selectedCreatureIndex = -1;
                    buildBagOverlay("main");
                });
                itemListPanel.add(btn);
                itemListPanel.add(Box.createVerticalStrut(gap));
            }
            if (peksonGaveAnting2) {
                boolean sel = (selectedIndex == 10 && selectedCreatureIndex == -1);
                JButton aBtn = styledItemBtn("Anting2 (" + (anting2Active ? "WORN" : "UNUSED") + ")", 1, new Color(255,180,50), sel);
                aBtn.setMaximumSize(new Dimension(leftW, rowH));
                aBtn.setPreferredSize(new Dimension(leftW, rowH));
                aBtn.addActionListener(e -> {
                    selectedIndex = (selectedIndex == 10 && selectedCreatureIndex == -1) ? -1 : 10;
                    selectedCreatureIndex = -1;
                    buildBagOverlay("main");
                });
                itemListPanel.add(aBtn);
                itemListPanel.add(Box.createVerticalStrut(gap));
            }
        }

        JScrollPane itemsScroll = new JScrollPane(itemListPanel);
        itemsScroll.setOpaque(false);
        itemsScroll.getViewport().setOpaque(false);
        itemsScroll.setBorder(null);
        itemsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        itemsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        itemsScroll.setBounds(leftX, itemsTop + 20, leftW, itemsH - 20);
        overlay.add(itemsScroll);

        JLabel teamHeader = new JLabel("TEAM  (" + (capturedTeam.size() + 1) + "/6)");
        teamHeader.setForeground(new Color(200,155,60));
        teamHeader.setFont(new Font("Monospaced", Font.BOLD, 12));
        teamHeader.setBounds(leftX, teamTop, leftW, 16);
        overlay.add(teamHeader);

        JPanel divLine = new JPanel();
        divLine.setBackground(new Color(100,70,15));
        divLine.setBounds(leftX, teamTop - 4, leftW, 1);
        overlay.add(divLine);

        ArrayList<Fighter> fullTeam = new ArrayList<>();
        fullTeam.add(playerFighter);
        fullTeam.addAll(capturedTeam);

        JPanel teamListPanel = new JPanel();
        teamListPanel.setOpaque(false);
        teamListPanel.setLayout(new BoxLayout(teamListPanel, BoxLayout.Y_AXIS));

        for (int i = 0; i < fullTeam.size(); i++) {
            final int ci = i;
            Fighter f = fullTeam.get(i);
            boolean isActive = (f == playerFighter), isSel = (selectedCreatureIndex == i);
            int hp    = (int) Math.max(0, f.stats.get(0).value);
            int maxHp = (int) f.stats.get(0).base;
            float ratio = maxHp > 0 ? (float) hp / maxHp : 0;
            Color barColor    = ratio > 0.5f ? new Color(60,200,60) : ratio > 0.25f ? new Color(220,180,0) : new Color(200,50,50);
            Color borderColor = isSel ? new Color(100,180,255) : isActive ? new Color(200,160,60) : new Color(70,50,10);
            Color bgColor     = isSel ? new Color(20,40,70)    : isActive ? new Color(60,40,10)   : new Color(28,18,5);

            JPanel row = new JPanel(null) {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(bgColor);     g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                    g2.setColor(borderColor); g2.setStroke(new BasicStroke(isSel||isActive?2f:1f)); g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
                    if (isActive) { g2.setColor(new Color(255,215,90)); g2.setFont(new Font("Monospaced",Font.BOLD,13)); g2.drawString("★",8,20); }
                    g2.setColor(isSel?new Color(100,200,255):isActive?new Color(255,215,90):Color.WHITE);
                    g2.setFont(new Font("Monospaced",Font.BOLD,12));
                    g2.drawString(f.name+" Lv."+f.level, isActive?26:10, 20);
                    g2.setColor(new Color(170,170,170)); g2.setFont(new Font("Monospaced",Font.PLAIN,10));
                    g2.drawString(hp+"/"+maxHp, getWidth()-70, 14);
                    int bx=10,by=24,bw=getWidth()-80,bh=6;
                    g2.setColor(new Color(50,50,50)); g2.fillRoundRect(bx,by,bw,bh,3,3);
                    g2.setColor(barColor);            g2.fillRoundRect(bx,by,(int)(bw*ratio),bh,3,3);
                }
            };
            row.setOpaque(false);
            row.setPreferredSize(new Dimension(leftW, 38));
            row.setMaximumSize(new Dimension(leftW, 38));
            row.setCursor(new Cursor(Cursor.HAND_CURSOR));
            row.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    selectedCreatureIndex = (selectedCreatureIndex == ci) ? -1 : ci;
                    selectedIndex = -1;
                    buildBagOverlay("main");
                }
            });
            teamListPanel.add(row);
            teamListPanel.add(Box.createVerticalStrut(4));
        }

        JScrollPane teamScroll = new JScrollPane(teamListPanel);
        teamScroll.setOpaque(false);
        teamScroll.getViewport().setOpaque(false);
        teamScroll.setBorder(null);
        teamScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        teamScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        teamScroll.setBounds(leftX, teamTop + 20, leftW, teamH - 20);
        overlay.add(teamScroll);

        int rightY = topY, rightH = totalH;
        JPanel rightPanel = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(15,10,4));   g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.setColor(new Color(100,70,20)); g2.setStroke(new BasicStroke(1.5f)); g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
            }
        };
        rightPanel.setOpaque(false);
        rightPanel.setBounds(rightX, rightY, rightW, rightH);
        overlay.add(rightPanel);

        if (selectedCreatureIndex >= 0 && selectedCreatureIndex < fullTeam.size())
            buildCreatureDetailContent(rightPanel, rightW, rightH, fullTeam.get(selectedCreatureIndex));
        else
            buildDetailContent(rightPanel, rightW, rightH);
    }

    // ══════════════════════════════════════════════════════════════
    // RELEASE CONFIRM DIALOG
    // ══════════════════════════════════════════════════════════════
    private void showReleaseConfirm(Fighter f) {
        if (bagOverlay != null) this.remove(bagOverlay);

        JPanel overlay = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 0, 0, 200));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlay.setOpaque(false);
        overlay.setBounds(0, 0, 1280, 720);

        int bw = 520, bh = 220, bx = (1280 - bw) / 2, by = (720 - bh) / 2;
        JPanel box = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(20, 8, 8));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(new Color(180, 60, 60));
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.setColor(new Color(60, 10, 10));
                g2.fillRoundRect(0, 0, getWidth(), 42, 14, 14);
                g2.fillRect(0, 22, getWidth(), 20);
                g2.setColor(new Color(255, 120, 120));
                g2.setFont(new Font("Monospaced", Font.BOLD, 15));
                FontMetrics fm = g2.getFontMetrics();
                String title = "⚠ Release Creature?";
                g2.drawString(title, (getWidth() - fm.stringWidth(title)) / 2, 29);
            }
        };
        box.setOpaque(false);
        box.setBounds(bx, by, bw, bh);

        JLabel line1 = new JLabel("Are you sure you want to release", SwingConstants.CENTER);
        line1.setForeground(Color.WHITE);
        line1.setFont(new Font("Monospaced", Font.PLAIN, 13));
        line1.setBounds(20, 52, bw - 40, 20);
        box.add(line1);

        JLabel line2 = new JLabel(f.name + " (Lv." + f.level + ") into the wild?", SwingConstants.CENTER);
        line2.setForeground(new Color(255, 180, 80));
        line2.setFont(new Font("Monospaced", Font.BOLD, 14));
        line2.setBounds(20, 74, bw - 40, 20);
        box.add(line2);

        JLabel line3 = new JLabel("This cannot be undone!", SwingConstants.CENTER);
        line3.setForeground(new Color(255, 100, 100));
        line3.setFont(new Font("Monospaced", Font.PLAIN, 11));
        line3.setBounds(20, 98, bw - 40, 16);
        box.add(line3);

        final JPanel fOverlay = overlay;

        JButton confirmBtn = new JButton("YES, RELEASE");
        confirmBtn.setBackground(new Color(140, 30, 30));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFont(new Font("Monospaced", Font.BOLD, 13));
        confirmBtn.setFocusPainted(false);
        confirmBtn.setBorder(BorderFactory.createLineBorder(new Color(220, 60, 60), 2));
        confirmBtn.setBounds(40, bh - 62, 190, 38);
        confirmBtn.addActionListener(e -> {
            capturedTeam.remove(f);
            selectedCreatureIndex = -1;
            selectedIndex = -1;
            this.remove(fOverlay);
            this.revalidate(); this.repaint();
            showFloatingMessage(f.name + " was released into the wild...", new Color(180, 180, 255));
            new Timer(600, ev -> {
                ((Timer) ev.getSource()).stop();
                buildBagOverlay("main");
            }).start();
        });
        box.add(confirmBtn);

        JButton cancelBtn = new JButton("CANCEL");
        cancelBtn.setBackground(new Color(40, 40, 40));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(new Font("Monospaced", Font.BOLD, 13));
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));
        cancelBtn.setBounds(bw - 230, bh - 62, 190, 38);
        cancelBtn.addActionListener(e -> {
            this.remove(fOverlay);
            this.revalidate(); this.repaint();
            buildBagOverlay("main");
        });
        box.add(cancelBtn);

        overlay.add(box);
        bagOverlay = overlay;
        this.add(overlay);
        this.setComponentZOrder(overlay, 0);
        this.revalidate(); this.repaint();
    }

    // ══════════════════════════════════════════════════════════════
    // CREATURE DETAIL CONTENT  (single authoritative definition)
    // ══════════════════════════════════════════════════════════════
    private void buildCreatureDetailContent(JPanel panel, int w, int h, Fighter f) {
        panel.removeAll();
        int pad = 16, cy = 14;
        boolean isActive = (f == playerFighter);
        JLabel nl = new JLabel(f.name.toUpperCase() + (isActive ? "  ★" : ""));
        nl.setForeground(isActive ? new Color(255,215,90) : new Color(100,200,255));
        nl.setFont(new Font("Monospaced", Font.BOLD, 16));
        nl.setBounds(pad, cy, w - pad * 2, 22);
        panel.add(nl); cy += 26;

        JPanel dv = new JPanel();
        dv.setBackground(new Color(80,55,15));
        dv.setBounds(pad, cy, w - pad * 2, 1);
        panel.add(dv); cy += 8;

        if (f.types != null && !f.types.isEmpty()) {
            StringBuilder ts = new StringBuilder("Type: ");
            for (int t = 0; t < f.types.size(); t++) {
                ts.append(f.types.get(t).name);
                if (t < f.types.size() - 1) ts.append(" / ");
            }
            JLabel tl = new JLabel(ts.toString());
            tl.setForeground(new Color(180,180,180));
            tl.setFont(new Font("Monospaced", Font.PLAIN, 12));
            tl.setBounds(pad, cy, w - pad * 2, 16);
            panel.add(tl); cy += 20;
        }

        JLabel lvl = new JLabel("Level: " + f.level + "   EXP: " + f.exp + "/" + f.expToNext);
        lvl.setForeground(new Color(100,200,255));
        lvl.setFont(new Font("Monospaced", Font.PLAIN, 11));
        lvl.setBounds(pad, cy, w - pad * 2, 16);
        panel.add(lvl); cy += 20;

        int hp = (int) Math.max(0, f.stats.get(0).value), maxHp = (int) f.stats.get(0).base;
        cy = addStatBar(panel, pad, cy, w, "HP",  hp, maxHp, new Color(60,210,60));
        cy = addStatBar(panel, pad, cy, w, "ATK", (int)f.stats.get(1).value, 300, new Color(220,80,80));
        cy = addStatBar(panel, pad, cy, w, "DEF", (int)f.stats.get(2).value, 300, new Color(80,120,220));
        cy = addStatBar(panel, pad, cy, w, "SPD", (int)f.stats.get(3).value, 300, new Color(220,180,0));
        cy += 8;

        JLabel mt = new JLabel("MOVES");
        mt.setForeground(new Color(200,160,60));
        mt.setFont(new Font("Monospaced", Font.BOLD, 12));
        mt.setBounds(pad, cy, w - pad * 2, 16);
        panel.add(mt); cy += 18;

        for (int m = 0; m < f.moveset.size() && m < 4; m++) {
            Move move = f.moveset.get(m);
            boolean locked = move.isLocked(), noPP = move.pp <= 0;
            JLabel ml = new JLabel("• " + move.name + (locked ? " (Lv." + move.lockedUntilLevel + "🔒)" : ""));
            ml.setForeground(locked ? new Color(100,100,100) : noPP ? new Color(150,50,50) : Color.WHITE);
            ml.setFont(new Font("Monospaced", Font.PLAIN, 11));
            ml.setBounds(pad, cy, w - pad * 2 - 70, 16);
            panel.add(ml);
            JLabel pl = new JLabel(locked ? "locked" : "PP " + move.pp + "/" + move.maxPp);
            pl.setForeground(locked ? new Color(100,100,100) : noPP ? new Color(150,50,50) : new Color(140,140,140));
            pl.setFont(new Font("Monospaced", Font.PLAIN, 11));
            pl.setBounds(w - pad - 70, cy, 70, 16);
            panel.add(pl); cy += 18;
        }

        // ── RELEASE BUTTON (cannot release the active/starter creature) ──
        if (!isActive) {
            cy += 10;
            JPanel divRelease = new JPanel();
            divRelease.setBackground(new Color(120,50,50));
            divRelease.setBounds(pad, cy, w - pad * 2, 1);
            panel.add(divRelease); cy += 8;

            JButton releaseBtn = new JButton("⚠ RELEASE INTO WILD");
            releaseBtn.setBackground(new Color(80,20,20));
            releaseBtn.setForeground(new Color(255,120,120));
            releaseBtn.setFont(new Font("Monospaced", Font.BOLD, 11));
            releaseBtn.setFocusPainted(false);
            releaseBtn.setBorder(BorderFactory.createLineBorder(new Color(180,60,60), 2));
            releaseBtn.setBounds(pad, cy, w - pad * 2, 32);
            final Fighter target = f;
            releaseBtn.addActionListener(e -> showReleaseConfirm(target));
            panel.add(releaseBtn);
        } else {
            cy += 10;
            JLabel cantRelease = new JLabel("★ Active creature cannot be released");
            cantRelease.setForeground(new Color(120,100,50));
            cantRelease.setFont(new Font("Monospaced", Font.PLAIN, 10));
            cantRelease.setBounds(pad, cy, w - pad * 2, 16);
            panel.add(cantRelease);
        }

        panel.revalidate(); panel.repaint();
    }

    private int addStatBar(JPanel panel, int pad, int cy, int w, String label, int value, int max, Color barColor) {
        JLabel lbl=new JLabel(label); lbl.setForeground(new Color(160,160,160)); lbl.setFont(new Font("Monospaced",Font.BOLD,11)); lbl.setBounds(pad,cy,36,14); panel.add(lbl);
        JLabel vl=new JLabel(String.valueOf(value)); vl.setForeground(Color.WHITE); vl.setFont(new Font("Monospaced",Font.PLAIN,11)); vl.setBounds(pad+38,cy,34,14); panel.add(vl);
        int barX=pad+76,barW=w-pad*2-76;
        JPanel bg=new JPanel(null); bg.setBackground(new Color(40,40,40)); bg.setBounds(barX,cy+2,barW,10); panel.add(bg);
        float ratio=max>0?Math.min(1f,(float)value/max):0;
        JPanel fill=new JPanel(); fill.setBackground(barColor); fill.setBounds(0,0,(int)(barW*ratio),10); bg.add(fill);
        return cy+20;
    }

    private void buildDetailContent(JPanel panel, int w, int h) {
        panel.removeAll();
        if (selectedIndex==-1) {
            JLabel hint=new JLabel("<html><center><font color='gray'>Click an item or<br>creature to see details</font></center></html>",SwingConstants.CENTER);
            hint.setFont(new Font("Monospaced",Font.PLAIN,13)); hint.setBounds(0,h/2-30,w,60); panel.add(hint); return;
        }
        if (selectedIndex == 10) {
            int pad=16, cy=16;
            JLabel tl=new JLabel("ANTING2"); tl.setForeground(new Color(255,200,80)); tl.setFont(new Font("Monospaced",Font.BOLD,18)); tl.setBounds(pad,cy,w-pad*2,24); panel.add(tl); cy+=30;
            String[] lines={"A gift from your best friend Pekson.","When worn, all creatures gain x2 EXP.","Status: "+(anting2Active?"WORN (Active)":"Not worn")};
            for (String line:lines) { JLabel lbl=new JLabel(line); lbl.setForeground(line.startsWith("Status")?new Color(100,255,100):new Color(180,180,180)); lbl.setFont(new Font("Monospaced",Font.PLAIN,12)); lbl.setBounds(pad,cy,w-pad*2,18); panel.add(lbl); cy+=20; }
            cy+=12;
            JButton useBtn=new JButton(anting2Active?"ALREADY WORN":"WEAR ANTING2");
            useBtn.setBackground(anting2Active?new Color(60,60,60):new Color(160,120,20));
            useBtn.setForeground(Color.WHITE); useBtn.setFont(new Font("Monospaced",Font.BOLD,13));
            useBtn.setFocusPainted(false); useBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE,2));
            useBtn.setEnabled(!anting2Active); useBtn.setBounds(pad,cy,w-pad*2,38);
            useBtn.addActionListener(e->{ anting2Active=true; expMultiplier=2.0; buildBagOverlay("main"); showFloatingMessage("Anting2 worn! x2 EXP active!",new Color(255,200,80)); });
            panel.add(useBtn); panel.revalidate(); panel.repaint(); return;
        }
        if (selectedIndex >= 3 && selectedIndex <= 5) {
            String[] sNames={"SUPER LUNAS","SUPER POTION","SUPER SCROLL"};
            Color[] sColors={new Color(80,200,160),new Color(80,160,220),new Color(160,80,220)};
            int[] sCounts={superLunasCount,superPotionCount,superScrollCount};
            String[][] sDescs={
                    {"Qty: x"+superLunasCount,"Restores 10 PP to one move.","Better than regular Lunas!"},
                    {"Qty: x"+superPotionCount,"Restores 70 HP to one creature.","Great for tough battles!"},
                    {"Qty: x"+superScrollCount,"Capture creatures — effective at mid HP.","Higher catch rate than Scroll."}
            };
            int si=selectedIndex-3, pad=16, cy=16;
            JLabel tl=new JLabel(sNames[si]); tl.setForeground(sColors[si]); tl.setFont(new Font("Monospaced",Font.BOLD,16)); tl.setBounds(pad,cy,w-pad*2,22); panel.add(tl); cy+=28;
            for (String line:sDescs[si]) { JLabel lbl=new JLabel(line); lbl.setForeground(line.startsWith("Qty")?Color.WHITE:new Color(180,180,180)); lbl.setFont(new Font("Monospaced",Font.PLAIN,12)); lbl.setBounds(pad,cy,w-pad*2,18); panel.add(lbl); cy+=20; }
            cy+=10;
            boolean has=sCounts[si]>0;
            JButton useBtn=new JButton(has?"USE":"NONE LEFT"); useBtn.setBackground(has?sColors[si]:new Color(80,80,80));
            useBtn.setForeground(Color.WHITE); useBtn.setFont(new Font("Monospaced",Font.BOLD,13));
            useBtn.setFocusPainted(false); useBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE,2));
            useBtn.setEnabled(has); useBtn.setBounds(pad,cy,w-pad*2,38);
            final int fsi=selectedIndex;
            useBtn.addActionListener(e->{
                if (fsi==3) { pendingItemUse="super_lunas"; buildBagOverlay("creatureSelect"); }
                else if (fsi==4) { pendingItemUse="super_potion"; buildBagOverlay("creatureSelect"); }
                else { showResultAndClose("Super Scroll can only be used in battle!"); }
            });
            panel.add(useBtn); panel.revalidate(); panel.repaint(); return;
        }
        String[] names={"SCROLL","LUNAS","POTION"};
        Color[] colors={new Color(140,70,200),new Color(60,180,100),new Color(60,140,220)};
        int[] counts={scrollCount,lunasCount,potionCount};
        String[][] descs={{"Qty: x"+scrollCount,"Used to capture wild","creatures in battle.","Catch rate up when HP is low."},{"Qty: x"+lunasCount,"Restores 5 PP to one move.","Use when moves run out of PP."},{"Qty: x"+potionCount,"Restores 30 HP to a creature.","Cannot revive fainted creatures."}};
        int pad=16,cy=16;
        JLabel tl=new JLabel(names[selectedIndex]); tl.setForeground(colors[selectedIndex]); tl.setFont(new Font("Monospaced",Font.BOLD,18)); tl.setBounds(pad,cy,w-pad*2,24); panel.add(tl); cy+=30;
        for (String line:descs[selectedIndex]) {
            JLabel lbl=new JLabel(line); lbl.setForeground(line.startsWith("Qty")?Color.WHITE:new Color(180,180,180));
            lbl.setFont(new Font("Monospaced",Font.PLAIN,13)); lbl.setBounds(pad,cy,w-pad*2,18); panel.add(lbl); cy+=18;
        }
        cy+=12;
        boolean has=counts[selectedIndex]>0;
        JButton useBtn=new JButton(has?"USE":"NONE LEFT"); useBtn.setBackground(has?colors[selectedIndex]:new Color(80,80,80));
        useBtn.setForeground(Color.WHITE); useBtn.setFont(new Font("Monospaced",Font.BOLD,14));
        useBtn.setFocusPainted(false); useBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE,2));
        useBtn.setEnabled(has); useBtn.setBounds(pad,cy,w-pad*2,38);
        final int sel=selectedIndex;
        useBtn.addActionListener(e->{
            if (sel==0) useScrollWorld();
            else if (sel==1) { pendingItemUse="lunas"; buildBagOverlay("creatureSelect"); }
            else if (sel==2) { pendingItemUse="potion"; buildBagOverlay("creatureSelect"); }
        });
        panel.add(useBtn); panel.revalidate(); panel.repaint();
    }

    private void buildCreatureSelectScreen(JPanel overlay, int winX, int winY, int winW, int winH) {
        String itemName = pendingItemUse.equals("lunas") ? "Lunas"
                : pendingItemUse.equals("super_lunas") ? "Super Lunas"
                  : pendingItemUse.equals("super_potion") ? "Super Potion" : "Potion";
        Color itemColor = pendingItemUse.contains("lunas") ? new Color(60,180,100) : new Color(60,140,220);
        int cx=winX+24,cy=winY+62,cw=winW-48;
        JLabel tl=new JLabel("Use "+itemName+" on which creature?"); tl.setForeground(itemColor);
        tl.setFont(new Font("Monospaced",Font.BOLD,15)); tl.setBounds(cx,cy,cw,24); overlay.add(tl); cy+=36;
        ArrayList<Fighter> ft=new ArrayList<>(); ft.add(playerFighter); ft.addAll(capturedTeam);
        int cols=3,btnW=(cw-(cols-1)*10)/cols,btnH=64,col=0,rowY=cy;
        for (Fighter f:ft) {
            int hp=(int)Math.max(0,f.stats.get(0).value),maxHp=(int)f.stats.get(0).base;
            boolean fainted=f.isFainted();
            boolean ppFull=pendingItemUse.contains("lunas")&&f.moveset.stream().allMatch(m->m.pp>=m.maxPp||m.isLocked());
            boolean canUse=!fainted&&(pendingItemUse.contains("potion")?hp<maxHp:!ppFull);
            String label="<html><center><b>"+f.name+"</b>"+(f==playerFighter?" ★":"")+"<br><font size='2'>HP: "+hp+"/"+maxHp+"</font></center></html>";
            Color bg=!canUse?new Color(60,40,40):(f==playerFighter?new Color(50,40,10):new Color(20,40,20));
            JButton btn=new JButton(label); btn.setBackground(bg); btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Monospaced",Font.BOLD,12)); btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(canUse?itemColor:new Color(80,40,40),2));
            btn.setEnabled(canUse); btn.setBounds(cx+col*(btnW+10),rowY,btnW,btnH);
            if (canUse) { final Fighter target=f; btn.addActionListener(ev->{pendingTarget=target;buildBagOverlay("confirm");}); }
            overlay.add(btn); col++; if (col>=cols){col=0;rowY+=btnH+10;}
        }
        JButton back=new JButton("BACK"); back.setBackground(new Color(80,80,80)); back.setForeground(Color.WHITE);
        back.setFont(new Font("Monospaced",Font.BOLD,14)); back.setFocusPainted(false);
        back.setBorder(BorderFactory.createLineBorder(Color.WHITE,2)); back.setBounds(winX+winW-140,winY+winH-56,120,36);
        back.addActionListener(e->{pendingItemUse="";selectedIndex=-1;buildBagOverlay("main");}); overlay.add(back);
    }

    private void buildConfirmScreen(JPanel overlay, int winX, int winY, int winW, int winH) {
        String itemName = pendingItemUse.equals("lunas") ? "Lunas"
                : pendingItemUse.equals("super_lunas") ? "Super Lunas"
                  : pendingItemUse.equals("super_potion") ? "Super Potion" : "Potion";
        Color itemColor = pendingItemUse.contains("lunas") ? new Color(60,180,100) : new Color(60,140,220);
        String effect = pendingItemUse.contains("lunas") ? "restore PP"
                : pendingItemUse.equals("super_potion") ? "restore 70 HP" : "restore 30 HP";
        int cx=winX+winW/2,cy=winY+winH/2-60;
        JLabel msg=new JLabel("<html><center>Use <b>"+itemName+"</b> on <b>"+pendingTarget.name+"</b><br>to "+effect+"?</center></html>",SwingConstants.CENTER);
        msg.setForeground(Color.WHITE); msg.setFont(new Font("Monospaced",Font.PLAIN,15)); msg.setBounds(winX+60,cy,winW-120,60); overlay.add(msg);
        int btnY=cy+80,btnW=160,btnH=46;
        JButton yes=new JButton("YES"); yes.setBackground(new Color(30,140,60)); yes.setForeground(Color.WHITE);
        yes.setFont(new Font("Monospaced",Font.BOLD,16)); yes.setFocusPainted(false);
        yes.setBorder(BorderFactory.createLineBorder(Color.WHITE,2)); yes.setBounds(cx-btnW-10,btnY,btnW,btnH);
        yes.addActionListener(e->applyItemWorld()); overlay.add(yes);
        JButton no=new JButton("NO"); no.setBackground(new Color(160,40,40)); no.setForeground(Color.WHITE);
        no.setFont(new Font("Monospaced",Font.BOLD,16)); no.setFocusPainted(false);
        no.setBorder(BorderFactory.createLineBorder(Color.WHITE,2)); no.setBounds(cx+10,btnY,btnW,btnH);
        no.addActionListener(e->buildBagOverlay("creatureSelect")); overlay.add(no);
    }

    private void applyItemWorld() {
        if (pendingTarget==null) return;
        if (pendingItemUse.equals("potion")) {
            potionCount--;
            int before=(int)pendingTarget.stats.get(0).value;
            pendingTarget.stats.get(0).value=Math.min(pendingTarget.stats.get(0).base,pendingTarget.stats.get(0).value+30);
            showResultAndClose(pendingTarget.name+" restored "+(int)(pendingTarget.stats.get(0).value-before)+" HP!");
        } else if (pendingItemUse.equals("super_potion")) {
            superPotionCount--;
            int before=(int)pendingTarget.stats.get(0).value;
            pendingTarget.stats.get(0).value=Math.min(pendingTarget.stats.get(0).base,pendingTarget.stats.get(0).value+70);
            showResultAndClose(pendingTarget.name+" restored "+(int)(pendingTarget.stats.get(0).value-before)+" HP! (Super Potion)");
        } else if (pendingItemUse.equals("lunas")) {
            lunasCount--;
            Move target=null;
            for (Move m:pendingTarget.moveset) { if (!m.isLocked()&&m.pp<m.maxPp) { if (target==null||m.pp<target.pp) target=m; } }
            String msg;
            if (target==null) msg=pendingTarget.name+"'s moves are all full!";
            else { int r=Math.min(5,target.maxPp-target.pp); target.pp=Math.min(target.maxPp,target.pp+5); msg=pendingTarget.name+"'s "+target.name+" restored "+r+" PP!"; }
            showResultAndClose(msg);
        } else if (pendingItemUse.equals("super_lunas")) {
            superLunasCount--;
            Move target=null;
            for (Move m:pendingTarget.moveset) { if (!m.isLocked()&&m.pp<m.maxPp) { if (target==null||m.pp<target.pp) target=m; } }
            String msg;
            if (target==null) msg=pendingTarget.name+"'s moves are all full!";
            else { int r=Math.min(10,target.maxPp-target.pp); target.pp=Math.min(target.maxPp,target.pp+10); msg=pendingTarget.name+"'s "+target.name+" restored "+r+" PP! (Super Lunas)"; }
            showResultAndClose(msg);
        }
    }

    private void useScrollWorld() { showResultAndClose("Scrolls can only be used in battle!"); }

    private void showResultAndClose(String message) {
        if (bagOverlay!=null) this.remove(bagOverlay);
        JPanel ro=new JPanel(null) { @Override protected void paintComponent(Graphics g) { super.paintComponent(g); g.setColor(new Color(0,0,0,180)); g.fillRect(0,0,getWidth(),getHeight()); }};
        ro.setOpaque(false); ro.setBounds(0,0,1280,720);
        int rw=500,rh=100,rx=(1280-rw)/2,ry=(720-rh)/2;
        JPanel box=new JPanel(new BorderLayout()); box.setBackground(new Color(22,14,6));
        box.setBorder(BorderFactory.createLineBorder(new Color(170,120,50),2)); box.setBounds(rx,ry,rw,rh);
        JLabel lbl=new JLabel(message,SwingConstants.CENTER); lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Monospaced",Font.BOLD,15)); box.add(lbl,BorderLayout.CENTER); ro.add(box);
        bagOverlay=ro; this.add(bagOverlay); this.setComponentZOrder(bagOverlay,0); this.revalidate(); this.repaint();
        new Timer(1500,e->{((Timer)e.getSource()).stop();closeBag();}).start();
    }

    private JButton styledItemBtn(String name, int count, Color accent, boolean selected) {
        JButton btn=new JButton("<html><b>"+name+"</b>&nbsp;&nbsp;x"+count+"</html>");
        btn.setBackground(selected?new Color(60,40,10):new Color(32,20,6)); btn.setForeground(selected?accent:Color.WHITE);
        btn.setFont(new Font("Monospaced",Font.PLAIN,13)); btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createLineBorder(selected?accent:new Color(80,55,15),selected?2:1)); return btn;
    }

    // ══════════════════════════════════════════════════════════════
    // CREATURE POOLS
    // ══════════════════════════════════════════════════════════════
    private Fighter randomWorld1Creature() {
        int level = 3 + coinRand.nextInt(5);
        switch (coinRand.nextInt(4)) {
            case 0: return Create.createAghoy(level);
            case 1: return Create.createEkek(level);
            case 2: return createSirena(level);
            default: return Create.createKapre(level);
        }
    }

    private Fighter randomWorld2Creature() {
        int level = 8 + coinRand.nextInt(7);
        switch (coinRand.nextInt(5)) {
            case 0: return Create.createSigbin(level);
            case 1: return Create.createBungisngis(level);
            case 2: return Create.createAmongmongo(level);
            case 3: return Create.createAmaninhig(level);
            default: return createMangkukulam(level);
        }
    }

    private Fighter randomWorld3Creature() {
        int level = 15 + coinRand.nextInt(10);
        switch (coinRand.nextInt(5)) {
            case 0: return Create.createBusaw(level);
            case 1: return Create.createKaperosa(level);
            case 2: return Create.createKolyog(level);
            case 3: return Create.createManananggal(level);
            default: return Create.createTikbalang(level);
        }
    }

    private Fighter createSirena(int level) {
        Type WATER=new Type("Water",0x4488FF); Type SPIRIT=new Type("Spirit",0xAADDFF);
        ArrayList<Type> types=new ArrayList<>(Arrays.asList(WATER,SPIRIT));
        ArrayList<Move> allMoves=new ArrayList<>(Arrays.asList(
                new Move("Aqua Whip",WATER,55,new ArrayList<>(),10),
                new Move("Charm Song",SPIRIT,35,new ArrayList<>(),10),
                new Move("Healing Tide",WATER,0,new ArrayList<>(),8),
                new Move("Tsunami Cry",WATER,95,new ArrayList<>(),6)
        ));
        ArrayList<Integer> ul=new ArrayList<>(Arrays.asList(1,1,5,10));
        ArrayList<Stat> stats=new ArrayList<>();
        int extra=level-5;
        stats.add(new Stat("HP",175+extra*10)); stats.add(new Stat("ATK",80+extra*4));
        stats.add(new Stat("DEF",85+extra*3));  stats.add(new Stat("SPD",90+extra*3));
        return new Fighter("Sirena","/images/Sirena.png","/images/Sirena.png",types,stats,allMoves,ul,level);
    }

    private Fighter createMangkukulam(int level) {
        Type SHADOW=new Type("Shadow",0x6644AA); Type SPIRIT=new Type("Spirit",0xAADDFF);
        ArrayList<Type> types=new ArrayList<>(Arrays.asList(SHADOW,SPIRIT));
        ArrayList<Move> allMoves=new ArrayList<>(Arrays.asList(
                new Move("Curse Bolt",SHADOW,50,new ArrayList<>(),10),
                new Move("Dark Whisper",SPIRIT,0,new ArrayList<>(),12),
                new Move("Soul Burn",SHADOW,70,new ArrayList<>(),8),
                new Move("Hex Strike",SHADOW,90,new ArrayList<>(),6)
        ));
        ArrayList<Integer> ul=new ArrayList<>(Arrays.asList(1,1,6,12));
        ArrayList<Stat> stats=new ArrayList<>();
        int extra=level-5;
        stats.add(new Stat("HP",160+extra*10)); stats.add(new Stat("ATK",90+extra*4));
        stats.add(new Stat("DEF",70+extra*3));  stats.add(new Stat("SPD",105+extra*3));
        return new Fighter("Mangkukulam","/images/Mangkukulam.png","/images/Mangkukulam.png",types,stats,allMoves,ul,level);
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
        int tileCol=worldX/TILE_DISPLAY_SIZE,tileRow=worldY/TILE_DISPLAY_SIZE;
        if (tileCol<0||tileRow<0||tileCol>=mapWidth||tileRow>=mapHeight) return true;
        for (int layer=2;layer<allLayerData.length;layer++) {
            if (solidTiles.contains(allLayerData[layer][tileRow][tileCol])) return true;
        }
        return false;
    }

    private void checkEncounter() {
        if (inBattle) return;
        if (allLayerData==null||allLayerData.length<3) return;
        if (System.currentTimeMillis()<encounterCooldownUntil) return;
        int feetX=playerX+PLAYER_SIZE_W/2,feetY=playerY+PLAYER_SIZE_H;
        int tileCol=feetX/TILE_DISPLAY_SIZE,tileRow=feetY/TILE_DISPLAY_SIZE;
        if (tileCol<0||tileRow<0||tileCol>=mapWidth||tileRow>=mapHeight) return;
        int tileId=0;
        for (int layer=0;layer<allLayerData.length;layer++) { int t=allLayerData[layer][tileRow][tileCol]; if (t>0) tileId=t; }
        if (tileId!=lastDebugTileId) {
            lastDebugTileId=tileId;
            StringBuilder dbg=new StringBuilder("[TILE] col=").append(tileCol).append(" row=").append(tileRow).append(" | ");
            for (int layer=0;layer<allLayerData.length;layer++) {
                int t=allLayerData[layer][tileRow][tileCol];
                if (t>0) dbg.append("L").append(layer).append("=").append(t).append(" ");
            }
            dbg.append("| TOP=").append(tileId);
            System.out.println(dbg);
            clearHoverMessage();
            if (!peksonDialogOpen && !oldWomanDialogOpen) {
                lastNpcTileKey = "";
            }
        }

        if (isWorld2()) { handleWorld2Tiles(tileCol, tileRow, tileId); return; }
        if (isWorld3()) { handleWorld3Tiles(tileCol, tileRow, tileId); return; }

        // World 1
        if (tileId==709&&!caveSceneShown&&!showCaveScene) { SwingUtilities.invokeLater(this::showCaveScene); return; }
        if (tileId==908&&!bossFightDone&&!bossTriggered) { SwingUtilities.invokeLater(this::triggerBossFight); return; }
        if (tileId==758) {
            if (Math.random()>0.05) return;
            inBattle=true;
            int savedX=playerX,savedY=playerY;
            Fighter wild=randomWorld1Creature();
            syncStateToGameScene();
            SwingUtilities.invokeLater(()->
                    gameScene.switchToBattleOnMap(playerFighter,wild,capturedTeam,
                            scrollCount,lunasCount,potionCount,savedX,savedY,currentMapPath));
            return;
        }
        if (tileId==897&&!showShop&&!showBag) { SwingUtilities.invokeLater(this::openShop); return; }
        if (tileId==222&&portalVisible) {
            if (!inBattle) {
                inBattle=true;
                syncStateToGameScene();
                SwingUtilities.invokeLater(this::enterPortalToWorld2);
            }
            return;
        }
        if (tileId==568) {
            if (!healTriggered) { healTriggered=true; healAllCreatures(); SwingUtilities.invokeLater(()->showFloatingMessage("The small pond healed your creatures!",new Color(80,200,255))); }
        } else { healTriggered=false; }
    }

    // ══════════════════════════════════════════════════════════════
    // WORLD 2 TILE HANDLING
    // ══════════════════════════════════════════════════════════════
    private void handleWorld2Tiles(int tileCol, int tileRow, int tileId) {
        if (tileCol==38 && tileRow==24 && tileId==786) {
            if (!treasureFound) {
                setHoverMessage("Congratulations you have found a treasure and it gave you a map");
                treasureFound = true;
                hasMap = true;
                syncStateToGameScene();
                SwingUtilities.invokeLater(() -> showFloatingMessage("Treasure found! You received a MAP! Press [M] to view it.", new Color(255, 215, 50)));
            } else {
                setHoverMessage("You already found the treasure here.\n(You have the Map)");
            }
            return;
        }
        if (tileCol==21 && tileRow==23 && tileId==1009) {
            setHoverMessage("Go left and youll meet an old friend and he might give you something\nto help you find your grandpa or go right and youll find something useful for your journey");
            return;
        }
        if (tileCol==14 && tileRow==23 && tileId==980) {
            if (!w2ShopOpen && !showBag) SwingUtilities.invokeLater(this::openWorld2Shop);
            return;
        }
        if (tileCol==9 && tileRow==26 && tileId==676) {
            if (!w2HealTriggered) {
                w2HealTriggered = true;
                healAllCreatures();
                SwingUtilities.invokeLater(() -> showFloatingMessage("The pond restored all your creatures' HP and PP!", new Color(80, 200, 255)));
            }
        } else {
            w2HealTriggered = false;
        }
        if (tileCol==10 && tileRow==25 && tileId==765) {
            String key = "pekson_10_25";
            if (!peksonDialogOpen && !lastNpcTileKey.equals(key)) {
                lastNpcTileKey = key;
                peksonDialogOpen = true;
                SwingUtilities.invokeLater(this::showPeksonDialog);
            }
            return;
        }
        if (tileCol==6 && tileRow==10 && tileId==710) {
            String key = "oldwoman_8_14";
            if (!oldWomanDialogOpen && !lastNpcTileKey.equals(key)) {
                lastNpcTileKey = key;
                oldWomanDialogOpen = true;
                SwingUtilities.invokeLater(this::showOldWomanDialog);
            }
            return;
        }
        if (tileCol==23 && tileRow==13 && tileId==846) {
            setHoverMessage("Beware an evil albularyo is at top he curses people in this village and ask tons of coins to heal them");
            return;
        }
        if (tileCol==22 && tileRow==10 && tileId==128) {
            if (!w2BossDone && !w2BossTriggered) {
                SwingUtilities.invokeLater(this::triggerWorld2Boss);
            } else if (w2BossDone) {
                setHoverMessage("You have already defeated the Town 2 Albularyo!");
            }
            return;
        }
        if (tileCol==21 && tileRow==11 && tileId==812) {
            if (w2BossDone && !w2PortalVisible) w2PortalVisible = true;
            if (w2PortalVisible) {
                if (!inBattle) {
                    inBattle = true;
                    syncStateToGameScene();
                    SwingUtilities.invokeLater(this::enterPortalToWorld3);
                }
            } else {
                setHoverMessage("A mysterious sealed portal...\nDefeat the Town 2 Albularyo to unseal it.");
            }
            return;
        }
        if (tileId==758) {
            if (Math.random()>0.05) return;
            inBattle=true;
            int savedX=playerX,savedY=playerY;
            Fighter wild = randomWorld2Creature();
            syncStateToGameScene();
            SwingUtilities.invokeLater(()->
                    gameScene.switchToBattleOnMap(playerFighter,wild,capturedTeam,
                            scrollCount,lunasCount,potionCount,savedX,savedY,currentMapPath));
            return;
        }
        if (tileId==897 && !w2ShopOpen && !showBag) {
            SwingUtilities.invokeLater(this::openWorld2Shop);
        }
    }

    // ══════════════════════════════════════════════════════════════
    // WORLD 3 TILE HANDLING
    // ══════════════════════════════════════════════════════════════
    private void handleWorld3Tiles(int tileCol, int tileRow, int tileId) {
        if (tileCol==24 && tileRow==38 && tileId==676) {
            if (!w3HealTriggered) {
                w3HealTriggered = true;
                healAllCreatures();
                SwingUtilities.invokeLater(() -> showFloatingMessage(
                        "The pond restored all your creatures' HP and PP!", new Color(80, 200, 255)));
            }
        } else {
            w3HealTriggered = false;
        }

        if (tileCol==23 && tileRow==38 && tileId==788) {
            if (!w3Quest3Triggered) {
                w3Quest3Triggered = true;
                syncStateToGameScene();
                SwingUtilities.invokeLater(this::showWorld3QuestIntro);
            } else {
                setHoverMessage("QUEST 3: Collect 5 Silver Coins from the wells\nand level 6 creatures to Lv.30!");
            }
            return;
        }

        if (tileCol==32 && tileRow==39 && tileId==1004) {
            if (!w3Coin1Found) {
                setHoverMessage("★ Collect the silver coin inside the well!");
                w3Coin1Found = true;
                syncStateToGameScene();
                SwingUtilities.invokeLater(() -> showCoinCollected(1));
                checkWorld3QuestComplete();
            } else {
                setHoverMessage("✓ Silver coin already collected here.");
            }
            return;
        }

        if (tileCol==32 && tileRow==16 && tileId==815) {
            if (!w3Coin2Found) {
                setHoverMessage("★ Collect the silver coin inside the well!");
                w3Coin2Found = true;
                syncStateToGameScene();
                SwingUtilities.invokeLater(() -> showCoinCollected(2));
                checkWorld3QuestComplete();
            } else {
                setHoverMessage("✓ Silver coin already collected here.");
            }
            return;
        }

        if (tileCol==29 && tileRow==1 && tileId==1014) {
            if (!w3Coin3Found) {
                setHoverMessage("★ Collect the silver coin inside the well!");
                w3Coin3Found = true;
                syncStateToGameScene();
                SwingUtilities.invokeLater(() -> showCoinCollected(3));
                checkWorld3QuestComplete();
            } else {
                setHoverMessage("✓ Silver coin already collected here.");
            }
            return;
        }

        if (tileCol==16 && tileRow==39 && tileId==1004) {
            if (!w3Coin4Found) {
                setHoverMessage("★ Collect the silver coin inside the well!");
                w3Coin4Found = true;
                syncStateToGameScene();
                SwingUtilities.invokeLater(() -> showCoinCollected(4));
                checkWorld3QuestComplete();
            } else {
                setHoverMessage("✓ Silver coin already collected here.");
            }
            return;
        }

        if (tileCol==0 && tileRow==18 && tileId==815) {
            if (!w3Coin5Found) {
                setHoverMessage("★ Collect the silver coin inside the well!");
                w3Coin5Found = true;
                syncStateToGameScene();
                SwingUtilities.invokeLater(() -> showCoinCollected(5));
                checkWorld3QuestComplete();
            } else {
                setHoverMessage("✓ Silver coin already collected here.");
            }
            return;
        }

        if (tileCol==40 && tileRow==0 && tileId==129) {
            if (!w3Quest3Complete) {
                setHoverMessage("You are not ready!\nComplete Quest 3 first before facing the boss.");
            } else if (w3BossDone) {
                setHoverMessage("You have already defeated the Town 3 Albularyo!");
            } else {
                SwingUtilities.invokeLater(this::triggerWorld3Boss);
            }
            return;
        }

        if (tileId==758) {
            if (Math.random() > 0.05) return;
            inBattle = true;
            int savedX = playerX, savedY = playerY;
            Fighter wild = randomWorld3Creature();
            syncStateToGameScene();
            SwingUtilities.invokeLater(() ->
                    gameScene.switchToBattleOnMap(playerFighter, wild, capturedTeam,
                            scrollCount, lunasCount, potionCount,
                            savedX, savedY, currentMapPath));
        }
    }

    private void checkWorld3QuestComplete() {
        if (w3Quest3Complete) return;
        boolean allCoins = w3Coin1Found && w3Coin2Found && w3Coin3Found && w3Coin4Found && w3Coin5Found;
        boolean sixAtLv30 = checkCreaturesAtLevel(30, 6);
        if (allCoins && sixAtLv30) {
            w3Quest3Complete = true;
            syncStateToGameScene();
            SwingUtilities.invokeLater(() ->
                    showFloatingMessage("Quest 3 Complete! Go face the Albularyo boss!", new Color(100, 255, 150)));
        }
    }

    private boolean checkCreaturesAtLevel(int level, int needed) {
        int count = 0;
        if (playerFighter.level >= level) count++;
        for (Fighter f : capturedTeam) if (f.level >= level) count++;
        return count >= needed;
    }

    private void showCoinCollected(int coinNumber) {
        int coinsFound = (w3Coin1Found?1:0)+(w3Coin2Found?1:0)+(w3Coin3Found?1:0)
                + (w3Coin4Found?1:0)+(w3Coin5Found?1:0);
        showFloatingMessage("Silver Coin #" + coinNumber + " collected! (" + coinsFound + "/5)", new Color(255, 215, 50));
    }

    private void showWorld3QuestIntro() {
        JPanel overlay = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(0, 0, 0, 200));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlay.setOpaque(false);
        overlay.setBounds(0, 0, 1280, 720);

        int bw = 860, bh = 360, bx = (1280-bw)/2, by = (720-bh)/2;
        JPanel box = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(10, 8, 20));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(200, 160, 50));
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.setColor(new Color(50, 38, 8));
                g2.fillRoundRect(0, 0, getWidth(), 46, 16, 16);
                g2.fillRect(0, 26, getWidth(), 20);
                g2.setColor(new Color(255, 215, 90));
                g2.setFont(new Font("Monospaced", Font.BOLD, 18));
                FontMetrics fm = g2.getFontMetrics();
                String t = "★  QUEST 3  ★";
                g2.drawString(t, (getWidth()-fm.stringWidth(t))/2, 33);
            }
        };
        box.setOpaque(false);
        box.setBounds(bx, by, bw, bh);

        String[] lines = {
                "Welcome to Manolo Fortich, " + playerName + "!",
                "This town has been cursed by the most powerful Albularyo yet.",
                "",
                "The elder has given you a task before you can face the boss:",
                "Find the 5 Silver Coins hidden inside the wells around town.",
                "They hold the power to weaken the Albularyo's curse.",
                "",
                "You must also level up 6 of your creatures to Lv.30",
                "to prove you are strong enough for this battle!"
        };
        Color[] lineColors = {
                new Color(255, 215, 90), new Color(255, 160, 160), Color.WHITE,
                new Color(200, 200, 200), new Color(100, 220, 255), new Color(180, 180, 180),
                Color.WHITE, new Color(100, 255, 150), new Color(100, 255, 150)
        };

        int ly = 56;
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].isEmpty()) { ly += 12; continue; }
            JLabel lbl = new JLabel(lines[i]);
            lbl.setForeground(lineColors[i]);
            lbl.setFont(new Font("Monospaced", i==0 ? Font.BOLD : Font.PLAIN, i==0 ? 14 : 13));
            lbl.setBounds(20, ly, bw-40, 20);
            box.add(lbl);
            ly += 22;
        }

        final JPanel fOverlay = overlay;
        JButton btn = new JButton("Let's do this!");
        btn.setBackground(new Color(60, 130, 60));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Monospaced", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(100, 220, 100), 2));
        btn.setBounds(bw/2-100, bh-56, 200, 38);
        btn.addActionListener(e -> {
            WorldPanel.this.remove(fOverlay);
            WorldPanel.this.revalidate();
            WorldPanel.this.repaint();
            requestFocusInWindow();
        });
        box.add(btn);
        overlay.add(box);
        this.add(overlay);
        this.setComponentZOrder(overlay, 0);
        this.revalidate();
        this.repaint();
    }

    // ══════════════════════════════════════════════════════════════
    // WORLD 3 QUEST PANEL
    // ══════════════════════════════════════════════════════════════
    private void drawWorld3QuestPanel(Graphics2D g2) {
        if (!w3Quest3Triggered) return;
        int coinsFound = (w3Coin1Found?1:0)+(w3Coin2Found?1:0)+(w3Coin3Found?1:0)
                + (w3Coin4Found?1:0)+(w3Coin5Found?1:0);
        boolean sixAtLv30 = checkCreaturesAtLevel(30, 6);

        int qx = 1280-240, qy = 10, qw = 230, qh = w3Quest3Complete ? 80 : 120;

        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(qx, qy, qw, qh, 12, 12);
        g2.setColor(w3Quest3Complete ? new Color(80, 200, 80) : new Color(200, 160, 50));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(qx, qy, qw, qh, 12, 12);

        g2.setColor(new Color(255, 215, 90));
        g2.setFont(new Font("Monospaced", Font.BOLD, 11));
        g2.drawString("QUEST 3", qx+8, qy+18);

        if (w3Quest3Complete) {
            g2.setColor(new Color(100, 255, 100));
            g2.setFont(new Font("Monospaced", Font.BOLD, 10));
            g2.drawString("✓ Quest Complete!", qx+8, qy+38);
            g2.setColor(new Color(255, 160, 80));
            g2.drawString("Go fight the Albularyo!", qx+8, qy+56);
        } else {
            g2.setColor(coinsFound >= 5 ? new Color(100,255,100) : new Color(200,200,200));
            g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
            g2.drawString((coinsFound>=5?"✓ ":"• ") + "Silver Coins ("+coinsFound+"/5)", qx+8, qy+36);
            g2.setColor(sixAtLv30 ? new Color(100,255,100) : new Color(200,200,200));
            int current6 = Math.min(6,
                    (playerFighter.level>=30?1:0) +
                            (int)capturedTeam.stream().filter(f->f.level>=30).count());
            g2.drawString((sixAtLv30?"✓ ":"• ") + "6 creatures Lv.30 ("+current6+"/6)", qx+8, qy+54);
            g2.setColor(new Color(255, 160, 80));
            g2.setFont(new Font("Monospaced", Font.BOLD, 10));
            g2.drawString("Find all wells around town!", qx+8, qy+78);
            g2.drawString("& train your creatures!", qx+8, qy+92);
        }
    }

    // ══════════════════════════════════════════════════════════════
    // WORLD 3 BOSS INDICATOR
    // ══════════════════════════════════════════════════════════════
    private void drawWorld3BossIndicator(Graphics2D g2) {
        if (w3BossDone) return;
        int tileX = 40 * TILE_DISPLAY_SIZE - cameraX;
        int tileY = 0  * TILE_DISPLAY_SIZE - cameraY;
        if (tileX+TILE_DISPLAY_SIZE<0||tileX>1280||tileY+TILE_DISPLAY_SIZE<0||tileY>720) return;
        long t = System.currentTimeMillis();
        float pulse = (float)(Math.sin(t/400.0)*0.3+0.7);
        int cx = tileX + TILE_DISPLAY_SIZE/2;
        int cy = tileY + TILE_DISPLAY_SIZE/2;
        g2.setColor(new Color(255, 50, 50, (int)(80*pulse)));
        g2.fillOval(cx-28, cy-28, 56, 56);
        g2.setColor(new Color(255, 100, 100));
        g2.setFont(new Font("Monospaced", Font.BOLD, 10));
        FontMetrics fm = g2.getFontMetrics();
        String label = w3Quest3Complete ? "BOSS ▶" : "BOSS (Locked)";
        g2.drawString(label, cx-fm.stringWidth(label)/2, tileY-4);
    }

    public void start() {
        if (gameThread == null) {
            gameThread = new Thread(this);
            gameThread.start();
        }

        if (pendingKhaibalang != null) {
            Fighter khai  = pendingKhaibalang;
            String  pName = pendingKhaiPName;
            pendingKhaibalang = null;
            pendingKhaiPName  = null;
            new Timer(1000, e -> {
                ((Timer) e.getSource()).stop();
                gameScene.switchToKhaiBoss(
                        playerFighter, khai, capturedTeam,
                        scrollCount, lunasCount, potionCount,
                        playerX, playerY,
                        null,
                        currentMapPath,
                        pName
                );
            }).start();
        }
    }

    private void update() {
        if (keyH.bagJustPressed) {
            if (showBag) closeBag();
            else if (!showWelcome && !showShop && !w2ShopOpen && !showCaveScene && !showMapOverlay) openBag();
            keyH.bagJustPressed=false;
        }
        if (keyH.shopJustPressed) {
            if (showShop) closeShop();
            if (w2ShopOpen) closeWorld2Shop();
            keyH.shopJustPressed=false;
        }
        if (showWelcome||showBag||showShop||w2ShopOpen||showCaveScene||showMapOverlay) return;
        boolean moving=false; int newX=playerX,newY=playerY;
        if (keyH.up)    { newY-=PLAYER_SPEED; currentRow=3; moving=true; }
        if (keyH.down)  { newY+=PLAYER_SPEED; currentRow=0; moving=true; }
        if (keyH.left)  { newX-=PLAYER_SPEED; currentRow=1; moving=true; }
        if (keyH.right) { newX+=PLAYER_SPEED; currentRow=2; moving=true; }
        if (!isSolid(newX,newY)&&!isSolid(newX+PLAYER_SIZE_W-1,newY)&&
                !isSolid(newX,newY+PLAYER_SIZE_H-1)&&!isSolid(newX+PLAYER_SIZE_W-1,newY+PLAYER_SIZE_H-1)) {
            playerX=newX; playerY=newY;
        }
        if (moving) { frameCounter++; if (frameCounter>=frameDelay) { frameCounter=1; currentFrame=(currentFrame+1)%50; } }
        else { currentFrame=0; frameCounter=0; }
        int mpW=mapWidth*TILE_DISPLAY_SIZE,mpH=mapHeight*TILE_DISPLAY_SIZE;
        playerX=Math.max(0,Math.min(playerX,mpW-PLAYER_SIZE_W));
        playerY=Math.max(0,Math.min(playerY,mpH-PLAYER_SIZE_H));
        int sw=getWidth()>0?getWidth():1280,sh=getHeight()>0?getHeight():720;
        cameraX=playerX-sw/2+PLAYER_SIZE_W/2; cameraY=playerY-sh/2+PLAYER_SIZE_H/2;
        cameraX=Math.max(0,Math.min(cameraX,mpW-sw)); cameraY=Math.max(0,Math.min(cameraY,mpH-sh));
        checkEncounter();
    }

    @Override public void run() {
        while (gameThread!=null) {
            update(); repaint();
            try { Thread.sleep(16); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2=(Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        if (allLayerData!=null) {
            for (int layer=0;layer<allLayerData.length;layer++)
                for (int row=0;row<mapHeight;row++)
                    for (int col=0;col<mapWidth;col++) {
                        int tileId=allLayerData[layer][row][col]; if (tileId<=0) continue;
                        int drawX=col*TILE_DISPLAY_SIZE-cameraX,drawY=row*TILE_DISPLAY_SIZE-cameraY;
                        if (drawX+TILE_DISPLAY_SIZE<0||drawX>getWidth()) continue;
                        if (drawY+TILE_DISPLAY_SIZE<0||drawY>getHeight()) continue;
                        BufferedImage img=tileCache.get(tileId);
                        if (img!=null) g2.drawImage(img,drawX,drawY,TILE_DISPLAY_SIZE,TILE_DISPLAY_SIZE,null);
                    }
            drawPortal(g2);
            if (isWorld2()) drawWorld2Portal(g2);
            if (isWorld3()) drawWorld3BossIndicator(g2);
            int px=playerX-cameraX,py=playerY-cameraY;
            if (playerSheet!=null) {
                int fw=25,fh=25;
                g2.drawImage(playerSheet,px,py,px+PLAYER_SIZE_W,py+PLAYER_SIZE_H,
                        currentFrame*fw,currentRow*fh,currentFrame*fw+fw,currentRow*fh+fh,null);
            } else { g2.setColor(Color.RED); g2.fillRect(px,py,PLAYER_SIZE_W,PLAYER_SIZE_H); }
        } else { g2.setColor(Color.RED); g2.setFont(new Font("Arial",Font.BOLD,20)); g2.drawString("Map failed to load!",50,50); }
        drawHUD(g2);
        drawTownSign(g2);
        drawQuestPanel(g2);
        drawHoverMessage(g2);
    }

    @Override public void addNotify() {
        super.addNotify();
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int hudX=10,hudY=10,w=200,tx=hudX+10;
                int ty=hudY+18+18+4+4+18+18+2+2+18+2+2+2+18+2+2+4;
                Rectangle codeBox=new Rectangle(tx+42,ty-1,w-56,16);
                hudCodeFocused=codeBox.contains(e.getPoint());
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override public void keyTyped(KeyEvent e) {
                if (!hudCodeFocused) return;
                if (showWelcome||showBag||showShop||showCaveScene) return;
                processHudCodeInput(e.getKeyChar());
            }
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_M && hasMap) {
                    if (showMapOverlay) closeMapOverlay();
                    else if (!showBag && !showShop && !w2ShopOpen && !showCaveScene && !showWelcome) openMapOverlay();
                }
            }
        });
    }

    private void loadMap(String path) {
        try {
            File file=new File(path); if (!file.exists()) { System.err.println("Map not found: "+path); return; }
            String content=new java.util.Scanner(file).useDelimiter("\\Z").next();
            mapWidth=Integer.parseInt(content.split("width=\"")[1].split("\"")[0]);
            mapHeight=Integer.parseInt(content.split("height=\"")[1].split("\"")[0]);
            String[] layers=content.split("<data encoding=\"csv\">"); allLayerData=new int[layers.length-1][mapHeight][mapWidth];
            for (int l=1;l<layers.length;l++) {
                String csvData=layers[l].split("</data>")[0].trim(); String[] values=csvData.split(",");
                for (int i=0;i<values.length&&i<mapWidth*mapHeight;i++) {
                    int r=i/mapWidth,c=i%mapWidth;
                    try { allLayerData[l-1][r][c]=Integer.parseInt(values[i].trim()); } catch (NumberFormatException e) { allLayerData[l-1][r][c]=0; }
                }
            }
            loadTilesets(content);
            System.out.println("[MAP] Loaded: "+path+" ("+mapWidth+"x"+mapHeight+")");
        } catch (Exception e) { System.err.println("Error loading map: "+e.getMessage()); e.printStackTrace(); }
    }

    private void loadTilesets(String tmxContent) {
        try {
            String[] blocks=tmxContent.split("<tileset ");
            for (int i=1;i<blocks.length;i++) {
                String block=blocks[i];
                int firstGid=Integer.parseInt(block.split("firstgid=\"")[1].split("\"")[0]);
                String tsxSrc=block.split("source=\"")[1].split("\"")[0];
                File tsxFile=null;
                String[] tsxPaths={"resources/"+tsxSrc,tsxSrc,"resources/"+new File(tsxSrc).getName(),"resources/Tilesets/"+new File(tsxSrc).getName()};
                for (String p:tsxPaths) { File f=new File(p); if (f.exists()) { tsxFile=f; break; } }
                if (tsxFile==null) { System.err.println("[MAP] TSX not found: "+tsxSrc); continue; }
                String tsxContent=new java.util.Scanner(tsxFile).useDelimiter("\\Z").next();
                int tw=TILE_SIZE,th=TILE_SIZE;
                if (tsxContent.contains("tilewidth=\"")) {
                    tw=Integer.parseInt(tsxContent.split("tilewidth=\"")[1].split("\"")[0]);
                    th=Integer.parseInt(tsxContent.split("tileheight=\"")[1].split("\"")[0]);
                }
                String imgSrc=tsxContent.split("source=\"")[1].split("\"")[0];
                String imgName=new File(imgSrc).getName();
                File imgFile=null;
                String[] imgPaths={"resources/"+imgSrc,imgSrc,"resources/Texture/"+imgName,"resources/Tilesets/"+imgName,"resources/"+imgName};
                for (String p:imgPaths) { File f=new File(p); if (f.exists()) { imgFile=f; break; } }
                if (imgFile==null) { System.err.println("[MAP] Tileset image not found: "+imgSrc); continue; }
                BufferedImage tilesetImg=ImageIO.read(imgFile);
                int cols=tilesetImg.getWidth()/tw,rows=tilesetImg.getHeight()/th;
                for (int r=0;r<rows;r++) for (int c=0;c<cols;c++) {
                    int gid=firstGid+(r*cols+c),sx=c*tw,sy=r*th;
                    if (sx+tw>tilesetImg.getWidth()||sy+th>tilesetImg.getHeight()) continue;
                    BufferedImage tile=tilesetImg.getSubimage(sx,sy,tw,th);
                    BufferedImage copy=new BufferedImage(tw,th,BufferedImage.TYPE_INT_ARGB);
                    Graphics2D tg=copy.createGraphics(); tg.drawImage(tile,0,0,null); tg.dispose();
                    tileCache.put(gid,copy);
                }
                System.out.println("[MAP] Loaded tileset: "+imgFile.getName()+" ("+cols*rows+" tiles, GID "+firstGid+")");
            }
        } catch (Exception e) { System.err.println("Error loading tilesets: "+e.getMessage()); e.printStackTrace(); }
    }
}