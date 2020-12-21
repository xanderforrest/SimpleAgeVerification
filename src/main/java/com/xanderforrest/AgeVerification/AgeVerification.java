package com.xanderforrest.AgeVerification;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class AgeVerification extends JavaPlugin {
    final FileConfiguration config = this.getConfig();
    final Map<String, Boolean> unverifiedCache = new HashMap<>();


    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Plugin enabled.");

        config.options().copyDefaults(true);
        config.addDefault("verification-question", "Welcome to the server! How old are you? (e.g. 21)");
        config.addDefault("test-old-players", false);
        saveConfig();

        getServer().getPluginManager().registerEvents(new MyListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Shutting down. Saving configuration.");
        saveConfig();
        getLogger().info("Saved configuration.");
    }
}
