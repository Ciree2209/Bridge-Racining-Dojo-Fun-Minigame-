package com.ciree.minigame.command;

import com.ciree.minigame.BridgeRacingDojo;
import com.ciree.minigame.game.RaceArena;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DojoTabCompleter implements TabCompleter {

    private final BridgeRacingDojo plugin;

    private static final List<String> MAP_COMMANDS = Arrays.asList(
            "join", "delete", "startset", "finishset", "setmaxplayers"
    );

    public DojoTabCompleter(BridgeRacingDojo plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>(Arrays.asList("join", "leave"));
            if (sender.hasPermission("bridgedojo.admin")) {
                completions.add("admin");
            }
            return filterList(completions, args[0]);
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("join")) {
                return getArenaNames(args[1]);
            }

            if (args[0].equalsIgnoreCase("admin") && sender.hasPermission("bridgedojo.admin")) {
                return filterList(Arrays.asList("create", "delete", "startset", "finishset", "setmaxplayers", "reload"), args[1]);
            }
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("admin") && sender.hasPermission("bridgedojo.admin") &&
                    MAP_COMMANDS.contains(args[1].toLowerCase())) {
                return getArenaNames(args[2]);
            }
        }

        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("admin") && sender.hasPermission("bridgedojo.admin") &&
                    args[1].equalsIgnoreCase("setmaxplayers")) {
                return filterList(Arrays.asList("1", "2", "4", "8"), args[3]);
            }
        }

        return new ArrayList<>();
    }

    /**
     * Helper to get a list of all loaded arena names.
     */
    private List<String> getArenaNames(String partial) {
        // Relies on plugin.getDataHandler().getLoadedArenas() returning a Map<String, RaceArena>
        return plugin.getDataHandler().getLoadedArenas().keySet().stream()
                .filter(name -> name.toLowerCase().startsWith(partial.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Helper to filter a list based on a partial input.
     */
    private List<String> filterList(List<String> list, String partial) {
        return list.stream()
                .filter(s -> s.toLowerCase().startsWith(partial.toLowerCase()))
                .collect(Collectors.toList());
    }
}