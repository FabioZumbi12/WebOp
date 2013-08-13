package me.jayfella.webop2;

import java.util.logging.Level;
import java.util.logging.Logger;
import me.jayfella.webop2.Core.PageHandler;
import me.jayfella.webop2.Core.RequestHandler;
import me.jayfella.webop2.DataStore.*;
import me.jayfella.webop2.Core.PluginProfiler;
import org.bukkit.command.ConsoleCommandSender;

public final class PluginContext
{
    private final WebOp2Plugin plugin;
    private final PluginSettings pluginSettings;
    private final SessionManager sessionManager;

    private final HealthMonitor healthMonitor;
    private final HealthMonitor memoryMonitor;
    private final EntityMonitor entityMonitor;
    private final ChunkMonitor chunkMonitor;
    private final PlayerMonitor playerMonitor;
    private final ConsoleMonitor consoleMonitor;
    private final LogReader logReader;
    private final MessageHandler messageHandler;
    private final ServerPropertiesHandler serverPropertiesHandler;

    private final RequestHandler requestHandler;
    private final PageHandler pageHandler;

    private PluginProfiler pluginProfiler;

    private final boolean vaultPresent;

    public PluginContext(WebOp2Plugin plugin)
    {
        this.plugin = plugin;
        this.pluginSettings = new PluginSettings(this);
        this.sessionManager = new SessionManager(this);

        java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
        java.lang.System.setProperty("java.net.preferIPv4Stack", "true");

        // monitors
        this.healthMonitor = new HealthMonitor(this);
        this.memoryMonitor = new HealthMonitor(this);
        this.entityMonitor = new EntityMonitor(this);
        this.chunkMonitor = new ChunkMonitor(this);
        this.playerMonitor = new PlayerMonitor(this);
        this.consoleMonitor = new ConsoleMonitor(this);
        this.serverPropertiesHandler = new ServerPropertiesHandler();

        this.messageHandler = new MessageHandler(this);

        this.vaultPresent = (plugin.getServer().getPluginManager().getPlugin("Vault") != null);

        this.logReader = new LogReader();

        // http server
        this.requestHandler = new RequestHandler(this);
        this.pageHandler = new PageHandler(this);

        // profiler
        try
        {
            this.pluginProfiler = new PluginProfiler(this);
        }
        catch (NoSuchMethodException | ClassNotFoundException | NoSuchFieldException | IllegalAccessException ex)
        {
            Logger.getLogger(PluginContext.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public final WebOp2Plugin getPlugin() { return this.plugin; }
    public final PluginSettings getPluginSettings() { return this.pluginSettings; }
    public final SessionManager getSessionManager() { return this.sessionManager; }

    public final Logger getLogger() { return this.plugin.getLogger(); }
    public final ConsoleCommandSender getConsole() { return this.plugin.getServer().getConsoleSender(); }

    public final HealthMonitor getHealthMonitor() { return this.healthMonitor; }
    public final HealthMonitor getMemoryMonitor() { return this.memoryMonitor; }
    public final EntityMonitor getEntityMonitor() { return this.entityMonitor; }
    public final ChunkMonitor getChunkMonitor() { return this.chunkMonitor; }
    public final PlayerMonitor getPlayerMonitor() { return this.playerMonitor; }
    public final ConsoleMonitor getConsoleMonitor() { return this.consoleMonitor; }
    public final MessageHandler getMessageHandler() { return this.messageHandler; }
    public final ServerPropertiesHandler getServerPropertiesHandler() { return this.serverPropertiesHandler; }

    public final boolean vaultPresent() { return this.vaultPresent; }

    public final LogReader getLogReader() { return this.logReader; }

    public final RequestHandler getRequestHandler() { return this.requestHandler; }
    public final PageHandler getPageHandler() { return this.pageHandler; }

    public final PluginProfiler getPluginProfiler() { return this.pluginProfiler; }

}
