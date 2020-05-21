// 
// Decompiled by Procyon v0.5.36
// 

package me.jayfella.webop.Servlets;

import me.jayfella.webop.WebOpPlugin;
import me.jayfella.webop.core.MessagePriority;
import me.jayfella.webop.core.WebOpMessage;
import me.jayfella.webop.core.WebOpUser;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

@WebSocket
public class MyWebSocket {
    private void sendMessage(final Session session, final String message) {
        if (!session.isOpen()) {
            return;
        }
        try {
            session.getRemote().sendString(message);
        } catch (Exception ex) {
            WebOpPlugin.PluginContext.getPlugin().getLogger().log(Level.WARNING, "WebSocket Error:", ex);
        }
    }

    @OnWebSocketConnect
    public void onConnect(final Session session) {
        WebOpPlugin.PluginContext.getSessionManager().addSession(session);
    }

    @OnWebSocketMessage
    public void onMessage(final Session session, final String message) {
        if (!WebOpPlugin.PluginContext.getSessionManager().isValidWebSocketConnection(session)) {
            return;
        }
        if (!WebOpPlugin.PluginContext.getSessionManager().isValidWebSocketConnection(message)) {
            return;
        }
        final Map<String, String> map = WebOpPlugin.PluginContext.getSessionManager().parseWebSocketRequest(message);
        final String socketUser = map.get("webop_user");
        final String socketSession = map.get("webop_session");
        for (int i = 0; i < WebOpPlugin.PluginContext.getSessionManager().getLoggedInUsers().size(); ++i) {
            final WebOpUser user = WebOpPlugin.PluginContext.getSessionManager().getLoggedInUsers().get(i);
            if (user.getName().equals(socketUser) && user.getSession().equals(socketSession)) {
                user.setWebSocketSession(session);
            }
        }
        final String socketCase = map.get("case");
        if (socketCase == null || socketCase.isEmpty()) {
            return;
        }
        String replace = message.replace("&webop_user=" + socketUser, "").replace("webop_user=" + socketUser, "");
        final String replace1 = replace.replace("&webop_session=" + socketSession, "").replace("webop_session=" + socketSession, "");
        String replace2 = replace1.replace("&case=" + socketCase, "").replace("case=" + socketCase, "");
        switch (socketCase) {
            case "serverUtilization": {
                WebOpPlugin.PluginContext.getPlugin().getServer().getScheduler().runTask(WebOpPlugin.PluginContext.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        int allChunksCount = 0;
                        int allEntitiesCount = 0;
                        for (final World world : WebOpPlugin.PluginContext.getPlugin().getServer().getWorlds()) {
                            world.getLoadedChunks();
                            allChunksCount += world.getLoadedChunks().length;
                            world.getEntities();
                            allEntitiesCount += world.getEntities().size();
                        }
                        final String response = "case=serverUtilization;" + "CPU=" + WebOpPlugin.PluginContext.getUtilizationMonitor().getCpuLoadPercent() + ";" + "MEM=" + WebOpPlugin.PluginContext.getUtilizationMonitor().getUsedMemoryPercent() + ";" + "TPS=" + WebOpPlugin.PluginContext.getUtilizationMonitor().getCurrentTPS() + ";" + "CHUNKS=" + allChunksCount + ";" + "ENTITIES=" + allEntitiesCount;
                        MyWebSocket.this.sendMessage(session, response);
                    }
                });
                break;
            }
            case "subscribeAllPlayersData": {
                WebOpPlugin.PluginContext.getPlayerMonitor().addSubscriber(socketUser);
                this.sendMessage(session, WebOpPlugin.PluginContext.getPlayerMonitor().generatePlayerString());
                break;
            }
            case "subscribeConsole": {
                if (!WebOpPlugin.PluginContext.getSessionManager().canViewConsole(socketUser)) {
                    return;
                }
                WebOpPlugin.PluginContext.getConsoleMonitor().addSubscriber(socketUser);
                break;
            }
            case "chat": {
                String sanitizedMsg = replace2.replace("&msg=", "").replace("msg=", "").replace(" ", "%20");
                if (sanitizedMsg.length() > 256) {
                    sanitizedMsg = sanitizedMsg.substring(0, 255);
                }
                final SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
                final String time = df.format(new Date(System.currentTimeMillis()));
                try {
                    sanitizedMsg = URLEncoder.encode(sanitizedMsg, "UTF-8");
                } catch (IOException ignored) {
                }
                final String response = "case=chatMessage;user=" + socketUser + ";time=" + time + ";msg=" + sanitizedMsg;
                for (final WebOpUser user2 : WebOpPlugin.PluginContext.getSessionManager().getLoggedInUsers()) {
                    user2.sendSocketMessage(response);
                }
                break;
            }
            case "message": {
                final String action = map.get("action");
                if (action == null || action.isEmpty()) {
                    return;
                }
                final String s2 = action;
                switch (s2) {
                    case "create": {
                        final String socketMsg = map.get("msg");
                        final String socketPriority = map.get("priority");
                        if (socketMsg == null || socketMsg.isEmpty()) {
                            return;
                        }
                        if (socketPriority == null || socketPriority.isEmpty()) {
                            return;
                        }
                        String sanitizedMsg2 = replace2.replace("&action=" + action, "").replace("action=" + action, "").replace("&priority=" + socketPriority, "").replace("priority=" + socketPriority, "").replace("&msg=", "").replace("msg=", "").replace(" ", "%20");
                        try {
                            sanitizedMsg2 = URLEncoder.encode(sanitizedMsg2, "UTF-8");
                        } catch (IOException ignored) {
                        }
                        final MessagePriority msgPriority = MessagePriority.valueOf(socketPriority);
                        final WebOpMessage newMessage = WebOpPlugin.PluginContext.getMessageHandler().createMessage(socketUser, msgPriority, sanitizedMsg2);
                        final String response2 = "case=message;action=new;" + WebOpPlugin.PluginContext.getMessageHandler().createWebSocketString(newMessage);
                        for (final WebOpUser user3 : WebOpPlugin.PluginContext.getSessionManager().getLoggedInUsers()) {
                            user3.sendSocketMessage(response2);
                        }
                        break;
                    }
                    case "delete": {
                        final String socketMsgId = map.get("msgId");
                        if (socketMsgId == null || socketMsgId.isEmpty()) {
                            return;
                        }
                        final int msgId = Integer.parseInt(socketMsgId);
                        WebOpPlugin.PluginContext.getMessageHandler().deleteMessage(msgId);
                        final String response3 = "case=message;action=delete;msgId=" + msgId;
                        for (final WebOpUser user4 : WebOpPlugin.PluginContext.getSessionManager().getLoggedInUsers()) {
                            user4.sendSocketMessage(response3);
                        }
                        break;
                    }
                    case "retrieve": {
                        for (final WebOpMessage msg : WebOpPlugin.PluginContext.getMessageHandler().getMessages()) {
                            final String response3 = "case=message;action=new;" + WebOpPlugin.PluginContext.getMessageHandler().createWebSocketString(msg);
                            this.sendMessage(session, response3);
                        }
                        break;
                    }
                }
                break;
            }
            case "consoleCommand": {
                final String socketCommand = map.get("command");
                final String socketAsConsole = map.get("asConsole");
                if (socketCommand == null || socketCommand.isEmpty()) {
                    return;
                }
                if (socketAsConsole == null || socketAsConsole.isEmpty()) {
                    return;
                }
                final boolean asConsole = Boolean.parseBoolean(socketAsConsole);
                final String sanitizedCommand = replace2.replace("&asConsole=" + socketAsConsole, "").replace("asConsole=" + socketAsConsole, "").replace("&command=", "").replace("command=", "");
                WebOpPlugin.PluginContext.getConsoleMonitor().executeCommand(sanitizedCommand, asConsole, socketUser);
                break;
            }
            case "teleport": {
                final String socketAction = map.get("action");
                if (socketAction == null || socketAction.isEmpty()) {
                    return;
                }
                switch (socketAction) {
                    case "player": {
                        final String socketToPlayer = map.get("to");
                        if (socketToPlayer == null || socketToPlayer.isEmpty()) {
                            return;
                        }
                        final Player playerToTeleport = WebOpPlugin.PluginContext.getPlugin().getServer().getPlayer(socketUser);
                        final Player playerDestination = WebOpPlugin.PluginContext.getPlugin().getServer().getPlayer(socketToPlayer);
                        if (playerToTeleport == null || playerDestination == null) {
                            return;
                        }
                        WebOpPlugin.PluginContext.getPlugin().getServer().getScheduler().runTask(WebOpPlugin.PluginContext.getPlugin(), new Runnable() {
                            @Override
                            public void run() {
                                playerToTeleport.teleport(playerDestination.getLocation());
                            }
                        });
                        break;
                    }
                    case "coord": {
                        final String socketX = map.get("x");
                        final String socketY = map.get("y");
                        final String socketZ = map.get("z");
                        final String socketW = map.get("w");
                        if (socketX == null || socketX.isEmpty() || socketY == null || socketY.isEmpty() || socketZ == null || socketZ.isEmpty() || socketW == null || socketW.isEmpty()) {
                            return;
                        }
                        final Player playerToTeleport2 = WebOpPlugin.PluginContext.getPlugin().getServer().getPlayer(socketUser);
                        if (playerToTeleport2 == null) {
                            return;
                        }
                        int x;
                        int y;
                        int z;
                        try {
                            x = Integer.parseInt(socketX);
                            y = Integer.parseInt(socketY);
                            z = Integer.parseInt(socketZ);
                        } catch (NumberFormatException ex) {
                            return;
                        }
                        final World world = WebOpPlugin.PluginContext.getPlugin().getServer().getWorld(socketW);
                        if (world == null) {
                            return;
                        }
                        final Location teleportLocation = new Location(world, x, y, z);
                        WebOpPlugin.PluginContext.getPlugin().getServer().getScheduler().runTask(WebOpPlugin.PluginContext.getPlugin(), new Runnable() {
                            @Override
                            public void run() {
                                playerToTeleport2.teleport(teleportLocation);
                            }
                        });
                        break;
                    }
                }
                break;
            }
            case "subscribeWorldData": {
                WebOpPlugin.PluginContext.getWorldMonitor().addSubscriber(socketUser);
                for (final World world2 : WebOpPlugin.PluginContext.getPlugin().getServer().getWorlds()) {
                    final String response4 = "case=worldData;" + WebOpPlugin.PluginContext.getWorldMonitor().getWorldDetails(world2);
                    this.sendMessage(session, response4);
                }
                break;
            }
        }
    }

    @OnWebSocketClose
    public void onClose(final int statusCode, final String reason) {
        final Iterator<Session> iterator = WebOpPlugin.PluginContext.getSessionManager().getSessions().iterator();
        WebOpUser closedUser = null;
        while (iterator.hasNext()) {
            final Session sess = iterator.next();
            if (!sess.isOpen()) {
                for (int i = 0; i < WebOpPlugin.PluginContext.getSessionManager().getLoggedInUsers().size(); ++i) {
                    final WebOpUser user = WebOpPlugin.PluginContext.getSessionManager().getLoggedInUsers().get(i);
                    if (user.getWebSocketSession().equals(sess)) {
                        user.setWebSocketSession(null);
                        closedUser = user;
                        WebOpPlugin.PluginContext.getConsoleMonitor().removeSubscriber(user.getName());
                        WebOpPlugin.PluginContext.getPlayerMonitor().removeSubscriber(user.getName());
                        WebOpPlugin.PluginContext.getWorldMonitor().removeSubscriber(user.getName());
                        break;
                    }
                }
                iterator.remove();
            }
        }
        /*final StringBuilder errorMessage = new StringBuilder().append("WebSocket Closed. Code: ").append(statusCode);
        if (reason != null && !reason.isEmpty()) {
            errorMessage.append(", Reason: ").append(reason);
        } else {
            errorMessage.append(", Reason: None");
        }
        if (closedUser != null) {
            WebOpPlugin.PluginContext.getPlugin().getLogger().log(Level.INFO, closedUser.getName() + "'s " + errorMessage.toString());
        } else {
            WebOpPlugin.PluginContext.getPlugin().getLogger().log(Level.INFO, errorMessage.toString());
        }*/
    }

    @OnWebSocketError
    public void onError(final Session session, final Throwable throwable) {
    }
}
