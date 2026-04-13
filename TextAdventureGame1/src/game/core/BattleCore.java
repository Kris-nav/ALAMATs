package game.core;

import java.util.*;
import java.util.stream.Collectors;

public class BattleCore {

    public static boolean allCreaturesFainted() {
        for (Creature creature : GameStatus.playerTeam) {
            if (creature.hp > 0) {
                return false;
            }
        }
        return true;
    }

    public static void startEncounter(boolean isTown2Or3) {
        String[] wildCreatures = GameData.locationEncounters.get(GameStatus.playerLocation);
        String rivalChoice = wildCreatures[TextAdventureGame.random.nextInt(wildCreatures.length)];

        Creature baseCreature = GameData.allCreatures.get(rivalChoice);
        int baseLevel;

        if (isTown2Or3) {
            baseLevel = 9 + TextAdventureGame.random.nextInt(7);
        } else {
            baseLevel = 3 + TextAdventureGame.random.nextInt(5);
        }

        GameStatus.rivalCreature = new Creature(baseCreature.name, baseCreature.type, baseLevel,
                baseCreature.moves, baseCreature.skillsByLevel, baseCreature.baseSpd,
                baseCreature.age, baseCreature.habitat, baseCreature.personality,
                (int)(baseCreature.maxHp), baseCreature.attack, baseCreature.defense, baseCreature.potions, baseCreature.scrolls, baseCreature.herbs);
        GameStatus.rivalCreature.level = baseLevel;
        GameStatus.rivalCreature.maxHp = 75 + (GameStatus.rivalCreature.level - 1) * 10;
        GameStatus.rivalCreature.hp = GameStatus.rivalCreature.maxHp;

        System.out.println("It's an " + GameStatus.rivalCreature.name + "!!!");
        boolean playerWon = runBattle(false);

        if (GameStatus.rivalCapturedInBattle) {
            endBattle(true, true, false);
        } else if (playerWon) {
            endBattle(true, false, false);
        } else if (GameStatus.playerActiveCreature.hp <= 0 && allCreaturesFainted()) {
            endBattle(false, false, false);
        } else if (GameStatus.playerRanAway) {
            endBattle(false, false, true);
        }
    }

    public static void startAlbularyoBattle(int albularyoNumber) {
        String[] albularyoCreatureNames;
        int rivalLevel;
        int xpReward;
        double xpMultiplier = GameStatus.XP_MULTIPLIER_ALBULARYO;

        if (albularyoNumber == 1) {
            albularyoCreatureNames = new String[]{"aswang", "kapre", "bakunawa"};
            rivalLevel = 15;
            xpReward = 100;
        } else if (albularyoNumber == 2) {
            albularyoCreatureNames = new String[]{"manananggal", "mangkukulam", "amihan"};
            rivalLevel = 25;
            xpReward = 500;
        } else if (albularyoNumber == 3) {
            albularyoCreatureNames = new String[]{"higaonon_sky_serpent", "palaopao_echoer", "kulaman_marsh_shade"};
            rivalLevel = 30;
            xpReward = 1000;
        } else {
            return;
        }

        GameStatus.currentAlbularyoCreatureCount = albularyoCreatureNames.length;

        for (int i = 0; i < albularyoCreatureNames.length; i++) {
            String creatureName = albularyoCreatureNames[i];
            Creature baseCreature = GameData.allCreatures.get(creatureName);
            GameStatus.rivalCreature = new Creature(baseCreature.name, baseCreature.type, rivalLevel,
                    baseCreature.moves, baseCreature.skillsByLevel, baseCreature.baseSpd,
                    baseCreature.age, baseCreature.habitat, baseCreature.personality,
                    75 + (rivalLevel - 1) * 10, baseCreature.attack, baseCreature.defense, baseCreature.potions, baseCreature.scrolls, baseCreature.herbs);

            System.out.printf("\nThe Albularyo sends out %s! (Creature %d/%d)\n",
                    GameStatus.rivalCreature.name, (i + 1), GameStatus.currentAlbularyoCreatureCount);

            boolean wonBattle = runBattle(true);

            if (!wonBattle) {
                System.out.println("You lost the battle against the Albularyo.");
                endBattle(false, false, false);
                return;
            }
            System.out.println("You defeated the Albularyo's " + GameStatus.rivalCreature.name + "!");

            GameStatus.playerActiveCreature.resetBattleStats();
        }

        System.out.println("\nYou have defeated all of the Albularyo's creatures!");
        if (albularyoNumber == 1) {
            System.out.println("NOOO HOW COULD YOU DEFEATED ME I MORE POWERFUL THEN YOUUUU " + GameStatus.playerName);
            System.out.println("He collapses, and a glowing amulet falls from his hand. You pick up the " + (GameStatus.antingAntingCount + 1) + "st Anting-Anting!");
            GameStatus.antingAntingCount++;
            GameStatus.firstAlbularyoDefeated = true;
        } else if (albularyoNumber == 2) {
            System.out.println("\"This is impossible! My curse was meant to stop you!\" the Albularyo screams before dissolving into smoke.");
            System.out.println("A powerful, shimmering amulet is left behind. You pick up the " + (GameStatus.antingAntingCount + 1) + "nd Anting-Anting!");
            GameStatus.antingAntingCount++;
            GameStatus.secondAlbularyoDefeated = true;
        } else if (albularyoNumber == 3) {
            System.out.println("You have defeated all of the Final Albularyo's creatures! You pick up the 3rd Anting-Anting!");
            GameStatus.antingAntingCount++;
            GameStatus.thirdAlbularyoDefeated = true;

            MonologueManager.printFinalAlbularyoMonologue();

            handleFinalBossBattle();
        }

        int goldReward = (TextAdventureGame.random.nextInt(50) + 30) * 2;
        GameStatus.playerGold += goldReward;
        System.out.println("You received " + goldReward + " gold!");

        int finalXPReward = (int)(xpReward * xpMultiplier);
        ProgressionManager.gainXP(GameStatus.playerActiveCreature, finalXPReward, false);
    }

