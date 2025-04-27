package pers.xanadu.enderdragon.manager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import pers.xanadu.enderdragon.util.Version;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class GlowManager {
    public static Set<Scoreboard> handled = new HashSet<>();
    private static final ChatColor[] colors;
    public static void reload(){
        handled.clear();
    }
    public static void setScoreBoard(Player p){
        Scoreboard board = p.getScoreboard();
        if(handled.contains(board)) return;
        for(ChatColor color : colors){
            String name = "ed-"+color.name();
            if(board.getTeam(name)==null) board.registerNewTeam(name);
            Team team = board.getTeam(name);
            assert team != null;
            if(Version.mcMainVersion >= 13) team.setColor(color);
            else team.setPrefix(color.toString());
        }
        handled.add(board);
    }
    public static void addUUID(String uuid,String color){
        Set<Team> teams = new HashSet<>();
        Bukkit.getOnlinePlayers().forEach(player -> {
            GlowManager.setScoreBoard(player);
            Team team = player.getScoreboard().getTeam("ed-"+color.toUpperCase());
            teams.add(team);
        });
        teams.forEach(team->{
            team.addEntry(uuid);
        });
    }
    public static void setGlowingColor(Entity entity, ChatColor color){
        Set<Team> teams = new HashSet<>();
        Bukkit.getOnlinePlayers().forEach(player -> {
            GlowManager.setScoreBoard(player);
            Team team = player.getScoreboard().getTeam("ed-"+color.name());
            teams.add(team);
        });
        teams.forEach(team->{
            team.addEntry(entity.getUniqueId().toString());
            entity.setGlowing(true);
        });
    }
    public static ChatColor getGlowColor(String str){
        ChatColor chatColor;
        if(str.equals("RANDOM")) chatColor = randomColor();
        else chatColor = ChatColor.valueOf(str);
        return chatColor;
    }
    public static ChatColor getGlowColor(GlowColor glowColor){
        if(glowColor == GlowColor.NONE) return null;
        if(glowColor == GlowColor.RANDOM) return randomColor();
        return ChatColor.valueOf(glowColor.name());
    }
    public static ChatColor randomColor(){
        return ChatColor.values()[ThreadLocalRandom.current().nextInt(16)];
    }
    public enum GlowColor{
        AQUA,BLACK,BLUE,DARK_AQUA,DARK_BLUE,DARK_GRAY,DARK_GREEN,DARK_PURPLE,DARK_RED,GOLD,GRAY,GREEN,LIGHT_PURPLE,RED,WHITE,YELLOW,
        NONE,RANDOM
    }
    static {
         colors = Arrays.stream(ChatColor.values()).limit(16).toArray(ChatColor[]::new);
    }
}
