package dev.nahtan.teamSpawns.listeners;

import dev.nahtan.teamSpawns.TeamSpawns;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {

    private final TeamSpawns plugin;

    public PlayerRespawnListener(TeamSpawns plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        String teamName = plugin.getTeamManager().getTeamNameFromPlayer(player);
        // ignore unknown players
        if(teamName == null) {
            return;
        }
        event.setRespawnLocation(plugin.getTeamManager().getTeamSpawnLoc(teamName));
    }
}
