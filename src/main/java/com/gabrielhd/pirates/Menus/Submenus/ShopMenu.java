package com.gabrielhd.pirates.Menus.Submenus;

import com.gabrielhd.pirates.Menus.Menu;
import com.gabrielhd.pirates.Pirates;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class ShopMenu extends Menu {

    public ShopMenu(Pirates plugin, String name, int rows) {
        super(plugin, name, rows);
    }

    @Override
    public void onClose(Player player, InventoryCloseEvent event) {

    }

    @Override
    public void onOpen(Player player, InventoryOpenEvent event) {

    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {

    }
}
