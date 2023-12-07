package pers.xanadu.enderdragon.util;

import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pers.xanadu.enderdragon.util.Version.mcMainVersion;

public class ColorUtil {

    public static Pattern hex_code = Pattern.compile("&#([0-9A-Fa-f]{6})");
    public static Pattern gradient = Pattern.compile("&\\[#([0-9a-fA-F]{6})-#([0-9a-fA-F]{6})(.+)]");
    public static String parse(String str){
        if(mcMainVersion >= 16){
            return org.bukkit.ChatColor.translateAlternateColorCodes('&',transGradient(str));
        }
        else{
            return org.bukkit.ChatColor.translateAlternateColorCodes('&',str);
        }
    }
    private static String translateHexCodes(String str) {
        Matcher matcher = hex_code.matcher(str);
        StringBuffer buffer = new StringBuffer();
        while(matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.of("#" + matcher.group(1)).toString());
        }
        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }
    private static String transGradient(String str) {
        if (str.contains("&[#")&&str.contains("]")) {
            Matcher matcher = gradient.matcher(str);
            StringBuffer buffer = new StringBuffer();
            while (matcher.find()) {
                String text = matcher.group(3);
                int[] colors = gradient(matcher.group(1),matcher.group(2),text.length());
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < text.length(); i++) {
                    Color c = new Color(colors[i]);
                    builder.append(ChatColor.of(c)).append(text.charAt(i));
                }
                matcher.appendReplacement(buffer, builder.toString());
            }
            matcher.appendTail(buffer);
            return buffer.toString();
        }
        return translateHexCodes(str);
    }
    private static int[] gradient(String start,String end,int length){
        int[] color = new int[length];
        int[] rgb1 = MathUtil.hex2Int(start);
        int[] rgb2 = MathUtil.hex2Int(end);
        if(length==0){return color;}
        if(length==1){
            for(int j=0;j<3;j++){
                color[0] = color[0]*256+rgb1[j];
            }
            return color;
        }
        for(int i=0;i<length;i++){
            for(int j=0;j<3;j++){
                color[i] = color[i]*256+rgb1[j]+(rgb2[j]-rgb1[j])*i/(length-1);
            }
        }
        return color;
    }

}
