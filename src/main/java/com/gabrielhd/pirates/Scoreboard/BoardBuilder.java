package com.gabrielhd.pirates.Scoreboard;

import com.bizarrealex.aether.scoreboard.Board;
import com.bizarrealex.aether.scoreboard.BoardAdapter;
import com.bizarrealex.aether.scoreboard.cooldown.BoardCooldown;
import com.gabrielhd.pirates.Arena.Arena;
import com.gabrielhd.pirates.Arena.ArenaState;
import com.gabrielhd.pirates.Pirates;
import com.gabrielhd.pirates.Player.PlayerData;
import com.gabrielhd.pirates.Player.PlayerState;
import com.gabrielhd.pirates.Utils.Utils;
import com.google.common.collect.Lists;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class BoardBuilder implements BoardAdapter {

    private final Pirates plugin;

    public BoardBuilder(Pirates plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getTitle(Player p0) {
        return Utils.Color(this.plugin.getConfigManager().getSettings().getString("Scoreboard.Title"));
    }

    @Override
    public List<String> getScoreboard(Player p, Board p1, Set<BoardCooldown> p2) {
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(p);
        if (playerData != null) {
            if(playerData.getState() != PlayerState.LOBBY && playerData.getArena() != null) {
                Arena arena = playerData.getArena();

                if(arena.getState() == ArenaState.WAITING || arena.getState() == ArenaState.STARTING) {
                    return this.getPreGameBoard(p, playerData, arena);
                }
                return this.getGameBoard(p, playerData, playerData.getArena());
            } else {
                return this.getLobbyBoard(p, playerData);
            }
        }
        return null;
    }

    private List<String> getLobbyBoard(Player p, PlayerData playerData) {
        List<String> lines = Lists.newArrayList();
        lines.clear();

        for (String replaceText : this.plugin.getConfigManager().getSettings().getStringList("Scoreboard.Lobby")) {
            replaceText = replaceText.replaceAll("%player%", p.getName());
            replaceText = replaceText.replaceAll("%player-displayname%", p.getDisplayName());

            replaceText = replaceText.replaceAll("%kills%", String.valueOf(playerData.getKills()));
            replaceText = replaceText.replaceAll("%deaths%", String.valueOf(playerData.getDeaths()));
            replaceText = replaceText.replaceAll("%level%", String.valueOf(playerData.getLevel()));
            replaceText = replaceText.replaceAll("%xp%", String.valueOf(playerData.getExp()));

            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                replaceText = PlaceholderAPI.setPlaceholders(p, replaceText);
            }

            replaceText = replaceText.replaceAll("%empty%", " ");
            replaceText = Utils.Color(replaceText);

            lines.add(replaceText);
        }
        return lines;
    }

    private List<String> getGameBoard(Player p, PlayerData playerData, Arena arena) {
        List<String> lines = Lists.newArrayList();
        lines.clear();

        for (String replaceText : this.plugin.getConfigManager().getSettings().getStringList("Scoreboard.Game")) {
            replaceText = replaceText.replaceAll("%player%", p.getName());
            replaceText = replaceText.replaceAll("%player-displayname%", p.getDisplayName());

            replaceText = replaceText.replaceAll("%kills%", String.valueOf(playerData.getKills()));
            replaceText = replaceText.replaceAll("%deaths%", String.valueOf(playerData.getDeaths()));
            replaceText = replaceText.replaceAll("%level%", String.valueOf(playerData.getLevel()));
            replaceText = replaceText.replaceAll("%xp%", String.valueOf(playerData.getExp()));

            replaceText = replaceText.replaceAll("%map%", arena.getName());
            replaceText = replaceText.replaceAll("%team%", arena.getPlayerTeam(p).getName());
            replaceText = replaceText.replaceAll("%players%", String.valueOf(arena.getPlayers().size()));
            replaceText = replaceText.replaceAll("%max-players%", String.valueOf(arena.getMaxPlayers()));
            replaceText = replaceText.replaceAll("%alive-players%", String.valueOf(arena.getAlivePlayers().size()));

            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                replaceText = PlaceholderAPI.setPlaceholders(p, replaceText);
            }

            replaceText = replaceText.replaceAll("%empty%", " ");
            replaceText = Utils.Color(replaceText);

            lines.add(replaceText);
        }
        return lines;
    }

    private List<String> getPreGameBoard(Player p, PlayerData playerData, Arena arena) {
        List<String> lines = Lists.newArrayList();
        lines.clear();

        for (String replaceText : this.plugin.getConfigManager().getSettings().getStringList("Scoreboard.Game")) {
            replaceText = replaceText.replaceAll("%player%", p.getName());
            replaceText = replaceText.replaceAll("%player-displayname%", p.getDisplayName());

            replaceText = replaceText.replaceAll("%kills%", String.valueOf(playerData.getKills()));
            replaceText = replaceText.replaceAll("%deaths%", String.valueOf(playerData.getDeaths()));
            replaceText = replaceText.replaceAll("%level%", String.valueOf(playerData.getLevel()));
            replaceText = replaceText.replaceAll("%xp%", String.valueOf(playerData.getExp()));

            replaceText = replaceText.replaceAll("%map%", arena.getName());
            replaceText = replaceText.replaceAll("%players%", String.valueOf(arena.getPlayers().size()));
            replaceText = replaceText.replaceAll("%max-players%", String.valueOf(arena.getMaxPlayers()));
            replaceText = replaceText.replaceAll("%alive-players%", String.valueOf(arena.getAlivePlayers().size()));
            replaceText = replaceText.replaceAll("%time%", String.valueOf(arena.getStartTask().getTime()));

            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                replaceText = PlaceholderAPI.setPlaceholders(p, replaceText);
            }

            replaceText = replaceText.replaceAll("%empty%", " ");
            replaceText = Utils.Color(replaceText);

            lines.add(replaceText);
        }
        return lines;
    }
}