function parseWorldData(message)
{
    var worldData = message.split(";");
    
    var name = "";
    var playercount = "";
    var players = "";
    var type = "";
    var difficulty = "";
    var israining = "";
    var isthundering = "";
    
    for (var i = 0; i < worldData.length; i++)
    {
        var dataSplit = worldData[i].split("=");
        
        if (dataSplit[0] === "name")
            name = dataSplit[1];
        if (dataSplit[0] === "playercount")
            playercount = dataSplit[1];
        if (dataSplit[0] === "type")
            type = dataSplit[1];
        if (dataSplit[0] === "difficulty")
            difficulty = dataSplit[1];
        if (dataSplit[0] === "israining")
            israining = dataSplit[1];
        if (dataSplit[0] === "isthundering")
            isthundering = dataSplit[1];
    }
    
    var weather = (israining === "true") ? "Rain" : "Sun";
    
    if (weather === "Rain") 
    {
        if (isthundering === "true")
            weather += " and thunder";
    }
    
    
    var html = 
              "<strong>World Name:</strong> " + name + "<br/>"
            + "<strong>Players:</strong> " + playercount + "<br/>"
            + "<strong>World Type:</strong> " + type + "<br/>"
            + "<strong>Difficulty:</strong> " + difficulty + "<br/>"
            + "<strong>Weather:</strong> " + weather + "<br/>";
    
    $("#" + name).attr("title", html);
}

function subscribeWorldData()
{
    var msg = socketValidationString() + "&case=subscribeWorldData";
    webopSocket.send(msg);
}