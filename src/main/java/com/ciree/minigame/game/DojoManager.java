package com.ciree.minigame.game;

import com.ciree.minigame.BridgeRacingDojo;
import com.ciree.minigame.util.MessageUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class DojoManager {

    private final BridgeRacingDojo plugin;
    // Map to track all active races by arena name (public for ActiveRace cleanup)
    public final Map<String, ActiveRace> activeRaces = new HashMap<>();
    // Map to track which race a player is currently in (using ActiveRace object for quick access)
    private final Map<UUID, ActiveRace> playersInRace = new HashMap<>();

    // List of wool colors for assigning unique player colors
    private static final List<Material> WOOL_COLORS = List.of(
            Material.WHITE_WOOL, Material.ORANGE_WOOL, Material.MAGENTA_WOOL,
            Material.LIGHT_BLUE_WOOL, Material.YELLOW_WOOL, Material.LIME_WOOL,
            Material.PINK_WOOL, Material.GRAY_WOOL, Material.CYAN_WOOL,
            Material.PURPLE_WOOL, Material.BLUE_WOOL, Material.BROWN_WOOL,
            Material.GREEN_WOOL, Material.RED_WOOL, Material.BLACK_WOOL
    );

    public DojoManager(BridgeRacingDojo plugin) {
        this.plugin = plugin;
    }

    public ActiveRace getActiveRace(UUID playerId) { return playersInRace.get(playerId); }
    public ActiveRace getActiveRace(String arenaName) { return activeRaces.get(arenaName); }

    private void removePlayerFromRaceMap(UUID playerId) { playersInRace.remove(playerId); }


    public void joinRace(Player player, String mapName) {
        RaceArena arena = plugin.getDataHandler().getArena(mapName);
        if (arena == null) {
            player.sendMessage(MessageUtil.getMessage("error-invalid-map").replace("%map_name%", mapName));
            return;
        }
        if (playersInRace.containsKey(player.getUniqueId())) {
            player.sendMessage(MessageUtil.getMessage("already-racing"));
            return;
        }

        ActiveRace race = getActiveRace(arena.getName());
        if (race == null) {
            race = new ActiveRace(plugin, arena);
            activeRaces.put(arena.getName(), race);
        }

        int playerIndex = race.getPlayers().size();
        int colorIndex = playerIndex % WOOL_COLORS.size();
        Material woolMaterial = WOOL_COLORS.get(colorIndex);

        arena.setBridgingBlock(woolMaterial);

        race.addPlayer(player);
        playersInRace.put(player.getUniqueId(), race);

        applyRaceKit(player, woolMaterial);

        if (arena.getStartLocation() != null) {
            player.teleport(arena.getStartLocation());
            player.setGameMode(GameMode.SURVIVAL); // Use SURVIVAL for block placing/breaking

            player.sendMessage(MessageUtil.getMessage("race-joined").replace("%map_name%", mapName));
            race.startIfReady();
        } else {
            plugin.getLogger().log(Level.WARNING, "Attempted to join map " + mapName + " but start location is null.");
            player.sendMessage(MessageUtil.colorize("&cError: Map is not fully configured."));
        }
    }

    public void leaveRace(Player player) {
        ActiveRace race = getActiveRace(player.getUniqueId());

        if (race == null) {
            player.sendMessage(MessageUtil.getMessage("error-no-race"));
            return;
        }

        // Delegate most cleanup back to ActiveRace, which then calls removePlayer(Player) below
        race.removePlayer(player);

        player.sendMessage(MessageUtil.getMessage("race-left"));
    }

    /**
     * Applies the required kit to the player's inventory for bridging.
     */
    public void applyRaceKit(Player player, Material woolMaterial) {
        player.getInventory().clear();

        ItemStack wool = new ItemStack(woolMaterial, 64);
        // Give 4 stacks of wool for competitive bridging
        for (int i = 0; i < 4; i++) {
            player.getInventory().setItem(i, wool);
        }

        // Give tool for breaking placed blocks
        ItemStack tool = new ItemStack(Material.SHEARS);
        player.getInventory().setItem(8, tool); // Place in last slot

        player.updateInventory();
    }

    /**
     * Centralized cleanup method (called by ActiveRace after they are removed from the race object)
     * Resets player state to default.
     */
    public void removePlayer(Player player) {
        player.teleport(player.getWorld().getSpawnLocation());
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();

        removePlayerFromRaceMap(player.getUniqueId());
    }

    public void endAllRaces() {
        for (ActiveRace race : activeRaces.values()) {
            race.stop(false);
        }
        activeRaces.clear();
        playersInRace.clear();
    }
}

