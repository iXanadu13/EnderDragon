package pers.xanadu.enderdragon.metadata;

import org.bukkit.entity.EnderDragon;

public class DragonInfo {
    private final EnderDragon handle;
    public String unique_name;
    public DragonInfo(final EnderDragon dragon, String unique_name){
        this.handle = dragon;
        this.unique_name = unique_name;
    }
    public EnderDragon getHandle(){
        return handle;
    }
}
