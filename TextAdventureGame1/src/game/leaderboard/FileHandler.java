package game.leaderboard;

import java.io.*;

public class FileHandler {
    private static final String LEADERBOARD_FILE = "resources/data/leaderboard.dat";
    private static final String DATA_DIR = "resources/data";

    /**
     * Initialize data directory if it doesn't exist
     */
    public static void initializeDataDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                System.out.println("[FileHandler] Created data directory: " + DATA_DIR);
            } else {
                System.err.println("[FileHandler] Failed to create data directory");
            }
        }
    }

    /**
     * Save leaderboard to file
     */
    public static boolean saveLeaderboard(Leaderboard leaderboard) {
        initializeDataDirectory();

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LEADERBOARD_FILE))) {
            oos.writeObject(leaderboard);
            System.out.println("[FileHandler] Leaderboard saved successfully");
            return true;
        } catch (IOException e) {
            System.err.println("[FileHandler] Error saving leaderboard: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Load leaderboard from file
     * Returns new empty leaderboard if file doesn't exist
     */
    public static Leaderboard loadLeaderboard() {
        File file = new File(LEADERBOARD_FILE);

        // If file doesn't exist, return new empty leaderboard
        if (!file.exists()) {
            System.out.println("[FileHandler] Leaderboard file not found, creating new");
            return new Leaderboard();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(LEADERBOARD_FILE))) {
            Leaderboard leaderboard = (Leaderboard) ois.readObject();
            System.out.println("[FileHandler] Leaderboard loaded successfully (" + leaderboard.size() + " entries)");
            return leaderboard;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[FileHandler] Error loading leaderboard: " + e.getMessage());
            e.printStackTrace();
            return new Leaderboard();
        }
    }

    /**
     * Delete leaderboard file (for reset)
     */
    public static boolean deleteLeaderboard() {
        File file = new File(LEADERBOARD_FILE);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("[FileHandler] Leaderboard file deleted");
                return true;
            } else {
                System.err.println("[FileHandler] Failed to delete leaderboard file");
                return false;
            }
        }
        return true;
    }

    /**
     * Check if leaderboard file exists
     */
    public static boolean leaderboardExists() {
        return new File(LEADERBOARD_FILE).exists();
    }

    /**
     * Get leaderboard file path
     */
    public static String getLeaderboardPath() {
        return LEADERBOARD_FILE;
    }
}