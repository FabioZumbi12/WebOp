package me.jayfella.webop2.WebPages;

import com.sun.net.httpserver.HttpExchange;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import me.jayfella.webop2.Core.LoggedInUser;
import me.jayfella.webop2.Core.WebPage;
import me.jayfella.webop2.PluginContext;
import org.bukkit.OfflinePlayer;

public class Page_Whitelist extends WebPage
{
    public Page_Whitelist(PluginContext context)
    {
        super(context);
    }

    @Override public String contentType() { return "text/html; charset=utf-8"; }

    @Override
    public byte[] get(HttpExchange he)
    {
        if (!this.getContext().getSessionManager().isAuthorised(he))
            return new byte[0];

        this.setPageTitle("[WebOp] Whitelist");

        String response = this.loadHtml("whitelist.html");

        // whitelist mode button
        response = (Boolean.valueOf(this.getContext().getServerPropertiesHandler().getValue("white-list")))
            ? response.replace("{serverwhitelistboolean}", "<button id='whitelistModeButton' class='smallRedButton'>ON - Only whitelisted players can join</button>")
            : response.replace("{serverwhitelistboolean}", "<button id='whitelistModeButton' class='smallGreenButton'>OFF - Anybody can join</button>");

        // server whitelisted players
        response = response.replace("{serverWhitelistedPlayers}", buildServerWhitelistHtml());

        // webop whitelisted players
        response = response.replace("{WebOpWhitelistedUsers}", buildWebOpWhitelistHtml());

        // webop consoleView whitelist
        response = response.replace("{consoleViewWhitelistedUsers}", buildWebOpConsoleViewWhitelistHtml());

        response = response.replace("{consoleOpWhitelistedUsers}", buildWebOpConsoleOpWhitelistHtml());

        this.setPageBody(response);

        return this.getPageOutput(he);
    }

    @Override
    public byte[] post(HttpExchange he)
    {
        if (!this.getContext().getSessionManager().isAuthorised(he))
            return new byte[0];

        LoggedInUser user = this.getContext().getSessionManager().getLoggedInUser(he.getRemoteAddress().getAddress());
        OfflinePlayer loggedInPlayer = this.getContext().getPlugin().getServer().getOfflinePlayer(user.getUsername());

        if (!loggedInPlayer.isOp())
            return "<strong style='color: red;'>FAILED - OP PERMISSIONS REQUIRED</strong>".getBytes();

        Map<String, String> vars = this.parsePostResponse(he);

        // list-based settings
        String varAction = vars.get("action"); // add, delete, toggleWhitelistMode
        String varMode = vars.get("mode"); // enable, disable
        String varListType = vars.get("listType"); // server, webop, consoleView, consoleOp
        String varUsers = vars.get("users"); // player names

        // enable & disable whitelist
        switch (varAction)
        {
            case "toggleWhitelistMode":
            {
                boolean whitelistMode = varMode.equals("enable");
                this.getContext().getPlugin().getServer().setWhitelist(whitelistMode);
                this.getContext().getServerPropertiesHandler().setValue("white-list", String.valueOf(whitelistMode).toLowerCase());
                return String.valueOf(whitelistMode).getBytes();
            }
            case "add":
            {
                switch(varListType)
                {
                    case "server":
                    {
                        String[] whitelistedUsers = parseUsersString(varUsers);
                        OfflinePlayer[] offPlayers = this.getContext().getPlugin().getServer().getOfflinePlayers();

                        for (int i = 0; i < offPlayers.length; i++)
                        {
                            OfflinePlayer offP = offPlayers[i];

                            for (int x = 0; x < whitelistedUsers.length; x++)
                            {
                                if (offP.getName().equalsIgnoreCase(whitelistedUsers[x]))
                                    offP.setWhitelisted(true);
                            }
                        }

                        return this.buildServerWhitelistHtml().getBytes();
                    }
                    case "webop":
                    {
                        String[] newUsers = parseUsersString(varUsers);

                        for (int i = 0; i < newUsers.length; i++)
                        {
                            this.getContext().getSessionManager().addToWhitelist(newUsers[i]);
                        }

                        return this.buildWebOpWhitelistHtml().getBytes();
                    }
                    case "consoleView":
                    {
                        String[] newUsers = parseUsersString(varUsers);

                        for (int i = 0; i < newUsers.length; i++)
                        {
                            this.getContext().getSessionManager().addToConsoleViewWhitelist(newUsers[i]);
                        }

                        return this.buildWebOpConsoleViewWhitelistHtml().getBytes();
                    }
                    case "consoleOp":
                    {
                        String[] newUsers = parseUsersString(varUsers);

                        for (int i = 0; i < newUsers.length; i++)
                        {
                            this.getContext().getSessionManager().addToConsoleOpWhitelist(newUsers[i]);
                        }

                        return this.buildWebOpConsoleOpWhitelistHtml().getBytes();
                    }

                }
            }
            case "remove":
            {
                switch(varListType)
                {
                    case "server":
                    {
                        String[] whitelistedUsers = parseUsersString(varUsers);
                        OfflinePlayer[] offPlayers = this.getContext().getPlugin().getServer().getOfflinePlayers();

                        for (int i = 0; i < offPlayers.length; i++)
                        {
                            OfflinePlayer offP = offPlayers[i];

                            for (int x = 0; x < whitelistedUsers.length; x++)
                            {
                                if (offP.getName().equalsIgnoreCase(whitelistedUsers[x]))
                                    offP.setWhitelisted(false);
                            }
                        }

                        return this.buildServerWhitelistHtml().getBytes();
                    }
                    case "webop":
                    {
                        String[] removedUsers = parseUsersString(varUsers);
                        // OfflinePlayer[] offPlayers = this.getContext().getPlugin().getServer().getOfflinePlayers();

                        for (int i = 0; i < removedUsers.length; i++)
                        {
                            // OfflinePlayer offP = offPlayers[i];
                            if (!this.getContext().getSessionManager().isWhitelisted(removedUsers[i]))
                                continue;

                            this.getContext().getSessionManager().removeFromWhitelist(removedUsers[i]);
                        }

                        return this.buildWebOpWhitelistHtml().getBytes();
                    }
                    case "consoleView":
                    {
                        String[] removedUsers = parseUsersString(varUsers);

                        for (int i = 0; i < removedUsers.length; i++)
                        {
                            this.getContext().getSessionManager().removeFromConsoleViewWhitelist(removedUsers[i]);
                        }

                        return this.buildWebOpConsoleViewWhitelistHtml().getBytes();
                    }
                    case "consoleOp":
                    {
                        String[] removedUsers = parseUsersString(varUsers);

                        for (int i = 0; i < removedUsers.length; i++)
                        {
                            this.getContext().getSessionManager().removeFromConsoleOpWhitelist(removedUsers[i]);
                        }

                        return this.buildWebOpConsoleOpWhitelistHtml().getBytes();
                    }
                }
            }
        }

        return new byte[0];
    }

