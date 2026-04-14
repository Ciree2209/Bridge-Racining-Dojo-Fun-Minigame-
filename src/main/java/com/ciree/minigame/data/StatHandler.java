package com.ciree.minigame.data;

import com.ciree.minigame.BridgeRacingDojo;
import com.ciree.minigame.util.MessageUtil;
import com.ciree.minigame.util.TimeUtil;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Handles the formatting and display of player statistics.
 */
public class StatHandler {

    private final BridgeRacingDojo plugin;

    // Line separators for display (Define these in MessageUtil or config if desired)
    private static final String SEPARATOR = MessageUtil.colorize("&8&m------------------------");
    private static final String LINE_BREAK = MessageUtil.colorize("&r");

    public StatHandler(BridgeRacingDojo plugin) {
        this.plugin = plugin;
    }

    public void sendPlayerStats(Player player) {
        UUID playerId = player.getUniqueId();
        DataHandler dataHandler = plugin.getDataHandler();

        String playerName = player.getName();
        int racesCompleted = dataHandler.getRacesCompleted(playerId);

        player.sendMessage(SEPARATOR);
        player.sendMessage(MessageUtil.colorize("&b&l" + playerName + "'s Stats &7--"));
        player.sendMessage(LINE_BREAK);
        player.sendMessage(MessageUtil.colorize("&6Races Completed: &f" + racesCompleted));
        player.sendMessage(LINE_BREAK);
        player.sendMessage(MessageUtil.colorize("&a--- Personal Bests ---"));

        Map<String, Long> bestTimes = getPlayerBestTimes(playerId);

        if (bestTimes.isEmpty()) {
            player.sendMessage(MessageUtil.colorize("&7You have no recorded best times. Get racing!"));
        } else {
            // Display each recorded PB
            for (Map.Entry<String, Long> entry : bestTimes.entrySet()) {
                String mapName = entry.getKey();
                long bestTime = entry.getValue();

                String formattedTime = TimeUtil.formatTime(bestTime);
                player.sendMessage(MessageUtil.colorize("&7- &e" + mapName + ": &a" + formattedTime));
            }
        }

        player.sendMessage(SEPARATOR);
    }

    public String getFormattedPersonalBest(UUID playerId, String mapName) {
        // We need to fetch the newly saved personal best time
        long newBestTime = plugin.getDataHandler().getPersonalBest(playerId, mapName);
        String formattedTime = TimeUtil.formatTime(newBestTime);

        // Format: >> Personal Best: 00:08:127 <<
        return MessageUtil.colorize(" &6&l>> &aPersonal Best: &e" + formattedTime + " &6&l<<");
    }

    private Map<String, Long> getPlayerBestTimes(UUID playerId) {
        Map<String, Long> bests = new HashMap<>();

        String mapName = "Map1";

        try {
            long time = plugin.getDataHandler().getPersonalBest(playerId, mapName);

            if (time != Long.MAX_VALUE) {
                bests.put(mapName, time);
            }

        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to load best times.");
        }

        return bests;
    }
}