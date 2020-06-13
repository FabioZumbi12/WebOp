function subscribeAllPlayersData()
{
    var msg = socketValidationString() + "&case=subscribeAllPlayersData";
    webopSocket.send(msg);
}

function parseSubscribeAllPlayersData(message)
{
    var allTimeCount = "";
    var onlineNowCount = "";
    var maximumPlayers = "";
    var onlinePlayers = "";
    
    var allPlayersResp = message.split(";");
    
    for (var i = 0; i < allPlayersResp.length; i++)
    {
        var allPlayersRespData = allPlayersResp[i].split("=");
        
        if (allPlayersRespData[0] === "ALLTIME")
        {
            allTimeCount = allPlayersRespData[1];
        }
        else if (allPlayersRespData[0] === "ONLINENOW")
        {
            onlineNowCount = allPlayersRespData[1];
        }
        else if (allPlayersRespData[0] === "MAXIMUM")
        {
            maximumPlayers = allPlayersRespData[1];
        }
        else if (allPlayersRespData[0] === "PLAYERS")
        {
            onlinePlayers = allPlayersRespData[1];
        }
    }
    
    if (onlinePlayers === "")
    {
        onlinePlayers = "None";
    }
    else
    {
        var splitPlayers = onlinePlayers.split(",");
        
        var newPlayers = "";
        
        for (var i = 0; i < splitPlayers.length; i++)
        {
            newPlayers += "<span title='' class='teleportButton mcPlayer essPlayer'>" + splitPlayers[i] + "</span>";
            
            if (i < splitPlayers.length - 1)
                newPlayers += ", ";
        }
        
    }
    
    $("#allPlayersEverPlayedCount").html(allTimeCount);
    $("#allPlayersOnlineNowCount").html(onlineNowCount + " / " + maximumPlayers);
    $("#allPlayersOnlineNow").html(newPlayers);
    
}