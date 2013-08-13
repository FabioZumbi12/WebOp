package me.jayfella.webop2.Core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import me.jayfella.webop2.PluginContext;

public class MojangValidator
{
    public static String isValidAccount(PluginContext context, String username, String password)
    {
        try
        {
            URL mojangUrl = new URL("https://login.minecraft.net/?user=" + username + "&password=" + password + "&version=13");
            URLConnection urlConn = mojangUrl.openConnection();

            String response = "";

            try(BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream())))
            {
                String inputLine;

                while ((inputLine = in.readLine()) != null)
                {
                    response += inputLine;
                }
            }

            String[] mojangResp = response.split(":");
            if (mojangResp.length != 5) { return ""; }

            String gameName = mojangResp[2];
            String sessionId = mojangResp[3];
            String serverId = context.getPlugin().getServer().getServerId();

            mojangUrl = new URL("http://session.minecraft.net/game/joinserver.jsp?user=" + gameName + "&sessionId=" + sessionId + "&serverId=" + serverId);
            urlConn = mojangUrl.openConnection();

            response = "";

            try(BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream())))
            {
                String inputLine;

                while ((inputLine = in.readLine()) != null)
                {
                    response += inputLine;
                }
            }

            if (response.equalsIgnoreCase("ok"))
            {
                return gameName;
            }

        }
        catch (IOException ex)
        {
            return "";
        }

        return "";
    }
}
