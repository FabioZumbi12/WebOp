package me.jayfella.webop2.WebPages;

import com.sun.net.httpserver.HttpExchange;
import me.jayfella.webop2.Core.WebPage;
import me.jayfella.webop2.PluginContext;

public class Page_StyleSheet extends WebPage
{
    public Page_StyleSheet(PluginContext context)
    {
        super(context);
    }

    @Override public String contentType() { return "text/css; charset=utf-8"; }

    @Override
    public byte[] get(HttpExchange he)
    {
        String query = he.getRequestURI().getQuery();
        return this.loadCss(query).getBytes();
    }

    @Override
    public byte[] post(HttpExchange he)
    {
        return new byte[0];
    }
    
}
