package com.ciree.minigame.game;

import java.io.Serializable;

public class GhostLocation implements Serializable {

    private static final long serialVersionUID = 1L;

    private final double x, y, z;
    private final long timestamp; // Time in milliseconds since race start
    private final float yaw, pitch; // yaw and pitch fields MUST exist

    public GhostLocation(double x, double y, double z, long timestamp, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.timestamp = timestamp;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    // --- Getters ---
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public long getTimestamp() { return timestamp; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; } // Ensure this getter exists
}