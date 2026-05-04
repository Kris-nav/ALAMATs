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

    // ══════════════════════════════════════════════════════════════
    // TOWN 3 CREATURES
    // ══════════════════════════════════════════════════════════════

    // ── Busaw (Ground) ────────────────────────────────────────────
    public static Fighter createBusaw(int level) {
        Type GROUND = new Type("Ground", 0xb85807);
        Type ROCK   = new Type("Rock",   0x9b7b60);

        ArrayList<Move> all = new ArrayList<>(Arrays.asList(
                mv("Bulldoze",     GROUND, 60,  12),
                mv("Rollout",      ROCK,   30,  12),
                mv("Earthquake",   GROUND, 100,  8),
                mv("Defense Curl", NORMAL,  0,  10)
        ));
        ArrayList<Integer> lvls = new ArrayList<>(Arrays.asList(1, 1, 6, 1));
        ArrayList<Stat> stats = new ArrayList<>();
        int extra = level - 5;
        stats.add(new Stat("HP",  197 + extra * 10));
        stats.add(new Stat("ATK", 189 + extra * 4));
        stats.add(new Stat("DEF", 189 + extra * 3));
        stats.add(new Stat("SPD", 112 + extra * 3));
        return new Fighter("Busaw",
                "/images/Busaw.png", "/images/Busaw.png",
                new ArrayList<>(Arrays.asList(GROUND)), stats, all, lvls, level);
    }

    // ── Kaperosa (Ghost + Poison) ─────────────────────────────────
    public static Fighter createKaperosa(int level) {
        Type GHOST  = new Type("Ghost",  0x480ec1);
        Type POISON = new Type("Poison", 0x8a1be2);
        Type DARK   = new Type("Dark",   0x3c3a3a);

        ArrayList<Move> all = new ArrayList<>(Arrays.asList(
                mv("Shadow Punch", GHOST,  60, 12),
                mv("Shadow Ball",  GHOST,  80, 10),
                mv("Payback",      DARK,   50, 10),
                mv("Poison Jab",   POISON, 80,  8)
        ));
        ArrayList<Integer> lvls = new ArrayList<>(Arrays.asList(1, 1, 5, 10));
        ArrayList<Stat> stats = new ArrayList<>();
        int extra = level - 5;
        stats.add(new Stat("HP",  167 + extra * 10));
        stats.add(new Stat("ATK", 244 + extra * 4));
        stats.add(new Stat("DEF", 161 + extra * 3));
        stats.add(new Stat("SPD", 200 + extra * 3));
        return new Fighter("Kaperosa",
                "/images/Kaperosa.png", "/images/Kaperosa.png",
                new ArrayList<>(Arrays.asList(GHOST, POISON)), stats, all, lvls, level);
    }

    // ── Kolyog (Grass + Psychic) ──────────────────────────────────
    public static Fighter createKolyog(int level) {
        Type GRASS   = new Type("Grass",   0x55cb0b);
        Type PSYCHIC = new Type("Psychic", 0xc612ce);

        ArrayList<Move> all = new ArrayList<>(Arrays.asList(
                mv("Seed Bomb", GRASS,   80, 10),
                mv("Confusion", PSYCHIC, 50, 12),
                mv("Psyshock",  PSYCHIC, 80,  8),
                mv("Synthesis", GRASS,    0, 10)
        ));
        ArrayList<Integer> lvls = new ArrayList<>(Arrays.asList(1, 1, 6, 1));
        ArrayList<Stat> stats = new ArrayList<>();
        int extra = level - 5;
        stats.add(new Stat("HP",  202 + extra * 10));
        stats.add(new Stat("ATK", 194 + extra * 4));
        stats.add(new Stat("DEF", 150 + extra * 3));
        stats.add(new Stat("SPD", 117 + extra * 3));
        return new Fighter("Kolyog",
                "/images/Kolyog.png", "/images/Kolyog.png",
                new ArrayList<>(Arrays.asList(GRASS, PSYCHIC)), stats, all, lvls, level);
    }

    // ── Manananggal (Fire + Dark) ─────────────────────────────────
    public static Fighter createManananggal(int level) {
        Type FIRE_T  = new Type("Fire",   0xf36d0b);
        Type DARK    = new Type("Dark",   0x3c3a3a);
        Type POISON  = new Type("Poison", 0x8a1be2);

        ArrayList<Move> all = new ArrayList<>(Arrays.asList(
                mv("Bite",       DARK,   60,  12),
                mv("Smog",       POISON, 30,  12),
                mv("Inferno",    FIRE_T, 100,  8),
                mv("Nasty Plot", DARK,    0,  10)
        ));
        ArrayList<Integer> lvls = new ArrayList<>(Arrays.asList(1, 1, 6, 1));
        ArrayList<Stat> stats = new ArrayList<>();
        int extra = level - 5;
        stats.add(new Stat("HP",  182 + extra * 10));
        stats.add(new Stat("ATK", 178 + extra * 4));
        stats.add(new Stat("DEF", 145 + extra * 3));
        stats.add(new Stat("SPD", 161 + extra * 3));
        return new Fighter("Manananggal",
                "/images/Manananggal.png", "/images/Manananggal.png",
                new ArrayList<>(Arrays.asList(FIRE_T, DARK)), stats, all, lvls, level);
    }

    // ── Tikbalang (Poison + Ground) ───────────────────────────────
    public static Fighter createTikbalang(int level) {
        Type POISON = new Type("Poison", 0x8a1be2);
        Type GROUND = new Type("Ground", 0xb85807);
        Type BUG    = new Type("Bug",    0x68a300);

        ArrayList<Move> all = new ArrayList<>(Arrays.asList(
                mv("Megahorn",    BUG,    120,  8),
                mv("Earth Power", GROUND,  90, 10),
                mv("Sludge Wave", POISON,  95,  8),
                mv("Leer",        NORMAL,   0, 12)
        ));
        ArrayList<Integer> lvls = new ArrayList<>(Arrays.asList(1, 1, 6, 1));
        ArrayList<Stat> stats = new ArrayList<>();
        int extra = level - 5;
        stats.add(new Stat("HP",  188 + extra * 10));
        stats.add(new Stat("ATK", 158 + extra * 4));
        stats.add(new Stat("DEF", 141 + extra * 3));
        stats.add(new Stat("SPD", 150 + extra * 3));
        return new Fighter("Tikbalang",
                "/images/Tikbalang.png", "/images/Tikbalang.png",
                new ArrayList<>(Arrays.asList(POISON, GROUND)), stats, all, lvls, level);
    }

    // ── Town 3 random wild encounter ──────────────────────────────
    public static Fighter randomTown3Creature() {
        int level = 15 + rand.nextInt(10); // 15–24
        switch (rand.nextInt(5)) {
            case 0: return createBusaw(level);
            case 1: return createKaperosa(level);
            case 2: return createKolyog(level);
            case 3: return createManananggal(level);
            default: return createTikbalang(level);
        }
    }
}