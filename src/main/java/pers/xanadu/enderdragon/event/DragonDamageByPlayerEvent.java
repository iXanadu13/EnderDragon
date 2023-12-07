package pers.xanadu.enderdragon.event;

import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
/**
 * Called when a dragon is damaged by a player, including block_explosion.
 */
public final class DragonDamageByPlayerEvent extends EntityDamageByEntityEvent {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
    private final Player damager;
    private final EnderDragon dragon;
    private final double finalDamage;

    public DragonDamageByPlayerEvent(final Player damager, final EnderDragon dragon, final DamageCause cause, final double finalDamage) {
        super(damager, dragon, cause, finalDamage);
        this.damager = damager;
        this.dragon = dragon;
        this.finalDamage = finalDamage;
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
    @Override
    public Player getDamager() {
        return damager;
    }

    public EnderDragon getDragon() {
        return dragon;
    }

    @Override
    public double getDamage() {
        return finalDamage;
    }
}
