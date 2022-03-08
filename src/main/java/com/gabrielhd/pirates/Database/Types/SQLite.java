package com.gabrielhd.pirates.Database.Types;

import com.gabrielhd.pirates.Database.*;
import com.gabrielhd.pirates.Config.*;
import com.gabrielhd.pirates.*;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;

import org.bukkit.entity.*;
import com.gabrielhd.pirates.Player.*;

public class SQLite implements DataHandler
{
    private final String table;
    private Connection connection;
    
    public SQLite(Pirates plugin) {
        this.table = new YamlConfig(plugin,"Settings").getString("MySQL.TableName");
        this.connect(plugin);
        this.setup(plugin);
    }
    
    private synchronized void connect(Pirates plugin) {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/Database.db");
        } catch (SQLException | ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "Can't initialize database connection! Please check your configuration!");
            plugin.getLogger().log(Level.SEVERE, "If this error persists, please report it to the developer!");

            ex.printStackTrace();
        }
    }
    
    private synchronized void setup(Pirates plugin) {
        try {
            Statement statement = this.connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + this.table + " (UUID VARCHAR(100))");
            DatabaseMetaData dm = this.connection.getMetaData();
            ResultSet wins = dm.getColumns(null, null, this.table, "Wins");
            if (!wins.next()) {
                statement.executeUpdate("ALTER TABLE " + this.table + " ADD COLUMN Wins int AFTER UUID;");
            }
            wins.close();
            ResultSet kills = dm.getColumns(null, null, this.table, "Kills");
            if (!kills.next()) {
                statement.executeUpdate("ALTER TABLE " + this.table + " ADD COLUMN Kills double AFTER Wins;");
            }
            kills.close();
            ResultSet losses = dm.getColumns(null, null, this.table, "Deaths");
            if (!losses.next()) {
                statement.executeUpdate("ALTER TABLE " + this.table + " ADD COLUMN Deaths int AFTER Kills;");
            }
            losses.close();
            ResultSet played = dm.getColumns(null, null, this.table, "Played");
            if (!played.next()) {
                statement.executeUpdate("ALTER TABLE " + this.table + " ADD COLUMN Played int AFTER Deaths;");
            }
            played.close();
            ResultSet level = dm.getColumns(null, null, this.table, "Level");
            if (!level.next()) {
                statement.executeUpdate("ALTER TABLE " + this.table + " ADD COLUMN Level int AFTER Played;");
            }
            level.close();
            ResultSet exp = dm.getColumns(null, null, this.table, "Exp");
            if (!exp.next()) {
                statement.executeUpdate("ALTER TABLE " + this.table + " ADD COLUMN Exp int AFTER Level;");
            }
            exp.close();
            statement.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error inserting columns! Please check your configuration!");
            plugin.getLogger().log(Level.SEVERE, "If this error persists, please report it to the developer!");

            e.printStackTrace();
        }

        plugin.getLogger().log(Level.INFO, "SQLite Setup finished");
    }
    
    public boolean playerExists(UUID uuid) {
        try {
            ResultSet rs = this.query("SELECT * FROM " + this.table + " WHERE UUID='" + uuid.toString() + "'");
            return rs != null && rs.next() && rs.getString("UUID") != null;
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public void createPlayer(Player player) {
        if (this.connection != null) {
            try {
                if (!this.playerExists(player.getUniqueId())) {
                    this.connection.createStatement().executeUpdate("INSERT INTO " + this.table + " (UUID, Wins, Kills, Deaths, Played, Level, Exp) VALUES ('" + player.getUniqueId() + "', '0', '0', '0', '0', '1', '10');");
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public ResultSet query(String query) {
        try {
            Statement stmt = this.connection.createStatement();
            return stmt.executeQuery(query);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public void loadPlayer(PlayerData playerData) {
        ResultSet resultSet = this.query("SELECT * FROM '" + this.table + "' WHERE UUID='" + playerData.getUuid() + "'");
        try {
            if (resultSet != null && resultSet.next()) {
                playerData.setWins(resultSet.getInt("Wins"));
                playerData.setKills(resultSet.getInt("Kills"));
                playerData.setDeaths(resultSet.getInt("Deaths"));
                playerData.setPlayed(resultSet.getInt("Played"));
                playerData.setLevel(resultSet.getInt("Level"));
                playerData.setExp(resultSet.getDouble("Exp"));
            }
            else {
                this.createPlayer(playerData.getPlayer());
            }
        }
        catch (SQLException ex) {
            this.createPlayer(playerData.getPlayer());
        }
    }
    
    @Override
    public void uploadPlayer(PlayerData playerData) {
        if (this.connection != null) {
            try {
                this.connection.createStatement().executeUpdate("UPDATE " + this.table + " SET Wins='" + playerData.getWins() + "', Kills='" + playerData.getKills() + "', Deaths='" + playerData.getDeaths() + "', Played='" + playerData.getPlayed() + "', Level='" + playerData.getLevel() + "', Exp='" + playerData.getExp() + "' WHERE UUID='" + playerData.getUuid() + "';");
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void close() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
