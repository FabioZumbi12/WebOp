package me.jayfella.webop2.WebPages;

import com.sun.net.httpserver.HttpExchange;
import me.jayfella.webop2.Core.WebPage;
import me.jayfella.webop2.PluginContext;

public class Page_404 extends WebPage
{
    public Page_404(PluginContext context)
    {
        super(context);
    }

    @Override public String contentType() { return "text/html; charset=utf-8"; }

    @Override
    public byte[] get(HttpExchange he)
    {
        this.setResponseCode(404);

        this.setPageBody("<div class='container'>Error 404 - Page Not Found.</div>");

        return this.getPageOutput(he);
    }

    @Override
    public byte[] post(HttpExchange he)
    {
        this.setResponseCode(404);

        this.setPageBody("<div class='container'>Error 404 - Page Not Found.</div>");

        return this.getPageOutput(he);
    }

}
