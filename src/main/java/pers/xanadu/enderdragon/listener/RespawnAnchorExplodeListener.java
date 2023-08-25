package pers.xanadu.enderdragon.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import pers.xanadu.enderdragon.event.PlayerExplodeDragonEvent;

import java.util.Collection;
import java.util.UUID;

import static pers.xanadu.enderdragon.manager.WorldManager.getExplosionDragon;

public class RespawnAnchorExplodeListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void OnRespawnAnchorExplode(final PlayerInteractEvent e){
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = e.getClickedBlock();
        if(block == null || block.getType() != Material.RESPAWN_ANCHOR) return;
        RespawnAnchor anchor = (RespawnAnchor) block.getBlockData();
        Player p = e.getPlayer();
        Material offHand = p.getInventory().getItemInOffHand().getType();
        if(e.getHand()== EquipmentSlot.HAND && e.getMaterial()!=Material.GLOWSTONE && offHand==Material.GLOWSTONE) return;
        if(e.getMaterial()==Material.GLOWSTONE && anchor.getCharges()<4) return;
        if(anchor.getCharges()==0) return;
        if(pers.xanadu.enderdragon.EnderDragon.getInstance().getRespawnAnchorManager().isRespawnAnchorWorks(p.getWorld())) return;
        if(p.isSneaking() && (p.getItemInHand().getType()!=Material.AIR || offHand!=Material.AIR)) return;
        long time = pers.xanadu.enderdragon.EnderDragon.getInstance().getWorldDataManager().getGameTime(e.getPlayer().getWorld());
        Location loc = block.getLocation();
        Collection<EnderDragon> entities = getExplosionDragon(5f,loc);
        for(EnderDragon dragon : entities){
            UUID uuid = p.getUniqueId();
            DragonExplosionHurtListener.addUID(uuid,new PlayerExplodeDragonEvent(time,p,dragon,loc));
            break;
        }
    }
}
