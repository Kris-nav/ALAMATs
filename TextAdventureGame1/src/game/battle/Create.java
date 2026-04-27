package game.battle;

import java.util.ArrayList;
import java.util.Random;


public class Create {
    static Type normal   = new Type("Normal",   0xa89e9e);
    static Type electric = new Type("Electric", 0xF7C600);
    static Type dark     = new Type("Dark",     0x3c3a3a);
    static Type fire     = new Type("Fire",     0xf36d0b);
    static Type dragon   = new Type("Dragon",   0x4a0bf3);
    static Type water    = new Type("Water",    0x329dfc);
    static Type steel    = new Type("Steel",    0xa6b7c6);
    static Type grass    = new Type("Grass",    0x55cb0b);
    static Type fighting = new Type("Fighting", 0xba1010);
    static Type psychic  = new Type("Psychic",  0xc612ce);
    static Type fairy    = new Type("Fairy",    0xfaaff3);
    static Type rock     = new Type("Rock",     0x9b7b60);
    static Type ground   = new Type("Ground",   0xb85807);
    static Type bug      = new Type("Bug",      0x68a300);
    static Type ghost    = new Type("Ghost",    0x480ec1);
    static Type poison   = new Type("Poison",   0x8a1be2);
    static Type ice      = new Type("Ice",      0x20eaf7);
    static Type flying   = new Type("Flying",   0xa6d7f7);

    private static ArrayList<Double> z() {
        ArrayList<Double> a = new ArrayList<>();
        while (a.size() < 4) a.add(0.0);
        return a;
    }

    private static ArrayList<Stat> stats(int h, int a, int d, int s) {
        ArrayList<Stat> list = new ArrayList<>();
        list.add(new Stat("Health",  h));
        list.add(new Stat("Attack",  a));
        list.add(new Stat("Defense", d));
        list.add(new Stat("Speed",   s));
        return list;
    }

    public static Fighter randomWildCreature() {
        int r = new Random().nextInt(18);
        switch (r) {
            case 0:  return createBungisngis();
            case 1:  return createAghoy();
            case 2:  return createSantelmo();
            case 3:  return createSirena();
            case 4:  return createAmomongo();
            case 5:  return createExeggutor();
            case 6:  return createEkek();
            case 7:  return createAmaranhig();
            case 8:  return createDonphan();
            case 9:  return createSnorlax();
            case 10: return createGengar();
            case 11: return createTyranitar();
            case 12: return createScizor();
            case 13: return createClefable();
            case 14: return createHoundoom();
            case 15: return createLapras();
            case 16: return createNidoking();
            default: return createArcanine();
        }
    }

    public static Fighter createBungisngis() {
        ArrayList<Type> types = new ArrayList<>(); types.add(electric);
        ArrayList<Type> weak  = new ArrayList<>(); weak.add(ground);
        ArrayList<Type> res   = new ArrayList<>(); res.add(flying); res.add(electric); res.add(steel);
        ArrayList<Type> imm   = new ArrayList<>();
        Fighter f = new Fighter("Bungisngis", types, stats(172,178,123,200), weak, res, imm,
                "/images/Bungisngis.png", "/images/Bungisngis.png");
        f.addMove(new Move("Tackle",       normal,   40, z()));
        f.addMove(new Move("Thunder Shock",electric, 40, z()));
        f.addMove(new Move("Discharge",    electric, 80, z()));
        ArrayList<Double> a = z(); a.set(2,-0.33);
        f.addMove(new Move("Tail Whip",    normal,    0, a));
        return f;
    }

    public static Fighter createAghoy() {
        ArrayList<Type> types = new ArrayList<>(); types.add(grass);
        ArrayList<Type> weak  = new ArrayList<>(); weak.add(fire); weak.add(ice); weak.add(flying); weak.add(poison); weak.add(bug);
        ArrayList<Type> res   = new ArrayList<>(); res.add(ground); res.add(water); res.add(grass); res.add(electric);
        ArrayList<Type> imm   = new ArrayList<>();
        Fighter f = new Fighter("Aghoy", types, stats(187,147,167,167), weak, res, imm,
                "/images/Aghoy.png", "/images/Aghoy.png");
        f.addMove(new Move("Razor Leaf",   grass,  55, z()));
        f.addMove(new Move("Magical Leaf", grass,  60, z()));
        f.addMove(new Move("Body Slam",    normal, 85, z()));
        ArrayList<Double> a = z(); a.set(1,-0.33);
        f.addMove(new Move("Growl",        normal,  0, a));
        return f;
    }

