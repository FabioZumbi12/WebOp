// 
// Decompiled by Procyon v0.5.36
// 

package me.jayfella.webop.datastore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogReader {
    public String searchLog(final String searchTerm) {
        if (searchTerm.length() < 3) {
            return "Search term must be greater than 3 characters.";
        }
        List<String> results = new ArrayList<>();
        try {
            try (Scanner scan = new Scanner(new FileInputStream(new File("logs/latest.log")))) {
                while (scan.hasNext()) {
                    String line = scan.nextLine();

                    if (line.toLowerCase().contains(searchTerm.toLowerCase())) {
                        results.add(line);
                    }
                }
            }
        } catch (FileNotFoundException ignored) {
        }

        StringBuilder response = new StringBuilder();

        if (results.isEmpty()) {
            results.add("No results found!");
        }

        for (String result : results) {
            response.append("&nbsp;&nbsp;&nbsp;&nbsp;<strong>&bull;</strong>&nbsp;").append(result).append("<br/>");
        }
        return response.toString();
    }
}
