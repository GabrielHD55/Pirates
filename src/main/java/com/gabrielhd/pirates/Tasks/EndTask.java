package com.gabrielhd.pirates.Tasks;

import com.gabrielhd.pirates.Events.RestartGameEvent;
import org.bukkit.scheduler.*;
import com.gabrielhd.pirates.Arena.*;
import com.gabrielhd.pirates.*;
import com.gabrielhd.pirates.Teams.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.*;

public class EndTask extends BukkitRunnable
{
    private final Arena arena;
    private int time;
    
    public EndTask(Pirates plugin, Arena arena) {
        this.arena = arena;
        this.time = arena.getEndTime();

        this.runTaskTimer(plugin, 0L, 20L);
    }
    
    public void run() {
        if (this.time <= 0) {
            this.arena.restart();
            this.cancel();
            return;
        }

        if(time == 6) {
            RestartGameEvent event = new RestartGameEvent(this.arena);
            Bukkit.getPluginManager().callEvent(event);
        }

        if(time <= 4) {
            if(!this.arena.getPlayers().isEmpty()) {
                for (int i = 0; i < this.arena.getPlayers().size(); i++) {
                    this.arena.removePlayer(this.arena.getPlayers().get(i), false);
                }
            }
        }

        for (Team team : this.arena.getTeams()) {
            for (Player member : team.getMembers()) {
                if (team.isAlive(this.arena)) {
                    spawnRandomFirework(member.getEyeLocation());
                }
            }
        }
        --this.time;
    }
    
    public static FireworkEffect getRandomFireworkEffect() {
        Random r = new Random();
        FireworkEffect.Builder builder = FireworkEffect.builder();
        return builder.flicker(false).trail(false).with(FireworkEffect.Type.BALL).withColor(Color.fromRGB(r.nextInt(255), r.nextInt(255), r.nextInt(255))).withFade(Color.fromRGB(r.nextInt(255), r.nextInt(255), r.nextInt(255))).build();
    }
    
    public static void spawnRandomFirework(Location location) {
        if (location == null) {
            return;
        }
        try {
            Firework f = location.getWorld().spawn(location, Firework.class);
            FireworkMeta fm = f.getFireworkMeta();
            fm.addEffect(getRandomFireworkEffect());
            fm.setPower(1);
            f.setFireworkMeta(fm);
        }
        catch (NullPointerException ignored) {}
    }
    
    public int getTime() {
        return this.time;
    }
}
