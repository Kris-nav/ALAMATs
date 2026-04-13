package game.core;

public class StatusEffect extends MoveEffect {
    private final String statusType;
    private final double chance;

    public StatusEffect(String statusType, double chance) {
        this.statusType = statusType;
        this.chance = chance;
    }

    @Override
    public void applyEffect(Creature user, Creature target) {
        Move move = user.getActiveMove();

        if (this.statusType.startsWith("lower_acc")) {
            target.accuracyReductionPercentage = Math.min(1.0, target.accuracyReductionPercentage + this.chance);
            System.out.printf("%s used **%s** and reduced %s's accuracy by %.0f%%!\n", user.name, move.name, target.name, this.chance * 100);
            return;
        }

        if (TextAdventureGame.random.nextDouble() < this.chance) {
            if (this.statusType.equals("stun")) {
                target.isStunned = true;
                System.out.printf("%s used **%s**! %s is **Stunned** for 1 turn!\n", user.name, move.name, target.name);
            } else if (this.statusType.equals("burn")) {
                target.isUnderBurnEffect = true;
                System.out.printf("%s used **%s** and **Burned** %s!\n", user.name, move.name, target.name);
            }
        } else {
            if (!this.statusType.startsWith("lower")) {
                System.out.printf("%s resisted the status effect from **%s**!\n", target.name, move.name);
            }
        }
    }
}