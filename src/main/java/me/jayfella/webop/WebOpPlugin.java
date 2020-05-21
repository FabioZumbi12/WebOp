// 
// Decompiled by Procyon v0.5.36
// 

package me.jayfella.webop;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class WebOpPlugin extends JavaPlugin {
    public static PluginContext PluginContext;

    public void onEnable() {
        WebOpPlugin.PluginContext = new PluginContext(this);
        getCommand("WebOp").setExecutor(new Commands());
    }

    public void onDisable() {
        WebOpPlugin.PluginContext.stopServer();
    }

    class Commands implements CommandExecutor {

        @Override
        public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    reloadPlugin();
                    sender.sendMessage(ChatColor.AQUA + "[WebOp] WebOp reloaded!");
                    return true;
                }
            }
            return true;
        }
    }

    private void reloadPlugin() {
        // Disable
        PluginContext.stopServer();
        getServer().getScheduler().cancelTasks(this);

        // Enable
        PluginContext = new PluginContext(this);
    }
}
