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
        final InputStream inp = this.loadImage(requestedFile);
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
