package me.jayfella.webop2.WebPages;

import com.sun.net.httpserver.HttpExchange;
import java.util.Map;
import me.jayfella.webop2.Core.WebPage;
import me.jayfella.webop2.PluginContext;

public class Page_LogSearch extends WebPage
{
    public Page_LogSearch(PluginContext context)
    {
        super(context);
    }

    @Override public String contentType() { return "text/plain; charset=utf-8"; }

    @Override
    public byte[] get(HttpExchange he)
    {
        if (!this.getContext().getSessionManager().isAuthorised(he))
            return new byte[0];

        Map<String, String> postParams = this.parseGetResponse(he);

        String searchQuery = postParams.get("searchTerm");
        String timeFrame = postParams.get("timeFrame");

        String response = this.getContext().getLogReader().searchLog(searchQuery, timeFrame);
        //this.getContext().getPlayerMonitor().findPlayers(partialName);

        return response.getBytes();
    }

    @Override
    public byte[] post(HttpExchange he)
    {
        this.setResponseCode(403);
        return new byte[0];
    }

}
