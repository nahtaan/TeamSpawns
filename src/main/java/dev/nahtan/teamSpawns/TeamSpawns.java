package dev.nahtan.teamSpawns;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class TeamSpawns extends JavaPlugin {

    public static Logger LOGGER;

    @Override
    public void onLoad() {
        LOGGER = getLogger();
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
    }
}
