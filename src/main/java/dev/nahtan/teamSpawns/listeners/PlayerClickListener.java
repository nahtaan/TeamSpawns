package dev.nahtan.teamSpawns.listeners;

import dev.nahtan.teamSpawns.TeamSpawns;
import dev.nahtan.teamSpawns.data.TeamSelector;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.List;

public class PlayerClickListener implements Listener {

    HashSet<String> animatingLeftPlayers = new HashSet<>();
    HashSet<String> animatingRightPlayers = new HashSet<>();

    TeamSpawns plugin;

    public PlayerClickListener(TeamSpawns plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        // ignore players that are not selecting
        if(!plugin.getTeamSelector().isPlayerSelecting(player)) return;

        float yaw = player.getYaw();
        float pitch = player.getPitch();

        final TeamSelector.PlayerInfo info = plugin.getTeamSelector().getPlayerInfo(player);
        // left button
        if((yaw <= -31.5 && yaw >= -44) && (pitch <= 24 && pitch >= 10) && !isAnimatingLeft(player)) {

            // play a sound to the player
            player.getScheduler().run(plugin, (task) ->
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, 100, 1)
            , null);
            player.getScheduler().runDelayed(plugin, (task) ->
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PRESSURE_PLATE_CLICK_OFF, 100, 1)
            , null, 10);

            // get the required team info
            List<String[]> allTeams = plugin.getTeamManager().getAllTeamText();
            int newIndex = info.getTeamIndex() - 1;
            if(newIndex < 0) {
                newIndex = allTeams.size() - 1;
            }
            info.setTeamIndex(newIndex);
            String[] teamInfo = allTeams.get(newIndex);

            // Create component for the text
            Component text = MiniMessage.miniMessage().deserialize("<b><" + teamInfo[2] + ">" + teamInfo[0] + "</b><reset><br><br>" + teamInfo[1]);

            // update text display
            info.getText().getScheduler().run(plugin, (task) ->
                            info.getText().text(text)
                    , null);

            // apply click animation
            info.getLeftButton().getScheduler().run(plugin, (task) -> {
                info.getLeftButton().setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(2,2,2), new AxisAngle4f()));
                animatingLeftPlayers.add(player.getUniqueId().toString());
            }, null);
            // apply click animation
            info.getLeftButton().getScheduler().runDelayed(plugin, (task) -> {
                info.getLeftButton().setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(2,2,2), new AxisAngle4f()));
                animatingLeftPlayers.remove(player.getUniqueId().toString());
            }, null, 10);

        // right button
        }else if ((yaw <= 44 && yaw >= 31.5) && (pitch <= 24 && pitch >= 10) && !isAnimatingRight(player)) {
            // play a sound to the player
            player.getScheduler().run(plugin, (task) ->
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, 100, 1)
            , null);
            player.getScheduler().runDelayed(plugin, (task) ->
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PRESSURE_PLATE_CLICK_OFF, 100, 1)
            , null, 10);

            // get the required team info
            List<String[]> allTeams = plugin.getTeamManager().getAllTeamText();
            int newIndex = info.getTeamIndex() + 1;
            if(newIndex > allTeams.size() - 1) {
                newIndex = 0;
            }
            info.setTeamIndex(newIndex);
            String[] teamInfo = allTeams.get(newIndex);

            // Create component for the text
            Component text = MiniMessage.miniMessage().deserialize("<b><" + teamInfo[2] + ">" + teamInfo[0] + "</b><reset><br><br>" + teamInfo[1]);

            // update text display
            info.getText().getScheduler().run(plugin, (task) ->
                info.getText().text(text)
            , null);

            // apply click animation
            info.getRightButton().getScheduler().run(plugin, (task) -> {
                info.getRightButton().setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(2,2,2), new AxisAngle4f()));
                animatingRightPlayers.add(player.getUniqueId().toString());
            }, null);
            info.getLeftButton().getScheduler().runDelayed(plugin, (task) -> {
                info.getLeftButton().setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(2,2,2), new AxisAngle4f()));
                animatingRightPlayers.remove(player.getUniqueId().toString());
            }, null, 10);

        // confirm button
        }else if ((yaw <= 5 && yaw >= -5) && (pitch <= 25 && pitch >= 12)) {
            // set player as being done to avoid the look listener interfering
            plugin.getTeamSelector().makeSelection(player);
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isAnimatingLeft(Player player) {
        return animatingLeftPlayers.contains(player.getUniqueId().toString());
    }
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isAnimatingRight(Player player) {
        return animatingRightPlayers.contains(player.getUniqueId().toString());
    }
}
