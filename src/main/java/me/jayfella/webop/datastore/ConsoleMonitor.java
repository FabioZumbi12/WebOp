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
import me.jayfella.webop.core.SocketSubscription;
import me.jayfella.webop.core.WebOpUser;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

public class ConsoleMonitor extends SocketSubscription {
    private final PluginContext context;

    public ConsoleMonitor(final PluginContext context) {
        this.context = context;
        ConsoleFilter filter = new ConsoleFilter();
        ((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(filter);
    }

    public void executeCommand(String command, final boolean asConsole, final String username) {
        if (asConsole) {
            final boolean isOp = this.context.getPlugin().getServer().getOfflinePlayer(username).isOp();
            if (isOp | this.context.getSessionManager().canExecuteConsoleOpCommands(username)) {
                final String resp = ChatColor.DARK_GREEN + "Player " + ChatColor.GREEN + username + ChatColor.DARK_GREEN + " issued console command: " + ChatColor.GOLD + command;
                this.context.getPlugin().getLogger().log(Level.INFO, resp);

                if (command.startsWith("/")) command = command.substring(1);
                String finalCommand = command;
                Bukkit.getScheduler().callSyncMethod(context.getPlugin(), () ->
                        Bukkit.dispatchCommand(this.context.getPlugin().getServer().getConsoleSender(), finalCommand));
            } else {
                final String errorString = ChatColor.RED + "Player " + ChatColor.GREEN + username + ChatColor.RED + " tried to execute a command as console without permission: " + ChatColor.GOLD + command;
                this.context.getPlugin().getLogger().log(Level.WARNING, errorString);
            }
        } else {
            final Player player = this.context.getPlugin().getServer().getPlayerExact(username);
            if (player == null) {
                final String errorString = ChatColor.RED + "Player " + ChatColor.GREEN + username + ChatColor.RED + " tried to issue a command whilst not logged in: " + ChatColor.GOLD + command;
                this.context.getPlugin().getLogger().log(Level.WARNING, errorString);
            } else {
                if (command.startsWith("/")) {
                    String finalCommand1 = command;
                    Bukkit.getScheduler().callSyncMethod(context.getPlugin(), () ->
                            Bukkit.dispatchCommand(player, finalCommand1.substring(1)));
                } else {
                    String finalCommand2 = command;
                    Bukkit.getScheduler().runTask(context.getPlugin(), () -> player.chat(finalCommand2));
                }
            }
        }
    }

    private String parseMcColors(String string) {
        string = string

                .replace("u00A7l", "<span style='font-weight: bold'>") // &l
                .replace("u00A7L", "<span style='font-weight: bold'>") // &l
                .replace("u00A7o", "<span style='font-style: italic'>") // &o
                .replace("u00A7O", "<span style='font-style: italic'>") // &o
                .replace("u00A7k", "<span style='text-decoration: blink' class='blink'>") // &k
                .replace("u00A7K", "<span style='text-decoration: blink' class='blink'>") // &k
                .replace("u00A7m", "<span style='text-decoration: line-through'>") // &m
                .replace("u00A7M", "<span style='text-decoration: line-through'>") // &m
                .replace("u00A7n", "<span style='text-decoration: underline'>") // &n
                .replace("u00A7M", "<span style='text-decoration: underline'>") // &n

                .replace("u00A70", "<span style='color: #c0c0c0'>") // &0
                .replace("u00A71", "<span style='color: #0000aa'>") // &1
                .replace("u00A72", "<span style='color: #00aa00'>") // &2
                .replace("u00A73", "<span style='color: #00aaaa'>") // &3
                .replace("u00A74", "<span style='color: #aa0000'>") // &4
                .replace("u00A75", "<span style='color: #aa00aa'>") // &5
                .replace("u00A76", "<span style='color: #ffaa00'>") // &6
                .replace("u00A77", "<span style='color: #aaaaaa'>") // &7
                .replace("u00A78", "<span style='color: #555555'>") // &8
                .replace("u00A79", "<span style='color: #5555ff'>") // &9
                .replace("u00A7a", "<span style='color: #55ff55'>") // &A
                .replace("u00A7A", "<span style='color: #55ff55'>") // &A
                .replace("u00A7b", "<span style='color: #55ffff'>") // &b
                .replace("u00A7B", "<span style='color: #55ffff'>") // &b
                .replace("u00A7c", "<span style='color: #ff5555'>") // &c
                .replace("u00A7C", "<span style='color: #ff5555'>") // &c
                .replace("u00A7d", "<span style='color: #ff55ff'>") // &d
                .replace("u00A7D", "<span style='color: #ff55ff'>") // &d
                .replace("u00A7e", "<span style='color: #ffff55'>") // &e
                .replace("u00A7E", "<span style='color: #ffff55'>") // &e
                .replace("u00A7f", "<span style='color: #ffffff'>") // &f
                .replace("u00A7F", "<span style='color: #ffffff'>") // &f
                .replace("u00A7r", "</span><span style='color: #ffffff'>") // &r
                .replace("u00A7R", "</span><span style='color: #ffffff'>"); // &r

        string = StringEscapeUtils.unescapeJava(string);

        // close all spans.
        int count = StringUtils.countMatches(string, "<span style=");
        StringBuilder stringBuilder = new StringBuilder(string);
        for (int i = 0; i < count; i++) {
            stringBuilder.append("</span>");
        }
        return stringBuilder.toString();
    }

    public class ConsoleFilter implements Filter {

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

        @Override
        public Result getOnMismatch() {
            return null;
        }

        @Override
        public Result getOnMatch() {
            return null;
        }

        /**
         * Filter an event.
         *
         * @param logger The Logger.
         * @param level  The event logging Level.
         * @param marker The Marker for the event or null.
         * @param msg    String text to filter on.
         * @param params An array of parameters or null.
         * @return the Result.
         */
        @Override
        public Result filter(Logger logger, org.apache.logging.log4j.Level level, Marker marker, String msg, Object... params) {
            return null;
        }

        /**
         * Filter an event.
         *
         * @param logger  The Logger.
         * @param level   the event logging level.
         * @param marker  The Marker for the event or null.
         * @param message The message.
         * @param p0      the message parameters
         * @return the Result.
         */
        @Override
        public Result filter(Logger logger, org.apache.logging.log4j.Level level, Marker marker, String message, Object p0) {
            return null;
        }

        /**
         * Filter an event.
         *
         * @param logger  The Logger.
         * @param level   the event logging level.
         * @param marker  The Marker for the event or null.
         * @param message The message.
         * @param p0      the message parameters
         * @param p1      the message parameters
         * @return the Result.
         */
        @Override
        public Result filter(Logger logger, org.apache.logging.log4j.Level level, Marker marker, String message, Object p0, Object p1) {
            return null;
        }

        /**
         * Filter an event.
         *
         * @param logger  The Logger.
         * @param level   the event logging level.
         * @param marker  The Marker for the event or null.
         * @param message The message.
         * @param p0      the message parameters
         * @param p1      the message parameters
         * @param p2      the message parameters
         * @return the Result.
         */
        @Override
        public Result filter(Logger logger, org.apache.logging.log4j.Level level, Marker marker, String message, Object p0, Object p1, Object p2) {
            return null;
        }

        /**
         * Filter an event.
         *
         * @param logger  The Logger.
         * @param level   the event logging level.
         * @param marker  The Marker for the event or null.
         * @param message The message.
         * @param p0      the message parameters
         * @param p1      the message parameters
         * @param p2      the message parameters
         * @param p3      the message parameters
         * @return the Result.
         */
        @Override
        public Result filter(Logger logger, org.apache.logging.log4j.Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3) {
            return null;
        }

        /**
         * Filter an event.
         *
         * @param logger  The Logger.
         * @param level   the event logging level.
         * @param marker  The Marker for the event or null.
         * @param message The message.
         * @param p0      the message parameters
         * @param p1      the message parameters
         * @param p2      the message parameters
         * @param p3      the message parameters
         * @param p4      the message parameters
         * @return the Result.
         */
        @Override
        public Result filter(Logger logger, org.apache.logging.log4j.Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
            return null;
        }

        /**
         * Filter an event.
         *
         * @param logger  The Logger.
         * @param level   the event logging level.
         * @param marker  The Marker for the event or null.
         * @param message The message.
         * @param p0      the message parameters
         * @param p1      the message parameters
         * @param p2      the message parameters
         * @param p3      the message parameters
         * @param p4      the message parameters
         * @param p5      the message parameters
         * @return the Result.
         */
        @Override
        public Result filter(Logger logger, org.apache.logging.log4j.Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
            return null;
        }

        /**
         * Filter an event.
         *
         * @param logger  The Logger.
         * @param level   the event logging level.
         * @param marker  The Marker for the event or null.
         * @param message The message.
         * @param p0      the message parameters
         * @param p1      the message parameters
         * @param p2      the message parameters
         * @param p3      the message parameters
         * @param p4      the message parameters
         * @param p5      the message parameters
         * @param p6      the message parameters
         * @return the Result.
         */
        @Override
        public Result filter(Logger logger, org.apache.logging.log4j.Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
            return null;
        }

        /**
         * Filter an event.
         *
         * @param logger  The Logger.
         * @param level   the event logging level.
         * @param marker  The Marker for the event or null.
         * @param message The message.
         * @param p0      the message parameters
         * @param p1      the message parameters
         * @param p2      the message parameters
         * @param p3      the message parameters
         * @param p4      the message parameters
         * @param p5      the message parameters
         * @param p6      the message parameters
         * @param p7      the message parameters
         * @return the Result.
         */
        @Override
        public Result filter(Logger logger, org.apache.logging.log4j.Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
            return null;
        }

        /**
         * Filter an event.
         *
         * @param logger  The Logger.
         * @param level   the event logging level.
         * @param marker  The Marker for the event or null.
         * @param message The message.
         * @param p0      the message parameters
         * @param p1      the message parameters
         * @param p2      the message parameters
         * @param p3      the message parameters
         * @param p4      the message parameters
         * @param p5      the message parameters
         * @param p6      the message parameters
         * @param p7      the message parameters
         * @param p8      the message parameters
         * @return the Result.
         */
        @Override
        public Result filter(Logger logger, org.apache.logging.log4j.Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
            return null;
        }

        /**
         * Filter an event.
         *
         * @param logger  The Logger.
         * @param level   the event logging level.
         * @param marker  The Marker for the event or null.
         * @param message The message.
         * @param p0      the message parameters
         * @param p1      the message parameters
         * @param p2      the message parameters
         * @param p3      the message parameters
         * @param p4      the message parameters
         * @param p5      the message parameters
         * @param p6      the message parameters
         * @param p7      the message parameters
         * @param p8      the message parameters
         * @param p9      the message parameters
         * @return the Result.
         */
        @Override
        public Result filter(Logger logger, org.apache.logging.log4j.Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
            return null;
        }

        /**
         * Filter an event.
         *
         * @param logger The Logger.
         * @param level  The event logging Level.
         * @param marker The Marker for the event or null.
         * @param msg    Any Object.
         * @param t      A Throwable or null.
         * @return the Result.
         */
        @Override
        public Result filter(Logger logger, org.apache.logging.log4j.Level level, Marker marker, Object msg, Throwable t) {
            return null;
        }

        /**
         * Filter an event.
         *
         * @param logger The Logger.
         * @param level  The event logging Level.
         * @param marker The Marker for the event or null.
         * @param msg    The Message
         * @param t      A Throwable or null.
         * @return the Result.
         */
        @Override
        public Result filter(Logger logger, org.apache.logging.log4j.Level level, Marker marker, Message msg, Throwable t) {
            return null;
        }

        @Override
        public Result filter(LogEvent logEvent) {
            Object[] params = logEvent.getMessage().getParameters();
            String output = logEvent.getMessage().getFormattedMessage();

            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; ++i) {
                    output = output.replace("{" + i + "}", params[i].toString());
                }
            }
            output = output.replace(">", "&gt;")
                    .replace("<", "&lt;")
                    .replace("\"", "&quot;");
            output = StringEscapeUtils.escapeJava(output);
            output = "[" + formatter.format(new Date(logEvent.getTimeMillis())) + " " + logEvent.getLevel() + "] " + output;
            output = parseMcColors(output);
            output = output.trim();

            // Debug
            /* try {
                File log = new File(context.getPlugin().getDataFolder(), "log.txt");
                if (!log.exists()) log.createNewFile();
                Files.write(log.toPath(), output.getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            } */

            // Remove credentials from console
            if (output.toLowerCase().contains("issued server command: /login") ||
                    output.toLowerCase().contains("issued server command: /register") ||
                    output.toLowerCase().contains("issued server command: /changepass")) return null;

            for (final WebOpUser user : context.getSessionManager().getLoggedInUsers()) {
                if (isSubscriber(user.getName())) {
                    if (user.getWebSocketSession() == null) {
                        continue;
                    }
                    if (!user.getWebSocketSession().isOpen()) {
                        continue;
                    }
                    final String response = "case=consoleData;<span class='consoleLine'>" + output + "<br/></span>";
                    if (context.getPlugin().isEnabled()) {
                        try {
                            context.getPlugin().getServer().getScheduler().runTask(context.getPlugin(), () -> {
                                if (user.getWebSocketSession() == null || !user.getWebSocketSession().isOpen()) {
                                    return;
                                }
                                try {
                                    user.getWebSocketSession().getRemote().sendString(response);
                                } catch (IOException ignored) {
                                }
                            });
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
            return null;
        }

        @Override
        public State getState() {
            return null;
        }

        @Override
        public void initialize() {

        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public boolean isStarted() {
            return false;
        }

        @Override
        public boolean isStopped() {
            return false;
        }
    }
}
