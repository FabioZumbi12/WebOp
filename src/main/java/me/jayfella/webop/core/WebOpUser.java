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

package me.jayfella.webop.core;

import me.jayfella.webop.WebOpPlugin;
import org.eclipse.jetty.websocket.api.Session;

import java.util.Date;
import java.util.logging.Level;

public class WebOpUser {
    private final String username;
    private final String hash;
    private long lastActivity;
    private Session websocketSession;

    public WebOpUser(final String username) {
        this.username = username;
        this.hash = WebOpPlugin.PluginContext.getSessionManager().generateSession(username);
        this.lastActivity = System.currentTimeMillis();
    }

    public String getName() {
        return this.username;
    }

    public String getSession() {
        return this.hash;
    }

    public Session getWebSocketSession() {
        return this.websocketSession;
    }

    public void setWebSocketSession(final Session websocketSession) {
        this.websocketSession = websocketSession;
    }

    public void updateLastActivity() {
        this.lastActivity = System.currentTimeMillis();
    }

    public boolean isSessionExpired() {
        final Date currentDate = new Date(System.currentTimeMillis());
        final Date lastActive = new Date(this.lastActivity);
        return currentDate.getTime() - lastActive.getTime() > 900000L;
    }

    public boolean isWebSocketReady() {
        return this.websocketSession != null && this.websocketSession.isOpen();
    }

    public void sendSocketMessage(final String message) {
        if (!this.isWebSocketReady()) {
            return;
        }
        try {
            this.websocketSession.getRemote().sendString(message);
        } catch (Exception ex) {
            WebOpPlugin.PluginContext.getPlugin().getLogger().log(Level.WARNING, "WebSocket Error:", ex);
        }
    }
}
