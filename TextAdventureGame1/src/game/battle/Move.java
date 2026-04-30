package game.battle;

import java.util.ArrayList;

public class Move {
    public String name;
    public Type   type;
    public int    damage;
    public ArrayList<Double> stat_changes;

    public int pp;
    public int maxPp;
    public int lockedUntilLevel; // ✅ 0 = available, >0 = locked until that level

    public Move(String name, Type type, int damage, ArrayList<Double> stat_changes) {
        this.name             = name;
        this.type             = type;
        this.damage           = damage;
        this.stat_changes     = stat_changes;
        this.maxPp            = 10;
        this.pp               = 10;
        this.lockedUntilLevel = 0;
    }

    public Move(String name, Type type, int damage,
                ArrayList<Double> stat_changes, int maxPp) {
        this(name, type, damage, stat_changes);
        this.maxPp = maxPp;
        this.pp    = maxPp;
    }

    public boolean isLocked() {
        return lockedUntilLevel > 0;
    }

    @Override
    public String toString() { return name; }
}