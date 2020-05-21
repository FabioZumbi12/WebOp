// 
// Decompiled by Procyon v0.5.36
// 

package me.jayfella.webop.core;

import me.jayfella.webop.PluginContext;
import me.jayfella.webop.WebOpPlugin;
import org.eclipse.jetty.websocket.api.Session;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpCookie;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SessionManager {
    private final PluginContext context;
    private final List<Session> websocketSessions;
    private final List<WebOpUser> users;
    private final List<String> whitelist;
    private final List<String> consoleViewPermission;
    private final List<String> consoleUsePermission;

    public SessionManager(final PluginContext context) {
        this.context = context;
        this.websocketSessions = new ArrayList<Session>();
        this.users = new ArrayList<WebOpUser>();
        this.whitelist = this.populateWhitelist("whitelist");
        this.consoleViewPermission = this.populateWhitelist("consoleView");
        this.consoleUsePermission = this.populateWhitelist("consoleOp");
        this.sessionRoundRobin();
    }

    private void sessionRoundRobin() {
        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
            final List<String> names = new ArrayList<String>();
            for (final WebOpUser user : SessionManager.this.users) {
                names.add(user.getName());
            }
            for (final String name : names) {
                final WebOpUser user2 = SessionManager.this.getUser(name);
                if (user2 == null) {
                    continue;
                }
                if (!user2.isSessionExpired()) {
                    continue;
                }
                SessionManager.this.logUserOut(name);
            }
        }, 1L, 1L, TimeUnit.MINUTES);
    }

    private List<String> populateWhitelist(final String node) {
        final List<String> list = new ArrayList<String>();
        list.addAll(this.context.getPluginSettings().getFileConfiguration().getStringList(node));
        return list;
    }

    public List<String> getWhitelist() {
        return this.whitelist;
    }

    public boolean isWhitelisted(final String username) {
        return WebOpPlugin.PluginContext.getPlugin().getServer().getOfflinePlayer(username).isOp() || this.whitelist.contains(username);
    }

    public void addToWhitelist(final String name) {
        if (!this.isWhitelisted(name)) {
            this.whitelist.add(name);
        }
        this.context.getPluginSettings().getFileConfiguration().set("whitelist", this.whitelist);
        this.context.getPlugin().saveConfig();
    }

    public void removeFromWhitelist(final String name) {
        if (this.isWhitelisted(name)) {
            this.whitelist.remove(name);
        }
        if (this.isLoggedIn(name)) {
            this.logUserOut(name);
        }
        this.context.getPluginSettings().getFileConfiguration().set("whitelist", this.whitelist);
        this.context.getPlugin().saveConfig();
    }

    public List<String> getConsoleViewList() {
        return this.consoleViewPermission;
    }

    public boolean canViewConsole(final String username) {
        return WebOpPlugin.PluginContext.getPlugin().getServer().getOfflinePlayer(username).isOp() || this.consoleViewPermission.contains(username);
    }

    public void addToConsoleViewWhitelist(final String name) {
        if (!this.canViewConsole(name)) {
            this.consoleViewPermission.add(name);
        }
        this.context.getPluginSettings().getFileConfiguration().set("consoleView", this.consoleViewPermission);
        this.context.getPlugin().saveConfig();
    }

    public void removeFromConsoleViewWhitelist(final String name) {
        if (this.canViewConsole(name)) {
            this.consoleViewPermission.remove(name);
        }
        this.context.getPluginSettings().getFileConfiguration().set("consoleView", this.consoleViewPermission);
        this.context.getPlugin().saveConfig();
    }

    public List<String> getConsoleUseList() {
        return this.consoleUsePermission;
    }

    public boolean canExecuteConsoleOpCommands(final String username) {
        return WebOpPlugin.PluginContext.getPlugin().getServer().getOfflinePlayer(username).isOp() || this.consoleUsePermission.contains(username);
    }

    public void addToConsoleOpWhitelist(final String name) {
        if (!this.canExecuteConsoleOpCommands(name)) {
            this.consoleUsePermission.add(name);
        }
        this.context.getPluginSettings().getFileConfiguration().set("consoleOp", this.consoleUsePermission);
        this.context.getPlugin().saveConfig();
    }

    public void removeFromConsoleOpWhitelist(final String name) {
        if (this.canExecuteConsoleOpCommands(name)) {
            this.consoleUsePermission.remove(name);
        }
        this.context.getPluginSettings().getFileConfiguration().set("consoleOp", this.consoleUsePermission);
        this.context.getPlugin().saveConfig();
    }

    private boolean isValidUserAndSession(final String username, final String session) {
        if (username.isEmpty() || session.isEmpty()) {
            return false;
        }
        for (final WebOpUser user : this.users) {
            if (user.getName().equals(username) && user.getSession().equals(session)) {
                if (user.isSessionExpired()) {
                    return false;
                }
                user.updateLastActivity();
                return true;
            }
        }
        return false;
    }

    public boolean isValidCookie(final HttpServletRequest req) {
        final Cookie[] cookies = req.getCookies();
        String username = "";
        String session = "";
        for (final Cookie cookie : cookies) {
            final String name = cookie.getName();
            switch (name) {
                case "webop_user": {
                    username = cookie.getValue();
                    break;
                }
                case "webop_session": {
                    session = cookie.getValue();
                    break;
                }
            }
        }
        return this.isValidUserAndSession(username, session);
    }

    public String getUsername(final HttpServletRequest req) {
        final Cookie[] arr$;
        final Cookie[] cookies = arr$ = req.getCookies();
        for (final Cookie cookie : arr$) {
            if (cookie.getName().equals("webop_user")) {
                return cookie.getValue();
            }
        }
        return "";
    }

    public boolean isValidWebSocketConnection(final String message) {
        final Map<String, String> params = this.parseWebSocketRequest(message);
        final String userParam = params.get("webop_user");
        final String sessionParam = params.get("webop_session");
        return this.isValidUserAndSession(userParam, sessionParam);
    }

    public boolean isValidWebSocketConnection(final Session session) {
        final List<HttpCookie> cookies = session.getUpgradeRequest().getCookies();
        String socketUser = "";
        String socketSession = "";
        for (final HttpCookie cookie : cookies) {
            final String name = cookie.getName();
            switch (name) {
                case "webop_user": {
                    socketUser = cookie.getValue();
                    continue;
                }
                case "webop_session": {
                    socketSession = cookie.getValue();
                }
            }
        }
        return this.isValidUserAndSession(socketUser, socketSession);
    }

    public Map<String, String> parseWebSocketRequest(final String query) {
        final Map<String, String> results = new HashMap<String, String>();
        final String[] arr$;
        final String[] pairs = arr$ = query.split("&");
        for (final String pair : arr$) {
            final String[] param = pair.split("=");
            if (param.length == 2) {
                try {
                    results.put(param[0], URLDecoder.decode(param[1], "UTF-8"));
                } catch (UnsupportedEncodingException ex) {
                    results.put(param[0], "");
                }
            } else {
                results.put(param[0], "");
            }
        }
        return results;
    }

    public void addSession(final Session session) {
        if (!this.websocketSessions.contains(session)) {
            this.websocketSessions.add(session);
        }
    }

    public void removeSession(final Session session) {
        this.websocketSessions.remove(session);
    }

    public List<Session> getSessions() {
        return this.websocketSessions;
    }

    public WebOpUser getUser(final String name) {
        for (final WebOpUser user : this.users) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        return null;
    }

    public List<WebOpUser> getLoggedInUsers() {
        return this.users;
    }

    public void logUserIn(final WebOpUser user) {
        if (!this.users.contains(user)) {
            this.users.add(user);
        }
        for (final WebOpUser person : this.users) {
            if (person.getWebSocketSession() != null) {
                if (!person.getWebSocketSession().isOpen()) {
                    continue;
                }
                try {
                    person.getWebSocketSession().getRemote().sendString("case=activityNotification;action=login;user=" + user.getName());
                } catch (IOException ignored) {
                }
            }
        }
    }

    public boolean isLoggedIn(final String username) {
        for (final WebOpUser user : this.users) {
            if (user.getName().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public void logUserOut(final String username) {
        final Iterator<WebOpUser> iterator = this.users.iterator();
        while (iterator.hasNext()) {
            final WebOpUser user = iterator.next();
            if (user.getName().equals(username)) {
                if (user.getWebSocketSession() != null) {
                    this.websocketSessions.remove(user.getWebSocketSession());
                    user.getWebSocketSession().close();
                }
                for (final WebOpUser person : this.users) {
                    if (person.getWebSocketSession() != null) {
                        if (!person.getWebSocketSession().isOpen()) {
                            continue;
                        }
                        try {
                            person.getWebSocketSession().getRemote().sendString("case=activityNotification;action=logout;user=" + user.getName());
                        } catch (IOException ignored) {
                        }
                    }
                }
                iterator.remove();
            }
        }
    }

    private String generateSalt() {
        final SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    private String hexEncode(final byte[] aInput) {
        final StringBuilder result = new StringBuilder();
        final char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        for (int idx = 0; idx < aInput.length; ++idx) {
            final byte b = aInput[idx];
            result.append(digits[(b & 0xF0) >> 4]);
            result.append(digits[b & 0xF]);
        }
        return result.toString();
    }

    public String generateSession(final String str) {
        try {
            final MessageDigest sha = MessageDigest.getInstance("SHA-1");
            final String hash = str + this.generateSalt();
            final byte[] hashOne = sha.digest(hash.getBytes());
            return this.hexEncode(hashOne);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(SessionManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public enum ListType {
        Whitelist,
        ConsoleView,
        ConsoleAsOp
    }
}
