package com.gabrielhd.pirates.Tasks;

import com.gabrielhd.pirates.Database.Database;
import com.gabrielhd.pirates.Pirates;
import com.gabrielhd.pirates.Player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SaveTask implements Runnable {

    private final Pirates plugin;

    public SaveTask(Pirates plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        this.plugin.getArenaManager().saveArenas();

        for(Player player : Bukkit.getOnlinePlayers()) {
            PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player);
            if(playerData != null) {
                Database.getStorage().uploadPlayer(playerData);
            }
        }
    }
}
