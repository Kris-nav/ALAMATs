package game.core;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Player {
    public int x = 200, y = 200;
    public int speed = 4;
    private BufferedImage sprite;
    private int size = 16 * 3; // Matching the map scale

    public Player(String path) {
        try {
            sprite = ImageIO.read(new File(path));
        } catch (Exception e) {
            System.out.println("Could not load Avatar1.png");
        }
    }

    public void update(boolean up, boolean down, boolean left, boolean right) {
        if (up) y -= speed;
        if (down) y += speed;
        if (left) x -= speed;
        if (right) x += speed;
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(sprite, x, y, size, size, null);
    }
}