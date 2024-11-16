package dev.nahtan.teamSpawns;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import dev.nahtan.teamSpawns.commands.TeamSpawnsCommand;
import dev.nahtan.teamSpawns.data.TeamManager;
import dev.nahtan.teamSpawns.data.TeamSelector;
import dev.nahtan.teamSpawns.listeners.*;
import dev.nahtan.teamSpawns.world.VoidBiomeProvider;
import dev.nahtan.teamSpawns.world.VoidChunkGenerator;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.logging.Logger;

public final class TeamSpawns extends JavaPlugin {

    public static Logger LOGGER;
    private TeamManager manager;
    private TeamSelector selector;
    PlayerClickListener playerClickListener;

    @Override
    public void onLoad() {
        LOGGER = getLogger();
        // PacketEvents
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        LOGGER.info("Loading...");
        saveDefaultConfig(); // save config
        manager = new TeamManager(this);
        if(!manager.loadTeamsFromConfig()) {
            LOGGER.severe("Failed to load configuration file. Plugin effectively disabled.");
            return;
        }
        selector = new TeamSelector(this);

        // register bukkit events
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDismountListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(this), this);
        getServer().getPluginManager().registerEvents(new DragonSpawnListener(this), this);
        playerClickListener = new PlayerClickListener(this);
        getServer().getPluginManager().registerEvents(playerClickListener, this);
        getServer().getPluginManager().registerEvents(new AdvancementListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);

        // register PacketEvents
        PacketEvents.getAPI().getEventManager().registerListener(
                new PlayerLookListener(this), PacketListenerPriority.NORMAL);

        // PacketEvents init
        PacketEvents.getAPI().init();

        // generate void world for selecting teams
        genVoidWorld();

        // register commands
        registerCommands();

        LOGGER.info("Finished loading!");
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        LOGGER.info("Goodbye!");
    }

    private void registerCommands() {
        getServer().getCommandMap().register(getName(), new TeamSpawnsCommand(this));
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
        world.setGameRule(GameRule.DO_MOB_SPAWNING, Boolean.FALSE);
        world.setChunkForceLoaded(8, 8, true);
    }

    public PlayerClickListener getPlayerClickListener() {
        return playerClickListener;
    }

    public TeamManager getTeamManager() {
        return manager;
    }

    public TeamSelector getTeamSelector() {
        return selector;
    }

    public static ItemStack getSkull(String texture) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), null);
        profile.getProperties().add(new ProfileProperty("textures", texture));
        skullMeta.setPlayerProfile(profile);
        skull.setItemMeta(skullMeta);
        return skull;
    }
}
