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

public class Login extends WebPage {
    public Login() {
        this.setResponseCode(200);
        this.setContentType("text/html; charset=utf-8");
    }

    @Override
    public byte[] get(final HttpServletRequest req, final HttpServletResponse resp) {
        if (WebOpPlugin.PluginContext.getSessionManager().isValidCookie(req)) {
            try {
                resp.sendRedirect("index.php");
            } catch (IOException ignored) {
            }
            return new byte[0];
        }
        String page = this.loadResource("me/jayfella/webop/website/html/login.html");
        page = this.addSiteTemplate(page, "[WebOp] Console Login", req);
        return page.getBytes();
    }

    @Override
    public byte[] post(final HttpServletRequest req, final HttpServletResponse resp) {
        return this.attemptLogin(req, resp);
    }

    private byte[] attemptLogin(final HttpServletRequest req, final HttpServletResponse resp) {
        String minecraftName = "";
        try {
            String username = req.getParameter("user");
            String password = req.getParameter("password");
            if (username == null || password == null) {
                return new byte[0];
            }
            username = URLDecoder.decode(username, "UTF-8");
            password = URLDecoder.decode(password, "UTF-8");
            minecraftName = PlayerValidator.isValidAccount(WebOpPlugin.PluginContext, username, password);
        } catch (UnsupportedEncodingException ex) {
            try {
                resp.sendRedirect("badlogin.php?error=1");
            } catch (IOException ignored) {
            }
            return new byte[0];
        }
        if (minecraftName.isEmpty()) {
            try {
                resp.sendRedirect("badlogin.php?error=2");
            } catch (IOException ignored) {
            }
            return new byte[0];
        }
        if (!WebOpPlugin.PluginContext.getSessionManager().isWhitelisted(minecraftName)) {
            try {
                resp.sendRedirect("badlogin.php?error=3");
            } catch (IOException ignored) {
            }
            return new byte[0];
        }
        if (WebOpPlugin.PluginContext.getSessionManager().isLoggedIn(minecraftName)) {
            WebOpPlugin.PluginContext.getSessionManager().logUserOut(minecraftName);
        }
        final WebOpUser user = new WebOpUser(minecraftName);
        WebOpPlugin.PluginContext.getSessionManager().logUserIn(user);
        final Cookie userCookie = new Cookie("webop_user", user.getName());
        final Cookie sessCookie = new Cookie("webop_session", user.getSession());
        resp.addCookie(userCookie);
        resp.addCookie(sessCookie);
        try {
            resp.sendRedirect("index.php");
        } catch (IOException ignored) {
        }
        return new byte[0];
    }
}
