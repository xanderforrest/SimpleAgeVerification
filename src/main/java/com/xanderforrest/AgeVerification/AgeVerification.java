package com.xanderforrest.AgeVerification;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.UUID;
import java.util.HashSet;

public final class AgeVerification extends JavaPlugin {
    final FileConfiguration config = getConfig();
    final Set<UUID> unverified = new HashSet<>();

    @Override
    public void onEnable() {
        getLogger().info("Plugin enabled.");

        config.options().copyDefaults(true);
        saveConfig();

        getServer().getPluginManager().registerEvents(new EventListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Shutting down. Saving configuration.");
        saveConfig();
        getLogger().info("Saved configuration.");
    }
}
