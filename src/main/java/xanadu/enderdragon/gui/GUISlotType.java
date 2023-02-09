package xanadu.enderdragon.gui;

import java.util.Map;
import java.util.TreeMap;

public enum GUISlotType {
    TIP,
    PAGE_TIP,
    PAGE_PREV,
    PAGE_NEXT,
    PAGE_JUMP,
    EMPTY,
    ITEM_SLOT,
    DRAGON_SLOT;
    private static final Map<String,GUISlotType> mp = new TreeMap<String,GUISlotType>(String.CASE_INSENSITIVE_ORDER);
    public static GUISlotType getByName(String str){
        if(str == null) return EMPTY;
        return mp.getOrDefault(str.replaceAll("_",""),EMPTY);
    }
    static{
        GUISlotType[] values = values();
        for (GUISlotType guiSlotType : values) {
            String name = guiSlotType.name();
            String target = "_";
            String replacement = "";
            mp.put(name.replaceAll(target, replacement), guiSlotType);
        }
    }
}
