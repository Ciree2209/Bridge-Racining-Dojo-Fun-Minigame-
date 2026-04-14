package com.ciree.minigame.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;

public class ScoreboardUtil {

    private final Scoreboard scoreboard;
    private final Objective objective;
    private final Player player;
    // Stores the colored text currently displayed at a score value.
    private final Map<Integer, String> scoreLines;

    public ScoreboardUtil(Player player, String title) {
        this.player = player;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.scoreLines = new HashMap<>();

        this.objective = scoreboard.registerNewObjective("race_stats", "dummy", title);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    /**
     * Sets or updates a line on the scoreboard.
     * @param score The numerical score/position (higher score is higher on the board).
     * @param text The text for the line (automatically colored).
     */
    public void setScore(int score, String text) {
        // Apply color formatting to the NEW text immediately
        String newColoredText = MessageUtil.colorize(text);

        // Retrieve the CURRENT colored text at this score position
        String currentColoredText = scoreLines.get(score);

        // Clean up old entry if the text is different
        // We compare the colored strings to ensure the scoreboard is reset correctly.
        if (currentColoredText != null && !currentColoredText.equals(newColoredText)) {
            // Remove the old entry before adding the new one
            // resetScores must be called with the exact, previously used string.
            scoreboard.resetScores(currentColoredText);
        }

        // Update map and set the new score (using the new COLORED text)
        scoreLines.put(score, newColoredText);
        objective.getScore(newColoredText).setScore(score);
    }

    /**
     * Displays the scoreboard to the player.
     */
    public void display() {
        if (player.isOnline()) {
            player.setScoreboard(this.scoreboard);
        }
    }

    /**
     * Removes the scoreboard, reverting the player to the main server scoreboard.
     */
    public void remove() {
        // Only remove if the player is online and currently viewing this specific scoreboard
        if (player.isOnline() && player.getScoreboard() != null && player.getScoreboard().equals(this.scoreboard)) {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }

    /**
     * Retrieves the underlying Bukkit Scoreboard object.
     */
    public Scoreboard getScoreboard() {
        return scoreboard;
    }
}