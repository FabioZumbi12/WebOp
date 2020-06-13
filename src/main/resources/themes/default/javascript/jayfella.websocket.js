var webopSocketAddress = "ws://" + document.domain + ":" + location.port + "/socket/";
var webopSocket = new WebSocket(webopSocketAddress);

// todo: initialize everything when the socket is open.
webopSocket.onopen = function(event) 
{ 
    getServerUtilization();
    retrieveAllMessages();
    
    subscribeAllPlayersData();
    subscribeToConsole();
    subscribeWorldData();
};

webopSocket.onmessage = function(message) 
{ 
    var msg = message.data;
    var messageCase = msg.substring(0, msg.indexOf(";")).replace("case=", "");
    
    switch(messageCase)
    {
        case "serverUtilization":
        {
            parseServerUtilization(msg);
            break;
        }
        case "allPlayersData":
        {
            parseSubscribeAllPlayersData(msg);
            break;
        }
        case "consoleData":
        {
            parseConsoleResponse(msg);
            break;
        }
        case "chatMessage":
        {
            parseChatResponse(msg);
            break;
        }
        case "activityNotification":
        {
            parseActivityStatus(msg);
            break;
        }
        case "message":
        {
            parseMessageResponse(msg);
            break;
        }
        case "worldData":
        {
            parseWorldData(msg);
            break;
        }
    }
        
};

function socketValidationString()
{
    var user = $.cookie("webop_user");
    var session = $.cookie("webop_session");
    
    return "webop_user=" + user + "&webop_session=" + session;
}