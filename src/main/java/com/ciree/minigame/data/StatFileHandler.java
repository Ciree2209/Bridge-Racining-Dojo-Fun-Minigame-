package com.ciree.minigame.data;

import com.ciree.minigame.BridgeRacingDojo;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Handles the loading, saving, and access for the stats.yml file.
 */
public class StatFileHandler {

    private final BridgeRacingDojo plugin;
    private File statsFile;
    private FileConfiguration statsConfig;

    public StatFileHandler(BridgeRacingDojo plugin) {
        this.plugin = plugin;
        this.plugin.getLogger().info("Initializing Stat File Handler...");
        setup();
    }

    private void setup() {
        statsFile = new File(plugin.getDataFolder(), "stats.yml");

        if (!statsFile.exists()) {
            try {
                statsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create stats.yml!", e);
            }
        }

        statsConfig = YamlConfiguration.loadConfiguration(statsFile);
    }

    public FileConfiguration getConfig() {
        return statsConfig;
    }

    public void save() {
        try {
            statsConfig.save(statsFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save stats.yml!", e);
        }
    }

    public void reload() {
        statsConfig = YamlConfiguration.loadConfiguration(statsFile);
    }
}