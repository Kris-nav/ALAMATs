package game.gui;

import game.core.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

public class WorldPanel extends JPanel implements Runnable {
    private Thread gameThread;
    private KeyHandler keyH = new KeyHandler();
    private Player player = new Player("resources/Texture/Avatar1.png");

    private final int TILE_SIZE = 16;
    private final int SCALE = 3;
    private final int TILE_DISPLAY_SIZE = TILE_SIZE * SCALE;

    private int[][][] allLayerData;
    private int mapWidth;
    private int mapHeight;
    private HashMap<Integer, BufferedImage> tileCache = new HashMap<>();

    public WorldPanel() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setPreferredSize(screenSize);
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        loadMap("resources/World1.tmx");
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

                String tsxContent = new java.util.Scanner(tsxFile).useDelimiter("\\Z").next();

                int tileWidth = TILE_SIZE;
                int tileHeight = TILE_SIZE;
                if (tsxContent.contains("tilewidth=\"")) {
                    tileWidth = Integer.parseInt(tsxContent.split("tilewidth=\"")[1].split("\"")[0]);
                    tileHeight = Integer.parseInt(tsxContent.split("tileheight=\"")[1].split("\"")[0]);
                }

                String imgSource = tsxContent.split("source=\"")[1].split("\"")[0];

                File imgFile = new File("resources/" + imgSource);
                if (!imgFile.exists()) {
                    imgFile = new File("resources/Texture/" + new File(imgSource).getName());
                }
                if (!imgFile.exists()) {
                    System.out.println("Cannot find image: " + imgSource);
                    continue;
                }

                BufferedImage tilesetImage = ImageIO.read(imgFile);
                System.out.println("Loaded: " + imgFile.getName()
                        + " (" + tilesetImage.getWidth() + "x" + tilesetImage.getHeight() + ")"
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

                        BufferedImage tileImg = tilesetImage.getSubimage(srcX, srcY, tileWidth, tileHeight);
                        BufferedImage copy = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_ARGB);
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

    @Override
    public void run() {
        while (gameThread != null) {
            player.update(keyH.up, keyH.down, keyH.left, keyH.right);
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

                        BufferedImage img = tileCache.get(tileId);
                        if (img != null) {
                            g2.drawImage(img,
                                    col * TILE_DISPLAY_SIZE,
                                    row * TILE_DISPLAY_SIZE,
                                    TILE_DISPLAY_SIZE,
                                    TILE_DISPLAY_SIZE,
                                    null);
                        } else {
                            g2.setColor(Color.MAGENTA);
                            g2.fillRect(col * TILE_DISPLAY_SIZE, row * TILE_DISPLAY_SIZE,
                                    TILE_DISPLAY_SIZE, TILE_DISPLAY_SIZE);
                        }
                    }
                }
            }
        } else {
            g2.setColor(Color.RED);
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.drawString("Map failed to load!", 50, 50);
        }

        if (player != null) {
            player.draw(g2);
        }
    }
}