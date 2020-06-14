/*
 * Copyright (c) 2020 - @FabioZumbi12
 * Last Modified: 14/06/2020 00:14.
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

package me.jayfella.webop.website.pages;

import me.jayfella.webop.WebOpPlugin;
import me.jayfella.webop.core.SessionManager;
import me.jayfella.webop.website.WebPage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

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
        page = page.replace("{serverWhitelist_users}", this.generateServerWListHtml());
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
        switch (caseParam) {
            case "serverWhitelist": {
                final String postAction = req.getParameter("action");
                final String postPlayers = req.getParameter("players");
                if (postAction == null || postAction.isEmpty() || postPlayers == null || postPlayers.isEmpty()) {
                    return "insufficient data".getBytes();
                }
                switch (postAction) {
                    case "add": {
                        this.processList(postPlayers, SessionManager.ListType.ServerWhitelist, true);
                        break;
                    }
                    case "remove": {
                        this.processList(postPlayers, SessionManager.ListType.ServerWhitelist, false);
                        break;
                    }
                    default: {
                        return new byte[0];
                    }
                }
                return this.generateAccessListHtml().getBytes();
            }
            case "webopaccess": {
                final String postAction = req.getParameter("action");
                final String postPlayers = req.getParameter("players");
                if (postAction == null || postAction.isEmpty() || postPlayers == null || postPlayers.isEmpty()) {
                    return "insufficient data".getBytes();
                }
                switch (postAction) {
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
                switch (postAction) {
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
                switch (postAction) {
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

    private String generateServerWListHtml() {
        final StringBuilder serverWhitelistUsers = new StringBuilder();
        for (final OfflinePlayer user : Bukkit.getServer().getWhitelistedPlayers()) {
            serverWhitelistUsers.append("<li class='ui-widget-content'>").append(user.getName()).append("</li>").append("\n");
        }
        return serverWhitelistUsers.toString();
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
