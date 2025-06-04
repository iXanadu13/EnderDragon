package pers.xanadu.enderdragon.manager;

import lombok.Getter;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.boss.DragonBattle;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;
import pers.xanadu.enderdragon.config.Config;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.metadata.DragonInfo;
import pers.xanadu.enderdragon.reward.RewardDist;
import pers.xanadu.enderdragon.reward.SpecialLoot;
import pers.xanadu.enderdragon.util.CollectionUtil;
import pers.xanadu.enderdragon.util.ExtraPotionEffect;
import pers.xanadu.enderdragon.metadata.MyDragon;
import pers.xanadu.enderdragon.util.Version;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pers.xanadu.enderdragon.EnderDragon.*;

public class DragonManager {
    /**
     * 底层是unmodifiableList
     */
    @Getter
    static List<MyDragon> dragons;
    static final Map<String, MyDragon> mp = new HashMap<>();
    public static List<String> dragon_names = new ArrayList<>();
    public static final Map<UUID,DragonInfo> existing_dragon = new ConcurrentHashMap<>();
    public static final Map<String,DragonInfo> main_dragon = new ConcurrentHashMap<>();
    private static int sum = 0;
    private static final int[][] nxt = {{3,0},{0,3},{-3,0},{0,-3}};
    private static final int[][] nxt_2 = {{2,0},{0,2},{-2,0},{0,-2}};
    private static final String getEDB_base;
    private static Class<?> CraftWorldClass;
    private static Class<?> WorldProviderTheEndClass;
    private static Class<?> EnderDragonBattleClass;
    private static Method DragonBattle_e;
    private static Method getX;
    private static Method getY;
    private static Method getZ;
    private static final Pattern pattern_attacker_top = Pattern.compile("%attacker_top_(\\d+)%");
    @Getter
    private static final EntityType ENDER_CRYSTAL = Version.NBT_UPDATE?EntityType.END_CRYSTAL:EntityType.valueOf("ENDER_CRYSTAL");

