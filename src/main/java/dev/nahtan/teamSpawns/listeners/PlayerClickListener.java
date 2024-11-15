package dev.nahtan.teamSpawns.listeners;

import dev.nahtan.teamSpawns.TeamSpawns;
import dev.nahtan.teamSpawns.data.TeamSelector;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.List;
import java.util.Vector;

public class PlayerClickListener implements Listener {
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
        if((yaw <= -31.5 && yaw >= -44) && (pitch <= 24 && pitch >= 10)) {

            // play a sound to the player
            player.getScheduler().run(plugin, (task) -> {
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, 100, 1);
            }, null);

            // get the required team info
            List<String[]> allTeams = plugin.getTeamManager().getAllTeamText();
            int newIndex = info.getTeamIndex() - 1;
            if(newIndex < 0) {
                newIndex = allTeams.size() - 1;
            }
            info.setTeamIndex(newIndex);
            String[] teamInfo = allTeams.get(newIndex);

            // update text displays
            info.getName().getScheduler().run(plugin, (task) -> {
                info.getName().text(Component.text(teamInfo[0])
                        .color(TextColor.color(Integer.decode(teamInfo[2]))));
            }, null);
            info.getDescription().getScheduler().run(plugin, (task) -> {
                info.getDescription().text(MiniMessage.miniMessage().deserialize(teamInfo[1]));
            }, null);

        // right button
        }else if ((yaw <= 44 && yaw >= 31.5) && (pitch <= 24 && pitch >= 10)) {
            // play a sound to the player
            player.getScheduler().run(plugin, (task) -> {
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, 100, 1);
            }, null);

            // get the required team info
            List<String[]> allTeams = plugin.getTeamManager().getAllTeamText();
            int newIndex = info.getTeamIndex() + 1;
            if(newIndex > allTeams.size() - 1) {
                newIndex = 0;
            }
            info.setTeamIndex(newIndex);
            String[] teamInfo = allTeams.get(newIndex);

            // update text displays
            info.getName().getScheduler().run(plugin, (task) -> {
                info.getName().text(Component.text(teamInfo[0])
                        .color(TextColor.color(Integer.decode(teamInfo[2]))));
            }, null);
            info.getDescription().getScheduler().run(plugin, (task) -> {
                info.getDescription().text(MiniMessage.miniMessage().deserialize(teamInfo[1]));
            }, null);

        // confirm button
        }else if ((yaw <= 5 && yaw >= -5) && (pitch <= -2 && pitch >= -13)) {

            // play lightning effect and translate
            info.getConfirmButton().getScheduler().run(plugin, (task) -> {
                World world = player.getWorld();
                world.spawn(info.getConfirmButton().getLocation(), LightningStrike.class);
                info.getConfirmButton().setInterpolationDelay(0);
                info.getConfirmButton().setInterpolationDuration(100);
                info.getConfirmButton().setTransformation(new Transformation(new Vector3f(0, -1, -4),
                        new AxisAngle4f(), new Vector3f(), new AxisAngle4f()));
            }, null);

            // apply gravity to other buttons/displays
            info.getLeftButton().getScheduler().run(plugin, (task) -> {
                info.getLeftButton().setGravity(true);
            }, null);
            info.getRightButton().getScheduler().run(plugin, (task) -> {
                info.getRightButton().setGravity(true);
            }, null);
            info.getDescription().getScheduler().run(plugin, (task) -> {
                info.getDescription().setGravity(true);
            }, null);
        }
    }
}
