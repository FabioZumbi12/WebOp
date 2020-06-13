// 
// Decompiled by Procyon v0.5.36
// 

package me.jayfella.webop.website.pages;

import me.jayfella.webop.WebOpPlugin;
import me.jayfella.webop.website.WebPage;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Index extends WebPage {
    public Index() {
        this.setResponseCode(200);
        this.setContentType("text/html; charset=utf-8");
    }

    @Override
    public byte[] get(final HttpServletRequest req, final HttpServletResponse resp) {
        if (!WebOpPlugin.PluginContext.getSessionManager().isValidCookie(req)) {
            try {
                resp.sendRedirect("login.php");
            } catch (IOException ignored) {
            }
            return new byte[0];
        }
        String page = this.loadResource("html", "index.html");
        page = this.addSiteTemplate(page, "[WebOp] Home Page", req);
        final int pluginCount = WebOpPlugin.PluginContext.getPlugin().getServer().getPluginManager().getPlugins().length;
        final StringBuilder pluginsSb = new StringBuilder();
        for (int i = 0; i < pluginCount; ++i) {
            final Plugin pl = WebOpPlugin.PluginContext.getPlugin().getServer().getPluginManager().getPlugins()[i];
            pluginsSb.append(pl.isEnabled() ? "<span style='color: #0d8022' " : "<span style='color: #800d0d' ").append("title='Version: ").append(pl.getDescription().getVersion()).append("'>").append(pl.getName()).append("</span>");
            if (i != pluginCount - 1) {
                pluginsSb.append(", ");
            }
        }
        final String arch = System.getProperty("os.arch").contains("64") ? "64bit" : "2bit";
        page = page.replace("{java_version}", System.getProperty("java.version") + " " + arch + " on " + System.getProperty("os.name")).replace("{bukkit_version}", WebOpPlugin.PluginContext.getPlugin().getServer().getVersion()).replace("{plugin_count}", Integer.toString(pluginCount)).replace("{plugin_list}", pluginsSb.toString());
        page = ((WebOpPlugin.PluginContext.getPlugin().getServer().getPluginManager().getPlugin("LogBlock") != null) ? page.replace("{logblock_plugin}", this.loadResource("html", "logblock.html")) : page.replace("{logblock_plugin}", ""));
        final String httpUser = WebOpPlugin.PluginContext.getSessionManager().getUsername(req);
        final boolean isOp = WebOpPlugin.PluginContext.getPlugin().getServer().getOfflinePlayer(httpUser).isOp();
        page = (isOp ? page.replace("{server_profiler}", this.loadResource("html", "serverprofiler.html")) : page.replace("{server_profiler}", ""));
        final StringBuilder worldsData = new StringBuilder();
        final World[] worlds = WebOpPlugin.PluginContext.getPlugin().getServer().getWorlds().toArray(new World[WebOpPlugin.PluginContext.getPlugin().getServer().getWorlds().size()]);
        for (int j = 0; j < worlds.length; ++j) {
            worldsData.append("<span class='worldData' id='").append(worlds[j].getName()).append("'>").append(worlds[j].getName()).append("</span>");
            if (j < worlds.length - 1) {
                worldsData.append(", ");
            }
        }
        page = page.replace("{world_data}", worldsData.toString()).replace("{world_count}", Integer.toString(worlds.length));
        page = (WebOpPlugin.PluginContext.getPlayerMonitor().essentialsExists() ? page.replace("{essentials_playerdata}", "<div id='essPlayerData' style='display: none; border-radius: 3px; background: #ffffff; border: 1px solid black; padding: 10px; box-shadow: 0px 0px 7px black; position: fixed; top: 10px; right: 10px;'></div>") : page.replace("{essentials_playerdata}", ""));
        return page.getBytes();
    }

    @Override
    public byte[] post(final HttpServletRequest req, final HttpServletResponse resp) {
        return new byte[0];
    }
}
