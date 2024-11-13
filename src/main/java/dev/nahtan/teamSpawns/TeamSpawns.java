package dev.nahtan.teamSpawns;

import dev.nahtan.teamSpawns.data.TeamManager;
import dev.nahtan.teamSpawns.data.TeamSelector;
import dev.nahtan.teamSpawns.listeners.DismountListener;
import dev.nahtan.teamSpawns.listeners.JoinListener;
import dev.nahtan.teamSpawns.listeners.PlayerRespawnListener;
import dev.nahtan.teamSpawns.world.VoidBiomeProvider;
import dev.nahtan.teamSpawns.world.VoidChunkGenerator;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;
import org.bukkit.block.data.type.SculkSensor;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.EnderDragon;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class TeamSpawns extends JavaPlugin {

    public static Logger LOGGER;
    private TeamManager manager;
    private TeamSelector selector;

    @Override
    public void onLoad() {
        LOGGER = getLogger();
    }

    @Override
    public void onEnable() {
        LOGGER.info("Loading...");
        manager = new TeamManager(this);
        selector = new TeamSelector(this);
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new DismountListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(this), this);
        // generate void world for selecting teams
        genVoidWorld();
        LOGGER.info("Finished loading!");
    }

    public World genVoidWorld() {
        NamespacedKey key = new NamespacedKey(this, "teamselectworld");
        World world = Bukkit.getWorld(key);
        if(world != null) {
            setupVoidWorld(Bukkit.getWorld(key));
            return world;
        }
        WorldCreator creator = new WorldCreator(key);
        creator.generateStructures(false);
        creator.keepSpawnLoaded(TriState.FALSE);
        creator.biomeProvider(new VoidBiomeProvider());
        creator.generator(new VoidChunkGenerator());
        creator.environment(World.Environment.THE_END);
        world = creator.createWorld();
        setupVoidWorld(world);
        return world;
    }
    private void setupVoidWorld(World world) {
        if(world == null) {
            LOGGER.severe("Failed to create team select world. Something has gone seriously wrong!");
            return;
        }
        world.setDifficulty(Difficulty.PEACEFUL);
        world.setTime(0);
        world.setPVP(false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, Boolean.FALSE);
        world.setGameRule(GameRule.DO_INSOMNIA, Boolean.FALSE);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, Boolean.FALSE);

        // remove dragon fight
        DragonBattle battle = world.getEnderDragonBattle();
        if(battle == null) return;
        EnderDragon dragon = battle.getEnderDragon();
        if(dragon == null) return;
        dragon.setHealth(0D);
        Location podiumLOC = dragon.getPodium();
        Location topLeftLOC = podiumLOC.add(5, 5, 5);
        Location bottomRightLOC = podiumLOC.add(-5, -5, -5);

    }

    @Override
    public void onDisable() {
        LOGGER.info("Goodbye!");
    }

    public TeamManager getTeamManager() {
        return manager;
    }

    public TeamSelector getTeamSelector() {
        return selector;
    }
}
