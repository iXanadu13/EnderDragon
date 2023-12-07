package pers.xanadu.enderdragon.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.TestOnly;
import pers.xanadu.enderdragon.EnderDragon;
import pers.xanadu.enderdragon.config.Config;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class NMSUtil {
    private Method getNMSWord;
    private Method asNMSCopy;
    private Method copyNMSStack;
    private Method asBukkitCopy;
    private Method asCraftMirror;
    /**
     * 浅拷贝unhandledTags
     */
    private Method asCraftCopy;
    private Method NMSItemStack_save;
    private Method NMSItemStack_setTag;
    private Method cpdToEi;
    private Method stringToCPD;
    private Method stringToNBTBase;
    private Method serializeNBTBase;
    private Method deserializeObject;
    private Method PDCtoCPD;
    private Method PersistentDataContainer_putAll;
    private Method NBTTagCompound_set;
    private Field unhandledTags;
    private Field internalTag;
    private Field MOJANGSON_PARSER;
    private Field world_c_environment;
    private Field NBTTagCompound_map;
    private Class<?> WorldClass;
    private Class<?> CraftWorldClass;
    private Class<?> WorldProviderClass;
    //private Class<?> WorldProviderTheEndClass;
    private Class<?> CraftItemStackClass;
    private Class<?> CraftMetaItemClass;
    private Class<?> NMSItemStackClass;
    private Class<?> CraftEntityClass;
    private Class<?> NBTTagCompoundClass;
    private Class<?> NBTBaseClass;
    private Class<?> MojangsonParserClass;
    private Class<?> CraftNBTTagConfigSerializerClass;
    private Class<?> CraftPersistentDataContainerClass;

    public void init(){
        String version = Version.getVersion();
        try{
            this.CraftWorldClass = Class.forName("org.bukkit.craftbukkit." + version + ".CraftWorld");
            this.getNMSWord = CraftWorldClass.getDeclaredMethod("getHandle");
            this.WorldClass = this.getNMSWord.getReturnType().getSuperclass();
            this.world_c_environment = CraftWorldClass.getDeclaredField("environment");
            this.world_c_environment.setAccessible(true);
            if(Version.mcMainVersion>=12 && Version.mcMainVersion<16){
                this.WorldProviderClass = Class.forName("net.minecraft.server."+version+".WorldProvider");
                //this.WorldProviderTheEndClass = Class.forName("net.minecraft.server."+version+".WorldProviderTheEnd");
            }
            this.CraftItemStackClass = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
            this.CraftMetaItemClass = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftMetaItem");
            this.unhandledTags = CraftMetaItemClass.getDeclaredField("unhandledTags");
            unhandledTags.setAccessible(true);
            this.internalTag = CraftMetaItemClass.getDeclaredField("internalTag");
            internalTag.setAccessible(true);
            this.NMSItemStackClass = CraftItemStackClass.getDeclaredField("handle").getType();
            this.CraftEntityClass = Class.forName("org.bukkit.craftbukkit."+version+".entity.CraftEntity");
            init_NBTTagCompoundClass();
            init_NBTBaseClass();
            init_NBTTagCompound_map();
            this.NBTTagCompound_set = NBTTagCompoundClass.getMethod(getMethodName(ReflectiveMethod.NBTTagCompound_set),String.class,NBTBaseClass);
            this.asNMSCopy = CraftItemStackClass.getMethod("asNMSCopy",ItemStack.class);
            this.copyNMSStack = CraftItemStackClass.getMethod("copyNMSStack",NMSItemStackClass,int.class);
            this.asBukkitCopy = CraftItemStackClass.getMethod("asBukkitCopy",NMSItemStackClass);
            this.asCraftMirror = CraftItemStackClass.getMethod("asCraftMirror",NMSItemStackClass);
            this.asCraftCopy = CraftItemStackClass.getMethod("asCraftCopy",ItemStack.class);
            this.NMSItemStack_save = NMSItemStackClass.getMethod(getMethodName(ReflectiveMethod.NMSItemStack_save),NBTTagCompoundClass);
            this.NMSItemStack_setTag = NMSItemStackClass.getMethod(getMethodName(ReflectiveMethod.NMSItemStack_setTag),NBTTagCompoundClass);
            if(Version.mcMainVersion>=13) this.cpdToEi = NMSItemStackClass.getMethod("a",NBTTagCompoundClass);

            if(Version.mcMainVersion<=16) this.MojangsonParserClass = Class.forName("net.minecraft.server."+version+".MojangsonParser");
            else this.MojangsonParserClass = Class.forName("net.minecraft.nbt.MojangsonParser");
            this.stringToCPD = MojangsonParserClass.getMethod(getMethodName(ReflectiveMethod.MojangsonParser_parse),String.class);
            this.stringToNBTBase = MojangsonParserClass.getDeclaredMethod(getMethodName(ReflectiveMethod.MojangsonParser_parseLiteral),String.class);
            stringToNBTBase.setAccessible(true);

            if(Version.mcMainVersion>13 || "v1_13_R2".equals(Version.getVersion())){
                this.CraftNBTTagConfigSerializerClass = Class.forName("org.bukkit.craftbukkit." + version + ".util.CraftNBTTagConfigSerializer");
                this.serializeNBTBase = CraftNBTTagConfigSerializerClass.getMethods()[0];
                this.deserializeObject = CraftNBTTagConfigSerializerClass.getMethod("deserialize",Object.class);
                this.MOJANGSON_PARSER = CraftNBTTagConfigSerializerClass.getDeclaredField("MOJANGSON_PARSER");
                MOJANGSON_PARSER.setAccessible(true);
            }
            if(Version.mcMainVersion>=14){
                this.CraftPersistentDataContainerClass = Class.forName("org.bukkit.craftbukkit." + version + ".persistence.CraftPersistentDataContainer");
                this.PDCtoCPD = CraftPersistentDataContainerClass.getDeclaredMethod("toTagCompound");
                this.PersistentDataContainer_putAll = CraftPersistentDataContainerClass.getDeclaredMethod("putAll",NBTTagCompoundClass);
            }



        }catch(ReflectiveOperationException e){
            e.printStackTrace();
        }
        if(Config.debug) check();
    }
    public void check(){
        try{
            Field[] fields = NMSUtil.class.getDeclaredFields();
            for(Field field : fields){
                Bukkit.getLogger().info(field.getName()+"->"+field.get(this));
            }
        }catch (ReflectiveOperationException e){
            e.printStackTrace();
        }
    }
//    public Object getMetaCPD(ItemMeta meta){
//        try{
//            Object craft_meta = CraftMetaItemClass.cast(meta);
//            Object internalTag = this.internalTag.get(craft_meta);
//            return internalTag;
//        }catch (ReflectiveOperationException e){
//            e.printStackTrace();
//            return null;
//        }
//    }

    /**
     * 只保留物品type和amount
     * nbt深合并需要遍历每一层
     */
    @Deprecated
    public ItemStack mergeItemCPD(final ItemStack item,final Object cpd){
        Map<String,Object> self = cpdToMap(getCPD(item));
        Map<String,Object> rhs = cpdToMap(cpd);
        self.putAll(rhs);
        Object new_cpd = getNBTTagCompound(self);
        return EnderDragon.getInstance().getNMSItemManager().cpdToItem(new_cpd);
    }
    public Map<String,Object> cpdToMap(final Object cpd){
        try{
            return (Map<String, Object>) NBTTagCompound_map.get(cpd);
        }catch (ReflectiveOperationException e){
            e.printStackTrace();
            return null;
        }
    }
    public Object getNBTTagCompound(final Map<String,Object> mp){
        try{
            Object cpd = NBTTagCompoundClass.newInstance();
            mp.forEach((key,value)->{
                Object res;
                if(value instanceof Map){
                    res = getNBTTagCompound((Map<String, Object>) value);
                }
                else res = value;
                try{
                    NBTTagCompound_set.invoke(cpd,key,res);
                }catch (ReflectiveOperationException e){
                    throw new RuntimeException("Reflective error!");
                }
            });
            return cpd;
        }catch (ReflectiveOperationException e){
            e.printStackTrace();
            return null;
        }
    }
    public Object StringParseLiteral(final String str){
        try{
            return stringToNBTBase.invoke(MOJANGSON_PARSER.get(null),str);
        }catch (ReflectiveOperationException e){
            e.printStackTrace();
            return null;
        }
    }
    public void setPersistentDataContainer(final PersistentDataContainer dataContainer,final Object cpd){
        try{
            Object PDC = CraftPersistentDataContainerClass.cast(dataContainer);
            PersistentDataContainer_putAll.invoke(PDC,cpd);
        }catch (ReflectiveOperationException e){
            e.printStackTrace();
        }
    }
    public String PDCtoString(final PersistentDataContainer dataContainer){
        try{
            Object PDC = CraftPersistentDataContainerClass.cast(dataContainer);
            return PDCtoCPD.invoke(PDC).toString();
        }catch (ReflectiveOperationException e){
            e.printStackTrace();
            return null;
        }
    }

    public Object deserializeObject(final Object object){
        try{
            return deserializeObject.invoke(null,object);
        }catch (ReflectiveOperationException e){
            e.printStackTrace();
            return null;
        }
    }
    public Object serializeNBTBase(final Object nbt_base){
        try{
            return serializeNBTBase.invoke(null, nbt_base);
        }catch (ReflectiveOperationException e){
            e.printStackTrace();
            return null;
        }
    }
    @TestOnly
    public void setUnhandledTags(final ItemMeta meta, Map<String,?> unhandledTags){
        try{
            Object craft_meta = CraftMetaItemClass.cast(meta);
            this.unhandledTags.set(craft_meta, unhandledTags);
        }catch (ReflectiveOperationException e){
            e.printStackTrace();
        }
    }
    /**
     * @return meta中unhandledTags的引用
     */
    @TestOnly
    public Map<String,Object> getUnhandledTags(final ItemMeta meta){
        try{
            Object craft_meta = CraftMetaItemClass.cast(meta);
            Object unhandledTags = this.unhandledTags.get(craft_meta);
            return (Map<String, Object>) unhandledTags;
        }catch (ReflectiveOperationException e){
            e.printStackTrace();
            return null;
        }
    }
    public ItemStack applyItemTag(final ItemStack item,final Object cpd){
        try{
            Object ei = asNMSCopy.invoke(null,item);
            NMSItemStack_setTag.invoke(ei,cpd);
            return (ItemStack) asBukkitCopy.invoke(null,ei);
        }catch (ReflectiveOperationException e){
            e.printStackTrace();
            return new ItemStack(Material.AIR);
        }
    }
    public String getNBT(final ItemStack item){
        try{
            Object ci = CraftItemStackClass.isInstance(item)?CraftItemStackClass.cast(item):asCraftCopy.invoke(null,item);//浅拷贝unhandledTags
            Object ei = asNMSCopy.invoke(null, ci);
            Object cpd = NMSItemStack_save.invoke(ei, NBTTagCompoundClass.newInstance());
            return cpd.toString();
        }catch (ReflectiveOperationException e){
            e.printStackTrace();
            return "{id:\"minecraft:air\"}";
        }
    }
    public Object getCPD(final ItemStack item){
        try{
            Object ci = CraftItemStackClass.isInstance(item)?CraftItemStackClass.cast(item):asCraftCopy.invoke(null,item);//浅拷贝unhandledTags
            Object ei = asNMSCopy.invoke(null, ci);
            return NMSItemStack_save.invoke(ei, NBTTagCompoundClass.newInstance());
        }catch (ReflectiveOperationException e){
            e.printStackTrace();
            return null;
        }
    }
    public Object getCPD(final String nbt){
        try{
            return stringToCPD.invoke(null,nbt);
        }catch (ReflectiveOperationException e){
            e.printStackTrace();
            return null;
        }
    }
    /**
     *  仅1.13+可用
     */
    public ItemStack getItemStack(final Object cpd){
        try{
            Object ei = cpdToEi.invoke(null,cpd);
            Object ci = asBukkitCopy.invoke(null,ei);
            return (ItemStack) ci;
        }catch (ReflectiveOperationException e){
            e.printStackTrace();
            return null;
        }
    }
    /**
     *  仅1.13+可用
     */
    public ItemStack getItemStack(final String nbt){
        try{
            Object cpd = stringToCPD.invoke(null,nbt);
            Object ei = cpdToEi.invoke(null,cpd);
            Object ci = asBukkitCopy.invoke(null,ei);
            return (ItemStack) ci;
        }catch (ReflectiveOperationException e){
            e.printStackTrace();
            return null;
        }
    }
    public void setEnvironment(final World world,final World.Environment env){
        Object world_c = getCraftWorld(world);
        try{
            //Object old_env = world_c_environment.get(world_c);
            world_c_environment.set(world_c,env);
        }catch (ReflectiveOperationException e){
            e.printStackTrace();
        }
    }

    public Object getWorldServer(final World ThisWorld) {
        try {
            Object castClass = getCraftWorld(ThisWorld);
            return this.CraftWorldClass.getDeclaredMethod("getHandle").invoke(castClass);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Object getCraftWorld(final World ThisWorld) {
        if (this.CraftWorldClass.isInstance(ThisWorld)) return this.CraftWorldClass.cast(ThisWorld);
        return null;
    }
    public Object getWorld_e(final World world){
        try {
            Object world_c = getCraftWorld(world);
            return this.getNMSWord.invoke(world_c);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Class<?> getNBTBaseClass(){
        return this.NBTBaseClass;
    }
    public Class<?> getNBTTagCompoundClass(){
        return this.NBTTagCompoundClass;
    }
    public Class<?> getWorldClass(){
        return this.WorldClass;
    }
    public Class<?> getWorldProviderClass(){
        return this.WorldProviderClass;
    }
    private String getMethodName(final ReflectiveMethod method){
        switch (method){
            case NMSItemStack_save: {
                if(Version.mcMainVersion<=17) return "save";
                return "b";
            }
            case NMSItemStack_setTag: {
                if(Version.mcMainVersion<=17) return "setTag";
                return "c";
            }
            case MojangsonParser_parse: {
                if(Version.mcMainVersion<=17) return "parse";
                return "a";
            }
            case MojangsonParser_parseLiteral: {
                if(Version.mcMainVersion==12) return "c";
                else if("v1_13_R1".equals(Version.getVersion())) return "b";
                else if(Version.mcMainVersion<=17) return "parseLiteral";
                else return "b";
            }
            case NBTTagCompound_set: {
                if(Version.mcMainVersion<=17) return "set";
                return "a";
            }
            default: return "unreachable";
        }
    }
    private void init_NBTTagCompoundClass(){
        try{
            Class<?> CraftMetaBlockStateClass = Class.forName("org.bukkit.craftbukkit."+Version.getVersion()+".inventory.CraftMetaBlockState");
            Field field = CraftMetaBlockStateClass.getDeclaredField("blockEntityTag");
            this.NBTTagCompoundClass = field.getType();
            return;
        }catch (ReflectiveOperationException ignored){}

        Method[] methods = CraftEntityClass.getDeclaredMethods();
        for (Method method : methods) {
            if ("save".equals(method.getName())) this.NBTTagCompoundClass = method.getReturnType();
        }
    }
    private void init_NBTTagCompound_map(){
        Field[] fields = NBTTagCompoundClass.getDeclaredFields();
        for(Field field : fields){
            if(field.getType() == this.unhandledTags.getType()){
                this.NBTTagCompound_map = field;
                break;
            }
        }
        this.NBTTagCompound_map.setAccessible(true);
    }
    private void init_NBTBaseClass(){
        try{
            //在mohist端使用getPackage()喜提null
            this.NBTBaseClass = Class.forName(this.NBTTagCompoundClass.getName().replace(".NBTTagCompound",".NBTBase"));
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }
    private static enum ReflectiveMethod {
        NMSItemStack_save,
        NMSItemStack_setTag,
        MojangsonParser_parse,
        MojangsonParser_parseLiteral,
        NBTTagCompound_set
    }

}
