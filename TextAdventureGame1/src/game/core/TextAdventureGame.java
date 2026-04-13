package game.core;

import game.gui.MainMenu;
import game.gui.GameScene;
import java.util.*;

public class TextAdventureGame {

    public static final Scanner scanner = new Scanner(System.in);
    public static final Random random = new Random();
    public static final long TEXT_DELAY = 50;

    public static void main(String[] args) {
        // 1. Show Menu
        MainMenu menu = new MainMenu();
        menu.waitForPlay();
        menu.dispose();

        // 2. Load Data
        GameData.initializeData();

        // 3. Setup GUI
        ProgressionManager pm = new ProgressionManager();
        GameScene gameGui = new GameScene(pm);
        pm.setGameScene(gameGui);

        gameGui.setVisible(true);
        pm.startStory();

        // 4. Console Game Loop
        while (GameStatus.isPlaying) {
            GameFlowManager.enterName();
            GameFlowManager.startNewGame();
        }

        System.out.println("Thanks for playing!");
    }

    public static void printSlowly(String message, long delay) {
        for (char ch : message.toCharArray()) {
            System.out.print(ch);
            try { Thread.sleep(delay); } catch (InterruptedException e) {}
        }
        System.out.println();
    }

    public static void printSlowlyWithDelay(String message, long delay) {
        printSlowly(message, delay);
    }

    public static String waitForInput() {
        return scanner.nextLine().toLowerCase();
    }
}