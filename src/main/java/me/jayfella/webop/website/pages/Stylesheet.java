// 
// Decompiled by Procyon v0.5.36
// 

package me.jayfella.webop.website.pages;

import me.jayfella.webop.website.WebPage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Stylesheet extends WebPage {
    public Stylesheet() {
        this.setResponseCode(200);
        this.setContentType("text/css; charset=utf-8");
    }

    @Override
    public byte[] get(final HttpServletRequest req, final HttpServletResponse resp) {
        final String requestedFile = req.getParameter("file");
        if (requestedFile == null || requestedFile.isEmpty()) {
            this.setResponseCode(404);
            return new byte[0];
        }
        final String stylesheet = this.loadResource("css", requestedFile);
        if (!stylesheet.isEmpty()) {
            return stylesheet.getBytes();
        }
        this.setResponseCode(404);
        return new byte[0];
    }

    @Override
    public byte[] post(final HttpServletRequest req, final HttpServletResponse resp) {
        return new byte[0];
    }
}
