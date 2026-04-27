package game.core;

import game.battle.BattleMove;

import java.util.*;

public class GameData {
    public static final Map<String, BattleMove> allMoves = new HashMap<>();
    public static final Map<String, Creature> allCreatures = new HashMap<>();
    public static final Map<String, Location> allLocations = new HashMap<>();
    public static final Map<String, Map<String, Double>> typeEffectiveness = new HashMap<>();
    public static final Map<String, String[]> locationEncounters = new HashMap<>();

    public static void initializeData() {
        // --- MOVE EFFECTS (Polymorphic Objects) ---
        MoveEffect damage = new DamageEffect();
        MoveEffect heal_5_percent = new HealEffect(0.05);

        MoveEffect acc_down_10_percent = new StatusEffect("lower_acc", 0.10);
        MoveEffect acc_down_5_percent = new StatusEffect("lower_acc", 0.05);
        MoveEffect acc_down_25_percent = new StatusEffect("lower_acc", 0.25);
        MoveEffect stun_35_percent = new StatusEffect("stun", 0.35);
        MoveEffect burn_25_percent = new StatusEffect("burn", 0.25);

        // --- MOVES ---
        allMoves.put("tail_whip", new BattleMove("Tail Whip", 20, "water", 100, 15, acc_down_10_percent, null));
        allMoves.put("moon's_gaze", new BattleMove("Moon's Gaze", 0, "lunar", 70, 10, acc_down_10_percent, "The creature attempts to confuse its foe."));
        allMoves.put("eclipse_devour", new BattleMove("Eclipse Devour", 60, "lunar", 50, 15, damage, null));
        allMoves.put("water_fangs", new BattleMove("Water Fangs", 40, "water", 80, 20, damage, null));
        allMoves.put("lunar_fury", new BattleMove("Lunar Fury", 0, "lunar", 100, 0, damage, "Deals extra damage at night."));

        allMoves.put("invisibility", new BattleMove("Invisibility", 0, "spirit", 100, 8, acc_down_10_percent, "The creature vanishes, avoiding the next attack."));
        allMoves.put("heal", new BattleMove("Heal", 0, "spirit", 80, 5, heal_5_percent, "The creature has healed for a small amount of HP!"));
        allMoves.put("venomous_touch", new BattleMove("Venomous Touch", 10, "poison", 70, 5, damage, null));
        allMoves.put("illusion", new BattleMove("Illusion", 0, "spirit", 100, 15, acc_down_10_percent, "A distracting illusion reduces the enemy's accuracy."));
        allMoves.put("evil_mode", new BattleMove("Evil Mode", 0, "spirit", 100, 15, damage, "The creature enters a strange, evil mode."));
        allMoves.put("cursed_strike", new BattleMove("Cursed Strike", 0, "poison", 100, 0, damage, null));

        allMoves.put("shapeshift", new BattleMove("Shapeshift", 0, "dark", 100, 10, damage, "The creature changes form and gains a speed boost (5% in-battle)."));
        allMoves.put("flesh_rend", new BattleMove("Flesh Rend", 25, "dark", 50, 6, damage, null));
        allMoves.put("soul_devourer", new BattleMove("Soul Devourer", 80, "dark", 60, 15, damage, null));
        allMoves.put("night_stalker", new BattleMove("Night Stalker", 0, "dark", 100, 0, damage, "Gains Speed and Attack at night."));

        allMoves.put("forest_mirage", new BattleMove("Forest Mirage", 0, "spirit", 100, 5, acc_down_5_percent, "A confusing mirage lowers the enemy's accuracy (5%)."));
        allMoves.put("heavy_hooves", new BattleMove("Heavy Hooves", 40, "normal", 70, 6, damage, null));
        allMoves.put("trickster's_whistle", new BattleMove("Trickster's Whistle", 0, "normal", 100, 7, acc_down_5_percent, "A sharp whistle lowers the enemy's defense (5%)."));
        allMoves.put("eclipse_rampage", new BattleMove("Eclipse Rampage", 75, "normal", 80, 15, damage, null));

        allMoves.put("smoke_veil", new BattleMove("Smoke Veil", 0, "forest", 100, 4, acc_down_10_percent, "A thick veil of smoke lowers the enemy's accuracy (10%)."));
        allMoves.put("tree_slam", new BattleMove("Tree Slam", 60, "forest", 70, 6, damage, null));
        allMoves.put("root_grasp", new BattleMove("Root Grasp", 30, "forest", 80, 7, damage, null));
        allMoves.put("forest_wrath", new BattleMove("Forest Wrath", 80, "forest", 70, 15, damage, null));

        allMoves.put("whirling_gust", new BattleMove("Whirling Gust", 60, "wind", 90, 10, acc_down_25_percent, "A gust reduces the enemy's accuracy by 20% (20% chance to land)."));
        allMoves.put("tempest_wing", new BattleMove("Tempest Wing", 80, "wind", 85, 8, damage, "The creature gains a 15% speed boost."));
        allMoves.put("wind_barrier", new BattleMove("Wind Barrier", 0, "wind", 100, 9, damage, "A shield reduces damage taken by 40%."));
        allMoves.put("hurricane_fury", new BattleMove("Hurricane Fury", 3 * 75, "wind", 80, 5, stun_35_percent, null));

        allMoves.put("baga_ng_kulam", new BattleMove("Baga ng Kulam", 55, "curse", 95, 10, burn_25_percent, null));
        allMoves.put("bulong_ng_dilim", new BattleMove("Bulong ng Dilim", 0, "curse", 100, 6, acc_down_25_percent, "A curse reduces the enemy's Attack and Defense by 20%."));
        allMoves.put("usok_ng_alitaptap", new BattleMove("Usok ng Alitaptap", 0, "curse", 100, 15, damage, "Creates a cloud, granting evasion and damage reduction."));
        allMoves.put("ritwal_ng_paghihiganti", new BattleMove("Ritwal ng Paghihiganti", 3 * 75, "curse", 85, 5, burn_25_percent, null));

        allMoves.put("aqua_whip", new BattleMove("Aqua Whip", 55, "water", 100, 10, damage, null));
        allMoves.put("charm_song", new BattleMove("Charm Song", 35, "spirit", 75, 5, stun_35_percent, null));
        allMoves.put("healing_tide", new BattleMove("Healing Tide", 0, "water", 100, 6, heal_5_percent, "The creature has healed and a debuff was removed! (Heal: 45 HP)"));
        allMoves.put("tsunami_cry", new BattleMove("Tsunami Cry", 95, "water", 80, 8, stun_35_percent, null));

        allMoves.put("winged_separation", new BattleMove("Winged Separation", 0, "dark", 100, 10, acc_down_25_percent, "Splits the body, gaining high evasion."));
        allMoves.put("heartpierce_screech", new BattleMove("Heartpierce Screech", 100, "dark", 70, 5, stun_35_percent, null));
        allMoves.put("blood_drain", new BattleMove("Blood Drain", 80, "dark", 85, 5, damage, null));
        allMoves.put("crimson_eclipse", new BattleMove("Crimson Eclipse", 250, "dark", 60, 9, damage, null));

        allMoves.put("gravebind_roots", new BattleMove("Gravebind Roots", 0, "control", 80, 5, acc_down_25_percent, "The creature's movement is restricted! Speed fell sharply (50% reduction)."));
        allMoves.put("cave_in_fury", new BattleMove("Cave-In Fury", 50, "earth", 70, 9, damage, null));
        allMoves.put("whispers_of_the_dead", new BattleMove("Whispers of the Dead", 0, "psychic", 60, 10, stun_35_percent, null));
        allMoves.put("sacred_pact", new BattleMove("Sacred Pact", 0, "buff", 100, 5, heal_5_percent, "A sacred light heals for 25 HP and buffs Defense."));

        allMoves.put("whisper_drift", new BattleMove("Whisper Drift", 0, "illusion", 80, 5, acc_down_25_percent, "The creature is shrouded in illusion! Accuracy fell by 25%."));
        allMoves.put("mire_grasp", new BattleMove("Mire Grasp", 25, "water", 90, 7, damage, null));
        allMoves.put("echo", new BattleMove("Echo", 0, "sound", 90, 6, acc_down_25_percent, "Releases a powerful echo wave, dealing flat damage and reducing accuracy."));
        allMoves.put("croak_mimic", new BattleMove("Croak Mimic", 0, "disrupt", 100, 8, stun_35_percent, "The creature performs a confusing croak, potentially stunning the enemy."));

        allMoves.put("resonant_burst", new BattleMove("Resonant Burst", 35, "sound", 80, 7, damage, null));
        allMoves.put("echo_maze", new BattleMove("Echo Maze", 0, "puzzle_illusion", 100, 4, acc_down_25_percent, null));
        allMoves.put("crystal_shard_slam", new BattleMove("Crystal Shard Slam", 20, "physical", 85, 6, damage, null));
        allMoves.put("soundless_zone", new BattleMove("Soundless Zone", 0, "buff_shield", 100, 5, damage, null));

        allMoves.put("gale_dive", new BattleMove("Gale Dive", 25, "wind_physical", 90, 8, damage, null));
        allMoves.put("moonlit_scales", new BattleMove("Moonlit Scales", 0, "buff", 100, 5, acc_down_25_percent, null));
        allMoves.put("storm_herald", new BattleMove("Storm Herald", 50, "lightning", 80, 8, stun_35_percent, null));
        allMoves.put("spirit_glide", new BattleMove("Spirit Glide", 0, "movement", 100, 20, damage, null));

        allMoves.put("say_stop", new BattleMove("Say Stop!", 0, "interrupt", 100, 10, damage, "Khaibalang snaps, \"Say stop!\""));
        allMoves.put("quote_of_the_day", new BattleMove("Quote of the Day", 15, "psychological", 100, 9, stun_35_percent, "Khaibalang chants,\n\n\"Pain is temporary, but regret lasts forever.\""));
        allMoves.put("eyes_here_people", new BattleMove("Eyes Here, People!", 0, "focus", 100, 12, damage, "Khaibalang claps sharply: \"Eyes here,\npeople!\""));
        allMoves.put("jabi", new BattleMove("JABI", 0, "buff", 100, 5, heal_5_percent, "Khaibalang pulls out a JABI meal, takes a\nbite, and powers up!\n\n\"My favorite... now I’m serious.\""));
        allMoves.put("you_did_not_pass", new BattleMove("YOU DID NOT PASS!", 80, "judgment", 100, 7, damage, "Khaibalang raises a burning report card.\n\n\"YOU. DID. NOT. PASS!\""));


        // --- CREATURES (Updated with createMoveMap) ---
        allCreatures.put("kapre", new Creature("Kapre", "forest", 7,
                Arrays.asList(),
                createMoveMap("Smoke Veil", 5, "Tree Slam", 1, "Root Grasp", 10, "Forest Wrath", 20), 80, "500 years old", "Forests, dark woods", "Gentle giant", 75 + (7 - 1) * 10, 50, 50, 3, 5, 5));

        allCreatures.put("tikbalang", new Creature("Tikbalang", "Mythical Beast", 7,
                Arrays.asList(),
                createMoveMap("Forest Mirage", 5, "Heavy Hooves", 1, "Trickster's Whistle", 10, "Eclipse Rampage", 20), 200, "436 years old", "Deep forests", "Trickster", 75 + (7 - 1) * 10, 50, 50, 3, 5, 5));

        allCreatures.put("duwende", new Creature("Duwende", "Elemental / Spirit", 7,
                Arrays.asList(),
                createMoveMap("Invisibility", 1, "Heal", 1, "Venomous Touch", 5, "Illusion", 15, "Evil Mode", 20, "Cursed Strike", 20), 400, "120 years old", "Underground", "Mischievous", 75 + (7 - 1) * 10, 50, 50, 3, 5, 5));

        allCreatures.put("aswang", new Creature("Aswang", "Shapeshifter / Dark Spirit", 3,
                Arrays.asList(),
                createMoveMap("Shapeshift", 1, "Flesh Rend", 1, "Soul Devourer", 20, "Night Stalker", 1), 250, "300+ years", "Deep forests", "Predatory", 75 + (3 - 1) * 10, 50, 50, 3, 5, 5));

        allCreatures.put("bakunawa", new Creature("Bakunawa", "Legendary Sea Serpent", 3,
                Arrays.asList(),
                createMoveMap("Tail Whip", 1, "Moon's Gaze", 20, "Eclipse Devour", 25, "Water Fangs", 1, "Lunar Fury", 1), 200, "2000 years old", "Deepest oceans", "Auspicious", 75 + (3 - 1) * 10, 50, 50, 3, 5, 5));

        // Town 2 & 3 Creatures
        int t2lvl = 9; int t2hp = 75 + (t2lvl - 1) * 10;
        allCreatures.put("amihan", new Creature("Amihan", "wind", t2lvl,
                Arrays.asList(),
                createMoveMap("Whirling Gust", 1, "Tempest Wing", 1, "Wind Barrier", 10, "Hurricane Fury", 15), 95, "Ancient", "Skies", "Proud", t2hp + 400, 65, 45, 5, 5, 5));

        allCreatures.put("mangkukulam", new Creature("Mangkukulam", "curse", t2lvl,
                Arrays.asList(),
                createMoveMap("Baga ng Kulam", 1, "Bulong ng Dilim", 5, "Usok ng Alitaptap", 1, "Ritwal ng Paghihiganti", 15), 70, "Old", "Villages", "Vengeful", t2hp + 380, 75, 50, 5, 5, 5));

        allCreatures.put("sirena", new Creature("Sirena", "water", t2lvl,
                Arrays.asList(),
                createMoveMap("Aqua Whip", 1, "Charm Song", 1, "Healing Tide", 5, "Tsunami Cry", 10), 85, "Youth", "Ocean", "Defensive", t2hp + 250, 75, 90, 5, 5, 5));

        allCreatures.put("manananggal", new Creature("Manananggal", "dark", t2lvl,
                Arrays.asList(),
                createMoveMap("Winged Separation", 1, "Heartpierce Screech", 1, "Blood Drain", 10, "Crimson Eclipse", 15), 60, "Varies", "Jungles", "Bloodthirsty", t2hp + 120, 90, 50, 5, 5, 5));

        int t3lvl = 15; int t3hp = 75 + (t3lvl - 1) * 10;
        allCreatures.put("talabugtas_warden", new Creature("Talabugta's Warden", "earth", t3lvl,
                Arrays.asList(),
                createMoveMap("Gravebind Roots", 1, "Cave-In Fury", 1, "Whispers of the Dead", 15, "Sacred Pact", 10), 30, "Ancient", "Mountain", "Guardian", t3hp + 200, 60, 100, 8, 8, 8));

        allCreatures.put("kulaman_marsh_shade", new Creature("Kulaman Marsh Shade", "swamp", t3lvl,
                Arrays.asList(),
                createMoveMap("Whisper Drift", 1, "Mire Grasp", 5, "Echo", 10, "Croak Mimic", 15), 95, "Ageless", "Mangima", "Lurer", t3hp - 50, 70, 40, 8, 8, 8));

        allCreatures.put("palaopao_echoer", new Creature("Palaopao Echoer", "rock", t3lvl,
                Arrays.asList(),
                createMoveMap("Resonant Burst", 5, "Echo Maze", 1, "Crystal Shard Slam", 1, "Soundless Zone", 10), 60, "Ancient", "Cave", "Guardian", t3hp + 100, 65, 80, 8, 8, 8));

        allCreatures.put("higaonon_sky_serpent", new Creature("Higaonon Sky Serpent", "wind", t3lvl,
                Arrays.asList(),
                createMoveMap("Gale Dive", 1, "Moonlit Scales", 5, "Storm Herald", 1, "Spirit Glide", 10), 110, "Ancient", "Canopy", "Noble", t3hp, 85, 60, 8, 8, 8));

        // Boss
        Creature khaibalang = new Creature("Khaibalang", "Mythical Beast/Dark", 50,
                Arrays.asList(),
                createMoveMap("Say Stop!", 1, "Quote of the Day", 1, "Eyes Here, People!", 1, "JABI", 1, "YOU DID NOT PASS!", 1), 90, "Ancient", "Unknown", "Malevolent", 1500, 190, 220, 3, 99, 99);
        khaibalang.megaPotions = 2;
        allCreatures.put("khaibalang", khaibalang);

        // --- LOCATIONS ---
        allLocations.put("home", new Location("Cozy home.", Map.of("north", "town_square"), "heal"));
        allLocations.put("town_square", new Location("Town Square.", Map.of("north", "forest_path", "south", "home", "east", "shop", "west", "grassy_field"), null));
        allLocations.put("grassy_field", new Location("Grassy field.", Map.of("north", "lake", "east", "forest_path", "south", "shop", "west", "town_square"), null));
        allLocations.put("forest_path", new Location("Forest path.", Map.of("south", "town_square", "east", "grassy_field", "west", "dark_woods", "north", "woods"), null));
        allLocations.put("dark_woods", new Location("Dark woods.", Map.of("east", "forest_path"), null));
        allLocations.put("woods", new Location("Woods.", Map.of("south", "forest_path", "east", "lake", "west", "cave"), null));
        allLocations.put("lake", new Location("Lake.", Map.of("west", "woods", "south", "grassy_field", "east", "fairy_land"), null));
        allLocations.put("shop", new Location("Shop.", Map.of("west", "town_square", "north", "grassy_field"), "shop_1_event"));
        allLocations.put("fairy_land", new Location("Fairy Land.", Map.of("west", "lake", "south", "the_first_albularyo"), null));
        allLocations.put("the_first_albularyo", new Location("Albularyo 1.", Map.of("north", "fairy_land", "east", "town_2_entrance"), null));
        allLocations.put("cave", new Location("Cave.", Map.of("east", "woods"), "grandpa_friend"));

        allLocations.put("town_2_entrance", new Location("Town 2 Entrance.", Map.of("west", "the_first_albularyo", "north", "town_2_center", "east", "lawa"), null));
        allLocations.put("town_2_center", new Location("Town 2 Center.", Map.of("south", "town_2_entrance", "north", "path_to_danielle", "east", "dark_forest_2", "west", "magic_well"), null));
        allLocations.put("path_to_danielle", new Location("Path to Danielle.", Map.of("south", "town_2_center", "north", "danielle_house"), null));
        allLocations.put("danielle_house", new Location("Danielle's House.", Map.of("south", "path_to_danielle"), "heal"));
        allLocations.put("shop_2", new Location("Shop 2.", Map.of("west", "dark_forest_2"), "shop_2_event"));
        allLocations.put("dark_forest_2", new Location("Dark Forest 2.", Map.of("west", "town_2_center", "east", "shop_2"), null));
        allLocations.put("magic_well", new Location("Magic Well.", Map.of("east", "town_2_center"), "magic_well_event"));
        allLocations.put("lawa", new Location("Lawa.", Map.of("west", "town_2_entrance", "south", "latian"), null));
        allLocations.put("latian", new Location("Latian.", Map.of("north", "lawa", "south", "albularyo_2"), null));
        allLocations.put("albularyo_2", new Location("Albularyo 2.", Map.of("north", "latian", "east", "road_to_manolo_fortich"), null));

        allLocations.put("road_to_manolo_fortich", new Location("Road to Manolo.", Map.of("west", "albularyo_2", "east", "manolo_fortich_entrance"), null));
        allLocations.put("manolo_fortich_entrance", new Location("Entrance.", Map.of("west", "road_to_manolo_fortich", "north", "manolo_fortich_town"), null));
        allLocations.put("manolo_fortich_town", new Location("Town Square 3.", Map.of("south", "manolo_fortich_entrance", "north", "kitanglad_mountain_range", "east", "paiyak_cave", "west", "pekson_shop"), null));
        allLocations.put("pekson_shop", new Location("Pekson Shop.", Map.of("east", "manolo_fortich_town"), "shop_3_event"));
        allLocations.put("kitanglad_mountain_range", new Location("Mountains.", Map.of("south", "manolo_fortich_town", "west", "dahilayan_forest", "east", "maurin_house"), null));
        allLocations.put("dahilayan_forest", new Location("Forest.", Map.of("east", "kitanglad_mountain_range"), null));
        allLocations.put("maurin_house", new Location("Maurin House.", Map.of("west", "kitanglad_mountain_range"), "heal"));
        allLocations.put("paiyak_cave", new Location("Paiyak Cave.", Map.of("west", "manolo_fortich_town", "south", "mangima_fen"), null));
        allLocations.put("mangima_fen", new Location("Mangima Fen.", Map.of("north", "paiyak_cave", "east", "albularyo_3"), null));
        allLocations.put("albularyo_3", new Location("Final Albularyo.", Map.of("west", "mangima_fen"), null));

        // --- TYPE EFFECTIVENESS ---
        typeEffectiveness.put("water", createMap("dark", 1.0, "normal", 1.0, "forest", 2.0, "spirit", 1.0, "wind", 1.0, "curse", 1.0, "earth", 1.0, "swamp", 0.5, "rock", 1.0, "lightning", 1.0, "psychic", 1.0));
        typeEffectiveness.put("lunar", createMap("dark", 2.0, "normal", 1.0, "forest", 1.0, "spirit", 1.0, "wind", 1.0, "curse", 1.0, "earth", 1.0, "swamp", 1.0, "rock", 1.0, "lightning", 1.0, "psychic", 1.0));
        typeEffectiveness.put("spirit", createMap("dark", 0.5, "normal", 1.0, "forest", 1.0, "water", 1.0, "wind", 1.0, "curse", 1.0, "earth", 1.0, "swamp", 1.0, "rock", 1.0, "lightning", 1.0, "psychic", 1.0));
        typeEffectiveness.put("poison", createMap("dark", 1.0, "normal", 1.0, "forest", 2.0, "water", 1.0, "wind", 1.0, "curse", 1.0, "earth", 1.0, "swamp", 1.0, "rock", 1.0, "lightning", 1.0, "psychic", 1.0));
        typeEffectiveness.put("dark", createMap("spirit", 2.0, "normal", 1.0, "forest", 1.0, "water", 1.0, "wind", 1.0, "curse", 1.0, "earth", 1.0, "swamp", 1.0, "rock", 1.0, "lightning", 1.0, "psychic", 1.0));
        typeEffectiveness.put("normal", createMap("dark", 1.0, "spirit", 1.0, "forest", 1.0, "water", 1.0, "wind", 1.0, "curse", 1.0, "earth", 1.0, "swamp", 1.0, "rock", 1.0, "lightning", 1.0, "psychic", 1.0));
        typeEffectiveness.put("forest", createMap("water", 0.5, "dark", 1.0, "spirit", 1.0, "normal", 1.0, "wind", 1.0, "curse", 1.0, "earth", 1.0, "swamp", 1.0, "rock", 1.0, "lightning", 1.0, "psychic", 1.0));
        typeEffectiveness.put("wind", createMap("forest", 2.0, "water", 0.5, "dark", 1.0, "curse", 1.0, "earth", 0.5, "swamp", 1.0, "rock", 1.0, "lightning", 1.0, "psychic", 1.0));
        typeEffectiveness.put("curse", createMap("spirit", 2.0, "dark", 1.0, "water", 1.0, "wind", 1.0, "earth", 1.0, "swamp", 1.0, "rock", 1.0, "lightning", 1.0, "psychic", 1.0));
        typeEffectiveness.put("earth", createMap("wind", 2.0, "water", 1.0, "forest", 1.0, "swamp", 1.5, "rock", 0.5, "lightning", 0.5));
        typeEffectiveness.put("swamp", createMap("earth", 1.5, "water", 1.0, "wind", 0.5));
        typeEffectiveness.put("rock", createMap("wind", 1.5, "normal", 1.0, "earth", 0.5, "water", 0.5));
        typeEffectiveness.put("lightning", createMap("water", 2.0, "wind", 1.5, "earth", 0.5, "rock", 1.5));
        typeEffectiveness.put("psychic", createMap("dark", 0.5, "normal", 1.0, "spirit", 1.0));

        // --- ENCOUNTERS ---
        locationEncounters.put("lake", new String[]{"bakunawa"});
        locationEncounters.put("grassy_field", new String[]{"duwende", "tikbalang"});
        locationEncounters.put("dark_woods", new String[]{"kapre", "tikbalang", "aswang"});
        locationEncounters.put("woods", new String[]{"duwende", "tikbalang", "kapre"});
        locationEncounters.put("forest_path", new String[]{"duwende"});
        locationEncounters.put("fairy_land", new String[]{"duwende"});
        locationEncounters.put("cave", new String[]{"duwende", "aswang"});
        locationEncounters.put("town_2_center", new String[]{"sirena", "amihan"});
        locationEncounters.put("path_to_danielle", new String[]{"sirena"});
        locationEncounters.put("lawa", new String[]{"sirena", "amihan"});
        locationEncounters.put("dark_forest_2", new String[]{"mangkukulam", "manananggal"});
        locationEncounters.put("latian", new String[]{"sirena", "mangkukulam", "amihan"});
        locationEncounters.put("magic_well", new String[]{"mangkukulam"});
        locationEncounters.put("manolo_fortich_town", new String[]{"talabugtas_warden", "palaopao_echoer"});
        locationEncounters.put("kitanglad_mountain_range", new String[]{"higaonon_sky_serpent", "talabugtas_warden"});
        locationEncounters.put("dahilayan_forest", new String[]{"talabugtas_warden", "higaonon_sky_serpent"});
        locationEncounters.put("paiyak_cave", new String[]{"palaopao_echoer", "kulaman_marsh_shade"});
        locationEncounters.put("mangima_fen", new String[]{"kulaman_marsh_shade", "palaopao_echoer"});
    }

    private static Map<String, Double> createMap(Object... keyValues) {
        Map<String, Double> map = new HashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            map.put((String) keyValues[i], (Double) keyValues[i + 1]);
        }
        return map;
    }

    // NEW HELPER: For Level/Integer Maps
    private static Map<String, Integer> createMoveMap(Object... keyValues) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            map.put((String) keyValues[i], (Integer) keyValues[i + 1]);
        }
        return map;
    }
}