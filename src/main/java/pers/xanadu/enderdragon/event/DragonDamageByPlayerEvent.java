package pers.xanadu.enderdragon.event;

import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class DragonDamageByPlayerEvent extends EntityDamageByEntityEvent {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
    private final Player damager;
    private final EnderDragon dragon;
    private final DamageCause cause;
    private final double finalDamage;

    public DragonDamageByPlayerEvent(final Player damager, final EnderDragon dragon, final DamageCause cause, final double finalDamage) {
        super(damager, dragon, cause, finalDamage);
        this.damager = damager;
        this.dragon = dragon;
        this.cause = cause;
        this.finalDamage = finalDamage;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public void setCancelled(final boolean cancel) {
        this.cancel = cancel;
    }

    public boolean isCancelled() {
        return cancel;
    }

    public Player getDamager() {
        return damager;
    }

    public EnderDragon getDragon() {
        return dragon;
    }

    public DamageCause getDamagerCause() {
        return cause;
    }
    @Override
    public double getDamage() {
        return finalDamage;
    }
}
