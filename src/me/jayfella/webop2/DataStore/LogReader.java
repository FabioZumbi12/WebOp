
package me.jayfella.webop2.DataStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogReader
{
    public LogReader()
    {

    }

    public String searchLog(String searchTerm, String timeFrame)
    {
        /* String[] messageData = message.split(";");
        String searchTerm = "";
        String timeFrame = "";

        for (String str : messageData)
        {
            if (str.startsWith("searchTerm="))
            {
                searchTerm = str.replace("searchTerm=", "");
            }
            else if (str.startsWith("timeFrame="))
            {
                timeFrame = str.replace("timeFrame=", "");
            }
        } */

        if (searchTerm.length() < 3)
        {
            // conn.send("logSearchResult : " + "Search term must be greater than 3 characters.");
            return "Search term must be greater than 3 characters.";
        }

        List<String> results = new ArrayList<>();
        int maxResults = 50;

        Date sinceDate = new Date(0);

        if (!timeFrame.isEmpty())
        {
            long since = parseDateDiff(timeFrame, Calendar.getInstance().getTime(), false);
            sinceDate = new Date(since);
        }

        int tot = 0;

        try
        {
            try (Scanner scan = new Scanner(new File("server.log")))
            {
                while (scan.hasNext())
                {
                    String line = scan.nextLine();

                    if (line.contains(searchTerm))
                    {
                        int index = line.indexOf("[");
                        if (index == -1) continue;

                        String dateString = line.substring(0, index -1).trim();

                        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);

                        if (date.before(sinceDate)) continue;

                        results.add(line);

                        tot++;

                        if (tot == maxResults)
                        {
                            break;
                        }
                    }
                }
            }
        }
        catch (FileNotFoundException | ParseException ex)
        {

        }

        String response = "";

        if (results.isEmpty())
        {
            results.add("No results found!");
        }

        for (String result : results)
        {
            response += "&nbsp;&nbsp;&nbsp;&nbsp;<strong>&bull;</strong>&nbsp;" + result + "<br/>";
        }

        return response;
        // conn.send("logSearchResult : " + response);
    }

    private long parseDateDiff(String time, Date beginDate, boolean future)
    {
        Pattern timePattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?(?:([0-9]+)\\s*(?:s[a-z]*)?)?", 2);

        Matcher m = timePattern.matcher(time);

        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        boolean found = false;

        while (m.find())
        {
            if ((m.group() != null) && (!m.group().isEmpty()))
            {
                for (int i = 0; i < m.groupCount(); i++)
                {
                    if ((m.group(i) != null) && (!m.group(i).isEmpty()))
                    {
                            found = true;
                            break;
                    }
                }
                if (found)
                {
                    if ((m.group(1) != null) && (!m.group(1).isEmpty()))
                    {
                        years = Integer.parseInt(m.group(1));
                    }
                    if ((m.group(2) != null) && (!m.group(2).isEmpty()))
                    {
                        months = Integer.parseInt(m.group(2));
                    }
                    if ((m.group(3) != null) && (!m.group(3).isEmpty()))
                    {
                        weeks = Integer.parseInt(m.group(3));
                    }
                    if ((m.group(4) != null) && (!m.group(4).isEmpty()))
                    {
                        days = Integer.parseInt(m.group(4));
                    }
                    if ((m.group(5) != null) && (!m.group(5).isEmpty()))
                    {
                        hours = Integer.parseInt(m.group(5));
                    }
                    if ((m.group(6) != null) && (!m.group(6).isEmpty()))
                    {
                        minutes = Integer.parseInt(m.group(6));
                    }
                    if ((m.group(7) != null) && (!m.group(7).isEmpty()))
                    {
                        seconds = Integer.parseInt(m.group(7));
                    }
                }
            }
        }

        if (!found)
        {
          // throw new Exception("illegalDate", new Object[0]));
            return 0;
        }

        Calendar c = new GregorianCalendar();
        c.setTime(beginDate);

        if (years > 0)
        {
            c.add(1, years * (future ? 1 : -1));
        }
        if (months > 0)
        {
            c.add(2, months * (future ? 1 : -1));
        }
        if (weeks > 0)
        {
            c.add(3, weeks * (future ? 1 : -1));
        }
        if (days > 0)
        {
            c.add(5, days * (future ? 1 : -1));
        }
        if (hours > 0)
        {
            c.add(11, hours * (future ? 1 : -1));
        }
        if (minutes > 0)
        {
            c.add(12, minutes * (future ? 1 : -1));
        }
        if (seconds > 0)
        {
            c.add(13, seconds * (future ? 1 : -1));
        }

        Calendar max = new GregorianCalendar();
        max.setTime(beginDate);

        max.add(1, 10);

        if (c.after(max))
        {
            return max.getTimeInMillis();
        }

        return c.getTimeInMillis();
    }

}
