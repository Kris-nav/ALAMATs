package game.core;

import game.battle.BattleMove;

public class HealEffect extends MoveEffect {
    private final double healPercentage;

    public HealEffect(double healPercentage) {
        this.healPercentage = healPercentage;
    }

    @Override
    public void applyEffect(Creature user, Creature target) {
        BattleMove move = user.getActiveMove();
        double healAmount = user.maxHp * healPercentage;

        user.hp = Math.min(user.maxHp, user.hp + healAmount);

        System.out.printf("%s used **%s**! It healed for %.0f HP (HP: %.0f/%.0f).\n",
                user.name, move.name, healAmount, user.hp, user.maxHp);
    }
}