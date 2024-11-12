package dev.nahtan.teamSpawns.data;

import dev.nahtan.teamSpawns.TeamSpawns;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.awt.*;
import java.awt.Color;
import java.util.HashMap;
import java.util.Set;

public class TeamManager {
    NamespacedKey teamNameKey;

    TeamSpawns plugin;
    // a mapping of team names to their associated team info
    private final HashMap<String, TeamInfo> teamSpawns = new HashMap<>();

    public TeamManager(TeamSpawns plugin) {
        this.plugin = plugin;
        teamNameKey = new NamespacedKey(plugin, "team_name");
    }

    /**
     * Attempts to load the teams from the configuration file.
     * @return true - Success, false - Failure
     */
    public boolean loadTeamsFromConfig() {
        teamSpawns.clear(); // allow for reloading
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection teamsSection = config.getConfigurationSection("teams");
        if(teamsSection == null) {
            TeamSpawns.LOGGER.warning("Configuration file had no teams! The plugin is effectively useless in this state.");
            return false;
        }
        Set<String> teamKeys = teamsSection.getKeys(false);
        for(String key: teamKeys) {
            ConfigurationSection teamSection = teamsSection.getConfigurationSection(key);

            // this shouldn't ever happen since we got the keys from the previous section but is handled to avoid errors
            if(teamSection == null) return false;

            String name = teamSection.getString("name");
            String description = teamSection.getString("description");
            String colour = teamSection.getString("colour");
            double x = teamSection.getDouble("x");
            double y = teamSection.getDouble("y");
            double z = teamSection.getDouble("z");
            double yaw = teamSection.getDouble("yaw");
            double pitch = teamSection.getDouble("pitch");
            String world = teamSection.getString("world");

            if(!validateConfig(key, name, description, colour, world)) {
                continue;
            }

            if(teamSpawns.containsKey(name)) {
                TeamSpawns.LOGGER.warning("A duplicate team name has been found: '" + name + "'. The second instance of this team will be ignored.");
                continue;
            }
            TeamInfo info = new TeamInfo(name, description, colour, x, y, z, (float) yaw, (float) pitch, world);
            teamSpawns.put(name, info);
        }
        return !teamSpawns.isEmpty();
    }

    private boolean validateConfig(String key, String name, String description, String colour, String world) {
        boolean valid = true;
        if(name == null) {
            TeamSpawns.LOGGER.warning("Name has not been provided for team '" + key + "'. This team will be ignored.");
            valid = false;
        }
        if(description == null) {
            TeamSpawns.LOGGER.warning("Description has not been provided for team '" + key + "'. This team will be ignored.");
            valid = false;
        }
        if(colour == null) {
            TeamSpawns.LOGGER.warning("Colour has not been provided for team '" + key + "'. This team will be ignored.");
            valid = false;
        }else {
            try {
                Color.decode(colour);
            } catch (NumberFormatException e) {
                TeamSpawns.LOGGER.warning("The colour code provided for team '" + key + "' was invalid. This team will be ignored.");
                valid = false;
            }
        }
        if(world == null) {
            TeamSpawns.LOGGER.warning("World has not been provided for team '" + key + "'. This team will be ignored.");
            valid = false;
        }
        return valid;
    }

    public String getTeamName(Player player) {
        return player.getPersistentDataContainer().get(teamNameKey, PersistentDataType.STRING);
    }

    public boolean doesTeamExist(String teamName) {
        return teamSpawns.containsKey(teamName);
    }

    public Location getTeamSpawnLoc(String teamName) {
        if(!doesTeamExist(teamName)) {
            return null;
        }
        TeamInfo info = teamSpawns.get(teamName);
        World world = Bukkit.getWorld(info.world);
        if(world == null) {
            TeamSpawns.LOGGER.severe("Could not find spawn world for team '" + info.name + "', world: '" + info.world + "'. Defaulting to first world that can be found.");
            world = Bukkit.getWorlds().getFirst();
        }
        return new Location(world, info.x, info.y, info.z, info.yaw, info.pitch);
    }

    private static class TeamInfo {
        String name;
        String description;
        String colourCode;
        double x;
        double y;
        double z;
        float yaw;
        float pitch;
        String world;

        public TeamInfo(String name, String description, String colourCode, double x, double y, double z, float yaw, float pitch, String world) {
            this.name = name;
            this.description = description;
            this.colourCode = colourCode;
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
            this.world = world;
        }
    }
}
