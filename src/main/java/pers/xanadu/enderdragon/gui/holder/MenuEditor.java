package pers.xanadu.enderdragon.gui.holder;

import org.bukkit.inventory.InventoryHolder;
import pers.xanadu.enderdragon.gui.GUI;
import pers.xanadu.enderdragon.gui.GUIHolder;

public class MenuEditor extends GUIHolder implements InventoryHolder {
    private final String dragon_key;
    public String getDragon_key(){
        return this.dragon_key;
    }

    public MenuEditor(GUI gui, String key) {
        super(gui);
        this.dragon_key = key;
    }
}
