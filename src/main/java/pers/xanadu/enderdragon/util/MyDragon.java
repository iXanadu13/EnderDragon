package pers.xanadu.enderdragon.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import pers.xanadu.enderdragon.reward.Reward;
import pers.xanadu.enderdragon.reward.RewardDist;

import java.util.ArrayList;
import java.util.List;

public class MyDragon implements Comparable<MyDragon>{
    public ItemStack icon;
    public String unique_name;
    public String display_name;
    public String drop_gui;
    public int edge;
    public int priority;
    public double spawn_chance;
    public int max_health;
    public int spawn_health;
    public int exp_drop;
    public int dragon_egg_spawn_delay;
    public int dragon_egg_spawn_x;
    public int dragon_egg_spawn_y;
    public int dragon_egg_spawn_z;
    public double dragon_egg_spawn_chance;
    public double attack_damage_modify;
    //public double move_speed_modify;
    public double armor_modify;
    public double armor_toughness_modify;
    public double crystal_heal_speed;
    public boolean suck_blood_enable;
    public double suck_blood_rate;
    public double suck_blood_base_amount;
    public boolean suck_blood_only_player;
    public List<PotionEffect> attack_potion_effect;
    public List<String> spawn_cmd;
    public List<String> death_cmd;
    public List<String> spawn_broadcast_msg;
    public List<String> death_broadcast_msg;
    public List<String> msg_to_killer;
    public String glow_color;
    public String bossbar_color;
    public String bossbar_style;
    public double effect_cloud_original_radius;
    public double effect_cloud_expand_speed;
    public int effect_cloud_duration;
    public int effect_cloud_color_R;
    public int effect_cloud_color_G;
    public int effect_cloud_color_B;
    public List<PotionEffect> effect_cloud_potion;
    public List<Reward> datum = new ArrayList<>();
    public RewardDist reward_dist;

    @Override
    public int compareTo(MyDragon o) {
        return o.priority - this.priority;//降序
    }

}
