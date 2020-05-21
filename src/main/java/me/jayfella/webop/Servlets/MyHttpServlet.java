// 
// Decompiled by Procyon v0.5.36
// 

package me.jayfella.webop.Servlets;

import me.jayfella.webop.WebOpPlugin;
import me.jayfella.webop.website.PageHandler;
import me.jayfella.webop.website.WebPage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class MyHttpServlet extends HttpServlet {
    private final PageHandler pageHandler;

    public MyHttpServlet() {
        this.pageHandler = new PageHandler();
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) {
        final String requestedPage = req.getRequestURI().replace("/", "");
        WebPage page;
        if (requestedPage.length() < 1) {
            if (WebOpPlugin.PluginContext.getSessionManager().isValidCookie(req)) {
                page = this.pageHandler.getWebPage("index.php");
            } else {
                page = this.pageHandler.getWebPage("login.php");
            }
        } else {
            page = this.pageHandler.getWebPage(requestedPage);
        }
        try {
            final byte[] content = this.gzip(page.get(req, resp));
            resp.setHeader("Content-Encoding", "gzip");
            resp.setContentType(page.getContentType());
            resp.setStatus(page.getResponseCode());
            resp.setContentLength(content.length);
            resp.getOutputStream().write(content);
        } catch (Exception ex) {
        }
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final String requestedPage = req.getRequestURI().replace("/", "");
        final WebPage page = this.pageHandler.getWebPage(requestedPage);
        try {
            final byte[] content = this.gzip(page.post(req, resp));
            resp.setHeader("Content-Encoding", "gzip");
            resp.setContentType(page.getContentType());
            resp.setStatus(page.getResponseCode());
            resp.setContentLength(content.length);
            resp.getOutputStream().write(content);
        } catch (Exception ex) {
        }
    }

    private byte[] gzip(final byte[] data) throws IOException {
        try (final ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
            try (final GZIPOutputStream out = new GZIPOutputStream(bytes)) {
                out.write(data);
            }
            return bytes.toByteArray();
        }
    }
}
