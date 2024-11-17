package dev.nahtan.teamSpawns.commands;

import dev.nahtan.teamSpawns.TeamSpawns;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class TeamSpawnsCommand extends Command {

    TeamSpawns plugin;

    public TeamSpawnsCommand(TeamSpawns plugin) {
        super("teamspawns");
        this.plugin = plugin;
        setDescription("Base admin command with various options.");
        setPermission("teamspawns.admin");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if(args.length == 0) {
            sender.sendMessage(Component.text("Unknown subcommand.").color(TextColor.fromHexString("#ff0000")));
            return false;
        }
        switch (args[0]) {
            case "removeteam" -> {
                if(args.length < 2) {
                    sender.sendMessage(Component.text("Please provide a target player.").color(TextColor.fromHexString("#ff0000")));
                    return false;
                }
                String playerName = args[1];
                Player player = Bukkit.getPlayerExact(playerName);
                if(player == null) {
                    sender.sendMessage(Component.text("The given player could not be found or is not online.").color(TextColor.fromHexString("#ff0000")));
                    return false;
                }
                plugin.getTeamSelector().handleEarlyPlayerQuit(player);
                plugin.getTeamManager().removeTeamName(player);
                plugin.getTeamSelector().handleTeamSelect(player);
                sender.sendMessage(Component.text("Successfully reset player's team.").color(TextColor.fromHexString("#00ff00")));
                return true;
            }
            case "reload" -> {
                plugin.reloadConfig();
                if(plugin.getTeamManager().loadTeamsFromConfig()) {
                    sender.sendMessage(Component.text("Successfully reloaded configuration file.").color(TextColor.fromHexString("#00ff00")));
                }else {
                    sender.sendMessage(Component.text("Failed to reload config file.").color(TextColor.fromHexString("#ff0000")));
                }
                return true;
            }
        }
        sender.sendMessage(Component.text("Unknown subcommand.").color(TextColor.fromHexString("#ff0000")));
        return false;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if(args.length == 1) {
            return List.of("removeteam", "reload");
        }
        //noinspection SwitchStatementWithTooFewBranches
        switch(args[0]) {
            case "removeteam" -> {
                if(args.length == 2) {
                    return Bukkit.getServer().getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(name -> name.startsWith(args[1]))
                            .collect(Collectors.toList());
                }else {
                    return Bukkit.getServer().getOnlinePlayers().stream()
                            .map(Player::getName)
                            .collect(Collectors.toList());
                }
            }
            default -> {return List.of();}
        }
    }
}
