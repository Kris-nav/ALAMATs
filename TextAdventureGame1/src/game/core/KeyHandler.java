package game.core;

import java.awt.event.*;

public class KeyHandler implements KeyListener {
    public boolean up, down, left, right;
    public boolean bagJustPressed  = false;
    public boolean shopJustPressed = false;
    private boolean bagWasDown  = false;
    private boolean shopWasDown = false;

    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W) up    = true;
        if (code == KeyEvent.VK_S) down  = true;
        if (code == KeyEvent.VK_A) left  = true;
        if (code == KeyEvent.VK_D) right = true;
        if (code == KeyEvent.VK_B && !bagWasDown) {
            bagJustPressed = true;
            bagWasDown     = true;
        }
        if (code == KeyEvent.VK_E && !shopWasDown) {
            shopJustPressed = true;
            shopWasDown     = true;
        }
    }

    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W) up    = false;
        if (code == KeyEvent.VK_S) down  = false;
        if (code == KeyEvent.VK_A) left  = false;
        if (code == KeyEvent.VK_D) right = false;
        if (code == KeyEvent.VK_B) bagWasDown  = false;
        if (code == KeyEvent.VK_E) shopWasDown = false;
    }

    public void keyTyped(KeyEvent e) {}
}