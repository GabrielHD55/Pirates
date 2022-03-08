package com.gabrielhd.pirates.Database;

import com.gabrielhd.pirates.Config.*;
import com.gabrielhd.pirates.Database.Types.*;
import com.gabrielhd.pirates.Pirates;
import org.bukkit.configuration.file.*;

public class Database
{
    private static DataHandler storage;
    
    public Database(Pirates plugin) {
        FileConfiguration data = new YamlConfig(plugin, "Settings");
        if (data.getString("StorageType", "SQLite").equalsIgnoreCase("MySQL")) {
            Database.storage = new MySQL(plugin, data.getString("MySQL.Host"), data.getString("MySQL.Port"), data.getString("MySQL.Database"), data.getString("MySQL.Username"), data.getString("MySQL.Password"));
        }
        else {
            Database.storage = new SQLite(plugin);
        }
    }
    
    public static DataHandler getStorage() {
        return Database.storage;
    }
}
