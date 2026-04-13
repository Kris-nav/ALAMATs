package game.gui;

import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JWindow {
    public SplashScreen(int duration) {
        JPanel content = new JPanel(new BorderLayout());

        // Since 'resources' is at the project root, we point to it directly

        ImageIcon icon = new ImageIcon("resources/Texture/background.gif");
        JLabel label = new JLabel(icon);

        content.add(label, BorderLayout.CENTER);
        getContentPane().add(content);

        // Adjust window to GIF size and center it
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        setVisible(false);
        dispose();
    }
}