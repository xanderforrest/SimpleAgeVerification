package com.xanderforrest.AgeVerification;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class EventListener implements Listener {
    AgeVerification plugin;

    public EventListener(AgeVerification instance) {
        plugin = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        FileConfiguration config = plugin.getConfig();

        if (!player.hasPlayedBefore()) {

            player.sendMessage(ChatColor.YELLOW +
                    config.getString("verification-question"));

            config.set("frozen." + event.getPlayer().getUniqueId(), true);
            plugin.unverified.add(player.getUniqueId());

        } else { // catch players who leave and rejoin to avoid verifying age

            if (config.getBoolean("frozen." + player.getUniqueId())) {
                plugin.unverified.add(player.getUniqueId());
            }

        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        plugin.unverified.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void chatEvent(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();

        if (plugin.unverified.contains(player.getUniqueId())) {
            event.setCancelled(true);

            String message = event.getMessage();
            int age;

            try {
                age = Integer.parseInt(message);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Please format your response as just your age (e.g 21)");
                return;
            }

            if (age < 16) {

                Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), "You must be over 16 to join this server.", null, "Age verification plugin");

                Bukkit.getScheduler().runTask(plugin, () -> player.kickPlayer("You must be over 16 to join this server."));

                Bukkit.broadcastMessage(ChatColor.RED + player.getName() + " was banned for being under 16.");

            } else {

                // They've passed the check, so let's remove them from the unverified list and update config
                plugin.unverified.remove(player.getUniqueId());

                plugin.getConfig().set("frozen." + event.getPlayer().getUniqueId(), false);

                player.sendMessage(ChatColor.GREEN + "You are no longer frozen!");

            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (plugin.unverified.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true); // No movement for you
            event.getPlayer().sendMessage(ChatColor.RED + "You are frozen! Enter your age in chat to be thawed!");
        }
    }
}
