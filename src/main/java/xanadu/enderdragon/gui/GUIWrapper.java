package xanadu.enderdragon.gui;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import xanadu.enderdragon.gui.slots.EmptySlot;
import xanadu.enderdragon.config.Lang;

import java.util.HashMap;
import java.util.List;

public class GUIWrapper extends GUI{
    private final String name;
    private int type;

    public String getName() {
        return this.name;
    }

    public GUIWrapper(GUIWrapper a, String key) {
        super(a.title, a.size, a.maxPage);
        this.inv = Bukkit.createInventory(new GUIHolder(this), this.size, this.title);
        this.slots.addAll(a.slots);
        this.dynamics.addAll(a.dynamics);
        this.name = a.name;
        this.type = a.type;
        this.init();
        this.resetPagedItem(type,key);
        this.setPage(0);
    }
    public GUIWrapper(ConfigurationSection config) {
        super(config.getString("Title", Lang.gui_default_title).replaceAll("&", "§"), calcline(config.getStringList("Slots").size()) * 9, config.getInt("Page", 1000));
        this.type = 0;
        this.name = config.getName();
        this.inv = Bukkit.createInventory(new GUIHolder(this), this.size, this.title);
        for(int i = 0 ; i < this.size ; i++){
            slots.add(new EmptySlot());
        }
        ConfigurationSection section = config.getConfigurationSection("Items");
        HashMap<Character,GUISlot> hash = new HashMap();
        if (section == null) return;
        for (String s : section.getKeys(false)) {
            GUISlot slot = GUISlot.parse(section.getConfigurationSection(s));
            hash.put(s.charAt(0), slot);
        }
        int n2 = 0;
        List<String> stringList = config.getStringList("Slots");
        for (String line : stringList) {
            int i;
            for (i = 0; i < line.length() && i < 9;  ++i) {
                char value = line.charAt(i);
                this.slots.set(n2++, (hash.getOrDefault(value, new EmptySlot())));
            }
            while(i ++ < 9){
                this.slots.set(n2++, new EmptySlot());
            }
        }
        boolean b = false;
        for(int i = 0 ; i < slots.size(); i++){
            GUISlotType type = this.slots.get(i).getType();
            if(type == GUISlotType.PAGE_PREV || type == GUISlotType.PAGE_NEXT || type == GUISlotType.PAGE_TIP){
                this.dynamics.add(i);
            }
            else if(type == GUISlotType.ITEM_SLOT){
                this.type = 1;
            }
            else if(type == GUISlotType.DRAGON_SLOT){
                this.type = 2;
            }
            else if (type == GUISlotType.PAGE_JUMP) {
                b = true;
            }
        }
    }


    private static int calcline(final int a) {
        if (a < 1) {
            return 1;
        }
        return Math.min(a, 6);
    }
}
