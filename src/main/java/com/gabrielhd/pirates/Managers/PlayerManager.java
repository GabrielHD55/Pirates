package com.gabrielhd.pirates.Managers;

import com.gabrielhd.pirates.Database.Database;
import com.gabrielhd.pirates.Pirates;
import com.gabrielhd.pirates.Player.OPPlayer;
import com.gabrielhd.pirates.Player.PlayerData;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private final Pirates plugin;

    @Getter private final Map<UUID, PlayerData> players;
    @Getter private final Map<UUID, OPPlayer> opPlayers = Maps.newHashMap();
    
    public PlayerManager(Pirates plugin) {
        this.plugin = plugin;

        this.players = Maps.newHashMap();
    }
    
    public PlayerData getPlayerData(Player player) {
        return this.players.get(player.getUniqueId());
    }

    public OPPlayer getOPPlayer(Player player) {
        return this.opPlayers.get(player.getUniqueId());
    }
    
    public void removePlayer(Player player) {
        PlayerData playerData = this.getPlayerData(player);
        if (playerData != null) {
            if (playerData.getArena() != null) {
                playerData.getArena().removePlayer(player, true);
            }
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> Database.getStorage().uploadPlayer(playerData));
        }
        this.players.remove(player.getUniqueId());

        this.opPlayers.remove(player.getUniqueId());
    }
    
    public void createPlayer(Player player) {
        PlayerData playerData = new PlayerData(this.plugin, player);
        this.players.put(player.getUniqueId(), playerData);

        OPPlayer opPlayer = new OPPlayer(player);

        if(!this.opPlayers.containsKey(player.getUniqueId())) {
            this.opPlayers.put(player.getUniqueId(), opPlayer);
        }
    }
}
