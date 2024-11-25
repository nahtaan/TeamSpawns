package dev.nahtan.teamSpawns.commands;

import dev.nahtan.teamSpawns.TeamSpawns;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TeamChatCommand extends Command {
    TeamSpawns plugin;

    public TeamChatCommand(TeamSpawns plugin) {
        super("teamchat");
        this.plugin = plugin;
        setDescription("Send a message only to your team!");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage("This command must be run by a player!");
            return false;
        }
        // no args means toggle the player's team chat status
        if(args.length == 0) {
            // check that the player has a team
            if(plugin.getTeamManager().getTeamNameFromPlayer(player) == null) return false;

            // toggle the status
            boolean teamChat = plugin.getChatManager().isPlayerInTeamChat(player);
            if(teamChat) {
                player.sendMessage(MiniMessage.miniMessage().deserialize(plugin.getConfig().getString("leave-team-chat-msg", "")));
                plugin.getChatManager().setPlayerInTeamChat(player, false);
                return true;
            }
            player.sendMessage(MiniMessage.miniMessage().deserialize(plugin.getConfig().getString("join-team-chat-msg", "")));
            plugin.getChatManager().setPlayerInTeamChat(player, true);
            return true;
        }

        // otherwise treat the args as the message itself
        StringBuilder sb = new StringBuilder();
        for(String str: args) {
            sb.append(str).append(' ');
        }
        plugin.getChatManager().handleChat(player, sb.substring(0, sb.length()-1));
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return List.of("<message>");
    }
}
