package game.core;

import java.util.*;

// Central container for all mutable game state variables.
public class GameStatus {
    // Player/Team Stats
    public static String playerName;
    public static String playerGender;
    public static int playerAge;
    public static Creature playerActiveCreature;
    public static List<Creature> playerTeam = new ArrayList<>();
    public static int playerGold = 300;

    // Global Player Inventory
    public static int playerPotions = 3;
    public static int playerScrolls = 5;
    public static int playerHerbs = 5;
    public static int playerMegaPotions = 0;

    // Battle/Location State
    public static Creature rivalCreature;
    public static String gamePhase; // "explore" or "battle"
    public static String playerLocation;
    public static int roundCounter;
    public static String timeOfDay = "day";

    // Global Flags & Progression
    public static boolean skipText = false;
    public static boolean isPlaying = true;
    public static boolean isSageModeActive = false;
    public static boolean playerRanAway = false;
    public static boolean rivalCapturedInBattle = false;

    // Quest and Albularyo Flags
    public static int antingAntingCount = 0;
    public static int currentAlbularyoCreatureCount = 0; // NEW FIELD
    public static boolean firstAlbularyoDefeated = false;
    public static boolean enteredTown2Monologue = false;
    public static boolean secondAlbularyoDefeated = false;
    public static boolean enteredTown3Monologue = false;
    public static boolean thirdAlbularyoDefeated = false;

    // Quest Counters
    public static int currentQuestIndex = 0;
    public static int aswangDefeatedCount = 0;
    public static int duwendeDefeatedCount = 0;
    public static int tikbalangDefeatedCount = 0;
    public static int kapreDefeatedCount = 0;

    // Constants
    public static final int MAX_TEAM_SIZE = 4;
    public static final int ALBULARYO_1_LEVEL_GATE = 15;
    public static final int TOWN_2_LEVEL_GATE = 20;
    public static final int TOWN_3_MIN_LEVEL = 21;

    public static final double ENCOUNTER_CHANCE_TOWN1 = 0.6;
    public static final double ENCOUNTER_CHANCE_TOWN2 = 0.5;
    public static final double ENCOUNTER_CHANCE_TOWN3 = 0.4;
    public static final double ENCOUNTER_CHANCE_HUNTING_GROUND = 0.9;

    public static final double XP_MULTIPLIER_WILD = 1.30;
    public static final double XP_MULTIPLIER_ALBULARYO = 1.70;
    public static final double XP_MULTIPLIER_KHAIBALANG = 1.90;

    // LOCATION SETS FOR TOWN INDICATOR
    public static final Set<String> TOWN_1_LOCATIONS = Set.of("home", "town_square", "grassy_field", "forest_path", "dark_woods", "woods", "lake", "shop", "fairy_land", "the_first_albularyo", "cave");
    public static final Set<String> TOWN_2_LOCATIONS = Set.of("town_2_entrance", "town_2_center", "danielle_house", "shop_2", "dark_forest_2", "lawa", "latian", "albularyo_2", "path_to_danielle", "magic_well");
    public static final Set<String> TOWN_3_LOCATIONS = Set.of("road_to_manolo_fortich", "manolo_fortich_entrance", "manolo_fortich_town", "pekson_shop", "kitanglad_mountain_range", "dahilayan_forest", "paiyak_cave", "mangima_fen", "albularyo_3", "maurin_house");
}