    public static Fighter createSantelmo() {
        ArrayList<Type> types = new ArrayList<>(); types.add(fire); types.add(flying);
        ArrayList<Type> weak  = new ArrayList<>(); weak.add(rock); weak.add(electric); weak.add(water);
        ArrayList<Type> res   = new ArrayList<>(); res.add(fighting); res.add(bug); res.add(steel); res.add(fire); res.add(grass); res.add(fairy);
        ArrayList<Type> imm   = new ArrayList<>(); imm.add(ground);
        Fighter f = new Fighter("Santelmo", types, stats(185,149,143,167), weak, res, imm,
                "/images/Santelmo.png", "/images/Santelmo.png");
        f.addMove(new Move("Flamethrower", fire,   90, z()));
        f.addMove(new Move("Air Slash",    flying, 75, z()));
        f.addMove(new Move("Dragon Breath",dragon, 60, z()));
        ArrayList<Double> a = z(); a.set(3,-0.33);
        f.addMove(new Move("Scary Face",   normal,  0, a));
        return f;
    }

    public static Fighter createSirena() {
        ArrayList<Type> types = new ArrayList<>(); types.add(water); types.add(flying);
        ArrayList<Type> weak  = new ArrayList<>(); weak.add(rock); weak.add(electric);
        ArrayList<Type> res   = new ArrayList<>(); res.add(fighting); res.add(bug); res.add(steel); res.add(fire); res.add(water);
        ArrayList<Type> imm   = new ArrayList<>(); imm.add(ground);
        Fighter f = new Fighter("Sirena", types, stats(202,194,144,146), weak, res, imm,
                "/images/Sirena.png", "/images/Sirena.png");
        f.addMove(new Move("Aqua Tail",    water, 90, z()));
        f.addMove(new Move("Ice Fang",     ice,   65, z()));
        f.addMove(new Move("Crunch",       dark,  80, z()));
        ArrayList<Double> a = z(); a.set(1,0.33);
        f.addMove(new Move("Dragon Dance", dragon, 0, a));
        return f;
    }

    public static Fighter createAmomongo() {
        ArrayList<Type> types = new ArrayList<>(); types.add(fighting);
        ArrayList<Type> weak  = new ArrayList<>(); weak.add(psychic); weak.add(flying); weak.add(fairy);
        ArrayList<Type> res   = new ArrayList<>(); res.add(rock); res.add(bug); res.add(dark);
        ArrayList<Type> imm   = new ArrayList<>();
        Fighter f = new Fighter("Amomongo", types, stats(197,200,145,117), weak, res, imm,
                "/images/Amomongo.png", "/images/Amomongo.png");
        f.addMove(new Move("Cross Chop", fighting, 100, z()));
        f.addMove(new Move("Knock Off",  dark,      65, z()));
        f.addMove(new Move("Strength",   normal,    80, z()));
        ArrayList<Double> a = z(); a.set(2,0.33);
        f.addMove(new Move("Bulk Up",    fighting,   0, a));
        return f;
    }

    public static Fighter createExeggutor() {
        ArrayList<Type> types = new ArrayList<>(); types.add(grass); types.add(psychic);
        ArrayList<Type> weak  = new ArrayList<>(); weak.add(ghost); weak.add(dark); weak.add(bug); weak.add(poison); weak.add(fire); weak.add(flying); weak.add(ice);
        ArrayList<Type> res   = new ArrayList<>(); res.add(fighting); res.add(electric); res.add(water); res.add(ground); res.add(grass); res.add(psychic);
        ArrayList<Type> imm   = new ArrayList<>();
        Fighter f = new Fighter("Exeggutor", types, stats(202,194,150,117), weak, res, imm,
                "/images/exeggutor.png", "/images/exeggutor_back.png");
        f.addMove(new Move("Seed Bomb", grass,   80, z()));
        f.addMove(new Move("Confusion", psychic, 50, z()));
        f.addMove(new Move("Psyshock",  psychic, 80, z()));
        ArrayList<Double> a = z(); a.set(0,0.25);
        f.addMove(new Move("Synthesis", grass,    0, a));
        return f;
    }

