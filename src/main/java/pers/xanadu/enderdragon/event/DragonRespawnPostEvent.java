package pers.xanadu.enderdragon.event;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.CreatureSpawnEvent;
import pers.xanadu.enderdragon.manager.DragonManager;
import pers.xanadu.enderdragon.metadata.DragonInfo;

/**
 * Called when a dragon is spawned, filtered through blacklist.
 * <p>
 * Cancelling this event make no changes.
 */
public final class DragonRespawnPostEvent extends CreatureSpawnEvent {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
    private final World world;
    private final DragonInfo info;
    public DragonRespawnPostEvent(final EnderDragon dragon, final SpawnReason reason, final DragonInfo dragonInfo) {
        super(dragon,reason);
        world = dragon.getWorld();
        this.info = dragonInfo;
    }

    public EnderDragon getEnderDragon(){
        return (EnderDragon) getEntity();
    }

    public Location getEndPortalLocation(){
        return DragonManager.getEndPortalLocation(world);
    }
    public DragonInfo getDragonInformation(){
        return info;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    @Override
    public void setCancelled(final boolean cancel) {
        this.cancel = cancel;
    }
    @Override
    public boolean isCancelled() {
        return cancel;
    }

}
