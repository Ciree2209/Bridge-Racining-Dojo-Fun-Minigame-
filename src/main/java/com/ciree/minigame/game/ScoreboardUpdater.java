package com.ciree.minigame.game;

import com.ciree.minigame.BridgeRacingDojo;
import com.ciree.minigame.util.MessageUtil;
import com.ciree.minigame.util.ScoreboardUtil;
import com.ciree.minigame.util.TimeUtil;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * A BukkitRunnable that updates the scoreboard for all active players
 * in an ActiveRace instance, showing the elapsed time.
 */
public class ScoreboardUpdater extends BukkitRunnable {

    private final BridgeRacingDojo plugin;
    private final ActiveRace race;

    // Defines the fixed score positions for static and dynamic lines
    private static final int TITLE_SCORE_POSITION = 14;
    private static final int TIMER_LABEL_SCORE_POSITION = 12; // Static line
    private static final int TIMER_VALUE_SCORE_POSITION = 11; // Dynamic line
    private static final int FOOTER_SCORE_POSITION = 1;

    public ScoreboardUpdater(BridgeRacingDojo plugin, ActiveRace race) {
        this.plugin = plugin;
        this.race = race;
    }

    @Override
    public void run() {
        if (!race.isActive()) {
            cancel();
            return;
        }

        long elapsedMillis = System.currentTimeMillis() - race.getStartTime();
        // The TimeUtil.formatTime already includes colors for good formatting
        String formattedTime = TimeUtil.formatTime(elapsedMillis);

        // Iterate over all currently active players in the race
        for (Player player : race.getPlayers()) {

            // Get or create the player's scoreboard instance
            ScoreboardUtil util = race.getScoreboard(player);

            if (util == null) {
                // Initial setup: If no scoreboard exists, create and display it
                String title = MessageUtil.colorize("&6&lBRIDGE DOJO");
                util = new ScoreboardUtil(player, title);
                race.setScoreboard(player, util); // Store it in ActiveRace map

                // Set up static lines (run once)
                util.setScore(TITLE_SCORE_POSITION, MessageUtil.colorize("&7&m-----------"));
                util.setScore(TIMER_LABEL_SCORE_POSITION, MessageUtil.colorize("&bTime:"));

                // Add a blank line for spacing
                util.setScore(TIMER_LABEL_SCORE_POSITION - 1, " ");

                // Add static map name line (assuming you want to show the map)
                util.setScore(TIMER_LABEL_SCORE_POSITION - 2, MessageUtil.colorize("&fMap: &e" + race.getArena().getName()));

                // Add another separator
                util.setScore(TIMER_LABEL_SCORE_POSITION - 3, MessageUtil.colorize("&7&m-----------"));

                util.setScore(FOOTER_SCORE_POSITION, MessageUtil.colorize("&7/dojo leave"));
                util.display();
            }

            // Note: Since TimeUtil.formatTime() produces unique text every millisecond,
            // setScore() should handle the update correctly by creating a new unique entry
            // and removing the old one, assuming your ScoreboardUtil is robust.

            // If your ScoreboardUtil only allows setScore(pos, text),
            // this is the required structure for dynamic content:
            util.setScore(TIMER_VALUE_SCORE_POSITION, formattedTime);
        }

        // Note: Race cancellation is handled by ActiveRace.stop() when the last player leaves/finishes.
    }
}