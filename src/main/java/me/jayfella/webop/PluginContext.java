/*
 * Copyright (c) 2020 - @FabioZumbi12
 * Last Modified: 14/06/2020 02:48.
 *
 * This class is provided 'as-is', without any express or implied warranty. In no event will the authors be held liable for any
 *  damages arising from the use of this class.
 *
 * Permission is granted to anyone to use this class for any purpose, including commercial plugins, and to alter it and
 * redistribute it freely, subject to the following restrictions:
 * 1 - The origin of this class must not be misrepresented; you must not claim that you wrote the original software. If you
 * use this class in other plugins, an acknowledgment in the plugin documentation would be appreciated but is not required.
 * 2 - Altered source versions must be plainly marked as such, and must not be misrepresented as being the original class.
 * 3 - This notice may not be removed or altered from any source distribution.
 *
 * Esta classe é fornecida "como está", sem qualquer garantia expressa ou implícita. Em nenhum caso os autores serão
 * responsabilizados por quaisquer danos decorrentes do uso desta classe.
 *
 * É concedida permissão a qualquer pessoa para usar esta classe para qualquer finalidade, incluindo plugins pagos, e para
 * alterá-lo e redistribuí-lo livremente, sujeito às seguintes restrições:
 * 1 - A origem desta classe não deve ser deturpada; você não deve afirmar que escreveu a classe original. Se você usar esta
 *  classe em um plugin, uma confirmação de autoria na documentação do plugin será apreciada, mas não é necessária.
 * 2 - Versões de origem alteradas devem ser claramente marcadas como tal e não devem ser deturpadas como sendo a
 * classe original.
 * 3 - Este aviso não pode ser removido ou alterado de qualquer distribuição de origem.
 */

package me.jayfella.webop;

import me.jayfella.webop.Servlets.MyHttpServlet;
import me.jayfella.webop.Servlets.MyWebSocket;
import me.jayfella.webop.core.ServerProfiler;
import me.jayfella.webop.core.SessionManager;
import me.jayfella.webop.datastore.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

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
    private final ServerPropertiesHandler serverPropertiesHandler;
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
        this.serverPropertiesHandler = new ServerPropertiesHandler();
        this.initJetty();
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
            ((org.apache.logging.log4j.core.Logger) LogManager.getLogger("org.eclipse.jetty.util.log")).setLevel(Level.OFF);

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

    public final ServerPropertiesHandler getServerPropertiesHandler() {
        return this.serverPropertiesHandler;
    }
}
