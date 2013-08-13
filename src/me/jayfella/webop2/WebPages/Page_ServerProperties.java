package me.jayfella.webop2.WebPages;

import com.sun.net.httpserver.HttpExchange;
import me.jayfella.webop2.Core.WebPage;
import me.jayfella.webop2.PluginContext;

public class Page_ServerProperties extends WebPage
{
    public Page_ServerProperties(PluginContext context)
    {
        super(context);
    }

    @Override public String contentType() { return "text/html; charset=utf-8"; }

    @Override
    public byte[] get(HttpExchange he)
    {
        this.setPageTitle("[WebOp] Server Properties");
        this.setPageBody(this.loadHtml("serverproperties.html"));

        return this.getPageOutput(he);

        /* TODO:
         * Banned Players
         * banned IP's
         * Opped players
         *
         *

        */

        // this.getContext().getPlugin().getServer().
    }

    @Override
    public byte[] post(HttpExchange he)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
