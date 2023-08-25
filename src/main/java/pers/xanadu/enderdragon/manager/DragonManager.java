package pers.xanadu.enderdragon.manager;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.boss.DragonBattle;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pers.xanadu.enderdragon.config.Config;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.reward.RewardDist;
import pers.xanadu.enderdragon.util.ExtraPotionEffect;
import pers.xanadu.enderdragon.util.MyDragon;
import pers.xanadu.enderdragon.util.Version;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static pers.xanadu.enderdragon.EnderDragon.*;

public class DragonManager {
    public static ArrayList<MyDragon> dragons = new ArrayList<>();
    public static HashMap<String, MyDragon> mp = new HashMap<>();
    public static List<String> dragon_names = new ArrayList<>();
    private static int sum = 0;
    private static final int[][] nxt = {{3,0},{0,3},{-3,0},{0,-3}};
    private Method DragonBattle_e;
    private Method getX;
    private Method getY;
    private Method getZ;

    public static void reload(){
        new BukkitRunnable(){
            @Override
            public void run(){
                dragons.clear();
                mp.clear();
                dragon_names.clear();
                sum = 0;
                if(Config.dragon_setting_file == null){
                    Lang.error("\"dragon_setting_file\" in config.yml is empty!");
                    Lang.warn("Plugin will use the default config...");
                    Config.dragon_setting_file = new ArrayList<>();
                    Config.dragon_setting_file.add("default:5");
                    Config.dragon_setting_file.add("special:5");
                }
                for(String str : Config.dragon_setting_file){
                    String[] s = str.split(":");
                    if(s.length != 2){
                        Lang.error("\"dragon_setting_file\" in config.yml error! Key: " + str);
                        continue;
                    }
                    String path = "setting/" + s[0] + ".yml";
                    int edge = -1;
                    try {
                        edge = Integer.parseInt(s[1]);
                    } catch (NumberFormatException ignored){}
                    if(edge < 0) {
                        Lang.error("\"dragon_setting_file\" in config.yml error! Key: " + str);
                        continue;
                    }
                    File file = new File(plugin.getDataFolder(),path);
                    if(!file.exists()) {
                        try{
                            plugin.saveResource("setting/"+file.getName(),false);
                            file = new File(plugin.getDataFolder(),path);
                        }catch (Exception ignored){
                            Lang.error("Not Found setting/" + s[0] + ".yml ,skipped it.");
                            continue;
                        }
                    }
                    FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
                    if(!Version.setting_dragon.equals(fc.getString("version"))){
                        Lang.warn(Lang.plugin_wrong_file_version.replace("{file_name}", file.getName()));
                    }
                    readSettingFile(fc,edge);
                }
                dragons.sort((o1, o2) -> o2.priority - o1.priority);
                RewardManager.reload();
            }
        }.runTaskAsynchronously(plugin);

    }
    public static MyDragon judge(){
        if(Config.special_dragon_jude_mode.equalsIgnoreCase("weight")){
            int cnt = 0, random = ThreadLocalRandom.current().nextInt(0, sum);
            for(MyDragon cur : dragons){
                if(cnt <= random && cnt + cur.edge > random){
                    return cur;
                }
                cnt += cur.edge;
            }
        }
        else if(Config.special_dragon_jude_mode.equalsIgnoreCase("pc")){
            Iterator<MyDragon> it = dragons.iterator();
            MyDragon cur = null;
            while (it.hasNext()){
                cur = it.next();
                boolean judge = cur.spawn_chance > ThreadLocalRandom.current().nextDouble(100);
                if(judge) return cur;
            }
            return cur;
        }
        else if(Config.special_dragon_jude_mode.equalsIgnoreCase("edge")){
            int cnt = 0, random = ThreadLocalRandom.current().nextInt(0, sum);
            for(MyDragon cur : dragons){
                if(cnt <= random && cnt + cur.edge > random){
                    return cur;
                }
                cnt += cur.edge;
            }
            Lang.error("\"edge\" in \"special_dragon_jude_mode\" of config.yml is deprecated!Please use \"weight\" instead.");
        }
        return null;
    }
    public static void setSpecialKey(Entity e, String key){
        e.addScoreboardTag(key);
    }
    public static String getSpecialKey(Entity e){
        for(MyDragon a : dragons){
            if(e.getScoreboardTags().contains(a.unique_name)) return a.unique_name;
        }
        return null;
    }
    public static void readSettingFile(FileConfiguration f,int edge){
        MyDragon myDragon = new MyDragon();
        myDragon.unique_name = f.getString("unique_name","default");
        if(mp.containsKey(myDragon.unique_name)){
            Lang.error("The unique_name conflict! Key: "+myDragon.unique_name);
            return;
        }
        myDragon.icon = ItemManager.readFromBukkit(f,"icon");
        myDragon.display_name = f.getString("display_name","Special Dragon");
        myDragon.drop_gui = f.getString("drop_gui");
        myDragon.edge = edge;
        myDragon.priority = f.getInt("priority",1);
        myDragon.spawn_chance = f.getDouble("spawn_chance",0);
        myDragon.max_health = f.getInt("max_health",200);
        myDragon.spawn_health = f.getInt("spawn_health",200);
        myDragon.no_damage_tick = f.getInt("no_damage_tick",10);
        myDragon.exp_drop = f.getInt("exp_drop",500);
        myDragon.dragon_egg_spawn_delay = f.getInt("dragon_egg_spawn.delay",410);
        myDragon.dragon_egg_spawn_x = f.getInt("dragon_egg_spawn.x",0);
        myDragon.dragon_egg_spawn_y = f.getInt("dragon_egg_spawn.y",70);
        myDragon.dragon_egg_spawn_z = f.getInt("dragon_egg_spawn.z",0);
        myDragon.dragon_egg_spawn_chance = f.getDouble("dragon_egg_spawn.chance",0);
        //myDragon.move_speed_modify = f.getDouble("move_speed_modify",0);
        myDragon.armor_modify = f.getDouble("armor_modify",0);
        myDragon.armor_toughness_modify = f.getDouble("armor_toughness_modify",0);
        myDragon.crystal_heal_speed = f.getDouble("crystal_heal_speed",2.0);

        myDragon.attack_damage_modify = f.getDouble("attack.damage_modify",0);

        myDragon.suck_blood_enable = f.getBoolean("suck_blood.enable",true);
        myDragon.suck_blood_rate = f.getDouble("suck_blood.rate",50) / 100d;
        myDragon.suck_blood_base_amount = f.getDouble("suck_blood.base_amount",1);
        myDragon.suck_blood_only_player = f.getBoolean("suck_blood.only_player",true);

        List<ExtraPotionEffect> extraEffect = new ArrayList<>();
        int fire = f.getInt("attack.extra_effect.fire");
        if(fire>0) extraEffect.add(new ExtraPotionEffect(ExtraPotionEffect.ExtraPotionEffectType.fire,fire));
        if(Version.mcMainVersion >= 17){
            int freeze = f.getInt("attack.extra_effect.freeze");
            if(freeze>0) extraEffect.add(new ExtraPotionEffect(ExtraPotionEffect.ExtraPotionEffectType.freeze,freeze));
        }
        myDragon.attack_extra_effect = extraEffect;

        List<String> stringList = f.getStringList("attack.potion_effect");
        List<PotionEffect> potions = new ArrayList<>();
        for(String string : stringList){
            String[] s = string.split(" ");
            if(s.length != 3) continue;
            PotionEffectType type = PotionEffectType.getByName(s[0].toUpperCase());
            if(type == null){
                Lang.error("Unknown potion type: " + s[0]);
                continue;
            }
            int duration = -1;
            try {
                duration = Integer.parseInt(s[1]);
            } catch (NumberFormatException ex){
                Lang.error("Wrong number format: " + s[1]);
            }
            if(duration == -1) continue;
            int level = -1;
            try {
                level = Integer.parseInt(s[2]);
            } catch (NumberFormatException ex){
                Lang.error("Wrong number format: " + s[2]);
            }
            if(level == -1) continue;
            PotionEffect potionEffect = new PotionEffect(type,duration*20,level-1);
            potions.add(potionEffect);
        }
        myDragon.attack_potion_effect = potions;
        myDragon.spawn_cmd = f.getStringList("spawn_cmd");
        myDragon.death_cmd = f.getStringList("death_cmd");
        myDragon.spawn_broadcast_msg = f.getStringList("spawn_broadcast_msg");
        myDragon.death_broadcast_msg = f.getStringList("death_broadcast_msg");
        myDragon.msg_to_killer = f.getStringList("msg_to_killer");
        myDragon.glow_color = f.getString("glow_color","random");
        myDragon.bossbar_color = f.getString("bossbar.color","WHITE");
        myDragon.bossbar_style = f.getString("bossbar.style","SOLID");
        myDragon.effect_cloud_original_radius = f.getDouble("effect_cloud.original_radius",3);
        myDragon.effect_cloud_expand_speed = f.getDouble("effect_cloud.expand_speed",0.1333333);
        myDragon.effect_cloud_duration = f.getInt("effect_cloud.duration",60);
        String effect_cloud_color = f.getString("effect_cloud.color","none");
        String[] s0 = effect_cloud_color.split(":");
        if(s0.length != 3) myDragon.effect_cloud_color_R = -1;
        else{
            try{
                myDragon.effect_cloud_color_R = Integer.parseInt(s0[0]);
                myDragon.effect_cloud_color_G = Integer.parseInt(s0[1]);
                myDragon.effect_cloud_color_B = Integer.parseInt(s0[2]);
            }
            catch (NumberFormatException e){
                Lang.error("Wrong effect_cloud_color format!");
                myDragon.effect_cloud_color_R = -1;
            }
        }
        List<String> stringList2 = f.getStringList("effect_cloud.potion");
        List<PotionEffect> effectCloudPotions = new ArrayList<>();
        for(String string : stringList2){
            String[] s = string.split(" ");
            if(s.length != 3) continue;
            PotionEffectType type = PotionEffectType.getByName(s[0].toUpperCase());
            if(type == null){
                Lang.error("Unknown potion type: " + s[0]);
                continue;
            }
            int duration = -1;
            try {
                duration = Integer.parseInt(s[1]);
            } catch (NumberFormatException ex){
                Lang.error("Wrong number format: " + s[1]);
            }
            if(duration == -1) continue;
            int level = -1;
            try {
                level = Integer.parseInt(s[2]);
            } catch (NumberFormatException ex){
                Lang.error("Wrong number format: " + s[2]);
            }
            if(level == -1) continue;
            PotionEffect potionEffect = new PotionEffect(type,duration*20,level-1);
            effectCloudPotions.add(potionEffect);
        }
        myDragon.effect_cloud_potion = effectCloudPotions;
        myDragon.reward_dist = RewardDist.parse(f.getConfigurationSection("reward_dist"));
        dragons.add(myDragon);
        mp.put(myDragon.unique_name,myDragon);
        dragon_names.add(myDragon.unique_name);
        sum += edge;
    }
    public static void disable(){
        dragons.clear();
        mp.clear();
        dragon_names.clear();
    }

