package me.jayfella.webop2.WebPages;

import com.sun.net.httpserver.HttpExchange;
import me.jayfella.webop2.Core.WebPage;
import me.jayfella.webop2.PluginContext;

public class Page_ProfileServer extends WebPage
{
    public Page_ProfileServer(PluginContext context)
    {
        super(context);
    }

    @Override public String contentType() { return "text/html; charset=utf-8"; }

    @Override
    public byte[] get(HttpExchange he)
    {
        if (!this.getContext().getSessionManager().isAuthorised(he))
        {
            return new byte[0];
        }

        this.setPageTitle("[WebOp] Profile Server");

        this.setPageBody(this.loadHtml("profileserver.html"));

        return this.getPageOutput(he);
    }

    @Override
    public byte[] post(HttpExchange he)
    {
        return new byte[0];
    }
}