    private String buildServerWhitelistHtml()
    {
        StringBuilder whitelistedPlayersHtml = new StringBuilder();
        for (OfflinePlayer player : this.getContext().getPlugin().getServer().getWhitelistedPlayers())
        {
            whitelistedPlayersHtml
                    .append("<li class='ui-widget-content'>")
                    .append(player.getName())
                    .append("</li>");
        }

        return whitelistedPlayersHtml.toString();
    }

    private String buildWebOpWhitelistHtml()
    {
        StringBuilder whitelistedPlayersHtml = new StringBuilder();
        for (String player : this.getContext().getSessionManager().getWhitelistedPlayers())
        {
            whitelistedPlayersHtml
                    .append("<li class='ui-widget-content'>")
                    .append(player)
                    .append("</li>");
        }

        return whitelistedPlayersHtml.toString();
    }

    private String buildWebOpConsoleViewWhitelistHtml()
    {
        StringBuilder whitelistedPlayersHtml = new StringBuilder();
        for (String player : this.getContext().getSessionManager().getConsoleViewWhitelistedPlayers())
        {
            whitelistedPlayersHtml
                    .append("<li class='ui-widget-content'>")
                    .append(player)
                    .append("</li>");
        }

        return whitelistedPlayersHtml.toString();
    }

    private String buildWebOpConsoleOpWhitelistHtml()
    {
        StringBuilder whitelistedPlayersHtml = new StringBuilder();
        for (String player : this.getContext().getSessionManager().getConsoleOpWhitelistedPlayers())
        {
            whitelistedPlayersHtml
                    .append("<li class='ui-widget-content'>")
                    .append(player)
                    .append("</li>");
        }

        return whitelistedPlayersHtml.toString();
    }

    private String[] parseUsersString(String usersString)
    {
        String[] dirtyUsers = usersString.split(",");
        List<String> users = new ArrayList<>();

        for (int i = 0; i < dirtyUsers.length; i++)
        {
            dirtyUsers[i] = dirtyUsers[i].trim();

            if (dirtyUsers[i].isEmpty())
                continue;

            users.add(dirtyUsers[i]);
        }

        return users.toArray(new String[users.size()]);
    }

}
