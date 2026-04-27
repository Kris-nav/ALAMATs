package game.core;

import game.battle.BattleMove;

public class InventoryManager {

    public static boolean showItemDescriptionAndConfirm(String item) {
        String description = "";
        if (item.equals("lunas")) {
            description = "A strong herbal potion that restores 30 HP to one of your creatures.";
        } else if (item.equals("mega_lunas")) {
            description = "A powerful, shimmering potion that restores 100 HP to one of your creatures.";
        } else if (item.equals("scroll")) {
            description = "An ancient parchment used to attempt to capture wild mythical creatures.";
        } else if (item.equals("herbal")) {
            description = "A mixture of potent leaves used to restore 5 PP to a single move.";
        }

        System.out.println("\n--- " + item.toUpperCase().replace("_", " ") + " ---");
        System.out.println(description);
        System.out.println("Use " + item.replace("_", " ") + "? (y/n)");

        String confirmation = TextAdventureGame.waitForInput().toLowerCase();
        if (confirmation.equals("y" ) || confirmation.equals("yes")) {
            return true;
        } else {
            System.out.println("You put the " + item.replace("_", " ") + " back in the bag.");
            return false;
        }
    }

    public static boolean playerBagChoice() {
        System.out.println("--- BAG ---");
        System.out.println("1. Lunas (" + GameStatus.playerPotions + ")");
        System.out.println("2. Mega Lunas (" + GameStatus.playerMegaPotions + ")");
        System.out.println("3. Scroll (" + GameStatus.playerScrolls + ")");
        System.out.println("4. Herbal (" + GameStatus.playerHerbs + ")");
        System.out.println("Back");

        String choice = TextAdventureGame.waitForInput().toLowerCase();
        switch (choice) {
            case "1":
            case "lunas":
                if (GameStatus.playerPotions > 0 && showItemDescriptionAndConfirm("lunas")) {
                    useHealingItemBattle("lunas");
                    return true;
                }
                return false;
            case "2":
            case "mega lunas":
            case "megalunas":
                if (GameStatus.playerMegaPotions > 0 && showItemDescriptionAndConfirm("mega_lunas")) {
                    useHealingItemBattle("mega_lunas");
                    return true;
                }
                return false;
            case "3":
            case "scroll":
                if (GameStatus.playerScrolls > 0 && showItemDescriptionAndConfirm("scroll")) {
                    useScroll();
                    return true;
                }
                return false;
            case "4":
            case "herbal":
                if (GameStatus.playerHerbs > 0 && showItemDescriptionAndConfirm("herbal")) {
                    useHerbalBattle();
                    return true;
                }
                return false;
            case "back":
                return false;
            default:
                System.out.println("Invalid choice.");
                return false;
        }
    }

    public static void showBagExplore() {
        System.out.println("--- BAG ---");
        System.out.println("1. Lunas (" + GameStatus.playerPotions + ")");
        System.out.println("2. Mega Lunas (" + GameStatus.playerMegaPotions + ")");
        System.out.println("3. Scroll (" + GameStatus.playerScrolls + ")");
        System.out.println("4. Herbal (" + GameStatus.playerHerbs + ")");
        System.out.println("Back");

        String choice = TextAdventureGame.waitForInput().toLowerCase();
        switch (choice) {
            case "1":
            case "lunas":
                if (GameStatus.playerPotions > 0 && showItemDescriptionAndConfirm("lunas")) {
                    useHealingItemExplore("lunas");
                }
                break;
            case "2":
            case "mega lunas":
            case "megalunas":
                if (GameStatus.playerMegaPotions > 0 && showItemDescriptionAndConfirm("mega_lunas")) {
                    useHealingItemExplore("mega_lunas");
                }
                break;
            case "3":
            case "scroll":
                if (showItemDescriptionAndConfirm("scroll")) {
                    System.out.println("Scrolls can only be used in battle!");
                }
                break;
            case "4":
            case "herbal":
                if (GameStatus.playerHerbs > 0 && showItemDescriptionAndConfirm("herbal")) {
                    useHerbalExplore();
                }
                break;
            case "back":
                break;
            default:
                System.out.println("Invalid choice.");
                showBagExplore();
                break;
        }
    }

