package dev.nahtan.teamSpawns.listeners;

import dev.nahtan.teamSpawns.TeamSpawns;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class DragonSpawnListener implements Listener {

    TeamSpawns plugin;

    public DragonSpawnListener(TeamSpawns plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDragonSpawn(EntitySpawnEvent e) {
        // ignore non dragons
        if(e.getEntityType() != EntityType.ENDER_DRAGON) return;
        // check world
        if(!e.getEntity().getWorld().getKey().equals(new NamespacedKey(plugin, "teamselectworld"))) return;
        // do not spawn
        e.setCancelled(true);
    }
}
