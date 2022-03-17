package com.gabrielhd.pirates.Managers;

import com.gabrielhd.pirates.Config.YamlConfig;
import com.gabrielhd.pirates.Pirates;
import lombok.Getter;

public class ConfigManager {

    private final Pirates plugin;

    @Getter private final YamlConfig settings;
    @Getter private final YamlConfig messages;

    public ConfigManager(Pirates plugin) {
        this.plugin = plugin;

        this.settings = new YamlConfig(plugin, "Settings");
        this.messages = new YamlConfig(plugin, "Messages");
    }
}
