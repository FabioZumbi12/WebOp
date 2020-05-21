// 
// Decompiled by Procyon v0.5.36
// 

package me.jayfella.webop.core;

import me.jayfella.webop.WebOpPlugin;
import org.eclipse.jetty.websocket.api.Session;

import java.util.Date;
import java.util.logging.Level;

public class WebOpUser {
    private final String username;
    private final String hash;
    private long lastActivity;
    private Session websocketSession;

    public WebOpUser(final String username) {
        this.username = username;
        this.hash = WebOpPlugin.PluginContext.getSessionManager().generateSession(username);
        this.lastActivity = System.currentTimeMillis();
    }

    public String getName() {
        return this.username;
    }

    public String getSession() {
        return this.hash;
    }

    public Session getWebSocketSession() {
        return this.websocketSession;
    }

    public void setWebSocketSession(final Session websocketSession) {
        this.websocketSession = websocketSession;
    }

    public void updateLastActivity() {
        this.lastActivity = System.currentTimeMillis();
    }

    public boolean isSessionExpired() {
        final Date currentDate = new Date(System.currentTimeMillis());
        final Date lastActive = new Date(this.lastActivity);
        return currentDate.getTime() - lastActive.getTime() > 900000L;
    }

    public boolean isWebSocketReady() {
        return this.websocketSession != null && this.websocketSession.isOpen();
    }

    public void sendSocketMessage(final String message) {
        if (!this.isWebSocketReady()) {
            return;
        }
        try {
            this.websocketSession.getRemote().sendString(message);
        } catch (Exception ex) {
            WebOpPlugin.PluginContext.getPlugin().getLogger().log(Level.WARNING, "WebSocket Error:", ex);
        }
    }
}
