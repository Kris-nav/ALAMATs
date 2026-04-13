package game.core;

import java.util.*;

public abstract class MythicalCreature {
    public String name;
    public String type;
    public double hp;
    public double maxHp;
    public int attack;
    public int defense;
    public int level;
    public int xp;
    public int xpToNextLevel;

    public String age;
    public String habitat;
    public String personality;

    public MythicalCreature(String name, String type, int level, double maxHp, int attack, int defense, String age, String habitat, String personality) {
        this.name = name;
        this.type = type;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.level = level;
        this.xp = 0;
        this.xpToNextLevel = 50 + (level - 1) * 10;
        this.age = age;
        this.habitat = habitat;
        this.personality = personality;
    }

    public abstract void applyPassiveBuffs();
}