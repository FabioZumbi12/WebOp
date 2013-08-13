package me.jayfella.webop2.WebPages;

import com.sun.net.httpserver.HttpExchange;
import me.jayfella.webop2.Core.WebPage;
import me.jayfella.webop2.PluginContext;

public class Page_JScript extends WebPage
{
    public Page_JScript(PluginContext context)
    {
        super(context);
    }

    @Override public String contentType() { return "text/javascript; charset=utf-8"; }

    @Override
    public byte[] get(HttpExchange he)
    {
        String query = he.getRequestURI().getQuery();

        if (query.contains("&"))
        {
            query = query.substring(0, query.indexOf("&"));
        }

        return this.loadJscript(query).getBytes();
    }

    @Override
    public byte[] post(HttpExchange he)
    {
        return new byte[0];
    }

}