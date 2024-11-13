package dev.nahtan.teamSpawns.data;

import dev.nahtan.teamSpawns.TeamSpawns;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.*;

public class TeamSelector {
    private final TeamSpawns plugin;

    Map<UUID, PlayerInfo> currentPlayers = new HashMap<>();

    public TeamSelector(TeamSpawns plugin) {
        this.plugin = plugin;
    }

    public void handleTeamSelect(Player player) {
        // check that the selection world exists
        NamespacedKey key = new NamespacedKey(plugin, "teamselectworld");

        // try to get the team select world
        World selectionWorld = Bukkit.getWorld(key);
        if(selectionWorld == null) {
            selectionWorld = plugin.genVoidWorld();
            // failed to load world
            if(selectionWorld == null) {
                player.kick(MiniMessage.miniMessage().deserialize("<red>Failed to load team select world. Please contact an admin."));
                return;
            }
        }

        // make it so that the other players cannot see each other
        for(PlayerInfo info: currentPlayers.values()) {
            Player plr = info.player;
            plr.hidePlayer(plugin, player);
            player.hidePlayer(plugin, plr);
        }

        // create the armorstand and make the player sit on it
        Location standLOC = new Location(selectionWorld, 0, 0, 0);
        ArmorStand stand = selectionWorld.spawn(new Location(selectionWorld, 0, 0, 0), ArmorStand.class, armorStand -> {
            armorStand.setVisibleByDefault(false);
            armorStand.setGravity(false);
            armorStand.setInvulnerable(true);
            armorStand.setInvisible(true);
        });
        player.teleport(standLOC);
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        player.showEntity(plugin, stand);
        stand.addPassenger(player);

    }

    public boolean isPlayerSelecting(Player player) {
        return currentPlayers.containsKey(player.getUniqueId());
    }

    private static class PlayerInfo {
        private Player player;
        private ArmorStand stand;

        public PlayerInfo(Player player, ArmorStand stand) {
            this.player = player;
            this.stand = stand;
        }
    }
}
