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
        String page = this.loadResource("html", "login.html");
        page = this.addSiteTemplate(page, "[WebOp] Console Login", req);
        return page.getBytes();
    }

    @Override
    public byte[] post(final HttpServletRequest req, final HttpServletResponse resp) {
        return this.attemptLogin(req, resp);
    }

    private byte[] attemptLogin(final HttpServletRequest req, final HttpServletResponse resp) {
        String minecraftName;
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
