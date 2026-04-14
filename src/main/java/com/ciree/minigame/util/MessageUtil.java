package com.ciree.minigame.util;

import com.ciree.minigame.BridgeRacingDojo;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class MessageUtil {

    private static FileConfiguration config;
    private static String prefix;

    public static void initialize() {
        // Use the static instance to get the configuration file
        config = BridgeRacingDojo.getInstance().getConfig();

        // Cache the prefix immediately to avoid repeated lookups
        // We use colorize here to translate the color codes for the prefix once.
        prefix = colorize(config.getString("messages.prefix", "&8[&bBridgeDojo&8] &7"));
    }

    /**
     * Retrieves a message from the config, prepends the cached prefix, and colorizes it.
     * @param path The path to the message in config.yml (e.g., "race-joined")
     * @return The formatted message string.
     */
    public static String getMessage(String path) {
        if (config == null) {
            // Fallback for fatal initialization failure
            BridgeRacingDojo.getInstance().getLogger().severe("MessageUtil was accessed before initialization!");
            return ChatColor.RED + "[BridgeDojo] Fatal Error: Config not loaded!";
        }

        // Lookup the message string from the config
        String message = config.getString("messages." + path);

        if (message == null) {
            // Report the missing message key clearly, as seen in your errors
            BridgeRacingDojo.getInstance().getLogger().severe("Message path 'messages." + path + "' not found in config.yml!");
            return colorize(prefix + "&cError: Message path '" + path + "' not found.");
        }

        // Return the colorized message with the cached prefix
        return colorize(prefix + message);
    }

    /**
     * Translates standard Minecraft color codes.
     */
    public static String colorize(String message) {
        if (message == null) return "";
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}