// 
// Decompiled by Procyon v0.5.36
// 

package me.jayfella.webop.website.pages;

import me.jayfella.webop.website.WebPage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class Image extends WebPage {
    public Image() {
        this.setResponseCode(200);
        this.setContentType("image/png; charset=utf-8");
    }

    @Override
    public byte[] get(final HttpServletRequest req, final HttpServletResponse resp) {
        final String requestedFile = req.getParameter("file");
        if (requestedFile == null || requestedFile.isEmpty()) {
            this.setResponseCode(404);
            return new byte[0];
        }
        final InputStream inp = this.getClass().getClassLoader().getResourceAsStream("me/jayfella/webop/website/images/" + requestedFile);
        if (inp == null) {
            this.setResponseCode(404);
            return new byte[0];
        }

        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inp.read(buffer)) != -1) {
                bytes.write(buffer, 0, len);
            }
        } catch (Exception ignored) {
            this.setResponseCode(404);
            return new byte[0];
        }
        return bytes.toByteArray();
    }

    @Override
    public byte[] post(final HttpServletRequest req, final HttpServletResponse resp) {
        return new byte[0];
    }
}
