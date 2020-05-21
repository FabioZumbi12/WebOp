// 
// Decompiled by Procyon v0.5.36
// 

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
