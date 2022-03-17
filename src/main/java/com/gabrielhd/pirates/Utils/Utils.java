package com.gabrielhd.pirates.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static String Color(String s) {
        return s.replaceAll("&", "§");
    }

    public static boolean is1_8() {
        String serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1);
        return serverVersion.toLowerCase().startsWith("1_8".toLowerCase());
    }

    public static boolean is1_9_1_12() {
        String serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1);
        return serverVersion.toLowerCase().startsWith("1_9".toLowerCase()) || serverVersion.toLowerCase().startsWith("1_10".toLowerCase()) || serverVersion.toLowerCase().startsWith("1_11".toLowerCase()) || serverVersion.toLowerCase().startsWith("1_12".toLowerCase());
    }

    public static boolean is1_13_Latest() {
        String serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1);
        return serverVersion.toLowerCase().startsWith("1_13".toLowerCase()) || serverVersion.toLowerCase().startsWith("1_14".toLowerCase()) || serverVersion.toLowerCase().startsWith("1_15".toLowerCase()) || serverVersion.toLowerCase().startsWith("1_16".toLowerCase()) || serverVersion.toLowerCase().startsWith("1_17".toLowerCase()) || serverVersion.toLowerCase().startsWith("1_18".toLowerCase());
    }
    
    public static List<Block> getNearbyBlocks(Location location, int radius) {
        List<Block> blocks = new ArrayList<>();
        for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; ++x) {
            for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; ++y) {
                for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; ++z) {
                    Block block = location.getWorld().getBlockAt(x, y, z);
                    if (block.getType() != Material.AIR) {
                        blocks.add(block);
                    }
                }
            }
        }
        return blocks;
    }
}
