package pers.xanadu.enderdragon.listener;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import pers.xanadu.enderdragon.config.Config;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.manager.DamageManager;
import pers.xanadu.enderdragon.manager.RewardManager;
import pers.xanadu.enderdragon.util.MyDragon;
import pers.xanadu.enderdragon.util.Version;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import static pers.xanadu.enderdragon.EnderDragon.*;
import static pers.xanadu.enderdragon.manager.DragonManager.*;
import static pers.xanadu.enderdragon.manager.GlowManager.*;

public class CreatureSpawnListener implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    public void OnDragonSpawn(final CreatureSpawnEvent e){
        if(!(e.getEntity() instanceof EnderDragon)) return;
        if(Config.blacklist_worlds.contains(e.getEntity().getWorld().getName())) return;
        EnderDragon dragon = (EnderDragon) e.getEntity();
        MyDragon myDragon = judge();
        if(myDragon == null) {
            Lang.warn("special_dragon_jude_mode setting error!");
            return;
        }
        setSpecialKey(dragon, myDragon.unique_name);
        DamageManager.data.put(dragon.getUniqueId(),new ConcurrentHashMap<>());
        int times = data.getInt("times");
        Lang.runCommands(myDragon.spawn_cmd);
        for(String str : myDragon.spawn_broadcast_msg){
            Lang.broadcastMSG(str.replaceAll("%times%",String.valueOf(times)));
        }
        dragon.setCustomName(myDragon.display_name);
        setAttribute(dragon, Attribute.GENERIC_MAX_HEALTH, myDragon.max_health);
        dragon.setHealth(myDragon.spawn_health);

        //modifyAttribute(dragon, Attribute.GENERIC_MOVEMENT_SPEED, myDragon.move_speed_modify);//

        modifyAttribute(dragon, Attribute.GENERIC_ARMOR, myDragon.armor_modify);

        modifyAttribute(dragon, Attribute.GENERIC_ARMOR_TOUGHNESS, myDragon.armor_toughness_modify);
        String color = myDragon.glow_color.toUpperCase();
        if(!color.equals("NONE")) setGlowingColor(dragon,getGlowColor(color));
        else dragon.setGlowing(false);
        String bossBar_color = myDragon.bossbar_color.toUpperCase();
        String bossBar_style = myDragon.bossbar_style.toUpperCase();

        if(Version.mcMainVersion >= 14){
            BossBar bossBar = dragon.getBossBar();
            if(bossBar != null){
                bossBar.setColor(BarColor.valueOf(bossBar_color));
                bossBar.setStyle(BarStyle.valueOf(bossBar_style));
            }
        }
        else if(Version.mcMainVersion >= 12){
            getInstance().getBossBarManager().setBossBar(dragon.getWorld(),myDragon.display_name,bossBar_color,bossBar_style);
        }
        //dragon.getMetadata();

    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void OnCrystalPlaced(final PlayerInteractEvent e){
        if(!Config.resist_player_respawn) return;
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(e.getMaterial() != Material.END_CRYSTAL) return;
        if(e.getClickedBlock() == null) return;
        Material blockType = e.getClickedBlock().getType();
        if(blockType != Material.OBSIDIAN && blockType != Material.BEDROCK) return;
        World world = e.getPlayer().getWorld();
        if(world.getEnvironment() != World.Environment.THE_END) return;
        Block block = e.getClickedBlock();
        int d0 = block.getX();
        int d1 = block.getY() + 1;
        int d2 = block.getZ();
        if(world.getBlockAt(d0, d1, d2).getType() != Material.AIR) return;
        Location cen = block.getLocation().clone().add(0.5,1,0.5);
        Collection<Entity> list = world.getNearbyEntities(cen,0.5,1,0.5);
        if(!list.isEmpty()) return;
        e.setCancelled(true);
        ItemStack item = e.getItem();
        int amount = item.getAmount();
        e.getItem().setAmount(amount-1);
        EnderCrystal crystal = (EnderCrystal) world.spawnEntity(cen,EntityType.ENDER_CRYSTAL);
        crystal.setShowingBottom(false);
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void OnEffectCloudSpawn(final EntitySpawnEvent e){
        if(!(e.getEntity() instanceof AreaEffectCloud)) return;
        AreaEffectCloud effectCloud = (AreaEffectCloud) e.getEntity();
        if(effectCloud.getSource() == null) return;
        if(!(effectCloud.getSource() instanceof EnderDragon)) return;
        EnderDragon dragon = (EnderDragon) effectCloud.getSource();
        String unique_name = getSpecialKey(dragon);
        if(unique_name == null) return;
        MyDragon myDragon = mp.get(unique_name);
        if(myDragon == null) return;
        effectCloud.setRadius((float) myDragon.effect_cloud_original_radius);
        effectCloud.setRadiusPerTick((float) myDragon.effect_cloud_expand_speed/20f);
        effectCloud.setDuration(myDragon.effect_cloud_duration * 20);
        if(myDragon.effect_cloud_color_R != -1){
            effectCloud.setParticle(Particle.SPELL_MOB);
            effectCloud.setColor(Color.fromRGB(myDragon.effect_cloud_color_R,myDragon.effect_cloud_color_G,myDragon.effect_cloud_color_B));
        }
        effectCloud.clearCustomEffects();
        for(PotionEffect effect : myDragon.effect_cloud_potion){
            effectCloud.addCustomEffect(effect,true);
        }
    }
    @EventHandler(priority = EventPriority.LOW)
    public void OnDragonBreathGather(final PlayerInteractEvent e){
        if(!Config.resist_dragon_breath_gather) return;
        if(e.getMaterial() != Material.GLASS_BOTTLE) return;
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR) return;
        World world = e.getPlayer().getWorld();
        if(world.getEnvironment() != World.Environment.THE_END) return;
        Player p = e.getPlayer();
        Location cen = p.getLocation().clone().add(0d,0.9d,0d);
        Collection<Entity> list = world.getNearbyEntities(cen,2.3d,2.9d,2.3d);
        for(Entity entity : list){
            if(entity instanceof AreaEffectCloud) {
                AreaEffectCloud effectCloud = (AreaEffectCloud) entity;
                if(effectCloud.getSource() instanceof EnderDragon){
                    e.setCancelled(true);
                    break;
                }
            }
        }
    }


}
