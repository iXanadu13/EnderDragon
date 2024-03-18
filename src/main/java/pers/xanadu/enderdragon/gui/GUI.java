package pers.xanadu.enderdragon.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pers.xanadu.enderdragon.config.Config;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.gui.slot.DragonSlot;
import pers.xanadu.enderdragon.manager.DragonManager;
import pers.xanadu.enderdragon.manager.ItemManager;
import pers.xanadu.enderdragon.gui.slot.EmptySlot;
import pers.xanadu.enderdragon.gui.slot.ItemSlot;
import pers.xanadu.enderdragon.manager.RewardManager;
import pers.xanadu.enderdragon.reward.Reward;
import pers.xanadu.enderdragon.metadata.MyDragon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class GUI {
    protected String title;
    protected int maxPage;
    protected int page;
    protected int size;
    protected ArrayList<ItemStack[]> pagedItems;
    protected ArrayList<String[]> pagedData;
    protected List<GUISlot> slots;
    protected Inventory inv;
    protected HashSet<Integer> dynamics;

    protected GUI(String str,int size,int maxPage){
        this.slots = new ArrayList<>();
        this.pagedItems = new ArrayList<>();
        this.pagedData = new ArrayList<>();
        this.dynamics = new HashSet<>();
        this.title = str;
        this.size = size;
        this.maxPage = maxPage;
        this.page = 0;
    }
    public GUISlot getSlot(int index){
        GUISlot slot = this.slots.get(index);
        if(slot == null) return new EmptySlot();
        return slot;
    }
    public void setPage(int a) {
        this.page = a;
        for(int i=0;i<this.slots.size();i++){
            GUISlot slot = this.slots.get(i);
            if (slot instanceof ItemSlot || slot instanceof DragonSlot) {
                this.inv.setItem(i, new ItemStack(Material.AIR));
            }
        }
        if (this.pagedItems.isEmpty()) {
            this.pagedItems.add(new ItemStack[this.size]);
        }
        if (this.pagedData.isEmpty()) {
            this.pagedData.add(new String[this.size]);
        }
        ItemStack[] array = this.pagedItems.get(a);
        for(int i=0 ; i<array.length ; i++){
            ItemStack itemStack = array[i];
            if (itemStack != null) {
                this.inv.setItem(i, itemStack);
            }
        }
        for(Integer n2 : this.dynamics){
            GUISlotType type = this.getSlot(n2).getType();
            ItemStack clone;
            if(type == GUISlotType.PAGE_PREV){
                if(a == 0) clone = this.slots.get(n2).getItemOnDisable();
                else clone = this.slots.get(n2).getItem();
                this.inv.setItem(n2,clone);
            }
            else if(type == GUISlotType.PAGE_NEXT){
                if(a == this.pagedItems.size()-1) clone = this.slots.get(n2).getItemOnDisable();
                else clone = this.slots.get(n2).getItem();
                this.inv.setItem(n2,clone);
            }
            else if(type == GUISlotType.PAGE_TIP){
                clone = this.slots.get(n2).getItem().clone();
                ItemMeta itemMeta = clone.getItemMeta();
                if (itemMeta != null) {
                    itemMeta.setDisplayName(String.format("§r%d/" + this.pagedItems.size(),a+1));
                    clone.setItemMeta(itemMeta);
                    this.inv.setItem(n2, clone);
                }
            }
//            else if(type == GUISlotType.PAGE_JUMP){
//                clone = this.slots.get(n2).getItem().clone();
//                this.inv.setItem(n2,clone);
//            }
        }
    }
    public Inventory current() {
        return this.inv;
    }
    public Inventory getInventory() {
        return this.inv;
    }
    protected void init() {
        for(int i=0;i<this.slots.size();i++){
            GUISlot slot = slots.get(i);
            inv.setItem(i, slot.getItem());
        }
    }
    public void resetPagedItem(ItemStack item){
        this.pagedItems.clear();
        this.pagedItems.add(new ItemStack[]{item});
    }
    public void resetPagedItem(int type, String key,boolean cmd){
        this.pagedItems.clear();
        this.pagedData.clear();
        if(type == 1){
            resetPagedItem(key,cmd);
        }
        else if(type == 2){
            for(MyDragon myDragon : DragonManager.get_dragons()){
                this.addDragon(myDragon.icon.clone(),myDragon.unique_name);
            }
        }

    }
    public void resetPagedItem(String key,boolean cmd){
        this.pagedItems.clear();
        List<Reward> rewards = RewardManager.getRewards(key);
        if(rewards == null) return;
        for(Reward reward : rewards){
            if(Config.debug) Bukkit.getLogger().info(reward.toString());
            //if(ItemManager.isEmpty(reward.getItem())) continue;
            ItemStack item = reward.getItem().clone();
            if("".equals(Lang.gui_item_lore)) Lang.gui_item_lore = "§6(chance: {drop_chance}%)§r";
            String lore = Lang.gui_item_lore.replaceAll("\\{drop_chance}",reward.getChance().getStr());
            if(cmd) {
                if("".equals(Lang.gui_item_cmd_lore)) Lang.gui_item_cmd_lore = "§6Shift+RightClick§f to remove§r";
                ItemManager.addLoreFront(item,Lang.gui_item_cmd_lore);
            }
            ItemManager.addLoreFront(item,lore);
            this.addItem(item);
        }
    }
    protected void addItem(ItemStack item) {
        for(int i = 0 ; i < this.maxPage ; i++){
            if (i >= this.pagedItems.size()) {
                this.pagedItems.add(new ItemStack[this.size]);
            }
            for(int j = 0 ; j < this.slots.size() ; j++){
                GUISlot slot = this.slots.get(j);
                if (slot instanceof ItemSlot) {
                    ItemStack itemStack = this.pagedItems.get(i)[j];
                    if (itemStack == null) {
                        this.pagedItems.get(i)[j] = item;
                        return;
                    }
                }
            }
        }
    }
    protected void addDragon(ItemStack item,String unique_name) {
        for(int i = 0 ; i < this.maxPage ; i++){
            if (i >= this.pagedItems.size()) {
                this.pagedItems.add(new ItemStack[this.size]);
                this.pagedData.add(new String[this.size]);
            }
            for(int j = 0 ; j < this.slots.size() ; j++){
                if (this.slots.get(j) instanceof DragonSlot) {
                    ItemStack itemStack = this.pagedItems.get(i)[j];
                    if (itemStack == null) {
                        //itemStack = item;(可以吗？)
                        this.pagedItems.get(i)[j] = item;
                        this.pagedData.get(i)[j] = unique_name;
                        return;
                    }
                }
            }
        }
    }
    public String getData(int page,int index){
        return this.pagedData.get(page)[index];
    }
    public int getSize() {
        return this.size;
    }
    public int getPage(){
        return this.page;
    }
    public void prev() {
        if (this.page > 0) {
            this.setPage(this.page - 1);
        }
    }

    public void addPage() {
        if (this.pagedItems.size() >= this.maxPage) {
            return;
        }
        this.pagedItems.add(new ItemStack[this.size]);
    }

    public void next() {
        if (this.page < this.pagedItems.size() - 1) {
            this.setPage(this.page + 1);
        }
    }
}
