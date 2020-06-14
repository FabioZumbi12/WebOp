/*
 * Copyright (c) 2020 - @FabioZumbi12
 * Last Modified: 14/06/2020 00:14.
 *
 * This class is provided 'as-is', without any express or implied warranty. In no event will the authors be held liable for any
 *  damages arising from the use of this class.
 *
 * Permission is granted to anyone to use this class for any purpose, including commercial plugins, and to alter it and
 * redistribute it freely, subject to the following restrictions:
 * 1 - The origin of this class must not be misrepresented; you must not claim that you wrote the original software. If you
 * use this class in other plugins, an acknowledgment in the plugin documentation would be appreciated but is not required.
 * 2 - Altered source versions must be plainly marked as such, and must not be misrepresented as being the original class.
 * 3 - This notice may not be removed or altered from any source distribution.
 *
 * Esta classe é fornecida "como está", sem qualquer garantia expressa ou implícita. Em nenhum caso os autores serão
 * responsabilizados por quaisquer danos decorrentes do uso desta classe.
 *
 * É concedida permissão a qualquer pessoa para usar esta classe para qualquer finalidade, incluindo plugins pagos, e para
 * alterá-lo e redistribuí-lo livremente, sujeito às seguintes restrições:
 * 1 - A origem desta classe não deve ser deturpada; você não deve afirmar que escreveu a classe original. Se você usar esta
 *  classe em um plugin, uma confirmação de autoria na documentação do plugin será apreciada, mas não é necessária.
 * 2 - Versões de origem alteradas devem ser claramente marcadas como tal e não devem ser deturpadas como sendo a
 * classe original.
 * 3 - Este aviso não pode ser removido ou alterado de qualquer distribuição de origem.
 */

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
            for (final String player : getSubscribers()) {
                final WebOpUser user = WebOpPlugin.PluginContext.getSessionManager().getUser(player);
                if (user != null) {
                    if (user.getWebSocketSession() != null) {
                        if (user.getWebSocketSession().isOpen()) {
                            try {
                                user.getWebSocketSession().getRemote().sendString("case=worldData;" + getWorldDetails(world));
                            } catch (IOException ignored) {
                            }
                        }
                    }
                }
            }
        }

        @EventHandler
        public void onWeatherChange(final WeatherChangeEvent event) {
            WebOpPlugin.PluginContext.getPlugin().getServer().getScheduler().runTaskLaterAsynchronously(WebOpPlugin.PluginContext.getPlugin(), () -> updateSubscribers(event.getWorld()), 20L);
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
                    updateSubscribers(event.getPlayer().getWorld());
                }
            }, 20L);
        }

        @EventHandler
        public void onPlayerQuit(final PlayerQuitEvent event) {
            final World world = event.getPlayer().getWorld();
            WebOpPlugin.PluginContext.getPlugin().getServer().getScheduler().runTaskLaterAsynchronously(WebOpPlugin.PluginContext.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    updateSubscribers(world);
                }
            }, 20L);
        }
    }
}
