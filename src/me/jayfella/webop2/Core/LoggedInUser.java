package me.jayfella.webop2.Core;

import static java.util.concurrent.TimeUnit.*;
import java.net.InetAddress;
import java.util.ArrayDeque;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.Iterator;
import java.util.Objects;

public final class LoggedInUser
{
    private final long MAX_DURATION = MILLISECONDS.convert(15, MINUTES);
    private final int maxHistory = 100;
    private final InetAddress httpAddress;
    private final String username;
    private final String session;

    private Date lastActivity;

    private boolean isProfiling;

    private final Deque<String> consoleData = new ArrayDeque<>();

    public LoggedInUser(InetAddress httpAddress, String username, String session)
    {
        this.httpAddress = httpAddress;
        this.username = username;
        this.session = session;

        this.isProfiling = false;
    }

    public InetAddress getHttpAddress() { return this.httpAddress; }
    public String getUsername() { return this.username; }
    public String getSession() { return this.session; }

    public Date getLastActivity() { return this.lastActivity; }
    public void flagLastActivity() { this.lastActivity = Calendar.getInstance().getTime(); }

    public boolean isProfiling() { return this.isProfiling; }
    public void setIsProfiling(boolean value) { this.isProfiling = value; }

    public boolean isSessionExpired()
    {
        long duration = Calendar.getInstance().getTimeInMillis() - lastActivity.getTime();
        return (duration >= MAX_DURATION);
    }

    public void addConsoleLine(String line)
    {
        this.consoleData.add(line);

        while (consoleData.size() > maxHistory)
            consoleData.removeFirst();
    }
    public String getConsoleLines()
    {
        StringBuilder result = new StringBuilder();
        Iterator<String> iterator = this.consoleData.iterator();

        //for (String line : this.consoleData)
        while (iterator.hasNext())
        {
            result.append(iterator.next()).append("<br/>");
            iterator.remove();
        }

        return result.toString().trim();
    }

    @Override
    public boolean equals(Object other)
    {
        if (other == null || (!(other instanceof LoggedInUser)))
        {
            return false;
        }

        return (this.hashCode() == other.hashCode());
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.username);
        return hash;
    }
}
