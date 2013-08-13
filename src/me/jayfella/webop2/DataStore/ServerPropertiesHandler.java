package me.jayfella.webop2.DataStore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;

public class ServerPropertiesHandler
{
    private File propertiesFile = new File("server.properties");

    public String getValue(String prefix)
    {
        String[] lines = readPropertiesFile();

        for (String line : lines)
        {
            if (line.startsWith(prefix))
                return line;
        }

        return "";
    }

    public boolean setValue(String prefix, String value)
    {
        String[] lines = readPropertiesFile();

        StringBuilder result = new StringBuilder();

        boolean found = false;

        for (int i = 0; i < lines.length; i++)
        {
            if (lines[i].startsWith(prefix))
            {
                lines[i] = prefix + "=" + value;
                found = true;
            }

            result.append(lines[i]).append("\n");
        }

        try
        {
            FileUtils.writeStringToFile(propertiesFile, result.toString());
        }
        catch(IOException ex) { return false; }

        return found;
    }

    private String[] readPropertiesFile()
    {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(propertiesFile)))
        {
            String line;

            while ((line = br.readLine()) != null)
            {
                lines.add(line);
            }
        }
        catch(IOException ex) { }

        return lines.toArray(new String[lines.size()]);
    }
}