    public static Fighter createEkek() {
        ArrayList<Type> types = new ArrayList<>(); types.add(dragon); types.add(flying);
        ArrayList<Type> weak  = new ArrayList<>(); weak.add(rock); weak.add(ice); weak.add(dragon); weak.add(fairy);
        ArrayList<Type> res   = new ArrayList<>(); res.add(fighting); res.add(bug); res.add(water); res.add(fire); res.add(grass);
        ArrayList<Type> imm   = new ArrayList<>(); imm.add(ground);
        Fighter f = new Fighter("Ekek", types, stats(198,204,167,145), weak, res, imm,
                "/images/Ekek.png", "/images/Ekek.png");
        f.addMove(new Move("Wing Attack", flying,  60, z()));
        f.addMove(new Move("Fire Punch",  fire,    75, z()));
        f.addMove(new Move("Slam",        normal,  80, z()));
        f.addMove(new Move("Dragon Rush", dragon, 100, z()));
        return f;
    }

    public static Fighter createAmaranhig() {
        ArrayList<Type> types = new ArrayList<>(); types.add(ground); types.add(steel);
        ArrayList<Type> weak  = new ArrayList<>(); weak.add(fighting); weak.add(ground); weak.add(fire); weak.add(water);
        ArrayList<Type> res   = new ArrayList<>(); res.add(normal); res.add(flying); res.add(rock); res.add(bug); res.add(steel); res.add(dragon); res.add(fairy); res.add(psychic);
        ArrayList<Type> imm   = new ArrayList<>(); imm.add(electric); imm.add(poison);
        Fighter f = new Fighter("Amaranhig", types, stats(182,150,277,90), weak, res, imm,
                "/images/Amaranhig.png", "/images/Amaranhig.png");
        f.addMove(new Move("Thunder Fang", electric, 80, z()));
        f.addMove(new Move("Rock Slide",   rock,     75, z()));
        f.addMove(new Move("Iron Tail",    steel,   100, z()));
        ArrayList<Double> a = z(); a.set(3,0.66);
        f.addMove(new Move("Rock Polish",  rock,      0, a));
        return f;
    }

    public static Fighter createDonphan() {
        ArrayList<Type> types = new ArrayList<>(); types.add(ground);
        ArrayList<Type> weak  = new ArrayList<>(); weak.add(grass); weak.add(ice); weak.add(water);
        ArrayList<Type> res   = new ArrayList<>(); res.add(poison); res.add(rock);
        ArrayList<Type> imm   = new ArrayList<>(); imm.add(electric);
        Fighter f = new Fighter("Donphan", types, stats(197,189,189,112), weak, res, imm,
                "/images/donphan.png", "/images/donphan_back.png");
        f.addMove(new Move("Bulldoze",    ground,  60, z()));
        f.addMove(new Move("Rollout",     rock,    30, z()));
        f.addMove(new Move("Earthquake",  ground, 100, z()));
        ArrayList<Double> a = z(); a.set(2,0.33);
        f.addMove(new Move("Defense Curl",normal,   0, a));
        return f;
    }

    public static Fighter createSnorlax() {
        ArrayList<Type> types = new ArrayList<>(); types.add(normal);
        ArrayList<Type> weak  = new ArrayList<>(); weak.add(fighting);
        ArrayList<Type> res   = new ArrayList<>();
        ArrayList<Type> imm   = new ArrayList<>();
        Fighter f = new Fighter("Snorlax", types, stats(267,178,178,90), weak, res, imm,
                "/images/snorlax.png", "/images/snorlax_back.png");
        f.addMove(new Move("Giga Impact", normal,   150, z()));
        f.addMove(new Move("Lick",        ghost,     30, z()));
        f.addMove(new Move("Hammer Arm",  fighting, 100, z()));
        ArrayList<Double> a = z(); a.set(1,0.33);
        f.addMove(new Move("Amnesia",     psychic,    0, a));
        return f;
    }

