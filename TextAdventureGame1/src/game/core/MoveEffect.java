package game.core;

import java.util.*;

public abstract class MoveEffect {

    public abstract void applyEffect(Creature user, Creature target);

    protected boolean isHit(Move move, Creature user, Creature target) {
        if (target.hasUsedInvisibility) {
            return false;
        }

        int spdDifference = user.getCurrentSpd() - target.getCurrentSpd();
        double speedBonus = spdDifference * 0.1;
        double finalHitChance = move.hitChance / 100.0 + (speedBonus / 100.0);

        finalHitChance *= target.getCurrentAccuracy();

        finalHitChance = Math.max(0.0, Math.min(1.0, finalHitChance));

        return TextAdventureGame.random.nextDouble() < finalHitChance;
    }

    protected double calculateBaseDamage(Move move, Creature user, Creature target) {
        if (move.damage == 0) return 0.0;

        Map<String, Double> targetEffects = GameData.typeEffectiveness.getOrDefault(move.type, new HashMap<>());
        double typeMultiplier = targetEffects.getOrDefault(target.type, 1.0);
        double levelMultiplier = 1 + (user.level * 0.1);

        double damage = (TextAdventureGame.random.nextDouble() * move.damage * 0.5 + move.damage * 0.5) * typeMultiplier * levelMultiplier;

        damage *= target.getCurrentDefenseMultiplier();

        if (target.hasWindBarrierActive) {
            damage *= 0.6;
            System.out.println(target.name + "'s **Wind Barrier** absorbed some damage!");
        }

        if (user.name.equals("Bakunawa") && GameStatus.timeOfDay.equals("night")) {
            damage *= 1.1;
            System.out.println("Bakunawa's Lunar Fury is active! It deals 10% more damage!");
        }

        if (typeMultiplier > 1.0) {
            System.out.println("It's Super Effective!");
        } else if (typeMultiplier < 1.0) {
            System.out.println("It's Not Very Effective...");
        }

        return damage;
    }
}