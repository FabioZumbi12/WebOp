
package me.jayfella.webop2.WebPages;

import com.sun.net.httpserver.HttpExchange;
import java.util.Map;
import me.jayfella.webop2.Core.LoggedInUser;
import me.jayfella.webop2.Core.WebPage;
import me.jayfella.webop2.PluginContext;

public class Page_Console extends WebPage
{
    public Page_Console(PluginContext context)
    {
        super(context);
    }

    @Override public String contentType() { return "text/html; charset=utf-8"; }

    @Override
    public byte[] get(HttpExchange he)
    {
        if (!this.getContext().getSessionManager().isAuthorised(he))
            return new byte[0];

        LoggedInUser user = this.getContext().getSessionManager().getLoggedInUser(he.getRemoteAddress().getAddress());

        if (!this.getContext().getPlugin().getServer().getOfflinePlayer(user.getUsername()).isOp())
        {
            if (!this.getContext().getSessionManager().canViewConsole(user.getUsername()))
            {
                return "NOT_ALLOWED".getBytes();
            }
        }

        String response = user.getConsoleLines();
        return response.getBytes();
    }

    @Override
    public byte[] post(HttpExchange he)
    {
        if (!this.getContext().getSessionManager().isAuthorised(he))
            return new byte[0];

        Map<String, String> vars = this.parsePostResponse(he);
        LoggedInUser user = this.getContext().getSessionManager().getLoggedInUser(he.getRemoteAddress().getAddress());

        String command = vars.get("command");
        boolean asConsole = Boolean.valueOf(vars.get("asConsole"));

        this.getContext().getConsoleMonitor().executeCommand(command, asConsole, user.getUsername());

        return "OK".getBytes();
    }
}
