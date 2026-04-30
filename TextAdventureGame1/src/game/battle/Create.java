package game.battle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Create {

    private static final Random rand = new Random();

    // ── Types ─────────────────────────────────────────────────────
    public static final Type FIRE   = new Type("Fire",   0xE25822);
    public static final Type WATER  = new Type("Water",  0x4488FF);
    public static final Type GRASS  = new Type("Grass",  0x44BB44);
    public static final Type SHADOW = new Type("Shadow", 0x6644AA);
    public static final Type SPIRIT = new Type("Spirit", 0xAADDFF);
    public static final Type WIND   = new Type("Wind",   0xAAEECC);
    public static final Type EARTH  = new Type("Earth",  0xBB8833);
    public static final Type NORMAL = new Type("Normal", 0xAAAAAA);

    // ── Helpers ───────────────────────────────────────────────────

    private static Move mv(String name, Type type, int dmg, int pp) {
        return new Move(name, type, dmg, new ArrayList<>(), pp);
    }

    private static ArrayList<Stat> stats(double hp, double atk,
                                         double def, double spd, int level) {
        int extra = level - 5;
        ArrayList<Stat> s = new ArrayList<>();
        s.add(new Stat("HP",  hp  + extra * 10));
        s.add(new Stat("ATK", atk + extra * 4));
        s.add(new Stat("DEF", def + extra * 3));
        s.add(new Stat("SPD", spd + extra * 3));
        return s;
    }

    // ══════════════════════════════════════════════════════════════
    // STARTER — Santelmo (Fire, starts at level 5)
    //   Slot 0: Ember         unlock lvl 1
    //   Slot 1: Air Slash     unlock lvl 1
    //   Slot 2: Scary Face    unlock lvl 6
    //   Slot 3: Flamethrower  unlock lvl 10
    //   (Dragon Breath replaces Struggle at lvl 18 via 5th entry)
    // ══════════════════════════════════════════════════════════════

    public static Fighter createPlayerStarter() {
        return createSantelmo(5);
    }

    public static Fighter createSantelmo(int level) {
        ArrayList<Type>    types = new ArrayList<>(Arrays.asList(FIRE));
        ArrayList<Move>    all   = new ArrayList<>(Arrays.asList(
                mv("Ember",        FIRE,   40, 10),
                mv("Air Slash",    WIND,   50, 10),
                mv("Scary Face",   NORMAL,  0, 15),
                mv("Flamethrower", FIRE,   90,  8),
                mv("Dragon Breath",FIRE,  110,  6)
        ));
        ArrayList<Integer> lvls = new ArrayList<>(Arrays.asList(1, 1, 6, 10, 18));
        return new Fighter("Santelmo",
                "/images/Santelmo.png", "/images/Santelmo.png",
                types, stats(185, 95, 80, 90, level), all, lvls, level);
    }

    // ══════════════════════════════════════════════════════════════
    // WILD CREATURES — random level 3-7
    // ══════════════════════════════════════════════════════════════

    public static Fighter randomWildCreature() {
        int wildLevel = 3 + rand.nextInt(5); // 3,4,5,6,7
        switch (rand.nextInt(7)) {
            case 0: return createSigbin(wildLevel);
            case 1: return createKapre(wildLevel);
            case 2: return createEkek(wildLevel);
            case 3: return createBungisngis(wildLevel);
            case 4: return createAmongmongo(wildLevel);
            case 5: return createAmaninhig(wildLevel);
            default: return createAghoy(wildLevel);
        }
    }

    // ── Sigbin (Shadow) ───────────────────────────────────────────
    public static Fighter createSigbin(int level) {
        ArrayList<Move> all = new ArrayList<>(Arrays.asList(
                mv("Shadow Bite",   SHADOW, 45, 10),
                mv("Dark Pulse",    SHADOW, 60,  8),
                mv("Howl",          NORMAL,  0, 15),
                mv("Phantom Rush",  SHADOW, 80,  8)
        ));
        ArrayList<Integer> lvls = new ArrayList<>(Arrays.asList(1, 1, 5, 10));
        return new Fighter("Sigbin", "/images/Sigbin.png", "/images/Sigbin.png",
                new ArrayList<>(Arrays.asList(SHADOW)),
                stats(170, 88, 75, 110, level), all, lvls, level);
    }

    // ── Kapre (Grass + Earth) ─────────────────────────────────────
    public static Fighter createKapre(int level) {
        ArrayList<Move> all = new ArrayList<>(Arrays.asList(
                mv("Vine Whip",  GRASS, 40, 10),
                mv("Mud Slap",   EARTH, 50, 10),
                mv("Leaf Storm", GRASS, 80,  8),
                mv("Earthquake", EARTH, 95,  8)
        ));
        ArrayList<Integer> lvls = new ArrayList<>(Arrays.asList(1, 1, 6, 12));
        return new Fighter("Kapre", "/images/Kapre.png", "/images/Kapre.png",
                new ArrayList<>(Arrays.asList(GRASS, EARTH)),
                stats(202, 105, 100, 65, level), all, lvls, level);
    }

    // ── Ekek (Wind) ───────────────────────────────────────────────
    public static Fighter createEkek(int level) {
        ArrayList<Move> all = new ArrayList<>(Arrays.asList(
                mv("Gust",       WIND,  35, 12),
                mv("Wing Attack",WIND,  55, 10),
                mv("Aerial Ace", WIND,  70,  8),
                mv("Hurricane",  WIND, 100,  6)
        ));
        ArrayList<Integer> lvls = new ArrayList<>(Arrays.asList(1, 1, 5, 11));
        return new Fighter("Ekek", "/images/Ekek.png", "/images/Ekek.png",
                new ArrayList<>(Arrays.asList(WIND)),
                stats(155, 85, 70, 120, level), all, lvls, level);
    }

    // ── Bungisngis (Earth) ────────────────────────────────────────
    public static Fighter createBungisngis(int level) {
        ArrayList<Move> all = new ArrayList<>(Arrays.asList(
                mv("Tackle",     NORMAL, 35, 12),
                mv("Rock Throw", EARTH,  50, 10),
                mv("Stone Edge", EARTH,  80,  8),
                mv("Rock Smash", EARTH,  95,  6)
        ));
        ArrayList<Integer> lvls = new ArrayList<>(Arrays.asList(1, 1, 6, 10));
        return new Fighter("Bungisngis", "/images/Bungisngis.png", "/images/Bungisngis.png",
                new ArrayList<>(Arrays.asList(EARTH)),
                stats(210, 100, 110, 55, level), all, lvls, level);
    }

    // ── Amongmongo (Earth + Normal) ───────────────────────────────
    public static Fighter createAmongmongo(int level) {
        ArrayList<Move> all = new ArrayList<>(Arrays.asList(
                mv("Scratch", NORMAL, 30, 12),
                mv("Dig",     EARTH,  60, 10),
                mv("Slash",   NORMAL, 70,  8),
                mv("Fissure", EARTH,  90,  6)
        ));
        ArrayList<Integer> lvls = new ArrayList<>(Arrays.asList(1, 1, 5, 11));
        return new Fighter("Amongmongo", "/images/Amongmongo.png", "/images/Amongmongo.png",
                new ArrayList<>(Arrays.asList(EARTH, NORMAL)),
                stats(195, 90, 95, 80, level), all, lvls, level);
    }

    // ── Amaninhig (Shadow + Spirit) ───────────────────────────────
    public static Fighter createAmaninhig(int level) {
        ArrayList<Move> all = new ArrayList<>(Arrays.asList(
                mv("Shadow Claw",  SHADOW, 45, 10),
                mv("Spirit Pulse", SPIRIT, 55, 10),
                mv("Night Slash",  SHADOW, 75,  8),
                mv("Soul Drain",   SPIRIT, 90,  6)
        ));
        ArrayList<Integer> lvls = new ArrayList<>(Arrays.asList(1, 1, 6, 12));
        return new Fighter("Amaninhig", "/images/Amaranhig.png", "/images/Amaranhig.png",
                new ArrayList<>(Arrays.asList(SHADOW, SPIRIT)),
                stats(165, 95, 70, 100, level), all, lvls, level);
    }

    // ── Aghoy (Water + Spirit) ────────────────────────────────────
    public static Fighter createAghoy(int level) {
        ArrayList<Move> all = new ArrayList<>(Arrays.asList(
                mv("Water Gun",  WATER,  40, 12),
                mv("Mist",       SPIRIT,  0, 15),
                mv("Aqua Jet",   WATER,  65, 10),
                mv("Hydro Pump", WATER,  95,  6)
        ));
        ArrayList<Integer> lvls = new ArrayList<>(Arrays.asList(1, 1, 5, 11));
        return new Fighter("Aghoy", "/images/Aghoy.png", "/images/Aghoy.png",
                new ArrayList<>(Arrays.asList(WATER, SPIRIT)),
                stats(175, 80, 85, 95, level), all, lvls, level);
    }
}