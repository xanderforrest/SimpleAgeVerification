package com.xanderforrest.AgeVerification;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class AgeVerification extends JavaPlugin {
    final FileConfiguration config = this.getConfig();


    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("On enable called");

        config.options().copyDefaults(true);
        saveConfig();

        getServer().getPluginManager().registerEvents(new MyListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("On disable called");
        saveConfig();
    }
}
