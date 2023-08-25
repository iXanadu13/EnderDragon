package pers.xanadu.enderdragon.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.PlayerInventory;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.gui.*;
import pers.xanadu.enderdragon.gui.holder.Menu;
import pers.xanadu.enderdragon.gui.holder.MenuEditor;
import pers.xanadu.enderdragon.gui.slot.PageJumpSlot;
import pers.xanadu.enderdragon.manager.DragonManager;
import pers.xanadu.enderdragon.manager.GuiManager;
import pers.xanadu.enderdragon.manager.RewardManager;
import pers.xanadu.enderdragon.util.MyDragon;

public class InventoryListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnInventoryClick(final InventoryClickEvent e){
        InventoryHolder holder = e.getInventory().getHolder();
        if(!(holder instanceof GUIHolder)) return;
        GUIHolder guiHolder = (GUIHolder) holder;
        if(!(guiHolder.getGUI() instanceof GUIWrapper)) return;
        e.setCancelled(true);
        GUIWrapper guiWrapper = (GUIWrapper) guiHolder.getGUI();
        if(e.getClickedInventory() instanceof PlayerInventory){
            return;
        }
        if(e.getRawSlot() >= 54 || e.getRawSlot() < 0){ //点击箱子以外界面会返回-999
            return;
        }
        Player p = (Player) e.getWhoClicked();
        GUISlot slot = guiWrapper.getSlot(e.getRawSlot());
        GUISlotType type = slot.getType();
        if(type == GUISlotType.EMPTY || type == GUISlotType.TIP || type == GUISlotType.PAGE_TIP){
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

        if(holder instanceof Menu){
            if(e.getClick() != ClickType.LEFT && e.getClick() != ClickType.RIGHT){
                return;
            }
            if(type == GUISlotType.ITEM_SLOT){
                return;
            }
            if(type == GUISlotType.PAGE_JUMP){
                String name = ((PageJumpSlot)slot).getGuiName();
                GuiManager.openGui(p,name,false);
                return;
            }
            if(type == GUISlotType.DRAGON_SLOT){
                String unique_name = guiWrapper.getData(guiWrapper.getPage(),e.getRawSlot());
                MyDragon dragon = DragonManager.mp.get(unique_name);
                if(dragon == null){
                    Lang.sendFeedback(p,Lang.gui_not_found);
                    return;
                }
                GuiManager.openGui(p,dragon.drop_gui,dragon.unique_name,false);
                return;
            }
        }
        else if(holder instanceof MenuEditor){
            MenuEditor menuEditor = (MenuEditor) holder;
            if(e.getClick() != ClickType.LEFT && e.getClick() != ClickType.RIGHT && e.getClick() != ClickType.SHIFT_RIGHT){
                return;
            }
            if(type == GUISlotType.ITEM_SLOT){
                if(e.getClick() != ClickType.SHIFT_RIGHT) return;
                int idx = guiWrapper.getPage()*guiWrapper.getItemSize()+guiWrapper.getItemSlotIdx(e.getRawSlot());
                //Bukkit.broadcastMessage(idx+"");
                boolean b = RewardManager.removeItem(menuEditor.getDragon_key(),idx);
                if(b) {
                    p.sendMessage(Lang.command_drop_item_remove_succeed);
                    guiWrapper.updateItemChanges(true);
                }
                else p.sendMessage(Lang.command_drop_item_remove_fail);
                return;
            }
            if(type == GUISlotType.PAGE_JUMP){
                String name = ((PageJumpSlot)slot).getGuiName();
                GuiManager.openGui(p,name,true);
                return;
            }
            if(type == GUISlotType.DRAGON_SLOT){
                String unique_name = guiWrapper.getData(guiWrapper.getPage(),e.getRawSlot());
                MyDragon dragon = DragonManager.mp.get(unique_name);
                if(dragon == null){
                    Lang.sendFeedback(p,Lang.gui_not_found);
                    return;
                }
                GuiManager.openGui(p,dragon.drop_gui,dragon.unique_name,true);
                return;
            }
        }

    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnInventoryDrag(final InventoryDragEvent e){
        InventoryHolder holder = e.getInventory().getHolder();
        if(!(holder instanceof GUIHolder)) return;
        if(holder instanceof Menu){
            GUIHolder guiHolder = (GUIHolder) holder;
            if(!(guiHolder.getGUI() instanceof GUIWrapper)) return;
            e.setCancelled(true);
        }
    }

}
