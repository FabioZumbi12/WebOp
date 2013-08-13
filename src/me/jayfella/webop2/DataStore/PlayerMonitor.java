package me.jayfella.webop2.DataStore;

import java.text.NumberFormat;
import me.jayfella.webop2.PluginContext;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlayerMonitor
{
    private final PluginContext context;

    public PlayerMonitor(PluginContext context)
    {
        this.context = context;
    }

    public String generatePlayerString()
    {
        int alltimeCount = context.getPlugin().getServer().getOfflinePlayers().length;
        Player[] onlinePlayers = context.getPlugin().getServer().getOnlinePlayers();

        StringBuilder sb = new StringBuilder()
                .append("ALLTIME=").append(NumberFormat.getIntegerInstance().format(alltimeCount))
                .append("&ONLINENOW=").append(NumberFormat.getIntegerInstance().format(onlinePlayers.length))
                .append("&PLAYERS=");

        for (int i = 0; i < onlinePlayers.length; i++)
        {
            // String prefixedName = chatProvider.getPlayerPrefix(onlinePlayers[i]) + onlinePlayers[i].getName();

            // sb.append(prefixedName);
            sb.append(onlinePlayers[i].getName());

            if (i < onlinePlayers.length -1)
            {
                sb.append(", ");
            }
        }

        return sb.toString();
    }

    public String findPlayers(String partialName)
    {
        StringBuilder results = new StringBuilder();

        int resultCount = 0;

        for (OfflinePlayer offlinePlayer : context.getPlugin().getServer().getOfflinePlayers())
        {
            String playerName = offlinePlayer.getName().toLowerCase();

            if (playerName.contains(partialName))
            {
                results.append(offlinePlayer.getName()).append(",");
                resultCount++;
            }

            if (resultCount == 50)
                break;
        }

        return results.toString();
    }


}
