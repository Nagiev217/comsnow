package com.snowcom.plugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class SnowComPlugin extends JavaPlugin {

    private static SnowComPlugin instance;
    private SnowComConfig pluginConfig;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        pluginConfig = new SnowComConfig(getConfig());
        SnowComCommand command = new SnowComCommand(this);
        getCommand("com").setExecutor(command);
        getCommand("com").setTabCompleter(command);
        getLogger().info("SnowCom plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SnowCom plugin disabled!");
    }

    public static SnowComPlugin getInstance() {
        return instance;
    }

    public SnowComConfig getPluginConfig() {
        return pluginConfig;
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        pluginConfig = new SnowComConfig(getConfig());
    }
}
