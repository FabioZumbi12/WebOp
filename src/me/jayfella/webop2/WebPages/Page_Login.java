package me.jayfella.webop2.WebPages;

import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.util.Map;
import java.util.logging.Level;
import me.jayfella.webop2.Core.LoggedInUser;
import me.jayfella.webop2.Core.MojangValidator;
import me.jayfella.webop2.Core.WebPage;
import me.jayfella.webop2.PluginContext;
import me.jayfella.webop2.SessionManager;

public class Page_Login extends WebPage
{
    public Page_Login(PluginContext context)
    {
        super(context);
    }

    @Override public String contentType() { return "text/html; charset=utf-8"; }

    @Override
    public byte[] get(HttpExchange he)
    {
        if (this.getContext().getSessionManager().isAuthorised(he))
        {
            this.setResponseCode(HttpURLConnection.HTTP_MOVED_PERM);
            he.getResponseHeaders().add("Location", "index.php");
            return "Already logged in. Redirecting...".getBytes();
        }

        this.setPageTitle("[WebOp] Login");
        this.setPageBody(loadHtml("login.html"));

        return this.getPageOutput(he);
    }

    @Override
    public byte[] post(HttpExchange he)
    {
        return attemptLogin(he);
    }

    private byte[] attemptLogin(HttpExchange he)
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
            this.getContext().getLogger().log(Level.SEVERE, null, ex);
            return ex.getMessage().getBytes();
        }

        Map<String, String> vars = this.parsePostResponse(postParams);

        String minecraftName = "";

        try
        {
            String username = URLDecoder.decode(vars.get("username"), "UTF-8");
            String password = URLDecoder.decode(vars.get("password"), "UTF-8");

            minecraftName = MojangValidator.isValidAccount(this.getContext(), username, password);
        }
        catch (UnsupportedEncodingException ex)
        {
            return "Invalid username or password.".getBytes();
        }

        if (minecraftName.isEmpty())
        {
            return "Invalid username or password.".getBytes();
        }
        else
        {
            if (!this.getContext().getSessionManager().isWhitelisted(minecraftName))
            {
                return "Username is not whitelisted.".getBytes();
            }

            if (this.getContext().getSessionManager().isLoggedIn(minecraftName))
                this.getContext().getSessionManager().logUserOut(minecraftName);

            LoggedInUser user = this.getContext().getSessionManager().addLoggedInUser(he.getRemoteAddress().getAddress(), minecraftName);

            // set cookies
            he.getResponseHeaders().add("Set-Cookie", SessionManager.SESSION_USER + "=" + minecraftName);
            he.getResponseHeaders().add("Set-Cookie", SessionManager.SESSION_SESSION + "=" + user.getSession());

            this.setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP);
            he.getResponseHeaders().add("Location", "index.php");
            return "OK".getBytes();
        }
    }
}
