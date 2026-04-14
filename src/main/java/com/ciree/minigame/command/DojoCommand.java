package com.ciree.minigame.command;

import com.ciree.minigame.BridgeRacingDojo;
import com.ciree.minigame.game.RaceArena;
import com.ciree.minigame.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class DojoCommand implements CommandExecutor {

    private final BridgeRacingDojo plugin;

    public DojoCommand(BridgeRacingDojo plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can execute dojo commands.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            sendAvailableArenas(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "join":
                if (args.length < 2) {
                    player.sendMessage(MessageUtil.colorize("&cUsage: /dojo join <mapName>"));
                    return true;
                }
                plugin.getDojoManager().joinRace(player, args[1]);
                return true;

            case "leave":
                plugin.getDojoManager().leaveRace(player);
                return true;

            case "stats":
                plugin.getStatHandler().sendPlayerStats(player);
                return true;

            case "admin":
                // Note: The AdminCommand constructor expects the full command arguments array.
                // We use Arrays.copyOfRange to pass the arguments *after* "admin"
                return new AdminCommand(plugin).onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));

            default:
                sendAvailableArenas(player);
                return true;
        }
    }

    /**
     * Sends the list of available (fully configured) race maps to the player.
     */
    private void sendAvailableArenas(Player player) {
        // Calls the DataHandler method that returns Set<RaceArena>
        Set<RaceArena> arenas = plugin.getDataHandler().getLoadedArenaValues();

        player.sendMessage(MessageUtil.colorize("&8&m----------"));
        player.sendMessage(MessageUtil.colorize("&6&lAvailable Race Maps &8(&e" + arenas.size() + "&8)"));
        player.sendMessage(MessageUtil.colorize("&8&m----------"));

        if (arenas.isEmpty()) {
            player.sendMessage(MessageUtil.colorize("&7No maps are currently loaded."));
            return;
        }

        String mapList = arenas.stream()
                // Filter only maps that have both start and finish set (fully configured)
                .filter(arena -> arena.getStartLocation() != null && arena.getFinishLocation() != null)
                .map(arena -> MessageUtil.colorize(
                        "&a" + arena.getName() +
                                " &7(&fMax Players: " + arena.getMaxPlayers() + "&7)"
                ))
                .collect(Collectors.joining(MessageUtil.colorize("&f, ")));

        if (mapList.isEmpty()) {
            player.sendMessage(MessageUtil.colorize("&7No fully configured maps are available to join."));
        } else {
            player.sendMessage(mapList);
        }
    }
}