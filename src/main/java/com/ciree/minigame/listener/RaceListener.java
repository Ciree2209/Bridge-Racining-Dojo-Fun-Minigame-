package com.ciree.minigame.listener;

import com.ciree.minigame.BridgeRacingDojo;
import com.ciree.minigame.game.ActiveRace;
import com.ciree.minigame.game.RaceArena;
import com.ciree.minigame.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class RaceListener implements Listener {

    private final BridgeRacingDojo plugin;

    public RaceListener(BridgeRacingDojo plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ActiveRace race = plugin.getDojoManager().getActiveRace(player.getUniqueId());

        if (race == null || !race.isActive() || race.isFinished(player.getUniqueId())) {
            return;
        }

        RaceArena arena = race.getArena();

        if (player.getLocation().getY() < -10) {
            race.reset(player);
            return;
        }

        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                event.getFrom().getBlockY() == event.getTo().getBlockY() &&
                event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        if (arena.getFinishLocation() == null) return;

        Block blockFeet = event.getTo().getBlock().getRelative(0, -1, 0);

        Block finishBlock = arena.getFinishLocation().getBlock();

        if (blockFeet.getLocation().equals(finishBlock.getLocation())) {
            race.finish(player);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ActiveRace race = plugin.getDojoManager().getActiveRace(player.getUniqueId());

        if (race == null || !race.isActive()) {
            if (!player.hasPermission("bridgedojo.admin.bypass")) {
                event.setCancelled(true);
                player.sendMessage(MessageUtil.getMessage("error-no-place"));
            }
            return;
        }

        if (!event.getBlockPlaced().getType().toString().contains("_WOOL")) {
            event.setCancelled(true);
            player.sendMessage(MessageUtil.getMessage("error-only-wool"));
            return;
        }

        race.getArena().addPlacedBlock(event.getBlockPlaced().getLocation());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ActiveRace race = plugin.getDojoManager().getActiveRace(player.getUniqueId());

        if (race == null || !race.isActive()) {
            if (!player.hasPermission("bridgedojo.admin.bypass")) {
                event.setCancelled(true);
                player.sendMessage(MessageUtil.getMessage("error-no-break"));
            }
            return;
        }

        // Only allow players to break blocks they placed (wool)
        if (!event.getBlock().getType().toString().contains("_WOOL")) {
            event.setCancelled(true);
            player.sendMessage(MessageUtil.getMessage("error-only-wool-break"));
        }
    }
}