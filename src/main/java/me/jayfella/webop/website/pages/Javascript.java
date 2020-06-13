// 
// Decompiled by Procyon v0.5.36
// 

package me.jayfella.webop.website.pages;

import me.jayfella.webop.website.WebPage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Javascript extends WebPage {
    public Javascript() {
        this.setResponseCode(200);
        this.setContentType("text/javascript; charset=utf-8");
    }

    @Override
    public byte[] get(final HttpServletRequest req, final HttpServletResponse resp) {
        final String requestedFile = req.getParameter("file");
        if (requestedFile == null || requestedFile.isEmpty()) {
            this.setResponseCode(404);
            return new byte[0];
        }
        final String script = this.loadResource("javascript", requestedFile);
        if (!script.isEmpty()) {
            return script.getBytes();
        }
        this.setResponseCode(404);
        return new byte[0];
    }

    @Override
    public byte[] post(final HttpServletRequest req, final HttpServletResponse resp) {
        return new byte[0];
    }
}
