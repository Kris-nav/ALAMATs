package game.core;

public class Move {
    public String name;
    public int damage;
    public String type;
    public int hitChance;
    public int maxPp;
    public int pp;

    public MoveEffect effectObject;

    public String flavorText;

    public Move(String name, int damage, String type, int hitChance, int maxPp, MoveEffect effectObject, String flavorText) {
        this.name = name;
        this.damage = damage;
        this.type = type;
        this.hitChance = hitChance;
        this.maxPp = maxPp;
        this.pp = maxPp;
        this.effectObject = effectObject;
        this.flavorText = flavorText;
    }

    public String toString() {
        return name;
    }
}