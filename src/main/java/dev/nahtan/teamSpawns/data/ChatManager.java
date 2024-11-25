package dev.nahtan.teamSpawns.data;

import dev.nahtan.teamSpawns.TeamSpawns;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Set;

public class ChatManager {

    TeamSpawns plugin;
    // a map of player UUIDs to their team chat status
    private final HashMap<String, Boolean> toggleStatus = new HashMap<>();

    public ChatManager(TeamSpawns plugin) {
        this.plugin = plugin;
    }

    public boolean isPlayerInTeamChat(Player player) {
        String uuid = player.getUniqueId().toString();
        if(!toggleStatus.containsKey(uuid)) {
            return false;
        }
        return toggleStatus.get(uuid);
    }

    public void setPlayerInTeamChat(Player player, boolean isInTeamChat) {
        toggleStatus.put(player.getUniqueId().toString(), isInTeamChat);
    }

    public void handleChat(Player player, String chatText) {
        String teamName = plugin.getTeamManager().getTeamNameFromPlayer(player);
        Set<Player> recipients = plugin.getTeamManager().getOnlinePlayers(teamName);
        String format = plugin.getConfig().getString("era-chat-format", "");
        format = format.replace("%name%", player.getName());
        Component component = MiniMessage.miniMessage().deserialize(format);
        component = component.replaceText(TextReplacementConfig.builder()
                        .match("%message%")
                        .replacement(chatText)
                .build());
        Audience.audience(recipients).sendMessage(component);
    }
}
