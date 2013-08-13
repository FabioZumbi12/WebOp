package me.jayfella.webop2.WebPages;

import com.sun.net.httpserver.HttpExchange;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import me.jayfella.webop2.Core.WebPage;
import me.jayfella.webop2.PluginContext;

public class Page_Image extends WebPage
{
    public Page_Image(PluginContext context)
    {
        super(context);
    }

    @Override public String contentType() { return "image/png; charset=utf-8"; }

    @Override
    public byte[] get(HttpExchange he)
    {
        String query = he.getRequestURI().getQuery();

        InputStream inp = getClass().getClassLoader().getResourceAsStream("me/jayfella/webop2/WebPages/Images/" + query);

        int bytesRead;
        byte[] buffer = new byte[8192];
        byte[] data = new byte[0];

        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream())
        {
            while ((bytesRead = inp.read(buffer)) != -1)
            {
                bytes.write(buffer, 0, bytesRead);
            }

            data = bytes.toByteArray();
        }
        catch (Exception ex)
        {
            this.setResponseCode(404);
            return new byte[0];
        }

        return data;
    }

    @Override
    public byte[] post(HttpExchange he)
    {
        return new byte[0];
    }

}
