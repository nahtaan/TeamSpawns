package dev.nahtan.teamSpawns.data;

import dev.nahtan.teamSpawns.TeamSpawns;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class TeamSelector {
    private final TeamSpawns plugin;

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

    }


}
