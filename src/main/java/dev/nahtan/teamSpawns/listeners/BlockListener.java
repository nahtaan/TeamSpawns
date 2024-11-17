package dev.nahtan.teamSpawns.listeners;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import dev.nahtan.teamSpawns.TeamSpawns;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

import static org.bukkit.event.EventPriority.HIGHEST;

public class BlockListener implements Listener {
    TeamSpawns plugin;
    public BlockListener(TeamSpawns plugin) {
        this.plugin = plugin;
    }
    @EventHandler(priority = HIGHEST)
    public void onPlace(BlockPlaceEvent e) {
        e.setCancelled(cancelEvent(e.getBlock().getLocation()));
    }
    @EventHandler(priority = HIGHEST)
    public void onBreak(BlockBreakEvent e) {
        e.setCancelled(cancelEvent(e.getBlock().getLocation()));
    }
    @EventHandler(priority = HIGHEST)
    public void pistonPush(BlockPistonExtendEvent e) {
        e.setCancelled(cancelPistonEvent(e.getBlocks(), e.getDirection(), e.getBlock()));
    }
    @EventHandler(priority = HIGHEST)
    public void pistonRetract(BlockPistonRetractEvent e) {
        e.setCancelled(cancelPistonEvent(e.getBlocks(), e.getDirection(), e.getBlock()));
    }
    @EventHandler(priority = HIGHEST)
    public void onFluidSpread(BlockFromToEvent e) {
        e.setCancelled(cancelEvent(e.getToBlock().getLocation()));
    }
    @EventHandler(priority = HIGHEST)
    public void onDestroy(BlockDestroyEvent e) {
        e.setCancelled(cancelEvent(e.getBlock().getLocation()));
    }
    @EventHandler(priority = HIGHEST)
    public void onBucketPlace(PlayerBucketEmptyEvent e) {
        e.setCancelled(cancelEvent(e.getBlock().getLocation()));
    }
    @EventHandler(priority = HIGHEST)
    public void onBucketFill(PlayerBucketFillEvent e) {
        e.setCancelled(cancelEvent(e.getBlock().getLocation()));
    }
    @EventHandler(priority = HIGHEST)
    public void onInteract(PlayerInteractEvent e) {
        if(e.getClickedBlock() != null) {
            e.setCancelled(cancelEvent(e.getClickedBlock().getLocation()));
        }
    }
    @EventHandler(priority = HIGHEST)
    public void onExplode(BlockExplodeEvent e) {
        e.setCancelled(cancelEvent(e.getBlock().getLocation()));
    }
    @EventHandler(priority = HIGHEST)
    public void onEntityExplode(EntityExplodeEvent e) {
        for(Block block: e.blockList()) {
            e.setCancelled(cancelEvent(block.getLocation()));
            if(e.isCancelled()) return;
        }
    }

    private boolean cancelPistonEvent(List<Block> blocks, BlockFace direction, Block piston) {
        int z = 0, x = 0;
        switch(direction) {
            case NORTH -> z -= 1;
            case EAST ->  x += 1;
            case SOUTH -> z += 1;
            case WEST -> x -= 1;
            case UP, DOWN -> {
                return cancelEvent(piston.getLocation());
            }
        }
        for(Block block: blocks) {
            Location loc = block.getLocation().add(x, 0, z);
            if(cancelEvent(loc)) {
                return true;
            }
        }
        return false;
    }


    private boolean cancelEvent(Location location) {
        List<Location> spawns = plugin.getTeamManager().getAllTeamSpawns();
        int radius = plugin.getConfig().getInt("spawn-protection-radius", 5);
        for(Location spawn: spawns) {
            // dont check coords if the world is wrong
            if(!spawn.getWorld().getKey().equals(location.getWorld().getKey())) continue;

            // check coordinates
            if(spawn.getX()+radius > location.getX() &&
                    spawn.getX()-radius < location.getX() &&
                    spawn.getZ()+radius > location.getZ() &&
                    spawn.getZ()-radius < location.getZ()) {
                return true;
            }
        }

        // if all teams are checked then return true
        return false;
    }

}
