package xanadu.enderdragon.listeners.mmoitems;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.damage.MeleeAttackMetadata;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.TypeSet;
import net.Indyuce.mmoitems.api.interaction.weapon.Weapon;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class MMOPlayerAttackListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void meleeAttacks(PlayerAttackEvent e) {
        if (!(e.getAttack() instanceof MeleeAttackMetadata)) {
            return;
        }
        MeleeAttackMetadata attackMetadata = (MeleeAttackMetadata) e.getAttack();
        Player player = e.getPlayer();
        PlayerData playerData = PlayerData.get(player);
        NBTItem nBTItem = MythicLib.plugin.getVersion().getWrapper().getNBTItem(player.getInventory().getItem(attackMetadata.getHand().toBukkit()));
        if (nBTItem.hasType() && Type.get(nBTItem.getType()) != Type.BLOCK) {
            Weapon weapon = new Weapon(playerData, nBTItem);
            if (weapon.getMMOItem().getType().getItemSet() == TypeSet.RANGE) {
                e.setCancelled(true);
            } else if (!weapon.checkItemRequirements()) {
                e.setCancelled(true);
            } else if (!weapon.handleTargetedAttack(e.getAttack(), e.getEntity())) {
                e.setCancelled(true);
            }
        }
    }

}
