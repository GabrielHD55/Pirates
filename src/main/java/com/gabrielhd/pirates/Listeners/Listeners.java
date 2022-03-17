package com.gabrielhd.pirates.Listeners;

import com.gabrielhd.pirates.Arena.Arena;
import com.gabrielhd.pirates.Arena.ArenaState;
import com.gabrielhd.pirates.Config.YamlConfig;
import com.gabrielhd.pirates.NPCs.CustomNPC;
import com.gabrielhd.pirates.NPCs.NPCType;
import com.gabrielhd.pirates.Pirates;
import com.gabrielhd.pirates.Player.OPPlayer;
import com.gabrielhd.pirates.Player.PlayerData;
import com.gabrielhd.pirates.Player.PlayerState;
import com.gabrielhd.pirates.Teams.Team;
import com.gabrielhd.pirates.Utils.Cuboid;
import com.gabrielhd.pirates.Utils.Utils;
import com.google.common.collect.Lists;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class Listeners implements Listener {

    private final Pirates plugin;

    public Listeners(Pirates plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        this.plugin.getPlayerManager().createPlayer(player);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        this.plugin.getPlayerManager().removePlayer(player);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onKick(PlayerKickEvent event) {
        Player player = event.getPlayer();

        this.plugin.getPlayerManager().removePlayer(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSelectRegion(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block != null && player.getInventory().getItemInHand().getType() == Material.BLAZE_ROD && player.isOp()) {
                OPPlayer opPlayer = this.plugin.getPlayerManager().getOPPlayer(player);
                if(opPlayer != null) {
                    if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        opPlayer.setSecond(block.getLocation());
                        player.sendMessage(Utils.Color("&a&lSecond point selected correctly"));
                        event.setCancelled(true);
                        return;
                    }
                    if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        opPlayer.setFirst(block.getLocation());
                        player.sendMessage(Utils.Color("&a&lFirst point selected correctly"));
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onDeath2(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player)event.getEntity();
            YamlConfig settings = new YamlConfig(this.plugin,"Settings");
            YamlConfig messages = new YamlConfig(this.plugin,"Messages");

            PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player);
            if (playerData != null) {
                if (playerData.getState() != PlayerState.PLAYING) {
                    return;
                }

                if (player.getHealth() - event.getDamage() < 0.5) {
                    if (playerData.getArena() != null) {
                        Arena arena = playerData.getArena();

                        if (arena.getState() != ArenaState.PLAYING) {
                            event.setCancelled(true);
                            return;
                        }

                        playerData.setDeaths(playerData.getDeaths() + 1);
                        arena.sendMessage(messages.getString("DeathPlayer", "&c%player% died").replace("%player%", player.getName()));

                        if (playerData.getLives() <= 0) {
                            arena.deathPlayer(player);
                        } else {
                            playerData.setLives(playerData.getLives() - 1);
                            arena.loadPlayer(player);
                        }
                    }
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onDeath(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player player = (Player)event.getEntity();

            YamlConfig settings = new YamlConfig(this.plugin,"Settings");
            YamlConfig messages = new YamlConfig(this.plugin,"Messages");

            if (player.getHealth() - event.getDamage() < 0.5) {
                PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player);
                if (playerData != null) {
                    if (playerData.getState() == PlayerState.LOBBY) {
                        return;
                    }
                    if (playerData.getArena() != null) {
                        Arena arena = playerData.getArena();
                        if (arena.getState() != ArenaState.PLAYING) {
                            event.setCancelled(true);
                            return;
                        }
                        if (event.getDamager() != null) {
                            Player killer = (Player)event.getDamager();
                            PlayerData killerData = this.plugin.getPlayerManager().getPlayerData(killer);
                            if (killerData != null) {
                                killerData.setKills(killerData.getKills() + 1);
                                int exp = new Random().nextInt(settings.getInt("Settings.ExpPerKill.Max"));
                                int min = settings.getInt("Settings.ExpPerKill.Min");
                                if (exp < min) {
                                    exp = min;
                                }
                                killerData.setExp(killerData.getExp() + exp);
                                if (killerData.getExp() >= killerData.getLevel() + settings.getDouble("Settings.LevelExpMultiplier")) {
                                    killerData.setLevel(killerData.getLevel() + 1);
                                    for (String cmd : settings.getStringList("Settings.LevelRewards." + killerData.getLevel())) {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", killer.getName()));
                                    }
                                }
                            }
                        }

                        PlayerDeathEvent playerDeathEvent = new PlayerDeathEvent(player, Lists.newArrayList(), 0, null);
                        Bukkit.getPluginManager().callEvent(playerDeathEvent);

                        playerData.setDeaths(playerData.getDeaths() + 1);

                        arena.sendMessage(messages.getString("DeathPlayer").replace("%player%", player.getName()));
                        int currentLives = playerData.getLives()-1;

                        if (currentLives <= 0) {
                            arena.deathPlayer(player);
                        } else {
                            playerData.setLives(playerData.getLives() - 1);

                            int time = settings.getInt("Settings.RespawnTime");

                            if(time > 1) {
                                player.setGameMode(GameMode.SPECTATOR);

                                if(arena.getSpecs() != null) player.teleport(arena.getSpecs());

                                Bukkit.getScheduler().runTaskLater(this.plugin, () -> arena.loadPlayer(player), time*20L);
                            } else {
                                arena.loadPlayer(player);
                            }
                        }
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if(!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player);
        if (playerData != null && playerData.getArena() != null && playerData.getState() != PlayerState.LOBBY) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onArrowHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile.getShooter() != null) {
            Player player = (Player)projectile.getShooter();
            PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player);
            if (playerData != null && playerData.getArena() != null) {
                Arena arena = playerData.getArena();
                Team team = arena.getPlayerTeam(player);
                if (team != null) {
                    for (Player members : team.getMembers()) {
                        if (projectile.getWorld().equals(arena.getSpawn().getWorld())) {
                            if (projectile.getLocation().distance(members.getLocation()) < 5.0) {
                                projectile.remove();
                                return;
                            }
                        }
                    }

                    for(CustomNPC customNpc : this.plugin.getNpcManager().getNpcs()) {
                        if(customNpc.getNpcType() == NPCType.SHOP) {
                            NPC npc = customNpc.getNPC();

                            Location npcMax = npc.getStoredLocation().clone().add(1.0, 1.0, 1.0);
                            Location npcMin = npc.getStoredLocation().clone().subtract(1.0, 1.0, 1.0);
                            Cuboid cuboid = new Cuboid(npcMax, npcMin);

                            if(cuboid.isInRegion(projectile.getLocation())) {
                                projectile.remove();
                                return;
                            }
                        }
                    }

                    YamlConfig settings = new YamlConfig(this.plugin, "Settings");

                    for (Block block : Utils.getNearbyBlocks(projectile.getLocation(), (int)settings.getDouble("Settings.DefaultExplosionRange", 4)+1)) {
                        playerData.getArena().getBlockTracker().add(block.getState());
                        block.getDrops().clear();
                    }

                    player.getWorld().createExplosion(projectile.getLocation(), (float) settings.getDouble("Settings.DefaultExplosionRange", 4), settings.getBoolean("Settings.ExplosionFire", false));
                    projectile.remove();
                }
            }
        }
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        YamlConfig settings = new YamlConfig(this.plugin,"Settings");
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player);
        if (playerData != null && item != null && (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {
            if (playerData.getState() != PlayerState.LOBBY && playerData.getArena() != null && item.getType().name().equalsIgnoreCase(settings.getString("Items.PreGame.Leave.ID", "BED")) && item.getItemMeta().getDisplayName().equals(Utils.Color(settings.getString("Items.PreGame.Leave.Name", "&c&lLeave")))) {
                playerData.getArena().removePlayer(player, false);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player);
        if(playerData != null && playerData.getArena() != null && playerData.getState() == PlayerState.PLAYING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player);
        if (playerData != null && playerData.getState() != PlayerState.LOBBY && playerData.getArena() != null) {
            Arena arena = playerData.getArena();
            if (arena.getCuboid() != null && !arena.getCuboid().isInRegion(player) && player.getWorld().equals(arena.getCuboid().getWorld())) {
                if (arena.getState() == ArenaState.PLAYING) {
                    Team team = arena.getPlayerTeam(player);
                    player.teleport(team.getLoc());
                }
                else {
                    player.teleport(arena.getSpecs());
                }
            }
        }
    }
    
    @EventHandler
    public void onIgnite(BlockBurnEvent event) {
        Block block = event.getBlock();
        if (block != null) {
            Arena arena = this.plugin.getArenaManager().getArena(block.getLocation());
            if (arena != null && arena.getCuboid() != null && arena.getCuboid().isInRegion(block.getLocation())) {
                for (Team teams : arena.getTeams()) {
                    Location teamMax = teams.getLoc().clone().add(5.0, 2.0, 5.0);
                    Location teamMin = teams.getLoc().clone().subtract(5.0, 2.0, 5.0);
                    Cuboid cuboid = new Cuboid(teamMax, teamMin);
                    if (cuboid.isInRegion(block.getLocation())) {
                        event.setCancelled(true);
                        return;
                    }
                }
                if (block.getType() != Material.AIR) {
                    arena.getBlockTracker().add(block.getState());
                    block.getDrops().clear();
                }
            }
        }
    }
    
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        Block block = event.getBlock();
        if (block != null) {
            event.setYield(0.0f);
            Arena arena = this.plugin.getArenaManager().getArena(block.getLocation());
            if (arena != null) {
                for (Team teams : arena.getTeams()) {
                    Location teamMax = teams.getLoc().clone().add(5.0, 2.0, 5.0);
                    Location teamMin = teams.getLoc().clone().subtract(5.0, 2.0, 5.0);
                    Cuboid cuboid = new Cuboid(teamMax, teamMin);
                    if (cuboid.isInRegion(block.getLocation())) {
                        event.blockList().clear();
                        return;
                    }
                }

                for(CustomNPC customNpc : this.plugin.getNpcManager().getNpcs()) {
                    if(customNpc.getNpcType() == NPCType.SHOP) {
                        NPC npc = customNpc.getNPC();

                        Location npcMax = npc.getStoredLocation().clone().add(1.0, 1.0, 1.0);
                        Location npcMin = npc.getStoredLocation().clone().subtract(1.0, 1.0, 1.0);
                        Cuboid cuboid = new Cuboid(npcMax, npcMin);

                        if(cuboid.isInRegion(block.getLocation())) {
                            event.blockList().clear();
                            return;
                        }
                    }
                }

                if (arena.getCuboid() != null && arena.getCuboid().isInRegion(block.getLocation())) {
                    for (Block blocks : event.blockList()) {
                        if (blocks.getType() != Material.AIR) {
                            arena.getBlockTracker().add(blocks.getState());
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Arena arena = this.plugin.getArenaManager().getArena(event.getLocation());
        if (arena != null) {
            event.setYield(0.0f);

            for (Team teams : arena.getTeams()) {
                Location teamMax = teams.getLoc().clone().add(5.0, 2.0, 5.0);
                Location teamMin = teams.getLoc().clone().subtract(5.0, 2.0, 5.0);
                Cuboid cuboid = new Cuboid(teamMax, teamMin);
                if (cuboid.isInRegion(event.getLocation())) {
                    event.blockList().clear();
                    return;
                }
            }

            for(CustomNPC customNpc : this.plugin.getNpcManager().getNpcs()) {
                if(customNpc.getNpcType() == NPCType.SHOP) {
                    NPC npc = customNpc.getNPC();

                    Location npcMax = npc.getStoredLocation().clone().add(1.0, 1.0, 1.0);
                    Location npcMin = npc.getStoredLocation().clone().subtract(1.0, 1.0, 1.0);
                    Cuboid cuboid = new Cuboid(npcMax, npcMin);

                    if(cuboid.isInRegion(event.getLocation())) {
                        event.blockList().clear();
                        return;
                    }
                }
            }

            if (arena.getCuboid() != null && arena.getCuboid().isInRegion(event.getLocation())) {
                for (Block block : event.blockList()) {
                    if (block.getType() != Material.AIR) {
                        arena.getBlockTracker().add(block.getState());
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player);
        if (playerData != null && playerData.getState() != PlayerState.LOBBY && playerData.getArena() != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player);
        if (playerData != null && playerData.getState() != PlayerState.LOBBY && playerData.getArena() != null) {
            event.setCancelled(true);
        }
    }
}