    public static Fighter createGengar() {
        ArrayList<Type> types = new ArrayList<>(); types.add(ghost); types.add(poison);
        ArrayList<Type> weak  = new ArrayList<>(); weak.add(ground); weak.add(ghost); weak.add(psychic); weak.add(dark);
        ArrayList<Type> res   = new ArrayList<>();
        ArrayList<Type> imm   = new ArrayList<>(); imm.add(normal); imm.add(fighting);
        Fighter f = new Fighter("Gengar", types, stats(167,244,161,200), weak, res, imm,
                "/images/gengar.png", "/images/gengar_back.png");
        f.addMove(new Move("Shadow Punch", ghost,  60, z()));
        f.addMove(new Move("Shadow Ball",  ghost,  80, z()));
        f.addMove(new Move("Payback",      dark,   50, z()));
        f.addMove(new Move("Poison Jab",   poison, 80, z()));
        return f;
    }

    public static Fighter createTyranitar() {
        ArrayList<Type> types = new ArrayList<>(); types.add(rock); types.add(dark);
        ArrayList<Type> weak  = new ArrayList<>(); weak.add(fighting); weak.add(ground); weak.add(bug); weak.add(steel); weak.add(fairy); weak.add(water); weak.add(grass);
        ArrayList<Type> res   = new ArrayList<>(); res.add(normal); res.add(flying); res.add(poison); res.add(ghost); res.add(fire); res.add(dark);
        ArrayList<Type> imm   = new ArrayList<>(); imm.add(psychic);
        Fighter f = new Fighter("Tyranitar", types, stats(207,237,222,135), weak, res, imm,
                "/images/tyranitar.png", "/images/tyranitar_back.png");
        f.addMove(new Move("Dark Pulse", dark,  80, z()));
        f.addMove(new Move("Smack Down", rock,  50, z()));
        f.addMove(new Move("Stone Edge", rock, 100, z()));
        ArrayList<Double> a = z(); a.set(2,-0.33);
        f.addMove(new Move("Screech",    normal, 0, a));
        return f;
    }

    public static Fighter createScizor() {
        ArrayList<Type> types = new ArrayList<>(); types.add(bug); types.add(steel);
        ArrayList<Type> weak  = new ArrayList<>(); weak.add(fire);
        ArrayList<Type> res   = new ArrayList<>(); res.add(normal); res.add(bug); res.add(steel); res.add(grass); res.add(psychic); res.add(ice); res.add(dragon); res.add(fairy);
        ArrayList<Type> imm   = new ArrayList<>(); imm.add(poison);
        Fighter f = new Fighter("Scizor", types, stats(177,200,167,128), weak, res, imm,
                "/images/scizor.png", "/images/scizor_back.png");
        f.addMove(new Move("Metal Claw",  steel,  50, z()));
        f.addMove(new Move("X-Scissor",   bug,    80, z()));
        ArrayList<Double> a3 = z(); a3.set(1,0.33);
        f.addMove(new Move("Swords Dance",normal,  0, a3));
        ArrayList<Double> a2 = z(); a2.set(3,0.33);
        f.addMove(new Move("Agility",     psychic, 0, a2));
        return f;
    }

    public static Fighter createClefable() {
        ArrayList<Type> types = new ArrayList<>(); types.add(fairy);
        ArrayList<Type> weak  = new ArrayList<>(); weak.add(poison); weak.add(steel);
        ArrayList<Type> res   = new ArrayList<>(); res.add(fighting); res.add(bug); res.add(dark);
        ArrayList<Type> imm   = new ArrayList<>(); imm.add(dragon);
        Fighter f = new Fighter("Clefable", types, stats(202,161,156,123), weak, res, imm,
                "/images/clefable.png", "/images/clefable_back.png");
        f.addMove(new Move("Pound",           normal, 40, z()));
        f.addMove(new Move("Disarming Voice",  fairy,  40, z()));
        f.addMove(new Move("Moonblast",        fairy,  95, z()));
        ArrayList<Double> a = z(); a.set(0,0.25);
        f.addMove(new Move("Life Dew",         water,   0, a));
        return f;
    }

