package com.gabrielhd.pirates.Utils;

import org.bukkit.inventory.*;
import org.bukkit.*;
import com.gabrielhd.pirates.*;
import com.google.common.collect.*;
import org.bukkit.inventory.meta.*;
import java.util.*;

public class ItemUtils
{
    public static ItemStack createItem(String material, String name, int amount, List<String> lore) {
        ItemStack item = new ItemStack(Material.getMaterial(material), amount);
        ItemMeta meta = item.getItemMeta();
        if (name != null) {
            if (name.isEmpty()) {
                name = "&f";
            }
            meta.setDisplayName(Utils.Color(name));
        }
        if (lore != null && !lore.isEmpty()) {
            List<String> lores = Lists.newArrayList();
            for (String l : lore) {
                lores.add(Utils.Color(l));
            }
            meta.setLore(lores);
        }
        item.setItemMeta(meta);
        return item;
    }
    
    public static ItemStack createItem(Material id, int amount, String name, int data) {
        ItemStack item = new ItemStack(id, amount, (byte)data);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Utils.Color(name));
        item.setItemMeta(meta);
        return item;
    }
    
    public static ItemStack createItem(Material id, int amount, String name) {
        ItemStack item = new ItemStack(id, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Utils.Color(name));
        item.setItemMeta(meta);
        return item;
    }
}
