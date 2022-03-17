package com.gabrielhd.pirates.Menus.Submenus;

import com.gabrielhd.pirates.Arena.Arena;
import com.gabrielhd.pirates.Arena.ArenaState;
import com.gabrielhd.pirates.Config.YamlConfig;
import com.gabrielhd.pirates.Menus.Menu;
import com.gabrielhd.pirates.Pirates;
import com.gabrielhd.pirates.Utils.ItemUtils;
import com.gabrielhd.pirates.Utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class GamesMenu extends Menu {

    private static final int[] GLASS_SLOTS = new int[] {
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            9,                      17,
            18,					    26,
            27,                     35,
            36,                     44,
            45,46,47,48,49,50,51,52,53
    };

    private BukkitTask updateTask;

    private final Pirates plugin;

    public GamesMenu(Pirates plugin) {
        super(plugin, new YamlConfig(plugin, "Settings").getString("Menu.Games.Title"), 6);

        this.plugin = plugin;

        this.updateMenu();
    }

    @Override
    public void onClose(Player player, InventoryCloseEvent event) {
        if(updateTask != null) {
            updateTask.cancel();
        }
        updateTask = null;
    }

    @Override
    public void onOpen(Player player, InventoryOpenEvent event) {
        this.updateMenu();
        updateTask = new BukkitRunnable() {

            @Override
            public void run() {
                if (getInventory().getViewers().isEmpty()) {
                    this.cancel();
                    updateTask = null;
                    return;
                }
                updateMenu();
            }
        }.runTaskTimerAsynchronously(this.plugin, 10L, 10L);
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        YamlConfig messages = new YamlConfig(this.plugin, "Messages");

        ItemStack currentItem = event.getCurrentItem();
        if(currentItem.getType() == Material.BARRIER || currentItem.getType() == (Utils.is1_13_Latest() ? Material.GRAY_STAINED_GLASS_PANE : Material.getMaterial("STAINED_GLASS_PANE"))) {
            return;
        } else if (event.getSlot() == 49) {
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

        String name = ChatColor.stripColor(currentItem.getItemMeta().getDisplayName());
        Arena arena = this.plugin.getArenaManager().getArena(name);
        if (arena == null || arena.getState() == ArenaState.PLAYING) {
            player.sendMessage(Utils.Color(messages.getString("ArenaNoAvailable")));
            return;
        }
        arena.addPlayer(player);
    }

    public void updateMenu() {
        YamlConfig settings = new YamlConfig(this.plugin, "Settings");

        for(int i : GLASS_SLOTS) {
            this.setItem(i, ItemUtils.createItem(Utils.is1_13_Latest() ? Material.GRAY_STAINED_GLASS_PANE : Material.getMaterial("STAINED_GLASS_PANE"), (short)14, 1, ""));
        }

        this.setItem(49, ItemUtils.createItem(Material.COMPASS, 1, settings.getString("Menu.Games.Random")));

        int slot = 10;
        for(Arena game : this.plugin.getArenaManager().getArenas().values()) {
            if (slot == 17 || slot == 26 || slot == 35) {
                slot +=2;
            }
            if (slot >= 44) {
                break;
            }

            List<String> lore = settings.getStringList("Menu.Games.GameState");
            this.getInventory().setItem(slot++, getItem(game, lore));
        }

        for (int i = 0; i < 45; i++) {
            ItemStack item = getInventory().getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                continue;
            }
            this.setItem(i, ItemUtils.createItem(Material.BARRIER, 1, settings.getString("Menu.Games.Looking")));
        }
    }

    private ItemStack getItem(Arena game, List<String> lore) {
        ItemStack item = new ItemStack(Utils.is1_13_Latest() ? Material.FIRE_CHARGE : Material.getMaterial("FIREWORK_CHARGE"));
        if(item.getItemMeta() instanceof FireworkEffectMeta) {
            FireworkEffectMeta fm = (FireworkEffectMeta) item.getItemMeta();
            FireworkEffect.Builder fe = FireworkEffect.builder().flicker(false).trail(false);
            if (game.getState() == ArenaState.WAITING) {
                fe.withColor(Color.LIME);
                fe.withFade(Color.LIME);
            } else if (game.getState() == ArenaState.STARTING) {
                if (!game.isFull()) {
                    fe.withColor(Color.YELLOW);
                    fe.withFade(Color.YELLOW);
                } else {
                    fe.withColor(Color.ORANGE);
                    fe.withFade(Color.ORANGE);
                }
            } else if (game.getState() == ArenaState.PLAYING) {
                fe.withColor(Color.RED);
                fe.withFade(Color.RED);
            } else {
                fe.withColor(Color.WHITE);
                fe.withFade(Color.WHITE);
            }
            fm.setEffect(fe.build());
            fm.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&d" + game.getName()));
            for (int i = 0; i < lore.size(); i++) {
                lore.set(i, Utils.Color(lore.get(i).replace("%map%", game.getName()).replace("%players%", String.valueOf(game.getAlivePlayers().size())).replace("%max%", String.valueOf(game.getMaxPlayers())).replace("%state%", game.getState().name())));
            }
            fm.setLore(lore);
            fm.addItemFlags(ItemFlag.values());
            item.setItemMeta(fm);
        }
        return item;
    }
}
