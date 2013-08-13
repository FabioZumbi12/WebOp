function processPlayerInformationResponse(value)
{
    $('#playerInformationLoading').css('visibility', 'hidden');

    var alltimeCount = 0;
    var onlineCount = 0;
    var playersList = "";

    var plData = value.split("&");

    for (var i = 0; i < plData.length; i++)
    {
        if (plData[i].length > 7 && plData[i].substring(0, 8) === "ALLTIME=")
        {
            alltimeCount = plData[i].substring(8);
        }
        else if (plData[i].length > 9 && plData[i].substring(0, 10) === "ONLINENOW=")
        {
            onlineCount = plData[i].substring(10);
        }
        else if (plData[i].length > 7 && plData[i].substring(0, 8) === "PLAYERS=")
        {
            playersList = plData[i].substring(8);
        }
    }

    $('#alltimeplayersVal').text(alltimeCount);
    $('#playercountVal').text(onlineCount);
    $('#playerlistVal').text(playersList.length > 0 ?  "Online Now: " + playersList : "");
}

$("#playerInformationRefresh").click(function()
{
    $.get("playerinfo.php", processPlayerInformationResponse);
    $('#playerInformationLoading').css('visibility', 'visible');
});

$.get("playerinfo.php", processPlayerInformationResponse);