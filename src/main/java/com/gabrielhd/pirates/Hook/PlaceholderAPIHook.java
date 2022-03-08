package com.gabrielhd.pirates.Hook;

import me.clip.placeholderapi.expansion.*;
import org.bukkit.entity.*;
import com.gabrielhd.pirates.*;
import com.gabrielhd.pirates.Player.*;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final Pirates plugin;

    public PlaceholderAPIHook(Pirates plugin) {
        this.plugin = plugin;
    }

    @Override
    public String onPlaceholderRequest(Player player, String s) {
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player);
        if (player == null || playerData == null) {
            return "";
        }
        if (s.equals("wins")) {
            return String.valueOf(playerData.getWins());
        }
        if (s.equals("kills")) {
            return String.valueOf(playerData.getKills());
        }
        if (s.equals("played")) {
            return String.valueOf(playerData.getPlayed());
        }
        if (s.equals("deaths")) {
            return String.valueOf(playerData.getDeaths());
        }
        if (s.equals("level")) {
            return String.valueOf(playerData.getLevel());
        }
        if (s.equals("exp")) {
            return String.valueOf(playerData.getExp());
        }
        return "";
    }
    
    public String getIdentifier() {
        return "pirates";
    }
    
    public String getAuthor() {
        return "GabrielHD55";
    }
    
    public String getVersion() {
        return this.plugin.getDescription().getVersion();
    }
    
    public boolean canRegister() {
        return true;
    }
    
    public boolean persist() {
        return true;
    }
}
