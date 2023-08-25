//If you want to read Chinese version, have a look at the WIKI

//Set the package name. Nothing matters if you skip this
package expansion.groovy.Example

//Usually, you need to import classes like what you do in Java before using.
//However, the plugin has automatically imported some classes for you during script engine initialization,
//so some class names and variables can be used directly.
import org.bukkit.Bukkit  //For example, you can delete this without affecting script operation
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import pers.xanadu.enderdragon.script.Events  //The same goes for this import

/**
 * Function enable() will be called when a script is loaded.
 * If this function is missing, you will receive a warning in console.
 */
static void enable(){
    // To avoid confusing you, this listener is not registered by default.
    // If you want to test it, just delete the "//".
    // registerListener()
    // registerAnotherListener()
    Bukkit.getLogger().info("Example script has been reloaded!")  //send a message in console when this script is loaded
}

// An example of registering listener
// Plugin has simplified the method to register. Here are two ways to register.
// You don't need to worry about problems about unregister them, because plugin will do that.
/**
 * Events.register(Class clazz, Consumer consumer)
 * @param clazz -> The class name to listen for events
 * @param consumer -> Function to run when this event is listened
 */
static void registerListener(){
    Events.register(PlayerJoinEvent.class, e -> Bukkit.broadcastMessage(e.getPlayer().getName()+ " joined!"))
}
/**
 * Events.register(Class clazz, EventPriority priority, boolean ignoreCancelled, Consumer consumer)
 * @param clazz -> The class name to listen for events
 * @param priority -> priority of listener(default: EventPriority.NORMAL)
 * @param ignoreCancelled -> Define if the handler ignores a cancelled event(default: false)
 * @param consumer -> Function to run when this event is listened
 */
static def registerAnotherListener(){
    Events.register(PlayerQuitEvent.class, EventPriority.NORMAL,false,event->{
        Bukkit.broadcastMessage("Quit message is:" + event.getQuitMessage())
        Bukkit.broadcastMessage("Player's name is " + event.player.displayName)
    })
}
/**
 * Priorities available for selection:
 * EventPriority.LOWEST (Process first)
 * EventPriority.LOW
 * EventPriority.NORMAL
 * EventPriority.HIGH
 * EventPriority.HIGHEST
 * EventPriority.MONITOR (Process last)
 */

