package game.leaderboard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Leaderboard implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<LeaderboardEntry> entries;
    private static final int MAX_ENTRIES = 10;

    public Leaderboard() {
        this.entries = new ArrayList<>();
    }

    /**
     * Add a new entry to the leaderboard
     * Automatically sorted by time (ascending = best/fastest)
     */
    public void addEntry(String playerName, long timeMillis) {
        LeaderboardEntry entry = new LeaderboardEntry(playerName, timeMillis);
        entries.add(entry);

        // Sort by time (ascending - lowest/fastest time is best)
        Collections.sort(entries);

        // Keep only top 10
        if (entries.size() > MAX_ENTRIES) {
            entries = new ArrayList<>(entries.subList(0, MAX_ENTRIES));
        }
    }

    /**
     * Get all entries sorted by rank (best first)
     */
    public List<LeaderboardEntry> getEntries() {
        return new ArrayList<>(entries);
    }

    /**
     * Get rank of a specific time (1 = best)
     */
    public int getRank(long timeMillis) {
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getTimeMillis() >= timeMillis) {
                return i + 1;
            }
        }
        return entries.size() + 1;
    }

    /**
     * Clear all entries
     */
    public void clear() {
        entries.clear();
    }

    /**
     * Get number of entries
     */
    public int size() {
        return entries.size();
    }

    /**
     * Check if leaderboard is full
     */
    public boolean isFull() {
        return entries.size() >= MAX_ENTRIES;
    }

    /**
     * Get the worst (slowest) time on the board
     */
    public long getWorstTime() {
        if (entries.isEmpty()) return Long.MAX_VALUE;
        return entries.get(entries.size() - 1).getTimeMillis();
    }

    /**
     * Inner class: Individual leaderboard entry
     */
    public static class LeaderboardEntry implements Comparable<LeaderboardEntry>, Serializable {
        private static final long serialVersionUID = 1L;
        private String playerName;
        private long timeMillis;
        private long timestamp;

        public LeaderboardEntry(String playerName, long timeMillis) {
            this.playerName = playerName;
            this.timeMillis = timeMillis;
            this.timestamp = System.currentTimeMillis();
        }

        public String getPlayerName() {
            return playerName;
        }

        public long getTimeMillis() {
            return timeMillis;
        }

        public long getTimestamp() {
            return timestamp;
        }

        /**
         * Format time as HH:MM:SS
         */
        public String getFormattedTime() {
            long totalSeconds = timeMillis / 1000;
            long hours = totalSeconds / 3600;
            long minutes = (totalSeconds % 3600) / 60;
            long seconds = totalSeconds % 60;

            if (hours > 0) {
                return String.format("%02d:%02d:%02d", hours, minutes, seconds);
            } else {
                return String.format("%02d:%02d", minutes, seconds);
            }
        }

        @Override
        public int compareTo(LeaderboardEntry other) {
            // Sort by time ascending (fastest/lowest first)
            return Long.compare(this.timeMillis, other.timeMillis);
        }

        @Override
        public String toString() {
            return playerName + " - " + getFormattedTime();
        }
    }
}