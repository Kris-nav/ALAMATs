package game.gui;

import game.core.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class WorldPanel extends JPanel implements Runnable {
    private Thread gameThread;
    private KeyHandler keyH = new KeyHandler();

    private final int TILE_SIZE = 16;
    private final int SCALE = 4;
    private final int TILE_DISPLAY_SIZE = TILE_SIZE * SCALE;

    // Player
    private int playerX = 1900;
    private int playerY = 2300;
    private final int PLAYER_SPEED = 3;
    private final int PLAYER_SIZE_W = 16;
    private final int PLAYER_SIZE_H = 16;
    private BufferedImage playerSheet;

    // Animation
    private int frameCounter = 10;
    private int frameDelay = 8;
    private int currentFrame = 0;
    private int currentRow = 0;

    // Camera
    private int cameraX = 0;
    private int cameraY = 0;

    // Collision
    private Set<Integer> solidTiles = new HashSet<>();

    // Map
    private int[][][] allLayerData;
    private int mapWidth;
    private int mapHeight;
    private HashMap<Integer, BufferedImage> tileCache = new HashMap<>();

    public WorldPanel() {
        this.setPreferredSize(new Dimension(1280, 720));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        try {
            playerSheet = ImageIO.read(new File("resources/Texture/Avatar1.png"));
            System.out.println("Player sheet: "
                    + playerSheet.getWidth() + "x" + playerSheet.getHeight());
        } catch (Exception e) {
            System.err.println("Could not load player: " + e.getMessage());
        }

        loadMap("resources/World1.tmx");
        initSolidTiles();
    }

    private void initSolidTiles() {
        // Add your solid tile IDs here
        solidTiles.add(838);
        // Add more IDs as needed — check Tiled by hovering over tiles
        // solidTiles.add(839);
        // solidTiles.add(900);
    }

    private boolean isSolid(int worldX, int worldY) {
        int tileCol = worldX / TILE_DISPLAY_SIZE;
        int tileRow = worldY / TILE_DISPLAY_SIZE;

        if (tileCol < 0 || tileRow < 0 || tileCol >= mapWidth || tileRow >= mapHeight) {
            return true;
        }

        for (int layer = 0; layer < allLayerData.length; layer++) {
            int tileId = allLayerData[layer][tileRow][tileCol];
            if (solidTiles.contains(tileId)) {
                return true;
            }
        }
        return false;
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

            mapWidth = Integer.parseInt(content.split("width=\"")[1].split("\"")[0]);
            mapHeight = Integer.parseInt(content.split("height=\"")[1].split("\"")[0]);
            System.out.println("Map size: " + mapWidth + "x" + mapHeight);

            String[] layers = content.split("<data encoding=\"csv\">");
            System.out.println("Found " + (layers.length - 1) + " layers");

            allLayerData = new int[layers.length - 1][mapHeight][mapWidth];

            for (int l = 1; l < layers.length; l++) {
                String csvData = layers[l].split("</data>")[0].trim();
                String[] values = csvData.split(",");

                for (int i = 0; i < values.length && i < mapWidth * mapHeight; i++) {
                    int row = i / mapWidth;
                    int col = i % mapWidth;
                    try {
                        allLayerData[l - 1][row][col] = Integer.parseInt(values[i].trim());
                    } catch (NumberFormatException e) {
                        allLayerData[l - 1][row][col] = 0;
                    }
                }
            }

            System.out.println("All layers loaded!");
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
                String block = tilesetBlocks[i];

                int firstGid = Integer.parseInt(block.split("firstgid=\"")[1].split("\"")[0]);
                String tsxSource = block.split("source=\"")[1].split("\"")[0];

                File tsxFile = new File("resources/" + tsxSource);
                if (!tsxFile.exists()) tsxFile = new File(tsxSource);
                if (!tsxFile.exists()) {
                    System.out.println("Cannot find tsx: " + tsxSource);
                    continue;
                }

                String tsxContent = new java.util.Scanner(tsxFile)
                        .useDelimiter("\\Z").next();

                int tileWidth = TILE_SIZE;
                int tileHeight = TILE_SIZE;
                if (tsxContent.contains("tilewidth=\"")) {
                    tileWidth = Integer.parseInt(
                            tsxContent.split("tilewidth=\"")[1].split("\"")[0]);
                    tileHeight = Integer.parseInt(
                            tsxContent.split("tileheight=\"")[1].split("\"")[0]);
                }

                String imgSource = tsxContent.split("source=\"")[1].split("\"")[0];

                File imgFile = new File("resources/" + imgSource);
                if (!imgFile.exists()) {
                    imgFile = new File("resources/Texture/" +
                            new File(imgSource).getName());
                }
                if (!imgFile.exists()) {
                    System.out.println("Cannot find image: " + imgSource);
                    continue;
                }

                BufferedImage tilesetImage = ImageIO.read(imgFile);
                System.out.println("Loaded: " + imgFile.getName()
                        + " (" + tilesetImage.getWidth() + "x"
                        + tilesetImage.getHeight() + ")"
                        + " firstGid=" + firstGid);

                int columns = tilesetImage.getWidth() / tileWidth;
                int rows = tilesetImage.getHeight() / tileHeight;

                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < columns; col++) {
                        int localId = row * columns + col;
                        int globalId = firstGid + localId;

                        int srcX = col * tileWidth;
                        int srcY = row * tileHeight;

                        if (srcX + tileWidth > tilesetImage.getWidth() ||
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

                System.out.println("Cached so far: " + tileCache.size());
            }

            System.out.println("Total tiles cached: " + tileCache.size());

        } catch (Exception e) {
            System.err.println("Error loading tilesets: " + e.getMessage());
            e.printStackTrace();
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

        // Check collision at all 4 corners of the player
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
                frameCounter = 0;
                currentFrame = (currentFrame + 1) % 3;
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
    }

    @Override
    public void run() {
        while (gameThread != null) {
            update();
            repaint();
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
                int frameWidth  = playerSheet.getWidth()  / 3;
                int frameHeight = playerSheet.getHeight() / 4;

                int srcX = currentFrame * frameWidth;
                int srcY = currentRow   * frameHeight;

                g2.drawImage(playerSheet,
                        playerScreenX,
                        playerScreenY,
                        playerScreenX + PLAYER_SIZE_W,
                        playerScreenY + PLAYER_SIZE_H,
                        srcX, srcY,
                        srcX + frameWidth,
                        srcY + frameHeight,
                        null);
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