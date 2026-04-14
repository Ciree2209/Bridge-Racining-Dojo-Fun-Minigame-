package com.ciree.minigame.util;

import java.util.concurrent.TimeUnit;

public class TimeUtil {

    private TimeUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Converts a duration in milliseconds into a beautifully formatted string.
     * Format: &bMM&f:&bSS&7.&fMMM
     * * @param millis The duration in milliseconds.
     * @return The formatted time string with color codes.
     */
    public static String formatTime(long millis) {
        if (millis < 0) {
            millis = 0;
        }

        // Calculate components
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) -
                TimeUnit.MINUTES.toSeconds(minutes);
        long milliseconds = millis % 1000;

        // Format components with leading zeros
        String mm = String.format("%02d", minutes);
        String ss = String.format("%02d", seconds);
        String mmm = String.format("%03d", milliseconds);

        // Final formatted string
        return MessageUtil.colorize(
                "&b" + mm +
                        "&f:" +
                        "&b" + ss +
                        "&7." +
                        "&f" + mmm
        );
    }

    /**
     * Helper method to convert a formatted time string back to milliseconds.
     * This might be useful for loading personal bests from config/database
     * but is not used by the current game logic.
     */
    public static long parseTime(String formattedTime) {
        // This is a placeholder for a complex implementation,
        // as parsing back with colors would be very difficult.
        // It's much better to save times as raw Long (millis) in the data handler.
        return 0;
    }
}