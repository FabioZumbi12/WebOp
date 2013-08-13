package me.jayfella.webop2.Profiler;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.AuthorNagException;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.TimedRegisteredListener;

public class WebOpPluginManager extends WebOpSimplePluginManager implements Closeable
{
    private volatile boolean isProfiling = false;

    private Map<Class<? extends Event>, Long> eventRunCount = new HashMap<>();
    private Map<Class<? extends Event>, Long> eventRunDuration = new HashMap<>();

    private Map<RegisteredListener, Long> listenerRunCount = new HashMap<>();
    private Map<RegisteredListener, Long> listenerRunDuration = new HashMap<>();

    public WebOpPluginManager(Server instance, SimpleCommandMap commandMap)
    {
        super(instance, commandMap);
    }

    public Map<Class<? extends Event>, Long> getEventDuration() { return this.eventRunDuration; }
    public Map<Class<? extends Event>, Long> getEventRunCount() { return this.eventRunCount; }

    public Map<RegisteredListener, Long> getRegisteredListenerRunCount() { return this.listenerRunCount; }
    public Map<RegisteredListener, Long> getRegisteredListenerDuration() { return this.listenerRunDuration; }


    public void clearProfilingResults()
    {
        this.eventRunCount = new HashMap<>();
        this.eventRunDuration = new HashMap<>();

        this.listenerRunCount = new HashMap<>();
        this.listenerRunDuration = new HashMap<>();

    }

    public void startProfiling(final long msDuration)
    {
        Logger.getLogger(WebOpPluginManager.class.getName()).log(Level.INFO, "[WebOp] Profiling started.");

        this.clearProfilingResults();
        this.isProfiling = true;
    }

    public void stopProfiling()
    {
        Logger.getLogger(WebOpPluginManager.class.getName()).log(Level.INFO, "[WebOp] Profiling stopped.");
        this.isProfiling = false;
    }

    @Override
    public void registerEvent(Class<? extends Event> event, Listener listener, EventPriority priority, EventExecutor executor, Plugin plugin, boolean ignoreCancelled)
    {
        if (!plugin.isEnabled())
        {
            throw new IllegalPluginAccessException("Plugin attempted to register " + event + " while not enabled");
        }

        RegisteredListener rl = (this.useTimings)
                ? new TimedRegisteredListener(listener, executor, priority, plugin, ignoreCancelled)
                : new RegisteredListener(listener, executor, priority, plugin, ignoreCancelled);

        getEventListeners(event).register(rl);
    }



    @Override
    public void registerEvents(Listener listener, Plugin plugin)
    {
        if (!plugin.isEnabled())
        {
            throw new IllegalPluginAccessException("Plugin attempted to register " + listener + " while not enabled");
        }

        for (Map.Entry<Class<? extends Event>, Set<RegisteredListener>> entry : plugin.getPluginLoader().createRegisteredListeners(listener, plugin).entrySet())
        {
            Class<? extends Event> delegatedClass = getRegistrationClass(entry.getKey());

            getEventListeners(delegatedClass).registerAll(entry.getValue());
        }

    }

    public synchronized void fullyDisablePlugin(Plugin plugin)
    {
        if (plugin == null)
        {
            return;
        }

        this.lookupNames.remove(plugin.getDescription().getName());
        disablePlugin(plugin);
    }

    public synchronized void disableAllPlugins()
    {
        Bukkit.getLogger().log(Level.INFO, "Disabling all plugins...");

        Plugin[] list = this.getPlugins().clone();

        for (Plugin pl : list)
        {
            this.fullyDisablePlugin(pl);
        }
    }

    @Override
    public synchronized void callEvent(Event event)
    {
        HandlerList handlers = event.getHandlers();
        RegisteredListener[] listeners = handlers.getRegisteredListeners();

        long eventStartTime = System.nanoTime();

        for (RegisteredListener registration : listeners)
        {
            if (registration.getPlugin().isEnabled())
            {
                Long listenerBeginTime = System.nanoTime();

                try
                {
                    registration.callEvent(event);
                }
                catch (AuthorNagException ex)
                {
                    Plugin plugin = registration.getPlugin();

                    if (plugin.isNaggable())
                    {
                        plugin.setNaggable(false);

                        String author = "<NoAuthorGiven>";

                        if (plugin.getDescription().getAuthors().size() > 0)
                        {
                            author = (String)plugin.getDescription().getAuthors().get(0);
                        }

                        Bukkit.getLogger().log(Level.SEVERE, String.format("Nag author: '{0}' of '{1}' about the following: {2}", new Object[] { author, plugin.getDescription().getName(), ex.getMessage() }));
                    }

                    Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                }
                catch (Throwable ex)
                {
                    Bukkit.getLogger().log(Level.SEVERE, "Could not pass event " + event.getEventName() + " to " + registration.getPlugin().getDescription().getName(), ex);
                }
                finally
                {
                    if (isProfiling)
                    {
                        Long listenerEndTime = System.nanoTime(); // this.listenerTimeRunning.get(registration);
                        long duration = listenerEndTime - listenerBeginTime;

                        if (duration > 0)
                        {
                            Long timesListenerCalled = this.listenerRunCount.get(registration);

                            if (timesListenerCalled == null)
                                timesListenerCalled = 1L;

                            this.listenerRunCount.put(registration, timesListenerCalled);

                            Long currentDuration = this.listenerRunDuration.get(registration);

                            if (currentDuration == null)
                                currentDuration = 0L;

                            currentDuration += duration;

                            long newDuration = currentDuration / timesListenerCalled;

                            this.listenerRunDuration.put(registration, newDuration);
                        }
                    }
                }
            }
        }

        // ignore events that dont take any time to complete
        // TODO : implement a possible > than timescale filter
        if (isProfiling)
        {
            long eventEndTime = System.nanoTime();
            long duration = eventEndTime - eventStartTime;

            // if (duration > 10000)
            {
                Long timesEventCalled = this.eventRunCount.get(event.getClass());

                timesEventCalled = (timesEventCalled == null)
                        ? 1L
                        : timesEventCalled + 1L;

                this.eventRunCount.put(event.getClass(), timesEventCalled);

                Long currentDuration = this.eventRunDuration.get(event.getClass());
                Long currentRunCount = this.eventRunCount.get(event.getClass());

                if (currentDuration == null)
                    currentDuration = 0L;

                currentDuration += duration;

                long newDuration = currentDuration / currentRunCount;

                this.eventRunDuration.put(event.getClass(), newDuration);
            }
        }
    }

    @Override
    public void close()
    {
        this.disableAllPlugins();
    }
}