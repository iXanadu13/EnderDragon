package xanadu.enderdragon.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GUIHolder implements InventoryHolder {
    private final GUI gui;

    public Inventory getInventory() {
        return this.gui.getInventory();
    }

    public GUI getGUI() {
        return this.gui;
    }

    public GUIHolder(GUI gui) {
        this.gui = gui;
    }
}
