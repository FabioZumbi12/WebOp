package me.jayfella.webop2;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.jayfella.webop2.Core.LoggedInUser;

public final class SessionManager
{
    public static final String SESSION_SESSION = "webop_session";
    public static final String SESSION_USER = "webop_user";

    private final PluginContext context;

    private final List<String> whitelistedUsers;
    private final List<String> consoleViewWhitelist;
    private final List<String> consoleOpWhitelist;

    private final Set<LoggedInUser> loggedInUsers = new HashSet<>();

    public SessionManager(PluginContext context)
    {
        this.context = context;
        this.whitelistedUsers = populateWhitelist("whitelist");
        this.consoleViewWhitelist = populateWhitelist("consoleView");
        this.consoleOpWhitelist = populateWhitelist("consoleOp");
    }

    public List<String> populateWhitelist(String node)
    {
        List<String> whitelist = new ArrayList<>();
        whitelist.addAll(context.getPluginSettings().getFileConfiguration().getStringList(node));
        return whitelist;
    }

    public List<String> getWhitelistedPlayers() { return this.whitelistedUsers; }
    public List<String> getConsoleViewWhitelistedPlayers() { return this.consoleViewWhitelist; }
    public List<String> getConsoleOpWhitelistedPlayers() { return this.consoleOpWhitelist; }

    public boolean canViewConsole(String username) { return (consoleViewWhitelist.contains(username)); }
    public boolean canExecuteConsoleOpCommands(String username) { return (consoleOpWhitelist.contains(username)); }

    public boolean isWhitelisted(String username) { return whitelistedUsers.contains(username); }

    public void addToWhitelist(String name)
    {
        if (!isWhitelisted(name))
            this.whitelistedUsers.add(name);

        context.getPluginSettings().getFileConfiguration().set("whitelist", whitelistedUsers);
        context.getPlugin().saveConfig();
    }

    public void addToConsoleViewWhitelist(String name)
    {
        if (!canViewConsole(name))
            this.consoleViewWhitelist.add(name);

        context.getPluginSettings().getFileConfiguration().set("consoleView", consoleViewWhitelist);
        context.getPlugin().saveConfig();
    }

    public void addToConsoleOpWhitelist(String name)
    {
        if (!canExecuteConsoleOpCommands(name))
            this.consoleOpWhitelist.add(name);

        context.getPluginSettings().getFileConfiguration().set("consoleOp", consoleOpWhitelist);
        context.getPlugin().saveConfig();
    }


    public void removeFromWhitelist(String name)
    {
        if (isWhitelisted(name))
            this.whitelistedUsers.remove(name);

        if (isLoggedIn(name))
            logUserOut(name);

        context.getPluginSettings().getFileConfiguration().set("whitelist", whitelistedUsers);
        context.getPlugin().saveConfig();
    }

    public void removeFromConsoleViewWhitelist(String name)
    {
        if (canViewConsole(name))
            this.consoleViewWhitelist.remove(name);

        context.getPluginSettings().getFileConfiguration().set("consoleView", consoleViewWhitelist);
        context.getPlugin().saveConfig();
    }

    public void removeFromConsoleOpWhitelist(String name)
    {
        if (canExecuteConsoleOpCommands(name))
            this.consoleOpWhitelist.remove(name);

        context.getPluginSettings().getFileConfiguration().set("consoleOp", consoleOpWhitelist);
        context.getPlugin().saveConfig();
    }

    public boolean isLoggedIn(String username)
    {
        for (LoggedInUser w : loggedInUsers)
        {
            if (w.getUsername().equalsIgnoreCase(username))
            {
                return true;
            }
        }

        return false;
    }

    public boolean isLoggedIn(InetAddress address)
    {
        for (LoggedInUser w : loggedInUsers)
        {
            if (w.getHttpAddress().getHostAddress().equalsIgnoreCase(address.getHostAddress()))
                return true;
        }

        return false;
    }

    public LoggedInUser addLoggedInUser(InetAddress httpAddress, String username)
    {
        for (LoggedInUser w : loggedInUsers)
        {
            if (w.getUsername().equalsIgnoreCase(username))
            {
                return w;
            }
        }

        LoggedInUser user = new LoggedInUser(httpAddress, username, this.generateSession(username));
        this.loggedInUsers.add(user);
        return user;
    }

    public void logUserOut(String username)
    {
        Iterator<LoggedInUser> iterator = loggedInUsers.iterator();

        while (iterator.hasNext())
        {
            LoggedInUser wu = iterator.next();

            if (wu.getUsername().equalsIgnoreCase(username))
            {
                iterator.remove();
                break;
            }
        }
    }

    public LoggedInUser getLoggedInUser(InetAddress address)
    {
        for (LoggedInUser w : loggedInUsers)
        {
            if (w.getHttpAddress().getHostAddress().equalsIgnoreCase(address.getHostAddress()))
                return w;
        }

        return null;
    }

    public LoggedInUser getLoggedInUser(String username)
    {
        for (LoggedInUser user : loggedInUsers)
        {
            if (user.getUsername().equalsIgnoreCase(username))
            {
                return user;
            }
        }

        return null;
    }

    public Set<LoggedInUser> getLoggedInUsers() { return this.loggedInUsers; }

    public boolean isValidCookie(HttpExchange he)
    {
        LoggedInUser webUser = getLoggedInUser(he.getRemoteAddress().getAddress());

        if (webUser == null)
            return false;

        Headers headers = he.getRequestHeaders();
        List<String> cookies = headers.get("Cookie");

        if (cookies == null)
            return false;

        String cookie_username = "";
        String cookie_session = "";

        for (String str : cookies)
        {
            String[] values = str.split(";");

            for (String value : values)
            {
                String[] nameValPair = value.split("=");

                if (nameValPair.length != 2)
                    continue;

                String name = nameValPair[0].trim();
                String val = nameValPair[1].trim();

                switch (name)
                {
                    case SessionManager.SESSION_USER:
                    {
                        cookie_username = val;
                        break;
                    }
                    case SessionManager.SESSION_SESSION:
                    {
                        cookie_session = val;
                        break;
                    }
                }
            }

        }

        return (webUser.getUsername().equals(cookie_username) && webUser.getSession().equals(cookie_session));
    }

    public String generateSalt()
    {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    public String generateSession(String str)
    {
        try
        {
            MessageDigest sha = MessageDigest.getInstance("SHA-1");

            String hash = str + generateSalt();

            byte[] hashOne = sha.digest(hash.getBytes());
            return hexEncode(hashOne);
        }
        catch (NoSuchAlgorithmException ex)
        {
            Logger.getLogger(SessionManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public boolean isAuthorised(HttpExchange he)
    {

        if (!this.isLoggedIn(he.getRemoteAddress().getAddress()))
            return false;

        if (!this.isValidCookie(he))
            return false;

        if (!this.isWhitelisted(this.getLoggedInUser(he.getRemoteAddress().getAddress()).getUsername()))
            return false;

        return true;
    }

    private String hexEncode( byte[] aInput)
    {
        StringBuilder result = new StringBuilder();
        char[] digits = {'0', '1', '2', '3', '4','5','6','7','8','9','a','b','c','d','e','f'};
        for (int idx = 0; idx < aInput.length; ++idx)
        {
            byte b = aInput[idx];
            result.append( digits[ (b&0xf0) >> 4 ] );
            result.append( digits[ b&0x0f] );
        }

        return result.toString();
    }

}
