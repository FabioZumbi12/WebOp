package me.jayfella.webop2.Core;

import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import me.jayfella.webop2.PluginContext;
import org.bukkit.Bukkit;

public abstract class WebPage implements IWebPage
{
    private final PluginContext context;

    private String pageContents;
    private int responseCode = HttpURLConnection.HTTP_OK;

    public WebPage(PluginContext context)
    {
        this.context = context;
        this.pageContents = this.loadHtml("overall_layout.html");
    }

    public final PluginContext getContext() { return this.context; }

    @Override
    public int responseCode() { return responseCode; }
    public final void setResponseCode(int code) { this.responseCode = code; }

    public final byte[] getPageOutput(HttpExchange he)
    {
        if (this.pageContents.contains("{title}"))
            this.pageContents = pageContents.replace("{title}", "WebOp");

        if (this.pageContents.contains("{headerIncludes}"))
            this.pageContents = pageContents.replace("{headerIncludes}", "");

        if (this.context.getSessionManager().isLoggedIn(he.getRemoteAddress().getAddress()))
        {
            String username = this.getContext().getSessionManager().getLoggedInUser(he.getRemoteAddress().getAddress()).getUsername();
            this.pageContents = pageContents
                    .replace("{mainmenu}", this.loadHtml("mainmenu.html"))
                    .replace("{username}", username)
                    .replace("{userlinks}", this.userlinks());
        }
        else
        {
            this.pageContents = pageContents.replace("{mainmenu}", "");
        }

        return this.pageContents.getBytes();
    }

    public final void setPageTitle(String title) { this.pageContents = this.pageContents.replace("{title}", title); }

    public final void setHeaderIncludes(String[] includes)
    {
        String result = "";

        for (String str : includes)
        {
            result += str + "\n";
        }

        this.pageContents = this.pageContents.replace("{headerIncludes}", result);
    }

    public final void setHeaderIncludes(List<String> includes)
    {
        this.setHeaderIncludes(includes.toArray(new String[includes.size()]));
    }

    public final void setPageBody(String body)
    {
        this.pageContents = this.pageContents.replace("{body}", body);
    }

    private String loadResource(String path)
    {
        String output = "";

        try(InputStream inp = getClass().getClassLoader().getResourceAsStream(path))
        {
            try(BufferedReader rd = new BufferedReader(new InputStreamReader(inp)))
            {
                String s;

                while (null != (s = rd.readLine()))
                {
                    output += s + "\n";
                }
            }
        }
        catch (Exception ex)
        {
            this.getContext().getLogger().log(Level.INFO, "unable to locate javascript file: {0}", path);
            return "";
        }

        return output;
    }

    public final String readFile(String path, Charset encoding) throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return encoding.decode(ByteBuffer.wrap(encoded)).toString();
    }

    public final Map<String, String> parseGetResponse(HttpExchange he)
    {
        URI requestedUri = he.getRequestURI();
        String query = requestedUri.getRawQuery();

        Map<String, String> data = parsePostResponse(query);
        return data;
    }

    public final Map<String, String> parsePostResponse(HttpExchange he)
    {
        String postParams;

        try (InputStreamReader inStream = new InputStreamReader(he.getRequestBody()))
        {
            try (BufferedReader bufferedReader = new BufferedReader(inStream))
            {
                postParams = bufferedReader.readLine();
            }
        }
        catch (IOException ex)
        {
            return new HashMap<>();
        }

        Map<String, String> vars = this.parsePostResponse(postParams);

        return vars;
    }

    public final Map<String, String> parsePostResponse(String query)
    {
        if (query == null || query.length() < 1)
        {
            return new HashMap<>();
        }

        Map<String, String> results = new HashMap<>();

        String[] pairs = query.split("&");

        for (String pair : pairs)
        {
            String[] param = pair.split("=");

            if (param.length == 2)
            {
                try
                {
                    results.put(param[0], URLDecoder.decode(param[1], "UTF-8"));
                }
                catch (UnsupportedEncodingException ex)
                {
                    results.put(param[0], "");
                }
            }
            else
            {
                results.put(param[0], "");
            }
        }

        return results;
    }

    private String userlinks()
    {
        File pluginDir = this.getContext().getPlugin().getDataFolder();

        FilenameFilter textFilter = new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                return name.toLowerCase().endsWith(".txt");
            }
        };

        File[] files = pluginDir.listFiles(textFilter);

        if (files.length > 0)
        {
            StringBuilder sb = new StringBuilder();

            for (File file : files)
            {
                String content;

                try
                {
                    content = this.readFile(file.getCanonicalPath(), Charset.availableCharsets().get("UTF-8"));
                }
                catch (IOException ex)
                {
                    Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                    continue;
                }

                String[] lines = content.split("\n");

                sb.append("<li class='has-sub '>")
                        .append("<a href='#'>").append(file.getName().replace(".txt", "")).append("</a>")
                        .append("<ul>");


                for (String line : lines)
                {
                    String[] values = line.split(">>");

                    if (values.length != 2)
                        continue;

                    sb
                            .append("<li>")
                            .append("<a href='").append(values[1].trim()).append("'>").append(values[0].trim()).append("</a>")
                            .append("</li>");
                }

                sb.append("</ul>").append("</li>");
            }

            return sb.toString();
        }
        else
        {
            return "";
        }
    }

    public final String loadHtml(String filename) { return loadResource("me/jayfella/webop2/WebPages/Html/" + filename); }
    public final String loadJscript(String filename) { return loadResource("me/jayfella/webop2/WebPages/JavaScript/" + filename); }
    public final String loadCss(String filename) { return loadResource("me/jayfella/webop2/WebPages/Css/" + filename); }
}
