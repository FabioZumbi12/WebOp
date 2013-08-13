
package me.jayfella.webop2.WebPages;

import com.sun.net.httpserver.HttpExchange;
import me.jayfella.webop2.Core.LoggedInUser;
import me.jayfella.webop2.Core.WebPage;
import me.jayfella.webop2.PluginContext;

public class Page_Logout extends WebPage
{
    public Page_Logout(PluginContext context)
    {
        super(context);
    }

    @Override public String contentType() { return "text/html; charset=utf-8"; }

    @Override
    public byte[] get(HttpExchange he)
    {
        LoggedInUser user = this.getContext().getSessionManager().getLoggedInUser(he.getRemoteAddress().getAddress());

        if (user != null)
        {
            this.getContext().getSessionManager().logUserOut(user.getUsername());
        }

        this.setResponseCode(301);
        he.getResponseHeaders().add("Location", "login.php");
        return new byte[0];
    }

    @Override
    public byte[] post(HttpExchange he)
    {
        return new byte[0];
    }

}
