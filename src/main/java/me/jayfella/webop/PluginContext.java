// 
// Decompiled by Procyon v0.5.36
// 

package me.jayfella.webop;

import me.jayfella.webop.Servlets.MyHttpServlet;
import me.jayfella.webop.Servlets.MyWebSocket;
import me.jayfella.webop.core.ServerProfiler;
import me.jayfella.webop.core.SessionManager;
import me.jayfella.webop.datastore.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import java.util.Properties;

public final class PluginContext {
    private final WebOpPlugin plugin;
    private final PluginSettings pluginSettings;
    private final SessionManager sessionManager;
    private final UtilizationMonitor utilizationMonitor;
    private final PlayerMonitor playerMonitor;
    private final ConsoleMonitor consoleMonitor;
    private final MessageHandler messageHandler;
    private final LogBlockMonitor logblockMonitor;
    private final EntityMonitor entityMonitor;
    private final WorldMonitor worldMonitor;
    private final ServerProfiler serverProfiler;
    private Server server;

    public PluginContext(final WebOpPlugin plugin) {
        this.plugin = plugin;
        this.pluginSettings = new PluginSettings(this);
        this.sessionManager = new SessionManager(this);
        this.utilizationMonitor = new UtilizationMonitor(this);
        this.playerMonitor = new PlayerMonitor(this);
        this.consoleMonitor = new ConsoleMonitor(this);
        this.messageHandler = new MessageHandler(this);
        this.logblockMonitor = new LogBlockMonitor();
        this.entityMonitor = new EntityMonitor();
        this.worldMonitor = new WorldMonitor(this);
        this.serverProfiler = new ServerProfiler(this);
        this.initJetty();
        this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, new AutoSaver(this), 6000L, 6000L);
    }

    private void initJetty() {
        new Thread(() -> {
            server = new Server(PluginContext.this.pluginSettings.getHttpPort());
            final HandlerCollection handlerCollection = new HandlerCollection();
            server.setHandler(handlerCollection);
            final WebSocketHandler wsHandler = new WebSocketHandler() {
                @Override
                public void configure(final WebSocketServletFactory factory) {
                    factory.register(MyWebSocket.class);
                }
            };

            this.plugin.getLogger().info("WebOp started on http://localhost:" + this.pluginSettings.getHttpPort());
            // Disable log
            ((org.apache.logging.log4j.core.Logger) LogManager.getLogger("org.eclipse.jetty.server.Server")).setLevel(Level.OFF);
            ((org.apache.logging.log4j.core.Logger) LogManager.getLogger("org.eclipse.jetty.server.session")).setLevel(Level.OFF);
            ((org.apache.logging.log4j.core.Logger) LogManager.getLogger("org.eclipse.jetty.server.handler.ContextHandler")).setLevel(Level.OFF);
            ((org.apache.logging.log4j.core.Logger) LogManager.getLogger("org.eclipse.jetty.server.AbstractConnector")).setLevel(Level.OFF);

            wsHandler.setServer(server);
            final ContextHandler wsContext = new ContextHandler();
            wsContext.setContextPath("/socket");
            wsContext.setHandler(wsHandler);
            handlerCollection.addHandler(wsContext);
            final ServletContextHandler httpContext = new ServletContextHandler(1);
            httpContext.addServlet(new ServletHolder(new MyHttpServlet()), "/*");
            handlerCollection.addHandler(httpContext);
            try {
                server.start();
                server.join();
            } catch (Exception ex) {
                this.plugin.getLogger().info("Failed to start WebOp server!");
                ex.printStackTrace();
            }
        }).start();
    }

    public void stopServer() {
        try {
            server.stop();
            this.plugin.getLogger().info("WebOp server stopped!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public WebOpPlugin getPlugin() {
        return this.plugin;
    }

    public PluginSettings getPluginSettings() {
        return this.pluginSettings;
    }

    public SessionManager getSessionManager() {
        return this.sessionManager;
    }

    public UtilizationMonitor getUtilizationMonitor() {
        return this.utilizationMonitor;
    }

    public PlayerMonitor getPlayerMonitor() {
        return this.playerMonitor;
    }

    public ConsoleMonitor getConsoleMonitor() {
        return this.consoleMonitor;
    }

    public MessageHandler getMessageHandler() {
        return this.messageHandler;
    }

    public LogBlockMonitor getLogBlockMonitor() {
        return this.logblockMonitor;
    }

    public EntityMonitor getEntityMonitor() {
        return this.entityMonitor;
    }

    public WorldMonitor getWorldMonitor() {
        return this.worldMonitor;
    }

    public ServerProfiler getServerProfiler() {
        return this.serverProfiler;
    }

    private class AutoSaver implements Runnable {
        private final PluginContext context;

        public AutoSaver(final PluginContext context) {
            this.context = context;
        }

        @Override
        public void run() {
            for (final World world : this.context.getPlugin().getServer().getWorlds()) {
                world.save();
            }
        }
    }
}
