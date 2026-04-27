package game.core;

import game.battle.BattleMove;

import java.util.*;

public class GameFlowManager {

    public static void enterName() {
        System.out.println("What is your name, adventurer?");
        do {
            System.out.print("> ");
            GameStatus.playerName = TextAdventureGame.scanner.nextLine().trim();

            if (GameStatus.playerName.isEmpty()) {
                System.out.println("Please enter a valid name to continue.");
            }
        } while (GameStatus.playerName.isEmpty());
        System.out.println("Welcome, " + GameStatus.playerName + "!\n");

        System.out.println("Are you Male or Female?");
        while (true) {
            System.out.print("> ");
            String genderChoice = TextAdventureGame.waitForInput().trim().toLowerCase();
            if (genderChoice.equals("male") || genderChoice.equals("m")) {
                GameStatus.playerGender = "Male";
                break;
            } else if (genderChoice.equals("female") || genderChoice.equals("f")) {
                GameStatus.playerGender = "Female";
                break;
            } else {
                System.out.println("Invalid choice. Please type 'Male' or 'Female'.");
            }
        }

        System.out.println("\nWhat is your age?");
        while (true) {
            System.out.print("> ");
            try {
                String ageInput = TextAdventureGame.waitForInput().trim();
                GameStatus.playerAge = Integer.parseInt(ageInput);

                if (GameStatus.playerAge >= 18) {
                    break;
                } else {
                    System.out.println("As a college student, your age must be 18 or older. Please enter a valid age.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numerical age.");
            }
        }
        System.out.println("Starting the adventure, " + GameStatus.playerName + " " + GameStatus.playerGender + " of " + GameStatus.playerAge + "...\n");
    }

    public static void startNewGame() {
        GameStatus.playerPotions = 3;
        GameStatus.playerScrolls = 5;
        GameStatus.playerHerbs = 5;
        GameStatus.playerMegaPotions = 0;
        GameStatus.playerGold = 300;
        GameStatus.playerTeam.clear();

        System.out.println("Choose your starter Creature:");
        System.out.println("1. Kapre");
        System.out.println("2. Tikbalang");
        System.out.println("3. Aswang");

        while (true) {
            System.out.print("> ");
            String choice = TextAdventureGame.waitForInput().trim().toLowerCase();
            switch (choice) {
                case "1":
                case "kapre":
                    chooseCreature("kapre");
                    return;
                case "2":
                case "tikbalang":
                    chooseCreature("tikbalang");
                    return;
                case "3":
                case "aswang":
                    chooseCreature("aswang");
                    return;
                default:
                    System.out.println("Invalid choice. Please choose again.");
                    break;
            }
        }
    }

    public static void chooseCreature(String choice) {
        Creature starter = new Creature(GameData.allCreatures.get(choice));
        GameStatus.playerTeam.add(starter);
        GameStatus.playerActiveCreature = starter;
        GameStatus.playerLocation = "home";
        System.out.println("You chose " + GameStatus.playerActiveCreature.name + "!");
        explore();
    }

    public static void explore() {
        GameStatus.gamePhase = "explore";
        while(GameStatus.isPlaying){

            if (GameStatus.playerLocation.equals("town_2_entrance") && !GameStatus.enteredTown2Monologue && GameStatus.firstAlbularyoDefeated) {
                if (MonologueManager.printTown2Monologue()) {
                    GameStatus.enteredTown2Monologue = true;
                    GameStatus.playerLocation = "town_2_center";
                } else {
                    System.out.println("You hesitate, staying at the entrance for now.");
                }
                continue;
            }

            if (GameStatus.playerLocation.equals("road_to_manolo_fortich") && !GameStatus.enteredTown3Monologue && GameStatus.secondAlbularyoDefeated) {
                MonologueManager.printTown3Monologue();
                GameStatus.enteredTown3Monologue = true;
                GameStatus.playerLocation = "manolo_fortich_entrance";
                continue;
            }

            System.out.println("\n--- " + GameStatus.playerName + "'s Adventure ---");
            String currentTown = getCurrentTown();
            System.out.println("---- " + currentTown + " -----");

            System.out.println("It is currently " + GameStatus.timeOfDay + ".");
            System.out.println(GameData.allLocations.get(GameStatus.playerLocation).description);

            if (GameStatus.playerLocation.equals("the_first_albularyo") && !GameStatus.firstAlbularyoDefeated) {
                handleAlbularyoLocation(1);
                if (!GameStatus.isPlaying) return;
            } else if (GameStatus.playerLocation.equals("albularyo_2") && !GameStatus.secondAlbularyoDefeated) {
                handleAlbularyoLocation(2);
                if (!GameStatus.isPlaying) return;
            } else if (GameStatus.playerLocation.equals("albularyo_3") && !GameStatus.thirdAlbularyoDefeated){
                handleAlbularyoLocation(3);
                if (!GameStatus.isPlaying) return;
            }

            if (GameStatus.currentQuestIndex > 0 && GameStatus.currentQuestIndex < 5) {
                ProgressionManager.displayCurrentQuestStatus();
            }

            if (GameData.allLocations.get(GameStatus.playerLocation).event != null) {
                handleLocationEvent(GameData.allLocations.get(GameStatus.playerLocation).event);
                if (!GameStatus.isPlaying) return;
            }

            System.out.println("What will you do? (north/south/east/west, 'status', 'bag', 'release' or 'quit')");
            System.out.print("> ");
            String action = TextAdventureGame.waitForInput().trim().toLowerCase();

            if (action.equals("peksonmaster153")) {
                ProgressionManager.activateAdminMode();
                continue;
            }
            if (action.equals("skip town 1") && GameStatus.isSageModeActive) {
                ProgressionManager.skipTown1();
                continue;
            }
            if (action.equals("skip town 2") && GameStatus.isSageModeActive) {
                ProgressionManager.skipTown2();
                continue;
            }
            if (action.equals("skip albularyo 3") && GameStatus.isSageModeActive) {
                ProgressionManager.skipFinalAlbularyo();
                continue;
            }
            if (action.equals("skip khaibalang") && GameStatus.isSageModeActive) {
                ProgressionManager.skipKhaibalang();
                continue;
            }

            if (GameData.allLocations.get(GameStatus.playerLocation).exits.containsKey(action)) {
                travel(action);
            } else if (action.equals("status")) {
                showStatus();
            } else if (action.equals("bag")) {
                InventoryManager.showBagExplore();
            } else if (action.equals("release")) {
                InventoryManager.releaseCreature(true);
            } else if (action.equals("quit")) {
                System.out.println("See you next time!");
                GameStatus.isPlaying = false;
            } else {
                System.out.println("Invalid command. Please try again.");
            }
        }
    }

    private static void handleAlbularyoLocation(int albularyoNumber) {
        if (albularyoNumber == 1) {
            if (!GameStatus.firstAlbularyoDefeated) {
                System.out.println(GameStatus.playerName + ":");
                System.out.println("The first Albularyo stands before you. \"You have come for the Anting-Anting... Very well, prove your strength!\"");
                BattleCore.startAlbularyoBattle(1);
            } else {
                System.out.println("yes you have defetead me and won but you won survive the next town and you'll never find your grandpa");
                GameData.allLocations.get("the_first_albularyo").exits.put("east", "town_2_entrance");
            }
        } else if (albularyoNumber == 2) {
            if (!GameStatus.secondAlbularyoDefeated) {
                System.out.println(GameStatus.playerName + ":");
                System.out.println("The second Albularyo sneers. \"Another weakling seeking power. I'll make sure you join your creatures in the darkness!\"");
                BattleCore.startAlbularyoBattle(2);
            } else {
                System.out.println("The second Albularyo's domain is quiet. You have already claimed the Anting-Anting here.");
            }
        } else if (albularyoNumber == 3) {
            if (!GameStatus.thirdAlbularyoDefeated) {
                System.out.println(GameStatus.playerName + ":");
                System.out.println("The final Albularyo rises before you. \"So, you have collected a few Anting-Antings. But your journey ends here!\"");
                BattleCore.startAlbularyoBattle(3);
            } else {
                System.out.println("You have already defeated the final Albularyo.");
            }
        }
    }

    public static void showStatus() {
        System.out.println("\n--- " + GameStatus.playerName + "'s CREATURE STATUS ---");
        System.out.println("Gender: " + GameStatus.playerGender + " | Age: " + GameStatus.playerAge);
        System.out.println("CREATURES: " + GameStatus.playerTeam.size() + "/" + GameStatus.MAX_TEAM_SIZE);
        System.out.println("Anting-Anting: " + GameStatus.antingAntingCount + "/3");
        System.out.println("Gold: " + GameStatus.playerGold);
        System.out.println("Inventory: Lunas (" + GameStatus.playerPotions + ") | Mega Lunas (" + GameStatus.playerMegaPotions + ") | Scrolls (" + GameStatus.playerScrolls + ") | Herbs (" + GameStatus.playerHerbs + ")");

        for (int i = 0; i < GameStatus.playerTeam.size(); i++) {
            Creature creature = GameStatus.playerTeam.get(i);
            System.out.printf("%d. Name: %s (Lvl. %d, Type: %s)\n", (i + 1), creature.name, creature.level, creature.type);
            System.out.printf("   HP: %.0f/%.0f | XP: %d/%d\n", creature.hp, creature.maxHp, creature.xp, creature.xpToNextLevel);
            System.out.println("--------------------");
        }

        System.out.println("\nEnter the creature's name (or number) for full details, or 'back' to return to exploration.");
        System.out.print("> ");
        String choice = TextAdventureGame.waitForInput().trim().toLowerCase();

        if (choice.equals("back")) return;

        Creature selectedCreature = null;
        try {
            int index = Integer.parseInt(choice) - 1;
            if (index >= 0 && index < GameStatus.playerTeam.size()) {
                selectedCreature = GameStatus.playerTeam.get(index);
            }
        } catch (NumberFormatException e) {
            for (Creature creature : GameStatus.playerTeam) {
                if (creature.name.toLowerCase().equals(choice)) {
                    selectedCreature = creature;
                    break;
                }
            }
        }

        if (selectedCreature != null) {
            showCreatureDetails(selectedCreature);
        } else {
            System.out.println("Invalid creature selection.");
            showStatus();
        }
    }

    public static void showCreatureDetails(Creature creature) {
        System.out.println("\n*** " + creature.name.toUpperCase() + " - FULL STATUS ***");
        System.out.println("Level: " + creature.level + " | Type: " + creature.type);
        System.out.printf("HP: %.0f/%.0f | XP: %d/%d\n", creature.hp, creature.maxHp, creature.xp, creature.xpToNextLevel);
        System.out.println("Attack: " + creature.attack + " | Defense: " + creature.defense);
        System.out.printf("Speed: %d (Base: %d) | Accuracy: %.0f%%\n", creature.getCurrentSpd(), creature.baseSpd, creature.getCurrentAccuracy() * 100);
        System.out.println("------------------------------------------");
        System.out.println("Lore: " + creature.age + " old, lives in " + creature.habitat + ". Personality: " + creature.personality);

        System.out.println("\nMOVES:");
        for (int i = 0; i < creature.moves.size(); i++) {
            BattleMove move = creature.moves.get(i);
            int requiredLevel = creature.skillsByLevel.getOrDefault(move.name, Integer.MAX_VALUE);
            String readyStatus = (creature.level >= requiredLevel) ? " (READY)" : "";
            System.out.printf("%d. %s (Lvl. %d Req) [PP: %d/%d]%s\n", (i + 1), move.name, requiredLevel, move.pp, move.maxPp, readyStatus);
        }

        System.out.println("\nEnter move number for description, or 'back'.");
        System.out.print("> ");
        String moveChoice = TextAdventureGame.waitForInput().trim().toLowerCase();

        if (moveChoice.equals("back")) {
            showStatus();
            return;
        }

        try {
            int moveIndex = Integer.parseInt(moveChoice) - 1;
            if (moveIndex >= 0 && moveIndex < creature.moves.size()) {
                showMoveDescription(creature.moves.get(moveIndex));
                showCreatureDetails(creature);
            }
        } catch (Exception e) {
            showCreatureDetails(creature);
        }
    }

    public static void showMoveDescription(BattleMove move) {
        System.out.println("\n--- MOVE DETAILS: " + move.name.toUpperCase() + " ---");
        System.out.println("Type: " + move.type + " | Damage: " + move.damage + " | Hit Chance: " + move.hitChance + "%");
        System.out.println("Description: " + (move.flavorText != null ? move.flavorText : "A standard ability."));
        System.out.println("Press Enter to continue...");
        TextAdventureGame.waitForInput();
    }

    public static void handleLocationEvent(String event) {
        if (event.equals("heal")) {
            System.out.println("Your creatures have been fully healed.");
            InventoryManager.healAllCreatures();
        } else if (event.startsWith("shop")) {
            InventoryManager.handleShopEvent(GameStatus.playerLocation);
        } else if (event.equals("magic_well_event")) {
            handleMagicWellEvent();
        }
    }

    public static void handleMagicWellEvent() {
        System.out.println("The Magic Well shimmers. Toss 1 Gold for a gift? (y/n)");
        System.out.print("> ");
        if (TextAdventureGame.waitForInput().startsWith("y")) {
            if (GameStatus.playerGold >= 1) {
                GameStatus.playerGold--;
                System.out.println("The well bubbles... you found a Lunas!");
                GameStatus.playerPotions++;
            } else {
                System.out.println("You have no gold.");
            }
        }
    }

    public static void travel(String direction) {
        Map<String, String> exits = GameData.allLocations.get(GameStatus.playerLocation).exits;
        String nextLocation = exits.get(direction);

        if (nextLocation == null) {
            System.out.println("You can't go that way.");
            return;
        }

        if (nextLocation.equals("town_2_entrance") && !GameStatus.firstAlbularyoDefeated) {
            System.out.println("The Albularyo blocks your path. Defeat him first.");
            return;
        }

        if (nextLocation.equals("town_2_entrance") && !GameStatus.enteredTown2Monologue) {
            if (GameStatus.playerActiveCreature.level < GameStatus.TOWN_2_LEVEL_GATE && GameStatus.currentQuestIndex < 5) {
                ProgressionManager.handleQuestBlock();
                System.out.println("You need a Level " + GameStatus.TOWN_2_LEVEL_GATE + " creature to pass.");
                return;
            }
        }

        GameStatus.playerLocation = nextLocation;
        System.out.println("You travel " + direction + " to " + GameStatus.playerLocation + ".");

        double chance = 0.2; // Simplified encounter logic for this copy
        if (GameData.locationEncounters.containsKey(GameStatus.playerLocation) && TextAdventureGame.random.nextDouble() < chance) {
            BattleCore.startEncounter(false);
        }
    }

    private static String getCurrentTown() {
        if (GameStatus.TOWN_1_LOCATIONS.contains(GameStatus.playerLocation)) return "TOWN 1";
        if (GameStatus.TOWN_2_LOCATIONS.contains(GameStatus.playerLocation)) return "TOWN 2";
        if (GameStatus.TOWN_3_LOCATIONS.contains(GameStatus.playerLocation)) return "TOWN 3";
        return "WILDERNESS";
    }
}