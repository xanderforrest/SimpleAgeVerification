package com.xanderforrest.AgeVerification;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class MyListener implements Listener {
    AgeVerification plugin;

    public MyListener(AgeVerification instance) {
        plugin = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPlayedBefore()) {
            event.getPlayer().sendMessage(ChatColor.BLUE + "Welcome to Craft A Cow! How old are you?");
            plugin.getConfig().addDefault("frozen." + event.getPlayer().getUniqueId(), true);
        }
    }

    @EventHandler
    public void chatEvent(AsyncPlayerChatEvent event) {
        if (plugin.getConfig().getBoolean("frozen." + event.getPlayer().getUniqueId())) {
            String message = event.getMessage();
            int age;
            try {
                age = Integer.parseInt(message);
            } catch (NumberFormatException e) {
                event.getPlayer().sendMessage(ChatColor.RED + "Please format your response as just your age (e.g 21)");
                event.setCancelled(true);
                return;
            }

            if (age <= 16) {
                Bukkit.getBanList(BanList.Type.NAME).addBan(event.getPlayer().getName(), "You must be over 16 to join this server.", null, "Age verification plugin");
            } else {
                plugin.getConfig().set("frozen." + event.getPlayer().getUniqueId(), false);
                event.getPlayer().sendMessage(ChatColor.GREEN + "You are no longer frozen!");
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (plugin.getConfig().getBoolean("frozen." + event.getPlayer().getUniqueId())) {
            event.setCancelled(true); // No movement for you
            event.getPlayer().sendMessage(ChatColor.RED + "You are frozen! Enter your age in chat to be thawed!");
        }
    }
}