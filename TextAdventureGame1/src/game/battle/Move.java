package game.battle;

import java.util.ArrayList;

public class Move {
    public String name;
    public Type type;
    public int damage;
    public ArrayList<Double> stat_changes;

    public Move(String name, Type type, int damage, ArrayList<Double> stat_changes) {
        this.name = name;
        this.type = type;
        this.damage = damage;
        this.stat_changes = stat_changes;
    }

    @Override
    public String toString() {
        return name;
    }
}