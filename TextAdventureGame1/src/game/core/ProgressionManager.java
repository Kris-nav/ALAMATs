package game.core;

import game.gui.GameScene;
import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

public class ProgressionManager {
    private GameScene gameScene;
    private int sceneIndex = 0;

    public void setGameScene(GameScene scene) {
        this.gameScene = scene;
    }

    public void startStory() {
        if (gameScene != null) {
            gameScene.updateDisplay("resources/Texture/bus.png", "");
            new Thread(() -> {
                MonologueManager.runIntroInGUI(gameScene, GameStatus.playerName);
            }).start();
        }
    }

    public void handleNextScene() {
        if (gameScene == null) return;

        if (sceneIndex == 0) {
            gameScene.updateDisplay("resources/Texture/sigbin.png", "");
            new Thread(() -> {
                MonologueManager.runStoryPart1InGUI(gameScene, GameStatus.playerName);
            }).start();
            sceneIndex++;
        }
        else if (sceneIndex == 1) {
            gameScene.updateDisplay("resources/Texture/Abs.png", "");
            new Thread(() -> {
                MonologueManager.runAbductionMonologueInGUI(gameScene);
            }).start();
            sceneIndex++;
        }
        else if (sceneIndex == 2) {
            gameScene.updateDisplay("resources/Texture/friend.png", "");
            new Thread(() -> {
                MonologueManager.runQuestMonologueInGUI(gameScene);
            }).start();
            sceneIndex++;
        }
        else if (sceneIndex == 3) {
            gameScene.updateDisplay("resources/Texture/info_scroll.png", "");
            new Thread(() -> {
                MonologueManager.runInfoMonologueInGUI(gameScene);
            }).start();
            sceneIndex++;
        }
        else {
            // UPDATED logic
            System.out.println("\n--- THE ADVENTURE BEGINS ---");
            GameStatus.isPlaying = true;

            // Swaps the view to the WorldPanel
            gameScene.switchToWorld();
        }
    }

    public static void displayCurrentQuestStatus() {
        System.out.println("\n--- CURRENT QUEST ---");
        switch (GameStatus.currentQuestIndex) {
            case 1: System.out.println("Objective: Defeat the Aswang."); break;
            case 2: System.out.println("Objective: Find the Albularyo."); break;
            default: System.out.println("Objective: Find your Grandpa."); break;
        }
        System.out.println("----------------------");
    }

    public static void handleQuestBlock() {
        System.out.println("You feel a heavy weight in the air. A mystical force suggests you aren't ready to advance yet.");
    }

    public static void gainXP(Creature creature, int xpGained, boolean isQuestReward) {
        creature.xp += xpGained;
        while (creature.xp >= creature.xpToNextLevel) {
            creature.xp -= creature.xpToNextLevel;
            creature.level++;
            System.out.println("Level up! " + creature.name + " is now Level " + creature.level);
        }
    }

    public static void completeQuest(int questNumber, int xpReward) {
        System.out.printf("\n*** QUEST %d COMPLETE! ***\n", questNumber);
        List<Creature> activeCreatures = GameStatus.playerTeam.stream()
                .filter(c -> c.hp > 0)
                .collect(Collectors.toList());

        if (activeCreatures.isEmpty()) {
            System.out.println("No active creatures to receive the bonus XP!");
            GameStatus.currentQuestIndex++;
            return;
        }

        int xpPerCreature = xpReward / activeCreatures.size();
        for (Creature creature : activeCreatures) {
            gainXP(creature, xpPerCreature, true);
        }
        GameStatus.currentQuestIndex++;
    }

    public static void checkQuestProgress(String defeatedCreatureName) {
        if (GameStatus.currentQuestIndex == 1 && defeatedCreatureName.equalsIgnoreCase("aswang")) {
            System.out.println("Quest Progress: You've defeated an Aswang!");
        }
    }

    public static void handleKhaibalangDefeated() {
        System.out.println("\n=================================================");
        System.out.println("   KHAIBALANG HAS BEEN DEFEATED! ");
        System.out.println("   You have saved your grandfather and the province!");
        System.out.println("=================================================");
        GameStatus.isPlaying = false;
    }

    public static void activateAdminMode() {
        GameStatus.playerGold = 9999;
        GameStatus.playerPotions = 99;
        GameStatus.playerMegaPotions = 99;
        GameStatus.isSageModeActive = true;
        for (Creature creature : GameStatus.playerTeam) {
            creature.level = 50;
            creature.maxHp = 500;
            creature.hp = 500;
        }
        System.out.println("SAGE MODE ACTIVATED. You have attained godhood.");
    }

    public static void skipTown1() {
        System.out.println("DEBUG: Skipping Town 1...");
        GameStatus.firstAlbularyoDefeated = true;
        GameStatus.antingAntingCount = Math.max(GameStatus.antingAntingCount, 1);
        GameStatus.currentQuestIndex = 3;
        GameStatus.playerLocation = "town_2_entrance";
    }

    public static void skipTown2() {
        System.out.println("DEBUG: Skipping Town 2...");
        GameStatus.secondAlbularyoDefeated = true;
        GameStatus.antingAntingCount = Math.max(GameStatus.antingAntingCount, 2);
        GameStatus.currentQuestIndex = 5;
        GameStatus.playerLocation = "road_to_manolo_fortich";
    }

    public static void skipFinalAlbularyo() {
        System.out.println("DEBUG: Skipping Final Albularyo...");
        GameStatus.thirdAlbularyoDefeated = true;
        GameStatus.antingAntingCount = 3;
        GameStatus.playerLocation = "manolo_fortich_entrance";
    }

    public static void skipKhaibalang() {
        System.out.println("DEBUG: Ending Game...");
        handleKhaibalangDefeated();
    }
}