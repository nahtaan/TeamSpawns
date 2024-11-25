package dev.nahtan.teamSpawns.listeners;

import dev.nahtan.teamSpawns.TeamSpawns;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PlayerChatListener implements Listener {

    TeamSpawns plugin;

    public PlayerChatListener(TeamSpawns plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncChatEvent e) {
        // ignore plugin forced chats
        if(!e.isAsynchronous()) return;

        // do nothing if the player is not in team chat
        if(!plugin.getChatManager().isPlayerInTeamChat(e.getPlayer())) return;

        // let chat manager handle it
        plugin.getChatManager().handleChat(e.getPlayer(), e.signedMessage().message());
        e.setCancelled(true);
    }
}
