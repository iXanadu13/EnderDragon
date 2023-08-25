package pers.xanadu.enderdragon.util;

import org.bukkit.entity.Player;

public class ExtraPotionEffect {
    private final ExtraPotionEffectType type;
    private final int tick;

    public void apply(final Player player){
        switch (type){
            case fire: {
                player.setFireTicks(player.getFireTicks()+tick);
                break;
            }
            case freeze: {
                player.setFreezeTicks(player.getFreezeTicks()+tick);
                break;
            }
        }
    }
    public ExtraPotionEffect(final String effect,int second){
        this(ExtraPotionEffectType.valueOf(effect.toLowerCase()),second);
    }
    public ExtraPotionEffect(final ExtraPotionEffectType type,int second){
        this.type = type;
        this.tick = second*20;
    }
    public enum ExtraPotionEffectType{
        fire,
        freeze
    }
}
