package pers.xanadu.enderdragon.script.tool

import org.bukkit.Bukkit
import org.bukkit.command.CommandExecutor
import org.bukkit.command.PluginCommand
import org.bukkit.command.TabCompleter
import org.bukkit.plugin.SimplePluginManager
import pers.xanadu.enderdragon.manager.GroovyManager
import pers.xanadu.enderdragon.util.Version

import static pers.xanadu.enderdragon.EnderDragon.plugin
import static pers.xanadu.enderdragon.EnderDragon.pm

class ScriptCommand{
    private PluginCommand command
    private String namespaceKey
    ScriptCommand(String name){
        command = new PluginCommand(name,plugin)
        namespaceKey = name
    }
    ScriptCommand setAliases(List<String> aliases){
        command.setAliases(aliases)
        return this
    }
    ScriptCommand setExecutor(CommandExecutor executor){
        command.setExecutor(executor)
        return this
    }
    ScriptCommand setTabCompleter(TabCompleter tabCompleter){
        command.setTabCompleter(tabCompleter)
        return this
    }
    ScriptCommand setPermission(String permission){
        command.setPermission(permission)
        return this
    }
    ScriptCommand setPermissionMessage(String message){
        command.setPermissionMessage(message)
        return this
    }
    ScriptCommand setNamespace(String namespace){
        this.namespaceKey = namespace
        return this
    }
    ScriptCommand setDescription(String description){
        command.setDescription(description)
        return this
    }
    ScriptCommand setUsage(String usage){
        command.setUsage(usage)
        return this
    }
    void register(){
        if(Bukkit.isPrimaryThread()){
            (pm as SimplePluginManager).commandMap.register(namespaceKey,command)
            if(Version.mcMainVersion >= 13) Bukkit.getServer().metaClass.invokeMethod(Bukkit.getServer(),"syncCommands")
            //Bukkit.getOnlinePlayers().forEach(Player::updateCommands)
        }
        else{
            Bukkit.getScheduler().runTask(plugin,() -> {
                (pm as SimplePluginManager).commandMap.register(namespaceKey,command)
                if(Version.mcMainVersion >= 13) Bukkit.getServer().metaClass.invokeMethod(Bukkit.getServer(),"syncCommands")
            })
        }
        GroovyManager.cmd_set.add(this)
    }
    void unregister(){
        if(Bukkit.isPrimaryThread()){
            def knownCommands = (pm as SimplePluginManager).commandMap.knownCommands
            knownCommands.remove(command.name)
            knownCommands.remove("${namespaceKey}:${command.name}")
            if(Version.mcMainVersion >= 13) Bukkit.getServer().metaClass.invokeMethod(Bukkit.getServer(),"syncCommands")

        }
        else{
            Bukkit.getScheduler().runTask(plugin,() -> {
                def knownCommands = (pm as SimplePluginManager).commandMap.knownCommands
                knownCommands.remove(command.name)
                knownCommands.remove("${namespaceKey}:${command.name}")
                if(Version.mcMainVersion >= 13) Bukkit.getServer().metaClass.invokeMethod(Bukkit.getServer(),"syncCommands")
            })
        }
    }
}