    public static void setAttribute(final EnderDragon dragon,final Attribute attribute, double amount){
        AttributeInstance instance = dragon.getAttribute(attribute);
        assert instance != null;
        instance.setBaseValue(amount);
    }
    public static void modifyAttribute(final EnderDragon dragon,final Attribute attribute, double amount){
        AttributeInstance instance = dragon.getAttribute(attribute);
        assert instance != null;
        instance.addModifier(new AttributeModifier("EnderDragon",amount,AttributeModifier.Operation.ADD_NUMBER));
    }

    public void initiateRespawn(final Player p){
        DragonRespawnResult res = initiateRespawn(p.getWorld());
        if(res == DragonRespawnResult.success) Lang.broadcastMSG(Lang.dragon_auto_respawn);
        else Lang.sendFeedback(p,"§c"+res.getMessage());
    }
    public void initiateRespawn(final CommandSender sender,final String world_name){
        DragonRespawnResult res = initiateRespawn(Bukkit.getWorld(world_name));
        if(res == DragonRespawnResult.success) Lang.broadcastMSG(Lang.dragon_auto_respawn);
        else Lang.sendFeedback(sender,"§c"+res.getMessage());
    }
    public void initiateRespawn(final String world_name){
        DragonRespawnResult res = initiateRespawn(Bukkit.getWorld(world_name));
        if(res == DragonRespawnResult.success) Lang.broadcastMSG(Lang.dragon_auto_respawn);
        else Lang.error(res.getMessage());
    }
    public boolean canRespawn(final String world_name){
        return canRespawn(Bukkit.getWorld(world_name));
    }
    public boolean canRespawn(final World world){
        if(world == null) return false;
        if(world.getEnvironment() != World.Environment.THE_END) return false;
        if(Version.mcMainVersion >= 16){//executes 1e5 times within 27ms
            DragonBattle battle = world.getEnderDragonBattle();
            if(battle == null) return false;
            if(battle.getEnderDragon() != null) return false;
            if(battle.getRespawnPhase() != DragonBattle.RespawnPhase.NONE) return false;
            Location cen = battle.getEndPortalLocation();
            if(cen == null) {
                battle.initiateRespawn();
                cen = battle.getEndPortalLocation();
                if(cen == null){
                    return false;
                }
            }
            return true;
        }
        try {//executes 1e5 times within 76ms
            Object battle = getInstance().getNMSManager().getEnderDragonBattle(world);
            if(battle == null) return false;
            Field k = battle.getClass().getDeclaredField("k");
            k.setAccessible(true);
            Object isAlive = k.get(battle);
            if(!((boolean) isAlive)) return false;
            Field p = battle.getClass().getDeclaredField("p");
            p.setAccessible(true);
            Object phase = p.get(battle);
            if(phase != null) return false;
            Field field = battle.getClass().getDeclaredField("o");
            field.setAccessible(true);
            Object BlockPosition = field.get(battle);
            if(BlockPosition == null){
                if (this.DragonBattle_e == null) this.DragonBattle_e = battle.getClass().getMethod("e");
                this.DragonBattle_e.invoke(battle);
                BlockPosition = field.get(battle);
                if(BlockPosition == null) return false;
            }
            return true;
        } catch (ReflectiveOperationException e) {
            return false;
        }
    }
    public boolean isRespawnRunning(final World world){
        if(world == null) return false;
        if(world.getEnvironment() != World.Environment.THE_END) return false;
        if(Version.mcMainVersion >= 16){
            DragonBattle battle = world.getEnderDragonBattle();
            assert battle != null;
            return battle.getRespawnPhase() != DragonBattle.RespawnPhase.NONE;
        }
        try {
            Object battle = getInstance().getNMSManager().getEnderDragonBattle(world);
            assert battle != null;
            Field p = battle.getClass().getDeclaredField("p");
            p.setAccessible(true);
            Object phase = p.get(battle);
            return phase != null;
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return false;
        }
    }
    public void refresh_respawn(final World world){
        if(world == null) return;
        if(world.getEnvironment() != World.Environment.THE_END) return;
        if(Version.mcMainVersion >= 16){
            DragonBattle battle = world.getEnderDragonBattle();
            assert battle != null;
            battle.initiateRespawn();
            return;
        }
        try {
            Object battle = getInstance().getNMSManager().getEnderDragonBattle(world);
            assert battle != null;
            if (this.DragonBattle_e == null) this.DragonBattle_e = battle.getClass().getMethod("e");
            this.DragonBattle_e.invoke(battle);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    private DragonRespawnResult initiateRespawn(final World world){
        if(world == null) return DragonRespawnResult.world_not_found;
        if(world.getEnvironment() != World.Environment.THE_END) return DragonRespawnResult.world_wrong_env;
        if(Version.mcMainVersion >= 16){
            DragonBattle battle = world.getEnderDragonBattle();
            assert battle != null;
            if(battle.getEnderDragon() != null) return DragonRespawnResult.dragon_has_existed;
            if(battle.getRespawnPhase() != DragonBattle.RespawnPhase.NONE) return DragonRespawnResult.respawn_has_started;
            Location cen = battle.getEndPortalLocation();
            if(cen == null) {
                battle.initiateRespawn();
                cen = battle.getEndPortalLocation();
                if(cen == null){
                    return DragonRespawnResult.world_unloaded;//也可尝试chunk.load()
                }
            }
            placeEndCrystals(world,cen);
            battle.initiateRespawn();
            return DragonRespawnResult.success;
        }
        try {
            Object battle = getInstance().getNMSManager().getEnderDragonBattle(world);
            assert battle != null;
            Field k = battle.getClass().getDeclaredField("k");
            k.setAccessible(true);
            Object isAlive = k.get(battle);
            if(!((boolean) isAlive)) return DragonRespawnResult.dragon_has_existed;
            Field p = battle.getClass().getDeclaredField("p");
            p.setAccessible(true);
            Object phase = p.get(battle);
            if(phase != null) return DragonRespawnResult.respawn_has_started;
            Field field = battle.getClass().getDeclaredField("o");
            field.setAccessible(true);
            Object BlockPosition = field.get(battle);
            if(BlockPosition == null){
                if (this.DragonBattle_e == null) this.DragonBattle_e = battle.getClass().getMethod("e");
                this.DragonBattle_e.invoke(battle);
                BlockPosition = field.get(battle);
                if(BlockPosition == null) return DragonRespawnResult.world_unloaded;
            }
            if(this.getX == null) this.getX = BlockPosition.getClass().getMethod("getX");
            if(this.getY == null) this.getY = BlockPosition.getClass().getMethod("getY");
            if(this.getZ == null) this.getZ = BlockPosition.getClass().getMethod("getZ");
            Location loc = new Location(world,(int)getX.invoke(BlockPosition),(int)this.getY.invoke(BlockPosition),(int)this.getZ.invoke(BlockPosition));
            placeEndCrystals(world, loc);
            if (this.DragonBattle_e == null) this.DragonBattle_e = battle.getClass().getMethod("e");
            this.DragonBattle_e.invoke(battle);
            return DragonRespawnResult.success;
        } catch (ReflectiveOperationException e) {
            return DragonRespawnResult.version_not_support;
        }

    }
    private void placeEndCrystals(final World world,final Location cen){
        cen.add(0.5,1,0.5);
        for(int i = 0; i < 4; i++){
            EnderCrystal crystal = (EnderCrystal) world.spawnEntity(cen.clone().add(nxt[i][0],0,nxt[i][1]), EntityType.ENDER_CRYSTAL);
            if(Config.crystal_invulnerable) crystal.setInvulnerable(true);
            crystal.setShowingBottom(false);
        }
    }
    public enum DragonRespawnResult{
        success,
        world_not_found,
        world_unloaded,
        world_wrong_env,
        respawn_has_started,
        dragon_has_existed,
        version_not_support;
        public String getMessage(){
            switch (this){
                case success: return "Success";
                case world_not_found: return "Can't find this world!";
                case world_unloaded: return "The world_the_end is unloaded.";
                case world_wrong_env: return "Respawn only can be called in the End.";
                case respawn_has_started: return "The respawning has already started.";
                case dragon_has_existed: return "There is already a dragon here.";
                case version_not_support: return "Your server version (" + Version.getVersion() + ") is not supported!";
                default: return "Unknown error!";
            }
        }
    }

}
