package com.ciree.minigame.data;

import com.ciree.minigame.BridgeRacingDojo;
import com.ciree.minigame.game.RaceArena;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Handles persistent data loading and saving (Arenas, Player Stats).
 */
public class DataHandler {

    private final BridgeRacingDojo plugin;
    private final Map<String, RaceArena> loadedArenas = new HashMap<>();

    private final StatFileHandler statFileHandler;

    public DataHandler(BridgeRacingDojo plugin) {
        this.plugin = plugin;
        this.statFileHandler = new StatFileHandler(plugin);
    }

    public void loadAllArenas() {
        plugin.getLogger().log(Level.INFO, "DataHandler: Loading all arena data (STUB).");
        // Real implementation: Load arenas from 'arenas.yml' into loadedArenas map.
    }
    public void saveAllData() {
        plugin.getLogger().log(Level.INFO, "DataHandler: Saving all data (STATS ONLY).");
        statFileHandler.save();
    }
    public void close() {
        plugin.getLogger().log(Level.INFO, "DataHandler: Closing connections (STATS ONLY).");
        statFileHandler.save();
    }

    public Map<String, RaceArena> getLoadedArenas() {
        return loadedArenas;
    }

    public Set<RaceArena> getLoadedArenaValues() {
        return Collections.unmodifiableSet(Set.copyOf(loadedArenas.values()));
    }

    public RaceArena getArena(String name) {
        return loadedArenas.get(name);
    }

    public void saveArena(RaceArena arena) {
        plugin.getLogger().log(Level.INFO, "DataHandler: Saving arena " + arena.getName() + " (STUB).");
    }

    public void deleteArena(String name) {
        plugin.getLogger().log(Level.INFO, "DataHandler: Deleting arena " + name + " (STUB).");
        loadedArenas.remove(name);
    }


    /**
     * Retrieves a player's best time for a specific map from stats.yml.
     */
    public long getPersonalBest(UUID playerId, String mapName) {
        FileConfiguration config = statFileHandler.getConfig();
        String path = playerId.toString() + "." + mapName + ".best_time";

        // Returns Long.MAX_VALUE if no record exists, ensuring the first run is always a PB.
        return config.getLong(path, Long.MAX_VALUE);
    }

    /**
     * Saves a player's new personal best and increments their races completed count.
     */
    public void savePersonalBest(UUID playerId, String mapName, long time) {
        FileConfiguration config = statFileHandler.getConfig();

        // 1. Save the new best time
        String bestTimePath = playerId.toString() + "." + mapName + ".best_time";
        config.set(bestTimePath, time);

        // 2. Save total races completed (increment counter)
        String racesPath = playerId.toString() + ".races_completed";
        int races = config.getInt(racesPath, 0);
        config.set(racesPath, races + 1);

        statFileHandler.save();
    }

    /**
     * Retrieves the total number of races completed by a player.
     * Required by StatHandler.
     */
    public int getRacesCompleted(UUID playerId) {
        FileConfiguration config = statFileHandler.getConfig();
        String racesPath = playerId.toString() + ".races_completed";
        return config.getInt(racesPath, 0);
    }
}