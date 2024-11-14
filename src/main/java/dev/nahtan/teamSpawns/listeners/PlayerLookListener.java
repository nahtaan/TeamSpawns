package dev.nahtan.teamSpawns.listeners;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerRotation;
import dev.nahtan.teamSpawns.TeamSpawns;
import dev.nahtan.teamSpawns.data.TeamSelector;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

public class PlayerLookListener implements PacketListener {

    TeamSpawns plugin;

    public PlayerLookListener(TeamSpawns plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        // ignore when the rider is not a player
        if(event.getPacketType() != PacketType.Play.Client.PLAYER_ROTATION) {
            return;
        }

        Player player = event.getPlayer();
        TeamSelector selector = plugin.getTeamSelector();
        // ignore players that are not selecting
        if(!selector.isPlayerSelecting(player)) {
            return;
        }

        WrapperPlayClientPlayerRotation packet = new WrapperPlayClientPlayerRotation(event);

        float yaw = packet.getYaw();
        float pitch = packet.getPitch();

        final TeamSelector.PlayerInfo info = selector.getPlayerInfo(player);
        // left button
        if((yaw <= -31.5 && yaw >= -44) && (pitch <= 24 && pitch >= 10)) {
            info.getLeftButton().getScheduler().run(plugin, (task) -> {
                info.getLeftButton().setGlowing(true);
                info.getLeftButton().setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(2.3f,2.3f,2.3f), new AxisAngle4f()));
            }, null);
        }else {
            info.getLeftButton().getScheduler().run(plugin, (task) -> {
                info.getLeftButton().setGlowing(false);
                info.getLeftButton().setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(2,2,2), new AxisAngle4f()));
            }, null);
        }

        // right button
        if ((yaw <= 44 && yaw >= 31.5) && (pitch <= 24 && pitch >= 10)) {
            info.getRightButton().getScheduler().run(plugin, (task) -> {
                info.getRightButton().setGlowing(true);
                info.getRightButton().setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(2.3f,2.3f,2.3f), new AxisAngle4f()));
            }, null);
        }else {
            info.getRightButton().getScheduler().run(plugin, (task) -> {
                info.getRightButton().setGlowing(false);
                info.getRightButton().setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(2,2,2), new AxisAngle4f()));
            }, null);
        }

        // confirm button
        if ((yaw <= 5 && yaw >= -5) && (pitch <= -2 && pitch >= -13)) {
            info.getConfirmButton().getScheduler().run(plugin, (task) -> {
                info.getConfirmButton().setGlowing(true);
                info.getConfirmButton().setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(1.8f,1.8f,1.8f), new AxisAngle4f()));
            }, null);
        }else {
            info.getConfirmButton().getScheduler().run(plugin, (task) -> {
                info.getConfirmButton().setGlowing(false);
                info.getConfirmButton().setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(1.5f,1.5f,1.5f), new AxisAngle4f()));
            }, null);
        }
    }
}
