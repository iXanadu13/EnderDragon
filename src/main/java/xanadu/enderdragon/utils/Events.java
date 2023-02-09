package xanadu.enderdragon.utils;

import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.plugin.EventExecutor;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import static xanadu.enderdragon.EnderDragon.plugin;
import static xanadu.enderdragon.EnderDragon.pm;

public class Events<T extends Event> implements Listener, EventExecutor {
    private final Consumer<T> consumer;
    private final Class<T> clazz;
    private boolean G;
    private final AtomicLong atomicLong;

    public Events<T> withTime(final long a) {
        this.atomicLong.set(a);
        return this;
    }

    public void unregister() {
        if (this.G) return;
        try {
            HandlerList list = (HandlerList)this.clazz.getMethod("getHandlerList").invoke(null, new Object[0]);
            if (Bukkit.isPrimaryThread()) list.unregister(this);
            else Bukkit.getScheduler().runTask(plugin, () -> list.unregister(this));
        }
        catch (final Exception ignored) {}
        this.G = true;
    }

    public static <T extends Event> Events<T> subscribe(Class<T> a, EventPriority a2, boolean a3, Consumer<T> a4) {
        if (Bukkit.isPrimaryThread()) {
            pm.registerEvent(a, (Listener) a4, a2, (EventExecutor) a4, plugin, a3);
            return (Events<T>) a4;
        }
        Bukkit.getScheduler().runTask(plugin,()->pm.registerEvent(a, (Listener)a4, a2, (EventExecutor)a4, plugin, a3));
        return (Events<T>) a4;
    }

    public static <T extends Event> Events<T> subscribe(Class<T> clazz, Consumer<T> consumer) {
        return Events.subscribe(clazz, EventPriority.HIGHEST, true, consumer);
    }

    public Events(Class<T> clazz, Consumer<T> consumer) {
        this.atomicLong = new AtomicLong(-1L);
        this.G = false;
        this.consumer = consumer;
        this.clazz = clazz;
    }

    public void execute(Listener listener, Event event) {
        if (listener == null) iIIiii(0);
        if (event == null) iIIiii(1);
        if (this.atomicLong.get() > 0L && System.currentTimeMillis() > this.atomicLong.get()) {
            Bukkit.getScheduler().runTask(plugin, this::unregister);
            return;
        }
        if (!this.clazz.isInstance(event)) return;
        this.consumer.accept((T) event);
    }

    public boolean releaseIfExpired() {
        if (this.atomicLong.get() > 0L && System.currentTimeMillis() > this.atomicLong.get()) {
            boolean b = true;
            this.unregister();
            return b;
        }
        return false;
    }

    private static /* synthetic */ void iIIiii(final int a) {
        final String format = "Argument for @NotNull parameter '%s' of %s.%s must not be null";
        final Object[] args = new Object[3];
        if (a == 1) args[0] = "event";
        else args[0] = "listener";
        args[1] = "xanadu/enderdragon/utils/Events";
        args[2] = "execute";
        throw new IllegalArgumentException(String.format(format, args));
    }
}
