package game.core;

import game.battle.BattleMove;
import game.battle.Type;

import java.util.ArrayList;

public class Move extends BattleMove {
    private static final MoveEffect acc_down_10_percent = null ;
    public String name;
    public Type type;
    public int damage;
    public ArrayList<Double> stat_changes;
    public ArrayList<Double> hitChance;
    public Object maxPp;
    public Object effectObject;
    public Object flavorText;

    public Move(String name, Type type, int damage, ArrayList<Double> stat_changes) {
        super("Tail Whip", 20, "water", 100, 15, acc_down_10_percent, null);
        this.name = name;
        this.type = type;
        this.damage = damage;
        this.stat_changes = stat_changes;
    }

    public Move(String name, int damage, Type type, ArrayList<Double> hitChance, Object maxPp, Object effectObject, Object flavorText) {
        super("Tail Whip", 20, "water", 100, 15, acc_down_10_percent, null);
    }
}