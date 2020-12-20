package com.xanderforrest.AgeVerification;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
            plugin.unverifiedCache.put(event.getPlayer().getUniqueId().toString(), true);
        } else { // catch players who leave and rejoin to avoid verifying age
            plugin.unverifiedCache.put(event.getPlayer().getUniqueId().toString(),
                    plugin.getConfig().getBoolean("frozen." + event.getPlayer().getUniqueId()));
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        plugin.unverifiedCache.remove(event.getPlayer().getUniqueId().toString());
    }

    @EventHandler
    public void chatEvent(AsyncPlayerChatEvent event) {
        if (plugin.unverifiedCache.get(event.getPlayer().getUniqueId().toString())) {
            event.setCancelled(true);
            String message = event.getMessage();
            int age;
            try {
                age = Integer.parseInt(message);
            } catch (NumberFormatException e) {
                event.getPlayer().sendMessage(ChatColor.RED + "Please format your response as just your age (e.g 21)");
                return;
            }

            if (age < 16) {
                Bukkit.getBanList(BanList.Type.NAME).addBan(event.getPlayer().getName(), "You must be over 16 to join this server.", null, "Age verification plugin");

                Bukkit.getScheduler().runTask(plugin, () -> event.getPlayer().kickPlayer("You must be over 16 to join this server."));

                Bukkit.broadcastMessage(ChatColor.YELLOW + event.getPlayer().getName() + " was banned for being under 16.");
            } else {
                plugin.unverifiedCache.put(event.getPlayer().getUniqueId().toString(), false);
                plugin.getConfig().set("frozen." + event.getPlayer().getUniqueId(), false);
                event.getPlayer().sendMessage(ChatColor.GREEN + "You are no longer frozen!");
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (plugin.unverifiedCache.get(event.getPlayer().getUniqueId().toString())) {
            event.setCancelled(true); // No movement for you
            event.getPlayer().sendMessage(ChatColor.RED + "You are frozen! Enter your age in chat to be thawed!");
        }
    }
}
