/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.jayfella.webop2.WebPages;

import com.sun.net.httpserver.HttpExchange;
import java.util.ArrayList;
import java.util.List;
import me.jayfella.webop2.Core.WebPage;
import me.jayfella.webop2.PluginContext;
import org.bukkit.plugin.Plugin;

public class Page_Index extends WebPage
{
    public Page_Index(PluginContext context)
    {
        super(context);
    }

    @Override public String contentType() { return "text/html; charset=utf-8"; }

    @Override
    public byte[] get(HttpExchange he)
    {
        if (!this.getContext().getSessionManager().isAuthorised(he))
        {
            this.setResponseCode(301);
            he.getResponseHeaders().add("Location", "login.php");
            return new byte[0];
        }

        this.setPageTitle("[WebOp] Index");

        List<String> includes = new ArrayList<>();

        includes.add("<script type='text/javascript' src='jscript.php?jquery.flot.js'></script>");
        this.setHeaderIncludes(includes);


        int pluginCount = this.getContext().getPlugin().getServer().getPluginManager().getPlugins().length;
        StringBuilder runningPlugins = new StringBuilder();

        for (int i = 0; i < pluginCount; i++)
        {
            Plugin pl = this.getContext().getPlugin().getServer().getPluginManager().getPlugins()[i];


            runningPlugins
                    .append((pl.isEnabled()) ? "<span style='color: #0d8022' " : "<span style='color: #800d0d' ")
                    .append("title='Version: ").append(pl.getDescription().getVersion()).append("'>")
                    .append(pl.getName())
                    .append("</span>");

            if (i != pluginCount - 1)
            {
                runningPlugins.append(", ");
            }
        }



        String pageBody = this.loadHtml("index.html")
                .replace("{javaversion}", System.getProperty("java.version"))
                .replace("{bukkitversion}", this.getContext().getPlugin().getServer().getVersion())
                .replace("{plugincount}", Integer.toString(pluginCount))
                .replace("{pluginlist}", runningPlugins.toString());

        this.setPageBody(pageBody);


        return this.getPageOutput(he);
    }

    @Override
    public byte[] post(HttpExchange he)
    {
        return new byte[0];
    }

}
