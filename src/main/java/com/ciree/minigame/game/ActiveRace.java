package com.ciree.minigame.game;

import com.ciree.minigame.BridgeRacingDojo;
import com.ciree.minigame.util.MessageUtil;
import com.ciree.minigame.util.ScoreboardUtil;
import com.ciree.minigame.util.TimeUtil;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class ActiveRace {

    private final BridgeRacingDojo plugin;
    private final RaceArena arena;

    private final Set<UUID> players;
    private final Map<UUID, Long> finishTimes;

    private final Map<UUID, ScoreboardUtil> playerBoards;
    private BukkitTask scoreboardTask;

    private boolean isRunning;
    private long startTime;

    public ActiveRace(BridgeRacingDojo plugin, RaceArena arena) {
        this.plugin = plugin;
        this.arena = arena;
        this.players = new HashSet<>();
        this.finishTimes = new HashMap<>();
        this.playerBoards = new HashMap<>();
        this.isRunning = false;
        this.startTime = 0;
    }

    public Collection<Player> getPlayers() {
        Set<Player> activePlayers = new HashSet<>();
        for (UUID uuid : players) {
            Player p = plugin.getServer().getPlayer(uuid);
            if (p != null && p.isOnline()) {
                activePlayers.add(p);
            }
        }
        return activePlayers;
    }

    public long getStartTime() { return startTime; }
    public RaceArena getArena() { return arena; }
    public boolean isActive() { return isRunning; }
    public boolean isFinished(UUID playerId) { return finishTimes.containsKey(playerId); }

    public ScoreboardUtil getScoreboard(Player player) {
        return playerBoards.get(player.getUniqueId());
    }

    public void setScoreboard(Player player, ScoreboardUtil util) {
        playerBoards.put(player.getUniqueId(), util);
    }


    public void addPlayer(Player player) {
        this.players.add(player.getUniqueId());
    }

    public void startIfReady() {
        if (isRunning) return;

        if (!players.isEmpty()) {
            plugin.getLogger().log(Level.INFO, "Race on " + arena.getName() + " starting.");
            this.isRunning = true;
            this.startTime = System.currentTimeMillis();

            this.scoreboardTask = new ScoreboardUpdater(plugin, this).runTaskTimer(plugin, 0L, 5L);
        }
    }

    public void finish(Player player) {
        UUID playerId = player.getUniqueId();

        if (!isRunning || finishTimes.containsKey(playerId)) return;

        long raceDuration = System.currentTimeMillis() - this.startTime;
        finishTimes.put(playerId, raceDuration);

        String formattedTime = TimeUtil.formatTime(raceDuration);
        long currentBest = plugin.getDataHandler().getPersonalBest(playerId, arena.getName());
        String rankText; // This variable holds the content for the %rank% placeholder

        if (raceDuration < currentBest) {
            // New Personal Best!
            plugin.getDataHandler().savePersonalBest(playerId, arena.getName(), raceDuration);

            rankText = plugin.getStatHandler().getFormattedPersonalBest(playerId, arena.getName());

            player.sendMessage(MessageUtil.getMessage("race-record-beat")
                    .replace("%map_name%", arena.getName())
                    .replace("%time%", formattedTime)
            );
        } else {
            // Not a PB, use the simple separator text defined in config.yml (e.g., "-----")
            rankText = MessageUtil.getMessage("personal-best-unbeaten");
        }

        // Send the primary finish message
        player.sendMessage(
                MessageUtil.getMessage("race-finished")
                        .replace("%time%", formattedTime)
                        .replace("%rank%", rankText)
        );

        // Cleanup
        ScoreboardUtil util = playerBoards.remove(playerId);
        if (util != null) {
            util.remove();
        }

        this.players.remove(playerId);
        plugin.getDojoManager().removePlayer(player);

        if (players.isEmpty()) {
            stop(true);
        }
    }

    public void reset(Player player) {
        if (arena.getStartLocation() != null) {
            // Reapply kit (assumes first item in inventory is wool)
            plugin.getDojoManager().applyRaceKit(player, player.getInventory().getItem(0).getType());

            player.teleport(arena.getStartLocation());
            player.sendMessage(MessageUtil.getMessage("race-fell-reset"));
        } else {
            player.sendMessage(MessageUtil.colorize("&cMap start location missing. Cannot reset."));
        }
    }

    public void removePlayer(Player player) {
        UUID playerId = player.getUniqueId();

        ScoreboardUtil util = playerBoards.remove(playerId);
        if (util != null) {
            util.remove();
        }

        this.players.remove(playerId);
        plugin.getDojoManager().removePlayer(player);

        if (players.isEmpty()) {
            stop(false);
        }
    }

    public void stop(boolean completed) {
        if (!isRunning && players.isEmpty()) return;
        this.isRunning = false;

        if (scoreboardTask != null) {
            scoreboardTask.cancel();
            scoreboardTask = null;
        }

        for (UUID playerId : new HashSet<>(players)) {
            Player player = plugin.getServer().getPlayer(playerId);
            if (player != null && player.isOnline()) {
                ScoreboardUtil util = playerBoards.remove(playerId);
                if (util != null) util.remove();
                plugin.getDojoManager().removePlayer(player);
            }
        }

        arena.clearPlacedBlocks();

        players.clear();
        finishTimes.clear();
        playerBoards.clear();

        plugin.getDojoManager().activeRaces.remove(arena.getName());
    }
}