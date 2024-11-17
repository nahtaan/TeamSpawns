package dev.nahtan.teamSpawns.papi;

import dev.nahtan.teamSpawns.TeamSpawns;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PAPIExpansion extends PlaceholderExpansion {

    TeamSpawns plugin;

    public PAPIExpansion(TeamSpawns plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "teamspawns";
    }

    @Override
    public @NotNull String getAuthor() {
        return "_nahtan.";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        String teamName = plugin.getTeamManager().getTeamNameFromPlayer(player);
        switch (params) {
            case "teamname" -> {
                return teamName == null ? "" : teamName;
            }
            case "coloured_teamname" -> {
                String colourCode = plugin.getTeamManager().getTeamColourCode(teamName);
                return "<" + colourCode + ">" + teamName;
            }
            case "teamcolour" -> {
                String colourCode = plugin.getTeamManager().getTeamColourCode(teamName);
                return "<" + colourCode + ">";
            }
        }
        return null;
    }
}
