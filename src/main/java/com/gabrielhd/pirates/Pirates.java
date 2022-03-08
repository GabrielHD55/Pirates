package com.gabrielhd.pirates;

import com.gabrielhd.pirates.Arena.Arena;
import com.gabrielhd.pirates.Commands.PiratesCmd;
import com.gabrielhd.pirates.Commands.OthersCmd;
import com.gabrielhd.pirates.Config.YamlConfig;
import com.gabrielhd.pirates.Database.Database;
import com.gabrielhd.pirates.Hook.PlaceholderAPIHook;
import com.gabrielhd.pirates.Listeners.Listeners;
import com.gabrielhd.pirates.Managers.ArenaManager;
import com.gabrielhd.pirates.Managers.NPCManager;
import com.gabrielhd.pirates.Managers.PlayerManager;
import com.gabrielhd.pirates.Tasks.SaveTask;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class Pirates extends JavaPlugin {

    @Getter private NPCManager npcManager;
    @Getter private ArenaManager arenaManager;
    @Getter private PlayerManager playerManager;

    @Getter private Location spawnLocation;
    
    @Override
    public void onEnable() {
        new YamlConfig(this,"Settings");
        new YamlConfig(this,"Messages");
        
        new Database(this);

        npcManager = new NPCManager(this);
        arenaManager = new ArenaManager(this);
        playerManager = new PlayerManager(this);

        this.getCommand("join").setExecutor(new OthersCmd(this));
        this.getCommand("stats").setExecutor(new OthersCmd(this));
        this.getCommand("leave").setExecutor(new OthersCmd(this));
        this.getCommand("pirates").setExecutor(new PiratesCmd(this));

        this.getServer().getPluginManager().registerEvents(new Listeners(this), this);
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, new SaveTask(this), 1200L, 1200L);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderAPIHook(this).register();
        }
    }
    
    @Override
    public void onDisable() {
        arenaManager.saveArenas();
        
        for (Arena arena : arenaManager.getArenas().values()) {
            arena.restart();
        }
    }
}
