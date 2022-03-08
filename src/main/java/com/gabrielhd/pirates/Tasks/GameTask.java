package com.gabrielhd.pirates.Tasks;

import com.gabrielhd.pirates.Arena.Arena;
import com.gabrielhd.pirates.Arena.ArenaState;
import com.gabrielhd.pirates.Config.YamlConfig;
import com.gabrielhd.pirates.Pirates;
import com.gabrielhd.pirates.Player.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameTask extends BukkitRunnable {

    private final Pirates plugin;
    private final Arena arena;

    public GameTask(Pirates plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;

        this.runTaskTimer(plugin, 0L, 20L);
    }

    @Override
    public void run() {
        ArenaState currentState = this.arena.getState();

        if(currentState != ArenaState.PLAYING) {
            this.cancel();
            return;
        }

        if(currentState == ArenaState.PLAYING) {
            YamlConfig messages = new YamlConfig(this.plugin, "Messages");
            YamlConfig settings = new YamlConfig(this.plugin, "Settings");

            for(Player player : this.arena.getAlivePlayers()) {
                PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player);
                if (playerData != null) {
                    Material m = player.getLocation().getBlock().getType();
                    if (m.name().equalsIgnoreCase("STATIONARY_WATER") || m == Material.WATER) {
                        double damage = settings.getDouble("Settings.WaterDamage", 2.0);
                        if(player.getHealth() - damage > 0.5) {
                            player.damage(damage);
                        } else {
                            playerData.setDeaths(playerData.getDeaths() + 1);
                            if (playerData.getLives() <= 0) {
                                arena.deathPlayer(player);
                            } else {
                                playerData.setLives(playerData.getLives() - 1);
                                arena.loadPlayer(player);
                            }
                            arena.sendMessage(messages.getString("DeathPlayer", "&c%player% died").replace("%player%", player.getName()));
                        }
                    }
                }
            }
        }
    }
}
