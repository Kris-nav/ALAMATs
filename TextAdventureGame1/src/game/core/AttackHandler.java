package game.core;

import java.util.*;
import java.util.stream.Collectors;

public class AttackHandler {

    public static boolean playerAttackChoice() {
        System.out.println("--- CHOOSE A MOVE ---");
        for (int i = 0; i < GameStatus.playerActiveCreature.moves.size(); i++) {
            Move move = GameStatus.playerActiveCreature.moves.get(i);
            int requiredLevel = GameStatus.playerActiveCreature.skillsByLevel.getOrDefault(move.name, Integer.MAX_VALUE);
            if (GameStatus.playerActiveCreature.level >= requiredLevel) {
                System.out.println((i + 1) + ". " + move.name + " (" + move.pp + "/" + move.maxPp + " PP)");
            }
        }
        System.out.println("Back");
        System.out.print("> ");

        String choice = TextAdventureGame.waitForInput().toLowerCase();
        if (choice.equals("back")) {
            return false;
        }

        try {
            int moveIndex = Integer.parseInt(choice) - 1;
            if (moveIndex >= 0 && moveIndex < GameStatus.playerActiveCreature.moves.size()) {
                Move chosenMove = GameStatus.playerActiveCreature.moves.get(moveIndex);
                int requiredLevel = GameStatus.playerActiveCreature.skillsByLevel.getOrDefault(chosenMove.name, Integer.MAX_VALUE);
                if (GameStatus.playerActiveCreature.level >= requiredLevel && chosenMove.pp > 0) {

                    GameStatus.playerActiveCreature.activeMove = chosenMove;

                    return playerAttack(chosenMove);
                } else {
                    System.out.println("Your creature is not a high enough level or out of PP to use that move!");
                    return false;
                }
            } else {
                System.out.println("Invalid move number.");
                return false;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return false;
        }
    }

    public static boolean playerAttack(Move move) {
        System.out.println("\n--- Player's Turn ---");

        GameStatus.rivalCreature.hasWindBarrierActive = false;

        if (GameStatus.rivalCreature.name.equals("Khaibalang") && GameStatus.rivalCreature.moves.stream().anyMatch(m -> m.name.equals("Say Stop!")) && TextAdventureGame.random.nextDouble() < 0.3) {
            System.out.println(GameData.allMoves.get("say_stop").flavorText);
            System.out.println("The creature freezes, confused. Its move fails to land.");
            GameStatus.rivalCreature.speedReductionPercentage = Math.min(1.0, GameStatus.rivalCreature.speedReductionPercentage + 0.20);
            return true;
        }

        move.pp--;

        move.effectObject.applyEffect(GameStatus.playerActiveCreature, GameStatus.rivalCreature);

        return false;
    }

    public static void rivalAttack() {

        if (GameStatus.rivalCreature.name.equals("Khaibalang") && GameStatus.rivalCreature.hp < GameStatus.rivalCreature.maxHp * 0.4) {
            if (GameStatus.rivalCreature.megaPotions > 0) {
                GameStatus.rivalCreature.megaPotions--;
                int healAmount = 100;
                GameStatus.rivalCreature.hp = Math.min(GameStatus.rivalCreature.maxHp, GameStatus.rivalCreature.hp + healAmount);
                System.out.printf("Khaibalang used a **Mega Lunas**! It healed for %d HP! (%.0f/%.0f)\n",
                        healAmount, GameStatus.rivalCreature.hp, GameStatus.rivalCreature.maxHp);
                return;
            } else if (GameStatus.rivalCreature.potions > 0 && GameStatus.rivalCreature.hp < GameStatus.rivalCreature.maxHp * 0.6) {
                GameStatus.rivalCreature.potions--;
                int healAmount = 30;
                GameStatus.rivalCreature.hp = Math.min(GameStatus.rivalCreature.maxHp, GameStatus.rivalCreature.hp + healAmount);
                System.out.printf("Khaibalang used a **Lunas**! It healed for %d HP! (%.0f/%.0f)\n",
                        healAmount, GameStatus.rivalCreature.hp, GameStatus.rivalCreature.maxHp);
                return;
            }
        }

        List<Move> eligibleMoves = GameStatus.rivalCreature.moves.stream()
                .filter(move -> GameStatus.rivalCreature.level >= GameStatus.rivalCreature.skillsByLevel.getOrDefault(move.name, Integer.MAX_VALUE) && move.pp > 0)
                .collect(Collectors.toList());

        if (eligibleMoves.isEmpty()) {
            System.out.println(GameStatus.rivalCreature.name + " has no eligible moves or is out of PP for all moves!");
            return;
        }

        Move rivalMove = eligibleMoves.get(TextAdventureGame.random.nextInt(eligibleMoves.size()));
        rivalMove.pp--;

        System.out.printf("%s used **%s**!\n", GameStatus.rivalCreature.name, rivalMove.name);

        GameStatus.playerActiveCreature.hasWindBarrierActive = false;

        GameStatus.rivalCreature.activeMove = rivalMove;

        rivalMove.effectObject.applyEffect(GameStatus.rivalCreature, GameStatus.playerActiveCreature);
    }

    public static void tryToRun(boolean isBossBattle) {
        if (isBossBattle) {
            System.out.println("You can't run from this fight!");
            return;
        }

        if (TextAdventureGame.random.nextDouble() < 0.5) {
            GameStatus.playerRanAway = true;
        } else {
            System.out.println("You couldn't escape!");
        }
    }
}