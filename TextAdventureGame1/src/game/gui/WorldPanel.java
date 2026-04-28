package game.gui;

import game.battle.Fighter;
import game.core.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
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

    // ✅ inBattle prevents multiple triggers
    private boolean inBattle = false;

    // ✅ cooldownUntil is a timestamp - System.currentTimeMillis() > cooldownUntil = ok to encounter
    private long encounterCooldownUntil;

    private int[][][] allLayerData;
    private int mapWidth;
    private int mapHeight;
    private HashMap<Integer, BufferedImage> tileCache = new HashMap<>();

    // ✅ First time constructor
    public WorldPanel(GameScene gameScene) {
        this(gameScene, 4205, 5125,
                new ArrayList<>(),
                game.battle.Create.createPlayerStarter(),
                3,
                0L);
    }

    // ✅ Full constructor - restores ALL state including cooldown
    public WorldPanel(GameScene gameScene,
                      int startX, int startY,
                      ArrayList<Fighter> team,
                      Fighter playerFighter,
                      int scrollCount,
                      long cooldownUntil) {
        this.gameScene             = gameScene;
        this.playerX               = startX;
        this.playerY               = startY;
        this.capturedTeam          = team;
        this.playerFighter         = playerFighter;
        this.scrollCount           = scrollCount;
        this.encounterCooldownUntil = cooldownUntil; // ✅ restored directly

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

    private void initSolidTiles() {
        solidTiles.add(838);
        solidTiles.add(201);
        solidTiles.add(202);
        solidTiles.add(203);
        solidTiles.add(230);
        solidTiles.add(228);
        solidTiles.add(257);
        solidTiles.add(255);
        solidTiles.add(703);
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

        // ✅ Block if cooldown has not expired yet
        if (System.currentTimeMillis() < encounterCooldownUntil) return;

        int feetX   = playerX + PLAYER_SIZE_W / 2;
        int feetY   = playerY + PLAYER_SIZE_H;
        int tileCol = feetX / TILE_DISPLAY_SIZE;
        int tileRow = feetY / TILE_DISPLAY_SIZE;

        if (tileCol < 0 || tileRow < 0 ||
                tileCol >= mapWidth || tileRow >= mapHeight) return;

        if (allLayerData[2][tileRow][tileCol] == 758) {
            if (Math.random() > 0.05) return;

            // ✅ Lock immediately so no second trigger
            inBattle = true;

            int savedX = playerX;
            int savedY = playerY;

            game.battle.Fighter wildFighter = game.battle.Create.randomWildCreature();

            SwingUtilities.invokeLater(() ->
                    // ✅ Pass savedX/savedY - GameScene handles all routing
                    gameScene.switchToBattle(
                            playerFighter,
                            wildFighter,
                            capturedTeam,
                            scrollCount,
                            savedX,
                            savedY)
            );
        }
    }

    public void start() {
        if (gameThread == null) {
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    private void loadMap(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                System.err.println("Map file not found: " + path);
                return;
            }
            String content = new java.util.Scanner(file).useDelimiter("\\Z").next();
            mapWidth  = Integer.parseInt(content.split("width=\"")[1].split("\"")[0]);
            mapHeight = Integer.parseInt(content.split("height=\"")[1].split("\"")[0]);

            String[] layers = content.split("<data encoding=\"csv\">");
            allLayerData = new int[layers.length - 1][mapHeight][mapWidth];

            for (int l = 1; l < layers.length; l++) {
                String csvData = layers[l].split("</data>")[0].trim();
                String[] values = csvData.split(",");
                for (int i = 0; i < values.length && i < mapWidth * mapHeight; i++) {
                    int row = i / mapWidth;
                    int col = i % mapWidth;
                    try {
                        allLayerData[l-1][row][col] = Integer.parseInt(values[i].trim());
                    } catch (NumberFormatException e) {
                        allLayerData[l-1][row][col] = 0;
                    }
                }
            }
            loadTilesets(content);
        } catch (Exception e) {
            System.err.println("Error loading map: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadTilesets(String tmxContent) {
        try {
            String[] tilesetBlocks = tmxContent.split("<tileset ");
            for (int i = 1; i < tilesetBlocks.length; i++) {
                String block     = tilesetBlocks[i];
                int firstGid     = Integer.parseInt(block.split("firstgid=\"")[1].split("\"")[0]);
                String tsxSource = block.split("source=\"")[1].split("\"")[0];

                File tsxFile = new File("resources/" + tsxSource);
                if (!tsxFile.exists()) tsxFile = new File(tsxSource);
                if (!tsxFile.exists()) continue;

                String tsxContent  = new java.util.Scanner(tsxFile).useDelimiter("\\Z").next();
                int tileWidth  = TILE_SIZE;
                int tileHeight = TILE_SIZE;
                if (tsxContent.contains("tilewidth=\"")) {
                    tileWidth  = Integer.parseInt(tsxContent.split("tilewidth=\"")[1].split("\"")[0]);
                    tileHeight = Integer.parseInt(tsxContent.split("tileheight=\"")[1].split("\"")[0]);
                }

                String imgSource = tsxContent.split("source=\"")[1].split("\"")[0];
                File imgFile = new File("resources/" + imgSource);
                if (!imgFile.exists())
                    imgFile = new File("resources/Texture/" + new File(imgSource).getName());
                if (!imgFile.exists()) continue;

                BufferedImage tilesetImage = ImageIO.read(imgFile);
                int columns = tilesetImage.getWidth()  / tileWidth;
                int rows    = tilesetImage.getHeight() / tileHeight;

                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < columns; col++) {
                        int globalId = firstGid + (row * columns + col);
                        int srcX     = col * tileWidth;
                        int srcY     = row * tileHeight;
                        if (srcX + tileWidth  > tilesetImage.getWidth() ||
                                srcY + tileHeight > tilesetImage.getHeight()) continue;

                        BufferedImage tileImg = tilesetImage.getSubimage(
                                srcX, srcY, tileWidth, tileHeight);
                        BufferedImage copy = new BufferedImage(
                                tileWidth, tileHeight, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D tg = copy.createGraphics();
                        tg.drawImage(tileImg, 0, 0, null);
                        tg.dispose();
                        tileCache.put(globalId, copy);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading tilesets: " + e.getMessage());
        }
    }

    private void update() {
        boolean moving = false;
        int newX = playerX;
        int newY = playerY;

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
                        if (img != null) {
                            g2.drawImage(img, drawX, drawY,
                                    TILE_DISPLAY_SIZE, TILE_DISPLAY_SIZE, null);
                        }
                    }
                }
            }

            int playerScreenX = playerX - cameraX;
            int playerScreenY = playerY - cameraY;

            if (playerSheet != null) {
                int frameWidth  = 25;
                int frameHeight = 25;
                int srcX = currentFrame * frameWidth;
                int srcY = currentRow   * frameHeight;
                g2.drawImage(playerSheet,
                        playerScreenX, playerScreenY,
                        playerScreenX + PLAYER_SIZE_W,
                        playerScreenY + PLAYER_SIZE_H,
                        srcX, srcY,
                        srcX + frameWidth,
                        srcY + frameHeight, null);
            } else {
                g2.setColor(Color.RED);
                g2.fillRect(playerScreenX, playerScreenY,
                        PLAYER_SIZE_W, PLAYER_SIZE_H);
            }
        } else {
            g2.setColor(Color.RED);
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.drawString("Map failed to load!", 50, 50);
        }
    }
}