package dev.nahtan.teamSpawns.listeners;

import dev.nahtan.teamSpawns.TeamSpawns;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;

public class DismountListener implements Listener {

    TeamSpawns plugin;

    public DismountListener(TeamSpawns plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDismount(EntityDismountEvent event) {
        // ignore non-players
        if(!(event.getEntity() instanceof Player player)) return;
        // cancel event if the player is selecting
        event.setCancelled(plugin.getTeamSelector().isPlayerSelecting(player));
    }

}
