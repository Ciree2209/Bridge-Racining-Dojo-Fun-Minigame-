package com.ciree.minigame.listener;

import com.ciree.minigame.BridgeRacingDojo;
import com.ciree.minigame.game.ActiveRace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GeneralListener implements Listener {

    private final BridgeRacingDojo plugin;

    public GeneralListener(BridgeRacingDojo plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ActiveRace race = plugin.getDojoManager().getActiveRace(player.getUniqueId());

        if (race != null) {
            // Force-stop the race without sending a message (as they are logging out)
            race.stop(false);
            plugin.getLogger().info("Forcibly ended race for " + player.getName() + " due to quit.");
        }
    }
}