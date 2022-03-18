package com.gabrielhd.pirates.Commands;

import com.gabrielhd.pirates.Arena.Arena;
import com.gabrielhd.pirates.Arena.ArenaState;
import com.gabrielhd.pirates.Config.YamlConfig;
import com.gabrielhd.pirates.Managers.ArenaManager;
import com.gabrielhd.pirates.Managers.NPCManager;
import com.gabrielhd.pirates.Menus.Submenus.GamesMenu;
import com.gabrielhd.pirates.NPCs.CustomNPC;
import com.gabrielhd.pirates.NPCs.NPCType;
import com.gabrielhd.pirates.Pirates;
import com.gabrielhd.pirates.Player.OPPlayer;
import com.gabrielhd.pirates.Player.PlayerData;
import com.gabrielhd.pirates.Player.PlayerState;
import com.gabrielhd.pirates.Teams.Team;
import com.gabrielhd.pirates.Teams.TeamColor;
import com.gabrielhd.pirates.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class PiratesCmd implements CommandExecutor {

    private final Pirates plugin;

    public PiratesCmd(Pirates plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        this.command(commandSender, command, s, strings);
        return true;
    }
    
    public void command(CommandSender sender, Command cmd, String label, String[] args) {
        YamlConfig settings = this.plugin.getConfigManager().getSettings();
        YamlConfig messages = this.plugin.getConfigManager().getMessages();

        ArenaManager arenaManager = this.plugin.getArenaManager();

        if (sender instanceof Player) {
            Player player = (Player)sender;

            if (args.length == 0) {
                this.sendHelp(player);
                return;
            }

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("join")) {
                    PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player);
                    if(playerData != null) {
                        if(playerData.getState() == PlayerState.LOBBY) {
                            if(args.length == 1) {
                                List<Arena> arenas = Arrays.stream(this.plugin.getArenaManager().getAllGames()).filter(game -> (game.getState() == ArenaState.WAITING || game.getState() == ArenaState.STARTING) && !game.isFull()).collect(Collectors.toList());
                                if (arenas.isEmpty()) {
                                    player.sendMessage(Utils.Color(messages.getString("NoArenasAvailable")));
                                    return;
                                }
                                Collections.shuffle(arenas);
                                Arena game = (arenas.size() == 1) ? arenas.get(0) : arenas.get(ThreadLocalRandom.current().nextInt(0, arenas.size()-1));

                                game.addPlayer(player);
                                return;
                            }

                            new GamesMenu(this.plugin).openInventory(player);
                            return;
                        } else {
                            player.sendMessage(Utils.Color(messages.getString("AlreadyInGame")));
                        }
                        return;
                    }
                    return;
                }

                if (args[0].equalsIgnoreCase("leave")) {
                    PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player);
                    if(playerData != null) {
                        if (playerData.getState() != PlayerState.LOBBY && playerData.getArena() != null) {
                            playerData.getArena().removePlayer(player, false);
                            return;
                        } else {
                            player.sendMessage(Utils.Color(messages.getString("NoInGame")));
                        }
                        return;
                    }
                    return;
                }

                if (args[0].equalsIgnoreCase("stats")) {
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
                    return;
                }

                NPCManager npcManager = this.plugin.getNpcManager();
                if (args[0].equalsIgnoreCase("setnpc")) {
                    if (!player.hasPermission("pirates.admin")) {
                        player.sendMessage(Utils.Color(messages.getString("NoPermissions")));
                        return;
                    }

                    CustomNPC customNPC = this.plugin.getNpcManager().getNPC(NPCType.ARENAS);
                    if(customNPC == null) {
                        npcManager.createNPC(player.getLocation(), npcManager.getConfig().getString("NPCs.Arenas.Name"), NPCType.ARENAS, npcManager.getConfig().getString("NPCs.Arenas.Skin"));
                    } else {
                        customNPC.getNPC().teleport(player.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
                    }

                    npcManager.saveNPCs();
                    player.sendMessage(Utils.Color("&aNPC Games updated correctly!"));
                    return;
                }

                if(args[0].equalsIgnoreCase("reload")) {
                    if (!player.hasPermission("pirates.admin")) {
                        player.sendMessage(Utils.Color(messages.getString("NoPermissions")));
                        return;
                    }

                    this.plugin.reload();
                    player.sendMessage(Utils.Color(messages.getString("&aPlugin reloaded correctly!")));
                    return;
                }

                if (args[0].equalsIgnoreCase("addshop")) {
                    if (!player.hasPermission("pirates.admin")) {
                        player.sendMessage(Utils.Color(messages.getString("NoPermissions")));
                        return;
                    }

                    npcManager.createNPC(player.getLocation(), npcManager.getConfig().getString("NPCs.Shop.Name"), NPCType.SHOP, npcManager.getConfig().getString("NPCs.Shop.Skin"));
                    npcManager.saveNPCs();
                    player.sendMessage(Utils.Color("&aShop NPC added correctly!"));
                    return;
                }

                if(args[0].equalsIgnoreCase("delshop")) {
                    if (!player.hasPermission("pirates.admin")) {
                        player.sendMessage(Utils.Color(messages.getString("NoPermissions")));
                        return;
                    }

                    if(!npcManager.getNpcs().isEmpty()) {
                        CustomNPC customNPC = npcManager.getNPC("shop-"+(npcManager.getNpcs().size() - 1));

                        if(customNPC != null) {
                            npcManager.removeNPC(customNPC);
                            npcManager.saveNPCs();

                            player.sendMessage(Utils.Color("&cShop NPC removed correctly!"));
                        }
                        return;
                    } else {
                        player.sendMessage(Utils.Color("&cThere are no NPCs to remove."));
                    }
                    return;
                }

                if(args[0].equalsIgnoreCase("setlobby")) {
                    if (!player.hasPermission("pirates.admin")) {
                        player.sendMessage(Utils.Color(messages.getString("NoPermissions")));
                        return;
                    }

                    this.plugin.setSpawnLocation(player.getLocation());
                    player.sendMessage(Utils.Color("&aMain lobby setted correctly!"));
                    return;
                }

                this.sendHelp(player);
                return;
            }

            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("stats")) {
                    String name = args[1];
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
                        return;
                    }

                    player.sendMessage(Utils.Color(messages.getString("PlayerIsNotOnline")));
                    return;
                }

                if (args[0].equalsIgnoreCase("join")) {
                    PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player);
                    if(playerData != null) {
                        if(playerData.getState() == PlayerState.LOBBY) {
                            Arena game = this.plugin.getArenaManager().getArena(args[1]);

                            if(game == null) {
                                player.sendMessage(Utils.Color(messages.getString("ArenaNoExists").replace("%arena%", args[0])));
                                return;
                            }

                            if(game.getState() == ArenaState.PLAYING || game.getState() == ArenaState.ENDING) {
                                player.sendMessage(Utils.Color(messages.getString("ArenaInGame").replace("%arena%", game.getName())));
                                return;
                            }

                            if(game.isFull()) {
                                player.sendMessage(Utils.Color(messages.getString("ArenaFull")));
                                return;
                            }

                            game.addPlayer(player);
                            return;
                        } else {
                            player.sendMessage(Utils.Color(messages.getString("AlreadyInGame")));
                        }
                        return;
                    }
                    return;
                }

                if (!player.hasPermission("pirates.admin")) {
                    player.sendMessage(Utils.Color(messages.getString("NoPermissions")));
                    return;
                }

                if (args[0].equalsIgnoreCase("enable")) {
                    String name = args[1];
                    Arena arena = arenaManager.getArena(name);
                    if (arena != null) {
                        arena.setEnable(true);
                        player.sendMessage(Utils.Color(messages.getString("ArenaEnable").replace("%arena%", name)));

                        arenaManager.saveArena(arena);
                    } else {
                        player.sendMessage(Utils.Color(messages.getString("ArenaNoExists").replace("%arena%", name)));
                    }
                    return;
                }

                if (args[0].equalsIgnoreCase("disable")) {
                    String name = args[1];
                    Arena arena = arenaManager.getArena(name);
                    if (arena != null) {
                        arena.setEnable(false);
                        arena.forceEnd();
                        player.sendMessage(Utils.Color(messages.getString("ArenaDisable").replace("%arena%", name)));

                        arenaManager.saveArena(arena);
                    } else {
                        player.sendMessage(Utils.Color(messages.getString("ArenaNoExists").replace("%arena%", name)));
                    }
                    return;
                }

                if (args[0].equalsIgnoreCase("create")) {
                    String name = args[1];

                    if (!arenaManager.getArenas().containsKey(name.toLowerCase())) {
                        OPPlayer opPlayer = this.plugin.getPlayerManager().getOPPlayer(player);
                        if (opPlayer.getFirst() != null && opPlayer.getSecond() != null) {
                            arenaManager.createArena(name, opPlayer.getFirst(), opPlayer.getSecond());

                            player.sendMessage(Utils.Color(messages.getString("ArenaCreate").replace("%arena%", name)));
                        } else {
                            player.sendMessage(Utils.Color("&cYou must set the min and max point with a blaze wand"));
                        }
                    } else {
                        player.sendMessage(Utils.Color(messages.getString("ArenaExists").replace("%arena%", name)));
                    }
                    return;
                }

                if (args[0].equalsIgnoreCase("delete")) {
                    String name = args[1];
                    if (arenaManager.getArenas().containsKey(name.toLowerCase())) {
                        arenaManager.deleteArena(name);

                        player.sendMessage(Utils.Color(messages.getString("ArenaDelete").replace("%arena%", name)));
                    } else {
                        player.sendMessage(Utils.Color(messages.getString("ArenaNoExists").replace("%arena%", name)));
                    }
                    return;
                }

                if (!args[0].equalsIgnoreCase("finish")) {
                    String name = args[1];
                    Arena arena = arenaManager.getArena(name);
                    if (arena != null) {
                        if (arena.getState() == ArenaState.STARTING || arena.getState() == ArenaState.PLAYING) {
                            arena.forceEnd();
                            player.sendMessage(Utils.Color(messages.getString("ArenaFinish").replace("%arena%", name)));
                        }
                    } else {
                        player.sendMessage(Utils.Color(messages.getString("ArenaNoExists").replace("%arena%", name)));
                    }
                    return;
                }

                this.sendHelp(player);
                return;
            }

            if (args.length == 3) {
                if (!player.hasPermission("pirates.admin")) {
                    player.sendMessage(Utils.Color(messages.getString("NoPermissions")));
                    return;
                }

                if (!args[0].equalsIgnoreCase("set")) {
                    this.sendHelp(player);
                    return;
                }

                if (args[1].equalsIgnoreCase("lobby")) {
                    String name = args[2];
                    Arena arena = arenaManager.getArena(name);
                    if (arena != null) {
                        arena.setSpawn(player.getLocation());
                        arenaManager.saveArena(arena);

                        player.sendMessage(Utils.Color("&aSpawn set correctly for the " + name + " arena"));
                    }
                    else {
                        player.sendMessage(Utils.Color(messages.getString("ArenaNoExists").replace("%arena%", name)));
                    }
                    return;
                }

                if (args[1].equalsIgnoreCase("spectate")) {
                    String name = args[2];
                    Arena arena = arenaManager.getArena(name);
                    if (arena != null) {
                        arena.setSpecs(player.getLocation());
                        arenaManager.saveArena(arena);

                        player.sendMessage(Utils.Color("&aSpawn spectator set correctly for the " + name + " arena"));
                    } else {
                        player.sendMessage(Utils.Color(messages.getString("ArenaNoExists").replace("%arena%", name)));
                    }
                    return;
                }

                this.sendHelp(player);
                return;
            }

            if(args.length == 4) {
                if (args[0].equalsIgnoreCase("set")) {
                    if (!player.hasPermission("pirates.admin")) {
                        player.sendMessage(Utils.Color(messages.getString("NoPermissions")));
                        return;
                    }

                    if (args[1].equalsIgnoreCase("min")) {
                        String name = args[2];
                        Arena arena = arenaManager.getArena(name);
                        if (arena != null) {
                            if (this.isInt(args[3])) {
                                arena.setMinPlayers(Integer.parseInt(args[3]));
                                arenaManager.saveArena(arena);

                                player.sendMessage(Utils.Color("&aMin players set correctly for the " + name + " arena"));
                            } else {
                                player.sendMessage(Utils.Color("&cThe last argument must be a number"));
                            }
                        } else {
                            player.sendMessage(Utils.Color(messages.getString("ArenaNoExists").replace("%arena%", name)));
                        }
                        return;
                    }

                    if (args[1].equalsIgnoreCase("max")) {
                        String name = args[2];
                        Arena arena = arenaManager.getArena(name);
                        if (arena != null) {
                            if (this.isInt(args[3])) {
                                arena.setMaxPlayers(Integer.parseInt(args[3]));
                                player.sendMessage(Utils.Color("&aMax players set correctly for the " + name + " arena"));
                                arenaManager.saveArena(arena);
                            }
                            else {
                                player.sendMessage(Utils.Color("&cThe last argument must be a number"));
                            }
                        }
                        else {
                            player.sendMessage(Utils.Color(messages.getString("ArenaNoExists").replace("%arena%", name)));
                        }
                        return;
                    }

                    if (args[1].equalsIgnoreCase("time")) {
                        String name = args[2];
                        Arena arena = arenaManager.getArena(name);
                        if (arena != null) {
                            if (this.isInt(args[3])) {
                                arena.setStartTime(Integer.parseInt(args[3]));
                                arenaManager.saveArena(arena);

                                player.sendMessage(Utils.Color("&aStart time set correctly for the " + name + " arena"));
                            } else {
                                player.sendMessage(Utils.Color("&cThe last argument must be a number"));
                            }
                        } else {
                            player.sendMessage(Utils.Color(messages.getString("ArenaNoExists").replace("%arena%", name)));
                        }
                        return;
                    }

                    if (args[1].equalsIgnoreCase("spawn")) {
                        String name = args[2];
                        Arena arena = arenaManager.getArena(name);
                        if (arena != null) {
                            Team team = arena.getTeam(TeamColor.getTeamColor(args[3]));
                            team.setLoc(player.getLocation());

                            arenaManager.saveArena(arena);

                            player.sendMessage(Utils.Color(((team.getTeam() == TeamColor.BLUE) ? "&1" : "&4") + team.getTeam().name() + " &aSpawn Location set correctly for the " + name + " arena"));
                        } else {
                            player.sendMessage(Utils.Color(messages.getString("ArenaNoExists").replace("%arena%", name)));
                        }
                        return;
                    }
                }
                this.sendHelp(player);
            }
        }
    }
    
    public void sendHelp(Player player) {
        player.sendMessage(Utils.Color("&7"));
        player.sendMessage(Utils.Color("&6/leave &8- &7Use this command to enter a game."));
        player.sendMessage(Utils.Color("&6/games &8- &7Use this command to open the arena menu."));
        player.sendMessage(Utils.Color("&6/join [Arena name] &8- &7Use this command to enter a game."));
        player.sendMessage(Utils.Color("&6/stats [Player] &8- &7Use this command to see your stats or that of other players."));
        player.sendMessage(Utils.Color("&7"));

        if (player.hasPermission("pirates.admin")) {
            player.sendMessage(Utils.Color("&eAdmin commands: "));
            player.sendMessage(Utils.Color("&7"));
            player.sendMessage(Utils.Color("&6/pirates setnpc &8- &7Use this command to set Games NPC."));
            player.sendMessage(Utils.Color("&6/pirates setlobby &8- &7Use this command to set the main lobby."));
            player.sendMessage(Utils.Color("&6/pirates create <Arena> &8- &7Use this command to create an arena."));
            player.sendMessage(Utils.Color("&6/pirates delete <Arena> &8- &7Use this command to delete an arena."));
            player.sendMessage(Utils.Color("&6/pirates finish <Arena> &8- &7Use this command to end the game."));
            player.sendMessage(Utils.Color("&6/pirates enable <Arena> &8- &7Use this command to enable the arena."));
            player.sendMessage(Utils.Color("&6/pirates disable <Arena> &8- &7Use this command to disable the arena."));
            player.sendMessage(Utils.Color("&6/pirates set lobby <Arena> &8- &7Use this command to set the game's spawn."));
            player.sendMessage(Utils.Color("&6/pirates set spectate <Arena> &8- &7Use this command to set the game's spectator spawn."));
            player.sendMessage(Utils.Color("&6/pirates set min <Arena> <Amount> &8- &7Use this command to set the minimum number of players in the game."));
            player.sendMessage(Utils.Color("&6/pirates set max <Arena> <Amount> &8- &7Use this command to set the maximum number of players in the game."));
            player.sendMessage(Utils.Color("&6/pirates set time <Arena> <Amount> &8- &7Use this command to set the game start time."));
            player.sendMessage(Utils.Color("&6/pirates set spawn <Arena> <blue/red> &8- &7Use this command to set the spawn point of the players."));
            player.sendMessage(Utils.Color("&6/pirates addshop &8- &7Use this command to add a store in a game."));
            player.sendMessage(Utils.Color("&6/pirates delshop &8- &7Use this command to remove the last store added."));
            player.sendMessage(Utils.Color("&6/pirates reload &8- &7Use this command to reload the plugin."));
            player.sendMessage(Utils.Color("&7"));
            player.sendMessage(Utils.Color("&e&lPlugin by GabrielHD"));
            player.sendMessage(Utils.Color("&7"));
        }
    }
    
    public boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
