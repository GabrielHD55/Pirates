package com.gabrielhd.pirates.Tasks;

import lombok.Setter;
import org.bukkit.scheduler.*;
import com.gabrielhd.pirates.*;
import com.gabrielhd.pirates.Config.*;
import com.gabrielhd.pirates.Arena.*;

public class StartTask extends BukkitRunnable {

    private final Pirates plugin;
    private final Arena arena;
    private boolean isForce;

    @Setter private int time;
    
    public StartTask(Pirates plugin, Arena arena) {
        this.plugin = plugin;

        this.arena = arena;
        this.time = arena.getStartTime();

        this.runTaskTimer(plugin, 0L, 20L);
    }

    @Override
    public void run() {
        YamlConfig messages = new YamlConfig(plugin, "Messages");
        Arena arena = this.arena;
        if (arena.getPlayers().size() < arena.getMinPlayers()) {
            arena.setStarting(false);
            arena.setStarted(false);
            arena.setForce(false);
            arena.setState(ArenaState.WAITING);
            this.cancel();
            return;
        }
        if (arena.getBroadcast().contains(this.time)) {
            arena.sendMessage(messages.getString("StartTime").replace("%time%", String.valueOf(this.time)));
        }
        if (this.time <= 1) {
            if (this.time == 1) {
                this.arena.start();
            }
            this.cancel();
            return;
        }
        --this.time;
    }
    
    public int getTime() {
        return this.time;
    }
}
