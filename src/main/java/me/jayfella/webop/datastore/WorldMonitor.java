// 
// Decompiled by Procyon v0.5.36
// 

package me.jayfella.webop.datastore;

import me.jayfella.webop.PluginContext;
import me.jayfella.webop.WebOpPlugin;
import me.jayfella.webop.core.SocketSubscription;
import me.jayfella.webop.core.WebOpUser;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.io.IOException;

public class WorldMonitor extends SocketSubscription {

    public WorldMonitor(final PluginContext context) {
        context.getPlugin().getServer().getPluginManager().registerEvents(new WorldEventMonitor(), context.getPlugin());
    }

    public String getWorldDetails(final World world) {
        return "name=" + world.getName() + ";" + "playercount=" + world.getPlayers().size() + ";" + "type=" + world.getEnvironment().name() + ";" + "difficulty=" + world.getDifficulty().name() + ";" + "israining=" + world.hasStorm() + ";" + "isthundering=" + world.isThundering();
    }

    private class WorldEventMonitor implements Listener {
        private void updateSubscribers(final World world) {
            for (final String player : WorldMonitor.this.getSubscribers()) {
                final WebOpUser user = WebOpPlugin.PluginContext.getSessionManager().getUser(player);
                if (user != null) {
                    if (user.getWebSocketSession() != null) {
                        if (user.getWebSocketSession().isOpen()) {
                            try {
                                user.getWebSocketSession().getRemote().sendString("case=worldData;" + WorldMonitor.this.getWorldDetails(world));
                            } catch (IOException ignored) {
                            }
                        }
                    }
                }
            }
        }

        @EventHandler
        public void onWeatherChange(final WeatherChangeEvent event) {
            WebOpPlugin.PluginContext.getPlugin().getServer().getScheduler().runTaskLaterAsynchronously(WebOpPlugin.PluginContext.getPlugin(), () -> WorldEventMonitor.this.updateSubscribers(event.getWorld()), 20L);
        }

        @EventHandler
        public void onPlayerChangedWorld(final PlayerChangedWorldEvent event) {
            this.updateSubscribers(event.getFrom());
            this.updateSubscribers(event.getPlayer().getWorld());
        }

        @EventHandler
        public void onPlayerJoin(final PlayerJoinEvent event) {
            WebOpPlugin.PluginContext.getPlugin().getServer().getScheduler().runTaskLaterAsynchronously(WebOpPlugin.PluginContext.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    WorldEventMonitor.this.updateSubscribers(event.getPlayer().getWorld());
                }
            }, 20L);
        }

        @EventHandler
        public void onPlayerQuit(final PlayerQuitEvent event) {
            final World world = event.getPlayer().getWorld();
            WebOpPlugin.PluginContext.getPlugin().getServer().getScheduler().runTaskLaterAsynchronously(WebOpPlugin.PluginContext.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    WorldEventMonitor.this.updateSubscribers(world);
                }
            }, 20L);
        }
    }
}
