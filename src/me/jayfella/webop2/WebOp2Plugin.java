
package me.jayfella.webop2;

import org.bukkit.plugin.java.JavaPlugin;

public class WebOp2Plugin extends JavaPlugin
{
    public static final int maxHistoryLength = 3600;
    private PluginContext context;

    @Override
    public void onEnable()
    {
        this.context = new PluginContext(this);
    }

    @Override
    public void onDisable()
    {
        try
        {
            this.context.getRequestHandler().close();
            this.context.getPluginProfiler().close();
        }
        catch (Exception ex)
        {
            
        }
    }
}
