package me.jayfella.webop2.WebPages;

import com.sun.net.httpserver.HttpExchange;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import me.jayfella.webop2.Core.MessagePriority;
import me.jayfella.webop2.Core.WebOpMessage;
import me.jayfella.webop2.Core.WebPage;
import me.jayfella.webop2.PluginContext;
import org.apache.commons.lang.ArrayUtils;

public class Page_Messages extends WebPage
{
    private String contentType;

    public Page_Messages(PluginContext context)
    {
        super(context);
    }

    @Override public String contentType() { return contentType; }

    @Override
    public byte[] get(HttpExchange he)
    {
        if (!this.getContext().getSessionManager().isAuthorised(he))
            return new byte[0];

        Map<String, String> vars = this.parseGetResponse(he);

        String varState = vars.get("state");
        String varCurrentIds = vars.get("currentIds");
        String varDeleteId = vars.get("deleteId");
        String varRetrieveIds = vars.get("retrieveIds");

        String varNewMessageUser = vars.get("newMessageUser");
        String varNewMessagePriority = vars.get("newMessagePriority");
        String varNewMessageText = vars.get("newMessageText");

        switch (varState)
        {
            case "check":
            {
                this.contentType = "application/json; charset=utf-8";

                Integer[] currentIdsArray;

                if (!varCurrentIds.isEmpty())
                {
                    String[] currentIdsString = varCurrentIds.split(",");
                    currentIdsArray = new Integer[currentIdsString.length];

                    for (int i = 0; i < currentIdsString.length; i++)
                    {
                        currentIdsString[i] = currentIdsString[i].trim();

                        if (currentIdsString[i].isEmpty())
                            continue;

                        currentIdsArray[i] = Integer.parseInt(currentIdsString[i]);
                    }
                }
                else
                {
                    currentIdsArray = new Integer[0];
                }

                Set<Integer> currentIds = new HashSet<>(Arrays.asList(currentIdsArray));

                Set<Integer> messageIds = new HashSet<>();
                for (WebOpMessage message : this.getContext().getMessageHandler().getMessages())
                    messageIds.add(message.getId());

                Set<Integer> newIds = new HashSet<>(messageIds);
                Set<Integer> delIds = new HashSet<>(currentIds);

                newIds.removeAll(currentIds);
                delIds.removeAll(messageIds);

                StringBuilder response = new StringBuilder();

                // [ { "id": "1", "state": "DELETE" }, { "id": "5", "state": "NEW" } ]


                response.append("[").append("\n");

                int iteratorCount = 0;
                Iterator<Integer> iterator = newIds.iterator();

                while (iterator.hasNext())
                {
                    Integer num = iterator.next();

                    if (num == null)
                        continue;

                    response
                            .append("{ ")
                            .append("\"id\": ").append("\"").append(num).append("\"").append(", ")
                            .append("\"state\": \"NEW\"")
                            .append("}");



                    if (iteratorCount < newIds.size() -1)
                        response.append(",\n");

                    iteratorCount++;
                }

                if (newIds.size() > 0 && delIds.size() > 0)
                    response.append(",\n");


                iteratorCount = 0;
                iterator = delIds.iterator();

                while (iterator.hasNext())
                {
                    Integer num = iterator.next();

                    if (num == null)
                        continue;

                    response
                            .append("{ ")
                            .append("\"id\": ").append("\"").append(num).append("\"").append(", ")
                            .append("\"state\": \"DELETE\"")
                            .append("}");



                    if (iteratorCount < delIds.size() -1)
                        response.append(",\n");

                    iteratorCount++;
                }

                response.append("\n").append("]");

                return response.toString().getBytes();
            }
            case "create":
            {
                // String varNewMessageUser = vars.get("newMessageUser");
                // String varNewMessagePriority = vars.get("newMessagePriority");
                // String varNewMessageText = vars.get("newMessageText");
                this.contentType = "text/html; charset=utf-8";

                long timeStamp = System.currentTimeMillis();

                this.getContext().getMessageHandler().createMessage(varNewMessageUser, timeStamp, MessagePriority.HIGH, varNewMessageText);

                return "OK".getBytes();
            }
            case "delete":
            {
                int idToDelete = Integer.parseInt(varDeleteId);

                this.getContext().getMessageHandler().deleteMessage(idToDelete);

                return "OK".getBytes();
            }
            case "retrieve":
            {
                this.contentType = "text/html; charset=utf-8";

                String[] retrievedIds = varRetrieveIds.split(",");
                int[] newIds = new int[retrievedIds.length];

                for (int i = 0; i < newIds.length; i++)
                {
                    retrievedIds[i] = retrievedIds[i].trim();

                        if (retrievedIds[i].isEmpty())
                            continue;

                    newIds[i] = Integer.parseInt(retrievedIds[i]);
                }

                ArrayUtils.reverse(newIds);

                StringBuilder response = new StringBuilder();

                // for (int i = 0; i < this.getContext().getMessageHandler().getMessages().size(); i++)
                for (int i = 0; i < newIds.length; i++)
                {
                    WebOpMessage message = this.getContext().getMessageHandler().getMessage(newIds[i]);

                    if (message == null)
                        continue;
                    /*  <ul class='alertUl red'>
                            <li><strong>From: </strong>TheZachAttack01<br>Test message</li>
                            <li class='alertUlClose'></li>
                        </ul>
                    */

                    Date msgDate = new Date(message.getTimeStamp());

                    response
                            .append("<ul class='alertUl red'>")

                            .append("<li><strong>")
                            .append("From: ")
                            .append(message.getUser())
                            .append("</strong> @ ")
                            .append(msgDate.toString())
                            .append("<span class='messageId' style='visibility: hidden'>").append(message.getId()).append("</span>")
                            .append("<br/>")
                            .append(message.getMessage())
                            .append("</li>")

                            .append("<li class='alertUlClose'></li>")
                            .append("</ul>");
                }

                return response.toString().getBytes();
            }
        }

        StringBuilder response = new StringBuilder();

        response.append("[").append("\n");
        // for (WebOpMessage msg : this.getContext().getMessageHandler().getMessages())
        for (int i = 0; i < this.getContext().getMessageHandler().getMessages().size(); i++)
        {
            WebOpMessage msg = this.getContext().getMessageHandler().getMessages().get(i);

            response.append("{ ");
            response.append("\"id\": ").append("\"").append(msg.getId()).append("\"").append(", ");
            response.append("\"user\": ").append("\"").append(msg.getUser()).append("\"").append(", ");
            response.append("\"timestamp\": ").append("\"").append(msg.getTimeStamp()).append("\"").append(", ");
            response.append("\"priority\": ").append("\"").append(msg.getPriority()).append("\"").append(", ");
            response.append("\"message\": ").append("\"").append(msg.getMessage()).append("\"");
            response.append("}");

            if (i < this.getContext().getMessageHandler().getMessages().size() -1)
            response.append(",\n");
        }

        response.append("\n").append("]");

        return response.toString().getBytes();
    }

    @Override
    public byte[] post(HttpExchange he)
    {
        return new byte[0];
    }

}
