package com.ciree.minigame;

import com.ciree.minigame.command.AdminCommand;
import com.ciree.minigame.command.DojoCommand;
import com.ciree.minigame.command.DojoTabCompleter;
import com.ciree.minigame.data.DataHandler;
import com.ciree.minigame.data.StatHandler;
import com.ciree.minigame.game.DojoManager;
import com.ciree.minigame.listener.RaceListener;
import com.ciree.minigame.util.MessageUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class BridgeRacingDojo extends JavaPlugin {

    private static BridgeRacingDojo instance;
    private DataHandler dataHandler;
    private DojoManager dojoManager;
    private StatHandler statHandler;

    public static BridgeRacingDojo getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        MessageUtil.initialize();

        this.dataHandler = new DataHandler(this);

        copyDefaultArenaIfMissing();
        this.dataHandler.loadAllArenas();

        this.dojoManager = new DojoManager(this);
        this.statHandler = new StatHandler(this);

        DojoCommand dojoCommandExecutor = new DojoCommand(this);
        AdminCommand adminCommandExecutor = new AdminCommand(this);
        DojoTabCompleter tabCompleter = new DojoTabCompleter(this);

        if (getCommand("dojo") != null) {
            getCommand("dojo").setExecutor(dojoCommandExecutor);
            getCommand("dojo").setTabCompleter(tabCompleter);
        } else {
            getLogger().log(Level.SEVERE, "Command 'dojo' not found in plugin.yml!");
        }

        if (getCommand("dojo admin") != null) {
            getCommand("dojo admin").setExecutor(adminCommandExecutor);
            getCommand("dojo admin").setTabCompleter(tabCompleter);
        } else {
            getLogger().log(Level.WARNING, "Command 'dojo admin' not explicitly found. Delegation from 'dojo' will be used.");
        }

        getServer().getPluginManager().registerEvents(new RaceListener(this), this);

        getLogger().info("Bridge Racing Dojo enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (dojoManager != null) {
            dojoManager.endAllRaces();
        }
        if (dataHandler != null) {
            dataHandler.saveAllData();
            dataHandler.close();
        }
    }

    public DataHandler getDataHandler() {
        return dataHandler;
    }

    public DojoManager getDojoManager() {
        return dojoManager;
    }

    public StatHandler getStatHandler() {
        return statHandler;
    }

    private void copyDefaultArenaIfMissing() {
        File arenaFile = new File(getDataFolder(), "arenas.yml");

        if (!arenaFile.exists()) {
            try {
                // Ensure the parent directory exists
                if (getDataFolder().mkdirs() && !arenaFile.exists()) {
                    arenaFile.createNewFile();
                }
            } catch (IOException e) {
                getLogger().severe("Could not create arenas.yml: " + e.getMessage());
                return;
            }
        }

        if (arenaFile.length() == 0) {
            saveResource("default_arena.yml", false);

            File defaultFile = new File(getDataFolder(), "default_arena.yml");
            YamlConfiguration defaultArenaConfig = YamlConfiguration.loadConfiguration(defaultFile);

            ConfigurationSection defaultMapSection = defaultArenaConfig.getConfigurationSection("default_map");

            if (defaultMapSection != null) {
                YamlConfiguration mainArenaConfig = YamlConfiguration.loadConfiguration(arenaFile);
                mainArenaConfig.set("arenas.DefaultBridge", defaultMapSection);

                try {
                    mainArenaConfig.save(arenaFile);
                    getLogger().info("Successfully initialized the 'DefaultBridge' map for first-time use.");
                } catch (IOException e) {
                    getLogger().severe("Failed to save default map data to arenas.yml: " + e.getMessage());
                }
            }
            defaultFile.delete();
        }
    }
}