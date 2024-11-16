package dev.nahtan.teamSpawns.listeners;

import dev.nahtan.teamSpawns.TeamSpawns;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    TeamSpawns plugin;

    public PlayerQuitListener(TeamSpawns plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String teamName = plugin.getTeamManager().getTeamNameFromPlayer(player);
        // ignore players that have a team
        if(teamName != null) {
            return;
        }

        // handle the selection process
        plugin.getTeamSelector().handleEarlyPlayerQuit(player);
    }
}
