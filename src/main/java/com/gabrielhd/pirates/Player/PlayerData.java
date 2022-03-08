package com.gabrielhd.pirates.Player;

import org.bukkit.entity.*;
import java.util.*;
import com.gabrielhd.pirates.Arena.*;
import com.gabrielhd.pirates.Config.*;
import org.bukkit.*;
import com.gabrielhd.pirates.*;
import com.gabrielhd.pirates.Database.*;

public class PlayerData
{
    private final Player player;
    private final UUID uuid;
    private Arena arena;
    private PlayerState state;
    private int wins;
    private int played;
    private int deaths;
    private int kills;
    private int lives;
    private int level;
    private double exp;
    private long delay;
    private long lastDamage;
    private Player lastPlayer;
    
    public PlayerData(Pirates plugin, Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
        this.state = PlayerState.LOBBY;
        this.arena = null;
        this.wins = 0;
        this.played = 0;
        this.deaths = 0;
        this.kills = 0;
        this.level = 1;
        this.exp = 10.0;
        this.delay = 0L;
        this.lives = new YamlConfig(plugin, "Settings").getInt("Settings.Lives", 1);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> Database.getStorage().loadPlayer(this));
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public UUID getUuid() {
        return this.uuid;
    }
    
    public Arena getArena() {
        return this.arena;
    }
    
    public void setArena(Arena arena) {
        this.arena = arena;
    }
    
    public PlayerState getState() {
        return this.state;
    }
    
    public void setState(PlayerState state) {
        this.state = state;
    }
    
    public int getWins() {
        return this.wins;
    }
    
    public void setWins(int wins) {
        this.wins = wins;
    }
    
    public int getPlayed() {
        return this.played;
    }
    
    public void setPlayed(int played) {
        this.played = played;
    }
    
    public int getDeaths() {
        return this.deaths;
    }
    
    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }
    
    public int getKills() {
        return this.kills;
    }
    
    public void setKills(int kills) {
        this.kills = kills;
    }
    
    public int getLives() {
        return this.lives;
    }
    
    public void setLives(int lives) {
        this.lives = lives;
    }
    
    public int getLevel() {
        return this.level;
    }
    
    public void setLevel(int level) {
        this.level = level;
    }
    
    public double getExp() {
        return this.exp;
    }
    
    public void setExp(double exp) {
        this.exp = exp;
    }
    
    public long getDelay() {
        return this.delay;
    }
    
    public void setDelay(long delay) {
        this.delay = delay;
    }
    
    public long getLastDamage() {
        return this.lastDamage;
    }
    
    public void setLastDamage(long lastDamage) {
        this.lastDamage = lastDamage;
    }
    
    public Player getLastPlayer() {
        return this.lastPlayer;
    }
    
    public void setLastPlayer(Player lastPlayer) {
        this.lastPlayer = lastPlayer;
    }
}
