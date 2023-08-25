package pers.xanadu.enderdragon.script;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;
import pers.xanadu.enderdragon.manager.GroovyManager;

import static pers.xanadu.enderdragon.EnderDragon.plugin;

public class Events<T extends Event> implements Listener, EventExecutor {
    private final Consumer<T> consumer;
    private final Class<T> clazz;
    private boolean disabled;
    private final AtomicLong expireTime;
    public Events(Class<T> clazz, Consumer<T> consumer) {
        this.clazz = clazz;
        this.consumer = consumer;
        this.expireTime = new AtomicLong(-1L);
        this.disabled = false;
    }

    public void unregister() {
        if (!this.disabled) {
            try {
                Method method = this.clazz.getMethod("getHandlerList");
                HandlerList handlerList = (HandlerList) method.invoke(null);
                if (Bukkit.isPrimaryThread()) {
                    handlerList.unregister(this);
                }
                else {
                    Bukkit.getScheduler().runTask(plugin, () -> handlerList.unregister(this));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.disabled = true;
        }
    }
    public static <T extends Event> Events<T> registerPersistently(Class<T> clazz, Consumer<T> consumer) {
        return Events.registerPersistently(clazz, EventPriority.NORMAL, false, consumer);
    }
    public static <T extends Event> Events<T> registerPersistently(Class<T> clazz, EventPriority priority, boolean ignoreCancelled, Consumer<T> consumer) {
        Events<T> tmp = new Events<>(clazz, consumer);
        if (Bukkit.isPrimaryThread()) {
            Bukkit.getPluginManager().registerEvent(clazz,tmp,priority,tmp,plugin,ignoreCancelled);
        }
        else {
            Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getPluginManager().registerEvent(clazz,tmp,priority,tmp,plugin,ignoreCancelled));
        }
        return tmp;
    }
    public static <T extends Event> Events<T> register(Class<T> clazz, Consumer<T> consumer) {
        return Events.register(clazz, EventPriority.NORMAL, false, consumer);
    }
    public static <T extends Event> Events<T> register(Class<T> clazz, EventPriority priority, boolean ignoreCancelled, Consumer<T> consumer) {
        Events<T> tmp = new Events<>(clazz, consumer);
        if (Bukkit.isPrimaryThread()) {
            Bukkit.getPluginManager().registerEvent(clazz,tmp,priority,tmp,plugin,ignoreCancelled);
        }
        else {
            Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getPluginManager().registerEvent(clazz,tmp,priority,tmp,plugin,ignoreCancelled));
        }
        GroovyManager.event_set.add(tmp);
        return tmp;
    }
//    private static <T extends Event> void register(Class<T> clazz, Events<T> event, EventPriority priority, boolean ignoreCancelled) {
//        Bukkit.getPluginManager().registerEvent(clazz,event,priority,event,plugin,ignoreCancelled);
//    }

    @Override
    public void execute(@NotNull Listener listener, @NotNull Event event) {
        if (this.expireTime.get() > 0L && System.currentTimeMillis() > this.expireTime.get()) {
            Bukkit.getScheduler().runTask(plugin, this::unregister);
            return;
        }
        if (this.clazz.isInstance(event)) {
            this.consumer.accept((T) event);
        }
    }
    public Events<T> withTime(long time) {
        this.expireTime.set(time);
        return this;
    }

    public boolean cancelIfExpired() {
        if (this.expireTime.get() > 0L && System.currentTimeMillis() > this.expireTime.get()) {
            this.unregister();
            return true;
        }
        return false;
    }

}

