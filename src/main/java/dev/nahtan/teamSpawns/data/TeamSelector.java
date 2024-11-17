package dev.nahtan.teamSpawns.data;

import dev.nahtan.teamSpawns.TeamSpawns;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        // send welcome message to the player
        player.sendMessage(MiniMessage.miniMessage().deserialize(plugin.getConfig().getString("first-join-format", "").replace("%player%", player.getName())));

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

        List<String[]> teamTexts = plugin.getTeamManager().getAllTeamText();

        // Create component for the text
        Component text = MiniMessage.miniMessage().deserialize("<b><" + teamTexts.getFirst()[2] + ">" + teamTexts.getFirst()[0] + "</b><reset><br><br>" + teamTexts.getFirst()[1]);

        // Create description
        TextDisplay textEntity = selectionWorld.spawn(standLOC.add(0, 2, 5), TextDisplay.class, display -> {
            display.setVisibleByDefault(false);
            display.setBillboard(Display.Billboard.FIXED);
            display.text(text);
            display.setRotation(-180, 0);
        });

        // show the player the text
        player.showEntity(plugin, textEntity);

        // give night vision to the player so that they can see
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, -1, 1, false, false, false));

        standLOC.add(0, -1.5, -0.5); // reset standLOC y value

        // spawn armor stand buttons
        ItemDisplay leftButton = selectionWorld.spawn(standLOC.add(3.5,1.8,0), ItemDisplay.class, itemDisplay -> {
            itemDisplay.setVisibleByDefault(false);
            itemDisplay.setGravity(false);
            itemDisplay.setInvulnerable(true);
            itemDisplay.setInvisible(true);
            ItemStack helmet = TeamSpawns.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Y3YWFjYWQxOTNlMjIyNjk3MWVkOTUzMDJkYmE0MzM0MzhiZTQ2NDRmYmFiNWViZjgxODA1NDA2MTY2N2ZiZTIifX19");
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
            ItemStack item = TeamSpawns.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2QzNGVmMDYzODUzNzIyMmIyMGY0ODA2OTRkYWRjMGY4NWZiZTA3NTlkNTgxYWE3ZmNkZjJlNDMxMzkzNzcxNTgifX19");
            itemDisplay.setItemStack(item);
            itemDisplay.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(2,2,2), new AxisAngle4f()));
            itemDisplay.setRotation(36, 0);
        });
        player.showEntity(plugin, rightButton);

        // reset standLOC
        standLOC = new Location(selectionWorld, 1000.5, 10, 1000.5);

        ItemDisplay confirmButton = selectionWorld.spawn(standLOC.add(0, 1.8, 5), ItemDisplay.class, itemDisplay -> {
            itemDisplay.setVisibleByDefault(false);
            itemDisplay.setGravity(false);
            itemDisplay.setInvulnerable(true);
            itemDisplay.setInvisible(true);
            itemDisplay.setGlowColorOverride(Color.fromBGR(0, 255, 0));
            ItemStack item = TeamSpawns.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzcyMzYxNGE0OTc2ZTE5NGM4MDhmNGI3YmQ4MTlmZWE3MTczNmVkZmM2NDM4MzgxNTE3ZjhjMTMzY2ViYTIyMSJ9fX0=");
            itemDisplay.setItemStack(item);
            itemDisplay.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(1.5f,1.5f,1.5f), new AxisAngle4f()));
            itemDisplay.setRotation(0, 0);
        });
        player.showEntity(plugin, confirmButton);

        currentPlayers.put(player.getUniqueId().toString(), new PlayerInfo(player, stand, textEntity, leftButton, rightButton, confirmButton));
    }

    public PlayerInfo getPlayerInfo(Player player) {
        return currentPlayers.get(player.getUniqueId().toString());
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isPlayerSelecting(Player player) {
        if(!currentPlayers.containsKey(player.getUniqueId().toString())) {
            return false;
        }
        return !currentPlayers.get(player.getUniqueId().toString()).isDone();
    }

    public void makeSelection(Player player) {
        // ignore if the player is unknown
        if(!currentPlayers.containsKey(player.getUniqueId().toString())) return;
        PlayerInfo info = currentPlayers.get(player.getUniqueId().toString());

        // update done status
        info.setDone();

        // immediate effects
        // play lightning effect
        info.getConfirmButton().getScheduler().run(plugin, (task) -> {
            World world = player.getWorld();
            world.spawn(info.getConfirmButton().getLocation(), LightningStrike.class, (bolt) -> {
                bolt.setVisibleByDefault(false);
                player.showEntity(plugin, bolt);
            });
        }, null);

        // remove other buttons and text displays
        info.getLeftButton().getScheduler().run(plugin, (task) ->
            info.getLeftButton().remove()
        , null);
        info.getRightButton().getScheduler().run(plugin, (task) ->
            info.getRightButton().remove()
        , null);
        info.getText().getScheduler().run(plugin, (task) ->
            info.getText().remove()
        , null);

        // show title of team name to the player and set team in persistent data
        int index = info.getTeamIndex();
        String[] teamText = plugin.getTeamManager().getAllTeamText().get(index);
        String name = teamText[0];
        Component title = Component.text(name).color(TextColor.color(Integer.decode(teamText[2])));
        player.getScheduler().run(plugin, (task) -> {
            player.showTitle(Title.title(title, Component.empty(), Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ZERO)));
            plugin.getTeamManager().setTeamName(player, name);
        }, null);

        // queue the selection
        player.getScheduler().runDelayed(plugin, (task) -> {
            player.teleportAsync(plugin.getTeamManager().getTeamSpawnLoc(name));
            player.setGameMode(GameMode.SURVIVAL);
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }, null, 40L);

        // other delayed tasks
        new BukkitRunnable() {
            @Override
            public void run() {
                String text = plugin.getConfig().getString("announcement-format", "").replace("%player%", player.getName()).replace("%team%",'<' + teamText[2] + '>' + teamText[0]);
                Component msg = MiniMessage.miniMessage().deserialize(text);
                Bukkit.broadcast(msg);
                Bukkit.getServer().getOnlinePlayers().forEach(plr -> {
                    if(!player.getUniqueId().equals(plr.getUniqueId())) {
                        plr.getScheduler().run(plugin, (task) ->
                            plr.playSound(plr.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 100f, 1f)
                        , null);
                    }
                });
                // allow the player to see other players and vice versa
                for(PlayerInfo info: currentPlayers.values()) {
                    if(player.getUniqueId() == info.getPlayer().getUniqueId()) {
                        continue;
                    }
                    player.getScheduler().run(plugin, (task) -> player.showPlayer(plugin, info.getPlayer()), null);
                    player.getScheduler().run(plugin, (task) -> info.getPlayer().showPlayer(plugin, player), null);
                }
                // remove confirm button
                info.getConfirmButton().getScheduler().run(plugin, (task) -> info.getConfirmButton().remove(), null);
                // remove armor stand
                info.getStand().getScheduler().run(plugin, (task) -> info.getStand().remove(), null);
                // remove player from hashmap
                currentPlayers.remove(player.getUniqueId().toString());
            }
        }.runTaskLaterAsynchronously(plugin, 40L);
    }

    public void handleEarlyPlayerQuit(Player player) {
        PlayerInfo info = currentPlayers.get(player.getUniqueId().toString());
        if(info == null) return; // ignore players that we don't have

        // remove entities
        info.getText().getScheduler().run(plugin, (task) -> info.getText().remove(), null);
        info.getLeftButton().getScheduler().run(plugin, (task) -> info.getLeftButton().remove(), null);
        info.getRightButton().getScheduler().run(plugin, (task) -> info.getRightButton().remove(), null);
        info.getConfirmButton().getScheduler().run(plugin, (task) -> info.getConfirmButton().remove(), null);
        info.getStand().getScheduler().run(plugin, (task) -> info.getStand().remove(), null);

        // remove data
        currentPlayers.remove(player.getUniqueId().toString());
    }

    public static class PlayerInfo {
        private final Player player;
        private final ArmorStand stand;
        private int teamIndex = 0;
        private final TextDisplay text;
        private final ItemDisplay leftButton;
        private final ItemDisplay rightButton;
        private final ItemDisplay confirmButton;
        private boolean done = false;

        public PlayerInfo(Player player, ArmorStand stand, TextDisplay text, ItemDisplay leftButton, ItemDisplay rightButton, ItemDisplay confirmButton) {
            this.player = player;
            this.stand = stand;
            this.text = text;
            this.leftButton = leftButton;
            this.rightButton = rightButton;
            this.confirmButton = confirmButton;
        }

        public void setDone() {
            done = true;
        }

        public boolean isDone() {
            return done;
        }

        public Player getPlayer() {
            return player;
        }

        public ArmorStand getStand() {
            return stand;
        }

        public TextDisplay getText() {
            return text;
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
