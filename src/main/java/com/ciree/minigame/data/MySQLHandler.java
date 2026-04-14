package com.ciree.minigame.data;

import com.ciree.minigame.BridgeRacingDojo;
import com.ciree.minigame.game.RaceArena;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;

public class MySQLHandler {

    private final BridgeRacingDojo plugin;
    private Connection connection;

    public MySQLHandler(BridgeRacingDojo plugin) {
        this.plugin = plugin;
        initializeConnection();
        if (connection != null) {
            createTables();
        }
    }

    private void initializeConnection() {
        FileConfiguration config = plugin.getConfig();
        String host = config.getString("database.host");
        int port = config.getInt("database.port");
        String db = config.getString("database.database");
        String user = config.getString("database.username");
        String password = config.getString("database.password");

        try {
            if (connection != null && !connection.isClosed()) {
                return;
            }

            // Connection String
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", user, password);
            plugin.getLogger().info("Successfully connected to MySQL database!");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not connect to MySQL database!", e);
        } catch (ClassNotFoundException e) {
            plugin.getLogger().log(Level.SEVERE, "MySQL JDBC Driver not found!", e);
        }
    }

    private void createTables() {
        if (connection == null) return;

        try (Statement stmt = connection.createStatement()) {
            String arenaTable = "CREATE TABLE IF NOT EXISTS brd_arenas (" +
                    "name VARCHAR(64) PRIMARY KEY," +
                    "max_players INT NOT NULL DEFAULT 1," +
                    "start_location TEXT," + // Location will be serialized
                    "finish_location TEXT" + // Location will be serialized
                    ");";
            stmt.execute(arenaTable);

            String playerTable = "CREATE TABLE IF NOT EXISTS brd_player_data (" +
                    "uuid CHAR(36) PRIMARY KEY," +
                    "races_completed INT NOT NULL DEFAULT 0," +
                    "best_times_json TEXT" + // Stores MapName -> BestTimeMs as JSON
                    ");";
            stmt.execute(playerTable);

            plugin.getLogger().info("MySQL tables verified/created.");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to create MySQL tables!", e);
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                plugin.getLogger().info("MySQL connection closed.");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection!", e);
        }
    }


    // Converts Bukkit Location to Base64 String
    public static String locationToBase64(Location location) throws IllegalStateException {
        if (location == null) return null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
            dataOutput.writeObject(location);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to serialize location!", e);
        }
    }

    // Converts Base64 String to Bukkit Location
    public static Location locationFromBase64(String data) throws IOException, ClassNotFoundException {
        if (data == null || data.isEmpty()) return null;
        byte[] serializedObject = Base64.getDecoder().decode(data);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(serializedObject);
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
            return (Location) dataInput.readObject();
        }
    }

    // The rest of the MySQL CRUD methods (getPlayerData, savePlayerData, etc.) will be in DataHandler.
    public Connection getConnection() {
        return connection;
    }
}