package me.jayfella.webop2.Profiler;

import com.google.common.collect.ImmutableSet;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.AuthorNagException;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.TimedRegisteredListener;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.util.FileUtil;

public class WebOpSimplePluginManager implements PluginManager
{
    public Server server;
    public boolean useTimings = false;
    public static File updateDirectory = null;
    public SimpleCommandMap commandMap;

    public List<Plugin> plugins = new ArrayList<>();
    public Map<Pattern, PluginLoader> fileAssociations = new HashMap<>();
    public Map<String, Plugin> lookupNames = new HashMap<>();
    public Map<String, Permission> permissions = new HashMap<>();
    public Map<Boolean, Set<Permission>> defaultPerms = new LinkedHashMap<>();
    public Map<String, Map<Permissible, Boolean>> permSubs = new HashMap<>();
    public Map<Boolean, Map<Permissible, Boolean>> defSubs = new HashMap<>();

    public WebOpSimplePluginManager(Server instance, SimpleCommandMap commandMap)
    {
        this.server = instance;
        this.commandMap = commandMap;

        this.defaultPerms.put(true, new HashSet<Permission>());
        this.defaultPerms.put(false, new HashSet<Permission>());
    }

    @Override
    public void registerInterface(Class<? extends PluginLoader> loader) throws IllegalArgumentException
    {
        PluginLoader instance;

        if (PluginLoader.class.isAssignableFrom(loader))
        {
            try
            {
                Constructor constructor = loader.getConstructor(new Class[] { Server.class });
                instance = (PluginLoader)constructor.newInstance(new Object[] { this.server });
            }
            catch (NoSuchMethodException ex)
            {
                String className = loader.getName();

                Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                throw new IllegalArgumentException(String.format("Class {0} does not have a public {1} (Server) constructor", new Object[] { className, className }), ex);
            }
            catch (Exception ex)
            {
                Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                throw new IllegalArgumentException(String.format("Unexpected exception {0} while attempting to construct a new instance of {1}", new Object[] { ex.getClass().getName(), loader.getName() }), ex);
            }
        }
        else
        {
            throw new IllegalArgumentException(String.format("Class {0} does not implement interface PluginLoader", new Object[] { loader.getName() }));
        }

        Pattern[] patterns = instance.getPluginFileFilters();

        synchronized (this)
        {
            for (Pattern pattern : patterns)
            this.fileAssociations.put(pattern, instance);
        }
    }

