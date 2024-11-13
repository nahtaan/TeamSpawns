package dev.nahtan.teamSpawns.data;

import dev.nahtan.teamSpawns.TeamSpawns;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.*;

public class TeamSelector {
    private final TeamSpawns plugin;

    Map<String, PlayerInfo> currentPlayers = new HashMap<>();

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

        // make it so that the other players cannot see each other
        for(PlayerInfo info: currentPlayers.values()) {
            Player plr = info.player;
            plr.hidePlayer(plugin, player);
            player.hidePlayer(plugin, plr);
        }

        // create the armor stand and make the player sit on it
        Location standLOC = new Location(selectionWorld, 1000.5, 10, 1000.5);
        ArmorStand stand = selectionWorld.spawn(standLOC, ArmorStand.class, armorStand -> {
            armorStand.setVisibleByDefault(false);
            armorStand.setGravity(false);
            armorStand.setInvulnerable(true);
            armorStand.setInvisible(true);
        });
        player.teleportAsync(standLOC).thenAccept(b -> {
            player.showEntity(plugin, stand);
            stand.addPassenger(player);
        });
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);

        List<String[]> teamTexts = plugin.getTeamManager().getAllTeamNamesAndDescription();

        // Create name
        TextDisplay nameText = selectionWorld.spawn(standLOC.add(0, 2.5, 5), TextDisplay.class, display -> {
            display.setVisibleByDefault(false);
            display.setBillboard(Display.Billboard.FIXED);
            display.text(MiniMessage.miniMessage().deserialize(teamTexts.getFirst()[0]));
            display.setRotation(-180, 0);
        });

        // Create description
        TextDisplay descriptionText = selectionWorld.spawn(standLOC.add(0, -1, 0), TextDisplay.class, display -> {
            display.setVisibleByDefault(false);
            display.setBillboard(Display.Billboard.FIXED);
            display.text(MiniMessage.miniMessage().deserialize(teamTexts.getFirst()[1]));
            display.setRotation(-180, 0);
        });

        // show the player the text
        player.showEntity(plugin, nameText);
        player.showEntity(plugin, descriptionText);

        // give night vision to the player so that they can see
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, -1, 1, false, false, false));

        standLOC.add(0, -1.5, -0.5); // reset standLOC y value

        // spawn armor stand buttons
        ItemDisplay leftButton = selectionWorld.spawn(standLOC.add(3.5,1.8,0), ItemDisplay.class, itemDisplay -> {
            itemDisplay.setVisibleByDefault(false);
            itemDisplay.setGravity(false);
            itemDisplay.setInvulnerable(true);
            itemDisplay.setInvisible(true);
            ItemStack helmet = TeamSpawns.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjdhYWNhZDE5M2UyMjI2OTcxZWQ5NTMwMmRiYTQzMzQzOGJlNDY0NGZiYWI1ZWJmODE4MDU0MDYxNjY3ZmJlMiJ9fX0=");
            itemDisplay.setItemStack(helmet);
            itemDisplay.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(2,2,2), new AxisAngle4f()));
            itemDisplay.setRotation(-36, 0);
        });
        player.showEntity(plugin, leftButton);

        ItemDisplay rightButton = selectionWorld.spawn(standLOC.add(-7,0,0), ItemDisplay.class, itemDisplay -> {
            itemDisplay.setVisibleByDefault(false);
            itemDisplay.setGravity(false);
            itemDisplay.setInvulnerable(true);
            itemDisplay.setInvisible(true);
            ItemStack item = TeamSpawns.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDM0ZWYwNjM4NTM3MjIyYjIwZjQ4MDY5NGRhZGMwZjg1ZmJlMDc1OWQ1ODFhYTdmY2RmMmU0MzEzOTM3NzE1OCJ9fX0=");
            itemDisplay.setItemStack(item);
            itemDisplay.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(2,2,2), new AxisAngle4f()));
            itemDisplay.setRotation(36, 0);
        });
        player.showEntity(plugin, rightButton);

        // reset standLOC
        standLOC = new Location(selectionWorld, 1000.5, 10, 1000.5);

        ItemDisplay confirmButton = selectionWorld.spawn(standLOC.add(0, 4, 5), ItemDisplay.class, itemDisplay -> {
            itemDisplay.setVisibleByDefault(false);
            itemDisplay.setGravity(false);
            itemDisplay.setInvulnerable(true);
            itemDisplay.setInvisible(true);
            itemDisplay.setGlowColorOverride(Color.fromBGR(0, 255, 0));
            ItemStack item = TeamSpawns.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzIzNjE0YTQ5NzZlMTk0YzgwOGY0YjdiZDgxOWZlYTcxNzM2ZWRmYzY0MzgzODE1MTdmOGMxMzNjZWJhMjIxIn19fQ==");
            itemDisplay.setItemStack(item);
            itemDisplay.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(1.5f,1.5f,1.5f), new AxisAngle4f()));
            itemDisplay.setRotation(0, 0);
        });
        player.showEntity(plugin, confirmButton);

        currentPlayers.put(player.getUniqueId().toString(), new PlayerInfo(player, stand, nameText, descriptionText, leftButton, rightButton, confirmButton));
    }

    public PlayerInfo getPlayerInfo(Player player) {
        return currentPlayers.get(player.getUniqueId().toString());
    }

    public boolean isPlayerSelecting(Player player) {
        return currentPlayers.containsKey(player.getUniqueId().toString());
    }

    public static class PlayerInfo {
        private final Player player;
        private final ArmorStand stand;
        private int teamIndex = 0;
        private final TextDisplay name;
        private final TextDisplay description;
        private final ItemDisplay leftButton;
        private final ItemDisplay rightButton;
        private final ItemDisplay confirmButton;

        public PlayerInfo(Player player, ArmorStand stand, TextDisplay name, TextDisplay description, ItemDisplay leftButton, ItemDisplay rightButton, ItemDisplay confirmButton) {
            this.player = player;
            this.stand = stand;
            this.name = name;
            this.description = description;
            this.leftButton = leftButton;
            this.rightButton = rightButton;
            this.confirmButton = confirmButton;
        }

        public Player getPlayer() {
            return player;
        }

        public ArmorStand getStand() {
            return stand;
        }

        public TextDisplay getName() {
            return name;
        }

        public TextDisplay getDescription() {
            return description;
        }

        public void setTeamIndex(int index) {
            teamIndex = index;
        }

        public ItemDisplay getLeftButton() {
            return leftButton;
        }

        public ItemDisplay getRightButton() {
            return rightButton;
        }

        public ItemDisplay getConfirmButton() {
            return confirmButton;
        }

        public int getTeamIndex() {
            return teamIndex;
        }
    }
}
