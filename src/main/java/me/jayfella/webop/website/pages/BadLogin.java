// 
// Decompiled by Procyon v0.5.36
// 

package me.jayfella.webop.website.pages;

import me.jayfella.webop.website.WebPage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BadLogin extends WebPage {
    public BadLogin() {
        this.setResponseCode(404);
        this.setContentType("text/html; charset=utf-8");
    }

    @Override
    public byte[] get(final HttpServletRequest req, final HttpServletResponse resp) {
        String page = this.loadResource("html", "badlogin.html");
        page = this.addSiteTemplate(page, "[WebOp] Bad Login", req);
        String response = req.getParameter("error");
        if (response == null) {
            try {
                resp.sendRedirect("index.php");
            } catch (IOException ignored) {
            }
            return new byte[0];
        }
        switch (response) {
            case "1":
                page = page.replace("{error}", "Bad username or password encoding.");
                return page.getBytes();
            case "2":
                page = page.replace("{error}", "Invalid username or password.");
                return page.getBytes();
            case "3":
                page = page.replace("{error}", "Username is not whitelisted.");
                return page.getBytes();
            default:
                try {
                    resp.sendRedirect("index.php");
                } catch (IOException ignored) {
                }
                return new byte[0];
        }
    }

    @Override
    public byte[] post(final HttpServletRequest req, final HttpServletResponse resp) {
        return new byte[0];
    }
}
