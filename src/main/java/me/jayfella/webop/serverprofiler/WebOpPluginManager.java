// 
// Decompiled by Procyon v0.5.36
// 

package me.jayfella.webop.serverprofiler;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.*;

import java.io.Closeable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebOpPluginManager extends WebOpSimplePluginManager implements Closeable {
    private volatile boolean isProfiling;
    private Map<Class<? extends Event>, Long> eventRunCount;
    private Map<Class<? extends Event>, Long> eventRunDuration;
    private Map<RegisteredListener, Long> listenerRunCount;
    private Map<RegisteredListener, Long> listenerRunDuration;

    public WebOpPluginManager(final Server instance, final SimpleCommandMap commandMap) {
        super(instance, commandMap);
        this.isProfiling = false;
        this.eventRunCount = new HashMap<Class<? extends Event>, Long>();
        this.eventRunDuration = new HashMap<Class<? extends Event>, Long>();
        this.listenerRunCount = new HashMap<RegisteredListener, Long>();
        this.listenerRunDuration = new HashMap<RegisteredListener, Long>();
    }

    public Map<Class<? extends Event>, Long> getEventDuration() {
        return this.eventRunDuration;
    }

    public Map<Class<? extends Event>, Long> getEventRunCount() {
        return this.eventRunCount;
    }

    public Map<RegisteredListener, Long> getRegisteredListenerRunCount() {
        return this.listenerRunCount;
    }

    public Map<RegisteredListener, Long> getRegisteredListenerDuration() {
        return this.listenerRunDuration;
    }

    public void clearProfilingResults() {
        this.eventRunCount = new HashMap<Class<? extends Event>, Long>();
        this.eventRunDuration = new HashMap<Class<? extends Event>, Long>();
        this.listenerRunCount = new HashMap<RegisteredListener, Long>();
        this.listenerRunDuration = new HashMap<RegisteredListener, Long>();
    }

    public boolean isProfiling() {
        return this.isProfiling;
    }

    public void startProfiling(final long msDuration) {
        Logger.getLogger(WebOpPluginManager.class.getName()).log(Level.INFO, "[WebOp] Profiling started.");
        this.clearProfilingResults();
        this.isProfiling = true;
    }

    public void stopProfiling() {
        Logger.getLogger(WebOpPluginManager.class.getName()).log(Level.INFO, "[WebOp] Profiling stopped.");
        this.isProfiling = false;
    }

    @Override
    public void registerEvent(final Class<? extends Event> event, final Listener listener, final EventPriority priority, final EventExecutor executor, final Plugin plugin, final boolean ignoreCancelled) {
        if (!plugin.isEnabled()) {
            throw new IllegalPluginAccessException("Plugin attempted to register " + event + " while not enabled");
        }
        final RegisteredListener rl = this.useTimings ? new TimedRegisteredListener(listener, executor, priority, plugin, ignoreCancelled) : new RegisteredListener(listener, executor, priority, plugin, ignoreCancelled);
        this.getEventListeners(event).register(rl);
    }

    @Override
    public void registerEvents(final Listener listener, final Plugin plugin) {
        if (!plugin.isEnabled()) {
            throw new IllegalPluginAccessException("Plugin attempted to register " + listener + " while not enabled");
        }
        for (final Map.Entry<Class<? extends Event>, Set<RegisteredListener>> entry : plugin.getPluginLoader().createRegisteredListeners(listener, plugin).entrySet()) {
            final Class<? extends Event> delegatedClass = this.getRegistrationClass(entry.getKey());
            this.getEventListeners(delegatedClass).registerAll(entry.getValue());
        }
    }

    public synchronized void fullyDisablePlugin(final Plugin plugin) {
        if (plugin == null) {
            return;
        }
        this.lookupNames.remove(plugin.getDescription().getName());
        this.disablePlugin(plugin);
    }

    public synchronized void disableAllPlugins() {
        Bukkit.getLogger().log(Level.INFO, "Disabling all plugins...");
        final Plugin[] arr$;
        final Plugin[] list = arr$ = this.getPlugins().clone();
        for (final Plugin pl : arr$) {
            this.fullyDisablePlugin(pl);
        }
    }

    @Override
    public synchronized void callEvent(final Event event) {
        final HandlerList handlers = event.getHandlers();
        final RegisteredListener[] listeners = handlers.getRegisteredListeners();
        final long eventStartTime = System.nanoTime();
        for (final RegisteredListener registration : listeners) {
            if (registration.getPlugin().isEnabled()) {
                final Long listenerBeginTime = System.nanoTime();
                try {
                    registration.callEvent(event);
                } catch (AuthorNagException ex) {
                    final Plugin plugin = registration.getPlugin();
                    if (plugin.isNaggable()) {
                        plugin.setNaggable(false);
                        String author = "<NoAuthorGiven>";
                        if (plugin.getDescription().getAuthors().size() > 0) {
                            author = plugin.getDescription().getAuthors().get(0);
                        }
                        Bukkit.getLogger().log(Level.SEVERE, String.format("Nag author: '{0}' of '{1}' about the following: {2}", author, plugin.getDescription().getName(), ex.getMessage()));
                    }
                    Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                } catch (Throwable ex2) {
                    Bukkit.getLogger().log(Level.SEVERE, "Could not pass event " + event.getEventName() + " to " + registration.getPlugin().getDescription().getName(), ex2);
                } finally {
                    if (this.isProfiling) {
                        final Long listenerEndTime = System.nanoTime();
                        final long duration = listenerEndTime - listenerBeginTime;
                        Long timesListenerCalled = this.listenerRunCount.get(registration);
                        if (timesListenerCalled == null) {
                            timesListenerCalled = 1L;
                        }
                        this.listenerRunCount.put(registration, timesListenerCalled);
                        Long currentDuration = this.listenerRunDuration.get(registration);
                        if (currentDuration == null) {
                            currentDuration = 0L;
                        }
                        currentDuration += duration;
                        final long newDuration = currentDuration / timesListenerCalled;
                        this.listenerRunDuration.put(registration, newDuration);
                    }
                }
            }
        }
        if (this.isProfiling) {
            final long eventEndTime = System.nanoTime();
            final long duration2 = eventEndTime - eventStartTime;
            if (duration2 > 100000L) {
                Long timesEventCalled = this.eventRunCount.get(event.getClass());
                timesEventCalled = ((timesEventCalled == null) ? 1L : (timesEventCalled + 1L));
                this.eventRunCount.put(event.getClass(), timesEventCalled);
                Long currentDuration2 = this.eventRunDuration.get(event.getClass());
                final Long currentRunCount = this.eventRunCount.get(event.getClass());
                if (currentDuration2 == null) {
                    currentDuration2 = 0L;
                }
                currentDuration2 += duration2;
                final long newDuration2 = currentDuration2 / currentRunCount;
                this.eventRunDuration.put(event.getClass(), newDuration2);
            }
        }
    }

    @Override
    public void close() {
        this.disableAllPlugins();
    }
}
