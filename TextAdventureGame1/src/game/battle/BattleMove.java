package game.battle;

import game.core.DamageEffect;
import game.core.MoveEffect;

import java.util.ArrayList;

public class BattleMove {
    public String name;
    public Type type;
    public int damage;
    public ArrayList<Double> stat_changes;
    public double hitChance;
    public int pp;
    public int maxPp;
    public String flavorText;
    public DamageEffect effectObject;

    public BattleMove(String name, Type type, int damage, ArrayList<Double> stat_changes) {
        this.name = name;
        this.type = type;
        this.damage = damage;
        this.stat_changes = stat_changes;
    }

    public BattleMove(String tailWhip, int i, String water, int i1, int i2, MoveEffect acc_down_10_percent, Object o) {
        
    }
}