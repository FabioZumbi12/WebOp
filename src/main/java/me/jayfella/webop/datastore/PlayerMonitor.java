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

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import me.jayfella.webop.PluginContext;
import me.jayfella.webop.WebOpPlugin;
import me.jayfella.webop.core.SocketSubscription;
import me.jayfella.webop.core.WebOpUser;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayerMonitor extends SocketSubscription {
    private final boolean essentialsExists;

    public PlayerMonitor(final PluginContext context) {
        context.getPlugin().getServer().getPluginManager().registerEvents(new PlayerMonitorEvents(), context.getPlugin());
        this.essentialsExists = (context.getPlugin().getServer().getPluginManager().getPlugin("Essentials") != null);
    }

    public boolean essentialsExists() {
        return this.essentialsExists;
    }

    public String generatePlayerString() {
        final int alltimeCount = WebOpPlugin.PluginContext.getPlugin().getServer().getOfflinePlayers().length;
        final Player[] onlinePlayers = WebOpPlugin.PluginContext.getPlugin().getServer().getOnlinePlayers().toArray(new Player[0]);
        final StringBuilder sb = new StringBuilder().append("case=allPlayersData;").append("ALLTIME=").append(NumberFormat.getIntegerInstance().format(alltimeCount)).append(";").append("ONLINENOW=").append(NumberFormat.getIntegerInstance().format(onlinePlayers.length)).append(";").append("MAXIMUM=").append(WebOpPlugin.PluginContext.getPlugin().getServer().getMaxPlayers()).append(";").append("PLAYERS=");
        for (int i = 0; i < onlinePlayers.length; ++i) {
            sb.append(onlinePlayers[i].getName());
            if (i < onlinePlayers.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public String findPlayers(final String partialName) {
        final StringBuilder results = new StringBuilder();
        int resultCount = 0;
        for (final OfflinePlayer offlinePlayer : WebOpPlugin.PluginContext.getPlugin().getServer().getOfflinePlayers()) {
            final String playerName = offlinePlayer.getName().toLowerCase();
            if (playerName.contains(partialName)) {
                results.append(offlinePlayer.isBanned() ? "<span style='color: darkred';>" : "<span style='color: darkgreen';>").append(offlinePlayer.getName()).append("</span>").append(",");
                ++resultCount;
            }
            if (resultCount == 50) {
                break;
            }
        }
        return results.toString();
    }

    public String generateEssentialsPlayerDataString(final String playername) {
        if (!this.essentialsExists) {
            return "";
        }
        final Essentials ess = (Essentials) WebOpPlugin.PluginContext.getPlugin().getServer().getPluginManager().getPlugin("Essentials");
        final User essUser = ess.getOfflineUser(playername);
        final SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss");
        final DecimalFormat balanceFormatter = new DecimalFormat("###,###.##");
        final StringBuilder response = new StringBuilder().append("<span style='color: darkorange; font-weight: bold;'>").append(playername).append("</span><br/><br/>").append("<strong>Balance:</strong> ").append(balanceFormatter.format(essUser.getMoney().setScale(2, RoundingMode.HALF_EVEN))).append("<br/>").append("<strong>Last Login:</strong> ").append(df.format(new Date(essUser.getLastLogin()))).append("<br/>").append("<br/>").append("<Strong>Flying:</strong> ").append(essUser.getBase().isFlying() ? "Yes" : "No").append("<br/>").append("<strong>At Y:</strong> ").append(essUser.getLocation().getBlockY()).append("<br/>").append("<strong>In World:</strong> ").append(essUser.getLocation().getWorld().getName()).append("<br/>");
        if (essUser.isJailed() | essUser.isMuted()) {
            response.append("<br/>");
        }
        if (essUser.isJailed()) {
            response.append("<strong>Jailed Until:</strong> ").append(df.format(new Date(essUser.getJailTimeout()))).append("<br/>");
        }
        if (essUser.isMuted()) {
            response.append("<strong>Muted Until:</strong> ").append(df.format(new Date(essUser.getMuteTimeout()))).append("<br/>");
        }
        return response.toString();
    }

    private class PlayerMonitorEvents implements Listener {
        @EventHandler
        public void onPlayerJoin(final PlayerJoinEvent event) {
            WebOpPlugin.PluginContext.getPlugin().getServer().getScheduler().runTaskLaterAsynchronously(WebOpPlugin.PluginContext.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    for (final WebOpUser user : WebOpPlugin.PluginContext.getSessionManager().getLoggedInUsers()) {
                        if (isSubscriber(user.getName())) {
                            if (user.getWebSocketSession() == null) {
                                continue;
                            }
                            if (!user.getWebSocketSession().isOpen()) {
                                continue;
                            }
                            try {
                                user.getWebSocketSession().getRemote().sendString(generatePlayerString());
                            } catch (IOException ex) {
                            }
                        }
                    }
                }
            }, 10L);
        }

        @EventHandler
        public void onPlayerQuit(final PlayerQuitEvent event) {
            WebOpPlugin.PluginContext.getPlugin().getServer().getScheduler().runTaskLaterAsynchronously(WebOpPlugin.PluginContext.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    for (final WebOpUser user : WebOpPlugin.PluginContext.getSessionManager().getLoggedInUsers()) {
                        if (isSubscriber(user.getName())) {
                            if (user.getWebSocketSession() == null) {
                                continue;
                            }
                            if (!user.getWebSocketSession().isOpen()) {
                                continue;
                            }
                            try {
                                user.getWebSocketSession().getRemote().sendString(generatePlayerString());
                            } catch (IOException ex) {
                            }
                        }
                    }
                }
            }, 10L);
        }
    }
}
