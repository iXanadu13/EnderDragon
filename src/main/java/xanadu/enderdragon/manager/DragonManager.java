package xanadu.enderdragon.manager;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.boss.DragonBattle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import xanadu.enderdragon.config.Config;
import xanadu.enderdragon.config.Lang;
import xanadu.enderdragon.utils.MyDragon;
import xanadu.enderdragon.utils.Version;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static xanadu.enderdragon.EnderDragon.*;
import static xanadu.enderdragon.config.Lang.*;
import static xanadu.enderdragon.manager.ItemManager.readAsItem;

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
    private Method getNMSWord;
    private Class<?> CraftWorldClass = null;
    private Class<?> WorldProviderTheEndClass = null;

    public static void reload(){
        new BukkitRunnable(){
            @Override
            public void run(){
                dragons.clear();
                mp.clear();
                dragon_names.clear();
                sum = 0;
                if(Config.dragon_setting_file == null){
                    error("\"dragon_setting_file\" in config.yml is empty!");
                    warn("Plugin will use the default config...");
                    Config.dragon_setting_file = new ArrayList<>();
                    Config.dragon_setting_file.add("default:5");
                    Config.dragon_setting_file.add("special:5");
                }
                for(String str : Config.dragon_setting_file){
                    String[] s = str.split(":");
                    if(s.length != 2){
                        error("\"dragon_setting_file\" in config.yml error! Key: " + str);
                        continue;
                    }
                    String path = "setting/" + s[0] + ".yml";
                    int edge = -1;
                    try {
                        edge = Integer.parseInt(s[1]);
                    } catch (NumberFormatException ignored){}
                    if(edge < 0) {
                        error("\"dragon_setting_file\" in config.yml error! Key: " + str);
                        continue;
                    }
                    File file = new File(plugin.getDataFolder(),path);
                    if(!file.exists()) {
                        try{
                            plugin.saveResource("setting/"+file.getName(),false);
                            file = new File(plugin.getDataFolder(),path);
                        }catch (Exception ignored){
                            error("Not Found setting/" + s[0] + ".yml ,skipped it.");
                            continue;
                        }
                    }
                    FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
                    readSettingFile(fc,edge);
                }
                dragons.sort((o1, o2) -> o2.priority - o1.priority);
                RewardManager.reload();
            }
        }.runTaskAsynchronously(plugin);

    }
    public static MyDragon judge(){
        if(Config.special_dragon_jude_mode.equalsIgnoreCase("edge")){
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
            error("The unique_name conflict! Key: "+myDragon.unique_name);
            return;
        }
        myDragon.icon = readAsItem(f,"icon");
        myDragon.display_name = f.getString("display_name","Special Dragon");
        myDragon.drop_gui = f.getString("drop_gui");
        myDragon.edge = edge;
        myDragon.priority = f.getInt("priority",1);
        myDragon.spawn_chance = f.getDouble("spawn_chance",0);
        myDragon.max_health = f.getInt("max_health",200);
        myDragon.spawn_health = f.getInt("spawn_health",200);
        myDragon.exp_drop = f.getInt("exp_drop",500);
        myDragon.dragon_egg_spawn_delay = f.getInt("dragon_egg_spawn.delay",410);
        myDragon.dragon_egg_spawn_x = f.getInt("dragon_egg_spawn.x",0);
        myDragon.dragon_egg_spawn_y = f.getInt("dragon_egg_spawn.y",70);
        myDragon.dragon_egg_spawn_z = f.getInt("dragon_egg_spawn.z",0);
        myDragon.dragon_egg_spawn_chance = f.getDouble("dragon_egg_spawn.chance",0);
        myDragon.attack_damage_modify = f.getDouble("attack_damage_modify",0);
        myDragon.move_speed_modify = f.getDouble("move_speed_modify",0);
        myDragon.armor_modify = f.getDouble("armor_modify",0);
        myDragon.armor_toughness_modify = f.getDouble("armor_toughness_modify",0);
        myDragon.crystal_heal_speed = f.getDouble("crystal_heal_speed",2.0);
        myDragon.suck_blood_enable = f.getBoolean("suck_blood.enable",true);
        myDragon.suck_blood_rate = f.getDouble("suck_blood.rate",50) / 100d;
        myDragon.suck_blood_base_amount = f.getDouble("suck_blood.base_amount",1);
        myDragon.suck_blood_only_player = f.getBoolean("suck_blood.only_player",true);
        List<String> stringList = f.getStringList("attack_potion_effect");
        List<PotionEffect> potions = new ArrayList<>();
        for(String string : stringList){
            String[] s = string.split(" ");
            if(s.length != 3) continue;
            PotionEffectType type = PotionEffectType.getByName(s[0].toUpperCase());
            if(type == null){
                error("Unknown potion type: " + s[0]);
                continue;
            }
            int duration = -1;
            try {
                duration = Integer.parseInt(s[1]);
            } catch (NumberFormatException ex){
                error("Wrong number format: " + s[1]);
            }
            if(duration == -1) continue;
            int level = -1;
            try {
                level = Integer.parseInt(s[2]);
            } catch (NumberFormatException ex){
                error("Wrong number format: " + s[2]);
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
                error("Wrong effect_cloud_color format!");
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
                error("Unknown potion type: " + s[0]);
                continue;
            }
            int duration = -1;
            try {
                duration = Integer.parseInt(s[1]);
            } catch (NumberFormatException ex){
                error("Wrong number format: " + s[1]);
            }
            if(duration == -1) continue;
            int level = -1;
            try {
                level = Integer.parseInt(s[2]);
            } catch (NumberFormatException ex){
                error("Wrong number format: " + s[2]);
            }
            if(level == -1) continue;
            PotionEffect potionEffect = new PotionEffect(type,duration*20,level-1);
            effectCloudPotions.add(potionEffect);
        }
        myDragon.effect_cloud_potion = effectCloudPotions;
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
    public static void modifyAttribute(EnderDragon dragon, Attribute attribute, double amount){
        AttributeInstance instance = dragon.getAttribute(attribute);
        assert instance != null;
        instance.addModifier(new AttributeModifier("EnderDragon",amount,AttributeModifier.Operation.ADD_NUMBER));
    }
    public static ChatColor randomColor(){
        return ChatColor.values()[ThreadLocalRandom.current().nextInt(16)];
    }
    public static void setGlowingColor(Entity entity,ChatColor color){
        if(server.getScoreboardManager().getMainScoreboard().getTeam("enderdragon-glow") == null) {
            server.getScoreboardManager().getMainScoreboard().registerNewTeam("enderdragon-glow");
        }
        Team team = server.getScoreboardManager().getMainScoreboard().getTeam("enderdragon-glow");
        assert team != null;
        if(mcMainVersion >= 13) team.setColor(color);
        else team.setPrefix(color.toString());
        team.addEntry(entity.getUniqueId().toString());
        entity.setGlowing(true);
    }
    public void initiateRespawn(Player p){
        boolean f = initiateRespawn(p.getWorld());
        if(!f) sendFeedback(p,"§cThere is already a dragon here or respawning has started.");
        else Lang.broadcastMSG(Lang.dragon_auto_respawn);
    }
    public boolean initiateRespawn(World world){
        if(world == null) return false;
        if(world.getEnvironment() != World.Environment.THE_END) return false;
        if(mcMainVersion >= 16){
            DragonBattle battle = world.getEnderDragonBattle();
            assert battle != null;
            if(battle.getEnderDragon() != null){
                error("There is already a dragon here.");
                return false;
            }
            if(battle.getRespawnPhase() == DragonBattle.RespawnPhase.NONE){
                Location cen = battle.getEndPortalLocation();
                if(cen == null) {
                    error("The world_the_end is unloaded.");
                    return false;
                }
                placeEndCrystals(world,cen);
                battle.initiateRespawn();
                return true;
            }
            else{
                error("The respawning has already started.");
            }
            return false;
        }
        try {
            Object battle = getEnderDragonBattle(world);
            assert battle != null;
            Field k = battle.getClass().getDeclaredField("k");
            k.setAccessible(true);
            Object isAlive = k.get(battle);
            if(!((boolean) isAlive)) {
                error("There is already a dragon here.");
                return false;
            }
            Field p = battle.getClass().getDeclaredField("p");
            p.setAccessible(true);
            Object phase = p.get(battle);
            if(phase != null) {
                error("The respawning has already started.");
                return false;
            }
            Field field = battle.getClass().getDeclaredField("o");
            field.setAccessible(true);
            Object BlockPosition = field.get(battle);
            if(BlockPosition == null){
                error("The world_the_end is unloaded.");
                return false;
            }
            if(this.getX == null) this.getX = BlockPosition.getClass().getMethod("getX");
            if(this.getY == null) this.getY = BlockPosition.getClass().getMethod("getY");
            if(this.getZ == null) this.getZ = BlockPosition.getClass().getMethod("getZ");
            Location loc = new Location(world,(int)getX.invoke(BlockPosition),(int)this.getY.invoke(BlockPosition),(int)this.getZ.invoke(BlockPosition));
            placeEndCrystals(world, loc);
            if (this.DragonBattle_e == null) this.DragonBattle_e = battle.getClass().getMethod("e");
            this.DragonBattle_e.invoke(battle);
        } catch (ReflectiveOperationException e) {
            warn("Your server version (" + Version.getVersion() + ") is not supported!");
        }
        return true;
    }
    private void placeEndCrystals(World world, Location cen){
        cen.add(0.5,1,0.5);
        for(int i = 0; i < 4; i++){
            EnderCrystal crystal = (EnderCrystal) world.spawnEntity(cen.clone().add(nxt[i][0],0,nxt[i][1]), EntityType.ENDER_CRYSTAL);
            if(Config.auto_respawn_invulnerable) crystal.setInvulnerable(true);
            crystal.setShowingBottom(false);
        }
    }
    private Object getEnderDragonBattle(World world){
        String version = Version.getVersion();
        if (mcMainVersion >= 12) {
            try {
                Object worldServer = getWorldServer(world);
                assert worldServer != null;
                Field field = worldServer.getClass().getField("worldProvider");
                Object worldProvider = field.get(worldServer);
                if(this.WorldProviderTheEndClass == null){
                    WorldProviderTheEndClass = Class.forName("net.minecraft.server."+version+".WorldProviderTheEnd");
                }
                Object WorldProviderTheEnd = WorldProviderTheEndClass.cast(worldProvider);
                String method_name;
                switch (version) {
                    case "v1_12_R1" -> method_name = "t";
                    case "v1_13_R1", "v1_13_R2" -> method_name = "r";
                    case "v1_14_R1" -> method_name = "q";
                    case "v1_15_R1" -> method_name = "o";
                    default -> method_name = null;
                }
                if(method_name == null) return null;
                Method method = WorldProviderTheEnd.getClass().getDeclaredMethod(method_name);
                return method.invoke(WorldProviderTheEnd);
            } catch (ReflectiveOperationException e) {
                warn("Your server version (" + Version.getVersion() + ") is not supported!");
                e.printStackTrace();
                return null;
            }
        }
        warn("Your server version (" + Version.getVersion() + ") is not supported!");
        return null;
    }
    private Object getWorldServer(World ThisWorld) {
        try {
            Object castClass = getCraftWorld(ThisWorld);
            return this.CraftWorldClass.getDeclaredMethod("getHandle").invoke(castClass);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }
    private Object getCraftWorld(World ThisWorld) {
        try {
            if (this.CraftWorldClass == null) {
                this.CraftWorldClass = Class.forName("org.bukkit.craftbukkit." + Version.getVersion() + ".CraftWorld");
            }
            if (this.CraftWorldClass.isInstance(ThisWorld)) {
                return this.CraftWorldClass.cast(ThisWorld);
            }
            return null;
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }
    private Object getWorld_e(World world){
        try {
            Object world_c = getCraftWorld(world);
            if(this.getNMSWord == null){
                this.getNMSWord = this.CraftWorldClass.getDeclaredMethod("getHandle");
            }
            return this.getNMSWord.invoke(world_c);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }

}
