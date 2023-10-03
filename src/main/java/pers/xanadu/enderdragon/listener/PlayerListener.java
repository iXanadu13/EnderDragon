package pers.xanadu.enderdragon.listener;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import pers.xanadu.enderdragon.config.Config;
import pers.xanadu.enderdragon.manager.GlowManager;

import java.util.Collection;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent e){
        Player p = e.getPlayer();
        GlowManager.setScoreBoard(p);
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent e){
        DragonExplosionHurtListener.clearUID(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void OnCrystalPlaced(final PlayerInteractEvent e){
        if(!Config.resist_player_respawn) return;
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(e.getMaterial() != Material.END_CRYSTAL) return;
        if(e.getClickedBlock() == null) return;
        Material blockType = e.getClickedBlock().getType();
        if(blockType != Material.OBSIDIAN && blockType != Material.BEDROCK) return;
        World world = e.getPlayer().getWorld();
        if(world.getEnvironment() != World.Environment.THE_END) return;
        Block block = e.getClickedBlock();
        int d0 = block.getX();
        int d1 = block.getY() + 1;
        int d2 = block.getZ();
        if(world.getBlockAt(d0, d1, d2).getType() != Material.AIR) return;
        Location cen = block.getLocation().clone().add(0.5,1,0.5);
        Collection<Entity> list = world.getNearbyEntities(cen,0.5,1,0.5);
        if(!list.isEmpty()) return;
        if(e.getPlayer().getGameMode() == GameMode.ADVENTURE) return;
        e.setCancelled(true);
        if(e.getPlayer().getGameMode() != GameMode.CREATIVE){
            ItemStack item = e.getItem();
            assert item != null;
            int amount = item.getAmount();
            e.getItem().setAmount(amount-1);
        }
        EnderCrystal crystal = (EnderCrystal) world.spawnEntity(cen,EntityType.ENDER_CRYSTAL);
        crystal.setShowingBottom(false);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void OnDragonBreathGather(final PlayerInteractEvent e){
        if(!Config.resist_dragon_breath_gather) return;
        if(e.getMaterial() != Material.GLASS_BOTTLE) return;
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR) return;
        World world = e.getPlayer().getWorld();
        if(world.getEnvironment() != World.Environment.THE_END) return;
        Player p = e.getPlayer();
        Location cen = p.getLocation().clone().add(0d,0.9d,0d);
        Collection<Entity> list = world.getNearbyEntities(cen,2.3d,2.9d,2.3d);
        for(Entity entity : list){
            if(entity instanceof AreaEffectCloud) {
                AreaEffectCloud effectCloud = (AreaEffectCloud) entity;
                if(effectCloud.getSource() instanceof EnderDragon){
                    e.setCancelled(true);
                    break;
                }
            }
        }
    }
}
