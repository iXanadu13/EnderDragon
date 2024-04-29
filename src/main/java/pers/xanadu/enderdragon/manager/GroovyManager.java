package pers.xanadu.enderdragon.manager;

import groovy.lang.*;
import groovy.util.GroovyScriptEngine;
import io.netty.util.internal.ConcurrentSet;
import org.bukkit.entity.Player;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import pers.xanadu.enderdragon.EnderDragon;
import pers.xanadu.enderdragon.config.Config;
import pers.xanadu.enderdragon.script.Events;
import pers.xanadu.enderdragon.script.tool.ScriptCommand;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

import static pers.xanadu.enderdragon.EnderDragon.plugin;
import static pers.xanadu.enderdragon.config.Lang.error;
import static pers.xanadu.enderdragon.config.Lang.info;

public class GroovyManager {
    private static final File baseDir = new File(plugin.getDataFolder(),"expansion/groovy");
    private static final ConcurrentHashMap<String, Script> script_mp = new ConcurrentHashMap<>();
    public static final ConcurrentSet<Events<?>> event_set = new ConcurrentSet<>();
    public static final ConcurrentSet<ScriptCommand> cmd_set = new ConcurrentSet<>();
    private static final GroovyScriptEngine engine;
    private static final CompilerConfiguration compilerConfig;
    private static final String lib;

    static {
        try {
            compilerConfig = new CompilerConfiguration();
            ImportCustomizer importCustomizer = new ImportCustomizer();
            importCustomizer.addImport("Bukkit","org.bukkit.Bukkit");
            importCustomizer.addImport("Player","org.bukkit.entity.Player");
            importCustomizer.addImport("Events","pers.xanadu.enderdragon.script.Events");
            importCustomizer.addImport("Command","pers.xanadu.enderdragon.script.tool.ScriptCommand");
            importCustomizer.addStaticImport("plugin","pers.xanadu.enderdragon.EnderDragon","plugin");
            compilerConfig.addCompilationCustomizers(importCustomizer);
            GroovyClassLoader classLoader = new GroovyClassLoader(EnderDragon.class.getClassLoader(),compilerConfig);
            engine = new GroovyScriptEngine(new URL[]{baseDir.toURI().toURL()}, classLoader);
            InputStream inputStream = plugin.getResource("script/lib.groovy");
            lib = Config.convertInputStreamToString(inputStream);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void reload() {
        event_set.forEach(Events::unregister);
        event_set.clear();
        cmd_set.forEach(ScriptCommand::unregister);
        cmd_set.clear();
        script_mp.clear();
        loadAllScripts(baseDir);
        script_mp.forEach((key,script)->invoke(key,"enable"));
    }
    public static Object eval(String expression, Player player){
        Binding binding = new Binding();
        binding.setVariable("player",player);
        binding.setVariable("itemStack",player.getItemInHand());
        binding.setVariable("world",player.getWorld());
        GroovyShell shell = new GroovyShell(GroovyManager.class.getClassLoader(),binding,compilerConfig);
        return shell.evaluate(lib+expression);
    }

    public static Object invoke(String fileName, String function, Object... args) {
        Script script = GroovyManager.script_mp.get(fileName);
        if (script != null) {
            try {
                return script.invokeMethod(function, args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static void loadAllScripts(File folder) {
        if (folder.isDirectory()) {
            File[] subFiles = folder.listFiles();
            if(subFiles == null) return;
            for (File file : subFiles) {
                if(file.isDirectory()) loadAllScripts(file);
                else if(file.getName().endsWith(".groovy")) loadGroovyFile(file);
            }
        }
    }

    private static void loadGroovyFile(File file) {
        String key = file.getPath().substring(37);
        try {
            Script script = engine.createScript(key, new Binding());
            //script.evaluate("import org.bukkit.Bukkit");
            script_mp.put(key, script);
            info("Successfully load script: "+key);
        } catch (Exception e) {
            error("Failed to load script: "+key);
            e.printStackTrace();
        }
    }
}
