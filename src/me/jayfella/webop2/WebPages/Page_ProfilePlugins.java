package me.jayfella.webop2.WebPages;

import com.sun.net.httpserver.HttpExchange;
import java.util.Date;
import java.util.List;
import java.util.Map;
import me.jayfella.webop2.Core.LoggedInUser;
import me.jayfella.webop2.Core.RunningTask;
import me.jayfella.webop2.Core.WebPage;
import me.jayfella.webop2.PluginContext;

public class Page_ProfilePlugins extends WebPage
{
    public Page_ProfilePlugins(PluginContext context)
    {
        super(context);
    }

    @Override public String contentType() { return "text/html; charset=utf-8"; }

    @Override
    public byte[] get(HttpExchange he)
    {
        if (!this.getContext().getSessionManager().isAuthorised(he))
            return new byte[0];

        this.setPageTitle("[WebOp] Profile Plugins");

        LoggedInUser user = this.getContext().getSessionManager().getLoggedInUser(he.getRemoteAddress().getAddress());

        if (!this.getContext().getPlugin().getServer().getOfflinePlayer(user.getUsername()).isOp())
        {
            String body = new StringBuilder()
                    .append("<div class='container'><br/>Only OP'ed users can profile plugins.<br/></div>")
                    .toString();

            this.setPageBody(body);
        }
        else
        {
            this.setPageBody(this.loadHtml("profileplugins.html"));
        }

        return this.getPageOutput(he);
    }

    @Override
    public byte[] post(HttpExchange he)
    {
        Map<String, String> vars = this.parsePostResponse(he);

        String state = vars.get("state");

        switch (state)
        {
            case "start":
            {
                this.getContext().getSessionManager().getLoggedInUser(he.getRemoteAddress().getAddress()).setIsProfiling(true);
                this.getContext().getPluginProfiler().startProfiling();


                String response = "<strong>Profiling started:</strong> " + new Date(System.currentTimeMillis()).toString() + "<br/>";

                return response.getBytes();
            }
            case "stop":
            {
                this.getContext().getSessionManager().getLoggedInUser(he.getRemoteAddress().getAddress()).setIsProfiling(false);
                this.getContext().getPluginProfiler().stopProfiling();

                String response = "<strong>Profiling stopped:</strong> " + new Date(System.currentTimeMillis()).toString() + "<br/><br/>";
                response += this.getContext().getPluginProfiler().buildEventProfileResultHtml() + "<br/><br/>";

                // gc
                this.getContext().getPluginProfiler().clearProfilingResults();

                return response.getBytes();
            }
            case "forceabort":
            {
                this.getContext().getPluginProfiler().stopProfiling();

                for (LoggedInUser user : this.getContext().getSessionManager().getLoggedInUsers())
                {
                    user.setIsProfiling(false);
                }

                return "<br/><span style='color: darkred'>All users have been forced to stop profiling.</span><br/><br/>".getBytes();
            }
            case "tasks":
            {
                StringBuilder response = new StringBuilder();

                List<RunningTask> runningTasks = this.getContext().getPluginProfiler().getScheduledTasks();

                for (RunningTask task : runningTasks)
                {
                    response
                            .append("<strong>").append("[").append(task.getPluginName()).append("] ").append("</strong>")
                            .append("<em>").append(task.getClassName()).append("</em> ")
                            .append("<strong>Async:</strong> ").append(task.isAsync()).append(" ")
                            .append("<strong>Recurring:</strong> ").append(task.isRecurring()).append(task.isRecurring() ? " - " + task.getRecurringPeriod() + "L" : "")
                            .append("<br/>");
                }

                return response.toString().getBytes();
            }
        }

        return new byte[0];
    }



}
