package me.jayfella.webop2.WebPages;

import com.sun.net.httpserver.HttpExchange;
import me.jayfella.webop2.Core.WebPage;
import me.jayfella.webop2.PluginContext;

public class Page_PlayerInformation extends WebPage
{
    public Page_PlayerInformation(PluginContext context)
    {
        super(context);
    }

    @Override public String contentType() { return "text/plain; charset=utf-8"; }

    @Override
    public byte[] get(HttpExchange he)
    {
        if (!this.getContext().getSessionManager().isAuthorised(he))
            return new byte[0];

        String pageResponse = this.getContext().getPlayerMonitor().generatePlayerString();
        return pageResponse.toString().getBytes();
    }

    @Override
    public byte[] post(HttpExchange he)
    {
        this.setResponseCode(403);
        return new byte[0];
    }

}
