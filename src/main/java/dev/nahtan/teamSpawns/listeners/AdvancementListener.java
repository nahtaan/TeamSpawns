package dev.nahtan.teamSpawns.listeners;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import dev.nahtan.teamSpawns.TeamSpawns;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class AdvancementListener implements Listener {
    TeamSpawns plugin;

    public AdvancementListener(TeamSpawns plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAdvancement(PlayerAdvancementCriterionGrantEvent event) {
        // do not allow advancements in the selection world
        event.setCancelled(event.getPlayer().getWorld().getKey().equals(new NamespacedKey(plugin, "teamselectworld")));
    }
}
