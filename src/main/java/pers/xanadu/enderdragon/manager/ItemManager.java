package pers.xanadu.enderdragon.manager;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.persistence.PersistentDataContainer;
import pers.xanadu.enderdragon.EnderDragon;
import pers.xanadu.enderdragon.config.Config;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.reward.Reward;
import pers.xanadu.enderdragon.reward.Chance;
import pers.xanadu.enderdragon.util.Version;

import java.util.*;

public class ItemManager {
    public static String write(Reward reward){
        Chance chance = reward.getChance();
        return write(reward.getItem(),chance.getValue(),chance.getStr(),reward.getName());
    }
    public static String write(ItemStack item,double value,String str,String name){
        YamlConfiguration yaml = new YamlConfiguration();
        if(Config.advanced_setting_backslash_split_reward) yaml.options().pathSeparator('\\');
        if(name == null){
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                String displayName = meta.getDisplayName();
                if("".equals(displayName) || displayName == null){
                    name = item.getType().name().toLowerCase() + "(" + TaskManager.getCurrentTimeWithSpecialFormat() + ")";
                }
                else name = meta.getDisplayName();
            }
            else name = item.getType().name().toLowerCase() + "(" + TaskManager.getCurrentTimeWithSpecialFormat() + ")";
        }
        ConfigurationSection section = yaml.createSection(name);
        switch(Config.item_format_reward){
            case "_nbt" : {
                section.set("data_type","nbt");
                section.set("data",EnderDragon.getInstance().getNMSManager().getNBT(item));
                break;
            }
            case "_advanced" : {
                section.set("data_type","advanced");
                save2section_adv(item,section);
                break;
            }
            case "simple": {
                section.set("data_type","simple");
                save2section_simple(item,section);
                break;
            }
            case "nbt":
            case "advanced": {
                Lang.warn("Data_type 'nbt' and 'advanced' has been disabled temporarily for compatibility with 1.20.5+");
                //no break, go ahead to default block.
            }
            default : {
                section.set("data_type","default");
                section.set("data", item);
            }
        }
        ConfigurationSection drop_section = section.createSection("drop_chance");
        drop_section.set("value",value);
        drop_section.set("format",str);
        return yaml.saveToString();
    }
    public static Reward readAsReward(ConfigurationSection section){
        Set<String> strings = section.getKeys(false);
        String name = strings.iterator().next();
        ConfigurationSection section0 = section.getConfigurationSection(name);
        if(section0 == null) return null;
        String data_type = section0.getString("data_type");
        //data_type may be null
        ItemStack item;
        if("nbt".equals(data_type)) {
            item = readFromNBT(section0,"data");
        }
        else if("advanced".equals(data_type)) {
            item = readFromAdvData(section0, "data");
        }
        else {
            item = readFromBukkit(section0,"data");
        }
        ConfigurationSection chance_section = section0.getConfigurationSection("drop_chance");
        if(chance_section == null) return null;
        double d0 = chance_section.getDouble("value");
        String str = chance_section.getString("format");
        return new Reward(item,new Chance(d0, str));
    }
    public static ItemStack readFromAdvData(ConfigurationSection section, String path){
        if (Version.isNBT_UPDATE()){
            throw new UnsupportedOperationException(
                    "I'm sorry that data_type 'nbt' and 'advanced' have been temporarily disabled since 1.20.5.\n"+
                    "You can migrate configurations through '/ed migrate' EnderDragon v2.5.1.\n"+
                    "I will reconstruct this plugin add restore this function in the future."
            );
        }
        ConfigurationSection data = section.getConfigurationSection(path);
        if(data == null) return new ItemStack(Material.AIR);
        String type = data.getString("type");
        if(type == null || "AIR".equals(type)) return new ItemStack(Material.AIR);
        int amount = data.getInt("amount");
        Material material = Material.getMaterial(type);
        if(material == null) return new ItemStack(Material.AIR);
        ItemStack item = new ItemStack(material,amount);
        //internal
        if(data.contains("internal")){
            ConfigurationSection internal_section = data.getConfigurationSection("internal");
            if(internal_section!=null){
                if("nbt".equals(internal_section.getString("data_type"))){
                    String nbt = internal_section.getString("data");
                    Object cpd = EnderDragon.getInstance().getNMSManager().getCPD(nbt);
                    Map<String,Object> mp_tag = new HashMap<>();
                    mp_tag.put("tag",cpd);
                    Object full_cpd = EnderDragon.getInstance().getNMSManager().getNBTTagCompound(mp_tag);
                    item = EnderDragon.getInstance().getNMSManager().mergeItemCPD(item,full_cpd);
                }
//                else{
//                    Map<String,Object> mp = getUnhandledTags(internal_section);
//                    EnderDragon.getInstance().getNMSManager().setUnhandledTags(meta,mp);
//                }
//                Map<String,Object> mp = new HashMap<>();
//                internal_section.getKeys(false).forEach(key->{
//                    Object obj = internal_section.get(key);//obj instanceof String
//                    Object nbt_base = EnderDragon.getInstance().getNMSItemManager().readAsNBTBase((String) obj);
//                    mp.put(key,nbt_base);
//                });
//                Map<String,Object> mp = getUnhandledTags(internal_section);
//                NBTTagCompound cpd = getNBTTagCompound(mp);
//                net.minecraft.world.item.ItemStack ei = CraftItemStack.asNMSCopy(item);
//                ei.c(cpd);
//                item = CraftItemStack.asBukkitCopy(ei);
                //EnderDragon.getInstance().getNMSManager().setUnhandledTags(meta,mp);
            }
        }
        //damage
        if(data.contains("damage")){
            int damage = data.getInt("damage");
            item.setDurability((short) damage);
        }
        ItemMeta meta = item.getItemMeta();
        if(meta == null) return item;
        //DisplayName
        String display_name = data.getString("display_name");
        if(display_name != null) meta.setDisplayName(display_name);
        //LocalizedName
        if(data.contains("localized_name")){
            String LocalizedName = data.getString("localized_name");
            if(LocalizedName != null) meta.setLocalizedName(LocalizedName);
        }
        //lore
        if(data.contains("lore")){
            List<String> lores = data.getStringList("lore");
            if(!lores.isEmpty()) meta.setLore(lores);
        }
        //Enchantments
        if(data.contains("enchants")){
            ConfigurationSection enchants = data.getConfigurationSection("enchants");
            if(enchants != null){
                //Map<Enchantment,Integer> mp = new HashMap<>();
                enchants.getKeys(false).forEach(key->{
                    Enchantment enchantment = Enchantment.getByName(key);
                    int level = enchants.getInt(key);
                    if(enchantment != null && level>0){
                        meta.addEnchant(enchantment,level,true);
                    }
                });
            }
        }
        //CustomModelData
        if(data.contains("CustomModelData")){
            int CustomModelData = data.getInt("CustomModelData");
            meta.setCustomModelData(CustomModelData);
        }
        //AttributeModifiers
        if(data.contains("AttributeModifiers")){
            ConfigurationSection attributes = data.getConfigurationSection("AttributeModifiers");
            if(attributes != null){
                attributes.getKeys(false).forEach(name->{
                    Attribute attribute = Attribute.valueOf(name);
                    AttributeModifier modifier = (AttributeModifier) attributes.get(name);
                    if(modifier != null){
                        meta.addAttributeModifier(attribute,modifier);
                    }
                });
            }
        }
        //RepairCost
        if(data.contains("RepairCost")){
            int cost = data.getInt("RepairCost");
            if(meta instanceof Repairable){
                ((Repairable)meta).setRepairCost(cost);
            }
        }
        //ItemFlags
        if(data.contains("ItemFlags")){
            List<String> names = data.getStringList("ItemFlags");
            names.forEach(name->meta.addItemFlags(ItemFlag.valueOf(name)));
        }
        //Unbreakable
        if(data.contains("unbreakable") && data.getBoolean("unbreakable")) meta.setUnbreakable(true);
        //PersistentDataContainer
        if(data.contains("PersistentDataContainer")){
            ConfigurationSection dataContainer_section = data.getConfigurationSection("PersistentDataContainer");
            if(dataContainer_section != null){
                if("nbt".equals(dataContainer_section.getString("data_type"))){
                    String nbt = dataContainer_section.getString("data");
                    Object cpd = EnderDragon.getInstance().getNMSManager().getCPD(nbt);
                    PersistentDataContainer container = meta.getPersistentDataContainer();
                    EnderDragon.getInstance().getNMSManager().setPersistentDataContainer(container, cpd);
                }
            }
        }
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack readFromNBT(ConfigurationSection section, String path){
        if (Version.isNBT_UPDATE()){
            throw new UnsupportedOperationException(
                    "I'm sorry that data_type 'nbt' and 'advanced' have been temporarily disabled since 1.20.5.\n"+
                    "You can migrate configurations through '/ed migrate' EnderDragon v2.5.1.\n"+
                    "I will reconstruct this plugin add restore this function in the future."
            );
        }
        String nbt = section.getString(path);
        if (nbt == null) return new ItemStack(Material.AIR);
        return EnderDragon.getInstance().getNMSItemManager().readAsItem(nbt);
    }
    public static ItemStack readFromBukkit(ConfigurationSection section, String path){
        String nbt = section.getString(path);
        if (nbt == null) return new ItemStack(Material.AIR);
        return section.getItemStack(path);
    }
    public static ItemStack readFromSimple(ConfigurationSection section, String path){
        if (section == null) return new ItemStack(Material.AIR);
        section = section.getConfigurationSection(path);
        if(section == null) return new ItemStack(Material.AIR);
        //material
        String type = section.getString("material");
        if (type==null || "AIR".equals(type)) return new ItemStack(Material.AIR);
        Material material = Material.getMaterial(type);
        if (material == null) return new ItemStack(Material.AIR);
        //amount
        int amount = section.getInt("amount",1);
        ItemStack itemStack = new ItemStack(material,amount);
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        //display_name
        String display_name = section.getString("display_name");
        if (display_name != null) meta.setDisplayName(display_name);
        //loc_name
        if(section.contains("loc_name")){
            String LocalizedName = section.getString("loc_name");
            if(LocalizedName != null) meta.setLocalizedName(LocalizedName);
        }
        //lore
        if(section.contains("lore")){
            List<String> lore = section.getStringList("lore");
            if(!lore.isEmpty()) meta.setLore(lore);
        }
        //enchants
        if(section.contains("enchants")){
            ConfigurationSection enchants = section.getConfigurationSection("enchants");
            if(enchants != null){
                enchants.getKeys(false).forEach(key->{
                    Enchantment enchantment = Enchantment.getByName(key);
                    int level = enchants.getInt(key);
                    if(enchantment != null && level>0){
                        meta.addEnchant(enchantment,level,true);
                    }
                });
            }
        }
        //CustomModelData
        if(Version.mcMainVersion>=14 && section.contains("CustomModelData")){
            int CustomModelData = section.getInt("CustomModelData");
            meta.setCustomModelData(CustomModelData);
        }
        //RepairCost
        if(section.contains("RepairCost")){
            int cost = section.getInt("RepairCost");
            if(meta instanceof Repairable){
                ((Repairable)meta).setRepairCost(cost);
            }
        }
        //Unbreakable
        if(section.contains("unbreakable"))
            meta.setUnbreakable(section.getBoolean("unbreakable"));
        //ItemFlags
        if(section.contains("ItemFlags")){
            List<String> names = section.getStringList("ItemFlags");
            names.forEach(name->meta.addItemFlags(ItemFlag.valueOf(name)));
        }
        itemStack.setItemMeta(meta);
        //damage
        if (section.contains("damage")){
            int damage = section.getInt("damage");
            // 在itemStack.setItemMeta(meta)之后，直接设置到itemStack上
            itemStack.setDurability((short) damage);
        }
        return itemStack;
    }
    public static ItemStack readFromString(String str){
        YamlConfiguration yml = new YamlConfiguration();
        if(Config.advanced_setting_backslash_split_reward) yml.options().pathSeparator('\\');
        yml.set("test",str);
        return yml.getItemStack("test");
    }
    public static void save2section_simple(ItemStack itemStack, ConfigurationSection section){
        save2section_simple(itemStack,section,"data");
    }
    public static void save2section_simple(ItemStack itemStack, ConfigurationSection section,String path){
        ConfigurationSection res = section.createSection(path);
        //material
        String material = itemStack.getType().name();
        res.set("material",material);
        if ("AIR".equals(material)) return;
        //amount
        int amount = itemStack.getAmount();
        res.set("amount",amount);
        //damage
        int damage = itemStack.getDurability();
        if (damage != 0) res.set("damage",damage);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;
        //display_name
        if (meta.hasDisplayName()){
            String display_name = meta.getDisplayName();
            res.set("display_name",display_name);
        }
        //loc_name
        if (meta.hasLocalizedName()){
            String loc_name = meta.getLocalizedName();
            res.set("loc_name",loc_name);
        }
        //lore
        if (meta.hasLore()){
            List<String> lore = meta.getLore();
            res.set("lore",lore);
        }
        //enchants
        if (meta.hasEnchants()){
            ConfigurationSection enchants = res.createSection("enchants");
            Map<Enchantment,Integer> mp = meta.getEnchants();
            mp.forEach((enchantment, level) -> enchants.set(enchantment.getName(),level));
        }
        //CustomModelData
        if(Version.mcMainVersion>=14 && meta.hasCustomModelData()){
            res.set("CustomModelData",meta.getCustomModelData());//int
        }
        //RepairCost
        if(meta instanceof Repairable){
            Repairable repairable = (Repairable) meta;
            int RepairCost = repairable.getRepairCost();
            if (RepairCost != 0) res.set("RepairCost",RepairCost);
        }
        //Unbreakable
        if (meta.isUnbreakable()) {
            res.set("unbreakable",true);
        }
        //ItemFlags
        Set<ItemFlag> flags = meta.getItemFlags();
        if(!flags.isEmpty()){
            List<String> list = new ArrayList<>();
            flags.forEach(flag ->{
                list.add(flag.name());
            });
            res.set("ItemFlags",list);
        }
        //cannot handle nbt currently
    }
    public static void save2section_adv(ItemStack item, ConfigurationSection section){
        ConfigurationSection section_data = section.createSection("data");
        //type
        String type = item.getType().name();
        section_data.set("type",type);
        if("AIR".equals(type)) return;
        //amount
        int amount = item.getAmount();
        section_data.set("amount",amount);
        //damage
        int damage = item.getDurability();
        if(damage != 0) section_data.set("damage",damage);
        ItemMeta meta = item.getItemMeta();
        if(meta == null) return;
        //DisplayName
        String display_name = meta.getDisplayName();
        if(!"".equals(display_name)) section_data.set("display_name",display_name);
        //LocalizedName
        if(meta.hasLocalizedName()){
            section_data.set("localized_name",meta.getLocalizedName());
        }
        //lore
        List<String> lores = meta.getLore();
        if(lores != null) section_data.set("lore",lores);
        //Enchantments
        if(meta.hasEnchants()){
            ConfigurationSection enchants = section_data.createSection("enchants");
            Map<Enchantment,Integer> mp = meta.getEnchants();
            mp.forEach((enchantment,level) -> enchants.set(enchantment.getName(),level));
        }
        if(Version.mcMainVersion>=14 || "v1_13_R2".equals(Version.getVersion())){
            //AttributeModifiers
            if(meta.hasAttributeModifiers()){
                ConfigurationSection attributes = section_data.createSection("AttributeModifiers");
                meta.getAttributeModifiers().forEach((attribute,modifier)->{
                    attributes.set(attribute.name(),modifier);
                });
            }
        }
        if(Version.mcMainVersion>=14){
            //CustomModelData
            if(meta.hasCustomModelData()) {
                section_data.set("CustomModelData",meta.getCustomModelData());//int
            }
            //PersistentDataContainer
            PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
            if(!dataContainer.isEmpty()){
                ConfigurationSection dataContainer_section = section_data.createSection("PersistentDataContainer");
                String cpd = EnderDragon.getInstance().getNMSManager().PDCtoString(dataContainer);
                dataContainer_section.set("data_type","nbt");
                dataContainer_section.set("data",cpd);
            }
        }
        //RepairCost
        if(meta instanceof Repairable){
            Repairable repairable = (Repairable) meta;
            section_data.set("RepairCost",repairable.getRepairCost());
        }
        //ItemFlags
        Set<ItemFlag> flags = meta.getItemFlags();
        if(!flags.isEmpty()){
            List<String> list = new ArrayList<>();
            flags.forEach(flag ->{
                list.add(flag.name());
            });
            section_data.set("ItemFlags",list);
        }
        //Unbreakable
        section_data.set("unbreakable",meta.isUnbreakable());
        //internal
        Object cpd = EnderDragon.getInstance().getNMSManager().getCPD(item);
        Map<String,Object> mp = EnderDragon.getInstance().getNMSManager().cpdToMap(cpd);
        if(mp.containsKey("tag")){
            cpd = mp.get("tag");
            mp = EnderDragon.getInstance().getNMSManager().cpdToMap(cpd);
            mp.remove("Damage");
            if(mp.containsKey("display")){
                Object display = mp.get("display");
                Map<String,Object> display_mp = EnderDragon.getInstance().getNMSManager().cpdToMap(display);
                display_mp.remove("Name");
                display_mp.remove("LocName");
                display_mp.remove("Lore");
                Object new_display = EnderDragon.getInstance().getNMSManager().getNBTTagCompound(display_mp);
                mp.put("display",new_display);
            }
            //mp.remove("display");//Name, LocName, Lore, color
            mp.remove("Enchantments");
            mp.remove("AttributeModifiers");
            mp.remove("CustomModelData");
            mp.remove("PublicBukkitValues");//PersistentDataContainer
            mp.remove("RepairCost");
            mp.remove("HideFlags");//ItemFlags
            mp.remove("Unbreakable");
            //CanDestroy, CanPlaceOn
            Object new_cpd = EnderDragon.getInstance().getNMSManager().getNBTTagCompound(mp);
            ConfigurationSection internal = section_data.createSection("internal");
            internal.set("data_type","nbt");
            internal.set("data",new_cpd.toString());
        }
    }
    public static void addLoreFront(ItemStack item, String lore){
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            List<String> lores = meta.getLore();
            if (lores != null) {
                lores.add(0,lore);
                meta.setLore(lores);
            }
            else {
                meta.setLore(Collections.singletonList(lore));
            }
            item.setItemMeta(meta);
        }
    }
    public static void addLoreTail(ItemStack item, String lore){
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            List<String> lores = meta.getLore();
            if (lores != null) {
                lores.add(lore);
                meta.setLore(lores);
            }
            else {
                meta.setLore(Collections.singletonList(lore));
            }
            item.setItemMeta(meta);
        }
    }
    public static boolean isEmpty(ItemStack item){
        if(item == null) return true;
        if(item.getType() == Material.AIR) return true;
        return false;
    }




}
