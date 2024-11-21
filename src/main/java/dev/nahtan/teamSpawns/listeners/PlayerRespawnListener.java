package dev.nahtan.teamSpawns.listeners;

import dev.nahtan.teamSpawns.TeamSpawns;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import java.util.Set;

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

        // only affect spawn point if player hasn't set their spawn
        Set<PlayerRespawnEvent.RespawnFlag> flags = event.getRespawnFlags();
        if(flags.contains(PlayerRespawnEvent.RespawnFlag.BED_SPAWN) || flags.contains(PlayerRespawnEvent.RespawnFlag.ANCHOR_SPAWN)) {
            return;
        }

        event.setRespawnLocation(plugin.getTeamManager().getTeamSpawnLoc(teamName));
    }
}
