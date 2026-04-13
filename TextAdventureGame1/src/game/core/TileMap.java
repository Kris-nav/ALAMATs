package game.core;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class TileMap {
    private BufferedImage tileset;
    private int[] mapData;
    private int mapWidth, mapHeight;
    private int tileWidth = 16, tileHeight = 16;
    private int columnsInTileset;
    private int scale = 3; // Zooming in so it looks like a GBA

    public TileMap(String tilesetPath, String mapPath) {
        try {
            tileset = ImageIO.read(new File(tilesetPath));
            columnsInTileset = tileset.getWidth() / tileWidth;

            // Simple parser for the .tmj JSON file
            String content = new Scanner(new File(mapPath)).useDelimiter("\\Z").next();
            mapWidth = Integer.parseInt(content.split("\"width\":")[1].split(",")[0].trim());
            mapHeight = Integer.parseInt(content.split("\"height\":")[1].split(",")[0].trim());

            String dataPart = content.split("\"data\":\\[")[1].split("\\]")[0];
            String[] values = dataPart.split(",");
            mapData = new int[values.length];
            for (int i = 0; i < values.length; i++) {
                mapData[i] = Integer.parseInt(values[i].trim());
            }
        } catch (Exception e) {
            System.out.println("Error loading map: " + e.getMessage());
        }
    }

    public void draw(Graphics2D g2) {
        for (int i = 0; i < mapData.length; i++) {
            int tileId = mapData[i];
            if (tileId == 0) continue;

            int index = tileId - 1;
            int sx = (index % columnsInTileset) * tileWidth;
            int sy = (index / columnsInTileset) * tileHeight;

            int x = (i % mapWidth) * (tileWidth * scale);
            int y = (i / mapWidth) * (tileHeight * scale);

            g2.drawImage(tileset, x, y, x + (tileWidth * scale), y + (tileHeight * scale),
                    sx, sy, sx + tileWidth, sy + tileHeight, null);
        }
    }
}