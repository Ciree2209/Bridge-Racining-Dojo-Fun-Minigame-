package com.ciree.minigame.data;

import org.bukkit.configuration.ConfigurationSection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Stores race statistics and personal best times for a single player.
 * Now primarily structured for MySQL/JSON serialization.
 */
public class DojoData {

    private final UUID playerUUID;
    private int racesCompleted;
    private final Map<String, Long> bestTimes; // MapName -> BestTimeMs

    // Constructor for new player data
    public DojoData(UUID playerUUID) {
        this(playerUUID, 0, new HashMap<>());
    }

    // Constructor for MySQL/JSON deserialization
    public DojoData(UUID playerUUID, int racesCompleted, Map<String, Long> bestTimes) {
        this.playerUUID = playerUUID;
        this.racesCompleted = racesCompleted;
        this.bestTimes = bestTimes;
    }

    // --- Legacy YAML Serialization Methods (Keep for fallback) ---

    public static DojoData deserialize(UUID uuid, ConfigurationSection section) {
        if (section == null) {
            return new DojoData(uuid);
        }

        int races = section.getInt("races-completed", 0);

        Map<String, Long> times = new HashMap<>();
        ConfigurationSection timesSection = section.getConfigurationSection("best-times");
        if (timesSection != null) {
            for (String mapName : timesSection.getKeys(false)) {
                times.put(mapName, timesSection.getLong(mapName));
            }
        }

        return new DojoData(uuid, races, times);
    }

    public void serialize(ConfigurationSection section) {
        section.set("races-completed", racesCompleted);

        ConfigurationSection timesSection = section.createSection("best-times");
        for (Map.Entry<String, Long> entry : bestTimes.entrySet()) {
            timesSection.set(entry.getKey(), entry.getValue());
        }
    }


    // --- Getters & Business Logic ---

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public int getRacesCompleted() {
        return racesCompleted;
    }

    public void incrementRaces() {
        this.racesCompleted++;
    }

    public Map<String, Long> getBestTimesMap() {
        return bestTimes; // Exposed for Gson serialization
    }

    public long getBestTime(String mapName) {
        return bestTimes.getOrDefault(mapName.toLowerCase(), 0L);
    }

    public boolean setBestTime(String mapName, long newTime) {
        String key = mapName.toLowerCase();
        long currentBest = getBestTime(key);

        if (currentBest == 0 || newTime < currentBest) {
            bestTimes.put(key, newTime);
            return true;
        }
        return false;
    }
}