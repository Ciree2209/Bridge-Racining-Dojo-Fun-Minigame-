package com.ciree.minigame.game;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import com.ciree.minigame.BridgeRacingDojo;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GhostRecorder extends BukkitRunnable {

    private final Player player;
    private final List<GhostLocation> path;
    private final long startTime;

    public GhostRecorder(BridgeRacingDojo plugin, Player player) {
        this.player = player;
        this.path = new ArrayList<>();
        this.startTime = System.currentTimeMillis();

        // Start running immediately, recording the path every tick (20 times per second)
        this.runTaskTimer(plugin, 0L, 1L);
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            // Stop task if player logs out
            this.cancel();
            return;
        }

        // Calculate the timestamp relative to the race start
        long timestamp = System.currentTimeMillis() - startTime;
        Location location = player.getLocation();

        path.add(new GhostLocation(
                location.getX(),
                location.getY(),
                location.getZ(),
                timestamp,
                location.getYaw(),
                location.getPitch()
        ));
    }

    /**
     * Stops the recording task and returns the recorded path.
     * This is called by ActiveRace.stop(true) upon race completion.
     * @return The list of recorded GhostLocations.
     */
    public List<GhostLocation> stopAndGetPath() {
        // Stop the BukkitRunnable task
        this.cancel();
        return path;
    }
}