    public static void handleFinalBossBattle() {
        Creature boss = GameData.allCreatures.get("khaibalang");
        GameStatus.rivalCreature = new Creature(boss);

        System.out.println("\n*** FINAL BATTLE! ***");
        System.out.println("The merged form of Sir Khai and the Tikbalang, **Khaibalang**, emerges from the shadows!");
        System.out.println("You must defeat this monstrous creature to save your Grandpa!");

        boolean playerWon = runBattle(true);

        if (playerWon) {
            ProgressionManager.handleKhaibalangDefeated();
        } else {
            endBattle(false, false, false);
        }
    }

    public static boolean runBattle(boolean isBossBattle) {
        GameStatus.gamePhase = "battle";
        GameStatus.roundCounter = 1;

        GameStatus.playerActiveCreature.resetBattleStats();
        GameStatus.rivalCreature.resetBattleStats();

        GameStatus.playerRanAway = false;
        GameStatus.rivalCapturedInBattle = false;

        while (GameStatus.playerActiveCreature.hp > 0 && GameStatus.rivalCreature.hp > 0 && !GameStatus.playerRanAway && !GameStatus.rivalCapturedInBattle) {

            // --- UPDATED BATTLE DISPLAY WITH OWNER INDICATOR ---
            System.out.println("\n--- BATTLE STATUS (Round " + GameStatus.roundCounter + ") ---");

            // Determine Rival Owner
            String rivalOwner;
            if (GameStatus.rivalCreature.name.equals("Khaibalang")) {
                rivalOwner = "Khaibalang (BOSS)";
            } else if (isBossBattle) {
                rivalOwner = "Albularyo";
            } else {
                rivalOwner = "Wild Creature";
            }

            // Rival Display
            System.out.println(rivalOwner + ":");
            System.out.printf("%s (Lvl. %d) HP: %.0f/%.0f\n", GameStatus.rivalCreature.name, GameStatus.rivalCreature.level, GameStatus.rivalCreature.hp, GameStatus.rivalCreature.maxHp);

            // Player Display
            System.out.println("\n" + GameStatus.playerName + ":");
            System.out.printf("%s (Lvl. %d) HP: %.0f/%.0f\n", GameStatus.playerActiveCreature.name, GameStatus.playerActiveCreature.level, GameStatus.playerActiveCreature.hp, GameStatus.playerActiveCreature.maxHp);

            System.out.println("What will you do? (attack/bag/run/switch)");
            System.out.print(">");

            // --- Apply Passive Buffs ---
            GameStatus.playerActiveCreature.applyPassiveBuffs();
            GameStatus.rivalCreature.applyPassiveBuffs();
            // ---------------------------

            String choice = TextAdventureGame.waitForInput().toLowerCase();
            boolean actionTaken = false;
            boolean wasInterrupted = false;

            if (GameStatus.playerActiveCreature.isStunned) {
                System.out.println(GameStatus.playerActiveCreature.name + " is **Stunned** and cannot take an action this turn!");
                actionTaken = true;
                GameStatus.playerActiveCreature.isStunned = false;
            } else {
                switch (choice) {
                    case "attack":
                        wasInterrupted = AttackHandler.playerAttackChoice();
                        actionTaken = true;
                        break;
                    case "bag":
                        actionTaken = InventoryManager.playerBagChoice();
                        if (GameStatus.rivalCapturedInBattle) break;
                        break;
                    case "run":
                        AttackHandler.tryToRun(isBossBattle);
                        actionTaken = true;
                        if (GameStatus.playerRanAway) break;
                        break;
                    case "switch":
                        actionTaken = showSwitchMenu(false);
                        break;
                    default:
                        System.out.println("Invalid action. You lose your turn!");
                        actionTaken = true;
                        break;
                }
            }

            if (GameStatus.playerActiveCreature.hp <= 0) {
                if (!checkPlayerFaint()) {
                    return false;
                }
            }

            if (GameStatus.rivalCapturedInBattle || GameStatus.playerRanAway) {
                break;
            }

            // Rival's Turn. Skip if interrupted (Khaibalang used its turn).
            if (GameStatus.rivalCreature.hp > 0 && !wasInterrupted) {
                System.out.println("\n--- Rival's Turn ---");
                if (GameStatus.rivalCreature.isStunned) {
                    System.out.println(GameStatus.rivalCreature.name + " is **Stunned** and skips its turn!");
                    GameStatus.rivalCreature.isStunned = false;
                } else if (GameStatus.playerActiveCreature.hasUsedInvisibility) {
                    System.out.println(GameStatus.rivalCreature.name + " can't see " + GameStatus.playerActiveCreature.name + "!");
                    GameStatus.playerActiveCreature.hasUsedInvisibility = false;
                } else {
                    AttackHandler.rivalAttack();
                }
            }

            // Post-turn effects and checks
            if (GameStatus.rivalCreature.hp > 0 && GameStatus.rivalCreature.isUnderBurnEffect) {
                double burnDamage = GameStatus.rivalCreature.attack * 0.4;
                GameStatus.rivalCreature.hp -= burnDamage;
                System.out.printf("%s is **Burned**! It takes %.0f damage.\n", GameStatus.rivalCreature.name, burnDamage);
            }

            if (GameStatus.playerActiveCreature.hp <= 0) {
                if (!checkPlayerFaint()) {
                    return false;
                }
            }

            // Reset single-turn effects
            GameStatus.rivalCreature.hasUsedInvisibility = false;
            GameStatus.playerActiveCreature.hasWindBarrierActive = false;
            GameStatus.rivalCreature.hasWindBarrierActive = false;

            GameStatus.roundCounter++;
        }

        if (GameStatus.playerRanAway || GameStatus.rivalCapturedInBattle) {
            return false;
        } else if (GameStatus.playerActiveCreature.hp > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean showSwitchMenu(boolean mustSwitch) {
        List<Creature> switchable = GameStatus.playerTeam.stream()
                .filter(creature -> creature.hp > 0)
                .collect(Collectors.toList());

        if (switchable.isEmpty()) {
            return false;
        }

        while (true) {
            System.out.println("\n--- SWITCH CREATURE ---");
            System.out.println("Current Active: " + GameStatus.playerActiveCreature.name + " (Lvl. " + GameStatus.playerActiveCreature.level + ") HP: " + (int)GameStatus.playerActiveCreature.hp + "/" + (int)GameStatus.playerActiveCreature.maxHp);
            System.out.println("Choose a creature to switch to:");

            for (int i = 0; i < switchable.size(); i++) {
                Creature creature = switchable.get(i);
                System.out.printf("%d. %s (Lvl. %d) HP: %.0f/%.0f\n", (i + 1), creature.name, creature.level, creature.hp, creature.maxHp);
            }

            if (!mustSwitch) {
                System.out.println("Back");
            } else {
                System.out.println("You MUST choose an active creature to continue.");
            }

            String choice = TextAdventureGame.waitForInput().toLowerCase();

            if (!mustSwitch && choice.equals("back")) {
                return false;
            }

            try {
                int index = Integer.parseInt(choice);
                if (index > 0 && index <= switchable.size()) {
                    Creature newCreature = switchable.get(index - 1);
                    if (newCreature.name.equals(GameStatus.playerActiveCreature.name) && !mustSwitch) {
                        System.out.println("That creature is already active. Please choose another one.");
                        continue;
                    }
                    System.out.println(GameStatus.playerActiveCreature.name + " returns!");

                    GameStatus.playerActiveCreature = newCreature;
                    System.out.println("Go! " + GameStatus.playerActiveCreature.name + "!");
                    GameStatus.playerActiveCreature.hasUsedInvisibility = false;
                    return true;
                } else {
                    System.out.println("Invalid number. Please choose again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
    }

    public static boolean checkPlayerFaint() {
        List<Creature> remainingCreatures = GameStatus.playerTeam.stream()
                .filter(creature -> creature.hp > 0)
                .collect(Collectors.toList());

        if (remainingCreatures.isEmpty()) {
            return false;
        }

        if (GameStatus.playerActiveCreature.hp <= 0) {
            System.out.println(GameStatus.playerActiveCreature.name + " fainted! You must switch creatures.");
            return showSwitchMenu(true);
        }

        return true;
    }


    public static void endBattle(boolean playerWon, boolean rivalCaught, boolean ranAway) {
        GameStatus.gamePhase = "explore";

        GameStatus.playerTeam.forEach(Creature::resetBattleStats);
        if (GameStatus.rivalCreature != null) {
            GameStatus.rivalCreature.resetBattleStats();
        }

        if (ranAway) {
            System.out.println("You successfully ran away from the battle. All creatures' battle stats have been reset.");
            return;
        } else if (rivalCaught) {
            System.out.println("Congratulations! You added " + GameStatus.rivalCreature.name + " to your team! All creatures' battle stats have been reset.");
            return;
        } else if (playerWon) {
            System.out.println(GameStatus.rivalCreature.name + " fainted! You won the battle! All creatures' battle stats have been reset.");

            if (GameStatus.rivalCreature.name.equals("Khaibalang")) {
                double xpMultiplier = GameStatus.XP_MULTIPLIER_KHAIBALANG;
                int baseXP = 500;
                int finalXPGained = (int) (baseXP * xpMultiplier);

                ProgressionManager.gainXP(GameStatus.playerActiveCreature, finalXPGained, false);
                ProgressionManager.handleKhaibalangDefeated();
                return;
            }

            // --- XP MODIFICATION APPLIED HERE ---
            double xpMultiplier = GameStatus.XP_MULTIPLIER_WILD;

            // Base XP for wild battles
            int baseXP = TextAdventureGame.random.nextInt(30) + 50;

            // ADDED BONUS XP
            final int XP_BONUS = 350;
            int boostedBaseXP = baseXP + XP_BONUS;

            System.out.println("\n*** XP BONUS: +" + XP_BONUS + " added to base reward! ***");

            int levelDifference = GameStatus.rivalCreature.level - GameStatus.playerActiveCreature.level;
            double levelMultiplier = 1.0 + (levelDifference * 0.1);

            // Use the boosted base XP in the final calculation
            int finalXPGained = (int) (boostedBaseXP * Math.max(0.1, levelMultiplier) * xpMultiplier);
            // --- END XP MODIFICATION ---

            ProgressionManager.gainXP(GameStatus.playerActiveCreature, finalXPGained, false);
            ProgressionManager.checkQuestProgress(GameStatus.rivalCreature.name);

            int baseGold = TextAdventureGame.random.nextInt(20) + 10;
            int goldReward = baseGold * 2;
            GameStatus.playerGold += goldReward;
            System.out.println("You received " + goldReward + " gold!");
            return;
        } else {
            System.out.println(GameStatus.playerActiveCreature.name + " fainted. You lost the battle. All your Creatures fainted!");

            // Healing Logic
            if (GameStatus.playerLocation.startsWith("manolo_fortich") || GameStatus.playerLocation.startsWith("road_to_manolo_fortich") || GameStatus.playerLocation.startsWith("albularyo_3") || GameStatus.playerLocation.equals("maurin_house")) {
                System.out.println("You awaken, having been carried by a passerby to the safety of Maurin's House. You feel refreshed.");
                GameStatus.playerLocation = "maurin_house";
            }
            else if (GameStatus.playerLocation.startsWith("town_2") || GameStatus.playerLocation.startsWith("lawa") || GameStatus.playerLocation.startsWith("latian") || GameStatus.playerLocation.startsWith("albularyo_2")) {
                System.out.println("You awaken, having been carried by a passerby to Danielle's house. Your creatures are now healed.");
                GameStatus.playerLocation = "danielle_house";
            }
            else {
                System.out.println("You were brought back home. All your creatures are now healed.");
                GameStatus.playerLocation = "home";
            }

            InventoryManager.healAllCreatures();
        }
    }
}