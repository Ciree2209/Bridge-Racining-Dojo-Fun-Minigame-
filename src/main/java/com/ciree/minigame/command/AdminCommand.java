package com.ciree.minigame.command;

import com.ciree.minigame.BridgeRacingDojo;
import com.ciree.minigame.game.RaceArena;
import com.ciree.minigame.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.Map;

public class AdminCommand implements CommandExecutor {

    private final BridgeRacingDojo plugin;

    public AdminCommand(BridgeRacingDojo plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtil.colorize("&cOnly players can use admin commands."));
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("bridgedojo.admin")) {
            player.sendMessage(MessageUtil.colorize("&cYou do not have permission to use this command."));
            return true;
        }

        if (args.length < 1) {
            sendHelpMessage(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        // Check if mapName is provided for commands that require it
        String mapName = (args.length > 1) ? args[1] : null;

        switch (subCommand) {
            case "create":
                if (mapName == null) {
                    player.sendMessage(MessageUtil.colorize("&cUsage: /dojo admin create <mapName>"));
                    return true;
                }

                // Check if map already exists
                if (plugin.getDataHandler().getArena(mapName) != null) {
                    player.sendMessage(MessageUtil.colorize("&cError: A map with that name already exists."));
                    return true;
                }

                RaceArena arena = new RaceArena(mapName);

                plugin.getDataHandler().getLoadedArenas().put(mapName, arena);
                plugin.getDataHandler().saveArena(arena);

                String createMessage = MessageUtil.getMessage("admin-map-created").replace("%map_name%", mapName);
                player.sendMessage(createMessage);
                return true;

            case "delete":
                if (mapName == null) {
                    player.sendMessage(MessageUtil.colorize("&cUsage: /dojo admin delete <mapName>"));
                    return true;
                }

                if (plugin.getDataHandler().getArena(mapName) == null) {
                    player.sendMessage(MessageUtil.getMessage("error-invalid-map").replace("%map_name%", mapName));
                    return true;
                }

                plugin.getDataHandler().deleteArena(mapName);

                String deleteMessage = MessageUtil.getMessage("admin-map-deleted").replace("%map_name%", mapName);
                player.sendMessage(deleteMessage);
                return true;

            case "startset":
            case "finishset":
                if (mapName == null) {
                    player.sendMessage(MessageUtil.colorize("&cUsage: /dojo admin " + subCommand + " <mapName>"));
                    return true;
                }

                RaceArena targetArena = plugin.getDataHandler().getArena(mapName);
                if (targetArena == null) {
                    player.sendMessage(MessageUtil.getMessage("error-invalid-map").replace("%map_name%", mapName));
                    return true;
                }

                if (subCommand.equals("startset")) {
                    targetArena.setStartLocation(player.getLocation());
                    player.sendMessage(MessageUtil.getMessage("admin-start-set").replace("%map_name%", mapName));
                } else {
                    targetArena.setFinishLocation(player.getLocation());
                    player.sendMessage(MessageUtil.getMessage("admin-finish-set").replace("%map_name%", mapName));
                }

                plugin.getDataHandler().saveArena(targetArena);
                return true;

            case "setmaxplayers":
                if (args.length < 3) {
                    player.sendMessage(MessageUtil.colorize("&cUsage: /dojo admin setmaxplayers <mapName> <number>"));
                    return true;
                }

                String maxPlayersArg = args[2];
                int maxPlayers;
                try {
                    maxPlayers = Integer.parseInt(maxPlayersArg);
                } catch (NumberFormatException e) {
                    player.sendMessage(MessageUtil.getMessage("error-invalid-number"));
                    return true;
                }

                RaceArena maxArena = plugin.getDataHandler().getArena(mapName);
                if (maxArena == null) {
                    player.sendMessage(MessageUtil.getMessage("error-invalid-map").replace("%map_name%", mapName));
                    return true;
                }

                maxArena.setMaxPlayers(maxPlayers);
                plugin.getDataHandler().saveArena(maxArena);

                player.sendMessage(MessageUtil.getMessage("admin-max-players-set").replace("%max_players%", String.valueOf(maxPlayers)).replace("%map_name%", mapName));
                return true;

            case "reload":
                // Cleanup current races before reload
                plugin.getDojoManager().endAllRaces();

                // Reload data and configuration
                plugin.reloadConfig();
                plugin.getDataHandler().loadAllArenas(); // Reload map data

                player.sendMessage(MessageUtil.colorize("&aPlugin configuration and arena data reloaded."));
                return true;

            default:
                sendHelpMessage(player);
                return true;
        }
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage(MessageUtil.colorize("&b--- Bridge Racing Dojo Admin Help ---"));
        player.sendMessage(MessageUtil.colorize("&e/dojo admin create <name> &7- Create new map."));
        player.sendMessage(MessageUtil.colorize("&e/dojo admin delete <name> &7- Delete a map."));
        player.sendMessage(MessageUtil.colorize("&e/dojo admin startset <name> &7- Set start location."));
        player.sendMessage(MessageUtil.colorize("&e/dojo admin finishset <name> &7- Set finish location."));
        player.sendMessage(MessageUtil.colorize("&e/dojo admin setmaxplayers <name> <num> &7- Set player limit."));
        player.sendMessage(MessageUtil.colorize("&e/dojo admin reload &7- Reload config and arena data."));
    }
}