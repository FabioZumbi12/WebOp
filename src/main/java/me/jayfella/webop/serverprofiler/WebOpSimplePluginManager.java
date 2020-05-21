// 
// Decompiled by Procyon v0.5.36
// 

package me.jayfella.webop.serverprofiler;

import com.google.common.collect.ImmutableSet;
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
import org.bukkit.plugin.*;
import org.bukkit.util.FileUtil;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebOpSimplePluginManager implements PluginManager {
    public static File updateDirectory;

    static {
        WebOpSimplePluginManager.updateDirectory = null;
    }

    public Server server;
    public boolean useTimings;
    public SimpleCommandMap commandMap;
    public List<Plugin> plugins;
    public Map<Pattern, PluginLoader> fileAssociations;
    public Map<String, Plugin> lookupNames;
    public Map<String, Permission> permissions;
    public Map<Boolean, Set<Permission>> defaultPerms;
    public Map<String, Map<Permissible, Boolean>> permSubs;
    public Map<Boolean, Map<Permissible, Boolean>> defSubs;

    public WebOpSimplePluginManager(final Server instance, final SimpleCommandMap commandMap) {
        this.useTimings = false;
        this.plugins = new ArrayList<Plugin>();
        this.fileAssociations = new HashMap<Pattern, PluginLoader>();
        this.lookupNames = new HashMap<String, Plugin>();
        this.permissions = new HashMap<String, Permission>();
        this.defaultPerms = new LinkedHashMap<Boolean, Set<Permission>>();
        this.permSubs = new HashMap<String, Map<Permissible, Boolean>>();
        this.defSubs = new HashMap<Boolean, Map<Permissible, Boolean>>();
        this.server = instance;
        this.commandMap = commandMap;
        this.defaultPerms.put(true, new HashSet<Permission>());
        this.defaultPerms.put(false, new HashSet<Permission>());
    }

    public void registerInterface(final Class<? extends PluginLoader> loader) throws IllegalArgumentException {
        if (PluginLoader.class.isAssignableFrom(loader)) {
            PluginLoader instance = null;
            Label_0170:
            {
                try {
                    final Constructor<PluginLoader> constructor = (Constructor<PluginLoader>) loader.getConstructor(Server.class);
                    instance = constructor.newInstance(this.server);
                    break Label_0170;
                } catch (NoSuchMethodException ex) {
                    final String className = loader.getName();
                    Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                    throw new IllegalArgumentException(String.format("Class {0} does not have a public {1} (Server) constructor", className, className), ex);
                } catch (Exception ex2) {
                    Bukkit.getLogger().log(Level.SEVERE, ex2.getMessage(), ex2);
                    throw new IllegalArgumentException(String.format("Unexpected exception {0} while attempting to construct a new instance of {1}", ex2.getClass().getName(), loader.getName()), ex2);
                }
            }
            final Pattern[] patterns = instance.getPluginFileFilters();
            synchronized (this) {
                for (final Pattern pattern : patterns) {
                    this.fileAssociations.put(pattern, instance);
                }
            }
            return;
        }
        throw new IllegalArgumentException(String.format("Class {0} does not implement interface PluginLoader", loader.getName()));
    }

    public Plugin[] loadPlugins(final File directory) {
        Validate.notNull(directory, "Directory cannot be null");
        Validate.isTrue(directory.isDirectory(), "Directory must be a directory");
        final List<Plugin> result = new ArrayList<Plugin>();
        final Set<Pattern> filters = this.fileAssociations.keySet();
        if (!this.server.getUpdateFolder().equals("")) {
            WebOpSimplePluginManager.updateDirectory = new File(directory, this.server.getUpdateFolder());
        }
        final Map<String, File> plugins = new HashMap<String, File>();
        final Set<String> loadedPlugins = new HashSet<String>();
        final Map<String, LinkedList<String>> dependencies = new HashMap<String, LinkedList<String>>();
        final Map<String, LinkedList<String>> softDependencies = new HashMap<String, LinkedList<String>>();
        for (final File file : directory.listFiles()) {
            PluginLoader loader = null;
            for (final Pattern filter : filters) {
                final Matcher match = filter.matcher(file.getName());
                if (match.find()) {
                    loader = this.fileAssociations.get(filter);
                }
            }
            Label_0364:
            {
                if (loader != null) {
                    PluginDescriptionFile description;
                    try {
                        description = loader.getPluginDescription(file);
                    } catch (InvalidDescriptionException ex) {
                        Bukkit.getLogger().log(Level.SEVERE, "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'", ex);
                        break Label_0364;
                    }
                    plugins.put(description.getName(), file);
                    final Collection<String> dependencySet = description.getDepend();
                    final Collection<String> softDependencySet = description.getSoftDepend();
                    if (softDependencySet != null) {
                        softDependencies.put(description.getName(), new LinkedList<String>(softDependencySet));
                    }
                    if (dependencySet != null) {
                        dependencies.put(description.getName(), new LinkedList<String>(dependencySet));
                    }
                }
            }
        }
        while (!plugins.isEmpty()) {
            boolean missingDependency = true;
            Iterator<String> pluginIterator = plugins.keySet().iterator();
            while (pluginIterator.hasNext()) {
                final String plugin = pluginIterator.next();
                if (dependencies.containsKey(plugin)) {
                    final Iterator<String> dependencyIterator = dependencies.get(plugin).iterator();
                    while (dependencyIterator.hasNext()) {
                        final String dependency = dependencyIterator.next();
                        if (loadedPlugins.contains(dependency)) {
                            dependencyIterator.remove();
                        } else {
                            if (!plugins.containsKey(dependency)) {
                                missingDependency = false;
                                final File file2 = plugins.get(plugin);
                                pluginIterator.remove();
                                softDependencies.remove(plugin);
                                dependencies.remove(plugin);
                                this.server.getLogger().log(Level.SEVERE, "Could not load '" + file2.getPath() + "' in folder '" + directory.getPath() + "'", new UnknownDependencyException(dependency));
                                break;
                            }
                            continue;
                        }
                    }
                    if (dependencies.containsKey(plugin) && dependencies.get(plugin).isEmpty()) {
                        dependencies.remove(plugin);
                    }
                }
                if (softDependencies.containsKey(plugin)) {
                    final Iterator<String> softDependencyIterator = softDependencies.get(plugin).iterator();
                    while (softDependencyIterator.hasNext()) {
                        final String softDependency = softDependencyIterator.next();
                        if (!plugins.containsKey(softDependency)) {
                            softDependencyIterator.remove();
                        }
                    }
                    if (softDependencies.get(plugin).isEmpty()) {
                        softDependencies.remove(plugin);
                    }
                }
                if (!dependencies.containsKey(plugin) && !softDependencies.containsKey(plugin) && plugins.containsKey(plugin)) {
                    final File file = plugins.get(plugin);
                    pluginIterator.remove();
                    missingDependency = false;
                    try {
                        result.add(this.loadPlugin(file));
                        loadedPlugins.add(plugin);
                    } catch (InvalidPluginException ex2) {
                        Bukkit.getLogger().log(Level.SEVERE, "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'", ex2);
                    }
                }
            }
            if (missingDependency) {
                pluginIterator = plugins.keySet().iterator();
                while (pluginIterator.hasNext()) {
                    final String plugin = pluginIterator.next();
                    if (!dependencies.containsKey(plugin)) {
                        softDependencies.remove(plugin);
                        dependencies.remove(plugin);
                        missingDependency = false;
                        final File file = plugins.get(plugin);
                        pluginIterator.remove();
                        try {
                            result.add(this.loadPlugin(file));
                            loadedPlugins.add(plugin);
                        } catch (InvalidPluginException ex2) {
                            Bukkit.getLogger().log(Level.SEVERE, "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'", ex2);
                        }
                    }
                }
                if (!missingDependency) {
                    continue;
                }
                softDependencies.clear();
                dependencies.clear();
                final Iterator<File> failedPluginIterator = plugins.values().iterator();
                while (failedPluginIterator.hasNext()) {
                    final File file = failedPluginIterator.next();
                    failedPluginIterator.remove();
                    this.server.getLogger().log(Level.SEVERE, "Could not load ''{0}'' in folder ''{1}'': circular dependency detected", new Object[]{file.getPath(), directory.getPath()});
                }
            }
        }
        return result.toArray(new Plugin[result.size()]);
    }

    public synchronized Plugin loadPlugin(final File file) throws InvalidPluginException, UnknownDependencyException {
        Validate.notNull(file, "File cannot be null");
        this.checkUpdate(file);
        final Set<Pattern> filters = this.fileAssociations.keySet();
        Plugin result = null;
        for (final Pattern filter : filters) {
            final String name = file.getName();
            final Matcher match = filter.matcher(name);
            if (match.find()) {
                final PluginLoader loader = this.fileAssociations.get(filter);
                result = loader.loadPlugin(file);
            }
        }
        if (result != null) {
            this.plugins.add(result);
            this.lookupNames.put(result.getDescription().getName(), result);
        }
        return result;
    }

    private void checkUpdate(final File file) {
        if (WebOpSimplePluginManager.updateDirectory == null || !WebOpSimplePluginManager.updateDirectory.isDirectory()) {
            return;
        }
        final File updateFile = new File(WebOpSimplePluginManager.updateDirectory, file.getName());
        if (updateFile.isFile() && FileUtil.copy(updateFile, file)) {
            updateFile.delete();
        }
    }

    public synchronized Plugin getPlugin(final String name) {
        return this.lookupNames.get(name);
    }

    public synchronized Plugin[] getPlugins() {
        return this.plugins.toArray(new Plugin[this.plugins.size()]);
    }

    public boolean isPluginEnabled(final String name) {
        final Plugin plugin = this.getPlugin(name);
        return this.isPluginEnabled(plugin);
    }

    public boolean isPluginEnabled(final Plugin plugin) {
        return plugin != null && this.plugins.contains(plugin) && plugin.isEnabled();
    }

    public void enablePlugin(final Plugin plugin) {
        if (!plugin.isEnabled()) {
            final List<Command> pluginCommands = PluginCommandYamlParser.parse(plugin);
            if (!pluginCommands.isEmpty()) {
                this.commandMap.registerAll(plugin.getDescription().getName(), pluginCommands);
            }
            try {
                plugin.getPluginLoader().enablePlugin(plugin);
            } catch (Throwable ex) {
                Bukkit.getLogger().log(Level.SEVERE, "[WebOp] Error occurred (in the plugin loader) while enabling " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
            }
            HandlerList.bakeAll();
        }
    }

    public void disablePlugins() {
        for (final Plugin plugin : this.getPlugins()) {
            this.disablePlugin(plugin);
        }
    }

    public void disablePlugin(final Plugin plugin) {
        if (plugin.isEnabled()) {
            try {
                plugin.getPluginLoader().disablePlugin(plugin);
            } catch (Throwable ex) {
                Bukkit.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while disabling " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
            }
            try {
                this.server.getScheduler().cancelTasks(plugin);
            } catch (Throwable ex) {
                Bukkit.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while cancelling tasks for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
            }
            try {
                this.server.getServicesManager().unregisterAll(plugin);
            } catch (Throwable ex) {
                Bukkit.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while unregistering services for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
            }
            try {
                HandlerList.unregisterAll(plugin);
            } catch (Throwable ex) {
                Bukkit.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while unregistering events for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
            }
            try {
                this.server.getMessenger().unregisterIncomingPluginChannel(plugin);
                this.server.getMessenger().unregisterOutgoingPluginChannel(plugin);
            } catch (Throwable ex) {
                Bukkit.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while unregistering plugin channels for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
            }
        }
    }

    public void clearPlugins() {
        synchronized (this) {
            this.disablePlugins();
            this.plugins.clear();
            this.lookupNames.clear();
            HandlerList.unregisterAll();
            this.fileAssociations.clear();
            this.permissions.clear();
            this.defaultPerms.get(true).clear();
            this.defaultPerms.get(false).clear();
        }
    }

    public synchronized void callEvent(final Event event) {
        final HandlerList handlers = event.getHandlers();
        final RegisteredListener[] arr$;
        final RegisteredListener[] listeners = arr$ = handlers.getRegisteredListeners();
        for (final RegisteredListener registration : arr$) {
            if (registration.getPlugin().isEnabled()) {
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
                } catch (Throwable ex2) {
                    Bukkit.getLogger().log(Level.SEVERE, "Could not pass event " + event.getEventName() + " to " + registration.getPlugin().getDescription().getName(), ex2);
                }
            }
        }
    }

    public void registerEvents(final Listener listener, final Plugin plugin) {
        if (!plugin.isEnabled()) {
            throw new IllegalPluginAccessException("Plugin attempted to register " + listener + " while not enabled");
        }
        for (final Map.Entry<Class<? extends Event>, Set<RegisteredListener>> entry : plugin.getPluginLoader().createRegisteredListeners(listener, plugin).entrySet()) {
            this.getEventListeners(this.getRegistrationClass(entry.getKey())).registerAll(entry.getValue());
        }
    }

    public void registerEvent(final Class<? extends Event> event, final Listener listener, final EventPriority priority, final EventExecutor executor, final Plugin plugin) {
        this.registerEvent(event, listener, priority, executor, plugin, false);
    }

    public void registerEvent(final Class<? extends Event> event, final Listener listener, final EventPriority priority, final EventExecutor executor, final Plugin plugin, final boolean ignoreCancelled) {
        Validate.notNull(listener, "Listener cannot be null");
        Validate.notNull(priority, "Priority cannot be null");
        Validate.notNull(executor, "Executor cannot be null");
        Validate.notNull(plugin, "Plugin cannot be null");
        if (!plugin.isEnabled()) {
            throw new IllegalPluginAccessException("Plugin attempted to register " + event + " while not enabled");
        }
        if (this.useTimings) {
            this.getEventListeners(event).register(new TimedRegisteredListener(listener, executor, priority, plugin, ignoreCancelled));
        } else {
            this.getEventListeners(event).register(new RegisteredListener(listener, executor, priority, plugin, ignoreCancelled));
        }
    }

    public HandlerList getEventListeners(final Class<? extends Event> type) {
        try {
            final Method method = this.getRegistrationClass(type).getDeclaredMethod("getHandlerList");
            method.setAccessible(true);
            return (HandlerList) method.invoke(null, new Object[0]);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            try {
                final Method method = this.getRegistrationClass(type).getDeclaredMethod("getHandlers");
                method.setAccessible(true);
                return (HandlerList) method.invoke(null, new Object[0]);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex1) {
                throw new IllegalPluginAccessException(ex1.toString());
            }
        }
    }

    protected Class<? extends Event> getRegistrationClass(final Class<? extends Event> clazz) {
        try {
            clazz.getDeclaredMethod("getHandlerList");
            return clazz;
        } catch (NoSuchMethodException ex) {
            if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Event.class) && Event.class.isAssignableFrom(clazz.getSuperclass())) {
                return this.getRegistrationClass(clazz.getSuperclass().asSubclass(Event.class));
            }
            throw new IllegalPluginAccessException("Unable to find handler list for event " + clazz.getName());
        }
    }

    public Permission getPermission(final String name) {
        return this.permissions.get(name.toLowerCase());
    }

    public void addPermission(final Permission perm) {
        final String name = perm.getName().toLowerCase();
        if (this.permissions.containsKey(name)) {
            throw new IllegalArgumentException("The permission " + name + " is already defined!");
        }
        this.permissions.put(name, perm);
        this.calculatePermissionDefault(perm);
    }

    public Set<Permission> getDefaultPermissions(final boolean op) {
        return ImmutableSet.copyOf(this.defaultPerms.get(op));
    }

    public void removePermission(final Permission perm) {
        this.removePermission(perm.getName().toLowerCase());
    }

    public void removePermission(final String name) {
        this.permissions.remove(name);
    }

    public void recalculatePermissionDefaults(final Permission perm) {
        if (this.permissions.containsValue(perm)) {
            this.defaultPerms.get(true).remove(perm);
            this.defaultPerms.get(false).remove(perm);
            this.calculatePermissionDefault(perm);
        }
    }

    private void calculatePermissionDefault(final Permission perm) {
        if (perm.getDefault() == PermissionDefault.OP || perm.getDefault() == PermissionDefault.TRUE) {
            this.defaultPerms.get(true).add(perm);
            this.dirtyPermissibles(true);
        }
        if (perm.getDefault() == PermissionDefault.NOT_OP || perm.getDefault() == PermissionDefault.TRUE) {
            this.defaultPerms.get(false).add(perm);
            this.dirtyPermissibles(false);
        }
    }

    private void dirtyPermissibles(final boolean op) {
        final Set<Permissible> permissibles = this.getDefaultPermSubscriptions(op);
        for (final Permissible p : permissibles) {
            p.recalculatePermissions();
        }
    }

    public void subscribeToPermission(final String permission, final Permissible permissible) {
        final String name = permission.toLowerCase();
        Map<Permissible, Boolean> map = this.permSubs.computeIfAbsent(name, k -> new WeakHashMap<>());
        map.put(permissible, true);
    }

    public void unsubscribeFromPermission(final String permission, final Permissible permissible) {
        final String name = permission.toLowerCase();
        final Map<Permissible, Boolean> map = this.permSubs.get(name);
        if (map != null) {
            map.remove(permissible);
            if (map.isEmpty()) {
                this.permSubs.remove(name);
            }
        }
    }

    public Set<Permissible> getPermissionSubscriptions(final String permission) {
        final String name = permission.toLowerCase();
        final Map<Permissible, Boolean> map = this.permSubs.get(name);
        if (map == null) {
            return ImmutableSet.of();
        }
        return ImmutableSet.copyOf(map.keySet());
    }

    public void subscribeToDefaultPerms(final boolean op, final Permissible permissible) {
        Map<Permissible, Boolean> map = this.defSubs.computeIfAbsent(op, k -> new WeakHashMap<Permissible, Boolean>());
        map.put(permissible, true);
    }

    public void unsubscribeFromDefaultPerms(final boolean op, final Permissible permissible) {
        final Map<Permissible, Boolean> map = this.defSubs.get(op);
        if (map != null) {
            map.remove(permissible);
            if (map.isEmpty()) {
                this.defSubs.remove(op);
            }
        }
    }

    public Set<Permissible> getDefaultPermSubscriptions(final boolean op) {
        final Map<Permissible, Boolean> map = this.defSubs.get(op);
        if (map == null) {
            return ImmutableSet.of();
        }
        return ImmutableSet.copyOf(map.keySet());
    }

    public Set<Permission> getPermissions() {
        return new HashSet<Permission>(this.permissions.values());
    }

    public boolean useTimings() {
        return this.useTimings;
    }

    public void useTimings(final boolean use) {
        this.useTimings = use;
    }
}
