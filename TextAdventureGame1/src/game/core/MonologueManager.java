package game.core;

import game.gui.GameScene;

public class MonologueManager {

    /**
     * Helper to type out text onto the GUI screen slowly.
     * UPDATED: Ensure it stays thread-safe with the scroll auto-adjustment.
     */
    public static void printSlowlyToGUI(GameScene gameGui, String message, long delay) {
        for (char ch : message.toCharArray()) {
            gameGui.addChar(String.valueOf(ch));
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        gameGui.addChar("\n\n");
    }

    /**
     * GUI Cinematic: Intro
     */
    public static void runIntroInGUI(GameScene gameGui, String playerName) {
        gameGui.setNextButtonEnabled(false);
        gameGui.setNextButtonText("Wait...");
        gameGui.clearText();

        printSlowlyToGUI(gameGui, "You are a college student returning to your home province during a break.", 40);
        printSlowlyToGUI(gameGui, "After months of city life, the quiet roads bring a sense of peace.", 40);

        String nameRef = (playerName != null && !playerName.isEmpty()) ? ", " + playerName + "," : "";
        printSlowlyToGUI(gameGui, "Three hours into the ride" + nameRef + " you remember a creature your grandpa spoke of.", 40);
        printSlowlyToGUI(gameGui, "“What was it called again…? Ah, yes... a Sigbin.”", 50);

        printSlowlyToGUI(gameGui, "Fun Fact: The Sigbin is a legendary creature in Philippine folklore.", 40);
        printSlowlyToGUI(gameGui, "It’s said to come out at night and suck blood from shadows.", 40);
        printSlowlyToGUI(gameGui, "It walks backwards, with its head between its legs.", 40);

        gameGui.setNextButtonText("Next");
        gameGui.setNextButtonEnabled(true);
    }

    /**
     * GUI Cinematic: The Encounter
     */
    public static void runStoryPart1InGUI(GameScene gameGui, String playerName) {
        gameGui.setNextButtonEnabled(false);
        gameGui.setNextButtonText("Wait...");
        gameGui.clearText();

        String shout = (playerName != null && !playerName.isEmpty()) ? "“BE CAREFUL, " + playerName.toUpperCase() + "!”" : "“BE CAREFUL!”";
        printSlowlyToGUI(gameGui, "Suddenly, your grandpa shouted: " + shout, 60);
        printSlowlyToGUI(gameGui, "A real Sigbin reveals itself from the shadows.", 50);
        printSlowlyToGUI(gameGui, "Your grandfather reveals he is an Albularyo and begins your training.", 50);

        gameGui.setNextButtonText("Next");
        gameGui.setNextButtonEnabled(true);
    }

    /**
     * GUI Cinematic: The Abduction
     */
    public static void runAbductionMonologueInGUI(GameScene gameGui) {
        gameGui.setNextButtonEnabled(false);
        gameGui.setNextButtonText("Wait...");
        gameGui.clearText();

        printSlowlyToGUI(gameGui, "A Tikbalang appears from the trees and grabs your grandfather!", 40);
        printSlowlyToGUI(gameGui, "He is gone. You are alone in the woods.", 50);

        gameGui.setNextButtonText("Next");
        gameGui.setNextButtonEnabled(true);
    }

    /**
     * GUI Cinematic: The Cave Revelation
     */
    public static void runQuestMonologueInGUI(GameScene gameGui) {
        gameGui.setNextButtonEnabled(false);
        gameGui.setNextButtonText("Wait...");
        gameGui.clearText();

        printSlowlyToGUI(gameGui, "“To save him, you must find the 4 Anting-Anting amulets.”", 50);
        printSlowlyToGUI(gameGui, "Defeat the albularyos guarding the towns to reclaim them.", 50);

        gameGui.setNextButtonText("Next");
        gameGui.setNextButtonEnabled(true);
    }

    /**
     * GUI Cinematic: The Info Scene
     */
    public static void runInfoMonologueInGUI(GameScene gameGui) {
        gameGui.setNextButtonEnabled(false);
        gameGui.setNextButtonText("Wait...");
        gameGui.clearText();

        printSlowlyToGUI(gameGui, "SYSTEM NOTIFICATION:", 40);
        printSlowlyToGUI(gameGui, "Stats Initialized Albularyo Apprentice.", 40);
        printSlowlyToGUI(gameGui, "The fate of your grandfather rests in your hands.", 50);

        gameGui.setNextButtonText("Begin Adventure");
        gameGui.setNextButtonEnabled(true);
    }

    // =========================================================================
    // CONSOLE METHODS
    // =========================================================================

    public static void printFinalAlbularyoMonologue() {
        System.out.println("\n---------------------------------------------------");
        TextAdventureGame.printSlowlyWithDelay("\nAHH yes... You may have defeated my creatures...", TextAdventureGame.TEXT_DELAY);
        TextAdventureGame.printSlowlyWithDelay("But you have not seen it all. (A figure steps forward... it is Sir Khai!)", TextAdventureGame.TEXT_DELAY);
        TextAdventureGame.waitForInput();
        TextAdventureGame.printSlowlyWithDelay("\nSir Khai: YESSS... It was me. You FAILED my sacred exam.", TextAdventureGame.TEXT_DELAY);
        TextAdventureGame.printSlowlyWithDelay("THEY MERGE… REVEALING THE FINAL FORM: KHAIBALANG", TextAdventureGame.TEXT_DELAY);
        System.out.println("---------------------------------------------------\n");
        TextAdventureGame.waitForInput();
    }

    public static void printPostKhaibalangMonologue() {
        TextAdventureGame.printSlowlyWithDelay("\nThe battlefield is silent. Sir Khai’s monstrous form collapses.", TextAdventureGame.TEXT_DELAY);
        TextAdventureGame.printSlowlyWithDelay("GRANDPA: You did it... free at last.", TextAdventureGame.TEXT_DELAY);
        TextAdventureGame.waitForInput();
    }

    public static boolean printTown2Monologue() {
        System.out.println("\n*** WELCOME TO TOWN 2 ***\n");
        TextAdventureGame.printSlowlyWithDelay("I need to get stronger if I’m going to save my grandpa.", TextAdventureGame.TEXT_DELAY);
        System.out.println("Do you want to enter Town 2? (y/n)");
        String confirmation = TextAdventureGame.waitForInput().toLowerCase();
        return confirmation.equals("y") || confirmation.equals("yes");
    }

    public static void printTown3Monologue() {
        System.out.println("\n*** WELCOME TO MANOLO FORTICH ***\n");
        TextAdventureGame.printSlowlyWithDelay("Finally... just one more Anting-Anting to go.", TextAdventureGame.TEXT_DELAY);
        TextAdventureGame.waitForInput();
    }

    public static void printStoryPart1() {
        System.out.println();
        TextAdventureGame.printSlowlyWithDelay("Suddenly, your grandpa shouted: “BE CAREFUL!”", TextAdventureGame.TEXT_DELAY);
        TextAdventureGame.printSlowlyWithDelay("The rock moved. It shifted and twisted, revealing a horrifying creature—a real Sigbin.", TextAdventureGame.TEXT_DELAY);
        TextAdventureGame.printSlowlyWithDelay("Terrified, you froze.", TextAdventureGame.TEXT_DELAY);
        TextAdventureGame.printSlowlyWithDelay("Your grandfather shared a secret—He was an Albularyo.", TextAdventureGame.TEXT_DELAY);
        TextAdventureGame.printSlowlyWithDelay("He began teaching you his secrets over the next three months.", TextAdventureGame.TEXT_DELAY);
        System.out.println("\nPress Enter to continue...");
        TextAdventureGame.waitForInput();
    }

    public static void printStoryPart3() {
        System.out.println();
        TextAdventureGame.printSlowlyWithDelay("One night, you and your grandfather were deep in the dark woods.", TextAdventureGame.TEXT_DELAY);
        TextAdventureGame.printSlowlyWithDelay("A towering creature with the head of a horse and the body of a man appeared—a Tikbalang.", TextAdventureGame.TEXT_DELAY);
        TextAdventureGame.printSlowlyWithDelay("The creature lunged—grabbed your grandfather—and disappeared into the trees.", TextAdventureGame.TEXT_DELAY);
        System.out.println("\nPress Enter to continue...");
        TextAdventureGame.waitForInput();
    }

    public static void printStoryPart4() {
        System.out.println();
        TextAdventureGame.printSlowlyWithDelay("A towering creature with the head of a horse and the body of a man appeared—a Tikbalang.", TextAdventureGame.TEXT_DELAY);
        TextAdventureGame.printSlowlyWithDelay("The creature lunged—grabbed your grandfather—and disappeared into the trees.", TextAdventureGame.TEXT_DELAY);
        System.out.println("\nPress Enter to continue...");
        TextAdventureGame.waitForInput();
    }

    public static void printStoryPart5() {
        System.out.println();
        TextAdventureGame.printSlowlyWithDelay("Your journey begins. Your grandfather has been taken.", TextAdventureGame.TEXT_DELAY);
        TextAdventureGame.printSlowlyWithDelay("“If you want to save him, you must find the 4 ancient amulets—the Anting-Anting.”", TextAdventureGame.TEXT_DELAY);
        TextAdventureGame.printSlowlyWithDelay("To obtain them, you must defeat 3 powerful albularyos guarding different towns.", TextAdventureGame.TEXT_DELAY);
        System.out.println("\nPress Enter to continue...");
        TextAdventureGame.waitForInput();
    }
}