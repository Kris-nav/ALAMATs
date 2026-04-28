package game.battle;

import java.util.ArrayList;

public class Move {
    public String name;
    public Type type;
    public int damage;
    public ArrayList<Double> stat_changes;

    // ✅ PP tracking
    public int pp;
    public int maxPp;

    public Move(String name, Type type, int damage, ArrayList<Double> stat_changes) {
        this.name = name;
        this.type = type;
        this.damage = damage;
        this.stat_changes = stat_changes;
        this.maxPp = 10; // ✅ default max PP
        this.pp    = 10; // ✅ starts full
    }

    public Move(String name, Type type, int damage,
                ArrayList<Double> stat_changes, int maxPp) {
        this(name, type, damage, stat_changes);
        this.maxPp = maxPp;
        this.pp    = maxPp;
    }

    @Override
    public String toString() {
        return name;
    }
}