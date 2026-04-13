package game.core;

import java.util.*;

public class Creature extends MythicalCreature {

    public Move activeMove;

    public double speedReductionPercentage = 0.0;
    public double accuracyReductionPercentage = 0.0;
    public double defenseReductionPercentage = 0.0;

    public int baseSpd;
    private double baseAccuracy = 1.0;

    public boolean hasShapeshifted = false;
    public boolean hasUsedInvisibility = false;
    public boolean hasWindBarrierActive = false;
    public boolean isUnderBurnEffect = false;
    public boolean isStunned = false;
    public boolean isFocusLocked = false;

    public int potions;
    public int scrolls;
    public int herbs;
    public int megaPotions;

    public List<Move> moves;
    public Map<String, Integer> skillsByLevel;

    public Creature(String name, String type, int level, List<Move> moves,
                    Map<String, Integer> skillsByLevel, int spd, String age, String habitat, String personality, int hp, int attack, int defense, int potions, int scrolls, int herbs) {

        super(name, type, level, hp, attack, defense, age, habitat, personality);

        this.potions = potions;
        this.scrolls = scrolls;
        this.herbs = herbs;
        this.megaPotions = 0;
        this.moves = new ArrayList<>();
        for (Move move : moves) {
            this.moves.add(new Move(move.name, move.damage, move.type, move.hitChance, move.maxPp, move.effectObject, move.flavorText));
        }
        this.skillsByLevel = skillsByLevel;
        this.baseSpd = spd;
    }

    public Creature(Creature other) {
        super(other.name, other.type, other.level, other.maxHp, other.attack, other.defense, other.age, other.habitat, other.personality);
        this.hp = other.hp;
        this.xp = other.xp;
        this.xpToNextLevel = other.xpToNextLevel;

        this.potions = other.potions;
        this.scrolls = other.scrolls;
        this.herbs = other.herbs;
        this.megaPotions = other.megaPotions;

        this.moves = new ArrayList<>();
        for (Move move : other.moves) {
            this.moves.add(new Move(move.name, move.damage, move.type, move.hitChance, move.maxPp, move.effectObject, move.flavorText));
        }
        this.skillsByLevel = other.skillsByLevel;
        this.baseSpd = other.baseSpd;

        this.resetBattleStats();
    }

    @Override
    public void applyPassiveBuffs() {
        if (this.name.equals("Aswang") && GameStatus.timeOfDay.equals("night")) {
            this.speedReductionPercentage = Math.max(-0.10, this.speedReductionPercentage - 0.10);
            System.out.println("Aswang's Night Stalker is active! It's faster and stronger!");
        }
    }

    public Move getActiveMove() {
        return this.activeMove;
    }

    public void resetBattleStats() {
        this.speedReductionPercentage = 0.0;
        this.accuracyReductionPercentage = 0.0;
        this.defenseReductionPercentage = 0.0;
        this.hasShapeshifted = false;
        this.hasUsedInvisibility = false;
        this.hasWindBarrierActive = false;
        this.isUnderBurnEffect = false;
        this.isStunned = false;
        this.isFocusLocked = false;
    }

    public int getCurrentSpd() {
        return (int) (baseSpd * (1.0 - speedReductionPercentage));
    }

    public double getCurrentAccuracy() {
        return baseAccuracy * (1.0 - accuracyReductionPercentage);
    }

    public double getCurrentDefenseMultiplier() {
        return 1.0 - defenseReductionPercentage;
    }
}