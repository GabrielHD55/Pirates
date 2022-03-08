package com.gabrielhd.pirates.Managers;

import com.gabrielhd.pirates.Arena.*;
import org.bukkit.*;
import com.gabrielhd.pirates.*;
import java.io.*;
import com.gabrielhd.pirates.Config.*;
import com.gabrielhd.pirates.Utils.*;
import com.google.common.collect.*;
import com.gabrielhd.pirates.Teams.*;
import java.util.*;

public class ArenaManager {

    private final Pirates plugin;
    private final Map<String, Arena> arenas;
    
    public ArenaManager(Pirates plugin) {
        this.plugin = plugin;

        this.arenas = Maps.newHashMap();
        this.loadArenas();
    }

    public Arena[] getAllGames() {
        return this.arenas.values().toArray(new Arena[0]);
    }
    
    public void createArena(String name, Location max, Location min) {
        Arena arena = new Arena(this.plugin, name);

        Cuboid cuboid = new Cuboid(max, min);
        arena.setCuboid(cuboid);

        this.arenas.put(name.toLowerCase(), arena);

        saveArena(arena);
    }
    
    public void deleteArena(String name) {
        Arena arena = this.getArena(name);
        if (arena != null) {
            File file = new File(this.plugin.getDataFolder(), "Maps/" + arena.getName() + ".yml");
            if (file.exists()) {
                file.delete();
            }

            this.arenas.remove(arena.getName().toLowerCase());
        }
    }
    
    public Arena getArena(String name) {
        return this.arenas.get(name.toLowerCase());
    }
    
    public Arena getArena(Location loc) {
        for (Arena arena : this.arenas.values()) {
            if (arena.getCuboid().isInRegion(loc)) {
                return arena;
            }
        }
        return null;
    }
    
    public void saveArenas() {
        for (Arena arena : this.arenas.values()) {
            YamlConfig arenaConfig = new YamlConfig(this.plugin, "Maps/" + arena.getName());
            arenaConfig.set("Name", arena.getName());
            arenaConfig.set("Enable", arena.isEnable());
            arenaConfig.set("MinPlayers", arena.getMinPlayers());
            arenaConfig.set("MaxPlayers", arena.getMaxPlayers());
            arenaConfig.set("Broadcast", arena.getBroadcast());
            arenaConfig.set("StartTime", arena.getStartTime());
            arenaConfig.set("EndTime", arena.getEndTime());
            arenaConfig.set("Max", LocUtils.LocationToString(arena.getCuboid().getL1()));
            arenaConfig.set("Min", LocUtils.LocationToString(arena.getCuboid().getL2()));
            if (arena.getSpawn() != null) {
                arenaConfig.set("SpawnLoc", LocUtils.LocationToString(arena.getSpawn()));
            }
            if (arena.getSpecs() != null) {
                arenaConfig.set("SpecsLoc", LocUtils.LocationToString(arena.getSpecs()));
            }
            if (!arena.getTeams().isEmpty()) {
                for (Team team : arena.getTeams()) {
                    if (team.getLoc() != null) {
                        arenaConfig.set("Team." + team.getTeam().name() + ".Spawn", LocUtils.LocationToString(team.getLoc()));
                    }
                }
            }
            arenaConfig.save();
        }
    }
    
    public void saveArena(Arena arena) {
        YamlConfig arenaConfig = new YamlConfig(this.plugin, "Maps/" + arena.getName());
        arenaConfig.set("Name", arena.getName());
        arenaConfig.set("Enable", arena.isEnable());
        arenaConfig.set("MinPlayers", arena.getMinPlayers());
        arenaConfig.set("MaxPlayers", arena.getMaxPlayers());
        arenaConfig.set("Broadcast", arena.getBroadcast());
        arenaConfig.set("StartTime", arena.getStartTime());
        arenaConfig.set("EndTime", arena.getEndTime());
        arenaConfig.set("Max", LocUtils.LocationToString(arena.getCuboid().getL1()));
        arenaConfig.set("Min", LocUtils.LocationToString(arena.getCuboid().getL2()));
        if (arena.getSpawn() != null) {
            arenaConfig.set("SpawnLoc", LocUtils.LocationToString(arena.getSpawn()));
        }
        if (arena.getSpecs() != null) {
            arenaConfig.set("SpecsLoc", LocUtils.LocationToString(arena.getSpecs()));
        }
        if (!arena.getTeams().isEmpty()) {
            for (Team team : arena.getTeams()) {
                if (team.getLoc() != null) {
                    arenaConfig.set("Team." + team.getTeam().name() + ".Spawn", LocUtils.LocationToString(team.getLoc()));
                }
            }
        }
        arenaConfig.save();
    }
    
    public void loadArenas() {
        File directory = new File(this.plugin.getDataFolder(), "Maps/");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        for (File file : directory.listFiles()) {
            String name = file.getName();
            if (name.contains(".")) {
                name = name.split("\\.")[0];
            }

            YamlConfig arenaConfig = new YamlConfig(this.plugin, "Maps/" + name);
            Arena arena = new Arena(this.plugin, arenaConfig.getString("Name"));
            arena.setEnable(arenaConfig.getBoolean("Enable"));
            arena.setStartTime(arenaConfig.getInt("StartTime"));
            arena.setEndTime(arenaConfig.getInt("EndTime"));
            arena.setMinPlayers(arenaConfig.getInt("MinPlayers"));
            arena.setMaxPlayers(arenaConfig.getInt("MaxPlayers"));
            arena.setBroadcast(arenaConfig.getIntegerList("Broadcast"));
            Cuboid region = new Cuboid(LocUtils.StringToLocation(arenaConfig.getString("Max")), LocUtils.StringToLocation(arenaConfig.getString("Min")));
            arena.setCuboid(region);
            if (arenaConfig.isSet("SpawnLoc")) {
                arena.setSpawn(LocUtils.StringToLocation(arenaConfig.getString("SpawnLoc")));
            } else {
                arena.setEnable(false);
                this.plugin.getLogger().info("Arena " + arena.getName() + " disable. Please set spawn with /pirates set spawn " + arena.getName());
            }
            if (arenaConfig.isSet("SpecsLoc")) {
                arena.setSpecs(LocUtils.StringToLocation(arenaConfig.getString("SpecsLoc")));
            }
            if (arenaConfig.isSet("Team")) {
                List<Team> teams = Lists.newArrayList();
                Set<String> sections = arenaConfig.getConfigurationSection("Team").getKeys(false);
                for (String sec : sections) {
                    Team team = new Team(TeamColor.valueOf(sec));
                    team.setLoc(LocUtils.StringToLocation(arenaConfig.getString("Team." + sec + ".Spawn")));
                    teams.add(team);
                }
                arena.setTeams(teams);
            }

            arena.getSpawn().getWorld().setAutoSave(false);
            arena.getSpawn().getWorld().setPVP(false);

            this.arenas.put(arena.getName().toLowerCase(), arena);
        }
    }
    
    public Map<String, Arena> getArenas() {
        return this.arenas;
    }
}
