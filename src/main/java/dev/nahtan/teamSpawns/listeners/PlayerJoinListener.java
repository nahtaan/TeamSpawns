package dev.nahtan.teamSpawns.listeners;

import dev.nahtan.teamSpawns.TeamSpawns;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final TeamSpawns plugin;

    public PlayerJoinListener(TeamSpawns plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        String teamName = plugin.getTeamManager().retrieveTeamNameFromPlayer(player);
        // ignore players that already have a team
        if(teamName != null) {
            plugin.getTeamManager().addOnlinePlayer(player, teamName);
            return;
        }

        // handle the selection process
        plugin.getTeamSelector().handleTeamSelect(player);
    }

}
