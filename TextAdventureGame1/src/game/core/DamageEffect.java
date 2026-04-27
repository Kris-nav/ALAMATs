package game.core;

import game.battle.BattleMove;

public class DamageEffect extends MoveEffect {

    @Override
    public void applyEffect(Creature user, Creature target) {

        BattleMove move = user.getActiveMove();

        if (!isHit(move, user, target)) {
            System.out.printf("%s's attack **%s** missed!\n", user.name, move.name);
            return;
        }

        double damage = calculateBaseDamage(move, user, target);

        target.hp = Math.max(0, target.hp - damage);

        System.out.printf("%s used **%s**! %s took %.0f damage (HP: %.0f/%.0f).\n",
                user.name, move.name, target.name, damage, target.hp, target.maxHp);

        if (target.hp <= 0) {
            System.out.printf("%s fainted!\n", target.name);
        }
    }
}