    public static Fighter createHoundoom() {
        ArrayList<Type> types = new ArrayList<>(); types.add(fire); types.add(dark);
        ArrayList<Type> weak  = new ArrayList<>(); weak.add(fighting); weak.add(ground); weak.add(rock); weak.add(water);
        ArrayList<Type> res   = new ArrayList<>(); res.add(ghost); res.add(steel); res.add(dark); res.add(fire); res.add(grass); res.add(ice);
        ArrayList<Type> imm   = new ArrayList<>(); imm.add(psychic);
        Fighter f = new Fighter("Houndoom", types, stats(182,178,145,161), weak, res, imm,
                "/images/houndoom.png", "/images/houndoom_back.png");
        f.addMove(new Move("Bite",       dark,   60, z()));
        f.addMove(new Move("Smog",       poison, 30, z()));
        f.addMove(new Move("Inferno",    fire,  100, z()));
        ArrayList<Double> a = z(); a.set(1,0.66);
        f.addMove(new Move("Nasty Plot", dark,    0, a));
        return f;
    }

    public static Fighter createLapras() {
        ArrayList<Type> types = new ArrayList<>(); types.add(water); types.add(ice);
        ArrayList<Type> weak  = new ArrayList<>(); weak.add(fighting); weak.add(grass); weak.add(rock); weak.add(electric);
        ArrayList<Type> res   = new ArrayList<>(); res.add(water); res.add(ice);
        ArrayList<Type> imm   = new ArrayList<>();
        Fighter f = new Fighter("Lapras", types, stats(237,150,161,123), weak, res, imm,
                "/images/lapras.png", "/images/lapras_back.png");
        f.addMove(new Move("Water Pulse", water,  60, z()));
        f.addMove(new Move("Ice Beam",    ice,    90, z()));
        f.addMove(new Move("Hydro Pump",  water, 110, z()));
        ArrayList<Double> a = z(); a.set(2,0.66);
        f.addMove(new Move("Reflect",     psychic, 0, a));
        return f;
    }

    public static Fighter createNidoking() {
        ArrayList<Type> types = new ArrayList<>(); types.add(poison); types.add(ground);
        ArrayList<Type> weak  = new ArrayList<>(); weak.add(ground); weak.add(water); weak.add(psychic); weak.add(ice);
        ArrayList<Type> res   = new ArrayList<>(); res.add(fighting); res.add(poison); res.add(rock); res.add(bug); res.add(fairy);
        ArrayList<Type> imm   = new ArrayList<>(); imm.add(electric);
        Fighter f = new Fighter("Nidoking", types, stats(188,158,141,150), weak, res, imm,
                "/images/nidoking.png", "/images/nidoking_back.png");
        f.addMove(new Move("Megahorn",   bug,    120, z()));
        f.addMove(new Move("Earth Power",ground,  90, z()));
        f.addMove(new Move("Sludge Wave",poison,  95, z()));
        ArrayList<Double> a = z(); a.set(2,-0.66);
        f.addMove(new Move("Leer",       normal,   0, a));
        return f;
    }

    public static Fighter createArcanine() {
        ArrayList<Type> types = new ArrayList<>(); types.add(fire);
        ArrayList<Type> weak  = new ArrayList<>(); weak.add(ground); weak.add(water); weak.add(rock);
        ArrayList<Type> res   = new ArrayList<>(); res.add(steel); res.add(grass); res.add(fire); res.add(bug); res.add(fairy); res.add(ice);
        ArrayList<Type> imm   = new ArrayList<>();
        Fighter f = new Fighter("Arcanine", types, stats(197,178,145,161), weak, res, imm,
                "/images/arcanine.png", "/images/arcanine_back.png");
        f.addMove(new Move("Extreme Speed", normal, 80,  z()));
        f.addMove(new Move("Play Rough",    fairy,  90,  z()));
        f.addMove(new Move("Flare Blitz",   fire,  120,  z()));
        ArrayList<Double> a = z(); a.set(1,0.33);
        f.addMove(new Move("Howl",          normal,  0, a));
        return f;
    }
}