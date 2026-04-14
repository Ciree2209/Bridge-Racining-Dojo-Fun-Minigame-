package com.ciree.minigame.util;

import com.ciree.minigame.BridgeRacingDojo;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Utility class to manage the YAML file where all map configurations are stored.
 */
public class ArenaFile {

    private final BridgeRacingDojo plugin;
    private File file;
    private FileConfiguration config;
    private final String fileName = "arenas.yml";

    public ArenaFile(BridgeRacingDojo plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), fileName);

        // Check if the file exists, if not, create it
        if (!file.exists()) {
            try {
                // Ensure the plugin data folder exists
                if (!plugin.getDataFolder().exists()) {
                    plugin.getDataFolder().mkdirs();
                }
                // Create the file itself
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create " + fileName + ": " + e.getMessage());
            }
        }

        // Load the configuration from the file
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save " + fileName + ": " + e.getMessage());
        }
    }

    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(file);
    }
}