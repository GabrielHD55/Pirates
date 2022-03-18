package com.gabrielhd.pirates.Utils;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemUtils {

    public static ItemStack createItem(String material, String name, int amount, List<String> lore, int data) {
        ItemStack item;
        if(Utils.is1_13_Latest()) {
            item = new ItemStack(Material.getMaterial(material), amount);
        } else {
            item = new ItemStack(Material.getMaterial(material), amount, (byte) data);
        }

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
        ItemStack item;
        if(Utils.is1_13_Latest()) {
            item = new ItemStack(id, amount);
        } else {
            item = new ItemStack(id, amount, (byte) data);
        }

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Utils.Color(name));
        item.setItemMeta(meta);
        return item;
    }
}
