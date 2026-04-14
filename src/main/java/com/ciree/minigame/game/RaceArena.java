package com.ciree.minigame.game;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class RaceArena {

    private final String name;
    private int maxPlayers;
    private Location startLocation;
    private Location finishLocation;

    private Material bridgingBlock;

    private final List<Location> placedBlocks;

    public RaceArena(String name) {
        this.name = name;
        this.maxPlayers = 1;
        this.startLocation = null;
        this.finishLocation = null;
        this.placedBlocks = new ArrayList<>();
        this.bridgingBlock = Material.WHITE_WOOL; // Default initial color
    }

    // Private constructor used by DataHandler.deserialize
    private RaceArena(String name, int maxPlayers, Location startLocation, Location finishLocation, Material bridgingBlock) {
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.startLocation = startLocation;
        this.finishLocation = finishLocation;
        this.placedBlocks = new ArrayList<>();
        this.bridgingBlock = bridgingBlock;
    }


    public static RaceArena deserialize(String name, ConfigurationSection section) {
        if (section == null) return new RaceArena(name);

        Location start = (Location) section.get("start-location");
        Location finish = (Location) section.get("finish-location");
        int max = section.getInt("max-players", 1);

        // Deserialize the bridging block type if you save it (optional, but good practice)
        // For simplicity, we keep the block dynamic and don't save it to file here,
        // but we ensure the constructor handles a default.

        return new RaceArena(name, max, start, finish, Material.WHITE_WOOL); // Use a default color on load
    }

    public void serialize(ConfigurationSection section) {
        section.set("max-players", maxPlayers);
        section.set("start-location", startLocation);
        section.set("finish-location", finishLocation);
        // We do NOT save the bridgingBlock here because players use different colors.
    }


    /**
     * Called by the RaceListener whenever a player places a bridging block.
     */
    public void addPlacedBlock(Location loc) {
        this.placedBlocks.add(loc);
    }

    /**
     * Clears all blocks placed by players and resets the list.
     */
    public void clearPlacedBlocks() {
        for (Location loc : placedBlocks) {
            // Set the block back to air, effectively deleting it
            if (loc != null && loc.getWorld() != null) {
                loc.getBlock().setType(Material.AIR);
            }
        }
        // Clear the list to free up memory for the next race
        placedBlocks.clear();
    }


    public String getName() {
        return name;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public Location getFinishLocation() {
        return finishLocation;
    }

    public Material getBridgingBlock() {
        return bridgingBlock;
    }

    /**
     * Getter for the list of temporary placed blocks (for internal/admin use).
     */
    public List<Location> getPlacedBlocks() {
        return placedBlocks;
    }


    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public void setFinishLocation(Location finishLocation) {
        this.finishLocation = finishLocation;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setBridgingBlock(Material bridgingBlock) {
        this.bridgingBlock = bridgingBlock;
    }
}