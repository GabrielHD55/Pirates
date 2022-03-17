package com.gabrielhd.pirates;

import com.bizarrealex.aether.Aether;
import com.gabrielhd.pirates.Arena.Arena;
import com.gabrielhd.pirates.Commands.PiratesCmd;
import com.gabrielhd.pirates.Commands.OthersCmd;
import com.gabrielhd.pirates.Config.YamlConfig;
import com.gabrielhd.pirates.Database.Database;
import com.gabrielhd.pirates.Hook.PlaceholderAPIHook;
import com.gabrielhd.pirates.Listeners.Listeners;
import com.gabrielhd.pirates.Managers.ArenaManager;
import com.gabrielhd.pirates.Managers.ConfigManager;
import com.gabrielhd.pirates.Managers.NPCManager;
import com.gabrielhd.pirates.Managers.PlayerManager;
import com.gabrielhd.pirates.Scoreboard.BoardBuilder;
import com.gabrielhd.pirates.Tasks.SaveTask;
import com.gabrielhd.pirates.Utils.LocUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class Pirates extends JavaPlugin {

    @Getter private NPCManager npcManager;
    @Getter private ArenaManager arenaManager;
    @Getter private ConfigManager configManager;
    @Getter private PlayerManager playerManager;

    @Getter private Location spawnLocation;

    private Aether aether;
    
    @Override
    public void onEnable() {
        new YamlConfig(this,"Settings");
        new YamlConfig(this,"Messages");
        
        new Database(this);

        npcManager = new NPCManager(this);
        arenaManager = new ArenaManager(this);
        configManager = new ConfigManager(this);
        playerManager = new PlayerManager(this);

        this.getCommand("join").setExecutor(new OthersCmd(this));
        this.getCommand("stats").setExecutor(new OthersCmd(this));
        this.getCommand("leave").setExecutor(new OthersCmd(this));
        this.getCommand("games").setExecutor(new OthersCmd(this));
        this.getCommand("pirates").setExecutor(new PiratesCmd(this));

        this.getServer().getPluginManager().registerEvents(new Listeners(this), this);
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, new SaveTask(this), 1200L, 1200L);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderAPIHook(this).register();
        }

        this.loadSpawnLocation();
        this.loadScoreboard();
    }
    
    @Override
    public void onDisable() {
        npcManager.destroy();

        arenaManager.saveArenas();
        
        for (Arena arena : arenaManager.getArenas().values()) {
            arena.restart();
        }
    }

    public void setSpawnLocation(Location location) {
        spawnLocation = location;

        YamlConfig settings = new YamlConfig(this, "Spawn");
        settings.set("Location", LocUtils.LocationToString(location));

        settings.save();
    }

    public void loadSpawnLocation() {
        YamlConfig spawn = new YamlConfig(this, "Spawn");

        if(spawn.isSet("Location")) {
            spawnLocation = LocUtils.StringToLocation(spawn.getString("Location"));
        }
    }

    public void loadScoreboard() {
        YamlConfig settings = configManager.getSettings();

        if(this.aether != null) {
            this.aether.delete();
            this.aether = null;
        }

        if(settings.getBoolean("Scoreboard.Enable")) {
            this.aether = new Aether(this, new BoardBuilder(this));
        }
    }

    public void reload() {
        configManager = new ConfigManager(this);

        this.loadSpawnLocation();
        this.loadScoreboard();
    }
}
