package dev.nahtan.teamSpawns.listeners;

import dev.nahtan.teamSpawns.TeamSpawns;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;

public class PlayerDismountListener implements Listener {

    TeamSpawns plugin;

    public PlayerDismountListener(TeamSpawns plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDismount(EntityDismountEvent event) {
        // ignore non-players
        if(!(event.getEntity() instanceof Player player)) return;
        // cancel event if the player is in the selection world
        event.setCancelled(player.getWorld().getKey().equals(new NamespacedKey(plugin, "teamselectworld")));
    }

}
