package pers.xanadu.enderdragon.util;

import org.bukkit.scheduler.BukkitRunnable;
import pers.xanadu.enderdragon.EnderDragon;
import pers.xanadu.enderdragon.config.Lang;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class MyUpdater {
    public static void checkUpdate(){
        Lang.info(Lang.plugin_checking_update);
        new BukkitRunnable(){
            @Override
            public void run(){
                try {
                    URLConnection conn = new URL("https://api.github.com/repos/iXanadu13/EnderDragon/releases/latest").openConnection();
                    conn.setConnectTimeout(20000);
                    conn.setReadTimeout(60000);
                    InputStream is = conn.getInputStream();
                    String line = new BufferedReader(new InputStreamReader(is)).readLine();
                    is.close();
                    String newVer = line.substring(line.indexOf("\"tag_name\"") + 13, line.indexOf("\"target_commitish\"") - 2);
                    String localVer = EnderDragon.plugin.getDescription().getVersion();
                    if (!localVer.equals(newVer)) {
                        Lang.warn(Lang.plugin_out_of_date.replace("{0}",localVer).replace("{1}",newVer));
                    }
                    else{
                        Lang.info(Lang.plugin_up_to_date.replace("{1}",newVer));
                    }
                } catch (Exception e) {
                    Lang.warn(Lang.plugin_check_update_fail);
                }
            }
        }.runTaskAsynchronously(EnderDragon.plugin);
    }
}
