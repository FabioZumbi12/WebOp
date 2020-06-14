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
import me.jayfella.webop.datastore.LogReader;
import me.jayfella.webop.website.WebPage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class Data extends WebPage {
    public Data() {
        this.setResponseCode(200);
    }

    @Override
    public byte[] get(final HttpServletRequest req, final HttpServletResponse resp) {
        return new byte[0];
    }

    @Override
    public byte[] post(final HttpServletRequest req, final HttpServletResponse resp) {
        if (!WebOpPlugin.PluginContext.getSessionManager().isValidCookie(req)) {
            return new byte[0];
        }
        final String caseParam = req.getParameter("case");
        if (caseParam == null || caseParam.isEmpty()) {
            return new byte[0];
        }
        final String s = caseParam;
        switch (s) {
            case "playerNameSearch": {
                final String partialParam = req.getParameter("partialName");
                if (partialParam == null || partialParam.isEmpty()) {
                    return new byte[0];
                }
                final String response = WebOpPlugin.PluginContext.getPlayerMonitor().findPlayers(partialParam);
                return response.getBytes();
            }
            case "logSearch": {
                final String searchTermParam = req.getParameter("term");
                if (searchTermParam == null) {
                    return new byte[0];
                }
                final LogReader logReader = new LogReader();
                return logReader.searchLog(searchTermParam).getBytes();
            }
            case "logblock": {
                final String postOre = req.getParameter("ore");
                final String postSince = req.getParameter("since");
                String postPlayer = req.getParameter("player");
                if (postOre == null || postOre.isEmpty()) {
                    return new byte[0];
                }
                if (postSince == null || postSince.isEmpty()) {
                    return new byte[0];
                }
                if (postPlayer == null) {
                    postPlayer = "";
                }
                final List<String> results = WebOpPlugin.PluginContext.getLogBlockMonitor().LookupGeneralDestroyedOre(postPlayer, postOre, postSince);
                final StringBuilder response2 = new StringBuilder();
                for (final String result : results) {
                    response2.append("<div class=\"lbResult\">").append(result).append("</div>");
                }
                return response2.toString().getBytes();
            }
            case "findEntities": {
                final String postEntityCount = req.getParameter("count");
                String postEntities = req.getParameter("types");
                if (postEntityCount == null || postEntityCount.isEmpty()) {
                    return new byte[0];
                }
                if (postEntities == null) {
                    postEntities = "";
                }
                final int amount = Integer.parseInt(postEntityCount);
                final String response3 = postEntities.isEmpty() ? WebOpPlugin.PluginContext.getEntityMonitor().findAllHighestEntityCountInChunks(amount) : WebOpPlugin.PluginContext.getEntityMonitor().findCertainHighestEntityCountInChunks(postEntities, amount);
                return response3.getBytes();
            }
            case "essentials": {
                final String postAction = req.getParameter("action");
                if (postAction == null || postAction.isEmpty()) {
                    return new byte[0];
                }
                if ("playerData".equals(postAction)) {
                    final String postPlayer2 = req.getParameter("player");
                    if (postPlayer2 == null || postPlayer2.isEmpty()) {
                        return new byte[0];
                    }
                    final String response4 = WebOpPlugin.PluginContext.getPlayerMonitor().generateEssentialsPlayerDataString(postPlayer2);
                    return response4.getBytes();
                }
                return new byte[0];
            }
            case "serverprofile": {
                final String postAction = req.getParameter("action");
                if (postAction == null || postAction.isEmpty()) {
                    return new byte[0];
                }
                switch (postAction) {
                    case "start": {
                        if (WebOpPlugin.PluginContext.getServerProfiler().isProfiling()) {
                            return "Server is already profiling.".getBytes();
                        }
                        WebOpPlugin.PluginContext.getServerProfiler().startProfiling();
                        return "OK".getBytes();
                    }
                    case "stop": {
                        if (!WebOpPlugin.PluginContext.getServerProfiler().isProfiling()) {
                            return "NOT_PROFILING".getBytes();
                        }
                        WebOpPlugin.PluginContext.getServerProfiler().stopProfiling();
                        final String response3 = WebOpPlugin.PluginContext.getServerProfiler().buildEventProfileResultRaw();
                        return response3.getBytes();
                    }
                    default: {
                        return new byte[0];
                    }
                }
            }
            default: {
                return new byte[0];
            }
        }
    }
}
