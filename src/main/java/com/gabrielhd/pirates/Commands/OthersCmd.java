package com.gabrielhd.pirates.Commands;

import com.gabrielhd.pirates.Arena.Arena;
import com.gabrielhd.pirates.Arena.ArenaState;
import com.gabrielhd.pirates.Config.YamlConfig;
import com.gabrielhd.pirates.Menus.Submenus.GamesMenu;
import com.gabrielhd.pirates.Pirates;
import com.gabrielhd.pirates.Player.PlayerData;
import com.gabrielhd.pirates.Player.PlayerState;
import com.gabrielhd.pirates.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OthersCmd implements CommandExecutor {

    private final Pirates plugin;

    public OthersCmd(Pirates plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String ss, String[] args) {
        YamlConfig settings = this.plugin.getConfigManager().getSettings();
        YamlConfig messages = this.plugin.getConfigManager().getMessages();

        if(sender instanceof Player) {
            Player player = (Player) sender;

            if(args.length == 0) {
                if (cmd.getName().equalsIgnoreCase("leave")) {
                    PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player);
                    if(playerData != null) {
                        if (playerData.getState() != PlayerState.LOBBY && playerData.getArena() != null) {
                            playerData.getArena().removePlayer(player, false);
                            return true;
                        } else {
                            player.sendMessage(Utils.Color(messages.getString("NoInGame")));
                        }
                        return true;
                    }
                    return true;
                }

                if (cmd.getName().equalsIgnoreCase("stats")) {
                    PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player);
                    if (playerData != null) {
                        for (String s : messages.getStringList("YourStats")) {
                            s = s.replaceAll("%player%", player.getName());
                            s = s.replaceAll("%wins%", String.valueOf(playerData.getWins()));
                            s = s.replaceAll("%played%", String.valueOf(playerData.getPlayed()));
                            s = s.replaceAll("%deaths%", String.valueOf(playerData.getDeaths()));
                            s = s.replaceAll("%kills%", String.valueOf(playerData.getKills()));
                            s = s.replaceAll("%level%", String.valueOf(playerData.getLevel()));
                            s = s.replaceAll("%exp%", String.valueOf(playerData.getExp()));

                            player.sendMessage(Utils.Color(s));
                        }
                    }
                    return true;
                }

                if(cmd.getName().equalsIgnoreCase("games")) {
                    new GamesMenu(this.plugin).openInventory(player);
                    return true;
                }

                if (cmd.getName().equalsIgnoreCase("join")) {
                    PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player);
                    if(playerData != null) {
                        if(playerData.getState() == PlayerState.LOBBY) {
                            if(args.length == 1) {
                                Arena game = this.plugin.getArenaManager().getArena(args[1]);

                                if(game == null) {
                                    player.sendMessage(Utils.Color(messages.getString("ArenaNoExists").replace("%arena%", args[0])));
                                    return true;
                                }

                                if(game.getState() == ArenaState.PLAYING || game.getState() == ArenaState.ENDING) {
                                    player.sendMessage(Utils.Color(messages.getString("ArenaInGame").replace("%arena%", game.getName())));
                                    return true;
                                }

                                if(game.isFull()) {
                                    player.sendMessage(Utils.Color(messages.getString("ArenaFull")));
                                    return true;
                                }

                                game.addPlayer(player);
                                return true;
                            }

                            new GamesMenu(this.plugin).openInventory(player);
                            return true;
                        } else {
                            player.sendMessage(Utils.Color(messages.getString("AlreadyInGame")));
                        }
                        return true;
                    }
                    return true;
                }
                return true;
            }

            if (args.length == 1) {
                if (cmd.getName().equalsIgnoreCase("stats")) {
                    String name = args[0];
                    Player target = Bukkit.getPlayer(name);
                    if (target.isOnline()) {
                        PlayerData playerData2 = this.plugin.getPlayerManager().getPlayerData(target);
                        if (playerData2 != null) {
                            for (String s2 : messages.getStringList("OthersStats")) {
                                s2 = s2.replaceAll("%player%", player.getName());
                                s2 = s2.replaceAll("%wins%", String.valueOf(playerData2.getWins()));
                                s2 = s2.replaceAll("%played%", String.valueOf(playerData2.getPlayed()));
                                s2 = s2.replaceAll("%deaths%", String.valueOf(playerData2.getDeaths()));
                                s2 = s2.replaceAll("%kills%", String.valueOf(playerData2.getKills()));
                                s2 = s2.replaceAll("%level%", String.valueOf(playerData2.getLevel()));
                                s2 = s2.replaceAll("%exp%", String.valueOf(playerData2.getExp()));

                                player.sendMessage(Utils.Color(s2));
                            }
                        }
                        return true;
                    }

                    player.sendMessage(Utils.Color(messages.getString("PlayerIsNotOnline")));
                    player.sendMessage(Utils.Color("&6/stats [Player] &8- &7Use this command to see your stats or that of other players."));
                    return true;
                }
                return true;
            }
            return true;
        }
        return false;
    }
}
