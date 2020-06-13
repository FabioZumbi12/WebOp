// 
// Decompiled by Procyon v0.5.36
// 

package me.jayfella.webop.website.pages;

import me.jayfella.webop.WebOpPlugin;
import me.jayfella.webop.core.SessionManager;
import me.jayfella.webop.website.WebPage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Permissions extends WebPage {
    public Permissions() {
        this.setResponseCode(200);
        this.setContentType("text/html; charset=utf-8");
    }

    @Override
    public byte[] get(final HttpServletRequest req, final HttpServletResponse resp) {
        if (!WebOpPlugin.PluginContext.getSessionManager().isValidCookie(req)) {
            try {
                resp.sendRedirect("login.php");
            } catch (IOException ignored) {
            }
            return new byte[0];
        }
        String page = this.loadResource("html", "permissions.html");
        page = page.replace("{accessWhitelist_users}", this.generateAccessListHtml());
        page = page.replace("{consoleViewWhitelist_users}", this.generateConsoleViewListHtml());
        page = page.replace("{consoleAsOpWhitelist_users}", this.generateConsoleUseListHtml());
        page = this.addSiteTemplate(page, "[WebOp] Permissions", req);
        return page.getBytes();
    }

    @Override
    public byte[] post(final HttpServletRequest req, final HttpServletResponse resp) {
        if (!WebOpPlugin.PluginContext.getSessionManager().isValidCookie(req)) {
            return new byte[0];
        }
        final String httpUser = WebOpPlugin.PluginContext.getSessionManager().getUsername(req);
        if (httpUser.isEmpty() || !WebOpPlugin.PluginContext.getPlugin().getServer().getOfflinePlayer(httpUser).isOp()) {
            return "OP permission required".getBytes();
        }
        final String caseParam = req.getParameter("case");
        if (caseParam == null || caseParam.isEmpty()) {
            return "insufficient data".getBytes();
        }
        final String s = caseParam;
        switch (s) {
            case "webopaccess": {
                final String postAction = req.getParameter("action");
                final String postPlayers = req.getParameter("players");
                if (postAction == null || postAction.isEmpty() || postPlayers == null || postPlayers.isEmpty()) {
                    return "insufficient data".getBytes();
                }
                final String s2 = postAction;
                switch (s2) {
                    case "add": {
                        this.processList(postPlayers, SessionManager.ListType.Whitelist, true);
                        break;
                    }
                    case "remove": {
                        this.processList(postPlayers, SessionManager.ListType.Whitelist, false);
                        break;
                    }
                    default: {
                        return new byte[0];
                    }
                }
                return this.generateAccessListHtml().getBytes();
            }
            case "consoleView": {
                final String postAction = req.getParameter("action");
                final String postPlayers = req.getParameter("players");
                if (postAction == null || postAction.isEmpty() || postPlayers == null || postPlayers.isEmpty()) {
                    return "insufficient data".getBytes();
                }
                final String s3 = postAction;
                switch (s3) {
                    case "add": {
                        this.processList(postPlayers, SessionManager.ListType.ConsoleView, true);
                        break;
                    }
                    case "remove": {
                        this.processList(postPlayers, SessionManager.ListType.ConsoleView, false);
                        break;
                    }
                    default: {
                        return new byte[0];
                    }
                }
                return this.generateConsoleViewListHtml().getBytes();
            }
            case "consoleAsOp": {
                final String postAction = req.getParameter("action");
                final String postPlayers = req.getParameter("players");
                if (postAction == null || postAction.isEmpty() || postPlayers == null || postPlayers.isEmpty()) {
                    return "insufficient data".getBytes();
                }
                final String s4 = postAction;
                switch (s4) {
                    case "add": {
                        this.processList(postPlayers, SessionManager.ListType.ConsoleAsOp, true);
                        break;
                    }
                    case "remove": {
                        this.processList(postPlayers, SessionManager.ListType.ConsoleAsOp, false);
                        break;
                    }
                    default: {
                        return new byte[0];
                    }
                }
                return this.generateConsoleUseListHtml().getBytes();
            }
            default: {
                return new byte[0];
            }
        }
    }

    private void processList(final String playernames, final SessionManager.ListType type, final boolean add) {
        final List<String> playersToProcess = new ArrayList<String>();
        final String[] dirtyPlayers = playernames.split(",");
        for (int i = 0; i < dirtyPlayers.length; ++i) {
            dirtyPlayers[i] = dirtyPlayers[i].trim();
            if (!dirtyPlayers[i].isEmpty()) {
                playersToProcess.add(dirtyPlayers[i]);
            }
        }
        switch (type) {
            case Whitelist: {
                for (final String player : playersToProcess) {
                    if (add) {
                        WebOpPlugin.PluginContext.getSessionManager().addToWhitelist(player);
                    } else {
                        WebOpPlugin.PluginContext.getSessionManager().removeFromWhitelist(player);
                    }
                }
                break;
            }
            case ConsoleView: {
                for (final String player : playersToProcess) {
                    if (add) {
                        WebOpPlugin.PluginContext.getSessionManager().addToConsoleViewWhitelist(player);
                    } else {
                        WebOpPlugin.PluginContext.getSessionManager().removeFromConsoleViewWhitelist(player);
                    }
                }
                break;
            }
            case ConsoleAsOp: {
                for (final String player : playersToProcess) {
                    if (add) {
                        WebOpPlugin.PluginContext.getSessionManager().addToConsoleOpWhitelist(player);
                    } else {
                        WebOpPlugin.PluginContext.getSessionManager().removeFromConsoleOpWhitelist(player);
                    }
                }
                break;
            }
        }
    }

    private String generateAccessListHtml() {
        final StringBuilder whitelistUsers = new StringBuilder();
        for (final String user : WebOpPlugin.PluginContext.getSessionManager().getWhitelist()) {
            whitelistUsers.append("<li class='ui-widget-content'>").append(user).append("</li>").append("\n");
        }
        return whitelistUsers.toString();
    }

    private String generateConsoleViewListHtml() {
        final StringBuilder consoleViewUsers = new StringBuilder();
        for (final String user : WebOpPlugin.PluginContext.getSessionManager().getConsoleViewList()) {
            consoleViewUsers.append("<li class='ui-widget-content'>").append(user).append("</li>").append("\n");
        }
        return consoleViewUsers.toString();
    }

    private String generateConsoleUseListHtml() {
        final StringBuilder consoleAsOpUsers = new StringBuilder();
        for (final String user : WebOpPlugin.PluginContext.getSessionManager().getConsoleUseList()) {
            consoleAsOpUsers.append("<li class='ui-widget-content'>").append(user).append("</li>").append("\n");
        }
        return consoleAsOpUsers.toString();
    }
}
