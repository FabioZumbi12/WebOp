// 
// Decompiled by Procyon v0.5.36
// 

package me.jayfella.webop.website;

import me.jayfella.webop.WebOpPlugin;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class WebPage {
    private int responseCode;
    private String contentType;

    public abstract byte[] get(final HttpServletRequest p0, final HttpServletResponse p1);

    public abstract byte[] post(final HttpServletRequest p0, final HttpServletResponse p1);

    public int getResponseCode() {
        return this.responseCode;
    }

    public void setResponseCode(final int value) {
        this.responseCode = value;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(final String value) {
        this.contentType = value;
    }

    public String addSiteTemplate(final String content, final String title, final HttpServletRequest req) {
        String result = this.loadResource("me/jayfella/webop/website/html/overall_layout.html").replace("{page_body}", content).replace("{title}", title);
        if (WebOpPlugin.PluginContext.getSessionManager().isValidCookie(req)) {
            String username = "";
            for (final Cookie cookie : req.getCookies()) {
                if (cookie.getName().equals("webop_user")) {
                    username = cookie.getValue();
                    break;
                }
            }
            result = result.replace("{main_menu}", this.loadResource("me/jayfella/webop/website/html/mainmenu.html"));
            result = result.replace("{username}", username);
        } else {
            result = result.replace("{main_menu}", "");
        }
        return result;
    }

    public String loadResource(final String path) {
        String output = "";
        try (final InputStream inp = this.getClass().getClassLoader().getResourceAsStream(path);
             final BufferedReader rd = new BufferedReader(new InputStreamReader(inp))) {
            String s;
            while (null != (s = rd.readLine())) {
                output = output + s + "\n";
            }
        } catch (Exception ex) {
            return "";
        }
        return output;
    }
}