    public static void reload(){
        final List<MyDragon> dragons = new ArrayList<>();
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
            readSettingFile(fc,edge,dragons);
        }
        dragons.sort((o1, o2) -> o2.priority - o1.priority);
        DragonManager.dragons = Collections.unmodifiableList(dragons);
        RewardManager.reload();
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
        else{
            Lang.error("Unknown 'special_dragon_jude_mode' of config.yml.");
            Lang.error("Possible solutions: pc, weight(named 'edge' in old versions).");
        }
        //unreachable
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
    public static void readSettingFile(FileConfiguration f,int edge,List<MyDragon> dragons){
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
        myDragon.bossbar_color = f.getString("bossbar.color","WHITE").toUpperCase();
        myDragon.bossbar_style = f.getString("bossbar.style","SOLID").toUpperCase();
        myDragon.bossbar_create_fog = f.getBoolean("bossbar.create_fog",true);
        myDragon.bossbar_darken_sky = f.getBoolean("bossbar.darken_sky",true);
        myDragon.bossbar_play_boss_music = f.getBoolean("bossbar.play_boss_music",true);
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
        ConfigurationSection loots = f.getConfigurationSection("special_loot");
        Map<Integer, List<SpecialLoot>> loot_mp = new HashMap<>();
        if(loots != null){
            for (String key : loots.getKeys(false)) {
                ConfigurationSection loot = loots.getConfigurationSection(key);
                if(loot == null || !loot.getBoolean("enable")) continue;
                String type = loot.getString("type");
                SpecialLoot specialLoot;
                if("command".equals(type)){
                    List<String> commands = loot.getStringList("data");
                    boolean executeOffline = loot.getBoolean("execute-if-offline");
                    specialLoot = (player, damage) -> handleCommandLoot(commands,player,damage,executeOffline);
                }
                else if("exp".equals(type)){
                    int amount = loot.getInt("data"+f.options().pathSeparator()+"amount");
                    specialLoot = (player, __) -> {
                        Player p = Bukkit.getPlayer(player);
                        if(p != null) p.giveExp(amount);
                    };
                }
                else{
                    Lang.error("Unknown special_loot type: "+type);
                    continue;
                }
                String target = loot.getString("target");
                if(target == null) continue;
                if("%attacker%".equals(target)){
                    loot_mp.compute(0,(k,v)->{
                        if(v==null) return CollectionUtil.vec(specialLoot);
                        v.add(specialLoot);
                        return v;
                    });
                }
                else{
                    Matcher matcher = pattern_attacker_top.matcher(target);
                    if (matcher.find()) {
                        Integer rank = Integer.parseInt(matcher.group(1));
                        loot_mp.compute(rank,(k,v)->{
                            if(v==null) return CollectionUtil.vec(specialLoot);
                            v.add(specialLoot);
                            return v;
                        });
                    } else {
                        Lang.error("Wrong special_loot target: "+target);
                        continue;
                    }
                }
            }
        }
        myDragon.lootMap = loot_mp;
        dragons.add(myDragon);
        mp.put(myDragon.unique_name,myDragon);
        dragon_names.add(myDragon.unique_name);
        sum += edge;
    }
    private static void handleCommandLoot(List<String> list, String name, String damage, boolean executeOffline){
        if(list == null) return;

        Player player = Bukkit.getPlayer(name);
        if (player == null && !executeOffline)
            return;

        for (String cmd : list) {
            if(cmd.equals("")) continue;
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),cmd.replaceAll("%player%",name).replaceAll("%damage%",damage));
        }
    }
    public static void disable(){
        dragons = null;
        mp.clear();
        dragon_names.clear();
    }

    /**
     * 请勿修改MyDragon对象<p>
     * 方法将在后续被删除
     * @return MyDragon configuration
     */
    @Deprecated
    public static MyDragon get_dragon_config(String unique_name){
        return mp.get(unique_name);
    }

    @Nullable
    public static MyDragon getFromInfo(DragonInfo info){
        return mp.get(info.unique_name);
    }
    public static int getCount(){
        return data.getInt("times");
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

    public static void initiateRespawn(final Player p,final String uniqueId){
        DragonRespawnResult res = initiateRespawn(p.getWorld(),uniqueId);
        if(res == DragonRespawnResult.success) Lang.broadcastMSG(Lang.dragon_auto_respawn);
        else Lang.sendFeedback(p,"§c"+res.getMessage());
    }
    public static void initiateRespawn(final CommandSender sender,final String world_name,final String uniqueId){
        DragonRespawnResult res = initiateRespawn(Bukkit.getWorld(world_name),uniqueId);
        if(res == DragonRespawnResult.success) Lang.broadcastMSG(Lang.dragon_auto_respawn);
        else Lang.sendFeedback(sender,"§c"+res.getMessage());
    }
    public static void initiateRespawn(final String world_name,final String uniqueId){
        DragonRespawnResult res = initiateRespawn(Bukkit.getWorld(world_name),uniqueId);
        if(res == DragonRespawnResult.success) Lang.broadcastMSG(Lang.dragon_auto_respawn);
        else Lang.error(res.getMessage());
    }
    public static DragonRespawnResult initiateRespawn(final World world,final String uniqueId){
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
            if(uniqueId == null) placeEndCrystals(cen);
            else placeEndCrystals(cen,uniqueId);
            battle.initiateRespawn();
            return DragonRespawnResult.success;
        }
        try {
            Object battle = getEnderDragonBattle(world);
            assert battle != null;
            Field k = EnderDragonBattleClass.getDeclaredField("k");
            k.setAccessible(true);
            Object dragonKilled = k.get(battle);
            if(!((boolean) dragonKilled)) return DragonRespawnResult.dragon_has_existed;
            Field p = EnderDragonBattleClass.getDeclaredField("p");
            p.setAccessible(true);
            Object phase = p.get(battle);
            if(phase != null) return DragonRespawnResult.respawn_has_started;
            Field field = EnderDragonBattleClass.getDeclaredField("o");
            field.setAccessible(true);
            Object BlockPosition = field.get(battle);
            if(BlockPosition == null){
                DragonBattle_e.invoke(battle);
                BlockPosition = field.get(battle);
                if(BlockPosition == null) return DragonRespawnResult.world_unloaded;
            }
            if(getX == null) getX = BlockPosition.getClass().getMethod("getX");
            if(getY == null) getY = BlockPosition.getClass().getMethod("getY");
            if(getZ == null) getZ = BlockPosition.getClass().getMethod("getZ");
            Location loc = new Location(world,(int)getX.invoke(BlockPosition),(int)getY.invoke(BlockPosition),(int)getZ.invoke(BlockPosition));
            if(uniqueId == null) placeEndCrystals(loc);
            else placeEndCrystals(loc,uniqueId);
            DragonBattle_e.invoke(battle);
            return DragonRespawnResult.success;
        } catch (ReflectiveOperationException e) {
            return DragonRespawnResult.version_not_support;
        }
    }
    public static boolean canRespawn(final String world_name){
        return canRespawn(Bukkit.getWorld(world_name));
    }
    public static boolean canRespawn(final World world){
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
            Object battle = getEnderDragonBattle(world);
            if(battle == null) return false;
            Field k = EnderDragonBattleClass.getDeclaredField("k");
            k.setAccessible(true);
            Object dragonKilled = k.get(battle);
            if(!((boolean) dragonKilled)) return false;
            Field p = EnderDragonBattleClass.getDeclaredField("p");
            p.setAccessible(true);
            Object phase = p.get(battle);
            if(phase != null) return false;
            Field field = EnderDragonBattleClass.getDeclaredField("o");
            field.setAccessible(true);
            Object BlockPosition = field.get(battle);
            if(BlockPosition == null){
                DragonBattle_e.invoke(battle);
                BlockPosition = field.get(battle);
                if(BlockPosition == null) return false;
            }
            return true;
        } catch (ReflectiveOperationException e) {
            return false;
        }
    }

    /**
     * Only used to initialize. DON'T use this if speed is essential.
     * @param world world
     * @return The dragon found in this world, or null if not found.
     */
    public static EnderDragon getEnderDragon(final World world){
        if(world.getEnvironment() != World.Environment.THE_END){
            Iterator<EnderDragon> iterator = world.getEntitiesByClass(EnderDragon.class).iterator();
            return iterator.hasNext()?iterator.next():null;
        }
        if(Version.mcMainVersion >= 16){
            DragonBattle battle = world.getEnderDragonBattle();
            assert battle != null;
            return battle.getEnderDragon();
        }
        try {
            Object battle = getEnderDragonBattle(world);
            assert battle != null;
            Field k = EnderDragonBattleClass.getDeclaredField("k");
            k.setAccessible(true);
            Object dragonKilled = k.get(battle);
            if((boolean) dragonKilled) return null;
            Field m = EnderDragonBattleClass.getDeclaredField("m");
            m.setAccessible(true);
            Object uuid = m.get(battle);
//            Field d = EnderDragonBattleClass.getDeclaredField("d");
//            d.setAccessible(true);
//            Object worldServer = d.get(battle);
            return (EnderDragon) Bukkit.getEntity((UUID) uuid);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }
    public static Location getEndPortalLocation(final World world){
        if(world == null) return null;
        if(world.getEnvironment() != World.Environment.THE_END) return null;
        if(Version.mcMainVersion >= 16){
            DragonBattle battle = world.getEnderDragonBattle();
            if(battle == null) return null;
            Location cen = battle.getEndPortalLocation();
            if(cen == null) {
                battle.initiateRespawn();
                cen = battle.getEndPortalLocation();
            }
            return cen;
        }
        try {
            Object battle = getEnderDragonBattle(world);
            if(battle == null) return null;
            Field field = EnderDragonBattleClass.getDeclaredField("o");
            field.setAccessible(true);
            Object BlockPosition = field.get(battle);
            if(BlockPosition == null){
                DragonBattle_e.invoke(battle);
                BlockPosition = field.get(battle);
                if(BlockPosition == null) return null;
            }
            if(getX == null) getX = BlockPosition.getClass().getMethod("getX");
            if(getY == null) getY = BlockPosition.getClass().getMethod("getY");
            if(getZ == null) getZ = BlockPosition.getClass().getMethod("getZ");
            return new Location(world,(int)getX.invoke(BlockPosition),(int)getY.invoke(BlockPosition),(int)getZ.invoke(BlockPosition));
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }
    public static boolean isRespawnRunning(final World world){
        if(world == null) return false;
        if(world.getEnvironment() != World.Environment.THE_END) return false;
        if(Version.mcMainVersion >= 16){
            DragonBattle battle = world.getEnderDragonBattle();
            assert battle != null;
            return battle.getRespawnPhase() != DragonBattle.RespawnPhase.NONE;
        }
        try {
            Object battle = getEnderDragonBattle(world);
            assert battle != null;
            Field p = EnderDragonBattleClass.getDeclaredField("p");
            p.setAccessible(true);
            Object phase = p.get(battle);
            return phase != null;
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static void refresh_respawn(final World world){
        if(world == null) return;
        if(world.getEnvironment() != World.Environment.THE_END) return;
        if(Version.mcMainVersion >= 16){
            DragonBattle battle = world.getEnderDragonBattle();
            assert battle != null;
            battle.initiateRespawn();
            return;
        }
        try {
            Object battle = getEnderDragonBattle(world);
            assert battle != null;
            DragonBattle_e.invoke(battle);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
    private static void placeEndCrystals(final Location cen){
        World world = cen.getWorld();
        if(world == null) return;
        cen.add(0.5,1,0.5);
        for(int i = 0; i < 4; i++){
            EnderCrystal crystal = (EnderCrystal) world.spawnEntity(cen.clone().add(nxt[i][0],0,nxt[i][1]), ENDER_CRYSTAL);
            if(Config.crystal_invulnerable) crystal.setInvulnerable(true);
            crystal.setShowingBottom(false);
        }
    }
    private static void placeEndCrystals(final Location cen,final String uniqueId){
        World world = cen.getWorld();
        if(world == null) return;
        cen.add(0.5,1,0.5);
        for(int i=0;i<4;i++){
            Location loc = cen.clone().add(nxt_2[i][0],0,nxt_2[i][1]);
            world.getNearbyEntities(loc,0.5,0.5,0.5).forEach(entity -> {
                if(entity instanceof EnderCrystal) entity.remove();
            });
        }
        for(int i = 0; i < 4; i++){
            EnderCrystal crystal = (EnderCrystal) world.spawnEntity(cen.clone().add(nxt[i][0],0,nxt[i][1]), ENDER_CRYSTAL);
            if(Config.crystal_invulnerable) crystal.setInvulnerable(true);
            crystal.setShowingBottom(false);
            crystal.addScoreboardTag("ed_spe_"+uniqueId);
        }
    }
    public static MyDragon getDesignatedDragon(final World world){
        List<EnderCrystal> list = new ArrayList<>();
        Location cen = DragonManager.getEndPortalLocation(world).add(0.5,1,0.5);
        for(int i=0;i<4;i++){
            Location loc = cen.clone().add(nxt_2[i][0],0,nxt_2[i][1]);
            world.getNearbyEntities(loc,0.5,0.5,0.5).forEach(entity -> {
                if(entity instanceof EnderCrystal) list.add((EnderCrystal) entity);
            });
        }
        for(EnderCrystal crystal : list) {
            Set<String> tags = crystal.getScoreboardTags();
            for(MyDragon a : dragons){
                if(tags.contains("ed_spe_"+a.unique_name)) {
                    return a;
                }
            }
        }
        return null;
    }
    public static Object getEnderDragonBattle(final World world){
        String version = Version.getVersion();
        if (Version.mcMainVersion>=12 && Version.mcMainVersion<16) {
            try {
                Object worldServer = getWorldServer(world);
                assert worldServer != null;
                Field field = worldServer.getClass().getField("worldProvider");
                Object worldProvider = field.get(worldServer);
                Object WorldProviderTheEnd = WorldProviderTheEndClass.cast(worldProvider);
                String method_name = getEDB_base;
                if(method_name == null) return null;
                Method method = WorldProviderTheEnd.getClass().getDeclaredMethod(method_name);
                return method.invoke(WorldProviderTheEnd);
            } catch (ReflectiveOperationException e) {
                Lang.warn("Your server version (" + version + ") is not supported!");
                e.printStackTrace();
            }
        }
        Lang.warn("Your server version (" + version + ") is not supported!");
        return null;
    }
    private static Object getWorldServer(final World world) {
        try {
            Object castClass = getCraftWorld(world);
            return CraftWorldClass.getDeclaredMethod("getHandle").invoke(castClass);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static Object getCraftWorld(final World world) {
        if (CraftWorldClass.isInstance(world)) return CraftWorldClass.cast(world);
        return null;
    }
    @Deprecated
    public static void initiateRespawn(final Player p){
        DragonRespawnResult res = initiateRespawn(p.getWorld());
        if(res == DragonRespawnResult.success) Lang.broadcastMSG(Lang.dragon_auto_respawn);
        else Lang.sendFeedback(p,"§c"+res.getMessage());
    }
    @Deprecated
    public static void initiateRespawn(final CommandSender sender,final String world_name){
        DragonRespawnResult res = initiateRespawn(Bukkit.getWorld(world_name));
        if(res == DragonRespawnResult.success) Lang.broadcastMSG(Lang.dragon_auto_respawn);
        else Lang.sendFeedback(sender,"§c"+res.getMessage());
    }
    @Deprecated
    public static void initiateRespawn(final String world_name){
        DragonRespawnResult res = initiateRespawn(Bukkit.getWorld(world_name));
        if(res == DragonRespawnResult.success) Lang.broadcastMSG(Lang.dragon_auto_respawn);
        else Lang.error(res.getMessage());
    }
    @Deprecated
    public static DragonRespawnResult initiateRespawn(final World world){
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
                    return DragonRespawnResult.world_unloaded;
                }
            }
            placeEndCrystals(cen);
            battle.initiateRespawn();
            return DragonRespawnResult.success;
        }
        try {
            Object battle = getEnderDragonBattle(world);
            assert battle != null;
            Field k = EnderDragonBattleClass.getDeclaredField("k");
            k.setAccessible(true);
            Object dragonKilled = k.get(battle);
            if(!((boolean) dragonKilled)) return DragonRespawnResult.dragon_has_existed;
            Field p = EnderDragonBattleClass.getDeclaredField("p");
            p.setAccessible(true);
            Object phase = p.get(battle);
            if(phase != null) return DragonRespawnResult.respawn_has_started;
            Field field = EnderDragonBattleClass.getDeclaredField("o");
            field.setAccessible(true);
            Object BlockPosition = field.get(battle);
            if(BlockPosition == null){
                DragonBattle_e.invoke(battle);
                BlockPosition = field.get(battle);
                if(BlockPosition == null) return DragonRespawnResult.world_unloaded;
            }
            if(getX == null) getX = BlockPosition.getClass().getMethod("getX");
            if(getY == null) getY = BlockPosition.getClass().getMethod("getY");
            if(getZ == null) getZ = BlockPosition.getClass().getMethod("getZ");
            Location loc = new Location(world,(int)getX.invoke(BlockPosition),(int)getY.invoke(BlockPosition),(int)getZ.invoke(BlockPosition));
            placeEndCrystals(loc);
            DragonBattle_e.invoke(battle);
            return DragonRespawnResult.success;
        } catch (ReflectiveOperationException e) {
            return DragonRespawnResult.version_not_support;
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

    static{
        String version = Version.getVersion();
        switch (version) {
            case "v1_12_R1" : {
                getEDB_base = "t";
                break;
            }
            case "v1_13_R1" :
            case "v1_13_R2" : {
                getEDB_base = "r";
                break;
            }
            case "v1_14_R1" : {
                getEDB_base = "q";
                break;
            }
            case "v1_15_R1" : {
                getEDB_base = "o";
                break;
            }
            default : getEDB_base = null;
        }
        if(Version.mcMainVersion>=12 && Version.mcMainVersion<16){
            try{
                CraftWorldClass = Class.forName("org.bukkit.craftbukkit." + version + ".CraftWorld");
                WorldProviderTheEndClass = Class.forName("net.minecraft.server."+version+".WorldProviderTheEnd");
                EnderDragonBattleClass = Class.forName("net.minecraft.server."+version+".EnderDragonBattle");
                DragonBattle_e = EnderDragonBattleClass.getMethod("e");
            }catch (ReflectiveOperationException e){
                e.printStackTrace();
            }
        }
    }
}
