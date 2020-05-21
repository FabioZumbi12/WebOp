// 
// Decompiled by Procyon v0.5.36
// 

package me.jayfella.webop;

import org.bukkit.plugin.java.JavaPlugin;

public class WebOpPlugin extends JavaPlugin {
    public static PluginContext PluginContext;

    public void onEnable() {
        WebOpPlugin.PluginContext = new PluginContext(this);
    }

    public void onDisable() {
        WebOpPlugin.PluginContext.stopServer();
    }
}
