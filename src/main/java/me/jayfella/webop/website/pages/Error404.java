// 
// Decompiled by Procyon v0.5.36
// 

package me.jayfella.webop.website.pages;

import me.jayfella.webop.website.WebPage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Error404 extends WebPage {
    public Error404() {
        this.setResponseCode(404);
        this.setContentType("text/html; charset=utf-8");
    }

    @Override
    public byte[] get(final HttpServletRequest req, final HttpServletResponse resp) {
        String page = this.loadResource("html", "404.html");
        page = this.addSiteTemplate(page, "[WebOp] Error", req);
        return page.getBytes();
    }

    @Override
    public byte[] post(final HttpServletRequest req, final HttpServletResponse resp) {
        String page = this.loadResource("html", "404.html");
        page = this.addSiteTemplate(page, "[WebOp] Error", req);
        return page.getBytes();
    }
}
