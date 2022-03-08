package com.gabrielhd.pirates.Utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static String Color(String s) {
        return s.replaceAll("&", "§");
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
