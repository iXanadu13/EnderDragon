package xanadu.enderdragon.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.PlayerInventory;
import xanadu.enderdragon.gui.GUISlot;
import xanadu.enderdragon.gui.GUISlotType;
import xanadu.enderdragon.gui.GUIWrapper;
import xanadu.enderdragon.gui.GUIHolder;
import xanadu.enderdragon.gui.slots.PageJumpSlot;
import xanadu.enderdragon.config.Lang;
import xanadu.enderdragon.manager.GuiManager;
import xanadu.enderdragon.utils.MyDragon;

import static xanadu.enderdragon.manager.DragonManager.mp;

public class InventoryListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnInventoryClick(InventoryClickEvent e){
        InventoryHolder holder = e.getInventory().getHolder();
        if(!(holder instanceof GUIHolder)) return;
        e.setCancelled(true);
        GUIHolder guiHolder = (GUIHolder) holder;
        if(!(guiHolder.getGUI() instanceof GUIWrapper)) return;
        GUIWrapper guiWrapper = (GUIWrapper) guiHolder.getGUI();
        if(e.getClickedInventory() instanceof PlayerInventory){
            return;
        }
        if(e.getClick() != ClickType.LEFT && e.getClick() != ClickType.RIGHT){
            return;
        }
        if(e.getRawSlot() >= 54 || e.getRawSlot() < 0){ //点击箱子以外界面会返回-999
            return;
        }
        Player p = (Player) e.getWhoClicked();
        GUISlot slot = guiWrapper.getSlot(e.getRawSlot());
        GUISlotType type = slot.getType();
        if(type == GUISlotType.EMPTY || type == GUISlotType.TIP || type == GUISlotType.PAGE_TIP || type == GUISlotType.ITEM_SLOT){
            return;
        }
        if(type == GUISlotType.PAGE_PREV){
            guiWrapper.prev();
            p.updateInventory();
            return;
        }
        if(type == GUISlotType.PAGE_NEXT){
            guiWrapper.next();
            p.updateInventory();
            return;
        }
        if(type == GUISlotType.PAGE_JUMP){
            String name = ((PageJumpSlot)slot).getGuiName();
            GuiManager.openGui(p,name);
            return;
        }
        if(type == GUISlotType.DRAGON_SLOT){
            String unique_name = guiWrapper.getData(guiWrapper.getPage(),e.getRawSlot());
            MyDragon dragon = mp.get(unique_name);
            if(dragon == null){
                Lang.sendFeedback(p,Lang.gui_not_found);
                return;
            }
            GuiManager.openGui(p,dragon.drop_gui,dragon.unique_name);
            return;
        }

    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnInventoryDrag(InventoryDragEvent e){
        InventoryHolder holder = e.getInventory().getHolder();
        if(!(holder instanceof GUIHolder)) return;
        GUIHolder guiHolder = (GUIHolder) holder;
        if(!(guiHolder.getGUI() instanceof GUIWrapper)) return;
        e.setCancelled(true);
    }
}