    @Override
    public Plugin[] loadPlugins(File directory)
    {
        Validate.notNull(directory, "Directory cannot be null");
        Validate.isTrue(directory.isDirectory(), "Directory must be a directory");

        List<Plugin> result = new ArrayList<>();
        Set<Pattern> filters = this.fileAssociations.keySet();

        if (!this.server.getUpdateFolder().equals(""))
        {
            updateDirectory = new File(directory, this.server.getUpdateFolder());
        }

        Map<String, File> plugins = new HashMap<>();
        Set<String> loadedPlugins = new HashSet<>();
        Map<String, LinkedList<String>> dependencies = new HashMap<>();
        Map<String, LinkedList<String>> softDependencies = new HashMap<>();

        for (File file : directory.listFiles())
        {
            PluginLoader loader = null;

            for (Pattern filter : filters)
            {
                Matcher match = filter.matcher(file.getName());
                if (match.find())
                {
                    loader = (PluginLoader)this.fileAssociations.get(filter);
                }
            }

            if (loader != null)
            {
                PluginDescriptionFile description;

                try
                {
                    description = loader.getPluginDescription(file);
                }
                catch (InvalidDescriptionException ex)
                {
                    Bukkit.getLogger().log(Level.SEVERE, "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'", ex);
                    continue;
                }

                plugins.put(description.getName(), file);

                Collection<String> dependencySet = description.getDepend();
                Collection<String> softDependencySet = description.getSoftDepend();

                if (softDependencySet != null)
                    softDependencies.put(description.getName(), new LinkedList<>(softDependencySet));

                if (dependencySet != null)
                    dependencies.put(description.getName(), new LinkedList<>(dependencySet));
            }
        }

        while (!plugins.isEmpty())
        {
            boolean missingDependency = true;
            Iterator<String> pluginIterator = plugins.keySet().iterator();

            while (pluginIterator.hasNext())
            {
                String plugin = pluginIterator.next();

                if (dependencies.containsKey(plugin))
                {
                    Iterator<String> dependencyIterator = dependencies.get(plugin).iterator();

                    while (dependencyIterator.hasNext())
                    {
                        String dependency = dependencyIterator.next();

                        if (loadedPlugins.contains(dependency))
                        {
                            dependencyIterator.remove();
                        }

                        else if (!plugins.containsKey(dependency))
                        {
                            missingDependency = false;
                            File file = (File)plugins.get(plugin);
                            pluginIterator.remove();
                            softDependencies.remove(plugin);
                            dependencies.remove(plugin);

                            this.server.getLogger().log(Level.SEVERE, "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'", new UnknownDependencyException(dependency));

                            break;
                        }
                    }

                    if ((dependencies.containsKey(plugin)) && (((Collection)dependencies.get(plugin)).isEmpty()))
                    {
                        dependencies.remove(plugin);
                    }
                }

                if (softDependencies.containsKey(plugin))
                {
                    Iterator<String> softDependencyIterator = softDependencies.get(plugin).iterator();

                    while (softDependencyIterator.hasNext())
                    {
                        String softDependency = softDependencyIterator.next();

                        if (!plugins.containsKey(softDependency))
                        {
                            softDependencyIterator.remove();
                        }
                    }

                    if (softDependencies.get(plugin).isEmpty())
                    {
                        softDependencies.remove(plugin);
                    }
                }

                if ((!dependencies.containsKey(plugin)) && (!softDependencies.containsKey(plugin)) && (plugins.containsKey(plugin)))
                {
                    File file = plugins.get(plugin);
                    pluginIterator.remove();
                    missingDependency = false;

                    try
                    {
                        result.add(loadPlugin(file));
                        loadedPlugins.add(plugin);
                    }
                    catch (InvalidPluginException ex)
                    {
                        Bukkit.getLogger().log(Level.SEVERE, "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'", ex);
                    }
                }
            }

            if (missingDependency)
            {
                pluginIterator = plugins.keySet().iterator();

                while (pluginIterator.hasNext())
                {
                    String plugin = pluginIterator.next();

                    if (!dependencies.containsKey(plugin))
                    {
                        softDependencies.remove(plugin);
                        dependencies.remove(plugin);
                        missingDependency = false;
                        File file = plugins.get(plugin);
                        pluginIterator.remove();

                        try
                        {
                            result.add(loadPlugin(file));
                            loadedPlugins.add(plugin);
                        }
                        catch (InvalidPluginException ex)
                        {
                            Bukkit.getLogger().log(Level.SEVERE, "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'", ex);
                        }
                    }
                }

                if (missingDependency)
                {
                    softDependencies.clear();
                    dependencies.clear();
                    Iterator<File> failedPluginIterator = plugins.values().iterator();

                    while (failedPluginIterator.hasNext())
                    {
                        File file = failedPluginIterator.next();
                        failedPluginIterator.remove();
                        this.server.getLogger().log(Level.SEVERE, "Could not load ''{0}'' in folder ''{1}'': circular dependency detected", new Object[]{file.getPath(), directory.getPath()});
                    }
                }
            }

        }

        return result.toArray(new Plugin[result.size()]);
    }

    @Override
    public synchronized Plugin loadPlugin(File file) throws InvalidPluginException, UnknownDependencyException
    {
        Validate.notNull(file, "File cannot be null");

        checkUpdate(file);

        Set<Pattern> filters = this.fileAssociations.keySet();
        Plugin result = null;

        for (Pattern filter : filters)
        {
            String name = file.getName();
            Matcher match = filter.matcher(name);

            if (match.find())
            {
                PluginLoader loader = (PluginLoader)this.fileAssociations.get(filter);
                result = loader.loadPlugin(file);
            }
        }

        if (result != null)
        {
            this.plugins.add(result);
            this.lookupNames.put(result.getDescription().getName(), result);
        }

        return result;
    }

    private void checkUpdate(File file)
    {
        if ((updateDirectory == null) || (!updateDirectory.isDirectory()))
        {
            return;
        }

        File updateFile = new File(updateDirectory, file.getName());

        if ((updateFile.isFile()) && (FileUtil.copy(updateFile, file)))
        {
            updateFile.delete();
        }
    }

    @Override
    public synchronized Plugin getPlugin(String name)
    {
        return this.lookupNames.get(name);
    }

    @Override
    public synchronized Plugin[] getPlugins()
    {
        return this.plugins.toArray(new Plugin[this.plugins.size()]);
    }

    @Override
    public boolean isPluginEnabled(String name)
    {
        Plugin plugin = getPlugin(name);
        return isPluginEnabled(plugin);
    }

    @Override
    public boolean isPluginEnabled(Plugin plugin)
    {
        if ((plugin != null) && (this.plugins.contains(plugin)))
        {
            return plugin.isEnabled();
        }

        return false;
    }

