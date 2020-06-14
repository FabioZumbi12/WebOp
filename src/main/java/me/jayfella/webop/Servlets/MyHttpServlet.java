/*
 * Copyright (c) 2020 - @FabioZumbi12
 * Last Modified: 14/06/2020 00:14.
 *
 * This class is provided 'as-is', without any express or implied warranty. In no event will the authors be held liable for any
 *  damages arising from the use of this class.
 *
 * Permission is granted to anyone to use this class for any purpose, including commercial plugins, and to alter it and
 * redistribute it freely, subject to the following restrictions:
 * 1 - The origin of this class must not be misrepresented; you must not claim that you wrote the original software. If you
 * use this class in other plugins, an acknowledgment in the plugin documentation would be appreciated but is not required.
 * 2 - Altered source versions must be plainly marked as such, and must not be misrepresented as being the original class.
 * 3 - This notice may not be removed or altered from any source distribution.
 *
 * Esta classe é fornecida "como está", sem qualquer garantia expressa ou implícita. Em nenhum caso os autores serão
 * responsabilizados por quaisquer danos decorrentes do uso desta classe.
 *
 * É concedida permissão a qualquer pessoa para usar esta classe para qualquer finalidade, incluindo plugins pagos, e para
 * alterá-lo e redistribuí-lo livremente, sujeito às seguintes restrições:
 * 1 - A origem desta classe não deve ser deturpada; você não deve afirmar que escreveu a classe original. Se você usar esta
 *  classe em um plugin, uma confirmação de autoria na documentação do plugin será apreciada, mas não é necessária.
 * 2 - Versões de origem alteradas devem ser claramente marcadas como tal e não devem ser deturpadas como sendo a
 * classe original.
 * 3 - Este aviso não pode ser removido ou alterado de qualquer distribuição de origem.
 */

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
