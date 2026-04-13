package game.gui;

import game.core.*;
import org.mapeditor.core.Map;
import org.mapeditor.core.Tile;
import org.mapeditor.core.TileLayer;
import org.mapeditor.io.TMXMapReader;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class WorldPanel extends JPanel implements Runnable {
    private Thread gameThread;
    private KeyHandler keyH = new KeyHandler();
    private Player player = new Player("resources/Texture/Avatar1.png");

    // NEW: Use the Map object from libtiled
    private Map map;
    private final int TILE_DISPLAY_SIZE = 48;

    public WorldPanel() {
        this.setPreferredSize(new Dimension(1000, 700));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        // Load the Map using libtiled
        loadMap("resources/samplemap1.tmx");
    }

    public void start() {
        if (gameThread == null) {
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    private void loadMap(String path) {
        try {
            TMXMapReader mapReader = new TMXMapReader();
            // Wrap the path string in a File object
            this.map = mapReader.readMap(new File(path).getAbsolutePath());

            // OR simply:
            // this.map = mapReader.read(path);
            // If the above still fails, some versions of libtiled require:
            // this.map = mapReader.read(new java.io.File(path).toURI().toURL().toString());

            System.out.println("Map Loaded Successfully: " + map.getWidth() + "x" + map.getHeight());
        } catch (Exception e) {
            System.err.println("Error loading map via libtiled: " + e.getMessage());
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

        if (map != null) {
            // Get the first layer of the map (index 0)
            TileLayer layer = (TileLayer) map.getLayer(0);

            for (int y = 0; y < map.getHeight(); y++) {
                for (int x = 0; x < map.getWidth(); x++) {
                    // Get the tile object at these coordinates
                    Tile tile = layer.getTileAt(x, y);

                    if (tile != null) {
                        Image tileImage = tile.getImage();
                        // Draw the tile scaled to your TILE_DISPLAY_SIZE
                        g2.drawImage(tileImage,
                                x * TILE_DISPLAY_SIZE,
                                y * TILE_DISPLAY_SIZE,
                                TILE_DISPLAY_SIZE,
                                TILE_DISPLAY_SIZE,
                                null);
                    }
                }
            }
        }

        if (player != null) {
            player.draw(g2);
        }
        g2.dispose();
    }
}