    @Override
    public void enablePlugin(Plugin plugin)
    {
        if (!plugin.isEnabled())
        {
            List<Command> pluginCommands = PluginCommandYamlParser.parse(plugin);

            if (!pluginCommands.isEmpty())
            {
                this.commandMap.registerAll(plugin.getDescription().getName(), pluginCommands);
            }

            try
            {
                plugin.getPluginLoader().enablePlugin(plugin);
            }
            catch (Throwable ex)
            {
                Bukkit.getLogger().log(Level.SEVERE, "[WebOp] Error occurred (in the plugin loader) while enabling " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
            }

            HandlerList.bakeAll();
        }
    }

    @Override
    public void disablePlugins()
    {
        for (Plugin plugin : getPlugins())
        {
            disablePlugin(plugin);
        }
    }

    @Override
    public void disablePlugin(Plugin plugin)
    {
        if (plugin.isEnabled())
        {
            try
            {
                plugin.getPluginLoader().disablePlugin(plugin);
            }
            catch (Throwable ex)
            {
                Bukkit.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while disabling " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
            }

            try
            {
                this.server.getScheduler().cancelTasks(plugin);
            }
            catch (Throwable ex)
            {
                Bukkit.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while cancelling tasks for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
            }

            try
            {
                this.server.getServicesManager().unregisterAll(plugin);
            }
            catch (Throwable ex)
            {
                Bukkit.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while unregistering services for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
            }

            try
            {
                HandlerList.unregisterAll(plugin);
            }
            catch (Throwable ex)
            {
                Bukkit.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while unregistering events for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
            }

            try
            {
                this.server.getMessenger().unregisterIncomingPluginChannel(plugin);
                this.server.getMessenger().unregisterOutgoingPluginChannel(plugin);
            }
            catch (Throwable ex)
            {
                Bukkit.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while unregistering plugin channels for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
            }
        }
    }

    @Override
    public void clearPlugins()
    {
        synchronized (this)
        {
            disablePlugins();
            this.plugins.clear();
            this.lookupNames.clear();
            HandlerList.unregisterAll();
            this.fileAssociations.clear();
            this.permissions.clear();
            this.defaultPerms.get(true).clear();
            this.defaultPerms.get(false).clear();
        }
    }

    @Override
    public synchronized void callEvent(Event event)
    {
        HandlerList handlers = event.getHandlers();
        RegisteredListener[] listeners = handlers.getRegisteredListeners();

        for (RegisteredListener registration : listeners)
        {
            if (registration.getPlugin().isEnabled())
            {
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

                }
                catch (Throwable ex)
                {
                    Bukkit.getLogger().log(Level.SEVERE, "Could not pass event " + event.getEventName() + " to " + registration.getPlugin().getDescription().getName(), ex);
                }
            }
        }
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
            getEventListeners(getRegistrationClass(entry.getKey())).registerAll(entry.getValue());
        }
    }

    @Override
    public void registerEvent(Class<? extends Event> event, Listener listener, EventPriority priority, EventExecutor executor, Plugin plugin)
    {
        registerEvent(event, listener, priority, executor, plugin, false);
    }

    @Override
    public void registerEvent(Class<? extends Event> event, Listener listener, EventPriority priority, EventExecutor executor, Plugin plugin, boolean ignoreCancelled)
    {
        Validate.notNull(listener, "Listener cannot be null");
        Validate.notNull(priority, "Priority cannot be null");
        Validate.notNull(executor, "Executor cannot be null");
        Validate.notNull(plugin, "Plugin cannot be null");

        if (!plugin.isEnabled())
        {
            throw new IllegalPluginAccessException("Plugin attempted to register " + event + " while not enabled");
        }

        if (this.useTimings)
        {
            getEventListeners(event).register(new TimedRegisteredListener(listener, executor, priority, plugin, ignoreCancelled));
        }
        else
        {
            getEventListeners(event).register(new RegisteredListener(listener, executor, priority, plugin, ignoreCancelled));
        }
    }

    public HandlerList getEventListeners(Class<? extends Event> type)
    {
        Exception exception;

        try
        {
            Method method = getRegistrationClass(type).getDeclaredMethod("getHandlerList", new Class[0]);
            method.setAccessible(true);
            return (HandlerList)method.invoke(null, new Object[0]);
        }
        catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
        {
            // Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
            // throw new IllegalPluginAccessException(ex.toString());

            exception = ex;
        }

        try
        {
            Method method = getRegistrationClass(type).getDeclaredMethod("getHandlers", new Class[0]);
            method.setAccessible(true);
            return (HandlerList)method.invoke(null, new Object[0]);
        }
        catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
        {
            // Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
            // throw new IllegalPluginAccessException(ex.toString());

            exception = ex;
        }

        throw new IllegalPluginAccessException(exception.toString());
    }

    protected Class<? extends Event> getRegistrationClass(Class<? extends Event> clazz)
    {
        try
        {
            clazz.getDeclaredMethod("getHandlerList", new Class[0]);
            return clazz;
        }
        catch (NoSuchMethodException ex)
        {
            if ((clazz.getSuperclass() != null) && (!clazz.getSuperclass().equals(Event.class)) && (Event.class.isAssignableFrom(clazz.getSuperclass())))
            {
                return getRegistrationClass(clazz.getSuperclass().asSubclass(Event.class));
            }
        }

        throw new IllegalPluginAccessException("Unable to find handler list for event " + clazz.getName());
    }

    @Override
    public Permission getPermission(String name)
    {
        return this.permissions.get(name.toLowerCase());
    }

    @Override
    public void addPermission(Permission perm)
    {
        String name = perm.getName().toLowerCase();

        if (this.permissions.containsKey(name))
        {
            throw new IllegalArgumentException("The permission " + name + " is already defined!");
        }

        this.permissions.put(name, perm);
        calculatePermissionDefault(perm);
    }

    @Override
    public Set<Permission> getDefaultPermissions(boolean op)
    {
        return ImmutableSet.copyOf((Collection<Permission>)this.defaultPerms.get(op));
    }

    @Override
    public void removePermission(Permission perm)
    {
        removePermission(perm.getName().toLowerCase());
    }

    @Override
    public void removePermission(String name)
    {
        this.permissions.remove(name);
    }

    @Override
    public void recalculatePermissionDefaults(Permission perm)
    {
        if (this.permissions.containsValue(perm))
        {
            this.defaultPerms.get(true).remove(perm);
            this.defaultPerms.get(false).remove(perm);

            calculatePermissionDefault(perm);
        }
    }

    private void calculatePermissionDefault(Permission perm)
    {
        if ((perm.getDefault() == PermissionDefault.OP) || (perm.getDefault() == PermissionDefault.TRUE))
        {
            this.defaultPerms.get(true).add(perm);
            dirtyPermissibles(true);
        }

        if ((perm.getDefault() == PermissionDefault.NOT_OP) || (perm.getDefault() == PermissionDefault.TRUE))
        {
            this.defaultPerms.get(false).add(perm);
            dirtyPermissibles(false);
        }
    }

    private void dirtyPermissibles(boolean op)
    {
        Set<Permissible> permissibles = getDefaultPermSubscriptions(op);

        for (Permissible p : permissibles)
        {
            p.recalculatePermissions();
        }
    }

    @Override
    public void subscribeToPermission(String permission, Permissible permissible)
    {
        String name = permission.toLowerCase();
        Map<Permissible, Boolean> map = this.permSubs.get(name);

        if (map == null)
        {
            map = new WeakHashMap<>();
            this.permSubs.put(name, map);
        }

        map.put(permissible, true);
    }

    @Override
    public void unsubscribeFromPermission(String permission, Permissible permissible)
    {
        String name = permission.toLowerCase();
        Map<Permissible, Boolean> map = this.permSubs.get(name);

        if (map != null)
        {
            map.remove(permissible);

            if (map.isEmpty())
            {
                this.permSubs.remove(name);
            }
        }
    }

    @Override
    public Set<Permissible> getPermissionSubscriptions(String permission)
    {
        String name = permission.toLowerCase();
        Map<Permissible, Boolean> map = this.permSubs.get(name);

        if (map == null)
        {
            return ImmutableSet.of();
        }

        return ImmutableSet.copyOf(map.keySet());
    }

    @Override
    public void subscribeToDefaultPerms(boolean op, Permissible permissible)
    {
        Map<Permissible, Boolean> map = this.defSubs.get(op);

        if (map == null)
        {
            map = new WeakHashMap<>();
            this.defSubs.put(op, map);
        }

        map.put(permissible, true);
    }

    @Override
    public void unsubscribeFromDefaultPerms(boolean op, Permissible permissible)
    {
        Map<Permissible, Boolean> map = this.defSubs.get(op);

        if (map != null)
        {
            map.remove(permissible);

            if (map.isEmpty())
            {
                this.defSubs.remove(op);
            }
        }
    }

    @Override
    public Set<Permissible> getDefaultPermSubscriptions(boolean op)
    {
        Map<Permissible, Boolean> map = this.defSubs.get(op);

        if (map == null)
        {
            return ImmutableSet.of();
        }

        return ImmutableSet.copyOf(map.keySet());
    }

    @Override
    public Set<Permission> getPermissions()
    {
        return new HashSet<>(this.permissions.values());
    }

    @Override
    public boolean useTimings()
    {
        return this.useTimings;
    }

    public void useTimings(boolean use)
    {
        this.useTimings = use;
    }
}