    public static Creature selectCreatureForUse(boolean includeFainted) {
        while (true) {
            System.out.println("--- CHOOSE TARGET CREATURE ---");
            for (int i = 0; i < GameStatus.playerTeam.size(); i++) {
                Creature creature = GameStatus.playerTeam.get(i);
                String faintStatus = (creature.hp <= 0 && !includeFainted) ? " (Fainted)" : "";
                System.out.printf("%d. %s (Lvl. %d) (HP: %.0f/%.0f)%s\n", (i + 1), creature.name, creature.level, creature.hp, creature.maxHp, faintStatus);
            }
            System.out.println("Back");

            String choice = TextAdventureGame.waitForInput().toLowerCase();
            if (choice.equals("back")) {
                return null;
            }

            try {
                int index = Integer.parseInt(choice);
                if (index > 0 && index <= GameStatus.playerTeam.size()) {
                    Creature target = GameStatus.playerTeam.get(index - 1);
                    if (target.hp <= 0 && !includeFainted) {
                        System.out.println("Cannot use item on a fainted creature.");
                        continue;
                    }

                    return target;
                } else {
                    System.out.println("Invalid creature number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
    }

    public static BattleMove selectMoveForHerbal(Creature creature) {
        while (true) {
            System.out.println("--- CHOOSE TARGET MOVE for " + creature.name + " ---");
            for (int i = 0; i < creature.moves.size(); i++) {
                BattleMove move = creature.moves.get(i);
                System.out.printf("%d. %s (PP: %d/%d)\n", (i + 1), move.name, move.pp, move.maxPp);
            }
            System.out.println("Back");

            String choice = TextAdventureGame.waitForInput().toLowerCase();
            if (choice.equals("back")) {
                return null;
            }

            try {
                int moveIndex = Integer.parseInt(choice) - 1;
                if (moveIndex >= 0 && moveIndex < creature.moves.size()) {
                    BattleMove targetMove = creature.moves.get(moveIndex);
                    if (targetMove.pp == targetMove.maxPp) {
                        System.out.println(targetMove.name + " already has full PP. Choose another move.");
                        continue;
                    }
                    return targetMove;
                } else {
                    System.out.println("Invalid move number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
    }

    public static void useHealingItemBattle(String item) {
        int healAmount = item.equals("mega_lunas") ? 100 : 30;

        if (item.equals("lunas") && GameStatus.playerPotions == 0 ||
                item.equals("mega_lunas") && GameStatus.playerMegaPotions == 0) {
            System.out.println("You have no " + item.replace("_", " ") + " left!");
            return;
        }

        if (GameStatus.playerActiveCreature.hp <= 0) {
            System.out.println("Cannot use healing item on a fainted creature in battle. Switch out instead!");
            return;
        }

        if (GameStatus.playerActiveCreature.hp == GameStatus.playerActiveCreature.maxHp) {
            System.out.println(GameStatus.playerActiveCreature.name + " is already at full HP. It was wasted!");
            return;
        }

        GameStatus.playerActiveCreature.hp = Math.min(GameStatus.playerActiveCreature.maxHp, GameStatus.playerActiveCreature.hp + healAmount);

        if (item.equals("lunas")) GameStatus.playerPotions--;
        else if (item.equals("mega_lunas")) GameStatus.playerMegaPotions--;

        System.out.println(GameStatus.playerActiveCreature.name + " used a **" + item.toUpperCase().replace("_", " ") + "**! It healed for " + healAmount + " HP. " + GameStatus.playerActiveCreature.name + "'s HP is now " + (int)GameStatus.playerActiveCreature.hp + "/" + (int)GameStatus.playerActiveCreature.maxHp + ".");
    }

    public static void useHealingItemExplore(String item) {
        int healAmount = item.equals("mega_lunas") ? 100 : 30;
        int reviveCost = 25;

        if (item.equals("lunas") && GameStatus.playerPotions == 0 ||
                item.equals("mega_lunas") && GameStatus.playerMegaPotions == 0) {
            System.out.println("You have no " + item.replace("_", " ") + " left!");
            return;
        }

        Creature target = selectCreatureForUse(true);
        if (target == null) return;

        if (target.hp == target.maxHp) {
            System.out.println(target.name + " is already at full HP.");
            return;
        }

        boolean itemConsumed = false;

        if (target.hp <= 0) {
            if (GameStatus.playerGold >= reviveCost) {
                GameStatus.playerGold -= reviveCost;
                target.hp = 1;
                System.out.println("You paid **" + reviveCost + " Gold** to revive " + target.name + ".");
                itemConsumed = true;
            } else {
                System.out.println("You don't have enough gold to revive " + target.name + "! Item not consumed.");
                return;
            }
        } else {
            itemConsumed = true;
        }

        if (itemConsumed) {
            if (item.equals("lunas")) GameStatus.playerPotions--;
            else if (item.equals("mega_lunas")) GameStatus.playerMegaPotions--;
        }

        target.hp = Math.min(target.maxHp, target.hp + healAmount);
        System.out.println(target.name + " used a **" + item.toUpperCase().replace("_", " ") + "**! It healed for " + healAmount + " HP. " + target.name + "'s HP is now " + (int)target.hp + "/" + (int)target.maxHp + ".");
    }

    public static void useHerbalBattle() {
        if (GameStatus.playerHerbs == 0) {
            System.out.println("You have no Herbal left!");
            return;
        }

        Creature targetCreature = selectCreatureForUse(false);
        if (targetCreature == null) return;

        BattleMove targetMove = selectMoveForHerbal(targetCreature);
        if (targetMove == null) return;

        int ppRestore = 5;
        targetMove.pp = Math.min(targetMove.maxPp, targetMove.pp + ppRestore);
        GameStatus.playerHerbs--;
        System.out.println(GameStatus.playerActiveCreature.name + " used an **Herbal**! It restored " + ppRestore + " PP to " + targetMove.name + " on " + targetCreature.name + ".");
    }

    public static void useHerbalExplore() {
        if (GameStatus.playerHerbs == 0) {
            System.out.println("You have no Herbal left!");
            return;
        }

        Creature targetCreature = selectCreatureForUse(false);
        if (targetCreature == null) return;

        BattleMove targetMove = selectMoveForHerbal(targetCreature);
        if (targetMove == null) return;

        int ppRestore = 5;
        targetMove.pp = Math.min(targetMove.maxPp, targetMove.pp + ppRestore);
        GameStatus.playerHerbs--;
        System.out.println(GameStatus.playerActiveCreature.name + " used an **Herbal**! It restored " + ppRestore + " PP to " + targetMove.name + " on " + targetCreature.name + ".");
    }

    public static void useScroll() {
        if (GameStatus.playerScrolls == 0) {
            System.out.println("You have no Scrolls left!");
            return;
        }

        GameStatus.playerScrolls--;
        System.out.println("You threw a **Scroll**! The ancient text glows as the creature is pulled into the parchment.");

        double baseCatchRate = 0.5;
        double healthMultiplier = (GameStatus.rivalCreature.maxHp - GameStatus.rivalCreature.hp) / GameStatus.rivalCreature.maxHp;
        double catchChance = baseCatchRate * (1 + healthMultiplier);

        if (TextAdventureGame.random.nextDouble() < catchChance) {

            if (GameStatus.playerTeam.size() >= GameStatus.MAX_TEAM_SIZE) {
                System.out.println("Success! The wild " + GameStatus.rivalCreature.name + " was captured into the scroll!");
                System.out.println("You have reached the maximum creature capacity of " + GameStatus.MAX_TEAM_SIZE + ".");
                System.out.println("Would you like to release a creature to make room for " + GameStatus.rivalCreature.name + "? (y/n)");
                String choice = TextAdventureGame.waitForInput().toLowerCase();

                if (choice.equals("y") || choice.equals("yes")) {
                    releaseCreature(false);
                    if (GameStatus.playerTeam.size() < GameStatus.MAX_TEAM_SIZE) {
                        finalCaptureLogic();
                    } else {
                        System.out.println("Capture failed. You chose not to release a creature.");
                    }
                } else {
                    System.out.println("Capture failed. You chose not to release a creature.");
                }
            } else {
                finalCaptureLogic();
            }

        } else {
            System.out.println("Oh no! The wild creature tore itself free from the scroll!");
        }
    }

    public static void finalCaptureLogic() {
        System.out.println("Success! The wild " + GameStatus.rivalCreature.name + " was captured into the scroll!");
        if (GameStatus.isSageModeActive) {
            int sageLevel = 50;
            GameStatus.rivalCreature.level = sageLevel;
            GameStatus.rivalCreature.maxHp = 75 + (sageLevel - 1) * 10;
            GameStatus.rivalCreature.hp = GameStatus.rivalCreature.maxHp;
            GameStatus.rivalCreature.xp = 0;
            GameStatus.rivalCreature.xpToNextLevel = 50 + (sageLevel - 1) * 10;
            System.out.println(GameStatus.rivalCreature.name + " was instantly leveled up to Level " + sageLevel + " by the power of the Sage Mode!");
        }
        GameStatus.playerTeam.add(GameStatus.rivalCreature);
        displayCapturedCreatureDetails(GameStatus.rivalCreature);
        GameStatus.rivalCapturedInBattle = true;
    }

    public static void displayCapturedCreatureDetails(Creature capturedCreature) {
        String border = "************************************************************************";
        System.out.println(border);
        System.out.printf(" %s\n", centerString(capturedCreature.name != null ? capturedCreature.name : "N/A", border.length() - 2));
        System.out.printf("* %-84s*\n", "Type: " + (capturedCreature.type != null ? capturedCreature.type : "N/A"));
        System.out.printf("* %-84s*\n", "Age: " + (capturedCreature.age != null ? capturedCreature.age : "N/A"));
        System.out.printf("* %-84s*\n", "Habitat: " + (capturedCreature.habitat != null ? capturedCreature.habitat : "N/A"));
        System.out.printf("* %-84s*\n", "Personality: " + (capturedCreature.personality != null ? capturedCreature.personality : "N/A"));
        if (capturedCreature.name.equals("Bakunawa")) {
            System.out.printf("* %-84s*\n", "Passive Skill - Lunar Fury: +10%% bonus damage during nighttime");
        }
        if (capturedCreature.name.equals("Aswang")) {
            System.out.printf("* %-84s*\n", "Passive Skill - Night Stalker: +10%% bonus speed and +5%% bonus attack during nighttime");
        }
        System.out.println(border);
    }

    public static String centerString(String s, int width) {
        if (s.length() >= width) {
            return s;
        }
        int leftPadding = (width - s.length()) / 2;
        int rightPadding = width - s.length() - leftPadding;
        return " ".repeat(leftPadding) + s + " ".repeat(rightPadding);
    }

    public static void releaseCreature(boolean fromExplore) {
        if (GameStatus.playerTeam.size() <= 1) {
            System.out.println("You cannot release your last creature!");
            return;
        }

        System.out.println("\n--- RELEASE A CREATURE ---");
        System.out.println("Select a creature to release:");
        for (int i = 0; i < GameStatus.playerTeam.size(); i++) {
            Creature creature = GameStatus.playerTeam.get(i);
            System.out.printf("%d. %s (Lvl. %d, Type: %s)\n", (i + 1), creature.name, creature.level, creature.type);
        }
        System.out.println("Back");

        String choice = TextAdventureGame.waitForInput().toLowerCase();
        if (choice.equals("back")) {
            System.out.println("You decided not to release any creature.");
            return;
        }

        try {
            int index = Integer.parseInt(choice) - 1;
            if (index >= 0 && index < GameStatus.playerTeam.size()) {
                Creature creatureToRelease = GameStatus.playerTeam.get(index);
                if (creatureToRelease.equals(GameStatus.playerActiveCreature)) {
                    System.out.println("You cannot release your currently active creature. Please switch first or choose another.");
                    return;
                }

                System.out.println("Are you sure you want to release " + creatureToRelease.name + "? (y/n)");
                String confirmation = TextAdventureGame.waitForInput().toLowerCase();

                if (confirmation.equals("y") || confirmation.equals("yes")) {
                    GameStatus.playerTeam.remove(index);
                    System.out.println(creatureToRelease.name + " was released back into the wild.");
                } else {
                    System.out.println("You decided not to release " + creatureToRelease.name + ".");
                }

            } else {
                System.out.println("Invalid number. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    public static void buyItem(String item, int unitCost, int quantity) {
        int totalCost = unitCost * quantity;
        if (GameStatus.playerGold >= totalCost) {
            GameStatus.playerGold -= totalCost;
            if (item.equals("lunas")) {
                GameStatus.playerPotions += quantity;
            } else if (item.equals("mega_lunas")) {
                GameStatus.playerMegaPotions += quantity;
            } else if (item.equals("scroll")) {
                GameStatus.playerScrolls += quantity;
            } else if (item.equals("herbal")) {
                GameStatus.playerHerbs += quantity;
            }
            System.out.println("You bought " + quantity + " " + item + "(s). Your gold is now " + GameStatus.playerGold + ".");
        } else {
            System.out.println("You don't have enough gold for " + quantity + " " + item + "(s). You need " + totalCost + " Gold.");
        }
    }

    public static void handleShopEvent(String playerLocation) {
        boolean inShop = true;
        String shopName = "";
        if (playerLocation.equals("shop")) {
            shopName = "Jesse's Shop";
        } else if (playerLocation.equals("shop_2")) {
            shopName = "Mary's Shop 2";
        } else if (playerLocation.equals("pekson_shop")) {
            shopName = "Pekson's Shop";
        }


        while (inShop) {
            System.out.println("--- " + shopName.toUpperCase() + " ---");
            System.out.println("Welcome! What can I get for you?");
            System.out.println("1. Lunas (25 Gold) (In Stock: " + GameStatus.playerPotions + ")");
            System.out.println("2. Scroll (30 Gold) (In Stock: " + GameStatus.playerScrolls + ")");
            System.out.println("3. Herbal (25 Gold) (In Stock: " + GameStatus.playerHerbs + ")");
            if (shopName.equals("Mary's Shop 2") || shopName.equals("Pekson's Shop")) {
                System.out.println("4. Mega Lunas (80 Gold) - Restores 100 HP! (In Stock: " + GameStatus.playerMegaPotions + ")");
            }
            System.out.println("5. Leave");
            System.out.println("You have " + GameStatus.playerGold + " Gold");

            String choice = TextAdventureGame.waitForInput().toLowerCase();
            String itemToBuy = null;
            int cost = 0;

            switch (choice) {
                case "1": case "lunas": itemToBuy = "lunas"; cost = 25; break;
                case "2": case "scroll": itemToBuy = "scroll"; cost = 30; break;
                case "3": case "herbal": itemToBuy = "herbal"; cost = 25; break;
                case "4":
                    if (shopName.equals("Mary's Shop 2") || shopName.equals("Pekson's Shop")) { itemToBuy = "mega_lunas"; cost = 80; break;}
                    else {inShop = false;}
                    break;
                case "5":
                    if (shopName.equals("Mary's Shop 2") || shopName.equals("Pekson's Shop")) { inShop = false; break;}
                case "leave": inShop = false; break;
                default: System.out.println("Invalid choice."); break;
            }

            if (itemToBuy != null) {
                System.out.println("How many " + itemToBuy + " would you like to buy?");
                int quantity = 0;
                try {
                    quantity = Integer.parseInt(TextAdventureGame.waitForInput().trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid quantity entered. Returning to shop menu.");
                    continue;
                }
                if (quantity > 0) {
                    buyItem(itemToBuy, cost, quantity);
                } else {
                    System.out.println("Must buy at least one item.");
                }
            }
        }
    }

    public static void healAllCreatures() {
        GameStatus.playerTeam.forEach(creature -> {
            creature.hp = creature.maxHp;
            for (BattleMove move : creature.moves) {
                move.pp = move.maxPp;
            }
        });
        System.out.println("All your creatures have been fully healed!");
    }
}