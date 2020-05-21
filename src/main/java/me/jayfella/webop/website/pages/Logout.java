// 
// Decompiled by Procyon v0.5.36
// 

package me.jayfella.webop.website.pages;

import me.jayfella.webop.WebOpPlugin;
import me.jayfella.webop.core.PlayerValidator;
import me.jayfella.webop.core.WebOpUser;
import me.jayfella.webop.website.WebPage;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class Logout extends WebPage {
    public Logout() {
        this.setResponseCode(200);
        this.setContentType("text/html; charset=utf-8");
    }

    @Override
    public byte[] get(final HttpServletRequest req, final HttpServletResponse resp) {
        if (!WebOpPlugin.PluginContext.getSessionManager().isValidCookie(req)) {
            try {
                resp.sendRedirect("login.php");
            } catch (IOException ignored) {
            }
            return new byte[0];
        }

        String user = WebOpPlugin.PluginContext.getSessionManager().getUsername(req);
        if (user != null) {
            WebOpPlugin.PluginContext.getSessionManager().logUserOut(user);
            try {
                resp.sendRedirect("login.php");
            } catch (IOException ignored) {
            }
        }
        return new byte[0];
    }

    @Override
    public byte[] post(final HttpServletRequest req, final HttpServletResponse resp) {
        return new byte[0];
    }

}
