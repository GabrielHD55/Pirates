package com.gabrielhd.pirates.Config;

import org.bukkit.configuration.file.*;
import java.io.*;
import com.gabrielhd.pirates.*;

public class YamlConfig extends YamlConfiguration
{
    private final File file;
    private final String path;
    private final Pirates plugin;
    
    public YamlConfig(Pirates plugin, String path) {
        this.plugin = plugin;

        this.path = path + ".yml";
        this.file = new File(plugin.getDataFolder(), this.path);
        this.saveDefault();
        this.reload();

        this.options().parseComments(true);
    }
    
    public void reload() {
        try {
            super.load(this.file);
        }
        catch (Exception ignored) {}
    }
    
    public void save() {
        try {
            super.save(this.file);
        }
        catch (Exception ignored) {}
    }
    
    public void saveDefault() {
        try {
            if (!this.file.exists()) {
                if (plugin.getResource(this.path) != null) {
                    plugin.saveResource(this.path, false);
                }
                else {
                    this.file.createNewFile();
                }
            }
        }
        catch (Exception ignored) {}
    }
}
