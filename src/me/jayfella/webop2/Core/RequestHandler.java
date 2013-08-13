package me.jayfella.webop2.Core;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.zip.GZIPOutputStream;
import me.jayfella.webop2.PluginContext;
import org.bukkit.Bukkit;

public final class RequestHandler implements HttpHandler, Closeable
{
    private final PluginContext context;
    private final boolean isInitialized;

    private HttpServer httpServer;

    public RequestHandler(PluginContext context)
    {
        this.context = context;

        this.isInitialized = this.initialize();
    }

    private boolean initialize()
    {
        try
        {
            int port = context.getPluginSettings().getHttpPort();
            InetAddress ipaddr = InetAddress.getByName(context.getPluginSettings().getIpAddress());

            // this.httpServer = HttpServer.create(new InetSocketAddress(ipaddr, port), 0);
            this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);
            this.httpServer.createContext("/", this);
            this.httpServer.setExecutor(null);
            this.httpServer.start();

            Bukkit.getLogger().log(Level.INFO, new StringBuilder().append("[WebOp] ").append("HTTP Server initialized on ").append(context.getPluginSettings().getIpAddress()).append(":").append(port).toString());
        }
        catch (IOException ex)
        {
            this.context.getLogger().log(Level.SEVERE, "Error initializing HTTP Server.", ex);
            return false;
        }

        return true;
    }

    public boolean isInitialized() { return this.isInitialized; }

    @Override
    public void close() throws IOException
    {
        this.context.getLogger().log(Level.INFO, "Shutting down HttpServer.");
        this.httpServer.stop(0);

        long waitTime = System.currentTimeMillis() + 5 * 1000;

        this.context.getLogger().log(Level.INFO, "Waiting for bound socket to close...");

        while (System.currentTimeMillis() < waitTime)
        {
            try
            {
                Thread.sleep(100);
            }
            catch (Exception ex)
            {

            }
        }

    }

    @Override
    public void handle(HttpExchange he) throws IOException
    {
        switch (he.getRequestMethod())
        {
            case "GET":
            {
                handleGET(he);
                break;
            }
            case "POST":
            {
                handlePOST(he);
                break;
            }
            default: // unknown/unused method
            {
                String pageResponse = "Error 501";

                he.sendResponseHeaders(HttpURLConnection.HTTP_NOT_IMPLEMENTED, pageResponse.length());
                try (OutputStream os = he.getResponseBody())
                {
                    os.write(pageResponse.getBytes());
                }

                he.close();
            }
        }
    }

    private byte[] gzip(byte[] data) throws IOException
    {
        try(ByteArrayOutputStream bytes = new ByteArrayOutputStream())
        {
            try (GZIPOutputStream out = new GZIPOutputStream(bytes))
            {
                out.write(data);
            }

            return bytes.toByteArray();
        }
    }

    private void handleGET(HttpExchange he)
    {
        String requestedPage = he.getRequestURI().getPath().replace("/", "").trim();

        WebPage page;

        // .htaccess
        if (requestedPage.length() < 1)
        {
            if (context.getSessionManager().isAuthorised(he))
            {
                page = this.context.getPageHandler().getPage("index.php");
            }
            else
            {
                page = this.context.getPageHandler().getPage("login.php");
            }
        }
        else
        {
             page = this.context.getPageHandler().getPage(requestedPage);
        }

        try
        {
            byte[] content = gzip(page.get(he));

            // he.getResponseHeaders().add("Cache-Control", "no-cache, no-store, must-revalidate");
            // he.getResponseHeaders().add("Pragma", "no-cache");
            // he.getResponseHeaders().add("Expires", "0");

            he.getResponseHeaders().set("Content-Encoding", "gzip");
            he.getResponseHeaders().add("Content-Type", page.contentType());
            he.sendResponseHeaders(page.responseCode(), content.length);
            he.getResponseBody().write(content);
        }
        catch(Exception ex)
        {
            // context.getPlugin().getLogger().log(Level.INFO, "Http Connection closed.", ex.getMessage());
            context.getPlugin().getLogger().log(Level.INFO, ex.getMessage());
        }
        finally
        {
            he.close();
        }
    }

    private void handlePOST(HttpExchange he)
    {
        String requestedPage = he.getRequestURI().getPath().replace("/", "");

        WebPage page = this.context.getPageHandler().getPage(requestedPage);

        try
        {
            byte[] content = gzip(page.post(he));

            he.getResponseHeaders().add("Cache-Control", "no-cache, no-store, must-revalidate");
            he.getResponseHeaders().add("Pragma", "no-cache");
            he.getResponseHeaders().add("Expires", "0");

            he.getResponseHeaders().set("Content-Encoding", "gzip");
            he.getResponseHeaders().add("Content-Type", page.contentType());
            he.sendResponseHeaders(page.responseCode(), content.length);
            he.getResponseBody().write(content);
        }
        catch(Exception ex)
        {
            context.getPlugin().getLogger().log(Level.INFO, "Connection closed.", ex);
        }
        finally
        {
            he.close();
        }
    }

}
